package com.blake.exception.test;

import java.util.concurrent.ExecutorService;

import static java.util.concurrent.Executors.newFixedThreadPool;

/*
 测试interrupt是否满足需求
 */
public class InterruptedExceptionTest {

    static ExecutorService tpe = newFixedThreadPool(3);

    public static void main(String args[]) {

        Thread1 t1 = new Thread1();
        Thread2 t2 = new Thread2();
        Thread t1t = new Thread(t1);
        t1t.start();
        Thread t2t = new Thread(t2);
        while (true) {

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            t1t.interrupt();
        }
    }

    static class Thread1 implements Runnable {
        @Override
        public void run() {

            while (true) {
                try {

                    Thread.sleep(1000);
                    long st = System.currentTimeMillis();
                    for(int i=0; i<100000000; i++) {

                        if(i % 10000000 == 0) {
                            System.out.println(i);
                        }
                    }
                    System.out.println("cost: " + (System.currentTimeMillis() - st));
                } catch (InterruptedException e) {
                    // e.printStackTrace();
                    System.out.println("interrupt");
                }
            }

        }
    }

    static class Thread2 implements Runnable {
        @Override
        public void run() {

        }
    }
}
