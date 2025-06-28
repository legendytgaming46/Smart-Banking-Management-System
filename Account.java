public class Account {
    private String accountNumber;
    private String name;
    private String dob;
    private String aadhaar;
    private String phone;
    private String email;
    private double balance;
    private boolean isActive;

    public Account(String accNo, String name, String dob, String aadhaar, String phone, String email, double balance) {
        this.accountNumber = accNo;
        this.name = name;
        this.dob = dob;
        this.aadhaar = aadhaar;
        this.phone = phone;
        this.email = email;
        this.balance = balance;
        this.isActive = true;
    }

    public String getAccountNumber() { return accountNumber; }
    public String getName() { return name; }
    public double getBalance() { return balance; }
    public void setBalance(double balance) { this.balance = balance; }
    public boolean isActive() { return isActive; }
    public void closeAccount() { isActive = false; }

    public String toFileFormat() {
        return accountNumber + "," + name + "," + dob + "," + aadhaar + "," + phone + "," + email + "," + balance + "," + isActive;
    }

    public static Account fromFileFormat(String line) {
        String[] parts = line.split(",");
        return new Account(
            parts[0], parts[1], parts[2], parts[3], parts[4], parts[5],
            Double.parseDouble(parts[6])
        );
    }
}