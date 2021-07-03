package com.techelevator.tenmo;

import com.techelevator.tenmo.models.AuthenticatedUser;
import com.techelevator.tenmo.models.Transfer;
import com.techelevator.tenmo.models.User;
import com.techelevator.tenmo.models.UserCredentials;
import com.techelevator.tenmo.services.AuthenticationService;
import com.techelevator.tenmo.services.AuthenticationServiceException;
import com.techelevator.tenmo.services.TenmoService;
import com.techelevator.tenmo.services.TenmoServiceException;
import com.techelevator.view.ConsoleService;

import java.math.BigDecimal;

public class App {

    private static final String API_BASE_URL = "http://localhost:8080/";

    private static final String MENU_OPTION_EXIT = "Exit";
    private static final String LOGIN_MENU_OPTION_REGISTER = "Register";
    private static final String LOGIN_MENU_OPTION_LOGIN = "Login";
    private static final String[] LOGIN_MENU_OPTIONS = {LOGIN_MENU_OPTION_REGISTER, LOGIN_MENU_OPTION_LOGIN, MENU_OPTION_EXIT};
    private static final String MAIN_MENU_OPTION_VIEW_BALANCE = "View your current balance";
    private static final String MAIN_MENU_OPTION_SEND_BUCKS = "Send TE bucks";
    private static final String MAIN_MENU_OPTION_VIEW_PAST_TRANSFERS = "View your past transfers";
    private static final String MAIN_MENU_OPTION_LOGIN = "Login as different user";
    private static final String[] MAIN_MENU_OPTIONS = {MAIN_MENU_OPTION_VIEW_BALANCE, MAIN_MENU_OPTION_SEND_BUCKS, MAIN_MENU_OPTION_VIEW_PAST_TRANSFERS, MAIN_MENU_OPTION_LOGIN, MENU_OPTION_EXIT};

    private AuthenticatedUser currentUser;
    private ConsoleService console;
    private AuthenticationService authenticationService;
    private TenmoService tenmoService;

    public static void main(String[] args) {
        App app = new App(new ConsoleService(System.in, System.out), new AuthenticationService(API_BASE_URL), new TenmoService(API_BASE_URL));
        app.run();
    }

    public App(ConsoleService console, AuthenticationService authenticationService, TenmoService tenmoService) {
        this.console = console;
        this.authenticationService = authenticationService;
        this.tenmoService = tenmoService;
    }

    public void run() {
        System.out.println("*********************");
        System.out.println("* Welcome to TEnmo! *");
        System.out.println("*********************");

        registerAndLogin();
        mainMenu();
    }

    private void mainMenu() {
        while (true) {
            String choice = (String) console.getChoiceFromOptions(MAIN_MENU_OPTIONS);
            if (MAIN_MENU_OPTION_VIEW_BALANCE.equals(choice)) {
                viewCurrentBalance();
            } else if (MAIN_MENU_OPTION_VIEW_PAST_TRANSFERS.equals(choice)) {
                viewTransferHistory();
            } else if (MAIN_MENU_OPTION_SEND_BUCKS.equals(choice)) {
                sendBucks();
            } else if (MAIN_MENU_OPTION_LOGIN.equals(choice)) {
                login();
            } else {
                // the only other option on the main menu is to exit
                exitProgram();
            }
        }
    }

    private void viewCurrentBalance() {
        try {
            BigDecimal balance = tenmoService.getBalanceData(currentUser.getToken());
            System.out.println("Your current account balance is: $" + balance.toString());
        } catch (TenmoServiceException e) {
            e.printStackTrace();
        }
    }

    private void viewTransferHistory() {
        try {
            long currentUserId = currentUser.getUser().getId();
            Transfer[] transfers = tenmoService.listTransfers(currentUser.getToken());

            if (transfers.length == 0) {
                System.out.println("No transfers have been sent from or to this account.");
                return;
            }

            int maxAmountLength = 0;

            for (Transfer transfer : transfers) {
                if (transfer.getAmount().toString().length() > maxAmountLength) {
                    maxAmountLength = transfer.getAmount().toString().length();
                }
            }
            StringBuilder amountString = new StringBuilder("$ ");
            StringBuilder lines = new StringBuilder("----------------------------------");
            for (int i = 0; i < maxAmountLength; i++) {
                amountString.append(" ");
                lines.append("-");
            }

            System.out.println(lines);
            System.out.println("Transfers");
            System.out.println("ID\t\t\tFrom/To\t\t\t\tAmount");
            System.out.println(lines);

            for (Transfer transfer : transfers) {
                String newAmountString = amountString.substring(0, amountString.length() - transfer.getAmount().toString().length()) + transfer.getAmount();
                if (transfer.getUserTo().getId() == currentUserId) {
                    String senderName = transfer.getUserFrom().getUsername();
                    System.out.println(transfer.getTransferId() + "\t\tFrom: " + senderName + "\t\t\t" + newAmountString);
                } else {
                    String recipientName = transfer.getUserTo().getUsername();
                    System.out.println(transfer.getTransferId() + "\t\tTo:   " + recipientName + "\t\t\t" + newAmountString);
                }
            }
            System.out.println(lines);
            String stringTransferId = console.getUserInput("Please enter transfer ID to view details (0 to cancel)").trim();
            if (stringTransferId.equals("0")) {
                return;
            }
            int transferId;
            try {
                transferId = Integer.parseInt(stringTransferId);
            } catch (NumberFormatException e) {
                System.out.println("Invalid or unspecified Transfer ID.");
                return;
            }

            for (Transfer transfer : transfers) {
                if (transfer.getTransferId() == transferId) {
                    System.out.println("--------------------------------------------");
                    System.out.println("Transfer Details");
                    System.out.println("--------------------------------------------");
                    System.out.println("Id: " + transfer.getTransferId());
                    String senderName = transfer.getUserFrom().getUsername();
                    System.out.println("From: " + senderName);
                    String recipientName = transfer.getUserTo().getUsername();
                    System.out.println("To: " + recipientName);
                    System.out.println("Type: " + transfer.getTransferType());
                    System.out.println("Status: " + transfer.getTransferStatus());
                    System.out.println("Amount: $" + transfer.getAmount());
                    return;
                }
            }

            System.out.println("Invalid Transfer ID Entered. Please specify the ID of a transfer sent to or from your account.");

        } catch (TenmoServiceException e) {
            e.printStackTrace();
        }
    }

