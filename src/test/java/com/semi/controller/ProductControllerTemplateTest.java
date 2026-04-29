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
        SpringTemplateEngine templateEngine = createTemplateEngine();

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
        assertThat(html).contains("member-welcome");
    }

    @Test
    void dashboardSearchResultTemplateRendersContinueShoppingWhenEmpty() {
        SpringTemplateEngine templateEngine = createTemplateEngine();

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        JakartaServletWebApplication application =
                JakartaServletWebApplication.buildApplication(request.getServletContext());
        WebContext context = new WebContext(
                application.buildExchange(request, response),
                Locale.KOREA,
                Map.of("query", "missing", "products", List.of()));

        String html = templateEngine.process("dashboard_search_result", context);

        assertThat(html).contains("href=\"/\"");
        assertThat(html).contains("&#44228;&#49549; &#49660;&#54609;&#54616;&#44592;");
    }

    @Test
    void dashboardFrameTemplatesRender() {
        SpringTemplateEngine templateEngine = createTemplateEngine();

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        JakartaServletWebApplication application =
                JakartaServletWebApplication.buildApplication(request.getServletContext());
        WebContext context = new WebContext(
                application.buildExchange(request, response),
                Locale.KOREA,
                Map.of());

        assertThat(templateEngine.process("all_orders", context)).contains("Heritage Namhae");
        assertThat(templateEngine.process("cancel_orders", context)).contains("Heritage Namhae");
        assertThat(templateEngine.process("mypage", context)).contains("Heritage Namhae");
    }

    @Test
    void cartTemplateRendersWithEmptyCartRows() {
        SpringTemplateEngine templateEngine = createTemplateEngine();

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        JakartaServletWebApplication application =
                JakartaServletWebApplication.buildApplication(request.getServletContext());
        WebContext context = new WebContext(
                application.buildExchange(request, response),
                Locale.KOREA,
                Map.of("cartRows", List.of()));

        String html = templateEngine.process("cart", context);

        assertThat(html).contains("Heritage Namhae");
        assertThat(html).contains("SERVER_CART_ITEMS");
        assertThat(html).contains("NAMHAE_CHECKOUT_KEY");
    }

    @Test
    void checkoutAndOrderDetailTemplatesRender() {
        SpringTemplateEngine templateEngine = createTemplateEngine();

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        JakartaServletWebApplication application =
                JakartaServletWebApplication.buildApplication(request.getServletContext());
        WebContext context = new WebContext(
                application.buildExchange(request, response),
                Locale.KOREA,
                Map.of());

        String checkoutHtml = templateEngine.process("checkout", context);
        assertThat(checkoutHtml).contains("processPayment");
        assertThat(checkoutHtml).contains("buildCheckoutOrder");
        assertThat(checkoutHtml).contains("formatWon");
        assertThat(checkoutHtml).doesNotContain("points-input");
        assertThat(checkoutHtml).doesNotContain("summary-points-discount");
        assertThat(checkoutHtml).doesNotContain("쿠폰 할인");
        assertThat(checkoutHtml).doesNotContain("포인트 할인");
        assertThat(templateEngine.process("order_detail", context)).contains("order-info");
        assertThat(templateEngine.process("order_success", context)).contains("check_circle");
    }

    private SpringTemplateEngine createTemplateEngine() {
        ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();
        resolver.setPrefix("templates/");
        resolver.setSuffix(".html");
        resolver.setTemplateMode(TemplateMode.HTML);
        resolver.setCharacterEncoding("UTF-8");

        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.setTemplateResolver(resolver);
        return templateEngine;
    }
}
