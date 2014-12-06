package ua.com.juja.study.jdbc;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by VICTOR on 04.12.2014.
 */
public class UserDao {
    private static Connection connection;

    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        UserDao userDao = new UserDao();
        Class.forName("org.postgresql.Driver");
        Connection connection = null;
        try {
            connection = establishConnection();
            UserDao.connection = connection;
//            System.out.println(userDao.getUserByEmail("victor.pidleteychuk@gmail.com"));
//            for (User user : userDao.getAllUsers()) {
//                System.out.println(user);
//            }
//            userDao.allTest();
//            userDao.addPhone("+380973482910", "victor.pidleteychuk@gmail.com");
//            userDao.changePhone("+380973482910", "+380973482919");
        } finally {
            closeConnection(connection);
        }
    }

    private static Connection establishConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:postgresql://localhost:5432/juja", "juja_core", "juja");
    }

    private static void closeConnection(Connection connection) throws SQLException {
        if (connection != null)
            connection.close();
    }

    public User getUserByEmail(String email) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("select p.email, p.name, ph.phone from participations p join " +
                "\"Phones\" ph on p.email=ph.email where p.email=?");
        preparedStatement.setString(1, email);
        ResultSet userRs = preparedStatement.executeQuery();
        User user = new User();
        List<String> phones = new LinkedList<>();
        while (userRs.next()) {
            user.setEmail(userRs.getString("email"));
            user.setName(userRs.getString("name"));
            phones.add(userRs.getString("phone"));
        }
        user.setPhoneNumbers(phones);
        userRs.close();
        return user;
    }

    public List<User> getAllUsers() throws SQLException {
        List<User> userList = new LinkedList<>();
        Statement statement = connection.createStatement();
        ResultSet participations = statement.executeQuery("select email from participations");
        while (participations.next()) {
            userList.add(getUserByEmail(participations.getString(1)));
        }
        return userList;
    }

    public void allTest() throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet participations = statement.executeQuery("select email from participations");
        while (participations.next()) {
            System.out.println(getUserByEmail(participations.getString(1)));
        }
    }

    public void addPhone(String phoneNumber, String email) throws SQLException {
        String insertTableSQL = "INSERT INTO \"Phones\""
                + "(phone, email) VALUES"
                + "(?,?)";
        PreparedStatement preparedStatement = connection.prepareStatement(insertTableSQL);
        preparedStatement.setString(1, phoneNumber);
        preparedStatement.setString(2, email);
        preparedStatement.executeUpdate();
        preparedStatement.close();
    }

    public void changePhone(String oldPhone, String newPhone) throws SQLException {
        String updateTableSQL = "UPDATE \"Phones\"" + "SET PHONE = ?"
                + " WHERE PHONE = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(updateTableSQL);
        preparedStatement.setString(1, newPhone);
        preparedStatement.setString(2, oldPhone);
        preparedStatement.executeUpdate();
        preparedStatement.close();
    }
}
