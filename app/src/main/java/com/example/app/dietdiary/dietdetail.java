package com.example.app.dietdiary;

/**
 * Created by Harpreet on 9/17/2018.
 */

public class dietdetail {
    int detailid;
    int dietid;
    String fname;
    String nutrient;
    String unit;
    float quantity;
    String currenttime;
    public dietdetail(int detailid,int dietid,String fname,String nutrient,String unit,float quantity,String currenttime)
    {
      this.detailid=detailid;
      this.dietid=dietid;
      this.fname=fname;
      this.nutrient=nutrient;
      this.unit=unit;
      this.quantity=quantity;
      this.currenttime=currenttime;
    }

}
