package br.com.speedy.appapp_ma;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.DialogFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import br.com.speedy.appapp_ma.adapter.EstoqueAdapter;
import br.com.speedy.appapp_ma.dialog.DialogFiltroPesquisaEstoque;
import br.com.speedy.appapp_ma.model.TipoPeixe;
import br.com.speedy.appapp_ma.util.DialogUtil;
import br.com.speedy.appapp_ma.util.HttpConnection;
import br.com.speedy.appapp_ma.util.ItemEstoque;
import br.com.speedy.appapp_ma.util.SessionApp;
import br.com.speedy.appapp_ma.util.SharedPreferencesUtil;
import br.com.speedy.appapp_ma.util.TipoPeixeUtil;


public class EstoqueActivity extends ActionBarActivity implements Runnable, SwipeRefreshLayout.OnRefreshListener {

    public static final int ATUALIZAR_LISTA = 1;
    public static final int ATUALIZAR_LISTA_SWIPE = 2;
    public static final int ABRIR_DIALOG_FILTRO = 3;

    private SwipeRefreshLayout refreshLayout;

    private View estoqueStatus;

    private View msgItensNaoEncontrados;

    private Thread threadEstoque;

    private EstoqueAdapter adapter;

    private List<ItemEstoque> itensEstoque;

    private List<TipoPeixe> tipos;

    private ExpandableListView listViewEstoque;

    private Button btPesquisar;

    private Button btFiltro;

    private Button btLimparFiltro;

    private BigDecimal totalPesoArm;

