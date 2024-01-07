// 選択されたidのデータを削除する（現金バージョン）

import java.io.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;

@WebServlet("/DeletedataServlet1")
public class DeletedataServlet1 extends HttpServlet {
  public void doPost(HttpServletRequest req, HttpServletResponse res)
    throws IOException, ServletException {
      req.setCharacterEncoding("UTF-8"); // 文字化け防止

      // id、残金の値を受け取る
      int id = Integer.parseInt(req.getParameter("id"));
      int cash = Integer.parseInt(req.getParameter("cash"));

      int get_money = 0;
      int out_money = 0;

      final String URL = "jdbc:mysql://localhost/kakeibodb";
      final String USER = "root";
      final String PASS = "pass";

      //選択されたidのデータをacc_data2テーブルから取得
      String sql1 = "SELECT * FROM acc_data2 WHERE id = ?";
      
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

      //選択されたidのデータをacc_data2テーブルから削除
      String sql2 = "DELETE FROM acc_data2 WHERE id=?";
      
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

      // 現金残を更新する
      String sql3 = "UPDATE setting SET cash=? WHERE id=1";
      
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

      //home1.jspにフォワード
      RequestDispatcher rd = req.getRequestDispatcher("/home1.jsp");
      rd.forward(req, res);
  }

  public void doGet(HttpServletRequest req, HttpServletResponse res)
    throws IOException, ServletException {
      doPost(req, res);
  }
}





