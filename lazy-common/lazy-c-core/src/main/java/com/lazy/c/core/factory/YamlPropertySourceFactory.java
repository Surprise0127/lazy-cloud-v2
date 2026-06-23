package com.lazy.c.core.factory;

import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.support.DefaultPropertySourceFactory;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.Objects;

/**
 * yaml属性源Factory
 * <p>
 * 将 yaml 或者 yml 文件解析为 PropertySource
 *
 * @author Surprise0127
 * @since 1.0.0
 */
public class YamlPropertySourceFactory extends DefaultPropertySourceFactory {

    @SuppressWarnings("all")
    @Override
    public PropertySource<?> createPropertySource(@Nullable String name,
                                                  EncodedResource resource) throws IOException {
        String filename = resource.getResource().getFilename();
        if (StringUtils.hasText(filename) && (filename.endsWith(".yml") || filename.endsWith(".yaml"))) {
            YamlPropertiesFactoryBean factory = new YamlPropertiesFactoryBean();
            factory.setResources(resource.getResource());
            factory.afterPropertiesSet();
            return new PropertiesPropertySource(filename, Objects.requireNonNull(factory.getObject()));
        }
        return super.createPropertySource(name, resource);
    }

}
