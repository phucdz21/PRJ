package controller;

import dao.UserAccountDAO;
import entity.UserAccount;
import jakarta.persistence.EntityManagerFactory;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        // If already logged in, redirect
        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute("loggedUser") != null) {
            resp.sendRedirect(req.getContextPath() + "/students");
            return;
        }
        req.getRequestDispatcher("/views/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String username = req.getParameter("username");
        String password = req.getParameter("password");

        EntityManagerFactory emf = (EntityManagerFactory) getServletContext().getAttribute("emf");
        UserAccountDAO userDAO = new UserAccountDAO(emf);

        UserAccount user = userDAO.findByUsernameAndPassword(username, password);

        if (user == null) {
            req.setAttribute("errorMsg", "Username or password is invalid!");
            req.getRequestDispatcher("/views/login.jsp").forward(req, resp);
            return;
        }

        if (user.getRole() == 3) {
            req.setAttribute("errorMsg", "You have no permission to access this function!");
            req.getRequestDispatcher("/views/login.jsp").forward(req, resp);
            return;
        }

        // Save to session
        HttpSession session = req.getSession();
        session.setAttribute("loggedUser", user);
        resp.sendRedirect(req.getContextPath() + "/students");
    }
}
