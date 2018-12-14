/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cowplan16package;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;



public class Io {

    static int maxlac;
    static double heiferprice;
    static double cowprice;
    
    public static String[] readcsv()  { //throws FileNotFoundException
        U.println("readcsv started ...");
        String fileNameDefined = "C:\\Users\\devries\\Dropbox\\Projects\\COWplan\\cowplan16dir\\commadelimited.csv";
        File filein = new File(fileNameDefined);
        
        String records[] = null;
        
        try {
            try (Scanner scan = new Scanner(filein)) {
                scan.useDelimiter(",");
                  
//            //option 1
//            while(scan.hasNext() == true){
//                System.out.print(scan.next()+"|");
//            }

                //option 2                
                List<String> record = new ArrayList<>();
                while (scan.hasNext()) {
                    record.add(scan.nextLine());
                }
//                for (String data : record) {
//                    System.out.println(data);   
//                }
                records = record.toArray(new String[0]);
                System.out.println("Number of records: " + records.length);
                
                
                //parsefarmdata(records);
//                for(int i = 0; i < records.length; i++) {
//                    //U.println(i + "[" + records[i] + "]");
//                    
//                    String onerecord[] = records[i].trim().split(",");
//                    double doubleArray[] = new double[onerecord.length];
//                    for (int j = 0; j < onerecord.length; j++) {
//                         //String numberAsString = onerecord[j];
//                         //intArray[j] = numberAsString;
//                         if(j > 0)
//                            doubleArray[j] = Double.parseDouble(onerecord[j]);
//                    }
//                    System.out.println("Number of values: " + onerecord.length);
//                    System.out.print("The values are:");
//                    for (String value : onerecord) {
//                        System.out.print(" " + value);
//                    }
//                    System.out.println();

//                    System.out.print("The doubles are:");                    
//                    for (double value : doubleArray) {
//                        System.out.print(" " + value);
//                    }
//                    System.out.println();
//                } 

                U.println("readcsv done ...");
                return records;                  
            }
        } catch (FileNotFoundException e) {
            U.println(filein + "not found " + e.getLocalizedMessage());
            U.stop("0");
            System.exit(0);
        }
        return records;
    }//end readcsv
    
    
    static void parsefarmdata(String[] records) {
        int problems = 0;
        for(int i = 0; i < records.length; i++) {
            String onerecord[] = records[i].trim().split(",");
            System.out.println("Number of values for record " + i + " = " + onerecord.length);
            System.out.print("The values are:");
            for (String value : onerecord) {
                System.out.print(" " + value);
            }
            System.out.println();

            switch (onerecord[0]) {
                case "aa": maxlac = Integer.valueOf(onerecord[1]); break;
                case "bbb": heiferprice = Double.valueOf(onerecord[1]); break;
                case "cccc": cowprice = Double.valueOf(onerecord[1]); break;
                default: problems++; break;
            }
        } 
        U.println("problems= " + problems);        
        U.println("maxlac= " + maxlac);
        U.println("heiferprice= " + heiferprice);
        U.println("cowprice= " + cowprice);
    }
    
    
}//class: Io


