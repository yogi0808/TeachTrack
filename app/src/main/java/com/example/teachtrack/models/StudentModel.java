package com.example.teachtrack.models;

public class StudentModel {
    private int Id , ClassId;
    private String Name, RollNo;
    private int presentCount,absentCount;
    public  StudentModel(){}

    public StudentModel(int id,String name,String rollNo,int classId){
        this.Id = id;
        this.Name = name;
        this.ClassId = classId;
        this.RollNo = rollNo;
    }

    public int getId(){
        return Id;
    }

    public String getName(){
        return Name;
    }

    public String getRollNo(){
        return RollNo;
    }

    public int getClassId(){
        return ClassId;
    }

    public void setId(int id){
        this.Id = id;
    }

    public void setName(String name){
        this.Name = name;
    }

    public void setRollNo(String rollNo){
        this.RollNo = rollNo;
    }

    public void setClassId(int classId){
        this.ClassId = classId;
    }

    public int getPresentCount() { return presentCount; }
    public void setPresentCount(int presentCount) { this.presentCount = presentCount; }

    public int getAbsentCount() { return absentCount; }
    public void setAbsentCount(int absentCount) { this.absentCount = absentCount; }
}
