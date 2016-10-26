/* $Id: DirectionalLightSource.java,v 1.1.1.1 2001/01/08 23:10:14 gregoire Exp $
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


public class DirectionalLightSource implements LightSource, java.io.Serializable, Writable
{
    private final RGB light;
    private final Vector3D direction;

    public DirectionalLightSource(Vector3D v, RGB light)
    {
        direction = v;
        this.light = light;
    }
    public DirectionalLightSource(double x, double y, double z, RGB light)
    {
        direction = new Vector3D(x, y, z);
        this.light = light;
    }

    public static Object build(ObjectReader reader)
	throws java.io.IOException
    {
	/* Initialisation */
	HashMap map = new HashMap();

	map.put("direction",null);
	map.put("light",null);

	/* Parsing */
	reader.readFields(map);

        return new DirectionalLightSource((Vector3D) map.get("direction"),
                                          (RGB) map.get("light"));
    }

    public LightRay getLightRay(Point3D p)
    {
	return new LightRay(p, Vector3D.opposite(direction), light, Double.POSITIVE_INFINITY);
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
        Object[][] fields = { { "direction", direction },
                              { "light", light } };
        writer.writeFields(fields);
    }
}
