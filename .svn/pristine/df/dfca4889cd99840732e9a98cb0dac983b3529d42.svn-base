package br.com.speedy.ipapp.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import br.com.speedy.ipapp.R;
import br.com.speedy.ipapp.model.Lote;

public class LoteAdapter extends BaseAdapter {

    private List<Lote> lotes;

    private Lote lote;

    private LayoutInflater mInflater;

    public LoteAdapter() {
        // TODO Auto-generated constructor stub
    }

    public LoteAdapter(Context context, List<Lote> lotes){
        this.lotes = lotes;

        mInflater = (LayoutInflater)
                context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
    }

    private class ViewHolder{
        TextView txtPeixe;
        TextView txtPesoLiquido;
        TextView txtPesoBruto;
        TextView txtQtdeCaixas;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return lotes.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return lotes.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return lotes.indexOf(getItem(position));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub

        ViewHolder holder = null;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_item_lote, null);
            holder = new ViewHolder();
            holder.txtPesoLiquido = (TextView) convertView.findViewById(R.id.txtLotePesoLiquido);
            holder.txtPesoBruto = (TextView) convertView.findViewById(R.id.txtLotePesoBruto);
            holder.txtQtdeCaixas = (TextView) convertView.findViewById(R.id.txtLoteQtdeCaixas);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        lote = (Lote) getItem(position);

        holder.txtPesoLiquido.setText(lote.getPesoLiquido().toString() + "kg");
        holder.txtPesoBruto.setText(lote.getPeso().toString() + "kg");
        holder.txtQtdeCaixas.setText(lote.getQtdCaixas().toString());

        return convertView;
    }

}
