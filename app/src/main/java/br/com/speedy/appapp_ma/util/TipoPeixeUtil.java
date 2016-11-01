package br.com.speedy.appapp_ma.util;

import java.math.BigDecimal;

/**
 * Created by henrique on 20/04/2015.
 */
public class TipoPeixeUtil {

    private String tipo;

    private BigDecimal peso;

    private BigDecimal pesoRetirada;

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public BigDecimal getPeso() {
        return peso;
    }

    public void setPeso(BigDecimal peso) {
        this.peso = peso;
    }

    public BigDecimal getPesoRetirada() {
        return pesoRetirada;
    }

    public void setPesoRetirada(BigDecimal pesoRetirada) {
        this.pesoRetirada = pesoRetirada;
    }
}
