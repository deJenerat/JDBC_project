package com.example.jdbc;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AddressDao {       //data access object  паттерн


    // Создание таблицы адресов и связи
    public void createTable() {
        // Создаем таблицу адресов
        String sqlAddress = "CREATE TABLE IF NOT EXISTS addresses (" +
                "id SERIAL PRIMARY KEY, " +
                "city VARCHAR(100), " +
                "street VARCHAR(100), " +
                "house VARCHAR(20))";//25а,25/3


        String sqlAlter = "ALTER TABLE users ADD COLUMN IF NOT EXISTS address_id INTEGER";
//изм .добавляя колонку(alter)
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute(sqlAddress);
            stmt.execute(sqlAlter);
            System.out.println("Таблица addresses создана и связана");

        } catch (SQLException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }


    public void addAddress(Address address) {
        String sql = "INSERT INTO addresses (city, street, house) VALUES (?, ?, ?)";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, address.getCity());
            pstmt.setString(2, address.getStreet());
            pstmt.setString(3, address.getHouse());
            pstmt.executeUpdate();

            ResultSet rs = pstmt.getGeneratedKeys();//запрос у бд сген.ключа
            if (rs.next()) {//указатель
                address.setId(rs.getInt(1));//id сохраняется в о.
            }//закрепление id за адресом
            rs.close();

            System.out.println("Адрес добавлен");
        } catch (SQLException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    public void linkAddressToUser(int userId, int addressId) {
        String sql = "UPDATE users SET address_id = ? WHERE id = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, addressId);
            pstmt.setInt(2, userId);
            pstmt.executeUpdate();

            System.out.println("Адрес привязан к пользователю");
        } catch (SQLException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    // Удалить пользователя с адресом
    // В AddressDao
    public void deleteUserWithAddress(int userId) {
        Connection conn = null;
        try {
            conn = Database.getConnection();
            conn.setAutoCommit(false);  // НАЧИНАЕМ ТРАНЗАКЦИЮ, автокомита нет для отката

            //  Узнаем, какой адрес у пользователя
            int addressId = -1;//0 нельзя
            String selectSql = "SELECT address_id FROM users WHERE id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(selectSql)) {
                pstmt.setInt(1, userId);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    addressId = rs.getInt("address_id");
                    // Проверяем, был ли адрес
                    if (rs.wasNull()) {//если getInt то если поль. нет вернет 0 - шляпа
                        addressId = -1;  // Нет адреса
                    }
                } else {
                    System.out.println("Пользователь с ID " + userId + " не найден");
                    conn.rollback();//чет пошло не так-откат
                    return;
                }
                rs.close();
            }

            //  Если у пользователя был адрес, сколько еще жильцов
            int remainingResidents = 0;
            boolean hadAddress = (addressId > 0);

            if (hadAddress) {
                String countSql = "SELECT COUNT(*) FROM users WHERE address_id = ? AND id != ?";
                try (PreparedStatement pstmt = conn.prepareStatement(countSql)) {
                    pstmt.setInt(1, addressId);
                    pstmt.setInt(2, userId);  // Исключаем текущего пользователя
                    ResultSet rs = pstmt.executeQuery();//SELECT и возвр бд count-только 1 строка
                    if (rs.next()) {
                        remainingResidents = rs.getInt(1);//сколько пользователей по адресу
                    }
                    rs.close();
                }
            }

            // Удаляем пользователя
            String deleteUserSql = "DELETE FROM users WHERE id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(deleteUserSql)) {
                pstmt.setInt(1, userId);
                pstmt.executeUpdate();
            }

            //  Если у пользователя был адрес и он был последний - удаляем адрес
            if (hadAddress && remainingResidents == 0) {
                String deleteAddressSql = "DELETE FROM addresses WHERE id = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(deleteAddressSql)) {
                    pstmt.setInt(1, addressId);
                    pstmt.executeUpdate();
                    System.out.println("Адрес с ID " + addressId + " удален (пользователь был последним)");
                }
            } else if (hadAddress) {
                System.out.println("Осталось жильцов по адресу: " + remainingResidents);
            }

            conn.commit();  // ПОДТВЕРЖДАЕМ ВСЕ ИЗМЕНЕНИЯ
            System.out.println("Пользователь с ID " + userId + " удален");

        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ee) {
                ee.printStackTrace();
            }
            System.out.println("Ошибка при удалении: " + e.getMessage());
        } finally {
            try {
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Найти пользователей по адресу
    public List<User> getUsersByAddress(String city, String street, String house) {
        List<User> users = new ArrayList<>();
        String sql = "SELECT u.id, u.first_name, u.last_name, u.age " +
                "FROM users u " +//AS u
                "JOIN addresses a ON u.address_id = a.id " +//по условию //AS a
                "WHERE a.city = ? AND a.street = ? AND a.house = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, city);
            pstmt.setString(2, street);
            pstmt.setString(3, house);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setFirstName(rs.getString("first_name"));
                user.setLastName(rs.getString("last_name"));
                user.setAge(rs.getInt("age"));
                users.add(user);
            }
            rs.close();

        } catch (SQLException e) {
            System.out.println("Ошибка при поиске по адресу: " + e.getMessage());
        }

        return users;
    }
}