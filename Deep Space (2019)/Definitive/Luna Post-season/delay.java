/////////////////////////////////////////////////////////////////////
//  File:  delay.java
/////////////////////////////////////////////////////////////////////
//
//  Purpose:  Utility class using the Java System.nanoTime() function
//            to create timing delays in milliseconds, microseconds,
//            and seconds.
//
//  Remarks:  Appears to be accurate to tenths of microseconds -
//            probably better without error calculation.
//
/////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////
package frc.robot;

class Delay  {
    public double delay_seconds;
    public double delay_milliseconds;
    public double delay_microseconds;
    public long delay_nanoseconds;

    Delay()  {
        delay_seconds=0.0;
        delay_milliseconds=0.0;
        delay_microseconds=0.0;
    }

    public double delay_seconds(double seconds)  {
        long start_time=0;
        long end_time=0;
        long nanoseconds;
        double elapsed_time;

        nanoseconds=(long)(seconds*1e9);

        start_time=System.nanoTime();
        end_time=System.nanoTime();

        while((end_time-start_time)<nanoseconds)  {
            end_time=System.nanoTime();
        }

        //  This code introduces a timing error of 0.1 - 0.2 microseconds.
        elapsed_time=(double)(end_time-start_time)/1e9;
        return(elapsed_time);

    }

    public double delay_milliseconds(double milliseconds)  {
        long start_time=0;
        long end_time=0;
        long nanoseconds;
        double elapsed_time;

        nanoseconds=(long)(milliseconds*1e6);

        start_time=System.nanoTime();
        end_time=System.nanoTime();

        while((end_time-start_time)<nanoseconds)  {
            end_time=System.nanoTime();
        }

        //  This code introduces a timing error of 0.1 - 0.2 microseconds.
        elapsed_time=(double)(end_time-start_time)/1e6;
        return(elapsed_time);

    }

    public double delay_microseconds(double microseconds)  {
        long start_time=0;
        long end_time=0;
        long nanoseconds;
        double elapsed_time;

        nanoseconds=(long)(microseconds*1e3);

        start_time=System.nanoTime();
        end_time=System.nanoTime();

        while((end_time-start_time)<nanoseconds)  {
            end_time=System.nanoTime();
        }

        //  This code introduces a timing error of 0.1 - 0.2 microseconds.
        elapsed_time=(double)(end_time-start_time)/1e3;
        return(elapsed_time);

    }
}