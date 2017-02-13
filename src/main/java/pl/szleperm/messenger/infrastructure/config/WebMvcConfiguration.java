package pl.szleperm.messenger.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import pl.szleperm.messenger.domain.message.MessageHandlerMethodArgumentResolver;

import java.util.List;

/**
 * @author Marcin Szleper
 */
@Configuration
public class WebMvcConfiguration extends WebMvcConfigurerAdapter{

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/login").setViewName("login");
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(new MessageHandlerMethodArgumentResolver());
    }
}
