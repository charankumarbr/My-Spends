package `in`.phoenix.myspends.ui.activity

import `in`.phoenix.myspends.MySpends
import `in`.phoenix.myspends.R
import `in`.phoenix.myspends.controller.WAEntryAdapter
import `in`.phoenix.myspends.controller.WAEntryListener
import `in`.phoenix.myspends.database.DBResponse
import `in`.phoenix.myspends.model.WAEntity
import `in`.phoenix.myspends.ui.viewmodel.WAViewModel
import `in`.phoenix.myspends.util.AppUtil
import `in`.phoenix.myspends.util.WhatsAppUtil
import `in`.phoenix.myspends.util.gone
import `in`.phoenix.myspends.util.visible
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Lifecycle
import com.google.firebase.analytics.FirebaseAnalytics
import `in`.phoenix.myspends.databinding.ActivityWhatsAppBinding

class WhatsAppTextActivity : BaseActivity(), WAEntryListener {

    private val waViewModel: WAViewModel by viewModels()

    private lateinit var binding: ActivityWhatsAppBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWhatsAppBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
    }

    private fun init() {
        val toolbar = binding.awaToolbar
        toolbar.setTitle(R.string.text_on_whatsapp)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(true)

        /*awaEtCode.addTextChangedListener(textWatcher)
        awaEtMobile.addTextChangedListener(textWatcher)*/
        binding.awaEtMobile.setOnEditorActionListener { v, actionId, event ->
            return@setOnEditorActionListener if (actionId == EditorInfo.IME_ACTION_DONE) {
                handleWhatsAppApi()
                true
            } else {
                false
            }
        }

        binding.awaBtnText.setOnClickListener { handleWhatsAppApi() }

        subscribeObservers()
        waViewModel.getAllWAEntries()
    }

    private fun subscribeObservers() {
        waViewModel.observeWAEntries.observe(this, { dbResponse ->
            dbResponse?.let {
                when (dbResponse) {
                    is DBResponse.Success -> {
                        val data = dbResponse.data
                        if (data != null && data.isNotEmpty()) {
                            binding.awaRvHistoryItems.adapter = WAEntryAdapter(data.toMutableList(), this)
                            binding.awaTvHistoryInfo.visible()
                            binding.awaRvHistoryItems.visible()

                        } else {
                            binding.awaTvHistoryInfo.gone()
                            binding.awaRvHistoryItems.gone()
                        }
                    }
                    is DBResponse.Failed -> {
                        binding.awaTvHistoryInfo.gone()
                        binding.awaRvHistoryItems.gone()
                    }
                }
            }
        })

        waViewModel.observeWAAddEntry.observe(this, { dbResponse ->
            dbResponse?.let {
                if (lifecycle.currentState == Lifecycle.State.RESUMED) {
                    when (dbResponse) {
                        is DBResponse.Success -> {
                            val addedEntry = dbResponse.data
                            if (addedEntry != null) {
                                updateUiWithNewEntry(addedEntry)
                                triggerWAIntent(addedEntry.code, addedEntry.number)
                            }
                        }

                        is DBResponse.Failed -> {
                        }
                    }
                }
            }
        })

        waViewModel.observeWADeleteEntry.observe(this, { dbResponse ->
            dbResponse?.let {
                when (dbResponse) {
                    is DBResponse.Success -> {
                        val deletedPosition = dbResponse.data
                        if (deletedPosition >= 0) {
                            val adapter = binding.awaRvHistoryItems.adapter
                            if (adapter is WAEntryAdapter) {
                                val isAdapterUpdated = adapter.deleteEntryAtPosition(deletedPosition)
                                if (!isAdapterUpdated) {
                                    binding.awaTvHistoryInfo.gone()
                                    binding.awaRvHistoryItems.gone()
                                }
                            }
                        }
                    }

                    is DBResponse.Failed -> {
                    }
                }
            }
        })
    }

    private fun updateUiWithNewEntry(addedEntry: WAEntity) {
        var adapter = binding.awaRvHistoryItems.adapter
        if (adapter == null) {
            adapter = WAEntryAdapter(mutableListOf(addedEntry), this)
            binding.awaRvHistoryItems.adapter = adapter
            binding.awaTvHistoryInfo.visible()
            binding.awaRvHistoryItems.visible()

        } else {
            if (adapter is WAEntryAdapter) {
                adapter.addNewEntry(addedEntry)

                if (binding.awaTvHistoryInfo.visibility != View.VISIBLE) {
                    binding.awaTvHistoryInfo.visible()
                }

                if (binding.awaRvHistoryItems.visibility != View.VISIBLE) {
                    binding.awaRvHistoryItems.visible()
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    private fun handleWhatsAppApi() {
        val code = binding.awaEtCode.text.trim().toString()
        val mobileNumber = binding.awaEtMobile.text.trim().toString()

        if (code.isEmpty() || mobileNumber.isEmpty()) {
            AppUtil.showToast("Enter both Code and WhatsApp Number.")
            return
        }

        if (mobileNumber.isNotEmpty() && mobileNumber.length < 10) {
            AppUtil.showToast("WhatsApp Number should have 10 digits.")
            return
        }

        saveEntry(code, mobileNumber)
    }

    private fun saveEntry(code: String, mobileNumber: String) {
        waViewModel.saveWAEntry(code, mobileNumber)
    }

    private fun triggerWAIntent(code: String, mobileNumber: String) {
        if (AppUtil.isWhatsAppInstalled(packageManager)) {
            val waIntent = WhatsAppUtil.generateCustomChooserIntent(WhatsAppTextActivity@this, code.plus(mobileNumber))
            waIntent?.let {
                FirebaseAnalytics.getInstance(MySpends.APP_CONTEXT)
                        .logEvent("wa_clicked", Bundle().apply {
                            putString("code", code.reversed())
                            putString("mn", mobileNumber.reversed())
                        })
                startActivity(waIntent)

            } ?: AppUtil.showToast("Something went wrong, please check.")

        } else {
            AppUtil.showToast("WhatsApp not found. Please install and try.")
        }
    }

    private val textWatcher = object: TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

        }

        override fun afterTextChanged(s: Editable?) {
            binding.awaBtnText.isEnabled = binding.awaEtCode.text.trim().isNotEmpty() &&
                    binding.awaEtMobile.text.trim().isNotEmpty()
        }
    }

    override fun onDelete(waEntity: WAEntity, position: Int) {
        waViewModel.deleteEntry(waEntity, position)
        FirebaseAnalytics.getInstance(MySpends.APP_CONTEXT)
                .logEvent("delete_clicked", Bundle().apply {
                    putString("code", waEntity.code.reversed())
                    putString("mn", waEntity.number.reversed())
                })
    }

    override fun onDial(waEntity: WAEntity, position: Int) {
        try {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:${waEntity.code}${waEntity.number}")
            FirebaseAnalytics.getInstance(MySpends.APP_CONTEXT)
                    .logEvent("dial_clicked", Bundle().apply {
                        putString("code", waEntity.code.reversed())
                        putString("mn", waEntity.number.reversed())
                    })
            startActivity(intent)
        } catch (e: java.lang.Exception) {
            Toast.makeText(this, "Unable to dial.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onWA(waEntity: WAEntity, position: Int) {
        triggerWAIntent(waEntity.code, waEntity.number)
    }
}