package br.com.speedy.appapp_ma.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.List;

import br.com.speedy.appapp_ma.R;
import br.com.speedy.appapp_ma.enumerated.Posto;
import br.com.speedy.appapp_ma.util.FormatterUtil;
import br.com.speedy.appapp_ma.util.ItemResumo;

/**
 * Created by Henrique Araújo on 2015-03-06.
 */
public class ResumoAdapter extends BaseExpandableListAdapter {

    private ItemResumo itemResumo;

    private List<ItemResumo> itensResumo;

    private Context context;

    private String posto;

    public ResumoAdapter(Context context, List<ItemResumo> itensResumo, String posto){
        this.context = context;
        this.itensResumo = itensResumo;
        this.posto = posto;
    }

    @Override
    public int getGroupCount() {

        return itensResumo.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return itensResumo.get(groupPosition).getListaAR().size();
    }

    @Override
    public Object getGroup(int groupPosition) {

        return itensResumo.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return itensResumo.get(groupPosition).getListaAR().get(childPosition);
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
        TextView txtDescricao;
    }

    private class ViewHolderChild{
        TextView txtId;
        TextView txtDescricao;
        TextView txtCamara;
        TextView txtPosicao;
        TextView txtEmbalagem;
        TextView txtQtdeEmbalagem;
        TextView txtPesoEmbalagem;
        TextView txtVPesoEmbalagem;
        TextView txtDestino;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        ViewHolderGroup holder = null;

        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater)
                    this.context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(android.R.layout.simple_expandable_list_item_1, null);
            holder = new ViewHolderGroup();
            holder.txtDescricao = (TextView) convertView.findViewById(android.R.id.text1);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolderGroup) convertView.getTag();
        }

        itemResumo = (ItemResumo) getGroup(groupPosition);
        int gp = groupPosition + 1;
        holder.txtDescricao.setText(gp + " - " + itemResumo.getTipo() + " (" + getChildrenCount(groupPosition) + ")");

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        ViewHolderChild holder = null;

        itemResumo = (ItemResumo) getGroup(groupPosition);

        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater) this.context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

            if (itemResumo.getTipo().equals("Armazenar")) {
                convertView = mInflater.inflate(R.layout.list_item_resumo_filho_entrada, null);

                holder = new ViewHolderChild();
                holder.txtId = (TextView) convertView.findViewById(R.id.txtREId);
                holder.txtDescricao = (TextView) convertView.findViewById(R.id.txtREDescricao);
                holder.txtCamara = (TextView) convertView.findViewById(R.id.txtRECamara);
                holder.txtPosicao = (TextView) convertView.findViewById(R.id.txtREPosicao);
                holder.txtEmbalagem = (TextView) convertView.findViewById(R.id.txtREEmbalagem);
                holder.txtQtdeEmbalagem = (TextView) convertView.findViewById(R.id.txtREQtdeEmbalagem);
                holder.txtPesoEmbalagem = (TextView) convertView.findViewById(R.id.txtREPesoEmbalagem);
                holder.txtVPesoEmbalagem = (TextView) convertView.findViewById(R.id.txtVREPesoEmbalagem);
            }else {
                convertView = mInflater.inflate(R.layout.list_item_resumo_filho_saida, null);

                holder = new ViewHolderChild();
                holder.txtId = (TextView) convertView.findViewById(R.id.txtResSId);
                holder.txtDescricao = (TextView) convertView.findViewById(R.id.txtResSDescricao);
                holder.txtCamara = (TextView) convertView.findViewById(R.id.txtResSCamara);
                holder.txtPosicao = (TextView) convertView.findViewById(R.id.txtResSPosicao);
                holder.txtDestino = (TextView) convertView.findViewById(R.id.txtResSDestino);
                holder.txtQtdeEmbalagem = (TextView) convertView.findViewById(R.id.txtResSQtdeEmbalagem);
            }

            convertView.setTag(holder);
        }else{
            holder = (ViewHolderChild) convertView.getTag();
        }

        int gp = groupPosition + 1;
        int cp = childPosition + 1;
        String id = gp + "." + cp;
        holder.txtId.setText(id);

        if (itemResumo.getTipo().equals("Armazenar")) {
            String tamanho = itemResumo.getListaAR().get(childPosition).getTamanhoPeixe() != null ? "tamanho " + itemResumo.getListaAR().get(childPosition).getTamanhoPeixe().getDescricao().toString() : "Sem tamanho";
            String embalagem =  itemResumo.getListaAR().get(childPosition).getEmbalagem() != null ? itemResumo.getListaAR().get(childPosition).getEmbalagem().getDescricao().toString() : "Sem embalagem";

            String descricao = FormatterUtil.getValorFormatado(itemResumo.getListaAR().get(childPosition).getPeso()) + " kg" + " de " + itemResumo.getListaAR().get(childPosition).getPeixe().getDescricao().toString()
                                + ", " + itemResumo.getListaAR().get(childPosition).getTipoPeixe().getDescricao().toString() + ", " + tamanho;
            holder.txtDescricao.setText(descricao);
            if (!posto.equals(Posto.TUNEL.toString()))
                holder.txtCamara.setText(itemResumo.getListaAR().get(childPosition).getCamara().getDescricao().toString());
            else
                holder.txtCamara.setText("--");

            if (!posto.equals(Posto.TUNEL.toString()))
                holder.txtPosicao.setText(itemResumo.getListaAR().get(childPosition).getPosicaoCamara().getDescricao().toString());
            else
                holder.txtPosicao.setText("--");
            holder.txtEmbalagem.setText(embalagem);
            holder.txtQtdeEmbalagem.setText(itemResumo.getListaAR().get(childPosition).getQtdeEmbalagem().toString());
        }else{
            String tamanho = itemResumo.getListaAR().get(childPosition).getTamanhoPeixe() != null ? "tamanho " + itemResumo.getListaAR().get(childPosition).getTamanhoPeixe().getDescricao().toString() : "Sem tamanho";
            String descricao = FormatterUtil.getValorFormatado(itemResumo.getListaAR().get(childPosition).getPeso()) + " kg" + " de " + itemResumo.getListaAR().get(childPosition).getPeixe().getDescricao().toString()
                    + ", " + itemResumo.getListaAR().get(childPosition).getTipoPeixe().getDescricao().toString() + ", " + tamanho;
            holder.txtDescricao.setText(descricao);

            if (!posto.equals(Posto.TUNEL.toString()))
                holder.txtCamara.setText(itemResumo.getListaAR().get(childPosition).getCamara().getDescricao().toString());
            else
                holder.txtCamara.setText("--");

            if (!posto.equals(Posto.TUNEL.toString()))
                holder.txtPosicao.setText(itemResumo.getListaAR().get(childPosition).getPosicaoCamara().getDescricao().toString());
            else
                holder.txtPosicao.setText("--");

            holder.txtDestino.setText(itemResumo.getListaAR().get(childPosition).getDestino());
            holder.txtQtdeEmbalagem.setText(itemResumo.getListaAR().get(childPosition).getQtdeEmbalagem().toString());
        }

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {

        return true;
    }
}
