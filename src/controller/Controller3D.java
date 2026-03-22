package controller;

import objectdata.Vertex;
import raster.ZBuffer;
import rasterize.LineRasterizer;
import rasterize.LineRasterizerGraphics;
import rasterize.LineRasterizerZBuffer;
import rasterize.TriangleRasterizer;
import renderer.RendererSolid;
import shader.ShaderConstant;
import shader.ShaderInterpolated;
import shader.ShaderLambert;
import solid.*;
import transforms.*;
import view.Panel;

import javax.imageio.ImageIO;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class Controller3D {
    private final Panel panel;
    private final ZBuffer zBuffer;
    private RendererSolid renderer;

    private Camera camera;
    private Mat4 projection;
    private boolean perspective = true;
    private boolean wireframe = false;
    private boolean textured = false;
    private List<shader.Shader> texturedShaders = new ArrayList<>();

    private final List<Solid> solids = new ArrayList<>();
    private final List<Mat4> modelMatrices = new ArrayList<>();
    private int activeIndex = 0;

    private int lastMouseX, lastMouseY;
    private boolean mousePressed = false;

    private static final double MOVE_SPEED = 0.15;
    private static final double ROT_SPEED = 0.05;
    private static final double MOUSE_SENS = 0.005;
    private static final double SCALE_STEP = 1.1;

    public Controller3D(Panel panel) {
        this.panel = panel;
        this.zBuffer = new ZBuffer(panel.getRaster());

        LineRasterizer lineRasterizer = new LineRasterizerZBuffer(panel.getRaster(), zBuffer);
        TriangleRasterizer triangleRasterizer = new TriangleRasterizer(zBuffer);
        renderer = new RendererSolid(lineRasterizer, triangleRasterizer);
        
        texturedShaders.add(new shader.ShaderTextured("res/textures/metal.jpg")); // 0. Arrow
        texturedShaders.add(new shader.ShaderTextured("res/textures/wood.png")); // 1. Cube
        texturedShaders.add(new shader.ShaderTextured("res/textures/sandstone.jpg")); // 2. Tetrahedron
        texturedShaders.add(new shader.ShaderTextured("res/textures/mramor.png")); // 3. Sphere
        texturedShaders.add(new shader.ShaderTextured("res/textures/concrete.jpg")); // 4. Cylinder
        texturedShaders.add(new shader.ShaderTextured("res/textures/ice.jpg")); // 5. Cone
        texturedShaders.add(new shader.ShaderTextured("res/textures/rock.jpg")); // 6. Prism
        texturedShaders.add(new shader.ShaderTextured("res/textures/fabric.png")); // 7. Frustum

        initSolids();
        camera = new Camera(new Vec3D(0, 0, 5), 0, -Math.PI / 2, 5, true);

        updateProjection();
        initListeners();

        drawScene();
    }

    private void initSolids() {
        Solid[] shapes = {
                new Arrow(),
                new Cube(),
                new Tetrahedron(),
                new Sphere(),
                new Cylinder(),
                new Cone(),
                new Prism(),
                new Frustum(),
                new Sphere()
        };
        double offset = 1.5;
        double[] xOffsets = {-1.5, -0.5, 0.5, 1.5, -1.5, -0.5, 0.5, 1.5, 0.0};
        double[] zOffsets = {-1.5, -1.5, -1.5, -1.5, 0.5, 0.5, 0.5, 0.5, 2.0};

        for (int i = 0; i < shapes.length; i++) {
            solids.add(shapes[i]);
            modelMatrices.add(new Mat4Transl(xOffsets[i], 0, zOffsets[i]));
        }
    }

    private void updateProjection() {
        int w = panel.getRaster().getWidth();
        int h = panel.getRaster().getHeight();
        double aspect = (double) w / h;
        if (perspective) {
            // k = height/width podle Mat4PerspRH
            projection = new Mat4PerspRH(Math.PI / 3, (double) h / w, 0.1, 100);
        } else {
            projection = new Mat4OrthoRH(4 * aspect, 4, 0.1, 100);
        }
    }

    private void initListeners() {
        panel.setFocusable(true);
        panel.requestFocusInWindow();

        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                mousePressed = true;
                lastMouseX = e.getX();
                lastMouseY = e.getY();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                mousePressed = false;
            }
        });

        panel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (!mousePressed) return;
                int dx = e.getX() - lastMouseX;
                int dy = e.getY() - lastMouseY;
                lastMouseX = e.getX();
                lastMouseY = e.getY();
                camera = camera.addAzimuth(-dx * MOUSE_SENS).addZenith(dy * MOUSE_SENS);
                drawScene();
            }
        });

        panel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                handleCamera(e);
                handleTransforms(e);
                handleModes(e);
                handleSelection(e);
                drawScene();
            }
        });
    }

    private void handleCamera(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W -> camera = camera.forward(MOVE_SPEED);
            case KeyEvent.VK_S -> camera = camera.backward(MOVE_SPEED);
            case KeyEvent.VK_A -> camera = camera.left(MOVE_SPEED);
            case KeyEvent.VK_D -> camera = camera.right(MOVE_SPEED);
        }
    }

    private void handleTransforms(KeyEvent e) {
        Mat4 m = modelMatrices.get(activeIndex);
        Mat4 delta = null;
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP -> delta = new Mat4Transl(0, MOVE_SPEED, 0);
            case KeyEvent.VK_DOWN -> delta = new Mat4Transl(0, -MOVE_SPEED, 0);
            case KeyEvent.VK_LEFT -> delta = new Mat4Transl(-MOVE_SPEED, 0, 0);
            case KeyEvent.VK_RIGHT -> delta = new Mat4Transl(MOVE_SPEED, 0, 0);
            case KeyEvent.VK_PAGE_UP -> delta = new Mat4Transl(0, 0, MOVE_SPEED);
            case KeyEvent.VK_PAGE_DOWN -> delta = new Mat4Transl(0, 0, -MOVE_SPEED);
            case KeyEvent.VK_R -> delta = new Mat4RotX(ROT_SPEED);
            case KeyEvent.VK_T -> delta = new Mat4RotY(ROT_SPEED);
            case KeyEvent.VK_Y -> delta = new Mat4RotZ(ROT_SPEED);
            case KeyEvent.VK_Z -> delta = new Mat4Scale(SCALE_STEP);
            case KeyEvent.VK_X -> delta = new Mat4Scale(1 / SCALE_STEP);
        }
        if (delta != null) {
            boolean isTranslation = e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_DOWN
                    || e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_RIGHT
                    || e.getKeyCode() == KeyEvent.VK_PAGE_UP || e.getKeyCode() == KeyEvent.VK_PAGE_DOWN;
            m = isTranslation ? delta.mul(m) : m.mul(delta);
            modelMatrices.set(activeIndex, m);
        }
    }

    private void handleModes(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_M) wireframe = !wireframe;
        if (e.getKeyCode() == KeyEvent.VK_T) textured = !textured;
        if (e.getKeyCode() == KeyEvent.VK_P) {
            perspective = !perspective;
            updateProjection();
        }
    }

    private void handleSelection(KeyEvent e) {
        if (e.getKeyCode() >= KeyEvent.VK_1 && e.getKeyCode() <= KeyEvent.VK_9) {
            int idx = e.getKeyCode() - KeyEvent.VK_1;
            if (idx < solids.size()) activeIndex = idx;
        }
    }

    private void drawScene() {
        zBuffer.clear();

        Mat4 view = camera.getViewMatrix();
        int w = panel.getRaster().getWidth();
        int h = panel.getRaster().getHeight();

        Solid axes = new AxisRGB();
        renderer.render(axes, new Mat4Identity(), view, projection, w, h, true);

        Point3D lightPos = new Point3D(0, 0, 0).mul(modelMatrices.get(8));
        Col lightColor = new Col(0xffff00); // Yellow light

        for (int i = 0; i < solids.size(); i++) {
            Mat4 model = modelMatrices.get(i);
            Solid solid = solids.get(i);
            if (i == 8) {
                // Light source itself
                solid.setShader(new ShaderConstant(lightColor));
            } else if (i == activeIndex) {
                solid.setShader(new ShaderLambert(lightPos, textured ? texturedShaders.get(i) : new ShaderInterpolated(), lightColor));
            } else {
                solid.setShader(new ShaderLambert(lightPos, new ShaderInterpolated(), lightColor));
            }
            renderer.render(solid, model, view, projection, w, h, wireframe);
        }

        panel.repaint();
    }
}
