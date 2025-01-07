package com.paula.ebbinhaus;

import java.util.Date;

public class Teste {
    private int id;
    private Date data;

    public Teste(int id, Date data) {
        this.id = id;
        this.data = data;
    }

    public int getId() {
        return id;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }
    
    public void addConteudo(Conteudo conteudo) {
    	conteudo.setIdTeste(id);
    }
}
