package controllers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JOptionPane;

import betfairUtils.LoginResponse;
import Exceptions.CryptoException;
import views.LoginView;
import views.SportSelectView;
import model.BetFairCore;

public class LoginController implements ActionListener
{
	private BetFairCore betFair;
	private LoginView view;
	private SportSelectView nextView;
	private boolean debug;
	private boolean collect;
	
	public LoginController(BetFairCore betFair, LoginView loginView)
	{
		this.betFair = betFair;
		this.view = loginView;
		debug = false;
		collect = false;
	}

	/**
	 * Deals with button presses and checkbox events
	 */
	@Override
	public void actionPerformed(ActionEvent e)
	{
		if(e.getActionCommand().equalsIgnoreCase("login"))
		{
			loginPress();
		}
		else if(e.getActionCommand().equalsIgnoreCase("debug mode"))
		{
			debugPress((JCheckBox) e.getSource());
		}
		else if(e.getActionCommand().equalsIgnoreCase("collection mode"))
		{
			collectPress((JCheckBox) e.getSource());
		}
	}
	
	private void loginPress()
	{
		String[] vals = view.getValues();
		try
		{
			betFair.setDebug(debug);
			LoginResponse response = betFair.login(vals[0], vals[1], vals[2]);
			
			//Valid login so view changes based on settings
			if(response.getLoginStatus().equalsIgnoreCase("success"))
			{	
				//Special transition to collection mode
				if(collect)
				{
					System.out.println("transition to collection mode!");
				}
				else
				{
					view.closeView();
					nextView = new SportSelectView(betFair);
				}
			}
		} 
		//Bad details passed in
		catch (CryptoException e)
		{
			JOptionPane.showMessageDialog(view.getFrame(), e.getMessage());
		}
	}
	
	private void debugPress(JCheckBox active)
	{
		if(active.isSelected())
		{
			debug = true;
		}
		else
		{
			debug = false;
		}
	}
	
	private void collectPress(JCheckBox active)
	{		
		if(active.isSelected())
		{
			collect = true;
		}
		else
		{
			collect = false;
		}
	}
}