    private TextView txtTotalPesoArm;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estoque);

        btFiltro = (Button) findViewById(R.id.btEFiltro);

        btFiltro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirFiltro();
            }
        });

        btLimparFiltro = (Button) findViewById(R.id.btELimparFiltro);

        btLimparFiltro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SessionApp.setFiltroDataFinal(null);
                SessionApp.setFiltroDataInicial(null);
                SessionApp.setFiltroTipo(null);
                SessionApp.setFiltroPeixe(null);
                DialogUtil.showDialogInformacao(EstoqueActivity.this, "O filtro de pesquisa foi limpo.");
            }
        });

        btPesquisar = (Button) findViewById(R.id.btEPesquisar);

        btPesquisar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgress(true);
                threadEstoque = new Thread(EstoqueActivity.this);
                threadEstoque.start();
            }
        });

        estoqueStatus = (View) findViewById(R.id.estoque_status);
        msgItensNaoEncontrados = (View) findViewById(R.id.obs_msg_item_nao_encontrado);

        listViewEstoque = (ExpandableListView) findViewById(R.id.eList);

        txtTotalPesoArm = (TextView) findViewById(R.id.txtETotalPeso);

        initSwipeDownToRefresh(getWindow().getDecorView().getRootView());

    }

    public void abrirFiltro(){

        progressDialog = ProgressDialog.show(EstoqueActivity.this, "", "Carregando, aguarde.", false, false);

        new Thread(new Runnable() {
            @Override
            public void run() {
                callServerTipos("get-json", "");

                Message msg = new Message();
                msg.what = ABRIR_DIALOG_FILTRO;
                handler.sendMessage(msg);
            }
        }).start();
    }

    public void limparFiltro(){

    }

    public void getEstoqueJSON(String dados){

        totalPesoArm = BigDecimal.ZERO;

        itensEstoque = new ArrayList<ItemEstoque>();

        try{
            JSONObject object = new JSONObject(dados);

            try{
                JSONArray jEstoques = object.getJSONArray("estoques");

                for (int i = 0; i < jEstoques.length() ; i++) {
                    ItemEstoque itemEstoque = itemAdicionado(jEstoques.getJSONObject(i).getString("peixe"), jEstoques.getJSONObject(i).getString("camara"));

                    if(itemEstoque == null){
                        itemEstoque = new ItemEstoque();
                        itemEstoque.setPeixe(jEstoques.getJSONObject(i).getString("peixe"));
                        itemEstoque.setCamara(jEstoques.getJSONObject(i).getString("camara"));
                        itensEstoque.add(itemEstoque);
                    }

                    TipoPeixeUtil tipoPeixeUtil = new TipoPeixeUtil();
                    tipoPeixeUtil.setTipo(jEstoques.getJSONObject(i).getString("tipo"));
                    tipoPeixeUtil.setPeso(new BigDecimal(jEstoques.getJSONObject(i).getString("peso")));
                    tipoPeixeUtil.setPesoRetirada(new BigDecimal(jEstoques.getJSONObject(i).getString("pesoRetirada")));

                    if (itemEstoque.getTipos() == null || itemEstoque.getTipos().size() == 0){
                        itemEstoque.setTipos(new ArrayList<TipoPeixeUtil>());
                        itemEstoque.getTipos().add(tipoPeixeUtil);
                    }else{
                        itemEstoque.getTipos().add(tipoPeixeUtil);
                    }

                    totalPesoArm = totalPesoArm.add(tipoPeixeUtil.getPeso().subtract(tipoPeixeUtil.getPesoRetirada()));
                }
            }catch (Exception e){
                e.printStackTrace();
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public ItemEstoque itemAdicionado(String peixe, String camara){

        for (ItemEstoque iea : itensEstoque){
            if (iea.getPeixe().equals(peixe) && iea.getCamara().equals(camara))
                return iea;
        }

        return null;
    }

    private void callServerEstoque(final String method, final String data){

        String ipServidor = SharedPreferencesUtil.getPreferences(EstoqueActivity.this, "ip_servidor");

        String endereco_ws = SharedPreferencesUtil.getPreferences(EstoqueActivity.this, "endereco_ws");

        String porta_servidor = SharedPreferencesUtil.getPreferences(EstoqueActivity.this, "porta_servidor");

        String resposta = HttpConnection.getSetDataWeb("http://" + ipServidor + ":" + porta_servidor + endereco_ws + "getEstoquePorCamara", method, data);

        if (!resposta.isEmpty())
            getEstoqueJSON(resposta);
    }

    public void getTiposJSON(String dados){

        tipos = new ArrayList<TipoPeixe>();

        TipoPeixe tp = new TipoPeixe();
        tp.setId(null);
        tp.setDescricao("Selecione o tipo");

        tipos.add(tp);

        try{
            JSONObject object = new JSONObject(dados);

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

    private void callServerTipos(final String method, final String data){

        String ipServidor = SharedPreferencesUtil.getPreferences(EstoqueActivity.this, "ip_servidor");

        String endereco_ws = SharedPreferencesUtil.getPreferences(EstoqueActivity.this, "endereco_ws");

        String porta_servidor = SharedPreferencesUtil.getPreferences(EstoqueActivity.this, "porta_servidor");

        String resposta = HttpConnection.getSetDataWeb("http://" + ipServidor + ":" + porta_servidor + endereco_ws + "getTipos", method, data);

        if (!resposta.isEmpty())
            getTiposJSON(resposta);
    }

    private void showProgress(final boolean show) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(
                    android.R.integer.config_shortAnimTime);

            refreshLayout.setVisibility(show ? View.GONE : View.VISIBLE);
            refreshLayout.animate().setDuration(shortAnimTime)
                    .alpha(show ? 0 : 1)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            refreshLayout.setVisibility(show ? View.GONE
                                    : View.VISIBLE);
                        }
                    });

            estoqueStatus.setVisibility(View.VISIBLE);
            estoqueStatus.animate().setDuration(shortAnimTime)
                    .alpha(show ? 1 : 0)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            estoqueStatus.setVisibility(show ? View.VISIBLE
                                    : View.GONE);
                        }
                    });
        } else {
            refreshLayout.setVisibility(show ? View.GONE : View.VISIBLE);
            estoqueStatus.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    private Handler handler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            switch (msg.what) {

                case ATUALIZAR_LISTA:

                    adapter = new EstoqueAdapter(EstoqueActivity.this, itensEstoque);

                    listViewEstoque.setAdapter(adapter);

                    showProgress(false);

                    if (itensEstoque != null && itensEstoque.size() > 0) {
                        refreshLayout.setVisibility(View.VISIBLE);
                        msgItensNaoEncontrados.setVisibility(View.GONE);
                    }else{
                        refreshLayout.setVisibility(View.GONE);
                        msgItensNaoEncontrados.setVisibility(View.VISIBLE);
                    }

                    for (int i = 0; i < adapter.getGroupCount(); i++) {
                        listViewEstoque.expandGroup(i);
                    }

                    txtTotalPesoArm.setText(totalPesoArm.toString() + "kg");

                    break;

                case ATUALIZAR_LISTA_SWIPE:

                    adapter = new EstoqueAdapter(EstoqueActivity.this, itensEstoque);

                    listViewEstoque.setAdapter(adapter);

                    for (int i = 0; i < adapter.getGroupCount(); i++) {
                        listViewEstoque.expandGroup(i);
                    }

                    txtTotalPesoArm.setText(totalPesoArm.toString() + "kg");

                    stopSwipeRefresh();

                    break;

                case ABRIR_DIALOG_FILTRO:

                    if (progressDialog != null && progressDialog.isShowing()){
                        progressDialog.dismiss();
                        progressDialog = null;
                    }

                    DialogFragment df = new DialogFiltroPesquisaEstoque(tipos);
                    df.show(getSupportFragmentManager(), "filtroEstoque");

                    break;
                default:
                    break;
            }
        }
    };

    public void getEstoque(){
        JSONObject object = new JSONObject();

        try {
            object.put("id", SessionApp.getFiltroTipo() != null ? SessionApp.getFiltroTipo().getId() : null);
            object.put("descricao", SessionApp.getFiltroPeixe() != null && !SessionApp.getFiltroPeixe().isEmpty() ? SessionApp.getFiltroPeixe() : null);
            object.put("dataInicial", SessionApp.getFiltroDataInicial() != null && !SessionApp.getFiltroDataInicial().isEmpty() ? SessionApp.getFiltroDataInicial() : null);
            object.put("dataFinal", SessionApp.getFiltroDataFinal() != null && !SessionApp.getFiltroDataFinal().isEmpty() ? SessionApp.getFiltroDataFinal() : null);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        callServerEstoque("post-json", object.toString());

        Message msg = new Message();
        msg.what = ATUALIZAR_LISTA;
        handler.sendMessage(msg);
    }

    public void initSwipeDownToRefresh(View view){
        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refreshLayout);
        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setColorScheme(android.R.color.holo_blue_light,
                android.R.color.white, android.R.color.holo_blue_light,
                android.R.color.white);
    }

    @Override
    public void onRefresh() {

        new Thread(new Runnable() {
            @Override
            public void run() {

                JSONObject object = new JSONObject();

                try {
                    object.put("id", SessionApp.getFiltroTipo() != null ? SessionApp.getFiltroTipo().getId() : null);
                    object.put("descricao", SessionApp.getFiltroPeixe() != null && !SessionApp.getFiltroPeixe().isEmpty() ? SessionApp.getFiltroPeixe() : null);
                    object.put("dataInicial", SessionApp.getFiltroDataInicial() != null && !SessionApp.getFiltroDataInicial().isEmpty() ? SessionApp.getFiltroDataInicial() : null);
                    object.put("dataFinal", SessionApp.getFiltroDataFinal() != null && !SessionApp.getFiltroDataFinal().isEmpty() ? SessionApp.getFiltroDataFinal() : null);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                callServerEstoque("post-json", object.toString());

                Message msg = new Message();
                msg.what = ATUALIZAR_LISTA_SWIPE;
                handler.sendMessage(msg);
            }
        }).start();
    }

    private void stopSwipeRefresh() {
        refreshLayout.setRefreshing(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_estoque, menu);
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

    @Override
    public void run() {
        getEstoque();
    }
}
