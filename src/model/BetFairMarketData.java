package model;

import java.util.List;

import betFairGSONClasses.MarketBook;
import betFairGSONClasses.Runner;

public class BetFairMarketData implements BetFairDataObject
{
	private String marketId;
	private String marketStatus;
	private double matchedAmount;
	private double unmatchedAmount;
	private List<Runner> marketRunners;
	private MarketBook rawBook;
	private long receivedTimeStamp;
	
	public BetFairMarketData(MarketBook book)
	{
		marketId = book.getMarketId();
		marketStatus = book.getStatus();
		matchedAmount = book.getTotalAvailable();
		unmatchedAmount = book.getTotalMatched();
		marketRunners = book.getRunners();
		rawBook = book;
		receivedTimeStamp = System.currentTimeMillis();
	}
	
	public MarketBook getRawBook()
	{
		return rawBook;
	}
	
	public long getReceivedTime()
	{
		return receivedTimeStamp;
	}
	
	
	public double getMatchedAmount()
	{
		return matchedAmount;
	}
	
	public double getUnmatchedAmount()
	{
		return unmatchedAmount;
	}
	
	public double getTotalAmount()
	{
		return matchedAmount + unmatchedAmount;
		
	}
	
	public String getStatus()
	{
		return marketStatus;
	}
	
	public List<Runner> getRunners()
	{
		return marketRunners;
	}
	
	@Override
	public String getName()
	{
		return null;
	}

	@Override
	public String getMarketId()
	{
		return marketId;
	}

}
