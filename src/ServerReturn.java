import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ServerReturn implements Runnable{

    Socket socket;
    private Scanner INPUT;
    private PrintWriter OUTPUT;
    String MESSAGE="";



    public ServerReturn(Socket s){
        this.socket = s;
    }

    public void checkConnection() throws IOException {
        if (!socket.isConnected()){

            for (int i=0;i<Server.connectionArray.size();i++){
                Socket temp_socket = (Socket) Server.connectionArray.get(i);
                PrintWriter temp_out = new PrintWriter(temp_socket.getOutputStream());
                temp_out.println(temp_socket.getLocalAddress().getHostName() + " disconnected!");
                temp_out.flush();


            }
            //socket.close();
        }
    }

    @Override
    public void run() {
    	// Accept messages from this client and broadcast them.
        try {

            try {

                INPUT = new Scanner(socket.getInputStream());
                OUTPUT = new PrintWriter(socket.getOutputStream());

                while (true){
                    checkConnection();

                    if(!INPUT.hasNext()){
                        return;
                    }

                    MESSAGE = INPUT.nextLine();


                    System.out.println( MESSAGE);

                    for (int i=0;i<Server.connectionArray.size();i++){

                        Socket temp_socket = Server.connectionArray.get(i);
                       // Checking that the socket is closed. If it is closed , remove connectionArray
                        if (temp_socket.isClosed()) {
                            Server.connectionArray.remove(temp_socket);
                            continue;
                        }
                        PrintWriter temp_out = new PrintWriter(temp_socket.getOutputStream());
                        temp_out.println(MESSAGE);
                        temp_out.flush();




                    }
                }
            }
            finally {
                socket.close();
            }


        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}