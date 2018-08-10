package br.com.speedy.appapp_ma.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import br.com.speedy.appapp_ma.R;
import br.com.speedy.appapp_ma.adapter.ResumoAdapter;
import br.com.speedy.appapp_ma.util.ArmazenamentoRetiradaAux;
import br.com.speedy.appapp_ma.util.DialogUtil;
import br.com.speedy.appapp_ma.util.FormatterUtil;
import br.com.speedy.appapp_ma.util.HttpConnection;
import br.com.speedy.appapp_ma.util.ItemResumo;
import br.com.speedy.appapp_ma.util.SessionApp;
import br.com.speedy.appapp_ma.util.SharedPreferencesUtil;

public class ResumoFragment extends Fragment implements Runnable {

    public static final int ATUALIZAR_LISTA = 1;
    public static final int FINALIZAR_ENVIO = 2;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private List<ItemResumo> itensResumo;

    private ExpandableListView eListView;

    private View itemStatus;

    private View itemLista;

    private ResumoAdapter adapter;

    private Thread threadResumo;

    private TextView txtTotalArmazenar;

    private TextView txtTotalRetirar;

    private View view;

    private BigDecimal totalArm;

    private BigDecimal totalRet;

    private String posto;

    // TODO: Rename and change types of parameters
    public static ResumoFragment newInstance(int position) {

        ResumoFragment fragment = new ResumoFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, position);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ResumoFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        posto = SharedPreferencesUtil.getPreferences(getActivity(), "posto");

        view = inflater.inflate(R.layout.fragment_resumo_list, container, false);

        eListView = (ExpandableListView) view.findViewById(R.id.eList);

        eListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                ItemResumo i = itensResumo.get(groupPosition);
                showDialogRemoverPai(i, groupPosition);
                return true;
            }
        });

        eListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                ItemResumo i = itensResumo.get(groupPosition);
                ArmazenamentoRetiradaAux ar = itensResumo.get(groupPosition).getListaAR().get(childPosition);
                showDialogRemoverFilho(i, ar, groupPosition, childPosition);
                return true;
            }
        });

        txtTotalArmazenar = (TextView) view.findViewById(R.id.txtRFTotalPesoArm);

        txtTotalRetirar = (TextView) view.findViewById(R.id.txtRFTotalPesoRet);

        txtTotalArmazenar.setText(BigDecimal.ZERO.toString());
        txtTotalRetirar.setText(BigDecimal.ZERO.toString());

        return view;
    }

    public void runThread(){
        threadResumo = new Thread(this);
        threadResumo.start();
    }

    public void getItens(){

        itensResumo = SessionApp.getItens() != null ? SessionApp.getItens() : new ArrayList<ItemResumo>();

        if (itensResumo.size() > 0)
            Collections.sort(itensResumo, new SortByTipo());

        Message msg = new Message();
        msg.what = ATUALIZAR_LISTA;
        handler.sendMessage(msg);
    }

    public class SortByTipo implements Comparator<ItemResumo>{

        @Override
        public int compare(ItemResumo p1, ItemResumo p2) {
            return p1.getTipo().compareToIgnoreCase(p2.getTipo());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        //runThread();

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

                    if (itensResumo != null) {

                        totalArm = BigDecimal.ZERO;
                        totalRet = BigDecimal.ZERO;

                        for (ItemResumo i : itensResumo){
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

                        adapter = new ResumoAdapter(getActivity(), itensResumo, posto);

                        setListAdapter(adapter);

                        for (int i = 0; i < adapter.getGroupCount(); i++) {
                            eListView.expandGroup(i);
                        }
                    }

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
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

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
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

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

    public void showDialogInformacao(String msg){
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

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

}
