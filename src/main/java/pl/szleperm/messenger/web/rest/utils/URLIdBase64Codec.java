package pl.szleperm.messenger.web.rest.utils;

import java.util.Base64;

/**
 * @author Marcin Szleper
 */
public class URLIdBase64Codec {

    @SuppressWarnings("WeakerAccess")
    public String encode(String name){
        return Base64.getEncoder().encodeToString(name.getBytes());
    }
    public String decode(String id){

        return id.length() < 2 ? id : new String(Base64.getDecoder().decode(id));
    }
}
