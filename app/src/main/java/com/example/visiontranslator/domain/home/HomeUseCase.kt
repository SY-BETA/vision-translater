package com.example.visiontranslator.domain.home

import com.example.visiontranslator.infra.model.translation.Translation

/**
 * ホーム画面(翻訳済みのデータをリスト表示する画面)で使用するmodel, api操作のメソッド定義
 */
interface HomeUseCase {
    /**
     * 一覧表示するためにTranslationModelのデータをすべてリストとして取得
     */
    suspend fun getAllTranslations(): MutableMap<Translation, Boolean>

    /**
     * 一覧からキーワード検索を行い結果をリストとして取得
     */
    suspend fun findTranslationByQueryWord(queryWord: String): MutableMap<Translation, Boolean>

    /**
     * idからTranslation モデルを削除
     */
    suspend fun deleteTranslations(translationIdList: List<Long>)

    /**
     * テスト用のTranslationモデルを生成し保存
     */
    suspend fun insertTranslationTestCase()
}
