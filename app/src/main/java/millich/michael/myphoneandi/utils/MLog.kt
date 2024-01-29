package millich.michael.myphoneandi.utils

import android.content.ContentValues
import android.content.Context
import android.provider.MediaStore
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import java.io.OutputStream
import kotlin.coroutines.CoroutineContext


class MLog private constructor( private val appContext: Context) {

    companion object {
        private val TAG= "MLOG"
        @Volatile private var instance: MLog? = null

        fun initialize(appContext: Context) {
            instance = MLog(appContext)
        }

        fun getInstance(): MLog {
            if (instance == null)
                throw IllegalStateException("MLog has not been initialized.")
            return instance!!
        }

        fun i(tag: String, message: String) {
            getInstance().logInfo(tag, message)
        }

        fun d(tag: String, message: String) {
            getInstance().logDebug(tag, message)
        }
        fun w(tag: String, message: String) {
            getInstance().logWarning(tag, message)
        }
        fun e(tag: String, message: String) {
            getInstance().logError(tag, message)
        }
        fun v(tag: String, message: String) {
            getInstance().logVerbose(tag, message)
        }


        // Other logging methods...
    }


    private var logFile : File? =null
    private var count =0


    private fun logInfo(tag: String, message: String) {
        Log.i(tag, message)
        writeToPrivateFile("[${getCurrentTimeInString()}][log $count]    I    [$tag]    $message",appContext)
    }

    private fun logDebug(tag: String, message: String ) {
        Log.d(tag, message)
        writeToPrivateFile("[${getCurrentTimeInString()}][log $count]    D    [$tag]    $message",appContext)
    }

    private fun logWarning(tag: String, message: String ) {
        Log.w(tag, message)
        writeToPrivateFile("[${getCurrentTimeInString()}][log $count]    W    [$tag]    $message",appContext)
    }

    private fun logError(tag: String, message: String ) {
        Log.e(tag, message)
        writeToPrivateFile("[${getCurrentTimeInString()}][log $count]    E    [$tag]    $message",appContext)
    }

    private fun logVerbose(tag: String, message: String ) {
        Log.e(tag, message)
        writeToPrivateFile("[${getCurrentTimeInString()}][log $count]    V    [$tag]    $message",appContext)
    }

    private fun writeToPrivateFile(logMessage: String ,context: Context) {
        count++
        CoroutineScope(Dispatchers.IO).launch {
            val file = getLogFile(context)
            file.appendText("$logMessage\n")
        }
    }

    private fun getLogFile(context:Context) : File {
        // if the file is already available, return it
        if (logFile!= null) {
            Log.d(TAG, "log file is not null, returning")
            return logFile!!
        }
        // else, check if the directory Logcat is available in Android/My.package.folder/data and create it if required
        val dir = File(context.getExternalFilesDir(null), "Logcat")
        Log.d(TAG, "log file is null, starting to make sure we save some information")
        Log.d(TAG, "location directory is ${dir.absolutePath}, does it exists? ${dir.exists()}")
        if (!dir.exists())
            dir.mkdirs()
        Log.d(TAG, "now creating the file")
        // create a new logging text file.
        logFile = File(dir, "log_${getCurrentTimeInStringUnderScore()}.txt")
        Log.d(TAG, "1) the file is ${logFile?.path}, does it exist? ${logFile?.exists()}")
        if (logFile?.exists() == false)
            logFile?.createNewFile()
        Log.d(TAG, "2) the file is ${logFile?.path}, does it exist? ${logFile?.exists()}")
        count =0
        return logFile!!
    }

}