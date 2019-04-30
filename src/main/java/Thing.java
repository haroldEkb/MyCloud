import java.io.Serializable;

public class Thing implements Serializable {
    private int age;

    public Thing(int age){
        this.age = age;
    }

    public int getAge() {
        return age;
    }
}
