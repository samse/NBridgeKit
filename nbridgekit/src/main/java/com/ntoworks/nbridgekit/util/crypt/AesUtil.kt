package com.ntoworks.nbridgekit.util

import android.annotation.SuppressLint
import android.util.Base64
import java.nio.charset.StandardCharsets
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import javax.crypto.BadPaddingException
import javax.crypto.Cipher
import javax.crypto.IllegalBlockSizeException
import javax.crypto.NoSuchPaddingException
import javax.crypto.spec.SecretKeySpec

class AesUtil {

    companion object {
        @Volatile private var instance: AesUtil? = null

        @JvmStatic fun getInstance() : AesUtil =
            instance ?: synchronized(this) {
                instance ?: AesUtil().also {
                    instance = it
                }
            }
        private const val AES = "AES"
        private const val AES_ECB_PKCS5 = "AES/ECB/PKCS5Padding"
    }

    /**
     * EncryptUtil 암호화
     * @param data 암호화할 데이터
     * @param secretKey 고정키
     * @return 암호화된 데이터
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws InvalidKeyException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     */
    @SuppressLint("GetInstance")
    @Throws(
        NoSuchAlgorithmException::class,
        NoSuchPaddingException::class,
        InvalidKeyException::class,
        IllegalBlockSizeException::class,
        BadPaddingException::class
    )
    fun encrypt(data: String, secretKey: String): String {
        val textBytes: ByteArray = data.toByteArray()
        val secKeySpec = SecretKeySpec(secretKey.toByteArray(), AES)
        val cipher = Cipher.getInstance(AES_ECB_PKCS5)
        cipher.init(Cipher.ENCRYPT_MODE, secKeySpec)
        return Base64.encodeToString(cipher.doFinal(textBytes), Base64.DEFAULT)
    }

    /**
     * EncryptUtil 복호화 - 서버에서 진행하는 프로세스로 테스트용 함수
     * @param encData 암호화된 데이터
     * @param secretKey 고정키
     * @return 복호화 된 데이터
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws InvalidKeyException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     */
    @SuppressLint("GetInstance")
    @Throws(
        NoSuchAlgorithmException::class,
        NoSuchPaddingException::class,
        InvalidKeyException::class,
        IllegalBlockSizeException::class,
        BadPaddingException::class
    )
    fun decrypt(encData: String?, secretKey: String): String {
        val textBytes = Base64.decode(encData, Base64.DEFAULT)
        val secKeySpec = SecretKeySpec(secretKey.toByteArray(), AES)
        val cipher = Cipher.getInstance(AES_ECB_PKCS5)
        cipher.init(Cipher.DECRYPT_MODE, secKeySpec)
        return String(cipher.doFinal(textBytes), StandardCharsets.UTF_8)
    }
}