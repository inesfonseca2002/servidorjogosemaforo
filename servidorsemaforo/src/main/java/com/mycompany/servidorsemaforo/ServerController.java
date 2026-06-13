package com.mycompany.servidorsemaforo;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import java.net.InetAddress;

public class ServerController {

    @FXML private Label ipLabel;
    @FXML private Label portaLabel;
    @FXML private ListView<String> clientListView;

    private MultiploServidor servidor;

    @FXML
    public void initialize() {
        try {
            String ip = InetAddress.getLocalHost().getHostAddress();
            ipLabel.setText(ip);
            portaLabel.setText("1234");
        } catch (Exception e) {
            ipLabel.setText("localhost");
        }

        servidor = new MultiploServidor(clientListView);
        Thread t = new Thread(servidor);
        t.setDaemon(true);
        t.start();
    }

    @FXML
    private void handleSair() {
        Platform.exit();
        System.exit(0);
    }
}