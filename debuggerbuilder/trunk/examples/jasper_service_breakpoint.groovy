/*
 * Dumps the stack to System.out each time the service method of Tomcat's
 * Jasper JspServlet is entered.
 *
 * In the callback closure below, "event" is a BreakpointEvent. See
 * http://java.sun.com/javase/6/docs/jdk/api/jpda/jdi/com/sun/jdi/event/BreakpointEvent.html
 */

def bpClass = "org.apache.jasper.servlet.JspServlet"
def bpMethod = "service"

debugger {
    breakpoint(class: bpClass, method: bpMethod) { event ->
        println "Breakpoint triggered:"
        printStackTrace(System.out, event.thread().frames(), "\t")
    }
}
