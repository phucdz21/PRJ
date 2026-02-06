
package Util;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class UtilJPA {
    public static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("PU_PRJ");
    
   public static EntityManager getEntityManager(){
       return emf.createEntityManager();
   }
}
