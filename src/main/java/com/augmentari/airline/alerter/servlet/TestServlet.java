package com.augmentari.airline.alerter.servlet;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.webharvest.Harvest;
import org.webharvest.HarvestLoadCallback;
import org.webharvest.Harvester;
import org.webharvest.definition.ConfigSource;
import org.webharvest.definition.ConfigSourceFactory;
import org.webharvest.definition.IElementDef;
import org.webharvest.ioc.HttpModule;
import org.webharvest.ioc.ScraperModule;
import org.webharvest.runtime.DynamicScopeContext;
import org.webharvest.runtime.web.HttpClientManager;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class TestServlet extends HttpServlet
{
    public static String wizzairPrice = "NONE YET";
    private static Logger LOGGER = LoggerFactory.getLogger(TestServlet.class);

    @Override
    public void init() throws ServletException
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

            Injector injector = Guice.createInjector(new ScraperModule("."),
                    new HttpModule(settings));

            ConfigSourceFactory configSourceFactory =
                    injector.getInstance(ConfigSourceFactory.class);
            Harvest harvest = injector.getInstance(Harvest.class);

            String config = IOUtils.toString(getClass().getResourceAsStream("/wizzair-scraper.xml"));
            ConfigSource configSource = configSourceFactory.create(config);

            final Harvester harvester = harvest.getHarvester(configSource, new HarvestLoadCallback()
            {
                @Override
                public void onSuccess(List<IElementDef> elements)
                {
                    LOGGER.info("Successfully initialized web scrapper.");
                }
            });

            DynamicScopeContext context = harvester.execute(new Harvester.ContextInitCallback()
            {
                @Override
                public void onSuccess(DynamicScopeContext context)
                {
                    LOGGER.info("Harvester initialization successful.");
                }
            });
            wizzairPrice = context.getVar("price").toString();
            LOGGER.info("Successful invocation, wizzair price is {}", wizzairPrice);

        } catch (Exception e)
        {
            LOGGER.error("Could not initialize web scraper. ", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        resp.getOutputStream().write(wizzairPrice.getBytes());
    }
}
