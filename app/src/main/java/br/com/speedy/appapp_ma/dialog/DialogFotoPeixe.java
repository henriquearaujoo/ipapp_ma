package br.com.speedy.appapp_ma.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

import br.com.speedy.appapp_ma.R;
import br.com.speedy.appapp_ma.model.Peixe;
import br.com.speedy.appapp_ma.util.PeixeDisponivel;

/**
 * TODO: document your custom view class.
 */
public class DialogFotoPeixe extends DialogFragment {

    private ImageView ivFotoPeixe;

    private ImageLoader il;

    private RequestQueue rq;

    private Peixe peixe;

    public DialogFotoPeixe(){

    }

    public DialogFotoPeixe(Peixe peixe){
        this.peixe = peixe;
    }

    /*public DialogDadosPeixe(Peixe peixe){
        this.peixe = peixe;
    }*/

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.sample_dialog_foto_peixe, null);

        ivFotoPeixe = (ImageView) view.findViewById(R.id.ivFPFotoPeixe);

        rq = Volley.newRequestQueue(getActivity());

        il = new ImageLoader(rq, new ImageLoader.ImageCache() {

            private final LruCache<String, Bitmap> cache = new LruCache<String, Bitmap>(20);

            @Override
            public Bitmap getBitmap(String url) {
                return cache.get(url);
            }

            @Override
            public void putBitmap(String url, Bitmap bitmap) {
                cache.put(url, bitmap);
            }
        });

        if (peixe.getUrlFoto() != null)
            il.get(peixe.getUrlFoto(), il.getImageListener(ivFotoPeixe, R.drawable.ic_fish_grey600_48dp, R.drawable.ic_fish_grey600_48dp));

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(view)
                .setNeutralButton("Fechar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        DialogFotoPeixe.this.getDialog().cancel();
                    }
                });


        return builder.create();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
}
