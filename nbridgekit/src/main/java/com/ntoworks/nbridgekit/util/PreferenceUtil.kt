package com.ntoworks.nbridgekit.util

import android.content.Context
import android.content.SharedPreferences

class PreferenceUtil {
    companion object {
        @Volatile private var instance: PreferenceUtil? = null

        @JvmStatic fun getInstance() : PreferenceUtil =
            instance ?: synchronized(this) {
                instance ?: PreferenceUtil().also {
                    instance = it
                }
            }

        private lateinit var prefs : SharedPreferences
        private lateinit var editor : SharedPreferences.Editor
    }

    private lateinit var secretKey: String
    fun init(context: Context, secretKey: String = "9876543210abcedf") {
        try {
            prefs = context.getSharedPreferences("BridgeCorePreference", Context.MODE_PRIVATE)
            this.secretKey = secretKey
            editor = prefs.edit()
        } catch (e : Exception){
        }
    }

    fun clear() {
        editor.clear()
        editor.commit()
    }

    fun remove(key: String) {
        editor.remove(key)
        editor.commit()
    }

    fun putString(key: String, value: String) {
        editor.putString(key, value)
        editor.commit()
    }

    fun putCryptedString(key: String?, value: String?) {
        val cryptValue = AesUtil.getInstance().encrypt(value!!, secretKey)
        editor.putString(key, cryptValue)
        editor.commit()
    }

    fun getString(key: String, defValue: String): String {
        return prefs.getString(key, defValue) ?: defValue
    }

    fun getCryptedString(key: String?, defValue: String?): String? {
        val value = prefs.getString(key, null)
        if (value != null) {
            return AesUtil.getInstance().decrypt(value, secretKey)
        }
        return defValue
    }

    fun putBoolean(key: String?, value : Boolean) {
        editor.putBoolean(key, value)
        editor.commit()
    }

    fun getBoolean(key: String): Boolean {
        return prefs.getBoolean(key, false)
    }

    fun putLong(key: String?, value: Long) {
        editor.putLong(key, value)
        editor.commit()
    }

    fun getLong(key: String?, defValue: Long): Long {
        return prefs.getLong(key, defValue)
    }

    fun putInt(key: String?, value: Int) {
        editor.putInt(key, value)
        editor.commit()
    }

    fun getInt(key: String?, defValue: Int): Int {
        return prefs.getInt(key, defValue)
    }
}