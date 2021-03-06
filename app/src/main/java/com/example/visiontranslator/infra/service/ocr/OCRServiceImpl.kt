package com.example.visiontranslator.infra.service.ocr

import android.content.Context
import android.net.Uri
import com.example.visiontranslator.infra.model.translation.Translation
import com.example.visiontranslator.util.ConstantKey.ErrorMsg
import com.google.api.client.extensions.android.json.AndroidJsonFactory
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.services.vision.v1.Vision
import com.google.api.services.vision.v1.VisionRequestInitializer
import com.google.api.services.vision.v1.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import javax.inject.Inject

/**
 * OCRのリクエストを送るメソッドを実装したリポジトリ
 */
class OCRServiceImpl @Inject constructor(private val context: Context) : OCRService {

    companion object {
        const val TEXT_DETECTION = "TEXT_DETECTION"
    }

    /**
     * 別スレッドで動作
     * Google Vision API にリクエストを送りOCRの検出結果を取得
     */
    override suspend fun getDetectedDescription(detectImgUri: Uri): Translation = withContext(Dispatchers.Default) {
        withTimeout(10000) {
//            TODO API Key の管理法
            val vision = Vision.Builder(NetHttpTransport(), AndroidJsonFactory(), null)
                .setVisionRequestInitializer(VisionRequestInitializer("AIzaSyDu8KlpFCS_UwGAY3sfXXjXQ9ElaXctEQ8"))
                .build()
            val bytes = context.contentResolver.openInputStream(detectImgUri)?.readBytes() ?: throw Exception(ErrorMsg.ERROR_GOOGLE_VISION_API)
            val annotateRequest = AnnotateImageRequest().apply {
                image = Image().encodeContent(bytes)
                // 今回はTEXT_DETECTIONのみだが、物体検知などの場合はFeatureをここに追加する
                features = listOf(Feature().setType(TEXT_DETECTION))
            }
            val batchAnnotateRequest = BatchAnnotateImagesRequest().apply {
                // 複数のファイルを一度に扱うときはここにリクエストを追加
                requests = listOf(annotateRequest)
            }
            // リクエストを送り、画像のテキスト検出結果を取得
            val response = vision.images().annotate(batchAnnotateRequest).execute() ?: throw Exception(ErrorMsg.ERROR_GOOGLE_VISION_API)
            val annotation: List<EntityAnnotation>? = if (response.responses[0] != null) response.responses[0].textAnnotations else null

            return@withTimeout Translation(
                originalText = annotation?.get(0)?.description ?: "",
                sourceLang = annotation?.get(0)?.locale ?: "en",
                translatedText = "",
                targetLang = "ja",
                imgUri = detectImgUri.toString()
            )
        }
    }
}