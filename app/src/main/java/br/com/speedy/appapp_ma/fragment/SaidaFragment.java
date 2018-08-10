package br.com.speedy.appapp_ma.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import br.com.speedy.appapp_ma.DadosArmazenamentoActivity;
import br.com.speedy.appapp_ma.DadosReriradaActivity;
import br.com.speedy.appapp_ma.R;
import br.com.speedy.appapp_ma.adapter.PeixeAdapter;
import br.com.speedy.appapp_ma.dialog.DialogDadosSaida;
import br.com.speedy.appapp_ma.model.Camara;
import br.com.speedy.appapp_ma.model.Peixe;
import br.com.speedy.appapp_ma.model.TamanhoPeixe;
import br.com.speedy.appapp_ma.model.TipoPeixe;
import br.com.speedy.appapp_ma.util.DialogUtil;
import br.com.speedy.appapp_ma.util.HttpConnection;
import br.com.speedy.appapp_ma.util.SessionApp;
import br.com.speedy.appapp_ma.util.SharedPreferencesUtil;

public class SaidaFragment extends ListFragment implements Runnable, SwipeRefreshLayout.OnRefreshListener {

    public static final int ATUALIZAR_LISTA = 1;
    public static final int ATUALIZAR_LISTA_SWIPE = 2;
    public static final int ABRIR_DIALOG = 3;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private List<Peixe> peixes;

    private Peixe peixe;

    private View peixesStatus;

    private View peixesLista;

    private View msgItensNaoEncontrados;

    private PeixeAdapter adapter;

    private Thread threadPeixes;

    private SwipeRefreshLayout refreshLayout;

    private Button btPesquisar;

    private EditText edtFiltroPeixe;

    private ImageLoader il;

    private RequestQueue rq;

    private ProgressDialog progressDialog;

    private List<Camara> camaras;

    private List<TipoPeixe> tipos;

    private  List<TamanhoPeixe> tamanhos;

