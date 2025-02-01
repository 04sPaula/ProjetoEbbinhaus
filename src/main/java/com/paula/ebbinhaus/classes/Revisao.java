package com.paula.ebbinhaus.classes;

import java.util.Date;

public class Revisao {
    private int id;
    private int conteudoId;
    private Date dataRevisao;
    private Status status;

    public enum Status{
        A_FAZER, EM_PROGRESSO, EM_PAUSA, CONCLUIDO
    }

    public Revisao(int id, int conteudoId, Date dataRevisao, Status status) {
        this.id = id;
        this.conteudoId = conteudoId;
        this.dataRevisao = dataRevisao;
        this.status = status;
    }

    // Getters e setters

    public int getId() {
        return id;
    }

    public int getconteudoId() {
        return conteudoId;
    }

    public void setconteudoId(int conteudoId) {
        this.conteudoId = conteudoId;
    }

    public Date getDataRevisao() {
        return dataRevisao;
    }

    public void setDataRevisao(Date dataRevisao) {
        this.dataRevisao = dataRevisao;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
