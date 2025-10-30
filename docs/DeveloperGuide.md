---
  layout: default.md
  title: "Developer Guide"
  pageNav: 3
---

# ClubHub Developer Guide

<!-- * Table of Contents -->
<page-nav-print />

--------------------------------------------------------------------------------------------------------------------

## **Acknowledgements**

- Project scaffold adapted from SE-EDU: AddressBook-Level3 (AB3) `https://github.com/se-edu/addressbook-level3`
- Documentation structure and diagrams adapted from SE-EDU guides
- UI built with JavaFX `https://openjfx.io/`
- JSON serialization with Jackson `https://github.com/FasterXML/jackson`
- Testing with JUnit 5 `https://junit.org/junit5/`
- PlantUML for diagrams `https://plantuml.com/`

--------------------------------------------------------------------------------------------------------------------

## **Setting up, getting started**

Refer to the guide [_Setting up and getting started_](SettingUp.md).

--------------------------------------------------------------------------------------------------------------------

## **Design**

### Architecture

<puml src="diagrams/ArchitectureDiagram.puml" width="280" />

The ***Architecture Diagram*** given above explains the high-level design of the App.

Given below is a quick overview of main components and how they interact with each other.

**Main components of the architecture**

**`Main`** (consisting of classes [`Main`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/Main.java) and [`MainApp`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/MainApp.java)) is in charge of the app launch and shut down.
* At app launch, it initializes the other components in the correct sequence, and connects them up with each other.
* At shut down, it shuts down the other components and invokes cleanup methods where necessary.

The bulk of the app's work is done by the following four components:

