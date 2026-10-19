package org.firstinspires.ftc.teamcode.opmodes.auto;

import com.pedropathing.api.PoseFactory;

/** Which alliance we're on. Decides which side of the field our autonomous poses are on. */
public enum Alliance {
    RED,
    BLUE;

    /**
     * Makes poses for this alliance. Our poses are written for red. The BIOBUZZ field looks the
     * same from both alliance areas, so blue's poses are red's turned 180° around the field center.
     */
    public PoseFactory poseFactory() {
        PoseFactory degrees = PoseFactory.degrees();
        if (this == RED) {
            return degrees;
        }
        return degrees.mirrorAroundPoint(AutoPoses.FIELD_CENTER, AutoPoses.FIELD_CENTER);
    }
}
