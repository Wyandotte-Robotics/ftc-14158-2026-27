package org.firstinspires.ftc.teamcode.util;

import com.bylazar.field.FieldManager;
import com.bylazar.field.PanelsField;
import com.bylazar.field.Style;
import com.pedropathing.follower.Follower;
import com.pedropathing.math.Pose;
import com.pedropathing.paths.Path;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Draws the robot, the trail behind it, and the path it's following on the Panels field.
 *
 * Open Panels at http://192.168.43.1:8001 while your laptop is connected to the robot's Wi-Fi.
 *
 * Use it from any OpMode that has a Pedro Pathing Follower:
 * <pre>
 *     init():  FieldDrawing.init();
 *     loop():  follower.update();
 *              FieldDrawing.draw(follower);
 * </pre>
 */
public final class FieldDrawing {
    /** Half the robot's width in inches. An 18 inch robot is about 9. */
    public static double ROBOT_RADIUS = 9;

    private static final int TRAIL_LENGTH = 300;
    private static final int PATH_SAMPLES = 40;

    // BearBots brand colors
    private static final Style ROBOT_STYLE = new Style("", "#F7B010", 1.5);  // Bear Gold
    private static final Style TRAIL_STYLE = new Style("", "#4935E8", 0.75); // Bear Blue, lighter
    private static final Style PATH_STYLE = new Style("", "#1F019F", 1.0);   // Bear Blue

    private static final FieldManager field = PanelsField.INSTANCE.getField();
    private static final Deque<Pose> trail = new ArrayDeque<>();

    private FieldDrawing() {}

    /** Call once in init(). Lines Panels up with Pedro Pathing's field coordinates and clears the trail. */
    public static void init() {
        field.setOffsets(PanelsField.INSTANCE.getPresets().getPEDRO_PATHING());
        trail.clear();
    }

    /** Draws the current path, the trail and the robot, then sends them to Panels. Call once per loop. */
    public static void draw(Follower follower) {
        Path path = follower.currentPath();
        if (path != null) {
            drawPath(path);
        }

        Pose pose = follower.pose();
        if (isValid(pose)) {
            trail.addLast(pose);
            while (trail.size() > TRAIL_LENGTH) {
                trail.removeFirst();
            }
        }

        drawTrail();
        drawRobot(pose);
        field.update();
    }

    /** Draws the robot as a circle with a line showing which way it's facing. */
    public static void drawRobot(Pose pose) {
        if (!isValid(pose)) {
            return;
        }

        field.setStyle(ROBOT_STYLE);
        field.moveCursor(pose.x(), pose.y());
        field.circle(ROBOT_RADIUS);

        field.moveCursor(pose.x(), pose.y());
        field.line(pose.x() + Math.cos(pose.heading()) * ROBOT_RADIUS,
                pose.y() + Math.sin(pose.heading()) * ROBOT_RADIUS);
    }

    /** Draws a Pedro path as a series of short lines. */
    public static void drawPath(Path path) {
        field.setStyle(PATH_STYLE);
        Pose previous = path.get(0);
        for (int i = 1; i <= PATH_SAMPLES; i++) {
            Pose next = path.get((double) i / PATH_SAMPLES);
            if (isValid(previous) && isValid(next)) {
                field.moveCursor(previous.x(), previous.y());
                field.line(next.x(), next.y());
            }
            previous = next;
        }
    }

    private static void drawTrail() {
        field.setStyle(TRAIL_STYLE);
        Pose previous = null;
        for (Pose pose : trail) {
            if (previous != null) {
                field.moveCursor(previous.x(), previous.y());
                field.line(pose.x(), pose.y());
            }
            previous = pose;
        }
    }

    private static boolean isValid(Pose pose) {
        return pose != null
                && !Double.isNaN(pose.x())
                && !Double.isNaN(pose.y())
                && !Double.isNaN(pose.heading());
    }
}