* [**`UI`**](#ui-component): The UI of the App.
* [**`Logic`**](#logic-component): The command executor.
* [**`Model`**](#model-component): Holds the data of the App in memory.
* [**`Storage`**](#storage-component): Reads data from, and writes data to, the hard disk.

[**`Commons`**](#common-classes) represents a collection of classes used by multiple other components.

**How the architecture components interact with each other**

The *Sequence Diagram* below shows how the components interact with each other for the scenario where the user issues the command `delete 1`.

<puml src="diagrams/ArchitectureSequenceDiagram.puml" width="574" />

Each of the four main components (also shown in the diagram above),

* defines its *API* in an `interface` with the same name as the Component.
* implements its functionality using a concrete `{Component Name}Manager` class (which follows the corresponding API `interface` mentioned in the previous point.

For example, the `Logic` component defines its API in the `Logic.java` interface and implements its functionality using the `LogicManager.java` class which follows the `Logic` interface. Other components interact with a given component through its interface rather than the concrete class (reason: to prevent outside component's being coupled to the implementation of a component), as illustrated in the (partial) class diagram below.

<puml src="diagrams/ComponentManagers.puml" width="300" />

The sections below give more details of each component.

### UI component

The **API** of this component is specified in [`Ui.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/ui/Ui.java)

<puml src="diagrams/UiClassDiagram.puml" width="550" alt="Structure of the UI Component"/>

The UI consists of a `MainWindow` that is made up of parts e.g.`CommandBox`, `ResultDisplay`, `PersonListPanel`, `StatusBarFooter` etc. All these, including the `MainWindow`, inherit from the abstract `UiPart` class which captures the commonalities between classes that represent parts of the visible GUI.

The `UI` component uses the JavaFx UI framework. The layout of these UI parts are defined in matching `.fxml` files that are in the `src/main/resources/view` folder. For example, the layout of the [`MainWindow`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/ui/MainWindow.java) is specified in [`MainWindow.fxml`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/resources/view/MainWindow.fxml)

The `UI` component,

* executes user commands using the `Logic` component.
* listens for changes to `Model` data so that the UI can be updated with the modified data.
* keeps a reference to the `Logic` component, because the `UI` relies on the `Logic` to execute commands.
* depends on some classes in the `Model` component, as it displays `Person` object residing in the `Model`.

### Logic component

**API** : [`Logic.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/logic/Logic.java)

Here's a (partial) class diagram of the `Logic` component:

<puml src="diagrams/LogicClassDiagram.puml" width="550"/>

The sequence diagram below illustrates the interactions within the `Logic` component, taking `execute("delete 1")` API call as an example.

<puml src="diagrams/DeleteSequenceDiagram.puml" width="550" alt="Interactions Inside the Logic Component for the `delete 1` Command" />

<box type="info" seamless>

**Note:** The lifeline for `DeleteCommandParser` should end at the destroy marker (X) but due to a limitation of PlantUML, the lifeline continues till the end of diagram.
</box>

How the `Logic` component works:

1. When `Logic` is called upon to execute a command, it is passed to an `AddressBookParser` object which in turn creates a parser that matches the command (e.g., `DeleteCommandParser`) and uses it to parse the command.
1. This results in a `Command` object (more precisely, an object of one of its subclasses e.g., `DeleteCommand`) which is executed by the `LogicManager`.
1. The command can communicate with the `Model` when it is executed (e.g. to delete a person).<br>
   Note that although this is shown as a single step in the diagram above (for simplicity), in the code it can take several interactions (between the command object and the `Model`) to achieve.
1. The result of the command execution is encapsulated as a `CommandResult` object which is returned back from `Logic`.

Here are the other classes in `Logic` (omitted from the class diagram above) that are used for parsing a user command:

<puml src="diagrams/ParserClasses.puml" width="600"/>

How the parsing works:
* When called upon to parse a user command, the `AddressBookParser` class creates an `XYZCommandParser` (`XYZ` is a placeholder for the specific command name e.g., `AddCommandParser`) which uses the other classes shown above to parse the user command and create a `XYZCommand` object (e.g., `AddCommand`) which the `AddressBookParser` returns back as a `Command` object.
* All `XYZCommandParser` classes (e.g., `AddCommandParser`, `DeleteCommandParser`, ...) inherit from the `Parser` interface so that they can be treated similarly where possible e.g, during testing.

### Model component
**API** : [`Model.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/model/Model.java)

<puml src="diagrams/ModelClassDiagram.puml" width="450" />


The `Model` component,

* stores the address book data i.e., all `Person` objects (which are contained in a `UniquePersonList` object).
* stores the currently 'selected' `Person` objects (e.g., results of a search query) as a separate _filtered_ list which is exposed to outsiders as an unmodifiable `ObservableList<Person>` that can be 'observed' e.g. the UI can be bound to this list so that the UI automatically updates when the data in the list change.
* stores a `UserPref` object that represents the user’s preferences. This is exposed to the outside as a `ReadOnlyUserPref` objects.
* does not depend on any of the other three components (as the `Model` represents data entities of the domain, they should make sense on their own without depending on other components)

<box type="info" seamless>

**Note:** An alternative (arguably, a more OOP) model is given below. It has a `Tag` list in the `AddressBook`, which `Person` references. This allows `AddressBook` to only require one `Tag` object per unique tag, instead of each `Person` needing their own `Tag` objects.<br>

<puml src="diagrams/BetterModelClassDiagram.puml" width="450" />

</box>


### Storage component

**API** : [`Storage.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/storage/Storage.java)

<puml src="diagrams/StorageClassDiagram.puml" width="550" />

The `Storage` component,
* can save both address book data and user preference data in JSON format, and read them back into corresponding objects.
* inherits from both `AddressBookStorage` and `UserPrefStorage`, which means it can be treated as either one (if only the functionality of only one is needed).
* depends on some classes in the `Model` component (because the `Storage` component's job is to save/retrieve objects that belong to the `Model`)

### Common classes

Classes used by multiple components are in the `seedu.address.commons` package.

--------------------------------------------------------------------------------------------------------------------

## **Implementation**

This section describes some noteworthy details on how certain features are implemented.

### Undo/redo feature

#### Implementation (current)

The undo/redo mechanism is implemented via `VersionedAddressBook`, which maintains two stacks:
- `addressBookStateHistory` (undo stack)
- `addressBookRedoHistory` (redo stack)

APIs:
- `VersionedAddressBook#commit()` saves a copy of the current state to the undo stack and clears the redo stack.
- `VersionedAddressBook#undo()` pushes the current state onto the redo stack, pops the previous state from the undo stack, and calls `resetData(previousState)`.
- `VersionedAddressBook#redo()` pushes the current state onto the undo stack, pops from the redo stack, and calls `resetData(nextState)`.

Model surface:
- `Model#commit()`, `Model#undo()`, `Model#redo()` delegate to the above `VersionedAddressBook` methods.
- On successful `undo()`/`redo()`, `ModelManager` refreshes filtered lists via `updateFilteredPersonList`, `updateFilteredEventList`, and `updateFilteredTaskList` to keep the UI consistent.

Given below is an example usage scenario and how the undo/redo mechanism behaves at each step.

Step 1. The user launches the application for the first time. The `VersionedAddressBook` will be initialized with the initial address book state, and the `currentStatePointer` pointing to that single address book state.

<puml src="diagrams/UndoRedoState0.puml" width="400" alt="UndoRedoState0" />

Step 2. The user executes `delete 5` command to delete the 5th person in the address book. The `delete` command calls `Model#commitAddressBook()`, causing the modified state of the address book after the `delete 5` command executes to be saved in the `addressBookStateList`, and the `currentStatePointer` is shifted to the newly inserted address book state.

<puml src="diagrams/UndoRedoState1.puml" width="400" alt="UndoRedoState1" />

Step 3. The user executes `add n/David …​` to add a new person. The `add` command also calls `Model#commitAddressBook()`, causing another modified address book state to be saved into the `addressBookStateList`.

<puml src="diagrams/UndoRedoState2.puml" width="400" alt="UndoRedoState2" />

<box type="info" seamless>

**Note:** If a command fails its execution, it will not call `Model#commitAddressBook()`, so the address book state will not be saved into the `addressBookStateList`.

</box>

Step 4. The user now decides that adding the person was a mistake, and decides to undo that action by executing the `undo` command. The `undo` command will call `Model#undoAddressBook()`, which will shift the `currentStatePointer` once to the left, pointing it to the previous address book state, and restores the address book to that state.

<puml src="diagrams/UndoRedoState3.puml" width="400" alt="UndoRedoState3" />


<box type="info" seamless>

**Note:** If the undo stack is empty, `undo()` returns false and no state change occurs. Similarly, if the redo stack is empty, `redo()` returns false.

</box>

The following sequence diagram shows how an undo operation goes through the `Logic` component:

<puml src="diagrams/UndoSequenceDiagram-Logic.puml" width="550" alt="UndoSequenceDiagram-Logic" />

<box type="info" seamless>

**Note:** The lifeline for `UndoCommand` should end at the destroy marker (X) but due to a limitation of PlantUML, the lifeline reaches the end of diagram.

</box>

Similarly, how an undo operation goes through the `Model` component is shown below:

<puml src="diagrams/UndoSequenceDiagram-Model.puml" width="550" alt="UndoSequenceDiagram-Model" />

The `redo` command calls `Model#redo()`, restoring the next state if available.

<box type="info" seamless>

**Note:** If the `currentStatePointer` is at index `addressBookStateList.size() - 1`, pointing to the latest address book state, then there are no undone AddressBook states to restore. The `redo` command uses `Model#canRedoAddressBook()` to check if this is the case. If so, it will return an error to the user rather than attempting to perform the redo.

</box>

Step 5. The user then decides to execute the command `list`. Commands that do not modify the address book, such as `list`, will usually not call `Model#commitAddressBook()`, `Model#undoAddressBook()` or `Model#redoAddressBook()`. Thus, the `addressBookStateList` remains unchanged.

<puml src="diagrams/UndoRedoState4.puml" width="400" alt="UndoRedoState4" />

Step 6. The user executes `clear`, which calls `Model#commitAddressBook()`. Since the `currentStatePointer` is not pointing at the end of the `addressBookStateList`, all address book states after the `currentStatePointer` will be purged. Reason: It no longer makes sense to redo the `add n/David …​` command. This is the behavior that most modern desktop applications follow.

<puml src="diagrams/UndoRedoState5.puml" width="400" alt="UndoRedoState5" />

The following activity diagram summarizes what happens when a user executes a new command:

<puml src="diagrams/CommitActivityDiagram.puml" width="250" />

#### Design considerations:

**Aspect: How undo & redo executes:**

* **Alternative 1 (current choice):** Saves the entire address book.
  * Pros: Easy to implement.
  * Cons: May have performance issues in terms of memory usage.

* **Alternative 2:** Individual command knows how to undo/redo by
  itself.
  * Pros: Will use less memory (e.g. for `delete`, just save the person being deleted).
  * Cons: We must ensure that the implementation of each individual command are correct.

_{more aspects and alternatives to be added}_

### Add member feature

The add member feature allows users to add new members to the address book with their details.

The sequence diagram below illustrates the interactions within the `Logic` component for adding a member:

<puml src="diagrams/AddSequenceDiagram.puml" width="550" alt="Interactions Inside the Logic Component for the `add` Command" />

<box type="info" seamless>

**Note:** The lifeline for `AddCommandParser` should end at the destroy marker (X) but due to a limitation of PlantUML, the lifeline continues till the end of diagram.

</box>

How the `add` command works:
1. When the user enters an `add` command, `LogicManager` passes it to `AddressBookParser`.
2. `AddressBookParser` creates an `AddCommandParser` to parse the command arguments.
3. `AddCommandParser` validates and parses all required fields (name, year, student number, email, phone, dietary requirements, role, and optional tags).
4. An `AddCommand` object is created and executed.
5. Before execution, the current state is committed for undo/redo functionality.
6. `AddCommand` checks if a person with the same student number already exists.
7. If not duplicate, the person is added to the address book.
8. The updated address book is saved to storage.

### Edit member feature

The edit member feature allows users to update existing member details.

The sequence diagram below illustrates the interactions within the `Logic` component for editing a member:

<puml src="diagrams/EditSequenceDiagram.puml" width="550" alt="Interactions Inside the Logic Component for the `edit` Command" />

<box type="info" seamless>

**Note:** The lifeline for `EditCommandParser` should end at the destroy marker (X) but due to a limitation of PlantUML, the lifeline continues till the end of diagram.

</box>

How the `edit` command works:
1. When the user enters an `edit` command with an index and field updates, `LogicManager` passes it to `AddressBookParser`.
2. `AddressBookParser` creates an `EditCommandParser` to parse the command arguments.
3. `EditCommandParser` validates the index and parses the optional fields to update.
4. An `EditCommand` object is created with an `EditPersonDescriptor` containing the updates.
5. Before execution, the current state is committed for undo/redo functionality.
6. `EditCommand` retrieves the person at the specified index from the filtered list.
7. A new `Person` object is created with the updated fields.
8. `EditCommand` checks if the edited person already exists (unless it's the same person).
9. If valid, the person is updated in the address book.
10. The filtered list is updated to show all persons.
11. The updated address book is saved to storage.

### Find member feature

The find member feature allows users to search for members by keywords across all fields.

The sequence diagram below illustrates the interactions within the `Logic` component for finding members:

<puml src="diagrams/FindSequenceDiagram.puml" width="550" alt="Interactions Inside the Logic Component for the `find` Command" />

How the `find` command works:
1. When the user enters a `find` command with keywords, `LogicManager` passes it to `AddressBookParser`.
2. `AddressBookParser` creates a `FindCommandParser` to parse the command arguments.
3. `FindCommandParser` splits the input into keywords.
4. A `PersonContainsKeywordsPredicate` is created with the keywords.
5. A `FindCommand` object is created and executed.
6. `FindCommand` updates the filtered person list with the predicate.
7. The predicate tests each person to see if their fields contain all the keywords (case-insensitive).
8. Only persons matching all keywords are shown in the filtered list.
9. The result message shows how many persons match the criteria.

### Add event feature

The add event feature allows users to create new events with an event ID, date, and description.

The sequence diagram below illustrates the interactions within the `Logic` component for adding an event:

<puml src="diagrams/AddEventSequenceDiagram.puml" width="550" alt="Interactions Inside the Logic Component for the `addevent` Command" />

<box type="info" seamless>

**Note:** The lifeline for `AddEventCommandParser` should end at the destroy marker (X) but due to a limitation of PlantUML, the lifeline continues till the end of diagram.

</box>

How the `addevent` command works:
1. When the user enters an `addevent` command, `LogicManager` passes it to `AddressBookParser`.
2. `AddressBookParser` creates an `AddEventCommandParser` to parse the command arguments.
3. `AddEventCommandParser` validates and parses the event ID, date, and description.
4. An `AddEventCommand` object is created and executed.
5. Before execution, the current state is committed for undo/redo functionality.
6. `AddEventCommand` checks if an event with the same ID already exists.
7. If not duplicate, the event is added to the address book.
8. The updated address book is saved to storage.

### Attendance feature

The attendance feature allows users to record which members attended specific events.

The class diagram below shows the structure of attendance-related classes:

<puml src="diagrams/AttendanceClassDiagram.puml" width="550" alt="Class Diagram for Attendance Feature" />

The sequence diagram below illustrates how attendance is added for an event:

<puml src="diagrams/AttendanceSequenceDiagram.puml" width="550" alt="Sequence Diagram for Adding Attendance" />

The activity diagram below summarizes the flow when adding attendance:

<puml src="diagrams/AttendanceActivity.puml" width="400" alt="Activity Diagram for Adding Attendance" />

How the attendance feature works:
1. When the user enters an `addattendance` command with an event ID and member names, `LogicManager` passes it to `AddressBookParser`.
2. `AddressBookParser` creates an `AddAttendanceCommandParser` to parse the command arguments.
3. An `AddAttendanceCommand` object is created and executed.
4. `AddAttendanceCommand` retrieves the event by event ID from the model.
5. For each member name, it checks if the member exists and if attendance hasn't already been recorded.
6. Duplicate entries are ignored.
7. New `Attendance` objects are created and added to the event.
8. A success message is built showing which members were added and which were duplicates.

--------------------------------------------------------------------------------------------------------------------

## **Documentation, logging, testing, configuration, dev-ops**

* [Documentation guide](Documentation.md)
* [Testing guide](Testing.md)
* [Logging guide](Logging.md)
* [Configuration guide](Configuration.md)
* [DevOps guide](DevOps.md)

--------------------------------------------------------------------------------------------------------------------

## **Appendix: Requirements**

### Product scope

## Target User Profile

**Primary User:**
CCA secretary in NUS, who needs to:
- Manage 50+ contacts of members with different roles in a structured way.
- Keep track of members’ preferences and dietary restrictions.
- Record event attendees and mark attendance.
- Delegate and track tasks.

**Pain Points:**
- **Fragmented systems:** Currently uses spreadsheets, group chats, and manual checklists, which are error-prone and time-consuming.
- **Repetitive tasks:** Re-entering the same member details across multiple files and attendance sheets.
- **Lack of structure:** Hard to filter/search members quickly (e.g., “all Year 1 members who are vegetarian”).


## Value Proposition

A streamlined address book that organises member details, tracks unique preferences, and simplifies event attendance — helping secretaries stay efficient, accurate, and focused on building stronger communities.


### User Stories

Priorities: High (must have) - `* * *`, Medium (nice to have) - `* *`, Low (unlikely to have) - `*`

| Priority | As a …​               | I want to …​                                                                      | So that I can…​                                    |
|----------|-----------------------|-----------------------------------------------------------------------------------|---------------------------------------------------|
| `* * *`  | secretary             | add a member with name, year, role, student number, phone, email, dietary info   | keep member details organized and complete        |
| `* * *`  | secretary             | add optional tags to members                                                      | categorize members for better organization        |
| `* * *`  | secretary             | edit a member’s details                                                           | correct mistakes without re-entering everything   |
| `* * *`  | secretary             | delete a member                                                                   | remove members no longer in the club              |
| `* * *`  | secretary             | find members by searching across all fields                                       | quickly locate specific members                   |
| `* * *`  | secretary             | list all members                                                                  | get an overview of all club members               |
| `* * *`  | secretary             | clear all member data                                                             | reset the system when starting fresh              |
| `* * *`  | secretary (events)    | create an event with unique ID, date, and description                             | plan and track club activities                    |
| `* * *`  | secretary (events)    | delete an event                                                                   | remove outdated or duplicate records              |
| `* * *`  | secretary (events)    | set a budget/expense for specific events                                          | track event costs and manage finances             |
| `* * *`  | secretary (attendance)| add members to an event’s attendance list                                         | prepare attendance records before the event       |
| `* * *`  | secretary (attendance)| mark members as attended/absent                                                   | record actual participation                       |
| `* * *`  | secretary (attendance)| view attendees and attendance summary                                             | get a quick overview of participation             |
| `* * *`  | secretary (tasks)     | add tasks with optional deadlines                                                 | assign responsibilities and track progress        |
| `* * *`  | secretary (tasks)     | mark/unmark tasks as completed                                                    | keep task status accurate                         |
| `* * *`  | secretary (tasks)     | delete tasks                                                                      | remove tasks that are no longer relevant          |
| `* * *`  | treasurer (budget)    | set a global budget with start and end dates                                      | track club finances for a period                  |
| `* * *`  | treasurer (budget)    | set expenses for individual events                                                | track costs per event                              |
| `* * *`  | treasurer (budget)    | view a budget report                                                              | see financial overview and spending patterns      |
| `* * *`  | secretary             | import/export member data as CSV                                                  | move data in/out efficiently                      |
| `* * *`  | secretary             | undo/redo my last action                                                          | recover from mistakes quickly                     |
| `* *`    | secretary             | see statistics about attendance                                                   | identify active vs inactive members               |
| `* *`    | secretary             | filter members by year or role                                                    | find target groups quickly                        |


## **Use Cases**

For all use cases below, the **System** is `ClubHub` and the **Actor** is the **Secretary**, unless specified otherwise.

---

### **Use case: UC01 – Add Member**

**System:** ClubHub
**Actor:** Secretary

**MSS (Main Success Scenario):**
1. Secretary chooses to add a member.
2. ClubHub requests name, year, role, dietary requirements, student number, phone number(above compulsory) and telegram handle(optional).
3. Secretary enters the requested details.
4. ClubHub validates inputs and creates the member record.
5. ClubHub displays confirmation and the updated member list.
   Use case ends.

**Extensions:**
- 3a. Missing/invalid fields.
    - 3a1. ClubHub shows specific error and usage hint.
    - 3a2. Secretary re-enters data.
    - Use case resumes from step 4.
- *a. At any time, Secretary cancels.
    - *a1. ClubHub asks to confirm cancellation.
    - *a2. Secretary confirms.
    - Use case ends.

---

### **Use case: UC02 – Search Members by Field**

**System:** ClubHub
**Actor:** Secretary

**MSS:**
1. Secretary chooses to search members.
2. ClubHub requests search criteria (e.g., **field** and **query**).
3. Secretary enters the field and query (e.g., Field: `Year`, Query: `3`).
4. ClubHub searches members whose specified field matches the query.
5. ClubHub displays a list of matching members with key details (e.g., Name, Year, Role, Dietary Restriction).
6. (Optional) Secretary selects a member to view full details.
   Use case ends.

**Extensions:**
- 2a. Secretary omits field or query.
    - 2a1. ClubHub prompts for the missing input(s).
    - Use case resumes from step 3.
- 3a. Invalid field name provided.
    - 3a1. ClubHub lists supported fields.
    - 3a2. Secretary re-enters a valid field.
    - Use case resumes from step 3.
- 4a. No members match the criteria.
    - 4a1. ClubHub displays "No member found." and suggests refining the query.
    - Use case ends.
- 5a. Result set is too large.
    - 5a1. ClubHub paginates results or prompts for filters.
    - Use case resumes from step 3 or continues browsing.
- *a. At any time, Secretary cancels.
    - *a1. ClubHub asks to confirm cancellation.
    - *a2. Secretary confirms.
    - Use case ends.

---

### **Use case: UC03 – Create Event**

**System:** ClubHub
**Actor:** Secretary

**MSS:**
1. Secretary chooses to create an event.
2. ClubHub requests event ID, date, and description.
3. Secretary enters details.
4. ClubHub validates and creates the event.
5. ClubHub displays confirmation.
   Use case ends.

**Extensions:**
- 3a. Duplicate/non-conforming EventID or invalid date/description.
    - 3a1. ClubHub shows validation error(s).
    - 3a2. Secretary re-enters data.
    - Use case resumes from step 4.

---

### **Use case: UC04 – Record Event Attendance**

**System:** ClubHub
**Actor:** Secretary

**MSS:**
1. Secretary searches for an event by ID or date.
2. ClubHub displays the event details.
3. Secretary chooses to record attendance.
4. ClubHub displays the list of members.
5. Secretary marks members as attended.
6. ClubHub saves the attendance and confirms.
   Use case ends.

**Extensions:**
- 1a. Event not found.
    - 1a1. ClubHub displays “Event not found.”
    - Use case ends.
- 5a. Secretary marks the same member again.
    - 5a1. ClubHub ignores duplicate and continues.

---

### **Use case: UC05 – Update Member Details**

**System:** ClubHub
**Actor:** Secretary

**MSS:**
1. Secretary searches for the member by name and year.
2. ClubHub displays the member’s current details.
3. Secretary chooses to edit details.
4. ClubHub requests updated information.
5. Secretary enters new details (e.g., role changed).
6. ClubHub validates and updates the record.
7. ClubHub confirms the update.
   Use case ends.

**Extensions:**
- 1a. No member matches the search.
    - 1a1. ClubHub displays “No member found.”
    - Use case ends.
- 6a. Invalid details entered.
    - 6a1. ClubHub requests correction.
    - 6a2. Secretary re-enters details.
    - Use case resumes from step 7.

---

### **Use case: UC06 – Undo Last Action**

**System:** ClubHub
**Actor:** Secretary

**MSS:**
1. Secretary issues the undo command.
2. ClubHub restores the system to its state before the last action.
3. ClubHub displays confirmation.
   Use case ends.

**Extensions:**
- 1a. No action available to undo.
    - 1a1. ClubHub displays “No action to undo.”
    - Use case ends.

---

### **Use case: UC07 – View Attendance and Follow-Up**

**System:** ClubHub
**Actor:** Secretary

**MSS:**
1. Secretary chooses to view attendance for an event.
2. ClubHub displays the list of members who attended.
3. Secretary identifies absentees for follow-up actions (e.g., reminders).
   Use case ends.

**Extensions:**
- 1a. Event not found.
    - 1a1. ClubHub displays “Event not found.”
    - Use case ends.
- 2a. No attendance recorded yet.
    - 2a1. ClubHub shows “No attendance recorded yet.”
    - Use case ends.

---

### **Use case: UC08 – Delete Event**

**System:** ClubHub
**Actor:** Secretary

**MSS:**
1. Secretary chooses to delete an event.
2. ClubHub requests event ID and confirmation.
3. Secretary provides the ID and confirms deletion.
4. ClubHub deletes the event and shows success message.
   Use case ends.

**Extensions:**
- 2a. Event not found.
    - 2a1. ClubHub displays “Event not found.”
    - Use case ends.
- *a. Secretary cancels at confirmation.
    - *a1. ClubHub cancels deletion.
    - Use case ends.

---

### **Use case: UC09 – View Pending Tasks**

**System:** ClubHub
**Actor:** Secretary

**MSS:**
1. Secretary chooses to view pending tasks.
2. ClubHub retrieves and lists all tasks not marked as completed, grouped by member.
3. Secretary reviews tasks.
   Use case ends.

**Extensions:**
- 2a. No pending tasks.
    - 2a1. ClubHub displays “No pending tasks.”
    - Use case ends.

---

### **Use case: UC10 – View Attendance Statistics**

**System:** ClubHub
**Actor:** Secretary

**MSS:**
1. Secretary chooses to view attendance statistics.
2. ClubHub calculates attendance percentage for each member across all events.
3. ClubHub displays summary statistics (e.g., member name, number of events attended, attendance rate).
   Use case ends.

**Extensions:**
- 2a. No attendance data available.
    - 2a1. ClubHub displays “No attendance data found.”
    - Use case ends.

---

### **Use case: UC11 – Bulk Import/Export Member Details**

**System:** ClubHub
**Actor:** Secretary

**MSS:**
1. Secretary chooses to import or export member details.
2. For import:
    - ClubHub requests a CSV/Excel file.
    - Secretary uploads the file.
    - ClubHub validates the file format and contents.
    - ClubHub imports member data and shows summary of new/updated records.
3. For export:
    - ClubHub generates a CSV/Excel file with current member details.
    - Secretary downloads the file.
      Use case ends.

**Extensions:**
- 2a. Import file invalid (wrong format, missing fields).
    - 2a1. ClubHub shows error message with line numbers.
    - 2a2. Secretary corrects file and retries.
- 2b. Duplicate entries in import file.
    - 2b1. ClubHub prompts whether to update or skip.
    - Use case resumes from step 2.

---

### **Use case: UC12 – Manage Budget**

**System:** ClubHub  
**Actor:** Secretary

**MSS:**
1. Secretary chooses to manage the club’s budget.
2. ClubHub prompts for budget amount, start date, and end date.
3. Secretary enters the amount and date range.
4. ClubHub validates the inputs.
5. ClubHub saves the budget and displays confirmation with budget details.
6. Secretary requests to view the budget report.
7. ClubHub retrieves expenses within the budget period.
8. ClubHub displays total budget, total spent, remaining balance, and expenses per event.
9. Secretary requests to reset the budget.
10. ClubHub prompts for confirmation.
11. Secretary confirms the reset action.
12. ClubHub clears the budget and shows a success message.
    Use case ends.

**Extensions:**
- 4a. Inputs are invalid (negative amount or invalid date range).
    - 4a1. ClubHub displays an error message describing the issue.
    - 4a2. Secretary corrects the inputs and retries from step 3.
- 4b. Entered budget period overlaps an existing budget.
    - 4b1. ClubHub prompts for confirmation to overwrite.
    - 4b2. Secretary confirms overwrite; use case resumes from step 5.
- 7a. No expenses recorded in the budget period.
    - 7a1. ClubHub displays zero spending in the report.
    - Use case resumes at step 8.
- 8a. No budget set when the report is requested.
    - 8a1. ClubHub displays “No budget set. Use `budgetset` to set one.”
    - Use case ends.

---

### **Use case: UC13 – Add Task to Task List**

**System:** ClubHub  
**Actor:** Secretary

**MSS:**
1. Secretary chooses to add a new task.
2. ClubHub prompts for the task title and optional deadline.
3. Secretary enters the task details.
4. ClubHub validates the details.
5. ClubHub adds the task to the list and displays the confirmation with task details.
   Use case ends.

**Extensions:**
- 4a. Task already exists.
    - 4a1. ClubHub displays “This task already exists.”
    - 4a2. Secretary modifies the details and retries from step 3.
- 4b. Deadline format is invalid.
    - 4b1. ClubHub displays the correct format (YYYY-MM-DD HH:MM).
    - 4b2. Secretary corrects the deadline and retries from step 3.

### Non-Functional Requirements

- **Performance**: Actions (add/search/edit) should respond within 1 second for up to 200 members.
- **Usability**: Must be usable via CLI with clear error messages and undo support.
- **Portability**: Runs locally on Windows, macOS, and Linux.
- **Single-user**: Only one secretary per copy of the app.
- **No remote server**: All data stored locally.
- **Reliability**: Data should not be lost when the program closes.
- **Incremental development**: Feature additions should not break previous functionality.
- **Security**: User data should not be exposed externally.


### Glossary

**Actor**
An entity (usually a user or external system) that interacts with ClubHub. In most use cases, the actor is the **Secretary**, though other Exco members may also interact with the system.

**Attendance**
A record of which members were present at an event. Used to generate statistics and track participation.

**ClubHub**
The system being developed to support secretarial duties, including member management, event organisation, task assignment, and record-keeping.

**Dietary Restriction**
Any limitation on food or drink that a member cannot consume (e.g., vegetarian, halal, no seafood). Stored in member records to aid event meal planning.

**Event**
A scheduled club activity created and tracked in ClubHub. Each event has an ID, date, and description, and may include attendance records.

**Exco (Executive Committee)**
The group of members responsible for running the club (e.g., President, Treasurer, Secretary). Exco members may have special access privileges.

**Field**
A specific attribute of a member record (e.g., Name, Year, Role, Dietary Restriction) used for searching and filtering.

**Member**
A registered individual in ClubHub with stored details such as name, year of study, role, student number, phone number, and optional telegram handle.

**Member Record**
The collection of data fields stored for a member in ClubHub.

**Pending Task**
A task assigned to a member that has not yet been marked as completed.

**Role**
The position a member holds in the club (e.g., President, Treasurer, Secretary, General Member).

**Secretary**
The main user of ClubHub. Responsible for managing members, creating events, tracking attendance, assigning tasks, and maintaining records.

**Task**
An assigned responsibility given to a member, tracked by ClubHub until marked completed.

**Undo**
A command that allows the Secretary to reverse the most recent action in ClubHub.

**Year of Study**
The academic year of a member (e.g., Year 1, Year 2, etc.). Used for filtering and organising members.

--------------------------------------------------------------------------------------------------------------------

## **Appendix: Instructions for manual testing**

Given below are instructions to test the app manually.

<box type="info" seamless>

**Note:** These instructions only provide a starting point for testers to work on;
testers are expected to do more *exploratory* testing.

</box>

### Launch and shutdown

1. Initial launch

   1. Download the jar file and copy into an empty folder

   1. Double-click the jar file Expected: Shows the GUI with a set of sample contacts. The window size may not be optimum.

1. Saving window preferences

   1. Resize the window to an optimum size. Move the window to a different location. Close the window.

   1. Re-launch the app by double-clicking the jar file.<br>
       Expected: The most recent window size and location is retained.


### Deleting a person

1. Deleting a person while all persons are being shown

   1. Prerequisites: List all persons using the `list` command. Multiple persons in the list.

   1. Test case: `delete 1`<br>
      Expected: First contact is deleted from the list. Details of the deleted contact shown in the status message. Timestamp in the status bar is updated.

   1. Test case: `delete 0`<br>
      Expected: No person is deleted. Error details shown in the status message. Status bar remains the same.

   1. Other incorrect delete commands to try: `delete`, `delete x`, `...` (where x is larger than the list size)<br>
      Expected: Similar to previous.


### Saving data

1. **Test case: Missing data file**

    1. Close the app.

    1. Navigate to your project’s `data/` folder and delete `addressbook.json`.

    1. Re-launch the app by double-clicking the JAR file.<br>
       **Expected:** The app starts successfully with an empty member list.<br>
       No crash or error message appears.<br>
       Upon adding a new member or closing the app, a new valid `addressbook.json` file is created automatically.

1. **Test case: Corrupted data file**

    1. Close the app.

    1. Navigate to your project’s `data/` folder and open `addressbook.json` in a text editor.

    1. Delete or modify part of the file (e.g. remove a closing brace `}` or change a field name). Save and close the file.

    1. Re-launch the app by double-clicking the JAR file.<br>
       **Expected:** The app displays a warning in the logs:<br>
       `"Data file not in the correct format. Starting with an empty AddressBook."`<br>
       The UI shows an empty list of members.<br>
       On exit, the corrupted file is replaced with a new, valid empty JSON file.
