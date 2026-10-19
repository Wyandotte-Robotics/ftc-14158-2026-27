package org.firstinspires.ftc.teamcode.opmodes.auto;

import static com.pedropathing.api.Paths.line;

import com.pedropathing.api.PoseFactory;
import com.pedropathing.math.Pose;
import com.pedropathing.paths.Path;

/**
 * Where our BIOBUZZ autonomous goes, and the paths between those spots.
 *
 * !!! These are BEST GUESSES. None of them have been measured on a real field yet. !!!
 * Put the robot on the field, drive it to each spot, read its position from the Field Viewer,
 * and fix the numbers here.
 *
 * Coordinates are Pedro Pathing's, in inches, looking down at the field with the audience at
 * the bottom:
 *
 *                     far wall (y = 144)
 *          +--------------------------------------+
 *          |                                      |
 *          |                                      |
 *     red  |          +-----------------+         |  blue
 *    wall  |          |  red    blue    |         |  wall
 *  (x = 0) |          |  HIVE   HIVE    |         | (x = 144)
 *          |          +-----------------+         |
 *          |   LOADING                            |
 *          |   ZONE                               |
 *          | GARDEN                               |
 *          +--------------------------------------+
 *        (0, 0)       audience wall (y = 0)
 *
 * Heading 0° faces the blue wall and 90° faces the far wall.
 * Everything here is written for RED. Alliance.BLUE turns it all 180° around the field center.
 *
 * Where the guesses come from:
 * - BIOBUZZ Competition Manual, sections 9 and 10: field size, rules for where we start, and
 *   what LEAVE and PARK mean.
 * - Two community 3D models of the field, which place the HIVEs about 12.75 in either side of
 *   center and the red HIVE's audience-side CELL tipped up at the start. They disagree about
 *   which end of the wall the LOADING ZONE and GARDEN are on, so those are the shakiest guesses.
 */
public class AutoPoses {
    /** The middle of the field. The manual says the field is about 144 in inside the walls. */
    public static final double FIELD_CENTER = 72;

    /** Our HIVE CELL that starts tipped up: red HIVE (x about 59), audience-side end. We aim here. */
    public final Pose hiveCell;

    /** Touching our wall (rule G304), facing into the field. 9 in is half our robot's length. */
    public final Pose start;
    /** Where we launch from: lined up with the HIVE CELL. Find the spot where Main TeleOp scores and measure it. */
    public final Pose launch;
    /** Our GARDEN's 4 POLLEN sit in a line along the audience wall, starting in our corner. We start the sweep here... */
    public final Pose gardenStart;
    /** ...and drive along the POLLEN toward the corner with the intake running. */
    public final Pose gardenEnd;
    /** Partly inside our LOADING ZONE, which is on our wall. That's PARK. */
    public final Pose park;

    public AutoPoses(Alliance alliance) {
        PoseFactory p = alliance.poseFactory();

        //                 x     y   heading
        hiveCell    = p.of(59,  57,    0);
        start       = p.of( 9,  84,    0);
        launch      = p.of(22,  57,    0);
        gardenStart = p.of(40,  10,  180);
        gardenEnd   = p.of(14,  10,  180);
        park        = p.of(12,  36,    0);
    }

    // Each path is a straight line. ".linear" turns the robot smoothly from the first pose's
    // heading to the second's, and ".constant" keeps the heading the same the whole way.

    public Path startToLaunch() {
        return line(start, launch).linear(start, launch);
    }

    public Path launchToGarden() {
        return line(launch, gardenStart).linear(launch, gardenStart);
    }

    public Path sweepGarden() {
        return line(gardenStart, gardenEnd).constant(gardenStart);
    }

    public Path gardenToLaunch() {
        return line(gardenEnd, launch).linear(gardenEnd, launch);
    }

    public Path launchToPark() {
        return line(launch, park).linear(launch, park);
    }

    /** From wherever the robot is to the park spot. Used if time is running out. */
    public Path toPark(Pose from) {
        return line(from, park).linear(from, park);
    }

    /** Every path, in order. Used to preview the plan on the Panels field. */
    public Path[] allPaths() {
        return new Path[] {startToLaunch(), launchToGarden(), sweepGarden(), gardenToLaunch(), launchToPark()};
    }
}
