/*
 *    GeoAPI - Java interfaces for OGC/ISO standards
 *    http://www.geoapi.org
 *
 *    Copyright (C) 2016-2021 Open Geospatial Consortium, Inc.
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
package org.iogp.gigs.internal.geoapi;

import java.util.ServiceLoader;
import javax.measure.Unit;
import javax.measure.quantity.Time;
import javax.measure.quantity.Angle;
import javax.measure.quantity.Length;
import javax.measure.quantity.Pressure;
import javax.measure.quantity.Dimensionless;
import javax.measure.spi.ServiceProvider;
import javax.measure.spi.SystemOfUnits;


/**
 * Pre-defined constants for the units of measurement used by the conformance tests.
 * This pseudo-factory provides separated methods for all units needed by {@code geoapi-conformance}.
 *
 * @author  Martin Desruisseaux (Geomatys)
 * @version 1.0
 * @since   1.0
 */
public class Units extends PseudoFactory {
    /**
     * The default instance, created when first needed.
     *
     * @see #getInstance()
     */
    private static Units instance;

    /**
     * The implementation-dependent system of units for creating base units.
     * The Unit Of Measurement library is determined by the GeoAPI implementation being tested.
     */
    public final SystemOfUnits system;

    /**
     * Linear units used in the tests.
     */
    private final Unit<Length> metre, kilometre, foot, footSurveyUS;

    /**
     * Angular units used in the tests.
     */
    private final Unit<Angle> radian, microradian, degree, grad, arcSecond;

    /**
     * Temporal units used in the tests.
     */
    private final Unit<Time> second, day;

    /**
     * Pressure units used in the tests.
     */
    private final Unit<Pressure> pascal, hectopascal;

    /**
     * Scale units used in the tests.
     */
    private final Unit<Dimensionless> one, ppm;

    /**
     * Creates a new factory which will use the given system of units.
     *
     * @param  system  the system of units to use for creating base units.
     */
    public Units(final SystemOfUnits system) {
        this.system  = system;
        metre        = system.getUnit(Length.class);
        radian       = system.getUnit(Angle.class);
        second       = system.getUnit(Time.class);
        pascal       = system.getUnit(Pressure.class);
        one          = getDimensionless(system);
        kilometre    = metre .multiply(1000);
        foot         = metre .multiply(0.3048);
        footSurveyUS = metre .multiply(12 / 39.37);
        degree       = radian.multiply(Math.PI / 180);
        grad         = radian.multiply(Math.PI / 200);
        arcSecond    = radian.multiply(Math.PI / (180*60*60));
        microradian  = radian.divide(1E6);
        day          = second.multiply(24*60*60);
        hectopascal  = pascal.multiply(100);
        ppm          = one   .divide(1000000);
    }

    /**
     * Returns the default units factory. This factory uses the unit service provider which is
     * {@linkplain ServiceProvider#current() current} at the time of the first invocation of this method.
     *
     * @return the default units factory.
     */
    public static synchronized Units getInstance() {
        if (instance == null) {
            setInstance(ServiceProvider.current());
        }
        return instance;
    }

    /**
     * Initializes {@link #instance} using the given unit service provider.
     *
     * @param  provider  the unit provider to use.
     */
    private static void setInstance(final ServiceProvider provider) {
        instance = new Units(provider.getSystemOfUnitsService().getSystemOfUnits());
    }

    /**
     * Sets the units factory by loading the first service provider found using the given class loader.
     *
     * @param  loader  the class loader to use for initializing the instance, or {@code null} to reset the default.
     */
    public static synchronized void setInstance(final ClassLoader loader) {
        if (loader != null) {
            for (final ServiceProvider provider : ServiceLoader.load(ServiceProvider.class, loader)) {
                setInstance(provider);
                return;
            }
        }
        instance = null;
    }

    /**
     * Returns the dimensionless unit. This is a workaround for what seems to be a bug
     * in the reference implementation 1.0.1 of unit API.
     *
     * @param  system  the system of units from which to get the dimensionless unit.
     * @return the dimensionless unit.
     */
    private static Unit<Dimensionless> getDimensionless(final SystemOfUnits system) {
        Unit<Dimensionless> unit = system.getUnit(Dimensionless.class);
        if (unit == null) try {
            unit = ((Unit<?>) Class.forName("tec.units.ri.AbstractUnit").getField("ONE").get(null)).asType(Dimensionless.class);
        } catch (ReflectiveOperationException | ClassCastException e) {
            throw new IllegalArgumentException("Can not create a dimensionless unit from the given provider.");
        }
        return unit;
    }

    /** {@return Base unit of measurement for lengths.}            */  public Unit<Length>   metre()        {return metre;}
    /** {@return Unit of measurement defined as 1000 metres.}      */  public Unit<Length>   kilometre()    {return kilometre;}
    /** {@return Unit of measurement defined as 0.3048 metres.}    */  public Unit<Length>   foot()         {return foot;}
    /** {@return Unit of measurement defined as 12/39.37 metres.}  */  public Unit<Length>   footSurveyUS() {return footSurveyUS;}
    /** {@return Base unit of measurement for angle.}              */  public Unit<Angle>    radian()       {return radian;}
    /** {@return Unit of measurement defined as 1E-6 radians.}     */  public Unit<Angle>    microradian()  {return microradian;}
    /** {@return Unit of measurement defined as π/180 radians.}    */  public Unit<Angle>    degree()       {return degree;}
    /** {@return Unit of measurement defined as π/200 radians.}    */  public Unit<Angle>    grad()         {return grad;}
    /** {@return Unit of measurement defined as 1/(60×60) degree.} */  public Unit<Angle>    arcSecond()    {return arcSecond;}
    /** {@return Base unit of measurement for durations.}          */  public Unit<Time>     second()       {return second;}
    /** {@return Unit of measurement defined as 24×60×60 seconds.} */  public Unit<Time>     day()          {return day;}
    /** {@return Base unit of measurement for pressure.}           */  public Unit<Pressure> pascal()       {return pascal;}
    /** {@return Unit of measurement defined as 100 pascals.}      */  public Unit<Pressure> hectopascal()  {return hectopascal;}
    /** {@return Dimensionless unit for scale measurements.}       */  public Unit<Dimensionless> one()     {return one;}
    /** {@return The "parts per million" unit.}                    */  public Unit<Dimensionless> ppm()     {return ppm;}
}
