package org.firstinspires.ftc.teamcode.pedro;

import com.pedropathing.revhub.drivetrains.Mecanum;
import com.pedropathing.tuning.autotune.Procedure;
import com.pedropathing.tuning.autotune.Tuner;

import org.firstinspires.ftc.teamcode.pedro.procedures.ForesightTuner;
import org.firstinspires.ftc.teamcode.pedro.procedures.MecanumTuner;
import org.firstinspires.ftc.teamcode.pedro.procedures.PinpointTuner;

/**
 * Tuners that show up in Pedro Pathing AutoTune at http://192.168.43.1:10158
 * Tuning guide: https://pedropathing.com/docs/pathing/tuning
 *
 * Run them in this order: Mecanum (to double-check the motors), Pinpoint, then Foresight.
 */
public class Tuning {
    @Tuner
    public static Procedure mecanumTuner() {
        return new MecanumTuner();
    }

    @Tuner
    public static Procedure pinpointTuner() {
        return new PinpointTuner();
    }

    @Tuner
    public static Procedure foresightTuner() {
        return new ForesightTuner(
                Constants::createLocalizer,
                hardwareMap -> new Mecanum(hardwareMap, Constants.drivetrainConfig));
    }

    // Once Constants has a foresightConfig, turn on the Tests to check path following.
    // https://pedropathing.com/docs/pathing/tuning/test
    // (imports: procedures.Tests, com.pedropathing.algorithm.Foresight)
    //
    // @Tuner
    // public static Procedure tests() {
    //     return new Tests(
    //             hardwareMap -> new Mecanum(hardwareMap, Constants.drivetrainConfig),
    //             Constants::createLocalizer,
    //             () -> new Foresight(Constants.foresightConfig));
    // }
}
