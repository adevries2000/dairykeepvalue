/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cowplan16package;

class Herd extends Param {
    //herd variables that are used in the dynamic program
    float bw[][][][] = new float[MWIM+1][MLAC+1][MBWT+1][MWPR+1];
    float concrate[][][][][] = new float[MWIM+1][MLAC+1][MFER+1][MINS+1][MSEA+1]; 
    float dailymilk_o[][][][][] = new float[MWIM+1][MLAC+1][MVAL+1][MPER+1][MSEA+1];
    float pcull_frh[][][][] = new float[MWIM+1][MLAC+1][MCUL+1][MSEA+1];
    float pcull[][][] = new float[MWIM+1][MLAC+1][MSEA+1];			//P(involuntary culling per week)
    float pdead_frh[][][][] = new float[MWIM+1][MLAC+1][MCUL+1][MSEA+1];
    float pdead[][][] = new float[MWIM+1][MLAC+1][MSEA+1];			//P(dead per week)
    float sel[][][][][] = new float[MWIM+1][MLAC+1][MBWT+1][MWPR+1][2*MSEA+1];
    float servrate[][][] = new float[MWIM+1][MVAL+1][MSEA+1];
    float iofc_o[][] [][][][] [] = new float[MWIM+1][MLAC+1] [MVAL+1][MPER+1][MBWT+1][MDMI+1] [2*MSEA+1];
    float iofc_p[][] [][][][] [][] = new float[MWIM+1][MLAC+1] [MVAL+1][MPER+1][MBWT+1][MDMI+1] [MWPR-MDRY+1] [2*MSEA+1];
    float iofc_d[][] [] [] = new float[MBWT+1][MDMI+1] [MWPR+1] [2*MSEA+1];
           
    float tval[][][] = new float[MVAL+1][MWPR+1] [MVAL+1];
    float tbwt[][][] = new float[MWIM+1][MBWT+1] [MBWT+1];
    float tcul[][][] = new float[MWIM+1][MWPR+1] [MCUL+1];
    float tfer[][][] = new float[MWIM+1][MWPR+1] [MFER+1];
 
    float abort[] = new float[MWPR+1];
    short calc_ful;    
    float cullmilk;
    float culliofc;
    String cullpolicy;
    float cullrrpreg;
    float deadrrpreg;
    short delayed;
    float df;
    String enterheifer[] = new String[2*MSEA+1];    
    String serve[] = new String[2*MSEA+1];    
    float geneticprogress;    
    float inseminationcost[] = new float[MINS+1];
    float invcullpct;    
    int maxlbw[] = new int[MLAC+1];
    int maxwim[] = new int[MLAC+1];
    int minvwp[] = new int[MLAC+1];
    int maxdry[] = new int[MLAC+1];
    int mindry[] = new int[MLAC+1];
    float p_calf[] = new float[MINS+1];    
    float p_cullloss;
    float p_deadloss;
    float p_heifer[] = new float[MSEA*2+1];
    float p_fixed;    
    float reprocost;
    int startsea; 
 

