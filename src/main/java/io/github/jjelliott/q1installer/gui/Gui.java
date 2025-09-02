package io.github.jjelliott.q1installer.gui;

import io.github.jjelliott.imgui.BaseImGuiApp;
import io.github.jjelliott.imgui.ConfirmWindow;
import io.github.jjelliott.imgui.DropdownWindow;
import io.github.jjelliott.imgui.ImGuiUtils;
import io.github.jjelliott.q1installer.config.Game;
import io.github.jjelliott.q1installer.config.UserProps;
import io.github.jjelliott.q1installer.os.CacheOperations;
import io.github.jjelliott.q1installer.os.ExamplePath;
import io.github.jjelliott.q1installer.os.HandlerInstaller;
import jakarta.inject.Singleton;
import java.util.List;

@Singleton
public class Gui extends BaseImGuiApp {

  private final ConfirmWindow handlerWindow;
  private final PathsWindow quakePathWindow;
  private final PathsWindow quake2PathWindow;
  private final ConfirmWindow cacheWindow;
  private final DropdownWindow skillWindow;
  private final float buttonWidth = 200;
  private io.github.jjelliott.imgui.Image logoImage;

  public Gui(
      UserProps userProps,
      HandlerInstaller handlerInstaller,
      ExamplePath examplePath,
      CacheOperations cacheOperations) {
    super("Quake One-Click Installer", 550, 650, "q1c.png");

    this.handlerWindow = new ConfirmWindow(
        "Install Handler", handlerInstaller.textPrompt(), "Install", handlerInstaller::install);
    this.cacheWindow = new ConfirmWindow(
        "Clear Cache",
        () -> "Cache currently sized at " + cacheOperations.getCacheSize()
            + ".\nWould you like to clear it?",
        "Clear Cache",
        cacheOperations::clearCache);
    this.quakePathWindow = new PathsWindow(Game.QUAKE, userProps, examplePath);
    this.quake2PathWindow = new PathsWindow(Game.QUAKE2, userProps, examplePath);
    this.skillWindow = new DropdownWindow(
        "Default Skill",
        "What skill would you like to launch?",
        List.of("Ask every time", "Easy", "Normal", "Hard", "Nightmare"),
        choice -> userProps.setSkill(choice - 1),
        () -> userProps.getSkill() + 1);

    // Add windows to the application
    addWindow(handlerWindow);
    addWindow(cacheWindow);
    addWindow(skillWindow);
    addWindow(quakePathWindow);
    addWindow(quake2PathWindow);
  }

  @Override
  protected void initializeUI() {
    logoImage = new io.github.jjelliott.imgui.Image("q1c.png");
  }

  @Override
  protected void renderMainContent() {
    float windowHeight = getWindowHeight();
    float spacing = imgui.ImGui.getStyle().getItemSpacing().y;

    float totalHeight = 356
        + imgui.ImGui.getTextLineHeightWithSpacing()
        + (imgui.ImGui.getFrameHeightWithSpacing() * 6)
        + (spacing * 7); // Calculate total height of elements

    imgui.ImGui.setCursorPosY((windowHeight - totalHeight) * 0.5f); // Center vertically

    // Center the logo
    ImGuiUtils.centerImage(logoImage, 356);
    ImGuiUtils.centerText("Quake One-Click Installer Menu");

    renderMenuButtons();
  }

  /**
   * Renders all the menu buttons.
   */
  private void renderMenuButtons() {
    ImGuiUtils.centerButton("Install handler", buttonWidth, handlerWindow::open);
    ImGuiUtils.centerButton("Set Quake 1 Paths", buttonWidth, quakePathWindow::open);
    ImGuiUtils.centerButton("Set Quake 2 Paths", buttonWidth, quake2PathWindow::open);
    ImGuiUtils.centerButton("Set default skill", buttonWidth, skillWindow::open);
    ImGuiUtils.centerButton("Clear cache", buttonWidth, cacheWindow::open);
    ImGuiUtils.centerButton("Exit", buttonWidth, () -> {
      dispose();
      System.exit(0);
    });
  }
}
