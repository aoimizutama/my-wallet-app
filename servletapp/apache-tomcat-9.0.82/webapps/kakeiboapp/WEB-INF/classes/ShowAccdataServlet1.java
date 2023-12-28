// データ登録後にデータベースから全てのデータを取得（現金バージョン）
// データをテーブルで表示させるためように変更

import java.io.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.util.*;

@WebServlet("/ShowAccdataServlet1")
public class ShowAccdataServlet1 extends HttpServlet {
    public void doPost(HttpServletRequest req, HttpServletResponse res)
      throws IOException, ServletException {
        final String URL = "jdbc:mysql://localhost/kakeibodb";
        final String USER = "root";
        final String PASS = "pass";

        //acc_data2テーブルからデータを抽出する
        String sql1 = "SELECT * FROM acc_data2 ORDER BY id DESC";
    
        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;

        ArrayList<String[]> list = new ArrayList<String[]>();

        try{
            //DBに接続
            con = DriverManager.getConnection(URL, USER, PASS);
            //ステートメントを生成
            stmt = con.createStatement();
            //SQLを実行
            rs = stmt.executeQuery(sql1);
            //検索結果の処理
            while (rs.next()) {
                String[] data = new String[6];
                data[0] = rs.getString("id");
                data[1] = rs.getString("acc_date");
                String item_code = rs.getString("item_code");
                if (item_code.equals("1")) {
                    data[2] = "食費";
                } else if (item_code.equals("2")) {
                    data[2] = "光熱費";
                } else {
                    data[2] = "住宅費";
                }
                data[3] = rs.getString("get_money");
                data[4] = rs.getString("out_money");
                data[5] = rs.getString("memo");
                list.add(data);
            }
            req.setAttribute("data", list);
        } catch(Exception e){
            e.printStackTrace();
        } finally {
            try{
                //リソースを解放
                if(rs != null) rs.close();
                if(stmt != null) stmt.close();
                if(con != null) con.close();
            } catch(Exception e){
                e.printStackTrace();
            }
        }

        //settingテーブルの現金残を抽出
        String sql2 = "SELECT * FROM setting WHERE id=1";
        
        con = null;
        stmt = null;
        rs = null;

        try{
            //DBに接続
            con = DriverManager.getConnection(URL, USER, PASS);
            //ステートメントを生成
            stmt = con.createStatement();
            //SQLを実行
            rs = stmt.executeQuery(sql2);
            //検索結果の処理
            while (rs.next()) {
                int cash = rs.getInt("cash");
                req.setAttribute("cash", cash);
            }
        } catch(Exception e){
            e.printStackTrace();
        } finally {
            try{
                //リソースを解放
                if(rs != null) rs.close();
                if(stmt != null) stmt.close();
                if(con != null) con.close();
            } catch(Exception e){
                e.printStackTrace();
            }
        }

        //showaccdata1.jspにフォワード
        RequestDispatcher rd = req.getRequestDispatcher("/showaccdata1.jsp");
        rd.forward(req, res);
    }

    public void doGet(HttpServletRequest req, HttpServletResponse res)
      throws IOException, ServletException {
        doPost(req, res);
    }
}