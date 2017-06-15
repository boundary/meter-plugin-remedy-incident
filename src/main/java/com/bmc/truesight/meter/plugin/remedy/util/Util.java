package com.bmc.truesight.meter.plugin.remedy.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bmc.thirdparty.org.apache.commons.codec.binary.Base64;
import com.bmc.truesight.meter.plugin.remedy.RemedyPluginConfigurationItem;
import com.bmc.truesight.saas.remedy.integration.beans.Configuration;
import com.bmc.truesight.saas.remedy.integration.beans.Template;
import com.boundary.plugin.sdk.Event;
import com.boundary.plugin.sdk.EventSinkStandardOutput;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 *
 * @author Santosh Patil
 * @author vitiwari
 *
 */
public class Util {

    private static final Logger LOG = LoggerFactory
            .getLogger(Util.class);

    public static String getFieldValues(String fieldValue[]) {
        StringBuffer fieldValues = new StringBuffer();
        if (fieldValue != null && fieldValue.length > 1) {
            for (String val : fieldValue) {
                fieldValues.append(val);
            }
        }
        return fieldValues.toString();
    }

    public static Map<JsonElement, String> getKeyAndValueFromJsonObjectForRemedy(JsonObject jsonObjects, String jsonKey) {
        Map<JsonElement, String> values = new HashMap<>();
        JsonObject jsonObject = jsonObjects.get(jsonKey).getAsJsonObject();
        if (jsonObject.isJsonObject()) {
            Set<Map.Entry<String, JsonElement>> elements = ((JsonObject) jsonObject).entrySet();
            if (elements != null) {
                // Iterate JSON Elements with Key values
                elements.stream().forEach((en) -> {
                    values.put(en.getValue(), en.getKey());
                });
            }
        }
        return values;
    }

    public static String encodeBase64(final String encodeToken) {
        byte[] encoded = Base64.encodeBase64(encodeToken.getBytes());
        return new String(encoded);
    }

    public static void send(final Event.EventSeverity severity, final String strShortSummary, final String strSummary, final String hostName, final String source) {
        EventSinkStandardOutput output = new EventSinkStandardOutput();
        Event event = new Event(severity, strShortSummary,
                strSummary, hostName, source, null);
        output.emit(event);
    }

    public static String remove(String removeString, String originalString) {

        originalString = originalString.replace(removeString, "").trim();
        return originalString;
    }

    public static Event eventMeterTSI(final String title, final String message, String severity) {
        Event event = new Event(title, message);
        return event;
    }

    public static Template updateConfiguration(Template template, RemedyPluginConfigurationItem config) {
        Configuration configuration = template.getConfig();
        configuration.setRemedyHostName(config.getHostName());
        configuration.setRemedyPort(config.getPort());
        configuration.setRemedyUserName(config.getUserName());
        configuration.setRemedyPassword(config.getPassword());
        //template.setConfig(configuration);
        return template;
    }
}
