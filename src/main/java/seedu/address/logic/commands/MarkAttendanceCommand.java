package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.AttendanceMessages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.attendance.Attendance;
import seedu.address.model.event.Event;
import seedu.address.model.event.EventId;
import seedu.address.model.person.Name;

/**
 * Marks members as attended for an event.
 */
public class MarkAttendanceCommand extends Command {

    public static final String COMMAND_WORD = "markattendance";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Marks members as attended for an event. "
            + "Format: " + COMMAND_WORD + " e/EVENTID m/MEMBER[/MEMBER]...\n"
            + "Example: " + COMMAND_WORD + " e/Orientation2023 m/John Doe/Jane Smith";

    public static final String MESSAGE_SUCCESS = AttendanceMessages.MESSAGE_MARK_ATTENDANCE_SUCCESS;
    public static final String MESSAGE_EVENT_NOT_FOUND = AttendanceMessages.MESSAGE_EVENT_NOT_FOUND;
    public static final String MESSAGE_MEMBER_NOT_FOUND = AttendanceMessages.MESSAGE_MEMBER_NOT_FOUND_IN_LIST;

    private static final Logger logger = LogsCenter.getLogger(MarkAttendanceCommand.class);

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

        // Check if event exists
        Event event = model.getEventByEventId(eventId);
        if (event == null) {
            throw new CommandException(MESSAGE_EVENT_NOT_FOUND);
        }

        Map<Name, Attendance> attendanceByName = AttendanceCommandUtil.collectAttendanceByName(model, eventId);

        List<Name> targetNames = new ArrayList<>(new LinkedHashSet<>(memberNames));
        AttendanceMarkingSummary summary = markAttendance(model, attendanceByName, targetNames);

        logger.fine(() -> String.format(
                "Marked attendance for event %s: newlyMarked=%d, alreadyMarked=%d",
                eventId,
                summary.getNewlyMarkedMembers().size(),
                summary.getAlreadyMarkedMembers().size()));

        assert summary.totalProcessed() == targetNames.size();

        String resultMessage = AttendanceMessages.buildMarkAttendanceResult(
                event.getEventId().toString(),
                summary.getNewlyMarkedMembers(),
                summary.getAlreadyMarkedMembers());
        return new CommandResult(resultMessage);
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

    private AttendanceMarkingSummary markAttendance(Model model,
                                                    Map<Name, Attendance> attendanceByName,
                                                    List<Name> targetNames) throws CommandException {
        List<Name> newlyMarked = new ArrayList<>();
        List<Name> alreadyMarked = new ArrayList<>();

        List<Attendance> attendancesToMark = new ArrayList<>();

        for (Name name : targetNames) {
            Attendance attendance = attendanceByName.get(name);
            if (attendance == null) {
                throw new CommandException(String.format(MESSAGE_MEMBER_NOT_FOUND, name));
            }

            if (attendance.hasAttended()) {
                assert !alreadyMarked.contains(name);
                alreadyMarked.add(name);
                continue;
            }

            assert !newlyMarked.contains(name);
            newlyMarked.add(name);
            attendancesToMark.add(attendance);
        }

        for (Attendance attendance : attendancesToMark) {
            Attendance updatedAttendance = attendance.markAttended();
            model.setAttendance(attendance, updatedAttendance);
        }

        return new AttendanceMarkingSummary(newlyMarked, alreadyMarked);
    }

    private static final class AttendanceMarkingSummary {
        private final List<Name> newlyMarkedMembers;
        private final List<Name> alreadyMarkedMembers;

        private AttendanceMarkingSummary(List<Name> newlyMarkedMembers, List<Name> alreadyMarkedMembers) {
            this.newlyMarkedMembers = List.copyOf(newlyMarkedMembers);
            this.alreadyMarkedMembers = List.copyOf(alreadyMarkedMembers);
        }

        private List<Name> getNewlyMarkedMembers() {
            return newlyMarkedMembers;
        }

        private List<Name> getAlreadyMarkedMembers() {
            return alreadyMarkedMembers;
        }

        private int totalProcessed() {
            return newlyMarkedMembers.size() + alreadyMarkedMembers.size();
        }
    }
}
