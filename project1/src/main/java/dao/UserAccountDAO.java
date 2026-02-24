package dao;


import entity.UserAccount;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;
import java.util.List;

public class UserAccountDAO {

    private final EntityManagerFactory emf;

    public UserAccountDAO(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public UserAccount findByUsernameAndPassword(String username, String password) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<UserAccount> query = em.createQuery(
                "SELECT u FROM UserAccount u WHERE u.username = :username AND u.password = :password",
                UserAccount.class
            );
            query.setParameter("username", username);
            query.setParameter("password", password);
            List<UserAccount> results = query.getResultList();
            return results.isEmpty() ? null : results.get(0);
        } finally {
            em.close();
        }
    }

    public boolean existsAny() {
        EntityManager em = emf.createEntityManager();
        try {
            Long count = em.createQuery("SELECT COUNT(u) FROM UserAccount u", Long.class)
                           .getSingleResult();
            return count > 0;
        } finally {
            em.close();
        }
    }

    public void save(UserAccount user) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(user);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }
}
