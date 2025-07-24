package com.sun.utils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

public class CookieUtils {

    public static String getCookieValue(HttpServletRequest request, String cookieName) {
        String cookieValue = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(cookieName)) {
                    cookieValue = cookie.getValue();
                    break;
                }
            }
        }
        return cookieValue;
    }

}
