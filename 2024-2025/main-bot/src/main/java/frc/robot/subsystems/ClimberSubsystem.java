package frc.robot.subsystems;

import com.ctre.phoenix6.hardware.TalonFX;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.ClimberConstants;
import frc.robot.archived_classes.CompCommands;
import frc.robot.archived_classes.Logging;

// Must change logging

public class ClimberSubsystem extends SubsystemBase {
   private boolean switchFlipped = false;
   private TalonFX ClimberMotor;

   public ClimberSubsystem() {
      ClimberMotor = new TalonFX(ClimberConstants.kClimberMotorID);
   }

   public void climberSwitchTriggered() {
      switchFlipped = true;
   }

   public void engageClimber() {
      ClimberMotor.set(0.1);
   }

   public void debugMethod() {
      SmartDashboard.putBoolean("Climber.IsSwitchedFlipped?", switchFlipped);
      SmartDashboard.putData("putData is cool. Have a ClimberMotor", ClimberMotor);
   }

}