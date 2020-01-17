package frc.robot;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;

public class RobotDrive {

    // Creating the joystick
    Joystick stick;

    // Linking classes together
    Robot robot;

    // Creating drive motors
    CANSparkMax frontLeft, frontRight, backLeft, backRight;

    // Grouping motors together
    SpeedControllerGroup leftDrive;
    SpeedControllerGroup rightDrive;
    SpeedControllerGroup allDrive;

    DifferentialDrive diff_drive;

    // This boolean controls whether the CubeInputsMode thing is active or not
    // By default, it's off. It turns on when the back button is pressed
    // boolean CubeJoystickInputsModeRunning;

    public RobotDrive() {

        // CubeJoystickInputsModeRunning = false;

        stick = new Joystick(0);

        robot = new Robot();

        frontLeft = new CANSparkMax(3, MotorType.kBrushless);
        backLeft = new CANSparkMax(1, MotorType.kBrushless);
        frontRight = new CANSparkMax(2, MotorType.kBrushless);
        backRight = new CANSparkMax(4, MotorType.kBrushless);

        leftDrive = new SpeedControllerGroup(frontLeft, backLeft);
        rightDrive = new SpeedControllerGroup(frontRight, backRight);
        allDrive = new SpeedControllerGroup(frontLeft, frontRight, backLeft, backRight);

        diff_drive = new DifferentialDrive(leftDrive, rightDrive);

        frontRight.setIdleMode(IdleMode.kCoast);
        backRight.setIdleMode(IdleMode.kCoast);
        frontLeft.setIdleMode(IdleMode.kCoast);
        backLeft.setIdleMode(IdleMode.kCoast);

        diff_drive.setSafetyEnabled(false);

    }

    // This method sets the joystick in CubeInputsMode, which cubes the joystick
    // inputs, making the robot much easier to control
    // It also disables the squaring of joystick inputs, which the ArcadeDrive
    // function does automatically
    // public void CubeJoystickInputsMode() {

    // if (stick.getRawButton(robot.BACK_BUTTON) == true) {
    // CubeJoystickInputsModeRunning = true;
    // Timer.delay(0.1);
    // } else {
    // CubeJoystickInputsModeRunning = false;
    // Timer.delay(0.1);
    // }

    // if (CubeJoystickInputsModeRunning == true) {
    // robot.LEFT_ANALOG_STICK_Y_AXIS = Math.pow(robot.LEFT_ANALOG_STICK_Y_AXIS, 3);
    // robot.LEFT_ANALOG_STICK_X_AXIS = Math.pow(robot.LEFT_ANALOG_STICK_X_AXIS, 3);
    // robot.SquareInputs = false;
    // } else {
    // robot.SquareInputs = true;
    // }
    // }
}