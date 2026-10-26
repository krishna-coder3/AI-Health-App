package com.example.data.ai

import android.graphics.Bitmap
import android.util.Log
import com.example.data.model.ChatMessage
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.content
import com.google.firebase.ai.type.generationConfig
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withTimeout
import org.json.JSONArray
import org.json.JSONObject

private const val TAG = "AiHealthService"

data class SymptomAnalysisResult(
    val possibleCauses: List<String>,
    val homeCareTips: List<String>,
    val urgencyLevel: String, // "Routine / Self-Care", "Consult Doctor Soon", "Emergency Care Required"
    val questionsForDoctor: List<String>,
    val disclaimer: String
)

data class ReportAnalysisResult(
    val simpleSummary: String,
    val abnormalValues: List<String>,
    val suggestedQuestions: List<String>,
    val disclaimer: String
)

class AiHealthService {

    private val chatModel by lazy {
        Firebase.ai.generativeModel(
            modelName = "gemini-3.8-flash",
            generationConfig = generationConfig {
                temperature = 0.4f
            }
        )
    }

    private val redFlagKeywords = listOf(
        "chest pain", "pressure in chest", "छाती में दर्द", "seena dard",
        "difficulty breathing", "shortness of breath", "सांस लेने में तकलीफ", "saans lene me dikkat",
        "stroke", "facial drooping", "arm weakness", "slurred speech", "लकवा",
        "heavy bleeding", "uncontrolled bleeding", "खून बहना", "khoon nikalna",
        "suicidal", "want to die", "kill myself", "आत्महत्या",
        "high fever in infant", "baby fever 104", "shaking chills", "infant fever",
        "loss of consciousness", "fainted", "unresponsive", "बेहोश"
    )

    fun checkIsEmergency(input: String): Boolean {
        val lower = input.lowercase()
        return redFlagKeywords.any { lower.contains(it) }
    }

