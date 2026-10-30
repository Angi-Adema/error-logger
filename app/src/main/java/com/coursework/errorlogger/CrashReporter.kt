package com.coursework.errorlogger

import android.content.Context
import android.util.Log
import java.io.PrintWriter
import java.io.StringWriter

// Saves uncaught exception info to the application's internal storage.
// Transfers control to Android's original handler after saving the report.
// (Android Developers - Thread.UncaughtExceptionHandler and Context.openFileOutput)
object CrashReporter {

    // Variable to store the most recent crash report.
    private const val FILE_NAME = "crash_report.txt"

    // Register a handler to capture uncaught exceptions.
    fun initialize(context: Context) {

        // Access internal storage using the application context.
        val appContext = context.applicationContext

        // Save the existing handler enabling Android to process the crash.
        val previousHandler = Thread.getDefaultUncaughtExceptionHandler()

        // Define what happens in the event of an uncaught exception. This helps identify where the error happened.
        Thread.setDefaultUncaughtExceptionHandler { thread, exception ->

            // Convert the exception stack trace into readable text.
            val stackTrace = StringWriter().also { writer ->
                exception.printStackTrace(PrintWriter(writer))
            }.toString()

            // Create a report to display crash details using String templates to insert the thread and exception info.
            val report = """
                Thread: ${thread.name}
                Exception: ${exception.javaClass.name}
                Message: ${exception.message}
                
                Stack trace:
                $stackTrace
            """.trimIndent()

            // Attempt to save the report before the process terminates. MODE_PRIVATE writes to the app's internal storage.
            // (Android Developers - Access app-specific files)
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
                // Delegate to the previous handler or terminate if none exists.
                if (previousHandler != null) {
                    previousHandler.uncaughtException(thread, exception)
                } else {
                    android.os.Process.killProcess(android.os.Process.myPid())
                    kotlin.system.exitProcess(10)
                }
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

