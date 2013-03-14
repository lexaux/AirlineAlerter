package com.augmentari.airline.alerter.servlet;

import com.mongodb.BasicDBObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.webharvest.HarvestLoadCallback;
import org.webharvest.Harvester;
import org.webharvest.definition.IElementDef;
import org.webharvest.runtime.DynamicScopeContext;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimerTask;

public class ScraperTimerTask extends TimerTask
{
    private static final Logger LOGGER = LoggerFactory.getLogger(ScraperTimerTask.class);

    @Override
    public void run()
    {
        LOGGER.info("Running scraper task.");

        try
        {
            final Harvester harvester;
            harvester = EntryPoint.harvest.getHarvester(EntryPoint.configSource, new HarvestLoadCallback()
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
                public void onContextInitSuccess(DynamicScopeContext context)
                {
                    Calendar c = Calendar.getInstance();
                    c.set(Calendar.YEAR, 2013);
                    c.set(Calendar.MONTH, 03);
                    c.set(Calendar.DATE, 29);

                    Date targetDate = c.getTime();
                    DateFormat dashedFormat = new SimpleDateFormat("YYYY-MM-dd");
                    DateFormat slashFormat = new SimpleDateFormat("dd/MM/YYYY");
                    context.setLocalVar("targetDateDashed", dashedFormat.format(targetDate));
                    context.setLocalVar("targetDateSlashes", slashFormat.format(targetDate));

                    context.setLocalVar("fromAirport", "IEV");
                    context.setLocalVar("toAirport", "BUD");
                    LOGGER.info("Harvester initialization successful.");
                }
            });

            String price = context.getVar("price").toString();
            Date when = new Date();

            BasicDBObject recording = new BasicDBObject("recording_date", when).append("price", price);
            EntryPoint.getDB().getCollection("wizzair").insert(recording);

        } catch (IOException e)
        {
            LOGGER.error("Error accessing Wizzair", e);
        }
    }
}
