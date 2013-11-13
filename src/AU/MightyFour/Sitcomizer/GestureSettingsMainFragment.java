package AU.MightyFour.Sitcomizer;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import org.apache.commons.logging.Log;

/**
 * Created with IntelliJ IDEA.
 * User: novokrest
 * Date: 13.11.13
 * Time: 4:10
 * To change this template use File | Settings | File Templates.
 */

public class GestureSettingsMainFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.pref_main_gesture);


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
        SwitchPreference main_swich = (SwitchPreference) findPreference("switch_main_key");
        SwitchPreference passive_switch = (SwitchPreference) findPreference("switch_passive_key");
        if (!main_swich.isChecked()) {
            passive_switch.setChecked(false);

        }

    }

}
