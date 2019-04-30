import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileBox extends AbstractBox {
    private static final int PACKAGE_VOLUME = 10485760;
    private byte[] content;
    private String fileName;
    private int length;

    public FileBox(Path path) throws IOException {
        fileName = path.getFileName().toString();
        content = Files.readAllBytes(path);
    }

    public byte[] getContent() {
        return content;
    }

    public String getFileName() {
        return fileName;
    }

//    static synchronized FileBox receiveFile(DataInputStream in) throws IOException {
//        FileBox file = new FileBox();
//        try{
//            ObjectInputStream ois = new ObjectInputStream(in);
//            file = (FileBox) ois.readObject();
//            System.out.println("File transfer completed");
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        }
//        return file;
//    }

}
