package com.example.first_aid;

public class User {
    private int id;
    private String login;
    private String password;
    private String position;

    public User() {}

    public User(String login, String password, String position) {
        this.login = login;
        this.password = password;
        this.position = position;
    }

    // Геттеры и сеттеры
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }
}