package br.com.speedy.appapp_ma.dialog;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Calendar;
import java.util.List;

import br.com.speedy.appapp_ma.R;
import br.com.speedy.appapp_ma.adapter.SpinnerTipoPeixeAdapter;
import br.com.speedy.appapp_ma.model.TipoPeixe;
import br.com.speedy.appapp_ma.util.PeixeDisponivel;
import br.com.speedy.appapp_ma.util.SessionApp;

/**
 * TODO: document your custom view class.
 */
public class DialogFiltroPesquisaEstoque extends DialogFragment {

    public static final int ATUALIZAR_TIPOS = 1;

    private PeixeDisponivel peixe;

    private EditText edtFiltroPeixe;

    private Spinner spnTipo;

    private Button btDataInicial;

    private Button btDataFinal;

    private static TextView txtDataInicial;

    private static TextView txtDataFinal;

    private List<TipoPeixe> tipos;

    public DialogFiltroPesquisaEstoque(){

    }

    @SuppressLint("ValidFragment")
    public DialogFiltroPesquisaEstoque(List<TipoPeixe> tipos){
        this.tipos = tipos;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.sample_dialog_filtro_estoque, null);

        edtFiltroPeixe = (EditText) view.findViewById(R.id.edtFEPeixe);

        edtFiltroPeixe.setText(SessionApp.getFiltroPeixe() != null && !SessionApp.getFiltroPeixe().isEmpty() ? SessionApp.getFiltroPeixe() : null);

        spnTipo = (Spinner) view.findViewById(R.id.spnFETipo);

        btDataInicial = (Button) view.findViewById(R.id.btFEDataInicial);

        btDataFinal = (Button) view.findViewById(R.id.btFEDataFinal);

        txtDataInicial = (TextView) view.findViewById(R.id.txtFEDataInicial);

        txtDataInicial.setText(SessionApp.getFiltroDataInicial() != null && ! SessionApp.getFiltroDataInicial().isEmpty() ? SessionApp.getFiltroDataInicial().toString() : null);

        txtDataFinal = (TextView) view.findViewById(R.id.txtFEDataFinal);

        txtDataFinal.setText(SessionApp.getFiltroDataFinal() != null && ! SessionApp.getFiltroDataFinal().isEmpty() ? SessionApp.getFiltroDataFinal().toString() : null);

        SpinnerTipoPeixeAdapter adapter = new SpinnerTipoPeixeAdapter(getActivity(), tipos);

        spnTipo.setAdapter(adapter);

        if(SessionApp.getFiltroTipo() != null && SessionApp.getFiltroTipo().getId() != null){
            for (int i = 0; i < tipos.size(); i++) {
                if (SessionApp.getFiltroTipo().getId().longValue() == tipos.get(i).getId().longValue()){
                    spnTipo.setSelection(i);
                    break;
                }
            }
        }

        btDataInicial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePicker = new DatePickerFragmentInicial();
                datePicker.show(getFragmentManager(), "dataInicial");
            }
        });

        btDataFinal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePicker = new DatePickerFragmentFinal();
                datePicker.show(getFragmentManager(), "dataFinal");
            }
        });

        builder.setView(view)
                // Add action buttons
                .setPositiveButton("Salvar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        SessionApp.setFiltroPeixe(edtFiltroPeixe.getText() != null && !edtFiltroPeixe.getText().toString().isEmpty() ? edtFiltroPeixe.getText().toString() : null);
                        SessionApp.setFiltroTipo(spnTipo.getSelectedItem() != null && ((TipoPeixe) spnTipo.getSelectedItem()).getId() != null ? (TipoPeixe) spnTipo.getSelectedItem() : null);
                        SessionApp.setFiltroDataFinal(txtDataFinal.getText().toString());
                        SessionApp.setFiltroDataInicial(txtDataInicial.getText().toString());
                    }
                })
                .setNegativeButton("Fechar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        DialogFiltroPesquisaEstoque.this.getDialog().cancel();
                    }
                });

        builder.setTitle("Filtro da pesquisa");

        return builder.create();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public static class DatePickerFragmentInicial extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog dialog = new DatePickerDialog(getActivity(), this, year, month, day);
            dialog.setTitle("Selecione a data inicial");
            dialog.setCancelable(true);
            return dialog;
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            month = month + 1;

            String dia = day < 10 ? "0" + day : "" + day;

            String mes = month < 10 ? "0" + month : "" + month;

            txtDataInicial.setText(dia + "/" + mes + "/" + year);
        }
    }

    public static class DatePickerFragmentFinal extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog dialog = new DatePickerDialog(getActivity(), this, year, month, day);
            dialog.setTitle("Selecione a data final");
            dialog.setCancelable(true);
            return dialog;
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            month = month + 1;

            String dia = day < 10 ? "0" + day : "" + day;

            String mes = month < 10 ? "0" + month : "" + month;

            txtDataFinal.setText(dia + "/" + mes + "/" + year);
        }
    }
}
