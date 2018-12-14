
package cowplan16package;

/*
https://en.engormix.com/MA-dairy-cattle/videos/an-app-that-predicts-cure-rate-clinical-mastitis-scott-mcdougall-t47749.htm
can I make simply say 5 states on the type of action with clinical mastitis and then cure rate?
easy to have current lactation, with clinical mastitis or not, and then type of treatments to choose from
but still need mastitis states in future lactations to model repeatability and carry over effects
or: break down any disease in factors: loss in milk, fertility, culling, treatment cost etc. 
*/

class Hrd extends Param {
        // use in System.out.println
        
    AA pindX = null;
    AA milkX = null;
    AA testX = null;
    
    int i, j, k, t;
//    double STAGE = 7.023;                                                       //length of one stage in days (365.2 / 52)
    int CUR = 7;                                                                //maximum milk production levels
    int LAC = 8;                                                                //maximum lactations
    int WFR = 6;                                                                //maximum weeks fresh
    int WIM = 144;                                                              //maximum weeks in a lactation
    int WPR = 40;                                                               //maximum weeks pregnant: 1-40  (0 = open)
    int INV = 2;                                                                //0,1,2 levels of involuntary culling (0=default)
    int BWT = 2;                                                                //0,1,2 levels of bodyweight (0=default)
    int REP = 2;                                                                //0,1,2 levels of reproduction (0=default)
    int DRY = 1;                                                                //0=wet, 1=dry (0=default)
    int SEA = 52;                                                               //maximum seasonality classes
//    int LBW = 104;                                                              //last breeding week: LBW = WIM - 40
//    int POL = 9;                                                                //default policy value
    int T = 144;                                                                //144 weeks into the future for cow[]->
    int WPRABORT = 40;                                                          //should be 32, but DP_FUT != MKV_FUT 
    int OPN = 7;                                                                //open cow states
    int SRV = 3;                                                                //serve states
    int MAXM = 60;


    //see void reset_hrd_record(void) in cowplan15.cpp line 11846 
    
       
    double abort[] = {//WPR+1
        0,              
        0,           0,           0,           0,           0.009921654, 0.009212964, 0.008504274, 0.007795585,
        0.007086895, 0.006378206, 0.005669516, 0.004960827, 0.004252137, 0.003543448, 0.002834758, 0.002126069,
        0.001417379, 0.001218946, 0.001048861, 0.000907123, 0.000708690, 0.000595299, 0.000481909, 0.000396866,
        0.000283476, 0.000283476, 0.000283476, 0.000283476, 0.000283476, 0.000283476, 0.000283476, 0.000283476,
        0.000283476, 0.000283476, 0.000283476, 0.000283476, 0.000283476, 0,           0,           0 };
  
    double bw1[] = {//WIM+1
        0,
        1188, 1181, 1176, 1173, 1171, 1170, 1171, 1172, 1174, 1177, 1180, 1183, 1187,
        1191, 1194, 1198, 1202, 1206, 1210, 1214, 1218, 1222, 1225, 1229, 1232, 1235, 
        1238, 1242, 1244, 1247, 1250, 1253, 1255, 1258, 1260, 1262, 1264, 1266, 1268,
        1270, 1272, 1274, 1275, 1277, 1278, 1280, 1281, 1282, 1284, 1285, 1286, 1287,
        1288, 1289, 1290, 1291, 1292, 1293, 1294, 1295, 1296, 1297, 1297, 1298, 1299,
        1300, 1300, 1301, 1301, 1302, 1303, 1303, 1304, 1304, 1305, 1305, 1306, 1306,
        1307, 1307, 1308, 1308, 1308, 1309, 1309, 1310, 1310, 1310, 1311, 1311, 1311,
        1312, 1312, 1312, 1312, 1313, 1313, 1313, 1314, 1314, 1314, 1314, 1315, 1315, 
        1315, 1315, 1315, 1316, 1316, 1316, 1316, 1316, 1317, 1317, 1317, 1317, 1317,
        1317, 1317, 1318, 1318, 1318, 1318, 1318, 1318, 1318, 1318, 1319, 1319, 1319,
        1319, 1319, 1319, 1319, 1319, 1319, 1320, 1320, 1320, 1320, 1320, 1320, 1320, 1320 };
    
    double bw2[] = {//WIM+1
        0,
        1369, 1351, 1336, 1325, 1316, 1310, 1306, 1303, 1302, 1302, 1303, 1304, 1306,
        1309, 1312, 1316, 1319, 1323, 1327, 1331, 1334, 1338, 1342, 1345, 1349, 1352,
        1356, 1359, 1362, 1365, 1368, 1370, 1373, 1375, 1377, 1379, 1381, 1383, 1385,
        1387, 1388, 1390, 1391, 1392, 1394, 1395, 1396, 1397, 1398, 1399, 1399, 1400,
        1401, 1402, 1402, 1403, 1403, 1404, 1404, 1405, 1405, 1405, 1406, 1406, 1406,
        1407, 1407, 1407, 1407, 1408, 1408, 1408, 1408, 1408, 1409, 1409, 1409, 1409,
        1409, 1409, 1409, 1409, 1409, 1410, 1410, 1410, 1410, 1410, 1410, 1410, 1410,
        1410, 1410, 1410, 1410, 1410, 1410, 1410, 1410, 1410, 1410, 1410, 1410, 1410, 
        1410, 1411, 1411, 1411, 1411, 1411, 1411, 1411, 1411, 1411, 1411, 1411, 1411,
        1411, 1411, 1411, 1411, 1411, 1411, 1411, 1411, 1411, 1411, 1411, 1411, 1411,
        1411, 1411, 1411, 1411, 1411, 1411, 1411, 1411, 1411, 1411, 1411, 1411, 1411, 1411 };
    
    double bw_wpr[] = {//WPR+1
        0,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 2, 3, 4, 5, 7, 9,
        12, 15, 18, 22, 26, 31, 36, 42, 48, 55, 63, 71, 81, 91, 101, 113, 125, 138, 152, 168 };
                                                                       //peak, peakdim, 305m, %
    double wood1a = 12.0; double wood1b = 0.310; double wood1c = 2.8;//	83, 109, 23022,  91%
    double wood2a = 17.8; double wood2b = 0.255; double wood2c = 3.4;//	91,  74, 24048,  95% 
    double wood3a = 19.5; double wood3b = 0.250; double wood3c = 3.5;// 97,  74, 25363, 100%

    double mlk_wpr[] = {//WPR+1
        1.00,
        1.00, 1.00, 1.00, 1.00, 1.00, 1.00, 1.00, 1.00, 1.00, 1.00,
        1.00, 1.00, 1.00, 1.00, 1.00, 1.00,	0.99, 0.98, 0.97, 0.96,
        0.95, 0.94, 0.93, 0.92, 0.91, 0.90, 0.89, 0.88, 0.87, 0.86, 
        0.85, 0.84, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00 };
    
    double mlk_funk[] = {//LBW+1
        0,
        0.9564, 0.9601, 0.9637, 0.9673, 0.9708, 0.9742, 0.9776, 0.9808, 
        0.9840, 0.9872, 0.9902, 0.9932, 0.9961, 0.9990, 1.0018, 1.0045,
        1.0071, 1.0097, 1.0122, 1.0146, 1.0169, 1.0192, 1.0214, 1.0236,
        1.0256, 1.0276, 1.0296, 1.0314, 1.0332, 1.0349, 1.0366, 1.0382,
        1.0397, 1.0411, 1.0424, 1.0437, 1.0450, 1.0461, 1.0472, 1.0482,
        1.0491, 1.0500, 1.0508, 1.0515, 1.0522, 1.0527, 1.0533, 1.0537,
        1.0541, 1.0544, 1.0546, 1.0547, 1.0548, 1.0549, 1.0548, 1.0547,
        1.0545, 1.0542, 1.0539, 1.0535, 1.0530, 1.0524, 1.0518, 1.0511, 
        1.0503, 1.0495, 1.0486, 1.0476, 1.0466, 1.0455, 1.0443, 1.0430,
        1.0417, 1.0403, 1.0388, 1.0373, 1.0356, 1.0340, 1.0322, 1.0304,
        1.0285, 1.0265, 1.0245, 1.0224, 1.0202, 1.0179, 1.0156, 1.0132,
        1.0107, 1.0082, 1.0056, 1.0029, 1.0002, 0.9974, 0.9945, 0.9915,
        0.9885, 0.9854, 0.9822, 0.9789, 0.9756, 0.9722, 0.9688, 0.9653 };//see REPEATABILITY-CURVE.XLS

