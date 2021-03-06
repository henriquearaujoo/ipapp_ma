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

import java.math.BigDecimal;
import java.util.ArrayList;

import br.com.speedy.appapp_ma.R;
import br.com.speedy.appapp_ma.util.ArmazenamentoRetiradaAux;
import br.com.speedy.appapp_ma.util.ItemResumo;
import br.com.speedy.appapp_ma.util.PeixeDisponivel;
import br.com.speedy.appapp_ma.util.SessionApp;

/**
 * TODO: document your custom view class.
 */
public class DialogDadosEntrada extends DialogFragment {

    private PeixeDisponivel peixe;

    private EditText editTextPeso;

    private EditText editTextCamara;

    private EditText editTextCurral;

    public DialogDadosEntrada(){

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.sample_dialog_dados_entrada, null);

        //this.peixe = SessionApp.getPeixe();

        editTextPeso = (EditText) view.findViewById(R.id.edtDDLEPeso);

        editTextCamara = (EditText) view.findViewById(R.id.edtDDLECamara);

        editTextCurral = (EditText) view.findViewById(R.id.edtDDLECurral);

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(view)
                // Add action buttons
                .setPositiveButton("Salvar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        ItemResumo itemResumo = null;

                        if (SessionApp.getItens() == null || SessionApp.getItens().size() == 0) {
                            SessionApp.setItens(new ArrayList<ItemResumo>());
                        } else {
                            for (ItemResumo i : SessionApp.getItens()) {
                                if (i.getTipo().equals("Armazenar")) {
                                    itemResumo = i;
                                    break;
                                }
                            }
                        }

                        if (itemResumo == null) {
                            itemResumo = new ItemResumo();
                            itemResumo.setTipo("Armazenar");
                            SessionApp.getItens().add(itemResumo);
                        }

                        ArmazenamentoRetiradaAux aux = new ArmazenamentoRetiradaAux();
                        aux.setPeso(new BigDecimal(editTextPeso.getText().toString()));
                        //aux.setCamara(editTextCamara.getText().toString());
                        //aux.setCurral(editTextCurral.getText().toString());
                        //aux.setDescricaoPeixe(peixe.getDescricao());

                        if (itemResumo.getListaAR() == null || itemResumo.getListaAR().size() == 0) {
                            itemResumo.setListaAR(new ArrayList<ArmazenamentoRetiradaAux>());
                            itemResumo.getListaAR().add(aux);
                        }else
                            itemResumo.getListaAR().add(aux);

                    }
                })
                .setNegativeButton("Fechar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        DialogDadosEntrada.this.getDialog().cancel();
                    }
                });

        builder.setTitle("Dados do armazenamento");

        return builder.create();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
