package controller;


import dao.DepartmentDAO;
import entity.Department;
import jakarta.persistence.EntityManagerFactory;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/departments")
public class DepartmentServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        EntityManagerFactory emf = (EntityManagerFactory) getServletContext().getAttribute("emf");
        DepartmentDAO deptDAO = new DepartmentDAO(emf);

        String action = req.getParameter("action");

        if ("delete".equals(action)) {
            String idParam = req.getParameter("id");
            deptDAO.delete(Integer.parseInt(idParam));
            resp.sendRedirect(req.getContextPath() + "/departments");
            return;
        }

        if ("edit".equals(action)) {
            String idParam = req.getParameter("id");
            Department dept = deptDAO.findById(Integer.parseInt(idParam));
            req.setAttribute("editDept", dept);
        }

        req.setAttribute("departments", deptDAO.findAll());
        req.getRequestDispatcher("/views/departments.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");
        EntityManagerFactory emf = (EntityManagerFactory) getServletContext().getAttribute("emf");
        DepartmentDAO deptDAO = new DepartmentDAO(emf);

        String action = req.getParameter("action");
        String name = req.getParameter("departmentName");

        List<String> errors = new ArrayList<>();
        if (name == null || name.trim().length() < 5 || name.trim().length() > 50) {
            errors.add("Department name must be 5–50 characters.");
        }

        if (!errors.isEmpty()) {
            req.setAttribute("errors", errors);
            req.setAttribute("departments", deptDAO.findAll());
            req.getRequestDispatcher("/views/departments.jsp").forward(req, resp);
            return;
        }

        if ("add".equals(action)) {
            deptDAO.save(new Department(name.trim()));
        } else if ("update".equals(action)) {
            int id = Integer.parseInt(req.getParameter("id"));
            Department dept = deptDAO.findById(id);
            if (dept != null) {
                dept.setDepartmentName(name.trim());
                deptDAO.update(dept);
            }
        }

        resp.sendRedirect(req.getContextPath() + "/departments");
    }
}
