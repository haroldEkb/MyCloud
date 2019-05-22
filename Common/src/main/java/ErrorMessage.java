public class ErrorMessage extends MessageBox {
    //сообщение об ошибке
    private Errors error;

    public ErrorMessage(Errors error){
        this.error = error;
    }

    public Errors getError() {
        return error;
    }
}
