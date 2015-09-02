package com.zhack.poskasir.util;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

import com.zhack.poskasir.model.Item;
import com.zhack.poskasir.model.ItemGroup;
import com.zhack.poskasir.model.ReportSales;
import com.zhack.poskasir.model.Transaction;

public class ZhackProvider extends ContentProvider {

    public static final String TABLE_ITEM           = "item_table";
    public static final String TABLE_ITEMGROUP      = "itemgroup_table";
    public static final String TABLE_REPORTSALES    = "reportsales_table";
    public static final String TABLE_TRANSACTION    = "transaction_table";

    public static final String CONTENT_AUTHORITY = "com.zhack.poskasir";
    public static final Uri ITEM_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY).buildUpon().appendPath(TABLE_ITEM).build();
    public static final Uri ITEMGROUP_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY).buildUpon().appendPath(TABLE_ITEMGROUP).build();
    public static final Uri REPORTSALES_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY).buildUpon().appendPath(TABLE_REPORTSALES).build();
    public static final Uri TRANSACTION_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY).buildUpon().appendPath(TABLE_TRANSACTION).build();

    public class ZhackSQLiteOpenHelper extends SQLiteOpenHelper {

        private static final int ZHACK_DB_VERSION = 1;

        public ZhackSQLiteOpenHelper(Context context) {
            super(context, "Zhack", null, ZHACK_DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_ITEM + " ("
                    + Item.ITEM_ID + " TEXT PRIMARY KEY,"
                    + Item.ITEM_TITLE + " TEXT,"
                    + Item.ITEM_IMAGE + " TEXT,"
                    + Item.ITEM_CATEGORY + " TEXT,"
                    + Item.ITEM_PRICE + " TEXT,"
                    + "UNIQUE (" + Item.ITEM_ID + ") ON CONFLICT REPLACE)");
            db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_ITEMGROUP + " ("
                    + ItemGroup.ITEMGROUP_ID + " TEXT PRIMARY KEY,"
                    + ItemGroup.ITEMGROUP_TITLE + " TEXT,"
                    + "UNIQUE (" + ItemGroup.ITEMGROUP_ID + ") ON CONFLICT REPLACE)");
            db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_REPORTSALES + " ("
                    + ReportSales.ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + ReportSales.INVOICE + " TEXT,"
                    + ReportSales.PRICE + " INTEGER,"
                    + ReportSales.PAY + " INTEGER,"
                    + ReportSales.DATE + " TEXT,"
                    + ReportSales.STATUS + " TEXT,"
                    + ReportSales.POS_DATA + " TEXT,"
                    + "UNIQUE (" + ReportSales.ID + ") ON CONFLICT REPLACE)");
            db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_TRANSACTION + " ("
                    + Transaction.TRANS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + Transaction.TRANS_INVOICE + " TEXT,"
                    + Transaction.TRANS_DATE + " TEXT,"
                    + Transaction.TRANS_PRICE + " TEXT,"
                    + Transaction.TRANS_TAX + " TEXT,"
                    + "UNIQUE (" + Transaction.TRANS_ID + ") ON CONFLICT REPLACE)");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_ITEM);
            db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_ITEM + " ("
                    + Item.ITEM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + Item.ITEM_TITLE + " TEXT,"
                    + Item.ITEM_IMAGE + " TEXT,"
                    + Item.ITEM_CATEGORY + " TEXT,"
                    + Item.ITEM_PRICE + " TEXT,"
                    + "UNIQUE (" + Item.ITEM_ID + ") ON CONFLICT REPLACE)");
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_ITEMGROUP);
            db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_ITEMGROUP + " ("
                    + ItemGroup.ITEMGROUP_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + ItemGroup.ITEMGROUP_TITLE + " TEXT,"
                    + "UNIQUE (" + ItemGroup.ITEMGROUP_ID + ") ON CONFLICT REPLACE)");
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_REPORTSALES);
            db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_REPORTSALES + " ("
                    + ReportSales.ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + ReportSales.INVOICE + " TEXT,"
                    + ReportSales.PRICE + " INTEGER,"
                    + ReportSales.PAY + " INTEGER,"
                    + ReportSales.DATE + " TEXT,"
                    + ReportSales.STATUS + " TEXT,"
                    + ReportSales.POS_DATA + " TEXT,"
                    + "UNIQUE (" + ReportSales.ID + ") ON CONFLICT REPLACE)");
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRANSACTION);
            db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_TRANSACTION + " ("
                    + Transaction.TRANS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + Transaction.TRANS_INVOICE + " TEXT,"
                    + Transaction.TRANS_DATE + " TEXT,"
                    + Transaction.TRANS_PRICE + " TEXT,"
                    + Transaction.TRANS_TAX + " TEXT,"
                    + "UNIQUE (" + Transaction.TRANS_ID + ") ON CONFLICT REPLACE)");
        }
    }

    private static final UriMatcher S_URI_MATCHER = buildUriMatcher();
    private static final int MATCH_ITEM         = 101;
    private static final int MATCH_ITEMGROUP    = 102;
    private static final int MATCH_REPORTSALES  = 103;
    private static final int MATCH_TRANSACTION  = 104;

    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

        matcher.addURI(CONTENT_AUTHORITY, TABLE_ITEM, MATCH_ITEM);
        matcher.addURI(CONTENT_AUTHORITY, TABLE_ITEMGROUP, MATCH_ITEMGROUP);
        matcher.addURI(CONTENT_AUTHORITY, TABLE_REPORTSALES, MATCH_REPORTSALES);
        matcher.addURI(CONTENT_AUTHORITY, TABLE_TRANSACTION, MATCH_TRANSACTION);

        return matcher;
    }

    private ZhackSQLiteOpenHelper openHelper;

    @Override
    public boolean onCreate() {
        // TODO Auto-generated method stub
        openHelper = new ZhackSQLiteOpenHelper(getContext());

        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] columns, String selection, String[] selectionArgs, String sort) {
        // Query result
        int match = S_URI_MATCHER.match(uri);
        switch (match) {
            case MATCH_ITEM: {
                SQLiteDatabase db = openHelper.getReadableDatabase();

                if (sort == null) {
                    sort = Item.ITEM_ID + " ASC";
                }
                return db.query(TABLE_ITEM, columns, selection, selectionArgs, null, null, sort);
            }
            case MATCH_ITEMGROUP: {
                SQLiteDatabase db = openHelper.getReadableDatabase();

                if (sort == null) {
                    sort = ItemGroup.ITEMGROUP_ID + " ASC";
                }
                return db.query(TABLE_ITEMGROUP, columns, selection, selectionArgs, null, null, sort);
            }
            case MATCH_REPORTSALES: {
                SQLiteDatabase db = openHelper.getReadableDatabase();

                if (sort == null) {
                    sort = ReportSales.ID + " ASC";
                }
                return db.query(TABLE_REPORTSALES, columns, selection, selectionArgs, null, null, sort);
            }
            case MATCH_TRANSACTION: {
                SQLiteDatabase db = openHelper.getReadableDatabase();

                if (sort == null) {
                    sort = Transaction.TRANS_ID + " ASC";
                }
                return db.query(TABLE_TRANSACTION, columns, selection, selectionArgs, null, null, sort);
            }
        }

        return null;
    }

    @Override
    public String getType(Uri uri) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // Insert value
        int match = S_URI_MATCHER.match(uri);
        switch (match) {
            case MATCH_ITEM: {
                SQLiteDatabase db = openHelper.getWritableDatabase();

                db.beginTransaction();
                db.insert(TABLE_ITEM, null, values);
                db.setTransactionSuccessful();
                db.endTransaction();

                String itemId = values.getAsString(Item.ITEM_ID);

                return ITEM_CONTENT_URI.buildUpon().appendPath(itemId).build();
            }
            case MATCH_ITEMGROUP: {
                SQLiteDatabase db = openHelper.getWritableDatabase();

                db.beginTransaction();
                db.insert(TABLE_ITEMGROUP, null, values);
                db.setTransactionSuccessful();
                db.endTransaction();

                String itemGroupId = values.getAsString(ItemGroup.ITEMGROUP_ID);

                return ITEMGROUP_CONTENT_URI.buildUpon().appendPath(itemGroupId).build();
            }
            case MATCH_REPORTSALES: {
                SQLiteDatabase db = openHelper.getWritableDatabase();

                db.beginTransaction();
                db.insert(TABLE_REPORTSALES, null, values);
                db.setTransactionSuccessful();
                db.endTransaction();

                String reportID = values.getAsString(ReportSales.ID);

                return REPORTSALES_CONTENT_URI.buildUpon().appendPath(reportID).build();
            }
            case MATCH_TRANSACTION: {
                SQLiteDatabase db = openHelper.getWritableDatabase();

                db.beginTransaction();
                db.insert(TABLE_TRANSACTION, null, values);
                db.setTransactionSuccessful();
                db.endTransaction();

                String transId = values.getAsString(Transaction.TRANS_ID);

                return TRANSACTION_CONTENT_URI.buildUpon().appendPath(transId).build();
            }
        }

        return null;
    }


    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Delete value
        int match = S_URI_MATCHER.match(uri);
        switch (match) {
            case MATCH_ITEM: {
                SQLiteDatabase db = openHelper.getWritableDatabase();

                db.beginTransaction();
                db.delete(TABLE_ITEM, selection, selectionArgs);
                db.setTransactionSuccessful();
                db.endTransaction();

                return 0;
            }
            case MATCH_ITEMGROUP: {
                SQLiteDatabase db = openHelper.getWritableDatabase();

                db.beginTransaction();
                db.delete(TABLE_ITEMGROUP, selection, selectionArgs);
                db.setTransactionSuccessful();
                db.endTransaction();

                return 0;
            }
            case MATCH_REPORTSALES: {
                SQLiteDatabase db = openHelper.getWritableDatabase();

                db.beginTransaction();
                db.delete(TABLE_REPORTSALES, selection, selectionArgs);
                db.setTransactionSuccessful();
                db.endTransaction();

                return 0;
            }
            case MATCH_TRANSACTION: {
                SQLiteDatabase db = openHelper.getWritableDatabase();

                db.beginTransaction();
                db.delete(TABLE_TRANSACTION, selection, selectionArgs);
                db.setTransactionSuccessful();
                db.endTransaction();

                return 0;
            }
        }

        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // TODO Auto-generated method stub
        int match = S_URI_MATCHER.match(uri);
        switch (match) {
            case MATCH_ITEM: {
                SQLiteDatabase db = openHelper.getWritableDatabase();

                return db.update(TABLE_ITEM, values, selection, selectionArgs);
            }
            case MATCH_ITEMGROUP: {
                SQLiteDatabase db = openHelper.getWritableDatabase();

                return db.update(TABLE_ITEMGROUP, values, selection, selectionArgs);
            }
            case MATCH_REPORTSALES: {
                SQLiteDatabase db = openHelper.getWritableDatabase();

                return db.update(TABLE_REPORTSALES, values, selection, selectionArgs);
            }
            case MATCH_TRANSACTION: {
                SQLiteDatabase db = openHelper.getWritableDatabase();

                return db.update(TABLE_TRANSACTION, values, selection, selectionArgs);
            }
        }

        return 0;
    }
}
