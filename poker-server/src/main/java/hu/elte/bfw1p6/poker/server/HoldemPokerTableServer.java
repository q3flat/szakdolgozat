package hu.elte.bfw1p6.poker.server;

import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

import hu.elte.bfw1p6.poker.client.observer.PokerTableServerObserver;
import hu.elte.bfw1p6.poker.client.observer.RemoteObserver;
import hu.elte.bfw1p6.poker.command.PokerCommand;
import hu.elte.bfw1p6.poker.command.holdem.HouseHoldemCommand;
import hu.elte.bfw1p6.poker.command.holdem.PlayerHoldemCommand;
import hu.elte.bfw1p6.poker.command.type.HoldemHouseCommandType;
import hu.elte.bfw1p6.poker.exception.PokerDataBaseException;
import hu.elte.bfw1p6.poker.exception.PokerTooMuchPlayerException;
import hu.elte.bfw1p6.poker.exception.PokerUserBalanceException;
import hu.elte.bfw1p6.poker.model.entity.PokerTable;
import hu.elte.bfw1p6.poker.model.entity.User;
import hu.elte.bfw1p6.poker.persist.repository.UserRepository;
import hu.elte.bfw1p6.poker.server.logic.Deck;
import hu.elte.bfw1p6.poker.server.logic.House;

/**
 * Maga a póker asztal megvalósítása
 * @author feher
 *
 */
