package com.felix.countbadhabits.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.felix.countbadhabits.model.BadHabit;
import java.util.ArrayList;
import java.util.List;

/**
 * 坏习惯数据访问对象
 */
public class BadHabitDao {
    private DatabaseHelper dbHelper;

    public BadHabitDao(DatabaseHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    /**
     * 获取所有活跃的坏习惯
     */
    public List<BadHabit> getAllActiveHabits() {
        List<BadHabit> habits = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        
        String selection = DatabaseHelper.COLUMN_HABIT_IS_ACTIVE + " = ?";
        String[] selectionArgs = {"1"};
        String orderBy = DatabaseHelper.COLUMN_HABIT_CREATED_DATE + " ASC";
        
        Cursor cursor = db.query(DatabaseHelper.TABLE_BAD_HABITS, null, selection, selectionArgs, null, null, orderBy);
        
        if (cursor != null) {
            while (cursor.moveToNext()) {
                habits.add(cursorToHabit(cursor));
            }
            cursor.close();
        }
        
        return habits;
    }

    /**
     * 获取所有坏习惯（包括已删除的）
     */
    public List<BadHabit> getAllHabits() {
        List<BadHabit> habits = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        
        String orderBy = DatabaseHelper.COLUMN_HABIT_CREATED_DATE + " ASC";
        Cursor cursor = db.query(DatabaseHelper.TABLE_BAD_HABITS, null, null, null, null, null, orderBy);
        
        if (cursor != null) {
            while (cursor.moveToNext()) {
                habits.add(cursorToHabit(cursor));
            }
            cursor.close();
        }
        
        return habits;
    }

    /**
     * 根据ID获取坏习惯
     */
    public BadHabit getHabitById(long habitId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        
        String selection = DatabaseHelper.COLUMN_HABIT_ID + " = ?";
        String[] selectionArgs = {String.valueOf(habitId)};
        
        Cursor cursor = db.query(DatabaseHelper.TABLE_BAD_HABITS, null, selection, selectionArgs, null, null, null);
        
        BadHabit habit = null;
        if (cursor != null && cursor.moveToFirst()) {
            habit = cursorToHabit(cursor);
            cursor.close();
        }
        
        return habit;
    }

    /**
     * 插入新的坏习惯
     */
    public long insertHabit(BadHabit habit) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_HABIT_NAME, habit.getName());
        values.put(DatabaseHelper.COLUMN_HABIT_DAILY_LIMIT, habit.getDailyLimit());
        values.put(DatabaseHelper.COLUMN_HABIT_CREATED_DATE, habit.getCreatedDate());
        values.put(DatabaseHelper.COLUMN_HABIT_IS_ACTIVE, habit.isActive() ? 1 : 0);
        
        long id = db.insert(DatabaseHelper.TABLE_BAD_HABITS, null, values);
        habit.setId(id);
        
        return id;
    }

    /**
     * 更新坏习惯
     */
    public int updateHabit(BadHabit habit) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_HABIT_NAME, habit.getName());
        values.put(DatabaseHelper.COLUMN_HABIT_DAILY_LIMIT, habit.getDailyLimit());
        values.put(DatabaseHelper.COLUMN_HABIT_IS_ACTIVE, habit.isActive() ? 1 : 0);
        
        String whereClause = DatabaseHelper.COLUMN_HABIT_ID + " = ?";
        String[] whereArgs = {String.valueOf(habit.getId())};
        
        return db.update(DatabaseHelper.TABLE_BAD_HABITS, values, whereClause, whereArgs);
    }

    /**
     * 删除坏习惯（软删除，设置为非活跃状态）
     */
    public int deleteHabit(long habitId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_HABIT_IS_ACTIVE, 0);
        
        String whereClause = DatabaseHelper.COLUMN_HABIT_ID + " = ?";
        String[] whereArgs = {String.valueOf(habitId)};
        
        return db.update(DatabaseHelper.TABLE_BAD_HABITS, values, whereClause, whereArgs);
    }

    /**
     * 彻底删除坏习惯及其所有记录
     */
    public int permanentDeleteHabit(long habitId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        
        // 由于设置了外键约束，删除习惯时会自动删除相关记录
        String whereClause = DatabaseHelper.COLUMN_HABIT_ID + " = ?";
        String[] whereArgs = {String.valueOf(habitId)};
        
        return db.delete(DatabaseHelper.TABLE_BAD_HABITS, whereClause, whereArgs);
    }

    /**
     * 获取第一个活跃的坏习惯ID（用于默认选择）
     */
    public long getFirstActiveHabitId() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        
        String selection = DatabaseHelper.COLUMN_HABIT_IS_ACTIVE + " = ?";
        String[] selectionArgs = {"1"};
        String orderBy = DatabaseHelper.COLUMN_HABIT_CREATED_DATE + " ASC";
        String limit = "1";
        
        Cursor cursor = db.query(DatabaseHelper.TABLE_BAD_HABITS, 
                new String[]{DatabaseHelper.COLUMN_HABIT_ID}, 
                selection, selectionArgs, null, null, orderBy, limit);
        
        long habitId = -1;
        if (cursor != null && cursor.moveToFirst()) {
            habitId = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_HABIT_ID));
            cursor.close();
        }
        
        return habitId;
    }

    /**
     * 将Cursor转换为BadHabit对象
     */
    private BadHabit cursorToHabit(Cursor cursor) {
        BadHabit habit = new BadHabit();
        
        habit.setId(cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_HABIT_ID)));
        habit.setName(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_HABIT_NAME)));
        habit.setDailyLimit(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_HABIT_DAILY_LIMIT)));
        habit.setCreatedDate(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_HABIT_CREATED_DATE)));
        habit.setActive(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_HABIT_IS_ACTIVE)) == 1);
        
        return habit;
    }
}