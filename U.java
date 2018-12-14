/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cowplan16package;
//import java.util.Date;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Collections;
//import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
//import java.lang.*;

//Utility class
public class U {

    //**************************************************************************
    public static double abs(double a) {
        return Math.abs(a);
    }//end: abs
    
    
    //**************************************************************************
    public static double exp(double a) {
        return Math.exp(a);
    }//end: exp
    
    
    //**************************************************************************
    //output: bytes.    1 byte = 8 bits
    public static double freememory() {
        Runtime runtime = Runtime.getRuntime();
        //return ((double)runtime.freeMemory());
        return ((double)(runtime.freeMemory()) /1000000);
    }//end: freememory
 
    
    //**************************************************************************
    public static float maxf(float... n) {    //variable number of parameters
        int i = 0;
        float max = n[i];
        while (++i < n.length) {
            if (n[i] > max) {
                max = n[i];
            }
        }
        return max;
    }//end: maxf
    
    
    //**************************************************************************
    public static float maxi(int... n) {    //variable number of parameters
        int i = 0;
        int max = n[i];
        while (++i < n.length) {
            if (n[i] > max) {
                max = n[i];
            }
        }
        return max;
    }//end: maxi
    
    
    //**************************************************************************
    //MAX2U: Returns the order of the maximum of 2 values.
    public static int max2u(double d1, double d2, int s1, int s2) {
	if(d1 > d2) 
            return s1;
	else 
            return s2;
    }/*** end MAX2 ***/

    //**************************************************************************
    //MAX3U: Returns the order of the maximum of 3 values.
    public static int max3u(double d1, double d2, double d3, int s1, int s2, int s3) {
        if(d1 > d2) if(d1 > d3) return s1; 
        else return s3;
        if(d2 > d3) return s2; 
        else return s3;
    }/*** end MAX3U ***/


    //**************************************************************************
    //MAX4U: Returns the order of the maximum of 4 values.
    public static short max4u(double d1, double d2, double d3, double d4, short s1, short s2, short s3, short s4) {
        if(d1 > d2 && d1 > d3 && d1 > d4) return s1;
        else if(d2 > d3 && d2 > d4) return s2;
        else if(d3 > d4) return s3;
        else return s4;
    }/*** end MAX4U ***/


    //**************************************************************************
    // MAX5U: Returns the order of the maximum of 5 values.
    public static short max5u(double d1, double d2, double d3, double d4, double d5, short s1, short s2, short s3, short s4, short s5) {
        if(d1 > d2 && d1 > d3 && d1 > d4 && d1 > d5) return s1;
        else if(d2 > d3 && d2 > d4 && d2 > d5) return s2;
        else if(d3 > d4 && d3 > d5) return s3;
        else if(d4 > d5) return s4;
        else return s5;
    }/*** end MAX5U ***/

    
    //**************************************************************************
    public static float mini(int... n) {    //variable number of parameters
        int i = 0;
        int min = n[i];
        while (++i < n.length) {
            if (n[i] < min) {
                min = n[i];
            }
        }
        return min;
    }//end: maxi
    
    
    
    //**************************************************************************
    public static void memory(int id) {
        Runtime runtime = Runtime.getRuntime();
        System.out.println();
        System.out.println(id + " max memory  : " + df(((double)runtime.maxMemory())  /1000000,6,0) + " MB");
        System.out.println(id + " total memory: " + df((double)(runtime.totalMemory())/1000000,6,0) + " MB");
        System.out.println(id + " free memory : " + df((double)(runtime.freeMemory()) /1000000,6,0) + " MB");
        System.out.println();
    }//end: memory
    
    
    
