package com.augmentari.airline.alerter.servlet;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.webharvest.Harvest;
import org.webharvest.Harvester;
import org.webharvest.definition.ConfigSource;
import org.webharvest.definition.ConfigSourceFactory;
import org.webharvest.ioc.HttpModule;
import org.webharvest.ioc.ScraperModule;
import org.webharvest.runtime.DynamicScopeContext;
import org.webharvest.runtime.web.HttpClientManager;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

public class TestServlet extends HttpServlet
{
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        super.doGet(req, resp);

        final Injector injector = Guice.createInjector(
                new ScraperModule("."),
                new HttpModule(HttpClientManager.ProxySettings.NO_PROXY_SET));

        final ConfigSourceFactory configSourceFactory =
                injector.getInstance(ConfigSourceFactory.class);
        final Harvest harvest = injector.getInstance(Harvest.class);
        final ConfigSource configSource = configSourceFactory.create(new File("/tmp/web-harvest-code/examples/canon.xml"));

        final Harvester harvester = harvest.getHarvester(configSource, null);
        harvester.execute(new Harvester.ContextInitCallback()
        {
            @Override
            public void onSuccess(DynamicScopeContext context)
            {
                String s = context.getVar("outPrice").toString();
            }
        });

        // takes variable created during execution
    }
}
