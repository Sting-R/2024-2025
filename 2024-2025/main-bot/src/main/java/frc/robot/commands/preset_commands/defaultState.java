package frc.robot.commands.preset_commands;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import frc.robot.subsystems.CommandSwerveDrivetrain;
import frc.robot.subsystems.CoralArmSubsystem;
import frc.robot.subsystems.CoralArmSubsystem.CoralArmLevels;
import frc.robot.subsystems.ElevatorSubsystem;
import frc.robot.subsystems.ElevatorSubsystem.ElevatorPresets;

public class defaultState extends Command {
    ElevatorSubsystem m_Elevator;
    CoralArmSubsystem m_CoralArm;
    CoralArmLevels coralLevel;
    ElevatorPresets elevatorLevel;
    CommandXboxController driveController;
    CommandXboxController operatorController;
    Timer outtakeTimer;
    boolean timerStarted = false;

    public defaultState(ElevatorSubsystem elevator, CoralArmSubsystem coralArm, CommandXboxController driveController,
            CommandXboxController operatorController) {
        addRequirements(elevator, coralArm);
        m_Elevator = elevator;
        m_CoralArm = coralArm;
        this.driveController = driveController;
        this.operatorController = operatorController;
        coralLevel = CoralArmLevels.defaultState;
        elevatorLevel = ElevatorPresets.defaultState;
    }

    public void execute() {
        m_Elevator.elevatorMoveToPresetMM(elevatorLevel);
        m_CoralArm.defaultState();
    }

    @Override
    public boolean isFinished() {
        return m_Elevator.isElevatorAtDesiredState(elevatorLevel).getAsBoolean()
                && m_CoralArm.isCoralArmAtDesiredState(coralLevel).getAsBoolean();
    }

    public void end(boolean interrupted) {
        m_Elevator.elevatorMoveToPresetMM(elevatorLevel);
        m_CoralArm.defaultState();
        // m_Elevator.stop();
    }

}
