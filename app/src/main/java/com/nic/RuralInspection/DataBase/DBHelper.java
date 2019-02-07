package com.nic.RuralInspection.DataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "RuralInspection";
    private static final int DATABASE_VERSION = 1;
    public static final String BLOCK_TABLE_NAME = "BlockList";
    public static final String SCHEME_TABLE_NAME = "SchemeList";
    public static final String FINANCIAL_YEAR_TABLE_NAME = "FinancialYear";
    public static final String WORK_LIST_DISTRICT_FINYEAR_WISE = "WorkListDistFinYearWise";
    private Context context;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;

    }

    //creating tables
    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE " + BLOCK_TABLE_NAME + " ("
                + "dcode varchar(4)," +
                "bcode varchar(4)," +
                "bname varchar(32))");

        db.execSQL("CREATE TABLE " + SCHEME_TABLE_NAME + " ("
                + "scheme_name varchar(32)," +
                "scheme_seq_id varchar(4))");

        db.execSQL("CREATE TABLE " + FINANCIAL_YEAR_TABLE_NAME + " ("
                + "fin_year  varchar(32))");

        db.execSQL("CREATE TABLE " + WORK_LIST_DISTRICT_FINYEAR_WISE + " ("
                + "bcode  varchar(4)," +
                "scheme_id  varchar(4)," +
                "work_id  varchar(4)," +
                "work_name  varchar(32)," +
                "as_value  varchar(32)," +
                "ts_value  varchar(32)," +
                "is_high_value varchar(4))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion >= newVersion) {
            //drop table if already exists
            db.execSQL("DROP TABLE IF EXISTS " + BLOCK_TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + SCHEME_TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + FINANCIAL_YEAR_TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + WORK_LIST_DISTRICT_FINYEAR_WISE);
            onCreate(db);
        }
    }


}
