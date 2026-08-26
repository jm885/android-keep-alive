package dev.hossain.keepalive.service

import dev.hossain.keepalive.util.AppConfig.APP_LAUNCH_VERIFICATION_DELAY_MS
import dev.hossain.keepalive.util.AppConfig.MAX_APP_LAUNCH_ATTEMPTS
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import timber.log.Timber

class AppRecoveryManager(
    private val launchApp: (String) -> Unit,
    private val isAppRunning: suspend (String) -> Boolean,
    private val verificationDelayMs: Long =
        APP_LAUNCH_VERIFICATION_DELAY_MS,
    private val maxAttempts: Int =
        MAX_APP_LAUNCH_ATTEMPTS,
) {

    suspend fun ensureAppRunning(
        packageName: String,
        forceStart: Boolean = false,
    ): RecoveryResult {

        if (isAppRunning(packageName)) {
            if (!forceStart) {
                return RecoveryResult.AlreadyRunning
            }

            launchApp(packageName)
            return RecoveryResult.Started(attempts = 1)
        }

        repeat(maxAttempts) { attemptIndex ->

            val attemptNumber = attemptIndex + 1

            Timber.i(
                "Launching $packageName " +
                    "(attempt $attemptNumber/$maxAttempts)"
            )

            try {
                launchApp(packageName)
            } catch (exception: Exception) {
                if (exception is CancellationException) {
                    throw exception
                }
                Timber.e(exception, "Failed to launch $packageName on attempt $attemptNumber")
            }

            Timber.d(
                "Waiting ${verificationDelayMs}ms " +
                    "before verifying $packageName"
            )

            delay(verificationDelayMs)

            val running = isAppRunning(packageName)

            if (running) {
                Timber.i(
                    "$packageName successfully started " +
                        "after attempt $attemptNumber"
                )

                return RecoveryResult.Started(
                    attempts = attemptNumber,
                )
            }

            Timber.w(
                "$packageName failed launch verification " +
                    "after attempt $attemptNumber"
            )
        }

        Timber.e(
            "$packageName could not be started " +
                "after $maxAttempts attempts"
        )

        return RecoveryResult.Failed(
            attempts = maxAttempts,
        )
    }
}

sealed interface RecoveryResult {

    data object AlreadyRunning : RecoveryResult

    data class Started(
        val attempts: Int,
    ) : RecoveryResult

    data class Failed(
        val attempts: Int,
    ) : RecoveryResult
}
