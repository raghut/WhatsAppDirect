package com.direct.whatsapp.rgu.whatsappdirect

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
                    call_log_rv.adapter = CallLogAdapter(it)
                }, { onLoadLogsFailed() })
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }

        return super.onOptionsItemSelected(item)
    }

    fun onLoadLogsFailed() {
        Log.d("rgu", "onLoadLogsFailed")
    }
}
