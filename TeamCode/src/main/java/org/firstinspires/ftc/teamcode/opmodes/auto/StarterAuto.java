package org.firstinspires.ftc.teamcode.opmodes.auto;

import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.robot.StarterBot;

/**
 * Our first autonomous routine. It's kept simple on purpose, so you can read it top to bottom
 * like a recipe and change it.
 *
 * In the 30-second autonomous period it:
 *   1. Waits START_DELAY seconds (handy for staying out of our alliance partner's way)
 *   2. Spins up the launcher and launches our 4 preloaded POLLEN
 *   3. Drives for DRIVE_TIME seconds, then stops
 *
 * Before you press Play: put the robot in a legal starting spot, aimed at our HIVE, at a distance
 * where the launcher scores in Main TeleOp.
 *
 * It uses timers instead of position tracking, so it works before Pedro Pathing is tuned. The
 * downside: a low battery or different carpet changes how far it goes, so test with a charged battery.
 *
 * The settings below show up in Panels (Configurables, StarterAuto), so you can try new values
 * without reinstalling. Copy the ones that work back into this file.
 */
@Configurable
@Autonomous(name = "Starter Auto", group = "BearBots", preselectTeleOp = "Main TeleOp")
public class StarterAuto extends LinearOpMode {
    /** Seconds to wait before doing anything. */
    public static double START_DELAY = 0;
    /** Longest we'll wait for the launcher to get up to speed before launching anyway. */
    public static double SPIN_UP_TIMEOUT = 3;
    /** Seconds of feeding. Make it long enough for all 4 POLLEN to leave the robot. */
    public static double LAUNCH_TIME = 4;
    /** Drive power, from -1 to 1. Positive is the way the left stick drives forward. */
    public static double DRIVE_POWER = 0.4;
    /** Seconds to drive after launching. */
    public static double DRIVE_TIME = 1.0;

    private StarterBot robot;
    private final ElapsedTime timer = new ElapsedTime();

    @Override
    public void runOpMode() {
        robot = new StarterBot(hardwareMap);

        showStatus("Ready. Aim at our HIVE and press Play.");
        waitForStart();

        // Step 1: wait, if we need to
        showStatus("Waiting " + START_DELAY + " seconds");
        waitSeconds(START_DELAY);

        // Step 2: launch the POLLEN we started with
        launchPreloads();

        // Step 3: drive, then stop
        showStatus("Driving");
        driveFor(DRIVE_POWER, 0, 0, DRIVE_TIME);

        showStatus("Done!");
    }

    /** Spins up the launcher, waits until it's fast enough, then feeds for LAUNCH_TIME seconds. */
    private void launchPreloads() {
        showStatus("Spinning up the launcher");
        robot.spinUpLauncher();
        timer.reset();
        while (opModeIsActive() && !robot.launcherReady() && timer.seconds() < SPIN_UP_TIMEOUT) {
            showLauncherSpeed();
        }

        showStatus("Launching");
        timer.reset();
        while (opModeIsActive() && timer.seconds() < LAUNCH_TIME) {
            // Just like Main TeleOp: only feed while the launcher is fast enough
            boolean ready = robot.launcherReady();
            robot.setFeeder(ready);
            robot.setIntake(ready ? StarterBot.FEED_INTAKE_BOOST : 0);
            showLauncherSpeed();
        }

        robot.setFeeder(false);
        robot.setIntake(0);
        robot.stopLauncher();
    }

    /** Drives with these powers (each -1 to 1) for some seconds, then stops. */
    private void driveFor(double forward, double strafe, double turn, double seconds) {
        robot.drive(forward, strafe, turn);
        waitSeconds(seconds);
        robot.stopDrive();
    }

    /** Waits without freezing the robot, so pressing Stop still works. */
    private void waitSeconds(double seconds) {
        timer.reset();
        while (opModeIsActive() && timer.seconds() < seconds) {
            idle();
        }
    }

    private void showStatus(String status) {
        telemetry.addData("Status", status);
        telemetry.update();
    }

    private void showLauncherSpeed() {
        telemetry.addData("Launcher", "%.0f / %.0f ticks per second",
                robot.launcherVelocity(), StarterBot.LAUNCHER_TARGET_VELOCITY);
        telemetry.update();
    }

    /*
     * Things to try, from easiest to hardest:
     *
     *   1. Change DRIVE_TIME until the robot stops where you want it.
     *   2. If it drives the wrong way, make DRIVE_POWER negative.
     *   3. Strafe instead of driving forward:   driveFor(0, 0.4, 0, 1.0);
     *   4. Add a turn after driving:            driveFor(0, 0, 0.3, 0.5);
     *   5. Make a copy for the other side of the field that strafes the opposite way.
     *   6. Let the driver choose START_DELAY with the D-pad before pressing Play.
     *      (Hint: use a while (opModeInInit()) loop before waitForStart().)
     *   7. Next level: rewrite it with Pedro Pathing, so the robot drives to exact spots on the
     *      field instead of guessing with timers. See https://pedropathing.com/docs/pathing/guide/setting-up-auto
     */
}
