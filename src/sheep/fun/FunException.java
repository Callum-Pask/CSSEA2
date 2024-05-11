package sheep.fun;

public class FunException extends Exception {
    public FunException(String message) {
        super(message);
    }

    public FunException(Exception base) {
        super(base);
    }
}
