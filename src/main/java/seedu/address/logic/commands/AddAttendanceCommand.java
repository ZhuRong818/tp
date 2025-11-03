package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.logic.AttendanceMessages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.attendance.Attendance;
import seedu.address.model.event.Event;
import seedu.address.model.event.EventId;
import seedu.address.model.person.Name;

/**
 * Adds members to the attendance list of an event.
 */
public class AddAttendanceCommand extends Command {

    public static final String COMMAND_WORD = "addattendance";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Adds members to an event's attendance list. "
            + "Format: " + COMMAND_WORD + " e/EVENTID m/MEMBER[/MEMBER]...\n"
            + "Example: " + COMMAND_WORD + " e/Orientation2023 m/John Doe/Jane Smith";

    public static final String MESSAGE_EVENT_NOT_FOUND = AttendanceMessages.MESSAGE_EVENT_NOT_FOUND;
    public static final String MESSAGE_MEMBER_NOT_FOUND = AttendanceMessages.MESSAGE_MEMBER_NOT_FOUND;
    public static final String MESSAGE_RESULT = AttendanceMessages.MESSAGE_ADD_ATTENDANCE_RESULT;

    private static final Logger logger = LogsCenter.getLogger(AddAttendanceCommand.class);

    private final EventId eventId;
    private final List<Name> memberNames;
    /**
     * Creates an {@code AddAttendanceCommand} to add the given members to the specified event.
     *
     * @param eventId The ID of the event to which members are added.
     * @param memberNames The list of member names to add to the event's attendance.
     */
    public AddAttendanceCommand(EventId eventId, List<Name> memberNames) {
        requireNonNull(eventId);
        requireNonNull(memberNames);
        this.eventId = eventId;
        this.memberNames = List.copyOf(memberNames);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        Event event = model.getEventByEventId(eventId);
        if (event == null) {
            throw new CommandException(MESSAGE_EVENT_NOT_FOUND);
        }

        Set<Name> uniqueNames = new LinkedHashSet<>(memberNames);
        AttendanceUpdateSummary summary = addMembersToEvent(model, uniqueNames);

        logger.fine(() -> String.format(
                "Processed %d member(s) for event %s: added=%d, duplicates=%d",
                uniqueNames.size(), eventId,
                summary.getAddedMembers().size(),
                summary.getDuplicateMembers().size()));

        assert summary.totalProcessed() == uniqueNames.size();

        String resultMessage = AttendanceMessages.buildAddAttendanceResult(
                event.getEventId().toString(),
                summary.getAddedMembers(),
                summary.getDuplicateMembers());

        return new CommandResult(resultMessage);
    }

    private AttendanceUpdateSummary addMembersToEvent(Model model,
                                                      Set<Name> uniqueNames) throws CommandException {
        List<Name> membersToAdd = new ArrayList<>();
        List<Name> duplicateMembers = new ArrayList<>();

        for (Name name : uniqueNames) {
            if (!memberExists(model, name)) {
                throw new CommandException(String.format(MESSAGE_MEMBER_NOT_FOUND, name));
            }

            Attendance attendance = new Attendance(eventId, name);
            if (model.hasAttendance(attendance)) {
                assert !duplicateMembers.contains(name);
                duplicateMembers.add(name);
                continue;
            }

            membersToAdd.add(name);
        }

        List<Name> addedMembers = new ArrayList<>(membersToAdd.size());
        for (Name name : membersToAdd) {
            Attendance attendance = new Attendance(eventId, name);
            model.addAttendance(attendance);
            addedMembers.add(name);
        }

        return new AttendanceUpdateSummary(addedMembers, duplicateMembers);
    }

    private boolean memberExists(Model model, Name name) {
        return model.getAddressBook().getPersonList().stream()
                .anyMatch(person -> person.getName().equals(name));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof AddAttendanceCommand)) {
            return false;
        }

        AddAttendanceCommand otherCommand = (AddAttendanceCommand) other;
        return eventId.equals(otherCommand.eventId)
                && memberNames.equals(otherCommand.memberNames);
    }

    private static final class AttendanceUpdateSummary {
        private final List<Name> addedMembers;
        private final List<Name> duplicateMembers;

        private AttendanceUpdateSummary(List<Name> addedMembers, List<Name> duplicateMembers) {
            this.addedMembers = List.copyOf(addedMembers);
            this.duplicateMembers = List.copyOf(duplicateMembers);
        }

        private List<Name> getAddedMembers() {
            return addedMembers;
        }

        private List<Name> getDuplicateMembers() {
            return duplicateMembers;
        }

        private int totalProcessed() {
            return addedMembers.size() + duplicateMembers.size();
        }
    }
}
