package `in`.phoenix.myspends.ui.activity

import `in`.phoenix.myspends.R
import `in`.phoenix.myspends.util.AppUtil
import `in`.phoenix.myspends.util.WhatsAppUtil
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.view.inputmethod.EditorInfo
import androidx.appcompat.widget.Toolbar
import kotlinx.android.synthetic.main.activity_whats_app.*

class WhatsAppTextActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_whats_app)

        init()
    }

    private fun init() {
        val toolbar = findViewById<Toolbar>(R.id.awaToolbar)
        toolbar.setTitle(R.string.text_on_whatsapp)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(true)

        /*awaEtCode.addTextChangedListener(textWatcher)
        awaEtMobile.addTextChangedListener(textWatcher)*/
        awaEtMobile.setOnEditorActionListener { v, actionId, event ->
            return@setOnEditorActionListener if (actionId == EditorInfo.IME_ACTION_DONE) {
                handleWhatsAppApi()
                true
            } else {
                false
            }
        }

        awaBtnText.setOnClickListener { handleWhatsAppApi() }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    private fun handleWhatsAppApi() {
        val code = awaEtCode.text.trim().toString()
        val mobileNumber = awaEtMobile.text.trim().toString()

        if (code.isEmpty() || mobileNumber.isEmpty()) {
            AppUtil.showToast("Enter both Code and WhatsApp Number.")
            return
        }

        if (mobileNumber.isNotEmpty() && mobileNumber.length < 10) {
            AppUtil.showToast("WhatsApp Number should have 10 digits.")
            return
        }

        if (AppUtil.isWhatsAppInstalled(packageManager)) {
            val waIntent = WhatsAppUtil.generateCustomChooserIntent(WhatsAppTextActivity@this, code.plus(mobileNumber))
            waIntent?.let {
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
            awaBtnText.isEnabled = awaEtCode.text.trim().isNotEmpty() && awaEtMobile.text.trim().isNotEmpty()
        }
    }
}