package com.example.app.dietdiary;

public class FoodItemDetail
{

   public String nutritionname;
    public String fooditemname;
   public String unit;
   public  float quantity;
  public String key;


    public FoodItemDetail() {

        nutritionname=nutritionname;
        unit=unit;
        quantity=quantity;
        fooditemname=fooditemname;
        key = key;

    }
    public FoodItemDetail(String key,String nutritionname,String unit,float quantity,String fooditemname) {

        this.nutritionname=nutritionname;
        this.key=key;
        this.unit=unit;
        this.quantity=quantity;
        this.fooditemname = fooditemname;

    }

}