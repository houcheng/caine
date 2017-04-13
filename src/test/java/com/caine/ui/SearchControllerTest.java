package com.caine.ui;

import com.caine.config.AppConfiguration;
import com.caine.core.CommandEngine;
import com.caine.core.QueryResultGenerator;
import com.caine.plugin.PluginManager;
import de.saxsys.javafx.test.JfxRunner;
import de.saxsys.javafx.test.TestInJfxThread;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.*;
import javafx.stage.Stage;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import javax.swing.*;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@RunWith(JfxRunner.class)
public class SearchControllerTest {

    private static final String DEFAULT_BANNER = "Default Banner";
    public static final String HOT_KEY_STRING = "HOT_KEY_STRING";
    public static final KeyStroke HOT_KEY_STROKE = KeyStroke.getKeyStroke(HOT_KEY_STRING);
    public static final int DELAY_FOR_INPUT_DELAY = 500;

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    private CommandEngine commandEngine;
    @Mock
    private AppConfiguration appConfiguration;
    @Mock
    private PluginManager pluginManager;
    @Mock
    private SearchListOrganizer searchListOrganizer;

    @Mock
    private QueryResultGenerator queryResultGenerator;

    private SearchController searchController;
    private Stage stage;

    void initializeUI() throws IOException {
        loadSearchWindowFromResource();
        setupMocks();
        searchController.initializeUI(stage);
    }

    @Test
    @TestInJfxThread
    public void testInitializeUI() throws IOException {

        initializeUI();

        assertThat(searchController.getListView().getItems().size()).isZero();
        assertThat(searchController.inputTextField.getPromptText()).isEqualTo(DEFAULT_BANNER);
    }

    @Test
    public void testHandleKeyTypedOnTextField() throws IOException, InterruptedException {

        initializeUIOnUIThread();

        String inputText = "";
        for (int i = 0; i < SearchController.MINIMUM_QUERY_STRING_LENGTH; i++) {
            verify(pluginManager, never()).updateQuery(any(), any());

            inputText += KeyCode.A.toString();
            searchController.getInputTextField().setText(inputText);
            searchController.handleKeyTypedOnTextField(createKeyTypedEvent(KeyCode.A));
        }

        Thread.sleep(DELAY_FOR_INPUT_DELAY);
        verify(pluginManager, times(3)).updateQuery(HOT_KEY_STROKE, inputText);
    }

    @Test
    @TestInJfxThread
    public void testHandleKeyPressedUp() throws IOException {

        initializeUI();

        searchController.handleKeyPressed(createKeyPressedEvent(KeyCode.UP));
        verify(searchListOrganizer).moveSelectedItem(-1);
    }

    @Test
    @TestInJfxThread
    public void testHandleKeyPressedDown() throws IOException {

        initializeUI();

        searchController.handleKeyPressed(createKeyPressedEvent(KeyCode.DOWN));
        verify(searchListOrganizer).moveSelectedItem(1);
    }

    @Test
    @TestInJfxThread
    public void testHandleKeyPressedPageDown() throws IOException {

        initializeUI();

        searchController.handleKeyPressed(createKeyPressedEvent(KeyCode.PAGE_DOWN));
        verify(searchListOrganizer).moveSelectedItemByPage(1);
    }

    @Test
    @TestInJfxThread
    public void testHandleKeyPressedPageUp() throws IOException {

        initializeUI();

        searchController.handleKeyPressed(createKeyPressedEvent(KeyCode.PAGE_UP));
        verify(searchListOrganizer).moveSelectedItemByPage(-1);
    }

    @Test
    @TestInJfxThread
    public void testHandleKeyPressedEnter() throws IOException {

        initializeUI();


        searchController.handleKeyPressed(createKeyPressedEvent(KeyCode.ENTER));

        verify(searchListOrganizer).getCurrentQueryResult();

        verify(searchListOrganizer).updateQueryString("");
        verify(pluginManager).cancelQuery(KeyStroke.getKeyStroke(HOT_KEY_STRING));
    }

