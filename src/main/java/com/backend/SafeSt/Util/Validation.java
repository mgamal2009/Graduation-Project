package com.backend.SafeSt.Util;

public class Validation {

    public static boolean validateString(String... requestString) {
        for (String s : requestString) {
            if (s == null) return false;
            else if (s.trim().isEmpty()) return false;
            else if (s.trim().length() == 0) return false;
        }
        return true;
    }
}
