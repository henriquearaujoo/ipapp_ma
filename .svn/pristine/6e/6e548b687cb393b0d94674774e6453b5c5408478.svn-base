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
import br.com.speedy.appapp_ma.model.Peixe;

public class SpinnerCamaraAdapter extends BaseAdapter {

    private List<Camara> camaras;

    private Camara camara;

    private LayoutInflater mInflater;

    public SpinnerCamaraAdapter() {
        // TODO Auto-generated constructor stub
    }

    public SpinnerCamaraAdapter(Context context, List<Camara> camaras){
        this.camaras = camaras;

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
        return camaras.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return camaras.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return camaras.indexOf(getItem(position));
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

        camara = (Camara) getItem(position);

        //String id = (position + 1) + "";

        //holder.txtId.setText(id);
        holder.txtDescricao.setText(camara.getDescricao());

        return convertView;
    }

}
