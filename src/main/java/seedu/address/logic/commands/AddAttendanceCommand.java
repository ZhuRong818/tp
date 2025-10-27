package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.List;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.attendance.AddAttendanceResult;
import seedu.address.model.attendance.AttendanceMessages;
import seedu.address.model.attendance.exceptions.AttendanceOperationException;
import seedu.address.model.event.Event;
import seedu.address.model.event.EventId;
import seedu.address.model.person.Name;

/**
 * Adds members to the attendance list of an event.
 */
public class AddAttendanceCommand extends Command {

    public static final String COMMAND_WORD = "addattendance";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Adds members to an event's attendance list. "
            + "Parameters: e/EVENTID m/MEMBER[/MEMBER]...\n"
            + "Example: " + COMMAND_WORD + " e/Orientation2023 m/John Doe/Jane Smith";

    public static final String MESSAGE_EVENT_NOT_FOUND = AttendanceMessages.MESSAGE_EVENT_NOT_FOUND;
    public static final String MESSAGE_MEMBER_NOT_FOUND = AttendanceMessages.MESSAGE_MEMBER_NOT_FOUND;
    public static final String MESSAGE_SUCCESS = "Attendance list for %1$s updated.\nAdded: %2$s%3$s";

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

        try {
            AddAttendanceResult result = model.addAttendance(eventId, memberNames);
            Event event = result.getEvent();
            String addedText = AttendanceMessageUtil.formatNames(result.getAddedMembers());
            List<Name> duplicateMembers = result.getDuplicateMembers();
            String duplicateMessage = duplicateMembers.isEmpty()
                    ? ""
                    : "\n" + AttendanceMessageUtil.formatAlreadyAddedMessage(duplicateMembers);
            return new CommandResult(String.format(MESSAGE_SUCCESS,
                    event.getDescription(), addedText, duplicateMessage));
        } catch (AttendanceOperationException e) {
            throw new CommandException(e.getMessage(), e);
        }
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
}
