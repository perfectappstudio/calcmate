package com.perfectappstudio.scientificcalc.ads

import android.app.Activity
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

/**
 * Singleton managing ad lifecycle across the app.
 *
 * Tracks calculation sessions and coordinates banner / interstitial / rewarded ads.
 * All ad unit IDs are Google-provided test IDs for development.
 */
object AdManager {

    // --------------- Production Ad Unit IDs ---------------
    const val BANNER_AD_UNIT_ID = "ca-app-pub-4637692872834816/3658089280"
    const val INTERSTITIAL_AD_UNIT_ID = "ca-app-pub-4637692872834816/7611652484"
    const val REWARDED_AD_UNIT_ID = "ca-app-pub-3940256099942544/5224354917" // test ID until rewarded unit created

    // --------------- Premium state ---------------
    /** True when the user has purchased ad removal via IAP. */
    var isPremium by mutableStateOf(false)
        private set

    // --------------- Session tracking ---------------
    /** Number of completed calculation sessions (a "session" = pressing equals). */
    var sessionCount by mutableIntStateOf(0)
        private set

    // --------------- Ad helpers (lazy so we never instantiate before init) ---------------
    private val interstitialHelper = InterstitialAdHelper()
    private val rewardedHelper = RewardedAdHelper()

    // --------------- Public API ---------------

    /**
     * Call once from [Activity.onCreate] to kick off the first interstitial preload.
     */
    fun initialize(context: Context) {
        if (!isPremium) {
            interstitialHelper.load(context)
            rewardedHelper.load(context)
        }
    }

    /**
     * Mark the user as premium (ad-free). Called after a successful IAP.
     */
    fun markPremium(premium: Boolean) {
        isPremium = premium
    }

    /**
     * Increment the session counter and decide whether to show an interstitial.
     *
     * Rules (R25):
     *  - Never show during the first session.
     *  - Show every 5th session thereafter.
     *
     * @return `true` if an interstitial should be shown now.
     */
    fun incrementSession(): Boolean {
        sessionCount++
        // Skip first session; after that every 5th triggers an interstitial.
        return sessionCount > 1 && sessionCount % 5 == 0
    }

    // --------------- Interstitial ---------------

    fun loadInterstitial(context: Context) {
        if (!isPremium) interstitialHelper.load(context)
    }

    fun showInterstitial(activity: Activity, onDismissed: () -> Unit) {
        if (isPremium) {
            onDismissed()
            return
        }
        interstitialHelper.show(activity) {
            // Preload next interstitial after dismiss.
            interstitialHelper.load(activity)
            onDismissed()
        }
    }

    fun isInterstitialLoaded(): Boolean = interstitialHelper.isLoaded()

    // --------------- Rewarded ---------------

    fun loadRewarded(context: Context) {
        if (!isPremium) rewardedHelper.load(context)
    }

    fun showRewarded(
        activity: Activity,
        onRewarded: () -> Unit,
        onDismissed: () -> Unit,
    ) {
        if (isPremium) {
            onRewarded()
            onDismissed()
            return
        }
        rewardedHelper.show(activity, onRewarded) {
            rewardedHelper.load(activity)
            onDismissed()
        }
    }

    fun isRewardedLoaded(): Boolean = rewardedHelper.isLoaded()
}
