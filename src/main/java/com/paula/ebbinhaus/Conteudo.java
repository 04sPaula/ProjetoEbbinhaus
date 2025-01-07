package com.paula.ebbinhaus;

public class Conteudo {
    private int id;
    private int idTeste;
    private int idDisciplina;
    private String nome;
    private String descricao;
    private Status status;

    public enum Status{
        A_FAZER, EM_PROGRESSO, EM_PAUSA, CONCLUIDO
    }

    public Conteudo(int id, String nome, String descricao, Status status) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.status = status;

    }

    public Conteudo(int id, String nome, Status status) {
        this.id = id;
        this.nome = nome;
        this.status = status;
        this.descricao = null;
    }

    // Getters e setters

    public int getId() {
        return id;
    }

    public int getIdTeste() {
        return idTeste;
    }

    public void setIdTeste(int idTeste) {
        this.idTeste = idTeste;
    }

    public int getIdDisciplina() {
        return idDisciplina;
    }

    public void setIdDisciplina(int idDisciplina) {
        this.idDisciplina = idDisciplina;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
