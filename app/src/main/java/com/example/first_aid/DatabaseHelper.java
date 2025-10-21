package com.example.first_aid;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "FirstAidApp.db";
    private static final int DATABASE_VERSION = 2;
    private final Context context;
    private String DB_PATH;

    // Названия таблиц и колонок
    private static final String TABLE_POSITIONS = "positions";
    private static final String TABLE_USERS = "users";
    private static final String TABLE_INCIDENTS = "incidents";
    private static final String TABLE_POSITION_INCIDENTS = "position_incidents";

    private static final String COLUMN_POSITION_ID = "id";
    private static final String COLUMN_POSITION_NAME = "position_name";
    private static final String COLUMN_USER_ID = "id";
    private static final String COLUMN_LOGIN = "login";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_POSITION_ID_REF = "position_id";
    private static final String COLUMN_INCIDENT_ID = "incident_id";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_DESCRIPTION = "description";
    private static final String COLUMN_IMAGE_URL = "image_url";
    private static final String COLUMN_CATEGORY = "category";
    private static final String COLUMN_PI_POSITION_ID = "position_id";
    private static final String COLUMN_PI_INCIDENT_REF = "incident_ref";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
        DB_PATH = context.getDatabasePath(DATABASE_NAME).getPath();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Создаем таблицы через код
        createTables(db);
        // Заполняем начальными данными
        addInitialData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Удаляем старые таблицы и создаем новые
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_POSITION_INCIDENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_INCIDENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_POSITIONS);
        onCreate(db);
    }

    // === МЕТОДЫ СОЗДАНИЯ ТАБЛИЦ ===

    private void createTables(SQLiteDatabase db) {
        createPositionsTable(db);
        createUsersTable(db);
        createIncidentsTable(db);
        createPositionIncidentsTable(db);
    }

    private void createPositionsTable(SQLiteDatabase db) {
        String CREATE_POSITIONS_TABLE = "CREATE TABLE " + TABLE_POSITIONS + "("
                + COLUMN_POSITION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_POSITION_NAME + " TEXT UNIQUE NOT NULL)";
        db.execSQL(CREATE_POSITIONS_TABLE);
    }

    private void createUsersTable(SQLiteDatabase db) {
        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + "("
                + COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_LOGIN + " TEXT UNIQUE NOT NULL,"
                + COLUMN_PASSWORD + " TEXT NOT NULL,"
                + COLUMN_POSITION_ID_REF + " INTEGER NOT NULL,"
                + "FOREIGN KEY(" + COLUMN_POSITION_ID_REF + ") REFERENCES " + TABLE_POSITIONS + "(" + COLUMN_POSITION_ID + "))";
        db.execSQL(CREATE_USERS_TABLE);
    }

    private void createIncidentsTable(SQLiteDatabase db) {
        String CREATE_INCIDENTS_TABLE = "CREATE TABLE " + TABLE_INCIDENTS + "("
                + COLUMN_INCIDENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_TITLE + " TEXT NOT NULL,"
                + COLUMN_DESCRIPTION + " TEXT NOT NULL,"
                + COLUMN_IMAGE_URL + " TEXT,"
                + COLUMN_CATEGORY + " TEXT NOT NULL)";
        db.execSQL(CREATE_INCIDENTS_TABLE);
    }

    private void createPositionIncidentsTable(SQLiteDatabase db) {
        String CREATE_POSITION_INCIDENTS_TABLE = "CREATE TABLE " + TABLE_POSITION_INCIDENTS + "("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_PI_POSITION_ID + " INTEGER NOT NULL,"
                + COLUMN_PI_INCIDENT_REF + " INTEGER NOT NULL,"
                + "FOREIGN KEY(" + COLUMN_PI_POSITION_ID + ") REFERENCES " + TABLE_POSITIONS + "(" + COLUMN_POSITION_ID + "),"
                + "FOREIGN KEY(" + COLUMN_PI_INCIDENT_REF + ") REFERENCES " + TABLE_INCIDENTS + "(" + COLUMN_INCIDENT_ID + "))";
        db.execSQL(CREATE_POSITION_INCIDENTS_TABLE);
    }

    // === МЕТОДЫ ДОБАВЛЕНИЯ НАЧАЛЬНЫХ ДАННЫХ ===

    private void addInitialData(SQLiteDatabase db) {
        addInitialPositions(db);
        addInitialUsers(db);
        addInitialIncidents(db);
        addPositionIncidentsRelations(db);
    }

    private void addInitialPositions(SQLiteDatabase db) {
        String[] positions = {"Преподаватель", "Врач", "Водитель", "Мастер ПК", "Универсальный"};

        for (int i = 0; i < positions.length; i++) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_POSITION_ID, i + 1); // Явно указываем ID
            values.put(COLUMN_POSITION_NAME, positions[i]);
            db.insert(TABLE_POSITIONS, null, values);
        }
    }

    private void addInitialUsers(SQLiteDatabase db) {
        String[][] users = {
                {"admin", "admin", "5"},
                {"doctor", "doctor", "2"},
                {"driver", "driver", "3"},
                {"master", "master", "4"},
                {"teacher", "teacher", "1"},
                {"universal", "universal", "5"}
        };

        for (String[] user : users) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_LOGIN, user[0]);
            values.put(COLUMN_PASSWORD, user[1]);
            values.put(COLUMN_POSITION_ID_REF, Integer.parseInt(user[2]));
            db.insert(TABLE_USERS, null, values);
        }
    }

    private void addInitialIncidents(SQLiteDatabase db) {
        String[][] incidents = {
                // incident_id, title, description, image_url, category
                {"1", "Порез",
                        "1. Промойте рану чистой водой с мылом\n2. Остановите кровотечение прямым давлением\n3. Наложите стерильную повязку\n4. При глубоких порезах обратитесь к врачу",
                        "cut.jpg", "medical"},

                {"2", "Ожог",
                        "1. Охладите обожженное место проточной водой 15-20 минут\n2. Накройте стерильной повязкой\n3. Не используйте масло, лед или вату\n4. При серьезных ожогах вызовите скорую",
                        "burn.jpg", "medical"},

                {"3", "Перелом",
                        "1. Обеспечьте неподвижность поврежденной конечности\n2. Наложите шину из подручных материалов\n3. Приложите холод для уменьшения отека\n4. Вызовите скорую помощь",
                        "fracture.jpg", "medical"},

                {"4", "Отравление",
                        "1. Вызовите скорую помощь\n2. Не вызывайте рвоту, если человек без сознания\n3. Сохраните образец отравляющего вещества\n4. Обеспечьте доступ свежего воздуха",
                        "poisoning.jpg", "medical"},

                {"5", "ДТП",
                        "1. Обеспечьте безопасность места происшествия\n2. Вызовите скорую и полицию\n3. Окажите первую помощь пострадавшим\n4. Не перемещайте тяжелораненых",
                        "accident.jpg", "transport"},

                {"6", "Поражение электрическим током",
                        "1. Обесточьте источник поражения\n2. Не прикасайтесь к пострадавшему голыми руками\n3. Проверьте дыхание и пульс\n4. При необходимости начните сердечно-легочную реанимацию",
                        "electric_shock.jpg", "technical"},

                {"7", "Пожар",
                        "1. Немедленно покиньте помещение\n2. Вызовите пожарных по телефону 101\n3. При задымлении двигайтесь ползком\n4. Не используйте лифт",
                        "fire.jpg", "universal"},

                {"8", "Утопление",
                        "1. Извлеките пострадавшего из воды\n2. Проверьте дыхание и пульс\n3. При отсутствии дыхания начните искусственное дыхание\n4. Вызовите скорую помощь",
                        "drowning.jpg", "universal"},

                {"9", "Сердечный приступ",
                        "1. Вызовите скорую помощь\n2. Усадите или уложите пострадавшего\n3. Расстегните тесную одежду\n4. При остановке сердца начните непрямой массаж",
                        "heart_attack.jpg", "medical"},

                {"10", "Обморок",
                        "1. Уложите пострадавшего на спину\n2. Приподнимите ноги выше уровня головы\n3. Обеспечьте доступ свежего воздуха\n4. При длительном обмороке вызовите скорую",
                        "fainting.jpg", "medical"},

                {"11", "Ученик упал в обморок",
                        "1. Уложите ученика, приподнимите ноги\n2. Вызовите школьного врача\n3. Обеспечьте доступ свежего воздуха\n4. Сообщите родителям и администрации",
                        "student_faint.jpg", "education"}
        };

        for (String[] incident : incidents) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_INCIDENT_ID, Integer.parseInt(incident[0]));
            values.put(COLUMN_TITLE, incident[1]);
            values.put(COLUMN_DESCRIPTION, incident[2]);
            values.put(COLUMN_IMAGE_URL, incident[3]);
            values.put(COLUMN_CATEGORY, incident[4]);
            db.insert(TABLE_INCIDENTS, null, values);
        }
    }

    private void addPositionIncidentsRelations(SQLiteDatabase db) {
        int[][] relations = {
                // Преподаватель (id: 1)
                {1, 1}, {1, 2}, {1, 3}, {1, 4}, {1, 7}, {1, 8}, {1, 9}, {1, 10}, {1, 11},
                // Врач (id: 2)
                {2, 1}, {2, 2}, {2, 3}, {2, 4}, {2, 9}, {2, 10}, {2, 8},
                // Водитель (id: 3)
                {3, 1}, {3, 2}, {3, 3}, {3, 5}, {3, 7}, {3, 9}, {3, 10},
                // Мастер ПК (id: 4)
                {4, 1}, {4, 2}, {4, 6}, {4, 7},
                // Универсальный (id: 5)
                {5, 1}, {5, 2}, {5, 3}, {5, 4}, {5, 5}, {5, 6}, {5, 7}, {5, 8}, {5, 9}, {5, 10}, {5, 11}
        };

        for (int[] relation : relations) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_PI_POSITION_ID, relation[0]);
            values.put(COLUMN_PI_INCIDENT_REF, relation[1]);
            db.insert(TABLE_POSITION_INCIDENTS, null, values);
        }
    }

    // === МЕТОДЫ ДЛЯ РАБОТЫ С ПОЛЬЗОВАТЕЛЯМИ ===

    public boolean addUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Получаем ID должности по имени
        int positionId = getPositionIdByName(db, user.getPosition());
        if (positionId == -1) {
            db.close();
            return false;
        }

        ContentValues values = new ContentValues();
        values.put(COLUMN_LOGIN, user.getLogin());
        values.put(COLUMN_PASSWORD, user.getPassword());
        values.put(COLUMN_POSITION_ID_REF, positionId);

        long result = db.insert(TABLE_USERS, null, values);
        db.close();
        return result != -1;
    }

    public boolean checkUser(String login, String password) {
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT u." + COLUMN_USER_ID +
                " FROM " + TABLE_USERS + " u" +
                " WHERE u." + COLUMN_LOGIN + " = ? AND u." + COLUMN_PASSWORD + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{login, password});
        int count = cursor.getCount();
        cursor.close();
        db.close();

        return count > 0;
    }

    public User getUser(String login) {
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT u." + COLUMN_USER_ID + ", u." + COLUMN_LOGIN + ", u." + COLUMN_PASSWORD +
                ", p." + COLUMN_POSITION_NAME +
                " FROM " + TABLE_USERS + " u" +
                " INNER JOIN " + TABLE_POSITIONS + " p ON u." + COLUMN_POSITION_ID_REF + " = p." + COLUMN_POSITION_ID +
                " WHERE u." + COLUMN_LOGIN + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{login});

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

    public boolean isLoginExists(String login) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS,
                new String[]{COLUMN_USER_ID},
                COLUMN_LOGIN + " = ?", new String[]{login},
                null, null, null);
        boolean exists = (cursor.getCount() > 0);
        cursor.close();
        db.close();
        return exists;
    }

    public boolean updateUser(String oldLogin, User updatedUser) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Получаем ID новой должности
        int newPositionId = getPositionIdByName(db, updatedUser.getPosition());
        if (newPositionId == -1) {
            db.close();
            return false;
        }

        ContentValues values = new ContentValues();
        values.put(COLUMN_LOGIN, updatedUser.getLogin());
        values.put(COLUMN_PASSWORD, updatedUser.getPassword());
        values.put(COLUMN_POSITION_ID_REF, newPositionId);

        int result = db.update(TABLE_USERS, values, COLUMN_LOGIN + " = ?", new String[]{oldLogin});
        db.close();
        return result > 0;
    }

    // === МЕТОДЫ ДЛЯ РАБОТЫ С ПРОИСШЕСТВИЯМИ ===

    public List<Incident> getIncidentsForPosition(String positionName) {
        List<Incident> incidentList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // ИСПРАВЛЕННЫЙ ЗАПРОС - добавлены пробелы после JOIN условий
        String query = "SELECT i." + COLUMN_INCIDENT_ID + ", i." + COLUMN_TITLE + ", " +
                "i." + COLUMN_DESCRIPTION + ", i." + COLUMN_IMAGE_URL + ", i." + COLUMN_CATEGORY +
                " FROM " + TABLE_INCIDENTS + " i " +
                "INNER JOIN " + TABLE_POSITION_INCIDENTS + " pi ON i." + COLUMN_INCIDENT_ID + " = pi." + COLUMN_PI_INCIDENT_REF + " " + // ДОБАВЛЕН ПРОБЕЛ
                "INNER JOIN " + TABLE_POSITIONS + " p ON pi." + COLUMN_PI_POSITION_ID + " = p." + COLUMN_POSITION_ID + " " + // ДОБАВЛЕН ПРОБЕЛ
                "WHERE p." + COLUMN_POSITION_NAME + " = ? " +
                "ORDER BY i." + COLUMN_TITLE;

        Log.d("DatabaseHelper", "Executing query: " + query);
        Log.d("DatabaseHelper", "Position name: " + positionName);

        Cursor cursor = db.rawQuery(query, new String[]{positionName});

        if (cursor.moveToFirst()) {
            do {
                Incident incident = new Incident();
                incident.setId(cursor.getInt(0));
                incident.setTitle(cursor.getString(1));
                incident.setDescription(cursor.getString(2));
                incident.setImageUrl(cursor.getString(3));
                incident.setCategory(cursor.getString(4));
                incidentList.add(incident);
            } while (cursor.moveToNext());
        } else {
            Log.d("DatabaseHelper", "No incidents found for position: " + positionName);
        }

        cursor.close();
        db.close();
        return incidentList;
    }

    public Incident getIncidentById(int incidentId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_INCIDENTS,
                new String[]{COLUMN_INCIDENT_ID, COLUMN_TITLE, COLUMN_DESCRIPTION, COLUMN_IMAGE_URL, COLUMN_CATEGORY},
                COLUMN_INCIDENT_ID + " = ?", new String[]{String.valueOf(incidentId)},
                null, null, null);

        Incident incident = null;
        if (cursor != null && cursor.moveToFirst()) {
            incident = new Incident();
            incident.setId(cursor.getInt(0));
            incident.setTitle(cursor.getString(1));
            incident.setDescription(cursor.getString(2));
            incident.setImageUrl(cursor.getString(3));
            incident.setCategory(cursor.getString(4));
            cursor.close();
        }
        db.close();
        return incident;
    }

    // === ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ ===

    private int getPositionIdByName(SQLiteDatabase db, String positionName) {
        Cursor cursor = db.query(TABLE_POSITIONS,
                new String[]{COLUMN_POSITION_ID},
                COLUMN_POSITION_NAME + " = ?",
                new String[]{positionName},
                null, null, null);

        int positionId = -1;
        if (cursor != null && cursor.moveToFirst()) {
            positionId = cursor.getInt(0);
            cursor.close();
        }
        return positionId;
    }

    public List<String> getAllPositions() {
        List<String> positions = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_POSITIONS,
                new String[]{COLUMN_POSITION_NAME},
                null, null, null, null, COLUMN_POSITION_NAME + " ASC");

        if (cursor.moveToFirst()) {
            do {
                positions.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return positions;
    }

    // Получение всех пользователей
    public List<User> getAllUsers() {
        List<User> userList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT u." + COLUMN_USER_ID + ", u." + COLUMN_LOGIN + ", u." + COLUMN_PASSWORD +
                ", p." + COLUMN_POSITION_NAME +
                " FROM " + TABLE_USERS + " u" +
                " INNER JOIN " + TABLE_POSITIONS + " p ON u." + COLUMN_POSITION_ID_REF + " = p." + COLUMN_POSITION_ID +
                " ORDER BY u." + COLUMN_LOGIN + " ASC";

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                User user = new User();
                user.setId(cursor.getInt(0));
                user.setLogin(cursor.getString(1));
                user.setPassword(cursor.getString(2));
                user.setPosition(cursor.getString(3));
                userList.add(user);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return userList;
    }

    // Удаление пользователя по логину
    public boolean deleteUser(String login) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Не позволяем удалить администратора
        if ("admin".equals(login)) {
            db.close();
            return false;
        }

        int result = db.delete(TABLE_USERS, COLUMN_LOGIN + " = ?", new String[]{login});
        db.close();
        return result > 0;
    }

    // Получение пользователя по ID
    public User getUserById(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT u." + COLUMN_USER_ID + ", u." + COLUMN_LOGIN + ", u." + COLUMN_PASSWORD +
                ", p." + COLUMN_POSITION_NAME +
                " FROM " + TABLE_USERS + " u" +
                " INNER JOIN " + TABLE_POSITIONS + " p ON u." + COLUMN_POSITION_ID_REF + " = p." + COLUMN_POSITION_ID +
                " WHERE u." + COLUMN_USER_ID + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

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

    // Обновление пользователя по ID
    public boolean updateUserById(int userId, User updatedUser) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Получаем ID новой должности
        int newPositionId = getPositionIdByName(db, updatedUser.getPosition());
        if (newPositionId == -1) {
            db.close();
            return false;
        }

        ContentValues values = new ContentValues();
        values.put(COLUMN_LOGIN, updatedUser.getLogin());
        values.put(COLUMN_PASSWORD, updatedUser.getPassword());
        values.put(COLUMN_POSITION_ID_REF, newPositionId);

        int result = db.update(TABLE_USERS, values, COLUMN_USER_ID + " = ?", new String[]{String.valueOf(userId)});
        db.close();
        return result > 0;
    }

    // Проверка является ли пользователь админом
    public boolean isAdmin(String login) {
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT " + COLUMN_USER_ID +
                " FROM " + TABLE_USERS +
                " WHERE " + COLUMN_LOGIN + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{login});
        boolean isAdmin = cursor.getCount() > 0;
        cursor.close();
        db.close();

        return isAdmin && "admin".equals(login);
    }
}