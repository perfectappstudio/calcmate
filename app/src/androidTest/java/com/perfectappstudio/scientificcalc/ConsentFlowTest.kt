package com.perfectappstudio.scientificcalc

import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Direction
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until
import com.google.android.ump.UserMessagingPlatform
import com.perfectappstudio.scientificcalc.ads.AdManager
import java.io.File
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ConsentFlowTest {
    @Test
    fun europeanConsentCanBeDeclinedAndReopenedFromSettings() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val context = instrumentation.targetContext
        val consent = UserMessagingPlatform.getConsentInformation(context)
        instrumentation.runOnMainSync { consent.reset() }
        val device = UiDevice.getInstance(instrumentation)
        val intent = Intent(context, MainActivity::class.java)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            .putExtra("consent_debug_region", "EEA")
        try {
          ActivityScenario.launch<MainActivity>(intent).use { scenario ->
            val reject = device.wait(Until.findObject(By.text("Do not consent")), 45000)
            assertNotNull("Published European consent must load on the test emulator", reject)
            device.takeScreenshot(File(context.getExternalFilesDir(null), "consent-europe.png"))
            reject!!.click()
            assertTrue(device.wait(Until.gone(By.text("Do not consent")), 10000))
            scenario.onActivity { assertTrue(AdManager.privacyOptionsRequired) }
            device.wait(Until.findObject(By.desc("Settings")), 5000)!!.click()
            var privacy = device.findObject(By.text("Ad privacy choices"))
            repeat(3) {
                if (privacy == null) {
                    device.findObject(By.scrollable(true))?.scroll(Direction.DOWN, 0.8f)
                    privacy = device.findObject(By.text("Ad privacy choices"))
                }
            }
            assertNotNull("Required privacy options must be reachable in Settings", privacy)
            privacy!!.click()
            assertTrue(device.wait(Until.hasObject(By.text("Do not consent")), 10000))
            device.takeScreenshot(File(context.getExternalFilesDir(null), "consent-reopened.png"))
          }
        } finally {
            instrumentation.runOnMainSync { consent.reset() }
        }
    }
}
