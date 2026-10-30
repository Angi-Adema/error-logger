package com.coursework.errorlogger

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.coursework.errorlogger.ui.theme.ErrorLoggerTheme
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState

// MainActivity launches the app and initializes the Compose UI.
// (Android Developers - Activity lifecycle and Jetpack Compose)
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Register the crash handler before executing application operations.
        // This allows uncaught exceptions to be saved to internal storage.
        CrashReporter.initialize(applicationContext)

        // Record an informational message when the Activity starts.
        AppLogger.info("ErrorLogger has started!")

        setContent {
            ErrorLoggerTheme {
                ErrorLoggerScreen()
            }
        }
    }
}

// Displays the app's test buttons and the latest result.
// Compose updates the displayed result when its state changes.
// (Android Developers - State and Jetpack Compose)
@Composable
fun ErrorLoggerScreen() {

    // Stores the latest test result for display on the screen.
    var result by remember {
        mutableStateOf("Select a test to begin.")
    }

    // Provides access to the application's context for reading saved reports.
    // Android Developers - LocalContext
    val context = LocalContext.current

    // Organize the UI using Android Developers - Scaffold.
    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Text(
                text = "Error Logger",
                style = MaterialTheme.typography.headlineMedium
            )

            Text("Test application logging and error handling.")

            // Records information and warning messages in Logcat.
            Button(
                onClick = {
                    AppLogger.info("Information test executed")
                    AppLogger.warning("Warning test executed")
                    result = "Information and warning messages logged."
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Test Logging")
            }

            // Demonstrates handling an exception without crashing.
            // (Android Developers - Log API and Kotlin - Exceptions)
            Button(
                onClick = {
                    try {
                        // Intentionally executes invalid.toInt() which throws a NumberFormatException.
                        "invalid".toInt()
                    // Instead of the application crashing, it is caught here and recorded in Logcat.
                    } catch (e: NumberFormatException) {
                        AppLogger.error("Invalid number conversion", e)
                        result = "Error caught and recorded in Logcat."
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Test Error Handling")
            }

            // Measures execution time and records the result in Logcat.
            // (Android Developers - SystemClock)
            Button(
                onClick = {
                    val duration = PerformanceMonitor.measureOperation {
                        // Simulates a computational operation for PerformanceMonitor to measure.
                        var total = 0L
                        for (i in 1..1_000_000) {
                            total += i
                        }
                    }

                    result = "Operation completed in $duration ms."
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Test Performance")
            }

            // Intentionally generates an uncaught exception to test crash reporting.
            // Intentionally throws an IllegalStateException without catching it and will close the app and save the report.
            Button(
                onClick = {
                    throw IllegalStateException("Test crash for ErrorLogger")
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Test Crash Reporting")
            }

            // Retrieves the saved crash report after the app restarts.
            // (Android Developers - Context.openFileInput)
            Button(
                onClick = {
                    val report = CrashReporter.readReport(context)

                    result = if (report != null) {
                        "Saved crash report:\n\n$report"
                    } else {
                        "No saved crash report found."
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("View Crash Report")
            }

            HorizontalDivider()

            Text(
                text = "Latest Result",
                style = MaterialTheme.typography.titleMedium
            )

            Text(result)
        }
    }
}
