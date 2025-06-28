import java.util.*;
import java.io.*;
import java.util.UUID;

public class BankOperations {

    static final String ACCOUNT_FILE = "accounts.txt";
    static final String TRANSACTION_FILE = "transactions.txt";

    public static void createNewAccount() {
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter Full Name: ");
        String name = sc.nextLine();

        System.out.print("Enter Date of Birth (dd-mm-yyyy): ");
        String dob = sc.nextLine();

        System.out.print("Enter Aadhaar Number: ");
        String aadhaar = sc.nextLine();

        System.out.print("Enter Phone Number: ");
        String phone = sc.nextLine();

        System.out.print("Enter Email ID: ");
        String email = sc.nextLine();

        System.out.print("Enter Initial Deposit Amount: ");
        double balance = sc.nextDouble();
        sc.nextLine();

        System.out.print("Enter Account Type (Savings/Current): ");
        String type = sc.nextLine();

        String accountNumber = UUID.randomUUID().toString().replace("-", "").substring(0, 10);

        String record = accountNumber + "," + name + "," + dob + "," + aadhaar + "," + phone + "," + email + "," + balance + "," + type + ",true";

        try {
            FileWriter fw = new FileWriter(ACCOUNT_FILE, true);
            fw.write(record + "\n");
            fw.close();
            System.out.println("✅ Account Created Successfully! Account Number: " + accountNumber);
        } catch (IOException e) {
            System.out.println("❌ Error writing to file.");
        }
    }

    public static void viewAccountDetails() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter Account Number to search: ");
        String accNo = sc.nextLine();
        boolean found = false;

