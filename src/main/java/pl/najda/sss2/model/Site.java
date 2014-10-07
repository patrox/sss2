/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.najda.sss2.model;

import java.util.List;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 *
 * @author patnaj
 */
public class Site {

    private final int order;
    private final String url;
    private final int duration;
    @JsonProperty("authentication")
    private final Authentication auth;
    private final List<String> actions;

    public Site() {
        order = 0;
        url = null;
        duration = 0;
        auth = null;
        actions = null;
    }

    public Site(int order, String url, int duration, Authentication auth, List<String> actions) {
        this.order = order;
        this.url = url;
        this.duration = duration;
        this.auth = auth;
        this.actions = actions;
    }
    
    public int getOrder() {
        return order;
    }

    public String getUrl() {
        return url;
    }

    public int getDuration() {
        return duration;
    }

    public Authentication getAuth() {
        return auth;
    }

    public List<String> getActions() {
        return actions;
    }

    public boolean isAuthRequired() {
        return (auth != null);
    }
    
    public boolean isRememberRequired() {
        return isAuthRequired() && auth.getRememberField() != null;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
//        if (isAuthRequired()) {
//            sb
//                    .append(auth.getUsername())
//                    .append("/")
//                    .append(auth.getPassword())
//                    .append(" @ ");
//        }
        
        sb.append(String.format("%d -> %s", order, url));

        return sb.toString();
    }

    public class Authentication {

        private final String usernameField;
        private final String passwordField;
        private final String rememberField;
        private final String username;
        private final String password;

        public Authentication() {
            this.usernameField = null;
            this.passwordField = null;
            this.rememberField = null;
            this.username = null;
            this.password = null;
        }

        public Authentication(
                String userNameField,
                String passwordField,
                String rememberField,
                String username,
                String password) {
            
            this.usernameField = userNameField;
            this.passwordField = passwordField;
            this.rememberField = rememberField;

            this.username = username;
            this.password = password;
        }

        public String getUsernameField() {
            return usernameField;
        }

        public String getPasswordField() {
            return passwordField;
        }
        
        public String getRememberField() {
            return rememberField;
        }

        public String getUsername() {
            return username;
        }

        public String getPassword() {
            return password;
        }
    }
}
