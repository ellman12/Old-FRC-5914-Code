/////////////////////////////////////////////////////////////////////
//  File:  RobotDrive.java
/////////////////////////////////////////////////////////////////////
//
//Purpose:  Defines the class responsible for robot movement forward
//          and backward, left and right turns.
//
//Programmers:  Elliott DuCharme and Brady Augedahl for Caledonia
//          Robotic Warriors team 5914
//
//Revision Date: 1/22/2019
//
//Remarks: Heavy use is made of the Talon SRX motor controller and
//         CTE magnetic encoder.  Also the ADI gyroscope(ADXRS450).
//		   (In Robot.java) Eliminated watchdog errors and useless gyro resetting
//		   that caused 5 second delay.
//
//         1/22/2019:  Ported this over to the first VS program.
//         Opened the source file from the Eclipse program and it
//         formatted without issue to the VS editor???
//
//         5/8/2019:  Completely re-written for the NEOS motors and
//         internal encoders used on the 2019 season robot.
//
/////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////
package frc.robot;

import edu.wpi.first.wpilibj.Timer;

//import com.ctre.phoenix.motorcontrol.FeedbackDevice;
//import com.ctre.phoenix.motorcontrol.StatusFrameEnhanced;
//import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.revrobotics.CANEncoder;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;

/////////////////////////////////////////////////////////////////////
//  Class:  RobotDrive
/////////////////////////////////////////////////////////////////////
//
//Purpose: Defines the parameters used to drive the robot.
//
//
//Remarks:  FRC iterative robot template.
//
/////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////
public class RobotDrive {

	// Fixed parameters for conversion of distance to encoder counts
	final double WHEEL_DIAMETER = 8.0;
	final double INCHES_PER_FOOT = 12.0;
	final double CM_PER_METER = 100.0;

	// Encoders are now on the motors (NEOS). Output from the
	// encoder in this case is 1.0. A gear reduction of 16:1 implies
	// 16.0 per revolution of the output shaft. The precision of this
	// is 42 counts per motor shaft revolution times 16 of the gear
	// reduction = 1/672. An eight inch wheel diameter implies a distance
	// traveled of PI*8.0 = 25.17 inches. Distance resolution of the
	// encoder/gearbox comination is 25.17/672 = 0.037 inches/count. Should be
	// good enough.
	// However the output when reading the encoder function
	// is 1.0 for each revolution of the motor, or 16 for one revolution
	// of the output shaft. This implies that the inch to output
	// conversion is 25.17/16.0 or 1.572 inches per unit output
	final double ENCODER_RESOLUTION = 1.572; // inches per output value

	// Fixed parameters for driveFwd(...)/driveBwd(...)
	final double START_SPEED = 0.6;
	final double BRAKE_SPEED = 0.3;
	final double BRAKE_FRACTION = 0.25;

	// Fixed parameters for console updates and while() loop escapes
	final int ENC_CONSOLE_UPDATE = 20;
	final int ENC_LOOP_ESCAPE = 250;
	final int GYRO_CONSOLE_UPDATE = 20;
	final int GYRO_LOOP_ESCAPE = 200;

	// Fixed parameters for gyro operation. Specified here to facilitate
	// changes without confusion in the various functions using these
	// variables.
	final double ROT_SPEED = 0.5; // Starting rotation speed for turning
	// As we approach the target we reduce the speed by this factor
	final double ROT_ATTEN = 1.5;
	// proximity (in degrees) to target angle stage 1 rotation attenuation rate
	final double ANGL_PROX_1 = 25.0;
	// proximity (in degrees) to target angle stage 2 rotation attenuation rate
	final double ANGL_PROX_2 = 5.0;

	// enc_rel_pos_1 and enc_rel_pos_2 are relative to some starting point
	public static double position1;
	public double initPosition1;
	public double initPosition2;

	public double initAngle;
	public static double angle;

	Joystick stick;

