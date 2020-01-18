package org.usfirst.frc.team5914.robot;

import edu.wpi.first.wpilibj.Timer;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/////////////////////////////////////////////////////////////////////
//  Class:
/////////////////////////////////////////////////////////////////////
//
//Purpose:
//
//Arguments:
//
//Returns:
//
//Remarks:
//
/////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////

public class VulcanDrive {

	ADXRS450_Gyro gyro;

	final int gyroChannel = 0;

	WPI_TalonSRX frontLeft, frontRight, backLeft, backRight;

	SpeedControllerGroup leftDrive, rightDrive, allDrive;

	speedCorrection corr_table;

	static boolean zoop;

	/// Joystick stick;

	DifferentialDrive drive;

	static boolean invertDrive;

	// DistanceDetermination dist_det; // not used?

	// Constructor
	/////////////////////////////////////////////////////////////////////
	// Function: public autonMove(int lf_ch, int lr_ch, int rf_ch, int rr_ch)
	/////////////////////////////////////////////////////////////////////
	//
	// Purpose: Constructor the the class. Creates the devices used to
	// control the robot drive system. Assumed is that
	//
	// Arguments:
	//
	// Returns:
	//
	// Remarks:
	//
	/////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////
	public VulcanDrive() {
		gyro = new ADXRS450_Gyro();

		corr_table = new speedCorrection();

		// Motors
		frontLeft = new WPI_TalonSRX(2);
		backLeft = new WPI_TalonSRX(1);
		frontRight = new WPI_TalonSRX(3);
		backRight = new WPI_TalonSRX(4);

		leftDrive = new SpeedControllerGroup(frontLeft, backLeft);
		rightDrive = new SpeedControllerGroup(frontRight, backRight);
		allDrive = new SpeedControllerGroup(frontLeft, backLeft, frontRight, backRight);

		drive = new DifferentialDrive(leftDrive, rightDrive);

		// Making the motors all turn the right way
		frontRight.setInverted(true);
		backRight.setInverted(true);

		// dist_det = new DistanceDetermination(21);

		frontLeft.setSafetyEnabled(false);
		backLeft.setSafetyEnabled(false);
		frontRight.setSafetyEnabled(false);
		backRight.setSafetyEnabled(false);

		drive.setSafetyEnabled(false);

		zoop = false;
	}

	/////////////////////////////////////////////////////////////////////
	// Function:
	/////////////////////////////////////////////////////////////////////
	//
	// Purpose:
	//
	// Arguments:
	//
	// Returns:
	//
	// Remarks:
	//
	/////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////

	public void moveFwd(double distance, boolean fast) {

		double time, speed;

		if (fast) {
			speed = 0.5;
		} else {
			speed = 0.4;
		}

		double right_speed;
		right_speed = corr_table.computeRightCommand(speed);

		time = Robot.auto_functions.determineTimeDelay(distance);

		frontLeft.set(speed);
		frontRight.set(right_speed);
		backRight.set(right_speed);
		backLeft.set(speed);
		Timer.delay(time);

		SmartDashboard.putNumber("Time", time);
		SmartDashboard.putNumber("Distance", distance);
	}

	public void moveFwd(double distance) {

		double time, speed;

		speed = 0.4;

		double right_speed;
		right_speed = corr_table.computeRightCommand(speed);

		time = Robot.auto_functions.determineTimeDelay(distance);

		frontLeft.set(speed);
		frontRight.set(right_speed);
		backRight.set(right_speed);
		backLeft.set(speed);
		Timer.delay(time);

		SmartDashboard.putNumber("Time", time);
		SmartDashboard.putNumber("Distance", distance);
	}

	/////////////////////////////////////////////////////////////////////
	// Function: public void moveBwd()
	/////////////////////////////////////////////////////////////////////
	//
	// Purpose: make the robot move backward for an amount of time. Timer.delay
	///////////////////////////////////////////////////////////////////// cannot be
	// used when controlling CANTalons due to them needing constant input.
	//
	// Arguments: none
	//
	// Returns: void
	//
	// Remarks: Initial Developement 1/20/18
	//
	/////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////

