package br.com.speedy.appapp_ma;

import java.util.List;
import java.util.Locale;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import br.com.speedy.appapp_ma.fragment.EntradaFragment;
import br.com.speedy.appapp_ma.fragment.ResumoFragment;
import br.com.speedy.appapp_ma.fragment.SaidaFragment;
import br.com.speedy.appapp_ma.util.ArmazenamentoRetiradaAux;
import br.com.speedy.appapp_ma.util.DialogUtil;
import br.com.speedy.appapp_ma.util.HttpConnection;
import br.com.speedy.appapp_ma.util.ItemResumo;
import br.com.speedy.appapp_ma.util.SessionApp;
import br.com.speedy.appapp_ma.util.SharedPreferencesUtil;


public class MainActivity extends ActionBarActivity implements ActionBar.TabListener {

    public static final int ATUALIZAR_INCONSISTENCIAS = 1;
    public static final int FINALIZAR_ENVIO = 2;
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    private Fragment fragmentResumo;

    private Button btBuscarDados;

    private Button btSalvarDados;

    private Button btEnviarDados;

    private Button btInconsistencias;

    private Button btEstoque;

    private Boolean executando;

    private List<ItemResumo> itemResumos;

    private ProgressDialog progressDialog;

    private Boolean valido;

    private Integer numInconsistencias;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up the action bar.
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        actionBar.setSubtitle("Usuário: " + SessionApp.getUsuario().getNome());

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }

        btBuscarDados = (Button) findViewById(R.id.btMBuscarDados);
        btBuscarDados.setText("  Buscar");
        btSalvarDados = (Button) findViewById(R.id.btMSalvarDados);
        btSalvarDados.setText("  Salvar");
        btEnviarDados = (Button) findViewById(R.id.btMEnviarDados);
        btEnviarDados.setText("  Enviar dados");
        btInconsistencias = (Button) findViewById(R.id.btMInconsistencias);
        btEstoque = (Button) findViewById(R.id.btMEstoque);
        btEstoque.setText("  Estoque");

        btBuscarDados.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prepararEnviarDados();
            }
        });

        btSalvarDados.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prepararSalvarDados();
            }
        });

        btEnviarDados.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prepararEnviarDados();
            }
        });

        btInconsistencias.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, InconsistenciasActivity.class);
                startActivity(i);
            }
        });

        btEstoque.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, EstoqueActivity.class);
                startActivity(i);
            }
        });

        executando = true;
        iniciarVerificacaoInconsistenmcias();
    }

    public void iniciarVerificacaoInconsistenmcias(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (executando){
                    try{

                        JSONObject object = new JSONObject();
                        object.put("id", SessionApp.getUsuario().getId());

                        String resposta = callServerInconsistencias("post-jason", object.toString());

                        JSONObject object1 = new JSONObject(resposta);

                        numInconsistencias = Integer.parseInt(object1.getString("num"));

                        Message msg = new Message();
                        msg.what = ATUALIZAR_INCONSISTENCIAS;
                        handler.sendMessage(msg);

                        Thread.sleep(5000);
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }
            }
        }).start();
    }

    private String callServerInconsistencias(final String method, final String data){

        String ipServidor = SharedPreferencesUtil.getPreferences(MainActivity.this, "ip_servidor");

        String endereco_ws = SharedPreferencesUtil.getPreferences(MainActivity.this, "endereco_ws");

        String porta_servidor = SharedPreferencesUtil.getPreferences(MainActivity.this, "porta_servidor");

        String resposta = HttpConnection.getSetDataWeb("http://" + ipServidor + ":" + porta_servidor + endereco_ws + "getNumInconsistencias", method, data);

        return resposta;

    }

    private String callServer(final String method, final String data){

        String ipServidor = SharedPreferencesUtil.getPreferences(MainActivity.this, "ip_servidor");

        String endereco_ws = SharedPreferencesUtil.getPreferences(MainActivity.this, "endereco_ws");

        String porta_servidor = SharedPreferencesUtil.getPreferences(MainActivity.this, "porta_servidor");

        String resposta = HttpConnection.getSetDataWeb("http://" + ipServidor + ":" + porta_servidor + endereco_ws + "salvarArmazenamentosERetiradas", method, data);

        return resposta;

    }

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            switch (msg.what) {
                case ATUALIZAR_INCONSISTENCIAS:

                    if (numInconsistencias > 0) {
                        btInconsistencias.setVisibility(View.VISIBLE);
                        btInconsistencias.setText(" " + numInconsistencias + " inconsistência(s)");
                    }else{
                        btInconsistencias.setVisibility(View.GONE);
                        btInconsistencias.setText("Inconsistências");
                    }

                    break;
                case FINALIZAR_ENVIO:

                    if (progressDialog != null && progressDialog.isShowing()){
                        progressDialog.dismiss();
                        progressDialog = null;
                    }

                    if(valido) {
                        limparSessionApp();
                        //runThread();
                        showDialogInformacao("Dados enviados com sucesso");
                    }else
                        DialogUtil.showDialogAdvertencia(MainActivity.this, "Não foi possivel enviar os dados.");

                    break;
            }
        }
    };

    public void prepararEnviarDados(){

        itemResumos = SessionApp.getItens();

        if (itemResumos != null && itemResumos.size() > 0){
            showDialogConfirmacaoEnvio("Confirma o envio dos dados?");
        }else{
            DialogUtil.showDialogAdvertencia(MainActivity.this, "Adicione pelo menos um armazenamento ou uma retirada antes de enviar os dados.");
        }

    }

    public void prepararSalvarDados(){

        itemResumos = SessionApp.getItens();

        if (itemResumos != null && itemResumos.size() > 0){
            showDialogConfirmacaoSalvar("Deseja realmente salvar os dados?");
        }else{
            DialogUtil.showDialogAdvertencia(MainActivity.this, "Adicione pelo menos um armazenamento ou uma retirada antes de salvar os dados.");
        }

    }

    public void showDialogConfirmacaoEnvio(String msg){
        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

        builder.setTitle("Confirmação");

        builder.setMessage(msg);

        builder.setNegativeButton("Fechar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        builder.setPositiveButton("Enviar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                enviarDados("enviar");
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void showDialogConfirmacaoSalvar(String msg){
        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

        builder.setTitle("Confirmação");

        builder.setMessage(msg);

        builder.setNegativeButton("Fechar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        builder.setPositiveButton("Enviar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                enviarDados("salvar");
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void showDialogInformacao(String msg){
        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

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

    public void enviarDados(final String status){

        progressDialog = ProgressDialog.show(MainActivity.this, "", "Finalizando, aguarde.", false, false);

        new Thread(new Runnable() {
            @Override
            public void run() {
                JSONObject objectEnvio = new JSONObject();

                try{
                    JSONArray jLista = new JSONArray();

                    for (ItemResumo itemResumo : itemResumos){

                        for (ArmazenamentoRetiradaAux aux : itemResumo.getListaAR()) {

                            JSONObject jItemResumo = new JSONObject();
                            jItemResumo.put("status", status);
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

                            JSONObject jTamanho = new JSONObject();

                            if (aux.getTamanhoPeixe() != null)
                                jTamanho.put("id", aux.getTamanhoPeixe().getId());

                            jItemResumo.put("tamanhoPeixe", jTamanho);

                            if (itemResumo.getTipo().equals("Armazenar")){

                                JSONObject jEmbalagem = new JSONObject();

                                if (aux.getEmbalagem() != null)
                                    jEmbalagem.put("id", aux.getEmbalagem().getId());

                                jItemResumo.put("embalagem", jEmbalagem);

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

                    String resposta = callServer("post-json", objectEnvio.toString());

                    JSONObject jResposta = new JSONObject(resposta);

                    valido = jResposta.getBoolean("valido");

                    Message msg = new Message();
                    msg.what = FINALIZAR_ENVIO;
                    handler.sendMessage(msg);

                }catch (Exception e){
                    Log.e("IPAPP_MA - Resumo", e.getMessage());
                    DialogUtil.showDialogAdvertencia(MainActivity.this, "Não foi possivel enviar os dados.");
                }
            }
        }).start();
    }

    public void limparSessionApp(){
        SessionApp.setPeixe(null);
        SessionApp.setItens(null);
        SessionApp.setArAux(null);
        SessionApp.setInconsistencias(null);
        SessionApp.setInconsistenciasActivity(null);
        SessionApp.setFiltroDataFinal(null);
        SessionApp.setFiltroDataInicial(null);
        SessionApp.setFiltroTipo(null);
        SessionApp.setFiltroPeixe(null);
        onTabSelected(getSupportActionBar().getTabAt(0), null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());

        if(tab.getPosition() == 2){
            ((ResumoFragment) fragmentResumo).runThread();
        }
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            Fragment fragment = null;

            switch (position){
                case 0:
                    fragment = EntradaFragment.newInstance(position + 1);
                    break;
                case 1:
                    fragment = SaidaFragment.newInstance(position + 1);
                    break;
                case 2:
                    fragmentResumo = new ResumoFragment().newInstance(position + 1);
                    fragment = fragmentResumo;
                    break;
            }

            return fragment;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_section1).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section2).toUpperCase(l);
                case 2:
                    return  getString(R.string.title_section3).toUpperCase(l);
            }
            return null;
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }
    }

}
