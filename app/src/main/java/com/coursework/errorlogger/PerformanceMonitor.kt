package com.coursework.errorlogger

import android.os.SystemClock

// Measures and records how long an operation takes using a monotonic clock.
// SystemClock.elapsedRealtime() measures elapsed time in milliseconds.
// (Android Developers - SystemClock)
object PerformanceMonitor {

    fun measureOperation(operation: () -> Unit): Long {

        // Record the starting time.
        val startTime = SystemClock.elapsedRealtime()

        // Execute the operation being measured.
        operation()

        // Calculate the elapsed execution time.
        val duration = SystemClock.elapsedRealtime() - startTime

        // Record the result using our custom logger.
        // (JetBrains - Strings)
        AppLogger.info("Operation completed in $duration ms")

        return duration
    }
}


