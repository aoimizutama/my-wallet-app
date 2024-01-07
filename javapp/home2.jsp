<!-- ホーム画面（銀行バージョン）　-->

<%@ page contentType="text/html;charset=utf-8" %>

<!DOCTYPE html>
<html>

<head>
    <meta charset="UTF-8">
    <link rel="stylesheet" type="text/css" href="menu.css">
    <title>HOME（銀行）</title>
</head>

<body>
    <ul class="topnav">
        <li><a href="/kakeiboapp/cashorbank.jsp">現金or銀行</a></li>
        <li><a href="/kakeiboapp/home2.jsp">HOME（銀行）</a></li>
    </ul>

    <h1>HOME（銀行）</h1>
    
    <form action="/kakeiboapp/SelectcashServlet2" method="POST">     <!-- 登録ボタンからSelectcashServlet2.javaへ　-->
        <input type="submit" value="登録">
    </form>

    <form action="/kakeiboapp/SelectupdateServlet2" method="POST">     <!-- 変更ボタンからSelectupdateServlet2.javaへ　-->
        <input type="submit" value="変更">
    </form>

    <form action="/kakeiboapp/ShowAccdataServlet2" method="POST">     <!-- 表示ボタンからShowAccdataServlet2.javaへ　-->
        <input type="submit" value="表示">
    </form>
</body>

</html>