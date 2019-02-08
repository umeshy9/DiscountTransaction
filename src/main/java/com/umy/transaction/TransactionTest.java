package com.umy.transaction;

import com.umy.model.*;
import com.umy.util.DateParser;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.*;

public class TransactionTest {

    private static List<Discount> monthlyDiscountList = new ArrayList<>();
    private static List<Discount> instantDiscountList = new ArrayList<>();
    private static List<TransactionDAO> transactionDAOList = new ArrayList<>();
    private static String discountName;

    public static void main(String[] args) throws ParseException {
        String fileName1 = "file/testfile.txt";
        String fileName2 = "file/slabs.txt";
        getFile(fileName1);
        printDAOList();
        getFile(fileName2);
        printDiscountList();
        calculateTransaction();
    }
    // Get File From Resource and Call To Parse data from File.
    private static void getFile(String fileName){
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        if(classLoader != null){
            File file = new File(classLoader.getResource(fileName).getFile());
            parseFileData(file);
        }else
            System.out.println("No File Found! Please Check resources");
    }
    /*
        Method to calculate transacted amount and sum of total transaction for each one month
        -------------------------------------------------------------------------------------
        To Do List
        1. apply instant discount on each row's date amount.
        2. apply monthly discount on each total amount for a month.
     */
    private static void calculateTransaction() throws ParseException {
        Map<String , Double> map = new HashMap<>();
        double sum = 0.0;
        String dateString = transactionDAOList.get(0).getDate();
        for (TransactionDAO transaction: transactionDAOList) {
            if(DateParser.parseDate(dateString).compareTo(DateParser.parseDate(transaction.getDate())) == 0){
                sum += calculateAmount(transaction.getAmount());
                continue;
            }
            map.put(DateParser.parseDate(dateString), sum);
            if(DateParser.parseDate(dateString).compareTo(DateParser.parseDate(transaction.getDate())) <0){
                sum = calculateAmount(transaction.getAmount());
                dateString = transaction.getDate();
            }
        }
        map.put(DateParser.parseDate(dateString), sum);
        System.out.println(map);
    }

    private static void printDAOList(){
        for (TransactionDAO transaction:transactionDAOList) {
            System.out.println("Amount:\t"+transaction.getAmount()+"\t Date:\t"+transaction.getDate());
        }
    }

    private static double calculateAmount(double amount){
        for (Discount instantDiscount : instantDiscountList) {
            if(amount > instantDiscount.getLowerLimit() && amount <instantDiscount.getUpperLimit()){
                amount -= (amount* Double.parseDouble(instantDiscount.getDiscountPercent().substring(0,2)))/100;
            }
        }
        return amount;
    }


    private static void printDiscountList(){
        System.out.println("Instant Discount:");
        for (Discount discount: instantDiscountList) {
            System.out.println(discount.getLowerLimit() +"\t" + discount.getUpperLimit() + "\t"+ discount.getDiscountPercent());
        }
        System.out.println("Monthly Discount:");
        for (Discount discount:monthlyDiscountList) {
            System.out.println(discount.getLowerLimit() +"\t" + discount.getUpperLimit() + "\t"+ discount.getDiscountPercent());
        }
    }

    //parse File data from slab.txt and testfile.txt
    private static void parseFileData(File file){

        switch(file.getName()){
            case "testfile.txt" :
                    try (Scanner scanner = new Scanner(file)) {
                        while (scanner.hasNextLine()) {
                            String line = scanner.nextLine();
                            processTestFile(line);
                        }
                    }catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
            case "slabs.txt" :
                    try (Scanner scanner = new Scanner(file)) {
                        while (scanner.hasNextLine()) {
                            String line = scanner.nextLine();
                            processSlabFile(line);
                        }
                    }catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
        }
    }

    //to extract file data from slabs.txt
    private static void processSlabFile(String line){

        if(!line.contains("-")){
            discountName = line;
            return;
        }

        Discount discount = DiscountFactory.getDiscount(discountName);

        //String slabList[] = line.split("-");
        if(discount instanceof InstantDiscount){
            Discount discountParams = setDiscountParams(discount,line);
            instantDiscountList.add(discountParams);
        }

        if(discount instanceof MonthlyDiscount){
            Discount discountParams = setDiscountParams(discount,line);
            monthlyDiscountList.add(discountParams);
        }
    }
    // set values for Discount instance parameters a single line recieved from slabs.txt file
    private static Discount setDiscountParams(Discount discountParams, String line){
        String slabList[] = line.split("-");
        discountParams.setLowerLimit(Integer.parseInt(slabList[0]));
        discountParams.setUpperLimit(Integer.parseInt(slabList[1]));
        discountParams.setDiscountPercent(slabList[2]);
        return  discountParams;
    }

    //to extract data from testfile.txt
    private static void processTestFile(String line){
        String dataList [] = line.split("\\s");
        TransactionDAO transactionDAO = new TransactionDAO();
        transactionDAO.setDate(dataList[0]);
        transactionDAO.setAmount(Double.parseDouble(dataList[1]));
        transactionDAOList.add(transactionDAO);
    }
}
