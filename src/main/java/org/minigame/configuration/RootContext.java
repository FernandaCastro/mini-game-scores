package org.minigame.configuration;

import org.minigame.level.LevelController;
import org.minigame.session.SessionController;
import org.minigame.session.SessionRepository;
import org.minigame.session.SessionService;

import java.time.Clock;
import java.util.HashMap;
import java.util.Map;

public class RootContext {

    private Map<Class<?>, Object> beans;

    private final HttpHelper httpHelper;
    private final SessionController sessionController;
    private final LevelController levelController;

    //TODO: Refactor to accumulate beans direct in the Map
    public RootContext(Clock clock, HttpHelper httpHelper, SessionRepository sessionRepository, SessionService sessionService, SessionController userController, LevelController levelController) {
        this.httpHelper = httpHelper;
        this.levelController = levelController;
        this.sessionController = userController;

        beans = new HashMap<>();
        beans.put(HttpHelper.class, this.httpHelper);
        beans.put(SessionController.class, this.sessionController);
        beans.put(LevelController.class, this.levelController);
    }

    public Object getBean(Class<?> clazz) {
        return beans.get(clazz);
    }

}
