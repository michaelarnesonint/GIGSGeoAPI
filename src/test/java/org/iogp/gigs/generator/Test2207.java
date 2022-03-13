/*
 *    GeoAPI - Java interfaces for OGC/ISO standards
 *    http://www.geoapi.org
 *
 *    Copyright (C) 2011-2021 Open Geospatial Consortium, Inc.
 *    All Rights Reserved. http://www.opengeospatial.org/ogc/legal
 *
 *    Permission to use, copy, and modify this software and its documentation, with
 *    or without modification, for any purpose and without fee or royalty is hereby
 *    granted, provided that you include the following on ALL copies of the software
 *    and documentation or portions thereof, including modifications, that you make:
 *
 *    1. The full text of this NOTICE in a location viewable to users of the
 *       redistributed or derivative work.
 *    2. Notice of any changes or modifications to the OGC files, including the
 *       date changes were made.
 *
 *    THIS SOFTWARE AND DOCUMENTATION IS PROVIDED "AS IS," AND COPYRIGHT HOLDERS MAKE
 *    NO REPRESENTATIONS OR WARRANTIES, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 *    TO, WARRANTIES OF MERCHANTABILITY OR FITNESS FOR ANY PARTICULAR PURPOSE OR THAT
 *    THE USE OF THE SOFTWARE OR DOCUMENTATION WILL NOT INFRINGE ANY THIRD PARTY
 *    PATENTS, COPYRIGHTS, TRADEMARKS OR OTHER RIGHTS.
 *
 *    COPYRIGHT HOLDERS WILL NOT BE LIABLE FOR ANY DIRECT, INDIRECT, SPECIAL OR
 *    CONSEQUENTIAL DAMAGES ARISING OUT OF ANY USE OF THE SOFTWARE OR DOCUMENTATION.
 *
 *    The name and trademarks of copyright holders may NOT be used in advertising or
 *    publicity pertaining to the software without specific, written prior permission.
 *    Title to copyright in this software and any associated documentation will at all
 *    times remain with copyright holders.
 */
package org.iogp.gigs.generator;

import java.util.Arrays;
import java.io.IOException;


/**
 * Code generator for {@link Test2207}. This generator needs to be executed only if the GIGS data changed.
 * The code is sent to the standard output; maintainer need to copy-and-paste the relevant methods to the
 * test class, but be aware that the original code may contain manual changes that need to be preserved.
 *
 * @author  Martin Desruisseaux (Geomatys)
 * @version 1.0
 * @since   1.0
 */
public final class Test2207 extends TestMethodGenerator {
    /**
     * Launcher.
     *
     * @param  args  ignored.
     * @throws IOException if an error occurred while reading the test data.
     */
    public static void main(String[] args) throws IOException {
        new Test2207().run();
    }

    /**
     * Creates a new generator.
     */
    private Test2207() {
    }

    /**
     * EPSG codes of CRS having north axis before east axis.
     * Values must be sorted in increasing order.
     */
    private final int[] isNorthAxisFirst = {
         2193,                                                                          // NZGD2000
         2338,  2339,  2340,  2341,  2342,  2343,  2344,  2345,  2346, 2347,   2348,    // Xian 1980
         3114,  3115,  3116,  3117,  3118,                                              // MAGNA-SIRGAS
         3334,  3335,  3346,  3350,  3351,  3352,  3836,  3844,                         // Pulkovo 1942 and LKS94
        21413, 21414, 21415, 21416, 21417, 21418, 21419, 21420, 21421, 21422, 21423,    // Beijing 1954
        21896, 21897, 21898, 21899,                                                     // Bogota 1975
        22171, 22172, 22173, 22174, 22175, 22176, 22177,                                // POSGAR 98
        22181, 22182, 22183, 22184, 22185, 22186, 22187,                                // POSGAR 94
        22191, 22192, 22193, 22194, 22195, 22196, 22197,                                // Campo Inchauspe
        28409, 28416, 28424,                                                            // Pulkovo 1942
        29702,                                                                          // Tananarive (Paris)
        31466, 31467, 31468, 31469                                                      // DHDN
    };

