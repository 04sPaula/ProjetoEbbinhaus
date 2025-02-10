package com.paula.ebbinhaus.classes;

public enum Status {
    A_FAZER, 
    EM_PROGRESSO, 
    EM_PAUSA, 
    CONCLUIDO,
    CANCELADO;
    
    public static boolean isAtivo(Status status) {
        return status == EM_PROGRESSO || status == A_FAZER;
    }

    public static boolean isInativo(Status status) {
        return status == EM_PAUSA || status == CONCLUIDO || status == CANCELADO;
    }
}
