<!-- データ変更画面（現金バージョン）　-->

<%@ page contentType="text/html;charset=utf-8" %>

<!DOCTYPE html>
<html>

<head>
    <meta charset="UTF-8">
    <link rel="stylesheet" type="text/css" href="menu.css">
    <title>変更画面（現金）</title>
</head>

<body>
    <ul class="topnav">
        <li><a href="/kakeiboapp/cashorbank.jsp">現金or銀行</a></li>
        <li><a href="/kakeiboapp/home1.jsp">HOME（現金）</a></li>
    </ul>
    
    <!-- 登録済みの入金、出金を取り消した残金の変数を作成 -->
    <% int cash_after = (int)request.getAttribute("cash") - (int)request.getAttribute("get_money") + (int)request.getAttribute("out_money"); %>

    <!-- 登録済みのデータを受け取って各欄に表示する -->
    <h1>変更画面（現金）</h1>

    <form action="/kakeiboapp/UpdateServlet1" method="POST">   <!-- 変更ボタンからUpdateServlet1.javaへ　-->
        <label>id</label><input type="number" name="id" value=<%= request.getAttribute("id") %>><br>
        <label>日付</label><input type="date" name="date" value=<%= request.getAttribute("acc_date") %>><br>
        <label>内訳</label> <!-- もともと入っていた内訳の値をセレクトボックスの初期値に入れる　-->
        <% int item_code  = (int)request.getAttribute("item_code"); %>
        <% if (item_code == 1) { %>
            <select name="item">
                <option value="1" selected>食費</option>
                <option value="2">光熱費</option>
                <option value="3">住宅費</option>
            </select><br>
        <% } else if (item_code == 2) { %>
            <select name="item">
                <option value="1">食費</option>
                <option value="2" selected>光熱費</option>
                <option value="3">住宅費</option>
            </select><br>
        <% } else { %>
            <select name="item">
                <option value="1">食費</option>
                <option value="2">光熱費</option>
                <option value="3" selected>住宅費</option>
            </select><br>
        <% } %>
        <label>入金</label><input type="number" name="getm" value=<%= request.getAttribute("get_money") %>><br>
        <label>出金</label><input type="number" name="outm" value=<%= request.getAttribute("out_money") %>><br>
        <label>メモ</label><input type="text" name="memo" value=<%= request.getAttribute("memo") %>>
        <p></p>
        <label>現金残</label><input type="number" name="cash_before" value=<%= request.getAttribute("cash") %> readonly><br>
        <input type="hidden" name="cash_after" value=<%= cash_after %>><br>
        <input type="submit" value="変更">
    </form>
</body>

</html>