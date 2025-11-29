package com.tcynik.racertimer.main_activity.data.wind_direction

import android.util.Log

/**
 * Class for smoothing and filtering wind direction calculations to reduce discreteness
 * and improve accuracy. Uses exponential moving average with circular angle handling.
 */
class WindDirectionSmoother {
    companion object {
        private const val PROJECT_LOG_TAG = "racer_timer_windSmoother"
        
        // Smoothing parameters
        private const val DEFAULT_SMOOTHING_FACTOR = 0.15 // 0.0 = no smoothing, 1.0 = no change
        private const val MIN_CHANGE_THRESHOLD = 2 // Minimum degrees change to trigger update
        private const val MAX_CHANGE_THRESHOLD = 45 // Maximum degrees change to accept (filters outliers)
    }
    
    private var smoothingFactor: Double
    private var smoothedWindDirection: Double = 0.0 // Internal value in degrees (can be fractional)
    private var lastReportedWindDirection: Int = 10000 // Last reported integer value
    private var isInitialized: Boolean = false
    private var updateCount: Int = 0 // Track number of updates for confidence

    constructor() : this(DEFAULT_SMOOTHING_FACTOR)

    constructor(smoothingFactor: Double) {
        this.smoothingFactor = smoothingFactor.coerceIn(0.0, 1.0) // Clamp between 0 and 1
    }

    /**
     * Process a new wind direction measurement and return smoothed value if changed
     * @param newWindDirection Raw wind direction in degrees (0-359)
     * @return Smoothed wind direction in degrees (0-359), or 10000 if no update needed
     */
    fun processWindDirection(newWindDirection: Int): Int {
        if (newWindDirection == 10000) {
            return 10000 // Invalid value, return as-is
        }

        if (!isInitialized) {
            // First measurement - initialize directly
            smoothedWindDirection = newWindDirection.toDouble()
            lastReportedWindDirection = newWindDirection
            isInitialized = true
            updateCount = 1
            Log.i(PROJECT_LOG_TAG, "Wind smoother initialized with direction: $newWindDirection")
            return newWindDirection
        }

        // Calculate circular difference (handles 0-360 wrap-around)
        val circularDiff = calculateCircularDifference(smoothedWindDirection, newWindDirection.toDouble())
        
        // Filter out outliers (sudden large changes)
        if (kotlin.math.abs(circularDiff) > MAX_CHANGE_THRESHOLD) {
            Log.i(PROJECT_LOG_TAG, "Filtered outlier wind direction change: $circularDiff " +
                  "degrees (from ${smoothedWindDirection.toInt()} to $newWindDirection)")
            return 10000 // Don't update
        }

        // Apply exponential moving average
        smoothedWindDirection = smoothedWindDirection + circularDiff * smoothingFactor
        
        // Normalize to 0-360 range
        smoothedWindDirection = normalizeAngle(smoothedWindDirection)
        
        updateCount++

        // Round to integer
        var roundedDirection = smoothedWindDirection.roundToInt()
        roundedDirection = normalizeAngle(roundedDirection)

        // Only report if change exceeds threshold
        val change = kotlin.math.abs(calculateCircularDifference(
            lastReportedWindDirection.toDouble(), 
            roundedDirection.toDouble()
        ))
        if (change >= MIN_CHANGE_THRESHOLD) {
            lastReportedWindDirection = roundedDirection
            Log.d(PROJECT_LOG_TAG, "Wind direction updated: $roundedDirection " +
                  "(raw: $newWindDirection, smoothed: ${String.format("%.1f", smoothedWindDirection)}, " +
                  "change: ${change.toInt()}°)")
            return roundedDirection
        }

        return 10000 // No significant change
    }

    /**
     * Calculate the shortest angular difference between two angles (handles wrap-around)
     * @param angle1 First angle in degrees
     * @param angle2 Second angle in degrees
     * @return Difference in degrees (-180 to +180)
     */
    private fun calculateCircularDifference(angle1: Double, angle2: Double): Double {
        var diff = angle2 - angle1
        
        // Normalize to -180 to +180 range
        while (diff > 180) diff -= 360
        while (diff < -180) diff += 360
        
        return diff
    }

    /**
     * Normalize angle to 0-360 range
     */
    private fun normalizeAngle(angle: Double): Double {
        var normalized = angle
        while (normalized < 0) normalized += 360
        while (normalized >= 360) normalized -= 360
        return normalized
    }

    /**
     * Normalize integer angle to 0-360 range
     */
    private fun normalizeAngle(angle: Int): Int {
        var normalized = angle
        while (normalized < 0) normalized += 360
        while (normalized >= 360) normalized -= 360
        return normalized
    }

    /**
     * Get current smoothed wind direction
     */
    fun getCurrentWindDirection(): Int {
        if (!isInitialized) return 10000
        return lastReportedWindDirection
    }

    /**
     * Get internal smoothed value (for debugging)
     */
    fun getSmoothedValue(): Double {
        return smoothedWindDirection
    }

    /**
     * Reset the smoother (useful when wind source changes significantly)
     */
    fun reset() {
        isInitialized = false
        smoothedWindDirection = 0.0
        lastReportedWindDirection = 10000
        updateCount = 0
        Log.i(PROJECT_LOG_TAG, "Wind smoother reset")
    }

    /**
     * Set smoothing factor (0.0 = no smoothing, 1.0 = no change)
     */
    fun setSmoothingFactor(factor: Double) {
        smoothingFactor = factor.coerceIn(0.0, 1.0)
        Log.i(PROJECT_LOG_TAG, "Smoothing factor set to: $smoothingFactor")
    }

    /**
     * Get update count (confidence indicator)
     */
    fun getUpdateCount(): Int {
        return updateCount
    }
}

