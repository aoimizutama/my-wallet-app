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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddAccountDataActivity extends AppCompatActivity {

    // データベースヘルパーオブジェクト
    private DatabaseHelper _helper;

    // 選択されたメソッド名を表すフィールド
    private String _methodName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_account_data);

        // DBヘルパーオブジェクトを生成
        _helper = new DatabaseHelper(AddAccountDataActivity.this);

        // メソッド名取得
        // インテントオブジェクトを取得
        Intent intent = getIntent();
        // [履歴]画面から渡されたデータを取得
        _methodName = intent.getStringExtra("_methodName");
        // メソッド名を[記録]画面に表示
        TextView tvAddAccountDataTitle = findViewById(R.id.tvAddAccountDataTitle);
        tvAddAccountDataTitle.setText(_methodName + " 記録");

        // データベースヘルパーオブジェクトからデータベース接続オブジェクトを取得
        SQLiteDatabase db = _helper.getWritableDatabase();

        // 振替先のカテゴリ作成
        // SimpleAdapterで使用するListオブジェクトを用意
        List<String> transferCategorylist = new ArrayList<>();
        // 検索SQL文字列の用意
        String sql9 = "SELECT * FROM account_method";
        // SQLの実行
        Cursor cursor = db.rawQuery(sql9, null);
        // SQLの戻り値であるカーソルオブジェクトをループさせてデータベース内のデータを取得
        while(cursor.moveToNext()) {
            // カラムのインデックス値を取得
            int idxMethodName = cursor.getColumnIndex("method_name");
            // カラムのインデックス値を元に実際のデータを取得
            String methodName = cursor.getString(idxMethodName);
            transferCategorylist.add(methodName);
        }
        // スピナーを作成
        Spinner spTransferCategory = findViewById(R.id.spTransferCategory);
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, transferCategorylist);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spTransferCategory.setAdapter(adapter);

        // 内訳のカテゴリ作成
        // SimpleAdapterで使用するListオブジェクトを用意
        List<String> moneyCategorylist = new ArrayList<>();
        // 検索SQL文字列の用意
        String sql10 = "SELECT * FROM money_category";
        // SQLの実行
        cursor = db.rawQuery(sql10, null);
        // SQLの戻り値であるカーソルオブジェクトをループさせてデータベース内のデータを取得
        while(cursor.moveToNext()) {
            // カラムのインデックス値を取得
            int idxMoneyCategoryName = cursor.getColumnIndex("money_category_name");
            // カラムのインデックス値を元に実際のデータを取得
            String moneyCategoryName = cursor.getString(idxMoneyCategoryName);
            moneyCategorylist.add(moneyCategoryName);
        }
        // スピナーを作成
        Spinner spMoneyCategory = findViewById(R.id.spMoneyCategory);
        ArrayAdapter adapter2 = new ArrayAdapter(this, android.R.layout.simple_spinner_item, moneyCategorylist);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spMoneyCategory.setAdapter(adapter2);

        // 振替のラジオボタンが選択されたら、money_categoryのspinnerに自動的に振替を表示
        // ラジオグループのオブジェクトを取得
        RadioGroup rgHowUse = findViewById(R.id.rgHowUse);
        // ラジオグループのチェック状態変更イベントを登録
        rgHowUse.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            // チェック状態変更時に呼び出されるメソッド
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // チェック状態時の処理を記述
                if (checkedId == R.id.rbTransferCredit) {
                    spMoneyCategory.setSelection(4);
                    spMoneyCategory.setEnabled(false);
                } else {
                    spMoneyCategory.setEnabled(true);
                    spMoneyCategory.setSelection(0);
                }
            }
        });

        // [記録する]ボタンのリスナ設定
        Button btAddAccountData = findViewById(R.id.btAddAccountData);
        AddAccountDataActivity.AddAccountDataListener addAccountDataListener = new AddAccountDataActivity.AddAccountDataListener();
        btAddAccountData.setOnClickListener(addAccountDataListener);
    }

    @Override
    protected void onDestroy() {
        _helper.close();
        super.onDestroy();
    }

    // [記録する]ボタンをクリックしたときのリスナクラス
    private class AddAccountDataListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            // 入力された値を取得
            EditText etDate = findViewById(R.id.etDate);
            String date = etDate.getText().toString();
            RadioGroup rgHowUse = findViewById(R.id.rgHowUse);
            int howUseId = rgHowUse.getCheckedRadioButtonId();    // チェックされたラジオボタンのidを取得
            RadioButton howUseObj = findViewById(howUseId);    // チェックされたラジオボタンのオブジェクトを取得
            String howUse = howUseObj.getText().toString();    // チェックされたラジオボタンのテキストを取得
            Spinner spTransferCategory = findViewById(R.id.spTransferCategory);
            int transferCategory = spTransferCategory.getSelectedItemPosition();    // 選択されたカテゴリの番号を取得
            String transferCategoryName = (String)spTransferCategory.getSelectedItem();    // 選択されたカテゴリのテキストを取得
            EditText etPrice = findViewById(R.id.etPrice);
            String strPrice = etPrice.getText().toString();
            int price = Integer.parseInt(strPrice);
            Spinner spMoneyCategory = findViewById(R.id.spMoneyCategory);
            int moneyCategory = spMoneyCategory.getSelectedItemPosition();    // 選択されたカテゴリの番号を取得
            EditText etMemo = findViewById(R.id.etMemo);
            String memo = etMemo.getText().toString();

            // 日付の入力形式の確認
            Pattern pattern = Pattern.compile("^[0-9]{4}-[0-9]+-[0-9]+$");
            Matcher matcher = pattern.matcher(date);
            if (matcher.find()) { // 日付が正しい形式で入力されている場合
                // データベースヘルパーオブジェクトからデータベース接続オブジェクトを取得
                SQLiteDatabase db = _helper.getWritableDatabase();
                // 検索SQL文字列の用意
                String sql = "SELECT * FROM account_method WHERE method_name = ?";
                // SQLの実行
                String[] params = {_methodName};
                Cursor cursor = db.rawQuery(sql, params);
                // データベースから取得した値を格納する変数の用意、データがなかったときのための初期値も用意
                int methodId = -1;
                int balance = 0;
                // SQLの戻り値であるカーソルオブジェクトをループさせてデータベース内のデータを取得
                while(cursor.moveToNext()) {
                    // カラムのインデックス値を取得
                    int idxId = cursor.getColumnIndex("_id");
                    int idxBalance = cursor.getColumnIndex("balance");
                    // カラムのインデックス値を元に実際のデータを取得
                    String strMethodId = cursor.getString(idxId);
                    methodId = Integer.parseInt(strMethodId);
                    String strBalance = cursor.getString(idxBalance);
                    balance = Integer.parseInt(strBalance);
                }

                // 振替以外の場合（収入・支出の場合）
                if (!(howUse.equals("振替"))) {
                    // インサート用SQL文字列の用意
                    String sql2 = "INSERT INTO account_data(method_id, date, howuse, price, money_category_id, memo) " +
                            "VALUES(?, ?, ?, ?, ?, ?)";
                    // SQL文字列を元にプリベアードステートメントを取得
                    SQLiteStatement stmt = db.compileStatement(sql2);
                    // 変数のバインド
                    stmt.bindLong(1, methodId);
                    stmt.bindString(2, date);
                    stmt.bindString(3, howUse);
                    stmt.bindLong(4, price);
                    stmt.bindLong(5, moneyCategory);
                    stmt.bindString(6, memo);
                    // インサートSQLの実行
                    stmt.executeInsert();

                    // 残高の更新
                    if (howUse.equals("収入")) {    // [収入]のラジオボタンが選択された場合
                        // アップデート用SQL文字列の用意
                        String sql3 = "UPDATE account_method SET balance = ? WHERE method_name = ?";
                        // SQL文字列を元にプリベアードステートメントを取得
                        stmt = db.compileStatement(sql3);
                        // 変数のバインド
                        stmt.bindLong(1, balance + price);
                        stmt.bindString(2, _methodName);
                        // アップデートSQLの実行
                        stmt.executeUpdateDelete();
                    } else {    // [支出]のラジオボタンが選択された場合
                        // アップデート用SQL文字列の用意
                        String sql4 = "UPDATE account_method SET balance = ? WHERE method_name = ?";
                        // SQL文字列を元にプリベアードステートメントを取得
                        stmt = db.compileStatement(sql4);
                        // 変数のバインド
                        stmt.bindLong(1, balance - price);
                        stmt.bindString(2, _methodName);
                        // アップデートSQLの実行
                        stmt.executeUpdateDelete();
                    }

                    // トーストで表示する文字列
                    String show = "記録しました！";
                    // トーストの表示
                    Toast.makeText(AddAccountDataActivity.this, show, Toast.LENGTH_LONG).show();
                    // [HOME]画面に遷移
                    Intent intent = new Intent(AddAccountDataActivity.this, HomeActivity.class);
                    startActivity(intent);

                } else {    // [振替]のラジオボタンが選択された場合
                    // もし振替先が振替元と同じだった場合
                    // 検索SQL文字列の用意
                    String sql13 = "SELECT * FROM account_method WHERE method_name = ?";
                    // SQLの実行
                    String[] params13 = {transferCategoryName};
                    cursor = db.rawQuery(sql13, params13);
                    // データベースから取得した値を格納する変数の用意、データがなかったときのための初期値も用意
                    int transferMethodId = -1;
                    // SQLの戻り値であるカーソルオブジェクトをループさせてデータベース内のデータを取得
                    while(cursor.moveToNext()) {
                        // カラムのインデックス値を取得
                        int idxTransferMethodId = cursor.getColumnIndex("_id");
                        // カラムのインデックス値を元に実際のデータを取得
                        transferMethodId = cursor.getInt(idxTransferMethodId);
                    }
                    if (methodId == transferMethodId) {
                        // トーストで表示する文字列
                        String show = "同じ場所への振替はできません！振替先を変更してください！";
                        // トーストの表示
                        Toast.makeText(AddAccountDataActivity.this, show, Toast.LENGTH_LONG).show();
                    } else {
                        // 検索SQL文字列の用意
                        String sql12 = "SELECT * FROM account_data";
                        // SQLの実行
                        cursor = db.rawQuery(sql12, null);
                        // データベースから取得した値を格納する変数の用意、データがなかったときのための初期値も用意
                        int maxDataId = 0;
                        // SQLの戻り値であるカーソルオブジェクトをループさせてデータベース内のデータを取得
                        while(cursor.moveToNext()) {
                            // カラムのインデックス値を取得
                            int idxMaxDataId = cursor.getColumnIndex("_id");
                            // カラムのインデックス値を元に実際のデータを取得
                            String strMaxDataId = cursor.getString(idxMaxDataId);
                            maxDataId = Integer.parseInt(strMaxDataId);
                        }

                        // インサート用SQL文字列の用意
                        String sql5 = "INSERT INTO account_data(method_id, date, howuse, price, money_category_id, memo) " +
                                "VALUES(?, ?, ?, ?, ?, ?)";
                        // SQL文字列を元にプリベアードステートメントを取得
                        SQLiteStatement stmt = db.compileStatement(sql5);
                        // 変数のバインド
                        stmt.bindLong(1, methodId);
                        stmt.bindString(2, date);
                        stmt.bindString(3, howUse + "元");
                        stmt.bindLong(4, price);
                        stmt.bindLong(5, maxDataId+2);    // 振替先のデータのid
                        stmt.bindString(6, memo);
                        // インサートSQLの実行
                        stmt.executeInsert();

                        // インサート用SQL文字列の用意
                        String sql6 = "INSERT INTO account_data(method_id, date, howuse, price, money_category_id, memo) " +
                                "VALUES(?, ?, ?, ?, ?, ?)";
                        // SQL文字列を元にプリベアードステートメントを取得
                        stmt = db.compileStatement(sql6);
                        // 変数のバインド
                        stmt.bindLong(1, transferMethodId);
                        stmt.bindString(2, date);
                        stmt.bindString(3, howUse + "先");
                        stmt.bindLong(4, price);
                        stmt.bindLong(5, maxDataId+1);    // 振替元のデータのid
                        stmt.bindString(6, memo);
                        // インサートSQLの実行
                        stmt.executeInsert();

                        // 残高更新
                        // アップデート用SQL文字列の用意
                        String sql7 = "UPDATE account_method SET balance = ? WHERE method_name = ?";
                        // SQL文字列を元にプリベアードステートメントを取得
                        stmt = db.compileStatement(sql7);
                        // 変数のバインド
                        stmt.bindLong(1, balance - price);
                        stmt.bindString(2, _methodName);
                        // アップデートSQLの実行
                        stmt.executeUpdateDelete();

                        // 検索SQL文字列の用意
                        String sql11 = "SELECT * FROM account_method WHERE method_name = ?";
                        // SQLの実行
                        String[] params11 = {transferCategoryName};
                        cursor = db.rawQuery(sql11, params11);
                        // データベースから取得した値を格納する変数の用意、データがなかったときのための初期値も用意
                        int balance2 = 0;
                        // SQLの戻り値であるカーソルオブジェクトをループさせてデータベース内のデータを取得
                        while(cursor.moveToNext()) {
                            // カラムのインデックス値を取得
                            int idxBalance2 = cursor.getColumnIndex("balance");
                            // カラムのインデックス値を元に実際のデータを取得
                            String strBalance2 = cursor.getString(idxBalance2);
                            balance2 = Integer.parseInt(strBalance2);
                        }

                        String sql8 = "UPDATE account_method SET balance = ? WHERE _id = ?";
                        // SQL文字列を元にプリベアードステートメントを取得
                        stmt = db.compileStatement(sql8);
                        // 変数のバインド
                        stmt.bindLong(1, balance2 + price);
                        stmt.bindLong(2, transferMethodId);
                        // アップデートSQLの実行
                        stmt.executeUpdateDelete();

                        // トーストで表示する文字列
                        String show = "記録しました！";
                        // トーストの表示
                        Toast.makeText(AddAccountDataActivity.this, show, Toast.LENGTH_LONG).show();
                        // [HOME]画面に遷移
                        Intent intent = new Intent(AddAccountDataActivity.this, HomeActivity.class);
                        startActivity(intent);
                    }
                }
            } else { // 日付が正しい形式で入力されていない場合
                // トーストで表示する文字列
                String show = "日付をYYYY-MM-DDの形式で入力してください。";
                // トーストの表示
                Toast.makeText(AddAccountDataActivity.this, show, Toast.LENGTH_LONG).show();
            }
        }
    }
}