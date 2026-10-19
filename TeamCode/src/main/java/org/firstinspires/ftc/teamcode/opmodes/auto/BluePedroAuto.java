package org.firstinspires.ftc.teamcode.opmodes.auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

/** Our Pedro autonomous from the blue side. All the logic is in PedroAuto. */
@Autonomous(name = "Pedro Auto: Blue", group = "BearBots", preselectTeleOp = "Main TeleOp")
public class BluePedroAuto extends PedroAuto {
    @Override
    protected Alliance alliance() {
        return Alliance.BLUE;
    }
}
