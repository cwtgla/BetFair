package model;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.StrictHostnameVerifier;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import betfairUtils.ApingOperation;
import betfairUtils.EventTypeResult;
import betfairUtils.EventTypeResultContainer;
import betfairUtils.HttpUtil;
import betfairUtils.JsonConverter;
import betfairUtils.JsonrpcRequest;
import betfairUtils.ListMarketBooksContainer;
import betfairUtils.ListMarketCatalogueContainer;
import betfairUtils.LoginResponse;
import betfairUtils.MarketBook;
import betfairUtils.MarketCatalogue;
import betfairUtils.MarketFilter;
import betfairUtils.MarketProjection;
import betfairUtils.MarketSort;
import betfairUtils.OrderProjection;
import betfairUtils.PriceProjection;
import betfairUtils.TimeRange;

import com.google.gson.Gson;

public class Core
{
	// private static final String username = "0ocwto0"; //prompt?
	// private static final String password = "@Cracker93"; //prompt?
	private static final String filePassword = "cracker"; // prompt?
	private static final String liveKey = "ztgZ1aJPu2lvvW6a"; // hard code req
	private static final String delayedKey = "scQ6H11vdb6C4s7t"; // hard code
																	// req
	private static int port = 443; // ??? whatever
	private String dirPrefix;
	private String sessionToken;
	protected final String FILTER = "filter";
	protected final String LOCALE = "locale";
	protected final String SORT = "sort";
	protected final String MAX_RESULT = "maxResults";
	protected final String MARKET_IDS = "marketIds";
	protected final String MARKET_ID = "marketId";
	protected final String INSTRUCTIONS = "instructions";
	protected final String CUSTOMER_REF = "customerRef";
	protected final String MARKET_PROJECTION = "marketProjection";
	protected final String PRICE_PROJECTION = "priceProjection";
	protected final String MATCH_PROJECTION = "matchProjection";
	protected final String ORDER_PROJECTION = "orderProjection";

	// Special method for login (ssl connection and encrypted password sent)
	// So special method for a request too

	// Then separate methods for calls and dealing with filters

	// Look at enums

	public Core()
	{
		dirPrefix = System.getProperty("user.dir");
	}

	public void login(String username, String password) throws Exception
	{
		// Client important since it will do requests for us?
		DefaultHttpClient httpClient = new DefaultHttpClient();
		Gson gson = new Gson();

		try
		{
			// SSL stuff
			SSLContext ctx = SSLContext.getInstance("TLS");
			// KeyManager[] keyManagers = getKeyManagers("pkcs12",new
			// FileInputStream(new File(dirPrefix +
			// "\\certs\\client-2048.p12")), filePassword);
			KeyManager[] keyManagers = getKeyManagers("pkcs12",
					new FileInputStream(new File(dirPrefix
							+ "/certs/client-2048.p12")), filePassword);

			ctx.init(keyManagers, null, new SecureRandom());
			SSLSocketFactory factory = new SSLSocketFactory(ctx,
					new StrictHostnameVerifier());
			ClientConnectionManager manager = httpClient.getConnectionManager();
			manager.getSchemeRegistry().register(
					new Scheme("https", port, factory));

			// Making a post object
			HttpPost httpPost = new HttpPost(
					"https://identitysso.betfair.com/api/certlogin");

			// Name value pair used for parameters?
			List<NameValuePair> nvps = new ArrayList<NameValuePair>();
			nvps.add(new BasicNameValuePair("username", username));
			nvps.add(new BasicNameValuePair("password", password));
			// httpPost.setParams(new HttpParams);
			// No idea
			httpPost.setEntity(new UrlEncodedFormEntity(nvps));

			// Header
			httpPost.setHeader("X-Application", "appkey");
			System.out.println("Executing request: "
					+ httpPost.getRequestLine());

			// Execute
			HttpResponse response = httpClient.execute(httpPost);
			HttpEntity entity = response.getEntity();

			System.out.println();
			System.out.println(response.getStatusLine());

			if (entity != null)
			{
				LoginResponse responseObject = gson.fromJson(
						EntityUtils.toString(entity), LoginResponse.class);
				System.out.println(responseObject);
				System.out.println("Response:\nsession token: "
						+ responseObject.sessionToken + " status: "
						+ responseObject.loginStatus);
				sessionToken = responseObject.sessionToken;

				// TODO Fix this
				List<EventTypeResult> r = listEventTypes(new MarketFilter(),
						liveKey, responseObject.sessionToken);
			}
		} finally
		{
			httpClient.getConnectionManager().shutdown();
		}
	}

	// private void printMarketCatalogue(MarketCatalogue mk)
	// {
	// System.out.println("Market Name: " + mk.getMarketName() + "; Id: "
	// + mk.getMarketId() + "\n");
	// List<RunnerCatalog> runners = mk.getRunners();
	// if (runners != null)
	// {
	// for (RunnerCatalog rCat : runners)
	// {
	// System.out.println("Runner Name: " + rCat.getRunnerName()
	// + "; Selection Id: " + rCat.getSelectionId() + "\n");
	// }
	// }
	// }

