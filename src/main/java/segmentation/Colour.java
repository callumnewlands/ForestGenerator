package segmentation;

import org.joml.Vector3f;

public class Colour {

    private Colour() {}

    public static Vector3f twig = new Vector3f(1.f, 0.5f, 0.f); // ORANGE -> OBSTACLE

    public static Vector3f ground = new Vector3f(0.5f, 0.5f, 0.5f); // GREY -> TERRAIN

    public static Vector3f extModel = new Vector3f(1.f, 0.5f, 0.f); // ORANGE -> OBSTACLE

    public static Vector3f fallenLeaves = new Vector3f(0.5f, 0.5f, 0.5f); // GREY -> TERRAIN

}
