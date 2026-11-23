package menu;

import java.util.Scanner;

public abstract class AbstractMenu {
    protected Scanner scanner;
    protected String title;
    protected String[] options;

    public AbstractMenu(Scanner scanner, String title, String[] options) {
        this.scanner = scanner;
        this.title = title;
        this.options = options;
    }

    protected abstract boolean handleChoice(int choice);

    public void run() {
        boolean continueLoop = true;
        while (continueLoop) {
            display();
            int choice = readIntChoice();
            if (choice == -1) {
                System.out.println("  [!] Invalid selection. Please enter a valid menu number.");
                continue;
            }
            continueLoop = handleChoice(choice);
        }
    }

    protected void display() {
        System.out.println();
        System.out.println("==================================================");
        System.out.println("  " + this.title);
        System.out.println("==================================================");
        for (int i = 0; i < options.length; i++) {
            System.out.println("  [" + (i + 1) + "] " + options[i]);
        }
        System.out.print("  Select option (1-" + options.length + "): ");
    }

    protected int readIntChoice() {
        try {
            String input = scanner.nextLine().trim();
            if (input.isEmpty())
                return -1;
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    protected String readPrompt(String message) {
        System.out.print("  " + message);
        return scanner.nextLine().trim();
    }

    protected double readDoublePrompt(String message) {
        System.out.print("  " + message);
        try {
            double val = Double.parseDouble(scanner.nextLine().trim());
            if (val <= 0.0) {
                System.out.println("  [!] Amount must be strictly greater than 0.");
                return -1.0;
            }
            return val;
        } catch (NumberFormatException e) {
            System.out.println("  [!] Please enter a valid decimal number.");
            return -1.0;
        }
    }

    protected int readIntPrompt(String message) {
        System.out.print("  " + message);
        try {
            int val = Integer.parseInt(scanner.nextLine().trim());
            if (val <= 0) {
                System.out.println("  [!] Quantity must be at least 1.");
                return -1;
            }
            return val;
        } catch (NumberFormatException e) {
            System.out.println("  [!] Please enter a valid whole number.");
            return -1;
        }
    }
}
