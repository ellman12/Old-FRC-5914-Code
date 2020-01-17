package frc.robot;

import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.StatusFrameEnhanced;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.SpeedControllerGroup;

public class Lift {

    // Creating lift motors
    WPI_TalonSRX leftLift, rightLift, redcarriage;

    Encoder MagEncoder;

    SpeedControllerGroup lift;

    // Linking classes together
    RobotDrive drive;
    Robot robot;

    // Final variables for the default positions of the lift
    final double INNER_CARRIAGE_DEFAULT_POSITION = 0;
    final double OUTER_CARRIAGE_DEFAULT_POSITION = 0;

    // Final variables used for automatic lift stuff
    // ALL OF THESE WILL MOST LIKELY HAVE TO BE ADJUSTED
    final int INNER_CARRIAGE_PLATE_PICKUP = -62000;
    final int INNER_CARRIAGE_BALL_PICKUP = -68000;
    final int INNER_CARRIAGE_CARGO_PLATE = -34656;
    final int INNER_CARRIAGE_CARGO_BALL = -20000;

    // WE WILL NEED SOME MORE OF THESE VARIABLES FOR LEVELS 1 AND 2 OF THE ROCKET
    // FOR BALLS AND PLATES!!!!
    // I just created some dummy variables for this purpose
    final int INNER_CARRIAGE_LEVEL_1_ROCKET_PLATE = 0;
    final int INNER_CARRIAGE_LEVEL_1_ROCKET_BAll = 0;
    final int INNER_CARRIAGE_LEVEL_2_ROCKET_PLATE = 0;
    final int INNER_CARRIAGE_LEVEL_2_ROCKET_BAll = 0;
    final int INNER_CARRIAGE_LEVEL_3_ROCKET_PLATE = 0;
    final int OUTER_CARRIAGE_LEVEL_3_ROCKET_PLATE = 0;

    final int IC_DEADBAND = 100;
    final int OC_DEADBAND = 100;

    public Lift() {

        leftLift = new WPI_TalonSRX(7);
        rightLift = new WPI_TalonSRX(8);
        redcarriage = new WPI_TalonSRX(9);

        lift = new SpeedControllerGroup(leftLift, rightLift);

        drive = new RobotDrive();
        robot = new Robot();

        leftLift.setNeutralMode(NeutralMode.Brake);
        rightLift.setNeutralMode(NeutralMode.Brake);
        redcarriage.setNeutralMode(NeutralMode.Brake);

        MagEncoder = new Encoder(0, 1, false, Encoder.EncodingType.k4X);

        leftLift.setStatusFramePeriod(StatusFrameEnhanced.Status_2_Feedback0, 1, 30);
        leftLift.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0, 30);

    }

    // This method makes the outer lift move up and down
    public void OuterLift() {

        if (drive.stick.getRawButton(robot.BUTTON_Y) == true) {
            lift.set(0.3);
        } else if (drive.stick.getRawButton(robot.BUTTON_Y) == false
                && drive.stick.getRawButton(robot.BUTTON_B) == false
                && drive.stick.getRawButton(robot.BUTTON_X) == false) {
            lift.stopMotor();
        }

        if (drive.stick.getRawButton(robot.BUTTON_B) == true) {
            lift.set(-0.17);
        }
    }

    // This method moves the inner carriage of the lift
    public void Carriage() {

        // This variable is used for making the inner carriage go up/down
        double triggerAxis;

        // Axes 2 and 3 are the left and right analog triggers, respectively
        triggerAxis = drive.stick.getRawAxis(3) - drive.stick.getRawAxis(4);
        redcarriage.set(triggerAxis * 0.50);
    }

    // This method allows you to set the position of the inner carriage
    public void setInnerCarriage(int encoder_target) {
        int RedLine_Encoder_Value = MagEncoder.get();
        if (RedLine_Encoder_Value < (encoder_target - IC_DEADBAND)) {
            redcarriage.set(-0.35);
        } else if (RedLine_Encoder_Value > (encoder_target + IC_DEADBAND)) {
            redcarriage.set(0.35);
        } else {
            redcarriage.stopMotor();
        }
    }

    // This method allows you to set the position of the outer carriage
    public void setOuterCarriage(int encoder_target) {
        int LeftLift_Encoder_Value = leftLift.getSelectedSensorPosition();
        if (LeftLift_Encoder_Value < (encoder_target - OC_DEADBAND)) {
            lift.set(-0.35);
        } else if (LeftLift_Encoder_Value > (encoder_target + OC_DEADBAND)) {
            lift.set(0.35);
        } else {
            lift.stopMotor();
        }
    }

    // public void MoveCarriageWithEncoder(String target) {

    // // Most of this most likely won't work
    // // A LOT of testing will need to be done

    // switch (target) {
    // case "Cargo Ship Plate":
    // setInnerCarriage(INNER_CARRIAGE_CARGO_PLATE);

    // // I have no idea if this will work, but, theoretically, it should make the
    // lift
    // // go back down. Same applies for the rest of these kind of things
    // // Not even sure if these reverse ones with the "-" will be needed...
    // setInnerCarriage(-INNER_CARRIAGE_CARGO_PLATE);

    // break;

    // case "Cargo Ship Ball":
    // setInnerCarriage(INNER_CARRIAGE_CARGO_BALL);

    // setInnerCarriage(-INNER_CARRIAGE_CARGO_BALL);

    // break;

    // case "Carriage Plate Pickup":
    // setInnerCarriage(INNER_CARRIAGE_PLATE_PICKUP);

    // setInnerCarriage(-INNER_CARRIAGE_PLATE_PICKUP);

    // break;

    // case "Carriage Ball Pickup":
    // setInnerCarriage(INNER_CARRIAGE_BALL_PICKUP);

    // setInnerCarriage(-INNER_CARRIAGE_BALL_PICKUP);

    // break;

    // case "Plate Level 2":
    // setInnerCarriage(INNER_CARRIAGE_LEVEL_2_ROCKET_PLATE);

    // break;

    // case "Ball Level 2":
    // // setInnerCarriage(INNER_CARRIAGE_LEVEL_2_ROCKET_BALL);

    // break;

    // }

    // }

    // public void MoveOuterLiftWithEncoder(String target) {

    // switch (target) {

    // case "Plate Level 3":

    // // setInnerCarriage();
    // break;

    // }

    // }

    // public void MoveToDefaultPosition() {

    // }

    // public void MoveOutOfDefaultPosition() {

    // }

}