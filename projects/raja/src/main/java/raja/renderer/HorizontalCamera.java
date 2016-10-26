/* $Id: HorizontalCamera.java,v 1.1.1.1 2001/01/08 23:10:14 gregoire Exp $
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


public class HorizontalCamera extends Camera implements Writable
{
    private final double screenWidth, screenHeight;
    private final Vector3D direction;   // vecteur norm�
    private final double focal;
    private final Point3D origin, screenTopLeft, screenTopRight, screenBottomLeft;

    public HorizontalCamera(Point3D origin, Vector3D direction, double focal, double screenWidth, double screenHeight)
    {
        super();
        this.origin = origin;
        this.direction = Vector3D.normalization(direction);
        this.focal = focal;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;


        // Computation of the screen's corners

        Vector3D d = this.direction;
        Point3D screenCenter = new Point3D(origin);
        screenCenter.translate(Vector3D.product(d, focal));

        Vector3D u = Vector3D.normalization(new Vector3D(-d.y, d.x, 0));
        Vector3D v = Vector3D.crossProduct(d, u);
        Vector3D uc = Vector3D.product(u, screenWidth / 2);
        Vector3D vc = Vector3D.product(v, screenHeight / 2);

        screenTopLeft = new Point3D(screenCenter, Vector3D.sum(uc, vc));
        screenTopRight = new Point3D(screenCenter, Vector3D.sum(Vector3D.opposite(uc), vc));
        screenBottomLeft = new Point3D(screenCenter, Vector3D.sum(uc, Vector3D.opposite(vc)));
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
	map.put("pixelWidth",null);
	map.put("pixelHeight",null);

	/* Parsing */
	reader.readFields(map);
       
	return new HorizontalCamera((Point3D) map.get("origin"),
                                    (Vector3D) map.get("direction"),
                                    ((Number) map.get("focal")).doubleValue(),
                                    ((Number) map.get("screenWidth")).doubleValue(),
                                    ((Number) map.get("screenHeight")).doubleValue());
    }

    public String toString()
    {
        return ObjectWriter.toString(this);
    }
    public void write(ObjectWriter writer) throws java.io.IOException
    {
        Object[][] fields = { { "origin", origin },
                              { "direction", direction },
                              { "focal", new Double(focal) },
                              { "screenWidth", new Double(screenWidth) },
                              { "screenHeight", new Double(screenHeight) } };
        writer.writeFields(fields);
    }

    Ray getRay(double x, double y)
    {
        Point3D pixel = new Point3D(screenTopLeft.x + (x * (screenTopRight.x - screenTopLeft.x)) + (y * (screenBottomLeft.x - screenTopLeft.x)),
                                    screenTopLeft.y + (x * (screenTopRight.y - screenTopLeft.y)) + (y * (screenBottomLeft.y - screenTopLeft.y)),
                                    screenTopLeft.z + (x * (screenTopRight.z - screenTopLeft.z)) + (y * (screenBottomLeft.z - screenTopLeft.z)));

        Vector3D direct = new Vector3D(origin, pixel);
        return new Ray(pixel, direct);
    }
}
