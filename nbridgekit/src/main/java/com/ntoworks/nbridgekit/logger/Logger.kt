package nbridgekit.logger

import android.annotation.SuppressLint
import com.ntoworks.nbridgekit.BuildConfig
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.*

class Logger {
    companion object {
        const val controlStackNumber = 3
        private const val defaultStackNumber = 2

        @JvmStatic var isEnabled: Boolean = true
        @JvmStatic var fileTarget: String? = null

        @JvmStatic
        fun debug(message: String? = null, stackNumber: Int = defaultStackNumber) {
            if(BuildConfig.DEBUG) {
                val stack = Throwable().stackTrace[stackNumber]
                printLog("🐛 DEBUG", stack, message.toString())

                fileTarget?.let {
                    logPrintToFile("DEBUG   $message")
                }
            }
        }

        @JvmStatic
        fun warning(message: String? = "", stackNumber: Int = defaultStackNumber) {
            if(BuildConfig.DEBUG) {
                val stack = Throwable().stackTrace[stackNumber]
                printLog("⚠️ WARNING", stack, message)

                fileTarget?.let {
                    logPrintToFile("WARNING   $message")
                }
            }
        }

        @JvmStatic
        fun error(message: String? = "", stackNumber: Int = defaultStackNumber) {
            if(BuildConfig.DEBUG) {
                val stack = Throwable().stackTrace[stackNumber]
                printLog("\uD83D\uDEAB ERROR", stack, message)

                fileTarget?.let {
                    logPrintToFile("ERROR   $message")
                }
            }
        }

        @JvmStatic
        fun info(message: String? = "", stackNumber: Int = defaultStackNumber) {
            if(BuildConfig.DEBUG) {
                val stack = Throwable().stackTrace[stackNumber]
                printLog("ℹ️ INFO", stack, message)

                fileTarget?.let {
                    logPrintToFile("INFO   $message")
                }
            }
        }

        @JvmStatic
        fun callFromWeb(message: String? = "", stackNumber: Int = defaultStackNumber) {
            if(BuildConfig.DEBUG) {
                val stack = Throwable().stackTrace[stackNumber]
                printLog("️📨 CALL", stack, message)

                fileTarget?.let {
                    logPrintToFile("CALL   $message")
                }
            }
        }

        @JvmStatic
        fun sendFromNative(message: String? = "", stackNumber: Int = defaultStackNumber) {
            if(BuildConfig.DEBUG) {
                val stack = Throwable().stackTrace[stackNumber]
                printLog("📬 SEND", stack, message)

                fileTarget?.let {
                    logPrintToFile("SEND   $message")
                }
            }
        }

        @JvmStatic
        fun request(message: String? = "", stackNumber: Int = defaultStackNumber) {
            if(BuildConfig.DEBUG) {
                val stack = Throwable().stackTrace[stackNumber]
                printLog("⬆️ REQUEST", stack, message)

                fileTarget?.let {
                    logPrintToFile("REQUEST   $message")
                }
            }
        }

        @JvmStatic
        fun response(message: String? = "", stackNumber: Int = defaultStackNumber) {
            if(BuildConfig.DEBUG) {
                val stack = Throwable().stackTrace[stackNumber]
                printLog("⬇️ RESPONSE", stack, message)

                fileTarget?.let {
                    logPrintToFile("RESPONSE   $message")
                }
            }
        }

        private var fileWriter: FileWriter? = null
        @JvmStatic
        fun logPrintToFile(message: String?) {
            if(fileWriter !=null) {
                if (isEnabled) {
                    fileTarget?.let {
                        val logFile = File(it)
                        fileWriter = FileWriter(logFile, true)
                    }
                }
                fileWriter?.let {
                    it.append("[${timeStamp()}] $message")
                    it.appendLine()
                    it.close()
                }
            }
        }

        @JvmStatic
        fun directLogPrintToFile(message: String?) {
            if(fileWriter !=null) {
                fileTarget?.let {
                    val logFile = File(it)
                    fileWriter = FileWriter(logFile, true).apply {
                        append("[${timeStamp()}] $message")
                        appendLine()
                        close()
                    }
                }
            }
        }

        private fun printLog(level: String, stack:StackTraceElement, message: String?) {
            println(" ")
            println("▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼")
            println("$level ${timeStamp()}")
            println("className : ${stack.className}")
            println("fun : ${stack.methodName} [${stack.lineNumber}]")
            println("-------------------------------------------------------------------------------------------------")
            println("$message")
            println("▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲")
            println(" ")
        }

        @SuppressLint("SimpleDateFormat")
        private fun timeStamp(): String {
            return SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        }
    }

}