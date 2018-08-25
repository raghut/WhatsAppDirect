package com.direct.whatsapp.rgu.whatsappdirect.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.direct.whatsapp.rgu.whatsappdirect.R
import com.direct.whatsapp.rgu.whatsappdirect.data.CallLogData
import com.direct.whatsapp.rgu.whatsappdirect.view.viewholder.CallLogViewHolder

class CallLogAdapter(val logsData: ArrayList<CallLogData>) : RecyclerView.Adapter<CallLogViewHolder>() {

    override fun getItemCount(): Int {
        return logsData.size
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): CallLogViewHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.item_call_log_layout, parent, false)

        return CallLogViewHolder(view)
    }

    override fun onBindViewHolder(holder: CallLogViewHolder, position: Int) {
        holder.bind(logsData.get(position))
    }

}