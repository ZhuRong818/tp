package seedu.address.model;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import javafx.collections.ObservableList;
import seedu.address.commons.core.GuiSettings;
import seedu.address.model.attendance.AddAttendanceResult;
import seedu.address.model.attendance.Attendance;
import seedu.address.model.attendance.MarkAttendanceResult;
import seedu.address.model.attendance.exceptions.AttendanceOperationException;
import seedu.address.model.budget.Budget;
import seedu.address.model.common.Money;
import seedu.address.model.event.Event;
import seedu.address.model.event.EventId;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.task.Task;

/**
 * The API of the Model component.
 */
public interface Model {
    /** {@code Predicate} that always evaluate to true */
    Predicate<Person> PREDICATE_SHOW_ALL_PERSONS = unused -> true;
    Predicate<Event> PREDICATE_SHOW_ALL_EVENTS = unused -> true;
    Predicate<Task> PREDICATE_SHOW_ALL_TASKS = unused -> true;

    /**
     * Replaces user prefs data with the data in {@code userPrefs}.
     */
    void setUserPrefs(ReadOnlyUserPrefs userPrefs);

    /**
     * Returns the user prefs.
     */
    ReadOnlyUserPrefs getUserPrefs();

    /**
     * Returns the user prefs' GUI settings.
     */
    GuiSettings getGuiSettings();

    /**
     * Sets the user prefs' GUI settings.
     */
    void setGuiSettings(GuiSettings guiSettings);

    /**
     * Returns the user prefs' address book file path.
     */
    Path getAddressBookFilePath();

    /**
     * Sets the user prefs' address book file path.
     */
    void setAddressBookFilePath(Path addressBookFilePath);

    /**
     * Replaces address book data with the data in {@code addressBook}.
     */
    void setAddressBook(ReadOnlyAddressBook addressBook);

    /** Returns the AddressBook */
    ReadOnlyAddressBook getAddressBook();

    /**
     * Returns true if a person with the same identity as {@code person} exists in the address book.
     */
    boolean hasPerson(Person person);

    /**
     * Deletes the given person.
     * The person must exist in the address book.
     */
    void deletePerson(Person target);

    /**
     * Adds the given person.
     * {@code person} must not already exist in the address book.
     */
    void addPerson(Person person);

    /**
     * Replaces the given person {@code target} with {@code editedPerson}.
     * {@code target} must exist in the address book.
     * The person identity of {@code editedPerson} must not be the same as another existing person in the address book.
     */
    void setPerson(Person target, Person editedPerson);

    /**
     * Returns true if an event with the same identity as {@code event} exists in the address book.
     */
    boolean hasEvent(Event event);

    /**
     * Deletes the given event.
     * The event must exist in the address book.
     */
    void deleteEvent(Event target);

    /**
     * Adds the given event.
     * {@code event} must not already exist in the address book.
     */
    void addEvent(Event event);

    /**
     * Replaces the given event {@code target} with {@code editedEvent}.
     * {@code target} must exist in the address book.
     * The event identity of {@code editedEvent} must not be the same as another existing event in the address book.
     */
    void setEvent(Event target, Event editedEvent);

    /**
     * Returns the event with the specified event ID, or null if not found.
     */
    Event getEventByEventId(EventId eventId);

    //// task-level operations

    /**
     * Returns true if a task with the same identity as {@code task} exists in the address book.
     */
    boolean hasTask(Task task);

    /**
     * Deletes the given task.
     * The task must exist in the address book.
     */
    void deleteTask(Task target);

    /**
     * Adds the given task.
     * {@code task} must not already exist in the address book.
     */
    void addTask(Task task);

    /**
     * Replaces the given task {@code target} with {@code editedTask}.
     * {@code target} must exist in the address book.
     * The task identity of {@code editedTask} must not be the same as another existing task in the address book.
     */
    void setTask(Task target, Task editedTask);

    /**
     * Returns true if an attendance record with the same identity as {@code attendance} exists in the address book.
     */
    boolean hasAttendance(Attendance attendance);

    /**
     * Adds the given attendance record.
     * {@code attendance} must not already exist in the address book.
     */
    void addAttendance(Attendance attendance);

    /**
     * Replaces the given attendance record {@code target} with {@code editedAttendance}.
     */
    void setAttendance(Attendance target, Attendance editedAttendance);

    /**
     * Marks attendance for the given members and returns the result.
     */
    MarkAttendanceResult markAttendance(EventId eventId, List<Name> memberNames)
            throws AttendanceOperationException;

    /**
     * Adds attendance records for the given members and returns the result.
     */
    AddAttendanceResult addAttendance(EventId eventId, List<Name> memberNames)
            throws AttendanceOperationException;

    /**
     * Returns an unmodifiable view of the filtered event list
     */
    ObservableList<Event> getFilteredEventList();

    /**
     * Returns an unmodifiable view of the filtered task list
     */
    ObservableList<Task> getFilteredTaskList();

    /** Returns an unmodifiable view of the filtered person list */
    ObservableList<Person> getFilteredPersonList();

    /**
     * Updates the filter of the filtered person list to filter by the given {@code predicate}.
     * @throws NullPointerException if {@code predicate} is null.
     */
    void updateFilteredPersonList(Predicate<Person> predicate);

    /**
     * Updates the filter of the filtered event list to filter by the given {@code predicate}.
     * @throws NullPointerException if {@code predicate} is null.
     */
    void updateFilteredEventList(Predicate<Event> predicate);

    /**
     * Updates the filter of the filtered task list to filter by the given {@code predicate}.
     * @throws NullPointerException if {@code predicate} is null.
     */
    void updateFilteredTaskList(Predicate<Task> predicate);

    //=========== Undo/Redo Operations ========================================================================

    /**
     * Saves the current state of the address book for undo functionality.
     */
    void commit();

    /**
     * Undoes the last change by restoring the previous state.
     * @return true if undo was successful, false if there are no states to undo
     */
    boolean undo();

    /**
     * Redoes the last undone change.
     * @return true if redo was successful, false if there are no states to redo
     */
    boolean redo();

    /**
     * Returns true if there are states available to undo.
     */
    boolean canUndo();

    /**
     * Returns true if there are states available to redo.
     */
    boolean canRedo();

    /**
     * Removes the last committed state from the history.
     * This is used when a command fails after commit.
     */
    void rollbackLastCommit();

    //=========== Budget Operations ========================================================================

    default Optional<Budget> getBudget() {
        return Optional.empty();
    }

    default void setBudget(Budget budget) {}

    default void clearBudget() {}

    default Money computeTotalExpensesWithin(LocalDate start, LocalDate end) {
        return Money.zero();
    }

    default List<Event> getEventsWithin(LocalDate start, LocalDate end) {
        return java.util.Collections.emptyList();
    }
}
