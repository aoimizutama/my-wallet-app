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
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class DeleteMethodActivity extends AppCompatActivity {

    // データベースヘルパーオブジェクト
    private DatabaseHelper _helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_method);

        // DBヘルパーオブジェクトを生成
        _helper = new DatabaseHelper(DeleteMethodActivity.this);

        // スピナーを作成
        // データベースヘルパーオブジェクトからデータベース接続オブジェクトを取得
        SQLiteDatabase db = _helper.getWritableDatabase();
        // SimpleAdapterで使用するListオブジェクトを用意
        List<String> deleteMethodCategorylist = new ArrayList<>();
        // 検索SQL文字列の用意
        String sql = "SELECT * FROM account_method";
        // SQLの実行
        Cursor cursor = db.rawQuery(sql, null);
        // SQLの戻り値であるカーソルオブジェクトをループさせてデータベース内のデータを取得
        while(cursor.moveToNext()) {
            // カラムのインデックス値を取得
            int idxMethodName = cursor.getColumnIndex("method_name");
            // カラムのインデックス値を元に実際のデータを取得
            String methodName = cursor.getString(idxMethodName);
            deleteMethodCategorylist.add(methodName);
        }
        // スピナーを作成
        Spinner spDeleteMethod = findViewById(R.id.spDeleteMethod);
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, deleteMethodCategorylist);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spDeleteMethod.setAdapter(adapter);

        // [削除する]ボタンのリスナ設定
        Button btDeleteMethod = findViewById(R.id.btDeleteMethod);
        DeleteMethodActivity.DeleteMethodListener deleteMethodListener = new DeleteMethodActivity.DeleteMethodListener();
        btDeleteMethod.setOnClickListener(deleteMethodListener);

        // [キャンセル]ボタンのリスナ設定
        Button btCancelDeleteMethod = findViewById(R.id.btCancelDeleteMethod);
        DeleteMethodActivity.CancelDeleteMethodListener cancelDeleteMethodListener = new DeleteMethodActivity.CancelDeleteMethodListener();
        btCancelDeleteMethod.setOnClickListener(cancelDeleteMethodListener);
    }

    @Override
    protected void onDestroy() {
        _helper.close();
        super.onDestroy();
    }

    // [削除する]ボタンをクリックしたときのリスナクラス
    private class DeleteMethodListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            Spinner spDeleteMethod = findViewById(R.id.spDeleteMethod);
            // 選択されているアイテムを取得
            String deleteMethod = (String)spDeleteMethod.getSelectedItem();

            // データベースヘルパーオブジェクトからデータベース接続オブジェクトを取得
            SQLiteDatabase db = _helper.getWritableDatabase();
            // 削除用SQL文字列の用意
            String sql2 = "DELETE FROM account_method WHERE method_name = ?";
            // SQL文字列を元にプリベアードステートメントを取得
            SQLiteStatement stmt = db.compileStatement(sql2);
            // 変数のバインド
            stmt.bindString(1, deleteMethod);
            // 削除SQLの実行
            stmt.executeUpdateDelete();

            // トーストで表示する文字列
            String show = "削除しました！";
            // トーストの表示
            Toast.makeText(DeleteMethodActivity.this, show, Toast.LENGTH_LONG).show();

            // インテントオブジェクトを生成
            Intent intent2 = new Intent(DeleteMethodActivity.this, HomeActivity.class);
            // [HOME]画面に遷移
            startActivity(intent2);
        }
    }

    // [キャンセル]ボタンをクリックしたときのリスナクラス
    private class CancelDeleteMethodListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            // トーストで表示する文字列
            String show = "キャンセルしました！";
            // トーストの表示
            Toast.makeText(DeleteMethodActivity.this, show, Toast.LENGTH_LONG).show();

            // インテントオブジェクトを生成
            Intent intent3 = new Intent(DeleteMethodActivity.this, HomeActivity.class);
            // [HOME]画面に遷移
            startActivity(intent3);
        }
    }
}