package AU.MightyFour.Sitcomizer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created with IntelliJ IDEA.
 * User: novokrest
 * Date: 12.11.13
 * Time: 1:09
 * To change this template use File | Settings | File Templates.
 */
import android.os.Bundle;
import android.preference.PreferenceActivity;

import java.util.List;

import static android.app.PendingIntent.getActivity;

public class SettingsActivity extends PreferenceActivity {

    @Override
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.pref_head, target);
    }
}


