package org.minigame.configuration;

import com.sun.net.httpserver.HttpExchange;

public interface Controller {

    void execute(String action, HttpExchange exchange);
}
