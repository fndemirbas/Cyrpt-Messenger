import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Panel;
import java.awt.TextArea;
import java.io.*;
import java.net.Socket;
import java.util.Arrays;
import java.util.Base64;
import java.util.Scanner;


import javax.crypto.SecretKey;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
//It can be run as many number of times depending upon number of clients required.
public class ClientGUI {

    private static Client client;
    public static String username = "Not Connected";
    //Main Window Globals
    private static JPanel contentPane;
    private final Panel panel = new Panel();
    private final Component horizontalStrut = Box.createHorizontalStrut(20);
    public static JFrame mainWindow = new JFrame();
    public static JButton btnConnect = new JButton("Connect");
    public static JButton btnDisconnect = new JButton("Disconnect");
    public static JPanel panel_1 = new JPanel();
    public static JPanel panel_1_1 = new JPanel();
    public static JRadioButton rdbtnCBC = new JRadioButton("CBC");
    public static JRadioButton rdbtnOFB = new JRadioButton("OFB");
    public static GroupLayout gl_panel_1_1 = new GroupLayout(panel_1_1);
    public static  JLabel lblTitle = new JLabel("Server");
    public static JPanel panel_2 = new JPanel();
    public static JPanel panel_2_1 = new JPanel();
    public static JTextArea textAreaCrypted = new JTextArea();
    public static GroupLayout gl_panel_2_1 = new GroupLayout(panel_2_1);
    public static JButton btnEncrypt = new JButton("Encrypt");
    public static JButton btnSend = new JButton("Send");
    public static JLabel lblConnectionState = new JLabel("Not Connected");
    public static JTextArea txtArea = new JTextArea();
    public static JTextArea txtAreaText = new JTextArea();
    public static GroupLayout gl_panel_2 = new GroupLayout(panel_2);
    public static JRadioButton rdbtnDES = new JRadioButton("DES");
    public static JRadioButton rdbtnAES = new JRadioButton("AES");
    public static GroupLayout gl_panel_1 = new GroupLayout(panel_1);
    //Login Window Globals
    public static JFrame loginWindow = new JFrame();
    private static JButton btnOK= new JButton("OK");
    private static JButton btnCancel= new JButton("Cancel");
    private static JLabel labelUsername= new JLabel("Enter username:" );
    private static JPanel pLogin= new JPanel();
    private static JTextField txtUsername=new JTextField();
    //Crypto globals
    public static String decryptedText="-";
    public static SecretKey AESKey;
    public static SecretKey DESKey;
    public static byte[] initVecAES=new byte[16];
    public static byte[] initVecDES=new byte[8];



    public static void main(String[] args) {

        BuildMainWindow();
        Initialize();

    }


    public static void Connect () {
        try {
            final int PORT =8082;
            final String HOST = "localhost";
            //establish socket connection to server
            Socket SOCK = new Socket(HOST,PORT)	;
            System.out.println("You connect to: " +HOST);

            client = new Client(SOCK);


            //Import Keys and Inýt Vector from Server
            ObjectInputStream inp = new ObjectInputStream(SOCK.getInputStream());
            AESKey = (SecretKey)inp.readObject();
            DESKey = (SecretKey)inp.readObject();
            initVecAES = (byte[]) inp.readObject();
            initVecDES = (byte[]) inp.readObject();



            PrintWriter OUT = new PrintWriter(SOCK.getOutputStream());
            OUT.println(username);
            OUT.flush();





            Thread x = new Thread(client);
            x.start();

        } catch (Exception e) {
            System.out.println(e);
            JOptionPane.showInternalMessageDialog(null, "Server not responding .");
            System.exit(0);
        }
    }

    public static void Initialize() {
        btnSend.setEnabled(false);
        btnDisconnect.setEnabled(false);
        btnConnect.setEnabled(true);
        textAreaCrypted.setEditable(false);
        txtArea.setEditable(false);
    
    }

    public static void BuildMainWindow() {

        mainWindow.setTitle("Crypto Messenger");
        mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainWindow.setBounds(100, 100, 598, 684);
        mainWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        mainWindow.setContentPane(contentPane);

        ConfigureMainWindow();
        MainWindow_Activation();
        mainWindow.setVisible(true);


    }

    public static void BuildLoginWindow() {

        loginWindow.setTitle("Input");
        loginWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        loginWindow.setBounds(100, 100, 316, 143);
        loginWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        pLogin = new JPanel();
        pLogin.setBorder(new EmptyBorder(5, 5, 5, 5));
        loginWindow.setContentPane(pLogin);
        ConfigureLoginWindow();
        Login_Action();
        loginWindow.setVisible(true);


    }

