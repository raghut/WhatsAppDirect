package com.direct.whatsapp.rgu.whatsappdirect.view.viewholder

import android.provider.CallLog
import android.support.v7.widget.RecyclerView
import android.view.View
import com.direct.whatsapp.rgu.whatsappdirect.R
import com.direct.whatsapp.rgu.whatsappdirect.data.CallLogData
import kotlinx.android.synthetic.main.item_call_log_layout.view.*

class CallLogViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bind(logData: CallLogData) = with(itemView) {
        itemView.name_no_tv.text = logData.phoneNumber
        itemView.time_tv.text = logData.date

        val dircode = Integer.parseInt(logData.callType)

        var resId = 0
        when (dircode) {
            CallLog.Calls.OUTGOING_TYPE -> resId = R.drawable.vc_call_outgoing

            CallLog.Calls.INCOMING_TYPE -> resId = R.drawable.vc_call_received

            CallLog.Calls.MISSED_TYPE -> resId = R.drawable.vc_call_missed
        }

        itemView.log_type_iv.setImageResource(resId)
    }
}