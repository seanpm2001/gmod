package org.codehaus.groovy.junctions.swingx;

import java.awt.Color;
import java.awt.Font;
import java.awt.SystemColor;
import java.awt.Toolkit;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.metal.MetalLookAndFeel;

import org.jdesktop.swingx.plaf.AbstractComponentAddon;
import org.jdesktop.swingx.plaf.LookAndFeelAddons;
import org.jdesktop.swingx.plaf.UIManagerExt;
import org.jdesktop.swingx.plaf.windows.WindowsClassicLookAndFeelAddons;
import org.jdesktop.swingx.plaf.windows.WindowsLookAndFeelAddons;
import org.jdesktop.swingx.util.JVM;
import org.jdesktop.swingx.util.OS;

public class PostPaneAddon extends AbstractComponentAddon {

   public PostPaneAddon() {
      super( "PostPane" );
   }

   @Override
   protected void addBasicDefaults( LookAndFeelAddons addon, List<Object> defaults ) {
      Font taskPaneFont = UIManagerExt.getSafeFont( "Label.font", new Font( "Dialog", Font.PLAIN,
            12 ) );
      taskPaneFont = taskPaneFont.deriveFont( Font.BOLD );

      Color menuBackground = new ColorUIResource( SystemColor.menu );
      defaults.addAll( Arrays.asList( new Object[] {
            PostPane.uiClassID,
            "org.codehaus.groovy.junctions.swingx.PostPaneUI",
            "TaskPane.font",
            new FontUIResource( taskPaneFont ),
            "TaskPane.background",
            UIManagerExt.getSafeColor( "List.background", new ColorUIResource(
                  Color.decode( "#005C5C" ) ) ),
            "TaskPane.specialTitleBackground",
            new ColorUIResource( menuBackground.darker() ),
            "TaskPane.titleBackgroundGradientStart",
            menuBackground,
            "TaskPane.titleBackgroundGradientEnd",
            menuBackground,
            "TaskPane.titleForeground",
            new ColorUIResource( SystemColor.menuText ),
            "TaskPane.specialTitleForeground",
            new ColorUIResource( SystemColor.menuText ).brighter(),
            "TaskPane.animate",
            Boolean.TRUE,
            "TaskPane.focusInputMap",
            new UIDefaults.LazyInputMap( new Object[] { "ENTER", "toggleExpanded", "SPACE",
                  "toggleExpanded" } ), } ) );
   }

   @Override
   protected void addLinuxDefaults( LookAndFeelAddons addon, List<Object> defaults ) {
      addMetalDefaults( addon, defaults );
   }

   @Override
   protected void addMetalDefaults( LookAndFeelAddons addon, List<Object> defaults ) {
      super.addMetalDefaults( addon, defaults );
      // if using Ocean, use the Glossy l&f
      String taskPaneGroupUI = "org.codehaus.groovy.junctions.swingx.PostPaneUI";
      if( JVM.current()
            .isOrLater( JVM.JDK1_5 ) ){
         try{
            Method method = MetalLookAndFeel.class.getMethod( "getCurrentTheme" );
            Object currentTheme = method.invoke( null );
            if( Class.forName( "javax.swing.plaf.metal.OceanTheme" )
                  .isInstance( currentTheme ) ){
               taskPaneGroupUI = "org.codehaus.groovy.junctions.swingx.PostPaneUI";
            }
         }catch( Exception e ){
         }
      }
      defaults.addAll( Arrays.asList( new Object[] { PostPane.uiClassID, taskPaneGroupUI,
            "TaskPane.foreground", UIManager.getColor( "activeCaptionText" ),
            "TaskPane.background", MetalLookAndFeel.getControl(),
            "TaskPane.specialTitleBackground", MetalLookAndFeel.getPrimaryControl(),
            "TaskPane.titleBackgroundGradientStart", MetalLookAndFeel.getPrimaryControl(),
            "TaskPane.titleBackgroundGradientEnd", MetalLookAndFeel.getPrimaryControlHighlight(),
            "TaskPane.titleForeground", MetalLookAndFeel.getControlTextColor(),
            "TaskPane.specialTitleForeground", MetalLookAndFeel.getControlTextColor(),
            "TaskPane.borderColor", MetalLookAndFeel.getPrimaryControl(), "TaskPane.titleOver",
            MetalLookAndFeel.getControl()
                  .darker(), "TaskPane.specialTitleOver",
            MetalLookAndFeel.getPrimaryControlHighlight() } ) );
   }

