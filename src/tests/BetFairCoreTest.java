package tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import model.BetFairCore;

import org.junit.Before;
import org.junit.Test;

import betFairGSONClasses.LoginResponse;
import exceptions.BadLoginDetailsException;
import exceptions.CryptoException;

/**
 * Test class for all BetFairCore methods.
 * 
 * @author Craig Thomson
 *
 */
public class BetFairCoreTest
{
	private static final boolean debug = true;
	private static final String userName = "0ocwto0";
	private static final String password = "2014Project";
	private static final String filePassword = "project";
	private BetFairCore betFair;

	/**
	 * Creating a BetFairCore object for tests to use
	 */
	@Before
	public void makeObject()
	{
		betFair = new BetFairCore(debug);
	}

	/**
	 * Testing BetFairCore login when given bad certificate file password
	 */
	@Test
	public void testLoginBadFilePassword()
	{
		try
		{
			betFair.login(userName, password, filePassword + "A");
			fail("CryptoException expected in testLoginBadFilePassword()");
		}
		catch (CryptoException expectedException)
		{
			System.out.println("testLoginBadFilePassword() pass!");
		}
	}

	/**
	 * Testing BetFairCore login when given bad account username
	 */
	@Test
	public void testLoginBadUsername()
	{
		try
		{
			betFair.login(userName + "A", password, filePassword);
			fail("CryptoException expected in testLoginBadUsername()");
		}
		catch (BadLoginDetailsException expectedException)
		{
			System.out.println("testLoginBadUsername() pass!");
		}
	}

	/**
	 * Testing BetFairCore login when given bad account password
	 */
	@Test
	public void testLoginBadPassword()
	{
		try
		{
			betFair.login(userName, password + "A", filePassword);
			fail("CryptoException expected in testLoginBadPassword()");
		}
		catch (BadLoginDetailsException expectedException)
		{
			System.out.println("testLoginBadPassword() pass!");
		}
	}

	/**
	 * Testing BetFairCore login when given correct credentials
	 */
	@Test
	public void testLoginSuccess()
	{
		try
		{
			LoginResponse response = betFair.login(userName, password,
					filePassword);
			assertEquals(response.getLoginStatus(), "SUCCESS");
		}
		catch (CryptoException expectedException)
		{
			System.out.println("testLoginBadPassword() pass!");
			fail("CryptoException not expected in testLoginSuccess()");
		}
	}

	/**
	 * Testing BetFairCore login when given correct credentials with no internet
	 * connection
	 */
	@Test
	public void testLoginNoInternet()
	{
		try
		{
			betFair.login(userName, password, filePassword);
			fail();
		}
		catch (Exception expectedException)
		{
			// expected
		}
	}
	// TODO test you get data back in multiple calls, need to call login first
	// before all following tests
	// TODO test implemented betfair methods are returning the right stuff
	// TODO test no internet
	// TODO test unique token?
	// TODO test event fired
	// TODO test markets closing
	// TODO test debug?

}
