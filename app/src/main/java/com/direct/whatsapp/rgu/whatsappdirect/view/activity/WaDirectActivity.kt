package com.direct.whatsapp.rgu.whatsappdirect.view.activity

import android.app.Activity
import android.content.ClipDescription.MIMETYPE_TEXT_PLAIN
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.TextInputLayout
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import com.direct.whatsapp.rgu.whatsappdirect.R
import com.direct.whatsapp.rgu.whatsappdirect.Utils
import com.direct.whatsapp.rgu.whatsappdirect.Utils.KEY_PHONE_NUMBER
import com.direct.whatsapp.rgu.whatsappdirect.Utils.WHATS_APP_URL
import com.hbb20.CountryCodePicker
import io.michaelrocks.libphonenumber.android.NumberParseException
import java.net.URLEncoder

class WaDirectActivity : AppCompatActivity() {

    private val TAG = "WaDirectActivity"
    val CALL_LOG_REQUEST = 100

    private lateinit var countryCodePickerView: CountryCodePicker
    private lateinit var phoneNumTil: TextInputLayout
    private lateinit var messageEt: EditText
    private var shouldShowClipboard = true;


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wa_direct)
        countryCodePickerView = findViewById(R.id.country_code_picker_view)
        phoneNumTil = findViewById(R.id.phone_no_til)
        messageEt = findViewById(R.id.messageEt)

        findViewById<Button>(R.id.submit_btn).setOnClickListener {
            handleSendButton();
        }
    }

    override fun onResume() {
        super.onResume()

        if (shouldShowClipboard) {
            handleClipboardPopUp()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.call_log_menu, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.call_history_item -> {
                val intent = Intent(this, CallLogActivity::class.java)
                startActivityForResult(intent, CALL_LOG_REQUEST)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun handleSendButton() {
        if (!TextUtils.isEmpty(phoneNumTil.editText!!.text)) {
            sendMessage(countryCodePickerView.selectedCountryCode + phoneNumTil.editText!!.text,  messageEt.text.toString())
        } else {
            phoneNumTil.setError(getString(R.string.phone_no_empty_error))
        }
    }

    private fun sendMessage(phoneNo: String, message: String = "") {

        val countryCode = ""
        try {
            val intent = Intent(Intent.ACTION_VIEW)
            val url = WHATS_APP_URL + countryCode + phoneNo + "&text=" + URLEncoder.encode(message, "UTF-8")

            intent.`package` = "com.whatsapp"
            intent.data = Uri.parse(url)
            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun handleClipboardPopUp() {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        if (clipboard.hasPrimaryClip() && clipboard.getPrimaryClipDescription().hasMimeType(MIMETYPE_TEXT_PLAIN)) {
            val clipText = clipboard.primaryClip.getItemAt(0).text
            if (!TextUtils.isEmpty(clipText) && Utils.isValidPhone(clipText.toString())) {
                val builder = AlertDialog.Builder(this)
                builder.setMessage(getString(R.string.clipboard_popup_message) + clipText)
                        .setPositiveButton(getString(R.string.ok)) { dialog, which ->
                            handleClipText(clipText)
                        }
                        .setNegativeButton(getString(R.string.cancel)) { dialog, which ->
                            handleClipboardPopUpDismiss()
                        }
                        .setOnDismissListener { handleClipboardPopUpDismiss() }

                builder.create().show()
            }
        }
    }

    private fun handleClipboardPopUpDismiss() {
        shouldShowClipboard = false
    }

    private fun handleClipText(clipText: CharSequence) {
        handleSelectedCallLogItem(clipText.toString())
    }

    private fun handleSelectedCallLogItem(phNumber: String) {

        try {
            val extractedPhoneNo = Utils.getExtractedPhoneNo(phNumber, this)

            Log.d(TAG, "phoneNo= " + extractedPhoneNo.nationalNumber + " country code= " + extractedPhoneNo.countryCode)

            countryCodePickerView.setCountryForPhoneCode(extractedPhoneNo.countryCode)
            phoneNumTil.editText!!.setText(extractedPhoneNo.nationalNumber.toString())

            sendMessage(phNumber, "");
        } catch (e: NumberParseException) {
            Log.e(TAG, " NumberParseException was thrown : " + e.toString());
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CALL_LOG_REQUEST && resultCode == Activity.RESULT_OK) {
            handleSelectedCallLogItem(data?.getStringExtra(KEY_PHONE_NUMBER)!!)
        }
    }
}
