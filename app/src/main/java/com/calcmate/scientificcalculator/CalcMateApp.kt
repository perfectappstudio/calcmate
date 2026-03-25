package com.calcmate.scientificcalculator

import android.app.Application
import com.google.android.gms.ads.MobileAds

class CalcMateApp : Application() {
    override fun onCreate() {
        super.onCreate()
        MobileAds.initialize(this)
    }
}
