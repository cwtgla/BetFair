package model;

import java.util.Date;
import java.util.List;

import betFairGSONClasses.Event;
import betFairGSONClasses.MarketCatalogue;
import betFairGSONClasses.RunnerCatalog;

public class BetFairMarketObject implements BetFairDataObject
{
	private String marketName;
	private String marketId;
	private List<RunnerCatalog> marketRunners;
	private Date marketOpenDate;
	private Event marketEvent;
	
	public BetFairMarketObject(MarketCatalogue market)
	{
		marketName = market.getMarketName();
		marketId = market.getMarketId();
		marketRunners = market.getRunners();
		marketOpenDate = market.getEvent().getOpenDate();
		marketEvent = market.getEvent();
	}
	
	public Date getOpenDate()
	{
		return marketOpenDate;
	}

	public List<RunnerCatalog> getRunners()
	{
		return marketRunners;
	}
	
	public String getName()
	{
		return marketName;
	}

	public String getGamesName()
	{
		return marketEvent.getName();
	}
	
	public String getMarketId()
	{
		return marketId;
	}
	
	@Override
	public String toString()
	{
		return marketName + " " + marketId;
	}
}