	CANSparkMax frontLeft, frontRight, backLeft, backRight;
	public static CANEncoder left_enc, right_enc;

	SpeedControllerGroup leftDrive;
	SpeedControllerGroup rightDrive;
	SpeedControllerGroup allDrive;

	DifferentialDrive diff_drive;

	public static ADXRS450_Gyro gyro;

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

		stick = new Joystick(0);

		frontLeft = new CANSparkMax(3, MotorType.kBrushless);
		backLeft = new CANSparkMax(1, MotorType.kBrushless);
		frontRight = new CANSparkMax(2, MotorType.kBrushless);
		backRight = new CANSparkMax(4, MotorType.kBrushless);

		left_enc = new CANEncoder(frontLeft);
		right_enc = new CANEncoder(frontRight);

		frontLeft.setIdleMode(IdleMode.kBrake);
		backLeft.setIdleMode(IdleMode.kBrake);
		frontRight.setIdleMode(IdleMode.kBrake);
		backRight.setIdleMode(IdleMode.kBrake);

		frontLeft.setMotorType(MotorType.kBrushless);
		backLeft.setMotorType(MotorType.kBrushless);
		frontRight.setMotorType(MotorType.kBrushless);
		backRight.setMotorType(MotorType.kBrushless);

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

		// We want to establish an initial encoder reading. This will enable reseting
		// encoder position to zero when we start moving. We use absolute values to
		// make the subsequent subtraction more easily interpreted.
		left_enc.setPosition(0.0);
		initPosition1 = left_enc.getPosition();
		position1 = initPosition1;

		System.out.println("RobotDrive Left Encoder Initial Position = " + initPosition1);

		right_enc.setPosition(0.0);
		initPosition2 = right_enc.getPosition();

		System.out.println("RobotDrive Right Encoder Initial Position = " + initPosition2);

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

		stick = new Joystick(0);

		frontLeft = new CANSparkMax(ch_fl, MotorType.kBrushless);
		backLeft = new CANSparkMax(ch_bl, MotorType.kBrushless);
		frontRight = new CANSparkMax(ch_fr, MotorType.kBrushless);
		backRight = new CANSparkMax(ch_br, MotorType.kBrushless);

		left_enc = new CANEncoder(frontLeft);
		right_enc = new CANEncoder(frontRight);

		frontLeft.setIdleMode(IdleMode.kBrake);
		backLeft.setIdleMode(IdleMode.kBrake);
		frontRight.setIdleMode(IdleMode.kBrake);
		backRight.setIdleMode(IdleMode.kBrake);

		frontLeft.setMotorType(MotorType.kBrushless);
		backLeft.setMotorType(MotorType.kBrushless);
		frontRight.setMotorType(MotorType.kBrushless);
		backRight.setMotorType(MotorType.kBrushless);

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

		// We want to establish an initial encoder reading. This will enable reseting
		// encoder position to zero when we start moving. We use absolute values to
		// make the subsequent subtraction more easily interpreted.
		left_enc.setPosition(0.0);
		initPosition1 = left_enc.getPosition();
		position1 = initPosition1;

		System.out.println("RobotDrive Left Encoder Initial Position = " + initPosition1);

		right_enc.setPosition(0.0);
		initPosition2 = right_enc.getPosition();

