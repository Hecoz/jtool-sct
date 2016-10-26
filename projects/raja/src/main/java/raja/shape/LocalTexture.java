/* $Id: LocalTexture.java,v 1.1.1.1 2001/01/08 23:10:14 gregoire Exp $
 * Copyright (C) 1999-2000 E. Fleury & G. Sutre
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package raja.shape;

import raja.RGB;
import raja.io.*;
import java.util.HashMap;


/**
 * A class to encapsulate the local physical properties of a form's
 * boundary.  This class provides methods to get the following local
 * physical properties of a form's boundary: the diffuse (kd),
 * reflection (krl, krg) and transmission (ktl, ktg) coefficients, the
 * reflection power index (ns) and the transmission power index (nt).
 * </ul>
 *
 * @see Texture
 * @see TexturedForm
 * @see TexturedLocalGeometry
 * @see RGB
 *
 * @author Emmanuel Fleury
 * @author Gr�goire Sutre
 */
public class LocalTexture implements java.io.Serializable, Writable
{
    private final RGB kd, krl, krg, ktl, ktg;
    private final int ns, nt;

    public LocalTexture(RGB kd, RGB kr, RGB kt, int ns, int nt)
    {
        this.kd = kd;
        this.krl = kr;
        this.krg = kr;
        this.ktl = kt;
        this.ktg = kt;
        this.ns = ns;
        this.nt = nt;
    }

    public LocalTexture(RGB kd, RGB krl, RGB krg, RGB ktl, RGB ktg, int ns, int nt)
    {
        this.kd = kd;
        this.krl = krl;
        this.krg = krg;
        this.ktl = ktl;
        this.ktg = ktg;
        this.ns = ns;
        this.nt = nt;
    }

    public LocalTexture(RGB kd, RGB krg, double krl, RGB ktg, double ktl, int ns, int nt)
    {
        this.kd = kd;
        this.krl = new RGB(krl);
        this.krg = krg;
        this.ktl = new RGB(ktl);
        this.ktg = ktg;
        this.ns = ns;
        this.nt = nt;
    }

    public LocalTexture(double kd, double krl, double krg, double ktl, double ktg, int ns, int nt, RGB color)
    {
        this.kd = RGB.product(color, kd);
        this.krl = new RGB(krl);
        this.krg = new RGB(krg);
        this.ktl = new RGB(ktl);
        this.ktg = new RGB(ktg);
        this.ns = ns;
        this.nt = nt;
    }

    public LocalTexture(double kd, double kr, double kt, int ns, int nt, RGB color)
    {
        this.kd = RGB.product(color, kd);
        this.krl = new RGB(kr);
        this.krg = krl;
        this.ktl = new RGB(kt);
        this.ktg = ktl;
        this.ns = ns;
        this.nt = nt;
    }

    private static RGB RGBFilter(Object obj)
    {
        if (obj instanceof Number)
        {
            return new RGB(((Number) obj).doubleValue());
        }
        else
        {
            return (RGB) obj;
        }
    }

    /**
     * Builds the object LocalTexture from a StreamLexer.
     */
    public static Object build(ObjectReader reader)
	throws java.io.IOException
    {
	/* Initialisation */
	HashMap map = new HashMap();

	map.put("color",null);
	map.put("kd",null);
	map.put("krg",null);
	map.put("krl",null);
	map.put("ktg",null);
	map.put("ktl",null);
	map.put("ns",null);
	map.put("nt",null);

	/* Parsing */
	reader.readFields(map);

        if (map.get("color") == null)
        {
            return new LocalTexture(RGBFilter(map.get("kd")),
                                    RGBFilter(map.get("krl")),
                                    RGBFilter(map.get("krg")),
                                    RGBFilter(map.get("ktl")),
                                    RGBFilter(map.get("ktg")),
                                    ((Number) map.get("ns")).intValue(),
                                    ((Number) map.get("nt")).intValue());
        }
        else
        {
            return new LocalTexture(((Number) map.get("kd")).doubleValue(),
                                    ((Number) map.get("krl")).doubleValue(),
                                    ((Number) map.get("krg")).doubleValue(),
                                    ((Number) map.get("ktl")).doubleValue(),
                                    ((Number) map.get("ktg")).doubleValue(),
                                    ((Number) map.get("ns")).intValue(),
                                    ((Number) map.get("nt")).intValue(),
                                    (RGB) map.get("color"));
        }
    }

    /**
     * Returns the diffuse coefficient.
     * @return an instance of <code>RGB</code> that is the diffuse coefficient.
     */
    public RGB getKd()
    {
        return kd;
    }

    /**
     * Returns the local reflection coefficient.
     * @return an instance of <code>RGB</code> that is the local reflection coefficient.
     */
    public RGB getKrl()
    {
        return krl;
    }

    /**
     * Returns the global reflection coefficient.
     * @return an instance of <code>RGB</code> that is the global reflection coefficient.
     */
    public RGB getKrg()
    {
        return krg;
    }

    /**
     * Returns the local transmission coefficient.
     * @return an instance of <code>RGB</code> that is the local transmission coefficient.
     */
    public RGB getKtl()
    {
        return ktl;
    }

    /**
     * Returns the global transmission coefficient.
     * @return an instance of <code>RGB</code> that is the global transmission coefficient.
     */
    public RGB getKtg()
    {
        return ktg;
    }

    /**
     * Returns the reflection power index.
     */
    public int getNs()
    {
        return ns;
    }

    /**
     * Returns the transmission power index.
     */
    public int getNt()
    {
        return nt;
    }

    public String toString()
    {
        return ObjectWriter.toString(this);
    }
    public void write(ObjectWriter writer) throws java.io.IOException
    {
        Object[][] fields = { { "kd", kd },
                              { "krl", krl },
                              { "krg", krg },
                              { "ktl", ktl },
                              { "ktg", ktg },
                              { "ns", new Integer(ns) },
                              { "nt", new Integer(nt) } };
        writer.writeFields(fields);
    }
}
