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
import br.com.speedy.appapp_ma.model.Camara;
import br.com.speedy.appapp_ma.model.TamanhoPeixe;

public class SpinnerTamanhoAdapter extends BaseAdapter {

    private List<TamanhoPeixe> tamanhoPeixes;

    private TamanhoPeixe tamanhoPeixe;

    private LayoutInflater mInflater;

    public SpinnerTamanhoAdapter() {
        // TODO Auto-generated constructor stub
    }

    public SpinnerTamanhoAdapter(Context context, List<TamanhoPeixe> tamanhoPeixes){
        this.tamanhoPeixes = tamanhoPeixes;

        mInflater = (LayoutInflater)
                context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
    }

    private class ViewHolder{
        TextView txtId;
        TextView txtDescricao;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return tamanhoPeixes.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return tamanhoPeixes.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return tamanhoPeixes.indexOf(getItem(position));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub

        ViewHolder holder = null;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_item_spinner, null);
            holder = new ViewHolder();
            //holder.txtId = (TextView) convertView.findViewById(R.id.txtIPId);
            holder.txtDescricao = (TextView) convertView.findViewById(R.id.txtSpnDecricao);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        tamanhoPeixe = (TamanhoPeixe) getItem(position);

        //String id = (position + 1) + "";

        //holder.txtId.setText(id);
        holder.txtDescricao.setText(tamanhoPeixe.getDescricao());

        return convertView;
    }

}