    // TODO: Rename and change types of parameters
    public static SaidaFragment newInstance(int position) {
        SaidaFragment fragment = new SaidaFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, position);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public SaidaFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_saida_list, container, false);

        peixesLista = view.findViewById(R.id.armazenamentos_lista);

        peixesStatus = view.findViewById(R.id.armazenamentos_status);

        msgItensNaoEncontrados = view.findViewById(R.id.fs_msg_item_nao_encontrado);

        edtFiltroPeixe = (EditText) view.findViewById(R.id.edtSFFiltroPeixe);

        btPesquisar = (Button) view.findViewById(R.id.btSFPesquisar);

        btPesquisar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (edtFiltroPeixe.getText() == null || edtFiltroPeixe.getText().toString().isEmpty()){
                    DialogUtil.showDialogAdvertencia(getActivity(), "Preencha o campo de pesquisa.");
                }else {
                    showProgress(true);
                    threadPeixes = new Thread(SaidaFragment.this);
                    threadPeixes.start();
                }
            }
        });

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

        initSwipeDownToRefresh(view);

        return view;
    }

    public void getPeixes(){

        JSONObject object = new JSONObject();
        try {
            object.put("descricao", edtFiltroPeixe.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        callServer("post-json", object.toString());

        Message msg = new Message();
        msg.what = ATUALIZAR_LISTA;
        handler.sendMessage(msg);
    }

    public void getPeixesJSON(String data){
        peixes = new ArrayList<Peixe>();

        try {
            JSONArray ja = new JSONArray(data);

            for (int i = 0; i < ja.length() ; i++) {
                Peixe p = new Peixe();
                p.setId(ja.getJSONObject(i).getLong("id"));
                p.setDescricao(ja.getJSONObject(i).getString("descricao"));
                p.setUrlFoto(ja.getJSONObject(i).getString("urlFoto"));

                peixes.add(p);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void getCamarasTiposETamanhosJSON(String dados){

        camaras = new ArrayList<Camara>();
        tipos = new ArrayList<TipoPeixe>();
        tamanhos = new ArrayList<TamanhoPeixe>();

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

            JSONArray jTamanho = object.getJSONArray("tamanhos");

            TamanhoPeixe tp = new TamanhoPeixe();
            tp.setId(new Long(24));
            tp.setDescricao("Sem tamanho");

            tamanhos.add(tp);

            for (int i = 0; i < jTamanho.length() ; i++) {
                TamanhoPeixe tamanho = new TamanhoPeixe();
                tamanho.setId(jTamanho.getJSONObject(i).getLong("id"));
                tamanho.setDescricao(jTamanho.getJSONObject(i).getString("descricao"));

                tamanhos.add(tamanho);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void callServer(final String method, final String data){

        String ipServidor = SharedPreferencesUtil.getPreferences(getActivity(), "ip_servidor");

        String endereco_ws = SharedPreferencesUtil.getPreferences(getActivity(), "endereco_ws");

        String porta_servidor = SharedPreferencesUtil.getPreferences(getActivity(), "porta_servidor");

        String resposta = HttpConnection.getSetDataWeb("http://" + ipServidor + ":" + porta_servidor + endereco_ws + "getPeixesVendaFiltro", method, data);

        if (!resposta.isEmpty())
            getPeixesJSON(resposta);
    }

    private void callServerCamarasTiposETamanhos(final String method, final String data){

        String ipServidor = SharedPreferencesUtil.getPreferences(getActivity(), "ip_servidor");

        String endereco_ws = SharedPreferencesUtil.getPreferences(getActivity(), "endereco_ws");

        String porta_servidor = SharedPreferencesUtil.getPreferences(getActivity(), "porta_servidor");

        String resposta = HttpConnection.getSetDataWeb("http://" + ipServidor + ":" + porta_servidor + endereco_ws + "getCamarasTiposETamanhos", method, data);

        if (!resposta.isEmpty())
            getCamarasTiposETamanhosJSON(resposta);
    }

    @Override
    public void run() {
        getPeixes();
    }

    public void showDialogInformacao(String msg){
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle("Informação");

        builder.setMessage(msg);

        builder.setPositiveButton("Fechar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
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

            peixesStatus.setVisibility(View.VISIBLE);
            peixesStatus.animate().setDuration(shortAnimTime)
                    .alpha(show ? 1 : 0)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            peixesStatus.setVisibility(show ? View.VISIBLE
                                    : View.GONE);
                        }
                    });
        } else {
            refreshLayout.setVisibility(show ? View.GONE : View.VISIBLE);
            peixesStatus.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    private Handler handler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            switch (msg.what) {

                case ATUALIZAR_LISTA:

                    adapter = new PeixeAdapter(getActivity(), peixes, il, getFragmentManager());

                    setListAdapter(adapter);

                    if (isAdded())
                        showProgress(false);

                    if (peixes != null && peixes.size() > 0) {
                        refreshLayout.setVisibility(View.VISIBLE);
                        msgItensNaoEncontrados.setVisibility(View.GONE);
                    }else{
                        refreshLayout.setVisibility(View.GONE);
                        msgItensNaoEncontrados.setVisibility(View.VISIBLE);
                    }

                    break;

                case ATUALIZAR_LISTA_SWIPE:

                    adapter = new PeixeAdapter(getActivity(), peixes, il, getFragmentManager());

                    setListAdapter(adapter);

                    stopSwipeRefresh();

                    break;
                case ABRIR_DIALOG:

                    if (progressDialog != null && progressDialog.isShowing()){
                        progressDialog.dismiss();
                        progressDialog = null;
                    }

                    DialogFragment dadosSaida = new DialogDadosSaida(getActivity(), camaras, tipos, tamanhos);
                    dadosSaida.show(getFragmentManager(), "dadosSaida");

                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        peixe = (Peixe) this.getListAdapter().getItem(position);

        SessionApp.setPeixe(peixe);

        Intent i = new Intent(getActivity(), DadosReriradaActivity.class);
        startActivity(i);

        /*progressDialog = ProgressDialog.show(getActivity(), "", "Carregando, aguarde.", false, false);

        new Thread(new Runnable() {
            @Override
            public void run() {

                callServerCamarasTiposETamanhos("get-json", "");

                Message msg = new Message();
                msg.what = ABRIR_DIALOG;
                handler.sendMessage(msg);
            }
        }).start();*/

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
                    object.put("descricao", edtFiltroPeixe.getText().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                callServer("post-json", object.toString());

                Message msg = new Message();
                msg.what = ATUALIZAR_LISTA_SWIPE;
                handler.sendMessage(msg);
            }
        }).start();
    }

    private void stopSwipeRefresh() {
        refreshLayout.setRefreshing(false);
    }
}
