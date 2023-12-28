<!-- 登録画面（現金バージョン）　-->

<%@ page contentType="text/html;charset=utf-8" %>

<!DOCTYPE html>
<html>

<head>
    <meta charset="UTF-8">
    <link rel="stylesheet" type="text/css" href="menu.css">
    <title>登録画面（現金）</title>
</head>

<body>
    <ul class="topnav">
        <li><a href="/kakeiboapp/cashorbank.jsp">現金or銀行</a></li>
        <li><a href="/kakeiboapp/home1.jsp">HOME（現金）</a></li>
    </ul>

    <h1>登録画面（現金）</h1>

    <form action="/kakeiboapp/InsertServlet1" method="POST">     <!-- 登録ボタンからInsertServlet1.javaへ　-->
        <label>日付</label><input type="date" name="date"><br>
        <label>内訳</label>
        <select name="item">
            <option value="1">食費</option>
            <option value="2">光熱費</option>
            <option value="3">住宅費</option>
        </select><br>
        <label>入金</label><input type="number" name="getm" value="0"><br>
        <label>出金</label><input type="number" name="outm" value="0"><br>
        <label>メモ</label><input type="text" name="memo">
        <p></p>
        <label>現金残</label><input type="number" name="cash" value=<%= request.getAttribute("cash") %> readonly><br>
        <p></p>
        <input type="submit" value="登録">
    </form>
</body>

</html>