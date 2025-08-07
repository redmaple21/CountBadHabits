package com.felix.countbadhabits.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.felix.countbadhabits.model.TriggerRecord;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * 触发记录数据访问对象
 */
public class TriggerRecordDao {
    private DatabaseHelper dbHelper;

    public TriggerRecordDao(DatabaseHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    /**
     * 获取今日的触发记录
     */
    public List<TriggerRecord> getTodayRecords(long habitId) {
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        return getRecordsByDate(habitId, today);
    }

    /**
     * 根据日期获取触发记录
     */
    public List<TriggerRecord> getRecordsByDate(long habitId, String date) {
        List<TriggerRecord> records = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        
        String selection = DatabaseHelper.COLUMN_RECORD_HABIT_ID + " = ? AND " 
                + DatabaseHelper.COLUMN_RECORD_TRIGGER_DATE + " = ?";
        String[] selectionArgs = {String.valueOf(habitId), date};
        String orderBy = DatabaseHelper.COLUMN_RECORD_TRIGGER_TIME + " ASC";
        
        Cursor cursor = db.query(DatabaseHelper.TABLE_TRIGGER_RECORDS, null, selection, selectionArgs, null, null, orderBy);
        
        if (cursor != null) {
            while (cursor.moveToNext()) {
                records.add(cursorToRecord(cursor));
            }
            cursor.close();
        }
        
        return records;
    }

    /**
     * 插入新的触发记录
     */
    public long insertRecord(TriggerRecord record) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        
        // 获取当日的序号
        int sequenceNumber = getTodayRecordCount(record.getHabitId()) + 1;
        record.setSequenceNumber(sequenceNumber);
        
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_RECORD_HABIT_ID, record.getHabitId());
        values.put(DatabaseHelper.COLUMN_RECORD_TRIGGER_DATE, record.getTriggerDate());
        values.put(DatabaseHelper.COLUMN_RECORD_TRIGGER_TIME, record.getTriggerTime());
        values.put(DatabaseHelper.COLUMN_RECORD_TRIGGER_DATETIME, record.getTriggerDateTime());
        values.put(DatabaseHelper.COLUMN_RECORD_DESCRIPTION, record.getDescription());
        values.put(DatabaseHelper.COLUMN_RECORD_SEQUENCE_NUMBER, record.getSequenceNumber());
        
        long id = db.insert(DatabaseHelper.TABLE_TRIGGER_RECORDS, null, values);
        record.setId(id);
        
