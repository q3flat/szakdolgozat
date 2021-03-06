package hu.elte.bfw1p6.poker.client.model;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.List;

import hu.elte.bfw1p6.poker.client.observer.PokerRemoteObserver;
import hu.elte.bfw1p6.poker.exception.PokerDataBaseException;
import hu.elte.bfw1p6.poker.exception.PokerInvalidPassword;
import hu.elte.bfw1p6.poker.exception.PokerInvalidUserException;
import hu.elte.bfw1p6.poker.exception.PokerTableDeleteException;
import hu.elte.bfw1p6.poker.model.entity.PokerTable;
import hu.elte.bfw1p6.poker.model.entity.User;
import hu.elte.bfw1p6.poker.properties.PokerProperties;
import hu.elte.bfw1p6.poker.rmi.PokerRemote;
import hu.elte.bfw1p6.poker.session.PokerSession;

/**
 * Kliens oldali model, amely a kisebb műveletek hívásáért felelős.
 * @author feher
 *
 */
public class Model {
	
	private static Model instance = null;
	
	/**
	 * A kliens oldali controllerek közötti paraméter. Táblaszerkesztés és táblához való csatlakozáskor.
	 */
	private static PokerTable paramPokerTable;

	/**
	 * A szerver kliens oldali csonkja.
	 */
	private static PokerRemote pokerRemote;

	/**
	 * A kliens sessionje.
	 */
	private static PokerSession pokerSession;
	
	private final String IP;
	private final String SVNAME;
	private final String PORT;
	
	private PokerProperties pokerProperties;

	private Model() throws MalformedURLException, RemoteException, NotBoundException {
		pokerProperties = PokerProperties.getInstance();
		IP = pokerProperties.getProperty("ip");
		SVNAME =  pokerProperties.getProperty("name");
		PORT = pokerProperties.getProperty("rmiport");
		connect();
	}
	
	public static Model getInstance() throws MalformedURLException, RemoteException, NotBoundException {
		if (instance == null) {
			instance = new Model();
		}
		return instance;
	}
	
	private void connect() throws MalformedURLException, RemoteException, NotBoundException {
		pokerRemote = (PokerRemote) Naming.lookup("//" + IP + ":" + PORT + "/" + SVNAME);
	}

	/**
	 * Bejelentkezés a pókerjátékba.
	 * @param username a felhasználónév
	 * @param password a jelszó (plain textben)
	 * @throws RemoteException
	 * @throws PokerDataBaseException
	 * @throws PokerInvalidUserException
	 * @throws NotBoundException 
	 * @throws MalformedURLException 
	 */
	public void login(String username, String password) throws RemoteException, PokerDataBaseException, PokerInvalidUserException, MalformedURLException, NotBoundException {
		connect();
		pokerSession = pokerRemote.login(username, password);
	}

	/**
	 * Regisztráció a pókerjátékba.
	 * @param username a felhasználónév
	 * @param password a jelszó (plain textben)
	 * @throws RemoteException
	 * @throws PokerDataBaseException
	 */
	public void registration(String username, String password) throws RemoteException, PokerDataBaseException {
		pokerRemote.registration(username, password);
	}

	/**
	 * Új játéktábla létrehozása.
	 * @param t a létrehozandó játéktábla szerver
	 * @throws RemoteException
	 * @throws PokerDataBaseException
	 */
	public void createTable(PokerTable t) throws RemoteException, PokerDataBaseException {
		pokerRemote.createTable(pokerSession.getId(), t);
	}

	/**
	 * A letárolt összes játéktábla lekérdezése.
	 * @return a letárolt játéktáblák
	 * @throws PokerDataBaseException
	 * @throws RemoteException 
	 */
	public List<PokerTable> getTables() throws PokerDataBaseException, RemoteException {
		return pokerRemote.getTables(pokerSession.getId());
	}

	/**
	 * A kliens beregisztrálása, hogy a szerver tábla frissítéseket tudjon leküldeni.
	 * @param observer a kliens (TableListerController)
	 * @return az adatbázisban letárolt játéktáblák
	 * @throws RemoteException
	 * @throws PokerDataBaseException
	 */
	public List<PokerTable> registerTableViewObserver(PokerRemoteObserver observer) throws RemoteException, PokerDataBaseException {
		return pokerRemote.registerTableViewObserver(pokerSession.getId(), observer);
	}