    public static void Login_Action() {
        btnOK.addActionListener(
                new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt)
                    {
                        ACTION_btnOK();
                    }

                }

        );

    }

    public static void ACTION_btnOK() {
        if (!txtUsername.getText().equals(""))
        {
            username=txtUsername.getText().trim();
            lblConnectionState.setText("Connected: "+username);
            Server.usernameArray.add(username);;
            loginWindow.setVisible(false);
            //btnSend.setEnabled(true);
            btnDisconnect.setEnabled(true);
            btnConnect.setEnabled(false);
            txtAreaText.setEditable(true);
            Connect();




        }

        else {
            JOptionPane.showMessageDialog(null,"Please enter a name : ");
        }

    }

    private static void ConfigureLoginWindow() {

        txtUsername.setColumns(10);
        loginWindow.getContentPane().add(txtUsername);

        loginWindow.getContentPane().add(btnOK);

        loginWindow.getContentPane().add(btnCancel);

        labelUsername.setFont(new Font("Tahoma", Font.BOLD, 11));
        loginWindow.getContentPane().add(labelUsername);

        //alternateGUIFrame.getContentPane()
         
        GroupLayout gl_contentPane = new GroupLayout(loginWindow.getContentPane());
        gl_contentPane.setHorizontalGroup(
                gl_contentPane.createParallelGroup(Alignment.TRAILING)
                        .addGroup(gl_contentPane.createSequentialGroup()
                                .addGap(23)
                                .addGap(18)
                                .addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
                                        .addComponent(labelUsername, GroupLayout.PREFERRED_SIZE, 149, GroupLayout.PREFERRED_SIZE)
                                        .addGroup(gl_contentPane.createSequentialGroup()
                                                .addGap(10)
                                                .addComponent(btnOK, GroupLayout.PREFERRED_SIZE, 80, GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(ComponentPlacement.RELATED)
                                                .addComponent(btnCancel, GroupLayout.PREFERRED_SIZE, 78, GroupLayout.PREFERRED_SIZE))
                                        .addComponent(txtUsername, GroupLayout.PREFERRED_SIZE, 187, GroupLayout.PREFERRED_SIZE))
                                .addGap(46))
        );
        gl_contentPane.setVerticalGroup(
                gl_contentPane.createParallelGroup(Alignment.LEADING)
                        .addGroup(gl_contentPane.createSequentialGroup()
                                .addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
                                        .addGroup(gl_contentPane.createSequentialGroup()
                                                .addComponent(labelUsername, GroupLayout.PREFERRED_SIZE, 19, GroupLayout.PREFERRED_SIZE)
                                                .addGap(0)
                                                .addComponent(txtUsername, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(ComponentPlacement.RELATED)
                                                .addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
                                                        .addComponent(btnCancel)
                                                        .addComponent(btnOK)))
                                        .addGroup(gl_contentPane.createSequentialGroup()
                                                .addContainerGap()))
                                .addContainerGap(25, Short.MAX_VALUE))
        );
        pLogin.setLayout(gl_contentPane);




    }

    public static void MainWindow_Activation() {
        btnSend.addActionListener(
                new java.awt.event.ActionListener() {

                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        ACTION_btnSend();
                    }
                }


        );
        btnDisconnect.addActionListener(
                new java.awt.event.ActionListener() {

                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        ACTION_btnDisconnect();
                        mainWindow.dispose();
                    }
                }

        );

        btnConnect.addActionListener(
                new java.awt.event.ActionListener() {

                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        BuildLoginWindow();
                    }
                }

        );
        btnEncrypt.addActionListener(
                new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        ACTION_btnEncrypt();


                    }
                }

        );



    }

    public static void ConfigureMainWindow() {

    	//btnConnect.setIcon(new ImageIcon("connect.png"));
        mainWindow.getContentPane().add(btnConnect);
        
        //btnDisconnect.setIcon(new ImageIcon("disconnect.png"));
        mainWindow.getContentPane().add(btnDisconnect);

        panel_1.setBorder(new TitledBorder(null, "Method", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        mainWindow.getContentPane().add(panel_1);


        panel_1_1.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, new Color(255, 255, 255), new Color(160, 160, 160)), "Mode", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
        mainWindow.getContentPane().add(panel_1_1);


        mainWindow.getContentPane().add(rdbtnCBC);

        mainWindow.getContentPane().add(rdbtnOFB);
        ButtonGroup G = new ButtonGroup();
        G.add(rdbtnAES);
        G.add(rdbtnDES);



        gl_panel_1_1.setHorizontalGroup(
                gl_panel_1_1.createParallelGroup(Alignment.LEADING)
                        .addGap(0, 124, Short.MAX_VALUE)
                        .addGroup(gl_panel_1_1.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(rdbtnCBC)
                                .addGap(2)
                                .addComponent(rdbtnOFB)
                                .addContainerGap(30, Short.MAX_VALUE))
        );
        gl_panel_1_1.setVerticalGroup(
                gl_panel_1_1.createParallelGroup(Alignment.TRAILING)
                        .addGap(0, 58, Short.MAX_VALUE)
                        .addGroup(gl_panel_1_1.createSequentialGroup()
                                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(gl_panel_1_1.createParallelGroup(Alignment.BASELINE)
                                        .addComponent(rdbtnOFB)
                                        .addComponent(rdbtnCBC))
                                .addGap(23))
        );
        panel_1_1.setLayout(gl_panel_1_1);


        mainWindow.getContentPane().add(lblTitle);

        panel_2.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, new Color(255, 255, 255), new Color(160, 160, 160)), "Text", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
        mainWindow.getContentPane().add(panel_2);

        panel_2_1.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, new Color(255, 255, 255), new Color(160, 160, 160)), "Crypted Text", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
        mainWindow.getContentPane().add(panel_2_1);

        mainWindow.getContentPane().add(textAreaCrypted);

        gl_panel_2_1.setHorizontalGroup(
                gl_panel_2_1.createParallelGroup(Alignment.LEADING)
                        .addComponent(textAreaCrypted, GroupLayout.DEFAULT_SIZE, 173, Short.MAX_VALUE)
        );
        gl_panel_2_1.setVerticalGroup(
                gl_panel_2_1.createParallelGroup(Alignment.LEADING)
                        .addComponent(textAreaCrypted, GroupLayout.DEFAULT_SIZE, 79, Short.MAX_VALUE)
        );
        panel_2_1.setLayout(gl_panel_2_1);



        mainWindow.getContentPane().add(btnEncrypt);
        
        //btnSend.setIcon(new ImageIcon("send.png"));
        mainWindow.getContentPane().add(btnSend);

        lblConnectionState.setText(username);
        mainWindow.getContentPane().add(lblConnectionState);

        mainWindow.getContentPane().add(txtArea);

        GroupLayout gl_contentPane = new GroupLayout(mainWindow.getContentPane());


        gl_contentPane.setHorizontalGroup(
                gl_contentPane.createParallelGroup(Alignment.LEADING)
                        .addGroup(gl_contentPane.createSequentialGroup()
                                .addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
                                        .addGroup(gl_contentPane.createSequentialGroup()
                                                .addGap(26)
                                                .addComponent(btnConnect, GroupLayout.PREFERRED_SIZE, 112, GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(ComponentPlacement.UNRELATED)
                                                .addComponent(btnDisconnect, GroupLayout.PREFERRED_SIZE, 116, GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(ComponentPlacement.RELATED)
                                                .addComponent(panel_1, GroupLayout.PREFERRED_SIZE, 124, GroupLayout.PREFERRED_SIZE)
                                                .addGap(18)
                                                .addComponent(panel_1_1, GroupLayout.PREFERRED_SIZE, 124, GroupLayout.PREFERRED_SIZE))
                                        .addComponent(lblTitle, GroupLayout.PREFERRED_SIZE, 81, GroupLayout.PREFERRED_SIZE)
                                        .addGroup(gl_contentPane.createSequentialGroup()
                                                .addComponent(panel_2, GroupLayout.PREFERRED_SIZE, 189, GroupLayout.PREFERRED_SIZE)
                                                .addGap(2)
                                                .addComponent(panel_2_1, GroupLayout.PREFERRED_SIZE, 185, GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(ComponentPlacement.RELATED)
                                                .addComponent(btnEncrypt, GroupLayout.PREFERRED_SIZE, 79, GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(ComponentPlacement.RELATED)
                                                .addComponent(btnSend, GroupLayout.PREFERRED_SIZE, 89, GroupLayout.PREFERRED_SIZE))
                                        .addComponent(txtArea, GroupLayout.PREFERRED_SIZE, 572, GroupLayout.PREFERRED_SIZE)
                                        .addGroup(gl_contentPane.createSequentialGroup()
                                                .addContainerGap()
                                                .addComponent(lblConnectionState, GroupLayout.PREFERRED_SIZE, 272, GroupLayout.PREFERRED_SIZE)))
                                .addGap(6))
        );
        gl_contentPane.setVerticalGroup(
                gl_contentPane.createParallelGroup(Alignment.TRAILING)
                        .addGroup(gl_contentPane.createSequentialGroup()
                                .addComponent(lblTitle)
                                .addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING)
                                        .addGroup(gl_contentPane.createSequentialGroup()
                                                .addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
                                                        .addComponent(btnConnect)
                                                        .addComponent(btnDisconnect))
                                                .addGap(24))
                                        .addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
                                                .addComponent(panel_1_1, GroupLayout.PREFERRED_SIZE, 58, GroupLayout.PREFERRED_SIZE)
                                                .addComponent(panel_1, GroupLayout.PREFERRED_SIZE, 58, GroupLayout.PREFERRED_SIZE)))
                                .addPreferredGap(ComponentPlacement.RELATED)
                                .addComponent(txtArea, GroupLayout.PREFERRED_SIZE, 400, GroupLayout.PREFERRED_SIZE)
                                .addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
                                        .addGroup(gl_contentPane.createSequentialGroup()
                                                .addGap(51)
                                                .addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
                                                        .addComponent(btnEncrypt)
                                                        .addComponent(btnSend)))
                                        .addGroup(gl_contentPane.createSequentialGroup()
                                                .addPreferredGap(ComponentPlacement.RELATED)
                                                .addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
                                                        .addComponent(panel_2, GroupLayout.PREFERRED_SIZE, 101, GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(panel_2_1, GroupLayout.PREFERRED_SIZE, 101, GroupLayout.PREFERRED_SIZE))))
                                .addPreferredGap(ComponentPlacement.UNRELATED)
                                .addComponent(lblConnectionState, GroupLayout.PREFERRED_SIZE, 27, GroupLayout.PREFERRED_SIZE)
                                .addGap(561))
        );


        mainWindow.getContentPane().add(txtAreaText);

        gl_panel_2.setHorizontalGroup(
                gl_panel_2.createParallelGroup(Alignment.LEADING)
                        .addGroup(gl_panel_2.createSequentialGroup()
                                .addComponent(txtAreaText, GroupLayout.PREFERRED_SIZE, 175, GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        gl_panel_2.setVerticalGroup(
                gl_panel_2.createParallelGroup(Alignment.LEADING)
                        .addGroup(gl_panel_2.createSequentialGroup()
                                .addComponent(txtAreaText, GroupLayout.PREFERRED_SIZE, 78, GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panel_2.setLayout(gl_panel_2);



        mainWindow.getContentPane().add(rdbtnDES);

        mainWindow.getContentPane().add(rdbtnAES);

        ButtonGroup G2 = new ButtonGroup();
        G2.add(rdbtnCBC);
        G2.add(rdbtnOFB);

        gl_panel_1.setHorizontalGroup(
                gl_panel_1.createParallelGroup(Alignment.LEADING)
                        .addGroup(gl_panel_1.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(rdbtnAES)
                                .addGap(2)
                                .addComponent(rdbtnDES)
                                .addContainerGap(30, Short.MAX_VALUE))
        );
        gl_panel_1.setVerticalGroup(
                gl_panel_1.createParallelGroup(Alignment.TRAILING)
                        .addGroup(gl_panel_1.createSequentialGroup()
                                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(gl_panel_1.createParallelGroup(Alignment.BASELINE)
                                        .addComponent(rdbtnDES)
                                        .addComponent(rdbtnAES))
                                .addGap(23))
        );
        panel_1.setLayout(gl_panel_1);



        contentPane.setLayout(gl_contentPane);







    }

    public static void ACTION_btnSend() {
        if (!txtUsername.getText().equals("")) {
            /*client.SEND(txtAreaText.getText());
            txtArea.requestFocus();*/
            client.SEND(textAreaCrypted.getText(), decryptedText);
            textAreaCrypted.requestFocus();
            textAreaCrypted.setText("");
            btnSend.setEnabled(false);

        }

    }


    public static void ACTION_btnEncrypt() {

        String plainText =txtAreaText.getText();
        txtAreaText.setText("");
        String method="-";
        String mode;
        SecretKey key;
        byte[] initVec =new  byte[16];

        if (rdbtnAES.isSelected()) {

            method="AES";


        }
        else if(rdbtnDES.isSelected()){

            method="DES";


        }
        if (rdbtnCBC.isSelected()) {
            mode="CBC";
        }
        else {
            mode="OFB";
        }


        textAreaCrypted.setText(Client.encrypt(plainText, method, mode));
        decryptedText = Client.decrypt(Client.encrypt(plainText, method, mode),method,mode);
        btnSend.setEnabled(true);


    }

    public static void ACTION_btnDisconnect() {
        try {
            client.DISCONNECT();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}