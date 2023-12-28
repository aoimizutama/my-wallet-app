// 自分用の家計簿アプリ（複数人での使用はしない前提）
// エラー処理などはまだ考慮していない（項目への入力がなかったときのエラー処理など）

package com.example.householdaccountbookapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    // データベースヘルパーオブジェクト
    private DatabaseHelper _helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // DBヘルパーオブジェクトを生成
        _helper = new DatabaseHelper(MainActivity.this);

        // [ログイン]ボタンのリスナ設定
        Button btLogin = findViewById(R.id.btLogin);
        LoginListener loginListener = new LoginListener();
        btLogin.setOnClickListener(loginListener);

        // [パスワード確認]ボタンのリスナ設定
        Button btToCheckPsw = findViewById(R.id.btToCheckPsw);
        ToCheckPswListener toCheckPswListener = new ToCheckPswListener();
        btToCheckPsw.setOnClickListener(toCheckPswListener);

        // [新規登録]ボタンのリスナ設定
        Button btToSignup = findViewById(R.id.btToSignup);
        ToSignupListener toSignupListener = new ToSignupListener();
        btToSignup.setOnClickListener(toSignupListener);
    }

    @Override
    protected void onDestroy() {
        _helper.close();
        super.onDestroy();
    }

    // [ログイン]ボタンをクリックしたときのリスナクラス
    private class LoginListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            // 入力されたパスワードを取得
            EditText etPsw = findViewById(R.id.etPsw);
            String psw = etPsw.getText().toString();

            // データベースヘルパーオブジェクトからデータベース接続オブジェクトを取得
            SQLiteDatabase db = _helper.getWritableDatabase();
            // 検索SQL文字列の用意
            String sql = "SELECT * FROM user WHERE psw = ?";
            // SQLの実行
            String[] params = {psw};
            Cursor cursor = db.rawQuery(sql, params);
            // データベースから取得した値を格納する変数の用意、データがなかったときのための初期値も用意
            String userName = "";
            // SQLの戻り値であるカーソルオブジェクトをループさせてデータベース内のデータを取得
            while(cursor.moveToNext()) {
                // カラムのインデックス値を取得
                int idxUsername = cursor.getColumnIndex("username");
                // カラムのインデックス値を元に実際のデータを取得
                userName = cursor.getString(idxUsername);
            }

            if(userName.equals("")) {
                // トーストで表示する文字列
                String show = "パスワードが違います！\nパスワード確認ボタンから正しいパスワードを確認してください。";
                // トーストの表示
                Toast.makeText(MainActivity.this, show, Toast.LENGTH_LONG).show();
            } else {
                // [HOME]画面に遷移
                Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        }
    }

    // [パスワード確認]ボタンをクリックしたときのリスナクラス
    private class ToCheckPswListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            // [パスワード確認]画面に遷移
            Intent intent = new Intent(MainActivity.this, CheckPswActivity.class);
            startActivity(intent);
        }
    }

    // [新規登録]ボタンをクリックしたときのリスナクラス
    private class ToSignupListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            // [新規登録]画面に遷移
            Intent intent = new Intent(MainActivity.this, SignupActivity.class);
            startActivity(intent);
        }
    }
}