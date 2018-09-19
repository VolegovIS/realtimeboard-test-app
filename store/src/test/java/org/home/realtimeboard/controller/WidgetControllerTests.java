package org.home.realtimeboard.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.home.realtimeboard.configuration.WebConfig;
import org.home.realtimeboard.model.Widget;
import org.home.realtimeboard.store.WidgetStore;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.web.servlet.MockMvc;
import org.testng.annotations.Test;

import java.util.Collections;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Тесты для {@link WidgetController}
 */
@WebMvcTest(WidgetController.class)
public class WidgetControllerTests extends AbstractTestNGSpringContextTests {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private WidgetStore widgetStore;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testAddWidget() throws Exception {
        Widget widget = getWidget();
        Mockito.when(widgetStore.add(ArgumentMatchers.any())).thenReturn(widget);

        mvc.perform(post("/widgets")
                .content(objectMapper.writeValueAsString(widget))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaTypes.HAL_JSON_UTF8))
                .andExpect(jsonPath("$.*", hasSize(8)))
                .andExpect(jsonPath("$.id", is(widget.getId())))
                .andExpect(jsonPath("$.x", is(widget.getX())))
                .andExpect(jsonPath("$.y", is(widget.getY())))
                .andExpect(jsonPath("$.width", is(widget.getWidth())))
                .andExpect(jsonPath("$.height", is(widget.getHeight())))
                .andExpect(jsonPath("$.zindex", is(widget.getZIndex())))
                .andExpect(jsonPath("$._links.*", hasSize(3)))
                .andExpect(jsonPath("$._links.self", notNullValue()))
                .andExpect(jsonPath("$._links.update", notNullValue()))
                .andExpect(jsonPath("$._links.delete", notNullValue()));
    }

    @Test
    public void testAddWidgetInvalid() throws Exception {
        Widget widget = getWidget();
        widget.setX(null);

        mvc.perform(post("/widgets")
                .content(objectMapper.writeValueAsString(widget))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetWidget() throws Exception {
        Widget widget = getWidget();

        Mockito.when(widgetStore.findOne(anyString())).thenReturn(widget);
        mvc.perform(get("/widgets/1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaTypes.HAL_JSON_UTF8))
                .andExpect(jsonPath("$.*", hasSize(8)))
                .andExpect(jsonPath("$.id", is(widget.getId())))
                .andExpect(jsonPath("$.x", is(widget.getX())))
                .andExpect(jsonPath("$.y", is(widget.getY())))
                .andExpect(jsonPath("$.width", is(widget.getWidth())))
                .andExpect(jsonPath("$.height", is(widget.getHeight())))
                .andExpect(jsonPath("$.zindex", is(widget.getZIndex())))
                .andExpect(jsonPath("$._links.*", hasSize(3)))
                .andExpect(jsonPath("$._links.self", notNullValue()))
                .andExpect(jsonPath("$._links.update", notNullValue()))
                .andExpect(jsonPath("$._links.delete", notNullValue()));
    }

    @Test
    public void testGetWidgetMissing() throws Exception {
        Mockito.when(widgetStore.findOne(anyString())).thenReturn(null);
        mvc.perform(get("/widgets/1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testUpdateWidget() throws Exception {
        Widget widget = getWidget();
        Mockito.when(widgetStore.update(anyString(), ArgumentMatchers.any())).thenReturn(widget);

        mvc.perform(put("/widgets/1")
                .content(objectMapper.writeValueAsString(widget))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaTypes.HAL_JSON_UTF8))
                .andExpect(jsonPath("$.*", hasSize(8)))
                .andExpect(jsonPath("$.id", is(widget.getId())))
                .andExpect(jsonPath("$.x", is(widget.getX())))
                .andExpect(jsonPath("$.y", is(widget.getY())))
                .andExpect(jsonPath("$.width", is(widget.getWidth())))
                .andExpect(jsonPath("$.height", is(widget.getHeight())))
                .andExpect(jsonPath("$.zindex", is(widget.getZIndex())))
                .andExpect(jsonPath("$._links.*", hasSize(3)))
                .andExpect(jsonPath("$._links.self", notNullValue()))
                .andExpect(jsonPath("$._links.update", notNullValue()))
                .andExpect(jsonPath("$._links.delete", notNullValue()));
    }

    @Test
    public void testUpdateWidgetInvalid() throws Exception {
        Widget widget = getWidget();
        widget.setX(null);

        mvc.perform(put("/widgets/1")
                .content(objectMapper.writeValueAsString(widget))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testFindAllWidgets() throws Exception {
        Widget widget = getWidget();
        Mockito.when(widgetStore.findAll(ArgumentMatchers.any(), ArgumentMatchers.any()))
                .thenReturn(new PageImpl<>(Collections.singletonList(widget), PageRequest.of(0, 20), 40));

        mvc.perform(get("/widgets").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaTypes.HAL_JSON_UTF8))
                .andExpect(jsonPath("$.*", hasSize(3)))
                .andExpect(jsonPath("$._embedded.*", hasSize(1)))
                .andExpect(jsonPath("$._embedded.content[0].id", is(widget.getId())))
                .andExpect(jsonPath("$._embedded.content[0].x", is(widget.getX())))
                .andExpect(jsonPath("$._embedded.content[0].y", is(widget.getY())))
                .andExpect(jsonPath("$._embedded.content[0].width", is(widget.getWidth())))
                .andExpect(jsonPath("$._embedded.content[0].height", is(widget.getHeight())))
                .andExpect(jsonPath("$._embedded.content[0].zindex", is(widget.getZIndex())))
                .andExpect(jsonPath("$._embedded.content[0]._links.*", hasSize(3)))
                .andExpect(jsonPath("$._embedded.content[0]._links.self", notNullValue()))
                .andExpect(jsonPath("$._embedded.content[0]._links.update", notNullValue()))
                .andExpect(jsonPath("$._embedded.content[0]._links.delete", notNullValue()))
                .andExpect(jsonPath("$._links.*", hasSize(4)))
                .andExpect(jsonPath("$._links.self", notNullValue()))
                .andExpect(jsonPath("$._links.next", notNullValue()))
                .andExpect(jsonPath("$._links.first", notNullValue()))
                .andExpect(jsonPath("$._links.last", notNullValue()))
                .andExpect(jsonPath("$.page.*", hasSize(4)))
                .andExpect(jsonPath("$.page.size", is(20)))
                .andExpect(jsonPath("$.page.totalElements", is(40)))
                .andExpect(jsonPath("$.page.totalPages", is(2)))
                .andExpect(jsonPath("$.page.number", is(0)));
    }

    @Test
    public void testFindAllWidgetsInvalid() throws Exception {
        Widget widget = getWidget();
        Mockito.when(widgetStore.findAll(ArgumentMatchers.any(), ArgumentMatchers.any()))
                .thenReturn(new PageImpl<>(Collections.singletonList(widget), PageRequest.of(0, 20), 40));

        mvc.perform(get("/widgets?top=1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testDelete() throws Exception {
        mvc.perform(delete("/widgets/1"))
                .andExpect(status().isNoContent());
    }

    private Widget getWidget() {
        return Widget.builder()
                .id(UUID.randomUUID().toString())
                .x(0)
                .y(10)
                .width(20)
                .height(30)
                .zIndex(40)
                .build();
    }

    @TestConfiguration
    @ContextConfiguration(classes = WebConfig.class)
    @ComponentScan("org.home.realtimeboard.integration")
    public static class ContextConfig {
        @MockBean
        public WidgetStore widgetStore;
    }
}
