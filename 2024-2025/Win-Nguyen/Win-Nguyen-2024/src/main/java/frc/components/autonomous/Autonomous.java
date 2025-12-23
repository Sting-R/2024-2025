package frc.components.autonomous;

import java.io.IOException;

import edu.wpi.first.math.controller.HolonomicDriveController;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.controller.ProfiledPIDController;
import edu.wpi.first.math.trajectory.Trajectory;
import edu.wpi.first.math.trajectory.TrajectoryUtil;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Filesystem;
import frc.components.IComponent;
import frc.state_machine.StateMachine;

/**
 * Autonomous component.
 * <p>
 * This class is used for autonomous mode.
 * It sets up a state machine and excutes the states contained.
 * </p>
 * <p>
 * If you want to add a new state, see the constructor.
 * {@link frc.components.autonomous.Autonomous#Autonomous()}
 * <p>
 * This class implements the
 * {@link frc.components.IComponent IComponent} interface.
 * </p>
 *
 * @see frc.state_machine.StateMachine
 */
public final class Autonomous implements IComponent {

    /**
     * Autonomous constructor.
     * <p>
     * This is where new states are added to the state machine.
     * Any state added here will be run during autonomous in
     * the order they are added.
     * </p>
     * <p>
     * You can add states by using the following code:
     *
     * <pre>
     * stateMachine.addState(new StateName());
     * </pre>
     *
     * <i>Any added states must also exist in this directory as separate
     * classes.</i>
     * </p>
     *
     * @see frc.components.autonomous.DriveState1
     */
    public Autonomous() {}

    @Override
    public void update() {
    }
}