		System.out.println("RobotDrive Right Encoder Initial Position = " + initPosition2);

	}

	/////////////////////////////////////////////////////////////////////
	// Function: public int driveFwd(double distance)
	/////////////////////////////////////////////////////////////////////
	//
	// Purpose: Drives the robot forward the distance in feet specified
	// by the argument.
	//
	// Arguments:double distance, The distance to be traveled in feet.
	//
	// Returns: An double representing overshoot/undershoot of the movement
	// in encoder counts.
	//
	// Remarks: It is assumed that the first time a movement is attempted
	// that it is a forward movement. The first movement appears
	// to determine the direction of the encoder counts.
	//
	/////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////
	public double driveFwd(double distance) {
		int loop_count = 0;
		double overshoot;
		double counts;
		double initial_position;
		double current_position;
		double target;

		double fraction;
		double heading;

		// Determine where we are pointing - we want to maintain this heading during
		// this
		// forward movement.
		heading = gyro.getAngle();

		// Read encoder #1, get the initial encoder position and assign it to
		// the current position. Calculate the number of encoder counts
		// necessary to reach the final destination (target).
		initial_position = left_enc.getPosition();
		current_position = initial_position;
		counts = calcCounts_SAE(distance);
		target = initial_position + counts;

		// fraction starts out as equal to 1.0 and decreases as we approach the target.
		// fraction is counts remaining divided by total counts.
		fraction = (target - current_position) / (target - initial_position);

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
			if (fraction > BRAKE_FRACTION) {
				moveFwd(START_SPEED, heading);
			} else {
				moveFwd(BRAKE_SPEED, heading);
			}
			Timer.delay(0.01);
			// We don't want to stay longer than we have to. Assuming
			// that the 10 msec is reasonably accurate we limit the
			// move to 5 seconds for starters.
			loop_count++;
			if ((loop_count % ENC_CONSOLE_UPDATE) == 0) {
				// Provide periodic status
				System.out.println(
						"current_position = " + current_position + " target = " + target + " fraction = " + fraction);
			}
			if (loop_count == ENC_LOOP_ESCAPE)
				break; // escape clause
			current_position = left_enc.getPosition();
			fraction = (target - current_position) / (target - initial_position);
		}

		// stop movement, compute error (overshoot or undershoot)
		diff_drive.arcadeDrive(0, 0);
		overshoot = current_position - target;
		System.out.println("overshoot = " + overshoot);

		return (overshoot);
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
	public double driveBwd(double distance) {
		int loop_count = 0;
		double overshoot;
		double counts;
		double initial_position;
		double current_position;
		double target;

		double fraction;
		double heading;

		// Determine where we are pointing - we want to maintain this heading during
		// this
		// forward movement.
		heading = gyro.getAngle();

		// Read encoder #1, get the initial encoder position and assign it to
		// the current position. Calculate the number of encoder counts
		// necessary to reach the final destination (target).
		initial_position = left_enc.getPosition();
		current_position = initial_position;
		counts = calcCounts_SAE(distance);
		target = initial_position - counts;

		// fraction starts out as equal to 1.0 and decreases as we approach the target.
		// fraction is counts remaining divided by total counts.
		fraction = (target - current_position) / (target - initial_position);

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
		while (current_position > target) {
			if (fraction > BRAKE_FRACTION) {
				moveBwd(START_SPEED, heading);
			} else {
				moveBwd(BRAKE_SPEED, heading);
			}
			Timer.delay(0.01);
			// We don't want to stay longer than we have to. Assuming
			// that the 10 msec is reasonably accurate we limit the
			// move to 5 seconds for starters.
			loop_count++;
			if ((loop_count % ENC_CONSOLE_UPDATE) == 0) {
				// Provide periodic status
				System.out.println(
						"current_position = " + current_position + " target = " + target + " fraction = " + fraction);
			}
			if (loop_count == ENC_LOOP_ESCAPE)
				break; // escape clause
			current_position = left_enc.getPosition();
			fraction = (target - current_position) / (target - initial_position);
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
	// Remarks:
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
	// Function: calcCounts_SAE(double distance)
	///////////////////////////////////////////////////////////////////////////
	//
	// Purpose: Computes the encoder readout corresponding to the submitted
	// distance.
	//
	// Arguments: Accepts a double representing the distance in feet.
	//
	// Returns: A double representing the encoder change associated
	// with that distance.
	//
	// Remarks: ENCODER_RESOLUTION is the value associated with one
	// inch of travel. Based on eight inch diameter wheels used
	// on the 2019 robot.
	//
	// Example: If we are traveling 10 inches, the expected change
	// in output from the encoder would be:
	//
	// 10.0/1.537 = 6.506
	//
	////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////
	public double calcCounts_SAE(double distance) {
		double enc_change;

		distance *= 12.0; // convert feet to inches.

		enc_change = distance / ENCODER_RESOLUTION;

		return (enc_change);

	}

	///////////////////////////////////////////////////////////////////////////
	// Function: void calcCounts_Metric(double radius,double distance)
	///////////////////////////////////////////////////////////////////////////
	//
	// Purpose: Given the distance to be traveled in meters, this function
	// calculates the encoder change required to travel that distance.
	//
	// Arguments: double distance (in meters).
	//
	// Returns: A double representing the encoder change for specified distance.
	//
	// Remarks:
	//
	////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////
	public double calcCounts_Metric(double distance) {
		double enc_change;

		distance *= 100.0; // convert meters to centimeters
		distance /= 2.54; // convert centimeters to inches

		enc_change = distance / ENCODER_RESOLUTION;

		return (enc_change);

	}

	/////////////////////////////////////////////////////////////////////
	// Function: public double turnRight_Arcade(double target,double rot_speed)
	/////////////////////////////////////////////////////////////////////
	//
	// Purpose: Rotates the robot CW the amount of degrees specified
	// by target. It is important to note that actual rotation target
	// is relative to the starting point determined at program start.
	// For example targets are 0-target.
	//
	// Arguments:The target in degrees.
	//
	// Returns: A double representing the angle acheived. This could
	// changed at some point to return the error.
	//
	// Remarks: Initial development 12/11/18. Uses Arcade drive to
	// rotate the robot. Note that we want motor braking
	// enabled. Added escape count to exit loop after a certain
	// time when it doesn't reach the target.
	//
	// 12/22/2018: Interesting to note that under some circumstances
	// turning right may not be appropriate. This function looks for
	// the gyro indication represented by the target angle. If the
	// the target is specified as "45" degrees and we are already at
	// "75", the while() loop is never entered and nothing will happen.
	// So this function does not turn 45 degrees, it attempts to reach
	// an absolute "heading" of "45 degrees.
	//
	/////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////
	public double turnRight_Arcade(double target) {
		int count = 0;
		double rot_speed;
		double angle; // current gyro angle

		rot_speed = ROT_SPEED;
		gyro.reset();

		angle = gyro.getAngle();
		System.out.println("ANGLE = " + angle + " Target = " + target);

		while (angle < target) {
			rotatePosArcade(rot_speed);
			angle = gyro.getAngle();

			if (angle > (target - ANGL_PROX_1)) {
				rot_speed /= ROT_ATTEN;
			}
			if (angle > (target - ANGL_PROX_2)) {
				rot_speed /= ROT_ATTEN;
			}
			Timer.delay(0.01);
			count++;
			if ((count % GYRO_CONSOLE_UPDATE) == 0) {
				System.out.println("angle = " + angle);
			}
			if (count == GYRO_LOOP_ESCAPE)
				break;
		}
		diff_drive.arcadeDrive(0, 0);
		angle = gyro.getAngle();
		return (angle);
	}

	/////////////////////////////////////////////////////////////////////
	// Function: public void rotatePosArcade(double rot_spd)
	/////////////////////////////////////////////////////////////////////
	//
	// Purpose: Uses arcadeDrive(...) to rotate the robot CW
	//
	// Arguments:Accepts a double representing the rotation speed.
	//
	// Returns: void
	//
	// Remarks: According to the documentation of this function the
	// argument "rot_speed" is squared.
	//
	/////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////
	public int rotatePosArcade(double rot_spd) {

		if (rot_spd > 1.0)
			return (-1);
		diff_drive.arcadeDrive(0, rot_spd);
		return (0);
	}

	/////////////////////////////////////////////////////////////////////
	// Function: public double turnLeft_Arcade(double target,double rot_speed)
	/////////////////////////////////////////////////////////////////////
	//
	// Purpose: Rotates the robot CCW the amount of degrees specified
	// by target. It is important to note that actual rotation target
	// is independent of the starting point at function call but is
	// relative to the last call of gyro.reset().
	//
	// Arguments: The target in degrees.
	//
	// Returns: A double representing the angle achieved. Could
	// be changed at a later date to represent the error.
	//
	// Remarks: Initial development 12/11/18. Uses Arcade drive to
	// rotate the robot. Note that we want motor braking
	// enabled. Added escape count to exit loop after a certain
	// time when it doesn't reach the target. Untested.
	//
	// 12/22/2018: Interesting to note that under some circumstances
	// turning left may not be appropriate. This function looks for
	// the gyro indication represented by the target angle. The submitted
	// target angle is multiplied by -1.0 immediately on entering
	// this function. If the the target is specified as "45" degrees
	// and we are already at "-75", the while() loop is never entered
	// and nothing will happen.
	// So this function does not turn left 45 degrees, it attempts to reach
	// an absolute "heading" of "-45 degrees. In another case, if the
	// gyro reads "500" degrees, the robot will turn counterclockwise
	// until -45 degrees is reached. If the gyro reads "-10" degrees
	// the robot turns to the left "35" degrees.
	//
	// The final variable ROT_ATTEN value is yet to be determined but
	// for the first iteration it is 2.0.
	//
	/////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////
	public double turnLeft_Arcade(double target) {
		int count = 0;
		double rot_speed = ROT_SPEED;
		double angle; // current gyro angle

		target *= -1.0;

		gyro.reset();

		angle = gyro.getAngle();

		while (angle > target) {
			rotateNegArcade(rot_speed);
			angle = gyro.getAngle();

			if (angle < (target + ANGL_PROX_1)) {
				rot_speed /= ROT_ATTEN;
			}
			if (angle < (target + ANGL_PROX_2)) {
				rot_speed /= ROT_ATTEN;
			}
			Timer.delay(0.01);
			count++;
			if ((count % GYRO_CONSOLE_UPDATE) == 0) {
			}
			if (count == GYRO_LOOP_ESCAPE)
				break;
		}
		diff_drive.arcadeDrive(0, 0);
		angle = gyro.getAngle();
		System.out.println("angle = " + angle);
		return (angle);

	}

	/////////////////////////////////////////////////////////////////////
	// Function: public int rotateNegArcade(double rot_spd)
	/////////////////////////////////////////////////////////////////////
	//
	// Purpose: Uses arcadeDrive(...) to rotate the robot CCW
	//
	// Arguments:Accepts a double representing the rotation speed.
	//
	// Returns: Zero normally, will return -1 if an unacceptable
	// rotation speed is entered
	//
	// Remarks: According to the documentation of this function the
	// argument "rot_speed" is squared.
	//
	/////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////
	public int rotateNegArcade(double rot_spd) {
		if (rot_spd > 1.0)
			return (-1);
		diff_drive.arcadeDrive(0, -rot_spd);
		return (0);
	}

	/////////////////////////////////////////////////////////////////////
	// Function: public double turnAbsolute(double degrees, double rot_speed)
	/////////////////////////////////////////////////////////////////////
	//
	// Purpose: Rotate the number of degrees in the direction specified
	// by the sign of the argument "degrees". Negative arguments will
	// rotate CCW, positive arguments CW.
	//
	// Arguments: A double representing the number of degrees to rotate
	// and a double representing the rotation speed to be supplied to
	// the arcadeDrive(...) function.
	//
	// Returns: A double representing the difference between the
	// achieved rotation and the requested rotation.
	//
	// Remarks: A few test cases:
	//
	// 1. Initial angle measurement is 110 degrees, we request a -35
	// degree rotation. Target is then 100-35=65 degrees. We
	// rotate ccw to 65 degrees.
	// 2. Initial angle measurement is -45 and we ask for 360. New
	// target is 315. We rotate cw all the way around to 315.
	//
	// Everything depends on the functions turnRight/Left_Arcade(...).
	// The braking algorithms will determine the accuracy.
	//
	/////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////
	public double turnAbsolute(double degrees) {

		// Rotation Direction Flags
		boolean ccw = false;
		boolean cw = false;

		double target = 0.0;
		double angle = 0.0; // instantaneous angle measurement
		double result = 0.0;

		if (degrees < 0.0) { // we will rotate ccw
			ccw = true;
			cw = false;
		} else { // we rotate cw
			cw = true;
			ccw = false;
		}

		angle = gyro.getAngle(); // where are we?

		if (cw == true) {
			target = angle + degrees;
			result = turnRight_Arcade(target);
		} else if (ccw == true) { // ccw==true
			target = angle - degrees;
			result = turnLeft_Arcade(target);
		}

		return (result);
	}

	/////////////////////////////////////////////////////////////////////
	// Function: public double turn2Heading(double heading)
	/////////////////////////////////////////////////////////////////////
	//
	// Purpose: Turns the robot to a compass heading (0->360).
	//
	// Arguments:Accepts a double representing the target heading
	//
	// Returns: The Acheived heading or a ridiculous value in the
	// event that a negative heading is entered.
	//
	// Remarks:
	//
	/////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////
	public double turn2Heading(double heading) {
		double angle;
		double delta;
		double change;
		double result;

		// Heading represents a "compass" reading, i.e.,
		// an angle from zero to something less than 360 degrees.
		// Negative arguments are not allowed. Because we want
		// to return the achieved heading we do not allow
		// submission of negative arguments and should return
		// a negative double so that the user can recognize
		// an error.
		if ((heading < 0.0) || (heading > 360.0)) {
			System.out.println("Submitted arguments must be greater than zero");
			return (-999.9);
		}

		// This function will return an angle between 0 and 360 degrees.
		angle = getHeading();

		delta = heading - angle; // number of degrees to turn

		System.out.println("angle = " + angle + " delta = " + delta);

		// Reduce delta to the smallest necessary rotation
		// It should be something less than 180.0 or greater
		// than -180.0
		if (delta > 180.0) {
			delta -= 360.0;
		}
		if (delta < -180.0) {
			delta += 360.0;
		}

		System.out.println("delta = " + delta);

		// Depending on the sign of delta we turn left (-) or right (+)
		change = turnAbsolute(delta);
		System.out.println("delta = " + delta + " change = " + change);

		// Measure the angle, convert to compass indication. Note that
		// reading of the gyro after a small rotation could still be
		// outside the range of compass indications. We want to put
		// it in the range of the compass (0->360)
		result = getHeading();
		return (result);
	}

	/////////////////////////////////////////////////////////////////////
	// Function: public double getHeading()
	/////////////////////////////////////////////////////////////////////
	//
	// Purpose: Utility routine to determine starting compass indication
	// of the robot.
	//
	// Arguments:none
	//
	// Returns: The current heading of the robot, 0->360 degrees.
	//
	// Remarks: Called twice within turn2Heading.
	//
	/////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////
	public double getHeading() {
		int debug = 1;
		double angle;

		// A heading represents a "compass" reading, i.e.,
		// an angle from zero to something less than 360 degrees.

		// In the event that the current gyroscope indication is greater than
		// 360 degrees, reduce it to something within the compass range.
		// The same argument applies if the initial indication is less
		// than -360.0 degrees.
		angle = gyro.getAngle();
		while (angle > 360.0) {
			angle -= 360.0;
			if (debug == 1) {
				System.out.println("Compass heading = " + angle);
			}
		}

		// In the event that the current gyroscope indication is less
		// than -360.0, increase it to something within the compass range.
		while (angle < -360.0) {
			angle += 360.0;
			if (debug == 1) {
				System.out.println("Compass heading = " + angle);
			}
		}
		System.out.println("Compass heading = " + angle);

		return (angle);
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
