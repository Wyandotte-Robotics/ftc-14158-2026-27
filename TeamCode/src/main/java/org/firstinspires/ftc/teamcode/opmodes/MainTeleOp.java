package org.firstinspires.ftc.teamcode.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

/**
 * Starter driver-controlled OpMode for a mecanum drivetrain. It works before Pedro Pathing is
 * tuned, so it's the first thing to run on a new robot.
 *
 * Controls (gamepad 1):
 *   Left stick   drive forward/back and strafe left/right
 *   Right stick  turn
 *   Right bumper hold for slow mode
 *
 * The motor names below must match the names in your Driver Hub robot configuration.
 * If a wheel spins the wrong way, flip its direction below.
 */
@TeleOp(name = "Main TeleOp", group = "BearBots")
public class MainTeleOp extends LinearOpMode {
    public static String FRONT_LEFT = "frontLeft";
    public static String FRONT_RIGHT = "frontRight";
    public static String BACK_LEFT = "backLeft";
    public static String BACK_RIGHT = "backRight";

    /** Speed multiplier while the right bumper is held. */
    public static double SLOW_MODE = 0.4;

    @Override
    public void runOpMode() {
        DcMotor frontLeft = hardwareMap.get(DcMotor.class, FRONT_LEFT);
        DcMotor frontRight = hardwareMap.get(DcMotor.class, FRONT_RIGHT);
        DcMotor backLeft = hardwareMap.get(DcMotor.class, BACK_LEFT);
        DcMotor backRight = hardwareMap.get(DcMotor.class, BACK_RIGHT);

        // On most mecanum robots the left side has to be reversed so "forward" is forward
        frontLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        backLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        frontRight.setDirection(DcMotorSimple.Direction.FORWARD);
        backRight.setDirection(DcMotorSimple.Direction.FORWARD);

        for (DcMotor motor : new DcMotor[] {frontLeft, frontRight, backLeft, backRight}) {
            motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        }

        telemetry.addLine("Go BearBots! Press Play to drive.");
        telemetry.update();
        waitForStart();

        while (opModeIsActive()) {
            double forward = -gamepad1.left_stick_y;
            double strafe = gamepad1.left_stick_x * 1.1; // strafing is a little slower than driving
            double turn = gamepad1.right_stick_x;

            // Scale everything down together so no wheel is asked for more than full power
            double denominator = Math.max(Math.abs(forward) + Math.abs(strafe) + Math.abs(turn), 1);
            double scale = gamepad1.right_bumper ? SLOW_MODE : 1.0;

            frontLeft.setPower((forward + strafe + turn) / denominator * scale);
            backLeft.setPower((forward - strafe + turn) / denominator * scale);
            frontRight.setPower((forward - strafe - turn) / denominator * scale);
            backRight.setPower((forward + strafe - turn) / denominator * scale);

            telemetry.addData("Mode", gamepad1.right_bumper ? "SLOW" : "Full speed");
            telemetry.update();
        }
    }
}
