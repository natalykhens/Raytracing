package ru.nsu.fit.g13204.Khenkina.file;

import ru.nsu.fit.g13204.Khenkina.surface.Surface;
import ru.nsu.fit.g13204.Khenkina.world.LightSource;

import java.awt.*;
import java.util.List;

/**
 * Created by Natalia on 31.05.16.
 */
public class SceneFileData {
    public Color diffusedLight;
    public List<LightSource> sources;
    public List<Surface> surfaces;

    public SceneFileData(Color dLight, List<LightSource> s, List<Surface> surf){
        diffusedLight = dLight;
        sources = s;
        surfaces = surf;
    }
}
