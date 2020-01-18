package org.usfirst.frc.team5914.robot;

//Here is an example of creating a separate class for speed correction that simplifies
//the "main()" application.  The guts of the interpolation routine are defined in this
//class.  I don't know exactly how this would be implemented with Joystick movements,
//perhaps we can talk about this on Thursday.
//
//Also:  I don't understand the various application formats provided by FRC.  Can we
//add additional class files to our existing application without messing it up?
//You guys can think about this and let me know.
//
//
//Note that interpolation methods could be applied for the distance traveled
//also.  Remember our counters for acceleration and deceleration?  
public class speedCorrection {

	// The class data members include an index (i) and two arrays for left and right
	// command values.
	int i;
	double leftMotor[];
	double rightMotor[];

	// Constructor takes the number of calibration points (n)
	// as the single argument. Allocates per n and then initializes
	// the leftMotor command array values. Note the use of n. An
	// increase in the number of table values will provide more
	// accurate commands (in theory) but is painful from the
	// characterization aspect.
	speedCorrection() {
		leftMotor = new double[10];
		rightMotor = new double[10];

		for (i = 0; i < 10; i++) {
			leftMotor[i] = i * (1.0 / (double) 10);

			rightMotor[i] = 1.00 * leftMotor[i];
		}
	}

	// This function uses both of the left and right command tables to compute the
	// modified right motor command to obtain straight travel forward and reverse.
	double computeRightCommand(double left_command) {

		boolean negative = false;
		int index;
		double cmd;
		double fraction;

		// Need to work with the absolute value. It is assumed that the
		// same frictional issues apply to both forward and reverse.
		if (left_command < 0.0) {
			negative = true; // this flag is needed to convert back to a negative command.
			left_command *= -1.0;
		}

		index = (int) (left_command * 10.0); // Need to multiply by 10.0 since all commands will
												// be less than 1.0.

		// This is the fractional change between the left command and the left table
		// values.
		// We will apply this fraction to the right table values to obtain the
		// interpolated
		// right motor command.
		fraction = (left_command - leftMotor[index]) / 0.1;

		// We apply the fraction to the right motor table span and then add to the
		// lower index table value.
		cmd = fraction * (rightMotor[index + 1] - rightMotor[index]) + rightMotor[index];

		// If are initial command was a negative value, we reinstate here and return.
		if (negative == true)
			cmd *= -1.0;

		return (cmd);
	}

}
