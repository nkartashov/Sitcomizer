package AU.MightyFour.Sitcomizer;

import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * Created with IntelliJ IDEA.
 * User: novokrest
 * Date: 12.11.13
 * Time: 3:13
 * To change this template use File | Settings | File Templates.
 */
public class GestureSettingsFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.pref_gesture);
    }
}
