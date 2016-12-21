import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.Scanner;

/**
 *
 * @author Flávio Barreiro Landes
 * E-mail: landesflavio@gmail.com
 */
public class Server {

    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException {
        
        int portServer;
        Scanner keyboard = new Scanner(System.in);
        
        System.out.print("Digite a porta que deseja abrir: ");
        portServer = keyboard.nextInt();
        
        ServerSocket server = new ServerSocket(portServer);
        
        System.out.println("Porta aberta \n");  
        
        System.out.println("Servidor iniciado em: " + new Date());
        
        Socket socketClient;
        
        ListennerClient client;
        Thread thread;
        
        while(true) {
            socketClient = server.accept();
            
            System.out.println("Cliente " + socketClient.getInetAddress().getHostAddress() + " conectado as " + new Date());
            
            client = new ListennerClient(socketClient);

            thread = new Thread(client);
            thread.start(); 
        }
    }
    
}
