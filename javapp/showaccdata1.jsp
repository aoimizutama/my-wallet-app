<!-- データ一覧を表示する画面（現金バージョン）　-->
<!-- データをテーブルで表示させるためように変更 -->

<%@page contentType="text/html;charset=utf-8" %>
<%@ page import="java.util.ArrayList"%>
<% ArrayList<String[]> list = (ArrayList<String[]>) request.getAttribute("data"); %>

<html>

<head>
	<meta charset="UTF-8">
    <link rel="stylesheet" type="text/css" href="example.css">
	<link rel="stylesheet" type="text/css" href="menu.css">
    <title>表示画面（現金）</title>
</head>

<body>
	<ul class="topnav">
        <li><a href="/kakeiboapp/cashorbank.jsp">現金or銀行</a></li>
        <li><a href="/kakeiboapp/home1.jsp">HOME（現金）</a></li>
    </ul>
	
    <h1>表示画面（現金）</h1>

	<label>現金残</label><input type="number" name="cash" value=<%= request.getAttribute("cash") %> readonly>
	<p></p>
	
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