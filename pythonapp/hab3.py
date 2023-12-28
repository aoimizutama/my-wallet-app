#変更ボタンを押した時の凡その処理を埋め込み済み/////////////////r5.8.19

# -*- coding: utf-8 -*-

import tkinter as tk
# python2の場合は、import Tkinter as tk
import tkinter.ttk as ttk
# python2の場合は、import ttk
import sqlite3

#グローバル変数として/登録内容の変更時に使用
change_id = 0
change_d = ""
change_u = 0
change_k = 0

# 登録画面のGUI
def create_gui():
    # ----------------------------------------
    # コールバック関数群
    # ----------------------------------------    
    # 表示ボタンが押下されたときのコールバック関数
    def select_button():
        root.destroy()
        select_gui()
    # ----------------------------------------
    # 終了ボタンが押下されたときのコールバック関数
    def quit_button():
        root.destroy()
    # ----------------------------------------
    # 登録ボタンがクリックされた時にデータをDBに登録するコールバック関数
    def create_sql(item_name):

        #if entry1の評価 ///////////////////////

        # データベースに接続
        c = sqlite3.connect("database3.db")
        # item_nameをWHERE句に渡してitem_codeを取得する
        item_code = c.execute("""
                    SELECT item_code FROM item
                    WHERE item_name = '{}'
                    """.format(item_name))
        item_code = item_code.fetchone()[0]

        # 金額の読み取り
        amount = entry4.get()

        # 日付の読み取り
        acc_data = entry2.get() #.replace("/","-") //不要?

        try: #エラーでなければ日付を[YYYY/MM/DD](0足し)に変換して登録
            from datetime import datetime
            #入力値が日付でないとエラー/エラーでなければ変数dtへ値を入力
            dt = datetime.strptime(acc_data, "%Y/%m/%d")
            #入力値を[YYYY/MM/DD](0足し)に変換
            acc_data = datetime.strftime(dt, "%Y/%m/%d")
        
        # SQLを発行してDBへ登録
        # python2の場合は、ユニコード文字列でsqlite3に渡す
        # また、コミットする場合は、commitメソッドを用いる
            c.execute("""
            INSERT INTO acc_data(acc_date,item_code,amount)
            VALUES('{}',{},{});
            """.format(acc_data,item_code,amount))
            c.execute("COMMIT;")
            print("登録しました！")
        # ドメインエラーなどにより登録できなかった場合のエラー処理
        except:
            print("エラー原因 : 1.日付？ or 2.変更モードで登録ボタンを押した or etc.")
            #変更モードで登録をボタンを押すとsqlエラーになる、単に金額マークの問題であるが、
            #変更モード時はif分岐をつかって登録を無効にする処理が必要なので、今はそのままにしている。
            #最終的にはプログラム変更が必要！
    # ----------------------------------------

    # 更新ボタンがクリックされた時にデータをDBに更新するコールバック関数
    def change_sql(item_name): #//変更ボタンの作成

        # データベースに接続
        c = sqlite3.connect("database3.db")
        # item_nameをWHERE句に渡してitem_codeを取得する
        item_code = c.execute("""
                    SELECT item_code FROM item
                    WHERE item_name = '{}'
                    """.format(item_name))
        item_code = item_code.fetchone()[0]

        # IDは読み込まれているグローバルidを使用
        global change_id

        # 金額の読み取り
        amount = entry4.get()
        # 日付の読み取り
        acc_data = entry2.get() #.replace("/","-") //不要?

        try: #エラーでなければ日付を[YYYY/MM/DD](0足し)に変換して登録
            from datetime import datetime
            #入力値が日付でないとエラー/エラーでなければ変数dtへ値を入力
            dt = datetime.strptime(acc_data, "%Y/%m/%d")
            #入力値を[YYYY/MM/DD](0足し)に変換
            acc_data = datetime.strftime(dt, "%Y/%m/%d")

        # SQLを発行してDBへ登録
        # python2の場合は、ユニコード文字列でsqlite3に渡す
        # また、コミットする場合は、commitメソッドを用いる
            c.execute("UPDATE acc_data SET acc_date=?, item_code=?, amount=? WHERE id = ?",
                       (acc_data, item_code, amount, change_id))
            c.execute("COMMIT;")
            print("更新しました！")

            change_id = 0 #グローバル変数idのリフレッシュ
        # ドメインエラーなどにより登録できなかった場合のエラー処理
        except:
            print("エラー／日付？")

    #------------------------------------------
    # 内訳テーブル(item)にあるitem_nameのタプルを作成する
    def createitemname():
        # データベースの接続
        c = sqlite3.connect("database3.db")
        # 空の「リスト型」を定義
        li = []
        # SELECT文を発行し、item_nameを取得し、for文で回す////////　タプル⇒取出処理が上手くいかないので
        #for r in c.execute("SELECT item_name FROM item"): ////　下記へ変更（forループではなく、シンプルに一括代入し、
            #item_nameをリストに追加する ///////////////////////   リスト化した方が色々と使い勝手がよい）
            #li.append(r)
        # リスト型のliをタプル型に変換して、ファンクションに戻す
        #return tuple(li)///////////////////////////////////////////////////////////////////////
        li = c.execute("SELECT * FROM item")
        return tuple(li)
    # ----------------------------------------
    
    # 空のデータベースを作成して接続する
    dbname = "database3.db"
    c = sqlite3.connect(dbname)
    c.execute("PRAGMA foreign_keys = 1")

    # 既にデータベースが登録されている場合は、ddlの発行でエラーが出るのでexceptブロックで回避する
    try:
        # itemテーブルの定義
        ddl = """
        CREATE TABLE item
        (
           item_code INTEGER PRIMARY KEY AUTOINCREMENT,
           item_name TEXT NOT NULL UNIQUE
        )
         """
        # SQLの発行
        c.execute(ddl)
        # acc_dataテーブルの定義    
        ddl = """
        CREATE TABLE acc_data
        ( 
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            acc_date DATE NOT NULL,
            item_code INTEGER NOT NULL,
            amount INTEGER,
            FOREIGN KEY(item_code) REFERENCES item(item_code)
        )
        """
        # itemテーブルへリファレンスデータの登録
        c.execute(ddl)
        c.execute("INSERT INTO item VALUES(1,'食費')")
        c.execute("INSERT INTO item VALUES(2,'住宅費')")
        c.execute("INSERT INTO item VALUES(3,'光熱費')")
        c.execute("COMMIT")
    except:
           pass

    # rootフレームの設定
    root = tk.Tk()
    root.title("家計簿アプリ")
    root.geometry("300x400")

    # メニューの設定
    frame = tk.Frame(root,bd=2,relief="ridge")
    frame.pack(fill="x")
    button1 = tk.Button(frame,text="新規")
    button1.pack(side="left")
    button2 = tk.Button(frame,text="表示",command=select_button)
    button2.pack(side="left")
    button3 = tk.Button(frame,text="終了",command=quit_button)
    button3.pack(side="right")

    # 入力画面ラベルの設定
    label1 = tk.Label(root,text="【入力画面】",font=("",16),height=2)
    label1.pack(fill="x")

    global change_id

    if change_id == 0: #idが0の時、つまり新規
        frame1 = tk.Frame(root,pady=10)
        frame1.pack()
        label2 = tk.Label(frame1,font=("",14),text="番号")
        label2.pack(side="left")
        entry1 = tk.Entry(frame1,font=("",14),justify="center",width=15,state="readonly")
        entry1.pack(side="left")

        # 日付のラベルとエントリーの設定
        frame2 = tk.Frame(root,pady=10)
        frame2.pack()
        label3 = tk.Label(frame2,font=("",14),text="日付")
        label3.pack(side="left")
        entry2 = tk.Entry(frame2,font=("",14),justify="center",width=15)
        entry2.pack(side="left")

        # 内訳のラベルとエントリーの設定
        frame3 = tk.Frame(root,pady=10)
        frame3.pack()
        label4 = tk.Label(frame3,font=("",14),text="内訳")
        label4.pack(side="left")
        # 内訳コンボボックスの作成
        combo = ttk.Combobox(frame3, state='readonly',font=("",14),width=13)

        list1 = list(createitemname()) #タプル値をリストに変換

        list2 = [] #空リスト作成
        for i in range(len(list1)): #list1のサイズをループ
             list2.append(list1[i][1]) #多重化リストを単一リスト(nameのみ)に整理
        #注）この単一リスト化作業はcreateitemname内でやっておいた方がシンプルかもしれない
             
        combo["values"] = list2 #単一化されたリストの代入
        combo.current(0) #リスト最初のデータを表示
        combo.pack()

        # 金額のラベルとエントリーの設定
        frame4 = tk.Frame(root,pady=10)
        frame4.pack()
        label5 = tk.Label(frame4,font=("",14),text="金額")
        label5.pack(side="left")
        entry4 = tk.Entry(frame4,font=("",14),justify="center",width=15)
        entry4.pack(side="left")
    else: #idが0でない時、つまり変更
        frame1 = tk.Frame(root,pady=10)
        frame1.pack()
        label2 = tk.Label(frame1,font=("",14),text="番号")
        label2.pack(side="left")
        entry1 = tk.Entry(frame1,font=("",14),justify="center",width=15) #state="readonly"を入れると数値代入がされない!?
        entry1.pack(side="left")
        entry1.insert(tk.END,change_id)

        # 日付のラベルとエントリーの設定
        frame2 = tk.Frame(root,pady=10)
        frame2.pack()
        label3 = tk.Label(frame2,font=("",14),text="日付")
        label3.pack(side="left")
        entry2 = tk.Entry(frame2,font=("",14),justify="center",width=15)
        entry2.pack(side="left")
        entry2.insert(tk.END,change_d)

        # 内訳のラベルとエントリーの設定
        frame3 = tk.Frame(root,pady=10)
        frame3.pack()
        label4 = tk.Label(frame3,font=("",14),text="内訳")
        label4.pack(side="left")
        # 内訳コンボボックスの作成
        combo = ttk.Combobox(frame3, state='readonly',font=("",14),width=13)

        list1 = list(createitemname()) #タプル値をリストに変換

        list2 = [] #空リスト作成
        for i in range(len(list1)): #list1のサイズをループ
             list2.append(list1[i][1]) #多重化リストを単一リスト(nameのみ)に整理
        #注）上記でも書いたが、この単一リスト化作業はcreateitemname内でやっておいた方がシンプルかもしれない

        combo["values"] = list2 #単一化されたリストの代入

        for i in range(len(list2)): #list2のサイズをループし、グローバル変数のchange_uと比較
             if list2[i]==change_u:
                  combo.current(i) #見つかった場合はi番目の内容をリストボックスに表示
                  break #見つかったらループから脱出
             else:
                  pass #見つからないときはパス（処理なし）
             
        combo.pack()

        # 金額のラベルとエントリーの設定
        frame4 = tk.Frame(root,pady=10)
        frame4.pack()
        label5 = tk.Label(frame4,font=("",14),text="金額")
        label5.pack(side="left")
        entry4 = tk.Entry(frame4,font=("",14),justify="center",width=15)
        entry4.pack(side="left")
        entry4.insert(tk.END,change_k)
        

    # 登録ボタンの設定
    button4 = tk.Button(root,text="登録",
                        font=("",16),
                        width=10,bg="gray",
                        command=lambda:create_sql(combo.get()))
    button4.pack()

    # 変更ボタンの設定
    button5 = tk.Button(root,text="変更",
                        font=("",16),
                        width=10,bg="gray",
                        command=lambda:change_sql(combo.get()))
    button5.pack()
    
    root.mainloop()

