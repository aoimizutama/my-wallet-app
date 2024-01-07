package com.example.householdaccountbookapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

public class DatabaseHelper extends SQLiteOpenHelper {
    // データベースファイル名の定数フィールド
    private static final String DATABASE_NAME = "HouseholdAccountBook.db";
    // バージョン情報の定数フィールド
    private static final int DATABASE_VERSION = 1;

    // コンストラクタ
    public DatabaseHelper(Context context) {
        // 親クラスのコンストラクタの呼び出し
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // テーブル作成用SQL文字列の作成
        StringBuilder sb1 = new StringBuilder();
        sb1.append("CREATE TABLE user(");
        sb1.append("_id INTEGER PRIMARY KEY,");
        sb1.append("username TEXT NOT NULL,");
        sb1.append("psw TEXT NOT NULL UNIQUE");
        sb1.append(");");
        String sql1 = sb1.toString();
        // SQLの実行
        db.execSQL(sql1);

        // テーブル作成用SQL文字列の作成
        StringBuilder sb2 = new StringBuilder();
        sb2.append("CREATE TABLE account_method(");
        sb2.append("_id INTEGER PRIMARY KEY AUTOINCREMENT,");
        sb2.append("method_category_id INTEGER NOT NULL,");
        sb2.append("method_name TEXT NOT NULL UNIQUE,");
        sb2.append("balance INTEGER NOT NULL");
        sb2.append(");");
        String sql2 = sb2.toString();
        // SQLの実行
        db.execSQL(sql2);

        // テーブル作成用SQL文字列の作成
        StringBuilder sb3 = new StringBuilder();
        sb3.append("CREATE TABLE account_data(");
        sb3.append("_id INTEGER PRIMARY KEY,");
        sb3.append("method_id INTEGER NOT NULL,");
        sb3.append("date TEXT NOT NULL,");
        sb3.append("howuse TEXT NOT NULL,");
        sb3.append("price INTEGER NOT NULL,");
        sb3.append("money_category_id INTEGER NOT NULL,");
        sb3.append("memo TEXT");
        sb3.append(");");
        String sql3 = sb3.toString();
        // SQLの実行
        db.execSQL(sql3);

        // テーブル作成用SQL文字列の作成
        StringBuilder sb4 = new StringBuilder();
        sb4.append("CREATE TABLE method_category(");
        sb4.append("_id INTEGER PRIMARY KEY,");
        sb4.append("method_category_name TEXT NOT NULL");
        sb4.append(");");
        String sql4 = sb4.toString();
        // SQLの実行
        db.execSQL(sql4);

        // テーブル作成用SQL文字列の作成
        StringBuilder sb5 = new StringBuilder();
        sb5.append("CREATE TABLE money_category(");
        sb5.append("_id INTEGER PRIMARY KEY,");
        sb5.append("money_category_name TEXT NOT NULL");
        sb5.append(");");
        String sql5 = sb5.toString();
        // SQLの実行
        db.execSQL(sql5);

        // インサート用SQL文字列の用意
        String sql6 = "INSERT INTO method_category(method_category_name) " +
                "VALUES(?), (?), (?), (?), (?);";
        // SQL文字列を元にプリベアードステートメントを取得
        SQLiteStatement stmt = db.compileStatement(sql6);
        // 変数のバインド
        stmt.bindString(1, "お財布");
        stmt.bindString(2, "銀行");
        stmt.bindString(3, "クレジットカード");
        stmt.bindString(4, "電子マネー");
        stmt.bindString(5, "資産");
        // インサートSQLの実行
        stmt.executeInsert();

        // インサート用SQL文字列の用意
        String sql7 = "INSERT INTO money_category(money_category_name) " +
                "VALUES(?), (?), (?), (?), (?);";
        // SQL文字列を元にプリベアードステートメントを取得
        SQLiteStatement stmt2 = db.compileStatement(sql7);
        // 変数のバインド
        stmt2.bindString(1, "食費");
        stmt2.bindString(2, "光熱費");
        stmt2.bindString(3, "住宅費");
        stmt2.bindString(4, "その他");
        stmt2.bindString(5, "振替");
        // インサートSQLの実行
        stmt2.executeInsert();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
