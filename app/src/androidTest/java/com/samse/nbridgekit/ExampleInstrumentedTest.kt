package com.samse.nbridgekit

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.ntoworks.nbridgekit.util.PreferenceUtil

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.samse.nbridgekit", appContext.packageName)
    }

    @Test
    fun preference_symmetric_test(){
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        PreferenceUtil.getInstance().init(appContext, "1234567890123456");

        val plainText = "1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890" +
                "1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890" +
                "1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890"
        PreferenceUtil.getInstance().putCryptedString("key1", plainText)
        val decryptedText = PreferenceUtil.getInstance().getCryptedString("key1", "")
        assertEquals(plainText, decryptedText);
    }

    @Test
    fun preference_assymmetric_test(){
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        PreferenceUtil.getInstance().init(appContext);

        val plainText = "1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890" +
                "1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890"
        PreferenceUtil.getInstance().putCryptedString("key2", plainText)
        val decryptedText = PreferenceUtil.getInstance().getCryptedString("key2", "")
        assertEquals(plainText, decryptedText);
    }
    @Test
    fun preference_assymmetric_fail_test(){
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        PreferenceUtil.getInstance().init(appContext);

        val plainText = "1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890" +
                "1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890" +
                "1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890" +
                "1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890"
        PreferenceUtil.getInstance().putCryptedString("key2", plainText)
        val decryptedText = PreferenceUtil.getInstance().getCryptedString("key2", "")
        assertNotEquals(plainText, decryptedText);
    }
}