package org.firstinspires.ftc.teamcode.pedro;

import com.pedropathing.follower.Follower;
import com.pedropathing.localization.Localizer;
import com.pedropathing.revhub.drivetrains.MecanumConfig;
import com.pedropathing.revhub.localizers.PinpointConfig;
import com.pedropathing.revhub.localizers.PinpointLocalizer;
import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.robot.StarterBot;

/**
 * Pedro Pathing's settings for our goBILDA StarterBot (mecanum) with a Pinpoint and 4-bar pods.
 *
 * AutoTune (http://192.168.43.1:10158) generates each config for you to paste in here.
 * Full example: https://pedropathing.com/docs/pathing/tuning/constants
 */
public class Constants {
    /** Drive motors. Names and directions match goBILDA's StarterBot example code. */
    public static MecanumConfig drivetrainConfig = new MecanumConfig(
            c -> {
                c.frontLeftName.set(StarterBot.LEFT_FRONT_DRIVE);
                c.backLeftName.set(StarterBot.LEFT_BACK_DRIVE);
                c.frontRightName.set(StarterBot.RIGHT_FRONT_DRIVE);
                c.backRightName.set(StarterBot.RIGHT_BACK_DRIVE);

                c.frontLeftDirection.set(DcMotorSimple.Direction.REVERSE);
                c.backLeftDirection.set(DcMotorSimple.Direction.REVERSE);
                c.frontRightDirection.set(DcMotorSimple.Direction.FORWARD);
                c.backRightDirection.set(DcMotorSimple.Direction.FORWARD);

                c.manualBrakeMode.set(true);
            }
    );

    /**
     * goBILDA Pinpoint with two 4-bar odometry pods.
     *
     * TODO: the pod directions and offsets below are placeholders. Run the Pinpoint Tuner in
     * AutoTune and paste the PinpointConfig it generates over this one.
     */
    public static PinpointConfig localizerConfig = new PinpointConfig(
            c -> {
                c.name.set(StarterBot.PINPOINT);
                c.podType.set(GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_4_BAR_POD);
                c.xPodDirection.set(GoBildaPinpointDriver.EncoderDirection.FORWARD);
                c.yPodDirection.set(GoBildaPinpointDriver.EncoderDirection.FORWARD);
                c.xPodOffset.set(0.0);
                c.yPodOffset.set(0.0);
            }
    );

    // TODO: paste the ForesightConfig from the Foresight Tuner here, named foresightConfig.

    /** The robot's position tracker on its own. The Field Viewer uses this, so it works before Foresight is tuned. */
    public static Localizer createLocalizer(HardwareMap h) {
        return new PinpointLocalizer(h, localizerConfig);
    }

    /**
     * The full path follower. Returns null until Foresight is tuned. Then replace the body with:
     *
     *     return new Follower(
     *             createLocalizer(h),
     *             new Mecanum(h, drivetrainConfig),
     *             new Foresight(foresightConfig));
     *
     * (imports: com.pedropathing.revhub.drivetrains.Mecanum, com.pedropathing.algorithm.Foresight)
     */
    public static Follower create(HardwareMap h) {
        return null;
    }
}
