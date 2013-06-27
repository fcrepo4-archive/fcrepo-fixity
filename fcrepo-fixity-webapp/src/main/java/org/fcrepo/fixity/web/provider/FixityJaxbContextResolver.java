/**
 * Copyright 2013 DuraSpace, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Provider
@Component
@Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
public class FixityJaxbContextResolver implements ContextResolver<JAXBContext> {

    private final JAXBContext context;

    private static final Logger LOG = LoggerFactory
            .getLogger(FixityJaxbContextResolver.class);

    /**
     * TODO
     */
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
            LOG.error(e.getMessage(), e);
            throw new IllegalStateException(
                    "Not able to instantiate Jaxb context", e);
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
