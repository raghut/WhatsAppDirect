package com.direct.whatsapp.rgu.whatsappdirect.view.viewholder

import android.provider.CallLog
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.View
import com.direct.whatsapp.rgu.whatsappdirect.R
import com.direct.whatsapp.rgu.whatsappdirect.Utils
import com.direct.whatsapp.rgu.whatsappdirect.adapter.CallLogAdapter
import com.direct.whatsapp.rgu.whatsappdirect.data.CallLogData
import kotlinx.android.synthetic.main.item_call_log_layout.view.*

class CallLogViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bind(logData: CallLogData, listener: CallLogAdapter.Listener) = with(itemView) {
        itemView.name_no_tv.text = if (TextUtils.isEmpty(logData.name)) logData.phoneNumber else logData.name
        itemView.time_tv.text = Utils.getTime(logData.date!!.toLong())

        val dircode = Integer.parseInt(logData.callType)

        var resId = 0
        when (dircode) {
            CallLog.Calls.OUTGOING_TYPE -> resId = R.drawable.vc_outgoing_call

            CallLog.Calls.INCOMING_TYPE -> resId = R.drawable.vc_income_call

            CallLog.Calls.MISSED_TYPE -> resId = R.drawable.vc_missed_call
        }

        itemView.log_type_iv.setImageResource(resId)

        itemView.setOnClickListener { listener.onItemClick(logData) }
    }
}