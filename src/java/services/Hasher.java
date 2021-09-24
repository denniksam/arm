package services;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.xml.bind.DatatypeConverter;

/*
    Класс для хеширования строк
 */
public class Hasher {
    
    public static String MD5( String str ) {
        try {
            MessageDigest md = MessageDigest.getInstance( "MD5" ) ;
            return DatatypeConverter.printHexBinary(  // пребразовываем в строку
                md.digest( str.getBytes() ) ) ;   // байты входной строки
                
        } catch( NoSuchAlgorithmException ex ) {
            System.err.println( "Hasher: " + ex.getMessage() ) ;
            return null ;
        }
    }
}