	public void moveBwd(double distance) {

		double time;

		double speed = -0.4;

		double right_speed;
		right_speed = corr_table.computeRightCommand(speed);

		time = Robot.auto_functions.determineTimeDelay(distance);

		frontLeft.set(speed);
		frontRight.set(right_speed);
		backRight.set(right_speed);
		backLeft.set(speed);
		Timer.delay(time);

		SmartDashboard.putNumber("Backwards Time", time);
		SmartDashboard.putNumber("Negative Distance", distance);
	}

	/////////////////////////////////////////////////////////////////////
	// Function:
	/////////////////////////////////////////////////////////////////////
	//
	// Purpose:
	//
	// Arguments:
	//
	// Returns:
	//
	// Remarks:
	//
	/////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////
	public void rotatePositive(double rot_spd) {

		frontRight.set(-rot_spd);
		backRight.set(-rot_spd);
		frontLeft.set(rot_spd);
		backLeft.set(rot_spd);

	}

	/////////////////////////////////////////////////////////////////////
	// Function:
	/////////////////////////////////////////////////////////////////////
	//
	// Purpose:
	//
	// Arguments:
	//
	// Returns:
	//
	// Remarks:
	//
	/////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////
	public void rotateNegative(double rot_spd) {

		frontRight.set(rot_spd);
		backRight.set(rot_spd);
		frontLeft.set(-rot_spd);
		backLeft.set(-rot_spd);

	}

	/////////////////////////////////////////////////////////////////////
	// Function: public void turnRight(double target,double rot_speed)
	/////////////////////////////////////////////////////////////////////
	//
	// Purpose: Rotates the robot CW. It is important to note that
	// actual rotation target is relative to the starting point
	// determined at program start. For example targets are
	// 0-target.
	//
	// Arguments:The target in degrees.
	//
	// Returns: void
	//
	// Remarks: Initial development 1/20/18.
	//
	/////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////
	public void turnRight(double target) { // target angle for gyro to reach

		// target angle for gyro to reach

		double angle; // current gyro angle
		double rot_speed;

		rot_speed = .17;

		angle = gyro.getAngle();
		SmartDashboard.putNumber("Gyro", gyro.getAngle());

		while (angle < target) {
			rotatePositive(rot_speed);
			angle = gyro.getAngle();
			SmartDashboard.putNumber("Gyro", gyro.getAngle());

			if ((angle < target + 15.0) && (angle > target - 15.0)) { // deadband,
																		// saying
																		// that
																		// if
																		// the
																		// actual
																		// angle
																		// is
																		// within
																		// 15
																		// degrees,
																		// stop
																		// motors
				break;
			}
			SmartDashboard.putNumber("Gyro", gyro.getAngle());
		}
		Timer.delay(.8);
		angle = gyro.getAngle();
		SmartDashboard.putNumber("Gyro", gyro.getAngle());
		rot_speed = .2;
		if (angle > target) {

			while (angle > target + 3.0) {
				rotateNegative(rot_speed);
				angle = gyro.getAngle();
				SmartDashboard.putNumber("Gyro", gyro.getAngle());
			}
		} else if (angle < target) {

			while (angle < target - 3.0) {
				rotatePositive(rot_speed);
				angle = gyro.getAngle();
				SmartDashboard.putNumber("Gyro", gyro.getAngle());
			}
		} else {
			; // do nothing
		}
		SmartDashboard.putNumber("Gyro", gyro.getAngle());
	}

