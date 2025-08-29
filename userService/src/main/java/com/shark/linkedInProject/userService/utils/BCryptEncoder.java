package com.shark.linkedInProject.userService.utils;

import org.mindrot.jbcrypt.BCrypt;

public class BCryptEncoder {

    public static String hash(String s) {
        return BCrypt.hashpw(s, BCrypt.gensalt());
    }

    public static boolean match(String passwordText, String passwordHashed) {
        return BCrypt.checkpw(passwordText, passwordHashed);
    }

}
