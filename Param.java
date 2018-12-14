/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cowplan16package;

/**
 *
 * @author devries
 */
public class Param {
    static String space = " ";  
    final static int X8 = 99999999;                                             //maximum number
    final static int X4 = 9999;         //maximum number
    final static float STAGE = 7.023f;  //length of one stage in days (365.2 / 52)
    final static float BIG = 0.01f;
    final static double PI = 3.14159265358979;                                //constant
    
    //delete the M so it becomes WIM etc.
    static int MWIM = 144;//maximum weeks in a lactation
    static int MWFR = 7;//maximum weeks fresh
    static int MLAC = 5;//maximum lactations
    
    static int MVAL = 7;//maximum milk value levels
    static int MPER = 0;//maximum lactation curve persistency levels
    static int MBWT = 0;//levels of bodyweight
    static int MDMI = 0;//levels dry matter intake
    
    static int MFER = 0;//levels of fertility
    static int MCUL = 0;//levels of involuntary culling
    static int MCYC = 3;//estrus cycle stage
    static int MWPR = 40;//maximum weeks pregnant: 1-40  (0 = open)
    static int MDRY = 0;//levels of weeks dry   
    static int MINS = 0;//calf value = insemination decisions
    
    static int MSEA = 0;//maximum seasonality classes (0 or 52
    

    static int LBW = MWIM - MWPR;//last breeding week  
     
    static int AWIM = 0, ALAC = 0, AVAL = 0, APER = 0, ABWT = 0, ADMI = 0, AFER = 0, ACUL = 0, AINS = 0;
                                                              
//wim,wfr,lac, val,per,bwt,dmi, fer,cul,cyc,wpr,dry,ins, sea 
    
    
    //**************************************************************************
    private byte ok(int order, int val, int min, int max) {
        if(val >= min && val <= max) return 0;
        else { 
            U.println("Param.ok() error:" + order);
            return 1;
        }
    }//end: ok
    
    
    //**************************************************************************
    private void check_max(){
        byte errors = 0;
               
        errors += ok(1,MWIM,52,201);
        errors += ok(2,MWFR,1,7);
        errors += ok(3,MLAC,1,15);
        errors += ok(4,MVAL,0,15);
        errors += ok(5,MPER,0,6);
        errors += ok(6,MBWT,0,6);
        errors += ok(7,MDMI,0,6);
        errors += ok(8,MSEA,0,52);
        errors += ok(9,MFER,0,6);
        errors += ok(10,MCUL,0,6);
        errors += ok(11,MCYC,3,6);
        errors += ok(12,MWPR,40,40);
        errors += ok(13,MDRY,0,12);       
        errors += ok(14,MINS,0,6);

        U.println("Param.check_max() errors: " + errors); 
        if(errors > 0) {
            U.stop("1");
            System.exit(1);
        }
    }//end check_max
            
    //==========================================================================
    Param() {
        check_max();       
        if(MWIM > 0) AWIM = MWIM/2;
        if(MLAC > 0) ALAC = MLAC/2;
        if(MVAL > 0) AVAL = MVAL/2;
        if(MPER > 0) APER = MPER/2;
        if(ABWT > 0) ABWT = MBWT/2;
        if(AFER > 0) AFER = MFER/2;
        if(ACUL > 0) ACUL = MCUL/2;
        if(AINS > 0) AINS = MINS/2;

    }//constructor
}//class
