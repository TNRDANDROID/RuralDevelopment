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
    public static final String OBSERVATION_TABLE = "observation";
    public static final String WORK_LIST_OPTIONAL = "WorkListOptional";
    public static final String WORK_STAGE_TABLE = "work_type_stage_link";
    public static final String VILLAGE_TABLE_NAME = "village_table_name";
    public static final String INSPECTION = "inspection";
    public static final String CAPTURED_PHOTO = "captured_photo";
    public static final String INSPECTION_ACTION = "inspection_action";

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

        db.execSQL("CREATE TABLE " + VILLAGE_TABLE_NAME  + " ("
                + "dcode varchar(4)," +
                "bcode varchar(4)," +
                "pvcode varchar(4)," +
                "pvname varchar(32))");

        db.execSQL("CREATE TABLE " + SCHEME_TABLE_NAME + " ("
                + "scheme_name varchar(32)," +
                "scheme_seq_id varchar(4))");

        db.execSQL("CREATE TABLE " + OBSERVATION_TABLE + " ("
                + "id INTEGER," +
                "observation TEXT)");

        db.execSQL("CREATE TABLE " + FINANCIAL_YEAR_TABLE_NAME + " ("
                + "fin_year  varchar(32))");

        db.execSQL("CREATE TABLE " + WORK_STAGE_TABLE + " ("
                + "work_group_id  varchar(4)," +
                "work_type_id  varchar(4)," +
                "work_stage_order  INTEGER," +
                "work_stage_code  varchar(32)," +
                "work_stage_name varchar(4))");

        db.execSQL("CREATE TABLE " + WORK_LIST_OPTIONAL + " ("
                + "dcode  varchar(4)," +
                 "pvcode  varchar(4)," +
                "bcode  varchar(4)," +
                "scheme_id  varchar(4)," +
                "work_group_id  varchar(4)," +
                "work_type_id  varchar(4)," +
                "fin_year  varchar(4)," +
                "work_id  varchar(4)," +
                "work_name  varchar(32)," +
                "as_value  varchar(32)," +
                "ts_value  varchar(32)," +
                "current_stage_of_work  varchar(32)," +
                "is_high_value varchar(4))");

        db.execSQL("CREATE TABLE "+ INSPECTION  + "("
                + "inspection_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "work_id TEXT," +
                "stage_of_work_on_inspection TEXT," +
                "date_of_inspection TEXT," +
                "inspected_by TEXT," +
                "observation TEXT," +
                "inspection_remark TEXT," +
                "created_date TEXT," +
                "created_ipaddress TEXT," +
                "delete_flag INTEGER," +
                "created_username TEXT)");

        db.execSQL("CREATE TABLE "+ CAPTURED_PHOTO + "("
                + "inspection_id INTEGER," +
                "work_id TEXT," +
                "latitude TEXT," +
                "longitude TEXT," +
                "image blob,"+
                "description TEXT)");

        db.execSQL("CREATE TABLE "+ INSPECTION_ACTION  + "("
                + "id INTEGER," +
                "work_id INTEGER," +
                "inspection_id INTEGER," +
                "date_of_action TEXT," +
                "action_taken TEXT," +
                "action_remark TEXT," +
                "created_date TEXT," +
                "dist_action TEXT," +
                "state_action TEXT," +
                "sub_div_action TEXT)");


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion >= newVersion) {
            //drop table if already exists
            db.execSQL("DROP TABLE IF EXISTS " + BLOCK_TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + VILLAGE_TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + SCHEME_TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + OBSERVATION_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + WORK_STAGE_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + FINANCIAL_YEAR_TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + WORK_LIST_OPTIONAL);
            db.execSQL("DROP TABLE IF EXISTS " + INSPECTION);
            db.execSQL("DROP TABLE IF EXISTS " + CAPTURED_PHOTO);
            db.execSQL("DROP TABLE IF EXISTS " + INSPECTION_ACTION);
            onCreate(db);
        }
    }


}
