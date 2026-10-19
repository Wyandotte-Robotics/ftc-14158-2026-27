<img src="assets/ftc-14158-banner.png" alt="WMS BearBots, FIRST Tech Challenge Team 14158">

# FTC 14158 robot code · BIOBUZZ 2026–27

[![Build](https://github.com/Wyandotte-Robotics/ftc-14158-biobuzz/actions/workflows/build.yml/badge.svg)](https://github.com/Wyandotte-Robotics/ftc-14158-biobuzz/actions/workflows/build.yml)

Robot code for the **WMS BearBots**, *FIRST* Tech Challenge Team 14158 at Wilson Middle School, for the 2026–27 BIOBUZZ season. Part of [Wyandotte Robotics](https://github.com/Wyandotte-Robotics).

## Our robot

The **[goBILDA StarterBot with mecanum wheels](https://www.gobilda.com/ftc-starterbot-base-with-mecanum-wheels-resource-guide-2026-2027/)** (2026–27): a Strafer mecanum chassis, a GripForce Gecko wheel intake, and a launcher for POLLEN. We added a **goBILDA Pinpoint** with two **4-bar odometry pods** so the robot always knows where it is on the field.

## What's inside

| | Version | What it's for |
| --- | --- | --- |
| [FTC SDK](https://github.com/FIRST-Tech-Challenge/FtcRobotController) | 12.0 | The official *FIRST* robot controller app |
| [Pedro Pathing](https://pedropathing.com/docs) | 3.0.2 | Follows paths in autonomous, and knows where the robot is on the field |
| Pedro Pathing AutoTune | 1.0.2 | A web page on the robot that walks you through tuning Pedro |
| [Panels](https://panels.bylazar.com) | 1.0.18 | A dashboard in your browser: live field view, telemetry, graphs, and values you can tune live |

## Your first day

1. **Install [Android Studio](https://developer.android.com/studio)** (Narwhal 3 Feature Drop or newer).
2. **Get the code.** In Android Studio, choose **File → New → Project from Version Control** and paste `https://github.com/Wyandotte-Robotics/ftc-14158-biobuzz.git`. Or clone it with GitHub Desktop and open the folder.
3. **Wait for Gradle sync.** The first time downloads a lot and can take several minutes.
4. **Set up the robot configuration** on the Driver Hub with the names below.
5. **Connect to the robot.** Join the Control Hub's Wi-Fi (or plug in USB), choose **TeamCode** at the top, and press **Run ▶**.
6. **Drive.** On the Driver Hub, pick **Main TeleOp** and press Play.

## Robot configuration

Use these names on the Driver Hub. They match goBILDA's example code and [`robot/StarterBot.java`](../TeamCode/src/main/java/org/firstinspires/ftc/teamcode/robot/StarterBot.java).

| Name | Device | What it is |
| --- | --- | --- |
| `left_front_drive` | Motor: goBILDA 5202/3/4 series | Front left wheel |
| `right_front_drive` | Motor: goBILDA 5202/3/4 series | Front right wheel |
| `left_back_drive` | Motor: goBILDA 5202/3/4 series | Back left wheel |
| `right_back_drive` | Motor: goBILDA 5202/3/4 series | Back right wheel |
| `intake` | Motor: goBILDA 5202/3/4 series | Intake roller |
| `launcher` | Motor: goBILDA 5202/3/4 series | Launcher wheel. Plug in its encoder cable too, because the launcher holds its speed with it |
| `left_intake_servo` | Continuous Rotation Servo | Left corner intake servo |
| `right_intake_servo` | Continuous Rotation Servo | Right corner intake servo |
| `windmillServo` | Continuous Rotation Servo | Feeds POLLEN into the launcher |
| `pinpoint` | goBILDA Pinpoint Odometry Computer (on an I2C bus) | Position tracking |

Pinpoint wiring, from Pedro's guide: plug it into an I2C bus **other than bus 0** (the Control Hub's own IMU uses bus 0), mount it **sticker side up**, plug the **forward** pod into **X** and the **sideways** pod into **Y**.

## Driving

| Control (gamepad 1) | Does |
| --- | --- |
| Left stick | Drive forward/back and strafe left/right |
| Right stick | Turn |
| Right trigger | Intake in (pull harder to go faster) |
| Left trigger | Intake out |
| Right bumper | Hold to spin up the launcher; it feeds automatically once it's up to speed |
| Left bumper | Hold for slow mode |

To tune the launcher, open Panels while **Main TeleOp** runs. **Configurables → StarterBot** has the target speed and PIDF values, and the launcher's speed shows in telemetry. Copy values that work back into `StarterBot.java`, because Panels changes are lost when the robot restarts.

## Autonomous

**Starter Auto** ([`opmodes/auto/StarterAuto.java`](../TeamCode/src/main/java/org/firstinspires/ftc/teamcode/opmodes/auto/StarterAuto.java)) is our first autonomous routine, written to be easy to learn from. It waits an optional delay, launches the 4 preloaded POLLEN, then drives for a set time and stops. Afterwards the Driver Hub selects **Main TeleOp** automatically.

Set the robot in a legal starting spot aimed at our HIVE, at a distance where the launcher scores, then choose **Starter Auto** from the Autonomous list. It runs on timers, so it works before Pedro is tuned. Test with a charged battery. Its settings (delay, launch time, drive power and time) show up in Panels under **Configurables → StarterAuto**. The bottom of the file has a list of things to try, from easy changes to rewriting it with Pedro Pathing.

## Where things go

```
TeamCode/src/main/java/org/firstinspires/ftc/teamcode/
├── robot/     StarterBot.java: hardware names, launcher settings, drive/intake/launch actions
├── opmodes/   Your TeleOps (Main TeleOp is here)
│   └── auto/  Autonomous routines (Starter Auto is here)
├── pedro/     Pedro Pathing setup: Constants.java, Tuning.java and the tuners
├── tools/     Field Viewer: drive and watch the robot on the Panels field
├── util/      FieldDrawing: draws the robot and its paths on Panels
└── gobilda/   goBILDA's original StarterBot TeleOp, kept as a known-good reference
```

Leave `FtcRobotController/` alone. That's *FIRST*'s code, and SDK updates replace it. If something acts strange, run goBILDA's **Mec BioBuzz StarterBot Teleop** to check whether the problem is the robot or our code.

## Dashboards on the robot

Connect your laptop to the robot's Wi-Fi, then open:

| Page | Address | Use it to |
| --- | --- | --- |
| **Panels** | http://192.168.43.1:8001 | Watch the robot on the field, see telemetry and graphs, tune values live |
| **Pedro AutoTune** | http://192.168.43.1:10158 | Tune Pedro Pathing step by step |
| Robot Controller | http://192.168.43.1:8080 | Manage the configuration, read logs, use OnBot Java |

## Setting up Pedro Pathing

[`pedro/Constants.java`](../TeamCode/src/main/java/org/firstinspires/ftc/teamcode/pedro/Constants.java) already has the StarterBot's drive motors and a Pinpoint with 4-bar pods. Three things are left, all in **AutoTune** (http://192.168.43.1:10158):

1. **Mecanum Tuner** (optional): double-checks the motor directions. Only paste its result if it disagrees with what's there.
2. **Pinpoint Tuner**: enter `pinpoint`, choose the **4-bar** pod, then push the robot forward, push it left, and spin it 180°. Paste the `PinpointConfig` it generates over the one in `Constants.java`.
   **Now the Field Viewer works:** open Panels, run **Field Viewer**, and drive. The gold circle on the field should move just like the robot.
3. **Foresight Tuner**: the robot drives itself to learn how it speeds up and brakes, so give it room. Paste the result into `Constants.java` as `foresightConfig`, make `create()` return a real `Follower` (the comment shows how), and turn on the **Tests** in [`pedro/Tuning.java`](../TeamCode/src/main/java/org/firstinspires/ftc/teamcode/pedro/Tuning.java) to check path following.

The full guide is at [pedropathing.com/docs/pathing/tuning](https://pedropathing.com/docs/pathing/tuning).

## Updating the FTC SDK

*FIRST* releases SDK updates during the season. This repo keeps the SDK's history, so updates merge cleanly:

```bash
git remote add upstream https://github.com/FIRST-Tech-Challenge/FtcRobotController.git
git fetch upstream --tags
git merge v12.1   # use the newest release tag
```

Do it on a branch, make sure it builds, and open a pull request like any other change. Check for newer Pedro Pathing and Panels versions in `build.dependencies.gradle` at the same time.

## How we work

Read the [contributing guide](https://github.com/Wyandotte-Robotics/.github/blob/main/CONTRIBUTING.md) before your first change. The short version:

- Make a branch, open a pull request, get a review. Never commit straight to `main`.
- Every pull request gets an automatic build check. Fix it if it goes red.
- Only code from `main` goes on the robot at competitions.
- No passwords, student last names, contact details or photos. This repo is public.

## Credits

Built on the [FTC SDK](https://github.com/FIRST-Tech-Challenge/FtcRobotController) by *FIRST* ([license](../LICENSE)), [Pedro Pathing](https://github.com/Pedro-Pathing), [Panels](https://panels.bylazar.com) by Lazar, and goBILDA's [StarterBot example code](../TeamCode/src/main/java/org/firstinspires/ftc/teamcode/gobilda) (MIT).
