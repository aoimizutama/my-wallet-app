<!-- 設定画面 -->

<%@ page contentType="text/html;charset=utf-8" %>

<!DOCTYPE html>
<html>

<head>
    <meta charset="UTF-8">
    <title>設定画面</title>
</head>

<body>
    <h1>設定画面</h1>

    <form action="/kakeiboapp/SettingServlet" method="POST">     <!-- 設定ボタンからSettingServlet.javaへ　-->
        <label>ユーザ名</label><input type="text" name="user_name"><br>
        <label>ユーザid</label><input type="text" name="user_id"><br>
        <label>パスワード</label><input type="text" name="pas"><br>
        <label>現金残</label><input type="number" name="cash"><br>
        <label>預金残</label><input type="number" name="bank"><br>
        <input type="submit" value="設定">
    </form>
</body>

</html>