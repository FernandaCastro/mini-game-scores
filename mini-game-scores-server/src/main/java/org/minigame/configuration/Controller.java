package org.minigame.configuration;

import java.util.Map;

public interface Controller {

    MiniGameResponse execute(String action, String body, String pathVar, Map<String, String> queryParam);
}
