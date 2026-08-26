package dev.hossain.keepalive.util

/**
 * Central configuration constants for the Keep Alive application.
 *
 * These constants define default values, minimums, and UI slider boundaries
 * for the app-check interval, as well as timing for multi-app checks.
 */
object AppConfig {
    /** The minimum allowable app-check interval, in minutes. Enforced by [dev.hossain.keepalive.data.SettingsRepository.appCheckIntervalFlow]. */
    const val MINIMUM_APP_CHECK_INTERVAL_MIN = 1

    /** The default app-check interval used when no value has been configured, in minutes. */
    const val DEFAULT_APP_CHECK_INTERVAL_MIN = 1

    /** Delay introduced between consecutive app checks when monitoring multiple apps, in milliseconds. */
    const val DELAY_BETWEEN_MULTIPLE_APP_CHECKS_MS = 2_000L

    /** Maximum launch attempts during one recovery cycle. */
    const val MAX_APP_LAUNCH_ATTEMPTS = 10

    /** Delay before verifying a launch and starting the next recovery attempt, in milliseconds. */
    const val APP_LAUNCH_VERIFICATION_DELAY_MS = 10_000L

    /** The minimum value displayed on the app-check interval slider in the settings UI, in minutes. */
    const val MIN_APP_CHECK_INTERVAL_SLIDER = 1

    /** The maximum allowable app-check interval, in minutes. */
    const val MAXIMUM_APP_CHECK_INTERVAL_MIN = 15

    /** The maximum value displayed on the app-check interval slider in the settings UI, in minutes. */
    const val MAX_APP_CHECK_INTERVAL_SLIDER = MAXIMUM_APP_CHECK_INTERVAL_MIN

    /** The step size for the app-check interval slider in the settings UI, in minutes. */
    const val APP_CHECK_INTERVAL_STEP = 1
}