   @Override
   protected void addWindowsDefaults( LookAndFeelAddons addon, List<Object> defaults ) {
      super.addWindowsDefaults( addon, defaults );

      if( addon instanceof WindowsLookAndFeelAddons ){
         defaults.addAll( Arrays.asList( new Object[] { PostPane.uiClassID,
               "org.codehaus.groovy.junctions.swingx.PostPaneUI" } ) );

         String xpStyle = OS.getWindowsVisualStyle();
         if( WindowsLookAndFeelAddons.HOMESTEAD_VISUAL_STYLE.equalsIgnoreCase( xpStyle ) ){
            defaults.addAll( Arrays.asList( new Object[] { "TaskPane.foreground",
                  new ColorUIResource( 86, 102, 45 ), "TaskPane.background",
                  new ColorUIResource( 246, 246, 236 ), "TaskPane.specialTitleBackground",
                  new ColorUIResource( 224, 231, 184 ), "TaskPane.titleBackgroundGradientStart",
                  new ColorUIResource( 255, 255, 255 ), "TaskPane.titleBackgroundGradientEnd",
                  new ColorUIResource( 224, 231, 184 ), "TaskPane.titleForeground",
                  new ColorUIResource( 86, 102, 45 ), "TaskPane.titleOver",
                  new ColorUIResource( 114, 146, 29 ), "TaskPane.specialTitleForeground",
                  new ColorUIResource( 86, 102, 45 ), "TaskPane.specialTitleOver",
                  new ColorUIResource( 114, 146, 29 ), "TaskPane.borderColor",
                  new ColorUIResource( 255, 255, 255 ), } ) );
         }else if( WindowsLookAndFeelAddons.SILVER_VISUAL_STYLE.equalsIgnoreCase( xpStyle ) ){
            defaults.addAll( Arrays.asList( new Object[] { "TaskPane.foreground",
                  new ColorUIResource( Color.black ), "TaskPane.background",
                  new ColorUIResource( 240, 241, 245 ), "TaskPane.specialTitleBackground",
                  new ColorUIResource( 222, 222, 222 ), "TaskPane.titleBackgroundGradientStart",
                  new ColorUIResource( Color.white ), "TaskPane.titleBackgroundGradientEnd",
                  new ColorUIResource( 214, 215, 224 ), "TaskPane.titleForeground",
                  new ColorUIResource( Color.black ), "TaskPane.titleOver",
                  new ColorUIResource( 126, 124, 124 ), "TaskPane.specialTitleForeground",
                  new ColorUIResource( Color.black ), "TaskPane.specialTitleOver",
                  new ColorUIResource( 126, 124, 124 ), "TaskPane.borderColor",
                  new ColorUIResource( Color.white ), } ) );
         }else if( OS.isWindowsVista() ){
            final Toolkit toolkit = Toolkit.getDefaultToolkit();
            defaults.addAll( Arrays.asList( new Object[] {
                  "TaskPane.foreground",
                  new ColorUIResource( Color.white ),
                  "TaskPane.background",
                  new ColorUIResource(
                        (Color) toolkit.getDesktopProperty( "win.3d.backgroundColor" ) ),
                  "TaskPane.specialTitleBackground",
                  new ColorUIResource( 33, 89, 201 ),
                  "TaskPane.titleBackgroundGradientStart",
                  new ColorUIResource( Color.white ),
                  "TaskPane.titleBackgroundGradientEnd",
                  new ColorUIResource(
                        (Color) toolkit.getDesktopProperty( "win.frame.inactiveCaptionColor" ) ),
                  "TaskPane.titleForeground",
                  new ColorUIResource(
                        (Color) toolkit.getDesktopProperty( "win.frame.inactiveCaptionTextColor" ) ),
                  "TaskPane.specialTitleForeground", new ColorUIResource( Color.white ),
                  "TaskPane.borderColor", new ColorUIResource( Color.white ), } ) );
         }else{
            defaults.addAll( Arrays.asList( new Object[] { "TaskPane.foreground",
                  new ColorUIResource( Color.white ), "TaskPane.background",
                  new ColorUIResource( 214, 223, 247 ), "TaskPane.specialTitleBackground",
                  new ColorUIResource( 33, 89, 201 ), "TaskPane.titleBackgroundGradientStart",
                  new ColorUIResource( Color.white ), "TaskPane.titleBackgroundGradientEnd",
                  new ColorUIResource( 199, 212, 247 ), "TaskPane.titleForeground",
                  new ColorUIResource( 33, 89, 201 ), "TaskPane.specialTitleForeground",
                  new ColorUIResource( Color.white ), "TaskPane.borderColor",
                  new ColorUIResource( Color.white ), } ) );
         }
      }

      if( addon instanceof WindowsClassicLookAndFeelAddons ){
         defaults.addAll( Arrays.asList( new Object[] { PostPane.uiClassID,
               "org.codehaus.groovy.junctions.swingx.PostPaneUI", "TaskPane.foreground",
               new ColorUIResource( Color.black ), "TaskPane.background",
               new ColorUIResource( Color.white ), "TaskPane.specialTitleBackground",
               new ColorUIResource( 10, 36, 106 ), "TaskPane.titleBackgroundGradientStart",
               new ColorUIResource( 212, 208, 200 ), "TaskPane.titleBackgroundGradientEnd",
               new ColorUIResource( 212, 208, 200 ), "TaskPane.titleForeground",
               new ColorUIResource( Color.black ), "TaskPane.specialTitleForeground",
               new ColorUIResource( Color.white ), "TaskPane.borderColor",
               new ColorUIResource( 212, 208, 200 ), } ) );
      }
   }

   @Override
   protected void addMacDefaults( LookAndFeelAddons addon, List<Object> defaults ) {
      super.addMacDefaults( addon, defaults );
      defaults.addAll( Arrays.asList( new Object[] { PostPane.uiClassID,
            "org.codehaus.groovy.junctions.swingx.PostPaneUI", "TaskPane.background",
            new ColorUIResource( 245, 245, 245 ), "TaskPane.titleForeground",
            new ColorUIResource( Color.black ), "TaskPane.specialTitleBackground",
            new ColorUIResource( 188, 188, 188 ), "TaskPane.specialTitleForeground",
            new ColorUIResource( Color.black ), "TaskPane.titleBackgroundGradientStart",
            new ColorUIResource( 250, 250, 250 ), "TaskPane.titleBackgroundGradientEnd",
            new ColorUIResource( 188, 188, 188 ), "TaskPane.borderColor",
            new ColorUIResource( 97, 97, 97 ), "TaskPane.titleOver",
            new ColorUIResource( 125, 125, 97 ), "TaskPane.specialTitleOver",
            new ColorUIResource( 125, 125, 97 ), } ) );
   }
}