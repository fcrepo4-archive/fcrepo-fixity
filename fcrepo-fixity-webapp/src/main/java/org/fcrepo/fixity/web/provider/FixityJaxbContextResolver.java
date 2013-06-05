/**
 *
 */

package org.fcrepo.fixity.web.provider;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.fcrepo.fixity.model.DailyStatistics;
import org.fcrepo.fixity.model.DatastreamFixityError;
import org.fcrepo.fixity.model.DatastreamFixityRepaired;
import org.fcrepo.fixity.model.DatastreamFixityResult;
import org.fcrepo.fixity.model.DatastreamFixitySuccess;
import org.fcrepo.fixity.model.ObjectFixityResult;
import org.fcrepo.fixity.model.Statistics;
import org.springframework.stereotype.Component;

/**
 * @author frank asseg
 *
 */
@Provider
@Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
@Component
public class FixityJaxbContextResolver implements ContextResolver<JAXBContext> {

    private final JAXBContext context;

    public FixityJaxbContextResolver() {
        try {
            context =
                    JAXBContext.newInstance(DailyStatistics.class,
                            Statistics.class, DatastreamFixityResult.class,
                            DatastreamFixityError.class,
                            DatastreamFixityRepaired.class,
                            DatastreamFixitySuccess.class,
                            ObjectFixityResult.class);
        } catch (JAXBException e) {
            throw new IllegalStateException("Not able to instantiate Jaxb context",e);
        }
    }

    /*
     * (non-Javadoc)
     * @see javax.ws.rs.ext.ContextResolver#getContext(java.lang.Class)
     */
    @Override
    public JAXBContext getContext(Class<?> type) {
        return context;
    }
}
