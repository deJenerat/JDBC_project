package com.example.jdbc;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDao {


    public void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS users (" +
                "id SERIAL PRIMARY KEY, " +
                "first_name VARCHAR(50), " +
                "last_name VARCHAR(50), " +
                "age INTEGER)";

        try (Connection conn = Database.getConnection();// создает о. типа - вызов у класса метода
             Statement stmt = conn.createStatement()) {//о. отправки запросов и пол.инфы от бд
            stmt.execute(sql);//bool, query- для SELECT -тбл(БД), Update-int
            System.out.println("Таблица users создана");
        } catch (SQLException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }


    public void addUser(User user) {
        String sql = "INSERT INTO users (first_name, last_name, age) VALUES (?, ?, ?)";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
//подг.запрос, перед.запрос с плейсх. просим бд вернуть сген. id
            pstmt.setString(1, user.getFirstName());
            pstmt.setString(2, user.getLastName());
            pstmt.setInt(3, user.getAge());
            pstmt.executeUpdate();
//получ. сген id
            ResultSet rs = pstmt.getGeneratedKeys();// запраш у бд сген кл(мал.бд)
            //получаем rs-таблицу ---- (SELECT!, НО возвращаем ключ-поэтому rs)
            if (rs.next()) {//палец-указатель
                user.setId(rs.getInt(1));// получаем сген ключ
            }
            rs.close();

            System.out.println("Пользователь добавлен");
        } catch (SQLException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }


    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users";

        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {// t/f----select запрос

            while (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setFirstName(rs.getString("first_name"));
                user.setLastName(rs.getString("last_name"));
                user.setAge(rs.getInt("age"));
                users.add(user);
            }

        } catch (SQLException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
        return users;
    }


    public User getUserById(int id) {
        String sql = "SELECT * FROM users WHERE id = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();//select запрос

            if (rs.next()) {//ид уник., не while
                User user = new User();//созд. пользов. из рез-та
                user.setId(rs.getInt("id"));
                user.setFirstName(rs.getString("first_name"));
                user.setLastName(rs.getString("last_name"));
                user.setAge(rs.getInt("age"));
                rs.close();
                return user;
            }
            rs.close();

        } catch (SQLException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
        return null;
    }


    public void updateUser(User user) {
        String sql = "UPDATE users SET first_name=?, last_name=?, age=? WHERE id=?";
        // postgresql!! можно так:
        // UPDATE users
        //SET (first_name, last_name, age) = (?, ?, ?)
        //WHERE id = ?;

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, user.getFirstName());
            pstmt.setString(2, user.getLastName());
            pstmt.setInt(3, user.getAge());
            pstmt.setInt(4, user.getId());
            pstmt.executeUpdate();

            System.out.println("Пользователь обновлен");
        } catch (SQLException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }


    public void deleteUserById(int id) {
        String sql = "DELETE FROM users WHERE id = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            System.out.println("Пользователь удален");

        } catch (SQLException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }


    public void deleteAllUsers() {
        String sql = "DELETE FROM users";

        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.executeUpdate(sql);
            stmt.execute("ALTER SEQUENCE users_id_seq RESTART WITH 1");//постгрес
            System.out.println("Все пользователи удалены");

        } catch (SQLException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }
}