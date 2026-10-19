package org.firstinspires.ftc.teamcode.tools;

import com.bylazar.telemetry.PanelsTelemetry;
import com.bylazar.telemetry.TelemetryManager;
import com.pedropathing.follower.Follower;
import com.pedropathing.math.Pose;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.pedro.Constants;
import org.firstinspires.ftc.teamcode.util.FieldDrawing;

/**
 * Drive the robot and watch where Pedro Pathing thinks it is, live on the Panels field.
 *
 * 1. Finish setting up pedro/Constants.java (see the README).
 * 2. Connect your laptop to the robot's Wi-Fi and open http://192.168.43.1:8001
 * 3. Run this OpMode and drive. The gold circle is the robot; the blue line is where it's been.
 *
 * If the circle doesn't move the way the robot does, your localizer needs tuning.
 */
@TeleOp(name = "Field Viewer", group = "Tools")
public class FieldViewer extends OpMode {
    /** Where the robot starts, in Pedro coordinates (inches, from the bottom-left corner). */
    public static Pose START_POSE = new Pose(72, 72, Math.toRadians(90));

    private Follower follower;
    private TelemetryManager panelsTelemetry;

    @Override
    public void init() {
        panelsTelemetry = PanelsTelemetry.INSTANCE.getTelemetry();
        FieldDrawing.init();

        follower = Constants.create(hardwareMap);
        if (follower == null) {
            telemetry.addLine("Pedro Pathing isn't set up yet.");
            telemetry.addLine("Fill in TeamCode/.../pedro/Constants.java first. See the README.");
        } else {
            follower.setPose(START_POSE);
            telemetry.addLine("Ready. Open Panels at http://192.168.43.1:8001 and press Play.");
        }
        telemetry.update();
    }

    @Override
    public void loop() {
        if (follower == null) {
            return;
        }

        follower.manual(
                -gamepad1.left_stick_y,
                gamepad1.left_stick_x,
                gamepad1.right_stick_x
        );
        follower.update();

        // Back button puts the robot back at the start pose and clears the trail
        if (gamepad1.backWasPressed()) {
            follower.setPose(START_POSE);
            FieldDrawing.init();
        }

        Pose pose = follower.pose();
        panelsTelemetry.addData("X (in)", String.format("%.1f", pose.x()));
        panelsTelemetry.addData("Y (in)", String.format("%.1f", pose.y()));
        panelsTelemetry.addData("Heading (deg)", String.format("%.1f", Math.toDegrees(pose.heading())));
        panelsTelemetry.addLine("Back button: reset to start");
        panelsTelemetry.update(telemetry);

        FieldDrawing.draw(follower);
    }
}
