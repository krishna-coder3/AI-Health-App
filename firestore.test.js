const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

let testEnv;
const PROJECT_ID = process.env.GCP_PROJECT || "demo-no-project";
const ALICE_UID = "alice_123";
const BOB_UID = "bob_456";
const OWNER_UID = "owner_789";
const OWNER_EMAIL = "krrrsingh190@gmail.com";

const [emulatorHost, emulatorPortStr] = (process.env.FIRESTORE_EMULATOR_HOST || "127.0.0.1:8085").split(":");
const emulatorPort = parseInt(emulatorPortStr, 10);

before(async () => {
  const rules = fs.readFileSync("./firestore.rules", "utf8");
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      rules,
      host: emulatorHost,
      port: emulatorPort,
    },
  });
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

beforeEach(async () => {
  if (testEnv) {
    await testEnv.clearFirestore();
  }
});

test("Unauthenticated read fails on users collection", async () => {
  const unauthedDb = testEnv.unauthenticatedContext().firestore();
  await assertFails(unauthedDb.collection("users").doc(ALICE_UID).get());
});

test("Authenticated user can create their own profile as user role", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID, { email: "alice@example.com", email_verified: true }).firestore();
  await assertSucceeds(aliceDb.collection("users").doc(ALICE_UID).set({
    userId: ALICE_UID,
    email: "alice@example.com",
    role: "user",
    displayName: "Alice",
    age: 28,
    gender: "Female",
    height: 165,
    weight: 60,
    languagePreference: "English",
    isBlocked: false,
    createdAt: new Date(),
  }));
});

test("Normal user cannot create profile with owner role", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID, { email: "alice@example.com", email_verified: true }).firestore();
  await assertFails(aliceDb.collection("users").doc(ALICE_UID).set({
    userId: ALICE_UID,
    email: "alice@example.com",
    role: "owner",
    createdAt: new Date(),
  }));
});

test("Owner admin can create a remedy", async () => {
  const ownerDb = testEnv.authenticatedContext(OWNER_UID, { email: OWNER_EMAIL, email_verified: true }).firestore();
  await assertSucceeds(ownerDb.collection("remedies").doc("remedy_1").set({
    nameEn: "Ginger Tea",
    nameHi: "Adrak Chai",
    category: "Cold and Cough",
    ingredients: ["Ginger", "Water", "Honey"],
    method: ["Boil water", "Add grated ginger", "Add honey"],
    benefits: ["Relieves sore throat"],
    precautions: ["Do not boil honey"],
    whoShouldAvoid: ["None"],
    doctorNote: "Safe for adults and children over 2",
    isDesi: true,
    createdAt: new Date(),
  }));
});

test("Non-owner cannot create a remedy", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID, { email: "alice@example.com", email_verified: true }).firestore();
  await assertFails(aliceDb.collection("remedies").doc("remedy_2").set({
    nameEn: "Test",
    nameHi: "Test Hi",
    category: "Cold and Cough",
    doctorNote: "See doctor",
    createdAt: new Date(),
  }));
});

test("User cannot read another user's health logs", async () => {
  await testEnv.withSecurityRulesDisabled(async (context) => {
    await context.firestore().collection("healthLogs").doc("alice_log").set({
      userId: ALICE_UID,
      date: "2026-10-26",
      systolic: 120,
      diastolic: 80,
      heartRate: 72,
      bloodSugar: 95,
      weight: 65,
      waterIntakeMl: 2500,
      sleepHours: 7.5,
      createdAt: new Date(),
    });
  });

  const bobDb = testEnv.authenticatedContext(BOB_UID, { email: "bob@example.com", email_verified: true }).firestore();
  await assertFails(bobDb.collection("healthLogs").doc("alice_log").get());

  const aliceDb = testEnv.authenticatedContext(ALICE_UID, { email: "alice@example.com", email_verified: true }).firestore();
  await assertSucceeds(aliceDb.collection("healthLogs").doc("alice_log").get());
});

test("User can book an appointment for themselves", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID, { email: "alice@example.com", email_verified: true }).firestore();
  await assertSucceeds(aliceDb.collection("appointments").doc("app_1").set({
    userId: ALICE_UID,
    doctorId: "doc_1",
    doctorName: "Dr. Sharma",
    date: "2026-10-30",
    timeSlot: "10:00 AM",
    status: "booked",
    fee: 500,
    createdAt: new Date(),
  }));
});
