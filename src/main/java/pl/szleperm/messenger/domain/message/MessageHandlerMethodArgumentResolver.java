package pl.szleperm.messenger.domain.message;

import org.springframework.core.MethodParameter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableArgumentResolver;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * @author Marcin Szleper
 */
public class MessageHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver {

    private PageableArgumentResolver pageableArgumentResolver = new PageableHandlerMethodArgumentResolver();

    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        return methodParameter.getParameterType().equals(MessageRequest.class);
    }

    @Override
    public MessageRequest resolveArgument(MethodParameter methodParameter,
                                          ModelAndViewContainer modelAndViewContainer,
                                          NativeWebRequest nativeWebRequest,
                                          WebDataBinderFactory webDataBinderFactory) throws Exception {
        MessageSpecifications messageSpecifications = MessageSpecifications.build(nativeWebRequest);
        PageRequest pageable = getPageRequest(methodParameter, modelAndViewContainer, nativeWebRequest, webDataBinderFactory);
        if (messageSpecifications == null) return null;
        else return new MessageRequest(pageable, messageSpecifications);
    }

    protected PageRequest getPageRequest(MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer, NativeWebRequest nativeWebRequest, WebDataBinderFactory webDataBinderFactory) {
        Pageable pageable = pageableArgumentResolver.resolveArgument(
                methodParameter,
                modelAndViewContainer,
                nativeWebRequest,
                webDataBinderFactory);
        return new PageRequest(pageable.getPageNumber(),
                pageable.getPageSize(),
                pageable.getSort() == null ? new Sort(Sort.Direction.DESC, "sentDate") : pageable.getSort());
    }
}