        try (BufferedReader br = new BufferedReader(new FileReader(ACCOUNT_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data[0].equals(accNo)) {
                    System.out.println("\n====== ACCOUNT DETAILS ======");
                    System.out.println("Account Number: " + data[0]);
                    System.out.println("Name: " + data[1]);
                    System.out.println("Date of Birth: " + data[2]);
                    System.out.println("Aadhaar: " + data[3]);
                    System.out.println("Phone: " + data[4]);
                    System.out.println("Email: " + data[5]);
                    System.out.println("Balance: ₹" + data[6]);
                    System.out.println("Account Type: " + data[7]);
                    System.out.println("Status: " + (data[8].equals("true") ? "Active" : "Closed"));
                    found = true;
                    break;
                }
            }
            if (!found) {
                System.out.println("❌ Account not found!");
            }
        } catch (IOException e) {
            System.out.println("❌ Error reading file.");
        }
    }

    public static void depositMoney() {
        updateBalance(true);
    }

    public static void withdrawMoney() {
        updateBalance(false);
    }

    private static void updateBalance(boolean isDeposit) {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter Account Number: ");
        String accNo = sc.nextLine();
        System.out.print("Enter Amount: ");
        double amount = sc.nextDouble();

        File inputFile = new File(ACCOUNT_FILE);
        File tempFile = new File("temp.txt");
        boolean success = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
             FileWriter writer = new FileWriter(tempFile)) {

            String currentLine;
            while ((currentLine = reader.readLine()) != null) {
                String[] data = currentLine.split(",");
                if (data[0].equals(accNo) && data[8].equals("true")) {
                    double balance = Double.parseDouble(data[6]);
                    if (!isDeposit && amount > balance) {
                        System.out.println("❌ Insufficient balance.");
                        writer.write(currentLine + "\n");
                        continue;
                    }
                    balance = isDeposit ? balance + amount : balance - amount;
                    data[6] = String.valueOf(balance);
                    writer.write(String.join(",", data) + "\n");
                    success = true;

                    logTransaction(accNo, (isDeposit ? "Deposit" : "Withdraw"), amount);
                } else {
                    writer.write(currentLine + "\n");
                }
            }
        } catch (IOException e) {
            System.out.println("❌ Error processing transaction.");
            return;
        }

        if (!inputFile.delete() || !tempFile.renameTo(inputFile)) {
            System.out.println("❌ Could not update account.");
        } else if (success) {
            System.out.println("✅ Transaction successful!");
        } else {
            System.out.println("❌ Account not found or inactive.");
        }
    }

    public static void transferMoney() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter Sender Account Number: ");
        String sender = sc.nextLine();
        System.out.print("Enter Receiver Account Number: ");
        String receiver = sc.nextLine();
        System.out.print("Enter Amount to Transfer: ");
        double amount = sc.nextDouble();

        withdrawMoney(sender, amount, false);
        depositMoney(receiver, amount);
        logTransaction(sender, "Transfer to " + receiver, amount);
        logTransaction(receiver, "Received from " + sender, amount);
        System.out.println("✅ Money transferred successfully!");
    }

    private static void withdrawMoney(String accNo, double amount, boolean log) {
        updateBalance(accNo, -amount, log);
    }

    private static void depositMoney(String accNo, double amount) {
        updateBalance(accNo, amount, false);
    }

    private static void updateBalance(String accNo, double amount, boolean log) {
        File inputFile = new File(ACCOUNT_FILE);
        File tempFile = new File("temp.txt");

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
             FileWriter writer = new FileWriter(tempFile)) {
            String currentLine;
            while ((currentLine = reader.readLine()) != null) {
                String[] data = currentLine.split(",");
                if (data[0].equals(accNo)) {
                    double balance = Double.parseDouble(data[6]);
                    balance += amount;
                    data[6] = String.valueOf(balance);
                    writer.write(String.join(",", data) + "\n");
                } else {
                    writer.write(currentLine + "\n");
                }
            }
        } catch (IOException e) {
            System.out.println("❌ Error during balance update.");
        }

        inputFile.delete();
        tempFile.renameTo(inputFile);
    }

    public static void viewTransactionHistory() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter Account Number: ");
        String accNo = sc.nextLine();
        boolean found = false;

        try (BufferedReader br = new BufferedReader(new FileReader(TRANSACTION_FILE))) {
            String line;
            System.out.println("\n====== TRANSACTION HISTORY ======");
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts[0].equals(accNo)) {
                    System.out.println(parts[1] + " - ₹" + parts[2]);
                    found = true;
                }
            }
            if (!found) System.out.println("No transactions found.");
        } catch (IOException e) {
            System.out.println("❌ Error reading transaction file.");
        }
    }

    private static void logTransaction(String accNo, String type, double amount) {
        try (FileWriter fw = new FileWriter(TRANSACTION_FILE, true)) {
            fw.write(accNo + "," + type + "," + amount + "\n");
        } catch (IOException e) {
            System.out.println("❌ Failed to log transaction.");
        }
    }

    public static void closeAccount() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter Account Number to close: ");
        String accNo = sc.nextLine();

        File inputFile = new File(ACCOUNT_FILE);
        File tempFile = new File("temp.txt");

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
             FileWriter writer = new FileWriter(tempFile)) {
            String currentLine;
            while ((currentLine = reader.readLine()) != null) {
                String[] data = currentLine.split(",");
                if (data[0].equals(accNo)) {
                    data[8] = "false";
                    writer.write(String.join(",", data) + "\n");
                } else {
                    writer.write(currentLine + "\n");
                }
            }
        } catch (IOException e) {
            System.out.println("❌ Error closing account.");
            return;
        }

        inputFile.delete();
        tempFile.renameTo(inputFile);
        System.out.println("✅ Account closed successfully.");
    }

    public static void updateContactDetails() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter Account Number: ");
        String accNo = sc.nextLine();
        System.out.print("Enter new Mobile Number: ");
        String phone = sc.nextLine();
        System.out.print("Enter new Email ID: ");
        String email = sc.nextLine();

        File inputFile = new File(ACCOUNT_FILE);
        File tempFile = new File("temp.txt");

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
             FileWriter writer = new FileWriter(tempFile)) {
            String currentLine;
            while ((currentLine = reader.readLine()) != null) {
                String[] data = currentLine.split(",");
                if (data[0].equals(accNo)) {
                    data[4] = phone;
                    data[5] = email;
                    writer.write(String.join(",", data) + "\n");
                } else {
                    writer.write(currentLine + "\n");
                }
            }
        } catch (IOException e) {
            System.out.println("❌ Error updating contact.");
        }

        inputFile.delete();
        tempFile.renameTo(inputFile);
        System.out.println("✅ Contact details updated.");
    }

    public static void applyForLoan() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter Account Number: ");
        String accNo = sc.nextLine();
        System.out.print("Enter Loan Amount: ");
        double amount = sc.nextDouble();

        System.out.println("✅ Loan Application Submitted for ₹" + amount);
        logTransaction(accNo, "Loan Applied", amount);
    }
}