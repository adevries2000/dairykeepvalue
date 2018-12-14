
package cowplan16package;

import static cowplan16package.U.df;

/*

wim = weeks since calving (52 to 208) 144
lac = lactation (1 to 15) 7

sol = level value of milk (0 to 14)
per = persistency value of milk (0 to 2)
bwt = body weight (0 to 5)
dmi = dry matter intake (0 to 5)
sea = season (0 to 52)

fer = fertility (0 to 2)
inv = involuntary culling state (fresh only) (0 to 2)
cyc = 0 to 6 (same as wpr, only open, weeks since breeding opportunity => opn = 0
wpr = 1 to 40 (not variable)
clv = types of insemination (0 to 5)
dry = weeks dry (last weeks of pregnancy) (0 to 14)

(range) default  ==> avg is half, say maxsol = 6 -> avgsol = maxsol/2 = 3  (0, 1, 2) 3 (4, 5, 6)

*/




public class Fut extends Param {

    //Param order: wim,wfr,lac, val,per,bwt,dmi, fer,cul,cyc,wpr,dry,ins, sea 
        
    float frh_fut[][] [][][][] [][] [] = null;     //[wim][lac] [val][per][bwt][dmi] [fer][cul] [ii]
    short frh_rpo[][] [][][][] [][] [] = null;
    byte  frh_pol[][] [][][][] [][] [] = null;
    
    float opn_fut[][] [][][][] [][] [] = null;      //[wim][lac] [val][per][bwt][dmi] [fer][cyc] [ii]
    short opn_rpo[][] [][][][] [][] [] = null;   
    byte  opn_pol[][] [][][][] [][] [] = null;
    
    float dnb_fut [][] [][][][] [][] [] = null;       //[wim][lac] [val][per][bwt][dmi] [fer][ins] [ii]
    float dnbvalue[][] [][][][] [][] [] = null;       //[wim][lac] [val][per][bwt][dmi] [fer][ins] [sea]     
    float insvalue[][] [][][][] [][] [] = null;     //[wim][lac] [val][per][bwt][dmi] [fer][ins] [sea]
    float pr1value[][] [][][][] [][] [] = null;     //[wim][lac] [val][per][bwt][dmi] [fer][ins] [sea]
    //+ delayed pregnancy value
    
    float prg_fut[][] [][][][] [][] [] = null;     //[wim][lac] [val][per][bwt][dmi] [wpr-dry][ins] [ii]
    short prg_rpo[][] [][][][] [][] [] = null;
    byte  prg_pol[][] [][][][] [][] [] = null;     
    
    float dry_fut[][] [][][][] [][][] [] = null;   //[wim][lac] [val][per][bwt][dmi] [wpr-dry][dry][ins] [ii]
    short dry_rpo[][] [][][][] [][][] [] = null;   
    byte  dry_pol[][] [][][][] [][][] [] = null;   
    
    boolean isdry[][] [][][][] [][] [] = null;     

    float enter[] = new float[2+1];
    byte pol_enter[] = new byte[2*MSEA+1];
    float cash[] = new float[104+1];
    float heifer[] = new float[MWPR+1];
   
    
    //**************************************************************************
    private double mem(int a) {
        double d = U.freememory();
        
        U.println(a + " " +d);
        return d;
    }
    
