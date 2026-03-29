package org.voximir.sky_torch.laser;

import java.util.ArrayList;
import java.util.List;

public abstract class GameObject {

    private static final List<GameObject> liveGameObjects = new ArrayList<>();

    public static List<GameObject> getLive() {
        return List.copyOf(liveGameObjects);
    }

    public GameObject() {
        liveGameObjects.add(this);
    }

    public void remove() {
        liveGameObjects.remove(this);
    }

    public abstract void update();
    public abstract void render();
}
