package edu.temple.cis.c3238.banksim;

import java.util.concurrent.Semaphore;

/**
 * @author Cay Horstmann
 * @author Modified by Paul Wolfgang
 * @author Modified by Charles Wang
 * @author Modified by Alexa Delacenserie
 * @author Modified by Tarek Elseify
 */
class TransferThread extends Thread {

    private final Bank bank;
    private final int fromAccount;
    private final int maxAmount;
    private final Semaphore sem;
    public TransferThread(Bank b, int from, int max, Semaphore sem) {
        bank = b;
        fromAccount = from;
        maxAmount = max;
        this.sem = sem;
    }
    /**
     * Does the transfers for the account the thread was assigned to.
     *
     */
    @Override
    public void run() {

        for (int i = 0; i < 10000; i++) {
//            try{
//                sem.acquire();
//            }catch (InterruptedException e){}

            int toAccount = (int) (bank.getNumAccounts() * Math.random());
            int amount = (int) (maxAmount * Math.random());
            bank.transfer(fromAccount, toAccount, amount);

        }

        //Finishes the operations
        System.out.printf("%-30s Account[%d] has finished with its transactions.\n", Thread.currentThread().toString(), fromAccount);
        bank.closeTheBank();
    }
}