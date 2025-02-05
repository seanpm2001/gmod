/**
 * 
 */
package org.lpny.groovyrestlet.builder.factory;

import groovy.lang.Closure;
import groovy.util.FactoryBuilderSupport;

import java.util.Map;

import org.restlet.Component;
import org.restlet.Guard;
import org.restlet.Restlet;
import org.restlet.Router;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Factory to create an instance of {@link Restlet} or any of its derived types.
 * 
 * <p>
 * If user does not specify {@link AbstractFactory#OF_BEAN} or
 * {@link AbstractFactory#OF_CLASS} attributes, this factory will create an
 * instance of {@link Restlet}. If {@link AbstractFactory#OF_CLASS} was
 * specified but not Spring Context was defined, this factory will create a new
 * {@link Restlet} instance using {@code Class.newInstance()}. For example:
 * 
 * <pre>
 * <code>
 *   builder.restlet(ofClass:&quot;org.restlet.Guard&quot;)
 *   //instance of class is also accepted
 *   builder.restlet(ofClass:Guard.class)
 * </code> 
 * </pre>
 * 
 * will create an instance of {@link Guard}.
 * </p>
 * <p>
 * Otherwise this factory will consult spring context to create a new instance.<br/>
 * 
 * If both <code>ofClass</code> and <code>ofBean</code> are specified,
 * <code>ofBean</code> will be treated first.
 * </p>
 * 
 * @author keke
 * @reversion $Revision$
 * @since 0.1.0
 */
public class RestletFactory extends AbstractFactory {

    private static final Logger LOG = LoggerFactory
            .getLogger(RestletFactory.class);

    protected static final String HANDLE = "handle";

    public RestletFactory() {
        this("restlet");

    }

    protected RestletFactory(final String name) {
        super(name);
        addFilter(HANDLE);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.lpny.gr.builder.factory.AbstractFactory#newInstanceInner(groovy.util.FactoryBuilderSupport,
     *      java.lang.Object, java.lang.Object, java.util.Map)
     */
    @SuppressWarnings("unchecked")
    @Override
    protected Object newInstanceInner(final FactoryBuilderSupport builder,
            final Object name, final Object value, final Map attributes)
            throws InstantiationException, IllegalAccessException {
        final Closure handler = (Closure) builder.getContext().get(HANDLE);
        if (handler == null) {
            return new Restlet(FactoryUtils.getParentRestletContext(builder));
        } else {
            return new Restlet(FactoryUtils.getParentRestletContext(builder)) {

                /*
                 * (non-Javadoc)
                 * 
                 * @see org.restlet.Restlet#handle(org.restlet.data.Request,
                 *      org.restlet.data.Response)
                 */
                @Override
                public void handle(final Request request,
                        final Response response) {
                    handler.call(FactoryUtils.packArgs(this, handler, request,
                            response));
                }
            };
        }
    }

    @Override
    protected Object setParentInner(final FactoryBuilderSupport builder,
            final Object parent, final Object child) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("SetParent: parent={} child={}", new Object[] { parent,
                    child });
        }

        if (parent == null) {
            return null;
        }
        final String uri = (String) builder.getContext().get(URI);
        if (uri == null) {
            return null;
        }
        if (parent instanceof Component) {
            return ((Component) parent).getDefaultHost().attach(uri,
                    (Restlet) child);
        } else if (parent instanceof Router) {
            return ((Router) parent).attach(uri, (Restlet) child);
        }
        return null;
    }

}
