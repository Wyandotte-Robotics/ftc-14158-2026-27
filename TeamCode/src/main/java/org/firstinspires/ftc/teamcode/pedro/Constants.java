package org.firstinspires.ftc.teamcode.pedro;

import com.pedropathing.follower.Follower;
import com.qualcomm.robotcore.hardware.HardwareMap;

/**
 * Pedro Pathing's settings for this robot.
 *
 * AutoTune (http://192.168.43.1:10158) generates each config for you to paste in here:
 * drivetrainConfig from the Mecanum Tuner, localizerConfig from your localizer's tuner, and
 * foresightConfig from the Foresight Tuner. Then make create() return a real Follower.
 *
 * Until create() stops returning null, path following and the Field Viewer won't run.
 * Full example: https://pedropathing.com/docs/pathing/tuning/constants
 */
public class Constants {
    public static Follower create(HardwareMap h) {
        // return new Follower(
        //         new PinpointLocalizer(h, localizerConfig),
        //         new Mecanum(h, drivetrainConfig),
        //         new Foresight(foresightConfig));
        return null;
    }
}
