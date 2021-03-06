package br.com.speedy.appapp_ma.dialog;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import br.com.speedy.appapp_ma.InconsistenciasActivity;
import br.com.speedy.appapp_ma.R;
import br.com.speedy.appapp_ma.adapter.SpinnerCamaraAdapter;
import br.com.speedy.appapp_ma.adapter.SpinnerDestinoAdapter;
import br.com.speedy.appapp_ma.adapter.SpinnerPosicoesAdapter;
import br.com.speedy.appapp_ma.adapter.SpinnerTamanhoAdapter;
import br.com.speedy.appapp_ma.adapter.SpinnerTipoPeixeAdapter;
import br.com.speedy.appapp_ma.model.Camara;
import br.com.speedy.appapp_ma.model.PosicaoCamara;
import br.com.speedy.appapp_ma.model.TamanhoPeixe;
import br.com.speedy.appapp_ma.model.TipoPeixe;
import br.com.speedy.appapp_ma.util.ArmazenamentoRetiradaAux;
import br.com.speedy.appapp_ma.util.HttpConnection;
import br.com.speedy.appapp_ma.util.ItemResumo;
import br.com.speedy.appapp_ma.util.PeixeDisponivel;
import br.com.speedy.appapp_ma.util.SessionApp;
import br.com.speedy.appapp_ma.util.SharedPreferencesUtil;

/**
 * TODO: document your custom view class.
 */
public class DialogDadosSaida extends DialogFragment {

    public static final int ATUALIZAR_DADOS = 1;
    public static final int ATUALIZAR_POSICOES = 2;

    private EditText editTextPeso;

    private EditText editTextQtdeEmbalagem;

    private EditText editTextObservacoes;

    private Spinner spnDestino;

    private PeixeDisponivel armazenamento;

    private Context context;

    private AlertDialog alertDialog;

    private Spinner spnCamaras;

    private Spinner spnPosicoes;

    private Spinner spnTipos;

    private Spinner spnTamanhos;

    private Camara camara;

    private List<Camara> camaras;

    private List<TipoPeixe> tipos;

    private List<TamanhoPeixe> tamanhos;

    private List<PosicaoCamara> posicaoCamaras;

    private ArmazenamentoRetiradaAux aux;

    private InconsistenciasActivity ia;

    public DialogDadosSaida(){

    }

    @SuppressLint("ValidFragment")
    public DialogDadosSaida(Context context, List<Camara> camaras, List<TipoPeixe> tipos){
        this.context = context;
        this.camaras = camaras;
        this.tipos = tipos;
    }

    @SuppressLint("ValidFragment")
    public DialogDadosSaida(Context context, List<Camara> camaras, List<TipoPeixe> tipos, List<TamanhoPeixe> tamanhos){
        this.context = context;
        this.camaras = camaras;
        this.tipos = tipos;
        this.tamanhos = tamanhos;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.sample_dialog_dados_saida, null);

        ia = SessionApp.getInconsistenciasActivity();

        aux = SessionApp.getArAux();

        editTextPeso = (EditText) view.findViewById(R.id.edtDSPeso);

        editTextQtdeEmbalagem = (EditText) view.findViewById(R.id.edtDSQtdeEmbalagem);

        editTextObservacoes =  (EditText) view.findViewById(R.id.edtDSObservacoes);

        spnDestino = (Spinner) view.findViewById(R.id.spnDSDestino);

        spnTipos = (Spinner) view.findViewById(R.id.spnDSTipo);

        spnTamanhos = (Spinner) view.findViewById(R.id.spnDSTamanho);

        spnCamaras = (Spinner) view.findViewById(R.id.spnDSCamara);

        spnCamaras.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                camara = (Camara) spnCamaras.getSelectedItem();
                getPosicoesCamara();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spnPosicoes = (Spinner) view.findViewById(R.id.spnDSPosicao);

