package com.example.householdaccountbookapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SignupActivity extends AppCompatActivity {

    // データベースヘルパーオブジェクト
    private DatabaseHelper _helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // DBヘルパーオブジェクトを生成
        _helper = new DatabaseHelper(SignupActivity.this);

        // [新規登録]ボタンのリスナ設定
        Button btSignup = findViewById(R.id.btSignup);
        SignupActivity.SignupListener signupListener = new SignupActivity.SignupListener();
        btSignup.setOnClickListener(signupListener);
    }

    @Override
    protected void onDestroy() {
        _helper.close();
        super.onDestroy();
    }

    // [新規登録]ボタンをクリックしたときのリスナクラス
    private class SignupListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            // 入力された値を取得
            EditText etUserName = findViewById(R.id.etUserName);
            String userName = etUserName.getText().toString();
            EditText etPsw2 = findViewById(R.id.etPsw2);
            String psw2 = etPsw2.getText().toString();
            EditText etMainBalance = findViewById(R.id.etMainBalance);
            String strMainBalance = etMainBalance.getText().toString();
            int mainBalance = Integer.parseInt(strMainBalance);

            // データベースヘルパーオブジェクトからデータベース接続オブジェクトを取得
            SQLiteDatabase db = _helper.getWritableDatabase();
            // インサート用SQL文字列の用意
            String sql = "INSERT INTO user(username, psw) VALUES(?, ?)";
            // SQL文字列を元にプリベアードステートメントを取得
            SQLiteStatement stmt = db.compileStatement(sql);
            // 変数のバインド
            stmt.bindString(1, userName);
            stmt.bindString(2, psw2);
            // インサートSQLの実行
            stmt.executeInsert();

            // インサート用SQL文字列の用意
            String sql2 = "INSERT INTO account_method(method_category_id, method_name, balance) " +
                    "VALUES (?, ?, ?)";
            // SQL文字列を元にプリベアードステートメントを取得
            SQLiteStatement stmt2 = db.compileStatement(sql2);
            // 変数のバインド
            stmt2.bindLong(1, 1);
            stmt2.bindString(2, "お財布");
            stmt2.bindLong(3, mainBalance);
            // インサートSQLの実行
            stmt2.executeInsert();

            // トーストで表示する文字列
            String show = "登録しました！";
            // トーストの表示
            Toast.makeText(SignupActivity.this, show, Toast.LENGTH_LONG).show();
            // [Log In]画面に遷移
            Intent intent = new Intent(SignupActivity.this, MainActivity.class);
            startActivity(intent);
        }
    }
}