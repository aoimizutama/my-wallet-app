// 更新画面に入力されたデータを更新する（現金バージョン）

import java.io.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;

@WebServlet("/UpdateServlet1")
public class UpdateServlet1 extends HttpServlet {
  public void doPost(HttpServletRequest req, HttpServletResponse res)
    throws IOException, ServletException {
      req.setCharacterEncoding("UTF-8"); // 文字化け防止

      // フォームの値を受け取る
      int id = Integer.parseInt(req.getParameter("id"));
      String date = req.getParameter("date");
      int item = Integer.parseInt(req.getParameter("item"));
      int getm = Integer.parseInt(req.getParameter("getm"));
      int outm = Integer.parseInt(req.getParameter("outm"));
      String memo = req.getParameter("memo");
      int cash_after = Integer.parseInt(req.getParameter("cash_after"));

      final String URL = "jdbc:mysql://localhost/kakeibodb";
      final String USER = "root";
      final String PASS = "pass";

      // 変更画面の値からacc_data2テーブルのデータを変更する
      String sql1 = "UPDATE acc_data2 SET acc_date=?, item_code=?, get_money=?, out_money=?, memo=? WHERE id=?";
      
      Connection con = null;
      PreparedStatement stmt = null;

      try{
          //DBに接続
          con = DriverManager.getConnection(URL, USER, PASS);
          //ステートメントを生成
          stmt = con.prepareStatement(sql1);
          //SQLを実行
          stmt.setString(1, date);
          stmt.setInt(2, item);
          stmt.setInt(3, getm);
          stmt.setInt(4, outm);
          stmt.setString(5, memo);
          stmt.setInt(6, id);
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
      String sql2 = "UPDATE setting SET cash=? WHERE id=1";
      
      con = null;
      stmt = null;

      try{
          //DBに接続
          con = DriverManager.getConnection(URL, USER, PASS);
          //ステートメントを生成
          stmt = con.prepareStatement(sql2);
          //SQLを実行
          int cash = cash_after + getm - outm;
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





