package br.com.speedy.appapp_ma.util;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by henrique on 20/04/2015.
 */
public class ItemEstoque {

    private String peixe;

    private String camara;

    private List<TipoPeixeUtil> tipos;

    public String getPeixe() {
        return peixe;
    }

    public void setPeixe(String peixe) {
        this.peixe = peixe;
    }

    public List<TipoPeixeUtil> getTipos() {
        return tipos;
    }

    public void setTipos(List<TipoPeixeUtil> tipos) {
        this.tipos = tipos;
    }

    public BigDecimal getPesoTotal(){

        BigDecimal total = BigDecimal.ZERO;

        if(tipos != null && tipos.size() > 0){
            for(TipoPeixeUtil tipoPeixeUtil : tipos)
                total = total.add(tipoPeixeUtil.getPeso().subtract(tipoPeixeUtil.getPesoRetirada()));
        }

        return total;
    }

    public String getCamara() {
        return camara;
    }

    public void setCamara(String camara) {
        this.camara = camara;
    }
}
