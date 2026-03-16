package com.example.jdbc;

import java.util.List;
import java.util.Scanner;

public class Main {

    private static UserDao userDao = new UserDao();
    private static AddressDao addressDao = new AddressDao();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {

        // Создаем таблицы
        userDao.createTable();
        addressDao.createTable();

        User user1 = new User("Пес", "Патрон", 25);
        User user2 = new User("Джимми", "Нейтрон", 30);
        User user3 = new User("Валя", "Карнавал", 28);
        User user4 = new User("Анна", "Вротердам", 35);

        userDao.addUser(user1);
        userDao.addUser(user2);
        userDao.addUser(user3);
        userDao.addUser(user4);


        Address addr1 = new Address("Москва", "Ленина", "10");
        Address addr2 = new Address("Москва", "Ленина", "10");  
        Address addr3 = new Address("Москва", "Тверская", "5");
        Address addr4 = new Address("Санкт-Петербург", "Невский", "20");

        addressDao.addAddress(addr1);
        addressDao.addAddress(addr2);
        addressDao.addAddress(addr3);
        addressDao.addAddress(addr4);

        // Привязываем адреса
        addressDao.linkAddressToUser(1, addr1.getId());
        addressDao.linkAddressToUser(2, addr2.getId());
        addressDao.linkAddressToUser(3, addr3.getId());
        addressDao.linkAddressToUser(4, addr4.getId());


        while (true) {
            System.out.println("\nВЫБЕРИТЕ ДЕЙСТВИЕ:");
            System.out.println("1 - Добавить пользователя");
            System.out.println("2 - Показать всех пользователей");
            System.out.println("3 - Найти пользователя по ID");
            System.out.println("4 - Обновить пользователя");
            System.out.println("5 - Удалить пользователя по ID");
            System.out.println("6 - Удалить всех пользователей");
            System.out.println("7 - Обновить адрес пользователя по ID");
            System.out.println("8 - Удалить пользователя с адресом");
            System.out.println("9 - Найти пользователей по дому");
            System.out.println("0 - Выход");
            System.out.print("Ваш выбор: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1: addUserWithAddress(); break;
                case 2: showAllUsers(); break;
                case 3: findUserById(); break;
                case 4: updateUser(); break;
                case 5: deleteUserById(); break;
                case 6: deleteAllUsers(); break;
                case 7: addAddress(); break;
                case 8: deleteUserWithAddress(); break;
                case 9: findUsersByAddress(); break;
                case 0: System.out.println("До свидания!"); return;
                default: System.out.println("Неверный выбор");
            }
        }
    }

    private static void addUserWithAddress() {

        System.out.print("Имя: ");
        String firstName = scanner.nextLine();
        System.out.print("Фамилия: ");
        String lastName = scanner.nextLine();
        System.out.print("Возраст: ");
        int age = scanner.nextInt();
        scanner.nextLine();

        System.out.println("\n--- ВВЕДИТЕ АДРЕС ---");
        System.out.print("Город: ");
        String city = scanner.nextLine();
        System.out.print("Улица: ");
        String street = scanner.nextLine();
        System.out.print("Дом: ");
        String house = scanner.nextLine();

        // Создаем пользователя
        User user = new User(firstName, lastName, age);
        userDao.addUser(user);
        System.out.println("Пользователь добавлен с ID: " + user.getId());

        // Создаем адрес
        Address address = new Address(city, street, house);
        addressDao.addAddress(address);
        System.out.println("Адрес добавлен с ID: " + address.getId());

        // Привязываем адрес к пользователю
        addressDao.linkAddressToUser(user.getId(), address.getId());

        System.out.println("\n ПОЛЬЗОВАТЕЛЬ С АДРЕСОМ ДОБАВЛЕН");
        System.out.println("   " + user.getFirstName() + " " + user.getLastName() +
                " - " + address.getCity() + ", " + address.getStreet() + ", " + address.getHouse());
    }

    private static void showAllUsers() {
        List<User> users = userDao.getAllUsers();
        if (users.isEmpty()) {
            System.out.println("Пользователей нет");
        } else {
            System.out.println("\nСПИСОК ПОЛЬЗОВАТЕЛЕЙ:");
            for (User user : users) {
                System.out.println(user);
            }
        }
    }

    private static void findUserById() {
        System.out.print("Введите ID: ");
        int id = scanner.nextInt();
        scanner.nextLine();

        User user = userDao.getUserById(id);
        if (user == null) {
            System.out.println("Пользователь не найден");
        } else {
            System.out.println("Найден: " + user);
        }
    }

    private static void updateUser() {
        System.out.print("ID пользователя для обновления: ");
        int id = scanner.nextInt();
        scanner.nextLine();

        User user = userDao.getUserById(id);
        if (user == null) {
            System.out.println("Пользователь не найден");
            return;
        }

        System.out.print("Новое имя " + user.getFirstName() + ": ");
        String firstName = scanner.nextLine();
        if (!firstName.isEmpty()) user.setFirstName(firstName);//если ничего не ввели-оставить

        System.out.print("Новая фамилия " + user.getLastName() + ": ");
        String lastName = scanner.nextLine();
        if (!lastName.isEmpty()) user.setLastName(lastName);

        System.out.print("Новый возраст " + user.getAge() + ": ");
        String ageStr = scanner.nextLine();
        if (!ageStr.isEmpty()) user.setAge(Integer.parseInt(ageStr));

        userDao.updateUser(user);
    }

    private static void deleteUserById() {
        System.out.print("ID пользователя для удаления: ");
        int id = scanner.nextInt();
        scanner.nextLine();
        userDao.deleteUserById(id);
    }

    private static void deleteAllUsers() {
        System.out.print("Точно удалить всех? (да/нет): ");
        String confirm = scanner.nextLine();
        if (confirm.equals("да")) {
            userDao.deleteAllUsers();
        }
    }

    private static void addAddress() {
        System.out.print("ID пользователя: ");
        int userId = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Город: ");
        String city = scanner.nextLine();
        System.out.print("Улица: ");
        String street = scanner.nextLine();
        System.out.print("Дом: ");
        String house = scanner.nextLine();

        Address address = new Address(city, street, house);
        addressDao.addAddress(address);
        addressDao.linkAddressToUser(userId, address.getId());
    }

    private static void deleteUserWithAddress() {
        System.out.print("ID пользователя для удаления : ");
        int id = scanner.nextInt();
        scanner.nextLine();
        addressDao.deleteUserWithAddress(id);  // Вызываем метод из AddressDao
    }

    private static void findUsersByAddress() {
        System.out.print("Введите город: ");
        String city = scanner.nextLine();
        System.out.print("Введите улицу: ");
        String street = scanner.nextLine();
        System.out.print("Введите дом: ");
        String house = scanner.nextLine();

        List<User> users = addressDao.getUsersByAddress(city, street, house);

        if (users.isEmpty()) {
            System.out.println("По адресу " + city + ", " + street + ", " + house + " никто не живет");
        } else {
            System.out.println("Жильцы по адресу " + city + ", " + street + ", " + house + ":");
            for (User user : users) {
                System.out.println("  " + user.getFirstName() + " " + user.getLastName() + " (ID: " + user.getId() + ")");
            }
            System.out.println("Всего: " + users.size() + " жильцов");
        }
    }
}