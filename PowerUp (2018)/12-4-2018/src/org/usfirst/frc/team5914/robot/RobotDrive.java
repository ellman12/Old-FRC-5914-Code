package org.usfirst.frc.team5914.robot;

import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.StatusFrameEnhanced;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/////////////////////////////////////////////////////////////////////
//  Class:  RobotDrive
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
public class RobotDrive {

	double error;
	double left_speed;
	double right_speed;
	double max_speed;

	double wheel_diameter = 6.0;
	double speed = 0.8;

	public int enc_rel_pos_1;
	public int enc_rel_pos_2;
	public int enc_update_count;
	public int auto_init;

	// enc_rel_pos_1 and enc_rel_pos_2 are relative to some starting point

	public int initPosition1;
	public int initPosition2;

	public int endPosition1;
	public int currentPosition;

	public double initAngle;

	// initPosition1 and initPosition2 are starting values

	public static double speedCap = 1;

	Joystick stick;

	WPI_TalonSRX frontLeft, frontRight, backLeft, backRight;

	SpeedControllerGroup leftDrive;
	SpeedControllerGroup rightDrive;
	SpeedControllerGroup allDrive;

	DifferentialDrive diff_drive;

	ADXRS450_Gyro gyro;

	final int gyroChannel = 0;

	// Constructor
	/////////////////////////////////////////////////////////////////////
	// Function: public RobotDrive()
	/////////////////////////////////////////////////////////////////////
	//
	// Purpose: Constructor for the class. Creates the devices used to
	// control the robot drive system.
	//
	// Arguments: void
	//
	// Returns: void
	//
	// Remarks:
	//
	/////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////
	public RobotDrive() {
		// Speed.addDefault("Speed Choice", speedChoice);
		// SmartDashboard.putData("speedChoice", Speed);
		//
		// ENC.addDefault("Encoder Position", encoderPOS);
		// SmartDashboard.putNumber("ENC_POS", enc_rel_pos_1);

		stick = new Joystick(0);

		frontLeft = new WPI_TalonSRX(3);
		backLeft = new WPI_TalonSRX(1);
		frontRight = new WPI_TalonSRX(2);
		backRight = new WPI_TalonSRX(4);

		leftDrive = new SpeedControllerGroup(frontLeft, backLeft);
		rightDrive = new SpeedControllerGroup(frontRight, backRight);
		allDrive = new SpeedControllerGroup(frontLeft, frontRight, backLeft, backRight);

		diff_drive = new DifferentialDrive(leftDrive, rightDrive);

		gyro = new ADXRS450_Gyro();

		// Initialize the gyro, calibrate, and reset to zero.
		gyro.calibrate();
		gyro.reset();

		// Making the motors all turn the right way
		// frontLeft.setInverted(true);
		frontRight.setInverted(false);
		// backLeft.setInverted(true);
		backRight.setInverted(false);
		
		backLeft.setSafetyEnabled(false);
		backRight.setSafetyEnabled(false);
		frontRight.setSafetyEnabled(false);
		backRight.setSafetyEnabled(false);

		// There are two encoders, one set to the Talon controller for the motor
		// on the rear left side and one for the Talon controller on the rear right
		// side.
		backLeft.setStatusFramePeriod(StatusFrameEnhanced.Status_2_Feedback0, 1, 30);
		backLeft.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0, 30);