    //**************************************************************************
    //Calculates the probability of the next bodyweight level
    //tbwt[WIM][BWT][BWT];
    void transition_bwt(Bio b) {   
        U.println("TRANSITION_BWT started ...");            
        if(MBWT == 0) {
            for(int wim = 0; wim <= MWIM; wim++) {
                tbwt[wim][0][0] = 1;
            }
        }
        else {
            for(int wim = 0; wim <= MWIM; wim++) {            
                for(int bwt = 0; bwt <= MBWT; bwt++) {
                    for(int nxt = 0; nxt <= MBWT; nxt++) {
                        if(bwt == nxt) tbwt[wim][bwt][nxt] = 1;
                        else tbwt[wim][bwt][nxt] = 0;
                    }
                }
            }
        }
        U.println("TRANSITION_BWT done ...");
    }//end: transition_bwt

    
    //**************************************************************************
    //Calculates the probability of the next fertility level
    //tfer[WIM][DRY][FER];
    void transition_fer(Bio b) {   
        U.println("TRANSITION_FER started ...");        
        if(MFER == 0) {
            for(int wim = 0; wim <= MWIM; wim++) {
                for(int dry = 0; dry <= MWPR; dry++) {                
                    tfer[wim][dry][0] = 1;
                }
            }
        }
        else {
            for(int wim = 0; wim <= MWIM; wim++) {            
                for(int dry = 0; dry <= MWPR; dry++) {
                    for(int nxt = 0; nxt <= MFER; nxt++) {
                        tfer[wim][dry][nxt] = 1/(MFER+1);
                    }
                }
            }
        }
        U.println("TRANSITION_FER done ...");
        //U.stop("transition_fer");        
    }//end: transition_fer
    

    
    //**************************************************************************
    //Calculates the probability of the next milk weight level given the predicted milk yield in next lactation.
    //- Method see Van Arendonk[128]117.
    //- Regression and repeatability coefficients, see REPEATABILITY-CURVE.XLS
    //these are conditional distributions: 
    //  multivariable case: https://stats.stackexchange.com/questions/30588/deriving-the-conditional-distributions-of-a-multivariate-normal-distribution
    //  single variable case: https://onlinecourses.science.psu.edu/stat414/node/118
    //tval[MVAL+1][MDRY+1][MVAL+1]
    void transition_val(Bio b) {
        double	//pa_cv	= 0.0,		//coefficient of variation: see Van Arendonk[128]107
            bb,                                                    //repeatability of 305-d milk yield between lactations: same as b21[0] in BIO_PROBABILITY()
            goal305, yield305, smy_, error = 0.0, perror,
            pferror, ferror, step, mlk_funk_,
            xlm, xum = 0.0, pxlm, pxum, sd, iter, pnext;
        int wim, lac, val, nxt, nlac, lbw, dry;

        U.println("TRANSITION_VAL started ...");
        //special cases: no transition to other classes
        if(b.pa_cv == 0.0 || b.mrepeat == 1.0 || MVAL == 0) {
            U.println(" special cases:");
            U.println(" pa_cv  : " + b.pa_cv);
            U.println(" mrepeat: " + b.mrepeat);
            U.println(" VAL    : " + MVAL);      

            for(val = 0; val <= MVAL; val++) {
                for(dry = 0; dry <= MWPR; dry++) {    
                    for(nxt = 0; nxt <= MVAL; nxt++) {
                        if(val == nxt) tval[val][dry] [nxt] = 1;
                        else tval[val][dry] [nxt] = 0;
                    }
                }
            }
            U.println("TRANSITION_VAL done (a)...");
            //U.stop("transition_val");
        }

    //transition into first lactation (heifer)
    //not implemented in dp_fut()
    //	sd = hrd->pa_cv;
    //	for(nxt = 1; nxt <= hrd->maxcur; nxt++)	
    //	{
    //		if(nxt == 1) xlm = -9999; else xlm = xum;		//z-score
    //		xum = (hrd->yum[nxt] - 100) / sd;
    //		if(nxt == hrd->maxcur) xum = 9999;
    //		pxlm = normal(xlm);					//cumulative P(z-score)
    //		pxum = normal(xum);
    //		fut->pnxt[0][0][0][nxt] = (pxum - pxlm);		//P(nxt | cur)
    //	//	printf("\n nxt:%d pnxt:%f",nxt,fut->pnxt[0][0][0][nxt]);
    //		if(hrd->mrepeat == 1.0) return;
    //	}//nxt

        //transition to 2nd or greater lactations (+ Funk adjustment)
        bb = b.mrepeat;
        dry = 0;
        for(lac = 1; lac <= MLAC; lac++) {																
            //U.println("[a]");
            if(lac == MLAC) nlac = MLAC; else nlac = lac + 1; 
            for(val = 1; val <= MVAL; val++) {
                //U.print("b");
                sd = b.pa_cv * U.sqrt(1 - bb*bb);	
                if(sd == 0.0) sd = 0.001;
                for(wim = MWPR+1; wim <= MWIM; wim++) {	//normally, wim >= 41 before calving, but abortion may occur earlier
                    //U.print("c");
                					//  and also transitions cow into next lactation
                    //smy_ = smy[cur];                     //not needed here, but useful in the printf("\nDONE ..) below 
                    lbw = wim - MWPR; if(lbw >= LBW) lbw = LBW;
                    if(b.funk == 1) mlk_funk_ = b.mlk_funk[lbw]; else mlk_funk_ = 1.0;
                    goal305 = 0.0;
                    yield305 = 0.0;
                    for(nxt = 1; nxt <= MVAL; nxt++) {							//determine goal305
                        //U.print("d");
                        if(nxt == 1) xlm = -99; else xlm = xum;				//z-score
                        xum = (b.yum[nxt] - bb * (b.smy[val] - 100) - 100) / sd;
                        if(nxt == MVAL) xum = 99;
                        pxlm = U.normal(xlm);									//cumulative P(z-score)
                        pxum = U.normal(xum);
                    //fix    h.tval[wim][lac][val][nxt] = pxum - pxlm;		//fut->pnxt not affected by mlk_funk, do that in the next step if hrd->funk == 1
                        tval[val][dry][nxt] = (float)(pxum - pxlm);
                        
                    //fix    U.println("lac val wim nxt tval:" +space + lac +space+ val +space+ wim +space+ nxt+space+ h.tval[wim][lac][val][nxt]);
                        //U.println("lac val wim nxt tval:" +space + lac +space+ val +space+ wim +space+ nxt+space+ tval[val][dry][nxt]);    
                    
                        //U.print(space + f_pcur[lac][cur][wim][nxt]);
                        //U.print("==> " + nlac + space + nxt + space);
                        //U.println(space + m305[nlac][nxt][0]);
                        
//                        goal305 += h.tval[wim][lac][val][nxt] * m305[nlac][nxt][0] * mlk_funk_;	//no seasonality, current lactation
//                        yield305 += h.tval[wim][lac][val][nxt] * m305[nlac][nxt][0];

			//printf("\n**lac:%d cur:%d wim:%d sd:%.3f nxt:%d xlm:%7.2f xum:%7.2f pxlm:%.4f pxum:%.4f",lac,cur,wim,sd,nxt,xlm,xum,pxlm,pxum);
                        //printf(" yum[nxt]:%.0f pnxt:%.4f m305:%.0f goal305:%.2f",hrd->yum[nxt],fut->pnxt[cur][lac][wim][nxt],hrd->m305[nxt][nlac][0],goal305);
                    }
//                    if(funk == 1) {		//Funk adjustment factor for days open in current lactation on milk yield in next lactation
//                        //search best distribution of fut->pnxt[cur][lac][wim][nxt]  to obtain yield305 eq. goal305.
//                        smy_ = smy[cur] * mlk_funk[lbw];			//guess: see REPEATABILITY-CURVE.XLS
//                        ferror = 999999;
//                        step = 10.0;
//                        iter = 0;
//                        do {	//vary smy to obtain yield305 == goal305:
//                            pnext = 0.0;
//                            yield305 = 0.0;
//                            for(nxt = 1; nxt <= CUR; nxt++) {
//                                if(nxt == 1) xlm = -99; else xlm = xum;		//z-score
//                                xum = (yum[nxt] - b * (smy_ - 100) - 100) / sd;
//                                if(nxt == CUR) xum = 99;
//                                pxlm = U.normal(xlm);							//cumulative P(z-score)
//                                pxum = U.normal(xum);
//                                f_pcur[lac][cur][wim][nxt] = pxum - pxlm;		
//                                pnext += f_pcur[lac][cur][wim][nxt];		//check
//                                yield305 += f_pcur[lac][cur][wim][nxt] * m305[nlac][nxt][0];
//
//                            //	printf("\n* nxt:%d xlm:%7.2f xum:%7.2f smy:%.2f",nxt,xlm,xum,smy);
//                            //	printf(" yum[nxt]:%.0f pnext:%f [%f] m305:%.0f",hrd->yum[nxt],fut->pnxt[cur][lac][wim][nxt],pnext,hrd->m305[nxt][nlac][0]);
//                            //	printf(" yield305:%.4f",yield305);
//                            }
//                            pferror = ferror;								//previous absolute error
//                            ferror = U.abs(yield305 - goal305);				//current absolute error
//                            if(iter >= 1) perror = error;
//                            else if(yield305 - goal305 < 0) perror = -999999; 
//                            else perror = 999999;
//                            error = yield305 - goal305;						//used to determine "step"
//                            iter += 1.0;
//                    //	printf("\n** smy:%.2f step:%f yield305:%.0f goal305:%.0f (error:%f perror:%f)\n",smy,step,yield305,goal305,error,perror);
//                    //	stop(0);
//                            if((error > 0 && perror < 0) || (error < 0 && perror > 0)) step = step * 0.5;
//                            if(yield305 < goal305) smy_ = smy_ + step; else smy_ = smy_ - step;
//                        } while(ferror > 1.00			//if error < 1 lbs then stop
//                                && smy_ > 50			//if smy < 50 then stop
//                                && smy_ < 150			//if smy > 150 then stop
//                                && iter < 100);			//if iterations > 100 then stop
//                    //	printf("\n ferror:%f  smy:%f  erpa:%f  iter:%d",ferror,smy,cow[cw]->erpa,iter);
//                    //	printf("\nFUT_PNXT funk-adjustment: lac:%d cur:%d wim:%d funk:%f goal305:%.0f error:%.0f smy:%.2f iter:%4.0f",lac,cur,wim,funk,goal305,error,smy,iter);
//                    //	stop(0);
//                    //	if(wim == WIM) stop(0);
//                    }
//                    for(nxt = 1; nxt <= CUR; nxt++) {
//                        //printf("\n##lac:%d cur:%d wim:%d sd:%f nxt:%d pnxt:%f",lac,cur,wim,sd,nxt,fut->pnxt[cur][lac][wim][nxt]);
//                    }
                    //printf("\nFUT_PNXT DONE lac:%d cur:%d wim:%d lbw:%d b:%.3f sd:%.2f smy:%6.2f  yield305:%6.0f - goal305:%6.0f = %5.0f  iter:%4.0f\n",
                    //	lac,cur,wim,lbw,b,sd,smy,yield305,goal305,yield305-goal305,iter);
                    //if(cur == 8 && lac == 1 && wim == WIM)
                    //stop(0);
                }//wim
                for(wim = 0; wim <= MWPR; wim++) {				//first 40 weeks
                    for(nxt = 1; nxt <= MVAL; nxt++) {				//determine goal305
                    //fix    h.tval[wim][lac][val][nxt] = h.tval[MWPR+1][lac][val][nxt];
                    }
                }
            }//val
        }//lac

        //U.print("B");
        //cur 0 == cur 4
        for(wim = 1; wim <= MWIM; wim++) {
            for(lac = 1; lac <= MLAC; lac++) {
                for(nxt = 1; nxt <= MVAL; nxt++) {
                //fix    h.tval[wim][lac][0][nxt] = h.tval[wim][lac][MVAL][nxt];
                }
            }
        }

//        //test
//        for(lac = 1; lac <= maxlac; lac++)
//            for(cur = 0; cur <= CUR; cur++)
//                for(wim = 1; wim <= WIM; wim++) {
//                    pnext = 0.0;
//                    for(nxt = 1; nxt <= CUR; nxt++) {
//                            pnext += f_pcur[lac][cur][wim][nxt];
//                    //	printf("\n lac:%d cur:%d wim:%d nxt:%d  pnxt:%f  pnext:%.15f",lac,cur,wim,nxt,fut->pnxt[cur][lac][wim][nxt],pnext);
//                    }
//                    if(U.abs(pnext - 1.0) > 0.0000000001) U.stop(1);
//                }
    //	stop(0);
        U.println("TRANSITION_VAL done (b)...");
        //U.stop("transition_val");
    }//end: hrd_cur


