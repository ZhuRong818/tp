package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.attendance.Attendance;
import seedu.address.model.event.Event;
import seedu.address.model.event.EventId;
import seedu.address.model.person.Name;

/**
 * Contains integration tests (interaction with the Model) and unit tests for MarkAttendanceCommand.
 */
public class MarkAttendanceCommandTest {

    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_markAttendance_success() {
        // Add an event to the model
        Event event = new seedu.address.testutil.EventBuilder()
                .withEventId("testEvent")
                .withDate("2023-12-25")
                .withDescription("Test Event")
                .build();
        model.addEvent(event);

        EventId eventId = event.getEventId();
        Name memberName = ALICE.getName();
        Attendance initialAttendance = new Attendance(eventId, memberName);
        model.addAttendance(initialAttendance);

        MarkAttendanceCommand markAttendanceCommand = new MarkAttendanceCommand(eventId, List.of(memberName));

        String newlyMarkedText = AttendanceMessageUtil.formatNames(List.of(memberName));
        String alreadyMarkedText = AttendanceMessageUtil.formatNames(List.of());
        String expectedMessage = String.format(MarkAttendanceCommand.MESSAGE_SUCCESS,
                event.getDescription(), newlyMarkedText, alreadyMarkedText);

        ModelManager expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.setAttendance(new Attendance(eventId, memberName),
                new Attendance(eventId, memberName, true));

        assertCommandSuccess(markAttendanceCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_eventNotFound_throwsCommandException() {
        EventId nonExistentEventId = new EventId("NonExistentEvent");
        Name memberName = ALICE.getName();

        MarkAttendanceCommand markAttendanceCommand = new MarkAttendanceCommand(nonExistentEventId,
                List.of(memberName));

        assertCommandFailure(markAttendanceCommand, model, MarkAttendanceCommand.MESSAGE_EVENT_NOT_FOUND);
    }

    @Test
    public void execute_memberNotFound_throwsCommandException() {
        // Add an event to the model
        Event event = new seedu.address.testutil.EventBuilder()
                .withEventId("testEvent")
                .withDate("2023-12-25")
                .withDescription("Test Event")
                .build();
        model.addEvent(event);

        EventId eventId = event.getEventId();
        Name nonExistentMemberName = new Name("Non Existent Member");

        MarkAttendanceCommand markAttendanceCommand = new MarkAttendanceCommand(eventId,
                List.of(nonExistentMemberName));

        assertCommandFailure(markAttendanceCommand, model,
                String.format(MarkAttendanceCommand.MESSAGE_MEMBER_NOT_FOUND, nonExistentMemberName));
    }

    @Test
    public void execute_duplicateAttendance_ignoresDuplicate() {
        // Add an event to the model
        Event event = new seedu.address.testutil.EventBuilder()
                .withEventId("testEvent")
                .withDate("2023-12-25")
                .withDescription("Test Event")
                .build();
        model.addEvent(event);

        EventId eventId = event.getEventId();
        Name memberName = ALICE.getName();

        // First attendance
        MarkAttendanceCommand markAttendanceCommand = new MarkAttendanceCommand(eventId, List.of(memberName));
        model.addAttendance(new Attendance(eventId, memberName, true));

        // Second attendance (duplicate)
        String expectedMessage = String.format(MarkAttendanceCommand.MESSAGE_SUCCESS,
                event.getDescription(),
                AttendanceMessageUtil.formatNames(List.of()),
                AttendanceMessageUtil.formatNames(List.of(memberName)));

        assertCommandSuccess(markAttendanceCommand, model, expectedMessage, model);
    }

    @Test
    public void equals() {
        EventId eventId1 = new EventId("Event1");
        EventId eventId2 = new EventId("Event2");
        Name memberName1 = new Name("Member1");
        Name memberName2 = new Name("Member2");

        MarkAttendanceCommand markAttendanceCommand1 = new MarkAttendanceCommand(eventId1, List.of(memberName1));
        MarkAttendanceCommand markAttendanceCommand2 = new MarkAttendanceCommand(eventId1, List.of(memberName1));
        MarkAttendanceCommand markAttendanceCommand3 = new MarkAttendanceCommand(eventId2, List.of(memberName1));
        MarkAttendanceCommand markAttendanceCommand4 = new MarkAttendanceCommand(eventId1, List.of(memberName2));

        // same object -> returns true
        assertTrue(markAttendanceCommand1.equals(markAttendanceCommand1));

        // same values -> returns true
        assertTrue(markAttendanceCommand1.equals(markAttendanceCommand2));

        // different eventId -> returns false
        assertFalse(markAttendanceCommand1.equals(markAttendanceCommand3));

        // different memberName -> returns false
        assertFalse(markAttendanceCommand1.equals(markAttendanceCommand4));

        // null -> returns false
        assertFalse(markAttendanceCommand1.equals(null));

        // different type -> returns false
        assertFalse(markAttendanceCommand1.equals(5));
    }
}
