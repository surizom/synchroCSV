package com.opencsv;

import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * This interface defines all the behavior of a csv writer class.
 *
 * @since 4.2
 */
public interface ICSVWriter extends Closeable, Flushable {
    /**
     * Default line terminator.
     */
    String DEFAULT_LINE_END = "\n";
    /**
     * RFC 4180 compliant line terminator.
     */
    String RFC4180_LINE_END = "\r\n";
    /**
     * Default buffer sizes
     */
    int INITIAL_STRING_SIZE = 1024;
    /**
     * The character used for escaping quotes.
     */
    char DEFAULT_ESCAPE_CHARACTER = '"';
    /**
     * The default separator to use if none is supplied to the constructor.
     */
    char DEFAULT_SEPARATOR = ',';
    /**
     * The default quote character to use if none is supplied to the
     * constructor.
     */
    char DEFAULT_QUOTE_CHARACTER = '"';
    /**
     * The quote constant to use when you wish to suppress all quoting.
     */
    char NO_QUOTE_CHARACTER = '\u0000';
    /**
     * The escape constant to use when you wish to suppress all escaping.
     */
    char NO_ESCAPE_CHARACTER = '\u0000';


    /**
     * Writes iterable to a CSV file. The list is assumed to be a String[]
     *
     * @param allLines         an Iterable of String[], with each String[] representing a line of
     *                         the file.
     * @param applyQuotesToAll true if all values are to be quoted.  false if quotes only
     *                         to be applied to values which contain the separator, escape,
     *                         quote or new line characters.
     */
    void writeAll(Iterable<String[]> allLines, boolean applyQuotesToAll);

    /**
     * Writes the entire list to a CSV file.
     * The list is assumed to be a String[].
     *
     * @param allLines         A List of String[] with each String[] representing a line of
     *                         the file.
     * @param applyQuotesToAll True if all values are to be quoted. False if quotes only
     *                         to be applied to values which contain the separator, escape,
     *                         quote, or new line characters.
     */
    void writeAll(List<String[]> allLines, boolean applyQuotesToAll);

    /**
     * Writes iterable to a CSV file. The list is assumed to be a String[]
     *
     * @param allLines an Iterable of String[], with each String[] representing a line of
     *                 the file.
     */
    void writeAll(Iterable<String[]> allLines);

    /**
     * Writes the entire list to a CSV file.
     * The list is assumed to be a String[].
     *
     * @param allLines A List of String[] with each String[] representing a line of
     *                 the file.
     */
    void writeAll(List<String[]> allLines);

    /**
     * Writes the entire ResultSet to a CSV file.
     * <p>
     * The caller is responsible for closing the ResultSet. Values are not trimmed.
     * Quotes are applied to all values in the output.
     *
     * @param rs                 The result set to write
     * @param includeColumnNames True if you want column names in the output, false otherwise
     * @return Number of lines written.
     * @throws java.io.IOException   Thrown by ResultSetHelper.getColumnValues()
     * @throws java.sql.SQLException Thrown by ResultSetHelper.getColumnValues()
     */
    int writeAll(ResultSet rs, boolean includeColumnNames) throws SQLException, IOException;

    /**
     * Writes the entire ResultSet to a CSV file.
     * <p>
     * The caller is responsible for closing the ResultSet. Quotes are applied to
     * all values in the output.
     *
     * @param rs                 The Result set to write.
     * @param includeColumnNames Include the column names in the output.
     * @param trim               Remove spaces from the data before writing.
     * @return Number of lines written - including header.
     * @throws java.io.IOException   Thrown by ResultSetHelper.getColumnValues()
     * @throws java.sql.SQLException Thrown by ResultSetHelper.getColumnValues()
     */
    int writeAll(ResultSet rs, boolean includeColumnNames, boolean trim) throws SQLException, IOException;

    /**
     * Writes the entire ResultSet to a CSV file.
     *
     * The caller is responsible for closing the ResultSet.
     *
     * @param rs The Result set to write.
     * @param includeColumnNames Include the column names in the output.
     * @param trim Remove spaces from the data before writing.
     * @param applyQuotesToAll Whether all values should be quoted.
     *
     * @throws java.io.IOException   Thrown by ResultSetHelper.getColumnValues()
     * @throws java.sql.SQLException Thrown by ResultSetHelper.getColumnValues()
     *
     * @return Number of lines written - including header.
     */
    int writeAll(ResultSet rs, boolean includeColumnNames, boolean trim, boolean applyQuotesToAll) throws SQLException, IOException;

    /**
     * Writes the next line to the file.
     *
     * @param nextLine         A string array with each comma-separated element as a separate
     *                         entry.
     * @param applyQuotesToAll True if all values are to be quoted. False applies quotes only
     *                         to values which contain the separator, escape, quote, or new line characters.
     */
    void writeNext(String[] nextLine, boolean applyQuotesToAll);

    /**
     * Writes the next line to the file.
     *
     * @param nextLine A string array with each comma-separated element as a separate
     *                 entry.
     */
    void writeNext(String[] nextLine);

    /**
     * Flushes the buffer and checks to see if the there has been an error in the printstream.
     *
     * @return True if the print stream has encountered an error
     *          either on the underlying output stream or during a format
     *          conversion.
     */
    boolean checkError();

    /**
     * Sets the result service.
     * @param resultService The ResultSetHelper
     */
    void setResultService(ResultSetHelper resultService);

    /**
     * Flushes the writer without throwing any exceptions.
     */
    void flushQuietly();
}
