package solid;

import objectdata.Part;
import objectdata.TopologyType;
import objectdata.Vertex;
import transforms.Col;
import transforms.Vec2D;

public class Prism extends Solid {
    public Prism() {
        this(6, 0.4, 1);
    }

    public Prism(int n, double radius, double height) {
        if (n < 3)
            n = 3;
        double h = height / 2;
        for (int i = 0; i < n; i++) {
            double t = 2 * Math.PI * i / n;
            double x = radius * Math.cos(t);
            double z = radius * Math.sin(t);
            double u = (double) i / n;
            vertexBuffer.add(new Vertex(x, h, z, new Col(0x66aa00), new Vec2D(u, 0)));
            vertexBuffer.add(new Vertex(x, -h, z, new Col(0x66aa00), new Vec2D(u, 1)));
        }
        vertexBuffer.add(new Vertex(0, h, 0, new Col(0x888800), new Vec2D(0.5, 0.5)));
        vertexBuffer.add(new Vertex(0, -h, 0, new Col(0x888800), new Vec2D(0.5, 0.5)));
        int top = n * 2;
        int bottom = n * 2 + 1;
        for (int i = 0; i < n; i++) {
            int a = i * 2;
            int b = ((i + 1) % n) * 2;
            addIndices(top, a, b);
            addIndices(bottom, b + 1, a + 1);
            addIndices(a, a + 1, b + 1, a, b + 1, b);
        }
        partBuffer.add(new Part(TopologyType.TRIANGLES, 0, n * 4));
    }
}
