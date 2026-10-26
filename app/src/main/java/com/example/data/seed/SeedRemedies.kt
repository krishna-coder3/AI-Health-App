package com.example.data.seed

import com.example.data.model.Remedy

object SeedRemedies {
    val remedies: List<Remedy> = listOf(
        // 1-5: Cold and Cough
        Remedy(
            id = "remedy_ginger_honey_tea",
            nameEn = "Warm Ginger & Honey Tea",
            nameHi = "अदरक और शहद की चाय (Adrak Honey Chai)",
            category = "Cold and Cough",
            ingredients = listOf("1 inch fresh crushed ginger", "1 cup boiling water", "1 tsp raw honey", "Few drops lemon juice"),
            method = listOf("Crush fresh ginger and boil in 1 cup water for 5 minutes.", "Strain into a cup and let it cool slightly until warm.", "Stir in raw honey and freshly squeezed lemon juice.", "Sip slowly 2-3 times a day."),
            benefits = listOf("Soothes scratchy throat and reduces coughing spasms", "Gingerols possess anti-inflammatory and antiviral qualities", "Honey provides a soothing protective coat to mucus membranes"),
            precautions = listOf("Never boil honey directly in bubbling water as heat alters honey nutrients", "Avoid excessive ginger if prone to severe acid reflux"),
            whoShouldAvoid = listOf("Infants under 1 year of age (infant botulism risk from honey)", "Individuals with severe acute gallstones"),
            doctorNote = "Seek medical care if cough persists over 2 weeks or is accompanied by blood, shortness of breath, or high fever.",
            isDesi = true
        ),
        Remedy(
            id = "remedy_tulsi_kadha",
            nameEn = "Holy Basil (Tulsi) & Black Pepper Kadha",
            nameHi = "तुलसी और काली मिर्च का काढ़ा (Tulsi Kadha)",
            category = "Cold and Cough",
            ingredients = listOf("8-10 fresh Holy Basil leaves", "4 crushed black peppercorns", "1 small cinnamon stick", "1.5 cups water", "1 tsp jaggery (optional)"),
            method = listOf("Boil water with tulsi leaves, crushed peppercorns, and cinnamon for 8-10 minutes.", "Reduce liquid to approximately 1 cup.", "Add jaggery for taste if desired.", "Strain and consume warm in the morning or evening."),
            benefits = listOf("Relieves nasal congestion and chest tightness", "Boosts respiratory immunity and cleanses airways", "Antimicrobial essential oils from holy basil"),
            precautions = listOf("Drink in moderate amounts (max 2 cups daily)", "Keep mild for young children"),
            whoShouldAvoid = listOf("Individuals scheduled for surgery within 2 weeks due to mild blood thinning effect"),
            doctorNote = "Consult a physician if you experience breathing difficulty or wheezing.",
            isDesi = true
        ),
        Remedy(
            id = "remedy_steam_inhalation",
            nameEn = "Eucalyptus Steam Inhalation",
            nameHi = "भाप लेना (Steam Inhalation)",
            category = "Cold and Cough",
            ingredients = listOf("Boiling hot water in a wide bowl", "2-3 drops eucalyptus essential oil or fresh mint leaves", "Clean towel"),
            method = listOf("Pour boiling water into a sturdy bowl placed on a flat stable table.", "Add eucalyptus oil or bruised mint leaves.", "Drape a towel over your head forming a tent 12 inches above bowl.", "Inhale steam gently through nose and mouth for 5-10 minutes."),
            benefits = listOf("Instantly loosens thick mucus in sinus cavities", "Moistens dry and irritated bronchial tubes", "Promotes easier nighttime breathing"),
            precautions = listOf("Keep safe distance from hot bowl to prevent accidental scalding", "Keep eyes closed during inhalation"),
            whoShouldAvoid = listOf("Unsupervised young children", "Severe asthmatics sensitive to strong eucalyptus aromas"),
            doctorNote = "If facial pain and yellow-green sinus discharge persist beyond 10 days, consult an ENT specialist.",
            isDesi = false
        ),
        Remedy(
            id = "remedy_salt_water_gargle",
            nameEn = "Warm Saltwater Gargle",
            nameHi = "नमक के गुनगुने पानी से गरारे (Namak Ke Garare)",
            category = "Cold and Cough",
            ingredients = listOf("1 glass warm water (approx 250ml)", "1/2 tsp pure table salt or rock salt"),
            method = listOf("Dissolve 1/2 tsp salt in warm (not hot) water.", "Take a sip, tilt head back, and gargle for 30 seconds.", "Spit out completely into sink; do not swallow.", "Repeat 3-4 times daily."),
            benefits = listOf("Osmotically draws excess fluid and swelling out of inflamed throat tissue", "Flushes away bacteria and irritants"),
            precautions = listOf("Ensure water is comfortably warm to avoid oral burns", "Do not swallow large amounts of saltwater"),
            whoShouldAvoid = listOf("Young children who cannot reliably spit"),
            doctorNote = "See a doctor if swallowing is severely painful or if white patches appear on tonsils.",
            isDesi = false
        ),
        Remedy(
            id = "remedy_mulethi_licorice",
            nameEn = "Licorice Root (Mulethi) Chew",
            nameHi = "मुलेठी की जड़ (Mulethi Churna)",
            category = "Cold and Cough",
            ingredients = listOf("1 small piece of clean Mulethi (licorice root) or 1/4 tsp Mulethi powder with warm water"),
            method = listOf("Chew on a small piece of licorice root slowly, swallowing the soothing juices.", "Alternatively, mix 1/4 tsp powder in warm water with a drop of honey."),
            benefits = listOf("Naturally demulcent, coats vocal cords and calms dry hacking cough", "Anti-inflammatory compounds soothe laryngitis"),
            precautions = listOf("Limit intake to short courses (under 7 consecutive days)"),
            whoShouldAvoid = listOf("Patients with uncontrolled hypertension (may elevate blood pressure)", "Pregnant women"),
            doctorNote = "Dry cough lasting more than 3 weeks requires clinical evaluation for underlying allergy or asthma.",
            isDesi = true
        ),

        // 6-8: Fever
        Remedy(
            id = "remedy_cool_compress",
            nameEn = "Lukewarm Sponge Compress",
            nameHi = "गुनगुने पानी की पट्टी (Lukewarm Sponge Compress)",
            category = "Fever",
            ingredients = listOf("Bowl of lukewarm (room temperature) water", "Clean cotton washcloths"),
            method = listOf("Dip cloth in lukewarm water and wring out thoroughly.", "Gently place on forehead, back of neck, and wrists.", "Change cloth every few minutes as it warms up.", "Continue for 10-15 minutes in a comfortable, airy room."),
            benefits = listOf("Gradually dissipates surface heat through mild evaporation without shivering", "Safe, non-invasive physical comfort measure"),
            precautions = listOf("NEVER use ice water or rubbing alcohol as cold causes violent shivering and paradoxical core temperature spike", "Keep patient comfortably dressed"),
            whoShouldAvoid = listOf("None when done with lukewarm water"),
            doctorNote = "Fever above 103°F (39.4°C) or any fever in infants under 3 months is a medical emergency requiring immediate hospitalization.",
            isDesi = false
        ),
        Remedy(
            id = "remedy_coriander_seed_water",
            nameEn = "Coriander Seed Cooling Infusion (Dhaniya Jal)",
            nameHi = "धनिया के बीज का पानी (Dhaniya Pani)",
            category = "Fever",
            ingredients = listOf("1 tbsp coriander seeds", "2 cups filtered water", "Half pinch cardamom powder"),
            method = listOf("Lightly crush coriander seeds and soak in 2 cups of water for 2-3 hours.", "Simmer on low heat until reduced to 1 cup.", "Strain and let it cool to room temperature before sipping."),
            benefits = listOf("Known in Ayurveda as a gentle cooling diuretic that clears metabolic toxins", "Restores essential electrolytes and hydration lost through mild perspiration"),
            precautions = listOf("Keep freshly prepared daily"),
            whoShouldAvoid = listOf("Patients with severe kidney filtration impairment"),
            doctorNote = "Ensure underlying causes of fever (such as dengue, malaria, UTI, or pneumonia) are tested if fever persists past 48 hours.",
            isDesi = true
        ),
        Remedy(
            id = "remedy_electrolyte_hydration",
            nameEn = "Citrus Coconut Hydration Elixir",
            nameHi = "नारियल पानी और नींबू (Nariyal Pani Elixir)",
            category = "Fever",
            ingredients = listOf("1 glass fresh tender coconut water", "Pinch of pink rock salt", "Few drops of lemon juice"),
            method = listOf("Pour fresh tender coconut water into a glass.", "Stir in pink rock salt and fresh lemon.", "Sip slowly throughout the day between fever episodes."),
            benefits = listOf("Replenishes critical potassium, sodium, and magnesium", "Prevents weakness, lethargy, and dehydration during febrile states"),
            precautions = listOf("Consume fresh; do not leave opened coconut water at room temperature for hours"),
            whoShouldAvoid = listOf("Patients with chronic kidney disease requiring strict potassium limitation"),
            doctorNote = "If patient is unable to keep fluids down due to vomiting, seek IV hydration immediately.",
            isDesi = true
        ),

        // 9-13: Digestion and Acidity
        Remedy(
            id = "remedy_ajwain_jeera_carom",
            nameEn = "Carom & Cumin Digestive Brew",
            nameHi = "अजवाइन और जीरे का पानी (Ajwain-Jeera Pani)",
            category = "Digestion and Acidity",
            ingredients = listOf("1/2 tsp Ajwain (carom seeds)", "1/2 tsp Jeera (cumin seeds)", "1.5 cups water", "Pinch of black salt (Kala Namak)"),
            method = listOf("Boil ajwain and jeera in 1.5 cups water until it turns golden brown (approx 5 mins).", "Strain into a cup and mix in a pinch of black salt.", "Drink warm 15 minutes after meals."),
            benefits = listOf("Thymol in ajwain relieves intestinal gas, flatulence, and spasmodic cramps", "Cumin stimulates secretion of pancreatic enzymes for smooth digestion"),
            precautions = listOf("Do not consume more than 2 cups a day"),
            whoShouldAvoid = listOf("Pregnant women in large concentrated amounts", "Those suffering from active bleeding ulcers"),
            doctorNote = "Severe unrelenting abdominal pain with vomiting or black stools requires immediate ER attention.",
            isDesi = true
        ),
        Remedy(
            id = "remedy_cold_milk_fennel",
            nameEn = "Cool Fennel & Milk Alkaline Soother",
            nameHi = "सौंफ और ठंडा दूध (Saunf & Cold Milk)",
            category = "Digestion and Acidity",
            ingredients = listOf("1/2 cup organic chilled milk", "1/2 cup cold water", "1/2 tsp crushed fennel seed powder"),
            method = listOf("Mix equal parts chilled milk and cold water.", "Add freshly crushed fennel powder and mix well.", "Sip slowly when experiencing acute heartburn or acid reflux."),
            benefits = listOf("Alkaline calcium in cold milk neutralizes gastric hydrochloric acid", "Fennel essential oils calm stomach spasms"),
            precautions = listOf("Use low-fat or diluted milk to prevent delayed gastric emptying"),
            whoShouldAvoid = listOf("Lactose-intolerant individuals (substitute with unsweetened almond milk)"),
            doctorNote = "Frequent heartburn occurring more than twice a week should be screened for GERD.",
            isDesi = true
        ),
        Remedy(
            id = "remedy_buttermilk_chaas",
            nameEn = "Spiced Probiotic Buttermilk (Chaas)",
            nameHi = "मसाला छाछ (Pudina Masala Chaas)",
            category = "Digestion and Acidity",
            ingredients = listOf("1/2 cup fresh homemade curd/yogurt", "1 cup cold water", "Fresh mint leaves", "1/4 tsp roasted cumin powder", "Pinch of rock salt"),
            method = listOf("Blend curd and water vigorously until light and frothy.", "Garnish with finely chopped fresh mint leaves, roasted cumin, and rock salt.", "Drink chilled after a heavy lunch."),
            benefits = listOf("Infuses gut with live beneficial Lactobacillus probiotics", "Cools internal digestive heat and prevents post-meal heaviness"),
            precautions = listOf("Use freshly prepared curd; avoid sour or spoiled curd"),
            whoShouldAvoid = listOf("People with severe dairy protein allergies"),
            doctorNote = "Safe for daily consumption for all ages.",
            isDesi = true
        ),
        Remedy(
            id = "remedy_peppermint_tea",
            nameEn = "Peppermint Leaves Digestive Infusion",
            nameHi = "पुदीना की चाय (Pudina Chai)",
            category = "Digestion and Acidity",
            ingredients = listOf("6-8 fresh peppermint leaves or 1 pure peppermint tea bag", "1 cup hot water"),
            method = listOf("Steep fresh bruised leaves in hot water covered for 5-7 minutes.", "Remove leaves and sip comfortably warm after lunch or dinner."),
            benefits = listOf("Menthol relaxes gastrointestinal smooth muscle, easing IBS bloating", "Reduces feelings of fullness and trapped air"),
            precautions = listOf("If you suffer from severe GERD, menthol may relax esophageal sphincter in some individuals"),
            whoShouldAvoid = listOf("Severe hiatus hernia with reflux"),
            doctorNote = "Safe and well-tolerated for general digestive wellness.",
            isDesi = false
        ),
        Remedy(
            id = "remedy_hing_asafoetida",
            nameEn = "Asafoetida (Hing) Navel Rub for Gas",
            nameHi = "हींग का लेप (Hing Lep for Bloating)",
            category = "Digestion and Acidity",
            ingredients = listOf("1 pinch pure asafoetida (Hing)", "1 tsp warm mustard oil or water"),
            method = listOf("Dissolve a pinch of Hing in warm mustard oil or water to create a mild paste.", "Gently massage in a clockwise circular motion around the naval area for 2 minutes."),
            benefits = listOf("Quick relief from trapped infant colic and adult abdominal distension", "External application eliminates digestive tract irritation"),
            precautions = listOf("Ensure oil is gently warm, never hot to the touch"),
            whoShouldAvoid = listOf("Broken skin, rashes, or open umbilical wounds"),
            doctorNote = "Extremely popular and safe Ayurvedic remedy for colicky babies and bloating.",
            isDesi = true
        ),

        // 14-17: Skin
        Remedy(
            id = "remedy_aloe_vera_gel",
            nameEn = "Pure Aloe Vera Soothing Compress",
            nameHi = "घृतकुमारी / एलोवेरा जेल (Fresh Aloe Gel)",
            category = "Skin",
            ingredients = listOf("1 fresh aloe vera leaf or 100% pure organic aloe gel"),
            method = listOf("Slice open fresh leaf and extract clear inner gel.", "Rinse off yellow aloin sap if harvested fresh from plant.", "Apply pure clear gel directly onto sunburned, irritated, or inflamed skin.", "Leave on for 20 minutes before gently rinsing with cool water."),
            benefits = listOf("Acemannan accelerates cutaneous wound healing", "Cooling hydration reduces redness, sunburn, and itching", "Antibacterial and non-comedogenic"),
            precautions = listOf("Perform a small inner-arm patch test first if sensitive to plants"),
            whoShouldAvoid = listOf("Open deep surgical wounds"),
            doctorNote = "Consult a dermatologist if rash is accompanied by blistering, swelling, or systemic symptoms.",
            isDesi = false
        ),
        Remedy(
            id = "remedy_turmeric_besan_pack",
            nameEn = "Turmeric & Chickpea Clarifying Mask (Ubtan)",
            nameHi = "हल्दी और बेसन का उबटन (Haldi Besan Ubtan)",
            category = "Skin",
            ingredients = listOf("2 tbsp chickpea flour (Besan)", "1/4 tsp wild turmeric (Kasturi Manjal)", "2 tbsp raw milk or rose water"),
            method = listOf("Mix chickpea flour, turmeric, and rose water into a smooth lump-free paste.", "Apply evenly to face avoiding direct eye contact.", "Allow to dry for 12-15 minutes.", "Rinse with lukewarm water using gentle circular exfoliating motions."),
            benefits = listOf("Curcumin suppresses acne-causing bacteria and dark spots", "Chickpea flour gently scrubs away dead epithelial cells and excess sebum"),
            precautions = listOf("Use cosmetic wild turmeric (Kasturi) to avoid deep yellow skin staining"),
            whoShouldAvoid = listOf("People with active severe cystic dermatitis"),
            doctorNote = "Safe natural cleanser used in traditional Indian skincare.",
            isDesi = true
        ),
        Remedy(
            id = "remedy_neem_paste",
            nameEn = "Neem Leaf Blemish Clarifier",
            nameHi = "नीम का लेप (Neem Paste for Acne)",
            category = "Skin",
            ingredients = listOf("10-12 fresh neem leaves", "1 tbsp pure rose water"),
            method = listOf("Grind clean neem leaves with rose water into a fine green paste.", "Dab specifically on active acne blemishes as a spot treatment.", "Leave for 20 minutes then wash off with cool water."),
            benefits = listOf("Potent azadirachtin compound destroys acne bacteria", "Reduces acute follicular swelling and redness"),
            precautions = listOf("Do not apply all over dry skin; best as a localized spot treatment"),
            whoShouldAvoid = listOf("Individuals with open eczema sores"),
            doctorNote = "Persistent cystic acne requires prescription dermatological intervention.",
            isDesi = true
        ),
        Remedy(
            id = "remedy_oatmeal_bath",
            nameEn = "Colloidal Oatmeal Anti-Itch Soak",
            nameHi = "ओट्स का सुखदायक लेप (Oatmeal Compress)",
            category = "Skin",
            ingredients = listOf("1/2 cup finely ground colloidal whole oats", "Warm water"),
            method = listOf("Finely grind rolled oats into a smooth powder.", "Mix with warm water to form a milky soothing paste.", "Apply directly to itchy skin or rash for 15 minutes before rinsing gently."),
            benefits = listOf("Avenanthramides dramatically calm histamine itching and allergic irritation", "Strengthens natural epidermal barrier lipids"),
            precautions = listOf("Pat skin dry with soft towel; do not rub vigorously"),
            whoShouldAvoid = listOf("Severe oat or gluten contact dermatitis"),
            doctorNote = "Effective FDA-recognized natural soothing treatment for mild eczema and mosquito bites.",
            isDesi = false
        ),

        // 18-21: Hair
        Remedy(
            id = "remedy_amla_coconut_oil",
            nameEn = "Amla & Virgin Coconut Oil Scalp Nourisher",
            nameHi = "आंवला और नारियल तेल (Amla Scalp Tonic)",
            category = "Hair",
            ingredients = listOf("3 tbsp pure cold-pressed coconut oil", "1 tbsp dried Indian gooseberry (Amla) pieces or powder"),
            method = listOf("Warm coconut oil with amla in a small pan on low flame until slightly darkened.", "Allow to cool to body temperature.", "Massage gently into scalp using fingertips for 8-10 minutes.", "Leave on for at least 1 hour or overnight before washing with mild herbal shampoo."),
            benefits = listOf("Abundant Vitamin C in Amla boosts scalp microcirculation", "Lauric acid in coconut oil penetrates hair cortex preventing protein loss"),
            precautions = listOf("Wash out thoroughly to avoid oily build-up on scalp pores"),
            whoShouldAvoid = listOf("Acute fungal scalp infections with open crusting"),
            doctorNote = "Sudden patchy hair loss (alopecia areata) requires clinical dermatological evaluation.",
            isDesi = true
        ),
        Remedy(
            id = "remedy_fenugreek_methi_mask",
            nameEn = "Fenugreek (Methi) Dandruff Hair Pack",
            nameHi = "मेथी दाना हेयर पैक (Methi Hair Mask)",
            category = "Hair",
            ingredients = listOf("2 tbsp fenugreek seeds", "Water for soaking", "2 tbsp fresh curd/yogurt"),
            method = listOf("Soak fenugreek seeds in water overnight.", "Grind into a smooth, slimy paste the next morning.", "Blend in curd and apply thoroughly to scalp and roots.", "Leave for 30 minutes, then rinse with plenty of water."),
            benefits = listOf("Mucilaginous fiber deeply hydrates dry brittle strands", "Antifungal saponins combat Malassezia yeast causing flaky dandruff"),
            precautions = listOf("Grind extremely fine to avoid difficulty washing out tiny seed grains"),
            whoShouldAvoid = listOf("Those with known legume/peanut cross-sensitivities"),
            doctorNote = "Apply once a week for best anti-dandruff maintenance.",
            isDesi = true
        ),
        Remedy(
            id = "remedy_rosemary_water_rinse",
            nameEn = "Rosemary Herb Scalp Tonic",
            nameHi = "रोज़मेरी हर्बल पानी (Rosemary Hair Rinse)",
            category = "Hair",
            ingredients = listOf("2 sprigs fresh rosemary or 1 tbsp dried rosemary leaves", "2 cups water"),
            method = listOf("Boil rosemary in 2 cups of water for 15 minutes.", "Turn off heat and let steep covered for 30 minutes until water turns pinkish-brown.", "Strain into a spray bottle and mist scalp daily after washing."),
            benefits = listOf("Carnosinic acid improves scalp nerve and cellular turnover", "Clinically shown to perform comparably to mild minoxidil in hair density trials"),
            precautions = listOf("Store in refrigerator and use within 7 days"),
            whoShouldAvoid = listOf("Known allergy to rosemary or mint family botanicals"),
            doctorNote = "Safe, pleasant-smelling herbal tonic with solid research backing.",
            isDesi = false
        ),
        Remedy(
            id = "remedy_onion_juice",
            nameEn = "Fresh Onion Juice Follicle Stimulator",
            nameHi = "प्याज का रस (Pyaz Ka Ras)",
            category = "Hair",
            ingredients = listOf("1 medium red onion", "Cotton ball"),
            method = listOf("Peel and grate onion, then squeeze through cheesecloth to extract fresh juice.", "Dab onto areas of thinning hair using a cotton ball.", "Leave on for 15-30 minutes, then shampoo thoroughly to remove scent."),
            benefits = listOf("High organic dietary sulfur content nourishes keratin production", "Catalase enzyme reduces oxidative follicle stress"),
            precautions = listOf("Strong pungent odor; always perform skin patch test first"),
            whoShouldAvoid = listOf("Hypersensitive scalps or weeping sores"),
            doctorNote = "Discontinue if redness or burning sensation occurs.",
            isDesi = true
        ),

        // 22-25: Immunity
        Remedy(
            id = "remedy_golden_turmeric_milk",
            nameEn = "Golden Turmeric Milk (Haldi Doodh)",
            nameHi = "हल्दी वाला दूध (Haldi Doodh)",
            category = "Immunity",
            ingredients = listOf("1 cup milk (or oat/almond milk)", "1/2 tsp pure turmeric powder", "1 pinch freshly cracked black pepper", "1/4 tsp ghee or coconut oil", "1/2 tsp honey or jaggery"),
            method = listOf("Gently heat milk in a pan with turmeric, black pepper, and ghee for 3-4 minutes.", "Pour into mug and let cool slightly before adding honey.", "Drink warm before bedtime."),
            benefits = listOf("Piperine in black pepper increases curcumin absorption by up to 2000%", "Natural immunomodulator that accelerates overnight cellular tissue repair"),
            precautions = listOf("Use pinch of pepper to ensure curcumin bioavailability"),
            whoShouldAvoid = listOf("Bile duct obstructions or active gallstone attacks"),
            doctorNote = "Centuries-old Indian night tonic revered for immunity and joint health.",
            isDesi = true
        ),
        Remedy(
            id = "remedy_chyawanprash_warm_milk",
            nameEn = "Amla Chyawanprash Elixir",
            nameHi = "च्यवनप्राश और गर्म दूध (Chyawanprash)",
            category = "Immunity",
            ingredients = listOf("1 tsp authentic herbal Chyawanprash", "1 cup warm milk or water"),
            method = listOf("Consume 1 tsp of Chyawanprash directly in the morning.", "Follow immediately with a cup of warm milk or warm water."),
            benefits = listOf("Rich in natural bioflavonoids, Vitamin C, and 40+ rejuvenating herbs", "Boosts phagocytic activity of white blood cells against infections"),
            precautions = listOf("Diabetics should choose sugar-free Chyawanprash formulations"),
            whoShouldAvoid = listOf("Patients with active chronic diarrhea"),
            doctorNote = "Superb daily health supplement during seasonal shifts.",
            isDesi = true
        ),
        Remedy(
            id = "remedy_elderberry_syrup",
            nameEn = "Elderberry & Clove Immune Tonic",
            nameHi = "एल्डरबेरी सिरप (Elderberry Immune Brew)",
            category = "Immunity",
            ingredients = listOf("1 tbsp dried black elderberries (Sambucus nigra)", "1.5 cups water", "1 clove", "1 tsp honey"),
            method = listOf("Simmer elderberries and clove in water for 20 minutes.", "Mash berries, strain liquid, and stir in honey once warm.", "Take 1 tablespoon daily during flu season."),
            benefits = listOf("Anthocyanins bind to viral surface spikes, reducing viral proliferation", "Rich in immune-protective zinc and Vitamin C"),
            precautions = listOf("Always cook elderberries thoroughly; never eat raw elderberries"),
            whoShouldAvoid = listOf("Patients on immunosuppressant medications for autoimmune diseases"),
            doctorNote = "Standard European traditional botanical remedy for cold and flu season.",
            isDesi = false
        ),
        Remedy(
            id = "remedy_garlic_honey_shot",
            nameEn = "Raw Crushed Garlic & Honey Shot",
            nameHi = "लहसुन और शहद (Kacha Lehsun with Honey)",
            category = "Immunity",
            ingredients = listOf("1 small clove fresh raw garlic", "1 tsp raw honey"),
            method = listOf("Crush or finely mince fresh garlic and let sit exposed to air for 5 minutes.", "Mix with 1 tsp raw honey.", "Swallow with water on an empty stomach in the morning."),
            benefits = listOf("Exposing crushed garlic to air activates Allicin, a potent broad-spectrum natural antimicrobial", "Supports healthy cholesterol and immune surveillance"),
            precautions = listOf("Do not eat excessive raw garlic on an empty stomach if prone to gastritis"),
            whoShouldAvoid = listOf("Individuals taking blood-thinning anticoagulants (Warfarin/Heparin)"),
            doctorNote = "Potent, time-tested natural antimicrobial agent.",
            isDesi = true
        ),

        // 26-29: Weight Management
        Remedy(
            id = "remedy_warm_lemon_water",
            nameEn = "Morning Warm Lemon Water",
            nameHi = "गुनगुना नींबू पानी (Warm Nimbu Pani)",
            category = "Weight Management",
            ingredients = listOf("1 glass warm water (not boiling)", "Juice of half fresh lemon", "Pinch of ground cinnamon (optional)"),
            method = listOf("Squeeze half a lemon into a glass of warm water.", "Add a pinch of ground cinnamon if desired.", "Drink first thing in the morning on an empty stomach."),
            benefits = listOf("Kicks off morning hydration and stimulates gastrocolic peristalsis", "Citric acid supports gastric secretions and healthy metabolic rate"),
            precautions = listOf("Rinse mouth with plain water afterwards to protect tooth enamel from acid"),
            whoShouldAvoid = listOf("Those with active oral ulcers or severe esophageal reflux"),
            doctorNote = "Hydration is the fundamental building block of sustained healthy weight loss.",
            isDesi = true
        ),
        Remedy(
            id = "remedy_apple_cider_vinegar",
            nameEn = "Diluted Apple Cider Vinegar (ACV)",
            nameHi = "सेब का सिरका (Apple Cider Vinegar Drink)",
            category = "Weight Management",
            ingredients = listOf("1-2 tsp raw, unfiltered Apple Cider Vinegar with 'The Mother'", "1 large glass warm water (250ml)"),
            method = listOf("Thoroughly dilute 1-2 tsp ACV in 250ml of warm water.", "Drink through a straw 15 minutes before your largest meal."),
            benefits = listOf("Acetic acid slows the rate at which stomach empties, reducing postprandial glucose spikes", "Increases satiety and prevents mid-day snacking"),
            precautions = listOf("NEVER drink undiluted vinegar as it burns the esophagus and erodes dental enamel"),
            whoShouldAvoid = listOf("Patients taking diuretics or with gastroparesis"),
            doctorNote = "Always dilute thoroughly; best combined with portion control and daily walking.",
            isDesi = false
        ),
        Remedy(
            id = "remedy_green_tea_ginger",
            nameEn = "Matcha Green Tea & Mint Antioxidant Infusion",
            nameHi = "ग्रीन टी और अदरक (Green Tea with Adrak)",
            category = "Weight Management",
            ingredients = listOf("1 green tea bag or 1 tsp loose leaf", "1 cup hot water (80°C)", "Thin slice of fresh ginger", "Mint leaves"),
            method = listOf("Steep green tea and ginger slice in hot water for 3 minutes.", "Remove bag to prevent bitterness.", "Enjoy between meals as a calorie-free, antioxidant-rich beverage."),
            benefits = listOf("EGCG (Epigallocatechin gallate) gently boosts fat oxidation during moderate physical activity", "Mild natural caffeine heightens morning focus"),
            precautions = listOf("Avoid drinking after 5 PM if sensitive to caffeine"),
            whoShouldAvoid = listOf("Individuals with severe caffeine sensitivity or insomnia"),
            doctorNote = "Replacing sugary sodas with green tea creates a clean, sustainable caloric deficit.",
            isDesi = false
        ),
        Remedy(
            id = "remedy_soaked_methi_water",
            nameEn = "Overnight Soaked Fenugreek Water",
            nameHi = "मेथी दाने का पानी (Soaked Methi Water)",
            category = "Weight Management",
            ingredients = listOf("1 tsp fenugreek seeds", "1 cup room temperature water"),
            method = listOf("Soak 1 tsp seeds in 1 cup water overnight.", "Strain and drink the water in the morning, chewing the softened seeds if tolerated."),
            benefits = listOf("Water-soluble galactomannan fiber expands in stomach, promoting feeling of fullness", "Aids in carbohydrate metabolism and insulin sensitivity"),
            precautions = listOf("Start with 1/2 tsp if prone to bloating"),
            whoShouldAvoid = listOf("Pregnant women (may stimulate uterine contractions)"),
            doctorNote = "Popular traditional adjuvant for healthy glucose regulation and weight management.",
            isDesi = true
        ),

        // 30-33: Sleep and Stress
        Remedy(
            id = "remedy_chamomile_tea",
            nameEn = "Calming Chamomile & Lavender Infusion",
            nameHi = "कैमोमाइल हर्बल टी (Chamomile Herbal Tea)",
            category = "Sleep and Stress",
            ingredients = listOf("1 tbsp dried chamomile flowers or 1 chamomile tea bag", "1 cup boiling water", "1/4 tsp pure lavender buds (optional)"),
            method = listOf("Steep chamomile and lavender in boiling water covered for 7-10 minutes.", "Strain into a mug and inhale the soothing botanical aroma.", "Sip slowly 30-45 minutes before going to bed."),
            benefits = listOf("Apigenin antioxidant binds to GABA-A receptors in the brain, reducing nervous anxiety", "Promotes muscle relaxation and ease of falling asleep"),
            precautions = listOf("Avoid adding sugar; keep beverage completely caffeine-free"),
            whoShouldAvoid = listOf("Severe allergy to ragweed or daisy family plants"),
            doctorNote = "Non-habit-forming, clinically researched botanical sleep aid.",
            isDesi = false
        ),
        Remedy(
            id = "remedy_ashwagandha_moon_milk",
            nameEn = "Ashwagandha Ayurvedic Moon Milk",
            nameHi = "अश्वगंधा मून मिल्क (Ashwagandha Doodh)",
            category = "Sleep and Stress",
            ingredients = listOf("1 cup warm milk or oat milk", "1/2 tsp pure Ashwagandha root powder", "1 pinch nutmeg (Jaiphal)", "1/2 tsp ghee"),
            method = listOf("Simmer milk with ashwagandha powder and nutmeg on low heat for 5 minutes.", "Add ghee and pour into your evening cup.", "Drink warm 1 hour before sleep."),
            benefits = listOf("Withanolides act as adaptogens that balance cortisol and stress response", "Nutmeg contains natural myristicin which induces deep, restorative sleep"),
            precautions = listOf("Do not exceed 1/2 tsp ashwagandha per serving"),
            whoShouldAvoid = listOf("Pregnant women", "Hyperthyroidism patients without doctor guidance"),
            doctorNote = "One of the most celebrated adaptogens in Indian Ayurvedic pharmacopeia.",
            isDesi = true
        ),
        Remedy(
            id = "remedy_brahmi_shankhpushpi",
            nameEn = "Brahmi Mind-Calming Tonic",
            nameHi = "ब्राह्मी और शंखपुष्पी (Brahmi Brain Tonic)",
            category = "Sleep and Stress",
            ingredients = listOf("1/2 tsp Brahmi powder", "1/2 cup warm water", "1/2 tsp honey"),
            method = listOf("Dissolve Brahmi powder in warm water.", "Add honey and consume in the late afternoon or early evening."),
            benefits = listOf("Bacosides enhance neurotransmitter synthesis, improving mental clarity and reducing stress-induced burnout", "Promotes tranquil cognitive state"),
            precautions = listOf("Take on a light stomach"),
            whoShouldAvoid = listOf("Patients taking sedative-hypnotic prescription medications"),
            doctorNote = "Consult doctor if suffering from clinical depression or panic disorder.",
            isDesi = true
        ),
        Remedy(
            id = "remedy_warm_foot_soak",
            nameEn = "Epsom Salt Lavender Foot Soak",
            nameHi = "सेंधा नमक पैर सेक (Epsom Salt Foot Bath)",
            category = "Sleep and Stress",
            ingredients = listOf("1 basin filled with pleasantly warm water", "2 tbsp Epsom salt (magnesium sulfate)", "3 drops lavender oil"),
            method = listOf("Dissolve Epsom salt and lavender in basin of warm water.", "Soak feet for 15-20 minutes in a dim, quiet room.", "Dry feet thoroughly and slip into warm cotton socks."),
            benefits = listOf("Magnesium ions relax peripheral vascular tension", "Stimulates parasympathetic nervous system, signaling body it is time to rest"),
            precautions = listOf("Test water temperature with hand first to prevent burns"),
            whoShouldAvoid = listOf("Diabetic patients with severe peripheral neuropathy without foot sensation"),
            doctorNote = "Simple, deeply relaxing nighttime bedtime ritual.",
            isDesi = false
        ),

        // 34-36: Joint Pain
        Remedy(
            id = "remedy_ginger_turmeric_poultice",
            nameEn = "Ginger & Mustard Oil Warm Compress",
            nameHi = "सरसों का तेल और लहसुन (Mustard Oil Garlic Massage)",
            category = "Joint Pain",
            ingredients = listOf("3 tbsp pure mustard oil", "4-5 crushed garlic cloves", "1/2 tsp carom seeds (Ajwain)"),
            method = listOf("Heat mustard oil in a pan with crushed garlic and ajwain until garlic turns golden.", "Let it cool until comfortably warm.", "Massage gently onto aching knees or joints with light upward strokes.", "Wrap with warm flannel cloth for 15 minutes."),
            benefits = listOf("Allyl isothiocyanate in mustard oil induces gentle thermal vasodilation", "Garlic sulfur compounds inhibit inflammatory cytokine pathways"),
            precautions = listOf("Ensure oil is comfortably warm, never blistering hot"),
            whoShouldAvoid = listOf("Open wounds, skin abrasions, or acute joint effusion/infection"),
            doctorNote = "Joint swelling with warmth, redness, or inability to bear weight needs urgent orthopedic review.",
            isDesi = true
        ),
        Remedy(
            id = "remedy_fenugreek_sesame_churna",
            nameEn = "Sesame & Methi Joint Strengthening Powder",
            nameHi = "तिल और मेथी का चूर्ण (Til Methi Churna)",
            category = "Joint Pain",
            ingredients = listOf("2 tbsp white sesame seeds", "1 tbsp fenugreek seeds", "Warm water"),
            method = listOf("Lightly dry-roast sesame and fenugreek seeds.", "Grind into a coarse powder.", "Take 1 teaspoon every morning with warm water."),
            benefits = listOf("Sesame is exceptionally rich in bioavailable plant calcium and zinc", "Fenugreek reduces inflammatory stiffness in morning joints"),
            precautions = listOf("Drink sufficient water throughout day"),
            whoShouldAvoid = listOf("High uric acid gout attacks (consult rheumatologist)"),
            doctorNote = "Nutritious daily bone and joint support for middle-aged and elderly adults.",
            isDesi = true
        ),
        Remedy(
            id = "remedy_tart_cherry_juice",
            nameEn = "Tart Cherry Anti-Inflammatory Elixir",
            nameHi = "चेरी का अर्क (Tart Cherry Juice)",
            category = "Joint Pain",
            ingredients = listOf("1/2 cup 100% pure unsweetened tart cherry juice", "1/2 cup water"),
            method = listOf("Dilute pure tart cherry juice with water.", "Drink once daily in the evening."),
            benefits = listOf("High concentrations of anthocyanins lower serum C-reactive protein (CRP)", "Reduces post-exercise muscle soreness and gout flare-ups"),
            precautions = listOf("Choose 100% pure juice with no added high fructose corn syrup"),
            whoShouldAvoid = listOf("Individuals on potassium-sparing diuretics"),
            doctorNote = "Widely utilized and scientifically studied by athletic trainers for joint inflammation.",
            isDesi = false
        ),

        // 37-38: Women's Health
        Remedy(
            id = "remedy_sesame_jaggery_ladoo",
            nameEn = "Sesame & Jaggery Iron-Rich Bites",
            nameHi = "तिल और गुड़ के लड्डू (Til Gud for Cramps)",
            category = "Women's Health",
            ingredients = listOf("1/2 cup roasted black or white sesame seeds", "1/2 cup organic jaggery (Gud)", "1/4 tsp cardamom powder"),
            method = listOf("Melt jaggery gently, stir in roasted sesame seeds and cardamom.", "Roll into small marble-sized balls.", "Eat 1 bite-sized ball daily during the week leading up to menstrual cycle."),
            benefits = listOf("Abundant natural non-heme iron and zinc prevents fatigue and anemia", "Essential fatty acids help alleviate primary dysmenorrhea cramps"),
            precautions = listOf("Consume in moderation (1-2 small balls per day)"),
            whoShouldAvoid = listOf("Uncontrolled diabetes mellitus due to jaggery content"),
            doctorNote = "Severe incapacitating pelvic pain should always be medically checked for endometriosis.",
            isDesi = true
        ),
        Remedy(
            id = "remedy_fennel_ajwain_menstrual_tea",
            nameEn = "Fennel & Ajwain Period Cramp Reliever",
            nameHi = "सौंफ और अजवाइन का काढ़ा (Saunf Menstrual Tea)",
            category = "Women's Health",
            ingredients = listOf("1 tsp fennel seeds (Saunf)", "1/2 tsp carom seeds (Ajwain)", "1.5 cups water", "Small piece jaggery"),
            method = listOf("Boil fennel and ajwain seeds in water for 6-8 minutes.", "Strain into mug, add small piece of jaggery, and sip comfortably warm.", "Drink twice daily during painful menstrual cycle days."),
            benefits = listOf("Anethole in fennel relaxes smooth uterine muscles, curbing painful prostaglandins", "Carom seeds dispel trapped pelvic bloating"),
            precautions = listOf("Best consumed warm"),
            whoShouldAvoid = listOf("Pregnancy"),
            doctorNote = "Safe, soothing herbal tea for routine monthly discomfort.",
            isDesi = true
        ),

        // 39-40: Kids Care
        Remedy(
            id = "remedy_ajwain_potli_warm_compress",
            nameEn = "Warm Ajwain Potli (Chest Pouch for Toddlers)",
            nameHi = "अजवाइन की पोटली (Ajwain Potli for Babies)",
            category = "Kids Care",
            ingredients = listOf("2 tbsp carom seeds (Ajwain)", "Clean soft cotton handkerchief/cloth"),
            method = listOf("Dry roast ajwain on a tawa until aromatic (do not burn).", "Tie securely inside a soft cotton cloth to form a small pouch (potli).", "Test heat against your own inner wrist to ensure it is gently warm, not hot.", "Place near baby's pillow or gently compress chest over toddler's clothing."),
            benefits = listOf("Natural thymol vapors clear clogged baby nostrils without oral medication", "Gentle warmth relieves infant chest congestion safely"),
            precautions = listOf("NEVER apply directly to bare baby skin to prevent burns; always test temperature first"),
            whoShouldAvoid = listOf("Infants with severe acute respiratory distress (flaring nostrils, retractions)"),
            doctorNote = "Infants under 6 months with fever or breathing distress require immediate pediatrician care.",
            isDesi = true
        ),
        Remedy(
            id = "remedy_mashed_banana_curd",
            nameEn = "Probiotic Mashed Banana & Curd for Tummy",
            nameHi = "केला और दही (Kela Dahi for Kids Tummy)",
            category = "Kids Care",
            ingredients = listOf("1 ripe banana", "2 tbsp fresh homemade sweet curd/yogurt", "Pinch of roasted cumin powder"),
            method = listOf("Mash ripe banana thoroughly with a fork.", "Fold in fresh sweet curd and a tiny pinch of roasted cumin.", "Feed to child suffering from loose motions or upset tummy."),
            benefits = listOf("Pectin in banana firms up stool naturally", "Electrolyte potassium restores energy, while yogurt repopulates friendly intestinal flora"),
            precautions = listOf("Ensure curd is fresh and not sour"),
            whoShouldAvoid = listOf("Known dairy allergy"),
            doctorNote = "If child shows signs of dehydration (sunken eyes, no wet diapers for 6 hours), consult pediatrician immediately.",
            isDesi = true
        )
    )
}
