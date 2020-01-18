package org.usfirst.frc.team5914.robot;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import edu.wpi.first.wpilibj.SpeedControllerGroup;

public class WinchMechanism {

	WPI_TalonSRX winch1, winch2;

	SpeedControllerGroup winch;

	public WinchMechanism() {

		winch1 = new WPI_TalonSRX(5);
		winch2 = new WPI_TalonSRX(6);

		winch = new SpeedControllerGroup(winch1, winch2);
	}

	public void WinchOperation() {
		winch.set(Robot.stick.getRawAxis(3) + Robot.stick.getRawAxis(2) * -1);

	}

}
