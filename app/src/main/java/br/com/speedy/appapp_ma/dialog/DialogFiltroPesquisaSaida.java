package br.com.speedy.appapp_ma.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import br.com.speedy.appapp_ma.R;
import br.com.speedy.appapp_ma.util.PeixeDisponivel;
import br.com.speedy.appapp_ma.util.SessionApp;

/**
 * TODO: document your custom view class.
 */
public class DialogFiltroPesquisaSaida extends DialogFragment {

    private PeixeDisponivel peixe;

    private EditText editTextPeixe;

    private EditText editTextCamara;

    private EditText editTextCurral;

    public DialogFiltroPesquisaSaida(){

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

        View view = inflater.inflate(R.layout.sample_dialog_filtro_saida, null);

        //this.peixe = SessionApp.getPeixeDisponivel();

        editTextPeixe = (EditText) view.findViewById(R.id.edtDSPeixe);

        editTextCamara = (EditText) view.findViewById(R.id.edtDSCamara);

        editTextCurral = (EditText) view.findViewById(R.id.edtDSCurral);

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(view)
                // Add action buttons
                .setPositiveButton("Salvar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                    }
                })
                .setNegativeButton("Fechar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        DialogFiltroPesquisaSaida.this.getDialog().cancel();
                    }
                });

        //builder.setTitle("Filtro da pesquisa");

        return builder.create();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
