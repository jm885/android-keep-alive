package dev.hossain.keepalive.service

import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class AppRecoveryManagerTest {

    @Test
    fun `already running app is not launched`() = runTest {
        var launches = 0

        val manager = AppRecoveryManager(
            launchApp = { launches++ },
            isAppRunning = { true },
            verificationDelayMs = 0L,
            maxAttempts = 3,
        )

        val result =
            manager.ensureAppRunning("com.example.app")

        assertEquals(
            RecoveryResult.AlreadyRunning,
            result,
        )

        assertEquals(0, launches)
    }

    @Test
    fun `force start does not retry an already running app`() = runTest {
        var launches = 0

        val manager = AppRecoveryManager(
            launchApp = { launches++ },
            isAppRunning = { true },
            verificationDelayMs = 10_000L,
            maxAttempts = 3,
        )

        val result =
            manager.ensureAppRunning(
                packageName = "com.example.app",
                forceStart = true,
            )

        assertEquals(
            RecoveryResult.Started(attempts = 1),
            result,
        )

        assertEquals(1, launches)
    }

    @Test
    fun `successful launch stops retrying`() = runTest {
        var launches = 0

        val manager = AppRecoveryManager(
            launchApp = { launches++ },
            isAppRunning = {
                launches > 0
            },
            verificationDelayMs = 0L,
            maxAttempts = 3,
        )

        val result =
            manager.ensureAppRunning("com.example.app")

        assertEquals(
            RecoveryResult.Started(attempts = 1),
            result,
        )

        assertEquals(1, launches)
    }

    @Test
    fun `failed launch is retried`() = runTest {
        var launches = 0

        val manager = AppRecoveryManager(
            launchApp = { launches++ },
            isAppRunning = {
                launches >= 2
            },
            verificationDelayMs = 0L,
            maxAttempts = 3,
        )

        val result =
            manager.ensureAppRunning("com.example.app")

        assertEquals(
            RecoveryResult.Started(attempts = 2),
            result,
        )

        assertEquals(2, launches)
    }

    @Test
    fun `failed launch stops at maximum attempts`() = runTest {
        var launches = 0

        val manager = AppRecoveryManager(
            launchApp = { launches++ },
            isAppRunning = { false },
            verificationDelayMs = 0L,
            maxAttempts = 3,
        )

        val result =
            manager.ensureAppRunning("com.example.app")

        assertEquals(
            RecoveryResult.Failed(attempts = 3),
            result,
        )

        assertEquals(3, launches)
    }
}
