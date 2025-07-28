package com.example.teachtrack.models;

public class AttendanceModel {
    private int Id, SessionId, StudentId;

    private boolean IsPresent;

    public AttendanceModel(){}

    public AttendanceModel(int id,int sessionId,int studentId,boolean isPresent){
        this.Id = id;
        this.SessionId = sessionId;
        this.StudentId = studentId;
        this.IsPresent = isPresent;
    }

    public int getId(){
        return Id;
    }

    public int getSessionId(){
        return SessionId;
    }

    public int getStudentId(){
        return StudentId;
    }

    public boolean isPresent(){
        return IsPresent;
    }

    public void setId(int id){
        this.Id = id;
    }

    public void setSessionId(int sessionId){
        this.SessionId = sessionId;
    }

    public void setStudentId(int studentId){
        this.StudentId = studentId;
    }

    public void setPresent(boolean present){
        this.IsPresent = present;
    }
}
