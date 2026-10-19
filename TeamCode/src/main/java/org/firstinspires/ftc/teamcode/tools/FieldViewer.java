package org.firstinspires.ftc.teamcode.tools;

import com.bylazar.telemetry.PanelsTelemetry;
import com.bylazar.telemetry.TelemetryManager;
import com.pedropathing.localization.Localizer;
import com.pedropathing.math.Pose;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.pedro.Constants;
import org.firstinspires.ftc.teamcode.robot.StarterBot;
import org.firstinspires.ftc.teamcode.util.FieldDrawing;

/**
 * Drive the robot and watch where the Pinpoint thinks it is, live on the Panels field.
 *
 * 1. Run the Pinpoint Tuner in AutoTune and paste its config into pedro/Constants.java.
 * 2. Connect your laptop to the robot's Wi-Fi and open http://192.168.43.1:8001
 * 3. Run this OpMode and drive. The gold circle is the robot; the blue line is where it's been.
 *
 * If the circle moves the wrong way or drifts, the Pinpoint needs (re)tuning.
 * This only needs the Pinpoint tuned, not Foresight.
 */
@TeleOp(name = "Field Viewer", group = "Tools")
public class FieldViewer extends OpMode {
    /** Where the robot starts, in Pedro coordinates (inches from the bottom-left corner, heading in radians). */
    public static Pose START_POSE = new Pose(72, 72, Math.toRadians(90));

    private StarterBot robot;
    private Localizer localizer;
    private TelemetryManager panelsTelemetry;

    @Override
    public void init() {
        robot = new StarterBot(hardwareMap);
        localizer = Constants.createLocalizer(hardwareMap);
        localizer.setPose(START_POSE);
        panelsTelemetry = PanelsTelemetry.INSTANCE.getTelemetry();
        FieldDrawing.init();

        telemetry.addLine("Ready. Open Panels at http://192.168.43.1:8001 and press Play.");
        telemetry.update();
    }

    @Override
    public void loop() {
        robot.drive(-gamepad1.left_stick_y, gamepad1.left_stick_x, gamepad1.right_stick_x);
        localizer.update();

        // Back button puts the robot back at the start pose and clears the trail
        if (gamepad1.backWasPressed()) {
            localizer.setPose(START_POSE);
            FieldDrawing.init();
        }

        Pose pose = localizer.pose();
        panelsTelemetry.addData("X (in)", String.format("%.1f", pose.x()));
        panelsTelemetry.addData("Y (in)", String.format("%.1f", pose.y()));
        panelsTelemetry.addData("Heading (deg)", String.format("%.1f", Math.toDegrees(pose.heading())));
        panelsTelemetry.addLine("Back button: reset to start");
        panelsTelemetry.update(telemetry);

        FieldDrawing.draw(pose);
    }

    @Override
    public void stop() {
        robot.stopDrive();
    }
}
