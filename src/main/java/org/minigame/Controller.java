package org.minigame;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

public interface Controller {

    void execute(String action, HttpExchange exchange) throws IOException;
}
