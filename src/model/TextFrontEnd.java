package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class TextFrontEnd
{
	Scanner userInput;
	SimpleBetFairCore betFair;
	String selectedSport;

	public TextFrontEnd()
	{
		betFair = new SimpleBetFairCore();
		userInput = new Scanner(System.in);
		prompt();
	}

	private void prompt()
	{
		// prompt for login
		// print sport options
		// prompt for sport
		// print game options?
		// prompt for game

		if (loginPrompt())
		{
			if (sportPrompt())
			{
				System.out.println("next stage");
			}
		}
		// System.out.println("Options: \n\t1. Get Sports list\n2. ");
	}

	// TODO provide exits
	private boolean loginPrompt()
	{
		while (true)
		{
			System.out.println("Please enter login details in form of : 'USERNAME-PASSWORD-FILEPASSWORD'");
			String inputLine = userInput.nextLine();
			String[] details = inputLine.split("-");

			if (details.length == 3)
			{
				String response = betFair.login(details[0], details[1], details[2]);
				if (response.equalsIgnoreCase("success"))
					return true;
			}
		}
	}

	private boolean sportPrompt()
	{
		List<String> results = new ArrayList<String>();
		
		while (true)
		{
			System.out.println("Options: \n\t\"LIST\". Get Sports list\n\t\"SELECT 'NUMBER'\". Select the sport with the given id");

			//int input = userInput.nextInt();
			String input = userInput.nextLine();
			
			String[] tokens = input.split(" ");
			
			if(tokens[0].equalsIgnoreCase("LIST"))
			{
				if(results.isEmpty())
					results = betFair.getSportSupportedSportList();
				for(String resultItem: results)
					System.out.println(resultItem);
			}
			else if(tokens[0].equalsIgnoreCase("SELECT"))
			{
				int index = Integer.parseInt(tokens[tokens.length-1]);
				//selected number (1 to 29)
				selectedSport = results.get(index-1);
			}
		}
	}

}
