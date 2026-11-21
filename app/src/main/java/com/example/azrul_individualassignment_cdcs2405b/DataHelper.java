
package com.example.azrul_individualassignment_cdcs2405b;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataHelper extends SQLiteOpenHelper {

    // Database Name
    private static final String DATABASE_NAME = "electricity.db";

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Table Name
    public static final String TABLE_BILLS = "bills";

    // Table Columns
    public static final String COL_ID = "id";
    public static final String COL_MONTH = "month";
    public static final String COL_UNITS = "units";
    public static final String COL_TOTAL = "total_charges";
    public static final String COL_REBATE = "rebate";
    public static final String COL_FINAL = "final_cost";

    // Create Constructor
    public DataHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Create Table
    @Override
    public void onCreate(SQLiteDatabase db) {

        String sql = "CREATE TABLE " + TABLE_BILLS + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_MONTH + " TEXT NOT NULL, " +
                COL_UNITS + " INTEGER NOT NULL, " +
                COL_TOTAL + " REAL NOT NULL, " +
                COL_REBATE + " REAL NOT NULL, " +
                COL_FINAL + " REAL NOT NULL)";

        db.execSQL(sql);
    }

    // Upgrade Database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Database upgrade logic can be added when the table structure changes.
    }

    // Insert Bill Data
    public long insertBill(String month, int units,
                           double totalCharges, double rebate,
                           double finalCost) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(COL_MONTH, month);
        values.put(COL_UNITS, units);
        values.put(COL_TOTAL, totalCharges);
        values.put(COL_REBATE, rebate);
        values.put(COL_FINAL, finalCost);

        long result = db.insert(TABLE_BILLS, null, values);

        db.close();

        return result;
    }

    // Retrieve All Bills
    public Cursor getAllBills() {

        SQLiteDatabase db = this.getReadableDatabase();

        return db.query(
                TABLE_BILLS,
                null,
                null,
                null,
                null,
                null,
                COL_ID + " DESC"
        );
    }

    // Retrieve Bill by ID
    public Cursor getBillById(int id) {

        SQLiteDatabase db = this.getReadableDatabase();

        return db.query(
                TABLE_BILLS,
                null,
                COL_ID + " = ?",
                new String[]{String.valueOf(id)},
                null,
                null,
                null
        );
    }

    // Update Bill
    public int updateBill(int id, String month, int units,
                          double totalCharges, double rebate,
                          double finalCost) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(COL_MONTH, month);
        values.put(COL_UNITS, units);
        values.put(COL_TOTAL, totalCharges);
        values.put(COL_REBATE, rebate);
        values.put(COL_FINAL, finalCost);

        int result = db.update(
                TABLE_BILLS,
                values,
                COL_ID + " = ?",
                new String[]{String.valueOf(id)}
        );

        db.close();

        return result;
    }

    // Delete Bill
    public int deleteBill(int id) {

        SQLiteDatabase db = this.getWritableDatabase();

        int result = db.delete(
                TABLE_BILLS,
                COL_ID + " = ?",
                new String[]{String.valueOf(id)}
        );

        db.close();

        return result;
    }
}
