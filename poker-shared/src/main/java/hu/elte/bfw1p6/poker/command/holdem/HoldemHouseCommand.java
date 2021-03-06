package hu.elte.bfw1p6.poker.command.holdem;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.cantero.games.poker.texasholdem.Card;

import hu.elte.bfw1p6.poker.command.HouseCommand;
import hu.elte.bfw1p6.poker.command.holdem.type.HoldemHouseCommandType;


/**
 * A ház utasításai holdem játékstílus esetén.
 * @author feher
 *
 */
public class HoldemHouseCommand extends HouseCommand {
	
	private static final long serialVersionUID = 7270842556559660805L;
	
	private static Set<HoldemHouseCommandType> acceptedTypes = new HashSet<>();
	
	static {
		acceptedTypes.add(HoldemHouseCommandType.FLOP);
		acceptedTypes.add(HoldemHouseCommandType.TURN);
		acceptedTypes.add(HoldemHouseCommandType.RIVER);
	}

	/**
	 * Az utasítás típusa.
	 */
	private HoldemHouseCommandType houseCommandType;

	@Override
	public String getCommandType() {
		return houseCommandType.name();
	}
	
	@Override
	public HouseCommand setUpBlindCommand(int fixSitPosition, int nthPlayer, int players, int dealer, int whosOn, List<String> clientsNames) {
		this.houseCommandType = HoldemHouseCommandType.BLIND;
		this.fixSitPosition = fixSitPosition;
		this.nthPlayer = nthPlayer;
		this.players = players;
		this.dealer = dealer;
		this.whosOn = whosOn;
		this.clientsNames = clientsNames;
		return this;
	}
	
	@Override
	public HouseCommand setUpDealCommand(Card cards[], int whosOn) {
		this.houseCommandType = HoldemHouseCommandType.DEAL;
		this.cards = cards;
		this.whosOn = whosOn;
		return this;
	}
	
	/**
	 * Ha a holdem szerver FLOP vagy TURN vagy RIVER utasítást küld, akkor ezt a metódust kell használni.
	 * @param type FLOP vagy TURN vagy RIVER
	 * @param cards a ház lapjai
	 * @param whosOn az épppen következő (soron levő) játékos
	 * @return a beállított utasítás
	 */
	public HoldemHouseCommand setUpFlopTurnRiverCommand(HoldemHouseCommandType type, Card[] cards, int whosOn) {
		if (!acceptedTypes.contains(type)) {
			throw new IllegalArgumentException("Nem megfelelő utasítás típus!");
//			throw new IllegalArgumentException("Nem megfelelő utasítás típus! Az alkalmazható típusok: " + acceptedTypes.stream().toArray(String[]::new));
		}
		this.houseCommandType = type;
		this.cards = cards;
		this.whosOn = whosOn;
		return this;
	}
	
	@Override
	public HouseCommand setUpWinnerCommand(Card[] cards, int winner, int whosOn) {
		this.houseCommandType = HoldemHouseCommandType.WINNER;
		this.cards = cards;
		this.winner = winner;
		this.whosOn = whosOn;
		return this;
	}
	
	public HoldemHouseCommandType getHouseCommandType() {
		return houseCommandType;
	}
}