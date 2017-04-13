package com.caine.ui;

import javafx.embed.swing.JFXPanel;
import javafx.scene.Parent;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class MainModuleTest {

    private JFXPanel jfxContext;
    private MainModule target = new MainModule();

    @Before
    public void setupJavaFxContext() {
        jfxContext = new JFXPanel();
    }

    @Test
    public void parent() throws IOException {
        Parent parent = target.parent();

        assertThat(parent).isNotNull();
        assertThat(parent).isInstanceOf(Parent.class);
    }

    @Test
    public void controller() throws IOException {
        target.parent();
        SearchController controller = target.controller();

        assertThat(controller).isNotNull();
        assertThat(controller).isExactlyInstanceOf(SearchController.class);
    }

    @Test
    public void listView() throws IOException {
        target.parent();
        SearchController controller = target.controller();

        assertThat(controller).isNotNull();
        assertThat(controller).isExactlyInstanceOf(SearchController.class);
    }

}