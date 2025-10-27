package seedu.address.model.attendance.exceptions;

/**
 * Signals that an attendance operation could not be completed.
 */
public class AttendanceOperationException extends Exception {

    public AttendanceOperationException(String message) {
        super(message);
    }
}

