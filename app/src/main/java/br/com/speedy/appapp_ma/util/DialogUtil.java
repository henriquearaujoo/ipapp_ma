package br.com.speedy.appapp_ma.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import br.com.speedy.appapp_ma.R;

/**
 * Created by henrique on 2015-04-09.
 */
public class DialogUtil {

    public static void showDialogAdvertencia(Context context, String msg){
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle("Advertência");

        builder.setIcon(R.drawable.ic_alert_grey600_48dp);

        builder.setMessage(msg);

        builder.setPositiveButton("Fechar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public static void showDialogInformacao(Context context, String msg){
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle("Informação");

        builder.setIcon(R.drawable.ic_information_grey600_48dp);

        builder.setMessage(msg);

        builder.setPositiveButton("Fechar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

}
