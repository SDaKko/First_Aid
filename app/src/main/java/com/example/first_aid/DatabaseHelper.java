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
    private static final int DATABASE_VERSION = 3;
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
                {"1", "Порез с артериальным кровотечением",
                        "КРИТИЧЕСКАЯ СИТУАЦИЯ: Обнаружено артериальное кровотечение с пульсирующей струей крови алого цвета.\n\n1. НЕМЕДЛЕННО вызовите скорую помощь по номеру 103\n2. Примените прямое давление на рану стерильной марлевой салфеткой\n3. Наложите жгут выше раны при продолжающемся кровотечении\n4. Зафиксируйте время наложения жгута\n5. Приподнимите поврежденную конечность\n6. Контролируйте сознание и пульс пострадавшего\n7. Подготовьте документы и историю болезни для передачи медикам",
                        "arterial_bleeding.jpg", "medical"},

                {"2", "Термический ожог III степени",
                        "ТЯЖЕЛАЯ ТРАВМА: Глубокий ожог с поражением всех слоев кожи, возможно обугливание тканей.\n\n1. Вызовите реанимационную бригаду скорой помощи\n2. НЕ ОТРЫВАЙТЕ прилипшую одежду от ожоговой поверхности\n3. Накройте ожог стерильной неплотной повязкой\n4. Исключите применение мазей, кремов и народных средств\n5. Контролируйте дыхание и сознание пострадавшего\n6. При обширных ожогах (>15% тела) - риск ожогового шока\n7. Подготовьте информацию о возможных аллергиях и хронических заболеваниях",
                        "severe_burn.jpg", "medical"},

                {"3", "Открытый перелом бедра с смещением",
                        "ТЯЖЕЛАЯ ТРАВМА: Нарушение целостности бедренной кости с повреждением кожных покровов.\n\n1. Вызовите специализированную травматологическую бригаду\n2. Обеспечьте полную иммобилизацию конечности шиной Крамера или подручными средствами\n3. Остановите наружное кровотечение давящей повязкой\n4. Накройте рану стерильным материалом\n5. При признаках травматического шока - приподнимите ноги, укройте пострадавшего\n6. ЗАПРЕЩЕНО пытаться вправлять костные отломки\n7. Мониторьте состояние до приезда медиков каждые 5 минут",
                        "open_fracture.jpg", "medical"},

                {"4", "Химическое отравление неизвестным веществом",
                        "ОПАСНАЯ СИТУАЦИЯ: Пострадавший контактировал с химическим веществом неизвестного происхождения.\n\n1. Вызовите токсикологическую бригаду скорой помощи\n2. Определите путь попадания яда (пероральный, ингаляционный, кожный)\n3. Сохраните образец отравляющего вещества для токсикологического анализа\n4. При ингаляционном отравлении - обеспечьте доступ свежего воздуха\n5. При кожном контакте - снимите загрязненную одежду, промойте кожу водой\n6. НЕ ВЫЗЫВАЙТЕ рвоту при отравлении кислотами, щелочами или нефтепродуктами\n7. Подготовьте данные о времени воздействия и количестве вещества",
                        "chemical_poisoning.jpg", "medical"},

                {"5", "Многоэтапное ДТП с массовым поражением",
                        "КАТАСТРОФА: Дорожно-транспортное происшествие с участием нескольких транспортных средств и многочисленными пострадавшими.\n\n1. НЕМЕДЛЕННО вызовите полицию (102), скорую помощь (103) и МЧС (101)\n2. Оцените обстановку на предмет пожароопасности и вторичных угроз\n3. Организуйте ограждение места происшествия\n4. Проведите первичную сортировку пострадавших по системе START\n5. Оказывайте помощь в порядке приоритета: остановка кровотечений, обеспечение проходимости дыхательных путей\n6. Не извлекайте зажатых в транспортных средствах пострадавших без специального оборудования\n7. Организуйте встречу экстренных служб",
                        "mass_accident.jpg", "transport"},

                {"6", "Поражение электрическим током высокого напряжения",
                        "ЧРЕЗВЫЧАЙНО ОПАСНАЯ СИТУАЦИЯ: Поражение электротоком напряжением свыше 1000 В.\n\n1. НЕМЕДЛЕННО обесточьте линию через диспетчера энергослужбы\n2. НЕ ПРИБЛИЖАЙТЕСЬ к пострадавшему ближе 8 метров без снятия напряжения\n3. Вызовите специализированную бригаду скорой помощи и аварийную энергослужбу\n4. После обесточивания оцените состояние пострадавшего\n5. При отсутствии дыхания и пульса - начните сердечно-легочную реанимацию\n6. Обработайте электроожоги стерильными повязками\n7. Иммобилизируйте конечности из-за риска переломов от мышечных сокращений",
                        "high_voltage_shock.jpg", "technical"},

                {"7", "Пожар в многоэтажном здании с задымлением",
                        "ЧРЕЗВЫЧАЙНАЯ СИТУАЦИЯ: Распространение пожара по этажам здания с интенсивным задымлением.\n\n1. НЕМЕДЛЕННО активируйте систему пожарной сигнализации\n2. Вызовите пожарную охрану (101) с указанием количества этажей и людей в здании\n3. Начните эвакуацию согласно плану эвакуации\n4. Используйте средства индивидуальной защиты органов дыхания\n5. Двигайтесь к выходам пригнувшись или ползком (внизу меньше дыма)\n6. ЗАПРЕЩЕНО пользоваться лифтами\n7. Проверьте помещения на наличие отставших при эвакуации",
                        "building_fire.jpg", "universal"},

                {"8", "Утопление в холодной воде с гипотермией",
                        "КОМПЛЕКСНАЯ ТРАВМА: Извлечение пострадавшего из воды температурой ниже +15°C с признаками переохлаждения.\n\n1. Вызовите реанимационную бригаду скорой помощи\n2. Аккуратно извлеките пострадавшего из воды, стабилизируя шейный отдел\n3. Определите наличие дыхания и пульса на сонной артерии\n4. При отсутствии дыхания - начните искусственную вентиляцию легких еще в воде (если возможно)\n5. После извлечения - начните СЛР при отсутствии признаков жизни\n6. Снимите мокрую одежду, укройте термоодеялом\n7. НЕ ДОПУСКАЙТЕ резких движений (риск фибрилляции желудочков при гипотермии)",
                        "cold_water_drowning.jpg", "universal"},

                {"9", "Острый коронарный синдром с остановкой сердца",
                        "КРИТИЧЕСКОЕ СОСТОЯНИЕ: Внезапная остановка кровообращения на фоне острой сердечной недостаточности.\n\n1. НЕМЕДЛЕННО вызовите кардиологическую бригаду скорой помощи\n2. Определите отсутствие сознания, дыхания и пульса на сонной артерии\n3. Немедленно начните сердечно-легочную реанимацию в соотношении 30:2\n4. Используйте автоматический наружный дефибриллятор при наличии\n5. Продолжайте реанимационные мероприятия до прибытия медиков или появления признаков жизни\n6. Обеспечьте проходимость дыхательных путей\n7. Подготовьте информацию о принимаемых лекарствах и ранее перенесенных заболеваниях",
                        "cardiac_arrest.jpg", "medical"},

                {"10", "Анафилактический шок тяжелой степени",
                        "УГРОЖАЮЩЕЕ ЖИЗНИ СОСТОЯНИЕ: Острая аллергическая реакция с нарушением витальных функций.\n\n1. НЕМЕДЛЕННО вызовите реанимационную бригаду\n2. Прекратите поступление аллергена (если известно)\n3. При наличии - введите эпинефрин (адреналин) в мышцу бедра\n4. Уложите пострадавшего с приподнятыми ногами\n5. Обеспечьте проходимость дыхательных путей\n6. Контролируйте уровень сознания, дыхание и пульс\n7. Будьте готовы к проведению СЛР при остановке сердца",
                        "anaphylactic_shock.jpg", "medical"},

                {"11", "Эпилептический статус у ученика",
                        "НЕОТЛОЖНОЕ СОСТОЯНИЕ: Продолжающийся эпилептический припадок длительностью более 5 минут или серия припадков.\n\n1. Вызовите неврологическую бригаду скорой помощи\n2. Уберите опасные предметы вокруг ученика\n3. Подложите что-то мягкое под голову\n4. НЕ УДЕРЖИВАЙТЕ силой и НЕ ВСТАВЛЯЙТЕ предметы в рот\n5. Засеките время начала приступа\n6. После прекращения судорог - придайте устойчивое боковое положение\n7. Сообщите родителям и школьной администрации, подготовьте медицинскую карту",
                        "epileptic_status.jpg", "education"},

                {"12", "Падение с высоты с политравмой",
                        "КРИТИЧЕСКАЯ ТРАВМА: Падение с высоты более 3 метров с множественными повреждениями.\n\n1. Вызовите травматологическую и реанимационную бригады\n2. НЕ ПЕРЕМЕЩАЙТЕ пострадавшего без крайней необходимости\n3. Стабилизируйте шейный отдел позвоночника вручную\n4. Оцените состояние по алгоритму ABC (дыхательные пути, дыхание, кровообращение)\n5. Остановите наружные кровотечения\n6. Накройте пострадавшего для профилактики гипотермии\n7. Мониторьте состояние до прибытия помощи",
                        "fall_from_height.jpg", "universal"},

                {"13", "Техногенная авария с выбросом АХОВ",
                        "ЧРЕЗВЫЧАЙНАЯ СИТУАЦИЯ: Авария на химически опасном объекте с выбросом аварийно химически опасных веществ.\n\n1. НЕМЕДЛЕННО сообщите в МЧС (101) и другие экстренные службы\n2. Определите направление ветра и двигайтесь перпендикулярно ему\n3. Используйте средства индивидуальной защиты\n4. Организуйте эвакуацию из зоны поражения\n5. При попадании вещества на кожу - проведите частичную санитарную обработку\n6. При ингаляционном поражении - обеспечьте доступ свежего воздуха\n7. Действуйте согласно плану гражданской обороны объекта",
                        "chemical_accident.jpg", "technical"},

                {"14", "Критическая поломка серверного оборудования",
                        "ЧРЕЗВЫЧАЙНАЯ СИТУАЦИЯ: Выход из строя критически важного IT-оборудования с остановкой бизнес-процессов.\n\n1. НЕМЕДЛЕННО уведомите руководство и IT-директора\n2. Активируйте план аварийного восстановления\n3. Изолируйте неисправное оборудование от сети\n4. Начните переключение на резервные системы\n5. Документируйте все предпринятые действия\n6. Организуйте взаимодействие с технической поддержкой вендора\n7. Обеспечьте информационную безопасность во время восстановления",
                        "server_crash.jpg", "technical"},

                {"15", "Массовая драка с применением подручных средств",
                        "ОПАСНАЯ СИТУАЦИЯ: Групповое нарушение общественного порядка с физическим насилием.\n\n1. НЕМЕДЛЕННО вызовите полицию (102) и скорую помощь (103)\n2. Не вмешивайтесь физически без наличия специальной подготовки\n3. Попытайтесь дистанционно успокоить конфликтующие стороны\n4. Организуйте эвакуацию посторонних лиц из зоны конфликта\n5. Окажите первую помощь пострадавшим после прекращения насилия\n6. Зафиксируйте участников конфликта на видео/фото для передачи полиции\n7. Составьте подробный рапорт о происшествии",
                        "mass_fight.jpg", "education"},

                {"16", "Острая реакция на стресс у подростка",
                        "ПСИХИАТРИЧЕСКОЕ НЕОТЛОЖНОЕ СОСТОЯНИЕ: Острая психотическая реакция с агрессией или аутоагрессией.\n\n1. Вызовите психиатрическую бригаду скорой помощи\n2. Обеспечьте безопасность подростка и окружающих\n3. Изолируйте от триггерных факторов\n4. Установите вербальный контакт, говорите спокойно и уверенно\n5. Не применяйте физическое воздействие без крайней необходимости\n6. Приготовьте информацию о принимаемых лекарствах и предыдущих диагнозах\n7. Уведомите родителей и школьного психолога",
                        "teenager_crisis.jpg", "education"},

                {"17", "ДТП с опасным грузом",
                        "КАТАСТРОФА ТЕХНОГЕННОГО ХАРАКТЕРА: Дорожно-транспортное происшествие с транспортным средством, перевозящим опасные грузы.\n\n1. НЕМЕДЛЕННО вызовите МЧС (101), полицию (102) и скорую помощь (103)\n2. Определите тип опасного груза по информационной табличке (оранжевый знак)\n3. Эвакуируйтесь на расстояние не менее 800 метров против ветра\n4. Не используйте открытый огонь и электрооборудование\n5. Организуйте оцепление зоны поражения\n6. Действуйте согласно инструкции по опасным грузам\n7. Дождитесь прибытия специализированных аварийных служб",
                        "hazardous_material_accident.jpg", "transport"},

                {"18", "Внезапная родовая деятельность",
                        "НЕОТЛОЖНОЕ МЕДИЦИНСКОЕ СОСТОЯНИЕ: Начало родовой деятельности в непредназначенном для этого месте.\n\n1. Вызовите акушерскую бригаду скорой помощи\n2. Подготовьте чистое место для роженицы\n3. Обеспечьте психологическую поддержку\n4. При начавшихся родах - подготовьтесь к приему новорожденного\n5. После рождения - оботрите ребенка чистой тканью и приложите к груди матери\n6. НЕ ПЕРЕРЕЗЫВАЙТЕ пуповину\n7. Дождитесь прибытия медиков для специализированной помощи",
                        "emergency_childbirth.jpg", "medical"},

                {"19", "Обрушение строительных конструкций",
                        "КАТАСТРОФА: Частичное или полное обрушение элементов здания или сооружения.\n\n1. НЕМЕДЛЕННО вызовите МЧС (101) и скорую помощь (103)\n2. Эвакуируйте людей из опасной зоны\n3. Оцените вероятность дальнейшего обрушения\n4. Не пытайтесь самостоятельно разбирать завалы\n5. Организуйте встречу аварийно-спасательных служб\n6. Предоставьте проектную документацию на сооружение\n7. Составьте список возможных находящихся под завалами",
                        "building_collapse.jpg", "universal"},

                {"20", "Кибератака на информационную инфраструктуру",
                        "ЧРЕЗВЫЧАЙНАЯ СИТУАЦИЯ: Массированная кибератака с шифрованием данных или выводом систем из строя.\n\n1. НЕМЕДЛЕННО отключите пораженные системы от сети\n2. Уведомите руководство и службу информационной безопасности\n3. Активируйте план восстановления после кибератаки\n4. Сохраните логи и доказательства атаки для последующего анализа\n5. Уведомите правоохранительные органы при необходимости\n6. Начните восстановление из чистых бэкапов\n7. Проведите анализ уязвимостей и усильте защиту",
                        "cyber_attack.jpg", "technical"},

                // НОВЫЕ ИНЦИДЕНТЫ
                {"21", "Аллергическая реакция в школьной столовой",
                        "НЕОТЛОЖНОЕ СОСТОЯНИЕ: Острая аллергическая реакция у ученика после приема пищи.\n\n1. НЕМЕДЛЕННО вызовите школьного врача и скорую помощь\n2. Установите продукт, вызвавший аллергию\n3. При наличии - введите антигистаминный препарат\n4. При затруднении дыхания - обеспечьте полусидячее положение\n5. Контролируйте сознание и дыхание\n6. Подготовьте медицинскую карту с информацией об аллергиях\n7. Уведомите родителей и администрацию школы",
                        "school_allergy.jpg", "education"},

                {"22", "Травма на спортивной площадке",
                        "ТРАВМАТИЧЕСКОЕ ПРОИСШЕСТВИЕ: Серьезная травма во время занятий физкультурой или на перемене.\n\n1. НЕМЕДЛЕННО остановите занятие и вызовите медработника\n2. Оцените характер травмы (перелом, вывих, растяжение)\n3. Обеспечьте иммобилизацию поврежденной конечности\n4. При кровотечении - наложите давящую повязку\n5. Не перемещайте ученика с подозрением на травму позвоночника\n6. Составьте акт о несчастном случае\n7. Уведомите родителей и руководство образовательного учреждения",
                        "sports_injury.jpg", "education"},

                {"23", "Пожар в химической лаборатории",
                        "ЧРЕЗВЫЧАЙНАЯ СИТУАЦИЯ: Возгорание реактивов в школьной лаборатории.\n\n1. НЕМЕДЛЕННО активируйте пожарную сигнализацию\n2. Эвакуируйте учащихся и персонал из лаборатории\n3. Вызовите пожарную охрану (101)\n4. Используйте углекислотные огнетушители для тушения электрооборудования\n5. НЕ ИСПОЛЬЗУЙТЕ воду для тушения химических реактивов\n6. Проветрите помещение после ликвидации возгорания\n7. Составьте подробный отчет для надзорных органов",
                        "lab_fire.jpg", "education"},

                {"24", "Обморок во время школьного мероприятия",
                        "НЕОТЛОЖНОЕ СОСТОЯНИЕ: Потеря сознания учеником во время массового мероприятия.\n\n1. НЕМЕДЛЕННО вызовите школьного врача\n2. Расстегните тесную одежду, обеспечьте приток свежего воздуха\n3. Уложите ученика, приподняв ноги выше уровня головы\n4. Изолируйте от скопления людей\n5. Измерьте пульс и проверьте дыхание\n6. При длительном обмороке (>2 минут) вызовите скорую\n7. Уведомите родителей о происшествии",
                        "faint_event.jpg", "education"},

                {"25", "Отказ тормозной системы в движении",
                        "ЧРЕЗВЫЧАЙНАЯ СИТУАЦИЯ: Полный или частичный отказ тормозов во время движения транспортного средства.\n\n1. СОХРАНЯЙТЕ СПОКОЙСТВИЕ и возьмите управление под контроль\n2. Плавно используйте стояночный тормоз для снижения скорости\n3. Переключитесь на пониженные передачи для торможения двигателем\n4. Подавайте звуковые и световые сигналы для предупреждения других участников движения\n5. Выберите безопасное направление для съезда с дороги\n6. Используйте естественные препятствия для остановки (подъем, рыхлый грунт)\n7. После остановки включите аварийную сигнализацию и выставите знак аварийной остановки",
                        "brake_failure.jpg", "transport"},

                {"26", "Возгорание в двигателе автомобиля",
                        "ОПАСНАЯ СИТУАЦИЯ: Появление открытого огня или дыма из моторного отсека транспортного средства.\n\n1. НЕМЕДЛЕННО остановите транспортное средство и заглушите двигатель\n2. Эвакуируйте всех пассажиров на безопасное расстояние (не менее 50 метров)\n3. Вызовите пожарную охрану (101) и скорую помощь (103)\n4. Используйте огнетушитель для тушения, направляя струю на основание пламени\n5. НЕ ОТКРЫВАЙТЕ полностью капот во избежание притока кислорода\n6. Отсоедините клеммы аккумулятора при возможности\n7. Организуйте ограждение места происшествия",
                        "engine_fire.jpg", "transport"},

                {"27", "Захват заложников в общественном транспорте",
                        "КРИТИЧЕСКАЯ СИТУАЦИЯ: Вооруженное нападение с захватом заложников в транспортном средстве.\n\n1. НЕМЕДЛЕННО сообщите в полицию (102) и службу безопасности\n2. СОБЛЮДАЙТЕ спокойствие и выполняйте требования захватчиков\n3. Не совершайте резких движений и не оказывайте сопротивления\n4. Запомните приметы преступников и их количество\n5. По возможности передайте информацию о ситуации наружу\n6. Во время штурма лягте на пол и закройте голову руками\n7. После освобождения следуйте указаниям спецназа",
                        "hostage_transport.jpg", "transport"},

                {"28", "Разлив топлива при ДТП",
                        "ОПАСНАЯ СИТУАЦИЯ: Массовый разлив горюче-смазочных материалов после дорожно-транспортного происшествия.\n\n1. НЕМЕДЛЕННО вызовите МЧС (101) и аварийные службы\n2. Эвакуируйте людей на расстояние не менее 100 метров\n3. Исключите использование открытого огня и курение\n4. Организуйте ограждение места разлива\n5. Предупредите о возможности взрыва паров топлива\n6. Не пытайтесь самостоятельно собирать разлившееся топливо\n7. Дождитесь прибытия специализированных аварийных бригад",
                        "fuel_spill.jpg", "transport"},

                {"29", "Обрушение строительных лесов",
                        "КАТАСТРОФА: Обрушение элементов строительных лесов с находящимися на них работниками.\n\n1. НЕМЕДЛЕННО вызовите МЧС (101) и скорую помощь (103)\n2. Оцените масштабы обрушения и количество пострадавших\n3. Отключите электроэнергию на объекте\n4. Организуйте безопасный доступ к пострадавшим\n5. Не перемещайте тяжелораненых без крайней необходимости\n6. Обеспечьте стабилизацию шейного отдела у пострадавших\n7. Предоставьте проектную документацию на леса спасателям",
                        "scaffolding_collapse.jpg", "technical"},

                {"30", "Взрыв газового оборудования",
                        "ЧРЕЗВЫЧАЙНАЯ СИТУАЦИЯ: Взрыв бытового или промышленного газового оборудования.\n\n1. НЕМЕДЛЕННО вызовите МЧС (101), скорую помощь (103) и газовую службу\n2. Эвакуируйте людей из зоны поражения\n3. Отключите подачу газа на главном вентиле\n4. Оцените структурную целостность здания\n5. Окажите первую помощь пострадавшим на безопасном расстоянии\n6. Не включайте электрооборудование и открытый огонь\n7. Организуйте встречу аварийных служб",
                        "gas_explosion.jpg", "technical"},

                {"31", "Падение с высоты при монтажных работах",
                        "ТЯЖЕЛАЯ ТРАВМА: Падение работника с высоты при выполнении монтажных или ремонтных работ.\n\n1. НЕМЕДЛЕННО вызовите скорую помощь и службу охраны труда\n2. Не перемещайте пострадавшего без крайней необходимости\n3. Обеспечьте иммобилизацию шейного отдела позвоночника\n4. Остановите наружные кровотечения\n5. Контролируйте сознание, дыхание и пульс\n6. Накройте пострадавшего для профилактики переохлаждения\n7. Составьте акт о несчастном случае на производстве",
                        "fall_construction.jpg", "technical"},

                {"32", "Короткое замыкание с возгоранием электропроводки",
                        "ОПАСНАЯ СИТУАЦИЯ: Короткое замыкание в электрощитовой с последующим возгоранием.\n\n1. НЕМЕДЛЕННО обесточьте объект через главный рубильник\n2. Вызовите пожарную охрану (101) и аварийную энергослужбу\n3. Используйте углекислотные или порошковые огнетушители\n4. НЕ ИСПОЛЬЗУЙТЕ воду для тушения электрооборудования под напряжением\n5. Эвакуируйте персонал из опасной зоны\n6. Организуйте вентиляцию помещения после тушения\n7. Проведите осмотр электрооборудования перед включением",
                        "electrical_fire.jpg", "technical"},

                {"33", "Террористическая угроза в общественном месте",
                        "КРИТИЧЕСКАЯ СИТУАЦИЯ: Обнаружение подозрительного предмета или получение информации о террористической угрозе.\n\n1. НЕМЕДЛЕННО сообщите в полицию (102) и службу безопасности объекта\n2. НЕ ПРИКАСАЙТЕСЬ к подозрительным предметам\n3. Организуйте эвакуацию людей на расстояние не менее 300 метров\n4. Отключите системы вентиляции и кондиционирования\n5. Не используйте радиосвязь вблизи подозрительного предмета\n6. Дождитесь прибытия специалистов-взрывотехников\n7. Выполняйте все указания правоохранительных органов",
                        "terrorist_threat.jpg", "universal"},

                {"34", "Массовая паника в многолюдном месте",
                        "ЧРЕЗВЫЧАЙНАЯ СИТУАЦИЯ: Возникновение массовой паники с давкой в месте массового скопления людей.\n\n1. НЕМЕДЛЕННО вызовите полицию (102) и скорую помощь (103)\n2. Используйте систему оповещения для успокоения людей\n3. Организуйте направленные потоки для эвакуации\n4. Откройте запасные выходы и эвакуационные пути\n5. Окажите помощь пострадавшим в давке\n6. Изолируйте источник паники при возможности\n7. Организуйте пункт сбора потерявшихся",
                        "mass_panic.jpg", "universal"}
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
                {1, 12}, {1, 15}, {1, 16}, {1, 19},
                // НОВЫЕ СВЯЗИ ДЛЯ ПРЕПОДАВАТЕЛЯ
                {1, 21}, {1, 22}, {1, 23}, {1, 24}, {1, 33}, {1, 34},

                // Врач (id: 2)
                {2, 1}, {2, 2}, {2, 3}, {2, 4}, {2, 8}, {2, 9}, {2, 10}, {2, 12}, {2, 18}, {2, 19},
                // НОВЫЕ СВЯЗИ ДЛЯ ВРАЧА
                {2, 21}, {2, 22}, {2, 24}, {2, 33}, {2, 34},

                // Водитель (id: 3)
                {3, 1}, {3, 2}, {3, 3}, {3, 5}, {3, 7}, {3, 9}, {3, 10}, {3, 12}, {3, 17}, {3, 19},
                // НОВЫЕ СВЯЗИ ДЛЯ ВОДИТЕЛЯ
                {3, 25}, {3, 26}, {3, 27}, {3, 28}, {3, 33}, {3, 34},

                // Мастер ПК (id: 4)
                {4, 1}, {4, 2}, {4, 6}, {4, 7}, {4, 13}, {4, 14}, {4, 19}, {4, 20},
                // НОВЫЕ СВЯЗИ ДЛЯ МАСТЕРА ПК
                {4, 29}, {4, 30}, {4, 31}, {4, 32}, {4, 33}, {4, 34},

                // Универсальный (id: 5)
                {5, 1}, {5, 2}, {5, 3}, {5, 4}, {5, 5}, {5, 6}, {5, 7}, {5, 8}, {5, 9}, {5, 10},
                {5, 11}, {5, 12}, {5, 13}, {5, 14}, {5, 15}, {5, 16}, {5, 17}, {5, 18}, {5, 19}, {5, 20},
                // НОВЫЕ СВЯЗИ ДЛЯ УНИВЕРСАЛЬНОГО
                {5, 21}, {5, 22}, {5, 23}, {5, 24}, {5, 25}, {5, 26}, {5, 27}, {5, 28},
                {5, 29}, {5, 30}, {5, 31}, {5, 32}, {5, 33}, {5, 34}
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