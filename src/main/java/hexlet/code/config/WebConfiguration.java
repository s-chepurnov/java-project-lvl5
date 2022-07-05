package hexlet.code.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;

import java.io.IOException;

/**
 * WebConfiguration defines callback methods to customize the Java-based configuration
 * for Spring MVC enabled via @EnableWebMvc.
 */
@Configuration
public class WebConfiguration implements WebMvcConfigurer {

    private final String baseApiPath;

    public WebConfiguration(@Value("${base-url}") String baseApiPathStr) {
        this.baseApiPath = baseApiPathStr;
    }

    /**
     * register resource handlers.
     * @param registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry
                .addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/static/");

        registry
                .addResourceHandler("/*.*")
                .addResourceLocations("classpath:/static/");

        registry
                .addResourceHandler("/", "/**")
                .setCachePeriod(0)
                .addResourceLocations("classpath:/static/index.html")
                .resourceChain(true)
                .addResolver(new PathResourceResolver() {
                    @Override
                    protected Resource getResource(String resourcePath, Resource location) throws IOException {
                        if (resourcePath.startsWith(baseApiPath) || resourcePath.startsWith(baseApiPath.substring(1))) {
                            return null;
                        }

                        return location.exists() && location.isReadable() ? location : null;
                    }
                });
    }
}
