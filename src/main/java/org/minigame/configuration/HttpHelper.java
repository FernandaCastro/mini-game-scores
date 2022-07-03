package org.minigame.configuration;

import com.sun.net.httpserver.HttpExchange;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HttpHelper {

    private final String REGEX_URI = "/([0-9]+)/([a-zA-Z]+)";
    public Pattern PATTERN_URI = Pattern.compile(REGEX_URI);
    public final String SESSION_KEY = "sessionkey";

    public String readRequestBody(HttpExchange exchange) {

        try {

            BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody()));
            StringBuilder stringBuilder = new StringBuilder();

            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
            reader.close();
            return stringBuilder.toString();

        }catch (IOException e){
            throw new IllegalArgumentException("Unable to read RequestBody: " + e.getMessage());
        }
    }

    public void sendResponse(HttpStatus httpStatus, String response, HttpExchange exchange) {
        try {
            exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
            exchange.sendResponseHeaders(httpStatus.getStatusCode(), response.length());

            OutputStream responseBody = exchange.getResponseBody();
            responseBody.write(response.getBytes());

            //System.out.println(httpStatus.getStatusCode() + ":" + response);
            exchange.close();

        }catch (IOException e){
            //TODO: Unable to send response anyway, just log the error
            e.printStackTrace();
        }
    }

    public String getPathVariable(HttpExchange exchange){

        Matcher matcher = PATTERN_URI.matcher(exchange.getRequestURI().getPath());
        if (matcher.matches()) {
            return matcher.group(1);
        }
        return null;
    }

    public Map<String, String> getQueryParam(HttpExchange exchange){

        Map<String, String> map = new HashMap<>();

        String query = exchange.getRequestURI().getQuery();
        if (query == null || query.isBlank())
            return null;

        String[] params = query.split("&");

        for (String param : params) {
            String[] keyValue = param.split("=");
            if (keyValue.length != 2)
                return null;

            map.put(keyValue[0], keyValue[1]);
        }

        return map;
    }
}
