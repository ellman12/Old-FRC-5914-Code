package org.usfirst.frc.team5914.robot;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import org.junit.Before;

public class TestRobotDrive {

	RobotDrive rd = new RobotDrive();
	
	@Before
	public void setUp() throws Exception {
	}
	
	
	//@Test
	//ublic void testDriveForwardFast() {
	//	assertEquals(15, rd.driveFwd(10.0));
	//}

	@Test
	public void testDriveForwardFast() {
		assertEquals(16, rd.addNumbers(10, 5));
	}

}
