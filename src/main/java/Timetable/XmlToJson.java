package Timetable;

import org.json.JSONObject;

import java.net.http.HttpResponse;

public class XmlToJson {
    public static JSONObject ToJSON(HttpResponse<String> response) {
        JSONObject timetable = new JSONObject();
        timetable.put("station", response.body());

        return timetable;
    }
}
