package com.example.teachtrack.models;

public class SessionModel {
    private int Id, ClassId;
    private String Topic, Date;
    private boolean AttDone;

    public SessionModel(){}

    public SessionModel(int id,int classId,String topic,String date,boolean attDone){
        this.Id = id;
        this.ClassId = classId;
        this.Topic = topic;
        this.Date = date;
        this.AttDone = attDone;
    }

    public int getId(){
        return Id;
    }

    public int getClassId(){
        return ClassId;
    }

    public String getTopic(){
        return Topic;
    }

    public String getDate(){
        return Date;
    }

    public void setId(int id){
        this.Id = id;
    }

    public boolean getAttendanceDone(){return AttDone;}

    public void setAttendanceDone(boolean isDone){
        this.AttDone = isDone;
    }

    public void setClassId(int classId){
        this.ClassId = classId;
    }

    public void setTopic(String topic){
        this.Topic = topic;
    }

    public void setDate(String date){
        this.Date = date;
    }
}