	/**
	 * Kijelentkezés.
	 * @param commCont a kommunikációs kontroller
	 * @throws RemoteException
	 */
	public void logout(PokerRemoteObserver commCont) throws RemoteException {
		pokerRemote.logout(pokerSession.getId());
		pokerSession = null;
	}
	
	/**
	 * Létező tábla entitás módosítása.
	 * @param t a létező, módosított tábla entitás
	 * @throws RemoteException
	 * @throws PokerDataBaseException
	 * @throws PokerTableDeleteException 
	 */
	public void modifyTable(PokerTable t) throws RemoteException, PokerDataBaseException, PokerTableDeleteException {
		pokerRemote.modifyTable(pokerSession.getId(), t);
	}

	/**
	 * Tábla entitást töröl.
	 * @param t a tábla entitás
	 * @throws RemoteException
	 * @throws PokerDataBaseException
	 * @throws PokerTableDeleteException 
	 */
	public void deleteTable(PokerTable t) throws RemoteException, PokerDataBaseException, PokerTableDeleteException {
		pokerRemote.deleteTable(pokerSession.getId(), t);
	}

	/**
	 * A felhasználó jelszó cseréje.
	 * @param oldPassword a régi jelszó
	 * @param newPassword az új jelszó
	 * @throws RemoteException
	 * @throws PokerDataBaseException
	 * @throws PokerInvalidPassword
	 */
	public void modifyPassword(String oldPassword, String newPassword) throws RemoteException, PokerDataBaseException, PokerInvalidPassword {
		pokerRemote.modifyPassword(pokerSession.getId(), oldPassword, newPassword);
	}

	/**
	 * Van-e még hely az asztalnál.
	 * @return ha van, akkor true, különben false
	 * @throws RemoteException
	 */
	public boolean canSitIn() throws RemoteException {
		return pokerRemote.canSitIn(pokerSession.getId(), paramPokerTable);
	}

	/**
	 * Már meglévő felhasználó módosítása
	 * @param u a módosítandó felhasználó
	 * @throws RemoteException
	 * @throws PokerDataBaseException
	 */
	public void modifyUser(User u) throws RemoteException, PokerDataBaseException {
		pokerRemote.modifyUser(pokerSession.getId(), u);
	}

	/**
	 * Már meglévő felhasználót töröl
	 * @param u a törlendő felhasználó
	 * @throws RemoteException
	 * @throws PokerDataBaseException
	 */
	public void deleteUser(User u) throws RemoteException, PokerDataBaseException {
		pokerRemote.deleteUser(pokerSession.getId(), u);
	}

	/**
	 * Kijelölt játékasztal újraindítása.
	 * @param t az újraindítandó asztal
	 * @throws RemoteException
	 * @throws PokerDataBaseException 
	 */
	public void resetTable(PokerTable t) throws RemoteException, PokerDataBaseException {
		pokerRemote.resetTable(pokerSession.getId(), t);
	}
	
	/**
	 * A felhasználó egyenlegét frissíti.
	 * @throws RemoteException
	 * @throws PokerDataBaseException
	 */
	public void refreshUserDetails() throws RemoteException, PokerDataBaseException {
		pokerSession.getUser().setAdmin(pokerRemote.isAdmin(pokerSession.getId()));
		pokerSession.refreshBalance(pokerRemote.refreshBalance(pokerSession.getId()));
	}
	
	/**
	 * A bejelentkezett felhasználó admin jogkörrel rendelekezik-e.
	 * @return ha a bejelentkezett felhasználó admin jogkörrel rendelkezik, akkor true, különben false
	 * @throws RemoteException
	 * @throws PokerDataBaseException
	 */
	public boolean isAdmin() throws RemoteException, PokerDataBaseException {
		return pokerRemote.isAdmin(pokerSession.getId());
	}
	
	public User getUser() {
		return pokerSession.getUser();
	}

	public List<User> getUsers() throws PokerDataBaseException, RemoteException {
		return pokerRemote.getUsers(pokerSession.getId());
	}
	
	public static PokerTable getParamPokerTable() {
		return paramPokerTable;
	}
	
	public static PokerSession getPokerSession() {
		return pokerSession;
	}

	public static PokerRemote getPokerRemote() {
		return pokerRemote;
	}
	
	public void setParameterPokerTable(PokerTable paramPokerTable) {
		Model.paramPokerTable = paramPokerTable;
	}
}