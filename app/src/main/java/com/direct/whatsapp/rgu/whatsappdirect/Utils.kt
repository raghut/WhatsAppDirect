package com.direct.whatsapp.rgu.whatsappdirect

import android.content.Context
import java.util.regex.Pattern
import android.provider.CallLog
import android.text.TextUtils
import android.util.Log
import com.direct.whatsapp.rgu.whatsappdirect.data.CallLogData
import rx.Observable
import java.lang.Long
import java.util.*
import kotlin.collections.ArrayList


object Utils {

    val WHATS_APP_URL = "https://api.whatsapp.com/send?phone="

    fun isValidPhone(phone: String): Boolean {
        val expression = "^([0-9\\+]|\\(\\d{1,3}\\))[0-9\\-\\. ]{3,15}$"
        val pattern = Pattern.compile(expression)
        val matcher = pattern.matcher(phone)
        return matcher.matches()
    }

    fun getCallDetails(context: Context): Observable<ArrayList<CallLogData>> {

        val callsData = ArrayList<CallLogData>()

        val sb = StringBuffer()
        val contactsCursor = context.contentResolver.query(CallLog.Calls.CONTENT_URI, null, null, null, null)
        val number = contactsCursor.getColumnIndex(CallLog.Calls.NUMBER)
        val type = contactsCursor.getColumnIndex(CallLog.Calls.TYPE)
        val date = contactsCursor.getColumnIndex(CallLog.Calls.DATE)
        val duration = contactsCursor.getColumnIndex(CallLog.Calls.DURATION)
        sb.append("Call Details :")
        while (contactsCursor.moveToNext()) {
            val callData = CallLogData()

            callData.phoneNumber = contactsCursor.getString(number)
            callData.callType = contactsCursor.getString(type)
            callData.date = contactsCursor.getString(date)
            callData.dayTime = Date(Long.valueOf(callData.date))
            callData.duration = contactsCursor.getString(duration)

            var dir: String? = null
            val dircode = Integer.parseInt(callData.callType)
            when (dircode) {
                CallLog.Calls.OUTGOING_TYPE -> dir = "OUTGOING"

                CallLog.Calls.INCOMING_TYPE -> dir = "INCOMING"

                CallLog.Calls.MISSED_TYPE -> dir = "MISSED"
            }
            Log.d("raghu", "\nPhone Number:--- " + callData.phoneNumber + " \nCall Type:--- "
                    + dir + " \nCall Date:--- " + callData.dayTime
                    + " \nCall duration in sec :--- " + callData.duration)
            sb.append("\n----------------------------------")

            if (!TextUtils.isEmpty(dir)) {
                callsData.add(callData)
            }
        }
        contactsCursor.close()

        return Observable.just(callsData)

    }
}
