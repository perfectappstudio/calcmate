package com.perfectappstudio.scientificcalc.ads

import android.view.View
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.key
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError

@Composable
fun BannerAdComposable(modifier: Modifier = Modifier) {
    if (AdManager.isPremium || !AdManager.canRequestAds) return

    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        val width = maxWidth.value.toInt().coerceAtLeast(1)
        key(width) {
            val context = LocalContext.current
            val adView = remember { AdView(context) }
            val adSize = remember { AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(context, width) }
            var failed by remember { mutableStateOf(false) }
            DisposableEffect(Unit) {
                onDispose { adView.destroy() }
            }
            AndroidView(
                modifier = Modifier.fillMaxWidth()
                    .padding(vertical = if (failed) 0.dp else 8.dp)
                    .height(if (failed) 0.dp else adSize.height.dp),
                factory = {
                    adView.apply {
                        setAdSize(adSize)
                        adUnitId = AdManager.BANNER_AD_UNIT_ID
                        adListener = object : AdListener() {
                            override fun onAdLoaded() { failed = false; visibility = View.VISIBLE }
                            override fun onAdFailedToLoad(error: LoadAdError) { failed = true; visibility = View.GONE }
                        }
                        visibility = View.INVISIBLE
                        loadAd(AdRequest.Builder().build())
                    }
                },
            )
        }
    }
}
