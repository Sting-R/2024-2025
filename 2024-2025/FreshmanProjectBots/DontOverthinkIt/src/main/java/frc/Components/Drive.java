package frc.Components;
import com.ctre.phoenix6.hardware.TalonFX;
// import com.ctre.phoenix6.hardware.TalonSRX;
//  import com.ctre.phoenix6.motorcontrol.ControlMode;
//  import com.ctre.phoenix6.motorcontrol.can.TalonSRX;

public class Drive {
  private TalonFX motor;

  public Drive(int motorId) { motor = new TalonFX(motorId); }
  public void MoveMotor(double power) { TalonFX leftMotor1 = new TalonFX(1); }
}