public class FileMessage extends MessageBox {
    private String filename;
    private String msg;

    public FileMessage(String filename) {
        this.filename = filename;
    }

    public FileMessage(String filename, String msg) {
        this.filename = filename;
        this.msg = msg;
    }

    public String getFilename() {
        return filename;
    }

    public String getMsg() {
        return msg;
    }
}
