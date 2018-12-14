
package cowplan16package;


//import static cowplan16package.Param.MWIM;

/*
https://en.engormix.com/MA-dairy-cattle/videos/an-app-that-predicts-cure-rate-clinical-mastitis-scott-mcdougall-t47749.htm
can I make simply say 5 states on the type of action with clinical mastitis and then cure rate?
easy to have current lactation, with clinical mastitis or not, and then type of treatments to choose from
but still need mastitis states in future lactations to model repeatability and carry over effects
or: break down any disease in factors: loss in milk, fertility, culling, treatment cost etc. 
*/


class Bio extends Param {
    static String space = " ";          // use in System.out.println
    
    float abort[] = new float[MWPR+1]; 
    float bw_open[][][] = new float[MWIM+1][MLAC+1][MBWT+1]; 
    float bw_preg[] = new float[MWPR+1];
//    String breed = "GE";
//    float bw[][] = new float[MWIM+1][MLAC+1];
//    float bwrepeatability;
//    float calved[] = new float[T+2];
//    short calc_cow = 0;
    short calc_ful = 1;
    short code[] = new short[999+1];
    float concfer[] = new float[MFER+1];
    float concwim[] = new float[MWIM+1];
    float conclac[] = new float[MLAC+1];
    float concins[] = new float[MINS+1];  //fertility of the male
//    short continuelac = 1;
//    float cullmargin = -999;
    float culliofc = 1.0f;
    float cullmilk = 5.0f;    
    String cullpolicy = "decide";    //decide, keepall, keepreg
    float cullrrpreg = 0.5f;
    float deadrrpreg = 0.5f;
//    short delayed = 0;
    float dmi_dry = 25.0f;
    //float dmi_maintenance = 25.0;
    //float dmi_production = 2.0;
    float dryoffyield = 0.0f;
    String enterheifer[] = new String[MSEA*2+1];
//    float erpa = 0.0f;
//    short errorcheck = 0;
    float fatpct = 0.035f;
//    float fresh[][][][][][] = new float[LAC+1][CUR+1][BWT+1][REP+1][INV+1][T+2];
    short funk = 1;
    float geneticprogress = 19.73f / 52f;
//    float heifer[] = new float[T+2];	
//    long herdcode = 58010029;
    float interest = 0.08f;
    float invcullpct = 0.85f;   //faction of the healthy cull price
    float invlevel[] = new float[MCUL+1];
    float m305[][][][] = new float[MVAL][MPER][MLAC+1][MSEA+1];
//    short maxid = 0;
//    float mefat = 853;
//    float memilk = 25363;
//    float memilk_adjustment = 1.0f;
//    float meprotein = 661;
    short metric = 0;   //0 = lbs, 1 = kg
//    float milkcr[] = new float[MVAL+1];
    float milksr[] = new float[MVAL+1];
    int minvwp[] = new int[MLAC+1];
    int maxlbw[] = new int[MLAC+1];
    int maxwim[] = new int[MLAC+1];
    int maxdry[] = new int[MLAC+1];
    int mindry[] = new int[MLAC+1];
 //   short mkv_herd = 0;   
    float mlk_multiplier[] = new float[MLAC+1];
    float mlk_peakyield[] = new float[MLAC+1];
    int mlk_peakdim[] = new int[MLAC+1];
    float mlk_persistency[] = new float[MLAC+1];
    float mlk_preg[] = new float[MWPR+1];
//    float mlk_slope[] = new float[MLAC+1];
//    float mlkpct[] = new float[MLAC+1];
    //float mlk_dopn[] = new float[LBW+1];
    float mlk_funk[] = new float[LBW+1]; 
    float month_conc[] = new float[12+1];
    float month_cull[] = new float[12+1];
    float month_milk[] = new float[12+1];
    float month_serv[] = new float[12+1];
    float month_dead[] = new float[12+1];
    float mrepeat = 0.60f;
//    short optimizedryoff = 0;
    float p_breeding[] = new float[MINS+1];
    float p_calf[] = new float[MINS+1];
    float p_feeddry[] = new float[MSEA*2+1];
    float p_feedwet[] = new float[MSEA*2+1];
    float p_fixed_labor = 1.50f;
    float p_fixed_other = 1.00f;
    float p_heifer[] = new float[MSEA*2+1];
    float p_labor = 10.00f;
    float p_cullloss = 50.0f;
    float p_deadloss = 100.0f;
    float p_milk[] = new float[MSEA*2+1];
    float p_other_dry = 0.20f;
    float p_other_lact = 0.30f;
    float p_sell[] = new float[MSEA*2+1];
    float p_tmrdmdry = 5.00f;
    float p_tmrdmlac = 13.0f;
    float pa_cv = 12.0f;
    float pcull[] = new float[MWIM+1];
    float pcull_odds[] = new float[MLAC+1];    
    float pdead[] = new float[MWIM+1];
    float pdead_odds[] = new float[MLAC+1];
//    float present[][] = new float[3+1][MAXM+1];
//    float probpreg = 0.4f;
//    float proteinpct = 0.032f;
//    float replevel[] = new float[MFER+1];
//    float repreg = 0.1f;
    float reprocost = 0.0f;
//    short res[] = new short[6+1];
    //boolean seasonality = false;
    String serve[] = new String[MSEA*2+1];
    float servrate[] = new float[LBW+1];
//    short showheat = 1;
    float smy[] = new float[MVAL+1];
    short startsea = 1;
//    short sureb[] = new short[MLAC+1];
    float timebreeding = 10;   //minutes
    float timedry = 30;        //minutes/calving
    float timefresh = 7;       //minutes/day
    float timelact = 3.0f;      //minutes/day
    float veterinarycost[] = new float[MWIM+1];
//    short weeksdry[] = new short[7+1];
    float yum[] = new float[MVAL+1];    
//    float zsave[] = new float[20+1];	


    
    //**************************************************************************
    private void default_bio_arrays() {
        U.println("DEFAULT_BIO_ARRAYS started ...");
        int wim, wfr, lac, val, per, bwt, dmi, fer, cul, cyc, wpr, dry, ins, sea;
        float div;        
        

        for(wpr = 0; wpr <= MWPR; wpr++) {
            abort[wpr] = (float)0.10/MWPR;
        }
        for(wim = 1; wim <= MWIM; wim++) {
            for(lac = 1; lac <= MLAC; lac++) {
                for(bwt = 0; bwt <= MBWT; bwt++) {   
                    bw_open[wim][lac][bwt] = 1200 + (bwt - MBWT/2) * 100;
                    
                    //U.println("wim lac bwt bw_open: " +wim+ space +lac+ space + bwt + space + bw_open[wim][lac][bwt]);
                }
            }
        }
        //U.wait(3,"bw_open");
        for(wpr = 0; wpr <= MWPR; wpr++) {
            if(wpr < 15) bw_preg[wpr] = 0;
            else bw_preg[wpr] = (wpr-15);
            
            //U.println("wpr bw_preg:" + wpr +space+ bw_preg[wpr]);
        } 
//            0,
//            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 2, 3, 4, 5, 7, 9,
//            12, 15, 18, 22, 26, 31, 36, 42, 48, 55, 63, 71, 81, 91, 101, 113, 125, 138, 152, 168 };
        //U.wait(3,"bw_preg");         

        
        for(lac = 0; lac <= MLAC; lac++) {
            minvwp[lac] = MWFR + 1;
            maxlbw[lac] = LBW;
            maxwim[lac] = MWIM;
            
            maxdry[lac] = MDRY;
            mindry[lac] = (short)U.mini(MDRY,8);
            U.println("lac:"+lac+space+mindry[lac]+space+maxdry[lac]);
        }
        //U.wait(1,"min & max dry");       
        
        for(wpr = 0; wpr <= MWPR; wpr++) {
            if (wpr <= 16) mlk_preg[wpr] = 1;
            else mlk_preg[wpr] = (float)(1 - (wpr-16)*0.01);
           
            //U.println("wpr mlk_preg:" + wpr +space+ mlk_preg[wpr]);
        }
//        float mlk_preg[] = {//WPR+1
//            1.00,
//            1.00, 1.00, 1.00, 1.00, 1.00, 1.00, 1.00, 1.00, 1.00, 1.00,
//            1.00, 1.00, 1.00, 1.00, 1.00, 1.00,	0.99, 0.98, 0.97, 0.96,
//            0.95, 0.94, 0.93, 0.92, 0.91, 0.90, 0.89, 0.88, 0.87, 0.86, 
//            0.85, 0.84, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00 };
        //U.stop("mlk_preg"); 

        for(wim = 0; wim <= LBW; wim++) {  
            mlk_funk[wim] = 1;
        }      
//        float mlk_funk[] = {//LBW+1
//            0,
//            0.9564, 0.9601, 0.9637, 0.9673, 0.9708, 0.9742, 0.9776, 0.9808, 
//            0.9840, 0.9872, 0.9902, 0.9932, 0.9961, 0.9990, 1.0018, 1.0045,
//            1.0071, 1.0097, 1.0122, 1.0146, 1.0169, 1.0192, 1.0214, 1.0236,
//            1.0256, 1.0276, 1.0296, 1.0314, 1.0332, 1.0349, 1.0366, 1.0382,
//            1.0397, 1.0411, 1.0424, 1.0437, 1.0450, 1.0461, 1.0472, 1.0482,
//            1.0491, 1.0500, 1.0508, 1.0515, 1.0522, 1.0527, 1.0533, 1.0537,
//            1.0541, 1.0544, 1.0546, 1.0547, 1.0548, 1.0549, 1.0548, 1.0547,
//            1.0545, 1.0542, 1.0539, 1.0535, 1.0530, 1.0524, 1.0518, 1.0511, 
//            1.0503, 1.0495, 1.0486, 1.0476, 1.0466, 1.0455, 1.0443, 1.0430,
//            1.0417, 1.0403, 1.0388, 1.0373, 1.0356, 1.0340, 1.0322, 1.0304,
//            1.0285, 1.0265, 1.0245, 1.0224, 1.0202, 1.0179, 1.0156, 1.0132,
//            1.0107, 1.0082, 1.0056, 1.0029, 1.0002, 0.9974, 0.9945, 0.9915,
//            0.9885, 0.9854, 0.9822, 0.9789, 0.9756, 0.9722, 0.9688, 0.9653 };//see REPEATABILITY-CURVE.XLS

        if (MVAL == 0) smy[0] = 100;
        else {
            for(val = 0; val <= MVAL; val++) {
                smy[val] = (float)(100 + (val - MVAL/2) * 9.438884);
            
                //U.println("smy: val smy" + val +space+ smy[val]);
            }
        }
        //U.stop("smy"); 
//        float smy[] = {//CUR+1
//            0,
//            71.589343, 81.097496, 90.561116, 100.000000, 109.438884, 118.902504, 128.410657};


        if (MVAL == 0) yum[0] = 100;
        else {
            for(val = 0; val <= MVAL; val++) {
                if (val == MVAL/2) yum[val] = 105;
                if (val < MVAL/2) yum[val] = 104 - (MVAL/2 - val) * 8;
                if (val > MVAL/2) yum[val] = 105 + (val - MVAL/2) * 8;

                //U.println("yum: val yum" + val +space+ yum[val]);                
            }   
        }
        //U.wait(2,"yum"); 
//        float yum[] = {//CUR+1
//            0,
//            80, 88, 96, 105, 115, 125, 135};         
            
        

        for(int i = 0; i <= 999; i++) code[i] = 0;
        for(fer = 0; fer <= MFER; fer++) concfer[fer] = 1.0f;
        for(wim = 1; wim <= MWIM; wim++) concwim[wim] = 0.35f;
        for(lac = 0; lac <= MLAC; lac++) conclac[lac] = 1.0f;
        for(ins = 0; ins <= MINS; ins++) concins[ins] = 1.0f;
        
        
        
        
        for(sea = 0; sea <= MSEA*2; sea++) enterheifer[sea] = "decide";    //yes, no, decide
//        invlevel[0] = 1.0; invlevel[1] = 0.5; invlevel[2] = 1.5;
    
        for(lac = 1; lac <= MLAC; lac++) maxlbw[lac] = LBW;
        
//        for(val = 0; val <= MVAL; val++) milkcr[val] = 1.0;
//        for(val = 0; val <= MVAL; val++) milksr[val] = 1.0;
        minvwp[0] = 0; 
        for(lac = 1; lac <= MLAC; lac++) minvwp[lac] = MWFR;
    
//        mlk[0][0] = mlk[1][0] = mlk[2][0] = mlk[3][0] = 0.0;
//        for(lac = 1; lac <= MWIM; lac++) mlk[0][lac] = 0.0;
//        for(lac = 1; lac <= MWIM; lac++) mlk[1][lac] = wood((int) ((float)(lac-1)*STAGE+4),wooda[1],woodb[1],woodc[1]);	//=mlk1[i];
//        for(lac = 1; lac <= MWIM; lac++) mlk[2][lac] = wood((int) ((float)(lac-1)*STAGE+4),wooda[2],woodb[2],woodc[2]);	//=mlk2[i];
//        for(lac = 3; lac <= MLAC; lac++) for(wim = 1; wim <= MWIM; wim++) mlk[lac][wim] = wood((int)((float)(lac-1)*STAGE+4),wooda[3],woodb[3],woodc[3]); //=mlk3[i];

//        mlk_slope[0] = 0.0;
//        mlk_slope[1] = (mlk[1][52] - mlk[1][MWIM]) / (MWIM-52);	//slope to extend curve past WIM
//        mlk_slope[2] = (mlk[2][52] - mlk[2][MWIM]) / (MWIM-52);
//        for(lac = 3; lac <= MLAC; lac++) mlk_slope[lac] = (mlk[3][52] - mlk[3][MWIM]) / (MWIM-52);	//hrd->mlkpct[lac] == 1;
//        mlk_dopn[0] = 0.70; for(wim = 1; wim <= LBW; wim++) mlk_dopn[wim] = mlk_dopn[wim-1] + 0.02;
        
        for(lac = 0; lac <= MLAC; lac++) {
            mlk_peakyield[lac] = 91.9f;
            mlk_peakdim[lac] = 58;
            mlk_persistency[lac] = 0.0635f;
            mlk_multiplier[lac] = 1.0f;
        }
        
//        for(lac = 0; lac <= MLAC; lac++) mlkpct[lac] = 1.0;
        for(int i = 0; i <= 12; i++) month_conc[i] = 1.0f;
        for(int i = 0; i <= 12; i++) month_cull[i] = 1.0f;
        for(int i = 0; i <= 12; i++) month_milk[i] = 1.0f;
        for(int i = 0; i <= 12; i++) month_serv[i] = 1.0f;
        
//        for(lac = 0; lac <= MLAC; lac++) odds[lac] = 1.0;
        for(sea = 0; sea <= MSEA*2; sea++) p_feeddry[sea] = 5.0f;
        for(sea = 0; sea <= MSEA*2; sea++) p_feedwet[sea] = 11.0f;
        for(sea = 0; sea <= MSEA*2; sea++) p_heifer[sea] = 1800.0f;
        for(sea = 0; sea <= MSEA*2; sea++) p_milk[sea] = 20.0f;
        for(sea = 0; sea <= MSEA*2; sea++) p_sell[sea] = 80.0f;
        
        for(ins = 0; ins <= MINS; ins++) {
            p_breeding[ins] = 10.00f;
            p_calf[ins] = 200.00f;
        }
        
//        pid[0] = 0;
//        pid[1] = 0.0062;
//        pid[2] = 0.0055;
//        pid[3] = 0.0050;
//        pid[4] = 0.0045;
//        pid[5] = 0.0040;
//        pid[6] = 0.0038;
//        pid[7] = 0.0035;
//        pid[8] = 0.0033;
//        pid[9] = 0.0029;
//        pid[10] = 0.0028;
//        pid[11] = 0.0026;
//        pid[12] = 0.0025;
//        pid[13] = 0.0024;
//        pid[14] = 0.0023;
        
        
        for(wim = 0; wim <= MWIM; wim++) pcull[wim] = 0.0022f;
        for(wim = 0; wim <= MWIM; wim++) pdead[wim] = 0.0001f;
//        replevel[0] = 1.0; replevel[1] = 0.8; replevel[2] = 1.2;
//        for(int i = 0; i <= 6; i++) res[i] = 0; res[0] = 1;    
        for(sea = 0; sea <= MSEA*2; sea++) serve[sea] = "decide";               //yes, no, decide
        for(wim = 0; wim <= LBW; wim++) servrate[wim] = 0.35f;
//        for(lac = 0; lac <= MLAC; lac++) sureb[lac] = 0;
        

        veterinarycost[0] = 0.0f;
        veterinarycost[1] = 39.0f;		//$/week (1st week after calving)
        veterinarycost[2] = 23.0f;		//$/week (2nd week after calving)
        veterinarycost[3] = 8.0f;		//$/week (3rd week after calving)
        for(wim = 4; wim <= MWIM; wim++) veterinarycost[wim] = 1.0f;
        
//        weeksdry[0] = 0;
//        weeksdry[1] = 9;	//lactation 1
//        weeksdry[2] = 7;	//lactation 2
//        weeksdry[3] = 7;	//lactation 3
//        weeksdry[4] = 7;	//lactation 4+
      
                                                            //peak, peakdim, 305m, %
//        wooda[1] = 12.0; woodb[1] = 0.310; woodc[1] = 2.8;  //	83, 109, 23022,  91%
//        wooda[2] = 17.8; woodb[2] = 0.255; woodc[2] = 3.4;  //	91,  74, 24048,  95% 
//        wooda[3] = 19.5; woodb[3] = 0.250; woodc[3] = 3.5;  //  97,  74, 25363, 100%
        
        
        
//        for(t = 0; t <= T+1; t++) heifer[t] = 0.0;
//        
//        for(i = 0; i <= LAC; i++) 
//            for(j = 0; j <= CUR; j++)
//                for(k = 0; k <= BWT; k++)
//                    for(int l = 0; l <= REP; l++)
//                            for(int m = 0; m <= INV; m++)
//                                    for(t = 0; t <= T+1; t++) {  
//                                        fresh[i][j][k][l][m][t] = 0.0;	//reset in: dp_fut()
//                                    }
//        for(i = 0; i <= LAC; i++)
//            for(j = 0; j <= CUR; j++) 
//                for(t = 0; t <= SEA; t++) {
//                    m305[i][j][t] = 0.0;	//reset in: hrd_m305()
//                }
//        
//        for(i = 0; i <= 20; i++) zsave[i] = 0.0;      
        //reset_hrd_record2();
 
        U.println("DEFAULT_BIO_ARRAYS done ...");
    }//end: default_bio_arrays 
    
   
    
    
    //**************************************************************************
    //CONSTRUCTOR
    Bio() {
        U.println("Bio constructor started ...");
        default_bio_arrays();          
        //read_hrd_data() READS HRD DATA FROM FILE: overwrite defaults
                 
        U.println("Bio constructor ended ...");
    }//end: Bio constructor  
  
}//end: Bio class

    

