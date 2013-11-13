package AU.MightyFour.Sitcomizer;

/**
 * Created with IntelliJ IDEA.
 * User: novokrest
 * Date: 12.11.13
 * Time: 11:20
 * To change this template use File | Settings | File Templates.
 */
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;

public class NetworkSettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.pref_network);

        ListInit();
    }

    private void ListInit() {
        ListPreference list_pref = (ListPreference) findPreference("list_roles_key");
        list_pref.setTitle("You are a " + list_pref.getEntry());
        if (list_pref.getEntry().equals("MASTER")) {
            list_pref.setIcon(R.drawable.ic_master);
        }
        else {
            list_pref.setIcon(R.drawable.ic_slave);
        }
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
            ListPreference list_pref = (ListPreference) pref;
            list_pref.setTitle("You are a " + list_pref.getEntry());
            if (list_pref.getEntry().equals("MASTER")) {
                list_pref.setIcon(R.drawable.ic_master);
            }
            else {
                list_pref.setIcon(R.drawable.ic_slave);
            }
        }
    }
}