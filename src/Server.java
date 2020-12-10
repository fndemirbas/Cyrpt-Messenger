import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.*;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
//GROUP 15
//Zeynep Sema Nur ÝNKAYA - 21827503 
//Fatma Nur DEMÝRBAÞ - 21727116



public class Server {

    public static byte[] initVecAES=new byte[16];
    public static byte[] initVecDES=new byte[8];

    public static SecretKey AESKey;

    public static SecretKey DESKey;
    //Key and vector assignment
    static {
        try {
            AESKey = generateKeys("AES");
            DESKey = generateKeys("DES");

            initVect();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
    public static ArrayList<Socket> connectionArray = new ArrayList<Socket>(); //hold connections
    public static ArrayList<String> usernameArray = new ArrayList<>(); //hold usernames
    private static final int SERVER_PORT = 8082;
    //Convert Keys to Base64
    public static final String AESkey_str = Base64.getEncoder().encodeToString(AESKey.getEncoded());
    public static final String DESkey_str = Base64.getEncoder().encodeToString(DESKey.getEncoded());
    public static PrintWriter outFile;




    public static void main(String[] args){


        String out1 = "\n-----LOG-----\nRandom Key for AES: " + AESkey_str
                + "\nRandom IV for AES: " + Base64.getEncoder().encodeToString(initVecAES)
                + "\nRandom Key for DES: " + DESkey_str
                + "\nRandom IV for DES: "+ Base64.getEncoder().encodeToString(initVecDES);

        System.out.println(out1);
        writeLogOutputs(out1);

        // Starts Server and Waits for a Connection
        try {
            ServerSocket server = new ServerSocket(SERVER_PORT);


            while (true){
                Socket socket = server.accept();
                connectionArray.add(socket);


                //Key and Init Vector distribution from Server to Clients
                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                out.writeObject(AESKey);
                out.writeObject(DESKey);
                out.writeObject(initVecAES);
                out.writeObject(initVecDES);

                addUserNameToArray(socket);

                System.out.println(usernameArray);

                ServerReturn chat = new ServerReturn(socket);
                Thread x = new Thread(chat);
                x.start();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }




    }

    private static void addUserNameToArray(Socket s) throws IOException {
        BufferedReader input = new BufferedReader(new InputStreamReader(s.getInputStream()));

        String username = input.readLine();


        usernameArray.add(username);

        for (int i=1;i<=connectionArray.size();i++){
            Socket temp = connectionArray.get(i-1);
            if(temp.isClosed()){
                connectionArray.remove(temp);
                usernameArray.remove(i-1);

                continue;
            }

            PrintWriter out = new PrintWriter(temp.getOutputStream());


            out.println("@" + usernameArray);
            out.flush();

        }

    }

    //Keys Initialization
    public static void initKeys() {

        try {
            AESKey=generateKeys("AES");
            DESKey=generateKeys("DES");

        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


    } 
    //Generation Keys 
    public static SecretKey generateKeys(String method) throws NoSuchAlgorithmException {
        int keySize;
        if (method.equals("AES")) {
            keySize=128;

        } else {
            keySize=56;
        }

        KeyGenerator keyGenerator = KeyGenerator.getInstance(method);
        keyGenerator.init(keySize, SecureRandom.getInstanceStrong());

        // Generate Key
        return keyGenerator.generateKey();


    }
    //Inýt Vector Initialization
    public static void initVect() {
        initVecAES=createInitializationVector(16);
        initVecDES=createInitializationVector(8);
    }
    //Generation Inýt Vector 
    public static byte[] createInitializationVector(int size)
    {
        byte[] initializationVector = new byte[size];
        SecureRandom secureRandom  = new SecureRandom();
        secureRandom.nextBytes(initializationVector);
        return initializationVector;
    }

    //Writing to "log.txt" (Adding to Existing One)
    public static void writeLogOutputs(String s)  {
        File file = new File("log.txt");

            try {
                if(!file.exists()){
                    file.createNewFile();
                }

                FileWriter writer = new FileWriter(file.getName(),true);
                BufferedWriter bufferedWriter = new BufferedWriter(writer);
                bufferedWriter.write(s);
                bufferedWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }




    }



}