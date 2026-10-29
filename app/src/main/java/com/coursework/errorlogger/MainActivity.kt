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

// MainActivity launches the app and initializes the Compose UI.
// (Android Developers - Activity lifecycle and Jetpack Compose)
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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

    // Organize the UI using Android Developers - Scaffold.
    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .padding(innerPadding)
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

            HorizontalDivider()

            Text(
                text = "Latest Result",
                style = MaterialTheme.typography.titleMedium
            )

            Text(result)
        }
    }
}
