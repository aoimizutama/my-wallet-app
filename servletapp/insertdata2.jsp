<!-- データ登録画面（銀行バージョン）　-->

<%@ page contentType="text/html;charset=utf-8" %>

<!DOCTYPE html>
<html>

<head>
    <meta charset="UTF-8">
    <link rel="stylesheet" type="text/css" href="menu.css">
    <title>登録画面（銀行）</title>
</head>

<body>
    <ul class="topnav">
        <li><a href="/kakeiboapp/cashorbank.jsp">現金or銀行</a></li>
        <li><a href="/kakeiboapp/home2.jsp">HOME（銀行）</a></li>
    </ul>
    
    <h1>登録画面（銀行）</h1>

    <form action="/kakeiboapp/InsertServlet2" method="POST">     <!-- 登録ボタンからInsertServlet2.javaへ　-->
        <label>日付</label><input type="date" name="date"><br>
        <label>内訳</label>
        <select name="item">
            <option value="1">食費</option>
            <option value="2">光熱費</option>
            <option value="3">住宅費</option>
        </select><br>
        <label>引き出し</label><input type="number" name="getm" value="0"><br>
        <label>預け入れ</label><input type="number" name="outm" value="0"><br>
        <label>メモ</label><input type="text" name="memo">
        <p></p>
        <label>現金残</label><input type="number" name="cash" value=<%= request.getAttribute("cash") %> readonly><br>
        <label>銀行残</label><input type="number" name="bank" value=<%= request.getAttribute("bank") %> readonly><br>
        <p></p>
        <input type="submit" value="登録">
    </form>  
</body>

</html>