    //**************************************************************************
    //Calculates the probability of involuntary culling level given  
    //the daysopen in the previous lactation.  See Pindo and De Vries.
    //long WIM means fat cows means more culling
    //tcul[MWIM+1][MCUL+1];
    void transition_cul(Bio b) {
        U.println("TRANSITION_CUL started ...");
        if(MCUL == 0) {
            for(int wim = 0; wim <= MWIM; wim++) {
                for(int dry = 0; dry <= MWPR; dry++) {
                    tcul[wim][dry][0] = 1;
                }
            }
        }
        else {
            for(int wim = 0; wim <= MWIM; wim++) {
                for(int dry = 0; dry <= MWPR; dry++) {
                    for(int cul = 0; cul <= MCUL; cul++) {
                        tcul[wim][dry][cul] = 1/(MCUL+1);
                    }
                }
            }
        }
        //stop(1);
        U.println("TRANSITION_CUL done ...");
        //U.stop("transition_cul");
    }//end: transition_cul

    
    
    //**************************************************************************
    //convert monthly input seasonality to weekly seasonality.
    //- 52 weeks, 12 months --> 4.333 weeks per month
    float thisweek_seasonality(Bio b, int thisweek, int whatvariable) {
        if (thisweek == 0) return 1;
        
        int i, j, ii, jj;
        float fraci, fracj, seasonality_ = 0;
        float month[] = new float[13+1];

   	//U.print("THISWEEK_SEASONALITY started ... week:" + thisweek +" what:" + what);
        month[0] =  -2.166666666667f;
        month[1] =   2.166666666667f;		//week halfway January: gets 100% of the January seasonality
        month[2] =   6.500000000000f;
        month[3] =  10.833333333333f;
        month[4] =  15.166666666667f;
        month[5] =  19.500000000000f;
        month[6] =  23.833333333333f;
        month[7] =  28.166666666667f;
        month[8] =  32.500000000000f;
        month[9] =  36.833333333333f;
        month[10] = 41.166666666667f;
        month[11] = 45.500000000000f;
        month[12] = 49.833333333333f;
        month[13] = 54.166666666667f;

        i = 12; while(thisweek < month[i]) i--;
        j = i + 1;
        ii = i; if(i == 0) ii = 12;
        jj = j; if(j == 13) jj = 1;
        fraci = (float)thisweek - month[i];
        fracj = month[j] - (float)thisweek;
//	printf("\n what:%d  thisweek:%d  [months:i:%d  j:%d]  fraci:%f  fracj:%f ",what,thisweek,i,j,fraci,fracj);
        switch(whatvariable) {
            case 1: seasonality_ = (float)((fraci * b.month_conc[jj] + fracj * b.month_conc[ii]) / (4.333333333333333333)); break;
            case 2: seasonality_ = (float)((fraci * b.month_cull[jj] + fracj * b.month_cull[ii]) / (4.333333333333333333)); break;
            case 3: seasonality_ = (float)((fraci * b.month_milk[jj] + fracj * b.month_milk[ii]) / (4.333333333333333333)); break;
            case 4: seasonality_ = (float)((fraci * b.month_serv[jj] + fracj * b.month_serv[ii]) / (4.333333333333333333)); break;
            case 5: seasonality_ = (float)((fraci * b.month_dead[jj] + fracj * b.month_dead[ii]) / (4.333333333333333333)); break;            
            default: U.println("error: " + whatvariable); break;
        }
        //U.println(" seasonality:" + seasonality_);
        return seasonality_;
    }//end: thisweek_seasonality
    
       
    //**************************************************************************
    //Calculate daily milk yield.
    //Greg Bethard formulas, v.calculator20.xlsm
    float milkyield(Bio b, int wim, int lac, int val, int per, int wpr, int sea) {
	float milk;
       
        float wc = -b.mlk_persistency[lac]*(b.mlk_peakdim[lac] + 305) / (b.mlk_peakdim[lac] - 305);
        float wb = wc * b.mlk_peakdim[lac] / 30.5f;
        float wa = b.mlk_peakyield[lac] * (float)(U.pow((wc / wb),wb) * U.exp(wb));
                
        milk = (float)(wa * U.pow(wim*STAGE/30.5f,wb) * U.exp(-wc * wim*STAGE/30.5f) * b.mlk_preg[wpr] * b.smy[val] / 100 * b.mlk_multiplier[lac]);
	milk = milk * thisweek_seasonality(b,sea,3);      //3 = milk

//        U.println("MILKYIELD a "+wim+space+lac+space+val+space+per+space+wpr+space+sea);
//        U.println("MILKYIELD b "+b.mlk_persistency[lac]+space+b.mlk_peakdim[lac]+space+b.mlk_peakdim[lac]);
//        U.println("MILKYIELD c "+wa+space+wb+space+wc+space+space+thisweek_seasonality(b,sea,3)+space+"==>"+space+milk);        

	if(milk < b.dryoffyield) milk = 0;
	if(milk < 0) {
            U.println("\nMILKYIELD wim: lac: val: per: wpr: sea: milk_:" + wim + lac + per + wpr + sea + milk);
            U.stop("1");
            return milk;
	}
	else return milk;
    }//end: milkyield

    
    
