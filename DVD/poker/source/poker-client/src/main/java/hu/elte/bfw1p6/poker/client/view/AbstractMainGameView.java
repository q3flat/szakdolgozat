package hu.elte.bfw1p6.poker.client.view;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

import com.cantero.games.poker.texasholdem.Card;
import com.cantero.games.poker.texasholdem.CardSuitEnum;

import hu.elte.bfw1p6.poker.client.defaultvalues.AbstractDefaultValues;
import hu.elte.bfw1p6.poker.command.HouseCommand;
import hu.elte.bfw1p6.poker.command.PlayerCommand;
import hu.elte.bfw1p6.poker.command.PokerCommand;
import hu.elte.bfw1p6.poker.command.holdem.HoldemHouseCommand;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

/**
 * A kliens játék közbeni absztrakt megjelenítését reprezentáló osztály.
 * @author feher
 *
 */
public abstract class AbstractMainGameView {

	/**
	 * Beégetett alapértékek, amelyek a megjelenítéshez kellenek.
	 */
	protected AbstractDefaultValues defaultValues;

	/**
	 * A saját kártyalapjaim.
	 */
	protected List<ImageView> myCards;

	/**
	 * A nyertes kártyalapok.
	 */
	private List<ImageView> winnerCards;

	/**
	 * Profilképek.
	 */
	private List<ImageView> profileImages;

	/**
	 * Az ellenfelek lefordított egész kártyái.
	 */
	private List<ImageView> opponentsCards;

	/**
	 * Az ellenfelek lefordított kártyaszélei.
	 */
	private List<ImageView> opponentsCardSides;

	/**
	 * A zsetonok.
	 */
	private List<ImageView> chips;

	/**
	 * A játékosok nevei.
	 */
	private List<Label> userNameLabels;

	/**
	 * Az egyenlegem.
	 */
	private Label myBalance;

	/**
	 * Az osztó gomb.
	 */
	private ImageView dealerButtonImageView;

	/**
	 * A színtérgráf gyökere.
	 */
	protected AnchorPane mainGamePane;

	/**
	 * Hány játékossal játszom egy asztalnál.
	 */
	private int clientsCount;

	/**
	 * Random objektum.
	 */
	private Random random;

	/**
	 * A következő játékos.
	 */
	private int nextPlayer = -1;

	/**
	 * Hanyadik vagy a körben.
	 */
	private int youAreNth;

	/**
	 * Fixen hanyadik játékosként vagy beülve az asztalhoz.
	 */
	private int fixSitPosition;
	
	public AbstractMainGameView(AnchorPane mainGamePane, AbstractDefaultValues defaultValues) {
		this.mainGamePane = mainGamePane;
		this.defaultValues = defaultValues;
		this.myCards = new ArrayList<>();
		this.winnerCards = new ArrayList<>();
		this.profileImages = new ArrayList<>();
		this.opponentsCards = new ArrayList<>();
		this.opponentsCardSides = new ArrayList<>();
		this.chips = new ArrayList<>();
		this.userNameLabels = new ArrayList<>();
		this.myBalance = new Label();
		this.random = new Random();

		setDealerButton();
		setProfileImages();
		setDeck();
		setCards();
		setLabels();
		hideAllProfiles();
	}

	/**
	 * Felhelyezi az asztalra a dealer gombot.
	 */
	protected void setDealerButton() {
		dealerButtonImageView = new ImageView(new Image(defaultValues.DEALER_BUTTON_IMAGE_URL));
		dealerButtonImageView.setFitHeight(defaultValues.DEALER_BUTTON_SIZE);
		dealerButtonImageView.setFitWidth(defaultValues.DEALER_BUTTON_SIZE);
		dealerButtonImageView.setVisible(false);
		mainGamePane.getChildren().add(dealerButtonImageView);
	}

	/**
	 * Legenerálja a profilképeket.
	 */
	protected void setProfileImages() {
		Image profileImage = new Image(defaultValues.PROFILE_IMAGE_URL);
		for (int i = 0; i < defaultValues.PROFILE_COUNT * 2; i+=2) {
			ImageView iv = new ImageView(profileImage);
			iv.setLayoutX(defaultValues.PROFILE_POINTS[i]);
			iv.setLayoutY(defaultValues.PROFILE_POINTS[i+1]);
			iv.fitHeightProperty().set(defaultValues.PROFILE_SIZE);
			iv.fitWidthProperty().set(defaultValues.PROFILE_SIZE);
			profileImages.add(iv);
			mainGamePane.getChildren().add(iv);
		}
	}

