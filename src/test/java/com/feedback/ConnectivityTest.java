
package com.feedback;

import org.junit.jupiter.api.Test;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectivityTest {

    @Test
    public void testJdbcConnection() {
        String url = "jdbc:postgresql://localhost:5433/feedback_db";
        String user = "feedback_user";
        String password = "feedback_password";

        System.out.println("Tentando conectar em: " + url);
        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            System.out.println(">>> CONEXÃO JDBC SUCESSO! <<<");
        } catch (SQLException e) {
            System.out.println(">>> FALHA NA CONEXÃO JDBC <<<");
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
