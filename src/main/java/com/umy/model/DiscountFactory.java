package com.umy.model;

//Factory design pattern to return Object Of Type Discount. Monthly/Instant
public class DiscountFactory {

        public static Discount getDiscount(String discountName){

            if(discountName.equalsIgnoreCase("Instant Discount")){
                return new InstantDiscount();
            }

            if(discountName.equalsIgnoreCase("Monthly Discount")){
                return new MonthlyDiscount();
            }
        return null;
    }
}
