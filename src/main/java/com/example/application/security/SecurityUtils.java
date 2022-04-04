package com.example.application.security;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.server.VaadinServletRequest;
import com.vaadin.flow.server.VaadinSession;
import javax.servlet.ServletException;

/**
 *  Manages the authentication and protects the resources by checking if user is logged in
 *  Also manages the logout session to protect the resources
 *
 */
public class SecurityUtils {

    public static String LOGOUT_SUCCESS_URL = "/";

    /**
     * VaadinServlet contains the request that is sent to the server
     * Principal contains the name of the user that makes the request
     * Vaadin constantly checks if the user is authenticated before allowing access to a requested resource and returns false if the user no longer is authenticated
     *
     * @return null if the object is null
     */
    public static boolean isAuthenticated() {
        VaadinServletRequest request = VaadinServletRequest.getCurrent();
        return request != null && request.getUserPrincipal() != null;
    }

    /**
     * VaadinServlet first authenticates and then binds the request to an authenticated user
     * Authenticate the user with the given credentials. The authenticate() method fails if it is called from a background thread; call must come from the foreground.
     * @param username
     * @param password
     * @return false if the user is not authorized to login
     */
    public static boolean authenticate(String username, String password) {
        VaadinServletRequest request = VaadinServletRequest.getCurrent();
        if (request == null) {
            return false;
        } try {
            request.login(username, password);
            return true;
        } catch (ServletException e) {
            return false;
        }
    }

    /**
     * Invalidate the session when user logs out and redirect user to the main page
     *
     */
    public static void logout() {
        VaadinSession.getCurrent().getSession().invalidate();
        UI.getCurrent().getPage().setLocation(LOGOUT_SUCCESS_URL);
    }
}
