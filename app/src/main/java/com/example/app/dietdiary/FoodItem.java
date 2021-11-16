package com.example.app.dietdiary;

public class FoodItem
{

   String name;
   String defaultname;
int photo;
int foodid;

    public FoodItem(int foodid,String name,String defaultname,int photo) {
this.foodid=foodid;
        this.name = name;
        this.photo = photo;
        this.defaultname=defaultname;
    }

}
