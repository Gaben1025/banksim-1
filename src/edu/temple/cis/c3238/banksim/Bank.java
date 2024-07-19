package edu.temple.cis.c3238.banksim;

import java.util.concurrent.Semaphore;

/**
 * @author Cay Horstmann
 * @author Modified by Paul Wolfgang
 * @author Modified by Charles Wang
 * @author Modified by Alexa Delacenserie
 * @author Modified by Tarek Elseify
 */

public class Bank {
    public static final int NTEST = 10;
    private final Account[] accounts;
    private long numTransactions = 0;
    private final int initialBalance;
    private final int numAccounts;
    private final Semaphore sem;
    //To check if bank is open or not
    private boolean bankOpen;


    public Bank(int numAccounts, int initialBalance, Semaphore sem) {
        this.initialBalance = initialBalance;
        this.numAccounts = numAccounts;
        accounts = new Account[numAccounts];
        for (int i = 0; i < accounts.length; i++) {
            accounts[i] = new Account(i, initialBalance, this);
        }
        numTransactions = 0;
        bankOpen = true;
        this.sem= sem;
    }
    /**
     * Transfer funds from one account to another account. First checks if the account has enough funds for the transfer.
     * If not, the thread will wait until there is available funds. Use semaphores so that when the TesterThread is running, there is protection on the race conditions.
     * Checks if the bank is open or not. Will just return if closed.
     *
     */
    public void transfer(int from, int to, int amount) {
        accounts[from].checkFunds(amount);
        try{
            sem.acquire();
        }catch (InterruptedException e){}
        if(!this.bankOpen){
            sem.release();
            return;
        }
        if (accounts[from].withdraw(amount)) {
            accounts[to].deposit(amount);
            sem.release();
        }

    }
    /**
     * Used for the TesterThread. Checks if the current total balance of the accounts are still the same. If they are different, there was a problem with the race conditions and data was loss and ends the program.
     * If they are the same, the program continues to run.
     *
     */
    public void test() {
        int totalBalance = 0;
        for (Account account : accounts) {
            System.out.printf("%-30s %s%n",
                    Thread.currentThread().toString(), account.toString());
            totalBalance += account.getBalance();
        }
        System.out.printf("%-30s Total balance: %d\n", Thread.currentThread().toString(), totalBalance);
        if (totalBalance != numAccounts * initialBalance) {
            System.out.printf("%-30s Total balance changed!\n", Thread.currentThread().toString());
            System.exit(0);
        } else {
            System.out.printf("%-30s Total balance unchanged.\n", Thread.currentThread().toString());
        }
    }

    /**
     * Returns the number of accounts.
     *
     * @return Returns the number of accounts.
     */
    public int getNumAccounts() {
        return numAccounts;
    }
    /**
     * Used to check whether or the bank is still open. If a thread has finished transactions, the bank will be closed.
     *
     * @return Returns true or false depending on if a Thread is finished or not.
     */
    public boolean isBankOpen(){
        return bankOpen;
    }
    /**
     * Increments a variable and checks if it's mod will equal 0. If it does, test() will run.
     *
     * @return Returns a boolean.
     */
    public boolean shouldTest() {
        return ++numTransactions % NTEST == 0;
    }
    /**
     * Turns bankOpen to false, and closes the bank. When the bank closes, all threads are notified and the program ends.
     *
     */
    public void closeTheBank() {
        synchronized (this) {
            //Make it false right away
            bankOpen = false;
        }
        //Get all accounts to be notified
        for (Account acc : accounts)
        {
            //Synchronize all acc objects
            synchronized (acc)
            {
                acc.notifyAll();
            }
        }
    }

}