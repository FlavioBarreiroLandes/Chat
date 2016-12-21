import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Flávio Barreiro Landes
 * E-mail: landesflavio@gmail.com
 */
public class ListennerClient implements Runnable {
    private static final Map<String, ObjectOutputStream> USERSONLINE = Collections.synchronizedMap(new HashMap<String, ObjectOutputStream>());  
    private ObjectInputStream clientInput;
    private ObjectOutputStream clientOutput;
    private String clientName = ""; 

   
    
    public ListennerClient(Socket socketCliente){
        try {
            this.clientInput = new ObjectInputStream(socketCliente.getInputStream());
            this.clientOutput = new ObjectOutputStream(socketCliente.getOutputStream());
        } catch (IOException ex) {
            Logger.getLogger(ListennerClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    public void run() {
        
        boolean run = true;
        boolean connected;
        Message message = null;
        Message messageThisClient;
        String oldText, newText;
        
        try {
            message = (Message) this.clientInput.readObject();
        } catch (IOException | ClassNotFoundException ex) {
            run = false;
        }  
        //CONNECT, SEND_ALL, SEND_RESTRICT, ADD_USER_NAME_ONLINE, REMOVE_USER_NAME_ONLINE; 
        while(run) {
            switch (message.getAction()) {
                case SEND_ALL:
                    oldText = message.getText();
                    newText = "[" + this.clientName + "]: " + oldText;
                    message.setText(newText);
                    sendAll(message);
                    break;
                case SEND_RESTRICT:
                    oldText = message.getText();
                    
                    // Set message to this client. 
                    newText = "[" + this.clientName + "]" + "[" + message.getReceiverName() + "](Restrict): " + oldText;
                    messageThisClient = new Message();
                    messageThisClient.setClientName(this.clientName);
                    messageThisClient.setReceiverName(this.clientName);
                    messageThisClient.setAction(Message.Action.SEND_RESTRICT);
                    messageThisClient.setText(newText);
                    sendRestrict(messageThisClient);
                    
                    // Set text of the message to another client.
                    newText = "[" + this.clientName + "](Restrict): " + oldText;
                    message.setText(newText);
                    sendRestrict(message);   
                    break;
                default:// case CONNECT
                    connected = connect(message);
                    if (connected == false) {// if the name is duplicated, then close this thread.
                        run = false;
                    }    
                    break;
            }
            
            if (run) {// Case the not name duplicated, then...
                try {
                    message = (Message) this.clientInput.readObject();
                } catch (IOException | ClassNotFoundException ex) {
                    userDisconnected();
                    run = false;
                }
            }    
        }      
    }
    
    // Verify if is possible connect. 
    private boolean connect(Message message) {
        Message messageDuplicateName; 
        Message messageConnected;
        Message messageAddUserNameOnline;
        Message messageAll;
        boolean connected;
        String ClientName = message.getClientName();
        ArrayList<String> namesOnlineUsers;
        
        if (USERSONLINE.containsKey(ClientName)) {// if name duplicate, then...
            messageDuplicateName = new Message();
            messageDuplicateName.setAction(Message.Action.CONNECT);
            messageDuplicateName.setText("Name duplicate!");
            try {
                this.clientOutput.writeObject(messageDuplicateName);
            } catch (IOException ex) {
                Logger.getLogger(ListennerClient.class.getName()).log(Level.SEVERE, null, ex);
            }
            connected = false;
        } else { // if not duplicate name, then...
            this.clientName = ClientName;
            
            messageAddUserNameOnline = new Message();
            messageAddUserNameOnline.setClientName(ClientName);
            messageAddUserNameOnline.setAction(Message.Action.ADD_USER_NAME_ONLINE);
            sendAll(messageAddUserNameOnline);
            
            USERSONLINE.put(ClientName, clientOutput);
            
            namesOnlineUsers = getUsersNamesOnline();
            messageConnected = new Message();
            messageConnected.setReceiverName(ClientName);
            messageConnected.setAction(Message.Action.CONNECT);
            messageConnected.setNamesOnlineUsers(namesOnlineUsers);
            messageConnected.setText("");
            sendRestrict(messageConnected);

            messageAll = new Message();
            messageAll.setClientName(ClientName);
            messageAll.setAction(Message.Action.SEND_ALL);                   
            messageAll.setText("[" + ClientName + "]: " + "Connected...");
            sendAll(messageAll);
            connected = true;
        }
        
        return connected;
    }
    
    // Send a message to all users.
    private void sendAll(Message message) {
        ObjectOutputStream output; 
        
        for (Map.Entry<String, ObjectOutputStream> user : USERSONLINE.entrySet()) {
            output = user.getValue();

            try {
                output.writeObject(message);
            } catch (IOException ex) {
                Logger.getLogger(ListennerClient.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }
    
    // Send a message restrict to a user.
    private void sendRestrict(Message message) {
        String receiverName = message.getReceiverName();
        ObjectOutputStream receiverOutput;
        
        if (USERSONLINE.containsKey(receiverName)) {
            receiverOutput = USERSONLINE.get(receiverName);

            try {
                receiverOutput.writeObject(message);
            } catch (IOException ex) {
                Logger.getLogger(ListennerClient.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }
    
    // Remove it user of the list of the users online and send message to all notifying this act.
    private void userDisconnected() {
        Message messageDisconnect;
        Message messageRemoveUserNameOnline;
        
        USERSONLINE.remove(this.clientName);// Remove the client that is offline.   
        
        messageDisconnect = new Message();
        messageDisconnect.setClientName(this.clientName);
        messageDisconnect.setAction(Message.Action.SEND_ALL);
        messageDisconnect.setText("[" + this.clientName + "]: Disconnected...");
        sendAll(messageDisconnect);

        messageRemoveUserNameOnline = new Message();
        messageRemoveUserNameOnline.setClientName(this.clientName);
        messageRemoveUserNameOnline.setAction(Message.Action.REMOVE_USER_NAME_ONLINE);
        sendAll(messageRemoveUserNameOnline);
    }
    
    // Converter the map of the user to an ArrayList and return this.
    private ArrayList<String> getUsersNamesOnline() {
        ArrayList<String> namesOnlineUsers = new ArrayList<String>();
        String name;
        
        for (Map.Entry<String, ObjectOutputStream> user : USERSONLINE.entrySet()) {
            name = user.getKey();
            namesOnlineUsers.add(name);
        }    
        
        return namesOnlineUsers;
    }
}
    
