/* $Id: StereoCamera.java,v 1.1.1.1 2001/01/08 23:10:14 gregoire Exp $
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

package raja.renderer;

import raja.*;
import raja.io.*;
import java.util.HashMap;


public class StereoCamera extends Camera
{
    private final Camera camera;
    private final Vector3D trans;

    public StereoCamera(Camera camera, Vector3D trans)
    {
        super();
        this.camera = camera;
        this.trans = trans;
    }

    public StereoCamera(Point3D origin, Vector3D direction, double focal, double screenWidth, double screenHeight, double step, boolean cross)
    {
        this(new HorizontalCamera(origin, direction, focal, screenWidth, screenHeight),
             cross ? Vector3D.product(Vector3D.normalization(new Vector3D(direction.y, -direction.x, 0)), step / 2)
                   : Vector3D.product(Vector3D.normalization(new Vector3D(-direction.y, direction.x, 0)), step / 2));
    }

    public static Object build(ObjectReader reader)
	throws java.io.IOException
    {
        /* Initialisation */
	HashMap map = new HashMap();

	map.put("screenWidth",null);
	map.put("screenHeight",null);
	map.put("focal",null);
	map.put("direction",null);
	map.put("origin",null);
	map.put("pixelwidth",null);
	map.put("pixelheight",null);
        map.put("step",null);
        map.put("cross",null);

	/* Parsing */
	reader.readFields(map);

        return new StereoCamera((Point3D) map.get("origin"),
                                (Vector3D) map.get("direction"),
                                ((Number) map.get("focal")).doubleValue(),
                                ((Number) map.get("screenWidth")).doubleValue(),
                                ((Number) map.get("screenHeight")).doubleValue(),
                                ((Number) map.get("step")).doubleValue(),
                                false);
    }

    Ray getRay(double x, double y)
    {
        Ray r;

        if (x < 0.5) {
            r = camera.getRay(2*x, y);
            r.origin.translate(trans);
        }
        else {
            r = camera.getRay(2*x - 1, y);
            r.origin.translate(Vector3D.opposite(trans));
        }

        return r;
    }
}
