// まだ振替からの変更処理と、振替への変更処理はできていない
// 変更では収入、支出、振替の支払い方法は変更できないようにする、変更したい場合は一旦削除してから新規に登録する必要がある
// 振替先・振替元の表示が、処理方法の削除などをするとずれてしまう

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

public class DetailHistoryActivity extends AppCompatActivity {

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
        setContentView(R.layout.activity_detail_history);

        // DBヘルパーオブジェクト生成
        _helper = new DatabaseHelper(DetailHistoryActivity.this);

        // SimpleAdapterで使用するListオブジェクトを用意
        List<String> transferCategorylist = new ArrayList<>();
        // データベースヘルパーオブジェクトからデータベース接続オブジェクトを取得
        SQLiteDatabase db = _helper.getWritableDatabase();
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
            transferCategorylist.add(methodName);
        }
        // スピナーを作成
        Spinner spTransferCategory = findViewById(R.id.spTransferCategory2);
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, transferCategorylist);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spTransferCategory.setAdapter(adapter);

        // SimpleAdapterで使用するListオブジェクトを用意
        List<String> moneyCategorylist = new ArrayList<>();
        // 検索SQL文字列の用意
        String sql2 = "SELECT * FROM money_category";
        // SQLの実行
        cursor = db.rawQuery(sql2, null);
        // SQLの戻り値であるカーソルオブジェクトをループさせてデータベース内のデータを取得
        while(cursor.moveToNext()) {
            // カラムのインデックス値を取得
            int idxMoneyCategoryName = cursor.getColumnIndex("money_category_name");
            // カラムのインデックス値を元に実際のデータを取得
            String moneyCategoryName = cursor.getString(idxMoneyCategoryName);
            moneyCategorylist.add(moneyCategoryName);
        }
        // スピナーを作成
        Spinner spMoneyCategory = findViewById(R.id.spMoneyCategory2);
        ArrayAdapter adapter2 = new ArrayAdapter(this, android.R.layout.simple_spinner_item, moneyCategorylist);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spMoneyCategory.setAdapter(adapter2);

        // 振替のラジオボタンが選択されたら、money_categoryのspinnerに自動的に振替を表示する
        // ラジオグループのオブジェクトを取得
        RadioGroup rgHowUse1 = findViewById(R.id.rgHowUse2);
        // ラジオグループのチェック状態変更イベントを登録
        rgHowUse1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            // チェック状態変更時に呼び出されるメソッド
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // チェック状態時の処理を記述
                if (checkedId == R.id.rbTransfer2) {
                    spMoneyCategory.setSelection(4);
                    spMoneyCategory.setEnabled(false);
                } else {
                    spMoneyCategory.setSelection(0);
                    spMoneyCategory.setEnabled(true);
                }
            }
        });

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
        if (_moneyHowUse.equals("振替先")) {
            TextView tvTransfer2 = findViewById(R.id.tvTransferCredit);
            tvTransfer2.setText("振替元");
        }
        String moneyCategory = money3[3];
        String moneyMemo = null;
        if (money3.length == 5) {
            moneyMemo = money3[4];
        }
        if (_moneyHowUse.equals("振替元") || _moneyHowUse.equals("振替先")) {
            // 検索SQL文字列の用意
            String sql3 = "SELECT * FROM account_data WHERE _id = ?";
            // SQLの実行
            String[] params3 = {String.valueOf(_selectId)};
            cursor = db.rawQuery(sql3, params3);
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
            String sql4 = "SELECT * FROM account_data WHERE _id = ?";
            // SQLの実行
            String[] params4 = {String.valueOf(transferId)};
            cursor = db.rawQuery(sql4, params4);
            // SQLの戻り値であるカーソルオブジェクトをループさせてデータベース内のデータを取得
            while(cursor.moveToNext()) {
                // カラムのインデックス値を取得
                int idxTransferMethod = cursor.getColumnIndex("method_id");
                // カラムのインデックス値を元に実際のデータを取得
                String strTransferMethod = cursor.getString(idxTransferMethod);
                _transferMethod = Integer.parseInt(strTransferMethod);
            }

            // 検索SQL文字列の用意
            String sql5 = "SELECT * FROM account_method WHERE _id = ?";
            // SQLの実行
            String[] params5 = {String.valueOf(_transferMethod)};
            cursor = db.rawQuery(sql5, params5);
            // SQLの戻り値であるカーソルオブジェクトをループさせてデータベース内のデータを取得
            while(cursor.moveToNext()) {
                // カラムのインデックス値を取得
                int idxTransferMethodName = cursor.getColumnIndex("method_name");
                // カラムのインデックス値を元に実際のデータを取得
                _transferMethodName = cursor.getString(idxTransferMethodName);
            }
        }

        // [履歴]画面から渡されたデータを表示
        EditText etDate2 = findViewById(R.id.etDate2);
        etDate2.setText(date);
        RadioGroup rgHowUse2 = findViewById(R.id.rgHowUse2);
        if (_moneyHowUse.equals("収入")) {
            rgHowUse2.check(R.id.rbIncome2);
        } else if (_moneyHowUse.equals("支出")) {
            rgHowUse2.check(R.id.rbSpending2);
        } else {
            rgHowUse2.check(R.id.rbTransfer2);
            Spinner spTransferCategory2 = findViewById(R.id.spTransferCategory2);    // 振替先Spinnerの設定
            int spinnerPosition = adapter.getPosition(_transferMethodName);
            spTransferCategory2.setSelection(spinnerPosition);
        }
        EditText etPrice2 = findViewById(R.id.etPrice2);
        etPrice2.setText(moneyPrice);
        Spinner spMoneyCategory2 = findViewById(R.id.spMoneyCategory2);
        // 検索SQL文字列の用意
        String sql5 = "SELECT * FROM money_category WHERE money_category_name = ?";
        // SQLの実行
        String[] params5 = {moneyCategory};
        cursor = db.rawQuery(sql5, params5);
        // データベースから取得した値を格納する変数の用意、データがなかったときのための初期値も用意
        int moneyCategoryId = -1;
        // SQLの戻り値であるカーソルオブジェクトをループさせてデータベース内のデータを取得
        while(cursor.moveToNext()) {
            // カラムのインデックス値を取得
            int idxMoneyCategoryId = cursor.getColumnIndex("_id");
            // カラムのインデックス値を元に実際のデータを取得
            String strMoneyCategoryId = cursor.getString(idxMoneyCategoryId);
            moneyCategoryId = Integer.parseInt(strMoneyCategoryId);
        }
        spMoneyCategory2.setSelection(moneyCategoryId - 1);
        EditText etMemo2 = findViewById(R.id.etMemo2);
        if (moneyMemo != null) {
            etMemo2.setText(moneyMemo);
        }

        // [変更]ボタンのリスナ設定
        Button btToChangeAccountData = findViewById(R.id.btToChangeAccountData);
        DetailHistoryActivity.ToChangeAccountDataListener toChangeAccountDataListener = new DetailHistoryActivity.ToChangeAccountDataListener();
        btToChangeAccountData.setOnClickListener(toChangeAccountDataListener);

        // [削除]ボタンのリスナ設定
        Button btToDeleteAccountData = findViewById(R.id.btToDeleteAccountData);
        DetailHistoryActivity.ToDeleteAccountDataListener toDeleteAccountDataListener = new DetailHistoryActivity.ToDeleteAccountDataListener();
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
            EditText etDate2 = findViewById(R.id.etDate2);
            String date3 = etDate2.getText().toString();
            RadioGroup rgHowUse2 = findViewById(R.id.rgHowUse2);
            int howUseId2 = rgHowUse2.getCheckedRadioButtonId();    // チェックされたラジオボタンのidを取得
            RadioButton howUseObj2 = findViewById(howUseId2);    // チェックされたラジオボタンのオブジェクトを取得
            String howUse2 = howUseObj2.getText().toString();    // チェックされたラジオボタンのテキストを取得
            Spinner spTransferCategory2 = findViewById(R.id.spTransferCategory2);
            int transferCategory2 = spTransferCategory2.getSelectedItemPosition();    // 選択されたカテゴリの番号を取得
            String transferCategoryName2 = (String)spTransferCategory2.getSelectedItem();    // 選択されたカテゴリのテキストを取得
            EditText etPrice2 = findViewById(R.id.etPrice2);
            String strPrice2 = etPrice2.getText().toString();
            int price2 = Integer.parseInt(strPrice2);
            Spinner spMoneyCategory2 = findViewById(R.id.spMoneyCategory2);
            int moneyCategory2 = spMoneyCategory2.getSelectedItemPosition();    // 選択されたカテゴリの番号を取得
            EditText etMemo2 = findViewById(R.id.etMemo2);
            String memo2 = etMemo2.getText().toString();

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
                String sql6 = "SELECT * FROM account_data WHERE _id = ?";
                // SQLの実行
                String[] params6 = {String.valueOf(_selectId)};
                Cursor cursor = db.rawQuery(sql6, params6);
                // データベースから取得した値を格納する変数の用意、データがなかったときのための初期値も用意
                int transferId = -1;
                // SQLの戻り値であるカーソルオブジェクトをループさせてデータベース内のデータを取得
                while(cursor.moveToNext()) {
                    // カラムのインデックス値を取得
                    int idxMethodId = cursor.getColumnIndex("method_id");
                    // カラムのインデックス値を元に実際のデータを取得
                    String strMethodId = cursor.getString(idxMethodId);
                    _methodId = Integer.parseInt(strMethodId);
                    if (_moneyHowUse.equals("振替先") || _moneyHowUse.equals("振替元")) {
                        int idxTransferId = cursor.getColumnIndex("money_category_id");
                        // カラムのインデックス値を元に実際のデータを取得
                        String strTransferId = cursor.getString(idxTransferId);
                        transferId = Integer.parseInt(strTransferId);
                    }
                }

                // 検索SQL文字列の用意
                String sql7 = "SELECT * FROM account_method WHERE _id = ?";
                // SQLの実行
                String[] params7 = {String.valueOf(_methodId)};
                cursor = db.rawQuery(sql7, params7);
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

                // 収入の変更処理
                if (_moneyHowUse.equals("収入")) {
                    if (howUse2.equals("収入")) {
                        // 削除処理
                        // 削除用SQL文字列の用意
                        String sql8 = "DELETE FROM account_data WHERE _id = ?";
                        // SQL文字列を元にプリベアードステートメントを取得
                        SQLiteStatement stmt = db.compileStatement(sql8);
                        // 変数のバインド
                        stmt.bindLong(1, _selectId);
                        // 削除SQLの実行
                        stmt.executeUpdateDelete();

                        // インサート処理
                        // インサート用SQL文字列の用意
                        String sql9 = "INSERT INTO account_data(method_id, date, howuse, price, money_category_id, memo) " +
                                "VALUES(?, ?, ?, ?, ?, ?)";
                        // SQL文字列を元にプリベアードステートメントを取得
                        stmt = db.compileStatement(sql9);
                        // 変数のバインド
                        stmt.bindLong(1, _methodId);
                        stmt.bindString(2, date3);
                        stmt.bindString(3, howUse2);
                        stmt.bindLong(4, price2);
                        stmt.bindLong(5, moneyCategory2);
                        stmt.bindString(6, memo2);
                        // インサートSQLの実行
                        stmt.executeInsert();

                        // 残高更新処理
                        // アップデート用SQL文字列の用意
                        String sql10 = "UPDATE account_method SET balance = ? WHERE _id = ?";
                        // SQL文字列を元にプリベアードステートメントを取得
                        stmt = db.compileStatement(sql10);
                        // 変数のバインド
                        stmt.bindLong(1, balance - moneyPrice + price2);
                        stmt.bindLong(2, _methodId);
                        // アップデートSQLの実行
                        stmt.executeUpdateDelete();

                        // トーストで表示する文字列
                        String show = "変更しました！";
                        // トーストの表示
                        Toast.makeText(DetailHistoryActivity.this, show, Toast.LENGTH_LONG).show();

                    } else {
                        // トーストで表示する文字列
                        String show = "使用方法の変更はできません。Sorry!";
                        // トーストの表示
                        Toast.makeText(DetailHistoryActivity.this, show, Toast.LENGTH_LONG).show();
                    }

                    // 支出の変更処理
                } else if (_moneyHowUse.equals("支出")) {
                    if (howUse2.equals("支出")) {
                        // 削除処理
                        // 削除用SQL文字列の用意
                        String sql11 = "DELETE FROM account_data WHERE _id = ?";
                        // SQL文字列を元にプリベアードステートメントを取得
                        SQLiteStatement stmt = db.compileStatement(sql11);
                        // 変数のバインド
                        stmt.bindLong(1, _selectId);
                        // 削除SQLの実行
                        stmt.executeUpdateDelete();

                        // インサート処理
                        // インサート用SQL文字列の用意
                        String sql12 = "INSERT INTO account_data(method_id, date, howuse, price, money_category_id, memo) " +
                                "VALUES(?, ?, ?, ?, ?, ?)";
                        // SQL文字列を元にプリベアードステートメントを取得
                        stmt = db.compileStatement(sql12);
                        // 変数のバインド
                        stmt.bindLong(1, _methodId);
                        stmt.bindString(2, date3);
                        stmt.bindString(3, howUse2);
                        stmt.bindLong(4, price2);
                        stmt.bindLong(5, moneyCategory2);
                        stmt.bindString(6, memo2);
                        // インサートSQLの実行
                        stmt.executeInsert();

                        // 残高更新処理
                        // アップデート用SQL文字列の用意
                        String sql13 = "UPDATE account_method SET balance = ? WHERE _id = ?";
                        // SQL文字列を元にプリベアードステートメントを取得
                        stmt = db.compileStatement(sql13);
                        // 変数のバインド
                        stmt.bindLong(1, balance + moneyPrice - price2);
                        stmt.bindLong(2, _methodId);
                        // アップデートSQLの実行
                        stmt.executeUpdateDelete();

                        // トーストで表示する文字列
                        String show = "変更しました！";
                        // トーストの表示
                        Toast.makeText(DetailHistoryActivity.this, show, Toast.LENGTH_LONG).show();

                    } else {
                        // トーストで表示する文字列
                        String show = "使用方法の変更はできません。Sorry!";
                        // トーストの表示
                        Toast.makeText(DetailHistoryActivity.this, show, Toast.LENGTH_LONG).show();
                    }

                    // 振替の変更処理（振替元からの変更のみ可）
                } else if (_moneyHowUse.equals("振替元")) {
                    if (howUse2.equals("振替")) {
                        // もし振替先が振替元と同じだった場合
                        // 検索SQL文字列の用意
                        String sql14 = "SELECT * FROM account_method WHERE method_name = ?";
                        // SQLの実行
                        String[] params14 = {transferCategoryName2};
                        cursor = db.rawQuery(sql14, params14);
                        // データベースから取得した値を格納する変数の用意、データがなかったときのための初期値も用意
                        int transferMethodId2 = -1;
                        // SQLの戻り値であるカーソルオブジェクトをループさせてデータベース内のデータを取得
                        while(cursor.moveToNext()) {
                            // カラムのインデックス値を取得
                            int idxTransferMethodId2 = cursor.getColumnIndex("_id");
                            // カラムのインデックス値を元に実際のデータを取得
                            transferMethodId2 = cursor.getInt(idxTransferMethodId2);
                        }
                        if (_methodId != transferMethodId2) {
                            // 削除処理
                            // 選択されたデータの振替先idを取得
                            String sql15 = "SELECT * FROM account_data WHERE _id = ?";
                            String params15[] = {String.valueOf(_selectId)};
                            cursor = db.rawQuery(sql15, params15);
                            int transferPlaceId = -1;
                            while (cursor.moveToNext()) {
                                int idxTransferPlaceId = cursor.getColumnIndex("money_category_id");
                                transferPlaceId = cursor.getInt(idxTransferPlaceId);
                            }
                            // 取得した振替先idのメソッドidを取得
                            String sql16 = "SELECT * FROM account_data WHERE _id = ?";
                            String[] params16 = {String.valueOf(transferPlaceId)};
                            cursor = db.rawQuery(sql16, params16);
                            int tranferMethodId = -1;
                            while (cursor.moveToNext()) {
                                int idxTransferMethodId = cursor.getColumnIndex("method_id");
                                tranferMethodId = cursor.getInt(idxTransferMethodId);
                            }
                            // 取得した振替先メソッドidの残高を取得
                            String sql17 = "SELECT * FROM account_method WHERE _id = ?";
                            String[] params17 = {String.valueOf(tranferMethodId)};
                            cursor = db.rawQuery(sql17, params17);
                            int transferBalance = 0;
                            while (cursor.moveToNext()) {
                                int idxTransferBalance = cursor.getColumnIndex("balance");
                                transferBalance = cursor.getInt(idxTransferBalance);
                            }

                            // 選択された振替元のデータを削除
                            // 削除用SQL文字列の用意
                            String sql18 = "DELETE FROM account_data WHERE _id = ?";
                            // SQL文字列を元にプリベアードステートメントを取得
                            SQLiteStatement stmt = db.compileStatement(sql18);
                            // 変数のバインド
                            stmt.bindLong(1, _selectId);
                            // 削除SQLの実行
                            stmt.executeUpdateDelete();

                            // 振替先のデータを削除
                            // 削除用SQL文字列の用意
                            String sql19 = "DELETE FROM account_data WHERE _id = ?";
                            // SQL文字列を元にプリベアードステートメントを取得
                            stmt = db.compileStatement(sql19);
                            // 変数のバインド
                            stmt.bindLong(1, transferPlaceId);
                            // 削除SQLの実行
                            stmt.executeUpdateDelete();

                            // インサート処理
                            // account_dataテーブルの最後のレコードのidを取得
                            // 検索SQL文字列の用意
                            String sql20 = "SELECT * FROM account_data";
                            // SQLの実行
                            cursor = db.rawQuery(sql20, null);
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

                            // 振替元のデータをインサート
                            // インサート用SQL文字列の用意
                            String sql21 = "INSERT INTO account_data(method_id, date, howuse, price, money_category_id, memo) " +
                                    "VALUES(?, ?, ?, ?, ?, ?)";
                            // SQL文字列を元にプリベアードステートメントを取得
                            stmt = db.compileStatement(sql21);
                            // 変数のバインド
                            stmt.bindLong(1, _methodId);
                            stmt.bindString(2, date3);
                            stmt.bindString(3, howUse2 + "元");
                            stmt.bindLong(4, price2);
                            stmt.bindLong(5, maxDataId+2);    // 振替先のデータのid
                            stmt.bindString(6, memo2);
                            // インサートSQLの実行
                            stmt.executeInsert();

                            // 振替先のデータをインサート
                            // インサート用SQL文字列の用意
                            String sql22 = "INSERT INTO account_data(method_id, date, howuse, price, money_category_id, memo) " +
                                    "VALUES(?, ?, ?, ?, ?, ?)";
                            // SQL文字列を元にプリベアードステートメントを取得
                            stmt = db.compileStatement(sql22);
                            // 変数のバインド
                            stmt.bindLong(1, transferMethodId2);
                            stmt.bindString(2, date3);
                            stmt.bindString(3, howUse2 + "先");
                            stmt.bindLong(4, price2);
                            stmt.bindLong(5, maxDataId+1);    // 振替先のデータのid
                            stmt.bindString(6, memo2);
                            // インサートSQLの実行
                            stmt.executeInsert();

                            // 残高更新処理
                            // 振替元の残高更新
                            // アップデート用SQL文字列の用意
                            String sql23 = "UPDATE account_method SET balance = ? WHERE _id = ?";
                            // SQL文字列を元にプリベアードステートメントを取得
                            stmt = db.compileStatement(sql23);
                            // 変数のバインド
                            stmt.bindLong(1, balance + moneyPrice - price2);
                            stmt.bindLong(2, _methodId);
                            // アップデートSQLの実行
                            stmt.executeUpdateDelete();

                            // 振替先の残高更新
                            // 元の振替先の残高を更新
                            // アップデート用SQL文字列の用意
                            String sql24 = "UPDATE account_method SET balance = ? WHERE _id = ?";
                            // SQL文字列を元にプリベアードステートメントを取得
                            stmt = db.compileStatement(sql24);
                            // 変数のバインド
                            stmt.bindLong(1, transferBalance - moneyPrice);
                            stmt.bindLong(2, tranferMethodId);
                            // アップデートSQLの実行
                            stmt.executeUpdateDelete();

                            // 振替先の残高を取得
                            // 検索SQL文字列の用意
                            String sql25 = "SELECT * FROM account_method WHERE method_name = ?";
                            // SQLの実行
                            String[] params25 = {String.valueOf(transferCategoryName2)};
                            cursor = db.rawQuery(sql25, params25);
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
                            String sql26 = "UPDATE account_method SET balance = ? WHERE _id = ?";
                            // SQL文字列を元にプリベアードステートメントを取得
                            stmt = db.compileStatement(sql26);
                            // 変数のバインド
                            stmt.bindLong(1, balance2 + price2);
                            stmt.bindLong(2, transferMethodId2);
                            // アップデートSQLの実行
                            stmt.executeUpdateDelete();

                            // トーストで表示する文字列
                            String show = "変更しました！";
                            // トーストの表示
                            Toast.makeText(DetailHistoryActivity.this, show, Toast.LENGTH_LONG).show();
                        } else {
                            // トーストで表示する文字列
                            String show = "同じ場所への振替はできません！振替先を変更してください！";
                            // トーストの表示
                            Toast.makeText(DetailHistoryActivity.this, show, Toast.LENGTH_LONG).show();
                        }
                    } else {
                        // トーストで表示する文字列
                        String show = "使用方法の変更はできません。Sorry!";
                        // トーストの表示
                        Toast.makeText(DetailHistoryActivity.this, show, Toast.LENGTH_LONG).show();
                    }

                    // 振替先からの変更は不可
                } else {
                    // トーストで表示する文字列
                    String show = "振替先からの変更はできません。\n振替元からの変更をお願いします。\nSorry!";
                    // トーストの表示
                    Toast.makeText(DetailHistoryActivity.this, show, Toast.LENGTH_LONG).show();
                }

                // インテントオブジェクトを生成
                intent = new Intent(DetailHistoryActivity.this, HomeActivity.class);
                // [HOME]画面に遷移
                startActivity(intent);

            } else { // 日付が正しい形式で入力されていない場合
                // トーストで表示する文字列
                String show = "日付をYYYY-MM-DDの形式で入力してください。";
                // トーストの表示
                Toast.makeText(DetailHistoryActivity.this, show, Toast.LENGTH_LONG).show();
            }
        }
    }

    // [削除]ボタンをクリックしたときのリスナクラス
    private class ToDeleteAccountDataListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            // インテントオブジェクトを生成
            Intent intent = new Intent(DetailHistoryActivity.this, DeleteAccountDataActivity.class);
            // [削除確認]画面に送るデータを格納
            intent.putExtra("selectId", _selectId);
            intent.putExtra("moneyHowUse", _moneyHowUse);
            intent.putExtra("moneyPrice", _moneyPrice);
            // [削除確認]画面に遷移
            startActivity(intent);
        }
    }
}