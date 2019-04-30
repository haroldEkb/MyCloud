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

    public static boolean checkUserByLoginAndPass(String login, int pass){
        String sql = String.format("SELECT password FROM mane\n" +
                "WHERE login = '%s'", login);
        try {
            ResultSet rs = statement.executeQuery(sql);

            if(rs.next()){
                int dbHash = rs.getInt(1);
                System.out.println(pass + " " + dbHash);
                return pass == dbHash;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void addUser(String login, String pass, String nick){
        String sql = String.format("INSERT INTO mane (login, password, nickname)\n" +
                "VALUES ('%s','%s', '%s')", login, pass.hashCode(), nick);
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
