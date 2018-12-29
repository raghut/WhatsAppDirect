package com.direct.whatsapp.rgu.whatsappdirect.view.activity

import android.app.Activity
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.MenuItem
import com.direct.whatsapp.rgu.whatsappdirect.adapter.CallLogAdapter
import kotlinx.android.synthetic.main.activity_call_log.*
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import android.support.v7.widget.DividerItemDecoration
import com.direct.whatsapp.rgu.whatsappdirect.R
import com.direct.whatsapp.rgu.whatsappdirect.Utils
import com.direct.whatsapp.rgu.whatsappdirect.Utils.KEY_PHONE_NUMBER
import com.direct.whatsapp.rgu.whatsappdirect.data.CallLogData


class CallLogActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_call_log)

        title = "Call Logs"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        initViews()

        loadCallLogs()
    }

    fun initViews() {
        call_log_rv.layoutManager = LinearLayoutManager(this)

        val dividerItemDecoration = DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL)
        call_log_rv.addItemDecoration(dividerItemDecoration)
    }

    private fun loadCallLogs() {
        Utils.getCallDetails(this)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    call_log_rv.adapter = CallLogAdapter(it, getClickListener())
                }, { onLoadLogsFailed() })
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }

        return super.onOptionsItemSelected(item)
    }

    private fun getClickListener(): CallLogAdapter.Listener {
        return object : CallLogAdapter.Listener {
            override fun onItemClick(logData: CallLogData) {
                intent.putExtra(KEY_PHONE_NUMBER, logData.phoneNumber)
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
        }
    }

    fun onLoadLogsFailed() {
        Log.d("rgu", "onLoadLogsFailed")
    }
}
