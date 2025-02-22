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
public final class Constants {
  public static class OperatorConstants {
    public static final int kDriverControllerPort = 0;
    public static final int kAuxiliaryControllerPort = 1;
  }

  // PLACEHOLDER IDS!!!
  public static class ElevatorConstants {
    public static final int kElevatorLeftMotorPort = 12;
    public static final int kElevatorRightMotorPort = 13;
    public static final double kMotorElevatorSpeed = 0.2;
    public static final int kElevatorEncoderBottomValue = 0;
    public static final int kElevatorEncoderTopValue = 1000;

    public static class ElevatorPreset {
      // PLACEHOLDER VALUES!!!
      public static final double level1EncoderValue = 0.5;
      public static final double level2EncoderValue = 1.0;
      public static final double level3EncoderValue = 1.5;
      public static final double level4EncoderValue = 2.0;
    }
  }

  public static final class AlgaeArmConstants {
    public static final int algaeArmBarID = 8;// placeholder
  }

  public static class CoralArmConstants {
    public static final int coralArmID = 69;// placeholder with funny number hehe
    public static final int kCoralEncoderTopValue = 1000;
    public static final int kCoralEncoderTopBuffer = kCoralEncoderTopValue + 10;
    public static final int kCoralEncoderBottomValue = 0;
    public static final int kCoralEncoderBottomBuffer = kCoralEncoderBottomValue - 10;
  }

  public static class ClimberConstants {
    public static final int kLeftClimberMotorID = 25; // TEMP
    public static final int kRightClimberMotorID = 26; // TEMP
    public static final int kClimberLimitSwitchID = 6; // TEMP
  }

  public static class DriveTrainConstants {
    public static final double MaxAngularRate = RotationsPerSecond.of(0.75).in(RadiansPerSecond);
    public static final double MaxSpeed = TunerConstants.kSpeedAt12Volts.in(MetersPerSecond);
    public static final double gearRatio = 4.59375;
    public static final double lengthBetweenSwerveModules = 22.5;
    public static final double lengthOfBumpers = 25.0; // placeholder

    // Motor and encoder ids (ALL ARE PLACEHOLDERS. FIND ACTUAL IDS IN
    // COMMANDSWERVEDRIVETRAIN.JAVA)
    public static final int FLDriveMotorID = 0;
    public static final int FLTurnMotorID = 1;
    // You can finish this out Scott! :D

    // Front Left Constants
    public static final int kFrontLeftDriveMotorId = 42;
    public static final int kFrontLeftSteerMotorId = 1;
    public static final int kFrontLeftEncoderId = 0;
    public static final Angle kFrontLeftEncoderOffset = Rotations.of(-0.432373046875);
    public static final boolean kFrontLeftSteerMotorInverted = true;
    public static final boolean kFrontLeftEncoderInverted = false;
    public static final Distance kFrontLeftXPos = Inches.of(11.25);
    public static final Distance kFrontLeftYPos = Inches.of(11.25);
    // Front Right Constants
    public static final int kFrontRightDriveMotorId = 2;
    public static final int kFrontRightSteerMotorId = 3;
    public static final int kFrontRightEncoderId = 2;
    public static final Angle kFrontRightEncoderOffset = Rotations.of(-0.282958984375);
    public static final boolean kFrontRightSteerMotorInverted = true;
    public static final boolean kFrontRightEncoderInverted = false;
    public static final Distance kFrontRightXPos = Inches.of(11.25);
    public static final Distance kFrontRightYPos = Inches.of(-11.25);
    // Back Left Constants
    public static final int kBackLeftDriveMotorId = 6;
    public static final int kBackLeftSteerMotorId = 7;
    public static final int kBackLeftEncoderId = 6;
    public static final Angle kBackLeftEncoderOffset = Rotations.of(-0.09326171875);
    public static final boolean kBackLeftSteerMotorInverted = true;
    public static final boolean kBackLeftEncoderInverted = false;
    public static final Distance kBackLeftXPos = Inches.of(-11.25);
    public static final Distance kBackLeftYPos = Inches.of(11.25);
    // Back Right Constants
    public static final int kBackRightDriveMotorId = 4;
    public static final int kBackRightSteerMotorId = 0;
    public static final int kBackRightEncoderId = 4;
    public static final Angle kBackRightEncoderOffset = Rotations.of(-0.111328125);
    public static final boolean kBackRightSteerMotorInverted = true;
    public static final boolean kBackRightEncoderInverted = false;
    public static final Distance kBackRightXPos = Inches.of(-11.25);
    public static final Distance kBackRightYPos = Inches.of(-11.25);
  }
}
