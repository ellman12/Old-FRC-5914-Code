package frc.robot;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;

public class Robot extends TimedRobot {

  WPI_TalonSRX frontRight, frontLeft, backRight, backLeft, candyShooter;

  DifferentialDrive drive;

  SpeedControllerGroup leftDrive, rightDrive;

  Joystick stick;

  @Override
  public void robotInit() {

    frontRight = new WPI_TalonSRX(999);
    frontLeft = new WPI_TalonSRX(998);
    backRight = new WPI_TalonSRX(997);
    backLeft = new WPI_TalonSRX(996);
    candyShooter = new WPI_TalonSRX(995);

    leftDrive = new SpeedControllerGroup(frontLeft, frontRight);
    rightDrive = new SpeedControllerGroup(frontRight, frontLeft);

    drive = new DifferentialDrive(leftDrive, rightDrive);
  }

  @Override
  public void robotPeriodic() {
  }

  @Override
  public void autonomousInit() {
  }

  @Override
  public void autonomousPeriodic() {

    teleopPeriodic();

  }

  @Override
  public void teleopPeriodic() {

    //Joystick inputs are squared
    drive.arcadeDrive(stick.getRawAxis(1), stick.getRawAxis(0), true);

    // Right analog trigger
    candyShooter.set(stick.getRawAxis(3));
  }

  @Override
  public void testPeriodic() {
    teleopPeriodic();
  }
}
