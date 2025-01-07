package com.paula.ebbinhaus;

public class Disciplina {
    private int id;
    private String nome;
    private String descricao;
    private Status status;

    public enum Status{
        CURSANDO, CONCLUIDA
    }

    public Disciplina(int id, String nome, String descricao, Status status) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.status = status;
    }

    public Disciplina(int id, String nome, Status status) {
        this.id = id;
        this.nome = nome;
        this.status = status;
        this.descricao = null;
    }

    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public Status getStatus() {
        return status;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

}
