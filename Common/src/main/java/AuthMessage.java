public class AuthMessage extends MessageBox {
    //Запрос на авторизацию
    private String msg;

    public AuthMessage(String msg){
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }
}
