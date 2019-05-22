import java.io.Serializable;

public abstract class AbstractBox implements Serializable {
    private int id = 0;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
