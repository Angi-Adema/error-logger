package com.coursework.errorlogger

import android.content.Context
import android.util.Log
import java.io.PrintWriter
import java.io.StringWriter

// Saves uncaught exception info to the application's private storage.
// Delegates to Android's original handler after saving the report.
// References: Java Thread.UncaughtExceptionHandler and Android Context.
object CrashReporter {

    private const val FILE_NAME = "crash_report.txt"

    fun initialize(context: Context) {
        val appContext = context.applicationContext

        // Preserve the existing handler so normal crash handling continues.
        val previousHandler = Thread.getDefaultUncaughtExceptionHandler()

        Thread.setDefaultUncaughtExceptionHandler { thread, exception ->

            // Convert the exception stack trace into readable text.
            val stackTrace = StringWriter().also { writer ->
                exception.printStackTrace(PrintWriter(writer))
            }.toString()

            val report = """
                Thread: ${thread.name}
                Exception: ${exception.javaClass.name}
                Message: ${exception.message}
                
                Stack trace:
                $stackTrace
            """.trimIndent()

            // Attempt to save the report before the process terminates.
            try {
                appContext.openFileOutput(
                    FILE_NAME,
                    Context.MODE_PRIVATE
                ).bufferedWriter().use { writer ->
                    writer.write(report)
                }
            } catch (e: Exception) {
                Log.e("ErrorLogger", "Failed to save crash report", e)
            } finally {
                // Allow Android's original handler to process the crash.
                previousHandler?.uncaughtException(thread, exception)
            }
        }
    }

    // Returns the saved report, or null if no report exists.
    fun readReport(context: Context): String? {
        return try {
            context.openFileInput(FILE_NAME)
                .bufferedReader()
                .use { it.readText() }
        } catch (e: java.io.FileNotFoundException) {
            null
        } catch (e: Exception) {
            AppLogger.error("Unable to read crash report", e)
            null
        }
    }
}

