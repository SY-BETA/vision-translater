package com.example.visiontranslator.domain.translation

import android.net.Uri
import com.example.visiontranslator.infra.model.translation.Translation
import com.example.visiontranslator.infra.repository.translation.TranslationRepository
import com.example.visiontranslator.infra.service.ocr.OCRService
import com.example.visiontranslator.infra.service.translate.TranslateService
import com.example.visiontranslator.util.ConstantKey.ErrorMsg.ERROR_NO_TEXT
import javax.inject.Inject

/**
 * UseCaseは今プロジェクトにおいては画面単位で作成する
 *
 * 翻訳画面TranslationActivityで使用するuseCaseを実装したクラス
 */
class TranslationUseCaseImpl
@Inject constructor(
    private val ocrService: OCRService,
    private val translateService: TranslateService,
    private val translationRepository: TranslationRepository
) : TranslationUseCase {

    /**
     * 画像から文字をOCRで検出し、翻訳した文字列を取得し翻訳結果をローカルに保存する
     * OCRServiceで画像からテキストを検出
     * そのテキストをTranslateServiceで翻訳
     * 翻訳が完了したらローカルDBに保存する
     */
    override suspend fun translateText(imgUri: Uri): Long {
        val detectedTranslation = detectTextFromImg(imgUri)
        if (detectedTranslation.originalText.trim() == "") throw Exception(ERROR_NO_TEXT)
        val translatedText = translateText(detectedTranslation)
        return insertTranslation(
            detectedTranslation.apply {
                this.translatedText = translatedText
            }
        )
    }

    /**
     * Google Vision API で画像からテキストを検出
     */
    private suspend fun detectTextFromImg(imgUri: Uri): Translation = ocrService.getDetectedDescription(imgUri)

    /**
     * GASでテキストを翻訳する
     */
    private suspend fun translateText(translation: Translation): String =
        translateService.translateText(
            text = translation.originalText,
            source = translation.sourceLang,
            target = translation.targetLang
        ).body()?.text ?: ""

    /**
     * TranslationモデルをローカルDBに保存する
     */
    private suspend fun insertTranslation(translation: Translation): Long = translationRepository.insertTranslation(translation)

}