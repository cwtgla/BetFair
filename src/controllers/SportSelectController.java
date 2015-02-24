package controllers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import model.BetFairCore;
import views.SportSelectView;

public class SportSelectController implements ActionListener
{
	private BetFairCore betFair;
	private SportSelectView view;
	
	public SportSelectController(BetFairCore betFair, SportSelectView view)
	{
		this.betFair = betFair;
		this.view = view;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		//if next is pressed collapse old new, make new view and pass around betfair ref
		
		//if back is pressed then collapse all, remove betfair init and go back to login view
	}
}