		backRight.setStatusFramePeriod(StatusFrameEnhanced.Status_2_Feedback0, 1, 30);
		backRight.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0, 30);

		// We want to establish an initial encoder reading. This will enable reseting
		// encoder position to zero when we start moving. We use absolute values to
		// make the subsequent subtraction more easily interpreted.
		initPosition1 = backLeft.getSelectedSensorPosition(1);

		initPosition1 = Math.abs(initPosition1);

		initPosition2 = frontRight.getSelectedSensorPosition(2);

		initPosition2 = Math.abs(initPosition2);

		enc_update_count = 0;

		// For reference we output to the console
		System.out.println("initPosition = " + initPosition1 + " initPosition2 = " + initPosition2);

		// Initialize the motor drive (left, right) speeds and encoder error.
		error = 0;
		left_speed = 0.4;
		right_speed = 0.4;
		max_speed = 0.5;

	}

	// Constructor
	/////////////////////////////////////////////////////////////////////
	// Function: public RobotDrive(int ch_fl,int ch_fr,int ch_bl,int ch_br)
	/////////////////////////////////////////////////////////////////////
	//
	// Purpose: Constructor for the class. Creates the devices used to
	// control the robot drive system.
	//
	// Arguments: Accepts the channel numbers for the four Talon_SRX
	// motor controllers.
	//
	// Returns: void
	//
	// Remarks:
	//
	/////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////
	public RobotDrive(int ch_fl, int ch_fr, int ch_bl, int ch_br) {
		// Speed.addDefault("Speed Choice", speedChoice);
		// SmartDashboard.putData("speedChoice", Speed);
		//
		// ENC.addDefault("Encoder Position", encoderPOS);
		// SmartDashboard.putNumber("ENC_POS", enc_rel_pos_1);

		stick = new Joystick(0);

		frontLeft = new WPI_TalonSRX(ch_fl);
		backLeft = new WPI_TalonSRX(ch_bl);
		frontRight = new WPI_TalonSRX(ch_fr);
		backRight = new WPI_TalonSRX(ch_br);

		leftDrive = new SpeedControllerGroup(frontLeft, backLeft);
		rightDrive = new SpeedControllerGroup(frontRight, backRight);
		allDrive = new SpeedControllerGroup(frontLeft, frontRight, backLeft, backRight);

		diff_drive = new DifferentialDrive(leftDrive, rightDrive);

		gyro = new ADXRS450_Gyro();

		// Initialize the gyro, calibrate, and reset to zero.
		gyro.calibrate();
		gyro.reset();

		// Making the motors all turn the right way
		// frontLeft.setInverted(true);
		frontRight.setInverted(false);
		// backLeft.setInverted(true);
		backRight.setInverted(false);

		// There are two encoders, one set to the Talon controller for the motor
		// on the rear left side and one for the Talon controller on the rear right
		// side.
		backLeft.setStatusFramePeriod(StatusFrameEnhanced.Status_2_Feedback0, 1, 30);
		backLeft.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0, 30);

		backRight.setStatusFramePeriod(StatusFrameEnhanced.Status_2_Feedback0, 1, 30);
		backRight.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0, 30);

		// We want to establish an initial encoder reading. This will enable reseting
		// encoder position to zero when we start moving. We use absolute values to
		// make the subsequent subtraction more easily interpreted.
		initPosition1 = backLeft.getSelectedSensorPosition(1);

		initPosition1 = Math.abs(initPosition1);

		initPosition2 = frontRight.getSelectedSensorPosition(2);

		initPosition2 = Math.abs(initPosition2);

		enc_update_count = 0;

		// For reference we output to the console
		System.out.println("initPosition = " + initPosition1 + " initPosition2 = " + initPosition2);

		// Initialize the motor drive (left, right) speeds and encoder error.
		error = 0;
		left_speed = 0.4;
		right_speed = 0.4;
		max_speed = 0.5;

	}

	/////////////////////////////////////////////////////////////////////
	// Function: public int driveFwd(double distance)
	/////////////////////////////////////////////////////////////////////
	//
	// Purpose: Constructor the the class. Creates the devices used to
	// control the robot drive system.
	//
	// Arguments:double distance, The distance to be traveled in inches.
	//
	// Returns: An int representing overshoot/undershoot of the movement
	// in encoder counts.
	//
	// Remarks: It is assumed that the first time a movement is attempted
	// that it is a forward movement. The first movement appears
	// to determine the direction of the encoder counts.
	//
	/////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////
	public int driveFwd(double distance) {
		int loop_count = 0;
		int overshoot;
		int counts;
		int initial_position;
		int current_position;
		int target;

		double fraction;
		double heading;

		// Determine where we are pointing - we want to maintain this heading during
		// this
		// forward movement.
		heading = gyro.getAngle();

		// Read encoder #1, get the initial encoder position and assign it to
		// the current position. Calculate the number of encoder counts
		// necessary to reach the final destination (target).
		initial_position = Math.abs(backLeft.getSelectedSensorPosition(1));
		current_position = initial_position;
		counts = calcCounts_SAE(wheel_diameter, distance);
		target = initial_position + counts;

		// fraction starts out as equal to 1.0 and decreases as we approach the target.
		// fraction is counts remaining divided by total counts.
		fraction = ((double) (target - current_position) / (double) (target - initial_position));

		System.out
				.println("initial_position = " + initial_position + " target = " + target + " fraction = " + fraction);

		// We attempt a bit of proportional control as we approach the target. We want
		// to slow down so that we don't overshoot. These fractions appear to work.
		// We drive at high speed for 80% of the distance and then slow. On carpet this
		// seemed to work very well for distance of 10 feet.
		// We want braking enabled.
		// We also need to put a timer within the while() loop to provide an escape in
		// the
		// event that the system gets lost during autonomous requiring a restart of the
		// program.
		while (current_position < target) {
			if (fraction > 0.30) {
				moveFwd(speed, heading);
			} else {
				moveFwd(0.28, heading);
			}
			Timer.delay(0.01);
			// We don't want to stay longer than we have to. Assuming
			// that the 10 msec is reasonably accurate we limit the
			// move to 5 seconds for starters.
			loop_count++;
			if ((loop_count % 10) == 0) {
				// Provide periodic status
				System.out.println(
						"current_position = " + current_position + " target = " + target + " fraction = " + fraction);
			}
			if (loop_count == 500)
				break; // escape clause
			current_position = Math.abs(backLeft.getSelectedSensorPosition(1));
			fraction = ((double) (target - current_position) / (double) (target - initial_position));
		}

		// stop movement, compute error (overshoot or undershoot)
		diff_drive.arcadeDrive(0, 0);
		overshoot = current_position - target;
		System.out.println("overshoot = " + overshoot);

		return (overshoot);
	}
	
	public int addNumbers(int x, int y) {
		return(x + y);
	}

	/////////////////////////////////////////////////////////////////////
	// Function: public int driveBwd(double distance)
	/////////////////////////////////////////////////////////////////////
	//
	// Purpose: Constructor the the class. Creates the devices used to
	// control the robot drive system.
	//
	// Arguments:double distance, The distance to be traveled in inches.
	//
	// Returns: An int representing overshoot/undershoot of the movement
	// in encoder counts.
	//
	// Remarks: It is assumed that the first time a movement is attempted
	// that it is a forward movement. The first movement appears
	// to determine the direction of the encoder counts. What
	// we observed during the last practice was that when we
	// started the program and the first motion was backwards
	// we saw increasing encoder counts. Assumed here is that
	// if the first movement on program start is forward then
	// reversing direction should create decreasing encoder
	// counts.
	// A possible option is to use the other encoder for reverse
	// motion. We shall see..
	//
	/////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////
	public int driveBwd(double distance) {
		int loop_count = 0;
		int overshoot;
		int counts;
		int initial_position;
		int current_position;
		int target;

		double fraction;
		double heading;

		// Determine where we are pointing - we want to maintain this heading during
		// this
		// forward movement.
		heading = gyro.getAngle();

		// Read encoder #1, get the initial encoder position and assign it to
		// the current position. Calculate the number of encoder counts
		// necessary to reach the final destination (target).
		initial_position = Math.abs(backLeft.getSelectedSensorPosition(1));
		current_position = initial_position;
		counts = calcCounts_SAE(wheel_diameter, distance);
		target = initial_position - counts;

		// fraction starts out as equal to 1.0 and decreases as we approach the target.
		// fraction is counts remaining divided by total counts.
		fraction = ((double) (target - current_position) / (double) (target - initial_position));

		System.out
				.println("initial_position = " + initial_position + " target = " + target + " fraction = " + fraction);

		// We attempt a bit of proportional control as we approach the target. We want
		// to slow down so that we don't overshoot. These fractions appear to work.
		// We drive at high speed for 80% of the distance and then slow. On carpet this
		// seemed to work very well for distance of 10 feet.
		// We want braking enabled.
		// We also need to put a timer within the while() loop to provide an escape in
		// the
		// event that the system gets lost during autonomous requiring a restart of the
		// program.

		// Need to test and determine sign of inequality. We had the curious behavior of
		// increasing encoder counts when moving backwards. It could have been due to
		// the fact that the first motion was backwards in our "spaghetti code". We will
		// Need to play around with the signs to get it working right.
		while (current_position < target) {
			if (fraction > 0.20) {
				moveBwd(speed, heading);
			} else {
				moveBwd(0.4, heading);
			}
			Timer.delay(0.01);
			// We don't want to stay longer than we have to. Assuming
			// that the 10 msec is reasonably accurate we limit the
			// move to 5 seconds for starters.
			loop_count++;
			if ((loop_count % 50) == 0) {
				// Provide periodic status
				System.out.println(
						"current_position = " + current_position + " target = " + target + " fraction = " + fraction);
			}
			if (loop_count == 500)
				break; // escape clause
		}

		// stop movement, compute error (overshoot or undershoot)
		diff_drive.arcadeDrive(0, 0);
		overshoot = current_position - target;
		System.out.println("overshoot = " + overshoot + " fraction = " + fraction);

		return (overshoot);
	}

	///////////////////////////////////////////////////////////////////////////
	// Function: void moveFwd(double speed,double target)
	///////////////////////////////////////////////////////////////////////////
	//
	// Purpose: Uses on_board gyro to drive the robot straight forward
	// given a target angle as an argument.
	// Requires use of the arcadeDrive function with the first
	// argument being the forward speed and the second being
	// the turn applied.
	//
	// Arguments: double speed. Must be between -1.0 and 1.0.
	// double angle - the target angle.
	//
	// Returns: void
	//
	// Remarks: 11/25/18: Correct polarity of movement TBD
	//
	////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////
	public void moveFwd(double speed, double target) {

		double corr = 0.2;
		double angle = 0;
		double delta; // The difference between the target and measured angle

		angle = gyro.getAngle();

		delta = angle - target;

		// According to the documentation for DifferentialDrive.arcadeDrive(speed,turn)
		// the arguments are squared to accomodate lower drive speeds. If for example
		// the gain coefficient is 0.05 and the angle error is 5 degrees, the turning
		// argument would be 0.25*0.25 = 0.0625. This is a pretty slow correction.
		// We needed a larger correction factor - trying 0.2 for now. The range for
		// the turn is -1.0 to 1.0. Positive values are said to turn clockwise,
		// negatives counterclockwise.
		diff_drive.arcadeDrive(speed, -corr * delta);

		// System.out.println(" target = " + target + " angle = " + angle + " delta = "
		// + delta);

	}

	///////////////////////////////////////////////////////////////////////////
	// Function: void moveBwd()
	///////////////////////////////////////////////////////////////////////////
	//
	// Purpose: Uses on_board gyro to drive the robot straight backward
	// given a target angle as an argument.
	// Requires use of the arcadeDrive function with the first
	// argument being the forward speed and the second being
	// the turn applied.
	//
	// Arguments: double speed. Must be between -1.0 and 1.0.
	// double angle - the target angle.
	//
	// Returns: void
	//
	// Remarks:
	//
	////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////
	public void moveBwd(double speed, double target) {

		double corr = 0.2;
		double angle = 0;
		double delta; // The difference between the target and measured angle

		angle = gyro.getAngle();

		delta = angle - target;

		// According to the documentation for DifferentialDrive.arcadeDrive(speed,turn)
		// the arguments are squared to accomodate lower drive speeds. If for example
		// the gain coefficient is 0.05 and the angle error is 5 degrees, the turning
		// argument would be 0.25*0.25 = 0.0625. This is a pretty slow correction.
		// We needed a larger correction factor - trying 0.2 for now. The range for
		// the turn is -1.0 to 1.0. Positive values are said to turn clockwise,
		// negatives counterclockwise.
		diff_drive.arcadeDrive(-speed, -corr * delta);

		// System.out.println(" target = " + target + " angle = " + angle + " delta = "
		// + delta);

	}

	///////////////////////////////////////////////////////////////////////////
	// Function: void calcCounts_SAE(double diameter,double distance)
	///////////////////////////////////////////////////////////////////////////
	//
	// Purpose: Given the wheel radius in inches and the distance to be
	// traveled in feet, this function calculates the encoder
	// counts required to travel that distance.
	//
	// Arguments: double speed. Must be between -1.0 and 1.0. For this
	// first try we will limit it internally to 0.6.
	// double angle - the target angle.
	//
	// Returns: void
	//
	// Remarks:
	//
	////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////
	public int calcCounts_SAE(double diameter, double distance) {
		int enc_counts;
		double inches;
		double counts_per_inch;
		double circumference;

		inches = distance * 12.0;
		circumference = Math.PI * diameter;

		counts_per_inch = 4095.0 / circumference;

		enc_counts = (int) ((counts_per_inch * inches) + 0.5);

		return (enc_counts);

	}

	///////////////////////////////////////////////////////////////////////////
	// Function: void calcCounts_Metric(double radius,double distance)
	///////////////////////////////////////////////////////////////////////////
	//
	// Purpose: Given the wheel radius in cm and the distance to be
	// traveled in meters, this function calculates the encoder
	// counts required to travel that distance.
	//
	// Arguments: double radius (in cm).
	// double distance (in meters).
	//
	// Returns: void
	//
	// Remarks:
	//
	////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////
	public int calcCounts_Metric(double diameter, double distance) {
		int enc_counts;
		double distance_cm;
		double counts_per_cm;
		double circumference;

		distance_cm = distance * 100.0; // converts from meters to centimeters
		circumference = Math.PI * diameter;

		counts_per_cm = 4095.0 / circumference; // 12-bits, i.e., 4095 counts per revolution

		enc_counts = (int) ((counts_per_cm * distance_cm) + 0.5); // common rounding method

		return (enc_counts);

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

	// Alternate method
	public void rotatePosArcade(double rot_spd) {
		diff_drive.arcadeDrive(0, rot_spd);
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

	// Alternate method
	public void rotateNegArcade(double rot_spd) {
		diff_drive.arcadeDrive(0, -rot_spd);
	}

	// Heading should be between 0 and 359.99 degrees.
	// Algorithm will compute the shortest path to the target
	// heading. Note that if we are at 270 and the target is
	// 20 degrees. The shortest path will be to rotate clockwise.
	// The gyro will indicate 380 degrees.
	public double turn2Heading(double heading, double rot_speed) {
		// Rotation Direction Flags
		boolean ccw = false;
		boolean cw = false;

		double init_delta; // Initial angle difference
		double angle; // instataneous angle measurement
		double delta = 0;
		double fraction;

		angle = gyro.getAngle();
		init_delta = heading - angle;

		if (init_delta > 0.0) {
			if (init_delta > 180) {
				init_delta -= 180;
				ccw = true;
				delta = init_delta;
			}
			while (delta > 0.0) {
				if (delta > 5.0) {
					diff_drive.arcadeDrive(0, -rot_speed);
				} else {
					diff_drive.arcadeDrive(0, -0.5 * rot_speed);
				}
				angle = gyro.getAngle();
				delta = heading - angle;
				if (delta > 180.0)
					delta -= 180.0;
				Timer.delay(0.01);
			}
			diff_drive.arcadeDrive(0, 0);
			return (delta);
		} else if (init_delta <= 0.0) {
			if (init_delta < -180.0) {
				init_delta += 180;
				cw = true;
				delta = init_delta;
			}
			while (delta < 0.0) {
				if (delta < -5.0) {
					diff_drive.arcadeDrive(0, rot_speed);
				} else {
					diff_drive.arcadeDrive(0, 0.5 * rot_speed);
				}
				angle = gyro.getAngle();
				delta = heading - angle;
				if (delta < -180.0)
					delta += 180.0;
				Timer.delay(0.01);
			}
			diff_drive.arcadeDrive(0, 0);
			return (delta);
		}
		return delta;
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
	// Remarks: Initial development 1/20/18. Modified for a simpler
	// configuration 12/1/2018. Note that we want motor braking
	// enabled. Added escape count to exit loop after a certain
	// time when it doesn't reach the target. Untested.
	//
	/////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////
	public void turnRight(double target) { // target angle for gyro to reach

		int escape_count = 0;
		double angle; // current gyro angle
		double rot_speed;

		rot_speed = .4;

		angle = gyro.getAngle();
		SmartDashboard.putNumber("Gyro", angle);

		while (angle < target) {
			rotatePositive(rot_speed);
			angle = gyro.getAngle();
			SmartDashboard.putNumber("Gyro", angle);

			if (angle > (target - 15.0)) {
				rot_speed /= 2.0;
			}
			if (angle > (target - 5.0)) {
				rot_speed /= 2.0;
			}
			Timer.delay(0.01);
			escape_count++;
			if (escape_count == 500)
				break;
		}
		allDrive.set(0);
		angle = gyro.getAngle();
		SmartDashboard.putNumber("Gyro", angle);
	}

	/////////////////////////////////////////////////////////////////////
	// Function: public void turnRight_Arcade(double target,double rot_speed)
	/////////////////////////////////////////////////////////////////////
	//
	// Purpose: Rotates the robot CW the amount of degrees specified
	// by target. It is important to note that actual rotation target
	// is relative to the starting point determined at program start.
	// For example targets are 0-target.
	//
	// Arguments:The target in degrees.
	//
	// Returns: void
	//
	// Remarks: Initial development 12/11/18. Uses Arcade drive to
	// rotate the robot. Note that we want motor braking
	// enabled. Added escape count to exit loop after a certain
	// time when it doesn't reach the target. Untested.
	//
	/////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////
	public void turnRight_Arcade(double target, double rot_speed) {
		int escape_count = 0;
		double angle; // current gyro angle

		angle = gyro.getAngle();
		SmartDashboard.putNumber("Gyro", angle);

		while (angle < target) {
			rotatePosArcade(rot_speed);
			angle = gyro.getAngle();
			SmartDashboard.putNumber("Gyro", angle);

			if (angle > (target - 15.0)) {
				rot_speed /= 2.0;
			}
			if (angle > (target - 5.0)) {
				rot_speed /= 2.0;
			}
			Timer.delay(0.01);
			escape_count++;
			if (escape_count == 500)
				break;
		}
		diff_drive.arcadeDrive(0, 0);
		angle = gyro.getAngle();
		SmartDashboard.putNumber("Gyro", angle);
	}

	public void turnRightLoose(double target) { // target angle for gyro to reach

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

			if (angle > (target - 20.0)) {

				allDrive.set(0);
				break;
			}
			SmartDashboard.putNumber("Gyro", gyro.getAngle());
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
	public void turnLeft(double target) { // target angle for gyro to reach

		int escape_count = 0;
		double angle; // current gyro angle
		double rot_speed;

		target *= -1.0;
		rot_speed = .4;

		angle = gyro.getAngle();
		SmartDashboard.putNumber("Gyro", angle);

		while (angle > target) {
			rotateNegative(rot_speed);
			angle = gyro.getAngle();
			SmartDashboard.putNumber("Gyro", angle);

			if (angle > (target + 15.0)) {
				rot_speed /= 2.0;
			}
			if (angle > (target + 5.0)) {
				rot_speed /= 2.0;
			}
			Timer.delay(0.01);
			escape_count++;
			if (escape_count == 500)
				break;
		}
		allDrive.set(0);
		angle = gyro.getAngle();
		SmartDashboard.putNumber("Gyro", angle);
	}

	/////////////////////////////////////////////////////////////////////
	// Function: public void turnLeft_Arcade(double target,double rot_speed)
	/////////////////////////////////////////////////////////////////////
	//
	// Purpose: Rotates the robot CCW the amount of degrees specified
	// by target. It is important to note that actual rotation target
	// is relative to the starting point determined at program start.
	// For example targets are 0-target.
	//
	// Arguments:The target in degrees.
	//
	// Returns: void
	//
	// Remarks: Initial development 12/11/18. Uses Arcade drive to
	// rotate the robot. Note that we want motor braking
	// enabled. Added escape count to exit loop after a certain
	// time when it doesn't reach the target. Untested.
	//
	/////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////
	public void turnLeft_Arcade(double target, double rot_speed) {
		int escape_count = 0;
		double angle; // current gyro angle

		angle = gyro.getAngle();
		SmartDashboard.putNumber("Gyro", angle);

		while (angle < target) {
			rotateNegArcade(rot_speed);
			angle = gyro.getAngle();
			SmartDashboard.putNumber("Gyro", angle);

			if (angle > (target + 15.0)) {
				rot_speed /= 2.0;
			}
			if (angle > (target + 5.0)) {
				rot_speed /= 2.0;
			}
			Timer.delay(0.01);
			escape_count++;
			if (escape_count == 500)
				break;
		}
		diff_drive.arcadeDrive(0, 0);
		angle = gyro.getAngle();
		SmartDashboard.putNumber("Gyro", angle);
	}

	public void turnLeftLoose(double target) { // target angle for gyro

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
	}

}