    private void sendBucks() {
        /* Display list of possible transfer recipients,
         * not including current user. */
        User[] users;
        User[] usersMinusCurrent;
        try {
            users = tenmoService.listUsers(currentUser.getToken());
            /* Cancel option will be added to list, so length will remain the same,
             * even though current user is removed from list. */
            usersMinusCurrent = new User[users.length];
            int index = 0;
            for (User user : users) {
                if (!user.getUsername().equals(currentUser.getUser().getUsername())) {
                    usersMinusCurrent[index] = user;
                    index++;
                }
            }
            // Add fake user to list to display as Cancel option.
            User cancel = new User();
            cancel.setId(0);
            cancel.setUsername("Cancel Transfer Request");
            usersMinusCurrent[users.length - 1] = cancel;

            System.out.println("Select user to receive transfer (or select option to cancel):");
            User recipient = (User) console.getChoiceFromOptions(usersMinusCurrent);

            // Cancel Option
            if (recipient.getId() == 0) {
                return;
            }

            String stringAmount = console.getUserInput("Please enter the amount to transfer");
            BigDecimal amount = new BigDecimal(stringAmount);

            Transfer transfer = new Transfer("Send", "Approved", currentUser.getUser(), recipient, amount);

            tenmoService.createTransfer(currentUser.getToken(),transfer);

        } catch (TenmoServiceException e) {
            if (e.getMessage().contains("Insufficient Funds for Transfer.")) {
                System.out.println("Insufficient Funds for Transfer. Please retry the transfer with an amount that does not exceed your current balance.");
            } else if (e.getMessage().contains("Transfer amount cannot be negative.")) {
                System.out.println("Transfer amount cannot be less than or equal to zero. Please retry the transfer with a positive transfer amount.");
            } else if (e.getMessage().contains("Transfer could not be processed.")) {
                System.out.println("Transfer could not be processed. Please retry the transfer, ensuring that the transfer amount is greater than 0.00 when rounded to two places.");
            } else {
                System.out.println(e.getMessage());
            }
        }
    }

    private void exitProgram() {
        System.exit(0);
    }

    private void registerAndLogin() {
        while (!isAuthenticated()) {
            String choice = (String) console.getChoiceFromOptions(LOGIN_MENU_OPTIONS);
            if (LOGIN_MENU_OPTION_LOGIN.equals(choice)) {
                login();
            } else if (LOGIN_MENU_OPTION_REGISTER.equals(choice)) {
                register();
            } else {
                // the only other option on the login menu is to exit
                exitProgram();
            }
        }
    }

    private boolean isAuthenticated() {
        return currentUser != null;
    }

    private void register() {
        System.out.println("Please register a new user account");
        boolean isRegistered = false;
        while (!isRegistered) //will keep looping until user is registered
        {
            UserCredentials credentials = collectUserCredentials();
            try {
                authenticationService.register(credentials);
                isRegistered = true;
                System.out.println("Registration successful. You can now login.");
            } catch (AuthenticationServiceException e) {
                System.out.println("REGISTRATION ERROR: " + e.getMessage());
                System.out.println("Please attempt to register again.");
            }
        }
    }

    private void login() {
        System.out.println("Please log in");
        currentUser = null;
        while (currentUser == null) //will keep looping until user is logged in
        {
            UserCredentials credentials = collectUserCredentials();
            try {
                currentUser = authenticationService.login(credentials);
            } catch (AuthenticationServiceException e) {
                System.out.println("LOGIN ERROR: " + e.getMessage());
                System.out.println("Please attempt to login again.");
            }
        }
    }

    private UserCredentials collectUserCredentials() {
        String username = console.getUserInput("Username");
        String password = console.getUserInput("Password");
        return new UserCredentials(username, password);
    }

}
