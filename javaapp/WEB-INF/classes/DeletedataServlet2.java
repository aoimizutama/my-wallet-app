// 選択されたidのデータを削除する（銀行バージョン）

import java.io.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;

@WebServlet("/DeletedataServlet2")
public class DeletedataServlet2 extends HttpServlet {
  public void doPost(HttpServletRequest req, HttpServletResponse res)
    throws IOException, ServletException {
      req.setCharacterEncoding("UTF-8"); // 文字化け防止

      // id、現金残、銀行残の値を受け取る
      int id = Integer.parseInt(req.getParameter("id"));
      int cash = Integer.parseInt(req.getParameter("cash"));
      int bank = Integer.parseInt(req.getParameter("bank"));

      int get_money = 0;
      int out_money = 0;

      final String URL = "jdbc:mysql://localhost/kakeibodb";
      final String USER = "root";
      final String PASS = "pass";

      //選択されたidのデータをacc_data3テーブルから取得
      String sql1 = "SELECT * FROM acc_data3 WHERE id = ?";
      
      Connection con = null;
      PreparedStatement stmt = null;
      ResultSet rs = null;

      try{
          //DBに接続
          con = DriverManager.getConnection(URL, USER, PASS);
          //ステートメントを生成
          stmt = con.prepareStatement(sql1);
          //SQLを実行
          stmt.setInt(1, id);
          rs = stmt.executeQuery();
          //検索結果の処理
          while (rs.next()) {
              get_money = rs.getInt("get_money");
              out_money = rs.getInt("out_money");
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

      //選択されたidのデータをacc_data3テーブルから削除
      String sql2 = "DELETE FROM acc_data3 WHERE id=?";
      
      con = null;
      stmt = null;

      try{
          //DBに接続
          con = DriverManager.getConnection(URL, USER, PASS);
          //ステートメントを生成
          stmt = con.prepareStatement(sql2);
          //SQLを実行
          stmt.setInt(1, id);
          stmt.executeUpdate();
      } catch(Exception e){
          e.printStackTrace();
      } finally {
          try{
              //リソースを解放
              if(stmt != null) stmt.close();
              if(con != null) con.close();
          } catch(Exception e){
              e.printStackTrace();
          }
      }

      // 現金残、銀行残を更新する
      String sql3 = "UPDATE setting SET cash=?, bank=? WHERE id=1";
      
      con = null;
      stmt = null;

      try{
          //DBに接続
          con = DriverManager.getConnection(URL, USER, PASS);
          //ステートメントを生成
          stmt = con.prepareStatement(sql3);
          //SQLを実行
          cash = cash - get_money + out_money;
          stmt.setInt(1, cash);
          bank = bank + get_money - out_money;
          stmt.setInt(2, bank);
          stmt.executeUpdate();
      } catch(Exception e){
          e.printStackTrace();
      } finally {
          try{
              //リソースを解放
              if(stmt != null) stmt.close();
              if(con != null) con.close();
          } catch(Exception e){
              e.printStackTrace();
          }
      }

      //home2.jspにフォワード
      RequestDispatcher rd = req.getRequestDispatcher("/home2.jsp");
      rd.forward(req, res);
  }

  public void doGet(HttpServletRequest req, HttpServletResponse res)
    throws IOException, ServletException {
      doPost(req, res);
  }
}





