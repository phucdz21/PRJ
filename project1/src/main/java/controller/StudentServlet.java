package controller;

import dao.DepartmentDAO;
import dao.StudentDAO;
import entity.Department;
import entity.Student;
import entity.UserAccount;
import jakarta.persistence.EntityManagerFactory;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/students")
public class StudentServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        UserAccount user = (UserAccount) session.getAttribute("loggedUser");

        EntityManagerFactory emf = (EntityManagerFactory) getServletContext().getAttribute("emf");
        StudentDAO studentDAO = new StudentDAO(emf);
        DepartmentDAO deptDAO = new DepartmentDAO(emf);

        String action = req.getParameter("action");
        if (action == null) action = "list";

     switch (action) {
    case "list":
        handleList(req, resp, user, studentDAO, deptDAO);
        break;
    case "edit":
        handleEditForm(req, resp, user, studentDAO, deptDAO);
        break;
    case "delete":
        handleDelete(req, resp, user, studentDAO);
        break;
    default:
        handleList(req, resp, user, studentDAO, deptDAO);
        break;
}
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");
        HttpSession session = req.getSession(false);
        UserAccount user = (UserAccount) session.getAttribute("loggedUser");

        // Staff only for CRUD
        if (user.getRole() != 2) {
            resp.sendRedirect(req.getContextPath() + "/students");
            return;
        }

        EntityManagerFactory emf = (EntityManagerFactory) getServletContext().getAttribute("emf");
        StudentDAO studentDAO = new StudentDAO(emf);
        DepartmentDAO deptDAO = new DepartmentDAO(emf);

        String action = req.getParameter("action");
        if ("add".equals(action)) {
            handleAdd(req, resp, user, studentDAO, deptDAO);
        } else if ("update".equals(action)) {
            handleUpdate(req, resp, user, studentDAO, deptDAO);
        }
    }

    // ===========================
    // HANDLERS
    // ===========================

    private void handleList(HttpServletRequest req, HttpServletResponse resp,
                            UserAccount user, StudentDAO studentDAO, DepartmentDAO deptDAO)
            throws ServletException, IOException {

        List<Student> students;
        int totalPages = 1;
        int currentPage = 1;

        if (user.getRole() == 1) {
            // Manager: top 5 by GPA only
            students = studentDAO.findTop5ByGpa();
        } else {
            // Staff: paginated
            String pageParam = req.getParameter("page");
            currentPage = (pageParam != null) ? Integer.parseInt(pageParam) : 1;
            if (currentPage < 1) currentPage = 1;

            students = studentDAO.findAll(currentPage);
            long totalStudents = studentDAO.countAll();
            totalPages = (int) Math.ceil((double) totalStudents / studentDAO.getPageSize());
            if (totalPages < 1) totalPages = 1;
        }

        req.setAttribute("students", students);
        req.setAttribute("departments", deptDAO.findAll());
        req.setAttribute("currentPage", currentPage);
        req.setAttribute("totalPages", totalPages);
        req.getRequestDispatcher("/views/students.jsp").forward(req, resp);
    }

    private void handleEditForm(HttpServletRequest req, HttpServletResponse resp,
                                UserAccount user, StudentDAO studentDAO, DepartmentDAO deptDAO)
            throws ServletException, IOException {

        if (user.getRole() != 2) {
            resp.sendRedirect(req.getContextPath() + "/students");
            return;
        }

        String idParam = req.getParameter("id");
        Student student = studentDAO.findById(Integer.parseInt(idParam));

        if (student == null || !student.getCreatedBy().equals(user.getUsername())) {
            req.setAttribute("errorMsg", "You do not have permission to edit this student.");
            List<Student> students = studentDAO.findAll(1);
            req.setAttribute("students", students);
            req.setAttribute("departments", deptDAO.findAll());
            req.setAttribute("currentPage", 1);
            req.setAttribute("totalPages", 1);
            req.getRequestDispatcher("/views/students.jsp").forward(req, resp);
            return;
        }

        req.setAttribute("editStudent", student);
        req.setAttribute("students", studentDAO.findAll(1));
        req.setAttribute("departments", deptDAO.findAll());
        req.setAttribute("currentPage", 1);
        long total = studentDAO.countAll();
        req.setAttribute("totalPages", (int) Math.ceil((double) total / studentDAO.getPageSize()));
        req.getRequestDispatcher("/views/students.jsp").forward(req, resp);
    }

    private void handleDelete(HttpServletRequest req, HttpServletResponse resp,
                              UserAccount user, StudentDAO studentDAO)
            throws IOException {

        if (user.getRole() != 2) {
            resp.sendRedirect(req.getContextPath() + "/students");
            return;
        }

        String idParam = req.getParameter("id");
        Student student = studentDAO.findById(Integer.parseInt(idParam));

        if (student != null && student.getCreatedBy().equals(user.getUsername())) {
            studentDAO.delete(student.getId());
        }

        resp.sendRedirect(req.getContextPath() + "/students");
    }

    private void handleAdd(HttpServletRequest req, HttpServletResponse resp,
                           UserAccount user, StudentDAO studentDAO, DepartmentDAO deptDAO)
            throws ServletException, IOException {

        List<String> errors = validateForm(req);

        if (!errors.isEmpty()) {
            req.setAttribute("errors", errors);
            req.setAttribute("students", studentDAO.findAll(1));
            req.setAttribute("departments", deptDAO.findAll());
            req.setAttribute("currentPage", 1);
            long total = studentDAO.countAll();
            req.setAttribute("totalPages", (int) Math.ceil((double) total / studentDAO.getPageSize()));
            req.getRequestDispatcher("/views/students.jsp").forward(req, resp);
            return;
        }

        Student student = buildStudentFromRequest(req, deptDAO);
        student.setCreatedAt(LocalDate.now());
        student.setUpdatedAt(LocalDate.now());
        student.setCreatedBy(user.getUsername());

        studentDAO.save(student);
        resp.sendRedirect(req.getContextPath() + "/students");
    }

    private void handleUpdate(HttpServletRequest req, HttpServletResponse resp,
                              UserAccount user, StudentDAO studentDAO, DepartmentDAO deptDAO)
            throws ServletException, IOException {

        String idParam = req.getParameter("id");
        Student existing = studentDAO.findById(Integer.parseInt(idParam));

        if (existing == null || !existing.getCreatedBy().equals(user.getUsername())) {
            resp.sendRedirect(req.getContextPath() + "/students");
            return;
        }

        List<String> errors = validateForm(req);
        if (!errors.isEmpty()) {
            req.setAttribute("errors", errors);
            req.setAttribute("editStudent", existing);
            req.setAttribute("students", studentDAO.findAll(1));
            req.setAttribute("departments", deptDAO.findAll());
            req.setAttribute("currentPage", 1);
            long total = studentDAO.countAll();
            req.setAttribute("totalPages", (int) Math.ceil((double) total / studentDAO.getPageSize()));
            req.getRequestDispatcher("/views/students.jsp").forward(req, resp);
            return;
        }

        String name = req.getParameter("name");
        float gpa = Float.parseFloat(req.getParameter("gpa"));
        int deptId = Integer.parseInt(req.getParameter("departmentId"));
        Department dept = deptDAO.findById(deptId);

        existing.setName(name);
        existing.setGpa(gpa);
        existing.setDepartment(dept);
        existing.setUpdatedAt(LocalDate.now());

        studentDAO.update(existing);
        resp.sendRedirect(req.getContextPath() + "/students");
    }

    // ===========================
    // HELPERS
    // ===========================

    private List<String> validateForm(HttpServletRequest req) {
        List<String> errors = new ArrayList<>();
        String name = req.getParameter("name");
        String gpaStr = req.getParameter("gpa");
        String deptIdStr = req.getParameter("departmentId");

        if (name == null || name.trim().length() < 5 || name.trim().length() > 50) {
            errors.add("Name must be 5–50 characters.");
        }

        try {
            float gpa = Float.parseFloat(gpaStr);
            if (gpa < 0.0f || gpa > 10.0f) {
                errors.add("GPA must be between 0.0 and 10.0.");
            }
        } catch (NumberFormatException e) {
            errors.add("GPA must be a valid number.");
        }

        if (deptIdStr == null || deptIdStr.isEmpty()) {
            errors.add("Department must not be empty.");
        }

        return errors;
    }

    private Student buildStudentFromRequest(HttpServletRequest req, DepartmentDAO deptDAO) {
        Student student = new Student();
        student.setStudentId(req.getParameter("studentId"));
        student.setName(req.getParameter("name").trim());
        student.setGpa(Float.parseFloat(req.getParameter("gpa")));
        int deptId = Integer.parseInt(req.getParameter("departmentId"));
        student.setDepartment(deptDAO.findById(deptId));
        return student;
    }
}
