// 変更できるデータ一覧を表示する（現金バージョン）
// データをテーブルで表示させるためように変更

import java.io.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.util.*;

@WebServlet("/SelectupdateServlet1")
public class SelectupdateServlet1 extends HttpServlet {
    public void doPost(HttpServletRequest req, HttpServletResponse res)
      throws IOException, ServletException {
        final String URL = "jdbc:mysql://localhost/kakeibodb";
        final String USER = "root";
        final String PASS = "pass";

        // acc_data2からデータを抽出
        String sql1 = "SELECT * FROM acc_data2 ORDER BY id DESC";
        String sql3 = "select * from item";
    
        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;
        ResultSet rs3 = null;      

        ArrayList<String[]> list = new ArrayList<String[]>();

        try{
            //DBに接続
            con = DriverManager.getConnection(URL, USER, PASS);
            //ステートメントを生成
            stmt = con.createStatement();
            //SQLを実行
            rs = stmt.executeQuery(sql1);
            rs3 = stmt.executeQuery(sql3);

            //検索結果の処理
            while (rs.next()) {
                String[] data = new String[6];
                data[0] = rs.getString("id");
                data[1] = rs.getString("acc_date");
                String item_code = rs.getString("item_code");
                while(rs3.next()){
                    if (rs3.getString("item_code").equals(item_code)) {
                        data[2] = rs3.getString("item_name");
                        break;
                    }
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

        //settingテーブルから現金残を抽出
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

        ///selectupdate1.jspにフォワード
        RequestDispatcher rd = req.getRequestDispatcher("/selectupdate1.jsp");
        rd.forward(req, res);
    }

    public void doGet(HttpServletRequest req, HttpServletResponse res)
      throws IOException, ServletException {
        doPost(req, res);
    }
}