// 現金残、預金残をデータベースから取得（銀行バージョン）

import java.io.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.util.*;

@WebServlet("/SelectcashServlet2")
public class SelectcashServlet2 extends HttpServlet {
    public void doPost(HttpServletRequest req, HttpServletResponse res)
        throws IOException, ServletException {
    final String URL = "jdbc:mysql://localhost/kakeibodb";
    final String USER = "root";
    final String PASS = "pass";

    String sql = "SELECT * FROM setting WHERE id=1";
    
    Connection con = null;
    Statement stmt = null;
    ResultSet rs = null;

    try{
        //DBに接続
        con = DriverManager.getConnection(URL, USER, PASS);
        //ステートメントを生成
        stmt = con.createStatement();
        //SQLを実行
        rs = stmt.executeQuery(sql);
        //検索結果の処理
        while (rs.next()) {
            int cash = rs.getInt("cash");
            req.setAttribute("cash", cash);
            int bank = rs.getInt("bank");
            req.setAttribute("bank", bank);
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

    //insertdata2.jspにフォワード
    RequestDispatcher rd = req.getRequestDispatcher("/insertdata2.jsp");
    rd.forward(req, res);
    }

    public void doGet(HttpServletRequest req, HttpServletResponse res)
        throws IOException, ServletException {
    doPost(req, res);
    }
}