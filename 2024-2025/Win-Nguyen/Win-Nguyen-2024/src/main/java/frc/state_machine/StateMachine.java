package frc.state_machine;

import java.util.ArrayList;
import java.util.List;

/**
 * State Machine class for controlling a state machine.
 * This is mainly used during auto.
 *
 * @see frc.components.autonomous.Autonomous
 */
public final class StateMachine {

    /**
     * The current state of the state machine.
     */
    private State currentState;

    /**
     * The list of states to run.
     */
    private List<State> states = new ArrayList<State>();

    /**
     * Adds a state to the state machine.
     *
     * @param state  The state to add to the state machine.
     */
    public void addState(final State state) {
        states.add(state);
    }

    /**
    * Method called to run the state machine.
    */
    public void run() {
        if (!states.isEmpty()) {
            if (currentState == null && states.get(0).isValid()) {
                currentState = states.get(0);
                currentState.onEnter();
                states.remove(0);
            }
        }

        if (currentState != null) {
            if (currentState.run()) {
                currentState.onExit();
                currentState = null;
            }
        }
    }
}
