package com.felix.countbadhabits.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * SQLite数据库助手类
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "countbadhabits.db";
    private static final int DATABASE_VERSION = 1;

    // 坏习惯表
    public static final String TABLE_BAD_HABITS = "bad_habits";
    public static final String COLUMN_HABIT_ID = "id";
    public static final String COLUMN_HABIT_NAME = "name";
    public static final String COLUMN_HABIT_DAILY_LIMIT = "daily_limit";
    public static final String COLUMN_HABIT_CREATED_DATE = "created_date";
    public static final String COLUMN_HABIT_IS_ACTIVE = "is_active";

    // 触发记录表
    public static final String TABLE_TRIGGER_RECORDS = "trigger_records";
    public static final String COLUMN_RECORD_ID = "id";
    public static final String COLUMN_RECORD_HABIT_ID = "habit_id";
    public static final String COLUMN_RECORD_TRIGGER_DATE = "trigger_date";
    public static final String COLUMN_RECORD_TRIGGER_TIME = "trigger_time";
    public static final String COLUMN_RECORD_TRIGGER_DATETIME = "trigger_datetime";
    public static final String COLUMN_RECORD_DESCRIPTION = "description";
    public static final String COLUMN_RECORD_SEQUENCE_NUMBER = "sequence_number";

    // 创建坏习惯表的SQL语句
    private static final String CREATE_BAD_HABITS_TABLE = "CREATE TABLE " + TABLE_BAD_HABITS + " ("
            + COLUMN_HABIT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_HABIT_NAME + " TEXT NOT NULL, "
            + COLUMN_HABIT_DAILY_LIMIT + " INTEGER NOT NULL DEFAULT 5, "
            + COLUMN_HABIT_CREATED_DATE + " TEXT NOT NULL, "
            + COLUMN_HABIT_IS_ACTIVE + " INTEGER DEFAULT 1"
            + ")";

    // 创建触发记录表的SQL语句
    private static final String CREATE_TRIGGER_RECORDS_TABLE = "CREATE TABLE " + TABLE_TRIGGER_RECORDS + " ("
            + COLUMN_RECORD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_RECORD_HABIT_ID + " INTEGER NOT NULL, "
            + COLUMN_RECORD_TRIGGER_DATE + " TEXT NOT NULL, "
            + COLUMN_RECORD_TRIGGER_TIME + " TEXT NOT NULL, "
            + COLUMN_RECORD_TRIGGER_DATETIME + " TEXT NOT NULL, "
            + COLUMN_RECORD_DESCRIPTION + " TEXT, "
            + COLUMN_RECORD_SEQUENCE_NUMBER + " INTEGER NOT NULL, "
            + "FOREIGN KEY (" + COLUMN_RECORD_HABIT_ID + ") REFERENCES " + TABLE_BAD_HABITS + "(" + COLUMN_HABIT_ID + ") ON DELETE CASCADE"
            + ")";

    private static DatabaseHelper instance;

    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 创建表
        db.execSQL(CREATE_BAD_HABITS_TABLE);
        db.execSQL(CREATE_TRIGGER_RECORDS_TABLE);

        // 插入默认坏习惯
        insertDefaultHabit(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 升级数据库时删除旧表并重新创建
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRIGGER_RECORDS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BAD_HABITS);
        onCreate(db);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        // 启用外键约束
        db.setForeignKeyConstraintsEnabled(true);
    }

    /**
     * 插入默认坏习惯
     */
    private void insertDefaultHabit(SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_HABIT_NAME, "吸烟");
        values.put(COLUMN_HABIT_DAILY_LIMIT, 5);
        values.put(COLUMN_HABIT_CREATED_DATE, "2025-01-09");
        values.put(COLUMN_HABIT_IS_ACTIVE, 1);
        
        db.insert(TABLE_BAD_HABITS, null, values);

        // 添加第二个默认习惯
        values.clear();
        values.put(COLUMN_HABIT_NAME, "熬夜刷手机");
        values.put(COLUMN_HABIT_DAILY_LIMIT, 3);
        values.put(COLUMN_HABIT_CREATED_DATE, "2025-01-09");
        values.put(COLUMN_HABIT_IS_ACTIVE, 1);
        
        db.insert(TABLE_BAD_HABITS, null, values);
    }
}