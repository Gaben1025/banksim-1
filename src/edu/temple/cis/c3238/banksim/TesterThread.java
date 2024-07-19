package edu.temple.cis.c3238.banksim;

import javax.swing.*;
import java.util.concurrent.Semaphore;

public class TesterThread extends Thread {


    private final Semaphore sem;
    private final Bank b;
    public TesterThread(Bank b, Semaphore sem) {
        this.b = b;
        this.sem = sem;
    }

    /**
     * Acquires all semaphore permits so that it can run the test without a race condition. When acquiring the permits, the thread will wait until all permits are available for it.
     * This causes the TransferThread to wait and there is a protection on the race conditions.
     *
     */
    public void run() {
        while(b.isBankOpen()){
            try{
                sem.acquire(b.getNumAccounts());
            }catch (InterruptedException e){}
            if(b.shouldTest()){
                b.test();
            }
            sem.release(b.getNumAccounts());
        }


    }

}