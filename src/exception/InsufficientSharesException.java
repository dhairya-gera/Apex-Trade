package exception;

public class InsufficientSharesException extends Exception {
    private static final long serialVersionUID = 1L;

    private int required;
    private int available;

    public InsufficientSharesException(String message) {
        super(message);
    }

    public InsufficientSharesException(int required, int available) {
        super("Insufficient shares! Required to sell: " + required + ", Available: " + available);
        this.required = required;
        this.available = available;
    }

    public int getRequired() {
        return this.required;
    }

    public int getAvailable() {
        return this.available;
    }
}
