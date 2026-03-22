package util;

import objectdata.Vertex;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class Clip {

    private static final Lerp<Vertex> LERP = new Lerp<>();

    public static boolean fastClipLine(Vertex a, Vertex b) {
        return !(outsideAll(a) && outsideAll(b));
    }

    public static boolean fastClipTriangle(Vertex a, Vertex b, Vertex c) {
        return !(outsideAll(a) && outsideAll(b) && outsideAll(c));
    }

    private static boolean outsideAll(Vertex v) {
        double x = v.getX(), y = v.getY(), z = v.getZ();
        return x < -1 || x > 1 || y < -1 || y > 1 || z < 0 || z > 1;
    }

    public static Optional<Vertex[]> clipLineZ(Vertex a, Vertex b) {
        Vertex v1 = a, v2 = b;
        double z1 = v1.getZ(), z2 = v2.getZ();

        if (z1 < 0 && z2 < 0)
            return Optional.empty();
        if (z1 > 1 && z2 > 1)
            return Optional.empty();

        if (z1 < 0) {
            double t = -z1 / (z2 - z1);
            v1 = LERP.lerp(a, b, t);
        } else if (z1 > 1) {
            double t = (1 - z1) / (z2 - z1);
            v1 = LERP.lerp(a, b, t);
        }
        z1 = v1.getZ();
        z2 = v2.getZ();
        if (z2 < 0) {
            double t = -z1 / (z2 - z1);
            v2 = LERP.lerp(v1, b, t);
        } else if (z2 > 1) {
            double t = (1 - z1) / (z2 - z1);
            v2 = LERP.lerp(v1, b, t);
        }
        return Optional.of(new Vertex[] { v1, v2 });
    }

    public static List<Vertex[]> clipTriangleZ(Vertex a, Vertex b, Vertex c) {
        double za = a.getZ(), zb = b.getZ(), zc = c.getZ();
        if (za >= 0 && zb >= 0 && zc >= 0)
            return Collections.singletonList(new Vertex[] { a, b, c });
        if (za < 0 && zb < 0 && zc < 0)
            return Collections.emptyList();

        return clipPolygonZ(List.of(a, b, c));
    }

    private static List<Vertex[]> clipPolygonZ(List<Vertex> poly) {
        List<Vertex> out = new ArrayList<>(poly);
        out = clipAgainstPlaneZ(out, 0, true);
        if (out.isEmpty())
            return Collections.emptyList();
        out = clipAgainstPlaneZ(out, 1, false);
        if (out.size() < 3)
            return Collections.emptyList();
        return triangulate(out);
    }

    private static List<Vertex> clipAgainstPlaneZ(List<Vertex> poly, double zPlane, boolean keepInside) {
        List<Vertex> out = new ArrayList<>();
        for (int i = 0; i < poly.size(); i++) {
            Vertex curr = poly.get(i);
            Vertex next = poly.get((i + 1) % poly.size());
            boolean currIn = keepInside ? curr.getZ() >= zPlane : curr.getZ() <= zPlane;
            boolean nextIn = keepInside ? next.getZ() >= zPlane : next.getZ() <= zPlane;

            if (currIn && nextIn) {
                out.add(next);
            } else if (currIn && !nextIn) {
                double t = (zPlane - curr.getZ()) / (next.getZ() - curr.getZ());
                out.add(LERP.lerp(curr, next, t));
            } else if (!currIn && nextIn) {
                double t = (zPlane - curr.getZ()) / (next.getZ() - curr.getZ());
                out.add(LERP.lerp(curr, next, t));
                out.add(next);
            }
        }
        return out;
    }

    private static List<Vertex[]> triangulate(List<Vertex> poly) {
        List<Vertex[]> result = new ArrayList<>();
        for (int i = 1; i < poly.size() - 1; i++) {
            result.add(new Vertex[] { poly.get(0), poly.get(i), poly.get(i + 1) });
        }
        return result;
    }
}
