package io.github.jjelliott.q1installer.gui;

import static io.github.jjelliott.q1installer.NoOp.NO_OP;

import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiWindowFlags;
import io.github.jjelliott.imgui.CenteredWindow;
import io.github.jjelliott.imgui.FilePicker;
import io.github.jjelliott.q1installer.config.Game;
import io.github.jjelliott.q1installer.config.UserProps;
import io.github.jjelliott.q1installer.os.ExamplePath;

public class PathsWindow extends CenteredWindow {

  private static final String TEXT = "Set paths";

  Game game;
  UserProps.GameProps gameProps;
  FilePicker enginePicker;
  FilePicker gamedirPicker;

  public PathsWindow(Game game, UserProps userProps, ExamplePath examplePath) {
    this.text = TEXT;
    this.game = game;
    this.gameProps = userProps.getGameProps(game);
    enginePicker = new FilePicker(
        game.name() + "_engine",
        "Engine path (Example " + examplePath.engine(game) + ")",
        "Select",
        false,
        gameProps.getEnginePath(),
        NO_OP);
    gamedirPicker = new FilePicker(
        game.name() + "_gamedir",
        "Game directory (Example " + examplePath.gameDir(game) + ")",
        "Select",
        true,
        gameProps.getDirectoryPath(),
        NO_OP);
  }

  public void open() {
    open = true;
    enginePicker.setFilePath(gameProps.getEnginePath());
    gamedirPicker.setFilePath(gameProps.getDirectoryPath());
  }

  public void render() {
    if (!open) {
      return;
    }
    position();
    if (ImGui.begin(
        "Paths for " + game.name(),
        null,
        ImGuiWindowFlags.NoDocking
            | ImGuiWindowFlags.NoResize
            | ImGuiWindowFlags.NoMove
            | ImGuiWindowFlags.NoCollapse
            | ImGuiWindowFlags.Modal)) {
      gamedirPicker.render();
      enginePicker.render();
      ImGui.separator();
      float windowWidth = ImGui.getWindowWidth();
      float buttonWidth = 100; // Adjust as needed
      float spacing = ImGui.getStyle().getItemSpacing().x;
      float totalWidth = (buttonWidth * 2) + spacing;

      ImGui.setCursorPosX((windowWidth - totalWidth) * 0.5f);
      if (ImGui.button("Cancel", new ImVec2(buttonWidth, 20))) {
        open = false;
      }

      ImGui.sameLine(); // Place the next item on the same line
      if (ImGui.button("Save", new ImVec2(buttonWidth, 20))) {
        //            confirmAction.run();
        gameProps.setPaths(gamedirPicker.getFilePath(), enginePicker.getFilePath());
        open = false;
      }
    }
    ImGui.end();
  }
}