	protected String makeRequest(String operation, Map<String, Object> params,
			String appKey, String ssoToken)
	{
		String requestString;
		// Handling the JSON-RPC request
		JsonrpcRequest request = new JsonrpcRequest();
		request.setId("1");
		request.setMethod("SportsAPING/v1.0/" + operation);
		request.setParams(params);

		requestString = JsonConverter.convertToJson(request);
		System.out.println("\nRequest: " + requestString);

		// We need to pass the "sendPostRequest" method a string in util format:
		// requestString
		HttpUtil requester = new HttpUtil();
		return requester.sendPostRequestJsonRpc(requestString, operation,
				appKey, sessionToken);
	}

	public List<MarketBook> listMarketBook(List<String> marketIds,
			PriceProjection priceProjection, OrderProjection orderProjection,
			MarketProjection matchProjection, String currencyCode,
			String appKey, String ssoId) throws Exception
	{
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(LOCALE, Locale.getDefault().toString());
		params.put(MARKET_IDS, marketIds);
		params.put(PRICE_PROJECTION, priceProjection);
		params.put(ORDER_PROJECTION, orderProjection);
		params.put(MATCH_PROJECTION, matchProjection);
		params.put("currencyCode", currencyCode);
		String result = makeRequest(ApingOperation.LISTMARKETBOOK.toString(),
				params, appKey, sessionToken);
		// String result = getInstance().makeRequest(
		// ApiNgOperation.LISTMARKETBOOK.getOperationName(), params,
		// appKey, ssoId);
		// if (ApiNGDemo.isDebug())
		System.out.println("\nResponse: " + result);

		ListMarketBooksContainer container = JsonConverter.convertFromJson(
				result, ListMarketBooksContainer.class);

		if (container.getError() != null)
			System.out.println("CONTAINER ERROR");
		// throw container.getError().getData().getAPINGException();

		return container.getResult();

	}

	public void getMarketCatalogue() throws Exception
	{
		ApingOperation jsonOperations;
		Set<String> eventCode = new HashSet<String>();
		eventCode.add(Integer.toString(6423));

		TimeRange time = new TimeRange();
		time.setFrom(new Date());
		Date t = new Date();
		t.setDate(31);
		time.setTo(t);
		// time.setTo((Date) (new Date().setDate(10)));
		Set<String> countries = new HashSet<String>();
		// countries.add("GB");

		Set<String> typesCode = new HashSet<String>();
		// typesCode.add("WIN");

		MarketFilter marketFilter = new MarketFilter();
		marketFilter.setEventTypeIds(eventCode);
		marketFilter.setMarketStartTime(time);
		marketFilter.setMarketCountries(countries);
		marketFilter.setMarketTypeCodes(typesCode);

		Set<MarketProjection> marketProjection = new HashSet<MarketProjection>();
		marketProjection.add(MarketProjection.RUNNER_DESCRIPTION);

		String maxResults = "1";

		List<MarketCatalogue> marketCatalogueResult = listMarketCatalogue(
				marketFilter, marketProjection, MarketSort.FIRST_TO_START,
				maxResults, liveKey, sessionToken);
	}

	public List<MarketCatalogue> listMarketCatalogue(MarketFilter filter,
			Set<MarketProjection> marketProjection, MarketSort sort,
			String maxResult, String appKey, String ssoId) throws Exception
	{
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(LOCALE, Locale.getDefault().toString());
		params.put(FILTER, filter);
		params.put(SORT, sort);
		params.put(MAX_RESULT, maxResult);
		params.put(MARKET_PROJECTION, marketProjection);
		// String result = getInstance().makeRequest(
		// ApiNgOperation.LISTMARKETCATALOGUE.getOperationName(), params,
		// appKey, ssoId);
		String result = makeRequest(
				ApingOperation.LISTMARKETCATALOGUE.toString(), params, appKey,
				sessionToken);
		// if (ApiNGDemo.isDebug())
		System.out.println("\nResponse: " + result);

		ListMarketCatalogueContainer container = JsonConverter.convertFromJson(
				result, ListMarketCatalogueContainer.class);

		if (container.getError() != null)
			System.out.println("EXPCEITON");
		// throw container.getError().getData().getAPINGException();

		return container.getResult();

	}

	public List<EventTypeResult> listEventTypes(MarketFilter filter,
			String appKey, String ssoId)
	{
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("filter", filter);
		params.put("locale", Locale.getDefault().toString());

		String result = makeRequest(
				ApingOperation.LISTEVENTTYPES.getOperationName(), params,
				appKey, ssoId);
		System.out.println("\nResponse: " + result);

		EventTypeResultContainer container = JsonConverter.convertFromJson(
				result, EventTypeResultContainer.class);
		if (container.getError() != null)
			System.out.println("err");

		return container.getResult();
	}

	private KeyManager[] getKeyManagers(String keyStoreType,
			InputStream keyStoreFile, String keyStorePassword) throws Exception
	{
		KeyStore keyStore = KeyStore.getInstance(keyStoreType);
		keyStore.load(keyStoreFile, keyStorePassword.toCharArray());
		KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory
				.getDefaultAlgorithm());
		kmf.init(keyStore, keyStorePassword.toCharArray());
		return kmf.getKeyManagers();
	}
}