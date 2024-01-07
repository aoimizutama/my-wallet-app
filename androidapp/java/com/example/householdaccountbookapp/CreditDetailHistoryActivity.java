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

public class CreditDetailHistoryActivity extends AppCompatActivity {

    // データベースヘルパーオブジェクト
    private DatabaseHelper _helper;

    // 選択されたidを表すフィールド
    private int _selectId = -1;
    private int _transferMethod = -1;
    private String _transferMethodName = "";
    private String _moneyHowUse = "";
    private int _methodId = -1;
    private int _moneyPrice = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credit_detail_history);

        // DBヘルパーオブジェクト生成
        _helper = new DatabaseHelper(CreditDetailHistoryActivity.this);

        // データベースヘルパーオブジェクトからデータベース接続オブジェクトを取得
        SQLiteDatabase db = _helper.getWritableDatabase();

        // インテントオブジェクトを取得
        Intent intent = getIntent();
        // [履歴]画面から渡されたデータを取得
        String date1 = intent.getStringExtra("date");
        String date2 = date1.replaceAll("\\(", "").replaceAll("\\)", "");
        String[] date3 = date2.split(" ");
        String strSelectId = date3[0];
        _selectId = Integer.parseInt(strSelectId);
        String date = date3[1];
        String money1 = intent.getStringExtra("money");
        String money2 = money1.replaceAll("\\[", "").replaceAll("\\]", "");
        String[] money3 = money2.split(" ");
        String moneySymbol = money3[0];
        String moneyPrice = money3[1];
        _moneyPrice = Integer.parseInt(moneyPrice);
        _moneyHowUse = money3[2];
        if (_moneyHowUse.equals("精算先")) {
            TextView tvCreditTransfer2 = findViewById(R.id.tvCreditTransfer2);
            tvCreditTransfer2.setText("精算元");
        }
        String moneyCategory = money3[3];
        String moneyMemo = null;
        if (money3.length == 5) {
            moneyMemo = money3[4];
        }
        if (_moneyHowUse.equals("精算元") || _moneyHowUse.equals("精算先")) {
            // 検索SQL文字列の用意
            String sql = "SELECT * FROM account_data WHERE _id = ?";
            // SQLの実行
            String[] params = {String.valueOf(_selectId)};
            Cursor cursor = db.rawQuery(sql, params);
            // データベースから取得した値を格納する変数の用意、データがなかったときのための初期値も用意
            int transferId = -1;
            // SQLの戻り値であるカーソルオブジェクトをループさせてデータベース内のデータを取得
            while(cursor.moveToNext()) {
                // カラムのインデックス値を取得
                int idxMethodId = cursor.getColumnIndex("_id");
                int idxTransferId = cursor.getColumnIndex("money_category_id");
                // カラムのインデックス値を元に実際のデータを取得
                String strMethodId = cursor.getString(idxMethodId);
                _methodId = Integer.parseInt(strMethodId);
                String strTransferId = cursor.getString(idxTransferId);
                transferId = Integer.parseInt(strTransferId);
            }

            // 検索SQL文字列の用意
            String sql2 = "SELECT * FROM account_data WHERE _id = ?";
            // SQLの実行
            String[] params2 = {String.valueOf(transferId)};
            cursor = db.rawQuery(sql2, params2);
            // SQLの戻り値であるカーソルオブジェクトをループさせてデータベース内のデータを取得
            while(cursor.moveToNext()) {
                // カラムのインデックス値を取得
                int idxTransferMethod = cursor.getColumnIndex("method_id");
                // カラムのインデックス値を元に実際のデータを取得
                String strTransferMethod = cursor.getString(idxTransferMethod);
                _transferMethod = Integer.parseInt(strTransferMethod);
            }

            // 検索SQL文字列の用意
            String sql3 = "SELECT * FROM account_method WHERE _id = ?";
            // SQLの実行
            String[] params3 = {String.valueOf(_transferMethod)};
            cursor = db.rawQuery(sql3, params3);
            // SQLの戻り値であるカーソルオブジェクトをループさせてデータベース内のデータを取得
            while(cursor.moveToNext()) {
                // カラムのインデックス値を取得
                int idxTransferMethodName = cursor.getColumnIndex("method_name");
                // カラムのインデックス値を元に実際のデータを取得
                _transferMethodName = cursor.getString(idxTransferMethodName);
            }
        }

        // [履歴]画面から渡されたデータを表示
        EditText etCreditDate2 = findViewById(R.id.etCreditDate2);
        etCreditDate2.setText(date);
        RadioGroup rgCreditHowUse2 = findViewById(R.id.rgCreditHowUse2);
        if (_moneyHowUse.equals("使用")) {
            rgCreditHowUse2.check(R.id.rbIncomeCredit2);
        } else {
            rgCreditHowUse2.check(R.id.rbTransferCredit2);
            EditText etTransferCredit2 = findViewById(R.id.etTransferCredit2);
            etTransferCredit2.setText(_transferMethodName);
        }
        EditText etCreditPrice2 = findViewById(R.id.etCreditPrice2);
        etCreditPrice2.setText(moneyPrice);
        EditText etCreditMemo2 = findViewById(R.id.etCreditMemo2);
        if (moneyMemo != null) {
            etCreditMemo2.setText(moneyMemo);
        }

        // [変更]ボタンのリスナ設定
        Button btToChangeAccountData = findViewById(R.id.btToChangeAccountData);
        CreditDetailHistoryActivity.ToChangeAccountDataListener toChangeAccountDataListener = new CreditDetailHistoryActivity.ToChangeAccountDataListener();
        btToChangeAccountData.setOnClickListener(toChangeAccountDataListener);

        // [削除]ボタンのリスナ設定
        Button btToDeleteAccountData = findViewById(R.id.btToDeleteAccountData);
        CreditDetailHistoryActivity.ToDeleteAccountDataListener toDeleteAccountDataListener = new CreditDetailHistoryActivity.ToDeleteAccountDataListener();
        btToDeleteAccountData.setOnClickListener(toDeleteAccountDataListener);
    }

    @Override
    protected void onDestroy() {
        _helper.close();
        super.onDestroy();
    }

    // [変更]ボタンをクリックしたときのリスナクラス
    private class ToChangeAccountDataListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            // 入力された値を取得
            EditText etCreditDate2 = findViewById(R.id.etCreditDate2);
            String date3 = etCreditDate2.getText().toString();
            RadioGroup rgCreditHowUse2 = findViewById(R.id.rgCreditHowUse2);
            int howUseId2 = rgCreditHowUse2.getCheckedRadioButtonId();    // チェックされたラジオボタンのidを取得
            RadioButton howUseObj2 = findViewById(howUseId2);    // チェックされたラジオボタンのオブジェクトを取得
            String howUse2 = howUseObj2.getText().toString();    // チェックされたラジオボタンのテキストを取得
            EditText etTransferCredit2 = findViewById(R.id.etTransferCredit2);
            String transferCredit = etTransferCredit2.getText().toString();
            EditText etCreditPrice2 = findViewById(R.id.etCreditPrice2);
            String strCreditPrice2 = etCreditPrice2.getText().toString();
            int price2 = Integer.parseInt(strCreditPrice2);
            EditText etCreditMemo2 = findViewById(R.id.etCreditMemo2);
            String memo2 = etCreditMemo2.getText().toString();

            Pattern pattern = Pattern.compile("^[0-9]{4}-[0-9]+-[0-9]+$");
            Matcher matcher = pattern.matcher(date3);
            if (matcher.find()) { // 日付が正しい形式で入力されている場合

                // データベースヘルパーオブジェクトからデータベース接続オブジェクトを取得
                SQLiteDatabase db = _helper.getWritableDatabase();

                // インテントオブジェクトを取得
                Intent intent = getIntent();
                // [履歴]画面から渡されたデータを取得
                String date21 = intent.getStringExtra("date");
                String date22 = date21.replaceAll("\\(", "").replaceAll("\\)", "");
                String[] date23 = date22.split(" ");
                String strSelectId2 = date23[0];
                _selectId = Integer.parseInt(strSelectId2);
                String money21 = intent.getStringExtra("money");
                String money22 = money21.replaceAll("\\[", "").replaceAll("\\]", "");
                String[] money23 = money22.split(" ");
                String moneySymbol = money23[0];
                String strMoneyPrice = money23[1];
                int moneyPrice = Integer.parseInt(strMoneyPrice);
                _moneyHowUse = money23[2];
                String moneyCategory = money23[3];
                String moneyMemo = null;
                if (money23.length == 5) {
                    moneyMemo = money23[4];
                }

                // 検索SQL文字列の用意
                String sql4 = "SELECT * FROM account_data WHERE _id = ?";
                // SQLの実行
                String[] params4 = {String.valueOf(_selectId)};
                Cursor cursor = db.rawQuery(sql4, params4);
                // データベースから取得した値を格納する変数の用意、データがなかったときのための初期値も用意
                int transferId = -1;
                // SQLの戻り値であるカーソルオブジェクトをループさせてデータベース内のデータを取得
                while(cursor.moveToNext()) {
                    // カラムのインデックス値を取得
                    int idxMethodId = cursor.getColumnIndex("method_id");
                    // カラムのインデックス値を元に実際のデータを取得
                    String strMethodId = cursor.getString(idxMethodId);
                    _methodId = Integer.parseInt(strMethodId);
                }

                // 検索SQL文字列の用意
                String sql5 = "SELECT * FROM account_method WHERE _id = ?";
                // SQLの実行
                String[] params5 = {String.valueOf(_methodId)};
                cursor = db.rawQuery(sql5, params5);
                // データベースから取得した値を格納する変数の用意、データがなかったときのための初期値も用意
                int balance = 0;
                // SQLの戻り値であるカーソルオブジェクトをループさせてデータベース内のデータを取得
                while(cursor.moveToNext()) {
                    // カラムのインデックス値を取得
                    int idxBalance = cursor.getColumnIndex("balance");
                    // カラムのインデックス値を元に実際のデータを取得
                    String strBalance = cursor.getString(idxBalance);
                    balance = Integer.parseInt(strBalance);
                }

                // 使用の変更処理
                if (_moneyHowUse.equals("使用")) {
                    if (howUse2.equals("使用")) {
                        // 削除処理
                        // 削除用SQL文字列の用意
                        String sql6 = "DELETE FROM account_data WHERE _id = ?";
                        // SQL文字列を元にプリベアードステートメントを取得
                        SQLiteStatement stmt = db.compileStatement(sql6);
                        // 変数のバインド
                        stmt.bindLong(1, _selectId);
                        // 削除SQLの実行
                        stmt.executeUpdateDelete();

                        // インサート処理
                        // インサート用SQL文字列の用意
                        String sql7 = "INSERT INTO account_data(method_id, date, howuse, price, money_category_id, memo) " +
                                "VALUES(?, ?, ?, ?, ?, ?)";
                        // SQL文字列を元にプリベアードステートメントを取得
                        stmt = db.compileStatement(sql7);
                        // 変数のバインド
                        stmt.bindLong(1, _methodId);
                        stmt.bindString(2, date3);
                        stmt.bindString(3, howUse2);
                        stmt.bindLong(4, price2);
                        stmt.bindLong(5, -1);
                        stmt.bindString(6, memo2);
                        // インサートSQLの実行
                        stmt.executeInsert();

                        // 残高更新処理
                        // アップデート用SQL文字列の用意
                        String sql8 = "UPDATE account_method SET balance = ? WHERE _id = ?";
                        // SQL文字列を元にプリベアードステートメントを取得
                        stmt = db.compileStatement(sql8);
                        // 変数のバインド
                        stmt.bindLong(1, balance - moneyPrice + price2);
                        stmt.bindLong(2, _methodId);
                        // アップデートSQLの実行
                        stmt.executeUpdateDelete();

                        // トーストで表示する文字列
                        String show = "変更しました！";
                        // トーストの表示
                        Toast.makeText(CreditDetailHistoryActivity.this, show, Toast.LENGTH_LONG).show();

                    } else {
                        // トーストで表示する文字列
                        String show = "使用方法の変更はできません。Sorry!";
                        // トーストの表示
                        Toast.makeText(CreditDetailHistoryActivity.this, show, Toast.LENGTH_LONG).show();
                    }

                    // 精算の変更処理（精算元からの変更のみ可）
                } else if (_moneyHowUse.equals("精算元")) {
                    if (howUse2.equals("精算")) {
                        // 検索SQL文字列の用意
                        String sql9 = "SELECT * FROM account_method WHERE method_name = ?";
                        // SQLの実行
                        String[] params9 = {transferCredit};
                        cursor = db.rawQuery(sql9, params9);
                        // データベースから取得した値を格納する変数の用意、データがなかったときのための初期値も用意
                        int transferMethodId = -1;
                        int transferMethodCategory = -1;
                        // SQLの戻り値であるカーソルオブジェクトをループさせてデータベース内のデータを取得
                        while(cursor.moveToNext()) {
                            // カラムのインデックス値を取得
                            int idxTransferMethodId = cursor.getColumnIndex("_id");
                            int idxTransferMethodCategory = cursor.getColumnIndex("method_category_id");
                            // カラムのインデックス値を元に実際のデータを取得
                            transferMethodId = cursor.getInt(idxTransferMethodId);
                            transferMethodCategory = cursor.getInt(idxTransferMethodCategory);
                        }
                        // 取得したカテゴリidのカテゴリ名を取得
                        // 検索SQL文字列の用意
                        String sql10 = "SELECT * FROM method_category WHERE _id = ?";
                        // SQLの実行
                        String[] params10 = {String.valueOf(transferMethodCategory+1)};
                        cursor = db.rawQuery(sql10, params10);
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
                            Toast.makeText(CreditDetailHistoryActivity.this, show, Toast.LENGTH_LONG).show();
                        } else if (!(transferMethodCategoryName.equals("銀行"))) { //　もし銀行以外の場所に精算されていた場合
                            // トーストで表示する文字列
                            String show = "正しい銀行の精算先を入力してください！";
                            // トーストの表示
                            Toast.makeText(CreditDetailHistoryActivity.this, show, Toast.LENGTH_LONG).show();
                        } else {
                            // 削除処理
                            // 選択されたデータの精算先idを取得
                            String sql11 = "SELECT * FROM account_data WHERE _id = ?";
                            String[] params11 = {String.valueOf(_selectId)};
                            cursor = db.rawQuery(sql11, params11);
                            int transferPlaceId = -1;
                            while (cursor.moveToNext()) {
                                int idxTransferPlaceId = cursor.getColumnIndex("money_category_id");
                                transferPlaceId = cursor.getInt(idxTransferPlaceId);
                            }
                            // 取得した精算先idのメソッドidを取得
                            String sql12 = "SELECT * FROM account_data WHERE _id = ?";
                            String[] params12 = {String.valueOf(transferPlaceId)};
                            cursor = db.rawQuery(sql12, params12);
                            int tranferMethodId2 = -1;
                            while (cursor.moveToNext()) {
                                int idxTransferMethodId2 = cursor.getColumnIndex("method_id");
                                tranferMethodId2 = cursor.getInt(idxTransferMethodId2);
                            }
                            // 取得した精算先メソッドidの残高を取得
                            String sql13 = "SELECT * FROM account_method WHERE _id = ?";
                            String[] params13 = {String.valueOf(tranferMethodId2)};
                            cursor = db.rawQuery(sql13, params13);
                            int transferBalance = 0;
                            while (cursor.moveToNext()) {
                                int idxTransferBalance = cursor.getColumnIndex("balance");
                                transferBalance = cursor.getInt(idxTransferBalance);
                            }

                            // 選択された精算元のデータを削除
                            // 削除用SQL文字列の用意
                            String sql14 = "DELETE FROM account_data WHERE _id = ?";
                            // SQL文字列を元にプリベアードステートメントを取得
                            SQLiteStatement stmt = db.compileStatement(sql14);
                            // 変数のバインド
                            stmt.bindLong(1, _selectId);
                            // 削除SQLの実行
                            stmt.executeUpdateDelete();

                            // 精算先のデータを削除
                            // 削除用SQL文字列の用意
                            String sql15 = "DELETE FROM account_data WHERE _id = ?";
                            // SQL文字列を元にプリベアードステートメントを取得
                            stmt = db.compileStatement(sql15);
                            // 変数のバインド
                            stmt.bindLong(1, transferPlaceId);
                            // 削除SQLの実行
                            stmt.executeUpdateDelete();

                            // インサート処理
                            // account_dataテーブルの最後のレコードのidを取得
                            // 検索SQL文字列の用意
                            String sql16 = "SELECT * FROM account_data";
                            // SQLの実行
                            cursor = db.rawQuery(sql16, null);
                            // データベースから取得した値を格納する変数の用意、データがなかったときのための初期値も用意
                            int maxDataId = 0;
                            // SQLの戻り値であるカーソルオブジェクトをループさせてデータベース内のデータを取得
                            while (cursor.moveToNext()) {
                                // カラムのインデックス値を取得
                                int idxMaxDataId = cursor.getColumnIndex("_id");
                                // カラムのインデックス値を元に実際のデータを取得
                                String strMaxDataId = cursor.getString(idxMaxDataId);
                                maxDataId = Integer.parseInt(strMaxDataId);
                            }

                            // インサート用SQL文字列の用意
                            String sql17 = "INSERT INTO account_data(method_id, date, howuse, price, money_category_id, memo) " +
                                    "VALUES(?, ?, ?, ?, ?, ?)";
                            // SQL文字列を元にプリベアードステートメントを取得
                            stmt = db.compileStatement(sql17);
                            // 変数のバインド
                            stmt.bindLong(1, _methodId);
                            stmt.bindString(2, date3);
                            stmt.bindString(3, howUse2 + "元");
                            stmt.bindLong(4, price2);
                            stmt.bindLong(5, maxDataId+2);    // 精算先のデータのid
                            stmt.bindString(6, memo2);
                            // インサートSQLの実行
                            stmt.executeInsert();

                            // インサート用SQL文字列の用意
                            String sql18 = "INSERT INTO account_data(method_id, date, howuse, price, money_category_id, memo) " +
                                    "VALUES(?, ?, ?, ?, ?, ?)";
                            // SQL文字列を元にプリベアードステートメントを取得
                            stmt = db.compileStatement(sql18);
                            // 変数のバインド
                            stmt.bindLong(1, transferMethodId);
                            stmt.bindString(2, date3);
                            stmt.bindString(3, howUse2 + "先");
                            stmt.bindLong(4, price2);
                            stmt.bindLong(5, maxDataId+1);    // 精算元のデータのid
                            stmt.bindString(6, memo2);
                            // インサートSQLの実行
                            stmt.executeInsert();

                            // 残高更新処理
                            // 精算元の残高更新
                            // アップデート用SQL文字列の用意
                            String sql19 = "UPDATE account_method SET balance = ? WHERE _id = ?";
                            // SQL文字列を元にプリベアードステートメントを取得
                            stmt = db.compileStatement(sql19);
                            // 変数のバインド
                            stmt.bindLong(1, balance + moneyPrice - price2);
                            stmt.bindLong(2, _methodId);
                            // アップデートSQLの実行
                            stmt.executeUpdateDelete();

                            // 精算先の残高更新
                            // 元の精算先の残高を更新
                            // アップデート用SQL文字列の用意
                            String sql20 = "UPDATE account_method SET balance = ? WHERE _id = ?";
                            // SQL文字列を元にプリベアードステートメントを取得
                            stmt = db.compileStatement(sql20);
                            // 変数のバインド
                            stmt.bindLong(1, transferBalance + moneyPrice);
                            stmt.bindLong(2, tranferMethodId2);
                            // アップデートSQLの実行
                            stmt.executeUpdateDelete();

                            // 精算先の残高を取得
                            // 検索SQL文字列の用意
                            String sql21 = "SELECT * FROM account_method WHERE _id = ?";
                            // SQLの実行
                            String[] params21 = {String.valueOf(transferMethodId)};
                            cursor = db.rawQuery(sql21, params21);
                            // データベースから取得した値を格納する変数の用意、データがなかったときのための初期値も用意
                            int balance2 = 0;
                            // SQLの戻り値であるカーソルオブジェクトをループさせてデータベース内のデータを取得
                            while (cursor.moveToNext()) {
                                // カラムのインデックス値を取得
                                int idxBalance2 = cursor.getColumnIndex("balance");
                                // カラムのインデックス値を元に実際のデータを取得
                                String strBalance2 = cursor.getString(idxBalance2);
                                balance2 = Integer.parseInt(strBalance2);
                            }
                            // 新たな振替先の残高更新
                            // アップデート用SQL文字列の用意
                            String sql22 = "UPDATE account_method SET balance = ? WHERE _id = ?";
                            // SQL文字列を元にプリベアードステートメントを取得
                            stmt = db.compileStatement(sql22);
                            // 変数のバインド
                            stmt.bindLong(1, balance2 - price2);
                            stmt.bindLong(2, transferMethodId);
                            // アップデートSQLの実行
                            stmt.executeUpdateDelete();

                            // トーストで表示する文字列
                            String show = "変更しました！";
                            // トーストの表示
                            Toast.makeText(CreditDetailHistoryActivity.this, show, Toast.LENGTH_LONG).show();
                        }
                    } else {
                        // トーストで表示する文字列
                        String show = "使用方法の変更はできません。Sorry!";
                        // トーストの表示
                        Toast.makeText(CreditDetailHistoryActivity.this, show, Toast.LENGTH_LONG).show();
                    }
                    // 精算先からの変更は不可
                } else {
                    // トーストで表示する文字列
                    String show = "精算先からの変更はできません。\n精算元からの変更をお願いします。\nSorry!";
                    // トーストの表示
                    Toast.makeText(CreditDetailHistoryActivity.this, show, Toast.LENGTH_LONG).show();
                }

                // インテントオブジェクトを生成
                intent = new Intent(CreditDetailHistoryActivity.this, HomeActivity.class);
                // [HOME]画面に遷移
                startActivity(intent);

            } else { // 日付が正しい形式で入力されていない場合
                // トーストで表示する文字列
                String show = "日付をYYYY-MM-DDの形式で入力してください。";
                // トーストの表示
                Toast.makeText(CreditDetailHistoryActivity.this, show, Toast.LENGTH_LONG).show();
            }
        }
    }

    // [削除]ボタンをクリックしたときのリスナクラス
    private class ToDeleteAccountDataListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            // インテントオブジェクトを生成
            Intent intent = new Intent(CreditDetailHistoryActivity.this, DeleteAccountDataActivity.class);
            // [削除確認]画面に送るデータを格納
            intent.putExtra("selectId", _selectId);
            intent.putExtra("moneyHowUse", _moneyHowUse);
            intent.putExtra("moneyPrice", _moneyPrice);
            // [削除確認]画面に遷移
            startActivity(intent);
        }
    }
}