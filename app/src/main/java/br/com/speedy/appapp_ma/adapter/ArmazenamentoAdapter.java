package br.com.speedy.appapp_ma.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import br.com.speedy.appapp_ma.R;
import br.com.speedy.appapp_ma.model.Armazenamento;
import br.com.speedy.appapp_ma.util.PeixeDisponivel;

public class ArmazenamentoAdapter extends BaseAdapter {

    private List<Armazenamento> armazenamentos;

    private Armazenamento armazenamento;

    private LayoutInflater mInflater;

    public ArmazenamentoAdapter() {
        // TODO Auto-generated constructor stub
    }

    public ArmazenamentoAdapter(Context context, List<Armazenamento> armazenamentos){
        this.armazenamentos = armazenamentos;

        mInflater = (LayoutInflater)
                context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
    }

    private class ViewHolder{
        TextView txtPeixe;
        TextView txtCamara;
        TextView txtCurral;
        TextView txtPeso;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return armazenamentos.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return armazenamentos.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return armazenamentos.indexOf(getItem(position));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub

        ViewHolder holder = null;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_item_armazenamento, null);
            holder = new ViewHolder();
            holder.txtPeixe = (TextView) convertView.findViewById(R.id.txtArmPeixe);
            holder.txtCamara= (TextView) convertView.findViewById(R.id.txtArmCamara);
            holder.txtCurral = (TextView) convertView.findViewById(R.id.txtArmCurral);
            holder.txtPeso = (TextView) convertView.findViewById(R.id.txtArmPeso);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        armazenamento = (Armazenamento) getItem(position);

        holder.txtPeixe.setText(armazenamento.getPeixe().getDescricao());
        holder.txtCamara.setText(armazenamento.getCamara());
        holder.txtCurral.setText(armazenamento.getCurral());
        holder.txtPeso.setText(armazenamento.getPeso().toString() + "kg");

        return convertView;
    }

}