	public int turnRightLoose(double target) { // target angle for gyro to reach

		int count;
		count = 0;

		// target angle for gyro to reach

		double angle; // current gyro angle
		double rot_speed;

		rot_speed = .2;

		angle = gyro.getAngle();
		SmartDashboard.putNumber("Gyro", gyro.getAngle());

		while (angle < target) {
			rotatePositive(rot_speed);
			angle = gyro.getAngle();
			SmartDashboard.putNumber("Gyro", gyro.getAngle());

			if ((angle < target + 20.0) && (angle > target - 20.0)) { // deadband,
																		// saying
																		// that
																		// if
																		// the
																		// actual
																		// angle
																		// is
																		// within
																		// 15
																		// degrees,
																		// stop
																		// motors
				allDrive.set(0);
				break;
			}
			SmartDashboard.putNumber("Gyro", gyro.getAngle());

			Timer.delay(.020);
			count++;
			if (count > 350) {
				count = 0;
				return -1;
			}

		}
		Timer.delay(.2);
		angle = gyro.getAngle();
		SmartDashboard.putNumber("Gyro", gyro.getAngle());
		rot_speed = .2;
		if (angle > target) {

			while (angle > target + 6.0) {
				rotateNegative(rot_speed);
				angle = gyro.getAngle();
				SmartDashboard.putNumber("Gyro", gyro.getAngle());
			}
			allDrive.set(0);
		} else if (angle < target) {

			while (angle < target - 6.0) {
				rotatePositive(rot_speed);
				angle = gyro.getAngle();
				SmartDashboard.putNumber("Gyro", gyro.getAngle());
			}
			allDrive.set(0);
		} else {
			; // do nothing
		}
		SmartDashboard.putNumber("Gyro", gyro.getAngle());

		return 0;
	}

	/////////////////////////////////////////////////////////////////////
	// Function: public void turnLeft(double target,double rot_speed)
	/////////////////////////////////////////////////////////////////////
	//
	// Purpose: Rotates the robot CCW. It is important to note that
	// actual rotation target is relative to the starting point
	// determined at program start. For example targets are
	// 0-target. Values do not need to be put in negative, as they will
	// be multiplied to become negative values.
	//
	// Arguments:The target in degrees.
	//
	// Returns: void
	//
	// Remarks: Initial development 1/20/18.
	//
	/////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////
	public void turnLeft(double target) { // target angle for gyro

		target *= -1.0;

		double rot_speed;

		rot_speed = .17;

		double angle; // current gyro angle

		angle = gyro.getAngle();
		SmartDashboard.putNumber("Gyro", gyro.getAngle());

		while (angle > target) {
			rotateNegative(rot_speed);
			angle = gyro.getAngle();
			SmartDashboard.putNumber("Gyro", gyro.getAngle());

			if ((angle < target + 15.0) && (angle > target - 15.0)) { // deadband,
																		// saying
																		// that
																		// if
																		// the
																		// actual
																		// angle
																		// is
																		// within
																		// 15
																		// degrees,
																		// stop
																		// motors
				allDrive.set(0);
				break;
			}
			SmartDashboard.putNumber("Gyro", gyro.getAngle());
		}
		Timer.delay(.8);
		angle = gyro.getAngle();
		SmartDashboard.putNumber("Gyro", gyro.getAngle());

		rot_speed = .2;
		if (angle < target) {

			while (angle < target - 4.0) {
				rotatePositive(rot_speed);
				angle = gyro.getAngle();
				SmartDashboard.putNumber("Gyro", gyro.getAngle());
			}
			allDrive.set(0);
		} else if (angle > target) {

			while (angle > target + 4.0) {
				rotateNegative(rot_speed);
				angle = gyro.getAngle();
				SmartDashboard.putNumber("Gyro", gyro.getAngle());
			}
			allDrive.set(0);
		} else {
			; // do nothing
		}
		SmartDashboard.putNumber("Gyro", gyro.getAngle());
	}

	public int turnLeftLoose(double target) { // target angle for gyro

		int count = 0;

		target *= -1.0;

		double rot_speed;

		rot_speed = .24;

		double angle; // current gyro angle

		angle = gyro.getAngle();
		SmartDashboard.putNumber("Gyro", gyro.getAngle());

		while (angle > target) {
			rotateNegative(rot_speed);
			angle = gyro.getAngle();
			SmartDashboard.putNumber("Gyro", gyro.getAngle());

			if ((angle < target + 20.0) && (angle > target - 20.0)) { // deadband,
																		// saying
																		// that
																		// if
																		// the
																		// actual
																		// angle
																		// is
																		// within
																		// 15
																		// degrees,
																		// stop
																		// motors
				allDrive.set(0);
				break;
			}
			SmartDashboard.putNumber("Gyro", gyro.getAngle());

			Timer.delay(.020);
			count++;
			if (count > 350) {
				count = 0;
				return -1;
			}
		}
		Timer.delay(.8);
		angle = gyro.getAngle();
		SmartDashboard.putNumber("Gyro", gyro.getAngle());

		rot_speed = .20;
		if (angle < target) {

			while (angle < target - 6.0) {
				rotatePositive(rot_speed);
				angle = gyro.getAngle();
				SmartDashboard.putNumber("Gyro", gyro.getAngle());
			}
			allDrive.set(0);
		} else if (angle > target) {

			while (angle > target + 6.0) {
				rotateNegative(rot_speed);
				angle = gyro.getAngle();
				SmartDashboard.putNumber("Gyro", gyro.getAngle());
			}
			allDrive.set(0);
		} else {
			; // do nothing
		}
		SmartDashboard.putNumber("Gyro", gyro.getAngle());
		return 0;
	}

