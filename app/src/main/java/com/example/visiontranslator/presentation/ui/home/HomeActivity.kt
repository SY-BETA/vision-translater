package com.example.visiontranslator.presentation.ui.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.visiontranslator.AppApplication
import com.example.visiontranslator.R
import com.example.visiontranslator.databinding.ActivityHomeBinding
import com.example.visiontranslator.presentation.ui.dialog.ErrorDialog
import com.example.visiontranslator.presentation.ui.edit.EditActivity
import com.example.visiontranslator.presentation.ui.preview.PreviewActivity
import com.example.visiontranslator.presentation.ui.translation.TranslationActivity
import com.example.visiontranslator.util.EventObserver
import kotlinx.android.synthetic.main.activity_home.*
import javax.inject.Inject

/**
 * translation modelのリストを表示するホーム画面
 * launch activity
 */
class HomeActivity : AppCompatActivity() {

    companion object {
        @JvmStatic
        fun createIntent(context: Context): Intent =
            Intent(context, HomeActivity::class.java).apply {
            }
    }

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel: HomeViewModel by lazy {
        ViewModelProvider(this, viewModelFactory).get(HomeViewModel::class.java)
    }

    private lateinit var binding: ActivityHomeBinding
    private lateinit var homeController: HomeEpoxyController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppApplication.component.inject(this)
        // Data Binding
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home)
        binding.let {
            lifecycle.addObserver(viewModel)
            it.lifecycleOwner = this@HomeActivity
            it.viewmodel = viewModel
        }

        setupEpoxy()
        setupSearchView()
        setupNavigation()
        setupDialog()
    }

    /**
     * 翻訳した文と画像をリスト表示するEpoxyを生成
     */
    private fun setupEpoxy() {
        homeController = HomeEpoxyController(viewModel)
        binding.homeEpoxy.adapter = homeController.adapter
        viewModel.translationList.observe(this) { translationList ->
            homeController.setData(translationList)
        }
    }

    /**
     * 検索インターフェースの設定
     */
    private fun setupSearchView() {
        // クエリリスナ
        binding.searchView.setOnQueryTextListener(
            object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    binding.searchView.clearFocus()
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    viewModel.setSearchQuery(newText)
                    return true
                }
            }
        )
        // searchViewのクリック領域を全体に
        viewModel.focusSearchViewEvent.observe(this, EventObserver {
            if (it) {
                binding.searchView.apply {
                    isIconified = false
                }
            }
        })
    }

    /**
     * 画面遷移イベント定義
     */
    private fun setupNavigation() {
        viewModel.openImageSelectEvent.observe(this, EventObserver {
            Toast.makeText(this, R.string.testDataNav, Toast.LENGTH_LONG).show()
            val intent = TranslationActivity.createIntent(this)
            startActivity(intent)
        })

        viewModel.openEditEvent.observe(this, EventObserver {
            val intent = EditActivity.createIntent(this)
            startActivity(intent)
        })

        viewModel.openPreviewEvent.observe(this, EventObserver {
            val intent = PreviewActivity.createIntent(this, it)
            startActivity(intent)
        })
    }

    /**
     * ダイアログの設定
     */
    private fun setupDialog() {
        viewModel.errorMsg.observe(this, EventObserver { errorMsg ->
            showErrorDialog("check error log 'HOME_ACTIVITY'\n" + errorMsg)
        })
    }

    /**
     * ローカルからTranslationListを取得しなおしてビューを更新
     */
    private fun refreshTranslationList() = viewModel.loadTranslations()

    private fun showErrorDialog(errorMsg: String) = ErrorDialog.showDialog(supportFragmentManager, errorMsg = errorMsg)
}
