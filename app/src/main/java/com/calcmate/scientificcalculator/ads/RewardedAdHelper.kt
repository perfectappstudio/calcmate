package com.calcmate.scientificcalculator.ads

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback

/**
 * Handles loading, caching, and showing a single rewarded ad.
 *
 * The reward callback signals that the user watched the full video and is
 * entitled to a benefit (e.g. unlocking history CSV export).
 */
class RewardedAdHelper {

    companion object {
        private const val TAG = "RewardedAdHelper"
    }

    private var rewardedAd: RewardedAd? = null
    private var isLoading = false

    /**
     * Request and cache a rewarded ad. Duplicate requests while one is
     * already in-flight are ignored.
     */
    fun load(context: Context) {
        if (isLoading || rewardedAd != null) return
        isLoading = true

        val adRequest = AdRequest.Builder().build()
        RewardedAd.load(
            context,
            AdManager.REWARDED_AD_UNIT_ID,
            adRequest,
            object : RewardedAdLoadCallback() {
                override fun onAdLoaded(ad: RewardedAd) {
                    rewardedAd = ad
                    isLoading = false
                    Log.d(TAG, "Rewarded ad loaded.")
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    rewardedAd = null
                    isLoading = false
                    Log.w(TAG, "Rewarded ad failed to load: ${error.message}")
                }
            },
        )
    }

    /**
     * Show the cached rewarded ad.
     *
     * @param activity        Host activity.
     * @param onUserEarnedReward Called when the user earns the reward (watched full video).
     * @param onDismissed      Called when the ad is dismissed (regardless of reward).
     *
     * If no ad is loaded, [onDismissed] fires immediately so the app flow
     * is never blocked.
     */
    fun show(
        activity: Activity,
        onUserEarnedReward: () -> Unit,
        onDismissed: () -> Unit,
    ) {
        val ad = rewardedAd
        if (ad == null) {
            Log.d(TAG, "Rewarded ad not ready; skipping.")
            onDismissed()
            return
        }

        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                Log.d(TAG, "Rewarded ad dismissed.")
                rewardedAd = null
                onDismissed()
            }

            override fun onAdFailedToShowFullScreenContent(error: AdError) {
                Log.w(TAG, "Rewarded ad failed to show: ${error.message}")
                rewardedAd = null
                onDismissed()
            }

            override fun onAdShowedFullScreenContent() {
                Log.d(TAG, "Rewarded ad shown.")
            }
        }

        ad.show(activity) {
            // UserEarnedRewardListener
            Log.d(TAG, "User earned reward: ${it.type}, amount: ${it.amount}")
            onUserEarnedReward()
        }
    }

    /** @return `true` when a cached rewarded ad is ready to show. */
    fun isLoaded(): Boolean = rewardedAd != null
}
