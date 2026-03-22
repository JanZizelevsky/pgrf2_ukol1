package rasterize;

import model.Line;
import objectdata.Vertex;
import raster.RasterBufferedImage;
import transforms.Col;

public abstract class LineRasterizer {
    protected RasterBufferedImage raster;
    protected Col color;

    public LineRasterizer(RasterBufferedImage raster) {
        this.raster = raster;
        this.color = new Col(0xffffff);
    }

    public void rasterize(int x1, int y1, int x2, int y2) {

    }

    public void rasterize(Vertex v1, Vertex v2) {
        rasterize(
                (int) Math.round(v1.getX()),
                (int) Math.round(v1.getY()),
                (int) Math.round(v2.getX()),
                (int) Math.round(v2.getY()));
    }

    public void rasterize(Line line) {
        rasterize(line.getX1(), line.getY1(), line.getX2(), line.getY2());
    }

    public void setColor(Col color) {
        this.color = color;
    }
}
