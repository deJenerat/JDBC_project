package com.example;

import com.example.dao.UserDAO;
import com.example.entity.User;
import com.example.entity.Address;
import java.util.List;
import java.util.Scanner;

public class Main {

    private static UserDAO userDao = new UserDAO();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {


        boolean running = true;
        while (running) {
            showMenu();
            int choice = getIntInput("Выберите операцию: ");

            switch (choice) {
                case 1:
                    createUserWithAddress();
                    break;
                case 2:
                    deleteUserById();
                    break;
                case 3:
                    showAllUsers();
                    break;
                case 4:
                    findUserById();
                    break;
                case 5:
                    updateUser();
                    break;
                case 6:
                    deleteAllUsers();
                    break;
                case 7:
                    findUsersByHouse();
                    break;
                case 0:
                    running = false;
                    System.out.println("До свидания!");
                    break;
                default:
                    System.out.println("Неверный выбор! Попробуйте снова.");
            }
        }
        HibernateUtil.shutdown();
        scanner.close();
    }

    private static void showMenu() {
        System.out.println("\n МЕНЮ ");
        System.out.println("1. Создать пользователя с адресом");
        System.out.println("2. Удалить пользователя по ID");
        System.out.println("3. Показать всех пользователей");
        System.out.println("4. Найти пользователя по ID");
        System.out.println("5. Обновить пользователя");
        System.out.println("6. Удалить всех пользователей");
        System.out.println("7. Найти пользователей по дому");
        System.out.println("0. Выход");
    }

    private static void createUserWithAddress() {

        System.out.print("Введите имя: ");
        String firstName = scanner.nextLine();

        System.out.print("Введите фамилию: ");
        String lastName = scanner.nextLine();

        System.out.print("Введите возраст: ");
        Integer age = getIntInput("");

        System.out.print("Введите город: ");
        String city = scanner.nextLine();

        System.out.print("Введите улицу: ");
        String street = scanner.nextLine();

        System.out.print("Введите номер дома: ");
        String house = scanner.nextLine();

        User user = new User(firstName, lastName, age);
        Address address = new Address(city, street, house);

        userDao.saveUser(user, address);
    }

    private static void deleteUserById() {
        showAllUsers();
        Long id = getLongInput("Введите ID пользователя для удаления: ");
        userDao.deleteUser(id);
    }

    private static void showAllUsers() {
        List<User> users = userDao.getAllUsers();
        if (users.isEmpty()) {
            System.out.println("Нет пользователей в базе данных.");
        } else {
            for (User user : users) {
                System.out.println(user);
            }
        }
    }

    private static void findUserById() {
        Long id = getLongInput("Введите ID пользователя: ");
        User user = userDao.getUserById(id);
        if (user != null) {
            System.out.println(user);
        } else {
            System.out.println("Пользователь с ID " + id + " не найден.");
        }
    }

    private static void updateUser() {
        showAllUsers();
        Long id = getLongInput("Введите ID пользователя для обновления: ");

        User user = userDao.getUserById(id);
        if (user != null) {
            System.out.println("Текущие данные: " + user);
            System.out.print("Введите новое имя: ");
            String firstName = scanner.nextLine();
            if (firstName.isEmpty()) firstName = user.getFirstName();

            System.out.print("Введите новую фамилию: ");
            String lastName = scanner.nextLine();
            if (lastName.isEmpty()) lastName = user.getLastName();

            System.out.print("Введите новый возраст: ");
            String ageStr = scanner.nextLine();
            Integer age = user.getAge();
            if (!ageStr.isEmpty()) age = Integer.parseInt(ageStr);

            userDao.updateUser(id, firstName, lastName, age);
        } else {
            System.out.println("Пользователь не найден!");
        }
    }

    private static void deleteAllUsers() {
            userDao.deleteAllUsers();
    }

    private static void findUsersByHouse() {
        System.out.print("Введите номер дома: ");
        String house = scanner.nextLine();
        List<User> users = userDao.getUsersByHouse(house);

        if (users.isEmpty()) {
            System.out.println("Нет пользователей, живущих в доме " + house);
        } else {
            System.out.println("Пользователи, живущие в доме " + house + ":");
            for (User user : users) {
                System.out.println(user);
            }
        }
    }

    private static int getIntInput(String message) {
        while (true) {
            try {
                System.out.print(message);
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Введите ЧИСЛО");
            }
        }
    }

    private static Long getLongInput(String message) {
        while (true) {
            try {
                System.out.print(message);
                return Long.parseLong(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Введите ЧИСЛО");
            }
        }
    }
}