# お小遣い帳アプリ

### 上のフォルダの内容
- pythonapp：Pythonを使ってGUIアプリを作成
- javaapp：Javaを使ってGUIアプリを作成
- servletapp：Java,servletを使ってWebアプリを作成
- androidapp：Javaを使ってAndroidアプリを作成

### 開発にあたって
大学ではPython言語を中心に学び自然言語の学習に取組んでいるものの、もっと身近なもので作った実感が湧く開発をしてみたいと考え、幼少期から使っていたお小遣い帳をパソコン上で管理できたら便利だと思いこのアプリを作成。

1. はじめに、PythonとデータベースのSQLite3でシンプルなGUIアプリを作成。（pythonappフォルダに入っているもの）
2. ただ、PythonはGUIが豊富でなく、調べるとJavaはGUIも豊富でかつ社会では必要に重宝されていると知ったため、Pythonで作ったものを勉強も兼ねてJavaに移行。（javaappフォルダに入っているもの）
3. その後、Javaを勉強しているとJavaには通信を利用したサーブレットという技術があることを知り、またその頃からネットサイトのシステムなどをエンジニア的な目で感じることができるようになったことから、お小遣い帳アプリをサーブレットを使用したものに移行。その際にデータベースをMySQLに変更。（servletappフォルダに入っているもの）
4. さらにその後、清算の都度、レシートを入力して現金残や、クレジット利用額が一目でわかれば便利だと感じ、現実的に利用しやすい形にするために、アンドロイドに移行。（androidappフォルダに入っているもの）手書きベースの小遣い帳を、単に入力に切替えた程度のものだが、一先ずは簡易的に使える程度の仕様になっている。ただ、レイアウトの調整など細かい修正は行っていないため、今後追加できればより使いやすくなると考えている。（実機での動作確認済み/機種：OPPO Reno3 A/Androidバージョン11）