    //**************************************************************************
    private void set_fut_arrays(){
        U.println("FUT: set_fut_arrays started ...");
        double mm = mem(0);
        try {
            mem(1); frh_fut = new float[MWFR+1][MLAC+1+1] [MVAL+1][MPER+1][MBWT+1][MDMI+1] [MFER+1][MCUL+1] [2+1];
            mem(2); frh_rpo = new short[MWFR+1][MLAC+1+1] [MVAL+1][MPER+1][MBWT+1][MDMI+1] [MFER+1][MCUL+1] [MSEA*2+1];
            mem(3); frh_pol = new byte [MWFR+1][MLAC+1+1] [MVAL+1][MPER+1][MBWT+1][MDMI+1] [MFER+1][MCUL+1] [MSEA*2+1];
            
            mem(4); opn_fut = new float[MWIM+1][MLAC+1] [MVAL+1][MPER+1][MBWT+1][MDMI+1] [MFER+1][MCYC+1] [2+1];
            mem(5); opn_rpo = new short[MWIM+1][MLAC+1] [MVAL+1][MPER+1][MBWT+1][MDMI+1] [MFER+1][MCYC+1] [MSEA*2+1];
            mem(6); opn_pol = new byte [MWIM+1][MLAC+1] [MVAL+1][MPER+1][MBWT+1][MDMI+1] [MFER+1][MCYC+1] [MSEA*2+1];
                       
            mem(7); prg_fut = new float[MWIM+1][MLAC+1] [MVAL+1][MPER+1][MBWT+1][MDMI+1] [MWPR+1][MINS+1] [2+1];  //wpr before dry length decisions
            mem(8); prg_rpo = new short[MWIM+1][MLAC+1] [MVAL+1][MPER+1][MBWT+1][MDMI+1] [MWPR+1][MINS+1] [MSEA*2+1];  //wpr before dry length decisions
            mem(9); prg_pol = new byte [MWIM+1][MLAC+1] [MVAL+1][MPER+1][MBWT+1][MDMI+1] [MWPR+1][MINS+1] [MSEA*2+1];  //wpr before dry length decisions
            
            mem(10); dry_fut = new float[MWIM+1][MLAC+1] [MVAL+1][MPER+1][MBWT+1][MDMI+1] [MDRY+1][MDRY+1][MINS+1] [2+1]; //wpr with dry length decisions
            mem(11); dry_rpo = new short[MWIM+1][MLAC+1] [MVAL+1][MPER+1][MBWT+1][MDMI+1] [MDRY+1][MDRY+1][MINS+1] [MSEA*2+1]; //wpr with dry length decisions
            mem(12); dry_pol = new byte [MWIM+1][MLAC+1] [MVAL+1][MPER+1][MBWT+1][MDMI+1] [MDRY+1][MDRY+1][MINS+1] [MSEA*2+1]; //wpr with dry length decisions
           
            
            mem(13); isdry = new boolean[MWIM+1][MLAC+1] [MVAL+1][MPER+1][MBWT+1][MDMI+1] [MWPR+1][MINS+1] [2+1];            

            
            mem(14); insvalue = new float[MWIM+1][MLAC+1] [MVAL+1][MPER+1][MBWT+1][MDMI+1] [MFER+1][MINS+1] [MSEA*2+1];
            mem(15); pr1value = new float[MWIM+1][MLAC+1] [MVAL+1][MPER+1][MBWT+1][MDMI+1] [MFER+1][MINS+1] [MSEA*2+1];
            mem(16); dnb_fut = new float[MWIM+1][MLAC+1] [MVAL+1][MPER+1][MBWT+1][MDMI+1] [MFER+1][MINS+1] [2+1];            
            mem(17); dnbvalue = new float[MWIM+1][MLAC+1] [MVAL+1][MPER+1][MBWT+1][MDMI+1] [MFER+1][MINS+1] [MSEA*2+1];  
            
            mem(18);
        } catch (Error e) {
            System.out.println("Fut.set_fut_arrays Error: " + e);
            System.exit(1);
        }
        mm = mm - mem(19);
        U.println("FUT: set_fut_arrays done ..." + mm + " MB");
        U.wait(5,"at set_fut_arrays done");
        //U.stop("done");
    }
    
    
    float currentlactation(Herd h, int i) {
        //calculate the current lacation stats for this cow;
        return 99;  //dummy value;
    }
    
        
    /****************************************************************************
    - KEEP: involuntary replacement at end of stage.
    - REPLACE: animal is replaced at start of stage.
    - Repeat pure DP calculations untill results are stable. Then add the pol->policy
      to the front. 
    - dpp->policy is the steady-state optimal policy.
    - pol->policy is the user-defined policy. But pol->[] == 8 is the part that is optimized by DP_POLICY
      pol->policy is only calculated after dpp->policy has been determined.
    - Dynamic programming based on Van Arendonk[128]103 and Huirne[155]80. 
      May 2003: milk class transition between months included (Houben[816]2986.
    - Repeatability milk production classes from Van Arendonk[128]119.
    - Net return due to genetic progress: 60% of 115 kg/year = $19.73 per year.
      Add genetic progress to replacement value.
    - Assumes no temporary environmental effects (on milk production or reproduction).
    - Some erratic behavior may occur near conversion due to float rounding errors.

    //see Survival function for P(culling | cow survived until now) PUBH8420
    //RPO can never be more than (p_heifer - sell price cow) for average cow.

    DP_POLICY() and MARKOV_CHAIN() reconsiliation:   May 30, 2003
    -heifer0 should be the same as mkv->cash[0] when the same policies are followed.
    -debugging: start DP with just one stage and compare heifer0 with mkv->cash[0] with one
                starting heifer and her replacements, for t=1 to 1
           etc, add one stage to DP and compare heifer0 with mkv->cash[0] for t=1 to 2  etc.
                            no more than 12 stages can be compared this way: if e.g. 15 stages were run, then
                            DP would have 15 different stages, but MKV would have 12 different stages and
                            3 stages floatd.  So the MKV policy would be different from the DP policy and
                            therefore give different results.
                            Make BIO_DEFAULT() as simple as possible.
    -check:     when DP has converged, then difference heifer0[t=1]-heifer0[t=2] equals 
                mkv->cash[t] for one stage with steady state herd.
                * 
                * 
      policy: 0=quick, 1=optimal, 2=keepall, 3=keepprg 
    ******************************************************************************/
     void valueiteration(Herd h, Fut f) {
        final int   MINCOUNTER = 156; //416                                     //minimum number of iterations before convergence is evaluated --> prevents early convergence
        final float MAXDIFF = 0.01f;                                             //criterion to stop iteration
        //FILE      *fp = NULL; 
        long        iter = 0, calculations = 0;
        int         calc = -1;
        int         doenter[] = new int[53+1];
        float       doheifer[] = new float[106+1];       //hrd->heifer[T=144 + 2]  //144 weeks into the future for cow[]->
        int         notconverged=0, i=0, w=0, bwt=0, 
                    lac = 0, wim = 0, wpr = 0, cul = 0, val = 0, fer = 0, per = 0, dmi = 0, cyc = 0, ins = 0, nlac = 0,
                   sea = 0,  heif_itt = 1, heif_cnt = 0, nfer = 0,
                    ii = 0, jj = 1;
        int         _cyc_ = 0, _fer_ = 0, weeksdry = 0;
        float      heif[] = new float[3+1];
        float       ppreg,
                    p_sell0, p_sell1, p_sell2,		
                    keep=0, serv=0, repl=0, buy=0, delay=0, keepwet=0, keepdry=0, dnb=0,
                    fut_in=0, fut_out=0, maxdiff, pcull, pdead,
                    fut_heiin=0, fut_heiout=0, hlp, cash52, 
                    sumfixed=0;
         
        float      f_heifer, psurvival;
        short       flag = 0;
        int         counter=0;


	U.println("VALUEITERATION started ...");
//	strcpy(tempfile,filedir);
//	strcat(tempfile,"dp.txt");
//	if(E) if((fp = fopen(tempfile,"w")) == NULL) { stop(1); }
//	if(E) printf(" calc_ful:%d  errorcheck:%d",hrd->calc_ful,hrd->errorcheck);
//	write_msg_file(3,0);				//write message in msg.txt
//	if(hrd->policy >= 2) printf("\n\a Policy >= 2");
//	printf("\n policy: %d",bio->policy);
//	printf("  startseason: %d",bio->start_sea);
//	printf("\n  serve: "); for(sea = 1; sea <= 2*SEA; sea++) printf("%d ",bio->serve[sea]);
//	printf("\n  enter: "); for(sea = 1; sea <= 2*SEA; sea++) printf("%d ",hrd->enter[sea]);
//	stop(0);

//	if(policy == 0 || policy == 1 || policy == 3) policy = 013;		//policy: 0=quick, 1=optimal, 3=keepprg
//	if(policy == 2) policy = 2;			//policy: 2=optimal

	heif[0] = heif[1] = heif[2] = heif[3] = 0.0f;
	sea = h.startsea;
        if(MSEA == 0) w = 0; else w =52;
	do {
try{            
            flag = 1;
            calculations = 0;
            
            counter++;								//do loop counter
            sea--; if(sea < 1) sea = MSEA;					//season in previous stage for next iteration
            //calculation stage definition (replaced dpp_done and pol_done):
            //-2    : iterate until dpp steady state is reached, w = 52 (prices year 2 + later)
            //-1    : iterate until pol steady state is reached, w = 52 (prices year 2 + later)
            // 1-52 : calculate remaining pol data, w = 52 (prices year 2 + later)
            //53-104: calculate remaining pol data, w = 0 (prices year 1)
            if(calc >= 0) calc++;
            if(calc >= 53) w = 0;
            if(sea == h.startsea) iter++;					//year counter
            
//            U.println("\nTOP sea " +sea+ " w " +w+ " calc " +calc + " iter " +iter+ " counter " +counter);

            if(counter == 1) fut_in = f.enter[0]; else fut_in = fut_out;	//future income before this stage
//*** ENTERING HEIFERS ******************************************************************
            flag = 2;
            pcull = h.pcull_frh[1][1][ACUL][sea];
            pdead = h.pdead_frh[1][1][ACUL][sea];
            delay = f.enter[ii] * h.df + h.geneticprogress - h.p_fixed;		//delay at start of stage until optimal time to enter
            for(i = MWPR; i >= 2; i--) f.heifer[i] = f.heifer[i-1] * h.df;
                f.heifer[1] = f.enter[ii] * h.df + h.geneticprogress;           //best decision one stage in future
            p_sell1 = h.sel[1][1][ABWT][0][sea+w] * h.invcullpct;
            
            flag = 3;
//            U.println("calculations:"+calculations);
//            U.println("calc:"+calc);
//            U.println("counter:"+counter);
//            U.println("f.enter[0] f.enter[1]:"+f.enter[0]+space+f.enter[1]);
//            U.println("ii jj:"+ii+space+jj);
//            U.println("sea w:"+sea+space+w);
//            U.println("a:"+h.iofc_o[1][1] [AVAL][APER][ABWT][ADMI] [sea+w]);
//            U.println("b:"+h.p_heifer[sea+w]);
//            U.println("c:"+h.p_calf[0]);
//            U.println("d:"+h.p_fixed);
//            U.println("e:"+pcull);
//            U.println("f:"+pdead);
//            U.println("g:"+f.heifer[1]);
//            U.println("h:"+f.frh_fut[0][0] [0][0][0][0] [0][0] [ii]);
//            U.println("i:"+AVAL+space+APER+space+ABWT+space+ADMI+space+AFER+space+ACUL+space+ii);
//            U.println("j:"+f.frh_fut[2][1] [AVAL][APER][ABWT][ADMI] [AFER][ACUL] [ii]);
            
          
            
            flag = 4;
            buy = h.iofc_o[1][1] [AVAL][APER][ABWT][ADMI] [sea+w] - h.p_heifer[sea+w] + h.p_calf[AINS] - h.p_fixed
                    + pcull * (- h.p_cullloss + f.heifer[1] + p_sell1) * h.df
                    + pdead * (- h.p_deadloss + f.heifer[1]) * h.df
                    + (1 - pcull)*(1 - pdead) * f.frh_fut[2][1] [AVAL][APER][ABWT][ADMI] [AFER][ACUL] [ii] * h.df;

            flag = 5;
            if(calc >= 105 - h.delayed) buy = delay - BIG;
            if("yes".equals(h.enterheifer[sea+w])) delay = buy - BIG;           //always buy heifer: delay < buy
            if("no".equals(h.enterheifer[sea+w])) buy = delay - BIG;            //always delay heifer: buy < delay
            if(counter == 53) {							//forced buy: prevents early convergence with empty herd
                flag = 6;
                hlp = 0; for(i = 53; i >= 1; i--) hlp = hlp + doenter[i];       //protect against early convergence if buy always < delay
                if(hlp == 0) delay = buy - BIG;                                 //forced buy to avoid early convergence
            }
            f.heifer[0] = U.maxf(buy,delay);
            f.enter[jj] = f.heifer[0];
//printf("\n--- cnt:%d  buy:%.2f  delay:%.2f  fut->heifer0:%.2f",counter,buy,delay,fut->heifer0);
//if(counter == 316) stop(0);
//stop(0);

            flag = 7;
            for(i = 53; i >= 2; i--) doenter[i] = doenter[i-1];                 //array to avoid early convergence
            if(buy < delay) doenter[1] = 0; else doenter[1] = 1;                //array to avoid early convergence
            if(h.calc_ful == 1) if(buy < delay) f.pol_enter[sea+w] = 0; else f.pol_enter[sea+w] = 1;

            flag = 8;
            if(calc >= 1) doheifer[106-calc] = f.heifer[1];
            if(calc == 104) doheifer[1] = f.heifer[0] * h.df;

} catch(Exception ex) {
U.println("\nenter catch");         
U.stop(" flag:" +flag+ " error: " + ex);
System.exit(1);
}                  
            
            //***************************************************************************************************
            //FRESH COWS: maximum first WPR weeks of lactation (various levels on CUL)
            //***************************************************************************************************
            for(wim = 1; wim <= MWFR; wim++) {                                   //1 to maximum weeks fresh (WFR)
                for(lac = 1; lac <= MLAC; lac++) { 
                    for(val = 0; val <= MVAL; val++) {
                        for(per = 0; per <= MPER; per++) {    
                            for(bwt = 0; bwt <= MBWT; bwt++) {
                                for(dmi = 0; dmi <= MDMI; dmi++) {
                                    for(fer = 0; fer <= MFER; fer++) {
                                        for(cul = 0; cul <= MCUL; cul++) {
try {                                              
                                            flag = 1;
                                            pcull = h.pcull_frh[wim][lac][cul][sea];
                                            pdead = h.pdead_frh[wim][lac][cul][sea];
                                            psurvival = (1-pcull)*(1-pdead);
                                            p_sell0 = h.sel[wim][lac][bwt][0][sea+w];			//salvage value start of stage
                                            p_sell2 = h.sel[wim][lac][bwt][0][sea+w];			//salvage value at end of stage
                                            p_sell1 = p_sell2 * h.invcullpct;				//salvage value involuntary cull
                                            f_heifer = pcull * (- h.p_cullloss + f.heifer[1] + p_sell1) * h.df
                                                     + pdead * (- h.p_deadloss + f.heifer[1]) * h.df;                                             
                                        //for koe?    if(calc >= 1) h.fresh[lac][cur][bwt][rep][inv][106-calc] = f.cow_f[lac][cur][bwt][rep][inv][2][ii];	//2nd week

//                                            U.println("\nfrh top  :"+space+wim+space+lac+space+space+val+space+per+space+bwt+space+dmi+space+space+fer+space+cul+space+ii);   
//                                            U.println("pcull    :"+pcull);
//                                            U.println("pdead    :"+pdead);
//                                            U.println("psurvival:"+psurvival);
//                                            U.println("p_sell0  :"+p_sell0);
//                                            U.println("p_sell1  :"+p_sell1);
//                                            U.println("p_sell2  :"+p_sell2);
//                                            U.println("f_heifer :"+f_heifer);
                                            
                                        
                                        
                                            keep = h.iofc_o[wim][lac] [val][per][bwt][dmi] [sea+w] - h.p_fixed + f_heifer;
                                            if(wim == 1) keep += h.p_calf[0];	//heifer is pregnant with ins=0 type 
                                            if(wim < MWFR) {
                                                flag = 2;
                                                keep += psurvival * f.frh_fut[wim+1][lac] [val][per][bwt][dmi] [fer][cul] [ii] * h.df;
                                            }
                                            else {//wim == WFR
                                                flag = 3;
                                                _cyc_ = 0;  //should point to f.opn_fut for cyc = 0, cyc = 1, cyc = 2 etc. depending on use of synchronization program
                                                keep += psurvival * f.opn_fut[wim+1][lac] [val][per][bwt][dmi] [fer][_cyc_] [ii] * h.df;
                                            }
                                            serv = keep - BIG;
                                            
                                            repl = p_sell0 + f.heifer[0];
                                            if(wim == 1) repl += h.p_calf[0];   //heifer is pregnant with ins=0 type 
                                            
                                            //###########################################################################;
                                            //policy: 0=quick, 1=optimal, 2=keepall, 3=keepprg
                                            flag = 4;
                                            if(!"keepall".equals(h.cullpolicy)) f.frh_fut[wim][lac] [val][per][bwt][dmi] [fer][cul] [jj] = U.maxf(repl,keep);
                                            if("keepall".equals(h.cullpolicy)) f.frh_fut[wim][lac] [val][per][bwt][dmi] [fer][cul] [jj] = keep;
                                            
                                            if(h.calc_ful == 1) { //----------------------------------------------------
                                                flag = 5;
                                                if(!"keepall".equals(h.cullpolicy)) f.frh_pol[wim][lac] [val][per][bwt][dmi] [fer][cul] [sea+w] = (byte)U.max2u(keep,repl,1,0);
                                                if("keepall".equals(h.cullpolicy)) f.frh_pol[wim][lac] [val][per][bwt][dmi] [fer][cul] [sea+w] = 1;
                                                f.frh_rpo[wim][lac] [val][per][bwt][dmi] [fer][cul] [sea+w] = (short)(keep - repl);
                                            }//--------------------------------------------------------------------------
                                            
} catch(Exception ex) {
U.println("\nfrh catch"+space+wim+space+lac+space+space+val+space+per+space+bwt+space+dmi+space+space+fer+space+cul+space+ii);         
U.stop(" flag:" +flag+ " error: " + ex);
System.exit(1);
}                                            
                                                                                        
                                        }//cul
                                    }//fer
                                            
                                }//dmi
                            }//bwt
                        }//per
                    }//val
                            
                }//lac
            }//wim
            
                    
            //*********************************************************************************************************
            //*** OPEN COWS: after fresh period, eligible for breeding
            //*********************************************************************************************************
            for(wim = MWFR+1; wim <= MWIM; wim++) {            
                for(lac = 1; lac <= MLAC; lac++) {
                    for(val = 0; val <= MVAL; val++) {
                        for(per = 0; per <= MPER; per++) {    
                            for(bwt = 0; bwt <= MBWT; bwt++) {
                                

                                pcull = h.pcull[wim][lac][sea];
                                pdead = h.pdead[wim][lac][sea];
                                psurvival = (1-pcull)*(1-pdead); 
                                p_sell0 = h.sel[wim][lac][bwt][0][sea+w];	//salvage value start of stage
                                p_sell2 = h.sel[wim][lac][bwt][0][sea+w];	//salvage value at end of stage
                                p_sell1 = p_sell2 * h.invcullpct;		//salvage value involuntary cull
                                f_heifer = pcull * (- h.p_cullloss + f.heifer[1] + p_sell1) * h.df
                                         + pdead * (- h.p_deadloss + f.heifer[1]) * h.df;                                   
                                
                                for(dmi = 0; dmi <= MDMI; dmi++) {  
                                    for(fer = 0; fer <= MFER; fer++) {
                                        for(cyc = 0; cyc <= MCYC; cyc++) {
                                            for(ins = 0; ins <= MINS; ins++) {
                                                                                                                                          
try {                          
//U.print("\nopn catch"+space+wim+space+lac+space+space+val+space+per+space+bwt+space+dmi+space+space+fer+space+cyc+space+ins+space+ii);  
                                                
                                                if(wim == h.maxwim[lac]) {			//last week open
                                                    flag = 1;    
                                                    keep = h.iofc_o[wim][lac] [val][per][bwt][dmi] [sea+w] - h.p_fixed
                                                           + psurvival * (p_sell2 + f.heifer[1]) * h.df		//end of stage (voluntary cull)
                                                           + f_heifer;                                                   //end of stage (involuntary cull)
                                                    serv = keep - BIG;
                                                } 
                                                else if(wim >= h.maxlbw[lac]) { //past breeding period
                                                    flag = 2;
                                                    _cyc_ = 0;  //cyc no longer relevant
                                                    keep = h.iofc_o[wim][lac] [val][per][bwt][dmi] [sea+w] - h.p_fixed + f_heifer
                                                        + psurvival * f.opn_fut[wim+1][lac] [val][per][bwt][dmi] [fer][_cyc_] [ii] * h.df;                                                        
                                                    serv = keep - BIG;
                                                }
                                                
                                                else if(wim <= h.minvwp[lac]) { //before breeding period
                                                    flag = 3;
                                                    _cyc_ = cyc + 1; if (_cyc_ > MCYC) _cyc_ = 0;
                                                    keep = h.iofc_o[wim][lac] [val][per][bwt][dmi] [sea+w] - h.p_fixed + f_heifer	
                                                        + (1-pcull)*(1-pdead) * f.opn_fut[wim+1][lac] [val][per][bwt][dmi] [fer][_cyc_] [ii] * h.df;
                                                    if(wim == 1) keep += h.p_calf[0];
                                                    serv = keep - BIG;

                                                }
                                                else if(wim >= h.minvwp[lac] && wim <= h.maxlbw[lac]) {     //breeding opportunity at start of week (if estrus detected)
                                                    flag = 4;
                                                    _cyc_ = cyc + 1; if (_cyc_ > MCYC) _cyc_ = 0;   //next cyc
                                                    keep = h.iofc_o[wim][lac] [val][per][bwt][dmi] [sea+w] - h.p_fixed + f_heifer
                                                        + psurvival * f.opn_fut[wim+1][lac] [val][per][bwt][dmi] [fer][_cyc_] [ii] * h.df;  
                                                    if(wim == 1) keep += h.p_calf[0];
                                                    serv = keep - BIG;
                                                    
                                                    if(cyc == 0) {  
                                                        flag = 5;
                                                        ppreg = h.servrate[wim][val][sea] * h.concrate[wim][lac][fer][ins][sea];                                                    
                                                        serv = h.iofc_o[wim][lac] [val][per][bwt][dmi] [sea+w] - h.p_fixed + f_heifer   //first week iofc same
                                                             - h.reprocost - h.servrate[wim][val][sea] * h.inseminationcost[ins]
                                                             + psurvival * (1 - ppreg) * f.opn_fut[wim+1][lac] [val][per][bwt][dmi] [fer][_cyc_] [ii] * h.df
                                                             + psurvival * ppreg * f.prg_fut[wim+1][lac] [val][per][bwt][dmi] [1][ins] [ii] * h.df;	//2nd week of pregnancy!
                                                        if(wim == 1) serv += h.p_calf[0];//normally does not occur that wim=1
                                                        if("yes".equals(h.serve[sea+w])) keep = serv - BIG;    //always serv (RPO ok)                                                        
                                                        if("no".equals(h.serve[sea+w])) serv = keep - BIG;     //never serv (RPO ok)  
                                                        
                                                        if(h.calc_ful == 1) { //----------------------------------------------------                                                        
                                                            flag = 6;
                                                            f.insvalue[wim][lac] [val][per][bwt][dmi] [fer][ins] [sea+w] = serv - keep;
                                                            
                                                            dnb = h.iofc_o[wim][lac] [val][per][bwt][dmi] [sea+w] - h.p_fixed + f_heifer
                                                                + psurvival * f.dnb_fut[wim+1][lac] [val][per][bwt][dmi] [fer][ins] [ii] * h.df;
                                                            f.dnbvalue[wim][lac] [val][per][bwt][dmi] [fer][ins] [sea+w] = dnb - U.maxf(keep,serv); 
                                                            f.dnb_fut[wim][lac] [val][per][bwt][dmi] [fer][ins] [jj] = dnb;
                                                        }//-------------------------------------------------------------------------
                                                    }//cyc == 0
                                                    
                                                }//else
                                                repl = p_sell0 + f.heifer[0];
                                                if(wim == 1) repl += h.p_calf[0];
                                                calculations++;
                                                
                                                if((h.dailymilk_o[wim][lac][val][per][sea] < h.cullmilk) ||                 //too low milk production
                                                (h.iofc_o[wim][lac] [val][per][bwt][dmi] [sea+w] / STAGE < h.culliofc)) {   //too low margin
                                                    flag = 7;
                                                    f.opn_fut[wim][lac] [val][per][bwt][dmi] [fer][cul] [jj] = repl;

                                                    if(h.calc_ful == 1) { //---------------------------------------------
                                                        flag = 8;
                                                        f.opn_pol[wim][lac] [val][per][bwt][dmi] [fer][cyc] [sea+w] = -3;
                                                        f.opn_rpo[wim][lac] [val][per][bwt][dmi] [fer][cyc] [sea+w] = (short)(U.maxf(serv,keep) - repl);
                                                    }//-------------------------------------------------------------------
                                                }
                                                else if("keepall".equals(h.cullpolicy)) {
                                                    flag = 9;
                                                    f.opn_fut[wim][lac] [val][per][bwt][dmi] [fer][cul] [jj] = U.maxf(serv,keep);
                                                
                                                    if(h.calc_ful == 1) { //--------------------------------------------------
                                                        flag = 10;
                                                        f.opn_pol[wim][lac] [val][per][bwt][dmi] [fer][cyc] [sea+w] = 3;
                                                        f.opn_rpo[wim][lac] [val][per][bwt][dmi] [fer][cyc] [sea+w]  = (short)(U.maxf(serv,keep) - repl);                                                       
                                                    }//-----------------------------------------------------------------------
                                                }                                                                                                
                                                else {
                                                    flag = 11;
                                                    f.opn_fut[wim][lac] [val][per][bwt][dmi] [fer][cul] [jj] = U.maxf(serv,keep,repl);
                                                    
                                                    if(h.calc_ful == 1) { //--------------------------------------------------
                                                        flag = 12;
                                                        f.opn_pol[wim][lac] [val][per][bwt][dmi] [fer][cyc] [sea+w] = (byte)(U.max3u(repl,keep,serv,0,1,2));
                                                        f.opn_rpo[wim][lac] [val][per][bwt][dmi] [fer][cyc] [sea+w]  = (short)(U.maxf(serv,keep) - repl);                                     
                                                    } //----------------------------------------------------------------------     
                                                }
                                                
} catch(Exception ex) {
U.print("\nopn catch"+space+wim+space+lac+space+space+val+space+per+space+bwt+space+dmi+space+space+fer+space+cyc+space+ins+space+ii);         
U.stop(" flag:" +flag+ " error: " + ex);
System.exit(1);
}                                                 
                                              
                                            }//ins
                                        }//cyc
                                    }//fer
                                }//dmi
                            }//bwt
                        }//per
                    }//val
                }//lac
            }//wim
            //***************************************************************************************************
            //*** PREGNANT COWS
            //***************************************************************************************************
            for(wim = MWFR+1; wim <= MWIM; wim++) {
                for(lac = 1; lac <= MLAC; lac++) {
                    if(lac < MLAC) nlac = lac + 1; else nlac = lac;             //continue after maxlac in the same lactation
                    for(val = 0; val <= MVAL; val++) {
                            for(per = 0; per <= MPER; per++) {    
                                for(bwt = 0; bwt <= MBWT; bwt++) {
                                    for(dmi = 0; dmi <= MDMI; dmi++) {                                
 
                                        for(wpr = 1; wpr <= MWPR; wpr++) if(wim - wpr >= h.minvwp[lac] && wim - wpr <= h.maxlbw[lac]) {	//if(wim - wpr <= h.maxlbw[lac])                                    
                                            for(ins = 0; ins <= MINS; ins++) {
                                                
//U.println("prg top"+space+wim+space+lac+space+space+val+space+per+space+bwt+space+dmi+space+space+wpr+space+ins+space+ii);   
try {                                                
                                                flag = 1;                                              
                                                pcull = h.pcull[wim][lac][sea] * h.cullrrpreg;
                                                pdead = h.pdead[wim][lac][sea] * h.deadrrpreg;
                                                psurvival = (1-pcull)*(1-pdead); 
                                                p_sell0 = h.sel[wim][lac][bwt][wpr][sea+w];	//salvage value start of stage
                                                p_sell2 = h.sel[wim][lac][bwt][wpr][sea+w];	//salvage value at end of stage
                                                p_sell1 = p_sell2 * h.invcullpct;		//salvage value involuntary cull
                                                f_heifer = pcull * (- h.p_cullloss + f.heifer[1] + p_sell1) * h.df
                                                         + pdead * (- h.p_deadloss + f.heifer[1]) * h.df;     
                                                
                                                
                                                //wet for sure, not calving ==============================================================
                                                if(wpr < MWPR - h.maxdry[lac]) {
//U.println(" wpr1:"+wpr);      
                                                    flag = 2;
                                                    _fer_ = 0; _cyc_ = MCYC;
                                                    keepwet = h.iofc_p[wim][lac] [val][per][bwt][dmi] [wpr][sea+w] - h.p_fixed - f_heifer
                                                        + psurvival * (1 - h.abort[wpr]) * f.prg_fut[wim+1][lac] [val][per][bwt][dmi] [wpr+1][ins] [ii] * h.df
                                                        + psurvival * h.abort[wpr] * f.opn_fut[wim+1][lac] [val][per][bwt][dmi] [_fer_][_cyc_] [ii] * h.df;	//rep == 0 is the default
            
                                                }//decide on wet or dry, not calving: ====================================================
                                                else if(wpr < MWPR && wpr >= MWPR - h.maxdry[lac] && wpr < MWPR - h.mindry[lac]) {
//U.println(" wpr2:"+wpr);
                                                    //one week later the cow is wet: must be wet this time
                                                    flag = 3;
                                                    _fer_ = 0; _cyc_ = MCYC;
                                                    weeksdry = MWPR - wpr + 1;
                                                    keepdry = h.iofc_d[bwt][dmi] [wpr][sea+w] - h.p_fixed - f_heifer
                                                        + psurvival * h.abort[wpr] * f.opn_fut[wim+1][lac] [val][per][bwt][dmi] [_fer_][_cyc_] [ii] * h.df
                                                        + psurvival * (1 - h.abort[wpr]) * f.dry_fut[wim+1][lac] [val][per][bwt][dmi] [wpr+1][weeksdry][ins] [ii] * h.df;
                                                    keepwet = keepdry - BIG;
                                                                                                    
                                                    //one week later the cow is dry: can be wet or dry this time
                                                    if(f.isdry[wim+1][lac] [val][per][bwt][dmi] [wpr+1][ins] [ii] == true) {
                                                        flag = 4;
                                                        keepwet = h.iofc_p[wim][lac] [val][per][bwt][dmi] [wpr][sea+w] - h.p_fixed - f_heifer
                                                                + psurvival * h.abort[wpr] * f.opn_fut[wim+1][lac] [val][per][bwt][dmi] [_fer_][_cyc_] [ii] * h.df
                                                                + psurvival * (1 - h.abort[wpr]) * f.prg_fut[wim+1][lac] [val][per][bwt][dmi] [wpr+1][ins] [ii] * h.df;
                                                    }
                                                    
                                                }//dry for sure, not calving: =============================================================
                                                else if(wpr < MWPR && wpr >= MWPR - h.mindry[lac]) {
//U.println(" wpr3:"+wpr);
//U.println(" fut: "+f.prg_fut[wim+1][lac] [val][per][bwt][dmi] [wpr][ins] [ii] );
                                                    flag = 5;
                                                    _fer_ = 0; _cyc_ = MCYC;
                                                    keepdry = h.iofc_d[bwt][dmi] [wpr][sea+w] - h.p_fixed - f_heifer
                                                        + psurvival * h.abort[wpr] * f.opn_fut[wim+1][lac] [val][per][bwt][dmi] [_fer_][_cyc_] [ii] * h.df
                                                        + psurvival * (1 - h.abort[wpr]) * f.prg_fut[wim+1][lac] [val][per][bwt][dmi] [wpr][ins] [ii] * h.df;
                                                    keepwet = keepdry - BIG;
                                                        
                                                
                                                }//calving ================================================================================= 
                                                else if(wpr == MWPR) {

                                                    flag = 6;
                                                    _fer_ = 0; _cyc_ = MCYC;
                                                    if(wpr >= MWPR - h.maxdry[lac]) {//wet or dry
//U.println(" wpr4:"+wpr);                                     
                                                        flag  = 7;
                                                        keepdry = h.iofc_d[bwt][dmi] [wpr][sea+w] - h.p_fixed - f_heifer;
                                                        if(wim < MWIM)
                                                            keepdry += psurvival * h.abort[wpr] * f.opn_fut[wim+1][lac] [val][per][bwt][dmi] [_fer_][_cyc_] [ii] * h.df;
                                                        else
                                                            keepdry += psurvival * h.abort[wpr] * (- h.p_cullloss + f.heifer[1] + p_sell1) * h.df;
                                                        
                                                        weeksdry = 1;//MWPR - wpr + 1;
                                                        for(int nval = 0; nval <= MVAL; nval++) {
                                                            for(int nbwt = 0; nbwt <= MBWT; nbwt++) {
                                                                for(int ncul = 0; ncul <= MCUL; ncul++) {
                                                                    flag = 77;
//                                                                    U.println("77:"+nval+space+nbwt+space+ncul+space+weeksdry);
//                                                                    U.println("a:"+h.tval[val][weeksdry] [nval] );
//                                                                    U.println("b:"+h.tbwt[wim][bwt] [nbwt] );
//                                                                    U.println("c:"+h.tcul[wim][weeksdry] [ncul] );
//                                                                    U.println("d:"+h.tfer[wim][weeksdry] [nfer] );
//                                                                    U.println("e:"+h.abort[wpr] );
//                                                                    U.println("f:"+f.frh_fut[1][nlac] [nval][per][nbwt][dmi] [nfer][ncul] [ii] );
                                                                    
                                                                    keepdry += h.tval[val][weeksdry] [nval] * h.tbwt[wim][bwt] [nbwt] * h.tcul[wim][weeksdry] [ncul] * h.tfer[wim][weeksdry] [nfer] *
                                                                        psurvival * (1 - h.abort[wpr]) * f.frh_fut[1][nlac] [nval][per][nbwt][dmi] [nfer][ncul] [ii] * h.df;  
                                                                }
                                                            }
                                                        }
                                                        keepwet = keepdry - BIG;
                                                    }
                                                    if(wpr <= MWPR - h.mindry[lac]) {//wet for sure
//U.println(" wpr5:"+wpr);                                          
                                                        flag = 8;
                                                        keepwet = h.iofc_p[wim][lac] [val][per][bwt][dmi] [wpr][sea+w] - h.p_fixed - f_heifer;
                                                        if(wim < MWIM)
                                                            keepwet += psurvival * h.abort[wpr] * f.opn_fut[wim+1][lac] [val][per][bwt][dmi] [_fer_][_cyc_] [ii] * h.df;  
                                                        else
                                                            keepwet += psurvival * h.abort[wpr] * (- h.p_cullloss + f.heifer[1] + p_sell1) * h.df;
                                                        
                                                        flag = 9;
                                                        weeksdry = 0;//MWPR - wpr
                                                        for(int nval = 0; nval <= MVAL; nval++) {
                                                            for(int nbwt = 0; nbwt <= MBWT; nbwt++) {
                                                                for(int ncul = 0; ncul <= MCUL; ncul++) {
                                                                    keepwet += h.tval[val][weeksdry] [nval] * h.tbwt[wim][bwt] [nbwt]* h.tcul[wim][weeksdry] [ncul] * h.tfer[wim][weeksdry] [nfer] *
                                                                        psurvival * (1 - h.abort[wpr]) * f.frh_fut[1][nlac] [nval][per][nbwt][dmi] [nfer][ncul] [ii] * h.df;                                                                      
                                                                }
                                                            }                            
                                                        }
                                                    }   
                                                }
                                                else U.stop("pregnant cow error"); 
                                                repl = p_sell0 + f.heifer[0];
                                                calculations++;
  
//U.println(" wpr6:"+wpr);                                                  
                                                                                                
                                                //###########################################################################;
                                                if("decide".equals(h.cullpolicy))  f.prg_fut[wim][lac] [val][per][bwt][dmi] [wpr][ins] [jj] = U.maxf(repl,keepwet,keepdry);
                                                if(!"decide".equals(h.cullpolicy)) f.prg_fut[wim][lac] [val][per][bwt][dmi] [wpr][ins] [jj] = U.maxf(keepwet,keepdry);
                                                if(keepwet >= keepdry) {    //once isdry == false, 
                                                    f.isdry[wim][lac] [val][per][bwt][dmi] [wpr][ins] [jj] = false;
                                                }
                                                                                                
                                                flag = 10;
                                                if(wpr > MWPR - MDRY) {//update f.dry_fut[][] [][][][] 

//U.println(" wpr7:"+wpr);                          
//U.print("prg dry"+space+wim+space+lac+space+space+val+space+per+space+bwt+space+dmi+space+space+wpr+space+ins+space+space+ii); 
//U.print(" _wpr_:" +_wpr_);
//U.print(" dry:"+dry+space+f.opn_fut[wim+1][lac] [val][per][bwt][dmi] [_fer_][_cyc_] [ii] );
//U.println(space+f.dry_fut[wim+1][lac] [val][per][bwt][dmi] [_wpr_][dry+1][ins] [ii] ); 

//what a mess: wim == WIM, wpr == MWPR
//                                                    flag = 11;
//                                                    if(wim < WIM) {
//                                                        _fer_ = 0; _cyc_ = MCYC; _wpr_ = MWPR - wpr; 
//                                                        for(dry = 0; dry < MDRY; dry++) {
//                                                            keepdry = h.iofc_d[bwt][dmi] [wpr][sea+w] - h.p_fixed - f_heifer
//                                                                + psurvival * h.abort[wpr] * f.opn_fut[wim+1][lac] [val][per][bwt][dmi] [_fer_][_cyc_] [ii] * h.df
//                                                                + psurvival * (1 - h.abort[wpr]) * f.dry_fut[wim+1][lac] [val][per][bwt][dmi] [_wpr_+1][dry+1][ins] [ii] * h.df;                                                    
//                                                            f.dry_fut[wim][lac] [val][per][bwt][dmi] [_wpr_][dry][ins] [jj] = keepdry;
//                                                        }
//                                                    }
//                                                    else {
//                                                            keepdry = h.iofc_d[bwt][dmi] [wpr][sea+w] - h.p_fixed - f_heifer
//                                                                + psurvival * h.abort[wpr] * (- h.p_cullloss + f.heifer[1] + p_sell1) * h.df;
//                                                        }
//                                                        f.dry_fut[wim][lac] [val][per][bwt][dmi] [_wpr_][dry][ins] [jj] = keepdry;
//                                                    }
                                                }
                                                    
                                                if(h.calc_ful == 1) { //----------------------------------------------------
                                                    flag = 13;
                                                    if("decide".equals(h.cullpolicy))  f.prg_pol[wim][lac] [val][per][bwt][dmi] [wpr][ins] [sea+w] = (byte)U.max3u(keepwet,keepdry,repl,2,1,-1);
                                                    if(!"decide".equals(h.cullpolicy)) f.prg_pol[wim][lac] [val][per][bwt][dmi] [wpr][ins] [sea+w] = (byte)U.max2u(keepwet,keepdry,2,1);
                                                    f.prg_rpo[wim][lac] [val][per][bwt][dmi] [wpr][ins] [sea+w] = (short)(keep - repl);

                                                    if(wpr == 1) {
                                                        flag = 14;
                                                        for(fer = 0; fer <= MFER; fer++) {
                                                            f.pr1value[wim][lac] [val][per][bwt][dmi] [fer][ins] [sea+w] =
                                                                f.prg_fut[wim][lac] [val][per][bwt][dmi] [wpr][ins] [jj]
                                                              - f.opn_fut[wim][lac] [val][per][bwt][dmi] [fer][0] [jj]; 
                                                        }
                                                    }
                                                    
                                                }//--------------------------------------------------------------------------
     
} catch(Exception ex) {
U.println("\nprg catch"+space+wim+space+lac+space+space+val+space+per+space+bwt+space+dmi+space+space+wpr+space+ins+space+ii);         
U.stop(" flag:" +flag+ " error: " + ex);
System.exit(1);
}                                                 
                                                
                                                
                                            
                                            }//ins
                                        }//wpr
                                    }//dmi
                                }//bwt
                            }//per
                    }//val
                }//lac
            }//wim
        
            
//*** DP BOTTOM *************************************************************************
try {
            flag = 1;
            pol_report(counter,h);
            fut_out = f.heifer[0];					//future revenue begin of stage
            
            for(i = 104; i >= 2; i--) f.cash[i] = f.cash[i-1];          //move cash up in array[1..104]
            f.cash[1] = fut_out - h.df * fut_in;                        //assign new cash[1]
            
            fut_heiin = fut_heiout;            
            fut_heiout = f.frh_fut[1][1] [AVAL][APER][ABWT][ADMI] [AFER][ACUL] [jj];
            flag = 2;
    //	printf("\n fut_in      :%f = old fut->heifer0",fut_in);
    //	printf("\n fut_out     :%f = new fut->heifer0",fut_out);
    //	printf("\n fut_cash[1] :%f = fut_out - df * fut_in",fut->cash[1]);
    //	stop(0);

            // accelerate convergence ////////////////////////////////////////////////////////
            // method: fit exponential distribution; CHESS-RO, Ruud Huirne, Wageningen University
            if(sea == 1 && counter > 104 && h.df < 1.0 && calc == -1 && heif_itt <= 6) {
                heif[++heif_cnt] = f.heifer[0];
            }
//            if(sea == 1 && heif_cnt == 3 &&               calc == -1 && heif_itt <= 6) { //set heif_itt for the # accelerations
//            //	printf("\n [%d] %.2f %.2f %.2f",heif_cnt,heif[1],heif[2],heif[3]);
//                System.out.printf(" A:");
//                heif[0] = estimate_heifer0(heif[1],heif[2],heif[3]);
//                heif[1] = heif[2] = heif[3] = 0.0;
//                heif_cnt = 0;
//                heif_itt++;
//                if(heif[0] != -X8 && U.abs(heif[0] - fut_out) > 20) {
//                    System.out.printf("%.0f",heif[0]);
//                    diff = heif[0] - fut_out;
//                    fut_out += diff;
//                    f.enter[jj] += diff;
//                    f.heifer[0] += diff;
//                    for(lac = 1; lac <= h.maxlac; lac++)
//                        for(cur = 0; cur <= h.maxcur; cur++)
//                            for(bwt = 0; bwt <= h.maxbwt; bwt++)
//                                for(wim = 1; wim <= WIM; wim++) {
//                                    for(rep = 0; rep <= h.maxrep; rep++) 
//                                        for(inv = 0; inv <= h.maxinv; inv++) 
//                                            f.cow_f[lac][cur][bwt][rep][inv][wim][jj] += diff;	//-fut->heifer + diff?
//
//                                    for(rep = 0; rep <= h.maxrep; rep++) 
//                                        f.cow_o[lac][cur][bwt][rep][wim][jj] += diff;	//-fut->heifer + diff?
//                                }
//                                if(h.policy != "?? keepall")
//                                    for(wpr = 1; wpr <= WPR; wpr++) if(wim - wpr >= 1 && wim - wpr <= LBW)
//                                        f.cow_x[lac][cur][bwt][wpr][wim][jj] += diff;	//-fut->heifer + diff?
//                }
//            }     
            
            //note at convergence when df > 0: 
            //   df = 1 - (fut->cash[1] / fut_out);
            //   fut->cash[1] = fut_out - df * fut_out 
            //   fut->cash[1] = (1 - df) * fut_out;
            //   fut->cash[1] = fut_out - fut_in;
            //---------------------------------------> this is the same solution as policy iteration
            ////////////////////////////////////////////////////////////////////////////////////////
            //June/July 2005:
            //Equivalent annual annuity = EAA = NPV / pvifa, see Keown et al. Foundations of finance, page 554
            //Calculate EAA for last 12 months and compare to previous 12 months, if same: converge
            //See 'Equivalent period costs.xls' in Meadows review
            //fut_cash[1] can change, but the change is perhaps due to a different path the algorithm takes and 
            //the real cash change is far in the future.  So say fut_cash is always 100 but then jumps to 101: this 
            //may be a result of a different path with 100 cash now and 101 cash somewhere (far) in the future. Calculating
            //EAA for the last 12 stages does not do justice to that and is not necessarily correct.
            //EAA calculated for the same 12 stages in MARKOV_CHAIN_STATS is correct and may be different from the EAA calculated
            //here in DP_POLICY.  Under stead-state conditions, the EAA in both the DP_POLICY and MARKOV_CHAIN_STATS should
            //be the same.
            
            //Because EAA in DP_POLICY is not correct, this codes does not matter anymore:
            //pvifa = 0.0; if(counter < SEA) for(i = 1; i <= counter; i++) pvifa = pvifa + pow(df,i-1);	//present-value interest factor for an annuity
            //else for(i = 1; i <= SEA; i++) pvifa = pvifa + pow(df,i-1);	//present-value interest factor for an annuity
            //printf(" df=%f pvifa=%f",df,pvifa);


            flag = 3;
            cash52 = 0; for(i = 1; i <= 52; i++) cash52 += f.cash[i];
            maxdiff = 0;
            for(i = 1; i <= 52; i++) { //if(counter - 53 >= i)
                maxdiff += U.abs(f.cash[i] - f.cash[i+52]);
            //	printf("\nfut_cash[%d]:%.2f  fut_cash[%d]:%.2f  maxdiff%.2f",i,fut_cash[i],i+12,fut_cash[i+12],maxdiff);
            }

            flag = 5;
            sumfixed -= h.p_fixed;
            if(fut_out == sumfixed) maxdiff = 1;	//prevents early convergence when alwaysbuy == 0 
            if(notconverged == 1) maxdiff = 0;	//results in convergence
            System.out.printf("\n %d:%.0f pol:%s calc:%2d w:%2d sea:%2d fut_out:%.0f [%.2f] fut_hei:%.0f cash:%6.2f cash52:%.2f conv:%f",
                counter, U.ceil((double)counter/52), h.cullpolicy, calc, w, sea, fut_out,fut_out/(double)counter, fut_heiout, f.cash[1], cash52, maxdiff);

            
            flag = 6;
            if(buy < delay) System.out.printf(" delay"); else System.out.printf(" buy");
//            if(sea == h.startsea) write_msg_file(4,maxdiff);

    //	save data in dp.txt:
    //	if(sea == 1)
    //	fprintf(fp,"\n%d calc: %d w: %d sea: %d fut_out: %f cash: %f yr: %f heifer0: %f heifer1: %f maxdiff: %f",counter,calc,w,sea,fut_out,fut->cash[1],cash52,fut->heifer0,heifer[1],maxdiff);


//calc -1   --> find steady state, save all fut->fresh[]
//calc 1 to 52: last 52 weeks if price is different

            flag = 7;
            if(calc == -1 && sea == h.startsea && maxdiff < MAXDIFF && counter >= MINCOUNTER) {	//converged
                U.print("*");
            //	if(bio->all == 1) for(i = 1; i <= SEA*2; i++) ful->pol_enter[i] = bio->enter[i];
                calc = 0;
            }
            if(calc == 52) {                                            

                //write_warm_file(1,jj);			//save v.warm.txt for warm start
            }
            if(calc == 104) {		//calc == 104? or calc == 52?
                //write_fresh_file(0,jj);	//creates file with cash flows for future lactations
            //	older versions have code here to calculate additional cow statistics such as allowable breeding space
            }//calc == 104


            flag = 8;
    //	if(counter == 20000 || (counter == 200 && fut_out == sumfixed))
            if(counter == 10 || (U.abs(fut_heiout - fut_heiin) < MAXDIFF && counter >= 300 && maxdiff == 1.0)) {
                if(counter == 10000) U.stop(" not converged");
                else U.println(" fut_hei converged");	//alternative convergence
                //write_msg_file(5,0);
                notconverged = 1;
            }
            jj = ii; if(ii == 0) ii = 1; else ii = 0;	//swap ii and jj
        //	printf(" end do");
    
        
} catch(Exception ex) {
U.println("\nbottom catch:"+space+wim+space+lac+space+space+val+space+per+space+bwt+space+dmi+space+space+wpr+space+ins+space+ii);         
U.stop(" flag:" +flag+ " error: " + ex);
System.exit(1);
}    
            //U.wait(1,"bottom");
        
	} while(calc != 104);
	System.out.printf("\n valueiteration done ... %.1f years to convergence  calc:%d",(double)(counter-104)/52,calc);
	System.out.printf(" calculations:[%d]",calculations);
	if(h.calc_ful == 1) { 
            if(MSEA == 52) {
                System.out.printf("\n policy yr 2: "); for(sea = 53; sea <= 104; sea++) System.out.printf("%d",f.pol_enter[sea]);
                System.out.printf("\n policy yr 1: "); for(sea = 1; sea <= 52; sea++) System.out.printf("%d",f.pol_enter[sea]);
            }
            else {
                System.out.printf("\n f.pol_enter[0]: "); System.out.printf("%d",f.pol_enter[0]);
            }
	}
        U.print(""+flag); //no need to print this
    }//end: valueiteration

    
      
    
    //**************************************************************************
    //ESTIMATE_HEIFER0: Predict fut->heifer0 when it converges when df > 0.
    //- Procedure to speed up convergence of DP.
    //- Same as procedure calc_enrgpd in unit calcdp1.pas (Ruud Huirne, CHESS-RO, Wageningen University)
    //- Fits a negative exponential function to the heifer0 estimates.
    static double estimate_heifer0(double a1, double a2, double a3) {
        double	param_elab, param_a, param_b, a1a2, a2a3;
        a1a2 = a1 - a2;
        a2a3 = a2 - a3;
//	printf("\n a1:%.2f a2:%f a3:%f a1a2:%f a2a3:%f",a1,a2,a3,a1a2,a2a3);
        if(a2a3 != 0 && (a1a2 * a2a3 - a2a3 * a2a3) != 0) {
            param_elab = a1a2 / a2a3;
            param_b = a1a2 * a1a2 * a1a2 / (a1a2 * a2a3 - a2a3 * a2a3);
            if(param_elab > 1.05) {	//change is decreasing --> convergence to constant fut_out possible
                param_a = a1 - param_b / param_elab;
            //	printf(" param_elab:%f  param_b:%f  param_a:%f ",param_elab,param_b,param_a);
            //	stop(0);
                return param_a;	//estimate of final fut_out
            }
            else {
                System.out.printf(":ratio:%.2f(%.0f)",param_elab,a1-param_b/param_elab);
                 return -X8;
            }
        }
        else return -X8;
    }/*** end ESTIMATE_HEIFER0 ***/
 
    
    
    
//    /******************************************************************************
//    DP_FUT: Calculates optimal insemination and replacement policy for individual
//    cows, including the Retention Pay-Off (RPO = KEEP - REPLACE) for all possible states.
//    - KEEP: involuntary replacement at end of stage.
//    - REPLACE: animal is replaced at start of stage.
//    - Repeat pure DP calculations untill results are stable. Then add the pol->policy
//      to the front. 
//    - dpp->policy is the steady-state optimal policy.
//    - pol->policy is the user-defined policy. But pol->[] == 8 is the part that is optimized by DP_POLICY
//      pol->policy is only calculated after dpp->policy has been determined.
//    - Dynamic programming based on Van Arendonk[128]103 and Huirne[155]80. 
//      May 2003: milk class transition between months included (Houben[816]2986.
//    - Repeatability milk production classes from Van Arendonk[128]119.
//    - Net return due to genetic progress: 60% of 115 kg/year = $19.73 per year.
//      Add genetic progress to replacement value.
//    - Assumes no temporary environmental effects (on milk production or reproduction).
//    - Some erratic behavior may occur near conversion due to float rounding errors.
//
//    //see Survival function for P(culling | cow survived until now) PUBH8420
//    //RPO can never be more than (p_heifer - sell price cow) for average cow.
//
//    DP_POLICY() and MARKOV_CHAIN() reconsiliation:   May 30, 2003
//    -heifer0 should be the same as mkv->cash[0] when the same policies are followed.
//    -debugging: start DP with just one stage and compare heifer0 with mkv->cash[0] with one
//                starting heifer and her replacements, for t=1 to 1
//           etc, add one stage to DP and compare heifer0 with mkv->cash[0] for t=1 to 2  etc.
//                            no more than 12 stages can be compared this way: if e.g. 15 stages were run, then
//                            DP would have 15 different stages, but MKV would have 12 different stages and
//                            3 stages floatd.  So the MKV policy would be different from the DP policy and
//                            therefore give different results.
//                            Make BIO_DEFAULT() as simple as possible.
//    -check:     when DP has converged, then difference heifer0[t=1]-heifer0[t=2] equals 
//                mkv->cash[t] for one stage with steady state herd.
//    ******************************************************************************/
//    static void dp_fut(Hrd h, Fut f) {
//        final int   MINCOUNTER = 156; //416	//minimum number of iterations before convergence is evaluated --> prevents early convergence
//        final float MAXDIFF = 0.01F;		//criterion to stop iteration
//        //FILE      *fp = NULL; 
//        String      tempfile;
//        long        iter = 0, counter = 0, calculations = 0;
//        int         calc = -1;
//        int         enter[] = new int[2*SEA+1];
//        int         notconverged = 0, i = 0, t = 0, w = 52, bwt, nc = 0, ni = 0,
//                    nb = 0, sea40 = 0, cur = 0, lac = 0, wim = 0, wpr = 0, 
//                    rep = 0, inv = 0, sea = 0, flag = 0, heif_itt = 1, heif_cnt = 0,
//                    policy = 0, ii = 0, jj = 1, minwim = 0, maxwim = 0, minni = 0, maxni = 0;
//        double      heif[] = new double[3+1];
//        double      p_heifer, p_fixed = 0.0,	ppreg = 0.0,
//                    breedcost = 0.0, df = 0.0, p_sell0	= 0.0, p_sell1	= 0.0, p_sell2 = 0.0,		
//                    breed = 0.0, keep = 0.0, serv = 0.0, repl = 0.0, buy = 0.0, delay = 0.0,
//                    prev_yield = 0.0, prev_rev = 0.0, diff = 0.0, help1 = 0.0, 
//                    fut_in = 0.0, fut_out = 0.0, maxdiff = 0.0, pidt = 0.0, abort = 0.0, pid = 0.0,
//                    fut_heiin = 0.0, fut_heiout = 0.0, breedingcost = 0.0, hlp = 0.0, cash52 = 0.0,
//                    pconc = 0.0, sumfixed = 0.0;
//
//	U.println("DP_FUT started ...");
////	strcpy(tempfile,filedir);
////	strcat(tempfile,"dp.txt");
////	if(E) if((fp = fopen(tempfile,"w")) == NULL) { stop(1); }
////	if(E) printf(" calc_ful:%d  errorcheck:%d",hrd->calc_ful,hrd->errorcheck);
////	write_msg_file(3,0);				//write message in msg.txt
////	if(hrd->policy >= 2) printf("\n\a Policy >= 2");
////	printf("\n policy: %d",bio->policy);
////	printf("  startseason: %d",bio->start_sea);
////	printf("\n  serve: "); for(sea = 1; sea <= 2*SEA; sea++) printf("%d ",bio->serve[sea]);
////	printf("\n  enter: "); for(sea = 1; sea <= 2*SEA; sea++) printf("%d ",hrd->enter[sea]);
////	stop(0);
//
//	if(policy == 0 || policy == 1 || policy == 3) policy = 013;		//policy: 0=quick, 1=optimal, 3=keepprg
//	if(policy == 2) policy = 2;												//policy: 2=optimal
//	heif[0] = heif[1] = heif[2] = heif[3] = 0.0;
//	for(lac = 1; lac <= LAC; lac++) 
//            if(h.minvwp[lac] < WFR || h.minvwp[lac] > h.maxlbw[lac] || h.maxlbw[lac] > h.LBW || h.maxwim <= h.maxlbw[lac]) { 
//                System.out.printf("lac:%d hrd->minvwp:%d or hrd->maxlbw:%d or hrd->maxwim:%d  error",lac,h.minvwp[lac],h.maxlbw[lac],h.maxwim); 
//                U.stop("1"); 
//            }
////	reset_fut_record();													//resets fut record (if futfile == 0) then do not read fut file
//	df = U.discount(h.interest/52,1);									//discount factor 1 week into the future
//
//	h.geneticprogress = 19.73 / 52;									//$ genetic progress per month
//	p_fixed = (h.p_fixed_labor + h.p_fixed_other) * STAGE;		//fixed cost for stage
//	breedingcost = h.p_breeding + h.timebreeding / 60 * h.p_labor;			//cost per breeding
//	sea = h.startsea;
//	do {
//            calculations = 0;
//            counter++;														//do loop counter
//            sea--; if(sea < 1) sea = SEA;									//season in previous stage for next iteration
//            //calculation stage definition (replaced dpp_done and pol_done):
//            //-2    : iterate until dpp steady state is reached, w = 52 (prices year 2 + later)
//            //-1    : iterate until pol steady state is reached, w = 52 (prices year 2 + later)
//            // 1-52 : calculate remaining pol data, w = 52 (prices year 2 + later)
//            //53-104: calculate remaining pol data, w = 0 (prices year 1)
//            if(calc >= 0) calc++;
//            if(calc >= 53) w = 0;
//            if(sea == h.startsea) iter++;								//year counter
//            p_heifer = h.p_heifer[sea+w];								//$ heifer price
//            if(counter == 1) fut_in = f.enter[0]; else fut_in = fut_out;	//future income before this stage
////*** ENTERING HEIFERS ******************************************************************
//            lac = 1; cur = 0; bwt = 1; rep = 0; inv = 0; wim = 1;
//            pid = h.f_pid_f[lac][inv][wim][sea];			
//            delay = f.enter[ii] * df + h.geneticprogress - p_fixed;			//delay at start of stage until optimal time to enter
//            for(i = WPR; i >= 2; i--) f.heifer[i] = f.heifer[i-1] * (float)df;
//            f.heifer[1] = f.enter[ii] * (float)df + (float)h.geneticprogress;	//best decision one stage in future
//            p_sell1 = h.f_sel[lac][bwt][wim][sea+w] * h.invcullpct;
//            buy = h.f_rev_o[lac][cur][bwt][wim][sea+w]- p_heifer + h.p_calf - p_fixed
//                    + pid * (- h.p_loss + p_sell1 + f.heifer[1]) * df
//                    + (1 - pid) * f.cow_f[lac][cur][bwt][rep][inv][wim+1][ii] * df;
//            //	printf("  [%.2f %.2f %.2f]  %.0f",fut->cow_o[nxt][1][2][41][ii],fut->cow[nxt][1][2][42][ii],fut->cow[nxt][1][2][43][ii],buy);
//    //		if(cur == 1) printf("\n");
//    //		printf("\nbuy cnt:%d cur:%2d sea+w:%d",counter,cur,sea+w);
//    //		printf(" 1:%f",fut->pnxt[0][0][0][cur]);
//    //		printf(" 2:%6.2f",fut->rev_o[cur][1][1][sea+w]);
//    //		printf(" 3:%f",pid);
//    //		printf(" 4:%.2f",p_sell1);
//    //		printf(" 5:%.2f",fut->heifer[1]);
//            //	printf(" 8:%.4f",df);
//            //	printf(" 7:%.2f",fut->cow_o[cur][1][2][1][ii]-fut->cow_o[cur][1][2][1][jj]);
//            //	printf(" 8:%.2f",fut->cow_o[cur][1][2][2][ii]-fut->cow_o[cur][1][2][2][jj]);
//            //	printf(" 9:%.2f",fut->cow_o[cur][1][2][3][ii]-fut->cow_o[cur][1][2][3][jj]);
//    //		printf(" ii:%.2f",fut->cow_o[cur][1][2][1][ii]);
//    //		printf(" jj:%.2f",fut->cow_o[cur][1][2][1][jj]);
//    //		printf(" 9:%.2f",fut->cow_o[cur][1][2][1][ii]-fut->cow_o[cur][1][2][1][jj]);
//    //		printf(" 0:%.2f",buy);
//
//            if(calc >= 105 - h.delayed) buy = delay - 0.01;
//            if(h.enter[sea+w] == 1) delay = buy - 0.01;	//always buy heifer: delay < buy
//            if(h.enter[sea+w] == 0) buy = delay - 0.01;	//always delay heifer: buy < delay
//            if(counter == 53) {									//forced buy: prevents early convergence with empty herd
//                hlp = 0; for(i = 53; i >= 1; i--) hlp = hlp + enter[i];
//                if(hlp == 0) delay = buy - 0.01;			    //forced buy
//            }
//            f.heifer[0] = U.maxf(buy,delay);
//
////printf("\n--- cnt:%d  buy:%.2f  delay:%.2f  fut->heifer0:%.2f",counter,buy,delay,fut->heifer0);
////if(counter == 316) stop(0);
////stop(0);
//
//            f.enter[jj] = f.heifer[0];
//
//    //	printf("\n\n\n enter:%d %d heifer[1]:%.2f heifer0:%.2f fut->enter[ii]:%.2f  buy:%f  delay:%f  buy-delay:%f\n",
//    //		h.enter[sea+w],sea,heifer[1],fut->heifer0,fut->enter[ii],buy,delay,buy-delay);
//    //	printf("  [%.2f %.2f %.2f]",fut->cow[8][1][2][41][ii],fut->cow[8][1][2][42][ii],fut->cow[8][1][2][43][ii]);
//    //	stop(0);
//
//            if(buy < delay) enter[sea+w] = 0; else enter[sea+w] = 1;	//array to avoid early convergence
//            if(h.calc_ful == 1) if(buy < delay) f.pol_enter[sea+w] = 0; else f.pol_enter[sea+w] = 1;
//    //	if(bio->all == 1) printf("\n    BIO sea+w:%d ",sea+w); for(i = 1; i <= SEA; i++) printf("%d",bio->enter[i+w]);
//    //	if(bio->all == 1) printf("\n    POL sea+w:%d ",sea+w); for(i = 1; i <= SEA; i++) printf("%d",ful->pol_enter[i+w]);
//
//    //	fut->fresh[0][0][sea+w] = heifer1;old
//            if(calc >= 1) h.heifer[106-calc] = f.heifer[1];
//            if(calc == 104) h.heifer[1] = f.heifer[0] * df;
//
////            if(h.errorcheck == 1)//=============================================
////            {
////                    //err->heifer0[sea+w] = fut->heifer[0];
////                    //err->heifer1[sea+w] = fut->heifer[1];
////            }//====================================================================
//
//            //***************************************************************************************************
//            //FRESH COWS: maximum first 6 weeks of lactation (various levels on INV)
//            //***************************************************************************************************
//            for(lac = 1; lac <= h.maxlac; lac++) {
//                for(cur = 0; cur <= h.maxcur; cur++) {
//                    for(bwt = 0; bwt <= h.maxbwt; bwt++) {
//                        for(rep = 0; rep <= h.maxrep; rep++) {
//                            for(inv = 0; inv <= h.maxinv; inv++) {
//                                for(wim = 1; wim <= WFR; wim++)	{				//1 to maximum weeks fresh (=6)
//                                    pid = h.f_pid_f[lac][inv][wim][sea];
//                                    p_sell0 = h.f_sel[lac][bwt][wim][sea+w];			//salvage value start of stage
//                                    p_sell2 = h.f_sel[lac][bwt][wim][sea+w];			//salvage value at end of stage
//                                    p_sell1 = p_sell2 * h.invcullpct;				//salvage value involuntary cull
//                                    if(calc >= 1) h.fresh[lac][cur][bwt][rep][inv][106-calc] = f.cow_f[lac][cur][bwt][rep][inv][2][ii];	//2nd week
//
//                                    serv = -X8;
//                                    keep = h.f_rev_o[lac][cur][bwt][wim][sea+w] - p_fixed + pid * (- h.p_loss + p_sell1 + f.heifer[1]) * df;
//                                    if(wim == 1) keep += h.p_calf;		
//                                    if(wim < WFR) 
//                                        keep += (1 - pid) * f.cow_f[lac][cur][bwt][rep][inv][wim+1][ii] * df;
//                                    else
//                                        keep += (1 - pid) * f.cow_o[lac][cur][bwt][rep][wim+1][ii] * df;
//                                    repl = p_sell0 + f.heifer[0];
//                                    if(wim == 1) repl += h.p_calf;
//                                    //###########################################################################;
//                                    //policy: 0=quick, 1=optimal, 2=keepall, 3=keepprg
//                                    if(policy == 013) f.cow_f[lac][cur][bwt][rep][inv][wim][jj] = (float)U.maxd(repl,keep);
//                                    if(policy ==   2) f.cow_f[lac][cur][bwt][rep][inv][wim][jj] = (float)keep;
//                                    if(h.calc_ful == 1) { //----------------------------------------------------
//                                        if(policy == 013) f.pol_f[lac][cur][bwt][rep][inv][wim][sea+w] = (byte)U.max2u(keep,repl,1,0);
//                                        if(policy ==   2) f.pol_f[lac][cur][bwt][rep][inv][wim][sea+w] = 1;
//                                        f.rpo_f[lac][cur][bwt][rep][inv][wim][sea+w] = (float)(keep - repl);
//                                    }//--------------------------------------------------------------------------
//                                }//wim
//                            }//inv
//                        }//rep
//                    }//bwt
//                }//cur
//            }//lac
//            //*********************************************************************************************************
//            //*** OPEN COWS: after fresh period
//            //*********************************************************************************************************
//            wpr = 0;
//            for(lac = 1; lac <= h.maxlac; lac++) {
//                for(cur = 0; cur <= h.maxcur; cur++) {
//                    for(bwt = 0; bwt <= h.maxbwt; bwt++) {						
//                        for(rep = 0; rep <= h.maxrep; rep++) {
//                            for(wim = WFR+1; wim <= WIM; wim++) {
//                                pid = h.f_pid[lac][wim][sea];
//                                p_sell0 = h.f_sel[lac][bwt][wim][sea+w];							//salvage value start of stage
//                                p_sell2 = h.f_sel[lac][bwt][wim][sea+w];							//salvage value at end of stage
//                                p_sell1 = p_sell2 * h.invcullpct;								//salvage value involuntary cull
//
//                                if(wim > h.maxwim) {
//                                    //an rpo is calculated for 1 to WIM: allows the calculation of the value of pregnancy
//                                    //it is also a clean way of dealing with cows aborting after h.maxwim and becoming open again
//                                    //--> cow should be culled immediately
//                                    keep = serv = -X8;
//
//                                    if(h.errorcheck == 1)//===================================================
//                                    {
//                                        flag = 0;
//                                    }//==========================================================================
//                                    System.out.printf("\n wim > hrd-maxwim"); U.stop("1");
//                                }
//                                else if(wim == h.maxwim) {			//last week open
//                                    serv = -X8;
//                                    keep = h.f_rev_o[lac][cur][bwt][wim][sea+w] - p_fixed
//                                           + (1 - pid) * (p_sell2 + f.heifer[1]) * df					//end of stage (voluntary cull)
//                                           + pid * (- h.p_loss + p_sell1 + f.heifer[1]) * df;				//end of stage (involuntary cull)
////                                        if(h.errorcheck == 1) if(cur == 0 && bwt == 0 && rep == 0)//==============
////                                        {
////                                            flag = 1;
////                                            err->keep_o[lac][wim][sea+w] = keep;
////                                            err->curkeep_o[lac][wim][sea+w] = fut->rev_o[lac][cur][bwt][wim][sea+w] - p_fixed;
////                                            err->futkeep_o[lac][wim][sea+w] = ((1 - pid) * (p_sell2 + fut->heifer[1]) + pid * (- p_loss + p_sell1 + fut->heifer[1])) * df;
////                                        }//==========================================================================
//                                }
//                                else {
//                                    serv = -X8;
//                                    keep = h.f_rev_o[lac][cur][bwt][wim][sea+w] - p_fixed					//do not try to breed
//                                            + pid * (- h.p_loss + p_sell1 + f.heifer[1]) * df
//                                            + (1 - pid) * f.cow_o[lac][cur][bwt][rep][wim+1][ii] * df;
//                                    if(wim == 1) keep += h.p_calf;
//
////                                        if(h.errorcheck == 1) if(cur == 0 && bwt == 0 && rep == 0)//==============
////                                        {
////                                            flag = 3;
////                                            err->keep_o[lac][wim][sea+w] = keep;
////                                            err->curkeep_o[lac][wim][sea+w] = fut->rev_o[lac][cur][bwt][wim][sea+w] - p_fixed;
////                                            if(wim == 1) err->curkeep_o[lac][wim][sea+w] += p_calf;
////                                            err->futkeep_o[lac][wim][sea+w] = (pid * (- p_loss + p_sell1 + fut->heifer[1]) + (1 - pid) * fut->cow_o[lac][cur][bwt][rep][wim+1][ii]) * df;
////                                        }//==========================================================================
//
//                                    if(wim >= h.minvwp[lac] && wim <= h.maxlbw[lac]) {					//breeding opportunity at start of week (if estrus detected)
//                                        ppreg = h.f_servrate[cur][wim][sea] * h.f_concrate[lac][rep][wim][sea];
//                                        //assume that rev first week after breeding is the same for open and pregnant cows
//                                        serv = h.f_rev_o[lac][cur][bwt][wim][sea+w] - p_fixed - h.reprocost - h.f_servrate[cur][wim][sea] * breedcost
//                                             + pid * (- h.p_loss + p_sell1 + f.heifer[1]) * df
//                                             + (1 - pid) * (1 - ppreg) * f.cow_o[lac][cur][bwt][rep][wim+1][ii] * df
//                                             + (1 - pid) * ppreg * f.cow_x[lac][cur][bwt][wpr+1][wim+1][ii] * df;	//2nd week of pregnancy!
//                                        if(wim == 1) serv += h.p_calf;
//
////                                            if(h.errorcheck == 1) if(cur == 0 && bwt == 0 && rep == 0)//==========
////                                            {
////                                                flag = 2;
////                                                err->serv_o[lac][wim][sea+w] = serv;
////                                                err->curserv_o[lac][wim][sea+w] = fut->rev_o[lac][cur][bwt][wim][sea+w] 
////                                                        - p_fixed - h.reprocost - fut->servrate[cur][wim][sea] * breedcost;
////                                                if(wim == 1) err->curserv_o[lac][wim][sea+w] += p_calf;
////                                                err->futserv_o[lac][wim][sea+w] =
////                                                        + pid * (- p_loss + p_sell1 + fut->heifer[1])
////                                                        + (1 - pid) * (1 - ppreg) * fut->cow_o[lac][cur][bwt][rep][wim+1][ii]
////                                                        + (1 - pid) * ppreg * fut->cow_x[lac][cur][bwt][wim+1][ii] * df;
////                                                        //replaced ftx->cow_x[cur][lac][wim+1][1][ii] because not available when h.policy  == 0
////                                            }//======================================================================
//                                        //constraint: always or never serv
//                                        if(h.serve[sea+w] == 1) keep = serv - 0.01;						//always serv (RPO ok)
//                                        if(h.serve[sea+w] == 0) serv = keep - 0.01;						//never serv (RPO ok)
//                                        if(wim < h.minvwp[lac] + h.sureb[lac]) keep = serv - 0.01;	//always serv (RPO ok)
//                                        //end: constraint
//
////                                            if(h.calc_ful == 1)//-------------------------------------------------
////                                            {//calculate insematination value (assume estrus detected)
////                                                    pconc = fut->concrate[lac][cur][wim][sea];
////                                                    breed = fut->rev_o[lac][cur][bwt][wim][sea+w] - p_fixed - h.reprocost - breedcost
////                                                            + pid * (- p_loss + p_sell1 + fut->heifer[1]) * df
////                                                            + (1 - pid) * (1 - pconc) * fut->cow_o[lac][cur][bwt][rep][wim+1][ii] * df
////                                                            + (1 - pid) * pconc * fut->cow_x[lac][cur][bwt][wim+1][ii] * df;	//2nd week of pregnancy!
////                                                    if(wim == 1) breed += p_calf;
////                                            }//----------------------------------------------------------------------
//                                    }
//                                }//else
//                                repl = p_sell0 + f.heifer[0];
//                                if(wim == 1) repl += h.p_calf;
//
////                                    if(h.errorcheck == 1) if(cur == 0 && bwt == 0 && rep == 0)//==================
////                                    {
////                                            flag = 5;
////                                            err->repl_o[lac][wim][sea+w] = repl;
////                                            err->pind[lac][wim][sea+w] = pid;
////                                    }//==============================================================================
//                                calculations++;
//                                //###############################################################################;
////                                    if(h.errorcheck == 1) if(cur == 0 && bwt == 0 && rep == 0)//==================
////                                    {	// error checking
////                                        if((fabs(err->keep_o[lac][wim][sea+w] - err->curkeep_o[lac][wim][sea+w] - err->futkeep_o[lac][wim][sea+w]) > 0.01) 
////                                        ||( fabs(err->serv_o[lac][wim][sea+w] - err->curserv_o[lac][wim][sea+w] - err->futserv_o[lac][wim][sea+w]) > 0.01))
////                                        {
////                                            printf("\n");
////                                            printf("\n flag:%d calc:%d lac:%d wim:%d sea+w:%d",flag,calc,lac,wim,sea+w);
////                                            printf("\n minvwp      %d",h.minvwp[lac]);
////                                            printf("\n maxlbw      %d",h.maxlbw[lac]);
////                                            printf("\n dailymilk_o %f",fut->dailymilk_o[lac][cur][wim][sea]);
////                                            printf("\n keep_o      %f",err->keep_o[lac][wim][sea+w]);
////                                            printf("\n-curkeep_o   %f",err->curkeep_o[lac][wim][sea+w]);
////                                            printf("\n-futkeep_o   %f",err->futkeep_o[lac][wim][sea+w]);
////                                            printf("\n=error:      %f",err->keep_o[lac][wim][sea+w] - err->curkeep_o[lac][wim][sea+w] - err->futkeep_o[lac][wim][sea+w]);
////                                            printf("\n serv_o      %f",err->serv_o[lac][wim][sea+w]);
////                                            printf("\n-curserv_o   %f",err->curserv_o[lac][wim][sea+w]);
////                                            printf("\n-futserv_o   %f",err->futserv_o[lac][wim][sea+w]);
////                                            printf("\n=error:    %  f",err->serv_o[lac][wim][sea+w] - err->curserv_o[lac][wim][sea+w] - err->futserv_o[lac][wim][sea+w]);	
////                                            printf("^");
////                                    //	stop(0);
////                                        }
////                                    }//==============================================================================
//
//                                if(sea == 1) prev_yield = h.f_dailymilk_o[lac][cur][wim-1][SEA];	
//                                else prev_yield = h.f_dailymilk_o[lac][cur][wim-1][sea-1];
//                                if(sea == 1) prev_rev = h.f_rev_o[lac][cur][bwt][wim-1][SEA] / STAGE;	
//                                else prev_rev = h.f_rev_o[lac][cur][bwt][wim-1][sea-1] / STAGE;
//                                if((prev_yield < h.cullmilk && wim >= 10)					//too low milk production
//                                || (prev_rev < h.cullmargin && wim >= 10)) {					//too low margin										//policy: replace
//                                    f.cow_o[lac][cur][bwt][rep][wim][jj] = (float)repl;
////                                        if(h.calc_ful == 1)//-----------------------------------------------------
////                                        {
////                                            ful->pol_o[lac][cur][wim][bwt][rep][sea+w] = -2;
////                                            ful->rpo_o[lac][cur][bwt][rep][wim][sea+w] = max2(serv,keep) - repl;
////                                            printf("\nopen %d lac:%d cur:%d bwt:%d wim:%d rep:%d sea:%d",counter,lac,cur,bwt,wim,rep,sea);
////                                            printf(" prev_yield:%.1f <? cullyield:%.1f",prev_yield,h.cullmilk);
////                                            printf(" prev_rev:%.1f <? cullmargin:%.1f",prev_rev,h.cullmargin);
////                                            stop(0);
////                                    }//--------------------------------------------------------------------------
//                                }
//                                //policy: 0=quick, 1=optimal, 2=keepall, 3=keepprg
//                                if(policy == 013) f.cow_o[lac][cur][bwt][rep][wim][jj] = (float)U.maxd(serv,keep,repl);
//                                if(policy ==   2) f.cow_o[lac][cur][bwt][rep][wim][jj] = (float)U.maxd(serv,keep);
////                                    if(h.calc_ful == 1) //--------------------------------------------------------
////                                    {
////                                        if(policy == 013) ful->pol_o[lac][cur][bwt][rep][wim][sea+w] = max3u(repl,keep,serv,0,1,2);
////                                        if(policy ==   2) ful->pol_o[lac][cur][bwt][rep][wim][sea+w] = max2u(keep,serv,1,2);
////                                        ful->rpo_o[lac][cur][bwt][rep][wim][sea+w] = max2(serv,keep) - repl;
////                                        if(wim >= h.minvwp[lac] && wim <= h.maxlbw[lac]) ful->ins[lac][cur][bwt][rep][wim][sea+w] = breed - keep;
////                                    }//------------------------------------------------------------------------------
//                            }//wim
//                        }//rep
//                    }//bwt
//                }//cur
//            }//lac
//            //***************************************************************************************************
//            //*** PREGNANT COWS
//            //***************************************************************************************************
////            if(h.policy == 0) {
////                //quick calculations: save future cashflows of last 40 weeks of pregnant cows
////                //- ignore abortion and involuntary culling (or let them take place at say wpr == 10
////                //- keep all pregnant cows
////                //- qck->fresh[WPR] is the cashflow WPR weeks from now from a cow that gets pregnant today  
////                //- slo->[lac][cur][bwt][wim][wpr], so rep should be ignored for qck to be the same as slo
////                //- it does not seem a lot faster than slo, because the amount of "for" iterations is the same
////                //- for(wim) and for(ni) only matters for fut->pinv[wim][ni], so if these were excluded it would be a lot faster
////                //- we need an array of [sea] to store the qck->fresh[1]
////                //-  qck->fresh[lac][cur][bwt][wim][sea]
////                //-  qck->fresh[lac][cur][bwt][wim][sea40] * power(df1,WPR)	//discount 40 weeks into the future
////                //		sea40 = the season 40 weeks from today (should not be overwritten atleast 40 weeks until qck->fresh[WPR] is read from the correct position in the array
////                if(h.maxinv >= 1) {
////                        minwim = h.minvwp[lac];
////                        maxwim = h.maxlbw[lac];
////                        minni = 0;
////                        maxni = h.maxinv;
////                }
////                else
////                {
////                        minwim = maxwim = minni = maxni = 0;
////                }
////
////
////                printf("\n need to add qck->cumrev_x[lac][cur][bwt][wim][sea+w]");
////
////                for(lac = 1; lac <= h.maxlac; lac++)		//store future cash flows to be used 40 weeks from today
////                {
////                        for(cur = 0; cur <= h.maxcur; cur++)
////                                for(bwt = 0; bwt <= h.maxbwt; bwt++)
////                                        for(wim = minwim; wim <= maxwim; wim++)							//only needed when wim affects involuntary culling
////                                                for(nc = 0; nc <= h.maxcur; nc++)						//cur depends on milkyield in the previous lactation 
////                                                        for(nb = 0; nb <= h.maxbwt; nb++)					//bwt depends on bodyweight in the previous lactation (0.78 regression?)
////                                                                for(ni = minni; ni <= maxni; ni++)				//inv depends on days open in previous lactation (wim)
////                                                                {
////                                                                        if(lac <= h.maxlac - 1) 
////                                                                                qck->fresh[lac][cur][bwt][wim][sea] += fut->pcur[lac][cur][wim][nc] * fut->pbwt[bwt][nb] * fut->pinv[wim][ni] * fut->cow_f[lac+1][nc][nb][0][ni][1][ii];
////                                                                        else if(lac == h.maxlac && h.continuelac == 1) 
////                                                                                qck->fresh[lac][cur][bwt][wim][sea] += fut->pcur[lac][cur][wim][nc] * fut->pbwt[bwt][nb] * fut->pinv[wim][ni] * fut->cow_f[lac][nc][nb][0][ni][1][ii];
////                                                                        else // if(lac == h.maxlac && h.continuelac == 0)
////                                                                                qck->fresh[lac][cur][bwt][wim][sea] += fut->pcur[lac][cur][wim][nc] * fut->pbwt[bwt][nb] * fut->pinv[wim][ni] * (p_sell2 + fut->heifer[1]);
////                                                                }	//discouting happens in the next step
////                }
////                for(lac = 1; lac <= h.maxlac; lac++)		
////                {
////                        for(cur = 0; cur <= h.maxcur; cur++)
////                                for(bwt = 0; bwt <= h.maxbwt; bwt++)
////                                        for(wim = WFR+1; wim <= WIM; wim++)
////                                                if(wim - 2 >= h.minvwp[lac] && wim - 2 <= h.maxlbw[lac])
////                                                {
////                                                        sea40 = sea - WPR; if(sea40 < 1) sea40 = SEA - (WPR - sea);
////                                                        fut->cow_x[lac][cur][bwt][wim][jj] = qck->fresh[lac][cur][bwt][wim][sea40] * pow_(df,(double)(WPR-1));
////                                                }
////                }
////            }
//            //###############################################################################################;
////            else if(h.policy != 0) {	//slo-> calculations: DP decisions for every pregnant state
//            if(h.policy != 0) {
//                for(lac = 1; lac <= h.maxlac; lac++) {
//                    for(cur = 0; cur <= h.maxcur; cur++) {
//                        for(bwt = 0; bwt <= h.maxbwt; bwt++) {						
//                            for(wim = WFR+1; wim <= WIM; wim++) {
//                                for(wpr = 1; wpr <= WPR; wpr++) if(wim - wpr >= h.minvwp[lac] && wim - wpr <= h.maxlbw[lac]) {	//if(wim - wpr <= h.maxlbw[lac])
//                                    abort = h.abort[wpr];											//risk of abortion at end of month 1 to 8
//                                    pid = h.f_pid[lac][wim][sea] * h.cullrrpreg;
//
////                                    if(h.errorcheck == 1)//===================================================
////                                    {	
////                                            err->abort[wpr][sea+w] = abort;
////                                    }//==========================================================================
//                                    if(wpr < WPR) {														//pregnant, not calving or abortion
//                                        keep = h.s_rev_x[lac][cur][bwt][wpr][wim][sea+w] - p_fixed
//                                                + pid * (- h.p_loss + p_sell1 + f.heifer[1]) * df
//                                                + (1 - pid) * (1 - abort) * f.cow_x[lac][cur][bwt][wpr+1][wim+1][ii] * df
//                                                + (1 - pid) * abort * f.cow_o[lac][cur][bwt][0][wim+1][ii] * df;	//rep == 0 is the default
////                                        if(h.errorcheck == 1) if(cur == 0 && bwt == 0)//======================
////                                        {
////                                                flag = 10;
////                                                err->curkeep_x[lac][wpr][wim][sea+w] = slo->rev_x[lac][cur][bwt][wpr][wim][sea+w] - p_fixed;
////                                                err->futkeep_x[lac][wpr][wim][sea+w] = pid * (- p_loss + p_sell1 + fut->heifer[1]) * df;
////                                                err->futkeep_x[lac][wpr][wim][sea+w] += (1 - pid) * ((1 - abort) * slo->cow_x[lac][cur][bwt][wpr+1][wim+1][ii] + abort * fut->cow_o[lac][cur][bwt][0][wim+1][ii]) * df;
////                                        }//======================================================================
//                                    }	
//                                    else if(wpr == WPR && lac <= h.maxlac - 1)						//pregnant, calving
//                                    {
//                                        keep = h.s_rev_x[lac][cur][bwt][wpr][wim][sea+w] - p_fixed
//                                                + pid * (- h.p_loss + p_sell1 + f.heifer[1]) * df;
//                                        for(nc = 0; nc <= h.maxcur; nc++)                                               //cur depends on milkyield in the previous lactation 
//                                            for(nb = 0; nb <= h.maxbwt; nb++)						//bwt depends on bodyweight in the previous lactation (0.78 regression?)
//                                                for(ni = 0; ni <= h.maxinv; ni++)					//inv depends on days open in previous lactation
//                                                    keep += h.f_pcur[cur][lac][wim][nc] * h.f_pbwt[bwt][nb] * h.f_pinv[wim][ni]
//                                                            * (1 - pid) * f.cow_f[lac+1][nc][nb][0][ni][1][ii] * df;
//                                                                                                                                                                        //rep == 0, currently no effect on reproduction in next lactation
////                                        if(h.errorcheck == 1) if(cur == 0 && bwt == 0)//======================
////                                        {
////                                                flag = 21;
////                                                err->curkeep_x[lac][wpr][wim][sea+w] = slo->rev_x[lac][cur][bwt][wpr][wim][sea+w] - p_fixed;
////                                                err->futkeep_x[lac][wpr][wim][sea+w] = pid * (- p_loss + p_sell1 + fut->heifer[1]) * df;
////                                                for(nc = 0; nc <= h.maxcur; nc++)						//cur depends on milkyield in the previous lactation 
////                                                        for(nb = 0; nb <= h.maxbwt; nb++)					//bwt depends on bodyweight in the previous lactation (0.78 regression?)
////                                                                for(ni = 0; ni <= h.maxinv; ni++)				//inv depends on days open in previous lactation
////                                                                        err->futkeep_x[lac][wpr][wim][sea+w]  += fut->pcur[cur][lac][wim][nc] * fut->pbwt[bwt][nb] * fut->pinv[wim][ni]
////                                                                                * (1 - pid) * fut->cow_f[lac+1][nc][nb][0][ni][1][ii] * df;
////                                        }//======================================================================
//                                    }
//                                    else if(wpr == WPR && lac == h.maxlac && h.continuelac == 1) {	//continue after maxlac in the same lactation
//                                    	//this formulation does not cull cows at end of LAC:
//                                        keep = h.s_rev_x[lac][cur][bwt][wpr][wim][sea+w] - p_fixed
//                                                + pid * (- h.p_loss + p_sell1 + f.heifer[1]) * df;
//                                        for(nc = 0; nc <= h.maxcur; nc++)					//cur depends on milkyield in the previous lactation 
//                                            for(nb = 0; nb <= h.maxbwt; nb++)					//bwt depends on bodyweight in the previous lactation (0.78 regression?)
//                                                for(ni = 0; ni <= h.maxinv; ni++)				//inv depends on days open in previous lactation
//                                                    keep += h.f_pcur[lac][cur][wim][nc] * h.f_pbwt[bwt][nb] * h.f_pinv[wim][ni]
//                                                            * (1 - pid) * f.cow_f[lac][nc][nb][0][ni][1][ii] * df;//remain in lac
//
////                                        if(h.errorcheck == 1) if(cur == 0 && bwt == 0)//======================
////                                        {
////                                                flag = 32;
////                                                err->curkeep_x[lac][wpr][wim][sea+w] = slo->rev_x[lac][cur][bwt][wpr][wim][sea+w] - p_fixed;
////                                                err->futkeep_x[lac][wpr][wim][sea+w] = pid * (- p_loss + p_sell1 + fut->heifer[1]) * df;
////                                                for(nc = 0; nc <= h.maxcur; nc++)						//cur depends on milkyield in the previous lactation 
////                                                        for(nb = 0; nb <= h.maxbwt; nb++)					//bwt depends on bodyweight in the previous lactation (0.78 regression?)
////                                                                for(ni = 0; ni <= h.maxinv; ni++)				//inv depends on days open in previous lactation
////                                                                        err->futkeep_x[lac][wpr][wim][sea+w]  += fut->pcur[cur][lac][wim][nc] * fut->pbwt[bwt][nb] * fut->pinv[wim][ni]
////                                                                                * (1 - pid) * fut->cow_f[lac][nc][nb][0][ni][1][ii] * df;
////                                        }//======================================================================
//                                    }
//                                    else if(wpr == WPR && lac == h.maxlac && h.continuelac == 0) {              //end of last lactation
//                                    	//this formulation culls cows at end of LAC:
//                                        flag = 33;
//                                        keep = h.s_rev_x[lac][cur][bwt][wpr][wim][sea+w] - p_fixed
//                                               + (1 - pid) * (p_sell2 + f.heifer[1]) * df			//end of stage (voluntary cull)
//                                               + pid * (- h.p_loss + p_sell1 + f.heifer[1]) * df;		//end of stage (involuntary cull)
////                                        if(h.errorcheck == 1) if(cur == 8) { //==================================
////                                            flag = 34;
////                                            err->curkeep_x[lac][wpr][wim][sea+w] = slo->rev_x[lac][cur][bwt][wpr][wim][sea+w] - p_fixed;
////                                            err->futkeep_x[lac][wpr][wim][sea+w] = ((1 - pid) * (p_sell2 + fut->heifer[1]) + pid * (- p_loss + p_sell1 + fut->heifer[1])) * df;
////                                        }//======================================================================
//                                    }
//                                    else { System.out.printf("\nPREG error"); U.stop("1"); }
//                                    repl = p_sell0 + f.heifer[0];
//                                    calculations++;
////                                    if(h.errorcheck == 1) if(cur == 8)//======================================
////                                    {
////                                            err->keep_x[lac][wim][wpr][sea+w] = keep;
////                                            err->repl_x[lac][wim][wpr][sea+w] = repl;
////                                    }//==========================================================================
//
//                                    //###########################################################################;
//                                    //policy: 0=quick, 1=optimal, 2=keepall, 3=keepprg
//                                    if(policy == 1) f.cow_x[lac][cur][bwt][wpr][wim][jj] = (float)U.maxd(repl,keep);
//                                    if(policy == 23) f.cow_x[lac][cur][bwt][wpr][wim][jj] = (float)keep;
//                            //        if(wpr == 2) f.cow_x[lac][cur][bwt][wpr][wim][jj] = f.cow_x[lac][cur][bwt][wpr][wim][jj];
//                                    if(h.calc_ful == 1) { //----------------------------------------------------
//                                        if(policy == 1)  f.pol_x[lac][cur][bwt][wpr][wim][sea+w] = (byte)U.max2u(keep,repl,1,0);
//                                        if(policy == 23) f.pol_x[lac][cur][bwt][wpr][wim][sea+w] = 1;
//                                        f.rpo_x[lac][cur][bwt][wpr][wim][sea+w] = (float)(keep - repl);
//
//                                        if(wpr == 1) 
//                                            for(rep = 0; rep <= h.maxrep; rep++)
//                                                f.prg1[lac][cur][bwt][rep][wim][sea+w] = f.cow_x[lac][cur][bwt][1][wim][jj] - f.cow_o[lac][cur][bwt][rep][wim][jj];
//                                    }//--------------------------------------------------------------------------
//                                }//wim
//                            }//wpr
//                        }//bwt
//                    }//cur
//                }//lac
////                if(counter == 1) {
////                    write_msg_file(4,(double)lac/h.maxlac*100);
////                }
//            }//quick == false
////*** DP BOTTOM *************************************************************************
//            fut_out = f.heifer[0];					//future revenue begin of stage
//            for(i = SEA*2; i >= 2; i--) f.cash[i] = f.cash[i-1];
//            f.cash[1] = (float)(fut_out - df * fut_in);
//            fut_heiin = fut_heiout;
//            fut_heiout = f.cow_f[1][0][0][0][0][1][jj];
//
//    //	printf("\n fut_in      :%f = old fut->heifer0",fut_in);
//    //	printf("\n fut_out     :%f = new fut->heifer0",fut_out);
//    //	printf("\n fut_cash[1] :%f = fut_out - df * fut_in",fut->cash[1]);
//    //	stop(0);
//
//            // accelerate convergence ////////////////////////////////////////////////////////
//            // method: fit exponential distribution; CHESS-RO, Ruud Huirne, Wageningen University
//            if(sea == 1 && counter > 104 && df < 1.0 && calc == -1 && heif_itt <= 6) heif[++heif_cnt] = f.heifer[0];
//            if(sea == 1 && heif_cnt == 3 &&             calc == -1 && heif_itt <= 6)	//set heif_itt for the # accelerations
//            {
//            //	printf("\n [%d] %.2f %.2f %.2f",heif_cnt,heif[1],heif[2],heif[3]);
//                System.out.printf(" A:");
//                heif[0] = estimate_heifer0(heif[1],heif[2],heif[3]);
//                heif[1] = heif[2] = heif[3] = 0.0;
//                heif_cnt = 0;
//                heif_itt++;
//                if(heif[0] != -X8 && U.abs(heif[0] - fut_out) > 20) {
//                    System.out.printf("%.0f",heif[0]);
//                    diff = heif[0] - fut_out;
//                    fut_out += diff;
//                    f.enter[jj] += diff;
//                    f.heifer[0] += diff;
//                    for(lac = 1; lac <= h.maxlac; lac++)
//                        for(cur = 0; cur <= h.maxcur; cur++)
//                            for(bwt = 0; bwt <= h.maxbwt; bwt++)
//                                for(wim = 1; wim <= WIM; wim++) {
//                                    for(rep = 0; rep <= h.maxrep; rep++) 
//                                        for(inv = 0; inv <= h.maxinv; inv++) 
//                                            f.cow_f[lac][cur][bwt][rep][inv][wim][jj] += diff;	//-fut->heifer + diff?
//
//                                    for(rep = 0; rep <= h.maxrep; rep++) 
//                                        f.cow_o[lac][cur][bwt][rep][wim][jj] += diff;	//-fut->heifer + diff?
//                                }
//                                if(h.policy >= 1)
//                                    for(wpr = 1; wpr <= WPR; wpr++) if(wim - wpr >= 1 && wim - wpr <= LBW)
//                                        f.cow_x[lac][cur][bwt][wpr][wim][jj] += diff;	//-fut->heifer + diff?
//                }
//            }
//            //note at convergence when df > 0: 
//            //   df = 1 - (fut->cash[1] / fut_out);
//            //   fut->cash[1] = fut_out - df * fut_out 
//            //   fut->cash[1] = (1 - df) * fut_out;
//            //   fut->cash[1] = fut_out - fut_in;
//            //---------------------------------------> this is the same solution as policy iteration
//            ////////////////////////////////////////////////////////////////////////////////////////
//            //June/July 2005:
//            //Equivalent annual annuity = EAA = NPV / pvifa, see Keown et al. Foundations of finance, page 554
//            //Calculate EAA for last 12 months and compare to previous 12 months, if same: converge
//            //See 'Equivalent period costs.xls' in Meadows review
//            //fut_cash[1] can change, but the change is perhaps due to a different path the algorithm takes and 
//            //the real cash change is far in the future.  So say fut_cash is always 100 but then jumps to 101: this 
//            //may be a result of a different path with 100 cash now and 101 cash somewhere (far) in the future. Calculating
//            //EAA for the last 12 stages does not do justice to that and is not necessarily correct.
//            //EAA calculated for the same 12 stages in MARKOV_CHAIN_STATS is correct and may be different from the EAA calculated
//            //here in DP_POLICY.  Under stead-state conditions, the EAA in both the DP_POLICY and MARKOV_CHAIN_STATS should
//            //be the same.
//            cash52 = 0.0; for(i = 1; i <= SEA; i++) cash52 += f.cash[i];
//            //Because EAA in DP_POLICY is not correct, this codes does not matter anymore:
//            //pvifa = 0.0; if(counter < SEA) for(i = 1; i <= counter; i++) pvifa = pvifa + pow(df,i-1);	//present-value interest factor for an annuity
//            //else for(i = 1; i <= SEA; i++) pvifa = pvifa + pow(df,i-1);	//present-value interest factor for an annuity
//            //printf(" df=%f pvifa=%f",df,pvifa);
//
//            maxdiff = 0.0;
//            for(i = 1; i <= SEA; i++) { //if(counter - 53 >= i)
//                maxdiff += U.abs(f.cash[i] - f.cash[i+52]);
//            //	printf("\nfut_cash[%d]:%.2f  fut_cash[%d]:%.2f  maxdiff%.2f",i,fut_cash[i],i+12,fut_cash[i+12],maxdiff);
//            }
//            sumfixed -= p_fixed;
//            if(fut_out == sumfixed) maxdiff = 1.0;	//prevents early convergence when alwaysbuy == 0 
//            if(notconverged == 1) maxdiff = 0.0;	//results in convergence
//            System.out.printf("\n %d:%.0f pol:%d calc:%2d w:%2d sea:%2d fut:%.0f [%.2f] fut_hei:%.0f cash:%6.2f yr:%.2f conv:%f",
//                counter,U.ceil((double)counter/52),h.policy,calc,w,sea,fut_out,fut_out/(double)counter,fut_heiout,f.cash[1],cash52,maxdiff);
//            if(buy < delay) System.out.printf(" delay"); else System.out.printf(" buy");
////            if(sea == h.startsea) write_msg_file(4,maxdiff);
//
//    //	save data in dp.txt:
//    //	if(sea == 1)
//    //	fprintf(fp,"\n%d calc: %d w: %d sea: %d fut_out: %f cash: %f yr: %f heifer0: %f heifer1: %f maxdiff: %f",counter,calc,w,sea,fut_out,fut->cash[1],cash52,fut->heifer0,heifer[1],maxdiff);
//
//
////calc -1   --> find steady state, save all fut->fresh[]
////calc 1 to 52: last 52 weeks if price is different
//
//
//            if(calc == -1 && sea == h.startsea && maxdiff < MAXDIFF && counter >= MINCOUNTER) {	//converged
//                System.out.printf("*");
//            //	if(bio->all == 1) for(i = 1; i <= SEA*2; i++) ful->pol_enter[i] = bio->enter[i];
//                calc = 0;
//            }
//            if(calc == 52) {
//                if(h.policy == 0) {} //write_warm_file(0,jj);			//save v.warm.txt for warm start
//                if(h.policy >= 1) {} //write_warm_file(1,jj);			//save v.warm.txt for warm start
//            }
//            if(calc == 104) {		//calc == 104? or calc == 52?
//                //write_fresh_file(0,jj);	//creates file with cash flows for future lactations
//            //	older versions have code here to calculate additional cow statistics such as allowable breeding space
//            }//calc == 104
//
//
//    //	if(counter == 20000 || (counter == 200 && fut_out == sumfixed))
//            if(counter == 1000 || (U.abs(fut_heiout - fut_heiin) < MAXDIFF && counter >= 300 && maxdiff == 1.0)) {
//                if(counter == 10000) System.out.printf(" not converged");
//                else System.out.printf(" fut_hei converged");	//alternative convergence
//                //write_msg_file(5,0);
//                notconverged = 1;
//            }
//            jj = ii; if(ii == 0) ii = 1; else ii = 0;	//swap ii and jj
//    //	printf(" end do");
//	} while(calc != 104);
//	System.out.printf("\n DP_FUT done ... %.1f years to convergence  calc:%d",(double)(counter-104)/52,calc);
//	System.out.printf(" calculations:[%d]",calculations);
//	if(h.calc_ful == 1) { 
//            System.out.printf("\n policy yr 2: "); for(sea = 53; sea <= 104; sea++) System.out.printf("%d",f.pol_enter[sea]);
//            System.out.printf("\n policy yr 1: "); for(sea = 1; sea <= 52; sea++) System.out.printf("%d",f.pol_enter[sea]);
//	}
//	//write_msg_file(6,cash52);
////	stop(0);
//	//if(E) fclose(fp);
//    }
//
///*** end DP_FUT ***/

    
    
    
    
    
    
    
    
    
    
    
    
    

    
    
//    class RRepr {
//        float rpo;
//        //short pol;
//        //float fut;
//        //float random;
//        //double  dmi;     
//        //double  array[];
//        RRepr(){//constructor
//            //CowPlan.print("Repr: constructor:" + b1); //things I want to record
//            rpo = 5;  //should be rpo[sea*2]
//        //    pol = 9;  //should be pol[sea*2]
//        //    fut = 7;  //should be fut[ii]
//            counter++;
//            //U.println("" + counter);
//        //    random = (float)Math.random();
//            //dmi = -3;
//            //array = new double[6];
//            //for(int i = 0; i < 6; ++i) {
//            //    array[i] = Math.random() + i;
//                //CowPlan.print("Milk:" + array[i]);
//            //}
//            //System.out.println("Milk: array.length " + array.length);
//        }
//    }//Repr
//    
//    
//    class BBody {
//        RRepr rep[][][] = new RRepr[r1+1][r2+1][r3+1];
//        BBody(){//constructor  int r1, int r2, int r3, int r4
//            //U.println("Body: constructor");
//            //RRepr rep_[][][][] = new RRepr[r1+1][r2+1][r3+1][r4+1];
//            for(int i = 0; i <= r1; ++i) {
//                for(int j = 0; j <= r2; ++j) {
//                    for(int k = 0; k <= r3; ++k) {
//                        rep[i][j][k] = new RRepr();
//                    }
//                }
//            }
//        }
//    }//Body
//
//    class MMilk {
//        BBody bdy[][][] = new BBody[b1+1][b2+1][b3+1];
//        MMilk() {//constructor    int b1, int b2, int b3
//            //U.println("Milk: constructor");
//            //BBody bdy_[][][] = new BBody[b1+1][b2+1][b3+1];
//            for(int i = 0; i <= b1; ++i) {
//                for(int j = 0; j <= b2; ++j) {
//                    for(int k = 0; k <= b3; ++k) {
//                        bdy[i][j][k] = new BBody();//r1,r2,r3,r4);    
//                    }
//                }
//            }
//        }//
//    }//Milk 
//    
//    
//    class Bio {    
//        MMilk mlk[][][][][] = new MMilk[m1+1][m2+1][m3+1][m4+1][m5+1];
//        Bio() { //int m1, int m2, int m3, int m4, int m5
//            //MMilk mlk_[][][][][] = new MMilk[m1+1][m2+1][m3+1][m4+1][m5+1];
//            for(int i = 0; i <= m1; ++i) {
//                for(int j = 0; j <= m2; ++j) {
//                    for(int k = 0; k <= m3; ++k) {
//                        for(int l = 0; l <= m4; ++l) {
//                            for(int m = 0; m <= m5; ++m) {
//                                mlk[i][j][k][l][m] = new MMilk();//b1,b2,b3);
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }//Bio
//
////    RRepr rep[][][][] = new RRepr[r1+1][r2+1][r3+1][r4+1];
////    BBody bdy[][][] = new BBody[b1+1][b2+1][b3+1];
////    MMilk mlk[][][][][] = new MMilk[m1+1][m2+1][m3+1][m4+1][m5+1];
//    
//    
//    class Test2 {    
//        float test[][][] [][] = new float[1][1][1000] [1000][1];   //32-bit
//        Test2() {
//        }
//    }//Test2
//    
//    Test2 ts[] = new Test2[2];
//    
//    Bio opn[][][] = new Bio[MLAC+1][MWIM+1][MCYC+1];
//    Bio prg[][][] = new Bio[MLAC+1][MWIM+1][MWPR+1];
//       
    

    void pol_report(int counter, Herd h) {
        byte r;
        int count0[] = new int[20];
        int count1[] = new int[20];
        int count2[] = new int[20];
        
        //frh
        for(int wim = 1; wim <= MWFR; wim++) {  
            for(int lac = 1; lac <= MLAC; lac++) { 
                for(int val = 0; val <= MVAL; val++) {
                    for(int per = 0; per <= MPER; per++) {    
                        for(int bwt = 0; bwt <= MBWT; bwt++) {
                            for(int dmi = 0; dmi <= MDMI; dmi++) {
                                for(int fer = 0; fer <= MFER; fer++) {
                                    for(int cul = 0; cul <= MCUL; cul++) {        
                                        for(int sea = 0; sea <= MSEA*2; sea++) {        
                                            r = frh_pol[wim][lac] [val][per][bwt][dmi] [fer][cul] [sea];
                                            count0[10+r]++;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
      
        //opn
       for(int wim = MWFR+1; wim <= MWIM; wim++) {            
            for(int lac = 1; lac <= MLAC; lac++) {
                for(int val = 0; val <= MVAL; val++) {
                    for(int per = 0; per <= MPER; per++) {    
                        for(int bwt = 0; bwt <= MBWT; bwt++) {                              
                            for(int dmi = 0; dmi <= MDMI; dmi++) {  
                                for(int fer = 0; fer <= MFER; fer++) {
                                    for(int cyc = 0; cyc <= MCYC; cyc++) {
                                        for(int ins = 0; ins <= MINS; ins++) {
                                            for(int sea = 0; sea <= MSEA*2; sea++) {   
                                                r = opn_pol[wim][lac] [val][per][bwt][dmi] [fer][cyc] [sea] = 3;
                                                count1[10+r]++;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
       }
        for(int wim = MWFR+1; wim <= MWIM; wim++) {
            for(int lac = 1; lac <= MLAC; lac++) {
                for(int val = 0; val <= MVAL; val++) {
                    for(int per = 0; per <= MPER; per++) {    
                        for(int bwt = 0; bwt <= MBWT; bwt++) {
                            for(int dmi = 0; dmi <= MDMI; dmi++) {                                
                                for(int wpr = 1; wpr <= MWPR; wpr++) if(wim - wpr >= h.minvwp[lac] && wim - wpr <= h.maxlbw[lac]) {	
                                    for(int ins = 0; ins <= MINS; ins++) {
                                        for(int sea = 0; sea <= MSEA*2; sea++) {  
                                            r = prg_pol[wim][lac] [val][per][bwt][dmi] [wpr][ins] [sea];
                                            count2[10+r]++;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        //U.print("\nfrh counter="+counter); for(int i=0; i<count0.length; i++) U.print(space+count0[i]);
        //U.print("\nopn counter="+counter); for(int i=0; i<count1.length; i++) U.print(space+count1[i]);
        //U.print("\nprg counter="+counter); for(int i=0; i<count2.length; i++) U.print(space+count2[i]);        
        
    }//end: pol report

    

    Fut() {      
        U.println("Fut: constructor start");
        U.memory(1);        
        
        set_fut_arrays();
     
        U.memory(3);
        U.println("Fut: constructor end"); 
    }//constructor
    
}// class Fut     
        
            


