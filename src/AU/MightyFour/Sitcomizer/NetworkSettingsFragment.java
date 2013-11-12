package AU.MightyFour.Sitcomizer;

/**
 * Created with IntelliJ IDEA.
 * User: novokrest
 * Date: 12.11.13
 * Time: 11:20
 * To change this template use File | Settings | File Templates.
 */
import android.os.Bundle;
import android.preference.PreferenceFragment;

public class NetworkSettingsFragment extends PreferenceFragment {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_newtwork);
    }

}