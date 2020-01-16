package reViewer;

import java.io.*;

public class ReViewer {

    private static InputStream inputStream;

    public static void main(String[] args) {
        try {
            inputStream = new BufferedInputStream(new FileInputStream("E:\\разное\\mail cups\\ai\\raic\\2019\\c1_lcukw9qycpxz4nhpctenflzpldv6x"));

            model.ServerMessageGame message = model.ServerMessageGame.readFrom(inputStream);

            System.out.println("");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
