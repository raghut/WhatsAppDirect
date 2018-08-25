package com.direct.whatsapp.rgu.whatsappdirect.view.activity

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
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import com.direct.whatsapp.rgu.whatsappdirect.R
import com.direct.whatsapp.rgu.whatsappdirect.Utils
import com.direct.whatsapp.rgu.whatsappdirect.Utils.WHATS_APP_URL
import java.net.URLEncoder

class WaDirectActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wa_direct)

        findViewById<Button>(R.id.submit_btn).setOnClickListener {
            val phoneNum = findViewById<TextInputLayout>(R.id.phone_no_til).editText?.text.toString()
            sendMessage("91" + phoneNum)
        }
    }

    override fun onResume() {
        super.onResume()
        handleClipboardPopUp()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.call_log_menu, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        when (item?.itemId) {
            R.id.call_history_item -> {
                val intent = Intent(this, CallLogActivity::class.java)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
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
            val clipText = clipboard.getPrimaryClip().getItemAt(0).text
            if (!TextUtils.isEmpty(clipText) && Utils.isValidPhone(clipText.toString())) {
                val builder = AlertDialog.Builder(this)
                builder.setMessage("Do you want to send it to " + clipText)
                        .setPositiveButton("ok", { dialog, which -> handleClipText(clipText) })
                        .setNegativeButton("cancel", { dialog, which -> dialog.dismiss() })

                builder.create().show()
            }
        }
    }

    private fun handleClipText(clipText: CharSequence) {
        sendMessage(clipText.toString())
    }

}
