package com.example.guessphrasesqlite

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHandler(context: Context): SQLiteOpenHelper(context,"PhrasesDataBase.db",null,1){
    override fun onCreate(db: SQLiteDatabase?) {
        if(db != null){
            db.execSQL("CREATE TABLE PhrasesTable (phrase text)")
        }
    }

    override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {}
    fun addPhrases(phrase:String): Long{
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put("phrase", phrase)
        val success = db.insert("PhrasesTable", null, contentValues)
        return success
    }
    @SuppressLint("Range")
    fun viewPhrases(): ArrayList<String>{
        val phraseList: ArrayList<String> = ArrayList()
        val selectQuery = "SELECT * FROM PhrasesTable"
        val db = this.readableDatabase
        var cursor: Cursor? = null
        try {
            cursor = db.rawQuery(selectQuery, null)
        } catch (e: SQLiteException){
            db.execSQL(selectQuery)
            return ArrayList()
        }
        var phraseText: String
        if(cursor.moveToFirst()){
            do {
                phraseText = cursor.getString(cursor.getColumnIndex("phrase"))
                phraseList.add(phraseText)
            } while (cursor.moveToNext())
        }
        return phraseList
    }
}