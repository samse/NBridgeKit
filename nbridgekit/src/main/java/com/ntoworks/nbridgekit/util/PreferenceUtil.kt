package com.ntoworks.nbridgekit.util

import android.content.Context
import android.content.SharedPreferences
import androidx.annotation.NonNull
import com.ntoworks.nbridgekit.util.crypt.AndroidRsaCipherHelper
import com.ntoworks.nbridgekit.util.crypt.SecureSharedPreferences

class PreferenceUtil {
    companion object {
        @Volatile private var instance: PreferenceUtil? = null

        @JvmStatic fun getInstance() : PreferenceUtil =
            instance ?: synchronized(this) {
                instance ?: PreferenceUtil().also {
                    instance = it
                }
            }

    }

    private lateinit var securePrefs: SecureSharedPreferences

    private var secretKey: String? = null
    private var secretKeyByteArray: ByteArray? = null
    private lateinit var prefs : SharedPreferences
    private lateinit var editor : SharedPreferences.Editor

    /**
     * RSA 암호화 방식 적용
     * 256자 이상의 데이터는 저장하지 못함.
     */
    fun init(context: Context) {
        try {
            AndroidRsaCipherHelper.init(context)
            prefs = context.getSharedPreferences("BridgeCorePreference", Context.MODE_PRIVATE)
            editor = prefs.edit()
            securePrefs = SecureSharedPreferences(prefs)
            secretKey = null
            secretKeyByteArray = null
        } catch (e : Exception){
        }
    }

    /**
     * AES256 암호화 방식 적용
     */
    fun init(context: Context, secretKey: String = "9876543210abcedf") {
        try {
            AndroidRsaCipherHelper.init(context)
            prefs = context.getSharedPreferences("BridgeCorePreference", Context.MODE_PRIVATE)
            this.secretKey = secretKey
            editor = prefs.edit()
        } catch (e : Exception){
        }
    }

    /**
     * AES256 암호화 방식 적용
     */
    fun init(context: Context, secretKey: ByteArray = "9876543210abcedf".toByteArray()) {
        try {
            AndroidRsaCipherHelper.init(context)
            prefs = context.getSharedPreferences("BridgeCorePreference", Context.MODE_PRIVATE)
            this.secretKeyByteArray = secretKey
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

    fun putString(key: String, @NonNull value: String) {
        editor.putString(key, value)
        editor.commit()
    }

    fun putCryptedString(key: String, @NonNull value: String) {
        if (secretKey!=null) {
            val cryptValue = AesUtil.getInstance().encrypt(value!!, secretKey!!)
            editor.putString(key, cryptValue)
            editor.commit()
            return
        } else if(secretKeyByteArray!=null) {
            val cryptValue = AesUtil.getInstance().encrypt(value!!, secretKeyByteArray!!)
            editor.putString(key, cryptValue)
            editor.commit()
            return
        }
        securePrefs.put(key= key, value= value)
    }

    fun getString(key: String, @NonNull defValue: String): String {
        return prefs.getString(key, defValue) ?: defValue
    }

    fun getCryptedString(key: String, @NonNull defValue: String): String? {
        if (secretKey!=null) {
            val value = prefs.getString(key, null)
            if (value != null) {
                return AesUtil.getInstance().decrypt(value, secretKey!!)
            }
            return defValue
        } else if(secretKeyByteArray!=null) {
            val value = prefs.getString(key, null)
            if (value != null) {
                return AesUtil.getInstance().decrypt(value, secretKeyByteArray!!)
            }
            return defValue
        }
        return securePrefs.get(key = key, defaultValue = defValue)
    }

    fun putBoolean(key: String?, value : Boolean) {
        editor.putBoolean(key, value)
        editor.commit()
    }

    fun getBoolean(key: String, defValue: Boolean): Boolean {
        return prefs.getBoolean(key, defValue)
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