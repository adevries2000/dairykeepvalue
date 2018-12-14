
/*

C.O.W. = Cow's Own Worth = RPO at ICBF.com  
https://twitter.com/search?f=tweets&q=%23cowtocull&src=typd  #CowtoCull
https://www.icbf.com/wp/?p=9448
http://www.journalofdairyscience.org/article/S0022-0302(15)00197-6/pdf 
https://www.icbf.com/wp/?page_id=22 (powerpoint presentations)
Based on reading (Kelleher. JDS 98:4225-4239):
- In Ireland cows are set dry per December 1 due to no grass growth --> make option to not have milking cows in certain weeks of the year
- How to get genetics in my model?  BWT, PER, FER could be considered permant (no transition to other state), but there is not enough space [][][][][][][][] for all traits
==> I could run model once with minimum settings (MBWT = 0, MPER = 0, MFER = 0 to represent an average cow and etc to calculate the heifer cash flow
, then use this heifer cash flow but set some traits higher, e.g. average milk yield, body weight etc.  The RPOs (+ future value for Koe math) than represent the higher producing cows.
, I could repeat this setting of traits a couple of times so all permanent traits are calculated.  "Setting" all results would make a large matrix for Koe math.
, However, the Koe could have all the genetics we need and "map" into the future states.  OR: simply use the net merit(+ permanent effect) states for future lactations
  Maybe the VAL class could be 4 milk yield traits, and we could have 2 more latent classes that represent the values of the remainder of the NM$ traits
  So combine the 14 NM$ traits into say 3 groups of traits: milk (following some kind of lactation curve), survival, and repro, remainder trait.
(So basically, instead of various permanent traits (without transitions), do calculations seperately for the various permanent traits (= a subset of the herd)
- Another idea is to have a state [] that is simply $$ per day. This would be the genetics not in the model (e.g. feed and legs, udder, calving ability etc.) 


Think big: run model on hipergator and online. 






Objectives: multithreading parts of a complex array
http://stackoverflow.com/questions/17584517/partitioning-and-analyzing-a-java-array-with-multithreaded-processing

Accomplished:
- multithreading by dividing an array, using Matrix class
- create an array of objects: ft[].veh[].mpg[] using Fut class

To do:
- public vs. private ??
- when should a class be in a seperate file, e.g. Vehicle.java ??
- list of cows, or array[] of cows: current lactation ??  How do I select a specific cow in the list of cows?
- build complex matrix like rpo[][].fresh[].milk[][], fut[][].[][][] etc.
- do multithreading math with the complex matrix structures, e.g. assign random numbers
- read/write files in JSON http://code.google.com/p/json-simple/wiki/EncodingExamples#Example_1-1_-_Encode_a_JSON_object
- read/write very simple transparent java code. Readability is a must.

IDEAS:
- At any time calculate optimum days to conception (or value of conception by dim)
- for cows past optimum, this would be the expected loss from not getting pregnant at the optimum time
- At any time calculate (best) insemination value by dim (conditional on cow being open, in estrus, and regular P(conc)
- Calculate fertilty premium (= DNB?)
- vary prices of calves or raising heifers to reach a solution where %culled == %calves born  (or >= if culling surplus heifers.  See DCRC 2016.
- Dairy Herd Management September 2016, on mastitis treatment with antibyotic use (=mastitis treatment or dry cow treatment)
    Have DP calculate to treat/not treat mastitis or antibiotic use/not during dry off
    "Cows with severe mastitis and sympoms beyond the udder should be treated immediately"
    "Cows with less severe cases such as abnormal milk or abnormal milk with inflamed udder should not be treated until their medical history is reviewed"
    "Cows that have had previous cases caused by Staph. aureus, mycoplasma M. bovis, prototheca or serration are unlikely to respond to treatment"
    "It is unusual for antibiotic therapy to be effective for cows that have chronic symptoms of mastitis: 
           - 3 or more cases of clinical mastitis during the current lactation
           - > 4 months of somatic cell counts > 200,000 cells/ml.
           ==> in these cases abnormal milk should be discarded until it returns to normal and cows should be watched to detect if the mastitis becomes more severe
  ==> do I need all kinds of states (abnormal milk, inflamed udder, clinical etc.) or could I just say in the "current lactation" that this cow has 
      abnormal milk and 3 months of high SCC) and calculate very specific "current" cash flow for this cow?

IDEAS September 2016
- See cowplan15.cpp comments, especially about meeting a herd constraint in DP, such as tried with kooksiek() in dairyvip30-zxc-DCRC-koolsiek.xlsm. See comments in xlsm 
- Better: per state we make decisions; keep, serve, cull etc: each decision projects forward a cash flow.  Highest cash flow wins the decision.
- Add: per state, have other flows as well, for example, probability of a dairy female calf, milk yield, SCC, phosphorus output, %milking etc.
-   now say we are interested in surplus heifer calves = 0: assign a cash penalty to each decision, multiplied with the addition to the probabiltiy of a dairy female calf of that decision
-   the penalty weight is a constant for all states. If the penalty is high, then decisions that add a big probability of a heifer calf are penalized more and less likely to be chosen by DP
-  (NEW Thought: better a penaly difference? e.g. RPO = KEEP - CULL + penalty.  only if RPO > penalty is the cow kept, otherwise sold. Advantages is that the profit does not need adjustment)
        RPO$ = profit(KEEP) - profit(CULL) + penalty$[sea]*(calves(KEEP) - calves(CULL)).  vary penalty$.  This is a penalized profit function, like Ben-Ari and Gal. See Kristensen (1992)
        RPO$ = profit(KEEP) - profit(CULL) + penalty$[sea]*(cullrate(KEEP) - cullrate(CULL)).  vary penalty$.  Here the goal is to reach an annual cull rate of 35% per year.
        Inseminate = profit(SERV) - profit(DELAY) +  penalty$[sea]*(calves(SERV) - calves(DELAY)). 
        apply the same "penalty$[sea]*(calves(SERV) - calves(DELAY))" to all decision makings in the model: enter heifer, cull, serv, delay, etc.
        penalty$[sea] may be different per season --> need to find 12 penalty$[sea] values that work well.  How?  Design experiment before fitting penalty$[sea] equation?
        https://www.researchgate.net/publication/223749114_Optimal_replacement_policy_for_multicomponent_systems_An_application_to_a_dairy_herd
        try this penalty$ principle with dairyvip30.cpp first because could be built in one day to show principle works ok.

        Kalantari and Cabrera (2012) page 6169: multiply Keep value by a constant to promote longevity (a suboptimal decision)
        It seems this approach adds the Keep value to the real cash flow, so this is not desirable because my method does not add an arbitrary value to the real cash flow
        http://www.ncbi.nlm.nih.gov/pubmed/22921621

-   just like DP calculates the profit of the average cow going through the herd (heifer cash flow), also calculate the other flows with the herd, e.g. dairy calves born
-   if too many dairy calves born (e.g. 0.5 per cow per year, wheras the goal is 0.4 per cow per year), then increase the penalty weight and run DP again.
-   iterate by varying penalty weight such that the surplus = zero eventually.
-   perhaps this approach is approximate dynamic programming?  The question is if this method is correct?  (varying just pcalf in koolsiek() was not correct to find surplus = 0)
-   Is this approximate dynamic programming? See also Reinforcement learning.
-   http://adp.princeton.edu/Papers/Powell%20-%20Perspectives%20of%20approximate%20dynamic%20programming.pdf
-   https://en.wikipedia.org/wiki/Reinforcement_learning 
-   https://webdocs.cs.ualberta.ca/~sutton/book/ebook/the-book.html
-   http://www2.econ.iastate.edu/tesfatsi/RLUsersGuide.ICAC2005.pdf 
- If this works somewhat, then the nice thing is that we still have individual cow decisions (LP in Excel cannot do that), and we have  met a herd constraint
- Have a state that is "empty". Say have 1000 slots. Can DP determine whow many of the slots have cows and how many are empty?

- What if the number of heifers is temporary low, but higher in the next month?  How should that affect the keep/cull decisions?
- What if bulktank SCC is high now, but maybe not next month?  
    Here we need to consider both the current and the longer term flows
Kristensen 1992. Optimal replacement in the dairy herd: A multi-component system. Agricultural Systems, February 1992

- for SQMI project: 
- Add SCC flow: compare ranking of cows with SCC-adjusted RPO with the DHI hotlist.  Use "current" part of DP to model actual cows in herd.  
- Problem: bulktank SCC today, tomorrow, and in the future?  
- Better than the Cornell/Grohn approach which has DP and a little Linear programming? (summer JDS editing)



* Have say 5 different rations that have 5 different $/lbs DM cost.  Davie Dairy has multiple rations and prices varied from $0.11 to $0.14 lbs/DM 

* calculate different RPO, depending on when to cull the cow: 1 week (if RPO is negative), 1 month, keep until end of lactation including calving
   --> this will better rank the negative cows: bigger differences between negative cows incase a replacement is not available

* what about DNB cows?  Are cows ever kept when DNB?  What is the RPO of a DNB cow?


Cabrera: A simple formulation and solution to the replacement problem: A practical tool to assess the economic cow value, the value
of a new pregnancy, and the cost of a pregnancy loss. http://www.journalofdairyscience.org/article/S0022-0302(12)00457-2/pdf
-very similar to v.calculator-Luser.  Does not include DP.
-Each real cow has its own calculation. Can be quite cow specific but he only varies milk yield.  He has monthly steps so quick. 
-Cowplan: DP in current lactation (+ future lactations?) + seasonality + weekly steps + multiple breedings + herd constraint (penalty$)


Compilation and release with Netbeans: DVP30.cpp


    https://en.wikipedia.org/wiki/Memory-mapped_file
    look at Irish paper I reviewed and was so good

 */

