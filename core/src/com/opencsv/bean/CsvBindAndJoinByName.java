/*
 * Copyright 2017 Andrew Rucker Jones.
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

import java.lang.annotation.*;
import java.util.regex.Matcher;

import org.apache.commons.collections4.MultiValuedMap;

/**
 * Joins the values of multiple columns from the input into one bean field based
 * on a pattern for the column names.
 * 
 * @author Andrew Rucker Jones
 * @since 4.2
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface CsvBindAndJoinByName {

    /**
     * Whether or not the annotated field is required to be present in every
     * data set of the input.
     * This means that the input cannot be empty. The output after conversion is
     * not guaranteed to be non-empty. "Input" means the string from every
     * matching field in the CSV file on reading and the bean member variable on
     * writing.
     *
     * @return If the field is required to contain information.
     */
    boolean required() default false;

    /**
     * A regular expression defining which column names are to be included in
     * this bean field.
     * If not specified, the name of the column must be identical to the name
     * of the field(s). Since this annotation can combine multiple columns from
     * the input, and they are allowed to have the same name, it is legitimate
     * to let opencsv default to using the field name even with this annotation.
     *
     * @return The name of the column(s) in the CSV file from which this field
     * should be taken.
     */
    String column() default "";

    /**
     * Defines the locale to be used for decoding the argument.
     * <p>If not specified, the current default locale is used. The locale must be
     * one recognized by {@link java.util.Locale}. Locale conversion is supported
     * for the following data types:<ul>
     * <li>byte and {@link java.lang.Byte}</li>
     * <li>float and {@link java.lang.Float}</li>
     * <li>double and {@link java.lang.Double}</li>
     * <li>int and {@link java.lang.Integer}</li>
     * <li>long and {@link java.lang.Long}</li>
     * <li>short and {@link java.lang.Short}</li>
     * <li>{@link java.math.BigDecimal}</li>
     * <li>{@link java.math.BigInteger}</li>
     * <li>All time data types supported by {@link com.opencsv.bean.CsvDate}</li></ul>
     * <p>The locale must be in a format accepted by
     * {@link java.util.Locale#forLanguageTag(java.lang.String)}</p>
     * <p>Caution must be exercised with the default locale, for the default
     * locale for numerical types does not mean the locale of the running
     * program, such as en-US or de-DE, but rather <em>no</em> locale. Numbers
     * will be parsed more or less the way the Java compiler would parse them.
     * That means, for instance, that thousands separators in long numbers are
     * not permitted, even if the locale of the running program would accept
     * them. When dealing with locale-sensitive data, it is always best to
     * specify the locale explicitly.</p>
     *
     * @return The locale selected. The default is indicated by an empty string.
     */
    String locale() default "";
    
    /**
     * Defines the class used for the multi-valued map.
     * <p>This must be a specific implementation of
     * {@link org.apache.commons.collections4.MultiValuedMap}, and not an
     * interface! The default is set to {@code MultiValuedMap.class} as a signal
     * to use the default for the interface supplied in the bean to be
     * populated.</p>
     * <p>The logic for determining which class to instantiate for the
     * multi-valued map is as follows. In all cases, the implementation must
     * have a nullary constructor.</p>
     * <ol><li>If the bean declares a specific implementation instead of the
     * associated interface
     * (e.g. {@link org.apache.commons.collections4.multimap.ArrayListValuedHashMap}
     * vs.
     * {@link org.apache.commons.collections4.ListValuedMap}), that specific
     * implementation will always be used.</li>
     * <li>Otherwise, the implementation named in this field will be used, if it
     * is not an interface.</li>
     * <li>If no implementation is specified in this field (i.e. if
     * an interface is specified, as is the default), a default is used
     * based on the interface of the bean field annotated. These are:
     * <ul><li>{@link org.apache.commons.collections4.multimap.ArrayListValuedHashMap} for {@link org.apache.commons.collections4.MultiValuedMap}</li>
     * <li>{@link org.apache.commons.collections4.multimap.ArrayListValuedHashMap} for {@link org.apache.commons.collections4.ListValuedMap}</li>
     * <li>{@link org.apache.commons.collections4.multimap.HashSetValuedHashMap} for {@link org.apache.commons.collections4.SetValuedMap}</li></ul></li></ol>
     * 
     * @return A class implementing {@link org.apache.commons.collections4.MultiValuedMap}
     */
    Class<? extends MultiValuedMap> mapType() default MultiValuedMap.class;
    
    /**
     * Defines what type the elements of the map should have.
     * It is necessary to instantiate elements of the map, and it is not
     * always possible to determine the type of the elements at runtime.
     * A perfect example of this is {@code Map<String, ? extends Number>}.
     * 
     * @return The type of the map elements
     */
    Class<?> elementType();

    /**
     * Once the data points have been recovered from the various columns of the
     * input, a custom converter can optionally be specified for conversion of
     * each of the data points before they are joined in a
     * {@link org.apache.commons.collections4.MultiValuedMap}.
     *
     * @return The converter applied to each of the data points extracted from
     * the input
     * @since 4.3
     */
    Class<? extends AbstractCsvConverter> converter() default AbstractCsvConverter.class;

    /**
     * If this is anything but an empty string, it will be used as a regular
     * expression to extract part of the input before conversion to the bean
     * field.
     * <p>An empty string behaves as if the regular expression {@code ^(.*)$}
     * had been specified.</p>
     * <p>The regular expression will be compiled and every field of input will
     * be passed through it, naturally after the input has been normalized
     * (quotations and escape characters removed). The first capture group will
     * be extracted, and that string will be passed on to the appropriate
     * conversion routine for the bean field in question.</p>
     * <p>This makes it possible to easily convert input fields with forms like
     * {@code Grade: 94.2} into {@code 94.2}, which can then be converted to a
     * floating point bean field, all without writing a custom converter.</p>
     * <p>The regular expression is applied to the entire string in question
     * (i.e. with {@link Matcher#matches()}), instead of just the beginning of
     * the string ({@link Matcher#lookingAt()}) or anywhere in the string
     * ({@link Matcher#find()}). If it fails to match, the input string is
     * passed unchanged to the appropriate conversion routine for the bean
     * field. The reason for this is two-fold:</p>
     * <ol><li>The matching may occur against an empty string. If the field is
     * not required, this is legitimate, but it's likely the regular expression
     * is not written to accommodate this possibility, and it may indeed not be
     * at all desirable to.</li>
     * <li>If there is an error in either the regular expression or the input
     * that causes the match to fail, there is a good likelihood that the
     * subsequent conversion will fail with a
     * {@link com.opencsv.exceptions.CsvDataTypeMismatchException} if the
     * input is not being converted into a simple string.</li></ol>
     * <p>This is the inverse operation of {@link #format()}.</p>
     *
     * @return A regular expression, the first capture group of which will be
     * used for conversion to the bean field
     * @since 4.3
     */
    String capture() default "";

    /**
     * If this is anything but an empty string, it will be used as a format
     * string for {@link java.lang.String#format(String, Object...)} on
     * writing.
     * <p>An empty string behaves as if the format string {@code "%s"} had been
     * specified.</p>
     * <p>The format string, if it is not empty, should contain one and only
     * one {@code %s}, which will be replaced by the string value of the bean
     * field after conversion. If, however, the bean field is empty, then the
     * output will be empty as well, as opposed to passing an empty string to
     * this format string and using that as the output.</p>
     * <p>This is the inverse operation of {@link #capture()}.</p>
     *
     * @return A format string for writing fields
     * @since 4.3
     */
    String format() default "";
}
