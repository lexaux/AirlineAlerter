package com.augmentari.airline.alerter.servlet;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.webharvest.Harvest;
import org.webharvest.definition.ConfigSource;
import org.webharvest.definition.ConfigSourceFactory;
import org.webharvest.ioc.HttpModule;
import org.webharvest.ioc.ScraperModule;
import org.webharvest.runtime.web.HttpClientManager;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.net.UnknownHostException;
import java.util.Timer;

public class EntryPoint implements ServletContextListener
{
    public static final int WIZZAIR_UPDATE_PERIOD = 3 * 60 * 60 * 1000; //every 3 hours do query wizzair
    private static final Logger LOGGER = LoggerFactory.getLogger(EntryPoint.class);
    public static Harvest harvest;
    public static ConfigSource configSource;
    private static DB db = null;
    private MongoClient mongoClient;
    private Timer scraperTimer;
    private Injector scraperGuiceInjector;

    public static DB getDB()
    {
        return db;
    }

    @Override
    public void contextInitialized(ServletContextEvent sce)
    {
        initializeDatabase();
        initializeScrapper();
        initializeScraperTimer();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce)
    {
        mongoClient.close();
        scraperTimer.cancel();
    }

    private void initializeScrapper()
    {
        try
        {
            HttpClientManager.ProxySettings settings = null;
            if (null != System.getProperty("useDebugProxy"))
            {
                HttpClientManager.ProxySettings.Builder builder = new HttpClientManager.ProxySettings.Builder("127.0.0.1");
                builder.setProxyPort(8008);
                settings = builder.build();
            } else
            {
                settings = HttpClientManager.ProxySettings.NO_PROXY_SET;
            }

            scraperGuiceInjector = Guice.createInjector(
                    new ScraperModule("."),
                    new HttpModule(settings));

            harvest = scraperGuiceInjector.getInstance(Harvest.class);

            ConfigSourceFactory configSourceFactory =
                    scraperGuiceInjector.getInstance(ConfigSourceFactory.class);
            String config = IOUtils.toString(getClass().getResourceAsStream("/wizzair-scraper.xml"));
            configSource = configSourceFactory.create(config);

        } catch (Exception e)
        {
            LOGGER.error("Could not initialize web scraper. ", e);
        }
    }

    private void initializeScraperTimer()
    {
        scraperTimer = new Timer(true);
        scraperTimer.schedule(new ScraperTimerTask(), 1000, WIZZAIR_UPDATE_PERIOD);
    }

    private void initializeDatabase()
    {
        try
        {
            // Enable MongoDB logging in general
            System.setProperty("DEBUG.MONGO", "true");

            // Enable DB operation tracing
            System.setProperty("DB.TRACE", "true");

            MongoClientURI mongoUri = new MongoClientURI("mongodb://127.0.0.1:27017/wizzair");
            mongoClient = new MongoClient(mongoUri);

            db = mongoClient.getDB(mongoUri.getDatabase());

            LOGGER.info("Connection to the database established, host is {} and DB is {}",
                    mongoUri.getHosts().get(0),
                    mongoUri.getDatabase());

        } catch (UnknownHostException e)
        {
            LOGGER.error("Could not connect to MongoDB", e);
            throw new IllegalStateException("No connection to the database - can not proceed.", e);
        }
    }
}
