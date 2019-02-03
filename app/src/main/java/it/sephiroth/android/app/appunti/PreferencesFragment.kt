package it.sephiroth.android.app.appunti


import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import it.sephiroth.android.app.appunti.models.SettingsManager
import timber.log.Timber


class PreferencesFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.appunti_preferences, rootKey)
    }

    override fun onPreferenceTreeClick(preference: Preference?): Boolean {
        Timber.i("onPreferenceTreeClick(${preference?.key})")

        when (preference?.key) {
            SettingsManager.PREFS_KEY_DARK_THEME -> askToRestartApplication()
        }

        return super.onPreferenceTreeClick(preference)
    }

    private fun askToRestartApplication() {
        activity?.let { activity ->
            AlertDialog.Builder(activity)
                    .setTitle(getString(R.string.restart_required))
                    .setMessage(getString(R.string.restart_required_body))
                    .setCancelable(false)
                    .setPositiveButton(getString(R.string.restart)) { _, _ -> triggerRebirth() }
                    .setNegativeButton(android.R.string.cancel) { dialog, _ -> dialog.dismiss() }
                    .show()
        }
    }

    private fun triggerRebirth() {
        activity?.let { activity ->
            val intent = Intent(activity, MainActivity::class.java)
            intent.addFlags(FLAG_ACTIVITY_NEW_TASK)
            activity.startActivity(intent)
            activity.finish()
        }
        Runtime.getRuntime().exit(0)
    }
}