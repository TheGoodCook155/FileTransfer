package sample;

import javafx.concurrent.Task;
import javafx.scene.control.ProgressBar;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class Receive extends Task<Void> {

    private InputStream inputStream;
    private File receivedFile;
    private String stringPath;
    private ProgressBar progressBar;


    public Receive(InputStream inputStream, File receivedFile, String stringPath,ProgressBar progressBar){
        this.inputStream = inputStream;
        this.receivedFile = receivedFile;
        this.stringPath = stringPath;
        this.progressBar = progressBar;
    }


    @Override
    protected Void call() throws Exception {

        try {
            BufferedInputStream buffInputStream = new BufferedInputStream(inputStream,8192);
            DataInputStream dataInputStream = new DataInputStream(buffInputStream);
            String fileName = dataInputStream.readUTF();
            long fileSize = dataInputStream.readLong();
            String fileName2 = "DOWNLOAD"+fileName;
            Path path = Paths.get(stringPath+fileName2);

            FileOutputStream fileOutputStream = new FileOutputStream(String.valueOf(path));

//            System.out.println("FileSize in receive is " + fileSize);
            byte[] buffer = new byte[8192];
            updateMessage("Waiting to receive a file...");
            int c;
            int count = 0;
            while ((c = dataInputStream.read(buffer)) > 0) {
                updateProgress(count,fileSize);
                updateMessage("Receiving...");
                fileOutputStream.write(buffer,0,c);
                fileOutputStream.flush();
                count+= buffer.length;
            }
            updateMessage("Done...Waiting to receive another file");


            System.out.println("File upload succeeded!");


        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
