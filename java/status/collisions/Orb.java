// Copyright (c) 2008 James A. Wilson All rights reserved. Use is
// subject to license terms.

// This file is part of CruiseSaver.
//
// CruiseSaver is free software; you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation; either version 2 of the License, or
// (at your option) any later version.
//
// CruiseSaver is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with CruiseSaver; if not, write to the Free Software
// Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA

package status.collisions;

import vector.V;
import vector.Vector2D;

/**
 * 
 * @author Nate H. Young, James Wilson
 *
 */
public class Orb {

	public double x;
	public double y;
	public Vector2D v;
	public double mass;
	public int pulsePosition = 0;
	public boolean collided;
	
    public Orb(double x, double y, Vector2D velocity, double mass) {
    	this.x = x;
    	this.y = y;
    	this.v = velocity;
    	this.mass = mass;
	}

	public Vector2D normal(Orb orb) {
    	return normal(this, orb);
	}
	
	public Vector2D tangent(Orb orb) {
		return tangent(this, orb);
	}
	
	public void collide(Orb orb) {
		Vector2D[] result = collide(this, orb);
		this.v = result[0];
		orb.v = result[1];
		
		this.collided = true;
		orb.collided = true;
	}

	public boolean isIntersecting(Orb orb, double radius) {
		return intersecting(this, orb, radius);
	}
	
	public static Vector2D normal(Orb orb1, Orb orb2) {
	    Vector2D norm = new Vector2D();
	    norm.x = orb1.x - orb2.x;
	    norm.y = orb1.y - orb2.y;
	
	    return norm;
	}
	
	public static Vector2D tangent(Orb orb1, Orb orb2) {
	    Vector2D tangent = normal(orb1, orb2);
	    double x = tangent.x;
	    tangent.x = -1 * tangent.y;
	    tangent.y = x;
	
	    return tangent;
	}
	
	public static boolean intersecting(Orb orb1, Orb orb2, double radius) {
		double distance = V.magnitude(Orb.normal(orb1, orb2));
		
		return (distance <= radius);
	}
	
//	//in this version of collide, we just worry about getting the 
//	//directions of the resultant vectors right, not the proper direction of velocity
//	public static Vector2D[] collide(Orb orb1, Orb orb2) {
//		Vector2D normal = V.unitOf(Orb.normal(orb1, orb2));
//	    Vector2D tangent = V.unitOf(Orb.tangent(orb1, orb2));
//	    
//	    Vector2D combined = V.unitOf(V.add(normal, tangent));
//	    
//	    double v1prime = elasticCollision(orb1.mass, V.magnitude(orb1.v), orb2.mass, V.magnitude(orb2.v)); 
//	    double v2prime = elasticCollision(orb2.mass, V.magnitude(orb2.v), orb1.mass, V.magnitude(orb1.v)); 
//	    
//	    Vector2D v1final = V.mult(v1prime, combined);
//	    Vector2D v2final = V.mult(v2prime, V.mult(-1, normal));
//	    
//	    return new Vector2D[] { v1final, v2final };
//	}
	
	public static Vector2D[] collide(Orb orb1, Orb orb2) {
		double v1x = elasticCollision(orb1.mass, orb1.v.x, orb2.mass, orb2.v.x);
		double v2x = elasticCollision(orb2.mass, orb2.v.x, orb1.mass, orb1.v.x);
		
		//get the signs right
		if(v1x > 0 && orb2.v.x < 0 || v1x < 0 && orb2.v.x > 0)
			v1x *= -1.0;
		if(v2x > 0 && orb1.v.x < 0 || v2x < 0 && orb1.v.x > 0)
			v2x *= -1.0;

		double v1y = elasticCollision(orb1.mass, orb1.v.y, orb2.mass, orb2.v.y);
		double v2y = elasticCollision(orb2.mass, orb2.v.y, orb1.mass, orb1.v.y);
		
		//get the signs right
		if(v1y > 0 && orb2.v.y < 0 || v1y < 0 && orb2.v.y > 0)
			v1y *= -1.0;
		if(v2y > 0 && orb1.v.y < 0 || v2y < 0 && orb1.v.y > 0)
			v2y *= -1.0;
		
		return new Vector2D[] { new Vector2D(v1x, v1y), new Vector2D(v2x, v2y) };
	}
	
//	//returns the resultant vectors of the orbs after collision
//	public static Vector2D[] fullElasticCollision(Orb orb1, Orb orb2) {
//		//calculate unit normal and tangent vectors
//	    Vector2D normal = V.unitOf(Orb.normal(orb1, orb2));
//	    Vector2D tangent = V.unitOf(Orb.tangent(orb1, orb2));
//	
//	    //break the orb's velocity vectors into scalar normal and tangential components
//	    double v1n = V.dot(orb1.v, normal);
//	    double v1t = V.dot(orb1.v, tangent);
//	    double v2n = V.dot(orb2.v, normal);
//	    double v2t = V.dot(orb2.v, tangent);
//	
//	    //calculate scalar velocity in normal direction after collision 
//	    v1n = elasticCollision(orb1.mass, v1n, orb2.mass, v2n);
//	    v2n = elasticCollision(orb2.mass, v2n, orb1.mass, v1n);
//	    
//	    //calculate normal component of final velocity vector
//	    Vector2D v1norm = V.mult(v1n, normal);
//	    Vector2D v2norm = V.mult(v2n, normal);
//	
//	    //calculate tangential component of final velocity vector
//	    Vector2D v1tang = V.mult(v1t, tangent);
//	    Vector2D v2tang = V.mult(v2t, tangent);
//	    
//	    //combine normal and tangential components to get final velocity vector
//	    Vector2D v1final = V.add(v1norm, v1tang);
//	    Vector2D v2final = V.add(v2norm, v2tang);
//	    
//	    return new Vector2D[] { v1final, v2final };
//	}
	
	public static double elasticCollision(double mass1, double velo1, double mass2, double velo2) {
	    return ((velo1 * (mass1 - mass2)) + 2 * mass2 * velo2) / (mass1 + mass2);
	}
}
