package it.cybernetics.itunesapp

import android.content.Context
import java.text.SimpleDateFormat
import java.util.*

object Utils {

    val DATE_FORMAT = SimpleDateFormat()
    val DATE_PATTERN_1 = "MMM dd, yyyy hh:mm a"

    val PREFS_KEY = "it.cybernetics.itunesapp.PRAISE_GOD"
    val LAST_ACCESS = "last_access"

    fun writePrefsString(context: Context, key: String, value: String?) {
        val sharedPref = context.getSharedPreferences(PREFS_KEY, Context.MODE_PRIVATE)
        val edit = sharedPref.edit()
        edit.putString(key, value)
        edit.commit()
    }

    fun readPrefs(context: Context, key: String): String? {
        val sharedPref = context.getSharedPreferences(PREFS_KEY, Context.MODE_PRIVATE)
        return sharedPref.getString(key, null)
    }

    fun formatDate(date: Date, pattern: String): String {
        DATE_FORMAT.applyPattern(pattern)
        return DATE_FORMAT.format(date)
    }
}