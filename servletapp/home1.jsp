<!-- ホーム画面（現金バージョン）　-->

<%@ page contentType="text/html;charset=utf-8" %>

<!DOCTYPE html>
<html>

<head>
    <meta charset="UTF-8">
    <link rel="stylesheet" type="text/css" href="menu.css">
    <title>HOME（現金）</title>
</head>

<body>
    <ul class="topnav">
        <li><a href="/kakeiboapp/cashorbank.jsp">現金or銀行</a></li>
        <li><a href="/kakeiboapp/home1.jsp">HOME（現金）</a></li>
    </ul>

    <h1>HOME（現金）</h1>
    
    <form action="/kakeiboapp/SelectcashServlet1" method="POST">     
        <input type="submit" value="登録">
    </form>

    <form action="/kakeiboapp/SelectupdateServlet1" method="POST">     <!-- 変更ボタンからSelectupdateServlet1.javaへ　-->
        <input type="submit" value="変更">
    </form>

    <form action="/kakeiboapp/ShowAccdataServlet1" method="POST">     <!-- 表示ボタンからShowAccdataServlet1.javaへ　-->
        <input type="submit" value="表示">
    </form>
</body>

</html>