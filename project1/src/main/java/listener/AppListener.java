package listener;

import dao.DepartmentDAO;
import dao.UserAccountDAO;
import entity.Department;
import entity.UserAccount;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class AppListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext ctx = sce.getServletContext();

        // Initialize EntityManagerFactory
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("StudentMS_PU");
        ctx.setAttribute("emf", emf);

        // Seed data on first run
        seedData(emf);

        System.out.println("==> StudentMS Application started. EMF initialized.");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        EntityManagerFactory emf = (EntityManagerFactory) sce.getServletContext().getAttribute("emf");
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
        System.out.println("==> StudentMS Application stopped. EMF closed.");
    }

    private void seedData(EntityManagerFactory emf) {
        UserAccountDAO userDAO = new UserAccountDAO(emf);
        DepartmentDAO deptDAO = new DepartmentDAO(emf);

        // Seed users only if none exist
        if (!userDAO.existsAny()) {
            userDAO.save(new UserAccount("manager", "123456", 1));
            userDAO.save(new UserAccount("staff1", "123456", 2));
            userDAO.save(new UserAccount("guest", "123456", 3));
            System.out.println("==> Sample user accounts created.");
        }

        // Seed departments only if none exist
        if (!deptDAO.existsAny()) {
            deptDAO.save(new Department("Information Technology"));
            deptDAO.save(new Department("Business Administration"));
            deptDAO.save(new Department("Software Engineering"));
            deptDAO.save(new Department("Computer Science"));
            System.out.println("==> Sample departments created.");
        }
    }
}
