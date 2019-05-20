package it.polimi.ingsw.server.presenter;

import it.polimi.ingsw.client.view.console.ConsoleView;
import it.polimi.ingsw.server.presenter.exceptions.RegexException;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonReader;
import javax.json.JsonValue;

public class RegexValidation {

    private static Map<String, List<Entry<Pattern, String>>> regexConnection = new HashMap<>();


    static {
        InputStream in = RegexValidation.class.getClassLoader().getResourceAsStream("Patter.json");
        JsonArray jsonArray = Json.createReader(in).readArray();
        jsonArray.stream().map(JsonValue::asJsonObject).forEach(x ->
                {
                    List<Entry<Pattern, String>> tmpList = new ArrayList<>();
                    String methodName = x.getString("method");
                    JsonArray jsonValues = x.getJsonArray("value");
                    jsonValues.stream().map(JsonValue::asJsonObject).forEach(y -> {
                        Pattern p;
                        p = Pattern.compile(y.getString("regex"));
                        tmpList.add(new SimpleEntry(p, y.getString("error")));
                    });
                    regexConnection.put(methodName, tmpList);
                }
        );

    }

    public static List<String> checkRegex(String value) throws RegexException {
        int i = 0;
        Throwable t = new Throwable();
        StackTraceElement[] stackTraceElements = t.getStackTrace();
        List<String> returnList = new ArrayList<>();
        List<Entry<Pattern, String>> test = regexConnection
                .get(stackTraceElements[1].getMethodName());
        Matcher m = test.get(i).getKey().matcher(value);
        while (m.lookingAt()) {
            returnList.add(m.group().replaceAll(" ", ""));
            value = value.substring(m.end());
            i++;
            if (i >= test.size()) {
                break;
            }
            m = test.get(i).getKey().matcher(value);

        }
        if (i < test.size()) {
            throw new RegexException(test.get(i).getValue());
        } else if (value.length() > 0) {
            throw new RegexException(test.get(i - 1).getValue());
        }
        return returnList;

    }


}
