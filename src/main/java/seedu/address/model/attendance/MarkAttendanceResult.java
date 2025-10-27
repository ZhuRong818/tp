package seedu.address.model.attendance;

import java.util.List;

import seedu.address.model.event.Event;
import seedu.address.model.person.Name;

/**
 * Represents the outcome of marking attendance.
 */
public class MarkAttendanceResult {

    private final Event event;
    private final List<Name> newlyMarkedMembers;
    private final List<Name> alreadyMarkedMembers;

    /**
     * Creates a result describing a mark-attendance operation.
     */
    public MarkAttendanceResult(Event event, List<Name> newlyMarkedMembers, List<Name> alreadyMarkedMembers) {
        this.event = event;
        this.newlyMarkedMembers = List.copyOf(newlyMarkedMembers);
        this.alreadyMarkedMembers = List.copyOf(alreadyMarkedMembers);
    }

    public Event getEvent() {
        return event;
    }

    public List<Name> getNewlyMarkedMembers() {
        return newlyMarkedMembers;
    }

    public List<Name> getAlreadyMarkedMembers() {
        return alreadyMarkedMembers;
    }
}
