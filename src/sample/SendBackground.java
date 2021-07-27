package sample;

import javafx.concurrent.Task;

import java.io.*;
import java.net.ConnectException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;


public class SendBackground extends Task<Void> {

    private File file;
    private String ip;

    public SendBackground(File file, String ip) throws IOException {
        this.file = file;
        this.ip = ip;
    }


    @Override
    protected Void call() throws Exception {
        try (Socket socket = new Socket(ip, 55555)) {

            BufferedOutputStream outputStream = new BufferedOutputStream(socket.getOutputStream(), 8192);

            DataOutputStream dataOutputStream = new DataOutputStream(outputStream);

            dataOutputStream.writeUTF(file.getName());
            dataOutputStream.writeLong(file.length());

            dataOutputStream.flush();

            BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(file), 8192);


            int c = -1;
            long workDone = 0;
            long fileSize = file.length();


            System.out.println("File size in SendBackground is " + fileSize);
            while ((c = bufferedInputStream.read()) != -1) {
                dataOutputStream.write(c);
                dataOutputStream.flush();
                workDone++;
                updateProgress(workDone, fileSize);
            }
            System.out.println("Work done is " + workDone);


            System.out.println("File sent");

        } catch (ConnectException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


}



