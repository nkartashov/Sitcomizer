package AU.MightyFour.Sitcomizer;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Toast;


public class MusicDialog extends DialogFragment{

    private Context context;

    private final boolean[] checkalarms_;
    private final String[] alarms_;


    public MusicDialog(Context maincontext, String[] alarms, boolean[] checkalarms)
    {
        context = maincontext;
        alarms_ = alarms;
        checkalarms_ = checkalarms;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Choose Music");
        //builder.setMessage("message");

        builder.setMultiChoiceItems(alarms_, checkalarms_ , new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick (DialogInterface dialog, int item, boolean isChecked) {
                checkalarms_[item] = isChecked;
                Toast.makeText(context, alarms_[item], Toast.LENGTH_SHORT).show();
            }
        });


        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Toast.makeText(context, "Choose OK", Toast.LENGTH_LONG).show();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Toast.makeText(context, "Choose Cancel", Toast.LENGTH_LONG).show();
                        dialog.cancel();
                        // User cancelled the dialog
                    }
                });
        builder.setCancelable(false);
        return builder.create();
    }
}
