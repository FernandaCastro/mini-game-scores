package org.minigame;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HttpHelper {

    //static final String REGEX_URI = "([http?://]+[-a-zA-Z|.|:|0-9]+)/([0-9]+)/([a-zA-Z]+)";
    static final String REGEX_URI = "/([0-9]+)/([a-zA-Z]+)";
    static final Pattern PATTERN_URI = Pattern.compile(REGEX_URI);

    public String readRequest(HttpExchange exchange) throws IOException{
        InputStream request = exchange.getRequestBody();
        byte[] buffer = new byte[request.available()];
        request.read(buffer);
        return Arrays.toString(buffer);
    }

    public void sendResponse(HttpStatus httpStatus, String response, HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
        exchange.sendResponseHeaders(httpStatus.getStatusCode(), response.length());

        OutputStream responseBody = exchange.getResponseBody();
        responseBody.write(response.getBytes());
        exchange.close();
    }

    public String getPathVariable(HttpExchange exchange){
        Matcher matcher = HttpHelper.PATTERN_URI.matcher(exchange.getRequestURI().getPath());
        if (matcher.matches()) {
            return matcher.group(1);
        }
        return null;
    }
}
