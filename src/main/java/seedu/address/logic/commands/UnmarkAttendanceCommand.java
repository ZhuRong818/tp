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
 * Marks members as absent for an event.
 */
public class UnmarkAttendanceCommand extends Command {

    public static final String COMMAND_WORD = "unmarkattendance";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Marks members as absent for an event. "
            + "Format: " + COMMAND_WORD + " e/EVENTID m/MEMBER[/MEMBER]...\n"
            + "Example: " + COMMAND_WORD + " e/Orientation2023 m/John Doe/Jane Smith";

    public static final String MESSAGE_SUCCESS = AttendanceMessages.MESSAGE_UNMARK_ATTENDANCE_SUCCESS;
    public static final String MESSAGE_EVENT_NOT_FOUND = AttendanceMessages.MESSAGE_EVENT_NOT_FOUND;
    public static final String MESSAGE_MEMBER_NOT_FOUND = AttendanceMessages.MESSAGE_MEMBER_NOT_FOUND_IN_LIST;

    private static final Logger logger = LogsCenter.getLogger(UnmarkAttendanceCommand.class);

    private final EventId eventId;
    private final List<Name> memberNames;

    /**
     * Creates an UnmarkAttendanceCommand to mark the specified members as absent for the specified event.
     */
    public UnmarkAttendanceCommand(EventId eventId, List<Name> memberNames) {
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
        AttendanceUnmarkingSummary summary = unmarkAttendance(model, attendanceByName, targetNames);

        logger.fine(() -> String.format(
                "Unmarked attendance for event %s: newlyAbsent=%d, alreadyAbsent=%d",
                eventId,
                summary.getNewlyAbsentMembers().size(),
                summary.getAlreadyAbsentMembers().size()));

        assert summary.totalProcessed() == targetNames.size();

        String resultMessage = AttendanceMessages.buildUnmarkAttendanceResult(
                event.getEventId().toString(),
                summary.getNewlyAbsentMembers(),
                summary.getAlreadyAbsentMembers());
        return new CommandResult(resultMessage);
    }

    private AttendanceUnmarkingSummary unmarkAttendance(Model model,
                                                        Map<Name, Attendance> attendanceByName,
                                                        List<Name> targetNames) throws CommandException {
        List<Name> newlyAbsent = new ArrayList<>();
        List<Name> alreadyAbsent = new ArrayList<>();

        List<Attendance> attendancesToUnmark = new ArrayList<>();

        for (Name name : targetNames) {
            Attendance attendance = attendanceByName.get(name);
            if (attendance == null) {
                throw new CommandException(String.format(MESSAGE_MEMBER_NOT_FOUND, name));
            }

            if (!attendance.hasAttended()) {
                assert !alreadyAbsent.contains(name);
                alreadyAbsent.add(name);
                continue;
            }

            assert !newlyAbsent.contains(name);
            newlyAbsent.add(name);
            attendancesToUnmark.add(attendance);
        }

        for (Attendance attendance : attendancesToUnmark) {
            Attendance updatedAttendance = attendance.markAbsent();
            model.setAttendance(attendance, updatedAttendance);
        }

        return new AttendanceUnmarkingSummary(newlyAbsent, alreadyAbsent);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof UnmarkAttendanceCommand)) {
            return false;
        }

        UnmarkAttendanceCommand otherCommand = (UnmarkAttendanceCommand) other;
        return eventId.equals(otherCommand.eventId)
                && memberNames.equals(otherCommand.memberNames);
    }

    private static final class AttendanceUnmarkingSummary {
        private final List<Name> newlyAbsentMembers;
        private final List<Name> alreadyAbsentMembers;

        private AttendanceUnmarkingSummary(List<Name> newlyAbsentMembers, List<Name> alreadyAbsentMembers) {
            this.newlyAbsentMembers = List.copyOf(newlyAbsentMembers);
            this.alreadyAbsentMembers = List.copyOf(alreadyAbsentMembers);
        }

        private List<Name> getNewlyAbsentMembers() {
            return newlyAbsentMembers;
        }

        private List<Name> getAlreadyAbsentMembers() {
            return alreadyAbsentMembers;
        }

        private int totalProcessed() {
            return newlyAbsentMembers.size() + alreadyAbsentMembers.size();
        }
    }
}
