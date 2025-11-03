package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
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
 * Removes members from an event's attendance list.
 */
public class RemoveAttendanceCommand extends Command {

    public static final String COMMAND_WORD = "removeattendance";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Removes members from an event's attendance list. "
            + "Format: " + COMMAND_WORD + " e/EVENTID m/MEMBER[/MEMBER]...\n"
            + "Example: " + COMMAND_WORD + " e/Orientation2023 m/John Doe/Jane Smith";

    public static final String MESSAGE_EVENT_NOT_FOUND = AttendanceMessages.MESSAGE_EVENT_NOT_FOUND;
    public static final String MESSAGE_MEMBER_NOT_FOUND = AttendanceMessages.MESSAGE_MEMBER_NOT_FOUND_IN_LIST;

    private static final Logger logger = LogsCenter.getLogger(RemoveAttendanceCommand.class);

    private final EventId eventId;
    private final List<Name> memberNames;

    /**
     * Creates a RemoveAttendanceCommand to delete the specified members from the event's attendance list.
     */
    public RemoveAttendanceCommand(EventId eventId, List<Name> memberNames) {
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

        Map<Name, Attendance> attendanceByName = AttendanceCommandUtil.collectAttendanceByName(model, eventId);

        List<Name> targetNames = new ArrayList<>(new LinkedHashSet<>(memberNames));
        List<Name> removedMembers = removeAttendees(model, attendanceByName, targetNames);

        logger.fine(() -> String.format("Removed %d member(s) from event %s",
                removedMembers.size(), eventId));

        assert removedMembers.size() == targetNames.size();

        String resultMessage = AttendanceMessages.buildRemoveAttendanceResult(
                event.getEventId().toString(), removedMembers);
        return new CommandResult(resultMessage);
    }

    private List<Name> removeAttendees(Model model,
                                        Map<Name, Attendance> attendanceByName,
                                        List<Name> targetNames) throws CommandException {
        List<Name> removedMembers = new ArrayList<>();

        List<Attendance> attendancesToRemove = new ArrayList<>();

        for (Name name : targetNames) {
            Attendance attendance = attendanceByName.get(name);
            if (attendance == null) {
                throw new CommandException(String.format(MESSAGE_MEMBER_NOT_FOUND, name));
            }

            attendancesToRemove.add(attendance);
            removedMembers.add(name);
        }

        for (Attendance attendance : attendancesToRemove) {
            model.removeAttendance(attendance);
        }

        return removedMembers;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof RemoveAttendanceCommand)) {
            return false;
        }

        RemoveAttendanceCommand otherCommand = (RemoveAttendanceCommand) other;
        return eventId.equals(otherCommand.eventId)
                && memberNames.equals(otherCommand.memberNames);
    }
}
