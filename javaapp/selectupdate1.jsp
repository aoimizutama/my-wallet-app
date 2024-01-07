<!-- 変更データを選択する画面（現金バージョン）　-->
<!-- データをテーブルで表示させるためように変更 -->

<%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="java.util.ArrayList"%>
<% ArrayList<String[]> list = (ArrayList<String[]>) request.getAttribute("data"); %>

<html>

<head>
	<meta charset="UTF-8">
    <link rel="stylesheet" type="text/css" href="example.css">
	<link rel="stylesheet" type="text/css" href="menu.css">
    <title>変更データ選択画面（現金）</title>
</head>

<body>
	<ul class="topnav">
        <li><a href="/kakeiboapp/cashorbank.jsp">現金or銀行</a></li>
        <li><a href="/kakeiboapp/home1.jsp">HOME（現金）</a></li>
    </ul>
	
    <h1>変更データ選択画面（現金）</h1>

	<form action="/kakeiboapp/CopydataServlet1" method="POST">  <!-- 変更ボタンからCopydataServlet1.javaへ　-->
		<label>変更するデータのid</label><input type="number" name="id">
		<input type="submit" value="変更"><br>
		<label></label><input type="hidden" name="cash" value=<%= request.getAttribute("cash") %>>
	</form>

	<form action="/kakeiboapp/DeletedataServlet1" method="POST">  <!-- 削除ボタンからDeletedataServlet1.javaへ　-->
		<label>削除するデータのid</label><input type="number" name="id">
		<input type="submit" value="削除"><br>
		<p></p>
		<label>現金残</label><input type="number" name="cash" value=<%= request.getAttribute("cash") %> readonly>
	</form>

    <!-- データ一覧を表示　-->
	<div class="example">
		<table>
			<tr>
				<th>id</th>
				<th>日付</th>
				<th>内訳</th>
				<th>入金</th>
				<th>出金</th>
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