    double smy[] = {//CUR+1
        0,
        71.589343, 81.097496, 90.561116, 100.000000, 109.438884, 118.902504, 128.410657};

    double yum[] = {//CUR+1
        0,
        80, 88, 96, 105, 115, 125, 135}; 
    
    short calc_cow = 0;
    short calc_ful = 0;
    String breed = "GE";
    double bw[][] = new double[LAC+1][WIM+1];
    double bwlevel[] = new double[BWT+1];
    double bwrepeatability;
    double calved[] = new double[T+2];
    short code[] = new short[999+1];
    double conclevel[] = new double[REP+1];
    double concrate[] = new double[LBW+1];
    double concrisk[] = new double[LAC+1];
    short continuelac = 1;
    double cullmargin = -999;
    double cullmilk = 0.0;
    double cullrrpreg = 0.5;
    short delayed = 0;
    double dmi_dry = 25.0;
    short dmi_formula = 1;
    double dmi_maintenance = 25.0;
    double dmi_production = 2.0;
    double dryoffyield = 0.0;
    short enter[] = new short[SEA*2+1];
    double erpa = 0.0;
    short errorcheck = 0;
    double fatpct = 0.035;
    double fresh[][][][][][] = new double[LAC+1][CUR+1][BWT+1][REP+1][INV+1][T+2];
    short funk = 1;
    double geneticprogress;
    double heifer[] = new double[T+2];	
//    long herdcode = 58010029;
    double interest = 0.08;
    double invcullpct = 0.25;
    double invlevel[] = new double[2+1];
    double m305[][][] = new double[LAC+1][CUR+1][SEA+1];
    int maxbwt = 1; //BWT
    short maxid = 0;
    int maxcur = CUR;
    int maxinv = 1; //INV
    int maxlac = 5; //LAC;
    int maxlbw[] = new int[LAC+1];
    int maxrep = REP;
    int maxwim = WIM;
    double mefat = 853;
    double memilk = 25363;
    double memilk_adjustment = 1.0;
    double meprotein = 661;
    short metric = 0;
    double milkcr[] = new double[CUR+1];
    double milksr[] = new double[CUR+1];
    int minvwp[] = new int[LAC+1];
    short mkv_herd = 0;
    double mlk[][] = new double[LAC+1][WIM+1];
    double mlk_dopn[] = new double[LBW+1];
    double mlk_slope[] = new double[LAC+1];
    double mlkpct[] = new double[LAC+1];
    double month_conc[] = new double[12+1];
    double month_cull[] = new double[12+1];
    double month_milk[] = new double[12+1];
    double month_serv[] = new double[12+1];
    double mrepeat = 0.60;
    double odds[] = new double[LAC+1];
    short optimizedryoff = 0;
    double p_breeding = 10.00;
    double p_calf = 200.00;
    double p_feeddry[] = new double[SEA*2+1];
    double p_feedwet[] = new double[SEA*2+1];
    double p_fixed_labor = 1.50;
    double p_fixed_other = 1.00;
    double p_heifer[] = new double[SEA*2+1];
    double p_labor = 10.00;
    double p_loss = 0.0;
    double p_milk[] = new double[SEA*2+1];
    double p_other_dry = 0.20;
    double p_other_lact = 0.30;
    double p_sell[] = new double[SEA*2+1];
    double p_tmrdmdry = 5.00;
    double p_tmrdmlac = 13.0;
    double pa_cv = 12.0;
    double pdeath[] = new double[WIM+1];
    double pid[] = new double[WIM+1];
    short policy = 1;
    double present[][] = new double[3+1][MAXM+1];
    double probpreg = 0.4;
    double proteinpct = 0.032;
    double replevel[] = new double[2+1];
    double repreg = 0.1;
    double reprocost = 0.0;
    short res[] = new short[6+1];
    boolean seasonality = false;
    int serve[] = new int[SEA*2+1];
    double servrate[] = new double[LBW+1];
    short showheat = 1;
    short startsea = 1;
    short sureb[] = new short[LAC+1];
    double timebreeding = 10;   //minutes
    double timedry = 30;        //minutes/calving
    double timefresh = 7;       //minutes/day
    double timelact = 3.0;      //minutes/day
    double veterinarycost[] = new double[WIM+1];
    short weeksdry[] = new short[4+1];
    double zsave[] = new double[20+1];	

    
    double f_bw[][][][] = new double[LAC+1][BWT+1][WIM+1][WPR+1];
    double f_concrate[][][][] = new double[LAC+1][REP+1][WIM+1][SEA+1]; 
    double f_dailymilk_o[][][][] = new double[LAC+1][CUR+1][WIM+1][SEA+1];
    double f_pid_f[][][][] = new double[LAC+1][INV+1][WIM+1][SEA+1];
    double f_pid[][][] = new double[LAC+1][WIM+1][SEA+1];			//P(involuntary culling per lactation per week)
    double f_sel[][][][] = new double[LAC+1][BWT+1][WIM+1][2*SEA+1];
    double f_servrate[][][] = new double[CUR+1][WIM+1][SEA+1];
    double f_rev_o[][][][][] = new double[LAC+1][CUR+1][BWT+1][WIM+1][2*SEA+1];
    float s_rev_x[][][][][][] = new float[LAC+1][CUR+1][BWT+1][WPR+1][WIM+1][2*SEA+1];    //slow
       
    double f_pbwt[][] = new double[BWT+1][BWT+1];
    double f_pcur[][][][] = new double[LAC+1][CUR+1][WIM+1][CUR+1];		//Pr(nxt | cur,lac,wim)
    double f_pinv[][] = new double[WIM+1][INV+1];
    
    
    