	/**
	 * Megjeleníti a kártyapaklit a GUI-n.
	 */
	protected void setDeck() {
		ImageView deck = new ImageView(new Image(defaultValues.DECK_IMAGE_URL));
		deck.setLayoutX(defaultValues.DECK_POINT[0]);
		deck.setLayoutY(defaultValues.DECK_POINT[1]);
		mainGamePane.getChildren().add(deck);
	}

	/**
	 * A GUI-n megtalálható összes kártyalapot legenerálja.
	 */
	protected void setCards() {
		// opponents cards
		for (int i = 0; i < (defaultValues.PROFILE_COUNT) * 2; i+=2) {
			for (int j = 0; j < defaultValues.MY_CARDS_COUNT - 1; j++) {
				ImageView cardSide = new ImageView(new Image(defaultValues.CARD_SIDE_IMAGE_URL));
				cardSide.setLayoutX(defaultValues.CARD_B1FV_POINTS[i] + j * defaultValues.CARD_SIDE_WIDTH);
				cardSide.setLayoutY(defaultValues.CARD_B1FV_POINTS[i+1]);
				cardSide.fitHeightProperty().set(defaultValues.CARD_HEIGHT);
				cardSide.fitWidthProperty().set(defaultValues.CARD_SIDE_WIDTH);
				opponentsCardSides.add(cardSide);
				mainGamePane.getChildren().add(cardSide);
			}
			ImageView backCard = new ImageView(new Image(defaultValues.CARD_BACKFACE_IMAGE));
			backCard.setLayoutX(defaultValues.CARD_B1FV_POINTS[i] + (defaultValues.MY_CARDS_COUNT - 1) * defaultValues.CARD_SIDE_WIDTH);
			backCard.setLayoutY(defaultValues.CARD_B1FV_POINTS[i+1]);
			backCard.fitHeightProperty().set(defaultValues.CARD_HEIGHT);
			backCard.fitWidthProperty().set(defaultValues.CARD_WIDTH);
			opponentsCards.add(backCard);
			mainGamePane.getChildren().add(backCard);
		}

		//winner cards
		for (int i = 0; i < defaultValues.MY_CARDS_COUNT; i++) {
			ImageView imageView = new ImageView();
			winnerCards.add(imageView);
			mainGamePane.getChildren().add(imageView);
		}

		//my cards
		for (int i = 0; i < defaultValues.MY_CARDS_COUNT; i++) {
			ImageView myCard = new ImageView();
			int gap = 5;
			myCard.setLayoutX(defaultValues.MY_CARDS_POSITION[0]);
			myCard.setLayoutY(defaultValues.MY_CARDS_POSITION[1]);
			myCard.setLayoutX(defaultValues.MY_CARDS_POSITION[0] + i * defaultValues.CARD_WIDTH + gap);
			myCard.setLayoutY(defaultValues.MY_CARDS_POSITION[1]);

			myCards.add(myCard);
			mainGamePane.getChildren().add(myCard);
		}
	}

	/**
	 * A usernevek címkéit generálja le.
	 */
	protected void setLabels() {
		for (int i = 0; i < defaultValues.PROFILE_COUNT * 2; i+=2) {
			Label label = new Label();
			label.setLayoutX(defaultValues.PROFILE_POINTS[i]);
			label.setLayoutY(defaultValues.PROFILE_POINTS[i+1]);
			userNameLabels.add(label);
			mainGamePane.getChildren().add(label);
		}
		myBalance.setLayoutX(defaultValues.PROFILE_POINTS[0]);
		myBalance.setLayoutY(defaultValues.PROFILE_POINTS[1]+20);
		mainGamePane.getChildren().add(myBalance);
	}
	
	public void setUserName(String userName) {
		userNameLabels.get(0).setText(userName);
	}

