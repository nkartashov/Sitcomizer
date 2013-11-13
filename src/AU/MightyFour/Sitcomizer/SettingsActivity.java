package AU.MightyFour.Sitcomizer;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: novokrest
 * Date: 12.11.13
 * Time: 1:09
 * To change this template use File | Settings | File Templates.
 */

public class SettingsActivity extends PreferenceActivity  {

    @Override
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.pref_head, target);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }



}


