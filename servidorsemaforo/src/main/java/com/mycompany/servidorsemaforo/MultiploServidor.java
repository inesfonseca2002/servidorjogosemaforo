package com.mycompany.servidorsemaforo;

import javafx.application.Platform;
import javafx.scene.control.ListView;
import java.io.*;
import java.net.*;
import java.util.*;

public class MultiploServidor implements Runnable {

    static List<ClientHandler> ar = new ArrayList<>();
    private ListView<String> clientListView;

    public MultiploServidor(ListView<String> listView) {
        this.clientListView = listView;
    }

    @Override
    public void run() {
        try {
            ServerSocket ss = new ServerSocket(1234);
            int i = 0;
            while (true) {
                Socket s = ss.accept();
                DataInputStream dis = new DataInputStream(s.getInputStream());
                DataOutputStream dos = new DataOutputStream(s.getOutputStream());
                ClientHandler ch = new ClientHandler(s, "client" + i, dis, dos);
                ar.add(ch);
                atualizarLista();
                new Thread(ch).start();
                i++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void atualizarLista() {
        Platform.runLater(() -> {
            clientListView.getItems().clear();
            for (ClientHandler c : ar)
                clientListView.getItems().add(c.name + " — " + c.s.getInetAddress());
        });
    }

    class ClientHandler implements Runnable {
        final DataInputStream dis;
        final DataOutputStream dos;
        final Socket s;
        String name;

        ClientHandler(Socket s, String name, DataInputStream dis, DataOutputStream dos) {
            this.s = s;
            this.name = name;
            this.dis = dis;
            this.dos = dos;
        }

        @Override
        public void run() {
            try { dos.writeUTF("Conectado (" + name + ")"); } catch (IOException e) {}
            while (true) {
                try {
                    String msg = dis.readUTF();
                    if (msg.equals("logout")) {
                        ar.remove(this);
                        s.close();
                        atualizarLista();
                        for (ClientHandler c : ar)
                            try { c.dos.writeUTF(name + " desconectou."); } catch (IOException e) {}
                        break;
                    }
                    for (ClientHandler c : ar)
                        if (!c.name.equals(name))
                            try { c.dos.writeUTF(name + ": " + msg); } catch (IOException e) {}
                } catch (IOException e) { break; }
            }
        }
    }
}