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

import java.awt.Rectangle
import java.awt.RenderingHints
import java.awt.image.BufferedImage
import javax.imageio.ImageIO

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
final class GraphicsRenderer {
    private GraphicsBuilder gb
    RenderingHints renderingHints = new RenderingHints(null)

    public GraphicsRenderer(){
       gb = new GraphicsBuilder()
    }

    public GraphicsBuilder getGraphicsBuilder(){
       return gb
    }

    /**
     * Renders an image.<br>
     * Assumes that the closure contains nodes that GraphicsBuilder can understand.
     * Will create a compatible BufferedImage with dimensions [width,height] and with
     * a clip set to the rectangle [0,0,width,height]
     *
     * @param width the width of the image/clip
     * @param height the height of the image/clip
     * @param closure a closure containg GraphicsBuilder's nodes
     */
    public BufferedImage render( int width, int height, Closure closure ){
       return render( width, height, gb.group(closure) )
    }

    /**
     * Renders an image.<br>
     * Will create a compatible BufferedImage with dimensions [width,height] and with
     * a clip set to the rectangle [0,0,width,height]
     *
     * @param width the width of the image/clip
     * @param height the height of the image/clip
     * @param go any GraphicsOperation
     */
    public BufferedImage render( int width, int height, GraphicsOperation go ){
       return render( GraphicsBuilderHelper.createCompatibleImage( width, height ), go )
    }

    /**
     * Renders an image.<br>
     * Assumes that the closure contains nodes that GraphicsBuilder can understand.
     * Will create a compatible BufferedImage with dimensions [clip.width,clip.height] and with
     * a clip set to the clip parameter
     *
     * @param clip the dimensions of the image/clip
     * @param closure a closure containg GraphicsBuilder's nodes
     */
    public BufferedImage render( Rectangle clip, Closure closure ){
       return render( clip, gb.group(closure) )
    }

    /**
     * Renders an image.<br>
     * Will create a compatible BufferedImage with dimensions [clip.width,clip.height] and with
     * a clip set to the clip parameter
     *
     * @param clip the dimensions of the image/clip
     * @param go any GraphicsOperation
     */
    public BufferedImage render( Rectangle clip, GraphicsOperation go ){
       return render( GraphicsBuilderHelper.createCompatibleImage( clip.width as int, clip.height as int ), clip, go )
    }

    /**
     * Renders an image.<br>
     * Assumes that the closure contains nodes that GraphicsBuilder can understand.
     *
     * @param image the destination image
     * @param closure a closure containg GraphicsBuilder's nodes
     */
    public BufferedImage render( BufferedImage dst, Closure closure ){
       return render( dst, gb.group(closure) )
    }

    /**
     * Renders an image.<br>
     *
     * @param image the destination image
     * @param go any GraphicsOperation
     */
    public BufferedImage render( BufferedImage dst, GraphicsOperation go ){
       return render( dst, [0,0,dst.width,dst.height] as Rectangle, go )
    }

    /**
     * Renders an image.<br>
     * Assumes that the closure contains nodes that GraphicsBuilder can understand.
     * Will set a clip as defined by the clip parameter
     *
     * @param image the destination image
     * @param clip the dimensions of the clip
     * @param closure a closure containg GraphicsBuilder's nodes
     */
    public BufferedImage render( BufferedImage dst, Rectangle clip, Closure closure ){
       return render( dst, clip, gb.group(closure) )
    }

    /**
     * Renders an image.<br>
     * Will set a clip as defined by the clip parameter
     *
     * @param image the destination image
     * @param clip the dimensions of the clip
     * @param go any GraphicsOperation
     */
    public BufferedImage render( BufferedImage dst, Rectangle clip, GraphicsOperation go ){
       def context = new GraphicsContext()
       def g = dst.createGraphics()
       g.renderingHints = renderingHints
       def cb = g.clipBounds
       g.setClip( clip )
       context.g = g
       go.execute( context )
       g.dispose()
       if( !cb ) g.setClip( cb )
       return dst
    }

    /**
     * Writes an image to a file.<br>
     * Assumes that the filename follows the unix conventions ("/" as file separator)
     * and the it ends with a file extension recognizable by the plugins registered
     * with javax.imageio.ImageIO.
     * Assumes that the closure contains nodes that GraphicsBuilder can understand.
     * Will create a compatible BufferedImage with dimensions [width,height] and with
     * a clip set to the rectangle [0,0,width,height]
     *
     * @param filename the name of the file where the image will be written
     * @param width the width of the image/clip
     * @param height the height of the image/clip
     * @param closure a closure containg GraphicsBuilder's nodes
     *
     * @throws IOException if the file can't be created and writen to.
     *
     * @return a File reference to the written image
     */
    public File renderToFile( String filename, int width, int height, Closure closure ){
       return renderToFile( filename, width, height, gb.group(closure) )
    }

    /**
     * Writes an image to a file.<br>
     * Assumes that the filename follows the unix conventions ("/" as file separator)
     * and the it ends with a file extension recognizable by the plugins registered
     * with javax.imageio.ImageIO.
     * Assumes that the closure contains nodes that GraphicsBuilder can understand.
     * Will create a compatible BufferedImage with dimensions [width,height] and with
     * a clip set to the rectangle [0,0,width,height]
     *
     * @param filename the name of the file where the image will be written
     * @param width the width of the image/clip
     * @param height the height of the image/clip
     * @param go any GraphicsOperation
     *
     * @throws IOException if the file can't be created and writen to.
     *
     * @return a File reference to the written image
     */
    public File renderToFile( String filename, int width, int height, GraphicsOperation go ){
       return renderToFile( filename, GraphicsBuilderHelper.createCompatibleImage( width, height, filename ==~ /(?i).*[png|gif]/ ), go )
    }