    /**
     * EPSG codes of CRS with west orientated longitude axis.
     * Values must be sorted in increasing order.
     */
    private final int[] isWestOrientated = {
        29371                                                                           // Schwarzeck
    };

    /**
     * EPSG codes of CRS with south orientated latitude axis.
     * Values must be sorted in increasing order.
     */
    private final int[] isSouthOrientated = {
        29371                                                                           // Schwarzeck
    };

    /**
     * Generates the code.
     *
     * @throws IOException if an error occurred while reading the test data.
     */
    private void run() throws IOException {
        final DataParser data = new DataParser(Series.PREDEFINED, "GIGS_lib_2207_ProjectedCRS.txt",
                Integer.class,      // [0]: EPSG Projected CRS Code
                Integer.class,      // [1]: EPSG Datum Code
                String .class,      // [2]: Geographic CRS Name
                String .class,      // [3]: Projected CRS Name
                String .class,      // [4]: Alias(es)
                String .class,      // [5]: EPSG Usage Extent
                String .class);     // [6]: GIGS Remarks
        /*
         * Appends columns giving the axis orientation that we expect.
         * Must be done before to group rows.
         */
        data.appendColumns(0,
                (code) -> Arrays.binarySearch(isNorthAxisFirst,  code) >= 0,        // [7]
                (code) -> Arrays.binarySearch(isWestOrientated,  code) >= 0,        // [8]
                (code) -> Arrays.binarySearch(isSouthOrientated, code) >= 0);       // [9]
        /*
         * Group related projections in a single method. For example the following projections will be
         * tested in a loop inside a single method instead of generating a method for each projection:
         *
         *   32601    6326    WGS 84    WGS 84 / UTM zone 1N    World - N hemisphere - 180°W to 174°W - by country
         *   32602    6326    WGS 84    WGS 84 / UTM zone 2N    World - N hemisphere - 174°W to 168°W - by country
         *   32603    6326    WGS 84    WGS 84 / UTM zone 3N    World - N hemisphere - 168°W to 162°W - by country
         *   32604    6326    WGS 84    WGS 84 / UTM zone 4N    World - N hemisphere - 162°W to 156°W - by country
         *   32605    6326    WGS 84    WGS 84 / UTM zone 5N    World - N hemisphere - 156°W to 150°W - by country
         *
         * Using loops for such group of projections save hundreds of methods.
         */
        data.regroup(0, new int[] {1, 2, 4, 7, 8, 9}, 3, "\\s+zone\\s+\\w+", "\\s+CM\\s+\\w+");

        while (data.next()) {
            final int[]    codes      = data.getInts   (0);
            final int      datumCode  = data.getInt    (1);
            final String   geographicCRS   = data.getString (2);
            final String   name       = data.getString (3);
            final String[] aliases    = data.getStrings(4);
            final String   extent     = data.getString (5);
            final String   remarks    = data.getString (6);

            out.append('\n');
            indent(1); out.append("/**\n");
            indent(1); out.append(" * Tests “").append(name).append("” projected CRS creation from the factory.\n");
            indent(1); out.append(" *\n");
            printJavadocKeyValues("EPSG projected CRS code", codes,
                                  "EPSG projected CRS name", name,
                                  "Alias(es) given by EPSG", aliases,
                                  "Geographic CRS name", geographicCRS,
                                  "EPSG Usage Extent", extent);
            printRemarks(remarks);
            printJavadocThrows("if an error occurred while creating the projected CRS from the EPSG code.");
            printTestMethodSignature(codes.length == 1 ? codes[0] : -1, name);
            printFieldAssignments("name",              name,
                                  "aliases",           aliases,
                                  "geographicCRS",     geographicCRS,
                                  "datumCode",         datumCode,
                                  "isNorthAxisFirst",  data.getBoolean(7),
                                  "isWestOrientated",  data.getBoolean(8),
                                  "isSouthOrientated", data.getBoolean(9));
            printCallsToMethod("createAndVerifyProjectedCRS", codes);
            indent(1); out.append('}');
            saveTestMethod();
        }
        flushAllMethods();
    }
}