package exception;

public class InsufficientFundsException extends Exception {
    private static final long serialVersionUID = 1L;

    private double required;
    private double available;

    public InsufficientFundsException(String message) {
        super(message);
    }

    public InsufficientFundsException(double required, double available) {
        super("Insufficient funds! Required: " + String.format("%.2f", required)
                + ", Available: " + String.format("%.2f", available));
        this.required = required;
        this.available = available;
    }

    public double getRequired() {
        return this.required;
    }

    public double getAvailable() {
        return this.available;
    }
}
