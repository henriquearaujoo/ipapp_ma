package br.com.speedy.appapp_ma;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import br.com.speedy.appapp_ma.adapter.ResumoAdapter;
import br.com.speedy.appapp_ma.dialog.DialogDadosSaida;
import br.com.speedy.appapp_ma.model.Camara;
import br.com.speedy.appapp_ma.model.Embalagem;
import br.com.speedy.appapp_ma.model.Peixe;
import br.com.speedy.appapp_ma.model.PosicaoCamara;
import br.com.speedy.appapp_ma.model.TamanhoPeixe;
import br.com.speedy.appapp_ma.model.TipoPeixe;
import br.com.speedy.appapp_ma.util.ArmazenamentoRetiradaAux;
import br.com.speedy.appapp_ma.util.DialogUtil;
import br.com.speedy.appapp_ma.util.FormatterUtil;
import br.com.speedy.appapp_ma.util.HttpConnection;
import br.com.speedy.appapp_ma.util.ItemResumo;
import br.com.speedy.appapp_ma.util.SessionApp;
import br.com.speedy.appapp_ma.util.SharedPreferencesUtil;

public class InconsistenciasActivity extends ActionBarActivity implements Runnable {

    public static final int ATUALIZAR_LISTA = 1;
    public static final int ABRIR_DIALOG = 2;
    public static final int FINALIZAR_ENVIO = 3;

    private ExpandableListView eListView;

    private View itemStatus;

    private View itemLista;

    private ResumoAdapter adapter;

    private Thread threadInconsistencias;

    private TextView txtTotalArmazenar;

    private TextView txtTotalRetirar;

    private BigDecimal totalArm;

    private BigDecimal totalRet;

    private Button btEnviarDaodos;

    private ProgressDialog progressDialog;

    private Boolean valido;

    private List<Camara> camaras;

