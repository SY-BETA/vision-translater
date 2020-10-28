package com.example.visiontranslator.presentation.ui.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.visiontranslator.AppApplication
import com.example.visiontranslator.R
import com.example.visiontranslator.databinding.ActivityHomeBinding
import com.example.visiontranslator.infra.model.translation.Translation
import com.example.visiontranslator.presentation.ui.preview.PreviewActivity
import com.example.visiontranslator.presentation.ui.translation.TranslationActivity
import com.example.visiontranslator.util.EventObserver
import kotlinx.android.synthetic.main.activity_home.*
import javax.inject.Inject


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

    private var controllerHome: HomeEpoxyController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppApplication.component.inject(this)
        // Data Binding
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home)
        binding.run {
            lifecycleOwner = this@HomeActivity
            viewmodel = viewModel
        }

        setupEpoxy()
        setupNavigation()

    }

    /**
     * 翻訳した文と画像をリスト表示するEpoxyを生成
     *
     */
    fun setupEpoxy() {
        controllerHome = HomeEpoxyController(viewModel)
        controllerHome!!.setData("")
        epoxyRecyclerViewMain.adapter = controllerHome!!.adapter
    }


    fun setupNavigation() {
        viewModel.openImageSelectEvent.observe(this, EventObserver {
            val intent = Intent(this, TranslationActivity::class.java)
            startActivity(intent)
        })

        viewModel.openEditEvent.observe(this, EventObserver {
            val intent = Intent(this, TranslationActivity::class.java)
            startActivity(intent)
        })

        viewModel.openSettingEvent.observe(this, EventObserver {
            val intent = Intent(this, TranslationActivity::class.java)
            startActivity(intent)
        })

        viewModel.openPreviewEvent.observe(this, EventObserver {
            val intent = Intent(this, PreviewActivity::class.java)
            startActivity(intent)
        })
    }

}