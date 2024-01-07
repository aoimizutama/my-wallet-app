package com.example.householdaccountbookapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class CheckPswActivity extends AppCompatActivity {

    // データベースヘルパーオブジェクト
    private DatabaseHelper _helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_psw);

        // DBヘルパーオブジェクトを生成
        _helper = new DatabaseHelper(CheckPswActivity.this);

        // [パスワードを確認する]ボタンのリスナ設定
        Button btCheckPsw = findViewById(R.id.btCheckPsw);
        CheckPswActivity.CheckPswListener checkPswListener = new CheckPswActivity.CheckPswListener();
        btCheckPsw.setOnClickListener(checkPswListener);
    }

    @Override
    protected void onDestroy() {
        _helper.close();
        super.onDestroy();
    }

    // [パスワードを確認する]ボタンをクリックしたときのリスナクラス
    private class CheckPswListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            // 入力されたユーザ名を取得
            EditText etUserName2 = findViewById(R.id.etUserName2);
            String userName2 = etUserName2.getText().toString();

            // データベースヘルパーオブジェクトからデータベース接続オブジェクトを取得
            SQLiteDatabase db = _helper.getWritableDatabase();
            // 検索SQL文字列の用意
            String sql = "SELECT * FROM user WHERE username = ?";
            // SQLの実行
            String[] params = {userName2};
            Cursor cursor = db.rawQuery(sql, params);
            // データベースから取得した値を格納する変数の用意、データがなかったときのための初期値も用意
            String psw = "";
            // SQLの戻り値であるカーソルオブジェクトをループさせてデータベース内のデータを取得
            while(cursor.moveToNext()) {
                // カラムのインデックス値を取得
                int idxPsw = cursor.getColumnIndex("psw");
                // カラムのインデックス値を元に実際のデータを取得
                psw = cursor.getString(idxPsw);
            }

            if(psw.equals("")) {
                // トーストで表示する文字列
                String show = "そのユーザ名は登録されていません！\nログイン画面に戻り、新規登録からお願いします。";
                // トーストの表示
                Toast.makeText(CheckPswActivity.this, show, Toast.LENGTH_LONG).show();
            } else {
                // ユーザのパスワードを表示
                TextView tvUserPsw = findViewById(R.id.tvUserPsw);
                tvUserPsw.setText("あなたのパスワード：" + psw);
                // トーストで表示する文字列
                String show = "ログイン画面に戻り、このパスワードでログインしてください。";
                // トーストの表示
                Toast.makeText(CheckPswActivity.this, show, Toast.LENGTH_LONG).show();
            }
        }
    }
}