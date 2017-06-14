package com.bmc.truesight.meter.plugin.remedy;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bmc.arsys.api.ARServerUser;
import com.bmc.arsys.api.OutputInteger;
import com.bmc.truesight.meter.plugin.remedy.util.Constants;
import com.bmc.truesight.meter.plugin.remedy.util.Util;
import com.bmc.truesight.saas.remedy.integration.ARServerForm;
import com.bmc.truesight.saas.remedy.integration.RemedyReader;
import com.bmc.truesight.saas.remedy.integration.adapter.RemedyEntryEventAdapter;
import com.bmc.truesight.saas.remedy.integration.beans.TSIEvent;
import com.bmc.truesight.saas.remedy.integration.beans.Template;
import com.bmc.truesight.saas.remedy.integration.impl.GenericRemedyReader;
import com.boundary.plugin.sdk.Collector;
import com.boundary.plugin.sdk.Event;
import com.boundary.plugin.sdk.EventSinkAPI;
import com.boundary.plugin.sdk.EventSinkStandardOutput;
import com.boundary.plugin.sdk.Measurement;
import com.google.gson.Gson;

/**
 *
 * @author vitiwari
 */
public class RemedyTicketsCollector implements Collector {

    private static final Logger LOG = LoggerFactory.getLogger(RemedyPlugin.class);
    private final RemedyPluginConfigurationItem config;
    private final Template template;
    private final ARServerForm arServerForm;
    private RemedyEntryEventAdapter remedyEntryEventAdapter = new RemedyEntryEventAdapter();

    public RemedyTicketsCollector(RemedyPluginConfigurationItem config, Template template, ARServerForm arServerForm) {
        this.config = config;
        this.template = template;
        template = Util.updateConfiguration(template, config);
        this.arServerForm = arServerForm;
    }

    @Override
    public Measurement[] getMeasures() {
        return null;
    }

    @Override
    public void run() {
        //System.err.println("Collector running ");
        EventSinkAPI eventSinkAPI = new EventSinkAPI();
        EventSinkStandardOutput eventSinkAPIstd = new EventSinkStandardOutput();

        while (true) {
            try {
                RemedyReader reader = new GenericRemedyReader();
                ARServerUser arServerContext = reader.createARServerContext(config.getHostName(), config.getPort(), config.getUserName(), config.getPassword());
                Calendar cal = null;
                try {
                    reader.login(arServerContext);
                    int chunkSize = Constants.REMEDY_CHUNK_SIZE;
                    int startFrom = 0;
                    int iteration = 1;
                    OutputInteger nMatches = new OutputInteger();
                    boolean readNext = true;
                    cal = Calendar.getInstance();
                    cal.add(Calendar.MINUTE, (0 - config.getPollInterval()));
                    template.getConfig().setStartDateTime(cal.getTime());
                    template.getConfig().setEndDateTime(new Date());
                    while (readNext) {
                        //System.err.println("before reading from remedy ");
                        List<TSIEvent> eventList = reader.readRemedyTickets(arServerContext, arServerForm, template, startFrom, chunkSize, nMatches, remedyEntryEventAdapter);
                        System.err.println("remedy events got {}" + eventList.size());
                        if (eventList.size() > 0) {
                            eventList.forEach(event -> {
                                Gson gson = new Gson();
                                String eventJson = gson.toJson(event, Object.class);
                                StringBuilder sendEventToTSI = new StringBuilder();
                                sendEventToTSI.append(Constants.REMEDY_PROXY_EVENT_JSON_START_STRING).append(eventJson).append(Constants.REMEDY_PROXY_EVENT_JSON_END_STRING);
                                eventSinkAPI.emit(sendEventToTSI.toString());
                            });
                        } else {
                            System.err.println("No remedy Data available");
                            eventSinkAPIstd.emit(Util.eventMeterTSI(Constants.REMEDY_PLUGIN_TITLE_MSG, Constants.REMEDY_IM_NO_DATA_AVAILABLE, Event.EventSeverity.INFO.toString()));
                        }
                        if (nMatches.longValue() <= (startFrom + chunkSize)) {
                            readNext = false;
                        }
                        iteration++;
                        startFrom = startFrom + chunkSize;
                    }

                } catch (Exception e) {
                    System.err.println("Exception occure while fetching the data" + e.getMessage());
                    eventSinkAPIstd.emit(Util.eventMeterTSI(Constants.REMEDY_PLUGIN_TITLE_MSG, e.getMessage(), Event.EventSeverity.ERROR.toString()));
                } finally {
                    reader.logout(arServerContext);
                }
                Thread.sleep(config.getPollInterval());
            } catch (InterruptedException ex) {
                eventSinkAPIstd.emit(Util.eventMeterTSI(Constants.REMEDY_PLUGIN_TITLE_MSG, ex.getMessage(), Event.EventSeverity.ERROR.toString()));
            }
        }
    }

}
