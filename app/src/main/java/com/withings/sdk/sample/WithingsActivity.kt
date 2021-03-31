package com.withings.sdk.sample

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.withings.library.webble.WithingsFragment

class WithingsActivity : AppCompatActivity() {

    companion object {
        fun createInstallIntent(context: Context): Intent {
            val url = "https://your_redirection_url_for_install"
            return createIntent(context, url)
        }

        fun createSettingsIntent(context: Context, accessToken: String): Intent {
            val url = "https://your_redirection_url_for_settings"
            return createIntent(context, url, accessToken)
        }

        private fun createIntent(context: Context, url: String, accessToken: String? = null): Intent {
            return Intent(context, WithingsActivity::class.java)
                .putExtra(EXTRA_KEY_URL, url)
                .putExtra(EXTRA_KEY_ACCESS_TOKEN, accessToken)
        }

        private const val EXTRA_KEY_URL = "url"
        private const val EXTRA_KEY_ACCESS_TOKEN = "access_token"
    }

    private val url by lazy { intent.getStringExtra(EXTRA_KEY_URL)!! }
    private val accessToken by lazy { intent.getStringExtra(EXTRA_KEY_ACCESS_TOKEN) }
    private val fragment by lazy { WithingsFragment.newInstance(url, accessToken) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_withings)
        if (isOnline()) {
            showWithingsFragment()
        } else {
            showNoInternet()
        }
    }

    private fun isOnline(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val networkCapabilities = connectivityManager.activeNetwork?.let { connectivityManager.getNetworkCapabilities(it) }
            networkCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED) == true
        } else true
    }

    private fun showWithingsFragment() {
        supportFragmentManager.beginTransaction().replace(R.id.content, fragment).commitAllowingStateLoss()
        fragment.setCloseIntentListener { finish() }
        fragment.setNotificationListener { type, parameters ->
            val status = ReturnStatus.get(type)
            if (status in ReturnStatus.getClosingCodes) {
                finish()
            }
            if (status in ReturnStatus.getSuccessInstallCodes) {
                Toast.makeText(this, "Installation Successful", Toast.LENGTH_LONG).show()
            } else {
                val toastText = "type : $type\n" + parameters.entries.joinToString("\n") { "${it.key} : ${it.value}" }
                Toast.makeText(this, toastText, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun showNoInternet() {
        AlertDialog.Builder(this)
            .setTitle(R.string.noInternet)
            .setPositiveButton(R.string.ok) { _, _ -> finish() }
            .setOnDismissListener { finish() }
            .create().show()
    }

    override fun onBackPressed() {
        if (fragment.canGoBack()) {
            fragment.goBack()
        } else {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                setResult(Activity.RESULT_CANCELED)
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}

@Suppress("MagicNumber")
private enum class ReturnStatus(val code: Int) {
    INSTALL_SUCCESS(1),
    INSTALL_FAILURE(2),
    ACCOUNT_ERROR(3),
    SETTINGS_INSTALL_SUCCESS(4),
    SETTINGS_INSTALL_FAILURE(5),
    DISSOCIATION_SUCCESS(6),
    DISSOCIATION_FAILURE(7),
    UPDATE_WIFI_SUCCESS(8),
    UPDATE_WIFI_FAILURE(9),
    LOGIN_SUCCESS(10),
    LOGIN_FAILURE(11);

    companion object {
        fun get(code: Int): ReturnStatus? = values().find { code == it.code }

        val getClosingCodes = listOf(INSTALL_SUCCESS, LOGIN_SUCCESS)

        val getSuccessInstallCodes = listOf(INSTALL_SUCCESS, SETTINGS_INSTALL_SUCCESS)
    }
}
