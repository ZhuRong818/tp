package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EVENT_ID;
import static seedu.address.logic.parser.CliSyntax.PREFIX_MEMBER;

import java.util.List;

import seedu.address.logic.commands.MarkAttendanceCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.event.EventId;
import seedu.address.model.person.Name;

/**
 * Parses input arguments and creates a new MarkAttendanceCommand object.
 */
public class MarkAttendanceCommandParser implements Parser<MarkAttendanceCommand> {

    @Override
    public MarkAttendanceCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_EVENT_ID, PREFIX_MEMBER);

        AttendanceCommandParserUtil.requireNoPreamble(argMultimap, MarkAttendanceCommand.MESSAGE_USAGE);
        AttendanceCommandParserUtil.requirePrefixesPresent(argMultimap, MarkAttendanceCommand.MESSAGE_USAGE,
                PREFIX_EVENT_ID, PREFIX_MEMBER);
        AttendanceCommandParserUtil.requireSingleValue(argMultimap, PREFIX_EVENT_ID);
        AttendanceCommandParserUtil.requireSingleValue(argMultimap, PREFIX_MEMBER);

        // Values must be non-blank after trimming
        String rawEventId = argMultimap.getValue(PREFIX_EVENT_ID).get().trim();
        String rawMember = argMultimap.getValue(PREFIX_MEMBER).get().trim();
        if (rawEventId.isEmpty() || rawMember.isEmpty()) {
            throw new ParseException(MESSAGE_INVALID_COMMAND_FORMAT);
        }

        // Parse into domain types
        try {
            EventId eventId = ParserUtil.parseEventId(rawEventId);
            List<Name> memberNames = AttendanceCommandParserUtil.parseMemberNames(rawMember);
            return new MarkAttendanceCommand(eventId, memberNames);
        } catch (ParseException pe) {
            // Tests expect a generic invalid-format message on invalid input
            throw new ParseException(MESSAGE_INVALID_COMMAND_FORMAT);
        }
    }
}
