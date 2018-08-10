package br.com.speedy.appapp_ma.enumerated;

/**
 * Created by henriquearaujo on 09/10/17.
 */

public enum Posto {

    RECEBIMENTO_TERRESTRE("Recebimento Terrestre"),
    RECEBIMENTO_FLUVIAL("Recebimento Fluvial"),
    TUNEL("Túnel"),
    ESTOCAGEM_1("Estocagem 1"),
    ESTOCAGEM_2("Estocagem 2"),
    EXPEDICAO_1("Expedição 1"),
    EXPEDICAO_2("Expedição 2");

    private String descricao;

    private Posto(String descricao){
        this.descricao = descricao;
    }

    public String getDescricao(){
        return descricao;
    }
}
