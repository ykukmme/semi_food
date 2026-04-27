package com.semi.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;

class ProductControllerTemplateTest {

    @Test
    void indexTemplateRendersWithEmptyProducts() {
        ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();
        resolver.setPrefix("templates/");
        resolver.setSuffix(".html");
        resolver.setTemplateMode(TemplateMode.HTML);
        resolver.setCharacterEncoding("UTF-8");

        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.setTemplateResolver(resolver);

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        JakartaServletWebApplication application =
                JakartaServletWebApplication.buildApplication(request.getServletContext());
        WebContext context = new WebContext(
                application.buildExchange(request, response),
                Locale.KOREA,
                Map.of("products", List.of()));

        String html = templateEngine.process("index", context);

        assertThat(html).contains("Heritage Namhae");
    }
}