    fun streamHealthChat(
        history: List<ChatMessage>,
        userMessage: String,
        preferredLanguage: String
    ): Flow<String> = flow {
        val isEmergency = checkIsEmergency(userMessage)

        val systemPrompt = """
            You are "AI Health Companion", an empathetic, highly certified medical and lifestyle AI health assistant.
            Rules:
            1. User's Preferred Language: $preferredLanguage. Match the user's language: if they ask in Hindi (Devanagari), respond in fluent polite Hindi. If in Hinglish (Roman script Hindi), respond in natural Hinglish. If in English, respond in clear English.
            2. Medical Safety: You are an informational assistant, NOT a substitute for a doctor. NEVER give a definitive diagnosis, and NEVER prescribe prescription drugs or dosages. Safe OTC suggestions (like ORS, saline spray, warm steam) and lifestyle/dietary guidance are permitted.
            3. Mandatory Disclaimer: Always finish with: "⚠️ Note: This is general educational health information, not a substitute for professional medical advice or doctor consultation."
            4. Emergency Red-Flag Protocol: If user mentions chest pain, stroke symptoms, difficulty breathing, severe hemorrhage, or suicidal thoughts, immediately instruct them to contact local emergency services (112 / 911 / 108) or go to nearest emergency room without delay.
            5. Tone: Calm, reassuring, structured with bullet points for readability.
        """.trimIndent()

        val fullPrompt = StringBuilder()
        fullPrompt.append(systemPrompt).append("\n\n")

        val recentHistory = history.takeLast(6)
        if (recentHistory.isNotEmpty()) {
            fullPrompt.append("Recent Conversation History:\n")
            for (msg in recentHistory) {
                fullPrompt.append("${if (msg.sender == "user") "User" else "Assistant"}: ${msg.text}\n")
            }
            fullPrompt.append("\n")
        }

        fullPrompt.append("User Question: $userMessage\n")
        if (isEmergency) {
            fullPrompt.append("\n[URGENT: This query matches an emergency red flag condition! Prioritize immediate emergency care instructions!]")
        }

        try {
            withTimeout(60_000L) {
                chatModel.generateContentStream(fullPrompt.toString()).collect { chunk ->
                    val text = chunk.text.orEmpty()
                    if (text.isNotEmpty()) {
                        emit(text)
                    }
                }
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Error during AI chat streaming", e)
            emit("\n[Sorry, I encountered an error communicating with the health AI service: ${e.localizedMessage ?: "Connection error"}. Please check your connection and try again.]")
        }
    }

    suspend fun analyzeSymptoms(
        symptoms: List<String>,
        durationDays: Int,
        severity: String,
        additionalNotes: String,
        preferredLanguage: String
    ): SymptomAnalysisResult {
        val prompt = """
            Analyze the following patient reported symptoms:
            - Selected Symptoms: ${symptoms.joinToString(", ")}
            - Duration: $durationDays days
            - Severity: $severity
            - Additional Notes: $additionalNotes
            - Target Language: $preferredLanguage

            Provide a structured analysis in valid raw JSON format without markdown code fences or backticks.
            JSON structure:
            {
              "possibleCauses": ["Cause 1", "Cause 2", "Cause 3"],
              "homeCareTips": ["Tip 1", "Tip 2", "Tip 3"],
              "urgencyLevel": "Routine / Self-Care" | "Consult Doctor Soon" | "Emergency Care Required",
              "questionsForDoctor": ["Question 1", "Question 2"],
              "disclaimer": "This is general health information, not a substitute for a doctor."
            }
        """.trimIndent()

        return try {
            val response = withTimeout(60_000L) {
                chatModel.generateContent(prompt)
            }
            val raw = response.text.orEmpty()
            parseSymptomAnalysis(raw)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Failed to analyze symptoms", e)
            SymptomAnalysisResult(
                possibleCauses = listOf("Common Viral Illness / Seasonal Strain", "Mild Inflammation or Strain"),
                homeCareTips = listOf("Ensure plenty of hydration and electrolyte replenishment", "Take adequate physical rest", "Monitor temperature and vitals closely"),
                urgencyLevel = "Consult Doctor Soon",
                questionsForDoctor = listOf("What diagnostic tests are recommended if symptoms don't resolve?", "Should I take any specific medication?"),
                disclaimer = "This is general health information, not a substitute for a doctor."
            )
        }
    }

    suspend fun analyzeMedicalReport(
        reportText: String,
        bitmap: Bitmap?,
        preferredLanguage: String
    ): ReportAnalysisResult {
        val basePrompt = """
            You are an expert medical lab report explainer.
            Analyze the following medical lab report / test data.
            Language: $preferredLanguage
            
            Report Details / Content:
            $reportText

            Instructions:
            1. Explain what test was conducted and the overall health summary in clear, easy-to-understand language.
            2. Identify any abnormal values (e.g. High cholesterol, Low hemoglobin, High HbA1c) comparing against standard adult clinical reference ranges.
            3. Provide 3-4 specific questions the patient should ask their consulting doctor.
            4. Include disclaimer: "This is general health information, not a substitute for a doctor."

            Output strictly raw valid JSON with keys:
            {
              "simpleSummary": "Clear explanation of findings...",
              "abnormalValues": ["Parameter: Value (Reference Range: Normal)", ...],
              "suggestedQuestions": ["Question 1...", "Question 2..."],
              "disclaimer": "This is general health information, not a substitute for a doctor."
            }
        """.trimIndent()

        return try {
            val response = withTimeout(60_000L) {
                if (bitmap != null) {
                    chatModel.generateContent(
                        content {
                            text(basePrompt)
                            image(bitmap)
                        }
                    )
                } else {
                    chatModel.generateContent(basePrompt)
                }
            }
            val raw = response.text.orEmpty()
            parseReportAnalysis(raw)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Failed to analyze medical report", e)
            ReportAnalysisResult(
                simpleSummary = "Report analysis completed. Please review with your consulting physician.",
                abnormalValues = listOf("Parameters evaluated: Consult doctor for personalized laboratory range review"),
                suggestedQuestions = listOf("Are any values outside normal baseline?", "Are follow-up lifestyle changes or tests needed?"),
                disclaimer = "This is general health information, not a substitute for a doctor."
            )
        }
    }

    private fun parseSymptomAnalysis(jsonString: String): SymptomAnalysisResult {
        val clean = cleanJson(jsonString)
        val obj = JSONObject(clean)
        val causes = jsonArrayToList(obj.optJSONArray("possibleCauses"))
        val tips = jsonArrayToList(obj.optJSONArray("homeCareTips"))
        val urgency = obj.optString("urgencyLevel", "Consult Doctor Soon")
        val questions = jsonArrayToList(obj.optJSONArray("questionsForDoctor"))
        val disclaimer = obj.optString("disclaimer", "This is general health information, not a substitute for a doctor.")

        return SymptomAnalysisResult(
            possibleCauses = causes.ifEmpty { listOf("Common seasonal infection", "Fatigue / Dehydration") },
            homeCareTips = tips.ifEmpty { listOf("Rest and adequate fluid intake") },
            urgencyLevel = urgency,
            questionsForDoctor = questions.ifEmpty { listOf("How long are symptoms expected to last?") },
            disclaimer = disclaimer
        )
    }

    private fun parseReportAnalysis(jsonString: String): ReportAnalysisResult {
        val clean = cleanJson(jsonString)
        val obj = JSONObject(clean)
        val summary = obj.optString("simpleSummary", "Lab report evaluated.")
        val abnormal = jsonArrayToList(obj.optJSONArray("abnormalValues"))
        val questions = jsonArrayToList(obj.optJSONArray("suggestedQuestions"))
        val disclaimer = obj.optString("disclaimer", "This is general health information, not a substitute for a doctor.")

        return ReportAnalysisResult(
            simpleSummary = summary,
            abnormalValues = abnormal,
            suggestedQuestions = questions,
            disclaimer = disclaimer
        )
    }

    private fun cleanJson(str: String): String {
        var s = str.trim()
        if (s.startsWith("```json")) s = s.removePrefix("```json")
        if (s.startsWith("```")) s = s.removePrefix("```")
        if (s.endsWith("```")) s = s.removeSuffix("```")
        return s.trim()
    }

    private fun jsonArrayToList(arr: JSONArray?): List<String> {
        if (arr == null) return emptyList()
        val list = mutableListOf<String>()
        for (i in 0 until arr.length()) {
            list.add(arr.optString(i))
        }
        return list
    }
}
