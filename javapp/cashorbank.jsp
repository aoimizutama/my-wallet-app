<!-- 現金処理か銀行処理かを選択　-->

<%@ page contentType="text/html;charset=utf-8" %>

<!DOCTYPE html>
<html>

<head>
    <meta charset="UTF-8">
    <title>現金or銀行</title>
</head>

<body>
    <h1>現金処理か銀行処理かを選択</h1>
    
    <form action="/kakeiboapp/home1.jsp" method="POST">     <!-- 現金処理の場合home1.jspへ　-->
        <input type="submit" value="現金">
    </form>
    
    <form action="/kakeiboapp/home2.jsp" method="POST">     <!-- 銀行処理の場合home2.jspへ　-->
        <input type="submit" value="銀行">
    </form>
</body>

</html>