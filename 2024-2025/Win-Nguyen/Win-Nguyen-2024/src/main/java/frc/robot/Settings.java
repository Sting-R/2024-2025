package frc.robot;

public class Settings {

  public class SwerveDrive {

    public class Wheel {

      public int driveID;
      public int turnID;
      public int encoderID;

      public double posX;
      public double posY;
    }

    public Wheel frontLeft;
    public Wheel frontRight;
    public Wheel backLeft;
    public Wheel backRight;
  }

  public SwerveDrive swerveDrive;

  public int controllerID;
}
