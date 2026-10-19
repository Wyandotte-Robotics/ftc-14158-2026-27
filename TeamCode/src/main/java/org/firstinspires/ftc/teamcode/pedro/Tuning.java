package org.firstinspires.ftc.teamcode.pedro;

import com.pedropathing.tuning.autotune.Procedure;
import com.pedropathing.tuning.autotune.Tuner;

import org.firstinspires.ftc.teamcode.pedro.procedures.MecanumTuner;
import org.firstinspires.ftc.teamcode.pedro.procedures.OTOSTuner;
import org.firstinspires.ftc.teamcode.pedro.procedures.OctoQuadTuner;
import org.firstinspires.ftc.teamcode.pedro.procedures.PinpointTuner;
import org.firstinspires.ftc.teamcode.pedro.procedures.ThreeWheelIMUTuner;
import org.firstinspires.ftc.teamcode.pedro.procedures.ThreeWheelTuner;
import org.firstinspires.ftc.teamcode.pedro.procedures.TwoWheelTuner;

/**
 * Tuners that show up in Pedro Pathing AutoTune at http://192.168.43.1:10158
 * Tuning guide: https://pedropathing.com/docs/pathing/tuning
 *
 * Step 1: run the Mecanum Tuner. Step 2: run the tuner for the localizer your robot has.
 * You can delete the localizer tuners you don't use.
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
    public static Procedure otosTuner() {
        return new OTOSTuner();
    }

    @Tuner
    public static Procedure twoWheelTuner() {
        return new TwoWheelTuner();
    }

    @Tuner
    public static Procedure threeWheelTuner() {
        return new ThreeWheelTuner();
    }

    @Tuner
    public static Procedure threeWheelIMUTuner() {
        return new ThreeWheelIMUTuner();
    }

    @Tuner
    public static Procedure octoQuadTuner() {
        return new OctoQuadTuner();
    }

    // Step 3: once Constants has drivetrainConfig and localizerConfig, add the Foresight Tuner.
    // Swap PinpointLocalizer for your localizer.
    // https://pedropathing.com/docs/pathing/tuning/foresight
    //
    // @Tuner
    // public static Procedure foresightTuner() {
    //     return new ForesightTuner(
    //             hardwareMap -> new PinpointLocalizer(hardwareMap, Constants.localizerConfig),
    //             hardwareMap -> new Mecanum(hardwareMap, Constants.drivetrainConfig));
    // }

    // Step 4: once Constants has foresightConfig too, add the Tests to check path following.
    // https://pedropathing.com/docs/pathing/tuning/test
    //
    // @Tuner
    // public static Procedure tests() {
    //     return new Tests(
    //             hardwareMap -> new Mecanum(hardwareMap, Constants.drivetrainConfig),
    //             hardwareMap -> new PinpointLocalizer(hardwareMap, Constants.localizerConfig),
    //             () -> new Foresight(Constants.foresightConfig));
    // }
}
