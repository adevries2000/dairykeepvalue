/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cowplan16package;

//import static cowplan16sourcepackage.Cowplan16.maxI;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;

/**
 *
 * @author devries
 */
public class Junk {
 
    
//    void multitread() {
//            // fill data[][] with random numbers    
//        U.println("Fill data[][] with random numbers");
//        double[][] data = new double[maxI][maxJ];
//        for(int i = 0; i < maxI; i++) {
//            for(int j = 0; j < maxJ; j++) {
//                data[i][j] = Math.random();         
//                //System.out.println("random: " + i + " " + data[i]);
//            }
//        }
//         
//        // test multithreading
//        System.out.println("Start test multithreading");        
//        ExecutorService executor = Executors.newFixedThreadPool(maxThreads);
//        int increment = data.length / maxThreads;
//        System.out.println("increment: " + increment);
//        for(int i = 0; i < maxThreads; i++) {
//            //new Thread(new MultiArray(i * increment, (i + 1) * increment, data)).start();
//            executor.execute(new Cowplan16(i * increment, (i + 1) * increment, data));
//            //System.out.println(" currentthreadid: " + Thread.currentThread().getId());
//        }
//        executor.shutdown();                        //page 985 book: Introduction to Java Programming eighth edition;
//        while(executor.isTerminated() == false) {   //wait until all tasks are finished
//        }
//        System.out.println("End test multithreading");
//    
//    }//end: multithread
    
    
    
    
    
    
    
    
    
    
    
    
    
//    public class Worker implements Runnable {
//        final private int minIndex; // first index, inclusive
//        final private int maxIndex; // last index, exclusive
//        final private float[] data;
//
//        public Worker(int minIndex, int maxIndex, float[] data) {
//            this.minIndex = minIndex;
//            this.maxIndex = maxIndex;
//            this.data = data;
//        }
//
//        public void run() {
//            for(int i = minIndex; i < maxIndex; i++) {
//                if(data[i] >= 0.75) {
//                    U.println("i:" + i + " data[i]:" + data[i]);
//                }
//            }
//        }
//    }
//
//
//    // *** Main Thread ***
//    float[] data = new float[12000];
//    int increment = data.length / 8;
//    for(int i = 0; i < 8; i++) {
//        new Thread(new Worker(i * increment, (i + 1) * increment, data)).start();
//    }
    
  

//    public class Worker implements Runnable {
//        private final int numberOfIterations;
//        private final float[] x = new float[12000];
//
//        public Worker(int numberOfIterations) {
//            this.numberOfIterations = numberOfIterations;
//        }
//
//        public void run() {
//            for(int i = 0; i < numberOfIterations; i++) {
//                Random random = new Random();
//
//                for (int i = 0; i < x.length; i++) {
//                    x[i] = random.nextFloat();
//                }
//
//                for (int i = 0; i < x.length; i++) {
//                    if (x[i] >= 0.75) {
//                        \\ do something interesting
//                    }
//                }
//            }
//        }
//    }
//
//
//    // *** Main Thread ***
//    Thread[] threads = new Thread[8];
//    for(int i = 0; i < 8; i++) {
//        threads[i] = new Thread(new Worker(12000/8));
//        threads[i].start();
//    }
//    for(int i = 0; i < 8; i++) {
//        threads[i].join();
//    }


}