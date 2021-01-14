package com.example.orderfoodandroidsever.model;

import java.util.List;


public class Request {
    private String latLog, phone, name, address, total, status,timeStamp;
    private List<Order> foods;

    public Request() {
    }

//    public Request(String phone, String name, String address, String total, String status, List<Order> foods) {
//        this.phone = phone;
//        this.name = name;
//        this.address = address;
//        this.total = total;
//        this.status = "0";
//        this.foods = foods;
//    }


    public Request(String latLog, String phone, String name, String address, String total, String status, String timeStamp, List<Order> foods) {
        this.latLog = latLog;
        this.phone = phone;
        this.name = name;
        this.address = address;
        this.total = total;
        this.status = "0";
        this.timeStamp = timeStamp;
        this.foods = foods;
    }

    public String getLatLog() {
        return latLog;
    }

    public void setLatLog(String latLog) {
        this.latLog = latLog;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Order> getFoods() {
        return foods;
    }

    public void setFoods(List<Order> foods) {
        this.foods = foods;
    }
}
