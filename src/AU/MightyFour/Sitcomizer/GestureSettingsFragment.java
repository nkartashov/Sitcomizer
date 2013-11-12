package AU.MightyFour.Sitcomizer;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.*;
import android.view.View;

import java.lang.reflect.Field;

/**
 * Created with IntelliJ IDEA.
 * User: novokrest
 * Date: 12.11.13
 * Time: 3:13
 * To change this template use File | Settings | File Templates.
 */
public class GestureSettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.pref_gesture);

        AllInit();

    }


    private void AllInit()
    {
        ListInit("list_shake_key");
        ListInit("list_winner_key");
        SwitchInit("switch_shake_key");
        SwitchInit("switch_winner_key");
    }

    private void ListInit(String key)
    {
        ListPreference list_pref = (ListPreference) findPreference(key);
        Field[] fields = R.raw.class.getFields();
        CharSequence[] raw_entries = new CharSequence[fields.length];
        for (int i = 0; i < fields.length; ++i) {
            raw_entries[i] = fields[i].getName();
        }
        list_pref.setEntries(raw_entries);
        list_pref.setEntryValues(raw_entries);
    }

    private void SwitchInit(String key)
    {
        SwitchPreference switch_pref = (SwitchPreference) findPreference(key);
        switch_pref.setChecked(false);
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }


    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference pref = findPreference(key);
        if (pref instanceof ListPreference) {
            ListPreference listPref = (ListPreference) pref;
            pref.setSummary(listPref.getEntry());
        }



    }

}
