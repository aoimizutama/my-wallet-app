// 現金残をデータベースから取得（現金バージョン）

import java.io.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.util.*;

@WebServlet("/SelectcashServlet1")
public class SelectcashServlet1 extends HttpServlet {
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

    //insertdata1.jspにフォワード
    RequestDispatcher rd = req.getRequestDispatcher("/insertdata1.jsp");
    rd.forward(req, res);
    }

    public void doGet(HttpServletRequest req, HttpServletResponse res)
        throws IOException, ServletException {
    doPost(req, res);
    }
}