package cowplan16package;


//import java.util.ArrayList;

import static cowplan16package.Io.parsefarmdata;
import java.util.ArrayList;
//import java.util.Arrays;
import java.util.Date;
//import java.util.List;
//import java.util.Random;
//import java.util.Scanner;
//import java.util.List;
//import java.lang.Math.*;
//import java.util.concurrent.*;
//import java.util.logging.Level;
//import java.util.logging.Logger;
//import java.io.IOException;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//import java.util.concurrent.locks.Lock;




////////////////////////////////////////////////////////////////////////////
// this is the Koe class in old cowplan: create one object per koe in the herd
// this is the current lactation and varies with herd size
// make an array list with the number of Cow objects in the herd
class Koe {
    static final int maxW = 104+1;
    static final int maxP = 40+1;
    //   static variables belong to the whole class, not per object
    //http://docs.oracle.com/javase/tutorial/java/javaOO/classvars.html
    static int numberOfCows = 0;    //Cow.numberOfCows : number of cows
    int id;
    double ran = Math.random();
    double milk[] = new double[maxW];
    double dmi[] = new double[maxW];
    double fut[][] = new double[maxW][maxP];
    int dim;
    Koe() {    //constructor
        id = ++numberOfCows;
    }
    
    public int getId(){
        return id;
    }
    
