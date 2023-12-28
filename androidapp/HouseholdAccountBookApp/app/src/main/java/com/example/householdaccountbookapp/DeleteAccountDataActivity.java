// 削除は選択したデータのみしか削除されない（振替先の削除はできない）
// 削除しても残金の更新はされない

package com.example.householdaccountbookapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class DeleteAccountDataActivity extends AppCompatActivity {

    // データベースヘルパーオブジェクト
    private DatabaseHelper _helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_account_data);

        // DBヘルパーオブジェクトを生成
        _helper = new DatabaseHelper(DeleteAccountDataActivity.this);

        // [削除する]ボタンのリスナ設定
        Button btYes = findViewById(R.id.btYes);
        DeleteAccountDataActivity.YesListener yesListener = new DeleteAccountDataActivity.YesListener();
        btYes.setOnClickListener(yesListener);

        // [キャンセル]ボタンのリスナ設定
        Button btNo = findViewById(R.id.btNo);
        DeleteAccountDataActivity.NoListener noListener = new DeleteAccountDataActivity.NoListener();
        btNo.setOnClickListener(noListener);
    }

    @Override
    protected void onDestroy() {
        _helper.close();
        super.onDestroy();
    }

    // [削除する]ボタンをクリックしたときのリスナクラス
    private class YesListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            // インテントオブジェクトを取得
            Intent intent = getIntent();
            // [履歴詳細]画面から渡されたデータを取得
            int selectId = intent.getIntExtra("selectId", -1);
            String moneyHowUse = intent.getStringExtra("moneyHowUse");
            int moneyPrice = intent.getIntExtra("moneyPrice", 0);
            // データベースヘルパーオブジェクトからデータベース接続オブジェクトを取得
            SQLiteDatabase db = _helper.getWritableDatabase();

            // 選択されたデータのメソッドidを取得
            String sql = "SELECT * FROM account_data WHERE _id = ?";
            String[] params = {String.valueOf(selectId)};
            Cursor cursor = db.rawQuery(sql, params);
            int deleteMethodId = -1;
            while (cursor.moveToNext()) {
                int idxDeleteMethodId = cursor.getColumnIndex("method_id");
                deleteMethodId = cursor.getInt(idxDeleteMethodId);
            }
            // 取得したメソッドidの残高を取得
            String sql2 = "SELECT * FROM account_method WHERE _id = ?";
            String[] params2 = {String.valueOf(deleteMethodId)};
            cursor = db.rawQuery(sql2, params2);
            int deleteBalance = 0;
            while (cursor.moveToNext()) {
                int idxDeleteBalance = cursor.getColumnIndex("balance");
                deleteBalance = cursor.getInt(idxDeleteBalance);
            }

            // 収入or支出のデータ削除の場合
            if (moneyHowUse.equals("収入") || moneyHowUse.equals("支出") || moneyHowUse.equals("使用")) {
                // 選択されたデータを削除
                // 削除用SQL文字列の用意
                String sql3 = "DELETE FROM account_data WHERE _id = ?";
                // SQL文字列を元にプリベアードステートメントを取得
                SQLiteStatement stmt = db.compileStatement(sql3);
                // 変数のバインド
                stmt.bindLong(1, selectId);
                // 削除SQLの実行
                stmt.executeUpdateDelete();

                // 残高の更新
                String sql4 = "UPDATE account_method SET balance = ? WHERE _id = ?";
                stmt = db.compileStatement(sql4);
                if (moneyHowUse.equals("収入") || moneyHowUse.equals("使用")) { // 収入or使用のデータを削除する場合
                    stmt.bindLong(1, deleteBalance - moneyPrice);
                } else { // 支出のデータを削除する場合
                    stmt.bindLong(1, deleteBalance + moneyPrice);
                }
                stmt.bindLong(2, deleteMethodId);
                stmt.executeUpdateDelete();

            } else if (moneyHowUse.equals("振替先") || moneyHowUse.equals("振替元")) { // 振替のデータを削除する場合
                // 選択されたデータの振替先or振替元idを取得
                String sql5 = "SELECT * FROM account_data WHERE _id = ?";
                String[] params5 = {String.valueOf(selectId)};
                cursor = db.rawQuery(sql5, params5);
                int transferPlaceId = -1;
                while (cursor.moveToNext()) {
                    int idxTransferPlaceId = cursor.getColumnIndex("money_category_id");
                    transferPlaceId = cursor.getInt(idxTransferPlaceId);
                }
                // 取得した振替先or振替元idのメソッドidを取得
                String sql6 = "SELECT * FROM account_data WHERE _id = ?";
                String[] params6 = {String.valueOf(transferPlaceId)};
                cursor = db.rawQuery(sql6, params6);
                int tranferMethodId = -1;
                while (cursor.moveToNext()) {
                    int idxTransferMethodId = cursor.getColumnIndex("method_id");
                    tranferMethodId = cursor.getInt(idxTransferMethodId);
                }
                // 取得した振替先or振替元メソッドidの残高を取得
                String sql7 = "SELECT * FROM account_method WHERE _id = ?";
                String[] params7 = {String.valueOf(tranferMethodId)};
                cursor = db.rawQuery(sql7, params7);
                int transferBalance = 0;
                while (cursor.moveToNext()) {
                    int idxTransferBalance = cursor.getColumnIndex("balance");
                    transferBalance = cursor.getInt(idxTransferBalance);
                }

                // 選択されたデータの削除
                // 削除用SQL文字列の用意
                String sql8 = "DELETE FROM account_data WHERE _id = ?";
                // SQL文字列を元にプリベアードステートメントを取得
                SQLiteStatement stmt = db.compileStatement(sql8);
                // 変数のバインド
                stmt.bindLong(1, selectId);
                // 削除SQLの実行
                stmt.executeUpdateDelete();

                // 取得した振替先or振替元idのデータを削除
                String sql9 = "DELETE FROM account_data WHERE _id = ?";
                stmt = db.compileStatement(sql9);
                stmt.bindLong(1, transferPlaceId);
                stmt.executeUpdateDelete();

                // 残高の更新（振替先・振替元の残高両方を更新）
                // 選択されたデータの残高の更新
                String sql10 = "UPDATE account_method SET balance = ? WHERE _id = ?";
                stmt = db.compileStatement(sql10);
                if (moneyHowUse.equals("振替先")) { // 振替先のデータを削除する場合
                    stmt.bindLong(1, deleteBalance - moneyPrice);
                } else { // 振替元のデータを削除する場合
                    stmt.bindLong(1, deleteBalance + moneyPrice);
                }
                stmt.bindLong(2, deleteMethodId);
                stmt.executeUpdateDelete();

                // 振替先or振替元の残高の更新
                String sql11 = "UPDATE account_method SET balance = ? WHERE _id = ?";
                stmt = db.compileStatement(sql11);
                if (moneyHowUse.equals("振替先")) { // 選択されたデータが振替先だった場合
                    stmt.bindLong(1, transferBalance + moneyPrice);
                } else { // 選択されたデータが振替元だった場合
                    stmt.bindLong(1, transferBalance - moneyPrice);
                }
                stmt.bindLong(2, tranferMethodId);
                stmt.executeUpdateDelete();

            } else { // 精算のデータを削除する場合
                // 選択されたデータの精算先or精算元idを取得
                String sql12 = "SELECT * FROM account_data WHERE _id = ?";
                String[] params12 = {String.valueOf(selectId)};
                cursor = db.rawQuery(sql12, params12);
                int transferPlaceId = -1;
                while (cursor.moveToNext()) {
                    int idxTransferPlaceId = cursor.getColumnIndex("money_category_id");
                    transferPlaceId = cursor.getInt(idxTransferPlaceId);
                }
                // 取得した精算先or精算元idのメソッドidを取得
                String sql13 = "SELECT * FROM account_data WHERE _id = ?";
                String[] params13 = {String.valueOf(transferPlaceId)};
                cursor = db.rawQuery(sql13, params13);
                int tranferMethodId = -1;
                while (cursor.moveToNext()) {
                    int idxTransferMethodId = cursor.getColumnIndex("method_id");
                    tranferMethodId = cursor.getInt(idxTransferMethodId);
                }
                // 取得した精算先or精算元メソッドidの残高を取得
                String sql14 = "SELECT * FROM account_method WHERE _id = ?";
                String[] params14 = {String.valueOf(tranferMethodId)};
                cursor = db.rawQuery(sql14, params14);
                int transferBalance = 0;
                while (cursor.moveToNext()) {
                    int idxTransferBalance = cursor.getColumnIndex("balance");
                    transferBalance = cursor.getInt(idxTransferBalance);
                }

                // 選択されたデータの削除
                // 削除用SQL文字列の用意
                String sql15 = "DELETE FROM account_data WHERE _id = ?";
                // SQL文字列を元にプリベアードステートメントを取得
                SQLiteStatement stmt = db.compileStatement(sql15);
                // 変数のバインド
                stmt.bindLong(1, selectId);
                // 削除SQLの実行
                stmt.executeUpdateDelete();

                // 取得した精算先or精算元idのデータを削除
                String sql16 = "DELETE FROM account_data WHERE _id = ?";
                stmt = db.compileStatement(sql16);
                stmt.bindLong(1, transferPlaceId);
                stmt.executeUpdateDelete();

                // 残高の更新
                // 選択されたデータの残高の更新
                String sql17 = "UPDATE account_method SET balance = ? WHERE _id = ?";
                stmt = db.compileStatement(sql17);
                stmt.bindLong(1, deleteBalance + moneyPrice);
                stmt.bindLong(2, deleteMethodId);
                stmt.executeUpdateDelete();

                // 振替先or振替元の残高の更新
                String sql18 = "UPDATE account_method SET balance = ? WHERE _id = ?";
                stmt = db.compileStatement(sql18);
                stmt.bindLong(1, transferBalance + moneyPrice);
                stmt.bindLong(2, tranferMethodId);
                stmt.executeUpdateDelete();
            }

            // トーストで表示する文字列
            String show = "削除しました！";
            // トーストの表示
            Toast.makeText(DeleteAccountDataActivity.this, show, Toast.LENGTH_LONG).show();

            // インテントオブジェクトを生成
            Intent intent2 = new Intent(DeleteAccountDataActivity.this, HomeActivity.class);
            // [HOME]画面に遷移
            startActivity(intent2);
        }
    }

    // [キャンセル]ボタンをクリックしたときのリスナクラス
    private class NoListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            // トーストで表示する文字列
            String show = "キャンセルしました！";
            // トーストの表示
            Toast.makeText(DeleteAccountDataActivity.this, show, Toast.LENGTH_LONG).show();

            // インテントオブジェクトを生成
            Intent intent3 = new Intent(DeleteAccountDataActivity.this, HomeActivity.class);
            // [HOME]画面に遷移
            startActivity(intent3);
        }
    }
}