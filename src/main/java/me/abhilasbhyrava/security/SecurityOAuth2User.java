package me.abhilasbhyrava.security;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Map;

@Getter
@ToString
public class SecurityOAuth2User {

    private Map<String, Object> attributes;

    private String name;
    private String email;

    public SecurityOAuth2User(Map<String, Object> attributes){
        this.attributes = attributes;
    }

    public String getName(){
        return (String) this.attributes.get("name");
    }

    public String getEmail(){
        return (String) this.attributes.get("email");
    }
}
