package com.vortexos.emotion

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class EmotionHistoryManager(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "EmotionHistory.db"
        const val TABLE_EMOTIONS = "emotions"
        const val COLUMN_ID = "_id"
        const val COLUMN_EMOTION = "emotion_name"
        const val COLUMN_TIMESTAMP = "timestamp"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = ("CREATE TABLE " + TABLE_EMOTIONS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_EMOTION + " TEXT,"
                + COLUMN_TIMESTAMP + " TEXT" + ")")
        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_EMOTIONS")
        onCreate(db)
    }

    fun addEmotion(emotion: String) {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_EMOTION, emotion)
        
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        values.put(COLUMN_TIMESTAMP, sdf.format(Date()))
        
        db.insert(TABLE_EMOTIONS, null, values)
        db.close()
    }

    fun getRecentEmotions(limit: Int): List<EmotionRecord> {
        val list = mutableListOf<EmotionRecord>()
        val db = this.readableDatabase
        val cursor = db.query(TABLE_EMOTIONS, arrayOf(COLUMN_EMOTION, COLUMN_TIMESTAMP),
            null, null, null, null, "$COLUMN_ID DESC", limit.toString())
        
        if (cursor.moveToFirst()) {
            do {
                val emotion = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMOTION))
                val timestamp = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TIMESTAMP))
                list.add(EmotionRecord(emotion, timestamp))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return list
    }
}

data class EmotionRecord(val emotion: String, val timestamp: String)