    //**************************************************************************
    void default_hrd_arrays() {
        U.println("DEFAULT_HRD_ARRAYS started ...");
        
        for(i = 0; i <= WIM; i++) bw[0][i] = 0.0;
        for(i = 0; i <= WIM; i++) bw[1][i] = bw1[i];
        for(j = 2; j <= LAC; j++) for(i = 0; i <= WIM; i++) bw[j][i] = bw2[i];

        bwlevel[0] = 1.0; bwlevel[1] = 0.8; bwlevel[2] = 1.2;
        for(i = 0; i <= 999; i++) code[i] = 0;
        for(i = 0; i <= LBW; i++) concrate[i] = 0.35;
        for(i = 0; i <= LAC; i++) concrisk[i] = 1.0;
        for(i = 0; i <= SEA*2; i++) enter[i] = 1;
        invlevel[0] = 1.0; invlevel[1] = 0.5; invlevel[2] = 1.5;
        
        for(i = 0; i <= LAC; i++) maxlbw[i] = LBW;
        for(i = 0; i <= CUR; i++) milkcr[i] = 1.0;
        for(i = 0; i <= CUR; i++) milksr[i] = 1.0;
        minvwp[0] = 0; 
        for(i = 1; i <= LAC; i++) minvwp[i] = WFR;
    
        mlk[0][0] = mlk[1][0] = mlk[2][0] = mlk[3][0] = 0.0;
        for(i = 1; i <= WIM; i++) mlk[0][i] = 0.0;
        for(i = 1; i <= WIM; i++) mlk[1][i] = wood((int) ((double)(i-1)*STAGE+4),wood1a,wood1b,wood1c);	//=mlk1[i];
        for(i = 1; i <= WIM; i++) mlk[2][i] = wood((int) ((double)(i-1)*STAGE+4),wood2a,wood2b,wood2c);	//=mlk2[i];
        for(j = 3; j <= LAC; j++) for(i = 1; i <= WIM; i++) mlk[j][i] = wood((int)((double)(i-1)*STAGE+4),wood3a,wood3b,wood3c); //=mlk3[i];

        mlk_slope[0] = 0.0;
        mlk_slope[1] = (mlk[1][52] - mlk[1][WIM]) / (WIM-52);	//slope to extend curve past WIM
        mlk_slope[2] = (mlk[2][52] - mlk[2][WIM]) / (WIM-52);
        for(i = 3; i <= LAC; i++) mlk_slope[i] = (mlk[3][52] - mlk[3][WIM]) / (WIM-52);	//hrd->mlkpct[lac] == 1;
        mlk_dopn[0] = 0.70; for(i = 1; i <= LBW; i++) mlk_dopn[i] = mlk_dopn[i-1] + 0.02;
        for(i = 0; i <= LAC; i++) mlkpct[i] = 1.0;
        for(i = 0; i <= 12; i++) month_conc[i] = 1.0;
        for(i = 0; i <= 12; i++) month_cull[i] = 1.0;
        for(i = 0; i <= 12; i++) month_milk[i] = 1.0;
        for(i = 0; i <= 12; i++) month_serv[i] = 1.0;
        for(i = 0; i <= LAC; i++) odds[i] = 1.0;
        for(i = 0; i <= SEA*2; i++) p_feeddry[i] = 5.0;
        for(i = 0; i <= SEA*2; i++) p_feedwet[i] = 11.0;
    
        for(i = 0; i <= SEA*2; i++) p_heifer[i] = 1800.0;
        for(i = 0; i <= SEA*2; i++) p_milk[i] = 18.0;
        for(i = 0; i <= SEA*2; i++) p_sell[i] = 40.0;
        for(i = 1; i <= WIM; i++) pdeath[0] = 0.001;
        pid[0] = 0;
        pid[1] = 0.0062;
        pid[2] = 0.0055;
        pid[3] = 0.0050;
        pid[4] = 0.0045;
        pid[5] = 0.0040;
        pid[6] = 0.0038;
        pid[7] = 0.0035;
        pid[8] = 0.0033;
        pid[9] = 0.0029;
        pid[10] = 0.0028;
        pid[11] = 0.0026;
        pid[12] = 0.0025;
        pid[13] = 0.0024;
        pid[14] = 0.0023;
        for(i = 15; i <= WIM; i++) pid[i] = 0.0022;
        replevel[0] = 1.0; replevel[1] = 0.8; replevel[2] = 1.2;
        for(i = 0; i <= 6; i++) res[i] = 0; res[0] = 1;    
        for(i = 0; i <= SEA*2; i++) serve[i] = 9;
        for(i = 0; i <= LBW; i++) servrate[i] = 0.35;
        for(i = 0; i <= LAC; i++) sureb[i] = 0;
        veterinarycost[0] = 0.0;
        veterinarycost[1] = 39.0;		//$/week (1st week after calving)
        veterinarycost[2] = 23.0;		//$/week (2nd week after calving)
        veterinarycost[3] = 8.0;		//$/week (3rd week after calving)
        for(i = 4; i <= WIM; i++) veterinarycost[i] = 1.0;
        weeksdry[0] = 0;
        weeksdry[1] = 9;	//lactation 1
        weeksdry[2] = 7;	//lactation 2
        weeksdry[3] = 7;	//lactation 3
        weeksdry[4] = 7;	//lactation 4+
      
        for(t = 0; t <= T+1; t++) heifer[t] = 0.0;
        
        for(i = 0; i <= LAC; i++) 
            for(j = 0; j <= CUR; j++)
                for(k = 0; k <= BWT; k++)
                    for(int l = 0; l <= REP; l++)
                            for(int m = 0; m <= INV; m++)
                                    for(t = 0; t <= T+1; t++) {  
                                        fresh[i][j][k][l][m][t] = 0.0;	//reset in: dp_fut()
                                    }
        for(i = 0; i <= LAC; i++)
            for(j = 0; j <= CUR; j++) 
                for(t = 0; t <= SEA; t++) {
                    m305[i][j][t] = 0.0;	//reset in: hrd_m305()
                }
        
        for(i = 0; i <= 20; i++) zsave[i] = 0.0;      
        //reset_hrd_record2();
 
        U.println("DEFAULT_HRD_ARRAYS done ...");
    }//end: default_hrd_arrays 
    
    
    //**************************************************************************
    //Calculates the probability of the next bodyweight level given the current bodyweight level.
    void fut_pbwt() {
        if(maxbwt == 0) {
            f_pbwt[0][0] = 1.0;
        }
        else {
            f_pbwt[0][0] = f_pbwt[2][2] = (1 - bwrepeatability);
            f_pbwt[0][1] = f_pbwt[2][1] = bwrepeatability;
            f_pbwt[1][1] = 1;
        }//else fut->pbwt[][] = 0;
    }//end: fut_pbwt

    
    //**************************************************************************
    //Calculates the probability of the next milk weight level given the predicted milk yield in next lactation.
    //- Method see Van Arendonk[128]117.
    //- Regression and repeatability coefficients, see REPEATABILITY-CURVE.XLS
    void fut_pcur() {
        double	//pa_cv	= 0.0,		//coefficient of variation: see Van Arendonk[128]107
            b,                                                    //repeatability of 305-d milk yield between lactations: same as b21[0] in BIO_PROBABILITY()
            goal305, yield305, smy_, error = 0.0, perror,
            pferror, ferror, step, mlk_funk_,
            xlm, xum = 0.0, pxlm, pxum, sd, iter, pnext;
        int  nxt, nlac, cur, lac, wim, lbw;

        U.println("FUT_PCUR started ...");
        //special cases: no transition to other classes
        if(pa_cv == 0.0 || mrepeat == 1.0 || maxcur == 0) {
            U.println(" special cases:");
            U.println(" pa_cv   %f" + pa_cv);
            U.println(" mrepeat %f" + mrepeat);
            U.println(" maxcur  %d" + maxcur);      
            for(lac = 0; lac <= maxlac; lac++)
                for(wim = 0; wim <= WIM; wim++)
                    for(cur = 0; cur <= maxcur; cur++) {
                        f_pcur[lac][cur][wim][cur] = 1.0;
                    }
            U.println("FUT_PCUR done ...");
            return;
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
        b = mrepeat;
        for(lac = 1; lac <= maxlac; lac++) {																
            //U.print("a");
            nlac = lac + 1; if(nlac > maxlac) nlac = maxlac;
            for(cur = 1; cur <= CUR; cur++) {
                //U.print("b");
                sd = pa_cv * U.sqrt(1 - b*b);	
                if(sd == 0.0) sd = 0.001;
                for(wim = WPR+1; wim <= WIM; wim++) {	//normally, wim >= 41 before calving, but abortion may occur earlier
                    //U.print("c");
                					//  and also transitions cow into next lactation
                    //smy_ = smy[cur];                     //not needed here, but useful in the printf("\nDONE ..) below 
                    lbw = wim - WPR; if(lbw >= LBW) lbw = LBW;
                    if(funk == 1) mlk_funk_ = mlk_funk[lbw]; else mlk_funk_ = 1.0;
                    goal305 = 0.0;
                    yield305 = 0.0;
                    for(nxt = 1; nxt <= CUR; nxt++) {							//determine goal305
                        //U.print("d");
                        if(nxt == 1) xlm = -99; else xlm = xum;				//z-score
                        xum = (yum[nxt] - b * (smy[cur] - 100) - 100) / sd;
                        if(nxt == CUR) xum = 99;
                        pxlm = U.normal(xlm);									//cumulative P(z-score)
                        pxum = U.normal(xum);
                        f_pcur[lac][cur][wim][nxt] = pxum - pxlm;		//fut->pnxt not affected by mlk_funk, do that in the next step if hrd->funk == 1
                        
                        //U.print(space + maxlac +space+ lac +space+ nlac +space+ cur +space+ wim +space+ nxt );
                        //U.print(space + f_pcur[lac][cur][wim][nxt]);
                        //U.print("==> " + nlac + space + nxt + space);
                        //U.println(space + m305[nlac][nxt][0]);
                        
                        goal305 += f_pcur[lac][cur][wim][nxt] * m305[nlac][nxt][0] * mlk_funk_;	//no seasonality, current lactation
                        yield305 += f_pcur[lac][cur][wim][nxt] * m305[nlac][nxt][0];

			//printf("\n**lac:%d cur:%d wim:%d sd:%.3f nxt:%d xlm:%7.2f xum:%7.2f pxlm:%.4f pxum:%.4f",lac,cur,wim,sd,nxt,xlm,xum,pxlm,pxum);
                        //printf(" yum[nxt]:%.0f pnxt:%.4f m305:%.0f goal305:%.2f",hrd->yum[nxt],fut->pnxt[cur][lac][wim][nxt],hrd->m305[nxt][nlac][0],goal305);
                    }
                    if(funk == 1) {		//Funk adjustment factor for days open in current lactation on milk yield in next lactation
                        //search best distribution of fut->pnxt[cur][lac][wim][nxt]  to obtain yield305 eq. goal305.
                        smy_ = smy[cur] * mlk_funk[lbw];			//guess: see REPEATABILITY-CURVE.XLS
                        ferror = 999999;
                        step = 10.0;
                        iter = 0;
                        do {	//vary smy to obtain yield305 == goal305:
                            pnext = 0.0;
                            yield305 = 0.0;
                            for(nxt = 1; nxt <= CUR; nxt++) {
                                if(nxt == 1) xlm = -99; else xlm = xum;		//z-score
                                xum = (yum[nxt] - b * (smy_ - 100) - 100) / sd;
                                if(nxt == CUR) xum = 99;
                                pxlm = U.normal(xlm);							//cumulative P(z-score)
                                pxum = U.normal(xum);
                                f_pcur[lac][cur][wim][nxt] = pxum - pxlm;		
                                pnext += f_pcur[lac][cur][wim][nxt];		//check
                                yield305 += f_pcur[lac][cur][wim][nxt] * m305[nlac][nxt][0];

                            //	printf("\n* nxt:%d xlm:%7.2f xum:%7.2f smy:%.2f",nxt,xlm,xum,smy);
                            //	printf(" yum[nxt]:%.0f pnext:%f [%f] m305:%.0f",hrd->yum[nxt],fut->pnxt[cur][lac][wim][nxt],pnext,hrd->m305[nxt][nlac][0]);
                            //	printf(" yield305:%.4f",yield305);
                            }
                            pferror = ferror;								//previous absolute error
                            ferror = U.abs(yield305 - goal305);				//current absolute error
                            if(iter >= 1) perror = error;
                            else if(yield305 - goal305 < 0) perror = -999999; 
                            else perror = 999999;
                            error = yield305 - goal305;						//used to determine "step"
                            iter += 1.0;
                    //	printf("\n** smy:%.2f step:%f yield305:%.0f goal305:%.0f (error:%f perror:%f)\n",smy,step,yield305,goal305,error,perror);
                    //	stop(0);
                            if((error > 0 && perror < 0) || (error < 0 && perror > 0)) step = step * 0.5;
                            if(yield305 < goal305) smy_ = smy_ + step; else smy_ = smy_ - step;
                        } while(ferror > 1.00			//if error < 1 lbs then stop
                                && smy_ > 50			//if smy < 50 then stop
                                && smy_ < 150			//if smy > 150 then stop
                                && iter < 100);			//if iterations > 100 then stop
                    //	printf("\n ferror:%f  smy:%f  erpa:%f  iter:%d",ferror,smy,cow[cw]->erpa,iter);
                    //	printf("\nFUT_PNXT funk-adjustment: lac:%d cur:%d wim:%d funk:%f goal305:%.0f error:%.0f smy:%.2f iter:%4.0f",lac,cur,wim,funk,goal305,error,smy,iter);
                    //	stop(0);
                    //	if(wim == WIM) stop(0);
                    }
                    for(nxt = 1; nxt <= CUR; nxt++) {
                        //printf("\n##lac:%d cur:%d wim:%d sd:%f nxt:%d pnxt:%f",lac,cur,wim,sd,nxt,fut->pnxt[cur][lac][wim][nxt]);
                    }
                    //printf("\nFUT_PNXT DONE lac:%d cur:%d wim:%d lbw:%d b:%.3f sd:%.2f smy:%6.2f  yield305:%6.0f - goal305:%6.0f = %5.0f  iter:%4.0f\n",
                    //	lac,cur,wim,lbw,b,sd,smy,yield305,goal305,yield305-goal305,iter);
                    //if(cur == 8 && lac == 1 && wim == WIM)
                    //stop(0);
                }//wim
                for(wim = 0; wim <= WPR; wim++)								//first 40 weeks
                    for(nxt = 1; nxt <= CUR; nxt++)							//determine goal305
                        f_pcur[lac][cur][wim][nxt] = f_pcur[lac][cur][WPR+1][nxt];
            }//cur
        }//lac

        //cur 0 == cur 4
        for(lac = 1; lac <= maxlac; lac++)
            for(wim = 1; wim <= WIM; wim++)
                for(nxt = 1; nxt <= CUR; nxt++)
                    f_pcur[lac][0][wim][nxt] = f_pcur[lac][4][wim][nxt];

        //test
        for(lac = 1; lac <= maxlac; lac++)
            for(cur = 0; cur <= CUR; cur++)
                for(wim = 1; wim <= WIM; wim++) {
                    pnext = 0.0;
                    for(nxt = 1; nxt <= CUR; nxt++) {
                            pnext += f_pcur[lac][cur][wim][nxt];
                    //	printf("\n lac:%d cur:%d wim:%d nxt:%d  pnxt:%f  pnext:%.15f",lac,cur,wim,nxt,fut->pnxt[cur][lac][wim][nxt],pnext);
                    }
                    if(U.abs(pnext - 1.0) > 0.0000000001) U.stop("1");
                }
    //	stop(0);
        U.println("FUT_PCUR done ...");
    }//end: hrd_cur
    //**older comments:
    //	5/11/2007
    //	The code works, but there is a slight difference in futkeep and futserv for koe1 when milkproduction is seasonal
    //	--> Maybe this is the result of a (float)?  So if enough space, make all math (double).
    //	--> Or it is caused by a shift in t?
    //
    //	cow[cw]->pnext[nxt][t] in cow_probability()
    //	Sea is not needed for pnext, therefore t would not be needed, except that wim (and therefore t) depend on funk
    //	So if funk = 0, then pnext[nxt][t] should all be the same (run for t=1 and copy results in t=2 to 104)
    //	Add the funk coefficient to the search algorithm:  goal305 = f(pnext * hrd->m305[nxt][lac][sea]) * funk + erpa
    //	This means that anytime funk != 1 or erpa != 0, the search algorithm has to be run.
    //	But adding the funk coefficient does not change the speed of the search algorithm, only the goal305 changes slightly.
    //	(Is the search algoritm the method of steepest ascend?  How do I show it finds the optimal smy?)
    //
    //	fut->pnext[cur][lac][wim][sea][nxt] in fut_probability()
    //	It appears that [sea] is not needed: cows stay in their same cur level regardless of seasonality.
    //	Also, the weekly model already has been running with only transitions between lactations (when wpr == WPR)
    //	Also, use the same search algorithm as in cow_probability() to adjust pnxt if funk = 1.  This is a faster algorithm than the current one.
    //	Also, the transition probabilities for lact1->2, 2->3, 3->4 etc. are all the same (only 0->1 is different?)
    //	--> remove pnext calculations within lactation (only keep when wpr == WPR). This should speed up the code a lot. Pruning needed? 
    //	--> fut->pnxt[cur][wim][nxt]
    //		sea and lac not needed: they are the same. for a heifer, use fut->pnxt[0][0][nxt].
    //		wim matters because the funk coefficients depend on wim.  VWP + WPR <= wim <= LBW + WPR.
    //
    //	Other things to do:
    //	- add delay entering heifer (hrd->delay)
    //	- regression equation for hrd->fresh[][][]
    //	- test model with default hrd->data, files missing
    //	- dpweek.exe command line: "debug" for full calculations + error checks (if argv[i] == "debug" then ...)
    //	- see dpweek.xls!main for additional things to calculate (dry-off, fertility premium, monsanto) 

    //if b21 == 1 when mim >= 1 and b21 = 0.55 when mim == 0 then we are back to the
    //old model where cows only move inbetween lactations.
    //bio->pnext[0][0][0][nxt]      : probability heifer is in a 'next' level in lactation 1
    //bio->pnext[cur][lac][wim][nxt]: probability cow is in 'next' level in next week
    //b21[] correlations taken from Houben[816]2986. (were monthly --> need to be weekly)
    //
    //August 8, 2005:  MILK TRANSITION PROBABILIES and EFFECT OF PREVIOUS CALVING INTERVALS
    //Houben[816]2986 uses both current and previous lactation. 
    //It is not clear how Houben uses the method of Van Arendonk[128]118 with the correlations in Houben[816]2986
    //He calculates this in DPAMELK.PAS, but this is too complex to understand.
    //Groenendaal[]2150 uses 0.55 and 0.50 but they have only the current lactation.
    //    Maybe they predict milk production in the future instead of backwards like in DP?
    //Van Arendonk[128]119 uses 0.55 and 0.50 but he has both current and previous lactations.
    //Van Arendonk[129]16 uses 0.60 when he uses only the current lactation.
    //I use only one lactation, but still want to use milk level transition probabilities between months.
    //It is not clear how to do that from Houben's paper.
    //Let's assume that CURR level is cumulative level of milk production from mim=1 to mim = now.  Then when mim progresses,
    //   we are getting more sure about the current level of milk production.  So CURR is the predicted level of 305 d milk?
    //--> I am going to use 0.60 according to Van Arendonk[129]16 
    //Van Arendonk[674] + Dekkers[490]239 include effects of previous calving interval on current milk production.
    //   I would like to add this: can be done when cow moves to next lactation.
    //Effect of number of days open in current lactation is included through effect of pregnancy on milk yield (
    //I would also like to add the effect of previous lenght of lactations
    //--> shift distribution of transition to next lactation, use factors in Funk[1200] 
    //	for(mim = 0; mim <= 10; mim++) funk[mim] = 1.0;
    //	funk[11] = 0.980392157;	//2 months open --> calving when mim == 11, MPREG == 9
    //	funk[12] = 1.000000000; //3 monhts open --> calving when mim == 12, MPREG == 9
    //	funk[13] = 1.010101010;	//Funk[1200]
    //	funk[14] = 1.020408163;
    //	funk[15] = 1.030927835;
    //	funk[16] = 1.036269430;
    //	funk[17] = 1.041666667;
    //	funk[18] = 1.047120419;
    //	funk[19] = 1.052631579;
    //	funk[20] = 1.052631579;
    //	funk[21] = 1.052631579;
    //	funk[22] = 1.052631579;
    //	funk[23] = 1.052631579;
    //	funk[24] = 1.052631579;

    //b21 = 0.327; //correlation between the last month in the current lactation and the first month in the next lactation
    //b21[0] is 0.55 in the old model with jumps only at start of new lactations
    //0.327 was used in delayedreplacement paper but I now believe 0.60 is better.
    //--> should be same as r in COW_PROBABILIT(): Bourbon[177]232 uses 0.5
    //note b21[sea=0], not b21[mim=0]
    //	b21[1] = 0.956;	//correlation between total milk yield in month 1 and total milk yield in month 2
    //	b21[2] = 0.979;	//correlation between total milk yield in month 2 and total milk yield in month 3
    //	b21[3] = 0.988; //month 3 and month 4
    //	b21[4] = 0.991; //month 4 and month 5
    //	b21[5] = 0.993; 
    //	b21[6] = 0.994;
    //	b21[7] = 0.996;
    //	b21[8] = 0.997;
    //	b21[9] = 0.997;
    //	for(mim = 10; mim <= MIM; mim++) b21[mim] = 1.0;	//cow remains in same curr level
    //	for(wim = 1; wim <= WIM; wim++) b21[wim] = 1.0;		//cow remains in same curr level

    //b21[1] = 0.99;
    //b21[2] = 0.99;
    //b21[3] = 0.99;
    //b21[4] = 0.99;
    //bio->pa_cv = 5;
    //printf("\n  transitions simple !!!!!!!!");
    //b21[0] = 0.6; 	for(mim = 1; mim <= MIM; mim++) b21[mim] = 1.0;	//settings for lactational transition only
    /*** end FUT_PCUR ***/



    //**************************************************************************
    //FUT_PINV: Calculates the probability of involuntary culling level given  
    //the daysopen in the previous lactation.
    //- 0 = average
    //- 1 = below
    //- 2 = above
    //* 
    //* not clear what the intend is of the procedure
    void fut_pinv() {
        short	wim, type = 0;
        double	d20 = 0.0, d10 = 0.0;

        U.println("FUT_PINV started ... (needs to be fixed");
        if(maxinv == 0)
            f_pinv[0][0] = 1.0;
        else {
            d20 = invlevel[2] - invlevel[0];
            d10 = invlevel[1] - invlevel[0];
            for(wim = 1; wim <= LBW; wim++) {
    //October 2014: very unclear what the intent of this code is.            
    //            if(hrd->mlk_dopn[wim] >= hrd->invlevel[2])                          //far above
    //            {                                                                   
    //                fut->pinv[wim][2] = 1.0;
    //                type = 1;
    //            }
    //            else if(hrd->mlk_dopn[wim] >= hrd->invlevel[0])                     //above
    //            {
    //                fut->pinv[wim][0] = (hrd->mlk_dopn[wim] - hrd->invlevel[0])/d20;
    //                fut->pinv[wim][2] = (hrd->invlevel[2] - hrd->mlk_dopn[wim])/d20;
    //                type = 2;
    //            }
    //            else if(hrd->mlk_dopn[wim] >= hrd->invlevel[0])                     //below
    //            {
    //                fut->pinv[wim][0] = (hrd->mlk_dopn[wim] - hrd->invlevel[0])/d10;
    //                fut->pinv[wim][1] = (hrd->invlevel[1] - hrd->mlk_dopn[wim])/d10;
    //                type = 3;
    //            }
    //            else if(hrd->mlk_dopn[wim] > hrd->invlevel[0])  
    //            {                                                                   //far below
    //                fut->pinv[wim][0] = 1.0;
    //                type = 4;
    //            }

                //band-aid code, October 2014. This is wrong, does not include hrd->mlk_dopn
                f_pinv[wim][1] = pid[wim];
                f_pinv[wim][0] = pid[wim];
                f_pinv[wim][2] = pid[wim];
            //    printf("\n\n FUT_PINV(%d) pid[wim]:%f mlk_dopn[%d]:%f lvl[0]:%f lvl[1]:%f lvl[2]%f d20:%f d10:%f",
            //            type,hrd->pid[wim],wim,hrd->mlk_dopn[wim],hrd->invlevel[0],hrd->invlevel[1],hrd->invlevel[2],d20,d10);
            //    printf("  pinv[1]:%f  pinv[0]:%f  pinv[2]:%f",fut->pinv[wim][1],fut->pinv[wim][0],fut->pinv[wim][2]);
            }
        }
        //stop(1);
        U.println("FUT_PINV done ...");
    }//end: FUT_PINV

   
    
    //**************************************************************************
    //cowplan15.cpp  line 1425: COMPLETE_FUT_DATA()
    final void complete_hrd_data() {
        short	lac, cur, bwt, rep, wpr, wim, sea;
        double	milk, labor = 0, dmi, cost;
        double	revenue1, revenue2;
        //short	ok, sea1, sea2, sea3, wimt;

        U.println("COMPLETE_HRD_DATA started ...");
        fut_pbwt();
        fut_pcur();
        fut_pinv();
    
        U.println("a");
        for(sea = 2; sea <= SEA*2; sea++) {
            if (seasonality == true) break;
            
            if(enter[sea] != enter[sea-1]) seasonality = true;
            if(serve[sea] != serve[sea-1]) seasonality = true;

            if(U.abs(p_heifer[sea] - p_heifer[sea-1]) > 0.001) seasonality = true;
            if(U.abs(p_milk[sea] - p_milk[sea-1]) > 0.001) seasonality = true;
            if(U.abs(p_sell[sea] - p_sell[sea-1]) > 0.001) seasonality = true;
        }
        for(sea = 2; sea <= 12; sea++) {
            if(U.abs(month_conc[sea] - month_conc[sea-1]) > 0.001) seasonality = true;
            if(U.abs(month_cull[sea] - month_cull[sea-1]) > 0.001) seasonality = true;
            if(U.abs(month_milk[sea] - month_milk[sea-1]) > 0.001) seasonality = true;
            if(U.abs(month_serv[sea] - month_serv[sea-1]) > 0.001) seasonality = true;
        }
        if(seasonality == true) U.println("-");
        ////////////////////////////////////////////////////////////////////////
        U.println("b");
        for(lac = 1; lac <= maxlac; lac++) {
            for(wim = 1; wim <= WIM; wim++) {
                for(sea = 1; sea <= SEA; sea++) {
                    f_pid[lac][wim][sea] = pid[wim] * odds[lac] * thisweek_seasonality(sea,2);
                    if(f_pid[lac][wim][sea] > 1) f_pid[lac][wim][sea] = 1;          //error check
                    if(f_pid[lac][wim][sea] < -0.0001) f_pid[lac][wim][sea] = 1;    //100% cull if negative number is entered

            //	if(sea == 1) printf("\nlac:%d wim:%d %.3f",lac,wim,fut->pid[lac][wim][sea]);
            //	else printf(" %.3lf",fut->pid[lac][wim][sea]);

            //	if(sea == 1 && wim == 1)  printf("\nlac:%d wim:%d pid:%.2f odd:%.3f seas:%.2f",lac,wim,pid[wim],odds[lac],thisweek_seasonality(sea,2));
            //	if(sea == 1 && wim <= 13) printf(" %.2lf",fut->pid[lac][wim][sea]*100);

                    if(seasonality == false) break;
                }//sea
            }//wim
    	//stop(0);
        }//lac
        if(seasonality == false)
            for(lac = 1; lac <= maxlac; lac++)
                for(wim = 1; wim <= WIM; wim++)
                    for(sea = 2; sea <= SEA; sea++)
                        f_pid[lac][wim][sea] = f_pid[lac][wim][1];
        ////////////////////////////////////////////////////////////////////////
        U.println("c");
        for(cur = 0; cur <= maxcur; cur++)
            for(wim = 1; wim <= LBW; wim++)
                for(sea = 1; sea <= SEA; sea++)
                    f_servrate[cur][wim][sea] = milksr[cur] * servrate[wim] * thisweek_seasonality(sea,4);
        for(lac = 1; lac <= maxlac; lac++)
            for(rep = 0; rep <= maxrep; rep++)
                for(wim = 1; wim <= maxlbw[lac]; wim++)
                    for(sea = 1; sea <= SEA; sea++) {

                        f_concrate[lac][rep][wim][sea] = concrisk[lac] * conclevel[rep] * concrate[wim] * thisweek_seasonality(sea,1);
                        if(f_concrate[lac][rep][wim][sea] > 1) f_concrate[lac][rep][wim][sea] = 1;
                //	if(lac == 1) printf("\nwim:%d sea:%d lac:%d  servrate:%f concrate:%f",wim,sea,lac,fut->servrate[wim][sea],fut->concrate[lac][wim][sea]);
                        if(seasonality == false) break;
                    }
        //	if(wim == 64) stop(0);
        if(seasonality == false)
            for(lac = 1; lac <= maxlac; lac++)
                for(rep = 1; rep <= maxrep; rep++)
                    for(wim = 1; wim <= maxlbw[lac]; wim++)
                        for(sea = 2; sea <= SEA; sea++)
                            f_concrate[lac][rep][wim][sea] = f_concrate[lac][rep][wim][1];
        ////////////////////////////////////////////////////////////////////////
        U.println("d" + maxlac);  
        for(lac = 1; lac <= maxlac; lac++) {
            for(bwt = 0; bwt <= maxbwt; bwt++) {
                for(wim = 1; wim <= WIM; wim++) {
                    for(wpr = 0; wpr <= WPR; wpr++) { 
                        
                        //U.println("lac:" +lac+ " bwt:" + bwt + " wim:" +wim+ " wpr:" +wpr);
                        
                        f_bw[lac][bwt][wim][wpr] = bw[lac][wim] + bw_wpr[wpr];
                    //	if(curr == 8 && lact == 1 && sea == 6) 
                    //	{
                    //		printf("\n body weight");
                    //		printf("\n lac:%d wim:%d wpr:%d bw:%.3f",lac,wim,wpr,fut->bw[lac][wim][wpr]);
                    //		stop(0);
                    //	}
                    }
                    for(sea = 1; sea <= SEA; sea++) { 
                        
                        //U.println("lac:" +lac+ " bwt:" + bwt + " wim:" +wim+ " sea:" + sea);
                        //U.println(" "+bw[1][wim]+" "+bw[2][wim]+" "+p_sell[sea]+" "+p_sell[sea+52]);
                        
                        if(lac <= 1) f_sel[lac][bwt][wim][sea] = bw[1][wim] * p_sell[sea] * 0.01;
                        else	     f_sel[lac][bwt][wim][sea] = bw[2][wim] * p_sell[sea] * 0.01;				

                        if(lac <= 1) f_sel[lac][bwt][wim][sea+52] = bw[1][wim] * p_sell[sea+52] * 0.01;
                        else	     f_sel[lac][bwt][wim][sea+52] = bw[2][wim] * p_sell[sea+52] * 0.01;

                        if(seasonality == false) break;	//only sea == 1 is evaluated
                    }//sea
                }//wim
            }//bwt
        //stop(0);
        }//lac
        if(seasonality == false)
            for(lac = 1; lac <= maxlac; lac++)
                for(wim = 1; wim <= WIM; wim++)
                    for(bwt = 0; bwt <= maxbwt; bwt++)
                        for(sea = 2; sea <= SEA*2; sea++)
                            f_sel[lac][bwt][wim][sea] = f_sel[lac][bwt][wim][1];                           
        ////////////////////////////////////////////////////////////////////////
        U.println("e");
        for(lac = 1; lac <= maxlac; lac++) {
            //U.println("-" + lac);
            if(seasonality == true && cur % 2 == 0) U.print("-");
            for(cur = 0; cur <= maxcur; cur++) {
                for(bwt = 0; bwt <= maxbwt; bwt++) {
                    for(wim = 1; wim <= WIM; wim++) {
                        for(sea = 1; sea <= SEA; sea++) {
                            if(seasonality == false && sea >= 2) break;
                            for(wpr = 0; wpr <= WPR; wpr++) {
                             //   printf("\ne lac:%d cur:%d bwt:%d wim:%d sea:%d wpr:%d ",lac,cur,bwt,wim,sea,wpr);
                                if(policy == 0 && wpr >= 1) break;
                                milk = milkyield(lac,cur,wpr,wim,sea);
                                if((wpr == 0) || (wpr >= 1 && wim - wpr >= minvwp[lac] && wim - wpr <= maxlbw[lac]))
                                {
                                    if(wpr == 0) f_dailymilk_o[lac][cur][wim][sea] = milk / STAGE;	//use in dp_fut() to repl low milk yield cows
                                //    printf("a");
                                //printf("\n mlk[1][16][36]:%f  mlk[%d][%d][%d]:%f",mlk[1][16][32],lac,wim,sea,mlk[lac][wim][sea]);

                                //printf("\n mlk[1][16][36]:%f  mlk[%d][%d][%d]:%f",mlk[1][16][32],lac,wim,sea,mlk[lac][wim][sea]);

                                //printf("    %f = %f x %f x %f",milk,mlk_wpr[wpr],smy[cur]/100,mlk[lac][wim][sea]);
                                //printf("\n mlk[1][16][36]:%f  mlk[%d][%d][%d]:%f",mlk[1][16][32],lac,wim,sea,mlk[lac][wim][sea]);

                                //printf("\nmilk-->cur:%d lac:%d wim:%d wpr:%d sea:%d milk:%f",cur,lac,wim,wpr,sea,milk);

                                //    printf("b");
                                    if(milk >= 0.01) {	//cow is lactating, NRC[1112]4					
                                        if(dmi_formula == 1)	//NRC equation
                                            dmi = drymatterintake(milk,fatpct*100,f_bw[lac][bwt][wim][wpr],wim,metric);
                                        else						//simple equation
                                            dmi = dmi_maintenance + milk / dmi_production;
                                        cost = (dmi * p_tmrdmlac * 0.01 + p_other_lact) * STAGE;
                                    }
                                    else {				//cow is dry
                                        dmi = dmi_dry;			//only maintenance
                                        cost = (dmi * p_tmrdmdry * 0.01 + p_other_dry) * STAGE;
                                    }
                                 //    printf("c");
                                    revenue1 = p_milk[sea] * milk * 0.01 * STAGE;
                                    revenue2 = p_milk[sea+52] * milk * 0.01 * STAGE;
                                    revenue1 -= cost;
                                    revenue2 -= cost;
                                    if(milk >= 0.01) labor = timelact * STAGE / 60;	//lactating cow (hrs/week)
                                    if(milk  < 0.01) labor = timedry * STAGE / 60;		//dry cow (hrs/week)
                                    if(wim == 1) labor += timefresh / 60;				//fresh cow (hrs/week)
                                    revenue1 -= labor * p_labor;
                                    revenue2 -= labor * p_labor;
                                    revenue1 -= veterinarycost[wim];
                                    revenue2 -= veterinarycost[wim];

                                 //    printf("d");
                                    if(wpr == 0) {
                                        f_rev_o[lac][cur][bwt][wim][sea] = revenue1;
                                        f_rev_o[lac][cur][bwt][wim][sea+52] = revenue2;
                                    }
                                    else {
                                        if(policy >= 1) {
                                            s_rev_x[lac][cur][bwt][wpr][wim][sea] = (float)revenue1;
                                            s_rev_x[lac][cur][bwt][wpr][wim][sea+52] = (float)revenue2;
                                        }
//                                        else if(wpr >= 2) {//revenues during the first week of pregnancy are fut->rev_o[] because inseminations now start at begin of week
//                                        		//so qck->cumrev[] is for wpr = 2 to WPR
//                                                        //wim is not correct
//                                            qck->cumrev_x[lac][cur][bwt][wim][sea] += revenue1;
//                                            qck->cumrev_x[lac][cur][bwt][wim][sea+52] += revenue2;
//                                        }
                                    }
                                //    printf("e");

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

                                }//iff
                            }//wpr
                        }//sea
                    }//wim
                }//bwt
            }//cur
        }//lac

        U.println("f");
     //   stop(1);
        if(seasonality == false) {
            for(lac = 1; lac <= LAC; lac++)
                for(cur = 0; cur <= maxcur; cur++)
                    for(bwt = 0; bwt <= maxbwt; bwt++)
                        for(wim = 1; wim <= WIM; wim++)
                            for(sea = 1; sea <= SEA; sea++) {
                            //    printf("\nf lac:%d cur:%d bwt:%d wim:%d sea:%d",lac,cur,bwt,wim,sea);
                                f_dailymilk_o[lac][cur][wim][sea] = f_dailymilk_o[lac][cur][wim][1];
                                f_rev_o[lac][cur][bwt][wim][sea] = f_rev_o[lac][cur][bwt][wim][sea+52] = f_rev_o[lac][cur][bwt][wim][1]; 
                                if(policy >= 1)	{
                                    for(wpr = 1; wpr <= WPR; wpr++) if((wim - wpr >= minvwp[lac] && wim - wpr <= maxlbw[lac])) {
                                        s_rev_x[lac][cur][bwt][wpr][wim][sea] = s_rev_x[lac][cur][bwt][wpr][wim][sea+52] = s_rev_x[lac][cur][bwt][wpr][wim][1];
                                    }
                                }
//                                else //policy == 0 is qck]
//                                {
//                                    qck->cumrev_x[lac][cur][bwt][wim][sea] = qck->cumrev_x[lac][cur][bwt][wim][1];
//                                }
                            }
        }
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
        U.println("COMPLETE_HRD_DATA done ...");
    }//end: complete_hrd_data

    
    //**************************************************************************
    double wood(int day,double a, double b, double c) {
   	if(day < 0.0) { 
            U.println("WOOD error day:%f < 0"); 
            U.stop("1"); 
        }
	return a * U.pow(day,b) * U.exp(-c / 1000 * day) * 2.20462262;//kg to lbs
    }//end: wood
    
    
    //**************************************************************************
    //convert monthly input seasonality to weekly seasonality.
    //- 52 weeks, 12 months --> 4.333 weeks per month
    double thisweek_seasonality(int thisweek, int what) {
        int ii, jj;
        double fraci, fracj, seasonality_ = 0;
        double month[] = new double[13+1];

   	//U.print("THISWEEK_SEASONALITY started ... week:" + thisweek +" what:" + what);
        month[0] =  -2.166666666667;
        month[1] =   2.166666666667;		//week halfway January: gets 100% of the January seasonality
        month[2] =   6.500000000000;
        month[3] =  10.833333333333;
        month[4] =  15.166666666667;
        month[5] =  19.500000000000;
        month[6] =  23.833333333333;
        month[7] =  28.166666666667;
        month[8] =  32.500000000000;
        month[9] =  36.833333333333;
        month[10] = 41.166666666667;
        month[11] = 45.500000000000;
        month[12] = 49.833333333333;
        month[13] = 54.166666666667;

        i = 12; while(thisweek < month[i]) i--;
        j = i + 1;
        ii = i; if(i == 0) ii = 12;
        jj = j; if(j == 13) jj = 1;
        fraci = (double)thisweek - month[i];
        fracj = month[j] - (double)thisweek;
//	printf("\n what:%d  thisweek:%d  [months:i:%d  j:%d]  fraci:%f  fracj:%f ",what,thisweek,i,j,fraci,fracj);
        switch(what) {
            case 1: seasonality_ = (fraci * month_conc[jj] + fracj * month_conc[ii]) / (4.333333333333333333); break;
            case 2: seasonality_ = (fraci * month_cull[jj] + fracj * month_cull[ii]) / (4.333333333333333333); break;
            case 3: seasonality_ = (fraci * month_milk[jj] + fracj * month_milk[ii]) / (4.333333333333333333); break;
            case 4: seasonality_ = (fraci * month_serv[jj] + fracj * month_serv[ii]) / (4.333333333333333333); break;
            default: U.println("error: " + what); break;
        }
        //U.println(" seasonality:" + seasonality_);
        return seasonality_;
    }//end: thisweek_seasonality
    
    
    
    //**************************************************************************
    //Calculate daily milk yield.
    double milkyield(int lac, int cur, int wpr, int wim, int sea) {
	double milk_;

	milk_ = mlk[lac][wim] * mlkpct[lac] * thisweek_seasonality(sea,3) * mlk_wpr[wpr] * smy[cur] / 100;
	if(milk_ < dryoffyield) milk_ = 0;
//	printf("\nMILKYIELD cur:%d lac:%d wim:%d wpr:%d sea:%d",cur,lac,wim,wpr,sea);
//	printf(" hrd->mlk:%f  hrd->mlkpct:%f seasonality:%f  hrd->mlk_wpr:%f  hrd->smy/100:%f",
//		hrd->mlk[lac][wim],
//		hrd->mlkpct[lac],
//		thisweek_seasonality(sea,3),
//		hrd->mlk_wpr[wpr],
//		hrd->smy[cur]/100);
	if(milk_ < 0) {
            U.println("\nMILKYIELD cur: lac: wim: wpr: sea: milk_:" + cur + lac + wim + wpr + sea + milk_);
            U.stop("1");
            return milk_;
	}
	else return milk_;
    }//end: milkyield

    
    //**************************************************************************
    double drymatterintake(double milkyield, double fatpct, double bw, int wim, int metric) {
	double fcm, dmi;

	if(milkyield < 0.01) {//delete this code if not needed anymore
            U.println("DRYMATTERINTAKE stop() dry cow, do not use function...");
            U.stop("0");
	}

	if(metric == 1) { //inputs in kg, equation in kg, dmi in kg
            fcm = (0.4 * milkyield + 0.15 * fatpct * milkyield);			//fat correct milk per day
            dmi = (0.372 * fcm + 0.0968 * U.pow(bw,0.75)) * (1 - U.exp(- 0.192 * ((double)wim + 3.67)));
	}
        else {	//inputs in lbs, equation in kg, dmi in lbs																//calculations must happen with kg
            fcm = (0.4 * milkyield + 0.15 * fatpct * milkyield);			//fat correct milk per day
            dmi = (0.372 * (fcm/2.204624) + 0.0968 * U.pow(bw/2.204624,0.75)) * (1 - U.exp(- 0.192 * ((double)wim + 3.67)));
            dmi = dmi * 2.204624;											//dmi as lbs
	}

//	printf("\nDRYMATTERINTAKE milkyield:%.2f  fatpct:%.3f  fcm:%.2f  bw:%.2f  wim:%d  metric:%d  dmi:%.2f",
//	milkyield,fatpct,fcm,bw,wim,metric,dmi);

	return dmi;
    }// end: drymatterintake

    
    
    
    
    
    //**************************************************************************
    //CONSTRUCTOR
    Hrd() {
        pindX = new AA(-1,2,1,2,2,2,"d"); 
        milkX = new AA(2,2,5,2,2,2,"d");
        testX = new AA(-1,-1,0,0,0,2,"d");
                
        default_hrd_arrays();          
        //read_hrd_data() READS HRD DATA FROM FILE: overwrite defaults
        complete_hrd_data();
        
        //complete_fut_data();  should be in different class, not in HRD
        
        U.println("Hrd created");
    }  
  
}//end: Hrd class

    



//class Open {
//    Milk mlk[][][][] = null;
//    
//    Open(int m1, int m2, int m3, int m4, int b1, int b2, int b3, int r1, int r2, int r3) {
//        mlk = new Milk[m1+1][m2+1][m3+1][m4+1];
//        for(int i = 0; i <= m1; ++i) {
//            for(int j = 0; j <= m2; ++j) {
//                for(int k = 0; k <= m3; ++k) {
//                    for(int l = 0; l <= m4; ++l) {
//                        mlk[i][j][k][l] = new Milk(b1,b2,b3,r1,r2,r3);
//                    }
//                }
//             }
//         }
//    }
//}//Open
//
//class Bred {
//    Milk mlk[][][][] = null;
//    
//    Bred(int m1, int m2, int m3, int m4, int b1, int b2, int b3, int r1, int r2, int r3) {
//        mlk = new Milk[m1+1][m2+1][m3+1][m4+1];
//        for(int i = 0; i <= m1; ++i) {
//            for(int j = 0; j <= m2; ++j) {
//                for(int k = 0; k <= m3; ++k) {
//                    for(int l = 0; l <= m4; ++l) {
//                        mlk[i][j][k][l] = new Milk(b1,b2,b3,r1,r2,r3);
//                    }
//                }
//            }
//        }
//    }
//}//Bred
//
//class Preg {
//    Milk mlk[][][][] = null;
//    
//    Preg(int m1, int m2, int m3, int m4, int b1, int b2, int b3, int r1, int r2, int r3) {
//        mlk = new Milk[m1+1][m2+1][m3+1][m4+1];
//        for(int i = 0; i <= m1; ++i) {
//            for(int j = 0; j <= m2; ++j) {
//                for(int k = 0; k <= m3; ++k) {
//                    for(int l = 0; l <= m4; ++l) {
//                        mlk[i][j][k][l] = new Milk(b1,b2,b3,r1,r2,r3);
//                    }
//                }
//            }
//        }
//    }
//}
//
//class Repr {
//    float  rpo;
//    //double  milk;
//    //double  dmi;     
//    //double  array[];
//    Repr(){//constructor
//        //CowPlan.print("Repr: constructor:" + b1); //things I want to record
//        rpo = 5;
//        CowPlan.counter++;
//        //milk = Math.random();
//        //dmi = -3;
//        //array = new double[6];
//        //for(int i = 0; i < 6; ++i) {
//        //    array[i] = Math.random() + i;
//            //CowPlan.print("Milk:" + array[i]);
//        //}
//        //System.out.println("Milk: array.length " + array.length);
//    }
//}//Repr
//
//
//class Body {
//    Repr    rep[][][] = null;
//    Body(int r1, int r2, int r3){//constructor
//        //CowPlan.print("Body: constructor");
//        rep = new Repr[r1+1][r2+1][r3+1];
//        for(int i = 0; i <= r1; ++i) {
//            for(int j = 0; j <= r2; ++j) {
//                for(int k = 0; k <= r3; ++k) {
//                    rep[i][j][k] = new Repr();
//                }
//            }
//        }
//    }
//}//Body
//
//class Milk {
//    Body    bdy[][][] = null;
//    Milk(int b1, int b2, int b3, int r1, int r2, int r3) {//constructor
//        //CowPlan.print("Milk: constructor:" + r1);
//        bdy = new Body[b1+1][b2+1][b3+1];
//        for(int i = 0; i <= b1; ++i) {
//            for(int j = 0; j <= b2; ++j) {
//                for(int k = 0; k <= b3; ++k) {
//                    bdy[i][j][k] = new Body(r1,r2,r3);    
//                }
//            }
//        }
//    }//
//}//Milk
//










