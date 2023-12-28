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
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HistoryActivity extends AppCompatActivity {

    // データベースヘルパーオブジェクト
    private DatabaseHelper _helper;

    // 選択されたメソッド名を表すフィールド
    private String _methodName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_histry);

        // DBヘルパーオブジェクトを生成
        _helper = new DatabaseHelper(HistoryActivity.this);

        // インテントオブジェクトを取得
        Intent intent = getIntent();
        // [HOME]画面から渡されたデータを取得
        _methodName = intent.getStringExtra("methodName");
        String balance = intent.getStringExtra("balance");
        // メソッド名と残高を[履歴]画面に表示
        TextView tvHistoryTitle = findViewById(R.id.tvHistoryTitle);
        tvHistoryTitle.setText(_methodName + " 履歴");
        EditText etBalance = findViewById(R.id.etBalance);
        etBalance.setText(balance);

        // 画面部品ListViewを取得
        ListView lvHistory = findViewById(R.id.lvHistory);
        // SimpleAdapterで使用するListオブジェクトを用意
        List<Map<String, String>> historyList = new ArrayList<>();

        // データベースヘルパーオブジェクトからデータベース接続オブジェクトを取得
        SQLiteDatabase db = _helper.getWritableDatabase();
        // 検索SQL文字列の用意
        String sql = "SELECT * FROM account_method WHERE method_name = ?";
        // SQLの実行
        String[] params = {_methodName};
        Cursor cursor = db.rawQuery(sql, params);
        // データベースから取得した値を格納する変数の用意、データがなかったときのための初期値も用意
        int methodId = -1;
        // SQLの戻り値であるカーソルオブジェクトをループさせてデータベース内のデータを取得
        while(cursor.moveToNext()) {
            // カラムのインデックス値を取得
            int idxId = cursor.getColumnIndex("_id");
            // カラムのインデックス値を元に実際のデータを取得
            String strMethodId = cursor.getString(idxId);
            methodId = Integer.parseInt(strMethodId);
        }

        // MoneyCategoryをマップとして保存
        Map<Integer, String> moneyCategoryIdMap = new HashMap<>();
        // 検索SQL文字列の用意
        String sql3 = "SELECT * FROM money_category";
        // SQLの実行
        cursor = db.rawQuery(sql3, null);
        // データベースから取得した値を格納する変数の用意、データがなかったときのための初期値も用意
        int moneyCategoryId = -1;
        // SQLの戻り値であるカーソルオブジェクトをループさせてデータベース内のデータを取得
        while(cursor.moveToNext()) {
            // カラムのインデックス値を取得
            int idxMoneyCategoryId = cursor.getColumnIndex("_id");
            int idxMoneyCategoryName = cursor.getColumnIndex("money_category_name");
            // カラムのインデックス値を元に実際のデータを取得
            String strMoneyCategoryId = cursor.getString(idxMoneyCategoryId);
            moneyCategoryId = Integer.parseInt(strMoneyCategoryId);
            String moneyCategoryName = cursor.getString(idxMoneyCategoryName);
            moneyCategoryIdMap.put(moneyCategoryId, moneyCategoryName);
        }

        // 主キーによる検索SQL文字列の用意
        String sql2 = "SELECT * FROM account_data WHERE method_id = ? ORDER BY date DESC";
        // SQLの実行
        String[] params2 = {String.valueOf(methodId)};
        cursor = db.rawQuery(sql2, params2);
        // SQL実行の戻り値であるカーソルオブジェクトをループさせてデータベース内のデータを取得
        while(cursor.moveToNext()) {
            // データを取得
            int idxSelectId = cursor.getColumnIndex("_id");
            String strSelectId = cursor.getString(idxSelectId);
            int idxDate = cursor.getColumnIndex("date");
            String date = cursor.getString(idxDate);
            int idxHowUse = cursor.getColumnIndex("howuse");
            String howUse = cursor.getString(idxHowUse);
            int idxPrice = cursor.getColumnIndex("price");
            String price = cursor.getString(idxPrice);
            int idxMoneyCategoryId = cursor.getColumnIndex("money_category_id");
            String strMoneyCategoryId = cursor.getString(idxMoneyCategoryId);
            int moneyCategoryId2 = Integer.parseInt(strMoneyCategoryId);
            String moneyCategory = "";
            if (howUse.equals("収入") || howUse.equals("支出")) {    // 収入・支出の場合
                for(Map.Entry<Integer, String> entry : moneyCategoryIdMap.entrySet()) {
                    if (entry.getKey() == moneyCategoryId2 + 1) {
                        moneyCategory = entry.getValue();
                    }
                }
            } else if (howUse.equals("振替先") || howUse.equals("振替元")) {    // 振替の場合
                moneyCategory = "振替";
            } else {
                moneyCategory = "クレジット処理";
            }
            int idxMemo = cursor.getColumnIndex("memo");
            String memo = cursor.getString(idxMemo);

            // データを格納するMapオブジェクトの用意とhistoryListへのデータ登録
            Map<String, String> history = new HashMap<>();
            history.put("date", "(" + strSelectId + ") " + date);
            if (howUse.equals("収入")) {
                history.put("money", "+ " + price + " [" + howUse + "]" + " [" + moneyCategory + "] " + memo);
            } else if (howUse.equals("支出")) {
                history.put("money", "- " + price + " [" + howUse + "]" + " [" + moneyCategory + "] " + memo);
            } else if (howUse.equals("振替元")) {
                history.put("money", "- " + price + " [" + howUse + "]" + " [" + moneyCategory + "] " + memo);
            } else if (howUse.equals("振替先")) {
                history.put("money", "+ " + price + " [" + howUse + "]" + " [" + moneyCategory + "] " + memo);
            } else if (howUse.equals("使用")) {
                history.put("money", "+ " + price + " [" + howUse + "]" + " [" + moneyCategory + "] " + memo);
            } else if (howUse.equals("精算元")) {
                history.put("money", "- " + price + " [" + howUse + "]" + " [" + moneyCategory + "] " + memo);
            } else if (howUse.equals("精算先")) {
                history.put("money", "- " + price + " [" + howUse + "]" + " [" + moneyCategory + "] " + memo);
            }
            historyList.add(history);
        }
        // SimpleAdapter第4引数from用データの用意
        String[] from = {"date", "money"};
        // SimpleAdapter第5引数to用データの用意
        int[] to = {android.R.id.text1, android.R.id.text2};
        // SimpleAdapter生成
        SimpleAdapter adapter = new SimpleAdapter(HistoryActivity.this, historyList,
                android.R.layout.simple_list_item_2, from, to);
        // アダプタ登録
        lvHistory.setAdapter(adapter);

        // [新たに記録]ボタンのリスナ設定
        Button btToAddWalletData = findViewById(R.id.btToAddWalletData);
        HistoryActivity.ToAddWalletDataListener toAddWalletDataListener = new HistoryActivity.ToAddWalletDataListener();
        btToAddWalletData.setOnClickListener(toAddWalletDataListener);

        // [HOMEに戻る]ボタンのリスナ設定
        Button btBackHome = findViewById(R.id.btBackHome);
        HistoryActivity.BackHomeListener backHomeListener = new HistoryActivity.BackHomeListener();
        btBackHome.setOnClickListener(backHomeListener);

        // リストタップのリスナクラス登録
        lvHistory.setOnItemClickListener(new ListItemClickListener());
    }

    @Override
    protected void onDestroy() {
        _helper.close();
        super.onDestroy();
    }

    // [新たに記録]ボタンをクリックしたときのリスナクラス
    private class ToAddWalletDataListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            // 使用方法名から使用方法カテゴリidを取得
            SQLiteDatabase db = _helper.getWritableDatabase();
            String sql3 = "SELECT * FROM account_method WHERE method_name = ?";
            String[] params3 = {_methodName};
            Cursor cursor = db.rawQuery(sql3, params3);
            int methodCategoryId = -1;
            while (cursor.moveToNext()) {
                int idxMethodCategoryId = cursor.getColumnIndex("method_category_id");
                methodCategoryId = cursor.getInt(idxMethodCategoryId);
            }
            // 取得した使用方法カテゴリidから使用方法カテゴリ名を取得
            String sql4 = "SELECT * FROM method_category WHERE _id = ?";
            String[] params4 = {String.valueOf(methodCategoryId+1)};
            cursor = db.rawQuery(sql4, params4);
            String methodCategoryName = "";
            while (cursor.moveToNext()) {
                int idxMethodCategoryName = cursor.getColumnIndex("method_category_name");
                methodCategoryName = cursor.getString(idxMethodCategoryName);
            }
            // 取得した使用方法カテゴリ名がクレジットカードだった場合は、クレジット専用の[記録]画面に遷移
            if (methodCategoryName.equals("クレジットカード")) {
                // クレジット専用の[記録]画面に遷移
                Intent intent = new Intent(HistoryActivity.this, AddCreditAccountDataActivity.class);
                // クレジット専用の[記録]画面に送るデータを格納
                intent.putExtra("_methodName", _methodName);
                startActivity(intent);
            } else {
                // [記録]画面に遷移
                Intent intent = new Intent(HistoryActivity.this, AddAccountDataActivity.class);
                // [記録]画面に送るデータを格納
                intent.putExtra("_methodName", _methodName);
                startActivity(intent);
            }
        }
    }

    // [HOMEに戻る]ボタンをクリックしたときのリスナクラス
    private class BackHomeListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            // [HOME]画面に遷移
            Intent intent = new Intent(HistoryActivity.this, HomeActivity.class);
            startActivity(intent);
        }
    }

    // リストがタップされたときの処理が記述されたメンバクラス
    private class ListItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Map<String, String> item = (Map<String, String>) parent.getItemAtPosition(position);
            String date = item.get("date");
            String money = item.get("money");
            String money2 = money.replaceAll("\\[", "").replaceAll("\\]", "");
            String[] money3 = money2.split(" ");
            String moneyCategory = money3[3];
            // クレジット処理だった場合は、クレジット専用の[履歴編集]画面に遷移
            if (moneyCategory.equals("クレジット処理")) {
                // インテントオブジェクトを生成
                Intent intent = new Intent(HistoryActivity.this, CreditDetailHistoryActivity.class);
                // [履歴詳細]画面に送るデータを格納
                intent.putExtra("date", date);
                intent.putExtra("money", money);
                // [履歴編集]画面の起動
                startActivity(intent);
            } else {
                // インテントオブジェクトを生成
                Intent intent = new Intent(HistoryActivity.this, DetailHistoryActivity.class);
                // [履歴詳細]画面に送るデータを格納
                intent.putExtra("date", date);
                intent.putExtra("money", money);
                // [履歴編集]画面の起動
                startActivity(intent);
            }
        }
    }
}