    //**************************************************************************
    float drymatterintake(float milkyield, float fatpct, float bw, int wim, int metric) {
	float fcm, dmi;

	if(milkyield < 0.01) {//delete this code if not needed anymore
            U.println("DRYMATTERINTAKE stop() dry cow, do not use function...");
            U.stop("drymatterintake");
	}

	if(metric == 1) { //inputs in kg, equation in kg, dmi in kg
            fcm = (float)(0.4 * milkyield + 0.15 * fatpct * milkyield);			//fat correct milk per day
            dmi = (float)((0.372 * fcm + 0.0968 * U.pow(bw,0.75)) * (1 - U.exp(- 0.192 * ((float)wim + 3.67))));
	}
        else {	//inputs in lbs, equation in kg, dmi in lbs																//calculations must happen with kg
            fcm = (float)(0.4 * milkyield + 0.15 * fatpct * milkyield);			//fat correct milk per day
            dmi = (float)((0.372 * (fcm/2.204624) + 0.0968 * U.pow(bw/2.204624,0.75)) * (1 - U.exp(- 0.192 * ((float)wim + 3.67))));
            dmi = (float)(dmi * 2.204624);					//dmi as lbs
	}

//	printf("\nDRYMATTERINTAKE milkyield:%.2f  fatpct:%.3f  fcm:%.2f  bw:%.2f  wim:%d  metric:%d  dmi:%.2f",
//	milkyield,fatpct,fcm,bw,wim,metric,dmi);

	return dmi;
    }// end: drymatterintake

    
    
