package com.example.teachtrack.models;

public class ClassModel {
    private int Id, StudentCount;
    private String Name;

    public ClassModel(){

    }

    public ClassModel(int id,String name,int studentCount){
        this.Id = id;
        this.StudentCount = studentCount;
        this.Name = name;
    }

    public int getId(){
        return Id;
    }

    public void setId(int id){
        this.Id = id;
    }

    public String getName(){
        return Name;
    }

    public void setName(String name){
        this.Name = name;
    }

    public int getStudentCount(){
        return StudentCount;
    }

    public void setStudentCount(int studentCount){
        this.StudentCount = studentCount;
    }
}
