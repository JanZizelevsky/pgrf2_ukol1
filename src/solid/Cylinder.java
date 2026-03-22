package solid;

import objectdata.Part;
import objectdata.TopologyType;
import objectdata.Vertex;
import transforms.Col;
import transforms.Vec2D;

public class Cylinder extends Solid {
    public Cylinder() {
        this(0.5, 1, 16);
    }

    public Cylinder(double radius, double height, int segments) {
        double h = height / 2;
        vertexBuffer.add(new Vertex(0, h, 0, new Col(0xff6600), new Vec2D(0.5, 0.5)));
        vertexBuffer.add(new Vertex(0, -h, 0, new Col(0xff6600), new Vec2D(0.5, 0.5)));
        for (int i = 0; i <= segments; i++) {
            double t = 2 * Math.PI * i / segments;
            double x = radius * Math.cos(t);
            double z = radius * Math.sin(t);
            double u = (double) i / segments;
            vertexBuffer.add(new Vertex(x, h, z, new Col(0x00aa88), new Vec2D(u, 0)));
            vertexBuffer.add(new Vertex(x, -h, z, new Col(0x00aa88), new Vec2D(u, 1)));
        }
        for (int i = 0; i < segments; i++) {
            int t0 = 2 + 2 * i, t1 = 2 + 2 * (i + 1);
            int b0 = t0 + 1, b1 = t1 + 1;
            addIndices(0, t0, t1);
            addIndices(1, b1, b0);
            addIndices(t0, b0, b1, t0, b1, t1);
        }
        partBuffer.add(new Part(TopologyType.TRIANGLES, 0, segments * 4));
    }
}
