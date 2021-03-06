package hu.elte.bfw1p6.poker.client.view;

import java.util.ArrayList;
import java.util.List;

import hu.elte.bfw1p6.poker.client.defaultvalues.ClassicDefaultValues;
import hu.elte.bfw1p6.poker.command.HouseCommand;
import hu.elte.bfw1p6.poker.command.classic.ClassicHouseCommand;
import hu.elte.bfw1p6.poker.command.classic.ClassicPlayerCommand;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.layout.AnchorPane;

/**
 * A kliens játék közbeni classic játékstílus megjelenítéséért felelős osztály.
 * @author feher
 *
 */
public class ClassicMainGameView extends AbstractMainGameView {
	
	private final int SLIP_OUT_PX = 15;

	public ClassicMainGameView(AnchorPane mainGamePane) {
		super(mainGamePane, ClassicDefaultValues.getInstance());
	}

	@Override
	protected void hideHouseCards() {
	}
	
	/**
	 * CHANGE típusú utasítás érkezett egy játékostól.
	 * @param classicHouseCommand az utasítás
	 */
	public void receivedChangeHouseCommand(ClassicHouseCommand classicHouseCommand) {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				colorNextPlayer(classicHouseCommand);
				myCards.forEach(card -> card.setOnMouseClicked(new EventHandler<Event>() {

					@Override
					public void handle(Event event) {
						if (card.getStyleClass().contains(defaultValues.SELECTED_CARD_MAKER_STYLECLASS)) {
							card.getStyleClass().remove(defaultValues.SELECTED_CARD_MAKER_STYLECLASS);
							card.setLayoutY(card.getLayoutY() + SLIP_OUT_PX);
						} else {
							card.getStyleClass().add(defaultValues.SELECTED_CARD_MAKER_STYLECLASS);
							card.setLayoutY(card.getLayoutY() - SLIP_OUT_PX);
						}
					}
				}));
			}
		});
	}
	
	/**
	 * DEAL2 típusú utasítás érkezett a szervertől.
	 * @param houseCommand az utasítás
	 */
	public void receivedDeal2HouseCommand(HouseCommand houseCommand) {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				loadMyCards(houseCommand);
				colorNextPlayer(houseCommand);
			}
		});
	}
	
	/**
	 * CHANGE típusú utasítás érkezett egy játékostól.
	 * @param classicPlayerCommand az utasítás
	 */
	public void receivedChangePlayerCommand(ClassicPlayerCommand classicPlayerCommand) {
		colorNextPlayer(classicPlayerCommand);
	}

	/**
	 * Összegyűjti a cserélendő lapokat.
	 * @return a cserélendő lapok sorszámai
	 */
	public List<Integer> getMarkedCards() {
		List<Integer> markedCards = new ArrayList<>();
		for (int i = 0; i < myCards.size(); i++) {
			if (myCards.get(i).getStyleClass().contains(defaultValues.SELECTED_CARD_MAKER_STYLECLASS)) {
				markedCards.add(i);
				myCards.get(i).getStyleClass().remove(defaultValues.SELECTED_CARD_MAKER_STYLECLASS);
				myCards.get(i).setLayoutY(myCards.get(i).getLayoutY() + SLIP_OUT_PX);
			}
		}
		myCards.forEach(card -> card.setOnMouseClicked(null));
		return markedCards;
	}
}