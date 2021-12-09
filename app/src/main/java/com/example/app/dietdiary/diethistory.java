package com.example.app.dietdiary;



public class diethistory {
   public String fooditem;
    public String diettype;
    public float calories;
    public int count;
    public String currenttime;
    public String dietid;

    public diethistory(){
        fooditem=fooditem;
        diettype=diettype;
        calories=calories;
        count=count;
        currenttime=currenttime;
        dietid=dietid;
    }

    public diethistory(String fooditem,String diettype,Float calories,int count,String currenttime,String dietid){
        this.fooditem=fooditem;
        this.diettype=diettype;
        this.calories=calories;
        this.count=count;
        this.currenttime=currenttime;
        this.dietid=dietid;
    }

}
