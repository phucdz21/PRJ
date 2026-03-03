package dao;


import entity.Student;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;
import java.util.List;

public class StudentDAO {

    private final EntityManagerFactory emf;
    private static final int PAGE_SIZE = 5;

    public StudentDAO(EntityManagerFactory emf) {
        this.emf = emf;
    }

    /** Staff: paginated list of all students */
    public List<Student> findAll(int page) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Student> query = em.createQuery(
                "SELECT s FROM Student s ORDER BY s.id", Student.class);
            query.setFirstResult((page - 1) * PAGE_SIZE);
            query.setMaxResults(PAGE_SIZE);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    /** Total student count (for pagination) */
    public long countAll() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT COUNT(s) FROM Student s", Long.class)
                     .getSingleResult();
        } finally {
            em.close();
        }
    }

    /** Manager: Top 5 students by GPA */
    public List<Student> findTop5ByGpa() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery(
                "SELECT s FROM Student s ORDER BY s.gpa DESC", Student.class)
                .setMaxResults(5)
                .getResultList();
        } finally {
            em.close();
        }
    }

    public Student findById(int id) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.find(Student.class, id);
        } finally {
            em.close();
        }
    }

    public void save(Student student) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(student);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public void update(Student student) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(student);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public void delete(int id) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Student s = em.find(Student.class, id);
            if (s != null) {
                em.remove(s);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public int getPageSize() {
        return PAGE_SIZE;
    }
}
