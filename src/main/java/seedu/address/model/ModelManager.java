package seedu.address.model;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.logging.Logger;

import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import seedu.address.commons.core.GuiSettings;
import seedu.address.commons.core.LogsCenter;
import seedu.address.model.attendance.AddAttendanceResult;
import seedu.address.model.attendance.Attendance;
import seedu.address.model.attendance.AttendanceMessages;
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
 * Represents the in-memory model of the address book data.
 */
public class ModelManager implements Model {
    private static final Logger logger = LogsCenter.getLogger(ModelManager.class);

    private final VersionedAddressBook addressBook;
    private final UserPrefs userPrefs;
    private final FilteredList<Person> filteredPersons;
    private final FilteredList<Event> filteredEvents;
    private final FilteredList<Task> filteredTasks;
    private Budget budget; // nullable

    /**
     * Initializes a ModelManager with the given addressBook and userPrefs.
     */
    public ModelManager(ReadOnlyAddressBook addressBook, ReadOnlyUserPrefs userPrefs) {
        requireAllNonNull(addressBook, userPrefs);

        logger.fine("Initializing with address book: " + addressBook + " and user prefs " + userPrefs);

        this.addressBook = new VersionedAddressBook(addressBook);
        this.userPrefs = new UserPrefs(userPrefs);
        filteredPersons = new FilteredList<>(this.addressBook.getPersonList());
        filteredEvents = new FilteredList<>(this.addressBook.getEventList());
        filteredTasks = new FilteredList<>(this.addressBook.getTaskList());
        this.budget = addressBook.getBudget().orElse(null);
    }

    public ModelManager() {
        this(new VersionedAddressBook(), new UserPrefs());
    }

    //=========== UserPrefs ==================================================================================

    @Override
    public void setUserPrefs(ReadOnlyUserPrefs userPrefs) {
        requireNonNull(userPrefs);
        this.userPrefs.resetData(userPrefs);
    }

    @Override
    public ReadOnlyUserPrefs getUserPrefs() {
        return userPrefs;
    }

    @Override
    public GuiSettings getGuiSettings() {
        return userPrefs.getGuiSettings();
    }

    @Override
    public void setGuiSettings(GuiSettings guiSettings) {
        requireNonNull(guiSettings);
        userPrefs.setGuiSettings(guiSettings);
    }

    @Override
    public Path getAddressBookFilePath() {
        return userPrefs.getAddressBookFilePath();
    }

    @Override
    public void setAddressBookFilePath(Path addressBookFilePath) {
        requireNonNull(addressBookFilePath);
        userPrefs.setAddressBookFilePath(addressBookFilePath);
    }

    //=========== AddressBook ================================================================================

    @Override
    public void setAddressBook(ReadOnlyAddressBook addressBook) {
        this.addressBook.resetData(addressBook);
        this.budget = addressBook.getBudget().orElse(null);
    }

    @Override
    public ReadOnlyAddressBook getAddressBook() {
        return addressBook;
    }

    @Override
    public boolean hasPerson(Person person) {
        requireNonNull(person);
        return addressBook.hasPerson(person);
    }

    @Override
    public void deletePerson(Person target) {
        logger.info("Deleting person: " + target.getName());
        addressBook.removePerson(target);
        logger.fine("Person deleted successfully. Total persons: " + addressBook.getPersonList().size());
    }

    @Override
    public void addPerson(Person person) {
        logger.info("Adding person: " + person.getName());
        addressBook.addPerson(person);
        updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
        logger.fine("Person added successfully. Total persons: " + addressBook.getPersonList().size());
    }

    @Override
    public void setPerson(Person target, Person editedPerson) {
        requireAllNonNull(target, editedPerson);
        logger.info("Editing person: " + target.getName() + " -> " + editedPerson.getName());
        addressBook.setPerson(target, editedPerson);
        logger.fine("Person edited successfully");
    }

    @Override
    public boolean hasEvent(Event event) {
        requireNonNull(event);
        return addressBook.hasEvent(event);
    }

    @Override
    public void deleteEvent(Event target) {
        addressBook.removeEvent(target);
    }

    @Override
    public void addEvent(Event event) {
        addressBook.addEvent(event);
        updateFilteredEventList(PREDICATE_SHOW_ALL_EVENTS);
    }

    @Override
    public void setEvent(Event target, Event editedEvent) {
        requireAllNonNull(target, editedEvent);

        addressBook.setEvent(target, editedEvent);
    }

    @Override
    public Event getEventByEventId(EventId eventId) {
        requireNonNull(eventId);
        return addressBook.getEventByEventId(eventId);
    }

    //=========== Task Management ==================================================================================

    @Override
    public boolean hasTask(Task task) {
        requireNonNull(task);
        return addressBook.hasTask(task);
    }

