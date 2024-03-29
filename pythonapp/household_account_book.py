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
change_o = 0
change_m = ""

#================================================================================================================

# 入力画面のGUI
def create_gui():
    
    # ------------------------------------------------------------------------------
    
    # コールバック関数群

    # 新規ボタンが押下されたときのコールバック関数
    def create_button():
        global change_id
        change_id = 0
        root.destroy()
        create_gui() 

    # 設定ボタンが押下されたときのコールバック関数
    def setting_button():
        root.destroy()
        setting_gui()   
    
    # 表示ボタンが押下されたときのコールバック関数
    def select_button():
        root.destroy()
        select_gui()
    
    # 終了ボタンが押下されたときのコールバック関数
    def quit_button():
        root.destroy()
        
    # -------------------------------------------------------------------------------
    
    # 登録ボタンがクリックされた時にデータをDBに登録するコールバック関数
    def create_sql(item_name):
        
        # データベースに接続
        c = sqlite3.connect("database8.db")
        
        # item_nameをWHERE句に渡してitem_codeを取得する
        item_code = c.execute("""
                    SELECT item_code FROM item
                    WHERE item_name = '{}'
                    """.format(item_name))
        item_code = item_code.fetchone()[0]

        # 日付の読み取り
        acc_data = entry1.get()

        # 入金額の読み取り
        if entry3.get() == "":
            get = 0
        else:
            get = int(entry3.get())
            
        # 出金額の読み取り
        if entry4.get() == "":
            out = 0
        else:
            out = int(entry4.get())

        # 備考の読み取り
        memo = entry5.get()

        # エラーがなければデータベースにデータを登録
        if (get == 0 and out == 0):
            messagebox.showerror("エラー","入金か出金のどちらかに金額を入力してください")
        elif (get != 0 and out != 0):
            messagebox.showerror("エラー","入金か出金のどちらかにのみ金額を入力してください")
        else:
            #エラーでなければ日付を[YYYY/MM/DD](0足し)に変換して登録
            try:
                from datetime import datetime
                #入力値が日付でないとエラー/エラーでなければ変数dtへ値を入力
                dt = datetime.strptime(acc_data, "%Y/%m/%d")
                #入力値を[YYYY/MM/DD](0足し)に変換
                acc_data = datetime.strftime(dt, "%Y/%m/%d")

                #設定テーブルにデータが登録されているかを確認
                try:
                    setting_cash = c.execute("SELECT cash FROM setting")
                    cash =  setting_cash.fetchone()[0]
                except:
                    messagebox.showerror("エラー","設定から初期設定を行った後に登録してください")
                
                # SQLを発行してDBへ登録
                # settingテーブルの現金残を更新
                c.execute("UPDATE setting SET cash=? WHERE id=?",(cash+get-out,1))
                c.execute("COMMIT;")
                
                # acc_dataテーブルにデータを追加
                c.execute("""
                INSERT INTO acc_data(acc_date,item_code,get,out,memo)
                VALUES('{}',{},{},{},'{}');
                """.format(acc_data,item_code,get,out,memo))
                c.execute("COMMIT;")
                
                messagebox.showinfo('確認', '登録しました！')

                # 現金残の表示を更新
                setting_cash = c.execute("SELECT cash FROM setting")
                cash =  setting_cash.fetchone()[0]
                entry6.delete(0,tk.END)
                entry6.insert(tk.END,cash)
            
            # ドメインエラーなどにより登録できなかった場合のエラー処理
            except:
                messagebox.showerror("エラー","日付を「YYYY/MM/DD」の形式で入力してください")

    #--------------------------------------------------------------------------------

    # 変更ボタンがクリックされた時にデータをDBに更新するコールバック関数
    def change_sql(item_name):

        # データベースに接続
        c = sqlite3.connect("database8.db")
        
        # item_nameをWHERE句に渡してitem_codeを取得する
        item_code = c.execute("""
                    SELECT item_code FROM item
                    WHERE item_name = '{}'
                    """.format(item_name))
        item_code = item_code.fetchone()[0]
        
        # 日付の読み取り
        acc_data = entry1.get()

        # 入金額の読み取り
        if entry3.get() == "":
            get = 0
        else:
            get = int(entry3.get())
            
        # 出金額の読み取り
        if entry4.get() == "":
            out = 0
        else:
            out = int(entry4.get())

        # 備考の読み取り
        memo = entry5.get()

        if (get == 0 and out == 0):
            messagebox.showerror("エラー","入金か出金のどちらかに金額を入力してください")
        elif (get != 0 and out != 0):
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
                # settingテーブルの現金残を更新
                global change_g
                global change_o
                change_g = int(change_g)
                change_o = int(change_o)
                if change_g != 0 or change_o != 0:
                    before_get = change_g # 更新前の入金
                    before_out = change_o # 更新前の出金
                    change_g = 0
                    change_o = 0
                    setting_cash = c.execute("SELECT cash FROM setting")
                    cash =  setting_cash.fetchone()[0]
                    c.execute("UPDATE setting SET cash=? WHERE id=?",(cash-before_get+before_out+get-out,1))
                    c.execute("COMMIT;")
                
                    # acc_dataテーブルのデータを更新
                    global change_id  # IDは読み込まれているグローバルidを使用
                    c.execute("UPDATE acc_data SET acc_date=?,item_code=?,get=?,out=?,memo=? WHERE id=?",
                              (acc_data,item_code,get,out,memo,change_id))
                    c.execute("COMMIT;")
                
                    messagebox.showinfo('確認', '変更しました！')
    
                    # エントリーの現金残の表示を更新
                    setting_cash = c.execute("SELECT cash FROM setting")
                    cash =  setting_cash.fetchone()[0]
                    entry6.delete(0,tk.END)
                    entry6.insert(tk.END,cash)

                    change_id = 0 #グローバル変数idのリフレッシュ
                else:
                    messagebox.showerror("エラー","表示からもう一度変更したい行を選択した後に変更してください")
            
            # ドメインエラーなどにより登録できなかった場合のエラー処理
            except:
                messagebox.showerror("エラー","日付を「YYYY/MM/DD」の形式で入力してください")

    #-----------------------------------------------------------------------------------------
            
    # itemテーブルにあるitem_nameのタプルを作成する
    def create_iname():
        
        # データベースの接続
        c = sqlite3.connect("database8.db")
        item_list = tuple(c.execute("SELECT * FROM item"))
        iname_list = [] #空リスト作成
        for i in range(len(item_list)):
             iname_list.append(item_list[i][1])

        return iname_list

    #------------------------------------------------------------------------------------------

    # 入力画面の作成
    
    # rootフレームの設定
    root = tk.Tk()
    root.title("家計簿アプリ")
    root.geometry("500x500")

    # メニューの設定
    frame = tk.Frame(root,bd=2,relief="ridge")
    frame.pack(fill="x")
    button0 = tk.Button(frame,text="設定",command=setting_button)
    button0.pack(side="left")
    button1 = tk.Button(frame,text="入力",command=create_button)
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
        
        # 日付のラベルとエントリーの設定
        frame1 = tk.Frame(root,pady=10)
        frame1.pack()
        label1 = tk.Label(frame1,font=("",14),text="日付")
        label1.pack(side="left")
        entry1 = tk.Entry(frame1,font=("",14),justify="center",width=15)
        entry1.pack(side="left")

        # 内訳のラベルとエントリーの設定
        frame2 = tk.Frame(root,pady=10)
        frame2.pack()
        label2 = tk.Label(frame2,font=("",14),text="内訳")
        label2.pack(side="left")
        # 内訳コンボボックスの作成
        combo = ttk.Combobox(frame2, state='readonly',font=("",14),width=13)
        iname_list = create_iname()
        combo["values"] = iname_list
        combo.current(0)
        combo.pack()

        # 入出金のラベルとエントリーの設定
        frame3 = tk.Frame(root,pady=10)
        frame3.pack()
        label3 = tk.Label(frame3,font=("",14),text="入金")
        label3.pack(side="left")
        entry3 = tk.Entry(frame3,font=("",14),justify="right",width=15)
        entry3.pack(side="left")
        label4 = tk.Label(frame3,font=("",14),text="出金")
        label4.pack(side="left")
        entry4 = tk.Entry(frame3,font=("",14),justify="right",width=15)
        entry4.pack(side="left")

        # 備考のラベルとエントリーの設定
        frame5 = tk.Frame(root,pady=10)
        frame5.pack()
        label5 = tk.Label(frame5,font=("",14),text="備考")
        label5.pack(side="left")
        entry5 = tk.Entry(frame5,font=("",14),justify="right",width=15)
        entry5.pack(side="left")

        # 登録ボタンの設定
        button4 = tk.Button(root,text="登録",
                            font=("",16),
                            width=10,bg="gray",
                            command=lambda:create_sql(combo.get()))
        button4.pack()
        
    else: #idが0でない時、つまり変更

        # 日付のラベルとエントリーの設定
        frame1 = tk.Frame(root,pady=10)
        frame1.pack()
        label1 = tk.Label(frame1,font=("",14),text="日付")
        label1.pack(side="left")
        entry1 = tk.Entry(frame1,font=("",14),justify="center",width=15)
        entry1.pack(side="left")
        entry1.insert(tk.END,change_d)

        # 内訳のラベルとエントリーの設定
        frame2 = tk.Frame(root,pady=10)
        frame2.pack()
        label2 = tk.Label(frame2,font=("",14),text="内訳")
        label2.pack(side="left")
        # 内訳コンボボックスの作成
        combo = ttk.Combobox(frame2, state='readonly',font=("",14),width=13)
        iname_list = create_iname()
        combo["values"] = iname_list
        for i in range(len(iname_list)):
            if iname_list[i]==change_u:
                combo.current(i)
                break
            else:
                pass     
        combo.pack()

        # 入出金のラベルとエントリーの設定
        frame3 = tk.Frame(root,pady=10)
        frame3.pack()
        label3 = tk.Label(frame3,font=("",14),text="入金")
        label3.pack(side="left")
        entry3 = tk.Entry(frame3,font=("",14),justify="right",width=15)
        entry3.pack(side="left")
        entry3.insert(tk.END,change_g)
        label4 = tk.Label(frame3,font=("",14),text="出金")
        label4.pack(side="left")
        entry4 = tk.Entry(frame3,font=("",14),justify="right",width=15)
        entry4.pack(side="left")
        entry4.insert(tk.END,change_o)

        # 備考のラベルとエントリーの設定
        frame5 = tk.Frame(root,pady=10)
        frame5.pack()
        label5 = tk.Label(frame5,font=("",14),text="備考")
        label5.pack(side="left")
        entry5 = tk.Entry(frame5,font=("",14),justify="right",width=15)
        entry5.pack(side="left")
        entry5.insert(tk.END,change_m)

        # 変更ボタンの設定
        button5 = tk.Button(root,text="変更",
                            font=("",16),
                            width=10,bg="gray",
                            command=lambda:change_sql(combo.get()))
        button5.pack()

    # 現金残の表示
    setting_cash = c.execute("SELECT cash FROM setting")
    cash =  setting_cash.fetchone()[0]
    frame6 = tk.Frame(root,padx=20,pady=70)
    frame6.pack(fill="x")
    entry6 = tk.Entry(frame6,font=("",14),justify="right",width=15)
    entry6.pack(side="right")
    entry6.insert(tk.END,cash)
    label6 = tk.Label(frame6,font=("",14),text="残金")
    label6.pack(side="right")
    
    root.mainloop()
        
