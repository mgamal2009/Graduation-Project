package com.backend.SafeSt.Util;

public class ResponseMessage {

    public static final String EXECUTED = "Executed Successfully";
    public static final String UPDATED = "Updated Successfully";
    public static final String DELETED = "Deleted Successfully";
    public static final String CREATED = "Created Successfully";
    public static final String ADDED = "Added Successfully";

    public static String noIdWith(String value,long id){
        return "No "+value+" with Id: "+id;
    }

}
