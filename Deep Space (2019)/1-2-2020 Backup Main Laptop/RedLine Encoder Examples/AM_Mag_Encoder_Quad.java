/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.frc.team3940.robot;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.Encoder;

/*************************************************
 * This is a basic Example how to use the am-3749
 * with the Encoder Class in WPILib.
 * For more elaborate usage, consult the WPILib 
 * Documentation or the ScreenSteps
 ************************************************/


public class Robot extends IterativeRobot {
	//Initialize the Encoder with the Encoder Class
	Encoder MagEncoder;
	// Define the distance as diameter over pulses per revolution
	double diameter = 6/12; // 6 inch wheels
	double dist =0.5*3.14/1024;  // ft per pulse
	
	@Override
	public void robotInit() {
		
		//Define an encoder on Channels 0 and 1 without reversing direction and 4x encoding type
		MagEncoder = new Encoder(0,1, false, Encoder.EncodingType.k4X);
		//Set the distance per pulse to the predetermined distance
		MagEncoder.setDistancePerPulse(dist);
	}
		
	@Override
	public void teleopInit()
	{
		// Reset the Encoder before enabling Teleop Mode for demonstration purposes
		MagEncoder.reset();
	} 
	
	@Override
	public void teleopPeriodic() {
		//Write Encoder ticks and Distance on the dashboard
		SmartDashboard.putNumber("Encoder Ticks", MagEncoder.get());
		SmartDashboard.putNumber("Distance", MagEncoder.getDistance());
	}

}
