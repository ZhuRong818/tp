package seedu.address.logic.commands;

import java.util.List;
import java.util.stream.Collectors;

import seedu.address.model.attendance.AttendanceMessages;
import seedu.address.model.person.Name;

/**
 * Utility methods for formatting attendance-related command messages.
 */
final class AttendanceMessageUtil {

    private AttendanceMessageUtil() {}

    static String formatNames(List<Name> names) {
        return names.isEmpty()
                ? AttendanceMessages.LABEL_NONE
                : names.stream().map(Name::toString).collect(Collectors.joining(", "));
    }

    static String formatAlreadyAddedMessage(List<Name> names) {
        String label = names.size() == 1
                ? AttendanceMessages.MESSAGE_MEMBER_ALREADY_ADDED_SINGLE
                : AttendanceMessages.MESSAGE_MEMBER_ALREADY_ADDED_PLURAL;
        return String.format(label, formatNames(names));
    }
}
