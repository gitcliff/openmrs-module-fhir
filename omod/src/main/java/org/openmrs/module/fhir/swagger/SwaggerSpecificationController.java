/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 * <p/>
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 * <p/>
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.fhir.swagger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.fhir.api.util.FHIRConstants;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SwaggerSpecificationController extends HttpServlet {
    protected Log log = LogFactory.getLog(getClass());

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {

        String swaggerSpecificationJSON;
        try {
            StringBuilder baseUrl = new StringBuilder();
            String scheme = request.getScheme();
            int port = request.getServerPort();

            baseUrl.append(scheme); // http, https
            baseUrl.append(SwaggerDocConstants.SLASHES);
            baseUrl.append(request.getServerName());
            if ((SwaggerDocConstants.HTTP.equals(scheme) && port != 80) || (SwaggerDocConstants.HTTPS.equals(scheme) && port != 443)) {
                baseUrl.append(SwaggerDocConstants.COLON);
                baseUrl.append(request.getServerPort());
            }

            baseUrl.append(request.getContextPath());
            String resourcesUrl = Context.getAdministrationService().getGlobalProperty(FHIRConstants.URI_PREFIX_GLOBAL_PROPERTY_NAME, baseUrl.toString());
            String urlWithoutScheme = "";
            String basePath = SwaggerDocConstants.SHORT_FHIR_REST_PREFIX;
            if (SwaggerDocConstants.HTTP.equals(scheme)) {
                urlWithoutScheme = resourcesUrl.replace(SwaggerDocConstants.HTTP_WITH_SLASHES, SwaggerDocConstants.STR_EMPTY);
            } else if (SwaggerDocConstants.HTTPS.equals(scheme)) {
                urlWithoutScheme = resourcesUrl.replace(SwaggerDocConstants.HTTPS_WITH_SLASHES, SwaggerDocConstants.STR_EMPTY);
            }
            urlWithoutScheme = urlWithoutScheme.replace(SwaggerDocConstants.OPENMRS_PREFIX, SwaggerDocConstants.STR_EMPTY);
            SwaggerSpecificationCreator creator = new SwaggerSpecificationCreator(urlWithoutScheme, basePath);
            swaggerSpecificationJSON = creator.buildJSON();
            response.setContentType(SwaggerDocConstants.PRODUCES_JSON);
            response.setCharacterEncoding(SwaggerDocConstants.UTF_8);
            response.getWriter().write(swaggerSpecificationJSON);
        } catch (Exception e) {
            log.error("Error while processing request", e);
        }
    }

}