	/**
	 * Megfelelő helyen jeleníti meg a beérkező userneveket.
	 * @param userNames a megjelenítendő usernevek
	 */
	protected void setLabelUserNames(List<String> userNames) {
		for (int i = fixSitPosition, counter = 0; counter < clientsCount; counter++,i++) {
			userNameLabels.get(counter).setText(userNames.get(i % clientsCount));
		}
	}

	/**
	 * Egy kártyát feleltet meg egy képnek.
	 * @param card a kártyalap
	 * @return a megfeleltetett érték
	 */
	protected int mapCard(Card card) {
		return defaultValues.CARDS_COUNT - (card.getRankToInt() * CardSuitEnum.values().length) - (CardSuitEnum.values().length - card.getSuit().ordinal() - 1);
	}

	/**
	 * Elrejti az összes ellenfél profilképet és lefordított kártyalapokat.
	 */
	protected void hideAllProfiles() {
		opponentsCards.forEach(card -> card.setVisible(false));
		opponentsCardSides.forEach(card -> card.setVisible(false));
		profileImages.subList(1, profileImages.size()).forEach(card -> card.setVisible(false));
		userNameLabels.subList(1, userNameLabels.size()).forEach(card -> card.setVisible(false));
	}

	/**
	 * Kitörli a chipeket.
	 */
	protected void clearChips() {
		chips.forEach(chip -> mainGamePane.getChildren().remove(chip));
		chips.clear();
	}

