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

public class AddCreditAccountDataActivity extends AppCompatActivity {

    // データベースヘルパーオブジェクト
    private DatabaseHelper _helper;

    // 選択されたメソッド名を表すフィールド
    private String _methodName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_credit_account_data);

        // DBヘルパーオブジェクトを生成
        _helper = new DatabaseHelper(AddCreditAccountDataActivity.this);

        // メソッド名取得
        // インテントオブジェクトを取得
        Intent intent = getIntent();
        // [履歴]画面から渡されたデータを取得
        _methodName = intent.getStringExtra("_methodName");
        // メソッド名を[記録]画面に表示
        TextView tvAddAccountDataTitle = findViewById(R.id.tvAddCreditAccountDataTitle);
        tvAddAccountDataTitle.setText(_methodName + " 記録");

        // [記録する]ボタンのリスナ設定
        Button btAddCreditAccountData = findViewById(R.id.btAddCreditAccountData);
        AddCreditAccountDataActivity.AddCreditAccountDataListener addCreditAccountDataListener = new AddCreditAccountDataActivity.AddCreditAccountDataListener();
        btAddCreditAccountData.setOnClickListener(addCreditAccountDataListener);
    }

    @Override
    protected void onDestroy() {
        _helper.close();
        super.onDestroy();
    }

    // [記録する]ボタンをクリックしたときのリスナクラス
    private class AddCreditAccountDataListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            // 入力された値を取得
            EditText etCreditDate = findViewById(R.id.etCreditDate);
            String date = etCreditDate.getText().toString();
            RadioGroup rgCreditHowUse = findViewById(R.id.rgCreditHowUse);
            int howCreditUseId = rgCreditHowUse.getCheckedRadioButtonId();    // チェックされたラジオボタンのidを取得
            RadioButton howCreditUseObj = findViewById(howCreditUseId);    // チェックされたラジオボタンのオブジェクトを取得
            String howUse = howCreditUseObj.getText().toString();    // チェックされたラジオボタンのテキストを取得
            EditText etTransferCredit = findViewById(R.id.etTransferCredit);
            String transferCredit = etTransferCredit.getText().toString();
            EditText etCreditPrice = findViewById(R.id.etCreditPrice);
            String strCreditPrice = etCreditPrice.getText().toString();
            int price = Integer.parseInt(strCreditPrice);
            EditText etCreditMemo = findViewById(R.id.etCreditMemo);
            String memo = etCreditMemo.getText().toString();

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

                // 使用方法が使用の場合
                if (howUse.equals("使用")) {
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
                    stmt.bindLong(5, -1);
                    stmt.bindString(6, memo);
                    // インサートSQLの実行
                    stmt.executeInsert();

                    // 残高の更新
                    // アップデート用SQL文字列の用意
                    String sql3 = "UPDATE account_method SET balance = ? WHERE method_name = ?";
                    // SQL文字列を元にプリベアードステートメントを取得
                    stmt = db.compileStatement(sql3);
                    // 変数のバインド
                    stmt.bindLong(1, balance + price);
                    stmt.bindString(2, _methodName);
                    // アップデートSQLの実行
                    stmt.executeUpdateDelete();

                    // トーストで表示する文字列
                    String show = "記録しました！";
                    // トーストの表示
                    Toast.makeText(AddCreditAccountDataActivity.this, show, Toast.LENGTH_LONG).show();
                    // [HOME]画面に遷移
                    Intent intent = new Intent(AddCreditAccountDataActivity.this, HomeActivity.class);
                    startActivity(intent);

                } else {    // 使用方法が精算の場合
                    // 精算先のメソッドカテゴリidを取得
                    // 検索SQL文字列の用意
                    String sql3 = "SELECT * FROM account_method WHERE method_name = ?";
                    // SQLの実行
                    String[] params3 = {transferCredit};
                    cursor = db.rawQuery(sql3, params3);
                    // データベースから取得した値を格納する変数の用意、データがなかったときのための初期値も用意
                    int transferMethodId = -1;
                    int transferMethodCategory = -1;
                    int balance2 = 0;
                    // SQLの戻り値であるカーソルオブジェクトをループさせてデータベース内のデータを取得
                    while(cursor.moveToNext()) {
                        // カラムのインデックス値を取得
                        int idxTransferMethodId = cursor.getColumnIndex("_id");
                        int idxTransferMethodCategory = cursor.getColumnIndex("method_category_id");
                        int idxBalance2 = cursor.getColumnIndex("balance");
                        // カラムのインデックス値を元に実際のデータを取得
                        transferMethodId = cursor.getInt(idxTransferMethodId);
                        transferMethodCategory = cursor.getInt(idxTransferMethodCategory);
                        balance2 = cursor.getInt(idxBalance2);
                    }
                    // 取得したカテゴリidのカテゴリ名を取得
                    // 検索SQL文字列の用意
                    String sql4 = "SELECT * FROM method_category WHERE _id = ?";
                    // SQLの実行
                    String[] params4 = {String.valueOf(transferMethodCategory+1)};
                    cursor = db.rawQuery(sql4, params4);
                    // データベースから取得した値を格納する変数の用意、データがなかったときのための初期値も用意
                    String transferMethodCategoryName = "";
                    // SQLの戻り値であるカーソルオブジェクトをループさせてデータベース内のデータを取得
                    while(cursor.moveToNext()) {
                        // カラムのインデックス値を取得
                        int idxTransferMethodCategoryName = cursor.getColumnIndex("method_category_name");
                        // カラムのインデックス値を元に実際のデータを取得
                        transferMethodCategoryName = cursor.getString(idxTransferMethodCategoryName);
                    }
                    // もし誤ったカテゴリ名が入力されていた場合
                    if (transferMethodId == -1) {
                        // トーストで表示する文字列
                        String show = "正しい精算先を入力してください！";
                        // トーストの表示
                        Toast.makeText(AddCreditAccountDataActivity.this, show, Toast.LENGTH_LONG).show();
                    } else if (!(transferMethodCategoryName.equals("銀行"))) { //　もし銀行以外の場所に精算されていた場合
                        // トーストで表示する文字列
                        String show = "正しい銀行の精算先を入力してください！";
                        // トーストの表示
                        Toast.makeText(AddCreditAccountDataActivity.this, show, Toast.LENGTH_LONG).show();
                    } else {
                        // 検索SQL文字列の用意
                        String sql5 = "SELECT * FROM account_data";
                        // SQLの実行
                        cursor = db.rawQuery(sql5, null);
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
                        String sql6 = "INSERT INTO account_data(method_id, date, howuse, price, money_category_id, memo) " +
                                "VALUES(?, ?, ?, ?, ?, ?)";
                        // SQL文字列を元にプリベアードステートメントを取得
                        SQLiteStatement stmt = db.compileStatement(sql6);
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
                        String sql7 = "INSERT INTO account_data(method_id, date, howuse, price, money_category_id, memo) " +
                                "VALUES(?, ?, ?, ?, ?, ?)";
                        // SQL文字列を元にプリベアードステートメントを取得
                        stmt = db.compileStatement(sql7);
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
                        String sql8 = "UPDATE account_method SET balance = ? WHERE method_name = ?";
                        // SQL文字列を元にプリベアードステートメントを取得
                        stmt = db.compileStatement(sql8);
                        // 変数のバインド
                        stmt.bindLong(1, balance - price);
                        stmt.bindString(2, _methodName);
                        // アップデートSQLの実行
                        stmt.executeUpdateDelete();

                        String sql9 = "UPDATE account_method SET balance = ? WHERE _id = ?";
                        // SQL文字列を元にプリベアードステートメントを取得
                        stmt = db.compileStatement(sql9);
                        // 変数のバインド
                        stmt.bindLong(1, balance2 - price);
                        stmt.bindLong(2, transferMethodId);
                        // アップデートSQLの実行
                        stmt.executeUpdateDelete();

                        // トーストで表示する文字列
                        String show = "記録しました！";
                        // トーストの表示
                        Toast.makeText(AddCreditAccountDataActivity.this, show, Toast.LENGTH_LONG).show();
                        // [HOME]画面に遷移
                        Intent intent = new Intent(AddCreditAccountDataActivity.this, HomeActivity.class);
                        startActivity(intent);
                    }
                }
            } else { // 日付が正しい形式で入力されていない場合
                // トーストで表示する文字列
                String show = "日付をYYYY-MM-DDの形式で入力してください。";
                // トーストの表示
                Toast.makeText(AddCreditAccountDataActivity.this, show, Toast.LENGTH_LONG).show();
            }
        }
    }
}