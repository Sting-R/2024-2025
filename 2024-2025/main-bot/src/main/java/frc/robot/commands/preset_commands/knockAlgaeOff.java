package frc.robot.commands.preset_commands;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import frc.robot.subsystems.CoralArmSubsystem;
import frc.robot.subsystems.CoralArmSubsystem.CoralArmLevels;
import frc.robot.subsystems.ElevatorSubsystem;
import frc.robot.subsystems.ElevatorSubsystem.ElevatorPresets;

public class knockAlgaeOff extends Command {
    private ElevatorSubsystem m_Elevator;
    private CoralArmSubsystem m_CoralArm;
    private CoralArmLevels coralLevel;
    private ElevatorPresets elevatorLevel;
    private CommandXboxController auxController;
    private boolean isLowerAlgae;
    private Timer outtakeTimer;

    public knockAlgaeOff(ElevatorSubsystem elevator, CoralArmSubsystem coralArm, CommandXboxController auxController,
            boolean isLowerAlgae) {
        addRequirements(elevator, coralArm);
        m_Elevator = elevator;
        m_CoralArm = coralArm;
        coralLevel = CoralArmLevels.defaultState;
        this.auxController = auxController;
        this.isLowerAlgae = isLowerAlgae;
        if (isLowerAlgae) {
            elevatorLevel = ElevatorPresets.kickLowerAlgaeOffStg1;
            coralLevel = CoralArmLevels.kickLowerAlgaeOffStg1;
        } else {
            elevatorLevel = ElevatorPresets.kickUpperAlgaeOffStg1;
            coralLevel = CoralArmLevels.kickUpperAlgaeOffStg1;
        }
        // System.out.println("Kick Algae Off Preset reached");
    }

    public void execute() {
        boolean isLeftStickPressed = auxController.getHID().getLeftStickButton();

        // Lower stage 2
        if (isLowerAlgae && isLeftStickPressed) {
            elevatorLevel = ElevatorPresets.kickLowerAlgaeOffStg2;
            coralLevel = CoralArmLevels.kickLowerAlgaeOffStg2;

            // Upper stage 2
        } else if (!isLowerAlgae && isLeftStickPressed) {
            elevatorLevel = ElevatorPresets.kickUpperAlgaeOffStg2;
            coralLevel = CoralArmLevels.kickUpperAlgaeOffStg2;
            // Lower Stage 1
        } else if (isLowerAlgae && !isLeftStickPressed) {
            elevatorLevel = ElevatorPresets.kickLowerAlgaeOffStg1;
            coralLevel = CoralArmLevels.kickLowerAlgaeOffStg1;
            // Upper stage 1
        } else {
            elevatorLevel = ElevatorPresets.kickUpperAlgaeOffStg1;
            coralLevel = CoralArmLevels.kickUpperAlgaeOffStg1;
        }

        m_Elevator.elevatorMoveToPresetMM(elevatorLevel);
        m_CoralArm.outtake(coralLevel, false);
    }

    @Override
    public boolean isFinished() {
        return m_CoralArm.isCoralArmAtDesiredState(coralLevel).getAsBoolean()
                && m_Elevator.isElevatorAtDesiredState(elevatorLevel).getAsBoolean();
    }

    // public void end(boolean interrupted) {

    // // m_Elevator.stop();
    // }

}