    public int getDim(){
        return dim;
    }
    
    public double[] getMilk(){
        return milk;
    }
    
    public double getRan(){
        return ran;
    }
    
}//class Cows


////////////////////////////////////////////////////////////////////////////
class Vehicle {
    int passengers;
    int fuelcap;
    int mpg;
    int array[] = new int[3];
    double rand;
    
    Vehicle() { //constructor --> sets default values for each object of this class
        passengers = 100;
        fuelcap = 110;
        mpg = 111;
        for(int i = 0; i < array.length; i++){
            array[i] = 120;
        }
        rand = Math.random();
        System.out.println("Vehicle constructor");
        System.out.println(passengers);
        System.out.println(fuelcap);
        System.out.println(mpg);
        System.out.println(rand);
        System.out.println(array.length);
        System.out.println();
    }
              
    void range() {
        System.out.println("Range is " + fuelcap * mpg);
    }
    
    void fillarray() {
        array[0] = passengers;
        array[1] = fuelcap;
        array[2] = mpg;
        System.out.println("Fillarray is " + array[0] + " " + array[1] + " " + array[2]);
    }
    
    void fuelneeded(int miles) {
        System.out.println("Fuelneeded is " + (double)miles/mpg);
    }
    
    void randvalue(){
        rand = Math.random();
        System.out.println("Randvalue is " + rand);
    }
    
    void getrand(){
        System.out.println("getrand is " + rand);
    }
  
}//class Vehicle



////////////////////////////////////////////////////////////////////////////
class Futt {
    static int waarde = 88; //"static" --> associated with the class, not instances. they have the same value for all objects in the class
    double array[] = null;  //"static" explanation: http://docs.oracle.com/javase/tutorial/java/javaOO/classvars.html
    double num;
    int id;
    Vehicle veh[] = null;
    
    Futt(int i){ //constructor --> sets default values for each object of this class
        array = new double[i];
        for(int t = 0; t < i; t++){
            array[t] = Math.random();
            System.out.println("Fut: t= " + t + " array= " + array[t]);
        }
        System.out.println("Fut: array.length " + array.length);
        System.out.println("Fut: num " + num + "\n");
        
        
        veh = new Vehicle[4];
        //veh[i].rand = 9;
        for(int t = 0; t < 4; t++){
            veh[t] = new Vehicle();
            veh[t].rand = 0; 
        }
    }//constructor
    
    void getArray() {
        for(int t = 0; t < array.length; t++){
            System.out.println(id + " getArray: t= " + t + " array= " + array[t] + " array.lenght: " + array.length);
        }   
        System.out.println("\n");
    }
}//class Fut







class Open {
    Milk mlk[][][][] = null;
    
    Open(int m1, int m2, int m3, int m4, int b1, int b2, int b3, int r1, int r2, int r3) {
        mlk = new Milk[m1+1][m2+1][m3+1][m4+1];
        for(int i = 0; i <= m1; ++i) {
            for(int j = 0; j <= m2; ++j) {
                for(int k = 0; k <= m3; ++k) {
                    for(int l = 0; l <= m4; ++l) {
                        mlk[i][j][k][l] = new Milk(b1,b2,b3,r1,r2,r3);
                    }
                }
             }
         }
    }
}//Open

class Bred {
    Milk mlk[][][][] = null;
    
    Bred(int m1, int m2, int m3, int m4, int b1, int b2, int b3, int r1, int r2, int r3) {
        mlk = new Milk[m1+1][m2+1][m3+1][m4+1];
        for(int i = 0; i <= m1; ++i) {
            for(int j = 0; j <= m2; ++j) {
                for(int k = 0; k <= m3; ++k) {
                    for(int l = 0; l <= m4; ++l) {
                        mlk[i][j][k][l] = new Milk(b1,b2,b3,r1,r2,r3);
                    }
                }
            }
        }
    }
}//Bred

class Preg {
    Milk mlk[][][][] = null;
    
    Preg(int m1, int m2, int m3, int m4, int b1, int b2, int b3, int r1, int r2, int r3) {
        mlk = new Milk[m1+1][m2+1][m3+1][m4+1];
        for(int i = 0; i <= m1; ++i) {
            for(int j = 0; j <= m2; ++j) {
                for(int k = 0; k <= m3; ++k) {
                    for(int l = 0; l <= m4; ++l) {
                        mlk[i][j][k][l] = new Milk(b1,b2,b3,r1,r2,r3);
                    }
                }
            }
        }
    }
}

class Repr {
    float  rpo;
    //double  milk;
    //double  dmi;     
    //double  array[];
    Repr(){//constructor
        //CowPlan.print("Repr: constructor:" + b1); //things I want to record
        rpo = 5;
        Cowplan16.counter++;
        //milk = Math.random();
        //dmi = -3;
        //array = new double[6];
        //for(int i = 0; i < 6; ++i) {
        //    array[i] = Math.random() + i;
            //CowPlan.print("Milk:" + array[i]);
        //}
        //System.out.println("Milk: array.length " + array.length);
    }
}//Repr


