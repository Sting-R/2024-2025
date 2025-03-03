// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import frc.robot.generated.TunerConstants;
import static edu.wpi.first.units.Units.*;
import edu.wpi.first.units.measure.Angle;

import edu.wpi.first.units.measure.Distance;

/**
 * The Constants class provides a convenient place for teams to hold robot-wide
 * numerical or boolean constants. This class should not be used for any other
 * purpose. All constants should be declared globally (i.e. public static). Do
 * not put anything functional in this class.
 *
 * <p>
 * It is advised to statically import this class (or one of its inner classes)
 * wherever the constants are needed, to reduce verbosity.
 */
public class Constants {
  public static class OperatorConstants {
    public static final int kDriverControllerPort = 0;
    public static final int kAuxiliaryControllerPort = 1;
  }

  // PLACEHOLDER IDS!!!
  public static class ElevatorConstants {
    public static final int kElevatorLeftMotorID = 15;
    public static final int kElevatorRightMotorID = 16;
    public static final double kMaxMotorElevatorSpeed = 0.05;
    public static final int kLeftElevatorEncoderID1 = 9;
    public static final int kLeftElevatorEncoderID2 = 8;
    // public static final int kRightElevatorEncoderID1 = 7;
    // public static final int kRightElevatorEncoderID2 = 6;
    public static final double kLeftElevatorEncoderBottomValue = 0;
    public static final double kLeftElevatorEncoderTopValue = 15.09;
    public static final double kRightElevatorEncoderBottomValue = 0;
    public static final double kRightElevatorEncoderTopValue = 15.13;

    public static class ElevatorPreset {
      // PLACEHOLDER VALUES!!!
      public static final double level1EncoderValue = 0;
      public static final double level2EncoderValue = 2;
      public static final double level3EncoderValue = 5;
      public static final double level4EncoderValue = 7;
    }
  }

  public static final class AlgaeArmConstants {
    public static final int algaeArmBarMotorID = 8;// placeholder
  }

  public static class CoralArmConstants {
    public static final int coralArmMotorID = 69;// placeholder with funny number hehe
    public static final int kLeftCoralMotorID = 70;
    public static final int kRightCoralMotorID = 71;
    public static final int kCoralArmEncoderID1 = 4;
    public static final int kCoralArmEncoderID2 = 5;
    public static final int kCoramIntakeEncoderID1 = 6;
    public static final int kCoralIntakeEncoderID2 = 7;
    public static final int kCoralLightSensorID = 3;
    public static final double kCoralEncoderTopValue = 3;
    public static final double kCoralEncoderTopPosition = 0.5;
    public static final double kCoralEncoderIntakePosition = 0.3; // Temp
    public static final double kCoralEncoderOuttakePosition = 0.1; // Temp
    public static final double kCoralEncoderDefaultPosition = 0.2; // Temp
    // public static final int kCoralEncoderTopBuffer = kCoralEncoderTopValue + 10;
    public static final double kCoralEncoderBottomPosition = 0;
    // public static final int kCoralEncoderBottomBuffer = kCoralEncoderBottomValue
    // - 10;
  }

  public static class ClimberConstants {
    public static final int kLeftClimberMotorID = 25; // TEMP
    public static final int kRightClimberMotorID = 26; // TEMP
    public static final int kClimberLimitSwitchID = 20; // TEMP
  }

  public static class LimelightConstants {
    public static final String kReefLimelightName = "ReefDriveLimelight";
    public static final String kIntakeLimelightName = "IntakeLimelight";
    public static final String kBackLimelightName = "BackLimelight";

    public static final double desiredIntakeDistance = 0.5; // Placeholder
    public static final double desiredOuttakeDistance = 0.5; // Placeholder
    public static final double desiredIntakeAngle = 0.5; // Placeholder
    public static final double desiredOuttakeAngle = 0.5; // Placeholder
    public static final double desiredIntakeAngleOffset = 0.5; // Placeholder. We also need these because the limelights
                                                               // are not centered
    public static final double desiredOuttakeAngleOffset = 0.5; // Placeholder
  }

  public static class DriveTrainConstants {
    public static final double MaxAngularRate = RotationsPerSecond.of(0.75).in(RadiansPerSecond);
    public static final double MaxSpeed = TunerConstants.kSpeedAt12Volts.in(MetersPerSecond);
    public static final double gearRatio = 4.59375;
    public static final double lengthBetweenSwerveModules = 22.5;
    public static final double lengthOfBumpers = 25.0; // placeholder

    // Motor and encoder ids (ALL ARE PLACEHOLDERS. FIND ACTUAL IDS IN
    // COMMANDSWERVEDRIVETRAIN.JAVA)
    // public static final int FLDriveMotorID = 0;
    // public static final int FLTurnMotorID = 1;
    // You can finish this out Scott! :D

    // Front Left Constants
    public static final int kFrontLeftDriveMotorId = 1;
    public static final int kFrontLeftTurnMotorId = 2;
    public static final int kFrontLeftEncoderId = 11;
    public static final Angle kFrontLeftEncoderOffset = Rotations.of(-0.432373046875);
    public static final boolean kFrontLeftSteerMotorInverted = true;
    public static final boolean kFrontLeftEncoderInverted = false;
    public static final Distance kFrontLeftXPos = Inches.of(11.25);
    public static final Distance kFrontLeftYPos = Inches.of(11.25);
    // Front Right Constants
    public static final int kFrontRightDriveMotorId = 3;
    public static final int kFrontRightTurnMotorId = 4;
    public static final int kFrontRightEncoderId = 12;
    public static final Angle kFrontRightEncoderOffset = Rotations.of(-0.282958984375);
    public static final boolean kFrontRightSteerMotorInverted = true;
    public static final boolean kFrontRightEncoderInverted = false;
    public static final Distance kFrontRightXPos = Inches.of(11.25);
    public static final Distance kFrontRightYPos = Inches.of(-11.25);
    // Back Left Constants
    public static final int kBackLeftDriveMotorId = 5;
    public static final int kBackLeftTurnMotorId = 6;
    public static final int kBackLeftEncoderId = 13;
    public static final Angle kBackLeftEncoderOffset = Rotations.of(-0.09326171875);
    public static final boolean kBackLeftSteerMotorInverted = true;
    public static final boolean kBackLeftEncoderInverted = false;
    public static final Distance kBackLeftXPos = Inches.of(-11.25);
    public static final Distance kBackLeftYPos = Inches.of(11.25);
    // Back Right Constants
    public static final int kBackRightDriveMotorId = 7;
    public static final int kBackRightTurnMotorId = 8;
    public static final int kBackRightEncoderId = 14;
    public static final Angle kBackRightEncoderOffset = Rotations.of(-0.111328125);
    public static final boolean kBackRightSteerMotorInverted = true;
    public static final boolean kBackRightEncoderInverted = false;
    public static final Distance kBackRightXPos = Inches.of(-11.25);
    public static final Distance kBackRightYPos = Inches.of(-11.25);
  }
}
