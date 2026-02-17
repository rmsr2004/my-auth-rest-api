package com.myauth.config;

import java.lang.reflect.Method;

import org.springframework.boot.autoconfigure.web.servlet.WebMvcRegistrations;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import com.myauth.config.versioning.ApiVersion;

@Configuration
public class VersioningConfig implements WebMvcRegistrations {
    @Override
    public RequestMappingHandlerMapping getRequestMappingHandlerMapping() {
        return new ApiVersionRequestMappingHandlerMapping();
    }

    private static class ApiVersionRequestMappingHandlerMapping extends RequestMappingHandlerMapping {
        @Override
        protected RequestMappingInfo getMappingForMethod(Method method, Class<?> handlerType) {
            RequestMappingInfo info = super.getMappingForMethod(method, handlerType);
            if (info == null) {
                return null;
            }

            ApiVersion methodAnnotation = AnnotationUtils.findAnnotation(method, ApiVersion.class);
            if (methodAnnotation != null) {
                return createVersionedInfo(methodAnnotation, info);
            }

            ApiVersion typeAnnotation = AnnotationUtils.findAnnotation(handlerType, ApiVersion.class);
            if (typeAnnotation != null) {
                return createVersionedInfo(typeAnnotation, info);
            }

            return info;
        }

        private RequestMappingInfo createVersionedInfo(ApiVersion annotation, RequestMappingInfo originalInfo) {
            String version = annotation.value();
            String prefix = "api/v" + version;

            return RequestMappingInfo.paths(prefix)
                    .build()
                    .combine(originalInfo);
        }
    }
}