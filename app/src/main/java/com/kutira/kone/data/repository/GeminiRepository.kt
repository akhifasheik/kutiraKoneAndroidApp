package com.kutira.kone.data.repository

import com.kutira.kone.BuildConfig
import com.kutira.kone.utils.GeminiConstants
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

@Singleton
class GeminiRepository @Inject constructor() {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .build()

    suspend fun generateDesignIdeas(
        materialType: String,
        color: String,
        size: String
    ): Result<String> = withContext(Dispatchers.IO) {

        try {

            val apiKey = BuildConfig.GEMINI_API_KEY

            if (apiKey.isBlank()) {

                return@withContext Result.failure(
                    Exception("Gemini API key missing")
                )
            }

            val prompt = """
Generate creative upcycling and sustainable fashion ideas using the following leftover fabric.

Fabric Details:
Material: $materialType
Color: $color
Size: $size

Instructions:
- Generate unique ideas every time
- Suggest practical DIY products
- Include fashion accessories
- Include home decor ideas
- Include modern trendy ideas
- Keep ideas short and useful
- Use bullet points only
- Do not introduce yourself
- Do not explain anything
- Do not repeat the prompt
- Give at least 10 ideas
""".trimIndent()

            val requestJson = JSONObject().apply {

                put(
                    "contents",
                    JSONArray().put(

                        JSONObject().apply {

                            put(
                                "parts",
                                JSONArray().put(

                                    JSONObject().apply {
                                        put("text", prompt)
                                    }
                                )
                            )
                        }
                    )
                )

                put(
                    "generationConfig",
                    JSONObject().apply {

                        put("temperature", 0.9)

                        put("maxOutputTokens", 600)

                        put("topP", 0.9)

                        put("topK", 40)
                    }
                )
            }

            val body = requestJson.toString()
                .toRequestBody(JSON_MEDIA)

            val request = Request.Builder()

                .url(
                    "${GeminiConstants.GENERATE_CONTENT_URL}?key=$apiKey"
                )

                .addHeader(
                    "Content-Type",
                    "application/json"
                )

                .post(body)
                .build()

            client.newCall(request).execute().use { response ->

                val responseBody =
                    response.body?.string().orEmpty()

                if (!response.isSuccessful) {

                    return@withContext Result.failure(
                        Exception(
                            "Gemini Error ${response.code}: $responseBody"
                        )
                    )
                }

                val json = JSONObject(responseBody)

                val text =
                    json.getJSONArray("candidates")
                        .getJSONObject(0)
                        .getJSONObject("content")
                        .getJSONArray("parts")
                        .getJSONObject(0)
                        .getString("text")

                Result.success(text)
            }

        } catch (e: Exception) {

            Result.failure(e)
        }
    }

    companion object {

        private val JSON_MEDIA =
            "application/json; charset=utf-8".toMediaType()
    }
}