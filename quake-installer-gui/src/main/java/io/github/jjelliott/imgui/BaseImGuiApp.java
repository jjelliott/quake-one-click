package io.github.jjelliott.imgui;

import imgui.ImGui;
import imgui.ImGuiViewport;
import imgui.ImVec2;
import imgui.app.Application;
import imgui.app.Configuration;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImBoolean;
import java.util.ArrayList;
import java.util.List;

/**
 * A base ImGui application with common functionality for creating fullscreen applications
 * with windows and dialogs.
 */
public abstract class BaseImGuiApp extends Application {

  private final List<Window> windows = new ArrayList<>();
  private float windowWidth;
  private float windowHeight;
  private final String appTitle;
  private final int initialWidth;
  private final int initialHeight;
  private final String iconResource;

  /**
   * Creates a new BaseImGuiApp.
   *
   * @param appTitle The application title
   * @param initialWidth The initial window width
   * @param initialHeight The initial window height
   * @param iconResource The path to the window icon resource
   */
  protected BaseImGuiApp(String appTitle, int initialWidth, int initialHeight, String iconResource) {
    this.appTitle = appTitle;
    this.initialWidth = initialWidth;
    this.initialHeight = initialHeight;
    this.iconResource = iconResource;
  }

  /**
   * Adds a window to the application.
   *
   * @param window The window to add
   */
  protected void addWindow(Window window) {
    windows.add(window);
  }

  @Override
  protected void configure(Configuration config) {
    config.setTitle(appTitle);
    config.setWidth(initialWidth);
    config.setHeight(initialHeight);
  }

  @Override
  protected void preRun() {
    if (iconResource != null && !iconResource.isEmpty()) {
      ImGuiUtils.setWindowIconFromClasspath(handle, iconResource);
    }
    initializeUI();
    super.preRun();
  }

  /**
   * Initialize the UI components.
   * This method is called once during startup.
   */
  protected abstract void initializeUI();

  /**
   * Render the main content area of the application.
   */
  protected abstract void renderMainContent();

  /**
   * Set the application theme colors.
   */
  protected void setThemeColors() {
    // Default dark theme
    ImGui.getStyle().setColor(ImGuiCol.WindowBg, 0.25f, 0.25f, 0.3f, 1f);
    ImGui.getStyle().setColor(ImGuiCol.Border, 0.3f, 0.3f, 0.25f, 1f);
    ImGui.getStyle().setColor(ImGuiCol.Separator, 0.3f, 0.3f, 0.25f, 1f);
  }

  @Override
  public void process() {
    setThemeColors();
    ImGuiViewport mainViewport = ImGui.getMainViewport();
    ImVec2 pos = mainViewport.getPos();
    ImVec2 size = mainViewport.getSize();

    ImGui.setNextWindowPos(pos.x, pos.y, ImGuiCond.Always);
    ImGui.setNextWindowSize(size.x, size.y, ImGuiCond.Always);

    if (ImGui.begin("MainApp", new ImBoolean(true),
        ImGuiWindowFlags.NoDocking |
            ImGuiWindowFlags.NoTitleBar |
            ImGuiWindowFlags.NoResize |
            ImGuiWindowFlags.NoMove |
            ImGuiWindowFlags.NoBringToFrontOnFocus |
            ImGuiWindowFlags.NoNavFocus)) {
      windowWidth = ImGui.getWindowWidth();
      windowHeight = ImGui.getWindowHeight();

      renderMainContent();

      ImGui.end();
    }

    // Style for modal windows
    ImGui.pushStyleColor(ImGuiCol.WindowBg, .1f, .1f, .15f, .9f);

    // Render all windows
    for (Window window : windows) {
      if (window.isOpen()) {
          window.render();
      }
    }

    ImGui.popStyleColor();
  }

  /**
   * Gets the current window width.
   *
   * @return The window width
   */
  protected float getWindowWidth() {
    return windowWidth;
  }

  /**
   * Gets the current window height.
   *
   * @return The window height
   */
  protected float getWindowHeight() {
    return windowHeight;
  }
}
