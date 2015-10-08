package hu.elte.bfw1p6.poker.model.entity;

import java.io.Serializable;
import java.math.BigDecimal;

public class PTable implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private int id;
	private String name;
	private PokerType pokerType;
	private int maxTime;
	private int maxPlayers;
	private BigDecimal smallBlind;
	private BigDecimal bigBlind;
	private BigDecimal maxBet;

	public PTable(String name, int maxTime, int maxPlayers, BigDecimal maxBet, BigDecimal smallBlind, BigDecimal bigBlind, PokerType pokerType) {
		this.name = name;
		this.maxTime = maxTime;
		this.maxPlayers = maxPlayers;
		this.maxBet = maxBet;
		this.smallBlind = smallBlind;
		this.bigBlind = bigBlind;
		this.pokerType = pokerType;
	}
	
	public PTable() {
		
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getMaxTime() {
		return maxTime;
	}

	public void setMaxTime(int maxTime) {
		this.maxTime = maxTime;
	}

	public int getMaxPlayers() {
		return maxPlayers;
	}

	public void setMaxPlayers(int maxPlayers) {
		this.maxPlayers = maxPlayers;
	}

	public BigDecimal getMaxBet() {
		return maxBet;
	}

	public void setMaxBet(BigDecimal maxBet) {
		this.maxBet = maxBet;
	}

	public BigDecimal getSmallBlind() {
		return smallBlind;
	}

	public void setSmallBlind(BigDecimal smallBlind) {
		this.smallBlind = smallBlind;
	}

	public BigDecimal getBigBlind() {
		return bigBlind;
	}

	public void setBigBlind(BigDecimal bigBlind) {
		this.bigBlind = bigBlind;
	}

	public PokerType getPokerType() {
		return pokerType;
	}

	public void setPokerType(PokerType pokerType) {
		this.pokerType = pokerType;
	}
}
