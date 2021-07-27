package sample;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;

import java.io.*;
import java.net.ConnectException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.FileSystem;


public class Controller {

    @FXML
    private GridPane gridPane;

    @FXML
    private Button sendButton;

    @FXML
    private Button pickFile;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private ToggleButton receiveToggle;

    @FXML
    ToggleButton sendToggle;

    @FXML
    private Label saveLocationLabel;

    @FXML
    private Label infoLabel;

    @FXML
    private TextField textField;

    private String loadLocation = null;
    private String desktop = System.getProperty("user.home") + "\\Desktop\\";

    private String ip;
    private File file = null;
    private File receivedFile = null;


    public void initialize() {
        progressBar.setVisible(false);
//        receiveToggle.setDisable(true);
//        sendToggle.setSelected(true);
        textField.setVisible(false);
        pickFile.setDisable(true);
        infoLabel.setText("Select send or receive mod");
        infoLabel.setVisible(true);
        saveLocationLabel.setVisible(false);
        if (loadLocation == null) {
            sendButton.setDisable(true);
        }

    }

    public String getIp() {

        String input = textField.getText();
//        System.out.println("input is " + input);

        if (input.matches("[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}")) {
            sendButton.setDisable(false);
            infoLabel.setText("OK");
            return textField.getText();
        } else {
            if (!textField.getText().isEmpty()) {
                infoLabel.setText("Invalid IP address");
                sendButton.setDisable(true);
            }

        }
        return "";

    }




    public void toggleButtons() {

        if (!receiveToggle.isSelected() && !sendToggle.isSelected()) {
            infoLabel.setText("Select send or receive mod");
            infoLabel.setVisible(true);
        }
        if (!receiveToggle.isSelected() || !sendToggle.isSelected()) {
            pickFile.setDisable(true);
            sendButton.setDisable(true);
        }
        if (sendToggle.isSelected()) {
            receiveToggle.setDisable(true);
            pickFile.setDisable(false);
            pickFile.setDisable(false);
            textField.setVisible(true);
            infoLabel.setText("Send mod");

        } else if (!sendToggle.isSelected()) {
            receiveToggle.setDisable(false);
            textField.setVisible(false);

        }
        if (receiveToggle.isSelected()) {
            sendToggle.setDisable(true);
            pickFile.setDisable(true);
            receiveToggle.setDisable(true);
            saveLocationLabel.setVisible(true);
            infoLabel.setText("Receive mod");
        } else if (!receiveToggle.isSelected()) {
            sendToggle.setDisable(false);
        }
    }


    public void openFile() throws IOException {

        FileChooser chooser = new FileChooser();
        chooser.setTitle("Please choose file to send");
        chooser.getExtensionFilters().addAll();

        file = chooser.showOpenDialog(gridPane.getScene().getWindow());
        if (file != null) {
            this.loadLocation = file.getPath();
            sendButton.setDisable(false);
        } else if (file == null) {
            System.out.println("Cancelled");
            sendButton.setDisable(true);
        }
    }

    public void sendButtonDisable() {
        if (textField.getText().isEmpty()) {
            sendButton.setDisable(true);
            infoLabel.setText("Enter IP address");
        } else if (getIp().equals("")){
            sendButton.setDisable(true);
            infoLabel.setText("Enter valid IP address");
        } else if (file == null){
            sendButton.setDisable(true);
            infoLabel.setText("Pick a file");
        }
    }


    public void receive() throws IOException {
        //TODO
        receivedFile = new File(desktop);
        Task<Void> task = new ServerSocketBackground(receivedFile, desktop, progressBar);
        Thread thread = new Thread(task);
        progressBar.progressProperty().bind(task.progressProperty());
        task.setOnRunning(e -> {
            progressBar.setVisible(true);
            infoLabel.setText("Receiving...");
        });
        task.setOnSucceeded(e -> {
            progressBar.setVisible(false);
            infoLabel.setVisible(false);
            receiveToggle.setSelected(false);
            receiveToggle.setDisable(false);
            infoLabel.setText(" ");

        });
        if (task.getState() == Task.State.SUCCEEDED) {

        } else {
            thread.start();
        }
    }

    public void send() throws IOException {
        //TODO
        infoLabel.setText("Sending...");

        Task<Void> task = new SendBackground(file, getIp());
//        System.out.println("Task run property " + task.getState());
        Thread thread = new Thread(task);
        thread.setDaemon(true);

        progressBar.progressProperty().bind(task.progressProperty());
        task.setOnRunning(e -> {
            progressBar.setVisible(true);
            infoLabel.setText("Sending...");
        });
        task.setOnSucceeded(e -> {
            progressBar.setVisible(false);
            infoLabel.setVisible(false);
        });
        thread.start();
    }

    //TODO
    public static void killApp() throws IOException {
        Runtime runtime = Runtime.getRuntime();
        Process process = runtime.exec("cmd /c netstat -aon | findstr 55555");

        System.out.println("Process is " + process);
        BufferedReader stdInput = new BufferedReader(new
                InputStreamReader(process.getInputStream()));
        String sc = "";
        String s = null;
        if ((s = stdInput.readLine()) != null) {
            System.out.println("S is " + s);
            int index = s.lastIndexOf(" ");
            sc = s.substring(index, s.length());
        }
        System.out.println("Sc is: " + sc);
        runtime.exec("cmd /c Taskkill /PID" + sc + " /T /F");

        System.out.println("Killed");
    }


}