	/**
	 * A következő játékos profilját jelöli meg a GUI-n.
	 * @param pokerCommand az utasítás
	 */
	protected void colorNextPlayer(PokerCommand pokerCommand) {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				nextPlayer = ultimateFormula(pokerCommand.getWhosOn());
				profileImages.forEach(profile -> profile.getStyleClass().remove(defaultValues.MARKER_STYLECLASS));
				if (nextPlayer >= 0) {
					profileImages.get(nextPlayer).getStyleClass().add(defaultValues.MARKER_STYLECLASS);
				}
				nextPlayer = pokerCommand.getWhosOn();
			}
		});
	}

	/**
	 * A paramétert konvertálja a megfelelő játékos adott táblanézete szerint.
	 * (Mindegyik játékos máshogy látja a táblát.)
	 * @param whosOn a paraméter
	 * @return a konvertált érték
	 */
	public int ultimateFormula(int whosOn) {
		if (clientsCount < 1) {
			clientsCount = 1;
		}
		int value = (whosOn - fixSitPosition) % clientsCount;
		value += value < 0 ? clientsCount : 0;
		return value;
	}

	/**
	 * BLIND típusú utasítás érkezett a szervertől.
	 * @param houseCommand az utasítás
	 */
	public void receivedBlindHouseCommand(HouseCommand houseCommand) {
		clientsCount = houseCommand.getPlayers();
		youAreNth = houseCommand.getNthPlayer();
		fixSitPosition = houseCommand.getFixSitPosition();
		int dealerButtonPosition = (clientsCount + houseCommand.getDealer() - youAreNth) % clientsCount;
		Platform.runLater(
				new Runnable() {

					@Override
					public void run() {
						hideAllProfiles();
						setLabelUserNames(houseCommand.getPlayersNames());
						resetOpacity();
						clearChips();
						int cc = clientsCount;
						userNameLabels.subList(0, cc).forEach(label -> label.setVisible(true));
						userNameLabels.subList(0, cc).forEach(label -> label.setOpacity(1));
						profileImages.subList(0, cc).forEach(label -> label.setVisible(true));
						opponentsCards.subList(0, cc - 1).forEach(card -> card.setVisible(true));
						opponentsCardSides.subList(0, (cc - 1) * (defaultValues.MY_CARDS_COUNT - 1)).forEach(card -> card.setVisible(true));
						profileImages.subList(0, cc).forEach(profile -> profile.setOpacity(1));

						dealerButtonImageView.setLayoutX(defaultValues.DEALER_BUTTON_POSITIONS[dealerButtonPosition * 2]);
						dealerButtonImageView.setLayoutY(defaultValues.DEALER_BUTTON_POSITIONS[dealerButtonPosition * 2 + 1]);
						dealerButtonImageView.setVisible(true);
					}
				});
	}

	/**
	 * DEAL típusú utasítás érkezett a szervertől.
	 * @param houseCommand az utasítás
	 */
	public void receivedDealHouseCommand(HouseCommand houseCommand) {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				resetOpacity();
				hideHouseCards();
				loadMyCards(houseCommand);
				nextPlayer = ultimateFormula(houseCommand.getWhosOn());
				colorNextPlayer(houseCommand);
			}
		});
	}

	/**
	 * Megjeleníti a saját kártyalapjaimat.
	 * @param houseCommand az utasítás
	 */
	public void loadMyCards(HouseCommand houseCommand) {
		Card[] cards = houseCommand.getCards();
		for (int j = 0; j < defaultValues.MY_CARDS_COUNT; j++) {
			myCards.get(j).setImage(new Image(defaultValues.CARD_IMAGE_PREFIX + mapCard(cards[j]) + defaultValues.PICTURE_EXTENSION));
		}
	}

	/**
	 * RAISE típusú utasítás érkezett egy játékostól.
	 * @param playerCommand az utasítés
	 */
	public void receivedRaisePlayerCommand(PlayerCommand playerCommand) {
		colorNextPlayer(playerCommand);
		addChip();
		addChip();
	}

	/**
	 * BLIND típusú utasítás érkezett egy játékostól.
	 * @param playerCommand az utasítás
	 */
	public void receivedBlindPlayerCommand(PlayerCommand playerCommand) {
		addChip();
	}

	/**
	 * CALL típusú utasítás érkezett egy játékostól.
	 * @param playerCommand az utasítás
	 */
	public void receivedCallPlayerCommand(PlayerCommand playerCommand) {
		colorNextPlayer(playerCommand);
		addChip();
	}

	/**
	 * CHECK típusú utasítás érkezett egy játékostól.
	 * @param playerCommand az utasítás
	 */
	public void receivedCheckPlayerCommand(PlayerCommand playerCommand) {
		colorNextPlayer(playerCommand);
	}

	/**
	 * FOLD típusú utasítás érkezett egy játékostól.
	 * @param playerCommand az utasítás
	 */
	public void receivedFoldPlayerCommand(PlayerCommand playerCommand) {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				double opacity = 0.4;
				int convertedWhoFold = ultimateFormula(playerCommand.getWhosQuit());
				if (convertedWhoFold == 0) {
					youAreNth = -1;
					myCards.forEach(card -> card.setOpacity(opacity));
				} else {
					setNthPlayersCardsOpacity(opacity, convertedWhoFold);
				}
				colorNextPlayer(playerCommand);
			}
		});
	}

	/**
	 * QUIT típusú utasítás érkezett egy játékostól.
	 * @param playerCommand az utasítás
	 */
	public void receivedQuitPlayerCommand(PlayerCommand playerCommand) {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				if (playerCommand.getClientsCount() < 2) {
					hideAllProfiles();
					hideHouseCards();
					myCards.forEach(card -> card.setOpacity(0));
					winnerCards.forEach(card -> card.setOpacity(0));
					dealerButtonImageView.setVisible(false);
					clearChips();
					profileImages.forEach(profile -> profile.getStyleClass().remove(defaultValues.MARKER_STYLECLASS));
					
				} else {
					double opacity = 0.0;
					int convertedWhoQuit = ultimateFormula(playerCommand.getWhosQuit());
					setNthPlayersCardsOpacity(opacity, convertedWhoQuit);
					profileImages.get(convertedWhoQuit).setOpacity(opacity);
					userNameLabels.get(convertedWhoQuit).setOpacity(opacity);
//					colorNextPlayer(playerCommand);
				}
			}
		});
	}

	/**
	 * A szerver WINNER típusú utasítást küldött.
	 * @param houseCommand az utasítás
	 */
	public void receivedWinnerHouseCommand(HouseCommand houseCommand) {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {

				int convertedWinnerIndex = ultimateFormula(houseCommand.getWinner());
				if (convertedWinnerIndex != 0) {
					IntStream.range(0, defaultValues.MY_CARDS_COUNT).forEach(i -> 
						winnerCards.get(i).setImage(new Image(defaultValues.CARD_IMAGE_PREFIX + mapCard(houseCommand.getCards()[i]) + defaultValues.PICTURE_EXTENSION))
					);
					setNthPlayersCardsOpacity(0, convertedWinnerIndex);
					if (houseCommand instanceof HoldemHouseCommand ? true : false) {
						for (int i = 0; i < defaultValues.MY_CARDS_COUNT; i++) {
							winnerCards.get(i).setLayoutX(defaultValues.CARD_B1FV_POINTS[(convertedWinnerIndex-1) * 2] + i * defaultValues.CARD_WIDTH);
							winnerCards.get(i).setLayoutY(defaultValues.CARD_B1FV_POINTS[(convertedWinnerIndex-1) * 2 + 1]);
						}
					} else {
						int gap = 0;
						for (int i = 0; i < defaultValues.MY_CARDS_COUNT; i++) {
//							gap = i == 0 ? 0 : defaultValues.CARD_WIDTH;
							winnerCards.get(i).setLayoutX(defaultValues.MIDDLE_CARD_POINT[i * 2] + gap);
							winnerCards.get(i).setLayoutY(defaultValues.MIDDLE_CARD_POINT[i * 2 + 1]);
						}
					}
					winnerCards.forEach(card -> {
						card.setVisible(true);
						card.setOpacity(1);
					});
				}
			}
		});
	}

	/**
	 * i. ellenfél kártyalapjainak az áttetszőgését módosítja.
	 * @param opacity az áttettszőség mértéke
	 * @param convertedValue az i. ellenfél
	 */
	private void setNthPlayersCardsOpacity(double opacity, int convertedValue) {
		if (convertedValue < 1) {
			convertedValue = 1;
		}
		opponentsCards.get(convertedValue - 1).setOpacity(opacity);
		for (int i = (convertedValue - 1) * (defaultValues.MY_CARDS_COUNT - 1), counter = 0; counter < defaultValues.MY_CARDS_COUNT - 1; i++, counter++) {
			opponentsCardSides.get(i).setOpacity(opacity);
		}
	}

	/**
	 * Visszaállítja az áttettszőség értékeket.
	 */
	protected void resetOpacity() {
		myCards.forEach(card -> card.setOpacity(1));
		opponentsCards.forEach(card -> card.setOpacity(1));
		opponentsCardSides.forEach(card -> card.setOpacity(1));
		profileImages.forEach(profile -> profile.setOpacity(1));
		winnerCards.forEach(card -> card.setOpacity(0));
	}

	/**
	 * Random helyezek el chipeket az asztalon.
	 */
	protected void addChip() {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				int chipsCount = 5;
				int spread = 20;
				String chipColor = "black";
				switch (random.nextInt(chipsCount)) {
				case 0:
					chipColor = "white";
					break;
				case 1:
					chipColor = "green";
					break;
				case 2:
					chipColor = "blue";
					break;
				case 3:
					chipColor = "black";
					break;
				case 4:
					chipColor = "red";
					break;
				default:
					break;
				}
				ImageView chip = new ImageView(new Image(defaultValues.CHIP_IMAGE_PREFIX + chipColor + defaultValues.PICTURE_EXTENSION));
				int max = defaultValues.CHIPS_POINT[0] + spread;
				int min = defaultValues.CHIPS_POINT[1] - spread;
				chip.setLayoutX(random.nextInt(max - min) + min);
				chip.setLayoutY(random.nextInt(max - min) + min);
				chip.setFitHeight(defaultValues.CHIPS_SIZE);
				chip.setFitWidth(defaultValues.CHIPS_SIZE);
				chips.add(chip);
				mainGamePane.getChildren().add(chip);
			}
		});
	}

	/**
	 * A ház lapjait rejti el.
	 */
	protected abstract void hideHouseCards();

	/**
	 * Frissíti a felhasználó egyenlegét.
	 * @param balance az új egyenleg
	 */
	public void setBalance(BigDecimal balance) {
		Platform.runLater(new Runnable() {
			
			@Override
			public void run() {
				myBalance.setText(balance.setScale(2, RoundingMode.FLOOR).toString());
			}
		});
	}

	public int getYouAreNth() {
		return youAreNth;
	}
}