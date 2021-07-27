package sample;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ServerSocketBackground extends Task<Void> {

    private File receivedFile;
    private String path;
    private  Socket socket;
    private ProgressBar progressBar;
    private Label infoLabel;

    public ServerSocketBackground(File receivedFile, String path, ProgressBar progressBar, Label infoLabel){
        this.receivedFile = receivedFile;
        this.path = path;
        this.progressBar = progressBar;
        this.infoLabel = infoLabel;
    }


    @Override
    protected Void call() throws Exception {
        try (ServerSocket serverSocket = new ServerSocket(55555)){

            System.out.println("Server started...");

            while (true) {
                System.out.println("Waiting to receive file");
                 socket = serverSocket.accept();
                Task<Void> task = new Receive(socket.getInputStream(),receivedFile,path,progressBar);
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.progressProperty().bind(task.progressProperty());
                    }
                });

                task.setOnRunning(e->{
                    progressBar.setVisible(true);
                });
                task.setOnSucceeded(e->{
                    progressBar.setVisible(false);
                    infoLabel.setVisible(false);

                });
                Thread thread = new Thread(task);
                thread.start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
