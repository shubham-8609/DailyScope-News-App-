package com.codeleg.dailyscope.ui.fragment

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.codeleg.dailyscope.R

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }
}