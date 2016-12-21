import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;

/**
 *
 * @author Flávio Barreiro Landes
 * E-mail: landesflavio@gmail.com
 */
public class MainInterface extends javax.swing.JFrame {
    
    Socket socketServer = null;
    private ObjectOutputStream serverOutput;

    /**
     * Creates new form Interface
     */
    public MainInterface() {
        initComponents(); 
      
        this.btnSend.setEnabled(false);
        this.txtAreaSend.setEnabled(false);
        this.jListUsersOnline.setVisible(false); 
    }
    
    // Class to listenner the server and get message and send message. 
    public class ListennerServer implements Runnable {
        
        private ObjectInputStream serverInput;


        public ListennerServer(Socket socketServer) {

            try {
                this.serverInput = new ObjectInputStream(socketServer.getInputStream());
            } catch (IOException ex) {
                Logger.getLogger(ListennerServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        @Override
        public void run() {

            boolean run = true;
            String text;
            String userName;
            Message message = null;
            ArrayList<String> namesOnlineUsers;


            try {
                message = (Message) this.serverInput.readObject();
            } catch (IOException | ClassNotFoundException ex) {
                run = false;
            }

            //CONNECT, SEND_ALL, SEND_RESTRICT, ADD_USER_NAME_ONLINE, REMOVE_USER_NAME_ONLINE;   
            while(run) {
                switch (message.getAction()) {
                    case SEND_ALL:
                        text =  message.getText();
                        txtAreaMessage.append(text + '\n');
                        break;
                    case SEND_RESTRICT:
                        text =  message.getText();
                        txtAreaMessage.append(text + '\n');
                        break;
                    case ADD_USER_NAME_ONLINE:
                        userName = message.getClientName();
                        addUserNameOnline(userName);
                        break;
                    case REMOVE_USER_NAME_ONLINE:
                        userName = message.getClientName();
                        removeUserNameOnline(userName);
                        break;
                    default:// case CONNECT
                        text =  message.getText();
                        if (text.equals("Name duplicate!")) {
                            JOptionPane.showMessageDialog(null, "Nome duplicado!");
                            txtName.setEditable(true);
                            btnConect.setEnabled(true);
                            run = false;
                        } else {
                            JOptionPane.showMessageDialog(null, "Conectado com sucesso!");
                            namesOnlineUsers = message.getNamesOnlineUsers();
                            addAllUserNamesOnline(namesOnlineUsers); 
                            txtName.setEditable(false);
                            btnConect.setEnabled(false);
                            jListUsersOnline.setVisible(true);
                            txtAreaSend.setEnabled(true);
                            btnSend.setEnabled(true);
                        }  
                        break;
                }

                if (run) {// Case the not name duplicated, then...
                    try {
                        message = (Message) this.serverInput.readObject();
                    } catch (IOException | ClassNotFoundException ex) {
                        JOptionPane.showMessageDialog(null, "You are Diconnected...");
                        btnConect.setEnabled(true);
                        btnSend.setEnabled(false);
                        txtAreaSend.setEnabled(false);
                        jListUsersOnline.setVisible(false);
                        run = false;
                    }
                }    
            }  
        }  
    }
 
    // Adds users' names online.
    private void addAllUserNamesOnline(ArrayList<String> namesOnlineUsers) {
        DefaultListModel modelNamesOnlineUsers = new DefaultListModel(); 
        
        namesOnlineUsers.add(0, "All");
        
        for (int index = 0; index < namesOnlineUsers.size(); index++) {
            modelNamesOnlineUsers.addElement(namesOnlineUsers.get(index));
        }
        
        this.jListUsersOnline.setModel(modelNamesOnlineUsers); 
    }
    
    // Send message to connect in the server.
    private void sendConnect() {
        Message message = new Message();
        message.setAction(Message.Action.CONNECT);
        message.setClientName(this.txtName.getText());
        
        try {
            this.serverOutput.writeObject(message);
        } catch (IOException ex) {
            Logger.getLogger(MainInterface.class.getName()).log(Level.SEVERE, null, ex);
        }  
    }
    
    // Add user name online.
    private void addUserNameOnline(String userName) {
        DefaultListModel modelNamesOnlineUsers;
        
        modelNamesOnlineUsers = (DefaultListModel) this.jListUsersOnline.getModel(); 
        modelNamesOnlineUsers.addElement(userName); 
    }
    
    // Remove user name online.
    private void removeUserNameOnline(String userName) {
        DefaultListModel modelNamesOnlineUsers;
        String userReceiver = this.lblReceiverName.getText();
        int indexUserName;
        
        if (userReceiver.equals(userName)) {
            this.lblReceiverName.setText("All");
            JOptionPane.showMessageDialog(null, userReceiver + " disconnected...");
        }
        
        modelNamesOnlineUsers = (DefaultListModel) this.jListUsersOnline.getModel(); 
        indexUserName = modelNamesOnlineUsers.indexOf(userName);
        modelNamesOnlineUsers.remove(indexUserName);
        this.jListUsersOnline.setModel(modelNamesOnlineUsers);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        spAreaMessage = new javax.swing.JScrollPane();
        txtAreaMessage = new javax.swing.JTextArea();
        txtAreaSend = new javax.swing.JTextField();
        btnSend = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jspUsersOnline = new javax.swing.JScrollPane();
        jListUsersOnline = new javax.swing.JList<>();
        jLabel2 = new javax.swing.JLabel();
        btnConect = new javax.swing.JButton();
        txtName = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        lblReceiverName = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setFont(new java.awt.Font("Aharoni", 0, 12)); // NOI18N
        setResizable(false);

        txtAreaMessage.setEditable(false);
        txtAreaMessage.setColumns(20);
        txtAreaMessage.setRows(5);
        txtAreaMessage.setRequestFocusEnabled(false);
        spAreaMessage.setViewportView(txtAreaMessage);

        btnSend.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        btnSend.setText("Enviar");
        btnSend.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSendActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel1.setText("Nome:");

        jListUsersOnline.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jListUsersOnlineMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jListUsersOnlineMouseEntered(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jListUsersOnlineMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jListUsersOnlineMouseReleased(evt);
            }
        });
        jspUsersOnline.setViewportView(jListUsersOnline);

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel2.setText("Usuários conectados");

        btnConect.setText("Conectar");
        btnConect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConectActionPerformed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel3.setText("Destino:");

        lblReceiverName.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        lblReceiverName.setText("All");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(spAreaMessage, javax.swing.GroupLayout.PREFERRED_SIZE, 542, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(135, 135, 135)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtName, javax.swing.GroupLayout.PREFERRED_SIZE, 206, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnConect))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addComponent(jLabel3)
                                .addGap(18, 18, 18)
                                .addComponent(lblReceiverName, javax.swing.GroupLayout.PREFERRED_SIZE, 159, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(220, 220, 220)
                                .addComponent(btnSend, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(txtAreaSend, javax.swing.GroupLayout.PREFERRED_SIZE, 542, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(155, 155, 155)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jspUsersOnline, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(23, 23, 23))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel2)
                        .addGap(6, 6, 6))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnConect, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtName))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(spAreaMessage, javax.swing.GroupLayout.PREFERRED_SIZE, 246, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(35, 35, 35)
                        .addComponent(txtAreaSend, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(btnSend, javax.swing.GroupLayout.DEFAULT_SIZE, 39, Short.MAX_VALUE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblReceiverName, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addComponent(jspUsersOnline, javax.swing.GroupLayout.PREFERRED_SIZE, 387, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    
    private void btnSendActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSendActionPerformed
        Message message = new Message();
        String receiverName = this.lblReceiverName.getText();
        String text = this.txtAreaSend.getText();
        
        if (text.isEmpty() == false) {
            message.setText(text);

            if (receiverName.equals("All")) {
                message.setAction(Message.Action.SEND_ALL);
            } else {
                message.setAction(Message.Action.SEND_RESTRICT);
                message.setReceiverName(receiverName);
            }

            try {
                this.serverOutput.writeObject(message);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, "Erro ao enviar a Mensagem!");
            }

            txtAreaSend.setText("");
        }
        txtAreaSend.requestFocus();
    }//GEN-LAST:event_btnSendActionPerformed

    private void btnConectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConectActionPerformed
        String ipservidor;
        int portaservidor;
        
        if (this.txtName.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Por favor, preencha o campo Nome.");
        }
        else {
            try {
                ipservidor = JOptionPane.showInputDialog("Digite o IP do servidor:");
                portaservidor = Integer.parseInt(JOptionPane.showInputDialog("Digite a porta do servidor:"));
                
                this.socketServer = new Socket(ipservidor, portaservidor);
                this.serverOutput = new ObjectOutputStream(socketServer.getOutputStream());

                sendConnect();// Send CONNECT to server.
                this.txtName.setEditable(false);
                this.btnConect.setEnabled(false);

                ListennerServer inputServer = new ListennerServer(this.socketServer);

                Thread threadInputServer = new Thread(inputServer);

                threadInputServer.start();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Falha ao se conectar ao servidor! Tente novamente...");
                 this.txtName.setEditable(true);
                 this.btnConect.setEnabled(true);
            }
        }    
    }//GEN-LAST:event_btnConectActionPerformed

    private void jListUsersOnlineMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jListUsersOnlineMouseClicked
        String userSelected = jListUsersOnline.getSelectedValue();
        this.lblReceiverName.setText(userSelected);
    }//GEN-LAST:event_jListUsersOnlineMouseClicked

    private void jListUsersOnlineMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jListUsersOnlineMousePressed
        // TODO add your handling code here:
    }//GEN-LAST:event_jListUsersOnlineMousePressed

    private void jListUsersOnlineMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jListUsersOnlineMouseEntered
         // TODO add your handling code here:
    }//GEN-LAST:event_jListUsersOnlineMouseEntered

    private void jListUsersOnlineMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jListUsersOnlineMouseReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_jListUsersOnlineMouseReleased

 
    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MainInterface.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainInterface.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainInterface.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainInterface.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MainInterface().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnConect;
    private javax.swing.JButton btnSend;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JList<String> jListUsersOnline;
    private javax.swing.JScrollPane jspUsersOnline;
    private javax.swing.JLabel lblReceiverName;
    private javax.swing.JScrollPane spAreaMessage;
    private javax.swing.JTextArea txtAreaMessage;
    private javax.swing.JTextField txtAreaSend;
    private javax.swing.JTextField txtName;
    // End of variables declaration//GEN-END:variables
}
