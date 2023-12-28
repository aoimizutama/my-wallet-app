# 表示画面から行を選択した際に、idが最大の行の出金額をprintで確認

# -*- coding: utf-8 -*-

import tkinter as tk
import tkinter.ttk as ttk
from tkinter import messagebox
import sqlite3

#グローバル変数として登録内容の変更時に使用
change_id = 0
change_d = ""
change_u = ""
change_g = 0
change_p = 0
change_z = 0
change_m = ""

# ---------------------------------------------------------------------------

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
        
        # データベースに接続
        c = sqlite3.connect("database5.db")
        
        # item_nameをWHERE句に渡してitem_codeを取得する
        item_code = c.execute("""
                    SELECT item_code FROM item
                    WHERE item_name = '{}'
                    """.format(item_name))
        item_code = item_code.fetchone()[0]

        # 日付の読み取り
        acc_data = entry2.get() #.replace("/","-") 不要?

        # 入金額の読み取り
        if entry4.get() == "":
            get = 0
        else:
            get = int(entry4.get())
        # 出金額の読み取り
        if entry5.get() == "":
            pay = 0
        else:
            pay = int(entry5.get())

        if (get == 0 and pay == 0):
            messagebox.showerror("エラー","入金か出金のどちらかに金額を入力してください")
        elif (get != 0 and pay != 0):
            messagebox.showerror("エラー","入金か出金のどちらかにのみ金額を入力してください")
        else:
            #エラーでなければ日付を[YYYY/MM/DD](0足し)に変換して登録
            try:
                from datetime import datetime
                #入力値が日付でないとエラー/エラーでなければ変数dtへ値を入力
                dt = datetime.strptime(acc_data, "%Y/%m/%d")
                #入力値を[YYYY/MM/DD](0足し)に変換
                acc_data = datetime.strftime(dt, "%Y/%m/%d")
        
            # SQLを発行してDBへ登録
                c.execute("""
                INSERT INTO acc_data(acc_date,item_code,get,pay)
                VALUES('{}',{},{},{});
                """.format(acc_data,item_code,get,pay))
                c.execute("COMMIT;")
                print("登録しました！")
            
            # ドメインエラーなどにより登録できなかった場合のエラー処理
            except:
                print("エラー原因 : 1.日付？ or 2.変更モードで登録ボタンを押した or etc.")
                #変更モードで登録をボタンを押すとsqlエラーになる、単に金額マークの問題であるが
                #変更モード時はif分岐をつかって登録を無効にする処理が必要なので、今はそのままにしている
                #最終的にはプログラム変更が必要！
            
    # ----------------------------------------

    # 更新ボタンがクリックされた時にデータをDBに更新するコールバック関数
    def change_sql(item_name):

        # データベースに接続
        c = sqlite3.connect("database5.db")
        
        # item_nameをWHERE句に渡してitem_codeを取得する
        item_code = c.execute("""
                    SELECT item_code FROM item
                    WHERE item_name = '{}'
                    """.format(item_name))
        item_code = item_code.fetchone()[0]

        # IDは読み込まれているグローバルidを使用
        global change_id
        
        # 日付の読み取り
        acc_data = entry2.get() #.replace("/","-") 不要?

        # 入金額の読み取り
        if entry4.get() == "":
            get = 0
        else:
            get = int(entry4.get())
        # 出金額の読み取り
        if entry5.get() == "":
            pay = 0
        else:
            pay = int(entry5.get())

        if (get == 0 and pay == 0):
            messagebox.showerror("エラー","入金か出金のどちらかに金額を入力してください")
        elif (get != 0 and pay != 0):
            messagebox.showerror("エラー","入金か出金のどちらかにのみ金額を入力してください")
        else:
            #エラーでなければ日付を[YYYY/MM/DD](0足し)に変換して登録
            try:
                from datetime import datetime
                #入力値が日付でないとエラー/エラーでなければ変数dtへ値を入力
                dt = datetime.strptime(acc_data, "%Y/%m/%d")
                #入力値を[YYYY/MM/DD](0足し)に変換
                acc_data = datetime.strftime(dt, "%Y/%m/%d")

            # SQLを発行してDBへ登録
                c.execute("UPDATE acc_data SET acc_date=?, item_code=?, get=?, pay=? WHERE id = ?",
                          (acc_data, item_code, get, pay, change_id))
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
        c = sqlite3.connect("database5.db")
        
        #for r in c.execute("SELECT item_name FROM item"):
            #li.append(r)
        #return tuple(r)
        #タプル⇒取出処理が上手くいかないので上記のコードを下記へ変更
        #forループではなく、シンプルに一括代入し、タプル型に変換
        #その後リストにタプルの要素を追加し、item_nameのみを返す
        
        li = tuple(c.execute("SELECT * FROM item"))
        li2 = [] #空リスト作成
        for i in range(len(li)): #list1のサイズをループ
             li2.append(li[i][1]) #多重化リストを単一リスト(nameのみ)に整理

        return li2
             
    # ----------------------------------------
    
    # 空のデータベースを作成して接続する
    dbname = "database5.db"
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
            get INTEGER NOT NULL,
            pay INTEGER NOT NULL,
            bal INTEGER,
            memo TEXT,
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
    root.geometry("500x500")

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
    label0 = tk.Label(root,text="【入力画面】",font=("",16),height=2)
    label0.pack(fill="x")

    global change_id

    if change_id == 0: #idが0の時、つまり新規
        frame1 = tk.Frame(root,pady=10)
        frame1.pack()
        label1 = tk.Label(frame1,font=("",14),text="番号")
        label1.pack(side="left")
        entry1 = tk.Entry(frame1,font=("",14),justify="center",width=15,state="readonly")
        entry1.pack(side="left")

        # 日付のラベルとエントリーの設定
        frame2 = tk.Frame(root,pady=10)
        frame2.pack()
        label2 = tk.Label(frame2,font=("",14),text="日付")
        label2.pack(side="left")
        entry2 = tk.Entry(frame2,font=("",14),justify="center",width=15)
        entry2.pack(side="left")

        # 内訳のラベルとエントリーの設定
        frame3 = tk.Frame(root,pady=10)
        frame3.pack()
        label3 = tk.Label(frame3,font=("",14),text="内訳")
        label3.pack(side="left")
        
        # 内訳コンボボックスの作成
        combo = ttk.Combobox(frame3, state='readonly',font=("",14),width=13)

        list = createitemname()
             
        combo["values"] = list #単一化されたリストの代入
        combo.current(0) #リスト最初のデータを表示
        combo.pack()

        # 入出金のラベルとエントリーの設定
        frame4 = tk.Frame(root,pady=10)
        frame4.pack()
        label4 = tk.Label(frame4,font=("",14),text="入金")
        label4.pack(side="left")
        entry4 = tk.Entry(frame4,font=("",14),justify="right",width=15)
        entry4.pack(side="left")
        label5 = tk.Label(frame4,font=("",14),text="出金")
        label5.pack(side="left")
        entry5 = tk.Entry(frame4,font=("",14),justify="right",width=15)
        entry5.pack(side="left")
        
    else: #idが0でない時、つまり変更
        frame1 = tk.Frame(root,pady=10)
        frame1.pack()
        label1 = tk.Label(frame1,font=("",14),text="番号")
        label1.pack(side="left")
        entry1 = tk.Entry(frame1,font=("",14),justify="center",width=15) #state="readonly"を入れると数値代入がされない!?
        entry1.pack(side="left")
        entry1.insert(tk.END,change_id)

        # 日付のラベルとエントリーの設定
        frame2 = tk.Frame(root,pady=10)
        frame2.pack()
        label2 = tk.Label(frame2,font=("",14),text="日付")
        label2.pack(side="left")
        entry2 = tk.Entry(frame2,font=("",14),justify="center",width=15)
        entry2.pack(side="left")
        entry2.insert(tk.END,change_d)

        # 内訳のラベルとエントリーの設定
        frame3 = tk.Frame(root,pady=10)
        frame3.pack()
        label3 = tk.Label(frame3,font=("",14),text="内訳")
        label3.pack(side="left")
        
        # 内訳コンボボックスの作成
        combo = ttk.Combobox(frame3, state='readonly',font=("",14),width=13)

        list = createitemname()
    
        combo["values"] = list #単一化されたリストの代入

        for i in range(len(list)): #list2のサイズをループし、グローバル変数のchange_uと比較
             if list[i]==change_u:
                  combo.current(i) #見つかった場合はi番目の内容をリストボックスに表示
                  break #見つかったらループから脱出
             else:
                  pass #見つからないときはパス（処理なし）
             
        combo.pack()

        # 入出金のラベルとエントリーの設定
        frame4 = tk.Frame(root,pady=10)
        frame4.pack()
        label4 = tk.Label(frame4,font=("",14),text="入金")
        label4.pack(side="left")
        entry4 = tk.Entry(frame4,font=("",14),justify="right",width=15)
        entry4.pack(side="left")
        entry4.insert(tk.END,change_g)
        label5 = tk.Label(frame4,font=("",14),text="出金")
        label5.pack(side="left")
        entry5 = tk.Entry(frame4,font=("",14),justify="right",width=15)
        entry5.pack(side="left")
        entry5.insert(tk.END,change_p)

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

