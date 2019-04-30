import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class Test {

    public static void main(String[] args) {
        Thing thing = new Thing(44);

        try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("p.dat"))){

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
