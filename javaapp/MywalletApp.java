//インポート
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.sql.*;
import java.util.ArrayList;

public class MywalletApp {
    public static void main(String args[]){
        MyFrame frame = new MyFrame("MyTitle");
        frame.setVisible(true);
    }
}

class MyFrame extends JFrame implements ActionListener, ListSelectionListener {

    //変数定義
    JPanel p1, p2, p21, p22, p23, p3, p4, p5, cp;
    JLabel l1, l2, l22, l3, l4, l5, l6, l8, l41, l42, l43, l44, l45, l46, l47, l51, l52, l53, l54, l55, l56;
    JTextField tf2, tf4, tf5, tf6, tf8, tf21, tf22, tf41, tf42, tf44, tf45, tf46, tf47, tf52, tf53, tf54, tf55, tf56;
    JButton b7, b21, b22, b41, b51;
    JComboBox<String> c1, c4;
    JMenuItem mi1, mi2, mi3, mi5;
    CardLayout layout;
    TextArea ta;
    JList<String> list;
    Object objl;
    public static int beforebal;

    ///////////////////////////////////////////////////////////////////////////////////////////////////

    //フレーム作成
    public MyFrame (String title) {
        setTitle(title);
        setBounds(100, 50, 600, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //入力画面作成
        p1 = new JPanel();
        p1.setLayout(null);

        l1 = new JLabel("【入力画面】");
        l1.setFont(new Font(Font.SERIF, Font.PLAIN, 25));
        l1.setBounds(225, 20, 200, 25);
        p1.add(l1);

        l2 = new JLabel("日付");
        l2.setFont(new Font(Font.SERIF, Font.PLAIN, 20));
        l2.setBounds(125, 80, 40, 30);
        p1.add(l2);
        tf2 = new JTextField();
        tf2.setFont(new Font(Font.SERIF, Font.PLAIN, 20));
        tf2.setBounds(175, 80, 300, 30);
        p1.add(tf2);

        l3 = new JLabel("内訳");
        l3.setFont(new Font(Font.SERIF, Font.PLAIN, 20));
        l3.setBounds(125, 130, 40, 30);
        p1.add(l3);
        String[] combodata = {"食費", "住宅費", "光熱費"};
        c1 = new JComboBox<String>(combodata);
        c1.setFont(new Font(Font.SERIF, Font.PLAIN, 20));   
        c1.setBounds(175, 130, 300, 30);
        p1.add(c1);

        l4 = new JLabel("入金");
        l4.setFont(new Font(Font.SERIF, Font.PLAIN, 20));
        l4.setBounds(40, 180, 40, 30);
        p1.add(l4);
        tf4 = new JTextField();
        tf4.setFont(new Font(Font.SERIF, Font.PLAIN, 20));
        tf4.setBounds(90, 180, 200, 30);
        p1.add(tf4);

        l5 = new JLabel("出金");
        l5.setFont(new Font(Font.SERIF, Font.PLAIN, 20));
        l5.setBounds(310, 180, 40, 30);
        p1.add(l5);
        tf5 = new JTextField();
        tf5.setFont(new Font(Font.SERIF, Font.PLAIN, 20));
        tf5.setBounds(360, 180, 200, 30);
        p1.add(tf5);

        l6 = new JLabel("備考");
        l6.setFont(new Font(Font.SERIF, Font.PLAIN, 20));
        l6.setBounds(125, 230, 40, 30);
        p1.add(l6);
        tf6 = new JTextField();
        tf6.setFont(new Font(Font.SERIF, Font.PLAIN, 20));
        tf6.setBounds(175, 230, 300, 30);
        p1.add(tf6);

        b7 = new JButton("登録");
        b7.setFont(new Font(Font.SERIF, Font.PLAIN, 25));
        b7.setBounds(250, 300, 100, 50);
        b7.addActionListener(this); //ボタンが押されたときにイベント処理を呼び出す
        p1.add(b7);

        l8 = new JLabel("残金");
        l8.setFont(new Font(Font.SERIF, Font.PLAIN, 20));
        l8.setBounds(300, 450, 40, 30);
        p1.add(l8);
        tf8 = new JTextField();
        tf8.setFont(new Font(Font.SERIF, Font.PLAIN, 20));
        tf8.setBounds(350, 450, 200, 30);
        p1.add(tf8);

        //表示画面作成
        p2 = new JPanel();

        p21 = new JPanel();
        list = new JList<String>();
        JScrollPane sp = new JScrollPane();
        sp.getViewport().setView(list);
        sp.setPreferredSize(new Dimension(500, 400));
        p21.add(sp);
        list.addListSelectionListener(this); //リストが選択されたときの処理に飛ぶ（valueChangedメソッド）
        p2.add(p21,BorderLayout.CENTER);

        p22 = new JPanel();
        b21 = new JButton("変更");
        b21.addActionListener(this);
        p22.add(b21);
        tf21 = new JTextField(30);
        tf21.setFont(new Font(Font.SERIF, Font.PLAIN, 20));
        p22.add(tf21);
        p2.add(p22);
        
        p23 = new JPanel();
        l22 = new JLabel("残金");
        l22.setFont(new Font(Font.SERIF, Font.PLAIN, 20));
        p23.add(l22);
        tf22 = new JTextField(10);
        tf22.setFont(new Font(Font.SERIF, Font.PLAIN, 20));
        p23.add(tf22);
        p2.add(p23);

        //表示2画面作成
        p3 = new JPanel();
        ta = new TextArea(20,40);
        ta.setFont(new Font("ＭＳ ゴシック", Font.BOLD, 16));
        p3.add(ta);

        //変更画面作成
        p4 = new JPanel();
        p4.setLayout(null);
        
        l41 = new JLabel("【変更画面】");
        l41.setFont(new Font(Font.SERIF, Font.PLAIN, 25));
        l41.setBounds(225, 20, 200, 25);
        p4.add(l41);
        tf41 = new JTextField();
        tf41.setFont(new Font(Font.SERIF, Font.PLAIN, 20));
        tf41.setBounds(20, 20, 50, 30);
        p4.add(tf41);

        l42 = new JLabel("日付");
        l42.setFont(new Font(Font.SERIF, Font.PLAIN, 20));
        l42.setBounds(125, 80, 40, 30);
        p4.add(l42);
        tf42 = new JTextField();
        tf42.setFont(new Font(Font.SERIF, Font.PLAIN, 20));
        tf42.setBounds(175, 80, 300, 30);
        p4.add(tf42);

        l43 = new JLabel("内訳");
        l43.setFont(new Font(Font.SERIF, Font.PLAIN, 20));
        l43.setBounds(125, 130, 40, 30);
        p4.add(l43);
        String[] combodata4 = {"食費", "住宅費", "光熱費"};
        c4 = new JComboBox<String>(combodata4);
        c4.setFont(new Font(Font.SERIF, Font.PLAIN, 20));   
        c4.setBounds(175, 130, 300, 30);
        p4.add(c4);

        l44 = new JLabel("入金");
        l44.setFont(new Font(Font.SERIF, Font.PLAIN, 20));
        l44.setBounds(40, 180, 40, 30);
        p4.add(l44);
        tf44 = new JTextField();
        tf44.setFont(new Font(Font.SERIF, Font.PLAIN, 20));
        tf44.setBounds(90, 180, 200, 30);
        p4.add(tf44);

        l45 = new JLabel("出金");
        l45.setFont(new Font(Font.SERIF, Font.PLAIN, 20));
        l45.setBounds(310, 180, 40, 30);
        p4.add(l45);
        tf45 = new JTextField();
        tf45.setFont(new Font(Font.SERIF, Font.PLAIN, 20));
        tf45.setBounds(360, 180, 200, 30);
        p4.add(tf45);

        l46 = new JLabel("備考");
        l46.setFont(new Font(Font.SERIF, Font.PLAIN, 20));
        l46.setBounds(125, 230, 40, 30);
        p4.add(l46);
        tf46 = new JTextField();
        tf46.setFont(new Font(Font.SERIF, Font.PLAIN, 20));
        tf46.setBounds(175, 230, 300, 30);
        p4.add(tf46);

        b41 = new JButton("更新");
        b41.setFont(new Font(Font.SERIF, Font.PLAIN, 25));
        b41.setBounds(250, 300, 100, 50);
        b41.addActionListener(this); //ボタンが押されたときにイベント処理を呼び出す
        p4.add(b41);

        l47 = new JLabel("残金");
        l47.setFont(new Font(Font.SERIF, Font.PLAIN, 20));
        l47.setBounds(300, 450, 40, 30);
        p4.add(l47);
        tf47 = new JTextField();
        tf47.setFont(new Font(Font.SERIF, Font.PLAIN, 20));
        tf47.setBounds(350, 450, 200, 30);
        p4.add(tf47);

        //設定画面作成
        p5 = new JPanel();
        p5.setLayout(null);
        
        l51 = new JLabel("【設定画面】");
        l51.setFont(new Font(Font.SERIF, Font.PLAIN, 25));
        l51.setBounds(225, 20, 200, 25);
        p5.add(l51);

        l52 = new JLabel("名前");
        l52.setFont(new Font(Font.SERIF, Font.PLAIN, 20));
        l52.setBounds(95, 80, 100, 30);
        p5.add(l52);
        tf52 = new JTextField();
        tf52.setFont(new Font(Font.SERIF, Font.PLAIN, 20));
        tf52.setBounds(205, 80, 300, 30);
        p5.add(tf52);

        l53 = new JLabel("パスワード");
        l53.setFont(new Font(Font.SERIF, Font.PLAIN, 20));
        l53.setBounds(95, 130, 100, 30);
        p5.add(l53);
        tf53 = new JTextField();
        tf53.setFont(new Font(Font.SERIF, Font.PLAIN, 20));
        tf53.setBounds(205, 130, 300, 30);
        p5.add(tf53);

        l54 = new JLabel("ユーザID");
        l54.setFont(new Font(Font.SERIF, Font.PLAIN, 20));
        l54.setBounds(95, 180, 100, 30);
        p5.add(l54);
        tf54 = new JTextField();
        tf54.setFont(new Font(Font.SERIF, Font.PLAIN, 20));
        tf54.setBounds(205, 180, 300, 30);
        p5.add(tf54);

        l55 = new JLabel("現金残");
        l55.setFont(new Font(Font.SERIF, Font.PLAIN, 20));
        l55.setBounds(20, 230, 60, 30);
        p5.add(l55);
        tf55 = new JTextField();
        tf55.setFont(new Font(Font.SERIF, Font.PLAIN, 20));
        tf55.setBounds(90, 230, 200, 30);
        p5.add(tf55);

        l56 = new JLabel("預金残");
        l56.setFont(new Font(Font.SERIF, Font.PLAIN, 20));
        l56.setBounds(300, 230, 60, 30);
        p5.add(l56);
        tf56 = new JTextField();
        tf56.setFont(new Font(Font.SERIF, Font.PLAIN, 20));
        tf56.setBounds(370, 230, 200, 30);
        p5.add(tf56);

        b51 = new JButton("設定");
        b51.setFont(new Font(Font.SERIF, Font.PLAIN, 25));
        b51.setBounds(250, 300, 100, 50);
        b51.addActionListener(this); //ボタンが押されたときにイベント処理を呼び出す
        p5.add(b51);

        //カードパネル作成
        cp = new JPanel();
        layout = new CardLayout();
        cp.setLayout(layout);

        //データベースに接続して、残金を取得
        String dbname = "database8.db";
        Connection conn = null;
        Statement stmt = null;

        String cash = null;
        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:" + dbname);
            
            String sql = "SELECT * FROM setting where id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, 1);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                cash = rs.getString("cash");
            }
            rs.close();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        //もし設定テーブルにデータが存在した場合、入力画面を最初に表示
        //設定テーブルにデータが存在しなかった場合には、設定画面を最初に表示 
        // String dbname = "database8.db"; // SQLite3のファイルPATH(適宜変更)
        // Connection conn = null;
        // Statement stmt = null;
        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:" + dbname);

            stmt = conn.createStatement();

            // meiboテーブルからデータを取得(適宜変更)
            ResultSet rs = stmt.executeQuery("SELECT * FROM setting");
            String name = null;
            while (rs.next()) {
                name = rs.getString("user_name"); // name列のデータを取得(適宜変更)
            }
            rs.close();
            
            if (name==null) {
                cp.add(p5, "p5");
                cp.add(p1, "p1");
            } else {
                cp.add(p1, "p1");
                tf8.setText(cash);
                cp.add(p5, "p5");
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        cp.add(p2, "p2");
        cp.add(p3, "p3");
        cp.add(p4, "p4");

        //メニュー作成
        JMenuBar mb = new JMenuBar();

        JMenu m = new JMenu("メニュー");
        mb.add(m);

        mi1 = new JMenuItem("入力");
        mi1.addActionListener(this);
        mi1.setActionCommand("p1");
        m.add(mi1);
        mi2 = new JMenuItem("表示&変更（JList）");
        mi2.addActionListener(this);
        mi2.setActionCommand("p2");
        m.add(mi2);
        mi3 = new JMenuItem("表示（テキストエリア）");
        mi3.addActionListener(this);
        mi3.setActionCommand("p3");
        m.add(mi3);
        mi5 = new JMenuItem("設定");
        mi5.addActionListener(this);
        mi5.setActionCommand("p5");
        m.add(mi5);

        mb.add(m);

        setJMenuBar(mb);

        //カードパネルをフレームに配置
        Container contentPane = getContentPane();
        contentPane.add(cp, BorderLayout.CENTER);

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //ボタンが押されたときのイベント
    public void actionPerformed (ActionEvent e) {
        Object obj = e.getSource();
        if (obj == mi1) {
            String cmd = e.getActionCommand();
            layout.show(cp, cmd);
            showBalance();
        } else if(obj == mi2){
            String cmd = e.getActionCommand();
            layout.show(cp, cmd);
            showJlist();
            showBalance();
        } else if(obj == mi3){
            String cmd = e.getActionCommand();
            layout.show(cp, cmd);
            showTa();
        } else if(obj == mi5){
            String cmd = e.getActionCommand();
            layout.show(cp, cmd);
        } else if (obj == b7) {
            dataInsert(); //登録ボタンを押したとき、データベースにデータを追加
            layout.show(cp, "p2");
            showJlist();
        } else if (obj == b21) {
            dataUpdateshow(); //変更ボタンを押したとき、変更画面に変更前データを表示
            layout.show(cp, "p4");
        } else if (obj == b41) {
            dataUpdate(); //更新ボタンを押したときにデータを更新する
            layout.show(cp, "p2");
            showJlist();
        } else if (obj == b51) {
            setBalance(); //設定ボタンを押したときに残金の初期値を設定
            layout.show(cp, "p1");
        }
    }

    //メニューの入力、表示を選択したときに残金を表示する
    private void showBalance() {
        //データベースに接続して、残金を取得して表示
        String dbname = "database8.db";
        Connection conn = null;
        Statement stmt = null;

        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:" + dbname);
            
            String sql = "SELECT * FROM setting where id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, 1);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String cash = rs.getString("cash");
                tf8.setText(cash);
                tf22.setText(cash);
            }
            rs.close();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    //メニューの表示ボタンを押したときのイベント処理（JListでデータ一覧表示）
    private void showJlist() {
        //データベースに接続して、acc_dataテーブルから日付順にデータを取得し、リストで表示
        String dbname = "database8.db";
        Connection conn = null;
        Statement stmt = null;

        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:" + dbname);

            stmt = conn.createStatement();

            ResultSet rs = stmt.executeQuery("select * from acc_data order by acc_date");

            //配列の作成
            ArrayList<String> listData = new ArrayList<String>();
            listData.add("id,  acc_date,  item_code,  get,  out,  memo");

            //結果セットからデータを取り出す next()で次の行に移動
            while (rs.next()) {
                int id = rs.getInt("id");
                String acc_date = rs.getString("acc_date");
                int item_code = rs.getInt("item_code");
                int get = rs.getInt("get");
                int out = rs.getInt("out");
                String memo = rs.getString("memo");
                //1つの配列に一項目のデータを入れる
                listData.add(id + ",   " + acc_date + ",   " + item_code + ",   " + get + ",   " + out + ",   " + memo);
            }
            rs.close();

            //Jlistにデータベースから取得して配列にした項目を入れる
            list.setListData(listData.toArray(new String[listData.size()]));

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    //メニューの表示2ボタンを押したときのイベント処理（テキストエリアでデータ一覧表示）
    private void showTa(){
        //データベースに接続して、acc_dataテーブルから日付順にデータを取得し、テキストエリアに表示
        String dbname = "database8.db";
        Connection conn = null;
        Statement stmt = null;

        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:" + dbname);
            
            stmt = conn.createStatement();

            ResultSet rs = stmt.executeQuery("select * from acc_data order by acc_date");

            StringBuffer temp  = new StringBuffer();
            temp.append("id\t\tacc_date\t\titem_code\t\tget\t\tout\t\tmemo\n");

            //結果セットからデータを取り出す next()で次の行に移動
            while (rs.next()) {
                int id = rs.getInt("id");
                String acc_date = rs.getString("acc_date");
                int item_code = rs.getInt("item_code");
                int get = rs.getInt("get");
                int out = rs.getInt("out");
                String memo = rs.getString("memo");
                temp.append(id + "\t\t");
                temp.append(acc_date + "\t\t");
                temp.append(item_code + "\t\t");
                temp.append(get + "\t\t");
                temp.append(out + "\t\t");
                temp.append(memo + "\n");
            }
            rs.close();

            //テキストエリアにデータを表示
            ta.setText(temp.toString());

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    //登録ボタンが押されたときのイベント処理
    private void dataInsert() {
        //入力された値を取得
        String acc_data = tf2.getText();
        int item_code = c1.getSelectedIndex();
        String get_str = tf4.getText();
        int get = 0;
        if (!(get_str.equals(""))) {
            get = Integer.parseInt(get_str);
        }
        String out_str = tf5.getText();
        int out = 0;
        if (!(out_str.equals(""))) {
            out = Integer.parseInt(out_str);
        }
        String memo = tf6.getText();

        //データベースに接続し、acc_dataテーブルにインサート
        String dbname = "database8.db";
        Connection conn = null;
        Statement stmt = null;
        
        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:" + dbname);

            String sql = "INSERT INTO acc_data(acc_date,item_code,get,out,memo) VALUES(?,?,?,?,?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, acc_data);
            ps.setInt(2, item_code);
            ps.setInt(3, get);
            ps.setInt(4, out);
            ps.setString(5, memo);

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        //データベースに接続して、設定テーブルから残金を取得
        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:" + dbname);

            String sql = "SELECT * FROM setting where id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, 1);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int cash = rs.getInt("cash");
                int bal = cash + get - out;
                String sbal = String.valueOf(bal);
                tf22.setText(sbal);
            }
            rs.close();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        
        //データベースに接続して、設定テーブルの残金を更新
        int ibal = Integer.parseInt(tf22.getText());
        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:" + dbname);

            String sql = "UPDATE setting SET cash = ? WHERE id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, ibal);
            ps.setInt(2, 1);

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        //テキストフィールドの初期化
        tf2.setText("");
        c1.setSelectedIndex(0);
        tf4.setText("");
        tf5.setText("");
        tf6.setText("");
    }

    //変更ボタンを押したときに、変更画面に変更前データを表示
    private void dataUpdateshow(){
        //選択された行のidを取得
        String str = objl.toString(); //ここに選択されている文字列を代入
        char kanma = ',';
        String dd = "";
        for (int i = 0; i < str.length(); i++) {
            if (kanma == str.charAt(i)) {
                break;
            } else {
                dd = dd + str.charAt(i);
            }
        }
        int ddi = Integer.parseInt(dd);

        //データベースからidがddiと同じデータを取得して表示
        String dbname = "database8.db";
        Connection conn = null;
        Statement stmt = null;

        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:" + dbname);

            String sql = "select * from acc_data where id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, ddi); 
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String id = rs.getString("id"); // id列のデータを取得(適宜変更)
                String acc_date = rs.getString("acc_date"); // acc_date列のデータを取得(適宜変更)
                int item_code = rs.getInt("item_code");
                String get = rs.getString("get");
                String out = rs.getString("out");
                String memo = rs.getString("memo");
                tf41.setText(id);
                tf42.setText(acc_date);
                c4.setSelectedIndex(item_code);
                tf44.setText(get);
                tf45.setText(out);
                tf46.setText(memo);
            }
            rs.close();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        int get = Integer.parseInt(tf44.getText());
        int out = Integer.parseInt(tf45.getText());
        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:" + dbname);

            String sql = "SELECT * FROM setting where id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, 1); 
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String scash = rs.getString("cash");
                tf47.setText(scash);
                int icash = rs.getInt("cash");
                beforebal = icash - get + out;
            }
            rs.close();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    //更新ボタンを押したときにデータを更新する
    private void dataUpdate() {
        //入力されたデータを取得
        String acc_data = tf42.getText();
        int item_code = c4.getSelectedIndex();

        String get_str = tf44.getText();
        int get = 0;
        if (!(get_str.equals(""))) {
            get = Integer.parseInt(get_str);
        }
        String out_str = tf45.getText();
        int out = 0;
        if (!(out_str.equals(""))) {
            out = Integer.parseInt(out_str);
        }
        String memo = tf46.getText();
        int id = Integer.parseInt(tf41.getText());

        //データベースに接続して
        String dbname = "database8.db";
        Connection conn = null;
        Statement stmt = null;
        
        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:" + dbname);

            String sql = "UPDATE acc_data SET acc_date = ?, item_code = ?, get = ?, out = ?, memo = ? WHERE id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, acc_data);
            ps.setInt(2, item_code);
            ps.setInt(3, get);
            ps.setInt(4, out);
            ps.setString(5, memo);
            ps.setInt(6, id);

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:" + dbname);

            String sql = "SELECT * FROM setting where id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, 1); 
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int afterbal = beforebal + get - out;
                String bal = String.valueOf(afterbal);
                tf22.setText(bal);
            }
            rs.close();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        int ibal = Integer.parseInt(tf22.getText());  
        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:" + dbname);

            String sql = "UPDATE setting SET cash = ? WHERE id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, ibal);
            ps.setInt(2, 1);

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        //テキストフィールド初期化
        tf21.setText("");
        tf41.setText("");
        tf42.setText("");
        c4.setSelectedIndex(0);
        tf44.setText("");
        tf45.setText("");
        tf46.setText("");
        tf47.setText("");
    }

    //設定ボタンで残金の初期値を設定
    private void setBalance() {
        //入力値を取得
        String user_name = tf52.getText();
        String pas = tf53.getText();
        String user_id = tf54.getText();
        String cash_str = tf54.getText();
        int cash = 0;
        if (!(cash_str.equals(""))) {
            cash = Integer.parseInt(cash_str);
        }
        String bank_str = tf56.getText();
        int bank = 0;
        if (!(bank_str.equals(""))) {
            bank = Integer.parseInt(bank_str);
        }

        //データベースに接続して設定テーブルにインサート
        String dbname = "database8.db";
        Connection conn = null;
        Statement stmt = null;
        
        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:" + dbname);

            String sql = "INSERT INTO setting(user_name, pas, user_id, cash, bank) VALUES(?,?,?,?,?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, user_name);
            ps.setString(2, pas);
            ps.setString(3, user_id);
            ps.setInt(4, cash);
            ps.setInt(5, bank);

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        tf8.setText(cash_str);
    }

    //Jlistで項目が選択されたときに項目のデータをテキストフィールドに入れるs
    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (e.getValueIsAdjusting()) {
            return;
        }
        objl = list.getSelectedValue();
        if (objl != null) {
            tf21.setText(objl.toString());
        }
    }
}