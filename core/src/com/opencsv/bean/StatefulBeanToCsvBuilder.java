/*
 * Copyright 2016 Andrew Rucker Jones.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.opencsv.bean;

import com.opencsv.CSVWriter;
import com.opencsv.ICSVParser;
import com.opencsv.ICSVWriter;
import org.apache.commons.lang3.ObjectUtils;

import java.io.Writer;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * This is a builder for StatefulBeanToCsv, allowing one to set all parameters
 * necessary for writing a CSV file.
 * 
 * @param <T> The type of the beans being written
 * @author Andrew Rucker Jones
 * @since 3.9
 */
public class StatefulBeanToCsvBuilder<T> {

    private char separator = ICSVWriter.DEFAULT_SEPARATOR;
    private char quotechar = ICSVWriter.DEFAULT_QUOTE_CHARACTER;
    private char escapechar = ICSVWriter.DEFAULT_ESCAPE_CHARACTER;
    private String lineEnd = CSVWriter.DEFAULT_LINE_END;
    private MappingStrategy<T> mappingStrategy = null;
    private final Writer writer;
    private final ICSVWriter csvWriter;
    private boolean throwExceptions = true;
    private boolean orderedResults = true;
    private Locale errorLocale = Locale.getDefault();
    private boolean applyQuotesToAll = true;
    
    /** The nullary constructor may never be used. */
    private StatefulBeanToCsvBuilder() {
        throw new IllegalStateException(String.format(
                ResourceBundle.getBundle(ICSVParser.DEFAULT_BUNDLE_NAME).getString("nullary.constructor.not.allowed"),
                getClass().getName()));
    }

    /**
     * Default constructor - Being stateful the writer is required by the builder at the start and not added in later.
     *
     * @param writer - the writer that will be used to output the csv version of the bean.
     */
    public StatefulBeanToCsvBuilder(Writer writer) {
        this.writer = writer;
        this.csvWriter = null;
    }

    /**
     * Being stateful the writer is required by the builder at the start and not added in later.
     * By passing in the ICSVWriter you can create a writer with the desired ICSVParser to allow you to
     * use the exact same parser for reading and writing.
     *
     * @param icsvWriter - the ICSVWriter that will be used to output the csv version of the bean.
     * @since 4.2
     */
    public StatefulBeanToCsvBuilder(ICSVWriter icsvWriter) {
        this.writer = null;
        this.csvWriter = icsvWriter;
    }
    
    /**
     * Sets the mapping strategy for writing beans to a CSV destination.
     * <p>If the mapping strategy is set this way, it will always be used instead
     * of automatic determination of an appropriate mapping strategy.</p>
     * <p>It is perfectly legitimate to read a CSV source, take the mapping
     * strategy from the read operation, and pass it in to this method for a
     * write operation. This conserves some processing time, but, more
     * importantly, preserves header ordering.</p>
     * 
     * @param mappingStrategy The mapping strategy to be used for write operations
     * @return this
     */
    public StatefulBeanToCsvBuilder<T> withMappingStrategy(MappingStrategy<T> mappingStrategy) {
        this.mappingStrategy = mappingStrategy;
        return this;
    }
    
    /**
     * @see com.opencsv.CSVWriter#separator
     * @param separator The field separator to be used when writing a CSV file
     * @return this
     */
    public StatefulBeanToCsvBuilder<T> withSeparator(char separator) {
        this.separator = separator;
        return this;
    }
    
    /**
     * @see com.opencsv.CSVWriter#quotechar
     * @param quotechar The quote character to be used when writing a CSV file
     * @return this
     */
    public StatefulBeanToCsvBuilder<T> withQuotechar(char quotechar) {
        this.quotechar = quotechar;
        return this;
    }
    
    /**
     * @see com.opencsv.CSVWriter#escapechar
     * @param escapechar The escape character to be used when writing a CSV file
     * @return this
     */
    public StatefulBeanToCsvBuilder<T> withEscapechar(char escapechar) {
        this.escapechar = escapechar;
        return this;
    }
    
    /**
     * @see com.opencsv.CSVWriter#lineEnd
     * @param lineEnd The line ending to be used when writing a CSV file
     * @return this
     */
    public StatefulBeanToCsvBuilder<T> withLineEnd(String lineEnd) {
        this.lineEnd = lineEnd;
        return this;
    }

    /**
     * @param throwExceptions Whether or not exceptions should be thrown while
     *   writing a CSV file. If not, they may be retrieved later by calling
     *   {@link com.opencsv.bean.StatefulBeanToCsv#getCapturedExceptions() }.
     * @return this
     */
    public StatefulBeanToCsvBuilder<T> withThrowExceptions(boolean throwExceptions) {
        this.throwExceptions = throwExceptions;
        return this;
    }
    
    /**
     * Sets whether or not results must be written in the same order in which
     * they appear in the list of beans provided as input.
     * 
     * @param orderedResults Whether or not the lines written are in the same
     *   order they appeared in the input
     * @return this
     * @see StatefulBeanToCsv#setOrderedResults(boolean)
     * @since 4.0
     */
    public StatefulBeanToCsvBuilder<T> withOrderedResults(boolean orderedResults) {
        this.orderedResults = orderedResults;
        return this;
    }
    
    /**
     * Sets the locale to be used for all error messages.
     * @param errorLocale Locale for error messages. If null, the default locale
     *   is used.
     * @return this
     * @see StatefulBeanToCsv#setErrorLocale(java.util.Locale) 
     * @since 4.0
     */
    public StatefulBeanToCsvBuilder<T> withErrorLocale(Locale errorLocale) {
        this.errorLocale = ObjectUtils.defaultIfNull(errorLocale, Locale.getDefault());
        return this;
    }

    /**
     * Sets whether all outputs should be put in quotes.
     * Defaults to {@code true}.
     *
     * @param applyQuotesToAll Whether all outputs should be quoted
     * @return this
     * @see com.opencsv.CSVWriter#writeNext(String[], boolean)
     * @since 4.2
     */
    public StatefulBeanToCsvBuilder<T> withApplyQuotesToAll(boolean applyQuotesToAll) {
        this.applyQuotesToAll = applyQuotesToAll;
        return this;
    }
    
    /**
     * Builds a StatefulBeanToCsv from the information provided, filling in
     * default values where none have been specified.
     * @return A new {@link StatefulBeanToCsv}
     */
    public StatefulBeanToCsv<T> build() {
        StatefulBeanToCsv<T> sbtcsv;
        if (writer != null) {
            sbtcsv = new StatefulBeanToCsv<>(escapechar, lineEnd, mappingStrategy,
                    quotechar, separator, throwExceptions, writer, applyQuotesToAll);
        } else {
            sbtcsv = new StatefulBeanToCsv<>(mappingStrategy, throwExceptions, applyQuotesToAll, csvWriter);
        }

        sbtcsv.setOrderedResults(orderedResults);
        sbtcsv.setErrorLocale(errorLocale);
        return sbtcsv;
    }
}
