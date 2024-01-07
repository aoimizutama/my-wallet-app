package com.example.householdaccountbookapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class AddMethodActivity extends AppCompatActivity {

    // データベースヘルパーオブジェクト
    private DatabaseHelper _helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_method);

        // DBヘルパーオブジェクトを生成
        _helper = new DatabaseHelper(AddMethodActivity.this);

        // SimpleAdapterで使用するListオブジェクトを用意
        List<String> methodCategorylist = new ArrayList<>();
        // データベースヘルパーオブジェクトからデータベース接続オブジェクトを取得
        SQLiteDatabase db = _helper.getWritableDatabase();
        // 検索SQL文字列の用意
        String sql2 = "SELECT * FROM method_category";
        // SQLの実行
        Cursor cursor = db.rawQuery(sql2, null);
        // SQLの戻り値であるカーソルオブジェクトをループさせてデータベース内のデータを取得
        while(cursor.moveToNext()) {
            // カラムのインデックス値を取得
            int idxMethodCategoryName = cursor.getColumnIndex("method_category_name");
            // カラムのインデックス値を元に実際のデータを取得
            String methodCategoryName = cursor.getString(idxMethodCategoryName);
            methodCategorylist.add(methodCategoryName);
        }
        // スピナーを作成
        Spinner spMethodCategory = findViewById(R.id.spMethodCategory);
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, methodCategorylist);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spMethodCategory.setAdapter(adapter);

        // [追加する]ボタンのリスナ設定
        Button btAddMethod = findViewById(R.id.btAddMethod);
        AddMethodActivity.AddMethodListener addMethodListener = new AddMethodActivity.AddMethodListener();
        btAddMethod.setOnClickListener(addMethodListener);
    }

    @Override
    protected void onDestroy() {
        _helper.close();
        super.onDestroy();
    }

    // [処理方法を追加]ボタンをクリックしたときのリスナクラス
    private class AddMethodListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            // 入力された値を取得
            Spinner spMethodCategory = findViewById(R.id.spMethodCategory);
            int methodCategory = spMethodCategory.getSelectedItemPosition();    // 選択されたカテゴリの番号を取得
            EditText etSetMethodName = findViewById(R.id.etSetMethodName);
            String methodName = etSetMethodName.getText().toString();
            EditText etSetBalance = findViewById(R.id.etSetBalance);
            String strSetBalance = etSetBalance.getText().toString();
            int balance = Integer.parseInt(strSetBalance);

            // データベースヘルパーオブジェクトからデータベース接続オブジェクトを取得
            SQLiteDatabase db = _helper.getWritableDatabase();
            // インサート用SQL文字列の用意
            String sql = "INSERT INTO account_method(method_category_id, method_name, balance) " +
                    "VALUES(?, ?, ?)";
            // SQL文字列を元にプリベアードステートメントを取得
            SQLiteStatement stmt = db.compileStatement(sql);
            // 変数のバインド
            stmt.bindLong(1, methodCategory);
            stmt.bindString(2, methodName);
            stmt.bindLong(3, balance);
            // インサートSQLの実行
            stmt.executeInsert();

            // トーストで表示する文字列
            String show = "追加しました！";
            // トーストの表示
            Toast.makeText(AddMethodActivity.this, show, Toast.LENGTH_LONG).show();

            // [HOME]画面に遷移
            Intent intent = new Intent(AddMethodActivity.this, HomeActivity.class);
            startActivity(intent);
        }
    }
}