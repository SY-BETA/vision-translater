package com.example.visiontranslator.infra.repository.translation

import com.example.visiontranslator.infra.model.translation.Translation

/**
 * MainViewModelに提供するデータプロバイダメソッド、モデル操作メソッドの定義
 */
interface TranslationRepository {

    /**
     * すべてのローカルTranslationモデルデータを取得
     */
    suspend fun getAllTranslations(): List<Translation>

    /**
     * 新規Translationモデルを保存
     * @return autoGeneratedID
     */
    suspend fun insertTranslation(translation: Translation): Long

    /**
     * Translationモデルをアップデート
     */
    suspend fun updateTranslation(translation: Translation)

    /**
     * Translationモデルの削除
     */
    suspend fun deleteTranslation(translationId: Long)

    /**
     * 複数のTranslationモデルの削除
     */
    suspend fun deleteTranslations(translationIdList: List<Long>)

    /**
     * すべてのTranslationsモデル削除
     */
    suspend fun deleteAllTranslations()

    /**
     * IDからTranslationモデル取得
     */
    suspend fun findTranslationByID(id: Long): Translation?

    /**
     * 一覧からキーワード検索を行い結果をリストとして取得
     */
    suspend fun findTranslationByQueryWord(queryWord: String): List<Translation>

    /**
     * テスト用のTranslationモデルを生成
     */
    fun getTranslationForTestCase(): Translation

    /**
     * テスト用のoriginalTextを生成
     */
    fun getOriginalTextForTestCase(): String
}
