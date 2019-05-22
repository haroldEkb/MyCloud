import java.sql.*;
import java.util.HashSet;

public class AuthService {

    private static Connection connection;
    private static Statement statement;

    public static void connect() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:main.db");
            statement = connection.createStatement();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static int getUserIDByLoginAndPass(String login, int pass){
        String sql = String.format("SELECT id FROM mane\n" +
                "WHERE login = '%s'\n" +
                "AND password = '%s'", login, pass);
        try {
            ResultSet rs = statement.executeQuery(sql);

            if(rs.next()){
                int dbID = rs.getInt(1);
                System.out.println(dbID);
                return dbID;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static void addUser(String login, int pass){
        String sql = String.format("INSERT INTO mane (login, password)\n" +
                "VALUES ('%s','%s')", login, pass);
        try {
            statement.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void disconnect(){
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
