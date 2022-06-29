package org.minigame;

import org.minigame.level.LevelController;
import org.minigame.user.UserController;

import java.util.HashMap;
import java.util.Map;

public class RootContext {

    private Map<Class<?>, Object> beans;

    private final HttpHelper httpHelper;
    private final UserController userController;
    private final LevelController levelController;

    public RootContext(HttpHelper httpHelper, UserController userController, LevelController levelController) {
        this.httpHelper = httpHelper;
        this.levelController = levelController;
        this.userController = userController;

        beans = new HashMap<>();
        beans.put(HttpHelper.class, this.httpHelper);
        beans.put(UserController.class, this.userController);
        beans.put(LevelController.class, this.levelController);
    }

    public Object getBean(Class<?> clazz) {
        return beans.get(clazz);
    }

}
