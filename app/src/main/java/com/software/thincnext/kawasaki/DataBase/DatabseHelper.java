package com.software.thincnext.kawasaki.DataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabseHelper {

    //Declaring variables
    private static String DB_PATH = "/data/data/com.software.thincnext.kawasaki/databases/";
    private static String DB_NAME = "kawasaki_database";
    private static final int DATABASE_VERSION = 1;

    private SQLiteDatabase kawasakiDataBase;
    private Context context;


    private KawasakiSQLiteHelper kawasakiSQLiteHelper;

    public DatabseHelper(Context passedContext)
    {
        kawasakiSQLiteHelper = new KawasakiSQLiteHelper(passedContext, DB_NAME, null, DATABASE_VERSION);
        this.context = passedContext;
    }

    public void createDataBase() {

        kawasakiDataBase = kawasakiSQLiteHelper.getWritableDatabase();

    }

    private class KawasakiSQLiteHelper extends SQLiteOpenHelper {

        public KawasakiSQLiteHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {


            String CREATE_FEEDBACK_TABLE = "CREATE TABLE Feedback( "      +
                    "_id INTEGER , "                           +
                    "registration_number TEXT PRIMARY KEY, "   +
                    "rating INTEGER, "                         +
                    "rating_comments TEXT);" ;

            sqLiteDatabase.execSQL(CREATE_FEEDBACK_TABLE);








        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {



        }
    }


    public boolean checkRegistrationNumber(String registration_number)
    {
        boolean existFlag = false;

        Cursor cursor = kawasakiDataBase.query("Feedback",
                null,
                "registration_number=?",
                new String[]{registration_number},
                null,null,null,null);

        if (cursor.getCount() > 0) {

            existFlag = true;
        }

        return existFlag;
    }

    public void insertRating(String registration_number, int rating, String rating_comments)
    {
        ContentValues cv= new ContentValues();

        cv.put("registration_number", registration_number);
        cv.put("rating", rating );
        cv.put("rating_comments", rating_comments);



        kawasakiDataBase.insert("Feedback", null, cv);

    }
}
