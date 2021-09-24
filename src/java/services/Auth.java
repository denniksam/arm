package services;

import java.util.Date;

/**
 * Предоставление информации об авторизации пользователя - был вход или нет
 */
public class Auth {
    
    private static orm.User user = null ;
    private static long loginTime ;

    public static long getLoginDuration() {
        return new Date().getTime() - loginTime ;
    }
    
    public static long getLoginTime() {
        return loginTime;
    }

    public static void setLoginTime(long loginTime) {
        Auth.loginTime = loginTime;
    }

    public static orm.User getUser() {
        return user;
    }

    public static void setUser(orm.User user) {
        Auth.user = user;
    }
    
    public static boolean isUserAdmin() {
        if( user == null ) return false ;
        return user.getId().equals( "CB0307F6EBFD9840E05011AC040030C8" ) ;
    }
}
