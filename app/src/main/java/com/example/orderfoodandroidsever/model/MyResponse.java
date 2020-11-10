package com.example.orderfoodandroidsever.model;

import java.util.List;

public class MyResponse {
    public long multicast_id;
    public int success;
    public int pubfailure;
    public int canonical_ids;
    public List<Result> results;
}
