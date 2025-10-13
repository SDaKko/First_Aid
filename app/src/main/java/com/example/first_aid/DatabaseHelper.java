package com.example.first_aid;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "FirstAidApp.db";
    private static final int DATABASE_VERSION = 1;

    // Таблица пользователей
    private static final String TABLE_USERS = "users";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_LOGIN = "login";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_POSITION = "position";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_LOGIN + " TEXT UNIQUE,"
                + COLUMN_PASSWORD + " TEXT,"
                + COLUMN_POSITION + " TEXT" + ")";
        db.execSQL(CREATE_USERS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    // Добавление нового пользователя
    public boolean addUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_LOGIN, user.getLogin());
        values.put(COLUMN_PASSWORD, user.getPassword());
        values.put(COLUMN_POSITION, user.getPosition());

        long result = db.insert(TABLE_USERS, null, values);
        db.close();
        return result != -1;
    }

    // Проверка существования пользователя
    public boolean checkUser(String login, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COLUMN_ID};
        String selection = COLUMN_LOGIN + " = ? AND " + COLUMN_PASSWORD + " = ?";
        String[] selectionArgs = {login, password}; //Вставляются вместо вопросов в selection

        Cursor cursor = db.query(TABLE_USERS, columns, selection, selectionArgs,
                null, null, null);
        int count = cursor.getCount();
        cursor.close();
        db.close();

        return count > 0;
    }

    // Получение пользователя по логину
    public User getUser(String login) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS,
                new String[]{COLUMN_ID, COLUMN_LOGIN, COLUMN_PASSWORD, COLUMN_POSITION},
                COLUMN_LOGIN + " = ?", new String[]{login},
                null, null, null);

        User user = null;
        if (cursor != null && cursor.moveToFirst()) {
            user = new User();
            user.setId(cursor.getInt(0));
            user.setLogin(cursor.getString(1));
            user.setPassword(cursor.getString(2));
            user.setPosition(cursor.getString(3));
            cursor.close();
        }
        db.close();
        return user;
    }

    // Проверка существования логина
    public boolean isLoginExists(String login) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS,
                new String[]{COLUMN_ID},
                COLUMN_LOGIN + " = ?", new String[]{login},
                null, null, null);
        boolean exists = (cursor.getCount() > 0);
        cursor.close();
        db.close();
        return exists;
    }


    // ПОЛУЧЕНИЕ ВСЕХ ПОЛЬЗОВАТЕЛЕЙ (для админ-панели)
    public List<User> getAllUsers() {
        List<User> userList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS,
                new String[]{COLUMN_ID, COLUMN_LOGIN, COLUMN_PASSWORD, COLUMN_POSITION},
                null, null, null, null, COLUMN_LOGIN + " ASC");

        if (cursor != null && cursor.moveToFirst()) {
            do {
                User user = new User();
                user.setId(cursor.getInt(0));
                user.setLogin(cursor.getString(1));
                user.setPassword(cursor.getString(2));
                user.setPosition(cursor.getString(3));
                userList.add(user);
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return userList;
    }

    // УДАЛЕНИЕ ПОЛЬЗОВАТЕЛЯ ПО ЛОГИНУ
    public boolean deleteUser(String login) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_USERS, COLUMN_LOGIN + " = ?", new String[]{login});
        db.close();
        return result > 0;
    }

    // ОБНОВЛЕНИЕ ПОЛЬЗОВАТЕЛЯ
    public boolean updateUser(String oldLogin, User updatedUser) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_LOGIN, updatedUser.getLogin());
        values.put(COLUMN_PASSWORD, updatedUser.getPassword());
        values.put(COLUMN_POSITION, updatedUser.getPosition());

        int result = db.update(TABLE_USERS, values, COLUMN_LOGIN + " = ?", new String[]{oldLogin});
        db.close();
        return result > 0;
    }

    // ПРОВЕРКА ЯВЛЯЕТСЯ ЛИ ПОЛЬЗОВАТЕЛЬ АДМИНОМ
    public boolean isAdmin(String login) {
        // Админ имеет специальный логин/пароль
        return "admin".equals(login);
    }
}