#=============================================================================================================================

# 設定画面のGUI
def setting_gui():
    
    # -----------------------------------------------------------------------------
    
    # コールバック関数群

    # 新規ボタンが押下されたときのコールバック関数
    def create_button():
        global change_id
        change_id = 0
        root.destroy()
        create_gui()

    # 設定ボタンが押下されたときのコールバック関数
    def setting_button():
        root.destroy()
        setting_gui()
    
    # 表示ボタンが押下されたときのコールバック関数
    def select_button():
        root.destroy()
        select_gui()
    
    # 終了ボタンが押下されたときのコールバック関数
    def quit_button():
        root.destroy()
        
    # -----------------------------------------------------------------------------

    # 設定ボタンがクリックされた時にデータをDBに登録するコールバック関数
    def setting_sql():
        
        # データベースに接続
        c = sqlite3.connect("database8.db")

        # 名前の読み取り
        user_name = entry1.get()

        # パスワードの読み取り
        pas = entry2.get()

        # ユーザidの読み取り
        user_id = entry3.get()

        # 現金残の読み取り
        cash = entry4.get()

        # 預金残の読み取り
        bank = entry5.get()

        # SQLを発行してDBへ登録
        c.execute("""
        INSERT INTO setting(user_name,pas,user_id,cash,bank)
        VALUES('{}','{}','{}',{},{});
        """.format(user_name,pas,user_id,cash,bank))
        c.execute("COMMIT;")

        messagebox.showinfo('確認', '設定しました！')

    #------------------------------------------------------------------------

    # 設定画面の作成
    
    # rootフレームの設定
    root = tk.Tk()
    root.title("家計簿アプリ")
    root.geometry("500x500")

    # メニューの設定
    frame = tk.Frame(root,bd=2,relief="ridge")
    frame.pack(fill="x")
    button0 = tk.Button(frame,text="設定",command=setting_button)
    button0.pack(side="left")
    button1 = tk.Button(frame,text="新規",command=create_button)
    button1.pack(side="left")
    button2 = tk.Button(frame,text="表示",command=select_button)
    button2.pack(side="left")
    button3 = tk.Button(frame,text="終了",command=quit_button)
    button3.pack(side="right")

    # 設定画面ラベルの設定
    label0 = tk.Label(root,text="【設定画面】",font=("",16),height=2)
    label0.pack(fill="x")

    # 名前のラベルとエントリーの設定
    frame1 = tk.Frame(root,pady=10)
    frame1.pack()
    label1 = tk.Label(frame1,font=("",14),text="名前")
    label1.pack(side="left")
    entry1 = tk.Entry(frame1,font=("",14),justify="center",width=15)
    entry1.pack(side="left")

    # パスワードのラベルとエントリーの設定
    frame2 = tk.Frame(root,pady=10)
    frame2.pack()
    label2 = tk.Label(frame2,font=("",14),text="パスワード")
    label2.pack(side="left")
    entry2 = tk.Entry(frame2,font=("",14),justify="center",width=15)
    entry2.pack(side="left")

    # ユーザidのラベルとエントリーの設定
    frame3 = tk.Frame(root,pady=10)
    frame3.pack()
    label3 = tk.Label(frame3,font=("",14),text="ユーザid")
    label3.pack(side="left")
    entry3 = tk.Entry(frame3,font=("",14),justify="center",width=15)
    entry3.pack(side="left")

    # 現金残と預金残のラベルとエントリーの設定
    frame4 = tk.Frame(root,pady=10)
    frame4.pack()
    label4 = tk.Label(frame4,font=("",14),text="現金残")
    label4.pack(side="left")
    entry4 = tk.Entry(frame4,font=("",14),justify="right",width=15)
    entry4.pack(side="left")
    label5 = tk.Label(frame4,font=("",14),text="預金残")
    label5.pack(side="left")
    entry5 = tk.Entry(frame4,font=("",14),justify="right",width=15)
    entry5.pack(side="left")

    # 設定ボタンの設定
    button4 = tk.Button(root,text="設定",
                        font=("",16),
                        width=10,bg="gray",
                        command=lambda:setting_sql())
    button4.pack()
        
    root.mainloop()

