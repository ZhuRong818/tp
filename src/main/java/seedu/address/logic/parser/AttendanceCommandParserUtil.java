package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import java.util.ArrayList;
import java.util.List;

import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Name;

/**
 * Shared helper methods for attendance-related command parsers.
 */
final class AttendanceCommandParserUtil {

    private AttendanceCommandParserUtil() {}

    static void requireNoPreamble(ArgumentMultimap arguments, String usageMessage) throws ParseException {
        if (!arguments.getPreamble().isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, usageMessage));
        }
    }

    static void requirePrefixesPresent(ArgumentMultimap arguments, String usageMessage, Prefix... prefixes)
            throws ParseException {
        if (!arePrefixesPresent(arguments, prefixes)) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, usageMessage));
        }
    }

    static void requireSingleValue(ArgumentMultimap arguments, Prefix prefix) throws ParseException {
        if (arguments.getAllValues(prefix).size() != 1) {
            throw new ParseException(MESSAGE_INVALID_COMMAND_FORMAT);
        }
    }

    static List<Name> parseMemberNames(String rawMembers) throws ParseException {
        String[] parts = rawMembers.split("/");
        List<Name> result = new ArrayList<>();

        for (String part : parts) {
            String trimmed = part.trim();
            if (trimmed.isEmpty()) {
                throw new ParseException(MESSAGE_INVALID_COMMAND_FORMAT);
            }
            result.add(ParserUtil.parseName(trimmed));
        }

        return result;
    }

    private static boolean arePrefixesPresent(ArgumentMultimap arguments, Prefix... prefixes) {
        for (Prefix prefix : prefixes) {
            if (arguments.getValue(prefix).isEmpty()) {
                return false;
            }
        }
        return true;
    }
}
