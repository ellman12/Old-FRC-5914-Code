package frc.robot;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.shuffleboard.SendableCameraWrapper;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Robot extends TimedRobot {

  WPI_TalonSRX frontRight, frontLeft, backRight, backLeft, candyShooter, spinnyThing;

  DifferentialDrive drive;

  SpeedControllerGroup leftDrive, rightDrive;

  Joystick stick;

  double stick2;
  double stick3;

  public static double speedCap = 1;

  public static final String speedChoice = "speedChoice";
  String speed;
  public SendableChooser<String> Speed = new SendableChooser<>();

  @Override
  public void robotInit() {

    Speed.addDefault("Speed Choice", speedChoice);
    SmartDashboard.putData("speedChoice", Speed);

    stick = new Joystick(0);

    frontRight = new WPI_TalonSRX(2);
    frontLeft = new WPI_TalonSRX(8);
    backRight = new WPI_TalonSRX(4);
    backLeft = new WPI_TalonSRX(1);
    candyShooter = new WPI_TalonSRX(11);
    spinnyThing = new WPI_TalonSRX(9);

    leftDrive = new SpeedControllerGroup(frontLeft, backLeft);
    rightDrive = new SpeedControllerGroup(frontRight, backRight);

    drive = new DifferentialDrive(leftDrive, rightDrive);

    // frontLeft.setInverted(true);
    // backLeft.setInverted(true);
    // frontRight.setInverted(true);
    // backRight.setInverted(true);
  }

  @Override
  public void robotPeriodic() {
  }

  @Override
  public void autonomousInit() {
  }

  @Override
  public void autonomousPeriodic() {
  }

  @Override
  public void teleopPeriodic() {

    speedCap = SmartDashboard.getNumber(speedChoice, 100) / 100;
    SmartDashboard.putNumber(speedChoice, speedCap * 100);

    // Joystick inputs are squared
    // drive.arcadeDrive(-stick.getRawAxis(1), stick.getRawAxis(0), true);

    // drive.arcadeDrive(stick.getRawAxis(0) * speedCap, -stick.getRawAxis(1) *
    // speedCap);

    drive.arcadeDrive(-stick.getRawAxis(1) * speedCap, stick.getRawAxis(0) * speedCap);

    // Left analog trigger
    stick2 = -stick.getRawAxis(2);
    // Right analog trigger
    Math.pow(stick2, 11);
    spinnyThing.set(stick2);

    stick3 = -stick.getRawAxis(3);
    // Right analog trigger
    Math.pow(stick3, 3001);
    stick3 /= 7.54;
    candyShooter.set(stick3);
  }

  @Override
  public void testPeriodic() {
  }
}