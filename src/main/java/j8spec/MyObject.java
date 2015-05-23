package j8spec;

public class MyObject {
    private int value;

    public int calculate() {
        return value;
    }

    public int addToMyValue(int value) {
        return this.value + value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getMyValue() {
        return value;
    }
}
