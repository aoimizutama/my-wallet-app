// 設定画面で登録されたデータをsettingテーブルに追加

import java.io.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;

@WebServlet("/SettingServlet")
public class SettingServlet extends HttpServlet {
  public void doPost(HttpServletRequest req, HttpServletResponse res)
      throws IOException, ServletException {
    req.setCharacterEncoding("UTF-8"); // 文字化け防止

    // フォームの値を受け取る
    String user_name = req.getParameter("user_name");
    String user_id = req.getParameter("user_id");
    String pas = req.getParameter("pas");
    int cash = Integer.parseInt(req.getParameter("cash"));
    int bank = Integer.parseInt(req.getParameter("bank"));

    final String URL = "jdbc:mysql://localhost/kakeibodb";
    final String USER = "root";
    final String PASS = "pass";

    // 実行するSQL
    String sql = "INSERT INTO setting (user_name, user_id, pas, cash, bank) VALUES(?,?,?,?,?)";
    
    Connection con = null;
    PreparedStatement stmt = null;

    try{
      //DBに接続
      con = DriverManager.getConnection(URL, USER, PASS);
      //ステートメントを生成
      stmt = con.prepareStatement(sql);
      //SQLを実行
      stmt.setString(1, user_name);
      stmt.setString(2, user_id);
      stmt.setString(3, pas);
      stmt.setInt(4, cash);
      stmt.setInt(5, bank);
      stmt.executeUpdate();
      //検索結果の処理
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
    //cashorbank.jspにフォワード
    RequestDispatcher rd = req.getRequestDispatcher("/cashorbank.jsp");
    rd.forward(req, res);
  }

  public void doGet(HttpServletRequest req, HttpServletResponse res)
      throws IOException, ServletException {
    doPost(req, res);
  }
}