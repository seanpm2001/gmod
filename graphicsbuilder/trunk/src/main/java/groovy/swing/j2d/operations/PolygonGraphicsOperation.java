/*
 * Copyright 2007 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 */

package groovy.swing.j2d.operations;

import groovy.lang.Closure;
import groovy.swing.j2d.impl.DelegatingGraphicsOperation;
import groovy.swing.j2d.impl.FillSupportGraphicsOperation;
import groovy.swing.j2d.impl.StrokingAndFillingGraphicsOperation;

import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.image.ImageObserver;
import java.util.List;

import org.codehaus.groovy.runtime.InvokerHelper;

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
public class PolygonGraphicsOperation extends DelegatingGraphicsOperation implements
      FillSupportGraphicsOperation {
   public PolygonGraphicsOperation() {
      super( "polygon", new String[] { "points" }, new StrokingAndFillingGraphicsOperation(
            "drawPolygon", new String[] { "xpoints", "ypoints", "npoints" } ) );
   }

   public Shape getClip(Graphics2D g, ImageObserver observer) {
      int[][] points = getPoints();
      if( points == null ){
         return null;
      }
      return new Polygon( points[0], points[1], points[2][0] );
   }

   protected void setupDelegateProperties(Graphics2D g, ImageObserver observer) {
      int[][] points = getPoints();
      if( points == null ){
         return;
      }

      Object delegate = getDelegate();
      InvokerHelper.setProperty( delegate, "xpoints", points[0] );
      InvokerHelper.setProperty( delegate, "ypoints", points[1] );
      InvokerHelper.setProperty( delegate, "npoints", new Integer( points[2][0] ) );
   }

   private int convertToInteger( Object o, int index ) {
      int p = 0;
      if( o == null ){
         throw new IllegalStateException( ((index % 2 == 0) ? "x" : "y") + "[" + index
               + "] is null" );
      }
      if( o instanceof Closure ){
         o = ((Closure) o).call();
      }
      if( o instanceof Number ){
         p = ((Number) o).intValue();
      }else{
         throw new IllegalStateException( ((index % 2 == 0) ? "x" : "y") + "[" + index
               + "] is not a number" );
      }
      return p;
   }

   private int[][] getPoints() {
      List points = (List) getParameterValue( "points" );
      if( points.size() == 0 ){
         return null;
      }

      if( points.size() % 2 == 1 ){
         throw new IllegalStateException( "Odd number of points" );
      }

      int npoints = points.size() / 2;
      int[] xpoints = new int[npoints];
      int[] ypoints = new int[npoints];
      for( int i = 0; i < npoints; i++ ){
         Object ox = points.get( 2 * i );
         Object oy = points.get( (2 * i) + 1 );
         xpoints[i] = convertToInteger( ox, 2 * 1 );
         ypoints[i] = convertToInteger( oy, (2 * i) + 1 );
      }
      return new int[][] { xpoints, ypoints, new int[] { npoints } };
   }
}