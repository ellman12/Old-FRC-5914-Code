
/////////////////////////////////////////////////////////////////////
//  File:  SensorThread.java
/////////////////////////////////////////////////////////////////////
//
//  Purpose:  Reads all the sensors and outputs the values to the
//            console. It was noted that at least one of the top
//            teams uses a thread like this to keep track of the
//            sensors.
//
//  Programmer:
//
//  Environment:Microsoft VS for FIRST FRC
//
//  Inception Date:  5/16/2019
//
//  Revisions:
//
/////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////


package frc.robot;

class SensorThread implements Runnable  {
    String name;
    Thread t;
    Runtime r = Runtime.getRuntime();
    private Delay delay;

 
    //  Constructor
    SensorThread(String threadname)  {
        name=threadname;
        t=new Thread(this,name);
        System.out.println("New thread: " + t);
        delay=new Delay();
        t.start();  //  Start the thread        
    }


    //  Entry point for the thread.  This is where any desired
    //  action needs to be implemented.  It will run in "parallel"
    //  or "time share" with other active threads.  The prototype
    //  of this function is fixed.  It cannot be overidden with
    //  a version that passes in variables.  This makes it necessary
    //  to create static variables within Robot.java that can be
    //  seen within this function.
    public void run()  {

        if(Robot.sensor_thread_active = true)  {
            readSensors();
        }

        //  Need to figure out when to terminate this thread.  Will
        //  it automatically terminate when the robot program is
        //  stopped?
        /*
        System.out.println(name + "Exiting Sensor Thread");
        r.gc();  //  force garbage collection (freeing of memory resources)
        Robot.sensor_thread_active=false;
        */

    }

    int readSensors()
    {
    
        //  Outer Lift encoder (Sims motor)
        Robot.outer_lift_position=Robot.left_motor.getSelectedSensorPosition(1);

        //  Inner Lift redline encoder
        Robot.inner_lift_position=Robot.MagEncoder.get();

        //  Intake tilt encoder (NEOS motor)
        Robot.tilt_position=Robot.tilt_enc.getPosition();

        //  Get the position associated with the left drive.
        RobotDrive.position1=RobotDrive.left_enc.getPosition();

        //  Gyro associated with robot direction control
        RobotDrive.angle=RobotDrive.gyro.getAngle();

        System.out.println("outer lift encoder value = " + Robot.outer_lift_position);
        
        System.out.println("inner lift encoder value = " +  Robot.inner_lift_position);
        
        System.out.println("tilt encoder value = " + Robot.tilt_position);
        
        System.out.println("drive encoder value = " +  RobotDrive.position1);
        
        System.out.println("drive angle = " + RobotDrive.angle);

        delay.delay_milliseconds(20);
        
        return(0);
    }
    
}
