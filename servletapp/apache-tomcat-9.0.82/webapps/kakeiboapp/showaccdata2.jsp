<!-- データ一覧を表示する画面（銀行バージョン）　-->
<!-- データをテーブルで表示させるためように変更 -->

<%@page contentType="text/html;charset=utf-8" %>
<%@ page import="java.util.ArrayList"%>
<% ArrayList<String[]> list = (ArrayList<String[]>) request.getAttribute("data"); %>

<html>

<head>
    <meta charset="UTF-8">
    <link rel="stylesheet" type="text/css" href="example.css">
    <link rel="stylesheet" type="text/css" href="menu.css">
    <title>表示画面（銀行）</title>
</head>

<body>
    <ul class="topnav">
        <li><a href="/kakeiboapp/cashorbank.jsp">現金or銀行</a></li>
        <li><a href="/kakeiboapp/home2.jsp">HOME（銀行）</a></li>
    </ul>
    
    <h1>表示画面（銀行）</h1>

    <label>現金残</label><input type="number" name="cash" value=<%= request.getAttribute("cash") %> readonly><br>
    <label>銀行残</label><input type="number" name="bank" value=<%= request.getAttribute("bank") %> readonly>
    <p></p>

    <!-- データ一覧を表示　-->
    <div class="example">
        <table>
            <tr>
                <th>id</th>
                <th>日付</th>
                <th>内訳</th>
                <th>引き出し金</th>
                <th>預け入れ金</th>
                <th>メモ</th>
            </tr>
            <%
                for (int i = 0; i < list.size(); i++) {
                    String[] data = list.get(i);
                    out.print("<tr><th>"+data[0]+"</th>");
                    out.print("<td>"+data[1]+"</td>");
                    out.print("<td>"+data[2]+"</td>");
                    out.print("<td>"+data[3]+"</td>");
                    out.print("<td>"+data[4]+"</td>");
                    out.print("<td>"+data[5]+"</td></tr>");
                }
            %>
        </table>
    </div>
</body>

</html>