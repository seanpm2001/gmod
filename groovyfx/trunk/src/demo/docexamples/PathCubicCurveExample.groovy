/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package demo.docexamples

import groovyx.javafx.GroovyFX
import groovyx.javafx.SceneGraphBuilder

GroovyFX.start({
    def sg = new SceneGraphBuilder();

    sg.stage(
        title: "Path Example (Groovy)",
        width: 400, height:250,
        visible: true,
    ){ 
        scene (fill: white) {
            path( translateX: 50, translateY: 50, stroke: red) {
                moveTo(x: 0, y: 0)
                cubicCurveTo(controlX1: -10, controlY1: 20,
                    controlX2: 80, controlY2: 100,
                x: 100, y: 50)
            }
        }
    }
});
