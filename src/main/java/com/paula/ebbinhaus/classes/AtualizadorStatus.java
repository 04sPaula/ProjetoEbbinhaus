package com.paula.ebbinhaus.classes;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AtualizadorStatus {
    public static void atualizarStatusConteudosTestesVencidos() {
        String sql = """
            UPDATE Conteudo c
            JOIN Teste t ON c.idTeste = t.id
            SET c.status = 'CONCLUIDO'
            WHERE t.data < CURDATE() 
            AND c.status != 'CONCLUIDO'
        """;

        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            int conteudosAtualizados = stmt.executeUpdate();
            if (conteudosAtualizados > 0) {
                System.out.println(conteudosAtualizados + " conteúdo(s) foram marcados como concluídos devido a testes passados.");
            }
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar status dos conteúdos: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
