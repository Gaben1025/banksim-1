package edu.temple.cis.c3238.banksim;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Cay Horstmann
 * @author Modified by Paul Wolfgang
 * @author Modified by Charles Wang
 * @author Modified by Alexa Delacenserie
 * @author Modified by Tarek Elseify
 */


public class Account {

    private volatile int balance;
    private final int id;
    private final Bank b;

    public Account(int id, int initialBalance, Bank b) {
        this.id = id;
        this.balance = initialBalance;
        this.b = b;
    }
    /**
     * Used to return the current balance of the account
     *
     * @return Returns account balance
     */
    public int getBalance() {
        return balance;
    }

    /**
     * Checks of the account has enough for a withdraw.
     * If there is not enough funds, the account thread will wait until notified that a deposit has been issued.
     *
     */

    public synchronized void checkFunds(int amount) {
        while (b.isBankOpen() && amount >= balance) {
            try {
                wait();
            } catch (InterruptedException e) {
            }
        }
    }

    /**
     * Does the actual withdraw from the accounts
     *
     * @return . Returns true of withdraw was successful, or false if not.
     */

    public synchronized boolean withdraw(int amount) {
        if (amount <= balance) {
            int currentBalance = balance;
            Thread.yield(); // Try to force collision
            int newBalance = currentBalance - amount;
            balance = newBalance;
            return true;
        } else {
            return false;
        }
    }

    /**
     * Deposits an amount to the account. Notifies all waiting threads to check their balance for a withdraw.
     *
     */

    public synchronized void deposit(int amount) {
        int currentBalance = balance;
        Thread.yield();   // Try to force collision
        int newBalance = currentBalance + amount;
        balance = newBalance;
        notifyAll();
    }

    @Override
    public String toString() {
        return String.format("Account[%d] balance %d", id, balance);
    }
}