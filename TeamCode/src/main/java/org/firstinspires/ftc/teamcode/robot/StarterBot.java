package org.firstinspires.ftc.teamcode.robot;

import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.util.Range;

/**
 * Our robot: the goBILDA StarterBot with mecanum wheels (2026-27), plus a goBILDA Pinpoint
 * with two 4-bar odometry pods.
 *
 * Every OpMode builds one of these so the hardware names and settings live in one place.
 * The names match goBILDA's example code, so use them in the Driver Hub robot configuration.
 *
 * The non-final static values show up in Panels (http://192.168.43.1:8001) under Configurables,
 * so you can tune the launcher while the robot runs. Copy good values back into this file.
 */
@Configurable
public class StarterBot {
    // Robot configuration names
    public static final String LEFT_FRONT_DRIVE = "left_front_drive";
    public static final String RIGHT_FRONT_DRIVE = "right_front_drive";
    public static final String LEFT_BACK_DRIVE = "left_back_drive";
    public static final String RIGHT_BACK_DRIVE = "right_back_drive";
    public static final String INTAKE = "intake";
    public static final String LAUNCHER = "launcher";
    public static final String WINDMILL_SERVO = "windmillServo";
    public static final String LEFT_INTAKE_SERVO = "left_intake_servo";
    public static final String RIGHT_INTAKE_SERVO = "right_intake_servo";
    public static final String PINPOINT = "pinpoint";

    /** Launcher speed in encoder ticks per second. Motors report 28 ticks per revolution, so 1250 is about 2680 RPM. */
    public static double LAUNCHER_TARGET_VELOCITY = 1250;
    /** The windmill only feeds POLLEN once the launcher is at least this fast. */
    public static double LAUNCHER_MIN_VELOCITY = 1200;
    /** Velocity PIDF for the launcher, from goBILDA's example. */
    public static double LAUNCHER_P = 40;
    public static double LAUNCHER_I = 0;
    public static double LAUNCHER_D = 0;
    public static double LAUNCHER_F = 12.5;
    /** Extra intake power while feeding, to keep POLLEN moving into the launcher. */
    public static double FEED_INTAKE_BOOST = 0.5;

    public final DcMotor leftFront;
    public final DcMotor rightFront;
    public final DcMotor leftBack;
    public final DcMotor rightBack;
    public final DcMotor intake;
    public final DcMotorEx launcher;
    public final CRServo windmill;
    public final CRServo leftIntakeServo;
    public final CRServo rightIntakeServo;

    private PIDFCoefficients appliedPidf;

    public StarterBot(HardwareMap hardwareMap) {
        leftFront = hardwareMap.get(DcMotor.class, LEFT_FRONT_DRIVE);
        rightFront = hardwareMap.get(DcMotor.class, RIGHT_FRONT_DRIVE);
        leftBack = hardwareMap.get(DcMotor.class, LEFT_BACK_DRIVE);
        rightBack = hardwareMap.get(DcMotor.class, RIGHT_BACK_DRIVE);
        intake = hardwareMap.get(DcMotor.class, INTAKE);
        launcher = hardwareMap.get(DcMotorEx.class, LAUNCHER);
        windmill = hardwareMap.get(CRServo.class, WINDMILL_SERVO);
        leftIntakeServo = hardwareMap.get(CRServo.class, LEFT_INTAKE_SERVO);
        rightIntakeServo = hardwareMap.get(CRServo.class, RIGHT_INTAKE_SERVO);

        // The left motors face the other way, so they're reversed to make "forward" forward
        leftFront.setDirection(DcMotorSimple.Direction.REVERSE);
        leftBack.setDirection(DcMotorSimple.Direction.REVERSE);
        rightFront.setDirection(DcMotorSimple.Direction.FORWARD);
        rightBack.setDirection(DcMotorSimple.Direction.FORWARD);

        for (DcMotor motor : new DcMotor[] {leftFront, rightFront, leftBack, rightBack, intake}) {
            motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        }

        // The launcher holds a speed using its encoder, so its encoder cable must be plugged in
        launcher.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        applyLauncherPidf();

        rightIntakeServo.setDirection(DcMotorSimple.Direction.REVERSE);
        windmill.setDirection(DcMotorSimple.Direction.REVERSE);
        setIntake(0);
        setFeeder(false);
    }

    /** Robot-centric mecanum drive. Each value is from -1 to 1. */
    public void drive(double forward, double strafe, double turn) {
        double leftFrontPower = forward + strafe + turn;
        double rightFrontPower = forward - strafe - turn;
        double leftBackPower = forward - strafe + turn;
        double rightBackPower = forward + strafe - turn;

        // Scale everything down together so no wheel is asked for more than full power
        double max = Math.max(1.0, Math.max(
                Math.max(Math.abs(leftFrontPower), Math.abs(rightFrontPower)),
                Math.max(Math.abs(leftBackPower), Math.abs(rightBackPower))));

        leftFront.setPower(leftFrontPower / max);
        rightFront.setPower(rightFrontPower / max);
        leftBack.setPower(leftBackPower / max);
        rightBack.setPower(rightBackPower / max);
    }

    public void stopDrive() {
        drive(0, 0, 0);
    }

    /** Runs the intake roller and both corner servos. Positive pulls POLLEN in, negative pushes it out. */
    public void setIntake(double power) {
        power = Range.clip(power, -1, 1);
        intake.setPower(power);
        leftIntakeServo.setPower(power);
        rightIntakeServo.setPower(power);
    }

    public void spinUpLauncher() {
        applyLauncherPidf();
        launcher.setVelocity(LAUNCHER_TARGET_VELOCITY);
    }

    public void stopLauncher() {
        launcher.setVelocity(0);
    }

    public double launcherVelocity() {
        return launcher.getVelocity();
    }

    /** True once the launcher is fast enough to make a good shot. */
    public boolean launcherReady() {
        return launcherVelocity() > LAUNCHER_MIN_VELOCITY;
    }

    /** Turns the windmill on to feed POLLEN into the launcher. */
    public void setFeeder(boolean on) {
        windmill.setPower(on ? 1 : 0);
    }

    /** Sends the PIDF values to the motor, but only when they've changed (for example, from Panels). */
    private void applyLauncherPidf() {
        PIDFCoefficients pidf = new PIDFCoefficients(LAUNCHER_P, LAUNCHER_I, LAUNCHER_D, LAUNCHER_F);
        if (appliedPidf == null
                || appliedPidf.p != pidf.p || appliedPidf.i != pidf.i
                || appliedPidf.d != pidf.d || appliedPidf.f != pidf.f) {
            launcher.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, pidf);
            appliedPidf = pidf;
        }
    }
}
