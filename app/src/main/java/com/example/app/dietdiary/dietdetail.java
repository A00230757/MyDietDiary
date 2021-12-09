package com.example.app.dietdiary;



public class dietdetail {
   public String detailid;
    public String dietid;
    public String fname;
    public String nutrient;
    public String unit;
    public float quantity;
    public  String currenttime;

    public dietdetail(){
        detailid=detailid;
        dietid=dietid;
        fname=fname;
        nutrient=nutrient;
        unit=unit;
        quantity=quantity;
        currenttime=currenttime;
    }
    public dietdetail(String detailid,String dietid,String fname,String nutrient,String unit,float quantity,String currenttime)
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