    /**
     * Writes an image to a file.<br>
     * Assumes that the filename follows the unix conventions ("/" as file separator)
     * and the it ends with a file extension recognizable by the plugins registered
     * with javax.imageio.ImageIO.
     * Assumes that the closure contains nodes that GraphicsBuilder can understand.
     * Will create a compatible BufferedImage with dimensions [clip.width,clip.height] and with
     * a clip set to the clip parameter
     *
     * @param filename the name of the file where the image will be written
     * @param clip the dimensions of the image/clip
     * @param closure a closure containg GraphicsBuilder's nodes
     *
     * @throws IOException if the file can't be created and writen to.
     *
     * @return a File reference to the written image
     */
    public File renderToFile( String filename, Rectangle clip, Closure closure ){
       return renderToFile( filename, clip, gb.group(closure) )
    }

    /**
     * Writes an image to a file.<br>
     * Assumes that the filename follows the unix conventions ("/" as file separator)
     * and the it ends with a file extension recognizable by the plugins registered
     * with javax.imageio.ImageIO.
     * Will create a compatible BufferedImage with dimensions [clip.width,clip.height] and with
     * a clip set to the clip parameter
     *
     * @param filename the name of the file where the image will be written
     * @param clip the dimensions of the image/clip
     * @param go any GraphicsOperation
     *
     * @throws IOException if the file can't be created and writen to.
     *
     * @return a File reference to the written image
     */
    public File renderToFile( String filename, Rectangle clip, GraphicsOperation go ){
       return renderToFile( filename, GraphicsBuilderHelper.createCompatibleImage( 
             clip.width as int, clip.height as int, filename ==~ /(?i).*[png|gif]/ ), clip, go )
    }

    /**
     * Writes an image to a file.<br>
     * Assumes that the filename follows the unix conventions ("/" as file separator)
     * and the it ends with a file extension recognizable by the plugins registered
     * with javax.imageio.ImageIO.
     * Assumes that the closure contains nodes that GraphicsBuilder can understand.
     *
     * @param filename the name of the file where the image will be written
     * @param image the destination image
     * @param closure a closure containg GraphicsBuilder's nodes
     *
     * @throws IOException if the file can't be created and writen to.
     *
     * @return a File reference to the written image
     */
    public File renderToFile( String filename, BufferedImage dst, Closure closure ){
       return renderToFile( filename, dst, [0,0,dst.width,dst.height] as Rectangle, gb.group(closure) )
    }

    /**
     * Writes an image to a file.<br>
     * Assumes that the filename follows the unix conventions ("/" as file separator)
     * and the it ends with a file extension recognizable by the plugins registered
     * with javax.imageio.ImageIO.
     *
     * @param filename the name of the file where the image will be written
     * @param image the destination image
     * @param go any GraphicsOperation
     *
     * @throws IOException if the file can't be created and writen to.
     *
     * @return a File reference to the written image
     */
    public File renderToFile( String filename, BufferedImage dst, GraphicsOperation go ){
       return renderToFile( filename, dst, [0,0,dst.width,dst.height] as Rectangle, go )
    }

    /**
     * Writes an image to a file.<br>
     * Assumes that the filename follows the unix conventions ("/" as file separator)
     * and the it ends with a file extension recognizable by the plugins registered
     * with javax.imageio.ImageIO.
     * Assumes that the closure contains nodes that GraphicsBuilder can understand.
     * Will set a clip as defined by the clip parameter
     *
     * @param filename the name of the file where the image will be written
     * @param image the destination image
     * @param clip the dimensions of the clip
     * @param closure a closure containg GraphicsBuilder's nodes
     *
     * @throws IOException if the file can't be created and writen to.
     *
     * @return a File reference to the written image
     */
    public File renderToFile( String filename, BufferedImage dst, Rectangle clip, Closure closure ){
       return renderToFile( filename, dst, clip, gb.group(closure) )
    }

    /**
     * Writes an image to a file.<br>
     * Assumes that the filename follows the unix conventions ("/" as file separator)
     * and the it ends with a file extension recognizable by the plugins registered
     * with javax.imageio.ImageIO.
     *
     * @param filename the name of the file where the image will be written
     * @param image the destination image
     * @param clip the dimensions of the clip
     * @param go any GraphicsOperation
     *
     * @throws IOException if the file can't be created and writen to.
     *
     * @return a File reference to the written image
     */
    public File renderToFile( String filename, BufferedImage dst, Rectangle clip, GraphicsOperation go ){
       def fileSeparator = "/" /*System.getProperty("file.separator")*/
       def file = null
       def extension = "jpg"

       if( filename.lastIndexOf(fileSeparator) != -1 ){
          def dirs = filename[0..(filename.lastIndexOf(fileSeparator)-1)]
          def fname = filename[(filename.lastIndexOf(fileSeparator)+1)..-1]
          extension = fname[(fname.lastIndexOf(".")+1)..-1]
          File parent = new File(dirs)
          parent.mkdirs()
          file = new File(parent,fname)
       }else{
          file = new File(filename)
          extension = filename[(filename.lastIndexOf(".")+1)..-1]
       }

       if( !dst ) dst = GraphicsBuilderHelper.createCompatibleImage( width, height, filename ==~ /(?i).*[png|gif]/ )
       ImageIO.write( render( dst, clip, go ), extension, file )
       return file
    }
}
