import java.awt.Frame;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Arrays;
import java.util.Base64;
import java.util.Scanner;
import java.util.zip.CheckedInputStream;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.sound.midi.Receiver;
import javax.swing.JOptionPane;



public class Client implements Runnable {

    //Globals
    Socket SOCK;
    Scanner INPUT;
    Scanner SEND = new Scanner(System.in);
    PrintWriter OUT;
    boolean isKeysAreRead = false;
    public static byte[] initVecAES=new byte[16];
    public static byte[] initVecDES=new byte[8];
    public static SecretKey AESKey;
    public static SecretKey DESKey;

    public static String AES_key_str;
    public static String DES_key_str;


    public Client(Socket X) {
        this.SOCK=X;


    }

    public void run() {
        try {
            try {
            	
                INPUT=new Scanner(SOCK.getInputStream());
                OUT= new PrintWriter(SOCK.getOutputStream());
                OUT.flush();
                CheckStream();

            } finally  {
                SOCK.close();
            }
        } catch (Exception e) {
            System.out.println(e);
        }


    }

    //Disconnect Clients 
    public void DISCONNECT() throws IOException{

        OUT.println(ClientGUI.username+" has disconnected");
        OUT.flush();
        JOptionPane.showMessageDialog(null, "You disconnected!");

        System.exit(0);

    }

    public void CheckStream() {
        while (true) {
            RECEIVE();
        }
    }

    public void RECEIVE() {



        if(INPUT.hasNext()) {


            String MESSAGE =INPUT.nextLine();
            ClientGUI.txtArea.append(MESSAGE+"\n");


        }

     }
    // Send on enter then clear to prepare for next message
    public void SEND(String x,String y)
    {
        String fileOut = "\n"+ ClientGUI.username + "> " + x;
        Server.writeLogOutputs(fileOut);//Printing Message to log.txt			
        OUT.println(x + "\n" + ClientGUI.username+"> " + y);  //Printing Message to  Chat Text Area
        OUT.flush();
        ClientGUI.txtAreaText.setText("");

    }


    //Encryption Method
    public static String encrypt(String strToEncrypt ,String method ,String mode)
    {
        if(method.equals("-")){
            return "";
        }
        try
        {

            SecretKey key;

            if(method.equals("AES")){

                key= ClientGUI.AESKey;

                IvParameterSpec ivSpec = new IvParameterSpec(ClientGUI.initVecAES);
                Cipher cipher = Cipher.getInstance(method+"/"+mode+"/PKCS5Padding");
                cipher.init(Cipher.ENCRYPT_MODE, ClientGUI.AESKey , ivSpec);
                return Base64.getEncoder().encodeToString(cipher.doFinal(strToEncrypt.getBytes("UTF-8")));
            }

            else if (method.equals("DES")){

                key=ClientGUI.DESKey;

                IvParameterSpec ivSpec = new IvParameterSpec(ClientGUI.initVecDES);

                Cipher cipher = Cipher.getInstance(method+"/"+mode+"/PKCS5Padding");
                cipher.init(Cipher.ENCRYPT_MODE, ClientGUI.DESKey , ivSpec);
                return Base64.getEncoder().encodeToString(cipher.doFinal(strToEncrypt.getBytes("UTF-8")));
            }

        }
        catch (Exception e)
        {
            System.out.println("Error while encrypting: " + e.toString());
        }
        return null;
    }

    //Decryption Method
    public static String decrypt(String strToEncrypt ,String method ,String mode)
    {
        if(method.equals("-")){
            return "";
        }
        try
        {

            SecretKey key;


            if(method.equals("AES")){

                key=ClientGUI.AESKey;

                IvParameterSpec ivSpec = new IvParameterSpec(ClientGUI.initVecAES);

                Cipher cipher = Cipher.getInstance(method+"/"+mode+"/PKCS5Padding");
                cipher.init(Cipher.DECRYPT_MODE, ClientGUI.AESKey , ivSpec);
                return new String(cipher.doFinal(Base64.getDecoder().decode(strToEncrypt)));

            }

            else if (method.equals("DES")){

                key=ClientGUI.DESKey;

                IvParameterSpec ivSpec = new IvParameterSpec(ClientGUI.initVecDES);

                Cipher cipher = Cipher.getInstance(method+"/"+mode+"/PKCS5Padding");
                cipher.init(Cipher.DECRYPT_MODE, ClientGUI.DESKey , ivSpec);
                return new String(cipher.doFinal(Base64.getDecoder().decode(strToEncrypt)));
            }


        }
        catch (Exception e)
        {
            System.out.println("Error while decrypting: " + e.toString());
        }
        return null;
    }

}