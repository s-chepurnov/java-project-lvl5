package hexlet.code;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@AutoConfigureMockMvc
@SpringBootTest
class AppApplicationTests {

    @Test
    void testInit() {
        assertThat(true).isTrue();
    }

//    @Test
//    public void dropDB() throws SQLException {

//        import java.sql.Connection;
//        import java.sql.DriverManager;
//        import java.sql.SQLException;
//        import java.sql.Statement;
//
//        String jdbcURL = "jdbc:h2:./taskManager";
//        String username = "";
//        String password = "";
//
//        Connection connection = DriverManager.getConnection(jdbcURL, username, password);
//        System.out.println("Connected to H2 in-memory database.");
//        Statement statement = connection.createStatement();
//        statement.execute("DROP ALL OBJECTS");
//        System.out.println("all data were DROPPED");
//        connection.close();
//    }

}
