// 更新対象の登録済みデータを更新画面に渡す（現金バージョン）

import java.io.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;

@WebServlet("/CopydataServlet1")
public class CopydataServlet1 extends HttpServlet {
  public void doPost(HttpServletRequest req, HttpServletResponse res)
    throws IOException, ServletException {
      req.setCharacterEncoding("UTF-8"); // 文字化け防止

      // idの値を受け取る
      int id = Integer.parseInt(req.getParameter("id"));
      req.setAttribute("id", id);

      // 残金の値を受け取る
      int cash = Integer.parseInt(req.getParameter("cash"));
      req.setAttribute("cash", cash);
      
      final String URL = "jdbc:mysql://localhost/kakeibodb";
      final String USER = "root";
      final String PASS = "pass";

      //acc_data2テーブルから選択されたidのデータを取得
      String sql = "SELECT * FROM acc_data2 WHERE id = ?";
      
      Connection con = null;
      PreparedStatement stmt = null;
      ResultSet rs = null;

      try{
          //DBに接続
          con = DriverManager.getConnection(URL, USER, PASS);
          //ステートメントを生成
          stmt = con.prepareStatement(sql);
          //SQLを実行
          stmt.setInt(1, id);
          rs = stmt.executeQuery();
          //検索結果の処理
          while (rs.next()) {
              String acc_date = rs.getString("acc_date");
              req.setAttribute("acc_date", acc_date);
              int item_code = rs.getInt("item_code");
              req.setAttribute("item_code", item_code);
              int get_money = rs.getInt("get_money");
              req.setAttribute("get_money", get_money);
              int out_money = rs.getInt("out_money");
              req.setAttribute("out_money", out_money);
              String memo = rs.getString("memo");
              req.setAttribute("memo", memo);
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

      //updatedata1.jspにフォワード
      RequestDispatcher rd = req.getRequestDispatcher("/updatedata1.jsp");
      rd.forward(req, res);
  }

  public void doGet(HttpServletRequest req, HttpServletResponse res)
    throws IOException, ServletException {
      doPost(req, res);
  }
}