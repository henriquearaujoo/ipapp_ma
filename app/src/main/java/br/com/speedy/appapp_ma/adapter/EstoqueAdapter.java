package br.com.speedy.appapp_ma.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.math.BigDecimal;
import java.util.List;

import br.com.speedy.appapp_ma.R;
import br.com.speedy.appapp_ma.util.ItemEstoque;

/**
 * Created by Henrique Araújo on 2015-03-06.
 */
public class EstoqueAdapter extends BaseExpandableListAdapter {

    private ItemEstoque itemEstoque;

    private List<ItemEstoque> itensEstoque;

    private Context context;

    public EstoqueAdapter(Context context, List<ItemEstoque> itensEstoque){
        this.context = context;
        this.itensEstoque = itensEstoque;
    }

    @Override
    public int getGroupCount() {

        return itensEstoque.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return itensEstoque.get(groupPosition).getTipos().size();
    }

    @Override
    public Object getGroup(int groupPosition) {

        return itensEstoque.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return itensEstoque.get(groupPosition).getTipos().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    private class ViewHolderGroup{
        TextView txtPeixe;
    }

    private class ViewHolderChild{
        TextView txtId;
        TextView txtDescricao;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        ViewHolderGroup holder = null;

        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater)
                    this.context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(android.R.layout.simple_expandable_list_item_1, null);
            holder = new ViewHolderGroup();
            holder.txtPeixe = (TextView) convertView.findViewById(android.R.id.text1);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolderGroup) convertView.getTag();
        }

        itemEstoque = (ItemEstoque) getGroup(groupPosition);
        int gp = groupPosition + 1;
        BigDecimal pesoTotal = itemEstoque.getPesoTotal();
        holder.txtPeixe.setText(gp + " - " + itemEstoque.getPeixe() + " (" + pesoTotal.toString() + "kg) - " + itemEstoque.getCamara());

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        ViewHolderChild holder = null;

        itemEstoque = (ItemEstoque) getGroup(groupPosition);

        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater) this.context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

            convertView = mInflater.inflate(R.layout.list_item_estoque_filho, null);

            holder = new ViewHolderChild();
            holder.txtId = (TextView) convertView.findViewById(R.id.txtREId);
            holder.txtDescricao = (TextView) convertView.findViewById(R.id.txtIEDescricao);

            convertView.setTag(holder);
        }else{
            holder = (ViewHolderChild) convertView.getTag();
        }

        int gp = groupPosition + 1;
        int cp = childPosition + 1;
        String id = gp + "." + cp;
        holder.txtId.setText(id);

        BigDecimal pesoLiquido = itemEstoque.getTipos().get(childPosition).getPeso().subtract(itemEstoque.getTipos().get(childPosition).getPesoRetirada());
        String descricao = "";
        if (itemEstoque.getTipos().get(childPosition).getPesoRetirada() != null && itemEstoque.getTipos().get(childPosition).getPesoRetirada().compareTo(BigDecimal.ZERO) == 1){
            descricao = itemEstoque.getTipos().get(childPosition).getPeso().toString() + "kg" + " " + itemEstoque.getTipos().get(childPosition).getTipo().toString() + " - "
                    + itemEstoque.getTipos().get(childPosition).getPesoRetirada().toString() + "kg retirados = " + pesoLiquido.toString() + "kg";
        }else {
            descricao = itemEstoque.getTipos().get(childPosition).getPeso().toString() + "kg" + " " + itemEstoque.getTipos().get(childPosition).getTipo().toString();
        }
        holder.txtDescricao.setText(descricao);

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {

        return true;
    }
}
