package com.taild.simpleshopapp.component;


import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import com.taild.simpleshopapp.models.User;

@Component
public class SecurityUtils {

    public User getLoggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null &&
                authentication.getPrincipal() instanceof User selectedUser) {
            if(!selectedUser.isActive()) {
                return null;
            }
            return selectedUser;
        }
        return null;
    }
}
