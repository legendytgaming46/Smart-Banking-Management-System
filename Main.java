import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int choice;

        do {
            System.out.println("\n======== WELCOME TO SMART BANKING SYSTEM ========");
            System.out.println("1. Create New Account");
            System.out.println("2. View Account Details");
            System.out.println("3. Deposit Money");
            System.out.println("4. Withdraw Money");
            System.out.println("5. Transfer Money");
            System.out.println("6. View Transaction History");
            System.out.println("7. Close Account");
            System.out.println("8. Update Mobile Number / Email");
            System.out.println("9. Apply for Loan");
            System.out.println("10. Exit");
            System.out.print("Enter your choice (1-10): ");
            choice = sc.nextInt();

            switch (choice) {
                case 1:
                    BankOperations.createNewAccount();
                    break;
                case 2:
                    BankOperations.viewAccountDetails();
                    break;
                case 3:
                    BankOperations.depositMoney();
                    break;
                case 4:
                    BankOperations.withdrawMoney();
                    break;
                case 5:
                    BankOperations.transferMoney();
                    break;
                case 6:
                    BankOperations.viewTransactionHistory();
                    break;
                case 7:
                    BankOperations.closeAccount();
                    break;
                case 8:
                    BankOperations.updateContactDetails();
                    break;
                case 9:
                    BankOperations.applyForLoan();
                    break;
                case 10:
                    System.out.println("🚪 Exiting... Thank you for using Smart Banking!");
                    break;
                default:
                    System.out.println("❌ Invalid choice. Please try again.");
            }

        } while (choice != 10);

        sc.close();
    }
}