        Message msg = new Message();
        msg.what = ATUALIZAR_DADOS;
        handler.sendMessage(msg);

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(view)
                // Add action buttons
                .setPositiveButton("Salvar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                            salvarRetirada();
                        }
                    }

                    ).

                    setNegativeButton("Fechar", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    alertDialog.cancel();
                                }
                            }

                    );

        builder.setTitle("Dados da retirada");

        alertDialog=builder.create();

        return alertDialog;
    }

    public void preencherDados(){

        if (camaras != null) {
            for (int i = 0; i < camaras.size(); i++) {
                if (aux.getCamara().getId().longValue() == camaras.get(i).getId().longValue()) {
                    spnCamaras.setSelection(i);
                    camara = aux.getCamara();
                    break;
                }
            }
        }

        for (int i = 0; i < tipos.size(); i++) {
            if(aux.getTipoPeixe().getId().longValue() == tipos.get(i).getId().longValue()){
                spnTipos.setSelection(i);
                break;
            }
        }

        for (int i = 0; i < tamanhos.size(); i++) {
            if (aux.getTamanhoPeixe().getId().longValue() == tamanhos.get(i).getId().longValue()){
                spnTamanhos.setSelection(i);
                break;
            }else
                spnTamanhos.setSelection(0);
        }

        if (aux.getDestino().toLowerCase().equals("processo"))
            spnDestino.setSelection(0);
        else if (aux.getDestino().toLowerCase().equals("descarte"))
            spnDestino.setSelection(1);
        else if (aux.getDestino().toLowerCase().equals("cozinha"))
            spnDestino.setSelection(2);
        else
            spnDestino.setSelection(3);

        editTextPeso.setText(aux.getPeso().toString());
        editTextQtdeEmbalagem.setText(aux.getQtdeEmbalagem().toString());
    }

    public void salvarRetirada(){

        if (aux == null) {
            if (editTextPeso.getText() != null && !editTextPeso.getText().toString().isEmpty()
                    && editTextQtdeEmbalagem.getText() != null && !editTextQtdeEmbalagem.getText().toString().isEmpty()
                    && spnCamaras.getSelectedItem() != null && spnPosicoes.getSelectedItem() != null && spnDestino.getSelectedItem() != null && spnTipos.getSelectedItem() != null) {
                try {
                    ItemResumo itemResumo = null;

                    if (SessionApp.getItens() == null || SessionApp.getItens().size() == 0) {
                        SessionApp.setItens(new ArrayList<ItemResumo>());
                    } else {
                        for (ItemResumo i : SessionApp.getItens()) {
                            if (i.getTipo().equals("Retirar")) {
                                itemResumo = i;
                                break;
                            }
                        }
                    }

                    if (itemResumo == null) {
                        itemResumo = new ItemResumo();
                        itemResumo.setTipo("Retirar");
                        SessionApp.getItens().add(itemResumo);
                    }

                    aux = new ArmazenamentoRetiradaAux();
                    aux.setPeso(new BigDecimal(editTextPeso.getText().toString().replace(",", ".")));
                    aux.setPeixe(SessionApp.getPeixe());
                    aux.setDestino((String) spnDestino.getSelectedItem());
                    aux.setQtdeEmbalagem(Integer.parseInt(editTextQtdeEmbalagem.getText().toString()));
                    aux.setCamara((Camara) spnCamaras.getSelectedItem());
                    aux.setPosicaoCamara((PosicaoCamara) spnPosicoes.getSelectedItem());
                    aux.setTipoPeixe((TipoPeixe) spnTipos.getSelectedItem());
                    aux.setTamanhoPeixe(spnTamanhos.getSelectedItem() != null ? (TamanhoPeixe) spnTamanhos.getSelectedItem() : null);
                    aux.setObservacoes(editTextObservacoes.getText() != null && !editTextObservacoes.getText().toString().isEmpty() ? editTextObservacoes.getText().toString() : null);

                    if (itemResumo.getListaAR() == null || itemResumo.getListaAR().size() == 0) {
                        itemResumo.setListaAR(new ArrayList<ArmazenamentoRetiradaAux>());
                        itemResumo.getListaAR().add(aux);
                    } else
                        itemResumo.getListaAR().add(aux);

                    showDialogInformacao("Retirada adicionada com sucesso.");
                } catch (Exception e) {
                    showDialogAdvertencia("Não foi possivel adicionar a retirada.");
                }
            } else {
                showDialogAdvertencia("Preencha todos os campos obrigatórios (*) antes de salvar.");
            }
        }else{
            if (editTextPeso.getText() != null && !editTextPeso.getText().toString().isEmpty()
                    && editTextQtdeEmbalagem.getText() != null && !editTextQtdeEmbalagem.getText().toString().isEmpty()
                    && spnCamaras.getSelectedItem() != null && spnPosicoes.getSelectedItem() != null && spnDestino.getSelectedItem() != null && spnTipos.getSelectedItem() != null) {

                try{

                    for (ItemResumo itemResumo : SessionApp.getInconsistencias()) {
                        if (!itemResumo.getTipo().equals("Retirar")) {
                            for (ArmazenamentoRetiradaAux aux1 : itemResumo.getListaAR()) {
                                if (aux1.getId().longValue() == aux.getId().longValue()) {
                                    aux1.setPeso(new BigDecimal(editTextPeso.getText().toString().replace(",", ".")));
                                    aux1.setPeixe(SessionApp.getPeixe());
                                    aux1.setDestino((String) spnDestino.getSelectedItem());
                                    aux1.setQtdeEmbalagem(Integer.parseInt(editTextQtdeEmbalagem.getText().toString()));
                                    aux1.setCamara((Camara) spnCamaras.getSelectedItem());
                                    aux1.setPosicaoCamara((PosicaoCamara) spnPosicoes.getSelectedItem());
                                    aux1.setTipoPeixe((TipoPeixe) spnTipos.getSelectedItem());
                                    aux1.setTamanhoPeixe(spnTamanhos.getSelectedItem() != null ? (TamanhoPeixe) spnTamanhos.getSelectedItem() : null);
                                    aux1.setObservacoes(editTextObservacoes.getText() != null && !editTextObservacoes.getText().toString().isEmpty() ? editTextObservacoes.getText().toString() : null);
                                }
                            }
                        }
                    }

                    showDialogInformacao("Retirada editada com sucesso.");
                }catch (Exception e){
                    showDialogAdvertencia("Não foi possivel editar a retirada.");
                }

            }else {
                showDialogAdvertencia("Preencha todos os campos obrigatórios (*) antes de salvar.");
            }
        }
    }

    public void getPosicoesJSON(String dados){
        posicaoCamaras = new ArrayList<PosicaoCamara>();

        try {
            JSONArray jPosicoes = new JSONArray(dados);

            for (int i = 0; i < jPosicoes.length() ; i++) {
                PosicaoCamara posicaoCamara = new PosicaoCamara();
                posicaoCamara.setId(jPosicoes.getJSONObject(i).getLong("id"));
                posicaoCamara.setDescricao(jPosicoes.getJSONObject(i).getString("descricao"));

                posicaoCamaras.add(posicaoCamara);
            }
        }catch (Exception e){
            Log.e("IPAPP_MA", e.getMessage());
        }

    }

    public void callServerPosicoesCamara(final String method, final String data){

        String ipServidor = SharedPreferencesUtil.getPreferences(getActivity(), "ip_servidor");

        String endereco_ws = SharedPreferencesUtil.getPreferences(getActivity(), "endereco_ws");

        String porta_servidor = SharedPreferencesUtil.getPreferences(getActivity(), "porta_servidor");

        String resposta = HttpConnection.getSetDataWeb("http://" + ipServidor + ":" + porta_servidor + endereco_ws + "getPosicoesCamara", method, data);

        if (!resposta.isEmpty())
            getPosicoesJSON(resposta);
    }

    public void getPosicoesCamara(){

        new Thread(new Runnable() {
            @Override
            public void run() {

                JSONObject object = new JSONObject();
                try {
                    object.put("id", camara.getId());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                callServerPosicoesCamara("post-json", object.toString());

                Message msg = new Message();
                msg.what = ATUALIZAR_POSICOES;
                handler.sendMessage(msg);

            }
        }).start();
    }

    public void showDialogAdvertencia(String msg){
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle("Advertência");

        builder.setIcon(R.drawable.ic_alert_grey600_48dp);

        builder.setMessage(msg);

        builder.setPositiveButton("Fechar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
                alertDialog.show();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void showDialogInformacao(String msg){
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle("Informação");

        builder.setIcon(R.drawable.ic_information_grey600_48dp);

        builder.setMessage(msg);

        builder.setPositiveButton("Fechar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (ia != null) {
                    ia.atualizarDados();
                    SessionApp.setInconsistenciasActivity(null);
                }
                dialog.cancel();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private Handler handler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            switch (msg.what) {

                case ATUALIZAR_DADOS:

                    if(camaras != null){
                        SpinnerCamaraAdapter adapter = new SpinnerCamaraAdapter(getActivity(), camaras);
                        spnCamaras.setAdapter(adapter);
                    }

                    if (tipos != null){
                        SpinnerTipoPeixeAdapter adapter = new SpinnerTipoPeixeAdapter(getActivity(), tipos);
                        spnTipos.setAdapter(adapter);
                    }

                    if (tamanhos != null){
                        SpinnerTamanhoAdapter adapter = new SpinnerTamanhoAdapter(getActivity(), tamanhos);
                        spnTamanhos.setAdapter(adapter);
                    }

                    List<String> itens = new ArrayList<String>();
                    itens.add("Processo");
                    itens.add("Descarte");
                    itens.add("Cozinha");
                    //itens.add("Venda");

                    SpinnerDestinoAdapter adapter = new SpinnerDestinoAdapter(getActivity(), itens);
                    spnDestino.setAdapter(adapter);

                    if(aux != null)
                        preencherDados();

                    break;

                case ATUALIZAR_POSICOES:
                    SpinnerPosicoesAdapter posicoesAdapter = new SpinnerPosicoesAdapter(getActivity(), posicaoCamaras);
                    spnPosicoes.setAdapter(posicoesAdapter);

                    if (aux != null){
                        for (int i = 0; i < posicaoCamaras.size(); i++) {
                            if(aux.getPosicaoCamara().getId().longValue() == posicaoCamaras.get(i).getId().longValue()){
                                spnPosicoes.setSelection(i);
                                break;
                            }
                        }
                    }

                    break;
                default:
                    break;
            }
        }
    };

}
