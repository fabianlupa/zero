package com.flaiker.zero.services;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

/**
 * Manages usersettings
 */
public class PreferencesManager {
    public static final String LOG = PreferencesManager.class.getSimpleName();

    private static final String PREFS_NAME              = "zeroprefs";
    private static final String PREF_FPSCOUNTER_ENABLED = "fpscounter.enabled";

    private Preferences preferences;

    private Preferences getPreferences() {
        if (preferences == null) {
            preferences = Gdx.app.getPreferences(PREFS_NAME);
        }
        return preferences;
    }

    public boolean isFpsCounterEnabled() {
        return getPreferences().getBoolean(PREF_FPSCOUNTER_ENABLED, true);
    }

    public void setFPSCounterEnabled(boolean fpsCounterEnabled) {
        getPreferences().putBoolean(PREF_FPSCOUNTER_ENABLED, fpsCounterEnabled);
        getPreferences().flush();
        Gdx.app.log(LOG, "Set " + PREF_FPSCOUNTER_ENABLED + " to " + fpsCounterEnabled);
    }
}