    //**************************************************************************
    //NORMAL: Calculates the cumulative probability of a value (z-score) on 
    //the standard normal distribution N(0,1).
    //- A.G. Adams, 1969. Computer Journal, Vol.12, 197-198. 
    //  "Algorithm 39. Areas under the normal curve." Latest revision 23 January 1981.
    //- Algorithm has been slightly rewritten from Fortran to C to Java.
    //- See Hill[741] and http://lib.stat.cmu.edu/apstat/66 subroutine nprob(z,p,q,pdf).
    //- Function is the inverse of NORMINV().
    public static double normal(double zscore) {
	double	a0 = 0.5, a1 = 0.398942280444, a2 = 0.399903438504, a3 = 5.75885480458,
		a4 = 29.8213557808, a5 = 2.62433121679, a6 = 48.6959930692,
		a7 = 5.92885724438, b0 = 0.398942280385, b1 = 3.8052E-8, b2 = 1.00000615302,
		b3 = 3.98064794E-4, b4 = 1.98615381364, b5 = 0.151679116635,
		b6 = 5.29330324926, b7 = 4.8385912808, b8 = 15.1508972451, 
		b9 = 0.742380924027, b10 = 30.789933034, b11 = 3.99019417011,
		yy, zabs, pdf;

	zabs = U.abs(zscore);
	if(zscore >  12.7) return(1);	
	if(zscore < -12.7) return(0);
	yy = a0 * zscore * zscore;
	pdf = exp(-yy) * b0;
	if(zscore >  1.28) return(1-pdf/(zabs-b1+b2/(zabs+b3+b4/(zabs-b5+b6/(zabs+b7-b8/(zabs+b9+b10/(zabs+b11)))))));
        else if(zscore < -1.28) return(pdf/(zabs-b1+b2/(zabs+b3+b4/(zabs-b5+b6/(zabs+b7-b8/(zabs+b9+b10/(zabs+b11)))))));
	else if(zscore >= 0) return(1-(a0-zabs*(a1-a2*yy/(yy+a3-a4/(yy+a5+a6/(yy+a7))))));
        else if(zscore <  0) return(a0-zabs*(a1-a2*yy/(yy+a3-a4/(yy+a5+a6/(yy+a7)))));
        return(-1);
    }//end: normal
    
   
    
    //**************************************************************************
    public static double pow(double a, double b) {
        return Math.pow(a,b);
    }//end: pow
    
    
    
    //**************************************************************************
    public static double sqrt(double a) {
        return Math.sqrt(a);
    }//end: sqrt
    
    
    //**************************************************************************
    public static double ceil(double a) {
        return Math.ceil(a);
    }//end: ceil
    
    
    //**************************************************************************
    //https://stackoverflow.com/questions/2538787/how-to-display-an-output-of-float-data-with-2-decimal-places-in-java
    public static String df(double number, int lead, int digits) {
        DecimalFormat dfo = new DecimalFormat();
        dfo.setMaximumFractionDigits(digits);
        dfo.setMinimumFractionDigits(digits);        
        int lengte = (int)Math.log10(Math.floor(number)) + 1;
        String spaces = "";
        if (lead > lengte) {
            int n = lead - lengte;
            spaces = String.join("", Collections.nCopies(n, " "));
        }
        return(spaces + dfo.format(number));
    }//end: df
    
    
    //**************************************************************************
    //DISCOUNT: Calculates the discount rate for a certain day into the future.
    public static float discount(float interestperperiod, float periods) {
        float rate = (1 / (float)pow((1 + interestperperiod),periods));
        return(rate);
    }//end: discount
    
    
    //**************************************************************************
    public static void print(String l){
        System.out.print(l);
    }//end: print
   
    
    //**************************************************************************
    public static void println(String l){
        System.out.println(l);
    }//end: println
    
    
    //**************************************************************************
    public static void stop(String why) {  //stops Output until keypressed
//        Scanner reader = new Scanner(System.in);  // Reading from System.in
        U.print("[STOP]"+why);
//        int n = reader.nextInt(); // Scans the next token of the input as an int.
//        reader.nextLine();
//        reader.close();       
        try {
            System.in.read();
        }  
        catch(IOException e) {}    
    }//end: stop


    
    //**************************************************************************
    public static void wait(int seconds, String why) {  //stops Output until keypressed
//        Scanner reader = new Scanner(System.in);  // Reading from System.in
        U.print("[WAIT:" + seconds + "]" + why);
        
        try {
            Thread.sleep(1000*seconds);
        } catch (InterruptedException ex) {
            Logger.getLogger(U.class.getName()).log(Level.SEVERE, null, ex);
        } 
//        try {
//            TimeUnit.MINUTES.sleep(seconds);
//        } catch (InterruptedException ex) {
//            Logger.getLogger(U.class.getName()).log(Level.SEVERE, null, ex);
//        }   
    }//end: wait
    
       
}//end: U class


