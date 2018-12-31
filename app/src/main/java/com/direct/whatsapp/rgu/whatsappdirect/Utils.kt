package com.direct.whatsapp.rgu.whatsappdirect

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.CallLog
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import com.direct.whatsapp.rgu.whatsappdirect.data.CallLogData
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil
import io.michaelrocks.libphonenumber.android.Phonenumber
import rx.Observable
import java.lang.Long
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern
import kotlin.collections.ArrayList


object Utils {

    //Constants
    val KEY_PHONE_NUMBER = "key_phone_number"

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
        val contactsCursor = context.contentResolver.query(CallLog.Calls.CONTENT_URI, null, null, null, CallLog.Calls.DATE + " DESC")
        val number = contactsCursor.getColumnIndex(CallLog.Calls.NUMBER)
        val name = contactsCursor.getColumnIndex(CallLog.Calls.CACHED_NAME)
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
            callData.name = contactsCursor.getString(name)

            var dir: String? = null
            val dircode = Integer.parseInt(callData.callType)
            when (dircode) {
                CallLog.Calls.OUTGOING_TYPE -> dir = "OUTGOING"

                CallLog.Calls.INCOMING_TYPE -> dir = "INCOMING"

                CallLog.Calls.MISSED_TYPE -> dir = "MISSED"
            }
            Log.d("call log", "\nPhone Number:--- " + callData.phoneNumber + " \nCall Type:--- "
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

    fun getExtractedPhoneNo(phoneNo: String, context: Context): Phonenumber.PhoneNumber {
        val phoneNoUtil = PhoneNumberUtil.createInstance(context)
        return phoneNoUtil.parse(phoneNo, "")
    }


    fun getTime(dateInMillis: kotlin.Long): String {

        val formatter = SimpleDateFormat("dd/MM/yyyy HH:MM")
        return formatter.format(Date(dateInMillis))
    }

    /**
     * Opens Application Settings.
     *
     * To be used when user has denied or blocked Permission.
     *
     * @param context Context
     */
    fun openApplicationSettings(context: Context) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + context.packageName))
        intent.addCategory(Intent.CATEGORY_DEFAULT)
        context.startActivity(intent)
    }

    fun openPlayStorForRating(context: Context) {
        val uri = Uri.parse("market://details?id=" + context.getPackageName())
        val goToMarket = Intent(Intent.ACTION_VIEW, uri)
        // To count with Play market backstack, After pressing back button,
        // to taken back to our application, we need to add following flags to intent.
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY or
                Intent.FLAG_ACTIVITY_NEW_DOCUMENT or
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
        try {
            context.startActivity(goToMarket)
        } catch (e: ActivityNotFoundException) {
            context.startActivity(Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + context.getPackageName())))
        }
    }

    fun shareApp(context: Context) {
        try {
            val i = Intent(Intent.ACTION_SEND)
            i.type = "text/plain"
            i.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.app_name))
            var sAux = "\n" + context.getString(R.string.share_app_desc) + "\n\n"
            sAux = sAux + "https://play.google.com/store/apps/details?id=" + context.getPackageName() + "\n\n"
            i.putExtra(Intent.EXTRA_TEXT, sAux)
            context.startActivity(Intent.createChooser(i, "Choose one"))
        } catch (e: Exception) {
            //e.toString();
        }

    }
}
