package br.com.speedy.ipapp.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import br.com.speedy.ipapp.R;
import br.com.speedy.ipapp.model.Compra;
import br.com.speedy.ipapp.model.Lote;
import br.com.speedy.ipapp.model.Peixe;
import br.com.speedy.ipapp.model.Usuario;
import br.com.speedy.ipapp.util.HttpConnection;
import br.com.speedy.ipapp.util.SessionApp;
import br.com.speedy.ipapp.util.SharedPreferencesUtil;

/**
 * TODO: document your custom view class.
 */
public class DialogDadosConfiguracaoSenha extends DialogFragment {

    public static final int FINALIZAR_PROCESSO = 1;

    private EditText editTextSenha;

    private EditText editTextConfirmacaoSenha;

    private Boolean fecharDialog = false;

    private Usuario usuario;

    private TextView txtMessage;

    private AlertDialog alertDialog;

    private Context context;

    private ProgressDialog progressDialog;

    private Boolean valido;

    public DialogDadosConfiguracaoSenha(){

    }

    public DialogDadosConfiguracaoSenha(Context context){
        this.context = context;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.sample_dialog_dados_configuracao_senha, null);

        this.usuario = SessionApp.getUsuario();

        editTextSenha = (EditText) view.findViewById(R.id.edtCSSenha);

        editTextConfirmacaoSenha = (EditText) view.findViewById(R.id.edtCSConfSenha);

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(view)
                // Add action buttons
                .setPositiveButton("Salvar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {

                                try {
                                    String senha = editTextSenha.getText().toString();

                                    String confirmacaoSenha = editTextConfirmacaoSenha.getText().toString();


                                    if ((senha != null && !senha.isEmpty()) && (confirmacaoSenha != null && !confirmacaoSenha.isEmpty())) {
                                        if (senha.equals(confirmacaoSenha)) {
                                            usuario.setSenha(senha);

                                            atualizarSenha();

                                        } else {
                                            showDialogErro("As senhas devem ser iguais.");
                                        }
                                    }else
                                        showDialogErro("Preecha a senha e a confirmação antes de salvar.");
                                } catch (Exception e) {
                                    showDialogErro("Não foi possivel atualizar a senha.");
                                }
                            }
                        }

                )
                            .

                setNegativeButton("Fechar", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                alertDialog.cancel();
                            }
                        }

                );

        builder.setTitle("Configuração de senha");

        alertDialog = builder.create();

        return alertDialog;
    }

    public void atualizarSenha(){

        if (progressDialog == null)
            progressDialog = ProgressDialog.show(context, "", "Carregando...", false, false);

        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    JSONObject jUsuario = new JSONObject();
                    jUsuario.put("id", usuario.getId());
                    jUsuario.put("senha", usuario.getSenha());

                    String resposta = callServer("post-json", jUsuario.toString());

                    JSONObject jResposta = new JSONObject(resposta);

                    valido = jResposta.getBoolean("valido");

                }catch (Exception e){
                    valido = false;
                }

                Message msg = new Message();
                msg.what = FINALIZAR_PROCESSO;
                handler.sendMessage(msg);

            }
        }).start();

    }

    private String callServer(final String method, final String data){

        String ipServidor = SharedPreferencesUtil.getPreferences(getActivity(), "ip_servidor");

        String endereco_ws = SharedPreferencesUtil.getPreferences(getActivity(), "endereco_ws");

        String resposta = HttpConnection.getSetDataWeb("http://" + ipServidor + ":8080" + endereco_ws + "salvarUsuario", method, data);

        return resposta;

    }

    public void showDialogSucesso(String msg){
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle("Sucesso");

        builder.setIcon(R.drawable.ic_information_grey600_48dp);

        builder.setMessage(msg);

        builder.setNeutralButton("Fechar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void showDialogErro(String msg){
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle("Advertência");

        builder.setIcon(R.drawable.ic_alert_grey600_48dp);

        builder.setMessage(msg);

        builder.setNeutralButton("Fechar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
                alertDialog.show();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private Handler handler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            switch (msg.what) {

                case FINALIZAR_PROCESSO:

                    if (progressDialog != null && progressDialog.isShowing()){
                        progressDialog.dismiss();
                        progressDialog = null;
                    }

                    if (valido) {
                        showDialogSucesso("Senha atualizada com sucesso.");
                    } else {
                        showDialogErro("Não foi possivel atualizar a senha.");
                    }

                    break;
                default:
                    break;
            }
        }
    };

}
