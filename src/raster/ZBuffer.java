package raster;

import transforms.Col;

public class ZBuffer {
    private final Raster<Col> imageBuffer;
    private final DepthBuffer depthBuffer;

    public ZBuffer(Raster<Col> imageBuffer) {
        this.imageBuffer = imageBuffer;
        this.depthBuffer = new DepthBuffer(imageBuffer.getWidth(), imageBuffer.getHeight());
    }

    public void setPixelWithZTest(int x, int y, double z, Col color) {
        if (x < 0 || x >= getWidth() || y < 0 || y >= getHeight())
            return;

        double current = depthBuffer.getValue(x, y).orElse(DepthBuffer.INITIAL_DEPTH);
        if (z >= 0 && z <= 1 && z < current) {
            imageBuffer.setValue(x, y, color);
            depthBuffer.setValue(x, y, z);
        }
    }

    public void clear() {
        imageBuffer.clear();
        depthBuffer.clear();
    }

    public int getWidth() {
        return imageBuffer.getWidth();
    }

    public int getHeight() {
        return imageBuffer.getHeight();
    }
}
