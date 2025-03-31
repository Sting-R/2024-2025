// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import frc.robot.generated.TunerConstants;
import static edu.wpi.first.units.Units.*;

// import data.Length;
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
    public static final double kMaxMotorElevatorSpeed = 0.8;
    public static final int kLeftElevatorEncoderID1 = 9;
    public static final int kLeftElevatorEncoderID2 = 8;
    public static final double kLeftElevatorEncoderBottomValue = 0;
    public static final double kLeftElevatorEncoderTopValue = 16.09;
    public static final double kRightElevatorEncoderBottomValue = 0;
    public static final double kRightElevatorEncoderTopValue = 15.13;

    // // As measured from intake
    // public static final Length MIN_HEIGHT_FROM_FLOOR =
    // Length.fromInches(10.7125); // Temp Values!!!
    // public static final Length MAX_HEIGHT_FROM_FLOOR = Length.fromInches(89.825);

    // public static final double MIN_HEIGHT = 0.0;
    // public static final Length MAX_HEIGHT =
    // MAX_HEIGHT_FROM_FLOOR.minus(MIN_HEIGHT_FROM_FLOOR); // 79.1125

    public static class ElevatorPreset {
      // PLACEHOLDER VALUES!!!
      public static final double level1EncoderValue = 3.0576; // 2.9785;
      public static final double level2EncoderValue = 8.6367; // 7.844726;
      public static final double level3EncoderValue = 0.3645; // 15.8208984375;
      public static final double level4EncoderValue = 15.0399; // 15.642578125;
      public static final double intakeEncoderValue = 12.3576171875;
      public static final double kickLowerAlgaeOff = 2.773925; // Temp
      public static final double kickUpperAlgaeOff = 15.370605; // Temp

      public static final double defaultStateEncoderValue = 0.8;
    }
  }

  public static class CoralArmConstants {
    public static final int coralArmMotorID = 22;
    public static final int kLeftCoralIntakeMotorID = 20;
    public static final int kRightCoralIntakeMotorID = 21;
    public static final int kCoralArmEncoderID1 = 4;
    public static final int kCoralArmEncoderID2 = 5;
    public static final int kCoramIntakeEncoderID1 = 6;
    public static final int kCoralIntakeEncoderID2 = 7;
    public static final int kCoralLightSensorID = 3;
    public static final double kMaxArmSpeed = 0.7;

    // Levels
    public static final double kCoralEncoderOuttakelvl1Position = -0.1176; // -0.033398;
    public static final double kCoralEncoderOuttakelvl2Position = -0.1772; // 0.1;
    public static final double kCoralEncoderOuttakelvl3Position = -9.1245; // 0.80595703125;
    public static final double kCoralEncoderOuttakelvl4Position = -8.7463; // 8.74560546875;
    // Aux actions
    public static final double kCoralEncoderIntakePosition = -1.3392578125;
    public static final double kCoralEncoderDefaultPosition = 0.02978515625; // Temp
    public static final double kCoralEncoderKickUpperCoralOff = -6;
    public static final double kCoralEncoderKickLowerCoralOff = -4.309;
    // Boundries
    public static final double kCoralEncoderTopPosition = -16.5;
    public static final double kCoralEncoderBottomPosition = 0.3;
  }

  public static class ClimberConstants {
    public static final int kClimberMotorID = 25;
    // public static final int kRightClimberMotorID = 26; // yeah we dont need that
    public static final int kClimberLimitSwitchID = 20;
    public static final double kCoralEncoderActivePosition = 5;
    public static final double kCoralEncoderEngagedPosition = 0;
  }

  public static class LimelightConstants {
    public static final String kLeftReefLimelightName = "limelight-lrll";
    public static final String kRightReefLimelightName = "limelight-rrll";
    public static final String kIntakeLimelightName = "limelight-inll";

    // Offsets For Reef
    public static final double desiredLeftReefOffset = 0.5; // Placeholder
    public static final double desiredRightReefOffset = 0.5; // Placeholder
    // Left Reef side
    public static final double desiredLeftRotationOffset = -1.308611035346985; // Placeholder
    public static final double desiredLeftYOffset = 0.5; // Placeholder
    public static final double desiredLeftXOffset = 0.5;
    public static final double desiredLeft3DXOffset = 0.5; // Placeholder
    public static final double desiredLeftArea = 0.5; // Placeholder

    // Right Reef side
    public static final double desiredRightRotationOffset = 0.5; // Placeholder
    public static final double desiredRightXOffset = -13.0792; // Placeholder
    public static final double desiredRightYOffset = 0;

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

  // public static final class AlgaeArmConstants {
  // public static final int algaeArmBarMotorID = 30;
  // }

  // Distances
  // public static final double desiredIntakeDistance = 0.5; // Placeholder
  // public static final double desiredOuttakeDistanceLvl1 = 0; // Placeholder
  // public static final double desiredOuttakeDistanceLvl2 = 0; // Placeholder
  // public static final double desiredOuttakeDistanceLvl3 = 0; // Placeholder
  // public static final double desiredOuttakeDistanceLvl4 = 0; // Placeholder

  // // Angle
  // public static final double desiredIntakeAngle = 0.5; // Placeholder
  // public static final double desiredOuttakeAngle = 0.5; // Placeholder

}
