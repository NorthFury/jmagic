package org.rnd.jmagic.comms;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.Exception;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.LinkedList;
import java.util.List;

public class GameFinder
{
	public static class Game
	{
		public final int currentPlayers;

		public final String description;

		public final String format;

		public final String hostPlayerName;

		public final String IP;

		public final int maxPlayers;

		public final int port;

		public Game(String hostPlayerName, String IP, int port, String description, int currentPlayers, int maxPlayers, String format)
		{
			this.currentPlayers = currentPlayers;
			this.description = description;
			this.format = format;
			this.hostPlayerName = hostPlayerName;
			this.IP = IP;
			this.maxPlayers = maxPlayers;
			this.port = port;
		}
	}

	public static class GameFinderException extends Exception
	{
		private static final long serialVersionUID = 1L;

		public GameFinderException(String message)
		{
			super(message);
		}
	}

	private URL cancelURL;

	private URL createURL;

	private URL heartbeatURL;

	private URL listURL;

	private String token = null;

	private URL updateURL;

	private URL versionURL;

	public GameFinder(String location) throws URISyntaxException, IllegalArgumentException, MalformedURLException, IOException, GameFinderException
	{
		if(null == location)
			throw new GameFinderException("Game finder URL is null");
		if(location.isEmpty())
			throw new GameFinderException("Game finder URL is empty");

		URI head = new URI(location);

		this.cancelURL = head.resolve("cancel.php").toURL();
		this.createURL = head.resolve("create.php").toURL();
		this.heartbeatURL = head.resolve("heartbeat.php").toURL();
		this.listURL = head.resolve("list.php").toURL();
		this.updateURL = head.resolve("update.php").toURL();
		this.versionURL = head.resolve("version.php").toURL();

		version();
	}

	public void cancel() throws IOException
	{
		if(null == this.token)
			return;

		HttpURLConnection connection = getPostConnection(this.cancelURL);

		OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream(), "UTF-8");
		out.write("token=" + URLEncoder.encode(this.token, "UTF-8"));
		out.close();

		// Need to get the input stream even if we don't use it so the request
		// will actually be sent
		connection.getInputStream().close();
	}

	public void create(String hostPlayerName, int port, String description, int maxPlayers, String format) throws IOException, GameFinderException
	{
		HttpURLConnection connection = getPostConnection(this.createURL);

		OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream(), "UTF-8");
		out.write("hostPlayerName=" + URLEncoder.encode(hostPlayerName, "UTF-8"));
		out.write("&port=" + URLEncoder.encode(Integer.toString(port), "UTF-8"));
		out.write("&description=" + URLEncoder.encode(description, "UTF-8"));
		out.write("&maxPlayers=" + URLEncoder.encode(Integer.toString(maxPlayers), "UTF-8"));
		out.write("&format=" + URLEncoder.encode(format, "UTF-8"));
		out.close();

		BufferedReader input = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
		String response = "";
		String line = input.readLine();
		while(null != line)
		{
			response += line;
			line = input.readLine();
		}
		input.close();

		response = response.trim();
		if(response.startsWith("Error"))
			throw new GameFinderException("Game-finder returned error: " + response);

		this.token = response;
	}

	private static HttpURLConnection getPostConnection(URL url) throws IOException
	{
		HttpURLConnection ret = (HttpURLConnection)(url.openConnection());
		ret.setRequestMethod("POST");
		ret.setDoOutput(true);
		return ret;
	}

	public void heartbeat() throws IOException
	{
		if(null == this.token)
			return;

		HttpURLConnection connection = getPostConnection(this.heartbeatURL);

		OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream(), "UTF-8");
		out.write("token=" + URLEncoder.encode(this.token, "UTF-8"));
		out.close();

		// Need to get the input stream even if we don't use it so the request
		// will actually be sent
		connection.getInputStream().close();
	}

	public List<Game> list() throws IOException, GameFinderException
	{
		HttpURLConnection connection = (HttpURLConnection)(this.listURL.openConnection());

		List<Game> ret = new LinkedList<Game>();
		BufferedReader input = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
		String line = input.readLine();
		while(null != line)
		{
			line = line.trim();
			if(!line.isEmpty())
			{
				String[] parts = line.split(":");
				if(7 != parts.length)
					throw new GameFinderException("Invalid format from the game-finder of line " + line);
				ret.add(new Game(parts[0], parts[1], Integer.parseInt(parts[2]), parts[3], Integer.parseInt(parts[4]), Integer.parseInt(parts[5]), parts[6]));
			}
			line = input.readLine();
		}
		input.close();
		return ret;
	}

	public void update() throws IOException
	{
		if(null == this.token)
			return;

		HttpURLConnection connection = getPostConnection(this.updateURL);

		OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream(), "UTF-8");
		out.write("token=" + URLEncoder.encode(this.token, "UTF-8"));
		out.close();

		// Need to get the input stream even if we don't use it so the request
		// will actually be sent
		connection.getInputStream().close();
	}

	private void version() throws IOException, GameFinderException
	{
		HttpURLConnection connection = (HttpURLConnection)(this.versionURL.openConnection());

		BufferedReader input = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
		String response = "";
		String line = input.readLine();
		while(null != line)
		{
			response += line;
			line = input.readLine();
		}
		String[] versions = response.trim().split("\\.");
		if((2 != versions.length) || (0 != Integer.parseInt(versions[0])) || (7 != Integer.parseInt(versions[1])))
			throw new GameFinderException("The game-finder is version " + response + "; this version of jMagic only supports a version 0.7 game-finder");
	}
}
