package hu.elte.bfw1p6.persist.dao;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.mindrot.jbcrypt.BCrypt;

import hu.elte.bfw1p6.model.entity.User;

public class UserDAO {
	
	private final String PU_NAME = "Poker-PU";
	private EntityManagerFactory emf;
	private EntityManager em;
	
	public UserDAO() {
		emf = Persistence.createEntityManagerFactory(PU_NAME);
		em = emf.createEntityManager();
	}
	
	public void persistUser(String username, String password) {
		User u = new User(username);
		String salt = generateSalt();
		u.setSalt(salt);
		u.setPassword(BCrypt.hashpw(u.getPassword(), salt));
		em.getTransaction().begin();
		em.persist(u);
		em.getTransaction().commit();
		em.close();
		emf.close();
	}
	
	public void modifyPassword(int id, String oldPassword, String newPassword) {
		User u = (User)em.find(User.class, id);
		String salt = generateSalt();
		u.setSalt(salt);
		u.setPassword(BCrypt.hashpw(newPassword, salt));
		em.getTransaction().begin();
		em.persist(u);
		em.getTransaction().commit();
		em.close();
		emf.close();
	}
	
	private String generateSalt() {
		return BCrypt.gensalt();
	}
}