class Body {
    Repr    rep[][][] = null;
    Body(int r1, int r2, int r3){//constructor
        //CowPlan.print("Body: constructor");
        rep = new Repr[r1+1][r2+1][r3+1];
        for(int i = 0; i <= r1; ++i) {
            for(int j = 0; j <= r2; ++j) {
                for(int k = 0; k <= r3; ++k) {
                    rep[i][j][k] = new Repr();
                }
            }
        }
    }
}//Body

class Milk {
    Body    bdy[][][] = null;
    Milk(int b1, int b2, int b3, int r1, int r2, int r3) {//constructor
        //CowPlan.print("Milk: constructor:" + r1);
        bdy = new Body[b1+1][b2+1][b3+1];
        for(int i = 0; i <= b1; ++i) {
            for(int j = 0; j <= b2; ++j) {
                for(int k = 0; k <= b3; ++k) {
                    bdy[i][j][k] = new Body(r1,r2,r3);    
                }
            }
        }
    }//
}//Milk




class D {
    float f;
    double d;
    boolean b;
    int i;  
    long l;
//choose type of variable with the constructor
    D(int ax, int bx, int cx, int dx, int ex, int fx, String gx) {
        U.print("D");
        switch (gx) {
            case "f": f = 0.1f; break;
            case "d": d = Math.random(); break;
            case "b": b = true; break;
            case "i": i = 1; break;
            case "l": l = 1; break; //long
            default: U.println("class D error " + gx); break;
        }
    }
}



class C {
    D d[] = null;
    C(int ax, int bx, int cx, int dx, int ex, int fx, String gx) {
        if(dx >= 0) {
            U.println("C d");
            d = new D[dx+1];
            for (int i = 0; i <= dx; i++){
                d[i] = new D(ax,bx,cx,dx,ex,fx,gx);
            }
        }
    }
}

class B {
    C c[] = null;
    D d[] = null;
    B(int ax, int bx, int cx, int dx, int ex, int fx, String gx) {
        if(cx >= 0) {
            U.println("B c " + cx + dx + ex);
            c = new C[cx+1];
            for (int i = 0; i <= cx; i++){
                c[i] = new C(ax,bx,cx,dx,ex,fx,gx);
            }
        }
        else if(dx >= 0) {
            U.println("B d");
            d = new D[dx+1];
            for (int i = 0; i <= dx; i++){
                d[i] = new D(ax,bx,cx,dx,ex,fx,gx);
            }
        }
    }
}

class A {
    B  b[] = null;
    C  c[] = null;
    D  d[] = null;
    A(int ax, int bx, int cx, int dx, int ex, int fx, String gx) {
        if(bx >= 0) {
            U.println("A b");
            b = new B[bx+1];
            for (int i = 0; i <= bx; i++){
                b[i] = new B(ax,bx,cx,dx,ex,fx,gx);
            }
        }
        else if(cx >= 0) {
            U.println("A c");
            c = new C[cx+1];
            for (int i = 0; i <= cx; i++){
                c[i] = new C(ax,bx,cx,dx,ex,fx,gx);
            }
        }
        else if(dx >= 0) {
            U.println("A d");
            d = new D[dx+1];
            for (int i = 0; i <= dx; i++){
                d[i] = new D(ax,bx,cx,dx,ex,fx,gx);
            }
        }
    }
}

class AA {  //makes classes A, B, C, D etc.
    A  a[] = null;
    B  b[] = null;
    C  c[] = null;
    D  d[] = null;
    AA(int ax, int bx, int cx, int dx, int ex, int fx, String gx) {
        if (ax >= 0) {
            U.println("AA a");
            a = new A[ax+1];
            for (int i = 0; i <= ax; i++){
                a[i] = new A(ax,bx,cx,dx,ex,fx,gx);
            }
        }
        else if(bx >= 0) {
            U.println("AA b");
            b = new B[bx+1];
            for (int i = 0; i <= bx; i++){
                b[i] = new B(ax,bx,cx,dx,ex,fx,gx);
            }
        }
        else if(cx >= 0) {
            U.println("AA c");
            c = new C[cx+1];
            for (int i = 0; i <= cx; i++){
                c[i] = new C(ax,bx,cx,dx,ex,fx,gx);
            }
        }
        else if(dx >= 0) {
            U.println("AA d");
            d = new D[dx+1];
            for (int i = 0; i <= dx; i++){
                d[i] = new D(ax,bx,cx,dx,ex,fx,gx);
            }
        }
    }
}



////////////////////////////////////////////////////////////////////////////
public class Cowplan16 implements Runnable {
    final private int minIndex;         // first index, inclusive
    final private int maxIndex;         // last index, exclusive
    final private double[][] data;
    static int maxI = 100;               // lenght of data[i][]
    static int maxJ = 1000;             // lenght of data[][j]
    static int maxThreads = 5;          // ==> CHANGE this to see effect of multiple threads
    static String space = " ";          // use in System.out.println
    static long teller = 0;
    static long counter = 0;
    static double sum[] = new double[maxI];
            
    public Cowplan16(int minIndex, int maxIndex, double[][] data) {
        U.println("constructor(): Cowplan16: " + minIndex + space + maxIndex);
        this.minIndex = minIndex;
        this.maxIndex = maxIndex;
        this.data = data;
    }//constructor
    
