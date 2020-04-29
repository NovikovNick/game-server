package com.metalheart.sample;

import com.metalheart.model.physic.Line;
import com.metalheart.model.physic.Point2d;
import com.metalheart.model.physic.Polygon2d;
import com.metalheart.service.PhysicUtil;
import sun.java2d.loops.DrawRect;

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


        if (!polygons.isEmpty() && polygons.size() == 2) {

            boolean intersect = PhysicUtil.isIntersect(polygons.get(0), polygons.get(1));

            for (int i = 0; i < 2; i++) {
                g.setColor(i == 0 ? Color.BLUE : Color.GREEN);

                Polygon p = new Polygon();
                for (Point2d point2d : polygons.get(i).getPoints()) {
                    p.addPoint(toXCoord(point2d.getX()), toYCoord(point2d.getY()));
                }

                if (intersect) {
                    g.drawPolygon(p);
                } else {
                    g.fillPolygon(p);
                }

                Line projection = PhysicUtil.getProjection(polygons.get(i), true);
                g.fillRect(
                        toXCoord(projection.getStart()),
                        toYCoord(0)-2,
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
        Polygon2d p1 = new Polygon2d(new Point2d(1, 1), new Point2d(1, 4), new Point2d(4, 4), new Point2d(4, 1));
        Polygon2d p2 = new Polygon2d(new Point2d(-1, 1), new Point2d(-1, 4), new Point2d(-4, 4), new Point2d(-4, 1));

        mainPanel.polygons = asList(p1, p2);

        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                for (int i = 0; i < 1000; i++) {
                    TimeUnit.MILLISECONDS.sleep(50);
                    mainPanel.polygons = asList(
                            PhysicUtil.rotate(p1, (float) Math.toRadians(angleDeg.incrementAndGet()), new Point2d(2.5f, 2.5f)),
                            PhysicUtil.rotate(p2, (float) Math.toRadians(angleDeg.incrementAndGet()))
                            );
                    mainPanel.repaint();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });


        btn.addActionListener((e) -> {

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