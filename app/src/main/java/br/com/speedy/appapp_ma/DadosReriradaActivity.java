package br.com.speedy.appapp_ma;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import br.com.speedy.appapp_ma.adapter.SpinnerCamaraAdapter;
import br.com.speedy.appapp_ma.adapter.SpinnerDestinoAdapter;
import br.com.speedy.appapp_ma.adapter.SpinnerEmbalagemAdapter;
import br.com.speedy.appapp_ma.adapter.SpinnerPosicoesAdapter;
import br.com.speedy.appapp_ma.adapter.SpinnerTamanhoAdapter;
import br.com.speedy.appapp_ma.adapter.SpinnerTipoPeixeAdapter;
import br.com.speedy.appapp_ma.enumerated.Posto;
import br.com.speedy.appapp_ma.model.Camara;
import br.com.speedy.appapp_ma.model.Embalagem;
import br.com.speedy.appapp_ma.model.PosicaoCamara;
import br.com.speedy.appapp_ma.model.TamanhoPeixe;
import br.com.speedy.appapp_ma.model.TipoPeixe;
import br.com.speedy.appapp_ma.util.ArmazenamentoRetiradaAux;
import br.com.speedy.appapp_ma.util.FormatterUtil;
import br.com.speedy.appapp_ma.util.HttpConnection;
import br.com.speedy.appapp_ma.util.ItemResumo;
import br.com.speedy.appapp_ma.util.SessionApp;
import br.com.speedy.appapp_ma.util.SharedPreferencesUtil;


public class DadosReriradaActivity extends ActionBarActivity implements Runnable {

    public static final int ATUALIZAR_DADOS = 1;
    public static final int ATUALIZAR_POSICOES = 2;

    private List<Camara> camaras;
    private List<Embalagem> embalagems;
    private List<TipoPeixe> tipoPeixes;
    private List<TamanhoPeixe> tamanhoPeixes;
    private List<PosicaoCamara> posicaoCamaras;

    private Spinner spnDestino;
    private Spinner spnTipoPeixe;
    private Spinner spnTamanho;
    private Spinner spnEmbalagem;
    private Spinner spnCamara;
    private Spinner spnPosicao;
    private EditText edtQtdeEmbalagem;
    private EditText edtPeso;
    private EditText edtObservacoes;
    private EditText edtPesoEmbalagem;
    private TextView txtPeixe;

    private Button btArmSalvar;

    private Button btArmFechar;

    private View dadosStatus;

    private View scrollDados;

    private View llPesoEmbalagem;

    private View llPosicao;

    private View llCamara;

    private Thread threadDados;

    private ProgressDialog progressDialog;

    private Camara camara;

    private ArmazenamentoRetiradaAux aux;

    private InconsistenciasActivity ia;

    private String posto;

    public DadosReriradaActivity(){

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dados_retirada);

        posto = SharedPreferencesUtil.getPreferences(this, "posto");

        ia = SessionApp.getInconsistenciasActivity();

        dadosStatus = findViewById(R.id.dados_status);

        scrollDados = findViewById(R.id.scrollDadosArmazenamento);

        llPesoEmbalagem = findViewById(R.id.llDAPesoEmbalagem);

        llCamara = findViewById(R.id.llDACamara);

        llPosicao = findViewById(R.id.llDAPosicao);

        spnDestino = (Spinner) findViewById(R.id.spnDSDestino);
        spnTipoPeixe = (Spinner) findViewById(R.id.spnDATipoPeixe);
        spnTamanho = (Spinner) findViewById(R.id.spnDATamanho);
        spnEmbalagem = (Spinner) findViewById(R.id.spnDAEmbalagem);
        spnCamara = (Spinner) findViewById(R.id.spnDACamara);
        spnPosicao = (Spinner) findViewById(R.id.spnDAPosicao);
        edtQtdeEmbalagem = (EditText) findViewById(R.id.edtDAQtdeEmbalagem);
        edtPeso = (EditText) findViewById(R.id.edtDAPeso);
        edtObservacoes = (EditText) findViewById(R.id.edtDAObservacoes);
        edtPesoEmbalagem = (EditText) findViewById(R.id.edtDAPesoEmbalagem);
        txtPeixe = (TextView) findViewById(R.id.txtDAPeixe);
        btArmSalvar = (Button) findViewById(R.id.btArmSalvar);
        btArmFechar = (Button) findViewById(R.id.btArmFechar);

        btArmSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                prepararSalvarArmazenamento();
            }
        });

        btArmFechar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        txtPeixe.setText(SessionApp.getPeixe().getDescricao());

        aux = SessionApp.getArAux();

        spnCamara.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                camara = (Camara) spnCamara.getSelectedItem();
                getPosicoesCamara();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spnEmbalagem.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Embalagem embalagem = (Embalagem) spnEmbalagem.getSelectedItem();
                if(embalagem.getDescricao().toLowerCase().equals("outro"))
                    llPesoEmbalagem.setVisibility(View.VISIBLE);
                else
                    llPesoEmbalagem.setVisibility(View.GONE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        if (posto.equals(Posto.TUNEL.toString())){
            llCamara.setVisibility(View.GONE);
            llPosicao.setVisibility(View.GONE);
        }else{
            llCamara.setVisibility(View.VISIBLE);
            llPosicao.setVisibility(View.VISIBLE);
        }

        showProgress(true);
        threadDados = new Thread(DadosReriradaActivity.this);
        threadDados.start();
    }

    public void getDadosArmazenamento(){

        callServer("get-json", "");

        Message msg = new Message();
        msg.what = ATUALIZAR_DADOS;
        handler.sendMessage(msg);
    }

    public void getDadosArmazenamentoJSON(String data){
        camaras = new ArrayList<Camara>();
        embalagems = new ArrayList<Embalagem>();
        tipoPeixes = new ArrayList<TipoPeixe>();
        tamanhoPeixes = new ArrayList<TamanhoPeixe>();

        try {
            JSONObject object = new JSONObject(data);

            JSONArray jCamaras = object.getJSONArray("camaras");

            JSONArray jEmbalagens = object.getJSONArray("embalagens");

            JSONArray jTamanhos = object.getJSONArray("tamanhos");

            JSONArray jTipos = object.getJSONArray("tipos");

            for (int i = 0; i < jCamaras.length() ; i++) {
                Camara camara = new Camara();
                camara.setId(jCamaras.getJSONObject(i).getLong("id"));
                camara.setDescricao(jCamaras.getJSONObject(i).getString("descricao"));

                camaras.add(camara);
            }

            Embalagem emb = new Embalagem();
            emb.setDescricao("Sem embalagem");

            embalagems.add(emb);

            for (int i = 0; i < jEmbalagens.length() ; i++) {
                Embalagem embalagem = new Embalagem();
                embalagem.setId(jEmbalagens.getJSONObject(i).getLong("id"));
                embalagem.setDescricao(jEmbalagens.getJSONObject(i).getString("descricao"));

                embalagems.add(embalagem);
            }

            TamanhoPeixe tp = new TamanhoPeixe();
            tp.setId(new Long(24));
            tp.setDescricao("Sem tamanho");

            tamanhoPeixes.add(tp);

            for (int i = 0; i < jTamanhos.length() ; i++) {
                TamanhoPeixe tamanhoPeixe = new TamanhoPeixe();
                tamanhoPeixe.setId(jTamanhos.getJSONObject(i).getLong("id"));
                tamanhoPeixe.setDescricao(jTamanhos.getJSONObject(i).getString("descricao"));

                tamanhoPeixes.add(tamanhoPeixe);
            }

            for (int i = 0; i < jTipos.length() ; i++) {
                TipoPeixe tipoPeixe = new TipoPeixe();
                tipoPeixe.setId(jTipos.getJSONObject(i).getLong("id"));
                tipoPeixe.setDescricao(jTipos.getJSONObject(i).getString("descricao"));

                tipoPeixes.add(tipoPeixe);
            }

        }catch (Exception e){
            e.printStackTrace();
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

    private void callServer(final String method, final String data){

        String ipServidor = SharedPreferencesUtil.getPreferences(DadosReriradaActivity.this, "ip_servidor");

        String endereco_ws = SharedPreferencesUtil.getPreferences(DadosReriradaActivity.this, "endereco_ws");

        String porta_servidor = SharedPreferencesUtil.getPreferences(DadosReriradaActivity.this, "porta_servidor");

        String resposta = HttpConnection.getSetDataWeb("http://" + ipServidor + ":" + porta_servidor + endereco_ws + "getDadosArmazenamento", method, data);

        if (!resposta.isEmpty())
            getDadosArmazenamentoJSON(resposta);
    }

    public void callServerPosicoesCamara(final String method, final String data){

        String ipServidor = SharedPreferencesUtil.getPreferences(DadosReriradaActivity.this, "ip_servidor");

        String endereco_ws = SharedPreferencesUtil.getPreferences(DadosReriradaActivity.this, "endereco_ws");

        String porta_servidor = SharedPreferencesUtil.getPreferences(DadosReriradaActivity.this, "porta_servidor");

        String resposta = HttpConnection.getSetDataWeb("http://" + ipServidor + ":" + porta_servidor + endereco_ws + "getPosicoesCamara", method, data);

        if (!resposta.isEmpty())
            getPosicoesJSON(resposta);
    }


    public void getPosicoesCamara(){
        //if (progressDialog == null)
        //    progressDialog = ProgressDialog.show(DadosArmazenamentoActivity.this, "", "Carregando...", false, false);

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

    private Handler handler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            switch (msg.what) {

                case ATUALIZAR_DADOS:

                    SpinnerCamaraAdapter camaraAdapter = new SpinnerCamaraAdapter(DadosReriradaActivity.this, camaras);
                    spnCamara.setAdapter(camaraAdapter);

                    SpinnerEmbalagemAdapter embalagemAdapter = new SpinnerEmbalagemAdapter(DadosReriradaActivity.this, embalagems);
                    spnEmbalagem.setAdapter(embalagemAdapter);

                    SpinnerTamanhoAdapter tamanhoAdapter = new SpinnerTamanhoAdapter(DadosReriradaActivity.this, tamanhoPeixes);
                    spnTamanho.setAdapter(tamanhoAdapter);

                    SpinnerTipoPeixeAdapter tipoPeixeAdapter = new SpinnerTipoPeixeAdapter(DadosReriradaActivity.this, tipoPeixes);
                    spnTipoPeixe.setAdapter(tipoPeixeAdapter);

                    List<String> itens = new ArrayList<String>();
                    itens.add("Processo");
                    itens.add("Descarte");
                    itens.add("Cozinha");

                    SpinnerDestinoAdapter adapter = new SpinnerDestinoAdapter(DadosReriradaActivity.this, itens);
                    spnDestino.setAdapter(adapter);

                    if(aux != null) {
                        preencherDados();
                    }

                    showProgress(false);

                    break;

                case ATUALIZAR_POSICOES:

                    SpinnerPosicoesAdapter posicoesAdapter = new SpinnerPosicoesAdapter(DadosReriradaActivity.this, posicaoCamaras);
                    spnPosicao.setAdapter(posicoesAdapter);

                    if (aux != null){
                        for (int i = 0; i < posicaoCamaras.size(); i++) {
                            if(aux.getPosicaoCamara().getId().longValue() == posicaoCamaras.get(i).getId().longValue()){
                                spnPosicao.setSelection(i);
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

    public void preencherDados(){

        for (int i = 0; i < camaras.size(); i++) {
            if(aux.getCamara().getId().longValue() == camaras.get(i).getId().longValue()){
                spnCamara.setSelection(i);
                camara = aux.getCamara();
                break;
            }
        }

        for (int i = 0; i < embalagems.size(); i++) {
            if (aux.getEmbalagem() != null) {
                if (embalagems.get(i).getId() != null && aux.getEmbalagem().getId().longValue() == embalagems.get(i).getId().longValue()) {
                    spnEmbalagem.setSelection(i);

                    if (aux.getEmbalagem().getDescricao().toLowerCase().equals("outro"))
                        edtPesoEmbalagem.setText(aux.getPesoEmbalagem().toString());
                    break;
                }
            }else
                spnEmbalagem.setSelection(0);
        }

        for (int i = 0; i < tamanhoPeixes.size(); i++) {
            if (aux.getTamanhoPeixe() != null) {
                if ( tamanhoPeixes.get(i).getId() != null && aux.getTamanhoPeixe().getId().longValue() == tamanhoPeixes.get(i).getId().longValue()) {
                    spnTamanho.setSelection(i);
                    break;
                }
            }else
                spnTamanho.setSelection(0);
        }

        for (int i = 0; i < tipoPeixes.size(); i++) {
            if(aux.getTipoPeixe().getId().longValue() == tipoPeixes.get(i).getId().longValue()){
                spnTipoPeixe.setSelection(i);
                break;
            }
        }

        if (aux.getDestino().toLowerCase().equals("processo"))
            spnDestino.setSelection(0);
        else if (aux.getDestino().toLowerCase().equals("descarte"))
            spnDestino.setSelection(1);
        else if (aux.getDestino().toLowerCase().equals("cozinha"))
            spnDestino.setSelection(2);
        else
            spnDestino.setSelection(3);

        edtPeso.setText(FormatterUtil.getValorFormatado(aux.getPeso()));
        edtQtdeEmbalagem.setText(aux.getQtdeEmbalagem().toString());
        txtPeixe.setText(aux.getPeixe().getDescricao().toString());

    }

    public void salvarArmazenamento(){
        try {

            if(aux == null) {
                if (!posto.equals(Posto.TUNEL.toString())) {
                    if (edtPeso.getText() != null && !edtPeso.getText().toString().isEmpty()
                            && edtQtdeEmbalagem.getText() != null && !edtQtdeEmbalagem.getText().toString().isEmpty()
                            && spnCamara.getSelectedItem() != null && spnPosicao.getSelectedItem() != null
                            && spnTipoPeixe.getSelectedItem() != null && spnDestino.getSelectedItem() != null) {

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
                        aux.setPeso(new BigDecimal(edtPeso.getText().toString().replace(",", ".")));
                        aux.setQtdeEmbalagem(Integer.parseInt(edtQtdeEmbalagem.getText().toString()));
                        aux.setObservacoes(edtObservacoes.getText() != null && !edtObservacoes.getText().toString().isEmpty() ? edtObservacoes.getText().toString() : null);
                        aux.setCamara((Camara) spnCamara.getSelectedItem());
                        aux.setPosicaoCamara((PosicaoCamara) spnPosicao.getSelectedItem());
                        aux.setEmbalagem(spnEmbalagem.getSelectedItem() != null ? (Embalagem) spnEmbalagem.getSelectedItem() : null);
                        aux.setTipoPeixe((TipoPeixe) spnTipoPeixe.getSelectedItem());
                        aux.setTamanhoPeixe(spnTamanho.getSelectedItem() != null ? (TamanhoPeixe) spnTamanho.getSelectedItem() : null);
                        aux.setPeixe(SessionApp.getPeixe());
                        aux.setDestino((String) spnDestino.getSelectedItem());
                        aux.setPesoEmbalagem(edtPesoEmbalagem.getText() != null && !edtPesoEmbalagem.getText().toString().isEmpty() ? new BigDecimal(edtPesoEmbalagem.getText().toString().replace(",", ".")) : null);

                        if (itemResumo.getListaAR() == null || itemResumo.getListaAR().size() == 0) {
                            itemResumo.setListaAR(new ArrayList<ArmazenamentoRetiradaAux>());
                            itemResumo.getListaAR().add(aux);
                        } else
                            itemResumo.getListaAR().add(aux);

                        showDialogInformacao("Retirada adicionado com sucesso.");

                    } else {
                        showDialogAdvertencia("Preencha todos os campos obrigatórios (*) antes de salvar.");
                    }
                }else{
                    if (edtPeso.getText() != null && !edtPeso.getText().toString().isEmpty()
                            && edtQtdeEmbalagem.getText() != null && !edtQtdeEmbalagem.getText().toString().isEmpty()
                            && spnTipoPeixe.getSelectedItem() != null && spnDestino.getSelectedItem() != null) {

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
                        aux.setPeso(new BigDecimal(edtPeso.getText().toString().replace(",", ".")));
                        aux.setQtdeEmbalagem(Integer.parseInt(edtQtdeEmbalagem.getText().toString()));
                        aux.setObservacoes(edtObservacoes.getText() != null && !edtObservacoes.getText().toString().isEmpty() ? edtObservacoes.getText().toString() : null);
                        aux.setCamara((Camara) spnCamara.getSelectedItem());
                        aux.setPosicaoCamara((PosicaoCamara) spnPosicao.getSelectedItem());
                        aux.setEmbalagem(spnEmbalagem.getSelectedItem() != null ? (Embalagem) spnEmbalagem.getSelectedItem() : null);
                        aux.setTipoPeixe((TipoPeixe) spnTipoPeixe.getSelectedItem());
                        aux.setTamanhoPeixe(spnTamanho.getSelectedItem() != null ? (TamanhoPeixe) spnTamanho.getSelectedItem() : null);
                        aux.setPeixe(SessionApp.getPeixe());
                        aux.setDestino((String) spnDestino.getSelectedItem());
                        aux.setPesoEmbalagem(edtPesoEmbalagem.getText() != null && !edtPesoEmbalagem.getText().toString().isEmpty() ? new BigDecimal(edtPesoEmbalagem.getText().toString().replace(",", ".")) : null);

                        if (itemResumo.getListaAR() == null || itemResumo.getListaAR().size() == 0) {
                            itemResumo.setListaAR(new ArrayList<ArmazenamentoRetiradaAux>());
                            itemResumo.getListaAR().add(aux);
                        } else
                            itemResumo.getListaAR().add(aux);

                        showDialogInformacao("Retirada adicionado com sucesso.");

                    } else {
                        showDialogAdvertencia("Preencha todos os campos obrigatórios (*) antes de salvar.");
                    }
                }

            }else{
                if (!posto.equals(Posto.TUNEL.toString())) {
                    if (edtPeso.getText() != null && !edtPeso.getText().toString().isEmpty()
                            && edtQtdeEmbalagem.getText() != null && !edtQtdeEmbalagem.getText().toString().isEmpty()
                            && spnCamara.getSelectedItem() != null && spnPosicao.getSelectedItem() != null
                            && spnTipoPeixe.getSelectedItem() != null && spnDestino.getSelectedItem() != null) {

                        for (ItemResumo itemResumo : SessionApp.getInconsistencias()) {
                            if (itemResumo.getTipo().equals("Retirar")) {
                                for (ArmazenamentoRetiradaAux aux1 : itemResumo.getListaAR()) {
                                    if (aux1.getId().longValue() == aux.getId().longValue()) {
                                        aux1.setPeso(new BigDecimal(edtPeso.getText().toString().replace(",", ".")));
                                        aux1.setQtdeEmbalagem(Integer.parseInt(edtQtdeEmbalagem.getText().toString()));
                                        aux1.setObservacoes(edtObservacoes.getText() != null && !edtObservacoes.getText().toString().isEmpty() ? edtObservacoes.getText().toString() : null);
                                        aux1.setCamara((Camara) spnCamara.getSelectedItem());
                                        aux1.setPosicaoCamara((PosicaoCamara) spnPosicao.getSelectedItem());
                                        aux1.setEmbalagem(spnEmbalagem.getSelectedItem() != null ? (Embalagem) spnEmbalagem.getSelectedItem() : null);
                                        aux1.setTipoPeixe((TipoPeixe) spnTipoPeixe.getSelectedItem());
                                        aux1.setTamanhoPeixe(spnTamanho.getSelectedItem() != null ? (TamanhoPeixe) spnTamanho.getSelectedItem() : null);
                                        aux1.setPeixe(SessionApp.getPeixe());
                                        aux1.setDestino((String) spnDestino.getSelectedItem());
                                        aux1.setPesoEmbalagem(edtPesoEmbalagem.getText() != null && !edtPesoEmbalagem.getText().toString().isEmpty() ? new BigDecimal(edtPesoEmbalagem.getText().toString().replace(",", ".")) : null);

                                        break;
                                    }

                                }
                            }
                        }

                        showDialogInformacao("Retirada editada com sucesso.");
                    } else {
                        showDialogAdvertencia("Preencha todos os campos obrigatórios (*) antes de salvar.");
                    }
                }else{
                    if (edtPeso.getText() != null && !edtPeso.getText().toString().isEmpty()
                            && edtQtdeEmbalagem.getText() != null && !edtQtdeEmbalagem.getText().toString().isEmpty()
                            && spnTipoPeixe.getSelectedItem() != null && spnDestino.getSelectedItem() != null) {

                        for (ItemResumo itemResumo : SessionApp.getInconsistencias()) {
                            if (itemResumo.getTipo().equals("Retirar")) {
                                for (ArmazenamentoRetiradaAux aux1 : itemResumo.getListaAR()) {
                                    if (aux1.getId().longValue() == aux.getId().longValue()) {
                                        aux1.setPeso(new BigDecimal(edtPeso.getText().toString().replace(",", ".")));
                                        aux1.setQtdeEmbalagem(Integer.parseInt(edtQtdeEmbalagem.getText().toString()));
                                        aux1.setObservacoes(edtObservacoes.getText() != null && !edtObservacoes.getText().toString().isEmpty() ? edtObservacoes.getText().toString() : null);
                                        aux1.setCamara((Camara) spnCamara.getSelectedItem());
                                        aux1.setPosicaoCamara((PosicaoCamara) spnPosicao.getSelectedItem());
                                        aux1.setEmbalagem(spnEmbalagem.getSelectedItem() != null ? (Embalagem) spnEmbalagem.getSelectedItem() : null);
                                        aux1.setTipoPeixe((TipoPeixe) spnTipoPeixe.getSelectedItem());
                                        aux1.setTamanhoPeixe(spnTamanho.getSelectedItem() != null ? (TamanhoPeixe) spnTamanho.getSelectedItem() : null);
                                        aux1.setPeixe(SessionApp.getPeixe());
                                        aux1.setDestino((String) spnDestino.getSelectedItem());
                                        aux1.setPesoEmbalagem(edtPesoEmbalagem.getText() != null && !edtPesoEmbalagem.getText().toString().isEmpty() ? new BigDecimal(edtPesoEmbalagem.getText().toString().replace(",", ".")) : null);

                                        break;
                                    }

                                }
                            }
                        }

                        showDialogInformacao("Retirada editada com sucesso.");
                    } else {
                        showDialogAdvertencia("Preencha todos os campos obrigatórios (*) antes de salvar.");
                    }
                }
            }
        }catch (Exception e){
            showDialogAdvertencia("Não foi possivel salvar a retirada.");
        }
    }

    public void prepararSalvarArmazenamento(){

        if (!posto.equals(Posto.TUNEL.toString())) {
            if ((edtPeso.getText() != null && !edtPeso.getText().toString().isEmpty())
                    && (edtQtdeEmbalagem.getText() != null && !edtQtdeEmbalagem.getText().toString().isEmpty())
                    && spnCamara.getSelectedItem() != null && spnPosicao.getSelectedItem() != null
                    && spnTipoPeixe.getSelectedItem() != null) {
                salvarArmazenamento();
            } else {
                showDialogAdvertencia("Preencha todos os campos obrigatórios (*) antes de salvar.");
            }
        }else{
            if ((edtPeso.getText() != null && !edtPeso.getText().toString().isEmpty())
                    && (edtQtdeEmbalagem.getText() != null && !edtQtdeEmbalagem.getText().toString().isEmpty())
                    && spnDestino.getSelectedItem() != null
                    && spnTipoPeixe.getSelectedItem() != null) {
                salvarArmazenamento();
            } else {
                showDialogAdvertencia("Preencha todos os campos obrigatórios (*) antes de salvar.");
            }
        }

    }

    private void showProgress(final boolean show) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(
                    android.R.integer.config_shortAnimTime);

            dadosStatus.setVisibility(View.VISIBLE);
            dadosStatus.animate().setDuration(shortAnimTime)
                    .alpha(show ? 1 : 0)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            dadosStatus.setVisibility(show ? View.VISIBLE
                                    : View.GONE);
                        }
                    });

            scrollDados.setVisibility(View.VISIBLE);
            scrollDados.animate().setDuration(shortAnimTime)
                    .alpha(show ? 0 : 1)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            scrollDados.setVisibility(show ? View.GONE
                                    : View.VISIBLE);
                        }
                    });

        } else {
            dadosStatus.setVisibility(show ? View.VISIBLE : View.GONE);
            scrollDados.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_dados_armazenamento, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id){
            case R.id.action_save:
                prepararSalvarArmazenamento();
                break;
            case R.id.action_close:
                finish();
                break;
        }
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void run() {
        getDadosArmazenamento();
    }

    public void showDialogAdvertencia(String msg){
        final AlertDialog.Builder builder = new AlertDialog.Builder(DadosReriradaActivity.this);

        builder.setTitle("Advertência");

        builder.setIcon(R.drawable.ic_alert_grey600_48dp);

        builder.setMessage(msg);

        builder.setNeutralButton("Fechar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void showDialogInformacao(String msg){
        final AlertDialog.Builder builder = new AlertDialog.Builder(DadosReriradaActivity.this);

        builder.setTitle("Informação");

        builder.setIcon(R.drawable.ic_information_grey600_48dp);

        builder.setMessage(msg);

        builder.setNeutralButton("Fechar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if(ia != null) {
                    ia.atualizarDados();
                    SessionApp.setInconsistenciasActivity(null);
                }
                dialog.cancel();
                finish();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
