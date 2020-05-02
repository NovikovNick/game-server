package com.metalheart.showcase;

import com.metalheart.model.physic.Line;
import com.metalheart.model.physic.Point2d;
import com.metalheart.model.physic.Polygon2d;
import com.metalheart.service.PhysicUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.Math.round;
import static java.util.Arrays.asList;

public class PolygonIntersectionShowcase extends JPanel {

    public static final int WIDTH = 1920;
    public static final int HEIGHT = 1080;

    private List<Polygon2d> polygons = new ArrayList<>();
    private int unit = 50;

    public PolygonIntersectionShowcase() {
        setBackground(Color.WHITE);

        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new KeyEventDispatcher() {

            @Override
            public boolean dispatchKeyEvent(KeyEvent ke) {

                System.out.println(ke.getKeyCode());

                switch (ke.getID()) {
                    case KeyEvent.KEY_PRESSED:
                        if (ke.getKeyCode() == KeyEvent.VK_W) {
                            // = true;
                        }
                        break;

                    case KeyEvent.KEY_RELEASED:
                        if (ke.getKeyCode() == KeyEvent.VK_W) {
                            // wPressed = false;
                        }
                        break;
                }
                return false;
            }
        });

        JButton btn = new JButton("rotate");
        //  add(btn);
        btn.addActionListener((e) -> {
            Executors.newSingleThreadExecutor().execute(() -> {

                for (int j = 0; j < 2; j++) {

                    List<Point2d> points = polygons.get(j).getPoints();
                    for (int i = 0; i < points.size(); i++) {

                        Point2d p1 = points.get(i);
                        Point2d p2 = i + 1 == points.size() ? points.get(0) : points.get(i + 1);

                        float angle = -PhysicUtil.getAngle(p1, p2);
                        polygons = asList(
                                PhysicUtil.rotate(polygons.get(0), angle),
                                PhysicUtil.rotate(polygons.get(1), angle)
                        );
                        repaint();
                        System.out.println(p1 + " : " + p2 + " = " + Math.toDegrees(angle) + "deg " + angle + "rad");
                        try {
                            TimeUnit.SECONDS.sleep(1);
                        } catch (InterruptedException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
            });
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (!polygons.isEmpty()) {

            boolean intersect = polygons.size() == 2 ? PhysicUtil.isIntersect(polygons.get(0), polygons.get(1)) : false;

            for (Polygon2d polygon : polygons) {

                g.setColor(Color.BLUE);

                Polygon p = new Polygon();
                for (Point2d point2d : polygon.getPoints()) {
                    g.fillOval(
                            toXCoord(point2d.getX()) - 4,
                            toYCoord(point2d.getY()) - 4,
                            8, 8
                    );

                    p.addPoint(toXCoord(point2d.getX()), toYCoord(point2d.getY()));
                }

                g.drawPolygon(p);

                if (intersect) {
                    g.fillPolygon(p);
                }

                Line projection = PhysicUtil.getProjection(polygon, true);
                g.fillRect(
                        toXCoord(projection.getStart()),
                        toYCoord(0) - 2,
                        (int) (unit * (projection.getEnd() - projection.getStart())),
                        4);
            }
            g.setColor(Color.BLACK);
        }

        System.out.println(MouseInfo.getPointerInfo().getLocation());

        // y
        // g.drawLine(WIDTH / 2, 0, WIDTH / 2, HEIGHT);

        // x
        g.drawLine(0, HEIGHT / 2, WIDTH, HEIGHT / 2);
    }

    private int toYCoord(float y) {
        return HEIGHT / 2 - round(y * unit);
    }

    private int toXCoord(float x) {
        return WIDTH / 2 + round(x * unit);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(WIDTH, HEIGHT);
    }

    // create the GUI explicitly on the Swing event thread
    private static PolygonIntersectionShowcase createAndShowGui() {
        PolygonIntersectionShowcase mainPanel = new PolygonIntersectionShowcase();

        AtomicInteger angleDeg = new AtomicInteger(0);
        Polygon2d polygon1 = new Polygon2d(
                new Point2d(1.0494196f, 0.16851634f),
                new Point2d(-0.21843511f, 2.8874397f),
                new Point2d(3.2255344f, 4.493389f),
                new Point2d(4.493389f, 1.7744658f));

        Polygon2d polygon2 = new Polygon2d(
                new Point2d(-1.3289261f, 0.4836895f),
                new Point2d(-2.5967808f, 3.2026129f),
                new Point2d(-5.3157043f, 1.934758f),
                new Point2d(-4.047849f, -0.7841653f));

        mainPanel.polygons = asList(polygon1, polygon2);

        Executors.newScheduledThreadPool(1).schedule(() -> {

            mainPanel.polygons = asList(
                    PhysicUtil.rotate(polygon1, (float) Math.toRadians(angleDeg.get() / 2f), new Point2d(2.5f, 2.5f)),
                    PhysicUtil.rotate(polygon2, (float) Math.toRadians(angleDeg.get()))
            );
            angleDeg.incrementAndGet();
            mainPanel.repaint();

        }, 20, TimeUnit.MILLISECONDS);




        return mainPanel;
    }

    public static void main(String[] args) {


        SwingUtilities.invokeLater(() -> {
            JFrame f = new JFrame();
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            f.setTitle("Polygon Intersection");
            f.setResizable(false);
            f.setFocusable(true);
            f.add(createAndShowGui(), BorderLayout.NORTH);
            //f.setUndecorated(true);
            // f.setLocationRelativeTo(null);
            f.pack();
            f.setVisible(true);
        });
    }
}