    //**************************************************************************
    //cowplan15.cpp  line 1425: COMPLETE_FUT_DATA()
    final void complete_herd_data(Bio b) {
        int wim, lac, val, per, bwt, dmi, fer, cul, wpr, ins, sea;
        int w = 0, flag = 0;
        float	milk, labor = 0, cost, drymatterintake;
        float	revenue1, revenue2, revenue3, revenue4;
        //short	ok, sea1, sea2, sea3, wimt;

        U.println("COMPLETE_HERD_DATA started ...");
        transition_bwt(b);
        transition_cul(b);
        transition_fer(b);
        transition_val(b);
        
        U.println("COMPLETE_HERD_DATA continued ...");
        //U.println("a");
        
         if(MSEA == 0) w = 0; else w = 52;
        
        for(wpr = 0; wpr <= MWPR; wpr++) {
            abort[wpr] = (float)b.abort[wpr];
        }
        
        cullpolicy = b.cullpolicy;
        
        for(lac = 1; lac <= MLAC; lac++) {        
            minvwp[lac] = b.minvwp[lac];
            maxlbw[lac] = b.maxlbw[lac];
            maxwim[lac] = b.maxwim[lac];
            maxdry[lac] = b.maxdry[lac]; 
            mindry[lac] = b.mindry[lac];
        }
//	for(lac = 1; lac <= LAC; lac++) {
//            if(h.minvwp[lac] < MWFR || h.minvwp[lac] > h.maxlbw[lac] || h.maxlbw[lac] > h.LBW || h.maxwim[lac] <= h.maxlbw[lac]) { 
//                System.out.printf("lac:%d hrd->minvwp:%d or hrd->maxlbw:%d or hrd->maxwim:%d  error",lac,h.minvwp[lac],h.maxlbw[lac],h.maxwim); 
//                U.stop("1"); 
//            }
//        }
        //U.println("1");    
        for(sea = 0; sea <= MSEA*2; sea++) {
            p_heifer[sea] = b.p_heifer[sea];
            enterheifer[sea] = b.enterheifer[sea];
            serve[sea] = b.serve[sea];
            
            U.println(sea+ space + p_heifer[sea]+space+enterheifer[sea]+space+serve[sea]);
        }
        //U.stop("bio");

        
        for(ins = 0; ins <= MINS; ins++) {        
            p_calf[ins] = b.p_calf[ins];
        }
        delayed = 0; //not sure what this does
        calc_ful = b.calc_ful;
        
        cullrrpreg = b.cullrrpreg;
        deadrrpreg = b.deadrrpreg;
        
        cullmilk = b.cullmilk;
        culliofc = b.culliofc;
        
        invcullpct = b.invcullpct;
        p_cullloss = b.p_cullloss;
        p_deadloss = b.p_deadloss;
        reprocost = b.reprocost;
        df = U.discount(b.interest/52,1);				
	geneticprogress = b.geneticprogress;									//$ genetic progress per month
	p_fixed = (float)((b.p_fixed_labor + b.p_fixed_other) * STAGE);		//fixed cost for stage
        
        for(ins = 0; ins <= MINS; ins++) {
            inseminationcost[ins] = (b.p_breeding[ins] + b.timebreeding / 60 * b.p_labor);			//cost per breeding
        }
        
        if(MSEA == 0) startsea = 0; else startsea = b.startsea;
        
       
        ////////////////////////////////////////////////////////////////////////
        //U.println("b");
        for(wim = 1; wim <= MWIM; wim++) {
            for(lac = 1; lac <= MLAC; lac++) {
                for(sea = 0; sea <= MSEA; sea++) {
                    pcull[wim][lac][sea] = (b.pcull[wim] * b.pcull_odds[lac] * thisweek_seasonality(b,sea,2));
                    if(pcull[wim][lac][sea] > 1) pcull[wim][lac][sea] = 1;          //error check
                    if(pcull[wim][lac][sea] < -0.0001) pcull[wim][lac][sea] = 1;    //100% cull if negative number is entered

                    pdead[wim][lac][sea] = (b.pdead[wim] * b.pdead_odds[lac] * thisweek_seasonality(b,sea,5));
                    if(pdead[wim][lac][sea] > 1) pdead[wim][lac][sea] = 1;          //error check
                    if(pdead[wim][lac][sea] < -0.0001) pdead[wim][lac][sea] = 1;    //100% cull if negative number is entered
                                        
                    if(MCUL == 0) pcull_frh[wim][lac][0][sea] = pcull[wim][lac][sea];
                    else {
                        for(cul = 0; cul <= MCUL; cul++) {
                            pcull_frh[wim][lac][cul][sea] = pcull[wim][lac][sea] * (float)(1 + (cul - MCUL/2) * 0.1);
                            pdead_frh[wim][lac][cul][sea] = pdead[wim][lac][sea] * (float)(1 + (cul - MCUL/2) * 0.1);
                        }
                    }
            //	if(sea == 1) printf("\nlac:%d wim:%d %.3f",lac,wim,fut->pid[lac][wim][sea]);
            //	else printf(" %.3lf",fut->pid[lac][wim][sea]);

            //	if(sea == 1 && wim == 1)  printf("\nlac:%d wim:%d pid:%.2f odd:%.3f seas:%.2f",lac,wim,pid[wim],odds[lac],thisweek_seasonality(sea,2));
            //	if(sea == 1 && wim <= 13) printf(" %.2lf",fut->pid[lac][wim][sea]*100);
                }//sea
            }//lac
        }//wim
        ////////////////////////////////////////////////////////////////////////
        //U.println("c");
        for(wim = 1; wim <= LBW; wim++)
            for(val = 0; val <= MVAL; val++)
                for(sea = 0; sea <= MSEA; sea++)
                    servrate[wim][val][sea] = (b.milksr[val] * b.servrate[wim] * thisweek_seasonality(b,sea,4));
        
        for(lac = 1; lac <= MLAC; lac++) {
            for(wim = 1; wim <= maxlbw[lac]; wim++) {
                for(fer = 0; fer <= MFER; fer++) {
                    for(ins = 0; ins <= MINS; ins++) {
                        for(sea = 0; sea <= MSEA; sea++) {

                            concrate[wim][lac][fer][ins][sea] = (float)(b.concwim[wim] * b.conclac[lac] * b.concfer[fer] * b.concins[ins] * thisweek_seasonality(b,sea,1));
                            if(concrate[wim][lac][fer][ins][sea] > 1) concrate[wim][lac][fer][ins][sea] = 1;
                //	if(lac == 1) printf("\nwim:%d sea:%d lac:%d  servrate:%f concrate:%f",wim,sea,lac,fut->servrate[wim][sea],fut->concrate[lac][wim][sea]);
                        }
                    }
                }
            }
        }
        //	if(wim == 64) stop(0);
//        if(seasonality == false)
//            for(lac = 1; lac <= maxlac; lac++)
//                for(rep = 1; rep <= maxrep; rep++)
//                    for(wim = 1; wim <= maxlbw[lac]; wim++)
//                        for(sea = 2; sea <= SEA; sea++)
//                            f_concrate[lac][rep][wim][sea] = f_concrate[lac][rep][wim][1];
        ////////////////////////////////////////////////////////////////////////
        //U.println("d");  
        for(wim = 1; wim <= MWIM; wim++) {        
            for(lac = 1; lac <= MLAC; lac++) {
                for(bwt = 0; bwt <= MBWT; bwt++) {
                    for(wpr = 0; wpr <= MWPR; wpr++) { 
                        
                        //U.println("lac:" +lac+ " bwt:" + bwt + " wim:" +wim+ " wpr:" +wpr);                                
                        bw[wim][lac][bwt][wpr] = (b.bw_open[wim][lac][bwt] + b.bw_preg[wpr]);
                    //	if(curr == 8 && lact == 1 && sea == 6) 
                    //	{
                    //		printf("\n body weight");
                    //		printf("\n lac:%d wim:%d wpr:%d bw:%.3f",lac,wim,wpr,fut->bw[lac][wim][wpr]);
                    //		stop(0);
                    //	}
                    
                        for(sea = 0; sea <= MSEA; sea++) { 
                            //U.println("lac:" +lac+ " bwt:" + bwt + " wim:" +wim+ " sea:" + sea);
                            //U.println(" "+bw[1][wim]+" "+bw[2][wim]+" "+p_sell[sea]+" "+p_sell[sea+52]);
                            sel[wim][lac][bwt][wpr][sea] = bw[wim][lac][bwt][wpr] * (b.p_sell[sea] * 0.01f);
                            sel[wim][lac][bwt][wpr][sea+w] = bw[wim][lac][bwt][wpr] * (b.p_sell[sea+w] * 0.01f);
                        }//sea
                    }//wpr
                }//bwt
            }//lac
        //stop(0);
        }//wim                  
        

        ////////////////////////////////////////////////////////////////////////
        //U.println("e");
        for(wim = 1; wim <= MWIM; wim++) {        
            for(lac = 1; lac <= MLAC; lac++) {
                //U.println("-" + lac);
                for(val = 0; val <= MVAL; val++) {
                    for(per = 0; per <= MPER; per++) {    
                        for(dmi = 0; dmi <= MDMI; dmi++) {
                            for(bwt = 0; bwt <= MBWT; bwt++) {
                                for(wpr = 0; wpr <= MWPR; wpr++) {
                                    for(sea = 0; sea <= MSEA; sea++) {
                                      
try {                                         
                                       
                                        flag = 1;
                                        
                                        //System.out.printf("\n wim:%d lac:%d val:%d per:%d dmi:%d bwt:%d wpr:%d sea:%d w:%d ",wim,lac,val,per,dmi,bwt,wpr,sea, w);
                                        milk = milkyield(b,wim,lac,val,per,wpr,sea);
                                        flag = 2;
                                        

                                        if((wpr == 0) || (wpr >= 1 && wim - wpr >= minvwp[lac] && wim - wpr <= maxlbw[lac])) {
                                            if(wpr == 0) dailymilk_o[wim][lac][val][per][sea] = milk;	//use in dp_fut() to repl low milk yield cows
                                        }
                                        flag = 3;                                      
                                        //U.print("b");
                                        //printf("\n mlk[1][16][36]:%f  mlk[%d][%d][%d]:%f",mlk[1][16][32],lac,wim,sea,mlk[lac][wim][sea]);
 
                                        //printf("\n mlk[1][16][36]:%f  mlk[%d][%d][%d]:%f",mlk[1][16][32],lac,wim,sea,mlk[lac][wim][sea]);

                                        //printf("    %f = %f x %f x %f",milk,mlk_wpr[wpr],smy[cur]/100,mlk[lac][wim][sea]);
                                        //printf("\n mlk[1][16][36]:%f  mlk[%d][%d][%d]:%f",mlk[1][16][32],lac,wim,sea,mlk[lac][wim][sea]);

                                        //printf("\nmilk-->cur:%d lac:%d wim:%d wpr:%d sea:%d milk:%f",cur,lac,wim,wpr,sea,milk);

                                        if(milk >= 0.01) {	//cow is lactating, NRC[1112]4					
                                            drymatterintake = drymatterintake(milk,b.fatpct*100,bw[wim][lac][bwt][wpr],wim,b.metric);
                                            //else dmi = dmi_maintenance + milk / dmi_production;
                                            cost = (drymatterintake * b.p_tmrdmlac * 0.01f + b.p_other_lact) * STAGE;
                                        }
                                        else {				//cow is dry
                                            drymatterintake = b.dmi_dry;			//only maintenance
                                            cost = (drymatterintake * b.p_tmrdmdry * 0.01f + b.p_other_dry) * STAGE;
                                        }
                                        flag = 4;                                         
                                        
                                        revenue1 = cost;
                                        revenue2 = cost;
                                        if(milk >= 0.01) labor = b.timelact * STAGE / 60;	//lactating cow (hrs/week)
                                        if(milk  < 0.01) labor = b.timedry * STAGE / 60;		//dry cow (hrs/week)
                                        if(wim == 1) labor += b.timefresh / 60;				//fresh cow (hrs/week)
                                        revenue1 -= labor * b.p_labor;
                                        revenue2 -= labor * b.p_labor;
                                        revenue1 -= b.veterinarycost[wim];
                                        revenue2 -= b.veterinarycost[wim];
                                        
                                        revenue3 = revenue1 + b.p_milk[sea] * milk * 0.01f * STAGE;
                                        revenue4 = revenue2 + b.p_milk[sea+w] * milk * 0.01f * STAGE;
                                        flag = 5; 
 
                                        if(wpr == 0) {
                                            iofc_o[wim][lac] [val][per][bwt][dmi] [sea] = revenue3;
                                            iofc_o[wim][lac] [val][per][bwt][dmi] [sea+w] = revenue4;
                                        }
                                        else {
                                            if(wpr <= MWPR-MDRY) {
                                                //U.print("e");
                                                iofc_p[wim][lac] [val][per][bwt][dmi] [wpr][sea] = revenue3;
                                                iofc_p[wim][lac] [val][per][bwt][dmi] [wpr][sea+w] = revenue4;                                                
                                            }
                                            //U.print("f");
                                            iofc_d[bwt][dmi] [wpr][sea] = revenue1; 
                                            iofc_d[bwt][dmi] [wpr][sea+w] = revenue2;
                                        }
                                        
                                        if(lac == 1) {
                                            if (wpr == 0) {
                                                flag = 6;                                            
                                                //U.print(" "+wim+space+lac+space+val+space+per+space+bwt+space+dmi+space+sea+space+space);
                                                //U.print(U.df(iofc_o[wim][lac] [val][per][bwt][dmi] [sea],4,2));
                                                //U.print(U.df(milk,3,2));
                                                //U.print(space+milk);
                                            }
                                            else { 
                                                flag = 7;
                                                //U.print(U.df(iofc_p[wim][lac] [val][per][bwt][dmi] [wpr][sea],4,2));   
                                                //U.print(U.df(milk,3,2));   
                                                //U.print(space+milk);   
                                            }                                        
                                            //if(wpr == MWPR) U.println(space);                                        
                                            //if(wim == MWIM && wpr == MWPR && val == MVAL) U.wait(3,"iofc");
                                        }                          
                                                                                
                                        
} catch(Exception ex) {
    System.out.printf("COMPLETE_HERD_DATA ERROR  wim:%d lac:%d val:%d per:%d dmi:%d bwt:%d wpr:%d sea:%d w:%d flag:%d",wim,lac,val,per,dmi,bwt,wpr,sea,w,flag);        
    U.stop("error: " + ex);
        }
                                        
//    float iofc_p[][] [][][][] [][] = new float[MWIM+1][MLAC+1] [MVAL+1][MPER+1][MBWT+1][MDMI+1] [MWPR-MDRY+1] [2*MSEA+1];
//    float iofc_d[][] [][][][] [][][] = new float[MWIM+1][MLAC+1] [MVAL+1][MPER+1][MBWT+1][MDMI+1] [MWPR-MDRY+1][MDRY+1] [2*MSEA+1];                                            
//        wim,wfr,lac,val,per,bwt,dmi,fer,cul,cyc,wpr,dry,ins,sea                                              
                                            
    //                                        else if(wpr >= 2) {//revenues during the first week of pregnancy are fut->rev_o[] because inseminations now start at begin of week
    //                                        		//so qck->cumrev[] is for wpr = 2 to WPR
    //                                                        //wim is not correct
    //                                            qck->cumrev_x[lac][cur][bwt][wim][sea] += revenue1;
    //                                            qck->cumrev_x[lac][cur][bwt][wim][sea+52] += revenue2;
    //                                        }
                                        //U.print("e");

                                    //	if(cur == 0 && lac == 1 && wim == 95 && wpr == 31)
                                    //	{
                                    //		printf("\n cur:%d lac:%d wim:%d wpr:%d sea:%d ",cur,lac,wim,wpr,sea);
                                    //		printf("\n milk: %f",milk);
                                    //		printf("\n dmi: %f",dmi);
                                    //		printf("\n cost: %f",cost);
                                    //		printf("\n laborcost: %f",labor*p_labor);
                                    //		printf("\n vetcost: %f",veterinarycost[wim]);
                                    //		printf("\n revenue1: %f",revenue1);
                                    //		printf("\n revenue2: %f",revenue2);
                                    //		if(wpr == 0) printf("\n rev_o: %f %f",fut->rev_o[cur][lac][wim][sea],fut->rev_o[cur][lac][wim][sea+52]);
                                    //		else  printf("\n rev_x: %f %f",ftx->rev_x[cur][lac][wim][wpr][sea],ftx->rev_x[cur][lac][wim][wpr][sea+52]);
                                    //		stop(0);
                                    //	}
                                        

                                    }//sea
                                }//wpr
                            }//bwt
                        }//dmi
                    }//per
                }//val
            }//lac
        }//wim

  
        
//        U.println("f");
//     //   stop(1);
//        if(seasonality == false) {
//            for(lac = 1; lac <= LAC; lac++) {
//                for(cur = 0; cur <= maxcur; cur++) {
//                    for(bwt = 0; bwt <= maxbwt; bwt++) {
//                        for(wim = 1; wim <= WIM; wim++) {
//                            for(sea = 1; sea <= SEA; sea++) {
//                            //    printf("\nf lac:%d cur:%d bwt:%d wim:%d sea:%d",lac,cur,bwt,wim,sea);
//                                f_dailymilk_o[lac][cur][wim][sea] = f_dailymilk_o[lac][cur][wim][1];
//                                f_rev_o[lac][cur][bwt][wim][sea] = f_rev_o[lac][cur][bwt][wim][sea+52] = f_rev_o[lac][cur][bwt][wim][1]; 
//                                if(policy >= 1)	{
//                                    for(wpr = 1; wpr <= WPR; wpr++) if((wim - wpr >= minvwp[lac] && wim - wpr <= maxlbw[lac])) {
//                                        s_rev_x[lac][cur][bwt][wpr][wim][sea] = s_rev_x[lac][cur][bwt][wpr][wim][sea+52] = s_rev_x[lac][cur][bwt][wpr][wim][1];
//                                    }
//                                }
////                                else //policy == 0 is qck]
////                                {
////                                    qck->cumrev_x[lac][cur][bwt][wim][sea] = qck->cumrev_x[lac][cur][bwt][wim][1];
////                                }
//                            }
//                        }
//                    }
//                }
//            }
//        }
/*        
    ///////////////////////////////////////////////////////////////////////////////////////////////////
        if(analysis != 2) {
            printf("g");
            sea = 0;												//sea is a help variable here
            for(i = 1; i <= SEA; i++) sea += enter[sea+SEA];	//if sea == 52 to sea == 104 is all delay, then start with 0
            if(sea > 0) {
                if(policy == 0) ok = read_warm_file(0);		//reads v.warm0.txt for qck warm start
                if(policy >= 1) ok = read_warm_file(1);		//reads v.warm1.txt for ftx warm start
                if(ok == 0) U.println(" warm file not read");
                if(ok == 1)											//warm file read successfully
                {
                    fut->enter[0] = fut->enter[2];
                    for(lac = 1; lac <= LAC; lac++)
                    {
                        for(cur = 0; cur <= CUR; cur++)
                        {
                            for(bwt = 0; bwt <= BWT; bwt++)
                            {
                                for(wim = 1; wim <= WIM; wim++)
                                {
                                    if(policy == 0)		//qck
                                    {
                                            if(wim <= LBW + 1)
                                                    fut->cow_x[lac][cur][bwt][wim][0] = fut->cow_x[lac][cur][bwt][wim][2];

                                    //	qck->cow_x[lac][cur][bwt][wim][sea] = ??
                                    }				
                                    else if(policy >= 1)	//slo
                                    {
                                            for(wpr = 1; wpr <= WPR; wpr++) if(wim - wpr >= 1 && wim - wpr <= LBW)
                                            {
                                                    slo->cow_x[lac][cur][bwt][wpr][wim][0] = slo->cow_x[lac][cur][bwt][wpr][wim][2];// - fut->heifer0; 
                                                    if(wpr == 2) fut->cow_x[lac][cur][bwt][wim][0] = fut->cow_x[lac][cur][bwt][wim][2];	

                                            //	printf("\n %d %d %d %d %f",i,j,k,m,fut->cow[i][j][k][m][0]);	
                                            //	printf("\nCOMPLETE_FUT_DATA %d %d %d %f",i,j,k,fut->cow1x[i][j][k][0]);
                                            }
                                    }
                                    else
                                    {
                                            printf("error");
                                            stop(1);
                                    }
                                }//wim
                            }//bwt
                        }//cur
                    }//lac
                }//ok
            }//sea
        }//analysis!=2
*/        
    //stop(0);
        U.println("COMPLETE_HERD_DATA done ...");
    }//end: complete_hrd_data

    
    
    //**************************************************************************
    //CONSTRUCTOR
    Herd(Bio b) {
        U.println("Herd constructor");
        
        complete_herd_data(b);
    }
}//class: Herd
    
