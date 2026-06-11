/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.servidorsemaforo;;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 *
 * @author Romero
 */
public class MultiploServidor {
    static List<ClientHandler> ar = new ArrayList<>();
    static int i = 0;
    
    public static void main(String[] args) throws IOException {
        
        ServerSocket ss = new ServerSocket(1234);
        Socket s;
        System.out.println("À espera de conexões.");
        while(true){
            s = ss.accept();
            System.out.println("Cliente "+ i + " conectou: " + s);
            DataInputStream dis = new DataInputStream(s.getInputStream());
            DataOutputStream dos = new DataOutputStream(s.getOutputStream());

            ClientHandler mtch = new ClientHandler(s, "client"+i, dis, dos);

            Thread t = new Thread(mtch);

            ar.add(mtch);

            t.start();
            i++;
    }
    }

    private static class ClientHandler implements Runnable {
        final DataInputStream dis;
        final DataOutputStream dos;
        final Socket s;
        private String name;
        boolean isLoggedin;

        private ClientHandler(Socket s, String string, DataInputStream dis, DataOutputStream dos) {
            this.s = s;
            this.dis = dis;
            this.dos = dos;
            this.name = string;
            this.isLoggedin = true;
        }

        @Override
        public void run() {
            try {
                dos.writeUTF("Conetado (" + this.name + ")");
            } catch(IOException e) {};
            String recebido;
            while(true){
                try {
                    recebido = dis.readUTF();
                    System.out.println(recebido);
                    if(recebido.equals("logout")){
                        this.isLoggedin = false;
                        this.s.close();
                        ar.remove(this);
                        for(ClientHandler mc:ar)
                            mc.dos.writeUTF(this.name + " desconectou.");
                        System.out.println(this.name + " desconectou.");
                        break;
                    }
                    
                    for(ClientHandler mc:ar){
                        if(!mc.name.equals(this.name))
                            mc.dos.writeUTF(this.name + ": "+ recebido);
                    }
                } catch (IOException e) {
                    e.getMessage();
                }
            }
            try {
                this.dis.close();
                this.dos.close();
            } catch (IOException e) {
            }
        }
    }
}
