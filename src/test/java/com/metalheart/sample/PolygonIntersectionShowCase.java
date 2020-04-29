package com.metalheart.sample;

import com.metalheart.model.physic.Line;
import com.metalheart.model.physic.Point2d;
import com.metalheart.model.physic.Polygon2d;
import com.metalheart.service.PhysicUtil;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.Arrays.asList;

public class PolygonIntersectionShowCase extends JPanel {

    public static final int WIDTH = 1024;
    public static final int HEIGHT = 500;

    private List<Polygon2d> polygons = new ArrayList<>();
    private int unit = 50;


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (!polygons.isEmpty()) {

            boolean intersect = polygons.size() == 2 ? PhysicUtil.isIntersect(polygons.get(0), polygons.get(1)) : false;

            for (int i = 0; i < 2; i++) {
                g.setColor(i == 0 ? Color.BLUE : Color.GREEN);

                Polygon p = new Polygon();
                for (Point2d point2d : polygons.get(i).getPoints()) {
                    p.addPoint(toXCoord(point2d.getX()), toYCoord(point2d.getY()));
                }

                if (intersect) {

                    System.out.println(i + ". " + polygons.get(i));

                    g.drawPolygon(p);
                } else {
                    g.fillPolygon(p);
                }

                Line projection = PhysicUtil.getProjection(polygons.get(i), true);
                g.fillRect(
                        toXCoord(projection.getStart()),
                        toYCoord(0) - 2,
                        (int) (unit * (projection.getEnd() - projection.getStart())),
                        4);
            }
            g.setColor(Color.BLACK);
        }

        g.drawLine(WIDTH / 2, 0, WIDTH / 2, HEIGHT);
        g.drawLine(0, HEIGHT / 2, WIDTH, HEIGHT / 2);


    }

    private int toYCoord(float y) {
        return HEIGHT - (int) (y * unit) - HEIGHT / 2;
    }

    private int toXCoord(float x) {
        return WIDTH / 2 + (int) (x * unit);
    }

    @Override
    public Dimension getPreferredSize() {
        // so that our GUI is big enough
        return new Dimension(WIDTH, HEIGHT);
    }

    // create the GUI explicitly on the Swing event thread
    private static void createAndShowGui() {
        PolygonIntersectionShowCase mainPanel = new PolygonIntersectionShowCase();
        JButton btn = new JButton("rotate");
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

        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                for (int i = 0; i < 1000; i++) {
                    TimeUnit.MILLISECONDS.sleep(40);
                    mainPanel.polygons = asList(
                            PhysicUtil.rotate(polygon1, (float) Math.toRadians(angleDeg.get() / 2f), new Point2d(2.5f, 2.5f)),
                            PhysicUtil.rotate(polygon2, (float) Math.toRadians(angleDeg.get() / 2f))
                    );
                    angleDeg.incrementAndGet();
                    mainPanel.repaint();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });


        btn.addActionListener((e) -> {
            Executors.newSingleThreadExecutor().execute(() -> {

                final List<Polygon2d> polygon2ds = asList(polygon1, polygon2);
                for (int j = 0; j < 2; j++) {

                    List<Point2d> points = polygon2ds.get(j).getPoints();
                    for (int i = 0; i < points.size(); i++) {

                        Point2d p1 = points.get(i);
                        Point2d p2 = i + 1 == points.size() ? points.get(0) : points.get(i + 1);

                        float angle = -PhysicUtil.getAngle(p1, p2);
                        mainPanel.polygons = asList(
                                PhysicUtil.rotate(polygon1, angle),
                                PhysicUtil.rotate(polygon2, angle)
                        );
                        mainPanel.repaint();
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
        mainPanel.add(btn);


        JFrame frame = new JFrame("DrawRect");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(mainPanel);
        frame.pack();
        frame.setLocationByPlatform(true);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGui();
            }
        });
    }
}