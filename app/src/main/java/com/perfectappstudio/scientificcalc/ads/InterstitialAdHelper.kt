package com.perfectappstudio.scientificcalc.ads

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

/**
 * Handles loading, caching, and showing a single interstitial ad.
 */
class InterstitialAdHelper {

    companion object {
        private const val TAG = "InterstitialAdHelper"
    }

    private var interstitialAd: InterstitialAd? = null
    private var isLoading = false

    /**
     * Request and cache an interstitial ad. Safe to call multiple times; duplicate
     * requests while one is already in-flight are ignored.
     */
    fun load(context: Context) {
        if (isLoading || interstitialAd != null) return
        isLoading = true

        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(
            context,
            AdManager.INTERSTITIAL_AD_UNIT_ID,
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad
                    isLoading = false
                    Log.d(TAG, "Interstitial ad loaded.")
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    interstitialAd = null
                    isLoading = false
                    Log.w(TAG, "Interstitial failed to load: ${error.message}")
                }
            },
        )
    }

    /**
     * Show the cached interstitial. If none is loaded the [onDismissed] callback
     * fires immediately so the app flow is never blocked.
     */
    fun show(activity: Activity, onDismissed: () -> Unit) {
        val ad = interstitialAd
        if (ad == null) {
            Log.d(TAG, "Interstitial not ready; skipping.")
            onDismissed()
            return
        }

        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                Log.d(TAG, "Interstitial dismissed.")
                interstitialAd = null
                onDismissed()
            }

            override fun onAdFailedToShowFullScreenContent(error: AdError) {
                Log.w(TAG, "Interstitial failed to show: ${error.message}")
                interstitialAd = null
                onDismissed()
            }

            override fun onAdShowedFullScreenContent() {
                Log.d(TAG, "Interstitial shown.")
            }
        }

        ad.show(activity)
    }

    /** @return `true` when a cached interstitial is ready to show. */
    fun isLoaded(): Boolean = interstitialAd != null
}
