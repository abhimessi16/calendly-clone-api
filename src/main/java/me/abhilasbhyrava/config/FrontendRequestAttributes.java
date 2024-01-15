package me.abhilasbhyrava.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestAttributes;

@Component
@Getter
@Setter
public class FrontendRequestAttributes implements RequestAttributes {

    private String headerValue;

    @Override
    public Object getAttribute(String name, int scope) {
        return headerValue;
    }

    @Override
    public void setAttribute(String name, Object value, int scope) {
        this.setHeaderValue((String) value);
    }

    @Override
    public void removeAttribute(String name, int scope) {
        this.setHeaderValue(null);
    }

    @Override
    public String[] getAttributeNames(int scope) {
        return new String[]{"frontend"};
    }

    @Override
    public void registerDestructionCallback(String name, Runnable callback, int scope) {

    }

    @Override
    public Object resolveReference(String key) {
        return null;
    }

    @Override
    public String getSessionId() {
        return null;
    }

    @Override
    public Object getSessionMutex() {
        return null;
    }
}
