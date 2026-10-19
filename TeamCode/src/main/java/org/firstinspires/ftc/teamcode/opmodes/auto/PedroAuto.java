package org.firstinspires.ftc.teamcode.opmodes.auto;

import com.bylazar.configurables.annotations.Configurable;
import com.pedropathing.follower.Follower;
import com.pedropathing.math.Pose;
import com.pedropathing.paths.Path;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.pedro.Constants;
import org.firstinspires.ftc.teamcode.robot.StarterBot;
import org.firstinspires.ftc.teamcode.util.FieldDrawing;

/**
 * Our Pedro Pathing autonomous. RedPedroAuto and BluePedroAuto both run this; they only pick
 * the alliance.
 *
 * The plan (positions are in AutoPoses, and are best guesses for now):
 *   1. Drive from the wall to our launch spot, spinning up the launcher on the way
 *   2. Launch the 4 preloaded POLLEN
 *   3. Drive to our GARDEN and sweep up its 4 POLLEN with the intake running
 *   4. Drive back to the launch spot and launch them
 *   5. Park in our LOADING ZONE
 *
 * It's a "state machine": the robot is always on one Step. loop() runs about 50 times a
 * second, checks whether the current Step is finished, and if so starts the next one. Nothing
 * here waits or sleeps, because Pedro needs follower.update() called every loop.
 *
 * Before Pedro is tuned this won't drive, but press INIT and open Panels
 * (http://192.168.43.1:8001): it draws the whole plan on the field so you can check it.
 */
@Configurable
public abstract class PedroAuto extends OpMode {
    /** Seconds to run the feeder at each launch spot. */
    public static double LAUNCH_TIME = 3;
    /** If we're not parking by this many seconds into the 30-second AUTO, give up and go park. */
    public static double PARK_BY = 25;

    /** Each part of the plan, in order. */
    private enum Step {
        DRIVE_TO_LAUNCH,
        LAUNCH_PRELOADS,
        DRIVE_TO_GARDEN,
        SWEEP_GARDEN,
        RETURN_TO_LAUNCH,
        LAUNCH_GARDEN_POLLEN,
        PARK,
        DONE
    }

    private StarterBot robot;
    private Follower follower;
    private AutoPoses poses;
    private Step step;
    private final ElapsedTime stepTimer = new ElapsedTime();
    private final ElapsedTime matchTimer = new ElapsedTime();

    /** RedPedroAuto and BluePedroAuto answer this. */
    protected abstract Alliance alliance();

    @Override
    public void init() {
        robot = new StarterBot(hardwareMap);
        poses = new AutoPoses(alliance());
        FieldDrawing.init();

        follower = Constants.create(hardwareMap);
        if (follower != null) {
            follower.setPose(poses.start);
        }
    }

    @Override
    public void init_loop() {
        // Show the plan on the Panels field while we wait for Play
        FieldDrawing.drawTarget(poses.hiveCell);
        FieldDrawing.drawPlan(poses.start, poses.allPaths());

        telemetry.addData("Alliance", alliance());
        telemetry.addLine("The plan is drawn on the Panels field (http://192.168.43.1:8001).");
        if (follower == null) {
            telemetry.addLine("Pedro Pathing isn't tuned yet, so this won't drive. Use Starter Auto instead.");
        } else {
            telemetry.addLine("Put the robot at the start spot shown on Panels, then press Play.");
        }
        telemetry.update();
    }

    @Override
    public void start() {
        matchTimer.reset();
        if (follower == null) {
            return;
        }
        follow(poses.startToLaunch());
        setStep(Step.DRIVE_TO_LAUNCH);
    }

    @Override
    public void loop() {
        if (follower == null) {
            telemetry.addLine("Pedro Pathing isn't tuned yet. See the README.");
            telemetry.update();
            return;
        }

        follower.update();

        // Always leave time to park: it's worth points, and it's better than stopping halfway
        if (matchTimer.seconds() > PARK_BY && step != Step.PARK && step != Step.DONE) {
            stopMechanisms();
            follow(poses.toPark(follower.pose()));
            setStep(Step.PARK);
        }

        switch (step) {
            case DRIVE_TO_LAUNCH:
                robot.spinUpLauncher(); // get up to speed on the way, so we can launch right away
                if (!follower.isBusy()) {
                    setStep(Step.LAUNCH_PRELOADS);
                }
                break;

            case LAUNCH_PRELOADS:
                if (launchFinished()) {
                    robot.setIntake(1);
                    follow(poses.launchToGarden());
                    setStep(Step.DRIVE_TO_GARDEN);
                }
                break;

            case DRIVE_TO_GARDEN:
                if (!follower.isBusy()) {
                    follow(poses.sweepGarden());
                    setStep(Step.SWEEP_GARDEN);
                }
                break;

            case SWEEP_GARDEN:
                if (!follower.isBusy()) {
                    follow(poses.gardenToLaunch());
                    setStep(Step.RETURN_TO_LAUNCH);
                }
                break;

            case RETURN_TO_LAUNCH:
                robot.spinUpLauncher();
                if (!follower.isBusy()) {
                    setStep(Step.LAUNCH_GARDEN_POLLEN);
                }
                break;

            case LAUNCH_GARDEN_POLLEN:
                if (launchFinished()) {
                    follow(poses.launchToPark());
                    setStep(Step.PARK);
                }
                break;

            case PARK:
                if (!follower.isBusy()) {
                    setStep(Step.DONE);
                }
                break;

            case DONE:
                break;
        }

        FieldDrawing.draw(follower);

        Pose pose = follower.pose();
        telemetry.addData("Step", step);
        telemetry.addData("Time", "%.1f s", matchTimer.seconds());
        telemetry.addData("Position", "x %.1f  y %.1f  heading %.0f°", pose.x(), pose.y(), Math.toDegrees(pose.heading()));
        telemetry.addData("Launcher", "%.0f / %.0f", robot.launcherVelocity(), StarterBot.LAUNCHER_TARGET_VELOCITY);
        telemetry.update();
    }

    @Override
    public void stop() {
        stopMechanisms();
    }

    /**
     * Feeds POLLEN into the launcher whenever it's fast enough, like Main TeleOp does.
     * Returns true once LAUNCH_TIME seconds have passed on this Step.
     */
    private boolean launchFinished() {
        robot.spinUpLauncher();
        boolean ready = robot.launcherReady();
        robot.setFeeder(ready);
        robot.setIntake(ready ? StarterBot.FEED_INTAKE_BOOST : 0);

        if (stepTimer.seconds() < LAUNCH_TIME) {
            return false;
        }
        stopMechanisms();
        return true;
    }

    private void follow(Path path) {
        follower.follow(path);
    }

    private void setStep(Step next) {
        step = next;
        stepTimer.reset();
    }

    private void stopMechanisms() {
        robot.setFeeder(false);
        robot.setIntake(0);
        robot.stopLauncher();
    }
}
