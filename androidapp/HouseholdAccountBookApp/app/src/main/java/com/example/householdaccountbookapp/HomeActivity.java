package com.example.householdaccountbookapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeActivity extends AppCompatActivity {

    // データベースヘルパーオブジェクト
    private DatabaseHelper _helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // DBヘルパーオブジェクトを生成
        _helper = new DatabaseHelper(HomeActivity.this);

        // データベースヘルパーオブジェクトからデータベース接続オブジェクトを取得
        SQLiteDatabase db = _helper.getWritableDatabase();
        // 検索SQL文字列の用意
        String sql = "SELECT * FROM user WHERE _id = 1";
        // SQLの実行
        Cursor cursor = db.rawQuery(sql, null);
        // データベースから取得した値を格納する変数の用意、データがなかったときのための初期値も用意
        String userName = "";
        // SQLの戻り値であるカーソルオブジェクトをループさせてデータベース内のデータを取得
        while(cursor.moveToNext()) {
            // カラムのインデックス値を取得
            int idxUsername = cursor.getColumnIndex("username");
            // カラムのインデックス値を元に実際のデータを取得
            userName = cursor.getString(idxUsername);
            // ホームメッセージを表示
            TextView tvHomeMessage = findViewById(R.id.tvHomeMessage);
            tvHomeMessage.setText("こんにちは、" + userName + "さん！");
        }

        // 検索SQL文字列の用意
        String sql2 = "SELECT * FROM account_method WHERE _id = 1";
        // SQLの実行
        cursor = db.rawQuery(sql2, null);
        // データベースから取得した値を格納する変数の用意、データがなかったときのための初期値も用意
        String mainBalance = "";
        // SQLの戻り値であるカーソルオブジェクトをループさせてデータベース内のデータを取得
        while(cursor.moveToNext()) {
            // カラムのインデックス値を取得
            int idxBalance = cursor.getColumnIndex("balance");
            // カラムのインデックス値を元に実際のデータを取得
            mainBalance = cursor.getString(idxBalance);
            // お財布残高を表示
            EditText etMainMethod = findViewById(R.id.etMainMethod);
            etMainMethod.setText(mainBalance);
        }

        // 画面部品ListViewを取得
        ListView lvMethodCategory = findViewById(R.id.lvMethodCategory);
        // SimpleAdapterで使用するListオブジェクトを用意
        List<Map<String, String>> methodCategoryList = new ArrayList<>();

        // 主キーによる検索SQL文字列の用意
        String sql3 = "SELECT * FROM account_method";
        // セレクトSQLの実行
        cursor = db.rawQuery(sql3, null);
        // SQL実行の戻り値であるカーソルオブジェクトをループさせてデータベース内のデータを取得
        while(cursor.moveToNext()) {
            // 各カラムのデータを取得
            int idxMethodCategoryId = cursor.getColumnIndex("method_category_id");
            String methodCategoryId = cursor.getString(idxMethodCategoryId);
            int idxMethodName = cursor.getColumnIndex("method_name");
            String methodName = cursor.getString(idxMethodName);
            int idxBalance = cursor.getColumnIndex("balance");
            String balance = cursor.getString(idxBalance);

            // データを格納するMapオブジェクトの用意とmethodCategoryListへのデータ登録
            Map<String, String> methodCategory = new HashMap<>();
            methodCategory.put("methodName", methodName);
            methodCategory.put("balance", balance);
            methodCategoryList.add(methodCategory);
        }
        // SimpleAdapter第4引数from用データの用意
        String[] from = {"methodName", "balance"};
        // SimpleAdapter第5引数to用データの用意
        int[] to = {android.R.id.text1, android.R.id.text2};
        // SimpleAdapter生成
        SimpleAdapter adapter = new SimpleAdapter(HomeActivity.this, methodCategoryList,
                android.R.layout.simple_list_item_2, from, to);
        // アダプタ登録
        lvMethodCategory.setAdapter(adapter);

        // [処理方法を追加]ボタンのリスナ設定
        Button btToAddMethod = findViewById(R.id.btToAddMethod);
        HomeActivity.ToAddMethodListener toAddMethodListener = new HomeActivity.ToAddMethodListener();
        btToAddMethod.setOnClickListener(toAddMethodListener);

        // [処理方法を削除]ボタンのリスナ設定
        Button btToDeleteMethod = findViewById(R.id.btToDeleteMethod);
        HomeActivity.ToDeleteMethodListener toDeleteMethodListener = new HomeActivity.ToDeleteMethodListener();
        btToDeleteMethod.setOnClickListener(toDeleteMethodListener);

        // リストタップのリスナクラス登録
        lvMethodCategory.setOnItemClickListener(new ListItemClickListener());
    }

    @Override
    protected void onDestroy() {
        _helper.close();
        super.onDestroy();
    }

    // [処理方法を追加]ボタンをクリックしたときのリスナクラス
    private class ToAddMethodListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            // [新たな処理方法]画面に遷移
            Intent intent = new Intent(HomeActivity.this, AddMethodActivity.class);
            startActivity(intent);
        }
    }

    // [処理方法を削除]ボタンをクリックしたときのリスナクラス
    private class ToDeleteMethodListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            // [処理方法を削除]画面に遷移
            Intent intent = new Intent(HomeActivity.this, DeleteMethodActivity.class);
            startActivity(intent);
        }
    }

    // リストがタップされたときの処理が記述されたメンバクラス
    private class ListItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            // タップされた行のデータを取得
            Map<String, String> item = (Map<String, String>) parent.getItemAtPosition(position);
            // 処理方法名と残高を取得
            String methodName = item.get("methodName");
            String balance = item.get("balance");
            // インテントオブジェクトを生成
            Intent intent = new Intent(HomeActivity.this, HistoryActivity.class);
            // [履歴]画面に送るデータを格納
            intent.putExtra("methodName", methodName);
            intent.putExtra("balance", balance);
            // [履歴]画面の起動
            startActivity(intent);
        }
    }
}