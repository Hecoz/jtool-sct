/* $Id: World.java,v 1.1 2001/02/25 01:25:18 gregoire Exp $
 * Copyright (C) 2001 E. Fleury & G. Sutre
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

package raja.renderer;

import raja.*;
import raja.light.LightSource;
import raja.shape.*;
import raja.io.*;
import raja.util.List;

import java.util.HashMap;
import java.util.Iterator;


public class World implements java.io.Serializable, Writable
{
    private final Solid solid;
    private final List lights;
    private RGB backgroundLight;
    private RGB ambiantLight;
    private Volume ambiantVolume;

    public World(Solid solid)
    {
        this.solid = solid;
        lights = new List();
        backgroundLight = RGB.black;
        ambiantLight = RGB.black;
        ambiantVolume = new IsotropicVolume(1.0);
    }

    public World(Solid solid, 
		 List lights, 
		 RGB backgroundLight, 
		 RGB ambiantLight, 
		 Volume ambiantVolume)
    {
        this.solid = solid;
        this.lights = lights;
        this.backgroundLight = backgroundLight;
        this.ambiantLight = ambiantLight;
        this.ambiantVolume = ambiantVolume;
    }    

    public static Object build(ObjectReader reader)
	throws java.io.IOException
    {
	/* Initialisation */
	HashMap map = new HashMap();

	map.put("lights", new List());
	map.put("solid", null);
	map.put("ambiantLight", RGB.black);
	map.put("backgroundLight", RGB.black);
	map.put("ambiantVolume", new IsotropicVolume(1.0));

	/* Parsing */
	reader.readFields(map);
       
	return new World((Solid) map.get("solid"),
                         (List) map.get("lights"),
                         (RGB) map.get("backgroundLight"),
                         (RGB) map.get("ambiantLight"),
                         (Volume) map.get("ambiantVolume"));
    }

    public void addLightSource(LightSource light)
    {
        lights.add(light);
    }
    Iterator lightIterator()
    {
        return lights.iterator();
    }
    Solid getSolid()
    {
        return solid;
    }
    public void setBackgroundLight(RGB light)
    {
        backgroundLight = light;
    }
    public void setAmbiantLight(RGB light)
    {
        ambiantLight = light;
    }
    public void setAmbiantVolume(Volume v)
    {
        ambiantVolume = v;
    }
    public RGB getBackgroundLight()
    {
        return backgroundLight;
    }
    public RGB getAmbiantLight()
    {
        return ambiantLight;
    }
    public double getAmbiantRefractiveIndex(Point3D p)
    {
        return ambiantVolume.refractiveIndex(p);
    }

    public String toString()
    {
        return ObjectWriter.toString(this);
    }
    public void write(ObjectWriter writer) throws java.io.IOException
    {
        Object[][] fields = { { "solid", solid },
                              { "lights", lights },
                              { "backgroundLight", backgroundLight },
                              { "ambiantLight", ambiantLight },
                              { "ambiantVolume", ambiantVolume } };
        writer.writeFields(fields);
    }
}
