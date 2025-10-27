package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.List;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.attendance.AttendanceMessages;
import seedu.address.model.attendance.MarkAttendanceResult;
import seedu.address.model.attendance.exceptions.AttendanceOperationException;
import seedu.address.model.event.Event;
import seedu.address.model.event.EventId;
import seedu.address.model.person.Name;

/**
 * Marks members as attended for an event.
 */
public class MarkAttendanceCommand extends Command {

    public static final String COMMAND_WORD = "markattendance";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Marks members as attended for an event. "
            + "Parameters: e/EVENTID m/MEMBER[/MEMBER]...\n"
            + "Example: " + COMMAND_WORD + " e/Orientation2023 m/John Doe/Jane Smith";

    public static final String MESSAGE_SUCCESS = "Attendance for %1$s marked.\n"
            + "Newly marked: %2$s\nAlready marked: %3$s";
    public static final String MESSAGE_EVENT_NOT_FOUND = AttendanceMessages.MESSAGE_EVENT_NOT_FOUND;
    public static final String MESSAGE_MEMBER_NOT_FOUND = AttendanceMessages.MESSAGE_MEMBER_NOT_IN_ATTENDANCE;

    private final EventId eventId;
    private final List<Name> memberNames;

    /**
     * Creates a MarkAttendanceCommand to mark the specified members as attended for the specified event.
     */
    public MarkAttendanceCommand(EventId eventId, List<Name> memberNames) {
        requireNonNull(eventId);
        requireNonNull(memberNames);
        this.eventId = eventId;
        this.memberNames = List.copyOf(memberNames);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        try {
            MarkAttendanceResult result = model.markAttendance(eventId, memberNames);
            Event event = result.getEvent();
            String newlyMarkedText = AttendanceMessageUtil.formatNames(result.getNewlyMarkedMembers());
            String alreadyMarkedText = AttendanceMessageUtil.formatNames(result.getAlreadyMarkedMembers());
            return new CommandResult(String.format(MESSAGE_SUCCESS,
                    event.getDescription(), newlyMarkedText, alreadyMarkedText));
        } catch (AttendanceOperationException e) {
            throw new CommandException(e.getMessage(), e);
        }
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof MarkAttendanceCommand)) {
            return false;
        }

        MarkAttendanceCommand otherMarkAttendanceCommand = (MarkAttendanceCommand) other;
        return eventId.equals(otherMarkAttendanceCommand.eventId)
                && memberNames.equals(otherMarkAttendanceCommand.memberNames);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("eventId", eventId)
                .add("memberNames", memberNames)
                .toString();
    }
}