	/////////////////////////////////////////////////////////////////////
	// Function: public int computeQuadrant(double x_pos,double y_pos)
	/////////////////////////////////////////////////////////////////////
	//
	// Purpose: Computes quadrant of joystick location based on x and
	// y joystick positions.
	//
	// Arguments:double x_pos, double y_pos
	//
	// Returns: An int representing the quadrant.
	//
	// Remarks: Note that the atan() function returns values between
	// PI/2 and -PI/2 in radians.
	//
	/////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////
	/*
	 * public int computeQuadrant(double x_pos, double y_pos) { int quadrant = 0; //
	 * double amplitude; double radians;
	 * 
	 * // Protect against divide by zero if (x_pos == 0.0) { return (-1); }
	 * 
	 * // amplitude = Math.sqrt(x_pos * x_pos + y_pos * y_pos); radians =
	 * Math.atan(y_pos / x_pos);
	 * 
	 * if ((radians < Math.PI / 2.0) && (radians >= 0.0)) { if ((x_pos > 0.0) &&
	 * (y_pos > 0.0)) { quadrant = 1; } else { quadrant = 3; } } else if ((radians
	 * >= (-Math.PI / 2.0)) && (radians <= 0.0)) { if (x_pos < 0.0) { quadrant = 2;
	 * } else if (y_pos < 0.0) { quadrant = 4; } } return (quadrant); }
	 * 
	 * ///////////////////////////////////////////////////////////////////// //
	 * Function: public void joystickDrive(double x_pos, double y_pos) {
	 * ///////////////////////////////////////////////////////////////////// // //
	 * Purpose: Method attempts to use manual manipulation of the // joystick and
	 * individual motor controllers to run the // robot in teleoperated mode. // //
	 * Arguments:double x_pos, double y_position (joystick positions) // // Returns:
	 * void // // Remarks: Joystick is extremely sensitive - more work required.
	 * 2/5/2018 // Trig functions are tricky to work with in terms of sign. //
	 * Adopted to use pythagorean theorem to compute y component // in that the
	 * result is always positive. // // Added local variables x_sq and amplitude_sq
	 * to minimize // the number of times we compute these values. //
	 * /////////////////////////////////////////////////////////////////////
	 * ///////////////////////////////////////////////////////////////////// public
	 * void joystickDrive(double x_pos, double y_pos) {
	 * 
	 * int quadrant = 0;
	 * 
	 * double amplitude; double amplitude_sq; double x_sq;
	 * 
	 * double left_speed; double right_speed;
	 * 
	 * amplitude = Math.sqrt(x_pos * x_pos + y_pos * y_pos);
	 * 
	 * if (amplitude < 0.15) return; // deadband
	 * 
	 * amplitude_sq = amplitude * amplitude; x_sq = x_pos * x_pos;
	 * 
	 * // determine quadrant quadrant = computeQuadrant(x_pos, y_pos);
	 * 
	 * // Note to decrease sensitivity speeds are squared. switch (quadrant) { case
	 * 1: // straight ahead or curve to right
	 * 
	 * left_speed = amplitude; left_speed *= left_speed; right_speed =
	 * Math.sqrt(amplitude_sq - x_sq); right_speed *= right_speed;
	 * 
	 * frontLeft.set(left_speed); backLeft.set(left_speed);
	 * frontRight.set(right_speed); backRight.set(right_speed); break;
	 * 
	 * case 2: // straight ahead or curve to right
	 * 
	 * right_speed = amplitude; right_speed *= right_speed; left_speed =
	 * Math.sqrt(amplitude_sq - x_sq); left_speed *= left_speed;
	 * 
	 * frontLeft.set(left_speed); backLeft.set(left_speed);
	 * frontRight.set(right_speed); backRight.set(right_speed); break;
	 * 
	 * case 3: // straight back or curve to left right_speed = amplitude;
	 * right_speed *= right_speed; left_speed = Math.sqrt(amplitude_sq - x_sq);
	 * left_speed *= left_speed;
	 * 
	 * frontLeft.set(left_speed); backLeft.set(left_speed);
	 * frontRight.set(-right_speed); backRight.set(-right_speed); break;
	 * 
	 * case 4: // straight back or curve to right left_speed = amplitude; left_speed
	 * *= left_speed; right_speed = Math.sqrt(amplitude_sq - x_sq); right_speed *=
	 * right_speed;
	 * 
	 * frontLeft.set(-left_speed); backLeft.set(-left_speed);
	 * frontRight.set(right_speed); backRight.set(right_speed); break;
	 * 
	 * }
	 * 
	 * }
	 */
	// Standard method that seems to work very well. Need to revisit
	// original working program to see implementation.
	public void joystickArcadeDrive() {

		Robot.speedCap = SmartDashboard.getNumber(Robot.speedChoice, 100) / 100;
		SmartDashboard.putNumber(Robot.speedChoice, Robot.speedCap * 100);

		double stick0 = Robot.stick.getRawAxis(0);
		double stick1 = Robot.stick.getRawAxis(1);

		// old stuff that I (Elliott) just commented out

		// stick0 = Robot.stick.getRawAxis(0);
		// stick1 = Robot.stick.getRawAxis(1);

		// stick0 = Robot.stick.getRawAxis(0) * Robot.speedFraction;
		// stick1 = Robot.stick.getRawAxis(1) * Robot.speedFraction;

		// THE NON NEGATED VERSION OF THESE 2 IS PROBABLY THE ONE THAT IS GOING TO WORK
		// (CORRECTLY)!

		// drive.arcadeDrive(-stick0 * -speedCap, stick1 * -speedCap);

		// drive.arcadeDrive(-stick0 * Robot.speedCap, stick1 * Robot.speedCap);

		drive.arcadeDrive(stick0 * Robot.speedCap * .65, -stick1 * Robot.speedCap * .8);

		// if ((Robot.stick.getRawButton(8) == true) && (invertDrive == false)) {
		//
		// invertDrive = true;
		//
		// Timer.delay(.1);
		//
		// } else if ((Robot.stick.getRawButton(8) == true) && (invertDrive == true)) {
		//
		// invertDrive = false;
		//
		// Timer.delay(.1);
		// }
		//
		// if (invertDrive == true) {
		// stick0 = Robot.stick.getRawAxis(0) * Robot.speedFraction;
		// stick1 = -Robot.stick.getRawAxis(1) * Robot.speedFraction;
		// } else if (invertDrive == false) {
		// stick0 = Robot.stick.getRawAxis(0) * Robot.speedFraction;
		// stick1 = Robot.stick.getRawAxis(1) * Robot.speedFraction;
		// }

		// if (invertDrive == true) {
		// stick0 = Robot.stick.getRawAxis(0) * .65;
		// stick1 = -Robot.stick.getRawAxis(1) * .8;
		// } else if (invertDrive == false) {
		// stick0 = Robot.stick.getRawAxis(0) * .65;
		// stick1 = Robot.stick.getRawAxis(1) * .8;
		// }

		// if (Robot.stick.getRawButton(1) == true) {
		// zoop = true; // zoop mean FAST
		//
		// Timer.delay(.1);
		// } else {
		// zoop = false;
		// }
		//
		// if (zoop == true) {
		// stick0 = Robot.stick.getRawAxis(0);
		// stick1 = Robot.stick.getRawAxis(1);
		// } else {
		// stick0 = Robot.stick.getRawAxis(0) * .65;
		// stick1 = Robot.stick.getRawAxis(1) * .8;
		// }

		// drive.arcadeDrive(stick0, -stick1);
	}
}

/////////////////////////////////////////////////////////////////////
// Function:
/////////////////////////////////////////////////////////////////////
//
// Purpose:
//
// Arguments:
//
// Returns:
//
// Remarks:
//
/////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////
