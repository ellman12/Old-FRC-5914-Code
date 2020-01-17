/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.frc.team3940.robot;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Counter;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;


/*************************************************
 * This is a basic Example how to use the am-3749
 * PWM Output for angle tracking using Counters
 ************************************************/


public class Robot extends IterativeRobot {
	// This counter is attached to DIO 0 and measures PWM
	Counter AmMag;   
	// This counter counts the index pulses (revolutions)
	Counter AmIndex; //full period counter
	
	@Override
	public void robotInit() {
		//Initialize the Counters 
		AmMag = new Counter(0); 
		AmIndex = new Counter(1);
		//Set Semi-Period Mode in order to Measure the Pulse Width
		AmMag.setSemiPeriodMode(true);
	}

	@Override
	public void teleopInit() {
		//Reset the counters
		AmMag.reset();
		AmIndex.reset();
	}
	
	@Override
	public void teleopPeriodic() {
		//read the pulse width
		double value = AmMag.getPeriod();
		SmartDashboard.putNumber("Rotations", AmIndex.get());
		SmartDashboard.putNumber("Intermediate", value);
		// The 9.73e-4 is the total period of the PWM output on the am-3749
		// The value will then be divided by the period to get duty cycle.
		// This is converted to degrees and Radians
		double angleDEG = (value/9.739499999999999E-4)*361 -1;
		double angleRAD = (value/9.739499999999999E-4)*2*(Math.PI) ;
		SmartDashboard.putNumber("Angle in Degrees", angleDEG);
		SmartDashboard.putNumber("Angle in Radians", angleRAD);
	}

}
