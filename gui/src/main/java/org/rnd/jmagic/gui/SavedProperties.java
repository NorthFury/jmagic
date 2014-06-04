package org.rnd.jmagic.gui;

import java.beans.DefaultPersistenceDelegate;
import java.beans.Encoder;
import java.beans.ExceptionListener;
import java.beans.Expression;
import java.beans.PersistenceDelegate;
import java.beans.Statement;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * XML-encodes properties to a file whenever any property is changed via
 * {@link #put(Object, Object)}. Use {@link #setFileName(String)} to set what
 * filename the properties are encoded to.
 */
public class SavedProperties extends Properties
{
	private static final Set<String> OLD_CLASSES;

	static
	{
		OLD_CLASSES = new HashSet<String>();
		OLD_CLASSES.add("org/rnd/jmagic/gui/Properties");
		OLD_CLASSES.add("org.rnd.jmagic.gui.Properties");
	}

	public static SavedProperties createFromFile(String filePath)
	{
		final AtomicBoolean fix = new AtomicBoolean(false);
		ExceptionListener exceptionListener = new ExceptionListener()
		{
			private boolean firstException = true;

			@Override
			public void exceptionThrown(Exception e)
			{
				if(this.firstException)
				{
					// A ClassNotFoundException will be thrown first if the
					// class of the properties object changed
					if((e instanceof ClassNotFoundException) && OLD_CLASSES.contains(e.getMessage()))
						fix.set(true);
					this.firstException = false;
				}
				LOG.log(Level.WARNING, "Error during decoding properties object or closing XML decoder", e);
			}
		};

		SavedProperties ret;
		XMLDecoder xml = null;
		try
		{
			xml = new XMLDecoder(new BufferedInputStream(new FileInputStream(filePath)), null, exceptionListener);

			ret = (SavedProperties)xml.readObject();
			// This happens in Java 6 instead of throwing an
			// ArrayIndexOutOfBoundsException
			if(null == ret)
				throw new ArrayIndexOutOfBoundsException();
		}
		catch(FileNotFoundException e)
		{
			LOG.log(Level.WARNING, "Could not find properties file", e);
			ret = new SavedProperties();
		}
		// Since the encoded properties object should be the only object in the
		// file, an error in decoding makes the file effectively empty, so this
		// communicates an error in decoding the properties object
		catch(ArrayIndexOutOfBoundsException e)
		{
			if(fix.get())
				return createFromFixedPropertiesFile(filePath);
			ret = new SavedProperties();
		}
		finally
		{
			if(null != xml)
				xml.close();
		}

		ret.setFileName(filePath);
		return ret;
	}

	private static SavedProperties createFromFixedPropertiesFile(String filePath)
	{
		try
		{
			Charset propertiesCharset = Charset.forName("UTF-8");
			FileInputStream propertiesFile = new FileInputStream(filePath);
			InputStreamReader propertiesFileDecoded = new InputStreamReader(propertiesFile, propertiesCharset);
			BufferedReader in = new BufferedReader(propertiesFileDecoded);

			StringBuilder fileContents = new StringBuilder();
			boolean foundJavaLine = false;
			String line = in.readLine();
			while(null != line)
			{
				if(foundJavaLine)
				{
					line = line.replace("<object class=\"org.rnd.jmagic.gui.Properties\">", "<object class=\"" + SavedProperties.class.getName() + "\">");
					foundJavaLine = false;
				}
				else
					foundJavaLine = line.startsWith("<java");
				fileContents.append(line);
				line = in.readLine();
			}
			in.close();

			byte[] buffer = fileContents.toString().getBytes(propertiesCharset);
			XMLDecoder xml = new XMLDecoder(new BufferedInputStream(new ByteArrayInputStream(buffer)));
			SavedProperties ret = (SavedProperties)xml.readObject();
			ret.setFileName(filePath);
			xml.close();
			return ret;
		}
		catch(FileNotFoundException e)
		{
			LOG.log(Level.WARNING, "Could not find properties file", e);
		}
		catch(IOException e)
		{
			LOG.log(Level.WARNING, "Error reading/writing properties file", e);
		}

		return new SavedProperties();
	}

	private static final Logger LOG = Logger.getLogger(SavedProperties.class.getName());

	private static final PersistenceDelegate MAP_PERSISTENCE_DELEGATE = new DefaultPersistenceDelegate()
	{
		@Override
		protected void initialize(Class<?> type, Object oldInstance, Object newInstance, Encoder out)
		{
			@SuppressWarnings("unchecked") Map<Object, Object> map = (Map<Object, Object>)oldInstance;

			for(Map.Entry<Object, Object> entry: map.entrySet())
				out.writeStatement(new Statement(oldInstance, "put", new Object[] {entry.getKey(), entry.getValue()}));
		}

		@Override
		protected Expression instantiate(Object oldInstance, Encoder out)
		{
			return super.instantiate(oldInstance, out);
		}
	};

	private static final long serialVersionUID = 1L;

	private String fileName = null;

	@Override
	public synchronized Object put(Object key, Object value)
	{
		Object ret = super.put(key, value);
		saveProperties();
		return ret;
	}

	private void saveProperties()
	{
		if(null == this.fileName)
			return;

		try
		{
			XMLEncoder xml = new XMLEncoder(new BufferedOutputStream(new FileOutputStream(this.fileName)));
			xml.setPersistenceDelegate(SavedProperties.class, MAP_PERSISTENCE_DELEGATE);
			xml.writeObject(this);
			xml.close();
		}
		catch(FileNotFoundException e)
		{
			LOG.log(Level.WARNING, "Could not create properties file", e);
		}
	}

	public void setFileName(String fileName)
	{
		this.fileName = fileName;
	}
}
