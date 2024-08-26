package org.bayton

import android.content.Intent
import android.content.RestrictionsManager
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LauncherActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Setting an orientation crashes the app due to the transparent background on Android 8.0
        // Oreo and below. We only set the orientation on Oreo and above. This only affects the
        // splash screen and Chrome will still respect the orientation.
        requestedOrientation = if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        } else {
            ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        }

        // Handle managed configuration
        handleManagedConfig()
    }

    private fun handleManagedConfig() {
        // Get the RestrictionsManager
        val restrictionsManager = getSystemService(RESTRICTIONS_SERVICE) as RestrictionsManager

        // Retrieve the app restrictions
        val restrictions = restrictionsManager.applicationRestrictions

        // Get the value of the 'startPath' restriction
        val startPath = restrictions.getString("startPath", "/") // default to "/"

        // Launch the appropriate activity based on Chrome availability
        launchAppropriateActivity(startPath)
    }

    private fun launchAppropriateActivity(startPath: String) {
        // Determine if Chrome is available
        if (isChromeAvailable) {
            // If Chrome is available, launch the Trusted Web Activity
            launchTWA(startPath)
        } else {
            // If Chrome is not available, launch the WebViewActivity
            Toast.makeText(this, "No browser, launching WebView", Toast.LENGTH_SHORT)
                .show()
            launchWebViewActivity(startPath)
        }
    }

    private val isChromeAvailable: Boolean
        get() {
            val packageManager = packageManager
            try {
                packageManager.getPackageInfo("com.android.chrome", 0)
                return true
            } catch (e: PackageManager.NameNotFoundException) {
                return false
            }
        }

    private fun launchTWA(startPath: String) {
        // You can modify the URL or other settings based on startPath if needed
        val url = "https://bayton.org$startPath"
        val uri = Uri.parse(url)

        // Launch the TWA
        val intent = Intent(Intent.ACTION_VIEW, uri)
        intent.setPackage("com.android.chrome")
        intent.putExtra("android.support.customtabs.extra.SESSION", null as String?)
        intent.putExtra("android.support.customtabs.extra.TITLE_VISIBILITY", 0)
        startActivity(intent)
        finish()
    }

    private fun launchWebViewActivity(startPath: String) {
        // Start the WebViewActivity with the modified URL
        val url = "https://bayton.org$startPath"
        val intent = Intent(this, WebViewActivity::class.java)
        intent.putExtra("url", url)
        startActivity(intent)
        finish()
    }
}