        return id;
    }

    /**
     * 更新触发记录
     */
    public int updateRecord(TriggerRecord record) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_RECORD_TRIGGER_TIME, record.getTriggerTime());
        values.put(DatabaseHelper.COLUMN_RECORD_TRIGGER_DATETIME, record.getTriggerDateTime());
        values.put(DatabaseHelper.COLUMN_RECORD_DESCRIPTION, record.getDescription());
        
        String whereClause = DatabaseHelper.COLUMN_RECORD_ID + " = ?";
        String[] whereArgs = {String.valueOf(record.getId())};
        
        return db.update(DatabaseHelper.TABLE_TRIGGER_RECORDS, values, whereClause, whereArgs);
    }

    /**
     * 删除触发记录
     */
    public int deleteRecord(long recordId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        
        String whereClause = DatabaseHelper.COLUMN_RECORD_ID + " = ?";
        String[] whereArgs = {String.valueOf(recordId)};
        
        return db.delete(DatabaseHelper.TABLE_TRIGGER_RECORDS, whereClause, whereArgs);
    }

    /**
     * 获取今日记录数量
     */
    public int getTodayRecordCount(long habitId) {
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        return getRecordCountByDate(habitId, today);
    }

    /**
     * 根据日期获取记录数量
     */
    public int getRecordCountByDate(long habitId, String date) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        
        String selection = DatabaseHelper.COLUMN_RECORD_HABIT_ID + " = ? AND " 
                + DatabaseHelper.COLUMN_RECORD_TRIGGER_DATE + " = ?";
        String[] selectionArgs = {String.valueOf(habitId), date};
        
        Cursor cursor = db.query(DatabaseHelper.TABLE_TRIGGER_RECORDS, 
                new String[]{"COUNT(*)"}, 
                selection, selectionArgs, null, null, null);
        
        int count = 0;
        if (cursor != null && cursor.moveToFirst()) {
            count = cursor.getInt(0);
            cursor.close();
        }
        
        return count;
    }

    /**
     * 获取月度统计数据（日期 -> 触发次数）
     */
    public Map<String, Integer> getMonthlyStatistics(long habitId, int year, int month) {
        Map<String, Integer> statistics = new HashMap<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        
        String monthStr = String.format(Locale.getDefault(), "%04d-%02d", year, month);
        String selection = DatabaseHelper.COLUMN_RECORD_HABIT_ID + " = ? AND " 
                + DatabaseHelper.COLUMN_RECORD_TRIGGER_DATE + " LIKE ?";
        String[] selectionArgs = {String.valueOf(habitId), monthStr + "%"};
        
        String sql = "SELECT " + DatabaseHelper.COLUMN_RECORD_TRIGGER_DATE + ", COUNT(*) as count " +
                "FROM " + DatabaseHelper.TABLE_TRIGGER_RECORDS + " " +
                "WHERE " + selection + " " +
                "GROUP BY " + DatabaseHelper.COLUMN_RECORD_TRIGGER_DATE;
        
        Cursor cursor = db.rawQuery(sql, selectionArgs);
        
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String date = cursor.getString(0);
                int count = cursor.getInt(1);
                statistics.put(date, count);
            }
            cursor.close();
        }
        
        return statistics;
    }

    /**
     * 获取年度统计数据（月份 -> 触发次数）
     */
    public Map<String, Integer> getYearlyStatistics(long habitId, int year) {
        Map<String, Integer> statistics = new HashMap<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        
        String yearStr = String.valueOf(year);
        String selection = DatabaseHelper.COLUMN_RECORD_HABIT_ID + " = ? AND " 
                + DatabaseHelper.COLUMN_RECORD_TRIGGER_DATE + " LIKE ?";
        String[] selectionArgs = {String.valueOf(habitId), yearStr + "%"};
        
        String sql = "SELECT substr(" + DatabaseHelper.COLUMN_RECORD_TRIGGER_DATE + ", 1, 7) as month, COUNT(*) as count " +
                "FROM " + DatabaseHelper.TABLE_TRIGGER_RECORDS + " " +
                "WHERE " + selection + " " +
                "GROUP BY month " +
                "ORDER BY month";
        
        Cursor cursor = db.rawQuery(sql, selectionArgs);
        
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String month = cursor.getString(0);
                int count = cursor.getInt(1);
                statistics.put(month, count);
            }
            cursor.close();
        }
        
        return statistics;
    }

    /**
     * 获取指定日期范围内的记录
     */
    public List<TriggerRecord> getRecordsByDateRange(long habitId, String startDate, String endDate) {
        List<TriggerRecord> records = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        
        String selection = DatabaseHelper.COLUMN_RECORD_HABIT_ID + " = ? AND " 
                + DatabaseHelper.COLUMN_RECORD_TRIGGER_DATE + " >= ? AND "
                + DatabaseHelper.COLUMN_RECORD_TRIGGER_DATE + " <= ?";
        String[] selectionArgs = {String.valueOf(habitId), startDate, endDate};
        String orderBy = DatabaseHelper.COLUMN_RECORD_TRIGGER_DATETIME + " ASC";
        
        Cursor cursor = db.query(DatabaseHelper.TABLE_TRIGGER_RECORDS, null, selection, selectionArgs, null, null, orderBy);
        
        if (cursor != null) {
            while (cursor.moveToNext()) {
                records.add(cursorToRecord(cursor));
            }
            cursor.close();
        }
        
        return records;
    }

    /**
     * 将Cursor转换为TriggerRecord对象
     */
    private TriggerRecord cursorToRecord(Cursor cursor) {
        TriggerRecord record = new TriggerRecord();
        
        record.setId(cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_RECORD_ID)));
        record.setHabitId(cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_RECORD_HABIT_ID)));
        record.setTriggerDate(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_RECORD_TRIGGER_DATE)));
        record.setTriggerTime(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_RECORD_TRIGGER_TIME)));
        record.setTriggerDateTime(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_RECORD_TRIGGER_DATETIME)));
        record.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_RECORD_DESCRIPTION)));
        record.setSequenceNumber(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_RECORD_SEQUENCE_NUMBER)));
        
        return record;
    }
}