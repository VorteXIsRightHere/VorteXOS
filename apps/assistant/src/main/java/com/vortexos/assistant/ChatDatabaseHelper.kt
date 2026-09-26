package com.vortexos.assistant

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class ChatDatabaseHelper(context: Context) : SQLiteOpenHelper(context, "chat_db", null, 1) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE messages (id INTEGER PRIMARY KEY AUTOINCREMENT, text TEXT, is_user INTEGER, timestamp INTEGER)")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS messages")
        onCreate(db)
    }

    fun addMessage(msg: ChatMessage) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("text", msg.text)
            put("is_user", if (msg.isUser) 1 else 0)
            put("timestamp", msg.timestamp)
        }
        db.insert("messages", null, values)
        db.close()
    }

    fun getAllMessages(): List<ChatMessage> {
        val list = mutableListOf<ChatMessage>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM messages ORDER BY timestamp ASC", null)
        if (cursor.moveToFirst()) {
            do {
                list.add(
                    ChatMessage(
                        cursor.getString(1),
                        cursor.getInt(2) == 1,
                        cursor.getLong(3),
                        cursor.getLong(0)
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return list
    }
}
