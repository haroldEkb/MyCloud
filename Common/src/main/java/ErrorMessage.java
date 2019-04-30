public class ErrorMessage extends MessageBox {
    private Errors error;
    private String filename;

    public ErrorMessage(Errors error){
        this.error = error;
    }

    public Errors getError() {
        return error;
    }
}
