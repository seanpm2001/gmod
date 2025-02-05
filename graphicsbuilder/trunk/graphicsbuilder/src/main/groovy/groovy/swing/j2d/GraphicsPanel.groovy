/*
 * Copyright 2007-2008 the original author or authors.
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

package groovy.swing.j2d

import java.awt.Graphics
import java.awt.LayoutManager
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import java.awt.event.MouseEvent
import java.awt.event.MouseListener
import java.awt.event.MouseMotionListener
import java.awt.event.MouseWheelEvent
import java.awt.event.MouseWheelListener
import java.beans.PropertyChangeEvent
import java.beans.PropertyChangeListener
import javax.swing.JPanel
import groovy.swing.j2d.event.*
import groovy.swing.j2d.operations.ShapeProvider

/**
 * A Panel that can use a GraphicsOperation to draw itself.
 *
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
class GraphicsPanel extends JPanel implements PropertyChangeListener, MouseListener,
   MouseMotionListener, MouseWheelListener, KeyListener {
     private GraphicsOperation graphicsOperation
     private GraphicsContext context = new GraphicsContext()
     private boolean displayed
     private List errorListeners = []
     private List lastTargets = []

     GraphicsPanel(){
         super( null )
         addMouseListener( this )
         addMouseMotionListener( this )
         addMouseWheelListener( this )
         addKeyListener( this )
     }

     public void setLayout( LayoutManager mgr ){
         // do not allow the layout to be changed
     }

     /**
      * Returns the current GraphicsOperation of this Panel
      * @return the current GraphicsOperation of this Panel
      */
     public GraphicsOperation getGraphicsOperation(){
         return graphicsOperation
     }

     /**
      * Sets the GraphicsOperation for this Panel.<br>
      * If the panel is visible, a <code>repaint()</code> will be ensued
      */
     public void setGraphicsOperation( GraphicsOperation graphicsOperation ){
         if( graphicsOperation ){
             if( this.graphicsOperation ){
                this.graphicsOperation.removePropertyChangeListener( this )
             }
             this.graphicsOperation = graphicsOperation
             this.graphicsOperation.addPropertyChangeListener( this )
             if( visible ){
                 repaint()
             }
         }
     }

     /**
      * Returns the current GraphicsOperation of this Panel.<br>
      * Alias for getGraphicsOperation()
      * @return the current GraphicsOperation of this Panel
      */
     public GraphicsOperation getGo(){
         return graphicsOperation
     }

     /**
      * Sets the GraphicsOperation for this Panel.<br>
      * Alias for setGraphicsOperation()
      * If the panel is visible, a <code>repaint()</code> will be ensued
      */
     public void setGo( GraphicsOperation graphicsOperation ){
         setGraphicsOperation( graphicsOperation )
     }

     public void paintComponent( Graphics g ){
         context.g = g
         context.component = this
         if( graphicsOperation ){
             g.clearRect( 0, 0, size.width as int, size.height as int )
             try{
                 context.eventTargets = []
                 context.groupContext = [:]
                 graphicsOperation.execute( context )
             }catch( Exception e ){
                 fireGraphicsErrorEvent( e )
             }
         }
     }

     public void addGraphicsErrorListener( GraphicsErrorListener listener ){
         if( !listener ) return;
         if( errorListeners.contains(listener) ) return;
         errorListeners.add( listener )
     }

     public void removeGraphicsErrorListener( GraphicsErrorListener listener ){
         if( !listener ) return;
         errorListeners.remove( listener )
     }

     public List getGraphicsErrorListeners(){
         return Collections.unmodifiableList( errorListeners )
     }

     protected void fireGraphicsErrorEvent( Throwable t ) {
         t.printStackTrace()
         def event = new GraphicsErrorEvent( this, t )
         errorListeners.each { listener ->
            listener.errorOccurred( event )
         }
     }

     public void propertyChange( PropertyChangeEvent event ){
         if( visible /*&& event.source instanceof GraphicsOperation*/ ){
             repaint()
         }
     }

     /* ===== MouseListener ===== */

     public void mouseEntered( MouseEvent e ){
         lastTargets.clear()
     }

     public void mouseExited( MouseEvent e ){
         lastTargets.clear()
     }

     public void mousePressed( MouseEvent e ){
         fireMouseEvent( e, "mousePressed" )
     }

     public void mouseReleased( MouseEvent e ){
         fireMouseEvent( e, "mouseReleased" )
     }

     public void mouseClicked( MouseEvent e ){
         fireMouseEvent( e, "mouseClicked" )
     }

     /* ===== MouseMotionListener ===== */

     public void mouseMoved( MouseEvent e ){
         if( !context.eventTargets ) return
         def targets = getTargets(e)
         if( targets ){
            def oldTargets = []
            def visitedTargets = []
            lastTargets.each { target ->
               if( !targets.contains(target) ){ 
                  oldTargets << target
               }else{
                  visitedTargets << target
               }
            }
            def newTargets = targets - visitedTargets
            oldTargets.each { t -> t.mouseExited( new GraphicsInputEvent( this, e, t ) ) }
            newTargets.each { t -> t.mouseEntered( new GraphicsInputEvent( this, e, t ) ) }
            targets.each { t -> t.mouseMoved( new GraphicsInputEvent( this, e, t ) ) }
            lastTargets = targets
                /*
                def inputEvent = new GraphicsInputEvent( this, e, target )
                if( target != lastTarget ){
                   if( lastTarget ) lastTarget.mouseExited( new GraphicsInputEvent( this, e, target ) )
                   lastTarget = target
                   target.mouseEntered( inputEvent )
                }
                target.mouseMoved( inputEvent )
                */
         }else if( lastTargets ){
            lastTargets.each { it.mouseExited( new GraphicsInputEvent( this, e, it ) ) }
            lastTargets.clear()
         }
     }

     public void mouseDragged( MouseEvent e ){
         fireMouseEvent( e, "mouseDragged" )
     }

     /* ===== MouseWheelListener ===== */

     public void mouseWheelMoved( MouseWheelEvent e ){
         fireMouseEvent( e, "mouseWheelMoved" )
     }

     /* ===== KeyListener ===== */

     public void keyPressed( KeyEvent e ){

     }

     public void keyReleased( KeyEvent e ){

     }

     public void keyTyped( KeyEvent e ){

     }

     /* ===== PRIVATE ===== */

     private void fireMouseEvent( MouseEvent e, String mouseEventMethod ){
         if( !context.eventTargets ) return
         getTargets(e).each { target ->
            def inputEvent = new GraphicsInputEvent( this, e, target )
            target."$mouseEventMethod"( inputEvent )
         }
     }

     private def getTargets( MouseEvent e ){
         def targets = []
         def eventTargets = context.eventTargets
         for( target in eventTargets.reverse() ){
             //def bp = target.getBoundingShape(context)
             def bs = target.runtime.boundingShape 
             if( bs && bs.contains(e.point) ){
                 targets << target
                 if( !target.passThrough ) break
             }
         }
         return targets
     }
}
