import java.io.*;

public class Command extends AbstractBox{
    private Commands id;

    private Errors error;
    private String filename;
    private String msg;
    public Command(Commands id){
        this.id = id;
    }

    public Command(Commands id, String filename){
        this.id = id;
        this.filename = filename;
    }

    public Command(String msg, Commands id){
        this.id = id;
        this.msg = msg;
    }

    public Command(Commands id, String filename, String message){
        this.id = id;
        this.filename = filename;
        this.msg = message;
    }

    public Command(Commands id, Errors error) {
        this.id = id;
        this.error = error;
    }

    public Commands getId() {
        return id;
    }

    public String getFileName() {
        return filename;
    }

    public String getMsg() {
        return msg;
    }

    public Errors getError() {
        return error;
    }
}