# -------------------------------------------------------------------------

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
        SELECT id,acc_date,item_name,get,pay,bal,memo
        FROM acc_data as a,item as i
        WHERE a.item_code = i.item_code AND
        acc_date BETWEEN '{}' AND '{}'
        ORDER BY acc_date
        """.format(start,end)
        
        # ツリービューにアイテムの追加
        i=0
        for r in c.execute(sql):
            # 金額(r[3])を通貨形式に変換
            r = (r[0],r[1],r[2],"¥{:,d}".format(r[3]),"¥{:,d}".format(r[4]),r[5],r[6])
            tree.insert("","end",tags=i,values=r)
            if i & 1:
                tree.tag_configure(i,background="#CCFFFF")
            i+=1
            
    # ----------------------------------------
    
    # 空のデータベースを作成して接続する
    dbname = "database5.db"
    c = sqlite3.connect(dbname)
    c.execute("PRAGMA foreign_keys = 1")

    # rootフレームの設定
    root = tk.Tk()
    root.title("家計簿アプリ")
    root.geometry("800x500")

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
    label0 = tk.Label(root,text="【表示画面】",font=("",16),height=2)
    label0.pack(fill="x")

    # 期間選択のラベルエントリーの設定
    frame1 = tk.Frame(root,pady=15)
    frame1.pack()
    label1 = tk.Label(frame1,font=("",14),text="期間 ")
    label1.pack(side="left")
    entry1 = tk.Entry(frame1,font=("",14),justify="center",width=12)
    entry1.pack(side="left")
    label2 = tk.Label(frame1,font=("",14),text="　～　")
    label2.pack(side="left")
    entry2 = tk.Entry(frame1,font=("",14),justify="center",width=12)
    entry2.pack(side="left")

    # 表示ボタンの設定
    button4 = tk.Button(root,text="表示",
                        font=("",16),
                        width=10,bg="gray",
                        command=lambda:select_sql(entry1.get(),entry2.get()))
    button4.pack()

    # --------------------------------------------

    def select_record(event):

        # データベースに接続
        c = sqlite3.connect("database5.db")

        #idが最大の行の出金額を取得
        maxid = c.execute("""
                    SELECT pay FROM acc_data
                    WHERE id = (SELECT MAX(id) FROM acc_data)
                    """)
        pay_maxid = maxid.fetchone()[0]
        print(pay_maxid)
        
        
        #選択行の選別
        record_id = tree.focus()
        record_values = tree.item(record_id, 'values')
        if messagebox.showinfo(title="選択行の確認",
                                  message="次の行が選択されました=> id:"
                                  + record_values[0]
                                  +",日付" + record_values[1]
                                  +",内訳" + record_values[2]
                                  +",入金" + record_values[3]
                                  +",出金" + record_values[4]
                                  +",残金" + record_values[5]
                                  +",備考" + record_values[6]):
            
                        global change_id 
                        change_id = record_values[0]
                        global change_d
                        change_d = record_values[1]
                        global change_u
                        change_u = record_values[2]
                        global change_g
                        change_g = record_values[3]
                        global change_p
                        change_p = record_values[4]
                        global change_z
                        change_z = record_values[5]
                        global change_m
                        change_m = record_values[6]
                        root.destroy()
                        create_gui()
                        
        else:
            pass #選択イベントが発生していないときは何もしない

    # ツリービューの作成
    tree = ttk.Treeview(root,padding=10)
    tree.bind("<<TreeviewSelect>>", select_record)
    tree["columns"] = (1,2,3,4,5,6,7)
    tree["show"] = "headings"
    tree.column(1,width=50)
    tree.column(2,width=100)
    tree.column(3,width=75)
    tree.column(4,width=100)
    tree.column(5,width=100)
    tree.column(6,width=100)
    tree.column(7,width=100)
    tree.heading(1,text="ID")
    tree.heading(2,text="日付")
    tree.heading(3,text="内訳")
    tree.heading(4,text="入金")
    tree.heading(5,text="出金")
    tree.heading(6,text="残金")
    tree.heading(7,text="備考")

    # ツリービューのスタイル変更
    style = ttk.Style()
    # TreeViewの全部に対して、フォントサイズの変更
    style.configure("Treeview",font=("",12))
    # TreeViewのHeading部分に対して、フォントサイズの変更と太字の設定
    style.configure("Treeview.Heading",font=("",14,"bold"))

    # SELECT文の作成
    sql = """
    SELECT id,acc_date,item_name,get,pay,bal,memo
    FROM acc_data as a,item as i
    WHERE a.item_code = i.item_code
    ORDER BY acc_date
    """
    # ツリービューにアイテムの追加
    i=0
    for r in c.execute(sql):
        # 金額(r[3])を通貨形式に変換
        r = (r[0],r[1],r[2],"¥{:,d}".format(r[3]),"¥{:,d}".format(r[4]),r[5],r[6])
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
