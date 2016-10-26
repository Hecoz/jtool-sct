/* $Id: Ray.java,v 1.1.1.1 2001/01/08 23:10:14 gregoire Exp $
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

package raja;


/**
 * The <code>Ray</code> class defines a high precision ray in the 3
 * dimensional space.  A ray is basically a semiline, given by its origin,
 * a <code>Point3D</code>, and its direction, a <code>Vector3D</code>.
 *
 * @see Point3D
 * @see Vector3D
 *
 * @author Emmanuel Fleury
 * @author Grégoire Sutre
 */
public class Ray
{
    /**
     * The <i>origin</i> of the ray.
     * @serial
     */
    public Point3D origin;

    /**
     * The <i>direction</i> of the ray.  This <code>Vector3D</code> is normed
     * upon construction, and it <i>should</i> stay normed.
     * @serial
     */
    public Vector3D direction;   // normed vector

    /**
     * Creates a <code>Ray</code> object initialized with the
     * specified origin and direction.
     * 
     * @param origin    the origin of the newly constructed
     *                  <code>Ray</code>.
     * @param direction the direction of the newly constructed
     *                  <code>Ray</code>.
     */
    public Ray(Point3D origin, Vector3D direction)
    {
        this.origin = origin;
        this.direction = Vector3D.normalization(direction);
    }

    /**
     * Creates a <code>Ray</code> object initialized with the
     * specified origin and whose direction connects the origin with the
     * specified destination.
     * 
     * @param origin      the origin of the newly constructed
     *                    <code>Ray</code>.
     * @param destination the destination that the direction of the newly
     *                    constructed <code>Ray</code> connects the origin
     *                    with.
     */
    public Ray(Point3D origin, Point3D destination)
    {
        this(origin, new Vector3D(origin, destination));
    }
}
