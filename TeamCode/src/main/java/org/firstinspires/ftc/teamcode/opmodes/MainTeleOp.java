package org.firstinspires.ftc.teamcode.opmodes;

import com.bylazar.configurables.annotations.Configurable;
import com.bylazar.telemetry.PanelsTelemetry;
import com.bylazar.telemetry.TelemetryManager;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.robot.StarterBot;

/**
 * Driver-controlled OpMode for our goBILDA StarterBot. The controls match goBILDA's example,
 * plus a slow mode.
 *
 * Gamepad 1:
 *   Left stick      drive forward/back and strafe left/right
 *   Right stick     turn
 *   Right trigger   intake in (pull harder to go faster)
 *   Left trigger    intake out
 *   Right bumper    hold to spin up the launcher; it feeds automatically once it's up to speed
 *   Left bumper     hold for slow mode
 */
@Configurable
@TeleOp(name = "Main TeleOp", group = "BearBots")
public class MainTeleOp extends OpMode {
    /** Drive speed multiplier while the left bumper is held. */
    public static double SLOW_MODE = 0.4;

    private StarterBot robot;
    private TelemetryManager panelsTelemetry;

    @Override
    public void init() {
        robot = new StarterBot(hardwareMap);
        panelsTelemetry = PanelsTelemetry.INSTANCE.getTelemetry();
        telemetry.addLine("Go BearBots! Press Play to drive.");
        telemetry.update();
    }

    @Override
    public void loop() {
        double speed = gamepad1.left_bumper ? SLOW_MODE : 1.0;
        robot.drive(
                -gamepad1.left_stick_y * speed,
                gamepad1.left_stick_x * speed,
                gamepad1.right_stick_x * speed);

        double intakePower = gamepad1.right_trigger - gamepad1.left_trigger;

        if (gamepad1.right_bumper) {
            robot.spinUpLauncher();
        } else {
            robot.stopLauncher();
        }

        // Only feed once the launcher is fast enough, so shots don't fall short
        boolean feeding = gamepad1.right_bumper && robot.launcherReady();
        robot.setFeeder(feeding);
        if (feeding) {
            intakePower += StarterBot.FEED_INTAKE_BOOST;
        }
        robot.setIntake(intakePower);

        panelsTelemetry.addData("Launcher", feeding ? "FEEDING" : gamepad1.right_bumper ? "spinning up" : "off");
        panelsTelemetry.addData("Launcher velocity", Math.round(robot.launcherVelocity()));
        panelsTelemetry.addData("Launcher target", Math.round(StarterBot.LAUNCHER_TARGET_VELOCITY));
        panelsTelemetry.addData("Intake power", String.format("%.2f", intakePower));
        panelsTelemetry.addData("Drive", gamepad1.left_bumper ? "SLOW" : "full speed");
        panelsTelemetry.update(telemetry);
    }

    @Override
    public void stop() {
        robot.stopDrive();
        robot.stopLauncher();
        robot.setIntake(0);
        robot.setFeeder(false);
    }
}
