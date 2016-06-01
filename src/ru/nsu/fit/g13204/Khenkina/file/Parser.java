package ru.nsu.fit.g13204.Khenkina.file;

import ru.nsu.fit.g13204.Khenkina.surface.*;
import ru.nsu.fit.g13204.Khenkina.matrix.Vector;
import ru.nsu.fit.g13204.Khenkina.tracing.RenderQuality;
import ru.nsu.fit.g13204.Khenkina.world.LightSource;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Natalia on 31.05.16.
 */
public class Parser {

    public static void writeScene(SceneFileData data, File file) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));

        writeColor(writer, data.diffusedLight);
        String numberSources = Integer.toString(data.sources.size());
        writer.write(numberSources, 0, numberSources.length());
        writer.newLine();

        writer.newLine();
        for(int i = 0; i < data.sources.size(); ++i){
            writeLightSource(writer, data.sources.get(i));
        }
        writer.newLine();

        for(int i = 0; i < data.surfaces.size(); ++i){
            writeSurface(writer, data.surfaces.get(i));
        }
        writer.close();
    }


    private static void writeLightSource(BufferedWriter writer, LightSource source) throws IOException {
        StringBuilder builder = new StringBuilder();

        double[] pos = source.getPosition().getVector();
        Color c = source.getColor();

        int[] col = new int[] {c.getRed(), c.getGreen(), c.getBlue()};

        for(int i = 0; i < pos.length; ++i){
            builder.append(pos[i]);
            builder.append(' ');
        }

        for(int i = 0; i < col.length; ++i){
            builder.append(col[i]);
            if(i != col.length - 1) {
                builder.append(' ');
            }
        }

        String str =  builder.toString();
        writer.write(str, 0, str.length());
        writer.newLine();
    }


    private static void writeSurface(BufferedWriter writer, Surface surface) throws IOException{
        String type = surface.getClass().getSimpleName();

        type = type.toUpperCase();

        switch (type){
            case "SPHERE":
                writeSphere(writer, surface);
                break;
            case "BOX":
                writeBox(writer, surface);
                break;
            case "TRIANGLE":
                writeTriangle(writer, surface);
                break;
            case "QUADRANGLE":
                writeQuadrangle(writer, surface);
                break;
            default:
                return;
        }
    }

    private static void writeSphere(BufferedWriter writer, Surface surface) throws IOException {
        String str = "SPHERE";
        writer.write(str, 0, str.length());
        writer.newLine();

        Sphere sphere = (Sphere)surface;

        double[] center = sphere.getCenter().getVector();
        str = composeString(center, center.length);
        writer.write(str, 0, str.length());
        writer.newLine();

        double radius = sphere.getRadius();
        str = Double.toString(radius);
        writer.write(str, 0, str.length());
        writer.newLine();

        writeOpticalCharacteristics(sphere.getOpticalCharacteristics(), writer);
    }

    private static void writeBox(BufferedWriter writer, Surface surface) throws IOException {
        String str = "BOX";
        writer.write(str, 0, str.length());
        writer.newLine();

        Box box = (Box)surface;

        double[] minPos = box.getMinPoint().getVector();
        str = composeString(minPos, minPos.length);
        writer.write(str, 0, str.length());
        writer.newLine();

        double[] maxPos = box.getMaxPoint().getVector();
        str = composeString(maxPos, maxPos.length);
        writer.write(str, 0, str.length());
        writer.newLine();

        writeOpticalCharacteristics(box.getOpticalCharacteristics(), writer);
    }

    private static void writeQuadrangle(BufferedWriter writer, Surface surface) throws IOException {
        String str = "QUADRANGLE";
        writer.write(str, 0, str.length());
        writer.newLine();

        Quadrangle quadrangle = (Quadrangle)surface;

        Vector[] vertices = quadrangle.getVertices();

        for(int i = 0; i < vertices.length; ++i){
            writeVector(vertices[i], writer);
        }

        writeOpticalCharacteristics(quadrangle.getOpticalCharacteristics(), writer);
    }

    private static void writeTriangle(BufferedWriter writer, Surface surface) throws IOException {
        String str = "TRIANGLE";
        writer.write(str, 0, str.length());
        writer.newLine();

        Triangle triangle = (Triangle)surface;

        Vector[] vertices = triangle.getVertices();

        for(int i = 0; i < vertices.length; ++i){
            writeVector(vertices[i], writer);
        }

        writeOpticalCharacteristics(triangle.getOpticalCharacteristics(), writer);
    }

    private static void writeVector(Vector v, BufferedWriter writer) throws IOException {
        double[] vect = v.getVector();
        String str = composeString(vect, vect.length);
        writer.write(str, 0, str.length());
        writer.newLine();
    }

    private static void writeOpticalCharacteristics(OpticalCharacteristics oCh, BufferedWriter writer) throws IOException {
        StringBuilder builder = new StringBuilder();

        for(int i = 0; i < oCh.kd.length; ++i){
            builder.append(oCh.kd[i]);
            builder.append(' ');
        }

        for(int i = 0; i < oCh.ks.length; ++i){
            builder.append(oCh.ks[i]);
            builder.append(' ');
        }

        builder.append(oCh.power);

        String str =  builder.toString();
        writer.write(str, 0, str.length());
        writer.newLine();
    }


















    public static SceneFileData readScene(File file) throws IOException, ParseFileException {
        BufferedReader reader = new BufferedReader(new FileReader(file));

        try {
            String line = readLine(reader);
            Color diffusedLight = parseColor(line, 0);

            line = readLine(reader);
            int[] number = parseParameters(line, 0, 1);
            if(number[0] < 0){
                throw new ParseFileException();
            }

            List<LightSource> sources = parseSources(reader, number[0]);
            List<Surface> surfaces = parseSurfaces(reader);

            return new SceneFileData(diffusedLight, sources, surfaces);
        } catch(NumberFormatException e){
            throw new ParseFileException();
        }
    }


    private static List<LightSource> parseSources(BufferedReader reader, int number) throws IOException, ParseFileException {
        List<LightSource> sources = new ArrayList<>(number);

        for(int i = 0; i < number; ++i){
            String line = readLine(reader);
            double[] pos = parseDoubleParameters(line, 0, 3);
            Color color = parseColor(line, 3);
            LightSource source = new LightSource(new Vector(pos), color);
            sources.add(source);
        }

        return sources;
    }

    private static List<Surface> parseSurfaces(BufferedReader reader) throws IOException, ParseFileException {
        List<Surface> surfaces = new ArrayList<>();

        Surface s = parseSurface(reader);
        while(s != null){
            surfaces.add(s);
            s = parseSurface(reader);
        }

        return surfaces;
    }


    private static Surface parseSurface(BufferedReader reader) throws IOException, ParseFileException {
        String line = readLine(reader);
        if(line == null){
            return null;
        }
        line = line.toUpperCase();
        Surface surface;

        switch (line){
            case "SPHERE":
                surface = parseSphere(reader);
                break;
            case "BOX":
                surface = parseBox(reader);
                break;
            case "TRIANGLE":
                surface = parseTriangle(reader);
                break;
            case "QUADRANGLE":
                surface = parseQuadrangle(reader);
                break;
            default:
                throw new ParseFileException();
        }

        return surface;
    }

    private static Surface parseSphere(BufferedReader reader) throws IOException, ParseFileException {
        String line = readLine(reader);
        double[] center = parseDoubleParameters(line, 0, 3);

        line = readLine(reader);
        double[] radius = parseDoubleParameters(line, 0, 1);
        if(radius[0] <= 0){
            throw new ParseFileException();
        }

        line = readLine(reader);
        OpticalCharacteristics oCh = parseOpticalCharacteristics(line);
        return new Sphere(new Vector(center), radius[0], oCh);
    }

    private static Surface parseBox(BufferedReader reader) throws IOException, ParseFileException {
        String line = readLine(reader);
        double[] min = parseDoubleParameters(line, 0, 3);

        line = readLine(reader);
        double[] max = parseDoubleParameters(line, 0, 3);

        line = readLine(reader);
        OpticalCharacteristics oCh = parseOpticalCharacteristics(line);
        return new Box(new Vector(min), new Vector(max), oCh);
    }

    private static Surface parseTriangle(BufferedReader reader) throws IOException, ParseFileException {
        Vector[] vertices = new Vector[3];
        double[] vert;

        String line;
        for(int i = 0; i < vertices.length; ++i){
            line = readLine(reader);
            vert = parseDoubleParameters(line, 0, 3);
            vertices[i] = new Vector(vert);
        }

        line = readLine(reader);
        OpticalCharacteristics oCh = parseOpticalCharacteristics(line);
        return new Triangle(vertices, oCh);
    }

    private static Surface parseQuadrangle(BufferedReader reader) throws IOException, ParseFileException {
        Vector[] vertices = new Vector[4];
        double[] vert;

        String line;
        for(int i = 0; i < vertices.length; ++i){
            line = readLine(reader);
            vert = parseDoubleParameters(line, 0, 3);
            vertices[i] = new Vector(vert);
        }

        line = readLine(reader);
        OpticalCharacteristics oCh = parseOpticalCharacteristics(line);
        return new Quadrangle(vertices, oCh);
    }

    private static OpticalCharacteristics parseOpticalCharacteristics(String line) throws ParseFileException {
        double[] kd = parseDoubleParameters(line, 0, 3);
        double[] ks = parseDoubleParameters(line, 3, 3);
        double[] pow = parseDoubleParameters(line, 6, 1);

        for(int i = 0; i < 3; ++i){
            if(kd[i] < 0 || kd[i] > 1 || ks[i] < 0 || ks[i] > 1){
                throw new ParseFileException();
            }
        }

        return new OpticalCharacteristics(kd, ks, pow[0]);
    }


    public static RenderFileData readRenderFile(File file) throws IOException, ParseFileException {
        BufferedReader reader = new BufferedReader(new FileReader(file));

        try {
            String line = readLine(reader);
            Color background = parseColor(line, 0);

            line = readLine(reader);
            double gamma = parseDouble(line);

            line = readLine(reader);
            int[] depth = parseParameters(line, 0, 1);
            if(depth[0] < 0){
                throw new ParseFileException();
            }

            line = readLine(reader);
            RenderQuality quality = parseQuality(line);

            line = readLine(reader);
            Vector camera = new Vector(parseDoubleParameters(line, 0, 3));

            line = readLine(reader);
            Vector view = new Vector(parseDoubleParameters(line, 0, 3));

            line = readLine(reader);
            Vector up = new Vector(parseDoubleParameters(line, 0, 3));

            line = readLine(reader);
            double[] z = parseDoubleParameters(line, 0, 2);

            for(int i = 0; i < z.length; ++i){
                if(z[i] <= 0){
                    throw new ParseFileException();
                }
            }

            line = readLine(reader);
            double[] s = parseDoubleParameters(line, 0, 2);
            for(int i = 0; i < s.length; ++i){
                if(s[i] <= 0){
                    throw new ParseFileException();
                }
            }

            return new RenderFileData(background, gamma, depth[0], quality, camera, view, up, z[0], z[1], s[0], s[1]);
        } catch(NumberFormatException e){
            throw new ParseFileException();
        }
    }

    private static double parseDouble(String line) throws ParseFileException {
        double[] number = parseDoubleParameters(line, 0, 1);
        if(number[0] < 0){
            throw new ParseFileException();
        }
        return number[0];
    }

    private static RenderQuality parseQuality(String line) throws ParseFileException {
        line = line.toUpperCase();
        RenderQuality quality;

        switch (line){
            case "ROUGH":
                quality = RenderQuality.ROUGH;
                break;
            case "NORMAL":
                quality = RenderQuality.NORMAL;
                break;
            case "FINE":
                quality = RenderQuality.FINE;
                break;
            default:
                throw new ParseFileException();
        }

        return quality;
    }













    private static String composeString(int[] params, int num){
        StringBuilder builder = new StringBuilder();
        builder.append(params[0]);
        for(int i = 1; i < num; ++i){
            builder.append(' ');
            builder.append(params[i]);
        }
        return builder.toString();
    }

    private static String composeString(double[] params, int num){
        StringBuilder builder = new StringBuilder();
        builder.append(params[0]);
        for(int i = 1; i < num; ++i){
            builder.append(' ');
            builder.append(params[i]);
        }
        return builder.toString();
    }


    private static String composeString(Coordinate c){
        StringBuilder builder = new StringBuilder();
        builder.append(c.x);
        builder.append(' ');
        builder.append(c.y);
        return builder.toString();
    }


    /*private static void writeSurfaces(BufferedWriter writer, SurfaceFileData[] surfaces) throws IOException {
        for(int i = 0; i < surfaces.length; ++i){
            SurfaceFileData surf = surfaces[i];
            writeColor(writer, surf.color);

            double[] center = surf.center.getVector();
            String str = composeString(center, center.length);
            writer.write(str, 0, str.length());
            writer.newLine();

            writeMatrix(writer, surf.rotationMatrix);

            String numberPivots = Integer.toString(surf.pivots.length);
            writer.write(numberPivots, 0, numberPivots.length());
            writer.newLine();

            for(int j = 0; j < surf.pivots.length; ++j){
                str = composeString(surf.pivots[j]);
                writer.write(str, 0, str.length());
                writer.newLine();
            }
        }

        String info = "//Additional info (the format extension)";
        writer.write(info, 0, info.length());
        writer.newLine();

        for(int i = 0; i < surfaces.length; ++i){
            SurfaceFileData surf = surfaces[i];
            String param = composeString(surf.grid);
            writer.write(param, 0, param.length());
            writer.newLine();
        }
    }*/

    private static void writeColor(BufferedWriter writer, Color c) throws IOException {
        int[] col = new int[] {c.getRed(), c.getGreen(), c.getBlue()};
        String color = composeString(col, col.length);
        writer.write(color, 0, color.length());
        writer.newLine();
    }



    private static String readLine(BufferedReader reader) throws IOException {
        String str = reader.readLine();
        if(str == null){
            return null;
        }

        int pos = str.indexOf("//");
        if (pos >= 0) {
            str = str.substring(0, pos);
        }

        while(str.isEmpty()){
            str = reader.readLine();
            if(str == null){
                return null;
            }
            pos = str.indexOf("//");
            if (pos >= 0) {
                str = str.substring(0, pos);
            }
        }
        return str;
    }

    private static Color parseColor(String string, int start) throws ParseFileException {
        int[] colorComponents = parseParameters(string, start, 3);
        for(int i = 0; i < colorComponents.length; ++i){
            if(colorComponents[i] < 0 || colorComponents[i] > 255){
                throw new ParseFileException();
            }
        }
        return new Color(colorComponents[0], colorComponents[1], colorComponents[2]);
    }

    private static int[] parseParameters(String string, int start, int number) throws ParseFileException, NumberFormatException {
        if(string != null) {
            int pos = string.indexOf("//");
            if (pos >= 0) {
                string = string.substring(0, pos);
            }
            String[] parameters = string.split("\\s");
            int[] coord = new int[number];
            int j = 0;
            for (int i = start; i < parameters.length && i < start + coord.length; ++i) {
                if (!parameters[i].isEmpty()) {
                    coord[j++] = Integer.parseInt(parameters[i]);
                }
            }
            if (j == coord.length) {
                return coord;
            }
        }
        throw new ParseFileException();
    }

    private static double[] parseDoubleParameters(String string, int start, int number) throws ParseFileException, NumberFormatException {
        if(string != null) {
            int pos = string.indexOf("//");
            if (pos >= 0) {
                string = string.substring(0, pos);
            }
            String[] parameters = string.split("\\s");
            double[] coord = new double[number];
            int j = 0;
            for (int i = start; i < parameters.length && i < start + coord.length; ++i) {
                if (!parameters[i].isEmpty()) {
                    coord[j++] = Double.parseDouble(parameters[i]);
                }
            }
            if (j == coord.length) {
                return coord;
            }
        }
        throw new ParseFileException();
    }
}

