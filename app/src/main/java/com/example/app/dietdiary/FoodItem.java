package com.example.app.dietdiary;

public class FoodItem
{

  public String name;
 public  String defaultname;
public int photo;
public int foodid;
    public FoodItem()
    {
        name=name;
        foodid=foodid;
        defaultname=defaultname;
        photo = photo;
    }
//    public FoodItem()
//    {
//
//    }
    public FoodItem(int foodid,String name,String defaultname,int photo) {
this.foodid=foodid;
        this.name = name;
        this.photo = photo;
        this.defaultname=defaultname;
    }

}
