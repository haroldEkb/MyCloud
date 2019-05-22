public class FileMessage extends MessageBox {

    //Запрос на передачу файла или отчет о его доставке
    private String filename;
    private String msg;
    private MessageType type;
    private long filePointer = 0;

    public FileMessage(String filename){
        this.filename = filename;
    }

    public FileMessage(String filename, String msg){
        this.filename = filename;
        this.msg = msg;
    }

    public FileMessage (MessageType type, String filename, long filePointer){
        this.type = type;
        this.filename = filename;
        this.filePointer = filePointer;
    }



//    public FileMessage(MessageType type){
//        this.type = type;
//}
//
//    public FileMessage(MessageType type, String filename){
//        this.type = type;
//        this.filename = filename;
//    }
//
//    public FileMessage(MessageType type, String filename, String msg){
//        this.type = type;
//        this.filename = filename;
//        this.msg = msg;
//    }
    public String getFilename() {
        return filename;
    }

    public String getMsg() {
        return msg;
    }

    public MessageType getType() {
        return type;
    }

    public long getFilePointer() {
        return filePointer;
    }
}
