// 更新画面に入力されたデータを更新する（銀行バージョン）

import java.io.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;

@WebServlet("/UpdateServlet2")
public class UpdateServlet2 extends HttpServlet {
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
      int bank_after = Integer.parseInt(req.getParameter("bank_after"));

      final String URL = "jdbc:mysql://localhost/kakeibodb";
      final String USER = "root";
      final String PASS = "pass";

      // 変更画面の値からacc_data3テーブルのデータを変更する
      String sql1 = "UPDATE acc_data3 SET acc_date=?, item_code=?, get_money=?, out_money=?, memo=? WHERE id=?";
      
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

      // 現金残、銀行残を更新する
      String sql2 = "UPDATE setting SET cash=?, bank=? WHERE id=1";
      
      con = null;
      stmt = null;

      try{
          //DBに接続
          con = DriverManager.getConnection(URL, USER, PASS);
          //ステートメントを生成
          stmt = con.prepareStatement(sql2);
          //SQLを実行
          int cash = cash_after + getm - outm;
          int bank = bank_after - getm + outm;
          stmt.setInt(1, cash);
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





