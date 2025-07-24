package com.sun.utils;

public class FixJsonUrl {

    public static String encodeURI(String uri) {
        String newUri = "";
        if(uri.charAt(0)=='[' && uri.charAt(uri.length()-1)==']'){
            newUri = uri.substring(1, uri.length()-1);
        }
        return "%5B" + newUri + "%5D";
    }

    public static String recodeURI(String uri) {
        return "[" + uri.substring(3, uri.length()-3) + "]";
    }
}
