package com.backend.SafeSt.Util;

public class Validation {

    public static boolean validateString(String... requestString) {
        for (String s : requestString) {
            if (s==null) return false;
            else if (s.trim().isEmpty()) return false;
            else if (s.trim().length() == 0) return false;
        }
        return true;
    }
    public static boolean validateLong(Object... object) {
        for (Object s : object) {
            if(s instanceof Long) {
                return true;
            } else {
                String string = object.toString();

                try {
                    Long.parseLong(string);
                } catch(Exception e) {
                    return false;
                }
            }
        }
        return true;
    }
    public static boolean validateInt(Object... object) {
        for (Object s : object) {
            if(s instanceof Integer) {
                return true;
            } else {
                String string = object.toString();

                try {
                    Integer.parseInt(string);
                } catch(Exception e) {
                    return false;
                }
            }
        }
        return true;
    }
    public static boolean validateDouble(Object... object) {
        for (Object s : object) {
            if(s instanceof Double) {
                return true;
            } else {
                String string = object.toString();

                try {
                    Double.parseDouble(string);
                } catch(Exception e) {
                    return false;
                }
            }
        }
        return true;
    }
}
