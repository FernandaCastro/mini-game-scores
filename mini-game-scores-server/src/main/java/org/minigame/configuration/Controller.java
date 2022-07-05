package org.minigame.configuration;

import com.sun.net.httpserver.HttpExchange;

import java.util.Map;

public interface Controller {

    void execute(String action, HttpExchange exchange, String body, String pathVar, Map<String, String> queryParam);
}
