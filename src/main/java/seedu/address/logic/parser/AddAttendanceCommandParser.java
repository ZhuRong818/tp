package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EVENT_ID;
import static seedu.address.logic.parser.CliSyntax.PREFIX_MEMBER;

import java.util.List;

import seedu.address.logic.commands.AddAttendanceCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.event.EventId;
import seedu.address.model.person.Name;

/**
 * Parses input arguments and creates a new AddAttendanceCommand object.
 */
public class AddAttendanceCommandParser implements Parser<AddAttendanceCommand> {

    @Override
    public AddAttendanceCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_EVENT_ID, PREFIX_MEMBER);

        AttendanceCommandParserUtil.requireNoPreamble(argMultimap, AddAttendanceCommand.MESSAGE_USAGE);
        AttendanceCommandParserUtil.requirePrefixesPresent(argMultimap, AddAttendanceCommand.MESSAGE_USAGE,
                PREFIX_EVENT_ID, PREFIX_MEMBER);
        AttendanceCommandParserUtil.requireSingleValue(argMultimap, PREFIX_EVENT_ID);
        AttendanceCommandParserUtil.requireSingleValue(argMultimap, PREFIX_MEMBER);

        String rawEventId = argMultimap.getValue(PREFIX_EVENT_ID).get().trim();
        String rawMembers = argMultimap.getValue(PREFIX_MEMBER).get().trim();

        if (rawEventId.isEmpty() || rawMembers.isEmpty()) {
            throw new ParseException(MESSAGE_INVALID_COMMAND_FORMAT);
        }

        try {
            EventId eventId = ParserUtil.parseEventId(rawEventId);
            List<Name> memberNames = AttendanceCommandParserUtil.parseMemberNames(rawMembers);
            return new AddAttendanceCommand(eventId, memberNames);
        } catch (ParseException pe) {
            throw new ParseException(MESSAGE_INVALID_COMMAND_FORMAT);
        }
    }
}
