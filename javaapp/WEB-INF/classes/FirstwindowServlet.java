// settingテーブルにデータが登録済みの場合は登録画面を表示、データがない場合は設定画面を表示

import java.io.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;

@WebServlet("/FirstwindowServlet")
public class FirstwindowServlet extends HttpServlet {
  public void doPost(HttpServletRequest req, HttpServletResponse res)
      throws IOException, ServletException {
    req.setCharacterEncoding("UTF-8"); // 文字化け防止

    final String URL = "jdbc:mysql://localhost/kakeibodb";
    final String USER = "root";
    final String PASS = "pass";

    // 実行するSQL
    String sql = "SELECT * FROM setting WHERE id=1";
    
    Connection con = null;
    Statement stmt = null;
    ResultSet rs = null;

    int count = 0;

    try{
        //DBに接続
        con = DriverManager.getConnection(URL, USER, PASS);
        //ステートメントを生成
        stmt = con.createStatement();
        //SQLを実行
        rs = stmt.executeQuery(sql);
        //検索結果の処理
        while (rs.next()) {
            count = 1;
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
    
    //もしデータベースにデータがあった場合、cashorbank.jspにフォワード
    //もしデータベースにデータがなかった場合、setting.jspにフォワード
    if (count == 1) {
        RequestDispatcher rd = req.getRequestDispatcher("/cashorbank.jsp");
        rd.forward(req, res);
    } else {
        RequestDispatcher rd = req.getRequestDispatcher("/setting.jsp");
        rd.forward(req, res);
    }
  }

  public void doGet(HttpServletRequest req, HttpServletResponse res)
      throws IOException, ServletException {
    doPost(req, res);
  }
}