    private List<TipoPeixe> tipos;
    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public InconsistenciasActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inconsistencias);

        itemStatus = findViewById(R.id.itens_inc_status);

        itemLista = findViewById(R.id.itens_inc_lista);

        eListView = (ExpandableListView) findViewById(R.id.eList);

        eListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                /*ItemResumo i = SessionApp.getInconsistencias().get(groupPosition);
                showDialogRemoverPai(i, groupPosition);*/
                return true;
            }
        });

        eListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                ItemResumo i = SessionApp.getInconsistencias().get(groupPosition);
                ArmazenamentoRetiradaAux ar = SessionApp.getInconsistencias().get(groupPosition).getListaAR().get(childPosition);
                //showDialogRemoverFilho(i, ar, groupPosition, childPosition);
                showDialogOpcoes(i, ar, groupPosition, childPosition);
                return true;
            }
        });

        btEnviarDaodos = (Button) findViewById(R.id.btRFEnviarArmsERets);

        btEnviarDaodos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prepararEnviarDados();
            }
        });

        txtTotalArmazenar = (TextView) findViewById(R.id.txtRFTotalPesoArm);

        txtTotalRetirar = (TextView) findViewById(R.id.txtRFTotalPesoRet);

        txtTotalArmazenar.setText(BigDecimal.ZERO.toString());
        txtTotalRetirar.setText(BigDecimal.ZERO.toString());

        showProgress(true);
        runThread();
    }

    public void runThread(){
        threadInconsistencias = new Thread(this);
        threadInconsistencias.start();
    }

    public void atualizarDados(){
        showProgress(true);
        Message msg = new Message();
        msg.what = ATUALIZAR_LISTA;
        handler.sendMessage(msg);
    }

    private void callServer(final String method, final String data){

        String ipServidor = SharedPreferencesUtil.getPreferences(InconsistenciasActivity.this, "ip_servidor");

        String endereco_ws = SharedPreferencesUtil.getPreferences(InconsistenciasActivity.this, "endereco_ws");

        String porta_servidor = SharedPreferencesUtil.getPreferences(InconsistenciasActivity.this, "porta_servidor");

        String resposta = HttpConnection.getSetDataWeb("http://" + ipServidor + ":" + porta_servidor + endereco_ws + "getInconsistenciasArmazenamentosERetiradas", method, data);

        if(!resposta.isEmpty())
            getInconsistenciasJSON(resposta);

    }

    private String callServerEnviarDados(final String method, final String data){

        String ipServidor = SharedPreferencesUtil.getPreferences(InconsistenciasActivity.this, "ip_servidor");

        String endereco_ws = SharedPreferencesUtil.getPreferences(InconsistenciasActivity.this, "endereco_ws");

        String porta_servidor = SharedPreferencesUtil.getPreferences(InconsistenciasActivity.this, "porta_servidor");

        String resposta = HttpConnection.getSetDataWeb("http://" + ipServidor + ":" + porta_servidor + endereco_ws + "salvarArmazenamentosERetiradas", method, data);

        return resposta;

    }

    private void callServerCamarasETipos(final String method, final String data){

        String ipServidor = SharedPreferencesUtil.getPreferences(InconsistenciasActivity.this, "ip_servidor");

        String endereco_ws = SharedPreferencesUtil.getPreferences(InconsistenciasActivity.this, "endereco_ws");

        String porta_servidor = SharedPreferencesUtil.getPreferences(InconsistenciasActivity.this, "porta_servidor");

        String resposta = HttpConnection.getSetDataWeb("http://" + ipServidor + ":" + porta_servidor + endereco_ws + "getCamarasETipos", method, data);

        if (!resposta.isEmpty())
            getCamarasETiposJSON(resposta);
    }

    public void prepararEnviarDados(){

        if (SessionApp.getInconsistencias() != null && SessionApp.getInconsistencias().size() > 0){
            showDialogConfirmacaoEnvio("Confirma o reenvio dos dados?");
        }else{
            DialogUtil.showDialogAdvertencia(InconsistenciasActivity.this, "Adicione pelo menos um armazenamento ou uma retirada antes de reenviar os dados.");
        }

    }

    public void enviarDados(){

        progressDialog = ProgressDialog.show(InconsistenciasActivity.this, "", "Enviando, aguarde.", false, false);

        new Thread(new Runnable() {
            @Override
            public void run() {
                JSONObject objectEnvio = new JSONObject();

                try{
                    JSONArray jLista = new JSONArray();

                    for (ItemResumo itemResumo : SessionApp.getInconsistencias()){

                        for (ArmazenamentoRetiradaAux aux : itemResumo.getListaAR()) {

                            JSONObject jItemResumo = new JSONObject();
                            jItemResumo.put("id", aux.getId());
                            jItemResumo.put("tipo", itemResumo.getTipo());
                            jItemResumo.put("peso", aux.getPeso());
                            jItemResumo.put("qtdeEmbalagem", aux.getQtdeEmbalagem());
                            jItemResumo.put("pesoEmbalagem", aux.getPesoEmbalagem() != null ? aux.getPesoEmbalagem() : "0");
                            jItemResumo.put("observacao", aux.getObservacoes());

                            JSONObject jUsuario = new JSONObject();
                            jUsuario.put("id", SessionApp.getUsuario().getId());

                            jItemResumo.put("usuario", jUsuario);

                            JSONObject jPeixe = new JSONObject();
                            jPeixe.put("id", aux.getPeixe().getId());

                            jItemResumo.put("peixe", jPeixe);

                            JSONObject jCamara = new JSONObject();
                            jCamara.put("id", aux.getCamara().getId());

                            jItemResumo.put("camara", jCamara);

                            JSONObject jPosicao = new JSONObject();
                            jPosicao.put("id", aux.getPosicaoCamara().getId());

                            jItemResumo.put("posicaoCamara", jPosicao);

                            JSONObject jTipoPeixe = new JSONObject();
                            jTipoPeixe.put("id", aux.getTipoPeixe().getId());

                            jItemResumo.put("tipoPeixe", jTipoPeixe);

                            if (itemResumo.getTipo().equals("Armazenar")){

                                JSONObject jEmbalagem = new JSONObject();

                                if (aux.getEmbalagem() != null)
                                    jEmbalagem.put("id", aux.getEmbalagem().getId());

                                jItemResumo.put("embalagem", jEmbalagem);

                                JSONObject jTamanho = new JSONObject();
                                if (aux.getTamanhoPeixe() != null)
                                    jTamanho.put("id", aux.getTamanhoPeixe().getId());

                                jItemResumo.put("tamanhoPeixe", jTamanho);


                            }else{
                                if (aux.getDestino().equals("Processo"))
                                    jItemResumo.put("destino", "PROCESSO");
                                else if (aux.getDestino().equals("Descarte"))
                                    jItemResumo.put("destino", "DESCARTE");
                                else if (aux.getDestino().equals("Cozinha"))
                                    jItemResumo.put("destino", "COZINHA");
                                else
                                    jItemResumo.put("destino", "VENDA");
                            }

                            jLista.put(jItemResumo);
                        }

                    }

                    objectEnvio.put("armazenamentosERetiradas", jLista);

                    String resposta = callServerEnviarDados("post-json", objectEnvio.toString());

                    JSONObject jResposta = new JSONObject(resposta);

                    valido = jResposta.getBoolean("valido");

                    Message msg = new Message();
                    msg.what = FINALIZAR_ENVIO;
                    handler.sendMessage(msg);

                }catch (Exception e){
                    Log.e("IPAPP_MA - Resumo", e.getMessage());
                    DialogUtil.showDialogAdvertencia(InconsistenciasActivity.this, "Não foi possivel enviar os dados.");
                }
            }
        }).start();
    }

    public void getCamarasETiposJSON(String dados){

        camaras = new ArrayList<Camara>();
        tipos = new ArrayList<TipoPeixe>();

        try{
            JSONObject object = new JSONObject(dados);

            JSONArray jCamaras = object.getJSONArray("camaras");

            for (int i = 0; i < jCamaras.length() ; i++) {
                Camara camara = new Camara();
                camara.setId(jCamaras.getJSONObject(i).getLong("id"));
                camara.setDescricao(jCamaras.getJSONObject(i).getString("descricao"));

                camaras.add(camara);
            }

            JSONArray jTipos = object.getJSONArray("tipos");

            for (int i = 0; i < jTipos.length() ; i++) {
                TipoPeixe tipoPeixe = new TipoPeixe();
                tipoPeixe.setId(jTipos.getJSONObject(i).getLong("id"));
                tipoPeixe.setDescricao(jTipos.getJSONObject(i).getString("descricao"));

                tipos.add(tipoPeixe);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void getInconsistenciasJSON(String dados){
        try{
            JSONObject object = new JSONObject(dados);
            JSONArray jListas = object.getJSONArray("inconsistencias");

            SessionApp.setInconsistencias(new ArrayList<ItemResumo>());

            for (int i = 0; i < jListas.length(); i++) {
                String tipo = jListas.getJSONObject(i).getString("tipo");

                if (tipo.equals("Armazenar")){
                    ArmazenamentoRetiradaAux armazenamento = new ArmazenamentoRetiradaAux();
                    armazenamento.setId(jListas.getJSONObject(i).getLong("id"));
                    armazenamento.setPeso(new BigDecimal(jListas.getJSONObject(i).getDouble("peso")));
                    armazenamento.setPesoEmbalagem(new BigDecimal(jListas.getJSONObject(i).getDouble("pesoEmbalagem")));
                    armazenamento.setQtdeEmbalagem(jListas.getJSONObject(i).getInt("qtdeEmbalagem"));

                    Camara camara = new Camara();
                    camara.setId(jListas.getJSONObject(i).getJSONObject("camara").getLong("id"));
                    camara.setDescricao(jListas.getJSONObject(i).getJSONObject("camara").getString("descricao"));
                    armazenamento.setCamara(camara);

                    Embalagem embalagem = new Embalagem();
                    if (jListas.getJSONObject(i).has("embalagem")) {
                        embalagem.setId(jListas.getJSONObject(i).getJSONObject("embalagem").getLong("id"));
                        embalagem.setDescricao(jListas.getJSONObject(i).getJSONObject("embalagem").getString("descricao"));
                        armazenamento.setEmbalagem(embalagem);
                    }

                    Peixe peixe = new Peixe();
                    peixe.setId(jListas.getJSONObject(i).getJSONObject("peixe").getLong("id"));
                    peixe.setDescricao(jListas.getJSONObject(i).getJSONObject("peixe").getString("descricao"));
                    armazenamento.setPeixe(peixe);

                    PosicaoCamara posicaoCamara = new PosicaoCamara();
                    posicaoCamara.setId(jListas.getJSONObject(i).getJSONObject("posicaoCamara").getLong("id"));
                    posicaoCamara.setDescricao(jListas.getJSONObject(i).getJSONObject("posicaoCamara").getString("descricao"));
                    armazenamento.setPosicaoCamara(posicaoCamara);

                    TamanhoPeixe tamanhoPeixe = new TamanhoPeixe();
                    if (jListas.getJSONObject(i).has("tamanhoPeixe")) {
                        tamanhoPeixe.setId(jListas.getJSONObject(i).getJSONObject("tamanhoPeixe").getLong("id"));
                        tamanhoPeixe.setDescricao(jListas.getJSONObject(i).getJSONObject("tamanhoPeixe").getString("descricao"));
                        armazenamento.setTamanhoPeixe(tamanhoPeixe);
                    }

                    TipoPeixe tipoPeixe = new TipoPeixe();
                    tipoPeixe.setId(jListas.getJSONObject(i).getJSONObject("tipoPeixe").getLong("id"));
                    tipoPeixe.setDescricao(jListas.getJSONObject(i).getJSONObject("tipoPeixe").getString("descricao"));
                    armazenamento.setTipoPeixe(tipoPeixe);

                    ItemResumo iA = null;
                    for(ItemResumo itemResumo1 : SessionApp.getInconsistencias()){
                        if(itemResumo1.getTipo().equals("Armazenar"))
                            iA = itemResumo1;
                    }

                    if(iA == null) {
                        iA = new ItemResumo();
                        iA.setTipo("Armazenar");
                        SessionApp.getInconsistencias().add(iA);
                    }

                    if (iA.getListaAR() == null || iA.getListaAR().size() == 0) {
                        iA.setListaAR(new ArrayList<ArmazenamentoRetiradaAux>());
                        iA.getListaAR().add(armazenamento);
                    } else
                        iA.getListaAR().add(armazenamento);

                }else{
                    ArmazenamentoRetiradaAux retirada = new ArmazenamentoRetiradaAux();
                    retirada.setId(jListas.getJSONObject(i).getLong("id"));
                    retirada.setPeso(new BigDecimal(jListas.getJSONObject(i).getDouble("peso")));
                    retirada.setQtdeEmbalagem(jListas.getJSONObject(i).getInt("qtdeEmbalagem"));
                    retirada.setDestino(jListas.getJSONObject(i).getString("destino"));

                    Peixe peixe = new Peixe();
                    peixe.setId(jListas.getJSONObject(i).getJSONObject("peixe").getLong("id"));
                    peixe.setDescricao(jListas.getJSONObject(i).getJSONObject("peixe").getString("descricao"));
                    retirada.setPeixe(peixe);

                    Camara camara = new Camara();
                    camara.setId(jListas.getJSONObject(i).getJSONObject("camara").getLong("id"));
                    camara.setDescricao(jListas.getJSONObject(i).getJSONObject("camara").getString("descricao"));
                    retirada.setCamara(camara);

                    PosicaoCamara posicaoCamara = new PosicaoCamara();
                    posicaoCamara.setId(jListas.getJSONObject(i).getJSONObject("posicaoCamara").getLong("id"));
                    posicaoCamara.setDescricao(jListas.getJSONObject(i).getJSONObject("posicaoCamara").getString("descricao"));
                    retirada.setPosicaoCamara(posicaoCamara);

                    TipoPeixe tipoPeixe = new TipoPeixe();
                    tipoPeixe.setId(jListas.getJSONObject(i).getJSONObject("tipoPeixe").getLong("id"));
                    tipoPeixe.setDescricao(jListas.getJSONObject(i).getJSONObject("tipoPeixe").getString("descricao"));
                    retirada.setTipoPeixe(tipoPeixe);

                    ItemResumo iR = null;
                    for(ItemResumo itemResumo1 : SessionApp.getInconsistencias()){
                        if(!itemResumo1.getTipo().equals("Armazenar"))
                            iR = itemResumo1;
                    }

                    if(iR == null) {
                        iR = new ItemResumo();
                        iR.setTipo("Retirada");
                        SessionApp.getInconsistencias().add(iR);
                    }

                    if (iR.getListaAR() == null || iR.getListaAR().size() == 0) {
                        iR.setListaAR(new ArrayList<ArmazenamentoRetiradaAux>());
                        iR.getListaAR().add(retirada);
                    } else
                        iR.getListaAR().add(retirada);
                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void getItens(){

        try{
            JSONObject object = new JSONObject();
            object.put("id", SessionApp.getUsuario().getId());

            callServer("post-jason", object.toString());

            Message msg = new Message();
            msg.what = ATUALIZAR_LISTA;
            handler.sendMessage(msg);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public class SortByTipo implements Comparator<ItemResumo>{

        @Override
        public int compare(ItemResumo p1, ItemResumo p2) {
            return p1.getTipo().compareToIgnoreCase(p2.getTipo());
        }
    }

    @Override
    public void run() {

        getItens();
    }

    private void showProgress(final boolean show) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(
                    android.R.integer.config_shortAnimTime);

            itemStatus.setVisibility(View.VISIBLE);
            itemStatus.animate().setDuration(shortAnimTime)
                    .alpha(show ? 1 : 0)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            itemStatus.setVisibility(show ? View.VISIBLE
                                    : View.GONE);
                        }
                    });

            itemLista.setVisibility(View.VISIBLE);
            itemLista.animate().setDuration(shortAnimTime)
                    .alpha(show ? 0 : 1)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            itemLista.setVisibility(show ? View.GONE
                                    : View.VISIBLE);
                        }
                    });
        } else {
            itemStatus.setVisibility(show ? View.VISIBLE : View.GONE);
            itemLista.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    private Handler handler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            switch (msg.what) {

                case ATUALIZAR_LISTA:

                    if (SessionApp.getInconsistencias() != null) {

                        totalArm = BigDecimal.ZERO;
                        totalRet = BigDecimal.ZERO;

                        for (ItemResumo i : SessionApp.getInconsistencias()){
                            if (i.getTipo().equals("Armazenar")){
                                for(ArmazenamentoRetiradaAux ar : i.getListaAR()){
                                    totalArm = totalArm.add(ar.getPeso());
                                }
                            }else{
                                for(ArmazenamentoRetiradaAux ar : i.getListaAR()){
                                    totalRet = totalRet.add(ar.getPeso());
                                }
                            }

                        }

                        txtTotalArmazenar.setText(FormatterUtil.getValorFormatado(totalArm) + " kg");
                        txtTotalRetirar.setText(FormatterUtil.getValorFormatado(totalRet) + " kg");

                        //adapter = new ResumoAdapter(InconsistenciasActivity.this, SessionApp.getInconsistencias());

                        setListAdapter(adapter);

                        for (int i = 0; i < adapter.getGroupCount(); i++) {
                            eListView.expandGroup(i);
                        }

                        showProgress(false);

                    }

                    break;
                case ABRIR_DIALOG:

                    if (progressDialog != null && progressDialog.isShowing()){
                        progressDialog.dismiss();
                        progressDialog = null;
                    }

                    DialogFragment dadosSaida = new DialogDadosSaida(InconsistenciasActivity.this, camaras, tipos);
                    dadosSaida.show(getSupportFragmentManager(), "dadosSaida");

                    break;
                case FINALIZAR_ENVIO:

                    if (progressDialog != null && progressDialog.isShowing()){
                        progressDialog.dismiss();
                        progressDialog = null;
                    }

                    if(valido) {
                        SessionApp.setArAux(null);
                        SessionApp.setPeixe(null);
                        SessionApp.setInconsistencias(null);
                        showDialogInformacao("Dados enviados com sucesso");
                    }else
                        DialogUtil.showDialogAdvertencia(InconsistenciasActivity.this, "Não foi possivel enviar os dados.");

                    break;
                default:
                    break;
            }
        }
    };

    public void setListAdapter (ExpandableListAdapter adapter) {

        eListView.setAdapter(adapter);
    }

    public void showDialogRemoverPai(final ItemResumo itemResumo, final int groupPosition){
        final AlertDialog.Builder builder = new AlertDialog.Builder(InconsistenciasActivity.this);

        builder.setTitle("Confirmação");

        builder.setMessage("Deseja remover o item " + itemResumo.getTipo() + " e todos os subitens?");

        builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                removerPai(groupPosition);
            }
        });

        builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                dialog.cancel();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void showDialogRemoverFilho(final ItemResumo i, final ArmazenamentoRetiradaAux ar, final int itemPosition, final int childPosition){
        final AlertDialog.Builder builder = new AlertDialog.Builder(InconsistenciasActivity.this);

        builder.setTitle("Confirmação");

        int item = childPosition + 1;

        builder.setMessage("Deseja remover o item " + item + " do tipo " + i.getTipo() + "?");

        builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                removerFilho(itemPosition, childPosition);
            }
        });
        builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void removerPai(int groupPosition){

        SessionApp.getItens().remove(groupPosition);

        runThread();
    }

    public void removerFilho(int groupPosition, int childPosition){

        SessionApp.getItens().get(groupPosition).getListaAR().remove(childPosition);

        if (SessionApp.getItens().get(groupPosition).getListaAR().size() == 0)
            SessionApp.getItens().remove(groupPosition);

        runThread();
    }

    public void showDialogOpcoes(final ItemResumo i, final ArmazenamentoRetiradaAux ar, final int groupPosition, final int itemPosition){

        final AlertDialog.Builder builder = new AlertDialog.Builder(InconsistenciasActivity.this);

        builder.setTitle("Opções");

        String[] itens = {"Editar item", "Visualizar observações"};

        builder.setItems(itens, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:

                        SessionApp.setArAux(ar);
                        SessionApp.setPeixe(ar.getPeixe());
                        SessionApp.setInconsistenciasActivity(InconsistenciasActivity.this);

                        if (i.getTipo().equals("Armazenar")) {
                            Intent intent = new Intent(InconsistenciasActivity.this, DadosArmazenamentoActivity.class);
                            startActivity(intent);
                        }else{
                            progressDialog = ProgressDialog.show(InconsistenciasActivity.this, "", "Carregando, aguarde.", false, false);

                            new Thread(new Runnable() {
                                @Override
                                public void run() {

                                    callServerCamarasETipos("get-json", "");

                                    Message msg = new Message();
                                    msg.what = ABRIR_DIALOG;
                                    handler.sendMessage(msg);
                                }
                            }).start();
                        }
                        break;
                    case 1:
                        SessionApp.setArAux(ar);
                        Intent intent = new Intent(InconsistenciasActivity.this, ObservacoesActivity.class);
                        intent.putExtra("tipo", i.getTipo().toString());
                        startActivity(intent);
                        break;
                }
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void showDialogConfirmacaoEnvio(String msg){
        final AlertDialog.Builder builder = new AlertDialog.Builder(InconsistenciasActivity.this);

        builder.setTitle("Confirmação");

        builder.setMessage(msg);

        builder.setNegativeButton("Fechar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        builder.setPositiveButton("Enviar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                enviarDados();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void showDialogInformacao(String msg){
        final AlertDialog.Builder builder = new AlertDialog.Builder(InconsistenciasActivity.this);

        builder.setTitle("Informação");

        builder.setIcon(R.drawable.ic_information_grey600_48dp);

        builder.setMessage(msg);

        builder.setPositiveButton("Fechar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
                dialog.cancel();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_inconsistencias, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_close) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
