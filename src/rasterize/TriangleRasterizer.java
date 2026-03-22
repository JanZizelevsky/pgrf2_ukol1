package rasterize;

import objectdata.Vertex;
import raster.RasterBufferedImage;
import raster.ZBuffer;
import shader.Shader;
import transforms.Col;
import util.Lerp;

public class TriangleRasterizer {

    private ZBuffer img;

    public TriangleRasterizer(ZBuffer img) {
        this.img = img;
    }

    public void rasterize(Vertex a, Vertex b, Vertex c, Shader shader) {

        if (a.getY() > b.getY()) {
            Vertex t = a;
            a = b;
            b = t;
        }
        if (b.getY() > c.getY()) {
            Vertex t = b;
            b = c;
            c = t;
        }
        if (a.getY() > b.getY()) {
            Vertex t = a;
            a = b;
            b = t;
        }

        int aY = (int) Math.round(a.getY());
        int bY = (int) Math.round(b.getY());
        int cY = (int) Math.round(c.getY());

        if (cY == aY)
            return;

        Lerp<Vertex> lerp = new Lerp<>();

        if (bY - aY > 0) {
            for (int y = aY; y < bY; y++) {
                double tAB = (double) (y - aY) / (bY - aY);
                double tAC = (double) (y - aY) / (cY - aY);
                Vertex ab = lerp.lerp(a, b, tAB);
                Vertex ac = lerp.lerp(a, c, tAC);
                drawSpan(ab, ac, y, lerp, shader);
            }
        }

        if (cY - bY > 0) {
            for (int y = bY; y <= cY; y++) {
                double tBC = (double) (y - bY) / (cY - bY);
                double tAC = (double) (y - aY) / (cY - aY);
                Vertex bc = lerp.lerp(b, c, tBC);
                Vertex ac = lerp.lerp(a, c, tAC);
                drawSpan(bc, ac, y, lerp, shader);
            }
        }
    }

    private void drawSpan(Vertex v1, Vertex v2, int y, Lerp<Vertex> lerp, Shader shader) {
        if (v1.getX() > v2.getX()) {
            Vertex t = v1;
            v1 = v2;
            v2 = t;
        }
        int x1 = (int) Math.round(v1.getX());
        int x2 = (int) Math.round(v2.getX());
        double dx = v2.getX() - v1.getX();

        for (int x = x1; x <= x2; x++) {
            double t = dx != 0 ? (x - v1.getX()) / dx : 0;
            Vertex pixel = lerp.lerp(v1, v2, t);
            img.setPixelWithZTest(x, y, pixel.getZ(), shader.getColor(pixel));
        }
    }

}
