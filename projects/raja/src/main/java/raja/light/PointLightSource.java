/* $Id: PointLightSource.java,v 1.1.1.1 2001/01/08 23:10:14 gregoire Exp $
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

package raja.light;

import raja.*;
import raja.io.*;
import java.util.HashMap;


public class PointLightSource implements LightSource, java.io.Serializable, Writable
{
    private final RGB light;
    private final Point3D origin;

    public PointLightSource(Point3D p, RGB light)
    {
        origin = p;
        this.light = light;
    }
    public PointLightSource(double x, double y, double z, RGB light)
    {
        origin = new Point3D(x, y, z);
        this.light = light;
    }

    public static Object build(ObjectReader reader)
	throws java.io.IOException
    {
	/* Initialisation */
	HashMap map = new HashMap();

	map.put("origin",null);
	map.put("light",null);

	/* Parsing */
	reader.readFields(map);
	
	return new PointLightSource((Point3D) map.get("origin"),
				    (RGB) map.get("light"));
    }

    public LightRay getLightRay(Point3D p)
    {
	return new LightRay(p, origin, light);
    }
    public RGB getMax()
    {
        return light;
    }

    public String toString()
    {
        return ObjectWriter.toString(this);
    }
    public void write(ObjectWriter writer) throws java.io.IOException
    {
        Object[][] fields = { { "origin", origin },
                              { "light", light } };
        writer.writeFields(fields);
    }
}
