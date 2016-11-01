package br.com.speedy.appapp_ma.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;

import java.util.List;

import br.com.speedy.appapp_ma.R;
import br.com.speedy.appapp_ma.dialog.DialogFotoPeixe;
import br.com.speedy.appapp_ma.model.Peixe;

public class PeixeAdapter extends BaseAdapter {

    private List<Peixe> peixes;

    private Peixe peixe;

    private LayoutInflater mInflater;

    private ImageLoader imageLoader;

    private FragmentManager fragmentManager;

    public PeixeAdapter() {
    }

    public PeixeAdapter(Context context, List<Peixe> peixes, ImageLoader imageLoader, FragmentManager fragmentManager){
        this.peixes = peixes;
        this.imageLoader = imageLoader;
        this.fragmentManager = fragmentManager;

        mInflater = (LayoutInflater)
                context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
    }

    private class ViewHolder{
        TextView txtId;
        TextView txtDescricao;
        ImageView ivFotoPeixe;
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
            holder.txtId = (TextView) convertView.findViewById(R.id.txtIPId);
            holder.txtDescricao = (TextView) convertView.findViewById(R.id.txtIPPeixe);
            holder.ivFotoPeixe = (ImageView) convertView.findViewById(R.id.ivIPFotoPeixe);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        final Peixe peixe = (Peixe) getItem(position);

        String id = (position + 1) + "";

        holder.txtId.setText(id);
        holder.txtDescricao.setText(peixe.getDescricao());
        if (peixe.getUrlFoto() != null)
            imageLoader.get(peixe.getUrlFoto(), imageLoader.getImageListener(holder.ivFotoPeixe, R.drawable.ic_fish_grey600_48dp, R.drawable.ic_fish_grey600_48dp));

        holder.ivFotoPeixe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment dialogFragment = new DialogFotoPeixe(peixe);
                dialogFragment.show(fragmentManager, "fotoPeixe");
            }
        });

        return convertView;
    }

}
