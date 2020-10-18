package id.mncplay.triviaquestions.database

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import id.mncplay.triviaquestions.models.DataCategory
import id.mncplay.triviaquestions.models.DataDetailHistory
import id.mncplay.triviaquestions.models.DataHistory
import id.mncplay.triviaquestions.models.DataUser
import kotlin.jvm.Throws

class DbHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_ENTRIES_CATEGORY)
        db.execSQL(SQL_CREATE_ENTRIES_USER)
        db.execSQL(SQL_CREATE_ENTRIES_HISTORY)
        db.execSQL(SQL_CREATE_ENTRIES_DETAIL_HISTORY)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES_CATEGORY)
        db.execSQL(SQL_DELETE_ENTRIES_USER)
        db.execSQL(SQL_DELETE_ENTRIES_HISTORY)
        db.execSQL(SQL_DELETE_ENTRIES_DETAIL_HISTORY)
        onCreate(db)
    }

    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        onUpgrade(db, oldVersion, newVersion)
    }

    @Throws(SQLiteConstraintException::class)
    fun insertCategory(category: DataCategory): Boolean {
        // Gets the data repository in write mode
        val db = writableDatabase

        // Create a new map of values, where column names are the keys
        val values = ContentValues()
        values.put(DBContract.UserEntry.COLUMN_QUESTION_CATEGORY_ID, category.id)
        values.put(DBContract.UserEntry.COLUMN_QUESTION_CATEGORY_NAME, category.name)

        // Insert the new row, returning the primary key value of the new row
        db.insert(DBContract.UserEntry.TABLE_QUESTIONS_CATEGORY, null, values)

        return true
    }

    @Throws(SQLiteConstraintException::class)
    fun insertUser(user: DataUser): Boolean {
        // Gets the data repository in write mode
        val db = writableDatabase

        // Create a new map of values, where column names are the keys
        val values = ContentValues()
        values.put(DBContract.UserEntry.COLUMN_USER_ID, user.id)
        values.put(DBContract.UserEntry.COLUMN_USER_USERNAME, user.username)
        values.put(DBContract.UserEntry.COLUMN_USER_NAME, user.name)
        values.put(DBContract.UserEntry.COLUMN_USER_SCORE, user.score)


        // Insert the new row, returning the primary key value of the new row
        val row = db.insert(DBContract.UserEntry.TABLE_USER, null, values)

        Log.d("insertuser", ""+row)
        return true
    }

    @Throws(SQLiteConstraintException::class)
    fun insertDetailHistory(user: DataDetailHistory): Boolean {
        // Gets the data repository in write mode
        val db = writableDatabase

        // Create a new map of values, where column names are the keys
        val values = ContentValues()
        //values.put(DBContract.UserEntry.COLUMN_DETAIL_HISTORY_ID, user.id)
        values.put(DBContract.UserEntry.COLUMN_DETAIL_HISTORY_ID_HISTORY, user.idHistory)
        values.put(DBContract.UserEntry.COLUMN_DETAIL_HISTORY_QUESTION, user.question)
        values.put(DBContract.UserEntry.COLUMN_DETAIL_HISTORY_OPTION_A, user.optionA)
        values.put(DBContract.UserEntry.COLUMN_DETAIL_HISTORY_OPTION_B, user.optionB)
        values.put(DBContract.UserEntry.COLUMN_DETAIL_HISTORY_OPTION_C, user.optionC)
        values.put(DBContract.UserEntry.COLUMN_DETAIL_HISTORY_OPTION_D, user.optionD)
        values.put(DBContract.UserEntry.COLUMN_DETAIL_HISTORY_TRUE_ANSWER, user.trueAnswer)
        values.put(DBContract.UserEntry.COLUMN_DETAIL_HISTORY_ANSWER, user.answer)

        // Insert the new row, returning the primary key value of the new row
        val datas =db.insert(DBContract.UserEntry.TABLE_DETAIL_HISTORY, null, values)
        Log.d("history", ""+datas)

        return true
    }

    @Throws(SQLiteConstraintException::class)
    fun insertHistory(data: DataHistory): Boolean {
        // Gets the data repository in write mode
        val db = writableDatabase

        // Create a new map of values, where column names are the keys
        val values = ContentValues()
        values.put(DBContract.UserEntry.COLUMN_HISTORY_ID, data.id)
        values.put(DBContract.UserEntry.COLUMN_HISTORY_USERNAME, data.username)
        values.put(DBContract.UserEntry.COLUMN_HISTORY_CATEGORY, data.category)
        values.put(DBContract.UserEntry.COLUMN_HISTORY_MODE, data.mode)
        values.put(DBContract.UserEntry.COLUMN_HISTORY_DATE, data.date)

        // Insert the new row, returning the primary key value of the new row
        db.insert(DBContract.UserEntry.TABLE_HISTORY, null, values)

        return true
    }

    @SuppressLint("Recycle")
    fun readAllUsers(): ArrayList<DataUser> {
        val users = ArrayList<DataUser>()
        val db = writableDatabase
        val cursor: Cursor?
        try {
            cursor = db.rawQuery("SELECT * FROM " + DBContract.UserEntry.TABLE_USER+" ORDER BY "+DBContract.UserEntry.COLUMN_USER_SCORE +" DESC ", null)
        } catch (e: SQLiteException) {
            db.execSQL(SQL_CREATE_ENTRIES_USER)
            return ArrayList()
        }

        var userId: String
        var username: String
        var name: String
        var score: Int
        if (cursor!!.moveToFirst()) {
            while (cursor.isAfterLast == false) {
                userId = cursor.getString(cursor.getColumnIndex(DBContract.UserEntry.COLUMN_USER_ID))
                username = cursor.getString(cursor.getColumnIndex(DBContract.UserEntry.COLUMN_USER_USERNAME))
                name = cursor.getString(cursor.getColumnIndex(DBContract.UserEntry.COLUMN_USER_NAME))
                score = cursor.getInt(cursor.getColumnIndex(DBContract.UserEntry.COLUMN_USER_SCORE))

                users.add(DataUser(userId, username, name, score))
                cursor.moveToNext()
            }
        }
        return users
    }

    @SuppressLint("Recycle")
    fun readAllCategory(): ArrayList<DataCategory> {
        val category = ArrayList<DataCategory>()
        val db = writableDatabase
        val cursor: Cursor?
        try {
            cursor = db.rawQuery("select * from " + DBContract.UserEntry.TABLE_QUESTIONS_CATEGORY, null)
        } catch (e: SQLiteException) {
            db.execSQL(SQL_CREATE_ENTRIES_CATEGORY)
            return ArrayList()
        }
        var categryId: Int
        var categoryName: String
        if (cursor!!.moveToFirst()) {
            while (cursor.isAfterLast == false) {
                categryId = cursor.getInt(cursor.getColumnIndex(DBContract.UserEntry.COLUMN_QUESTION_CATEGORY_ID))
                categoryName = cursor.getString(cursor.getColumnIndex(DBContract.UserEntry.COLUMN_QUESTION_CATEGORY_NAME))

                category.add(DataCategory(categryId,categoryName))
                cursor.moveToNext()
            }
        }
        return category
    }

    fun readAllHistoryByName(username: String): ArrayList<DataHistory> {
        val history = ArrayList<DataHistory>()
        val db = writableDatabase
        var cursor: Cursor? = null
        try {
            cursor = db.rawQuery("select * from " + DBContract.UserEntry.TABLE_HISTORY+ " WHERE " + DBContract.UserEntry.COLUMN_HISTORY_USERNAME + "='" + username + "'" +" order by "+DBContract.UserEntry.COLUMN_HISTORY_DATE +" asc", null)
        } catch (e: SQLiteException) {
            db.execSQL(SQL_CREATE_ENTRIES_HISTORY)
            return ArrayList()
        }

        var id: String
        var username: String
        var category: String
        var mode: String
        var date: String

        if (cursor!!.moveToFirst()) {
            while (!cursor.isAfterLast) {
                id = cursor.getString(cursor.getColumnIndex(DBContract.UserEntry.COLUMN_HISTORY_ID))
                username = cursor.getString(cursor.getColumnIndex(DBContract.UserEntry.COLUMN_HISTORY_USERNAME))
                category = cursor.getString(cursor.getColumnIndex(DBContract.UserEntry.COLUMN_HISTORY_CATEGORY))
                mode = cursor.getString(cursor.getColumnIndex(DBContract.UserEntry.COLUMN_HISTORY_MODE))
                date = cursor.getString(cursor.getColumnIndex(DBContract.UserEntry.COLUMN_HISTORY_DATE))

                history.add(DataHistory(id, username, category, mode, date))
                cursor.moveToNext()
            }
        }
        return history
    }

    fun readAllHistory(): ArrayList<DataHistory> {
        val history = ArrayList<DataHistory>()
        val db = writableDatabase
        var cursor: Cursor? = null
        try {
            cursor = db.rawQuery("select * from " + DBContract.UserEntry.TABLE_HISTORY +" order by "+DBContract.UserEntry.COLUMN_HISTORY_DATE +" asc", null)
        } catch (e: SQLiteException) {
            db.execSQL(SQL_CREATE_ENTRIES_HISTORY)
            return ArrayList()
        }

        var id: String
        var username: String
        var category: String
        var mode: String
        var date: String

        if (cursor!!.moveToFirst()) {
            while (!cursor.isAfterLast) {
                id = cursor.getString(cursor.getColumnIndex(DBContract.UserEntry.COLUMN_HISTORY_ID))
                username = cursor.getString(cursor.getColumnIndex(DBContract.UserEntry.COLUMN_HISTORY_USERNAME))
                category = cursor.getString(cursor.getColumnIndex(DBContract.UserEntry.COLUMN_HISTORY_CATEGORY))
                mode = cursor.getString(cursor.getColumnIndex(DBContract.UserEntry.COLUMN_HISTORY_MODE))
                date = cursor.getString(cursor.getColumnIndex(DBContract.UserEntry.COLUMN_HISTORY_DATE))

                history.add(DataHistory(id, username, category, mode, date))
                cursor.moveToNext()
            }
        }
        return history
    }

    fun readDetailHistory(historyId: String): ArrayList<DataDetailHistory> {
        val data = ArrayList<DataDetailHistory>()
        val db = writableDatabase
        var cursor: Cursor? = null
        try {
            cursor = db.rawQuery("select * from " + DBContract.UserEntry.TABLE_DETAIL_HISTORY + " WHERE " + DBContract.UserEntry.COLUMN_DETAIL_HISTORY_ID_HISTORY + "='" + historyId + "'", null)
        } catch (e: SQLiteException) {
            // if table not yet present, create it
            db.execSQL(SQL_CREATE_ENTRIES_DETAIL_HISTORY)
            return ArrayList()
        }

        var id: String
        var idHistory: String
        var question: String
        var optionA: String
        var optionB: String
        var optionC: String
        var optionD: String
        var trueAnswer: String
        var answer: String

        if (cursor!!.moveToFirst()) {
            while (!cursor.isAfterLast) {
                id = cursor.getString(cursor.getColumnIndex(DBContract.UserEntry.COLUMN_HISTORY_ID))
                idHistory = cursor.getString(cursor.getColumnIndex(DBContract.UserEntry.COLUMN_DETAIL_HISTORY_ID_HISTORY))
                question = cursor.getString(cursor.getColumnIndex(DBContract.UserEntry.COLUMN_DETAIL_HISTORY_QUESTION))
                optionA = cursor.getString(cursor.getColumnIndex(DBContract.UserEntry.COLUMN_DETAIL_HISTORY_OPTION_A))
                optionB = cursor.getString(cursor.getColumnIndex(DBContract.UserEntry.COLUMN_DETAIL_HISTORY_OPTION_B))
                optionC = cursor.getString(cursor.getColumnIndex(DBContract.UserEntry.COLUMN_DETAIL_HISTORY_OPTION_C))
                optionD = cursor.getString(cursor.getColumnIndex(DBContract.UserEntry.COLUMN_DETAIL_HISTORY_OPTION_D))
                trueAnswer = cursor.getString(cursor.getColumnIndex(DBContract.UserEntry.COLUMN_DETAIL_HISTORY_TRUE_ANSWER))
                answer = cursor.getString(cursor.getColumnIndex(DBContract.UserEntry.COLUMN_DETAIL_HISTORY_ANSWER))

                data.add(DataDetailHistory(id, idHistory, question, optionA, optionB, optionC, optionD, trueAnswer, answer))
                cursor.moveToNext()
            }
        }
        return data
    }

    @Throws(SQLiteConstraintException::class)
    fun deleteAllUser(): Boolean {
        // Gets the data repository in write mode
        val db = writableDatabase

        // Issue SQL statement.
        db.execSQL("DELETE FROM "+DBContract.UserEntry.TABLE_USER)

        return true
    }


    companion object {
        // If you change the database schema, you must increment the database version.
        val DATABASE_VERSION = 2
        val DATABASE_NAME = "TrivianQuestion.db"

        private val SQL_CREATE_ENTRIES_CATEGORY =
            "CREATE TABLE " + DBContract.UserEntry.TABLE_QUESTIONS_CATEGORY + " (" +
                    DBContract.UserEntry.COLUMN_QUESTION_CATEGORY_ID + " INTEGER PRIMARY KEY," +
                    DBContract.UserEntry.COLUMN_QUESTION_CATEGORY_NAME + " TEXT )"

        private val SQL_CREATE_ENTRIES_USER =
            "CREATE TABLE " + DBContract.UserEntry.TABLE_USER + " (" +
                    DBContract.UserEntry.COLUMN_USER_ID + " INTEGER PRIMARY KEY, " +
                    DBContract.UserEntry.COLUMN_USER_USERNAME + " TEXT," +
                    DBContract.UserEntry.COLUMN_USER_NAME + " TEXT," +
                    DBContract.UserEntry.COLUMN_USER_SCORE + " INTEGER )"

        private val SQL_CREATE_ENTRIES_HISTORY =
            "CREATE TABLE " + DBContract.UserEntry.TABLE_HISTORY + " (" +
                    DBContract.UserEntry.COLUMN_HISTORY_ID + " TEXT PRIMARY KEY, " +
                    DBContract.UserEntry.COLUMN_HISTORY_USERNAME + " TEXT," +
                    DBContract.UserEntry.COLUMN_HISTORY_CATEGORY + " TEXT," +
                    DBContract.UserEntry.COLUMN_HISTORY_MODE + " TEXT," +
                    DBContract.UserEntry.COLUMN_HISTORY_DATE + " TEXT )"

        private val SQL_CREATE_ENTRIES_DETAIL_HISTORY =
            "CREATE TABLE " + DBContract.UserEntry.TABLE_DETAIL_HISTORY + " (" +
                    DBContract.UserEntry.COLUMN_DETAIL_HISTORY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    DBContract.UserEntry.COLUMN_DETAIL_HISTORY_ID_HISTORY + " INTEGER, " +
                    DBContract.UserEntry.COLUMN_DETAIL_HISTORY_QUESTION + " TEXT," +
                    DBContract.UserEntry.COLUMN_DETAIL_HISTORY_OPTION_A + " TEXT," +
                    DBContract.UserEntry.COLUMN_DETAIL_HISTORY_OPTION_B + " TEXT," +
                    DBContract.UserEntry.COLUMN_DETAIL_HISTORY_OPTION_C + " TEXT," +
                    DBContract.UserEntry.COLUMN_DETAIL_HISTORY_OPTION_D + " TEXT," +
                    DBContract.UserEntry.COLUMN_DETAIL_HISTORY_TRUE_ANSWER + " TEXT," +
                    DBContract.UserEntry.COLUMN_DETAIL_HISTORY_ANSWER + " TEXT )"

        private val SQL_DELETE_ENTRIES_CATEGORY = "DROP TABLE IF EXISTS " + DBContract.UserEntry.TABLE_QUESTIONS_CATEGORY
        private val SQL_DELETE_ENTRIES_USER = "DROP TABLE IF EXISTS " + DBContract.UserEntry.TABLE_USER
        private val SQL_DELETE_ENTRIES_HISTORY = "DROP TABLE IF EXISTS " + DBContract.UserEntry.TABLE_HISTORY
        private val SQL_DELETE_ENTRIES_DETAIL_HISTORY = "DROP TABLE IF EXISTS " + DBContract.UserEntry.TABLE_DETAIL_HISTORY
    }


}