package com.coursework.errorlogger

import android.util.Log

// Logs messages for information, warnings, and errors that occur in the program.
// Kotlin object used to share a single instance rather than creating an instance each time and separates logging from UI responsibility.
// (Android Developers - Log API Reference and Kotlin Documentation - Object Declarations)
object AppLogger {

    // Identifies our application's messages in Logcat.
    private const val TAG = "ErrorLogger"

    // Logs informational messages, such as startup or successful operations.
    // Log.i() writes an INFO-priority message to Logcat.
    fun info(message: String) {
        Log.i(TAG, message)
    }

    // Logs warnings when something unexpected happens but the app can continue.
    // Log.w() writes a WARN-priority message to Logcat.
    fun warning(message: String) {
        Log.w(TAG, message)
    }

    // Logs errors and optional exceptions when an operation fails.
    // Throwable? allows null; = null makes the exception parameter optional.
    // Log.e() includes the exception's stack trace when one is provided.
    fun error(message: String, exception: Throwable? = null) {
        Log.e(TAG, message, exception)
    }
}