    @Override
    public void deleteTask(Task target) {
        addressBook.removeTask(target);
    }

    @Override
    public void addTask(Task task) {
        addressBook.addTask(task);
        updateFilteredTaskList(PREDICATE_SHOW_ALL_TASKS);
    }

    @Override
    public void setTask(Task target, Task editedTask) {
        requireAllNonNull(target, editedTask);

        addressBook.setTask(target, editedTask);
    }

    @Override
    public boolean hasAttendance(Attendance attendance) {
        requireNonNull(attendance);
        return addressBook.hasAttendance(attendance);
    }

    @Override
    public void addAttendance(Attendance attendance) {
        addressBook.addAttendance(attendance);
    }

    @Override
    public AddAttendanceResult addAttendance(EventId eventId, List<Name> memberNames)
            throws AttendanceOperationException {
        requireNonNull(eventId);
        requireNonNull(memberNames);

        Event event = requireEvent(eventId);
        List<Name> uniqueNames = deduplicatePreservingOrder(memberNames);

        List<Name> addedMembers = new ArrayList<>();
        List<Name> duplicateMembers = new ArrayList<>();

        for (Name name : uniqueNames) {
            if (!memberExists(name)) {
                throw new AttendanceOperationException(
                        String.format(AttendanceMessages.MESSAGE_MEMBER_NOT_FOUND, name));
            }

            Attendance attendance = new Attendance(eventId, name);
            if (addressBook.hasAttendance(attendance)) {
                duplicateMembers.add(name);
                continue;
            }

            addressBook.addAttendance(attendance);
            addedMembers.add(name);
        }

        logger.fine(() -> String.format("Added %d attendance records (skipped %d duplicates) for event %s.",
                addedMembers.size(), duplicateMembers.size(), eventId));
        return new AddAttendanceResult(event, addedMembers, duplicateMembers);
    }

    @Override
    public void setAttendance(Attendance target, Attendance editedAttendance) {
        requireAllNonNull(target, editedAttendance);
        addressBook.setAttendance(target, editedAttendance);
    }

    @Override
    public MarkAttendanceResult markAttendance(EventId eventId, List<Name> memberNames)
            throws AttendanceOperationException {
        requireNonNull(eventId);
        requireNonNull(memberNames);

        Event event = requireEvent(eventId);
        List<Name> uniqueNames = deduplicatePreservingOrder(memberNames);
        Map<Name, Attendance> attendanceByName = indexAttendanceByName(eventId);

        List<Name> newlyMarked = new ArrayList<>();
        List<Name> alreadyMarked = new ArrayList<>();

        for (Name name : uniqueNames) {
            Attendance attendance = attendanceByName.get(name);
            if (attendance == null) {
                throw new AttendanceOperationException(
                        String.format(AttendanceMessages.MESSAGE_MEMBER_NOT_IN_ATTENDANCE, name));
            }

            if (attendance.hasAttended()) {
                alreadyMarked.add(name);
                continue;
            }

            Attendance updatedAttendance = attendance.markAttended();
            addressBook.setAttendance(attendance, updatedAttendance);
            newlyMarked.add(name);
        }

        logger.fine(() -> String.format("Marked attendance for %d members (skipped %d already marked) in event %s.",
                newlyMarked.size(), alreadyMarked.size(), eventId));
        return new MarkAttendanceResult(event, newlyMarked, alreadyMarked);
    }

    //=========== Filtered Person List Accessors =============================================================

    /**
     * Returns an unmodifiable view of the list of {@code Person} backed by the internal list of
     * {@code versionedAddressBook}
     */
    @Override
    public ObservableList<Person> getFilteredPersonList() {
        return filteredPersons;
    }

    @Override
    public void updateFilteredPersonList(Predicate<Person> predicate) {
        requireNonNull(predicate);
        filteredPersons.setPredicate(predicate);
    }

    //=========== Filtered Event List Accessors =============================================================

    /**
     * Returns an unmodifiable view of the list of {@code Event} backed by the internal list of
     * {@code addressBook}
     */
    @Override
    public ObservableList<Event> getFilteredEventList() {
        return filteredEvents;
    }

    @Override
    public void updateFilteredEventList(Predicate<Event> predicate) {
        requireNonNull(predicate);
        filteredEvents.setPredicate(predicate);
    }

    //=========== Filtered Task List Accessors =============================================================

    /**
     * Returns an unmodifiable view of the list of {@code Task} backed by the internal list of
     * {@code addressBook}
     */
    @Override
    public ObservableList<Task> getFilteredTaskList() {
        return filteredTasks;
    }