#===========================================================================================================================

# 表示画面のGUI
def select_gui():
    
    # ----------------------------------------
    # コールバック関数群
    # ----------------------------------------
    
    # 新規ボタンが押下されたときのコールバック関数
    def create_button():
        global change_id
        change_id = 0
        root.destroy()
        create_gui()

    # 設定ボタンが押下されたときのコールバック関数
    def setting_button():
        root.destroy()
        setting_gui()

    # 表示ボタンが押下されたときのコールバック関数
    def select_button():
        root.destroy()
        select_gui()
    
    # 終了ボタンが押下されたときのコールバック関数
    def quit_button():
        root.destroy()

    #変更したい行が選択されたときのコールバック関数
    def select_record(event):
        
        #選択行の選別
        record_id = tree.focus()
        record_values = tree.item(record_id, 'values')
        if messagebox.showinfo(title="選択行の確認",
                               message="次の行が選択されました => 日付: "
                                   + record_values[1]
                                   +", 内訳: " + record_values[2]
                                   +", 入金: " + record_values[3]
                                   +", 出金: " + record_values[4]
                                   +", 備考: " + record_values[5]):
            global change_id 
            change_id = record_values[0]
            global change_d
            change_d = record_values[1]
            global change_u
            change_u = record_values[2]
            global change_g
            change_g = record_values[3]
            global change_o
            change_o = record_values[4]
            global change_m
            change_m = record_values[5]
            root.destroy()
            create_gui()               
        else:
            pass
        
    #------------------------------------------------------------------------------------
    
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
        SELECT id,acc_date,item_name,get,out,memo
        FROM acc_data as a,item as i
        WHERE a.item_code = i.item_code AND
        acc_date BETWEEN '{}' AND '{}'
        ORDER BY acc_date
        """.format(start,end)
        
        # ツリービューにアイテムの追加
        i=0
        for r in c.execute(sql):
            r = (r[0],r[1],r[2],r[3],r[4],r[5])
            tree.insert("","end",tags=i,values=r)
            if i & 1:
                tree.tag_configure(i,background="#CCFFFF")
            i+=1
            
    #-------------------------------------------------------------------------------------

    # 表示画面の作成
    
    # rootフレームの設定
    root = tk.Tk()
    root.title("家計簿アプリ")
    root.geometry("800x500")

    # メニューの設定
    frame = tk.Frame(root,bd=2,relief="ridge")
    frame.pack(fill="x")
    button0 = tk.Button(frame,text="設定",command=setting_button)
    button0.pack(side="left")
    button1 = tk.Button(frame,text="新規",command=create_button)
    button1.pack(side="left")
    button2 = tk.Button(frame,text="表示",command=select_button)
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

    # -----------------------------------------------------------------------

    # ツリービューの作成
    tree = ttk.Treeview(root,padding=10)
    tree.bind("<<TreeviewSelect>>", select_record)
    tree["columns"] = (1,2,3,4,5,6)
    tree["show"] = "headings"
    tree.column(1,width=0,stretch='no')
    tree.column(2,width=75)
    tree.column(3,width=100)
    tree.column(4,width=100)
    tree.column(5,width=100)
    tree.column(6,width=100)
    tree.heading(1,text="ID")
    tree.heading(2,text="日付")
    tree.heading(3,text="内訳")
    tree.heading(4,text="入金")
    tree.heading(5,text="出金")
    tree.heading(6,text="備考")
    # ツリービューのスタイル変更
    style = ttk.Style()
    # TreeViewの全部に対して、フォントサイズの変更
    style.configure("Treeview",font=("",12))
    # TreeViewのHeading部分に対して、フォントサイズの変更と太字の設定
    style.configure("Treeview.Heading",font=("",14,"bold"))

    # 空のデータベースを作成して接続する
    dbname = "database8.db"
    c = sqlite3.connect(dbname)
    c.execute("PRAGMA foreign_keys = 1")

    # SELECT文の作成
    sql = """
    SELECT id,acc_date,item_name,get,out,memo
    FROM acc_data as a,item as i
    WHERE a.item_code = i.item_code
    ORDER BY acc_date
    """
    # ツリービューにアイテムの追加
    i=0
    for r in c.execute(sql):
        r = (r[0],r[1],r[2],r[3],r[4],r[5])
        tree.insert("","end",tags=i,values=r)
        if i & 1:
            tree.tag_configure(i,background="#CCFFFF")
        i+=1
        
    # ツリービューの配置
    tree.pack(fill="x",padx=20,pady=20)

    # メインループ
    root.mainloop()

#==================================================================================================================

# GUI画面の表示

#-----------------------------------------------------------------------------------------------------

# データベースのテーブルを作成

# 空のデータベースを作成して接続する
dbname = "database8.db"
c = sqlite3.connect(dbname)
c.execute("PRAGMA foreign_keys = 1")

# 既にデータベースが登録されている場合は、ddlの発行でエラーが出るのでexceptブロックで回避する
try:
    # settingテーブルの定義
    ddl = """
    CREATE TABLE setting
    (
        id         INTEGER PRIMARY KEY AUTOINCREMENT,
        user_name  TEXT NOT NULL,
        pas        TEXT NOT NULL UNIQUE,
        user_id    TEXT NOT NULL UNIQUE,
        cash       INTEGER NOT NULL,
        bank       INTEGER NOT NULL
    )
     """ 
    # SQLの発行
    c.execute(ddl)
        
    # itemテーブルの定義
    ddl = """
    CREATE TABLE item
    (
       item_code  INTEGER PRIMARY KEY AUTOINCREMENT,
       item_name  TEXT NOT NULL UNIQUE
    )
     """
    # SQLの発行
    c.execute(ddl)
        
    # acc_dataテーブルの定義    
    ddl = """
    CREATE TABLE acc_data
    ( 
        id         INTEGER PRIMARY KEY AUTOINCREMENT,
        acc_date   DATE NOT NULL,
        item_code  INTEGER NOT NULL,
        get        INTEGER NOT NULL,
        out        INTEGER NOT NULL,
        memo       TEXT,
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

#------------------------------------------------------------------------------------------------

# 設定のテーブルにデータが登録されてなければ設定画面を表示し、それ以外のときは登録画面を表示する
setting_cash = tuple(c.execute("SELECT cash FROM setting"))
if not setting_cash:
    setting_gui()
else:
    create_gui()