# 表示画面のGUI
def select_gui():
    # ----------------------------------------
    # コールバック関数群
    # ----------------------------------------    
    # 新規ボタンが押下されたときのコールバック関数
    def create_button():
        global change_id
        change_id = 0 #グルーバル変数のchange_idをリフレッシュ
        root.destroy()
        create_gui()
    # ----------------------------------------
    # 終了ボタンが押下されたときのコールバック関数
    def quit_button():
        root.destroy()   
    # ----------------------------------------
    # 表示ボタンが押下されたときのコールバック関数
    def select_sql(start,end):
        # treeviewのアイテムをすべて削除
        tree.delete(*tree.get_children())
        # 開始日と終了日が空欄だったらデフォルト値の設定
        if start == "":
            start = "1900-01-01"
        if end == "":
            end = "2100-01-01"
        #SELECT文の作成
        sql = """
        SELECT id,acc_date,item_name,amount
        FROM acc_data as a,item as i
        WHERE a.item_code = i.item_code AND
        acc_date BETWEEN '{}' AND '{}'
        ORDER BY acc_date
        """.format(start,end)
        # ツリービューにアイテムの追加
        i=0
        for r in c.execute(sql):
            # 金額(r[3])を通貨形式に変換
            r = (r[0],r[1],r[2],"¥{:,d}".format(r[3]))
            tree.insert("","end",tags=i,values=r)
            if i & 1:
                tree.tag_configure(i,background="#CCFFFF")
            i+=1    
    # ----------------------------------------
    
    # 空のデータベースを作成して接続する
    dbname = "database3.db"
    c = sqlite3.connect(dbname)
    c.execute("PRAGMA foreign_keys = 1")

    # rootフレームの設定
    root = tk.Tk()
    root.title("家計簿アプリ")
    root.geometry("400x500")

    # メニューの設定
    frame = tk.Frame(root,bd=2,relief="ridge")
    frame.pack(fill="x")
    button1 = tk.Button(frame,text="新規",command=create_button)
    button1.pack(side="left")
    button2 = tk.Button(frame,text="表示")
    button2.pack(side="left")
    button3 = tk.Button(frame,text="終了",command=quit_button)
    button3.pack(side="right")

    # 入力画面ラベルの設定
    label1 = tk.Label(root,text="【表示画面】",font=("",16),height=2)
    label1.pack(fill="x")

    # 期間選択のラベルエントリーの設定
    frame1 = tk.Frame(root,pady=15)
    frame1.pack()
    label2 = tk.Label(frame1,font=("",14),text="期間 ")
    label2.pack(side="left")
    entry1 = tk.Entry(frame1,font=("",14),justify="center",width=12)
    entry1.pack(side="left")
    label3 = tk.Label(frame1,font=("",14),text="　～　")
    label3.pack(side="left")
    entry2 = tk.Entry(frame1,font=("",14),justify="center",width=12)
    entry2.pack(side="left")

    # 表示ボタンの設定
    button4 = tk.Button(root,text="表示",
                        font=("",16),
                        width=10,bg="gray",
                        command=lambda:select_sql(entry1.get(),entry2.get()))
    button4.pack()

    def select_record(event):
        #選択行の選別
        record_id = tree.focus()
        record_values = tree.item(record_id, 'values')
        from tkinter import messagebox
        if messagebox.showinfo(title="選択行の確認",
                                  message="次の行が選択されました=> id:"
                                  + record_values[0]
                                  +",日付" + record_values[1]
                                  +",内訳" + record_values[2]
                                  +",金額" + record_values[3]):
                        #tree.delete(record_id) #//選択終了後にツリーリスト（表示）からのみ削除
                        global change_id 
                        change_id = record_values[0]
                        global change_d
                        change_d = record_values[1]
                        global change_u
                        change_u = record_values[2]
                        global change_k
                        change_k = record_values[3]
                        root.destroy()
                        create_gui()
        else:
            pass #選択イベントが発生していないときは何もしない

    # ツリービューの作成
    tree = ttk.Treeview(root,padding=10)
    tree.bind("<<TreeviewSelect>>", select_record)
    tree["columns"] = (1,2,3,4)
    tree["show"] = "headings"
    tree.column(1,width=50)
    tree.column(2,width=100)
    tree.column(3,width=75)
    tree.column(4,width=100)
    tree.heading(1,text="ID")
    tree.heading(2,text="日付")
    tree.heading(3,text="内訳")
    tree.heading(4,text="金額")

    # ツリービューのスタイル変更
    style = ttk.Style()
    # TreeViewの全部に対して、フォントサイズの変更
    style.configure("Treeview",font=("",12))
    # TreeViewのHeading部分に対して、フォントサイズの変更と太字の設定
    style.configure("Treeview.Heading",font=("",14,"bold"))

    # SELECT文の作成
    sql = """
    SELECT id,acc_date,item_name,amount
    FROM acc_data as a,item as i
    WHERE a.item_code = i.item_code
    ORDER BY acc_date
    """
    # ツリービューにアイテムの追加
    i=0
    for r in c.execute(sql):
        # 金額(r[3])を通貨形式に変換
        r = (r[0],r[1],r[2],"¥{:,d}".format(r[3]))
        tree.insert("","end",tags=i,values=r)
        if i & 1:
            tree.tag_configure(i,background="#CCFFFF")
        i+=1
    # ツリービューの配置
    tree.pack(fill="x",padx=20,pady=20)

    # メインループ
    root.mainloop()

# GUI画面の表示
create_gui()
