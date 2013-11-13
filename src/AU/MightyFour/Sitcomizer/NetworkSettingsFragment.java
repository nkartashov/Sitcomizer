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
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;

public class NetworkSettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_network);

    }


    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
//        Preference pref = findPreference(key);
//        if (key.equals(""))
    }
}