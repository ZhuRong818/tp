package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.testutil.TypicalEvents.getTypicalAddressBook;

import java.util.List;

import org.junit.jupiter.api.Test;

import javafx.collections.ObservableList;
import seedu.address.commons.core.GuiSettings;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.ReadOnlyUserPrefs;
import seedu.address.model.UserPrefs;
import seedu.address.model.attendance.AddAttendanceResult;
import seedu.address.model.attendance.MarkAttendanceResult;
import seedu.address.model.attendance.exceptions.AttendanceOperationException;
import seedu.address.model.event.Event;
import seedu.address.model.event.EventId;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.task.Task;
import seedu.address.testutil.EventBuilder;

public class AddEventCommandTest {

    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void constructor_nullEvent_throwsNullPointerException() {
        seedu.address.testutil.Assert.assertThrows(NullPointerException.class, () -> new AddEventCommand(null));
    }

    @Test
    public void execute_eventAcceptedByModel_addSuccessful() throws Exception {
        Event validEvent = new EventBuilder().withEventId("unique_event").build();

        CommandResult commandResult = new AddEventCommand(validEvent).execute(model);

        assertEquals(String.format(AddEventCommand.MESSAGE_SUCCESS, validEvent.getDescription(), validEvent.getDate()),
                commandResult.getFeedbackToUser());
    }

    @Test
    public void execute_duplicateEvent_throwsCommandException() {
        Event eventInList = model.getAddressBook().getEventList().get(0);
        assertCommandFailure(new AddEventCommand(eventInList), model, AddEventCommand.MESSAGE_DUPLICATE_EVENT);
    }

    @Test
    public void equals() {
        Event event1 = new EventBuilder().withEventId("event1").build();
        Event event2 = new EventBuilder().withEventId("event2").build();
        AddEventCommand addEvent1Command = new AddEventCommand(event1);
        AddEventCommand addEvent2Command = new AddEventCommand(event2);

        // same object -> returns true
        assertTrue(addEvent1Command.equals(addEvent1Command));

        // same values -> returns true
        AddEventCommand addEvent1CommandCopy = new AddEventCommand(event1);
        assertTrue(addEvent1Command.equals(addEvent1CommandCopy));

        // different types -> returns false
        assertFalse(addEvent1Command.equals(1));

        // null -> returns false
        assertFalse(addEvent1Command.equals(null));

        // different event -> returns false
        assertFalse(addEvent1Command.equals(addEvent2Command));
    }

    @Test
    public void toStringMethod() {
        Event event = new EventBuilder().build();
        AddEventCommand addEventCommand = new AddEventCommand(event);
        String expected = AddEventCommand.class.getCanonicalName() + "{toAdd=" + event + "}";
        assertEquals(expected, addEventCommand.toString());
    }

    /**
     * A default model stub that have all of the methods failing.
     */
    private class ModelStub implements Model {
        @Override
        public void setUserPrefs(ReadOnlyUserPrefs userPrefs) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public ReadOnlyUserPrefs getUserPrefs() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public GuiSettings getGuiSettings() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setGuiSettings(GuiSettings guiSettings) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public java.nio.file.Path getAddressBookFilePath() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setAddressBookFilePath(java.nio.file.Path addressBookFilePath) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void addPerson(Person person) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setAddressBook(ReadOnlyAddressBook newData) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public ReadOnlyAddressBook getAddressBook() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public boolean hasPerson(Person person) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void deletePerson(Person target) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setPerson(Person target, Person editedPerson) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public ObservableList<Person> getFilteredPersonList() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void updateFilteredPersonList(java.util.function.Predicate<Person> predicate) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public boolean hasEvent(Event event) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void deleteEvent(Event target) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void addEvent(Event event) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setEvent(Event target, Event editedEvent) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public Event getEventByEventId(EventId eventId) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public ObservableList<Event> getFilteredEventList() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void updateFilteredEventList(java.util.function.Predicate<Event> predicate) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public boolean hasTask(Task task) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void deleteTask(Task target) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void addTask(Task task) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setTask(Task target, Task editedTask) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public ObservableList<Task> getFilteredTaskList() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void updateFilteredTaskList(java.util.function.Predicate<Task> predicate) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void commit() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public boolean undo() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public boolean redo() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public boolean canUndo() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public boolean canRedo() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void rollbackLastCommit() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public boolean hasAttendance(seedu.address.model.attendance.Attendance attendance) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void addAttendance(seedu.address.model.attendance.Attendance attendance) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public AddAttendanceResult addAttendance(EventId eventId, List<Name> memberNames)
                throws AttendanceOperationException {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setAttendance(seedu.address.model.attendance.Attendance target,
                seedu.address.model.attendance.Attendance editedAttendance) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public MarkAttendanceResult markAttendance(EventId eventId, List<Name> memberNames)
                throws AttendanceOperationException {
            throw new AssertionError("This method should not be called.");
        }
    }

    /**
     * A Model stub that contains a single event.
     */
    private class ModelStubWithEvent extends ModelStub {
        private final Event event;

        ModelStubWithEvent(Event event) {
            this.event = event;
        }

        @Override
        public boolean hasEvent(Event event) {
            return this.event.isSameEvent(event);
        }
    }

    /**
     * A Model stub that always accept the event being added.
     */
    private class ModelStubAcceptingEventAdded extends ModelStub {
        final java.util.ArrayList<Event> eventsAdded = new java.util.ArrayList<>();

        @Override
        public boolean hasEvent(Event event) {
            return false;
        }

        @Override
        public void addEvent(Event event) {
            eventsAdded.add(event);
        }

        @Override
        public ReadOnlyAddressBook getAddressBook() {
            return new AddressBook();
        }

        @Override
        public boolean hasAttendance(seedu.address.model.attendance.Attendance attendance) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void addAttendance(seedu.address.model.attendance.Attendance attendance) {
            throw new AssertionError("This method should not be called.");
        }
    }
}
