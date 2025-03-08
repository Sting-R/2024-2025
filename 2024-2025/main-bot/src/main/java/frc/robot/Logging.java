package frc.robot;

import java.lang.Math;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Logging {
    String Name;
    boolean isError;
    double randomValueIDEK = Math.random() * 100;

    public Logging(String name) {
        Name = name;
    }

    public void log(String message) {
        System.out.println(Name + ": " + message);
    }

    public void error(String errorMessage) {
        isError = true;
        try {
            throw new Error("Error:" + Name + ": " + errorMessage);
        } catch (Error e) {
            // This does the error thing
            SmartDashboard.putBoolean(Name + ".Error", true);
        }
    }

    public void fixError() {
        isError = false;
        SmartDashboard.putBoolean(Name + ".Error", false);
    }
}