package seedu.address.model.attendance;

import java.util.List;

import seedu.address.model.event.Event;
import seedu.address.model.person.Name;

/**
 * Represents the outcome of adding attendance records.
 */
public class AddAttendanceResult {

    private final Event event;
    private final List<Name> addedMembers;
    private final List<Name> duplicateMembers;

    public AddAttendanceResult(Event event, List<Name> addedMembers, List<Name> duplicateMembers) {
        this.event = event;
        this.addedMembers = List.copyOf(addedMembers);
        this.duplicateMembers = List.copyOf(duplicateMembers);
    }

    public Event getEvent() {
        return event;
    }

    public List<Name> getAddedMembers() {
        return addedMembers;
    }

    public List<Name> getDuplicateMembers() {
        return duplicateMembers;
    }
}