    @Override
    public void run() {
        teller++;   //does not correctly count
        U.println("run() top: " + minIndex + space + maxIndex + " currentThread.Id: " + Thread.currentThread().getId() + " teller:" + teller);
        for(int i = minIndex; i < maxIndex; i++) {
            for(int j = 0; j < maxJ; j++) {
                sum[i] += data[i][j];           //do something interesting ...
                if(data[i][j] >= 0.999) {       //limit amount of printing
                    //U.println("run() Id: " + Thread.currentThread().getId() + " i=" + i + " j=" + j + " data[i][j]=" + data[i][j] + " counter=" + counter);   
                }
            }
            try {                           //add some time to a thread's job
                Thread.sleep(20);          //sleep() needs a try and catch 
            } catch (InterruptedException ex) {
                //Logger.getLogger(MultiArray.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//end: run
    
          
    
    static Koe findId(int id, ArrayList<Koe> herd) {  
        for(Koe k : herd) {  
            if(k.getId() == id)  
                return k;  
        } 
        throw new IllegalStateException("Id: " + id + " is not in the list");  
    }//findID() 

                    

    //**************************************************************************
    public static void main(String[] args) throws InterruptedException {
        U.println("start: main()");
        
        Date start = new Date();
        Date end;
/*        
        int m1 = 0;     //permanent = genetics
        int m2 = 0;     //temporary
        int m3 = 0;     //persistency
        int m4 = 0;     //weeks dry
        
        int b1 = 0;     //body weight
        int b2 = 0;     //body condition score
        int b3 = 0;     //feed efficiency        
        
        int r1 = 0;     //permanent conception rate
        int r2 = 3;     //temporary conception rate (24 hours)
        int r3 = 3;     //calf properties
        
        long counto = 0;
        long countb = 0;
        long countp = 0;
        
        
        int LAC = 5;
        int WWO = 104;  //open
        int WWB = 104;  //bred = open + BRD
        int WWP = 156;  //preg = open + WPR
        int OPT = 3;    //cycle stage
        int BRD = 6;    //breeding options
        int WPR = 40;   //weeks pregnant
        
        U.memory(1);
        Open opn[][][] = new Open[LAC+1][WWO+1][OPT+1];
        U.memory(2);
        Bred brd[][][] = new Bred[LAC+1][WWB+1][BRD+1];
        U.memory(3);
        Preg prg[][][] = new Preg[LAC+1][WWP+1][WPR+1];
        U.memory(4);
        
        
    //opn[].mlk[].bdy[].rep[].rpo      
    
        for(int i = 0; i <= LAC; i++){
            U.println("i= " + i);
            
            
            //memory(9);
            for(int j = 0; j <= WWO; j++){
                for(int k = 0; k <= OPT; k++){
                    counto = counto + 1;
                    opn[i][j][k] = new Open(m1,m2,m3,m4,b1,b2,b3,r1,r2,r3);
                }
            }
            for(int j = 0; j <= WWB; j++){
                for(int k = 0; k <= BRD; k++){
                    countb = countb + 1;
                    brd[i][j][k] = new Bred(m1,m2,m3,m4,b1,b2,b3,r1,r2,r3);
                }
            }
            for(int j = 0; j <= WWP; j++){
                for(int k = 0; k <= WPR; k++){
                    countp = countp + 1;
                    prg[i][j][k] = new Preg(m1,m2,m3,m4,b1,b2,b3,r1,r2,r3);
                }
            }
            U.println("counts: " + counto +space+ countb +space+ countp);
        }
        U.println("counts: " + (counto + countb + countp) + " counter: " + counter);
        
        
        U.println("+++++++++++++++++++++++++++++++++++++");
        
        //memory(2);

        
        for(int i = 0; i <= m1; i++){
            for(int j = 0; j <= m2; j++){
                for(int k = 0; k <= r1; k++){
                    for(int l = 0; l <= b1; l++){
                        for(int m = 0; m <= b1; m++){
                        //    count = count + 1;
                        //    print(count + " rpo " + i + space + j + space + k + space + l + space + m + space + mlk[i][j].rep[k].bdy[l].opn[m].rpo);                            
                        }
                    }
                }
            }
        }
*/
        
        

        
        
        

/* notes:
        //potential problem: too many dimensions make it hard to keep track of which variable is in which dimention, e.g. hrd[wpr][sea] vs hrd[cur][lac]
        //  example: b.pid.[lac][cur][rep][sea][5][6][7]p[8][9] = 9
        //solution: every array has its own object
        //  example: b.pid.l[lac].c[cur].r[rep].s[sea].d[dry].b[bwt].bc[bcs].h[8].i[9] = 9;
        //==> makes it longer, but less room for error
        //string of objects, but some objects are not needed:
        //  example: b.mlk.l[lac].s[sea]    (or make this b.mlk_ls[3,4])
        //therefore, need to make string of objects, but may have to leave objects out
        //code below allows to leave objects out, but objects still need to be in order in the string
        
        hrd.pid.la[1].wi[1].m1[2].m2[2].m3[2].m4[].r1[1].r2[2].r3[0].b1[1].b2[1].f
        e.g. hrd.pid.p[px].w[wx].m4[m4x].f  
            this means that hrd.pid[px][wx][m4x].f  is a float with 3 dimensions: parity, wim, and m4 (= fat for example)
            this is useful because the hrd.variables have various kinds of dimensions, but not all dimensions need to be programmed   
                
                
        fut.opn[p][w][opt].mlk[m1][m2][m3][m4][m5].rep[r1][r2][r3].b[b1][b2].f
           
            in this case, all dimensions need to be programmed. Set some to not be a vector, e.g. m3 = 0 --> .mlk[5][5][0][5][5]
            the m2 is the .m2[] object in the hrd class, and the 2nd vector in the .mlk  class
            I could use the p[1].wi[1].m1[2].m2[2].m3[2].m4[].r1[1].r2[2].r3[0].b1[1].b2[1].f  notation, but this is very long
            the opn[p][w][opt].mlk[m1][m2][m3][m4][m5].rep[r1][r2][r3].b[b1][b2].f notation is a good blend of short and not get lost with the vector position
            
        design:
            the full DP model is useful to create herd policies and statistics, e.g. herd profit of 1 dry period lenght vs. optimal dry period length
            not too many dimensions are needed, however, for example no need to have all kinds of disease classes. curse of dimensionality
            i will use the RPOs from the DP model mostly for illustration. 
            on the other hand, if I can do say 10,000,000 states, surely a real cow fits one of these 10,000,000 states well
            
            the koe[current][future] model is more useful to calculate individual decisions because can be more accurate:
              e.g. individual lactation curves, fat, protein
              e.g. 5 different fertilities (0-24 hours) when waiting say a few hours after a precision signal. 
              e.g. mastitis now or not: treatment(s), do not treat, cull
              e.g. cull when RPO < 0, cull when iofc < 0, etc. --> have multiple values
              
              
            koe[current] with DP, then future lactations can be taken from the DP model or from simulation like v.calculator
            
            [current] cash flow needs to include first week of next parity (calving data)
               this has the calf value, e.g. when breeding with sexed semen in the current parity
               or increased risk of death or involuntary culling
               
            [future] cash flow (>= week2) could be DP if we want optimal decisions in future lactations
                advantage is that DP model is run once, then stores [future] cash flow, --> very quick for individual koe
                problem is that not all permanent (EBV) traits of cows are available in the DP classes
                  but maybe 5 permanent milk classes is enough?
                  also have permanent body size?
                  
                  or have a latent "profit[5]" class in DP?
                  or calculate remaining productive life time per 2nd week for each parity:
                  then assign a $profit per day of remainging life?
                  
                  Easier yet, at the next calving, we value the calf (=f(EBV NM$ dam)) but also the dam herself (=EBV NM$ dam)
                  so say if sexed semen breeding, then 90% heifer calf + +$100 EBV(NM) of calf + P(dystocia/stillbirth) --> value of calf
                  + $50 EBV(NM) of koe --> delta profit of koe in future lactations. This is in addition to the [future] cash flow
                  
                  the [future] cash flow already has genetics of milk, so subtract from $NM?
                  
                  [future] cash flow is useful, because it considers timing of when the next parity starts, parity number, season
        
        koe should also have maybe 3 rpo's, conditional on things PCDARt does not know:
            e.g. if BW = 1100 then RPO = $100, if BW = 1400 then RPO = $300 etc.
            e.g. on BCS, or for 3 mastitis categories: not, high SCC, clinical etc.
            e.g. lameness score
        ==> we need to know the effect of these conditions on the [future] --> map into [future] correctly
        ==> 
            
        also calculate additional statistics like v.Calculator: 
            e.g. wet value, DNB value, PREG value(s) 3 insemination values (x low, medium, high probabilty of conception)
            e.g. time remaining to get pregnant
            e.g. expected time left in the herd (for every RPO state)
            e.g. expected average income per day of remaining life
            e.g. green house foot print
            e.g. fertility premium
            e.g. optimum time of conception + loss of getting pregnant now vs at optimal time of conception (both before and after)
            e.g. optimum time of conception and cash flow that follows from that, vs. cash flow following current state (open, bred, preg)
                for example, if cow is at 300 days still open, vs. if she had gotten pregnant at say 120 DIM.
                = sunk cost = cost already incurred, but maybe an eyeopener
        
        Paper: what is the cost of repro cull: e.g. "5% more repro culls in this TAI program"
            is that just 5% more culling at end of lactation, and associated increase in cull rate cost?
            compare to a cow that got pregnant at the right time?       
*/                  
      
/*
        U.println("1");
                
        AA aap[] = new AA[2+1];        
        U.println("2");
        
                
        aap[0] = new AA(2,2,2,2,2,2,"d");
        U.println("==e4 " + aap[0].a[0].b[0].c[0].d[0].d);
        
       
        aap[1] = new AA(2,-1,2,2,2,2,"d");      //<0 implies do not make
        U.println("==e5 " + aap[1].a[1].c[0].d[0].d);
        
        
        AA pid = new AA(-1,2,1,2,2,2,"f");      //<0 implies do not make
        U.println("==e5 " + pid.b[0].c[0].d[0].f);
        
        aap[2]= new AA(2,-1,2,3,2,2,"b");
        U.println("==e6 " + aap[2].a[0].c[0].d[0].b);
        U.println("==e6 " + aap[2].a[0].c[0].d[1].b);
        U.println("==e6 " + aap[2].a[0].c[0].d[2].b);        

        AA pid1 = new AA(-1,2,-1,2,2,2,"i"); //leave b[] out
        U.println("==e7 " + pid1.b[1].d[0].i);
        
        AA pid2 = new AA(-1,-1,-1,0,2,2,"b");
        U.println("==e8 " + pid2.d[0].b);
        
        //aap = null;
        //pid = null;
        //pid1 = null;
        //pid2 = null;
        U.println("null");
        
        U.println("============================================================");
        
        U.memory(100);
        
        Hrd hh = new Hrd();    //constructor makes all the variable and objects  
        hh.milkX.a[1].b[1].c[1].d[1].f = 0.9f;
        hh.pindX.b[1].c[1].d[1].b = true;
        
        U.println(""+hh.timedry);
        U.println(hh.breed);
        
        U.memory(101);
        
        U.println("############################################################");
        
        U.memory(10);
        
        U.println("free3 :" + U.freememory()/1000000);         
        float test1[][] = new float[20000][30000];
        test1[0][0] = 0;
        U.println("free4 :" + U.freememory()/1000000);            
        
        double free1 = U.freememory()/1000000;

        
        //idea: feed the f[lac] for multithreaded programming
        //but if lac is small, then only #threads = lac.  This is not good.
        //better: multithread by WIM, as long as WIM never points to the same wim[ii];
        
*/        
        //U.stop("Bio starts next, press enter");

        //1. read .csv file with cow records
        //2. use the herd inputs from the front page in Bio() and Herd()
        //3. calculate:
        
        //list of 10 variables from website are in your class

                      
                
        U.memory(4);
        
        Bio b = new Bio();
        Herd h = new Herd(b); 
        Fut f = new Fut();
        
        f.valueiteration(h,f);     //use multithreading if it makes this faster
        
        //4. using multithreading, for each cow in the csv file, calculate a keep value:
        //    - run some code for each cow (not developed yet) + add a value from the frh_fut[][] [][][][] [][] []  matrix  (Fut class)
        
        double keepvalue[] = new double[1200+1];
        for(int cow = 1; cow <= 1200; cow++) {  //<== use the list of cows here and use multithreading
            keepvalue[cow] = f.currentlactation(h,cow) + f.frh_fut[0][0] [0][0][0][0] [0][0] [1];
        }
                
        //5. prepare the output-csv file for the user to download.
        
        
        
        
        
        
/*        
        String records[] = null;
        records = Io.readcsv();
        parsefarmdata(records);
        
        counter = 200000000;
        double free2 = U.freememory()/1000000;
        U.println("free1 :" + free1);       
        U.println("free2 :" + free2);
        U.println("used  :" + (free1-free2) + " " + counter + " " + (free1 - free2)/(double)counter );
        
*/  


        
        
        
//        for(int i = 0; i <= f.lac; ++i) {
//            for(int j = 0; j <= f.wim; ++j) {
//                for(int k = 0; k <= f.cyc; ++k) {                
//                    U.println("" + i + j + k + ":" + f.opn[i][j][k].mlk[0][0][0][0][0].bdy[0][0][0].rep[0][0][0].random);
//                }
//            }
//        }        
        

        //U.memory(11);

/* older Notes: ///////////////////////////////////////////////////////////

dryoff states --> dry-off value, or keepmilking value

dry = weeks dry
mlk = permanent milk 
cur = temporary milk
inv = involuntary culling state (fresh only)
fer = fertility
ins = 3 types of insemination
wim = weeks since calving
wpr = 1 to 40
opn = 1 to 40?  open
chk = 1 to 40 weeks since previous pregcheck --> pregcheck value
wgt = body weight
dmi = feed efficiency
sol = value of fat and protein (milk solids) in milk?


delayed or not delayed replacement

!optimize: per stall (total cows), or per milking slot, or per kg of milk  (see Kristensen)
   per milking slot: keep track of fut(money) and fut(%milking), then optimize fut(money)/fut(%milking)?

current year seperate from future years (like dairyvip)
have seperate/independent revenue for a replacement heifer, not necessary the same as traditional heifer stream
Bayesian updating?
current lactation seperate from future lactations (dpweek)

*/

            
     
//        U.println("====multithreading=======================================");
//        // fill data[][] with random numbers    
//        U.println(" Fill data[][] with random numbers: " + maxI + " " + maxJ);
//        double[][] data = new double[maxI][maxJ];
//        for(int i = 0; i < maxI; i++) {
//            for(int j = 0; j < maxJ; j++) {
//                data[i][j] = Math.random();         
//                //System.out.println("random: " + i + " " + data[i]);
//            }
//        }
//         
//        // test multithreading
//        U.println("Start test multithreading: maxThreads= " + maxThreads + " data.length=" + data.length);        
//        ExecutorService executor = Executors.newFixedThreadPool(maxThreads);
//        int increment = data.length / maxThreads;
//        U.println("increment: " + increment);
//        for(int i = 0; i < maxThreads; i++) {
//            //new Thread(new MultiArray(i * increment, (i + 1) * increment, data)).start();
//            executor.execute(new Cowplan16(i * increment, (i + 1) * increment, data));
//            //System.out.println(" currentthreadid: " + Thread.currentThread().getId());
//        }
//        executor.shutdown();                        //page 985 book: Introduction to Java Programming eighth edition;
//        while(executor.isTerminated() == false) {   //wait until all tasks are finished
//        }
//        U.println("End test multithreading");
 
        
        
        
 /* blocked to play with multithreading
        
        Fut f1 = new Fut(5);
        Fut f2 = new Fut(3);      
        //f1.name.toString(f1);
        //f2.name = "f2";
        f1.getArray();
        f2.getArray();
        
        Fut ft[] = new Fut[4];
        System.out.println("ft.lenght: " + ft.length);
        for(int i = 0; i < ft.length; i++) {
            ft[i] = new Fut(i+1);
            ft[i].id = i;
            ft[i].getArray();
            ft[i].veh[0].mpg = 9;
            System.out.println(ft[i].array[0]);
            System.out.println("yes!" + ft[i].veh[0].mpg);
        }
        System.out.println("yes!" + ft[1].veh[0].mpg);
        System.out.println(Fut.waarde); 
         
     
        ArrayList<Cows> herd = new ArrayList<>();
        for(int i = 0; i < 4; i++) {
            Cows cow = new Cows();
            herd.add(cow);
            System.out.println(" Cow id: " + cow.id);
        }
                
        for (Cows h : herd) {
            System.out.print(" Cow id: " + h.getId());
            System.out.print(" random: " + h.ran + " milk:");
            System.out.println(" " + Arrays.toString(h.getMilk()));
            
            //find object with Id = 4
            Cows p = findId(4, herd);
            System.out.println("findId: " + p.getId() + " " + p.getRan());
                                     
            //System.out.println(" herd.get(3) " + herd.get(3).getId());
        }
        
        //find an object by its id in the list, then change a property
        Cows p = findId(4, herd);
        System.out.println("findId: " + p.getId() + " " + p.getRan());
        p.ran = Math.random();
        System.out.println("findId: " + p.getId() + " " + p.getRan());
             
        
//      work in progress        
//        D1 rpo[][][] = new D1[5][6][10];
//        for(int i = 0; i < rpo.length; i++) {
//            ft[i] = new Fut(i+1);
     
        
//                
//        Vehicle fort = new Vehicle();
//        Vehicle audi = new Vehicle();
//        Vehicle daf = new Vehicle();
//        fort.range();
//        audi.range();
//        fort.fillarray();
//        audi.fillarray();
//        fort.getrand();
//        audi.getrand();
//        
//        System.out.println("\n Ford:");
//        
//        fort.fuelcap = 6;
//        fort.mpg = 2;
//        fort.passengers = 7;
//        fort.range();
//        fort.fillarray();
//        fort.randvalue();
//        
//        System.out.println("\n Audi:");
//        
//        audi.fuelcap = 5;
//        audi.mpg = 3;
//        audi.passengers = 4;
//        audi.range();
//        audi.fillarray();
//        audi.fuelneeded(133);
//        audi.randvalue();
       
        //test the Shortcut to system.out.println
        System.out.println("s findId: " + p.getId() + " " + p.getRan());
                         p("p findId: " + p.getId() + " " + p.getRan());
           
        System.out.println("at the bottom of main()");
        rpomatrix();
        
        Date end = new Date(); 
	double difference = end.getTime() - start.getTime();    // mili seconds
	System.out.println("\nTime taken: " + difference/1000 + " seconds.");
        
*/  
/* 
        domultithread();  
*/        

        U.memory(0);
        end = new Date();
        U.println("");
        U.println("start: " + start);
        U.println("  end: " + end);
                
        U.println("end: main()"); 
    }//end: main()

    
    
    
    public static final int THREADS =  Runtime.getRuntime().availableProcessors();
    //**************************************************************************
    static void domultithread() throws InterruptedException {
        U.println(" Start domultithread ============ THREADS = " + THREADS);
        // fill data[][] with random numbers    
        
          
        
        U.print(" Fill data2[][] with random numbers: " + maxI + "*" + maxJ);
        double[][] data2 = new double[maxI][maxJ];
        double som = 0;
        for(int i = 0; i < maxI; i++) {
            for(int j = 0; j < maxJ; j++) {
                data2[i][j] = Math.random();         
                //System.out.println("random: " + i + " " + data[i]);
                som += data2[i][j];
            }
        }
        U.println( " som1:" + som);
         
        // test multithreading
        //Idea is to split up the first array into pieces and let each thread execute that piece of the array
        U.println(" Start test multithreading: maxThreads= " + maxThreads + " data.length=" + data2.length);        
        //ExecutorService executor = Executors.newFixedThreadPool(maxThreads);
        int increment = data2.length / maxThreads;
        U.println(" increment: " + increment);
        
        int threadcnt = 0;
        Thread[] threads = new Thread[maxThreads+1];
        for(int i = 0; i < maxThreads; i++) {
            threads[i] = new Thread(new Cowplan16(i*increment, (i+1)*increment, data2));
            threads[i].start();
            threadcnt++;
            //executor.execute(new Cowplan16(i*increment, (i+1)*increment, data2));     //do stuff with data[][]
        }
        //add the final tread if there is a remainder
        if ((maxThreads)*increment < maxI) {
            threads[maxThreads] = new Thread(new Cowplan16((maxThreads)*increment, maxI, data2));
            threads[maxThreads].start(); 
            threadcnt++;
        }
        U.println(" threadcnt:" + threadcnt);
        for(int i = 0; i < threadcnt; i++) {
            threads[i].join();
        }
        
        //U.println(" executor.shutdown");
        //executor.shutdown();                        //page 985 book: Introduction to Java Programming eighth edition;
        //while(executor.isTerminated() == false) {   //wait until all tasks are finished
        //}
        
        U. println(" som1:" + som);        
        som = 0;
        for(int i = 0; i < maxI; i++) {
            som += sum[i];
        }
        U. println(" som2:" + som + " difference is double accuracy (15 digits)");
        
        
        U.println(" End domultithread =======================================");
    }//end: domultithread()
    
    
}//class Cowplan16






