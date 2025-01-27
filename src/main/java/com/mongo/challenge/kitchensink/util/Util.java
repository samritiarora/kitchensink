package com.mongo.challenge.kitchensink.util;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

public class Util {
    public static String getUser() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    public static List<String> getAuthorities() {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .map(GrantedAuthority::getAuthority).toList();
    }

}
