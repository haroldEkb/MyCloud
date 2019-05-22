import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileBox extends AbstractBox {
    //Служит для передачи файлов

    public static final int PACKAGE_VOLUME = 20*1024*1024;
    private byte[] content = new byte[PACKAGE_VOLUME];
    private String fileName;
    private boolean isFirst = true;
    private boolean isLast = true;

    public FileBox(String fileName){
        this.fileName = fileName;
        this.isLast = false;
    }


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

    public boolean isFirst(){
        return isFirst;
    }

    public boolean isLast() {
        return isLast;
    }

    public static void writeInBox(FileBox fb, RandomAccessFile raf, boolean isLast) throws IOException {
        if (raf.getFilePointer() != 0) fb.isFirst = false;
        fb.isLast = isLast;
        if (!isLast){
            raf.read(fb.content, 0, PACKAGE_VOLUME);
        } else {
            fb.content = new byte[(int)(raf.length() - raf.getFilePointer())];
            raf.read(fb.content);
        }
    }
}