public class HoldemPokerTableServer extends UnicastRemoteObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private final String ERR_BALANCE_MSG = "Nincs elég zsetonod!";

	/**
	 * Maga az asztal entitás, le lehet kérni mindent...
	 */
	private PokerTable pokerTable;

	/**
	 * Épp milyen utasítást fog kiadni a szerver (hol tartunk a körben)
	 */
	private HoldemHouseCommandType actualHoldemHouseCommandType;

	/**
	 * Maga a pénz stack
	 */
	private BigDecimal stack;

	/**
	 * Kliensek (observerek)
	 */
	private List<RemoteObserver> clients;

	private Deck deck;

	private House house;

	private int playersInRound;

	private int thinkerPlayer; // úgy kell megcsinálni, hogy call, checknél ++, raisenél = 0!!!;

	private int round = 0;

	private BigDecimal actualRaise;

	private int minPlayer = 2;

	/**
	 * valahogy kéne Decket nyilvan tartani inteket küldök át, és simán filename alapján visszakeresik maguknak a kliensek...
	 * @param pokerTable
	 */

	public HoldemPokerTableServer(PokerTable pokerTable) throws RemoteException {
		this.pokerTable = pokerTable;
		deck = new Deck();
		house = new House();
		clients = new ArrayList<>();
		// a játékosoknak osztok először lapokat
		this.actualHoldemHouseCommandType = HoldemHouseCommandType.values()[0];
	}

	public synchronized int join(RemoteObserver client) throws PokerTooMuchPlayerException {
		if (!clients.contains(client)) {
			if (clients.size() >= pokerTable.getMaxPlayers()) {
				throw new PokerTooMuchPlayerException("Az asztal betelt, nem tudsz csatlakozni!");
			} else {
				clients.add(client);
				if (clients.size() >= minPlayer) {
					playersInRound = clients.size();
					thinkerPlayer = 0;

					// be kell kérni a vakokat
					collectBlinds();
					// két lap kézbe
					dealCardsToPlayers();

					// megkezdődik az első kör (flop előtt) kisvak nagyvak etc...
					// nézni kell, hogy hol tart a kör, ha körbeértünk, akkor mehet a flop, vagy adott esetben újabb körök!!!
					// pl valaki emel...
				}
			}
		}
		return clients.size();
	}

	private void collectBlinds() {
		actualRaise = pokerTable.getDefaultPot();
		for (int i = 0; i < clients.size(); i++) {
			PokerCommand pokerCommand = new HouseHoldemCommand(actualHoldemHouseCommandType, i, clients.size(), round);
			try {
				clients.get(i).update(null, pokerCommand);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//sendPokerCommand(j, pokerCommand);
		}
		nextStep();
	}

	private void sendPokerCommand(int j, PokerCommand pokerCommand) {
		new Thread() {

			@Override
			public void run() {
				try {
					clients.get(j).update(null, pokerCommand);
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}}.start();
	}

	private void dealCardsToPlayers() {
		for (int i = 0; i < clients.size(); i++) {
			int j = i;
			PokerCommand pokerCommand = new HouseHoldemCommand(actualHoldemHouseCommandType, deck.popCard(), deck.popCard());
			sendPokerCommand(j, pokerCommand);
		}
		nextStep();
	}

	private void flop() {
		PokerCommand pokerCommand = new HouseHoldemCommand(actualHoldemHouseCommandType, deck.popCard(), deck.popCard(), deck.popCard());
		notifyClients(pokerCommand);
		nextStep();
	}

	private void turn() {
		PokerCommand pokerCommand = new HouseHoldemCommand(actualHoldemHouseCommandType, deck.popCard());
		notifyClients(pokerCommand);
		nextStep();
	}

	private void river() {
		PokerCommand pokerCommand = new HouseHoldemCommand(actualHoldemHouseCommandType, deck.popCard());
		notifyClients(pokerCommand);
		nextStep();
	}

	private void nextStep() {
		actualHoldemHouseCommandType = HoldemHouseCommandType.values()[(actualHoldemHouseCommandType.ordinal() + 1) % HoldemHouseCommandType.values().length];
	}

	private void notifyClients(PokerCommand pokerCommand) {
		for (RemoteObserver pokerTableServerObserver : clients) {
			System.out.println("ertesitem a " + pokerTableServerObserver.toString() + " klienst!");
			new Thread() {

				@Override
				public void run() {
					try {
						pokerTableServerObserver.update(null, pokerCommand);
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}}.start();
		}
	}

	public synchronized void leave(PokerTableServerObserver client) {
		clients.remove(client);
	}

	public synchronized void receivePlayerCommand(RemoteObserver client, PlayerHoldemCommand playerCommand) throws PokerDataBaseException, PokerUserBalanceException {
		if (clients.contains(client)) {
			switch(playerCommand.getPlayerCommandType()) {
			case BLIND: {
				blind(playerCommand);
				break;
			}
			case CALL: {
				call(playerCommand);
				++thinkerPlayer;
				break;
			}
			case CHECK: {
				++thinkerPlayer;
				break;
			}
			case FOLD: {
				++thinkerPlayer;
				break;
			}
			case RAISE: {
				raise(playerCommand);
				thinkerPlayer = 1;
				break;
			}
			case QUIT: {
				clients.remove(client);
				++thinkerPlayer;
				break;
			}
			default:
				break;
			}
			if (thinkerPlayer >= playersInRound) {
				switch (actualHoldemHouseCommandType) {
				case FLOP: {
					flop();
					break;
				}
				case TURN: {
					turn();
					break;
				}
				case RIVER: {
					river();
					break;
				}
				default:
					break;
				}
				thinkerPlayer %= playersInRound;
			}
			notifyClients(playerCommand);
		}
	}

	private void raise(PlayerHoldemCommand playerCommand) throws PokerDataBaseException {
		User u = UserRepository.getInstance().findByUserName(playerCommand.getSender());
		BigDecimal newBalance = u.getBalance().subtract(playerCommand.getCallAmount().subtract(playerCommand.getRaiseAmount()));
		if (newBalance.compareTo(new BigDecimal(0)) < 0) {
			System.out.println("nagy para van");
			//throw new Poker
		} else {
			u.setBalance(u.getBalance().subtract(playerCommand.getCallAmount()));
			UserRepository.getInstance().modify(u);
		}
	}

	private void blind(PlayerHoldemCommand playerCommand) throws PokerDataBaseException, PokerUserBalanceException {
		User u = UserRepository.getInstance().findByUserName(playerCommand.getSender());
		if (isThereEnoughMoney(u, playerCommand)) {
			u.setBalance(u.getBalance().subtract(playerCommand.getCallAmount()));
			UserRepository.getInstance().modify(u);
		}
	}

	private void call(PlayerHoldemCommand playerCommand) throws PokerDataBaseException, PokerUserBalanceException {
		User u = UserRepository.getInstance().findByUserName(playerCommand.getSender());
		if (isThereEnoughMoney(u, playerCommand)) {
			u.setBalance(u.getBalance().subtract(playerCommand.getCallAmount()));
			UserRepository.getInstance().modify(u);
		}
	}

	private boolean isThereEnoughMoney(User u, PlayerHoldemCommand playerCommand) throws PokerUserBalanceException {
		if (playerCommand.getCallAmount().compareTo(BigDecimal.ZERO) < 0) {
			throw new PokerUserBalanceException(ERR_BALANCE_MSG);
		}
		return true;
	}

}