    @Test
    @TestInJfxThread
    public void testHandleKeyPressedEscape() throws IOException {

        initializeUI();

        searchController.handleKeyPressed(createKeyPressedEvent(KeyCode.ESCAPE));

        verify(searchListOrganizer, never()).getCurrentQueryResult();

        verify(searchListOrganizer).updateQueryString("");
        verify(pluginManager).cancelQuery(KeyStroke.getKeyStroke(HOT_KEY_STRING));
    }

    @Test
    @TestInJfxThread
    public void testHandleMouseClickedOnList() throws IOException {

        initializeUI();

        MouseEvent mouseEvent = createMouseClickEvent(1);
        searchController.handleMouseClickedOnList(mouseEvent);

        verify(searchListOrganizer, times(1)).updateCurrentIndexByListSelection();
        verify(searchListOrganizer, never()).getCurrentQueryResult();
    }

    @Test
    @TestInJfxThread
    public void testHandleMouseDoubleClickedOnList() throws IOException {

        initializeUI();

        MouseEvent mouseEvent = createMouseClickEvent(2);
        searchController.handleMouseClickedOnList(mouseEvent);

        verify(searchListOrganizer, times(1)).updateCurrentIndexByListSelection();
        verify(searchListOrganizer, times(1)).getCurrentQueryResult();
    }

    @Test
    @TestInJfxThread
    public void testUpdateWindowSizeByItemNumber() throws IOException {

        initializeUI();

        searchController.updateWindowSizeByItemNumber(1);

        verify(searchListOrganizer).updateListViewSize(anyInt());
    }

    @Test
    @TestInJfxThread
    public void testShowWindowOnHotKey() throws IOException {

        initializeUI();

        KeyStroke keyStroke = KeyStroke.getKeyStroke("F2");
        when(pluginManager.getBannerFromHotKey(keyStroke)).thenReturn("Banner for F2");

        searchController.showWindowOnHotKey(keyStroke);

        verify(pluginManager, timeout(DELAY_FOR_INPUT_DELAY)).getBannerFromHotKey(keyStroke);
        assertThat(searchController.getInputTextField().getPromptText()).isEqualTo("Banner for F2");
    }

    @Test
    public void testAppendSearchResult() throws InterruptedException {
        initializeUIOnUIThread();

        String queryString = "query string";
        searchController.getInputTextField().setText(queryString);
        searchController.handleKeyTypedOnTextField(createKeyTypedEvent(KeyCode.A));
        Thread.sleep(DELAY_FOR_INPUT_DELAY);

        searchController.appendSearchResult(queryString, queryResultGenerator);

        verify(searchListOrganizer, timeout(1000)).appendQueryResult(queryString, queryResultGenerator);
    }

    private void loadSearchWindowFromResource() throws IOException {

        FXMLLoader loader = new FXMLLoader(SearchControllerTest.class.getResource("/SearchWindow.fxml"));
        Parent root = loader.load();
        stage = new Stage();
        stage.setScene(new Scene(root));

        searchController = loader.getController();
    }

    private void setupMocks() {

        searchController.updateDependencies(appConfiguration, pluginManager,
                searchListOrganizer, commandEngine);

        when(appConfiguration.getDefaultHotKey()).thenReturn(HOT_KEY_STRING);
        when(pluginManager.getBannerFromHotKey(HOT_KEY_STROKE)).thenReturn(DEFAULT_BANNER);
    }

    private KeyEvent createKeyTypedEvent(KeyCode keyCode) {
        return new KeyEvent(KeyEvent.KEY_PRESSED, "UP", "UP", keyCode, false, false, false, false);
    }

    private KeyEvent createKeyPressedEvent(KeyCode keyCode) {
        return new KeyEvent(KeyEvent.KEY_PRESSED, "UP", "UP", keyCode, false, false, false, false);
    }

    private MouseEvent createMouseClickEvent(int clickCount) {
        return new MouseEvent(MouseEvent.MOUSE_CLICKED, 0, 0, 0, 0,
                MouseButton.PRIMARY, clickCount, true, true, true, true, true, true, true, true, true, true, null);
    }

    private void initializeUIOnUIThread() throws InterruptedException {

        Platform.runLater(
                createInitializeUIJob());

        Thread.sleep(500);
    }

    private Runnable createInitializeUIJob() {
        return new Runnable() {
            @Override
            public void run() {
                try {
                    initializeUI();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
    }
}