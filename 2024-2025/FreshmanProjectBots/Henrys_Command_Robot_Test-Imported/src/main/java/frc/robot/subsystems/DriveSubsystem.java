package frc.robot.subsystems;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.ControlMode;

public class DriveSubsystem extends SubsystemBase {

    // Motors 
    private final TalonSRX FLMotor;
    private final TalonSRX FRMotor;
    private final TalonSRX BLMotor;
    private final TalonSRX BRMotor;

    public DriveSubsystem(int FLId, int FRId, int BLId, int BRId) {
        FLMotor = new TalonSRX(FLId);
        FRMotor = new TalonSRX(FRId);
        BLMotor = new TalonSRX(BLId);   
        BRMotor = new TalonSRX(BRId);
    }

    public Command setMotorVoltageCommand(double leftTrigger, double rightTrigger, double rightXAxis) { 
        return runOnce(() -> {
            // System.out.println("Command Ran");
            setMotorVoltage(leftTrigger, rightTrigger, rightXAxis);
        });
    }

    public void setMotorVoltage(double leftTrigger, double rightTrigger, double rightXAxis) {
        // System.out.println("method ran");
        FLMotor.set(ControlMode.PercentOutput, rightTrigger -leftTrigger + rightXAxis);
        FRMotor.set(ControlMode.PercentOutput, -rightTrigger + leftTrigger -rightXAxis);
        BLMotor.set(ControlMode.PercentOutput, rightTrigger -leftTrigger + rightXAxis);
        BRMotor.set(ControlMode.PercentOutput,  -rightTrigger + leftTrigger -rightXAxis);
        
        // FLMotor.set(ControlMode.PercentOutput, 0.5);
        // BLMotor.set(ControlMode.PercentOutput, 0.5);

        SmartDashboard.putNumber("Left Trigger", leftTrigger);
        SmartDashboard.putNumber("Right Trigger", rightTrigger);
        SmartDashboard.putNumber("Stick", rightXAxis);

    }

}
