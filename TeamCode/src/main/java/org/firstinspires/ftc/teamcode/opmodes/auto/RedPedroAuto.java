package org.firstinspires.ftc.teamcode.opmodes.auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

/** Our Pedro autonomous from the red side. All the logic is in PedroAuto. */
@Autonomous(name = "Pedro Auto: Red", group = "BearBots", preselectTeleOp = "Main TeleOp")
public class RedPedroAuto extends PedroAuto {
    @Override
    protected Alliance alliance() {
        return Alliance.RED;
    }
}
