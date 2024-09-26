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

        // Set orientation to avoid crashes in older versions of Android
        requestedOrientation = if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        } else {
            ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        }

        // Check if the activity was launched with a deep link (intent with data)
        val deepLinkUri: Uri? = intent?.data
        if (deepLinkUri != null) {
            // Handle deep link
            launchAppropriateActivity(deepLinkUri.toString())
        } else {
            // If no deep link, use the managed configuration's default path
            handleManagedConfig()
        }
    }

    private fun handleManagedConfig() {
        // Retrieve managed configuration
        val restrictionsManager = getSystemService(RESTRICTIONS_SERVICE) as RestrictionsManager
        val restrictions = restrictionsManager.applicationRestrictions
        val startPath = restrictions.getString("startPath", "/") // Default to "/"

        // Launch the appropriate activity with the startPath
        launchAppropriateActivity("https://bayton.org$startPath")
    }

    private fun launchAppropriateActivity(url: String) {
        // Check if Chrome is available and can handle the TWA
        if (isChromeAvailable && canHandleTwaIntent(url)) {
            // If Chrome can handle the TWA, launch it
            launchTWA(url)
        } else {
            // Fallback to WebView if Chrome is unavailable
            Toast.makeText(this, "No browser, launching WebView", Toast.LENGTH_SHORT).show()
            launchWebViewActivity(url)
        }
    }

    private val isChromeAvailable: Boolean
        get() {
            val packageManager = packageManager
            return try {
                packageManager.getPackageInfo("com.android.chrome", 0)
                true
            } catch (e: PackageManager.NameNotFoundException) {
                false
            }
        }

    private fun canHandleTwaIntent(url: String): Boolean {
        val uri = Uri.parse(url)
        val intent = Intent(Intent.ACTION_VIEW, uri).apply {
            setPackage("com.android.chrome")
        }
        val resolveInfo = packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY)
        return resolveInfo != null
    }

    private fun launchTWA(url: String) {
        try {
            val uri = Uri.parse(url)
            val intent = Intent(Intent.ACTION_VIEW, uri).apply {
                setPackage("com.android.chrome")
                putExtra("android.support.customtabs.extra.SESSION", null as String?)
                putExtra("android.support.customtabs.extra.TITLE_VISIBILITY", 0)
            }
            startActivity(intent)
            finish()
        } catch (e: Exception) {
            // Fallback to WebView in case of TWA failure
            Toast.makeText(this, "Error launching TWA, switching to WebView", Toast.LENGTH_SHORT).show()
            launchWebViewActivity(url)
        }
    }

    private fun launchWebViewActivity(url: String) {
        val intent = Intent(this, WebViewActivity::class.java).apply {
            putExtra("url", url)
        }
        startActivity(intent)
        finish()
    }
}