package com.calcmate.scientificcalculator.ads

import android.view.View
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError

/**
 * Composable that wraps an AdMob adaptive banner.
 *
 * Behaviour:
 *  - If the user is premium, nothing is rendered.
 *  - If the ad fails to load (e.g. offline), the view collapses to zero height
 *    so it never takes space away from calculator buttons (R32).
 *  - The [AdView] is destroyed when this composable leaves composition.
 */
@Composable
fun BannerAdComposable(
    modifier: Modifier = Modifier,
) {
    // Don't compose anything for premium users.
    if (AdManager.isPremium) return

    var adLoaded by remember { mutableStateOf(false) }

    // Hold a reference so DisposableEffect can destroy it.
    val adView = remember { mutableStateOf<AdView?>(null) }

    DisposableEffect(Unit) {
        onDispose {
            adView.value?.destroy()
            adView.value = null
        }
    }

    AndroidView(
        modifier = modifier.fillMaxWidth(),
        factory = { context ->
            AdView(context).apply {
                setAdSize(AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(
                    context,
                    AdSize.FULL_WIDTH,
                ))
                adUnitId = AdManager.BANNER_AD_UNIT_ID

                adListener = object : AdListener() {
                    override fun onAdLoaded() {
                        adLoaded = true
                        visibility = View.VISIBLE
                    }

                    override fun onAdFailedToLoad(error: LoadAdError) {
                        // Graceful degradation: collapse to zero height (R32).
                        adLoaded = false
                        visibility = View.GONE
                    }
                }

                // Start hidden; only show once the first ad is loaded.
                visibility = View.GONE

                loadAd(AdRequest.Builder().build())
                adView.value = this
            }
        },
        update = { view ->
            // Nothing to update dynamically; the AdView manages its own refresh.
        },
    )
}
