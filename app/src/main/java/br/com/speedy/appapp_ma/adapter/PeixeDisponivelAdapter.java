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
import br.com.speedy.appapp_ma.util.PeixeDisponivel;

public class PeixeDisponivelAdapter extends BaseAdapter {

    private List<PeixeDisponivel> peixes;

    private PeixeDisponivel peixe;

    private LayoutInflater mInflater;

    public PeixeDisponivelAdapter() {
        // TODO Auto-generated constructor stub
    }

    public PeixeDisponivelAdapter(Context context, List<PeixeDisponivel> peixes){
        this.peixes = peixes;

        mInflater = (LayoutInflater)
                context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
    }

    private class ViewHolder{
        TextView txtPeixe;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return peixes.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return peixes.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return peixes.indexOf(getItem(position));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub

        ViewHolder holder = null;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_item_peixe, null);
            holder = new ViewHolder();
            holder.txtPeixe = (TextView) convertView.findViewById(R.id.txtIPPeixe);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        peixe = (PeixeDisponivel) getItem(position);

        holder.txtPeixe.setText(peixe.getDescricao());

        return convertView;
    }

}
