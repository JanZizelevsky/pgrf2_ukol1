package rasterize;

import objectdata.Vertex;
import raster.RasterBufferedImage;
import raster.ZBuffer;
import transforms.Col;
import util.Lerp;

public class LineRasterizerZBuffer extends LineRasterizer {
    private final ZBuffer zBuffer;
    private final Lerp<Vertex> lerp = new Lerp<>();

    public LineRasterizerZBuffer(RasterBufferedImage raster, ZBuffer zBuffer) {
        super(raster);
        this.zBuffer = zBuffer;
    }

    @Override
    public void rasterize(Vertex v1, Vertex v2) {
        int x1 = (int) Math.round(v1.getX());
        int y1 = (int) Math.round(v1.getY());
        int x2 = (int) Math.round(v2.getX());
        int y2 = (int) Math.round(v2.getY());

        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);
        int steps = Math.max(dx, dy);
        if (steps == 0) {
            zBuffer.setPixelWithZTest(x1, y1, v1.getZ(), color);
            return;
        }

        for (int i = 0; i <= steps; i++) {
            double t = (double) i / steps;
            Vertex p = lerp.lerp(v1, v2, t);
            int x = (int) Math.round(p.getX());
            int y = (int) Math.round(p.getY());
            zBuffer.setPixelWithZTest(x, y, p.getZ(), color);
        }
    }
}
