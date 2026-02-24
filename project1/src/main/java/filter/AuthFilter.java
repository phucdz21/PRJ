package filter;

import entity.UserAccount;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebFilter(urlPatterns = {"/students/*", "/departments/*"})
public class AuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        HttpSession session = req.getSession(false);

        UserAccount user = (session != null) ? (UserAccount) session.getAttribute("loggedUser") : null;

        if (user == null) {
            res.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        // Check authorization for department management (Manager only)
        String uri = req.getRequestURI();
        if (uri.contains("/departments") && user.getRole() != 1) {
            req.setAttribute("errorMsg", "You have no permission to access this function!");
            req.getRequestDispatcher("/views/error.jsp").forward(req, res);
            return;
        }

        // Guest cannot access any protected resource
        if (user.getRole() == 3) {
            req.setAttribute("errorMsg", "You have no permission to access this function!");
            req.getRequestDispatcher("/views/error.jsp").forward(req, res);
            return;
        }

        chain.doFilter(request, response);
    }
}