    @Override
    public void updateFilteredTaskList(Predicate<Task> predicate) {
        requireNonNull(predicate);
        filteredTasks.setPredicate(predicate);
    }

    //=========== Undo/Redo Operations ========================================================================

    @Override
    public void commit() {
        logger.info("Committing current state for undo functionality");
        addressBook.commit();
        logger.fine("State committed successfully. Undo history size: " + addressBook.getUndoCount());
    }

    @Override
    public boolean undo() {
        logger.info("Attempting to undo last operation");
        boolean result = addressBook.undo();
        if (result) {
            logger.info("Undo successful. Updating filtered lists");
            // Update filtered lists to reflect the restored state
            updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
            updateFilteredEventList(PREDICATE_SHOW_ALL_EVENTS);
            updateFilteredTaskList(PREDICATE_SHOW_ALL_TASKS);
            logger.fine("Filtered lists updated. Remaining undo operations: " + addressBook.getUndoCount());
        } else {
            logger.warning("Undo failed - no operations to undo");
        }
        return result;
    }

    @Override
    public boolean redo() {
        logger.info("Attempting to redo last undone operation");
        boolean result = addressBook.redo();
        if (result) {
            logger.info("Redo successful. Updating filtered lists");
            // Update filtered lists to reflect the restored state
            updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
            updateFilteredEventList(PREDICATE_SHOW_ALL_EVENTS);
            updateFilteredTaskList(PREDICATE_SHOW_ALL_TASKS);
            logger.fine("Filtered lists updated. Remaining redo operations: " + addressBook.getRedoCount());
        } else {
            logger.warning("Redo failed - no operations to redo");
        }
        return result;
    }

    @Override
    public boolean canUndo() {
        return addressBook.canUndo();
    }

    @Override
    public boolean canRedo() {
        return addressBook.canRedo();
    }

    @Override
    public void rollbackLastCommit() {
        logger.info("Rolling back last commit due to command failure");
        addressBook.rollbackLastCommit();
        logger.fine("Rollback completed. Undo history size: " + addressBook.getUndoCount());
    }

    //=========== Budget Operations ========================================================================

    @Override
    public Optional<Budget> getBudget() {
        return Optional.ofNullable(budget);
    }

    @Override
    public void setBudget(Budget budget) {
        this.budget = budget;
        // AddressBook persists budget for storage roundtrip
        this.addressBook.setBudget(budget);
    }

    @Override
    public void clearBudget() {
        this.budget = null;
        this.addressBook.clearBudget();
    }

    @Override
    public Money computeTotalExpensesWithin(LocalDate start, LocalDate end) {
        Money total = Money.zero();
        for (Event e : addressBook.getEventList()) {
            LocalDate d = e.getDate();
            if ((d.isEqual(start) || d.isAfter(start)) && (d.isEqual(end) || d.isBefore(end))) {
                total = total.plus(e.getExpense());
            }
        }
        return total;
    }

    @Override
    public List<Event> getEventsWithin(LocalDate start, LocalDate end) {
        List<Event> result = new ArrayList<>();
        for (Event e : addressBook.getEventList()) {
            LocalDate d = e.getDate();
            if ((d.isEqual(start) || d.isAfter(start)) && (d.isEqual(end) || d.isBefore(end))) {
                result.add(e);
            }
        }
        return result;
    }

    private Event requireEvent(EventId eventId) throws AttendanceOperationException {
        Event event = addressBook.getEventByEventId(eventId);
        if (event == null) {
            throw new AttendanceOperationException(AttendanceMessages.MESSAGE_EVENT_NOT_FOUND);
        }
        return event;
    }

    private List<Name> deduplicatePreservingOrder(List<Name> names) {
        return new ArrayList<>(new LinkedHashSet<>(names));
    }

    private Map<Name, Attendance> indexAttendanceByName(EventId eventId) {
        Map<Name, Attendance> attendanceByName = new LinkedHashMap<>();
        for (Attendance attendance : addressBook.getAttendanceList()) {
            if (!attendance.getEventId().equals(eventId)) {
                continue;
            }
            Attendance existing = attendanceByName.putIfAbsent(attendance.getMemberName(), attendance);
            assert existing == null : "Attendance list should not contain duplicate members for the same event.";
        }
        return attendanceByName;
    }

    private boolean memberExists(Name name) {
        return addressBook.getPersonList().stream().anyMatch(person -> person.getName().equals(name));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof ModelManager)) {
            return false;
        }

        ModelManager otherModelManager = (ModelManager) other;
        return addressBook.equals(otherModelManager.addressBook)
                && userPrefs.equals(otherModelManager.userPrefs)
                && filteredPersons.equals(otherModelManager.filteredPersons)
                && filteredEvents.equals(otherModelManager.filteredEvents);
    }

}
