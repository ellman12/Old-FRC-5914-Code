package frc.robot;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;
import com.revrobotics.CANEncoder;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

public class Intake {

    // Final variables used for automatic tilt intake stuff
    // FYI, THESE MIGHT HAVE TO BE ADJUSTED
    // These represent the counts on the NEO intake tilt motor
    final double INTAKE_TILT_DEADBAND = 80.0;
    final double TILT_PLATE_INSERT = 900.0;
    final double TILT_BALL_PICKUP = 720.0;
    final double TILT_BALL_INSERT_ROCKET_LEVEL_1 = 900.0;
    final double TILT_BALL_CARGO_SHIP = 1080.0;
    final double TILT_BALL_INSERT_ROCKET_LEVEL_2 = 900.0;

    // Variable used for the value of the tilt encoder
    double intakeTiltEncoderValue;

    // Creating intake motors
    WPI_TalonSRX leftIntake;
    WPI_VictorSPX rightIntake;
    CANSparkMax intakeTilt;

    // Creates the encoder on the NEO motor on the tilt
    CANEncoder intakeTiltEncoder;

    // Linking classes together
    RobotDrive drive;
    Robot robot;

    public Intake() {

        leftIntake = new WPI_TalonSRX(6);
        rightIntake = new WPI_VictorSPX(10);
        intakeTilt = new CANSparkMax(11, MotorType.kBrushless);
        intakeTiltEncoder = new CANEncoder(intakeTilt);

        drive = new RobotDrive();
        robot = new Robot();

        rightIntake.setSafetyEnabled(false);
        leftIntake.setSafetyEnabled(false);

        rightIntake.setNeutralMode(NeutralMode.Brake);
        intakeTilt.setIdleMode(IdleMode.kBrake);
        leftIntake.setNeutralMode(NeutralMode.Brake);

        rightIntake.setInverted(true);

        intakeTiltEncoderValue = intakeTiltEncoder.getPosition();

    }

    // This method is used for ejecting balls
    public void Exhale() {
        if (drive.stick.getRawButton(robot.BUTTON_A) == true) {
            leftIntake.set(-.4);
            rightIntake.set(-.4);
        } else if (drive.stick.getRawButton(robot.BUTTON_A) == false
                && drive.stick.getRawButton(robot.LEFT_SHOULDER_BUTTON) == false) {
            leftIntake.stopMotor();
            rightIntake.stopMotor();
        }
    }

    // This method is used for intaking balls
    public void Inhale() {
        if (drive.stick.getRawButton(robot.LEFT_SHOULDER_BUTTON) == true) {
            leftIntake.set(.4);
            rightIntake.set(.4);
        } else if (drive.stick.getRawButton(robot.LEFT_SHOULDER_BUTTON) == false
                && drive.stick.getRawButton(robot.BUTTON_A) == false) {
            leftIntake.stopMotor();
            rightIntake.stopMotor();
        }
    }

    // This method is used for controlling the tilt on the intake
    public void Tilt() {
            intakeTilt.set(drive.stick.getRawAxis(5) * .35);
    }

    // This method allows for custom positioning of the tilt on the intake
    // public void setTilt(double tiltTarget) {
        
    //     intakeTiltEncoderValue = intakeTiltEncoder.getPosition();

    //     if (intakeTiltEncoderValue < (tiltTarget - INTAKE_TILT_DEADBAND)) {
    //         intakeTilt.set(-.2);
    //     } else if (intakeTiltEncoderValue > (tiltTarget + INTAKE_TILT_DEADBAND)) {
    //         intakeTilt.set(.2);
    //     } else {
    //         intakeTilt.stopMotor();
    //     }  
    // }

    // // Input a specific value as a String, and the tilt on the intake will go to a
    // // predefined spot
    // public void tiltPredefinedPositions(String target) {
    //     switch (target) {
    //     case "Plate Level 1":
    //         setTilt(TILT_PLATE_INSERT);
    //         break;
    //     case "Plate Level 2":
    //         setTilt(TILT_PLATE_INSERT);
    //         break;
    //     case "Plate Level 3":
    //         setTilt(TILT_PLATE_INSERT);
    //         break;
    //     case "Ball Pickup":
    //         setTilt(TILT_BALL_PICKUP);
    //         break;
    //     case "Ball Rocket 1":
    //         setTilt(TILT_BALL_INSERT_ROCKET_LEVEL_1);
    //         break;
    //     case "Ball Cargo Ship":
    //         setTilt(TILT_BALL_CARGO_SHIP);
    //         break;
    //     case "Ball Rocket 2":
    //         setTilt(TILT_BALL_INSERT_ROCKET_LEVEL_2);
    //         break;
    //     }
    // }
}