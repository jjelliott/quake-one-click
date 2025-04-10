package io.github.jjelliott.q1installer;

import imgui.ImGui;
import imgui.ImGuiViewport;
import imgui.ImVec2;
import imgui.app.Application;
import imgui.app.Configuration;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImBoolean;
import io.github.jjelliott.q1installer.config.UserProps;
import io.github.jjelliott.q1installer.os.ConfigLocation;
import io.github.jjelliott.q1installer.os.ExamplePath;
import io.github.jjelliott.q1installer.os.HandlerInstaller;
import jakarta.inject.Singleton;
import java.util.Scanner;

@Singleton
public class Gui extends Application {
  private final UserProps userProps;
  private final ConfigLocation configLocation;
  private final HandlerInstaller handlerInstaller;
  private final ExamplePath examplePath;
  private boolean handlerOpen = false;

  public Gui(UserProps userProps, ConfigLocation configLocation, HandlerInstaller handlerInstaller,
      ExamplePath examplePath) {
    this.userProps = userProps;
    this.configLocation = configLocation;
    this.handlerInstaller = handlerInstaller;
    this.examplePath = examplePath;
  }

  @Override
  protected void configure(Configuration config) {
    config.setTitle("Quake One-Click Installer");
  }

  @Override
  public void process() {
    ImGuiViewport mainViewport = ImGui.getMainViewport();
    ImVec2 pos = mainViewport.getPos();
    ImVec2 size = mainViewport.getSize();

    ImGui.setNextWindowPos(pos.x, pos.y, ImGuiCond.Always);
    ImGui.setNextWindowSize(size.x, size.y, ImGuiCond.Always);

    if (ImGui.begin("Operations", new ImBoolean(true),
        ImGuiWindowFlags.NoDocking |
            ImGuiWindowFlags.NoTitleBar |
            ImGuiWindowFlags.NoResize |
            ImGuiWindowFlags.NoMove |
            ImGuiWindowFlags.NoBringToFrontOnFocus |
            ImGuiWindowFlags.NoNavFocus)) {
      if (ImGui.button("Install handler")) {
        handlerOpen = true;
      }
      ImGui.button("Set Quake 1 Paths");
      ImGui.button("Set Quake 2 Paths");
      ImGui.button("Set default skill");
      ImGui.button("Clear cache");
      ImGui.button("Exit");

      ImGui.end();

    }

    ImVec2 screenMidpoint = mainViewport.getSize().div(2, 2);

    if (handlerOpen) {
      String text = handlerInstaller.textPrompt();
      float textWidth = ImGui.calcTextSize(text).x;
      float textHeight = ImGui.calcTextSize(text).y;

      ImGui.setNextWindowPos(screenMidpoint.x - ((textWidth * 1.1F) * 0.5F),
          screenMidpoint.y - ((textHeight * 6) * 0.5F), ImGuiCond.Always);
      ImGui.setNextWindowSize(textWidth * 1.1F, 0, ImGuiCond.Always);
      if (ImGui.begin("Install Handler", null,
          ImGuiWindowFlags.NoDocking |
              ImGuiWindowFlags.NoResize |
              ImGuiWindowFlags.NoMove |
              ImGuiWindowFlags.NoCollapse |
              ImGuiWindowFlags.Modal)) {

        float windowWidth = ImGui.getWindowWidth();
        ImGui.setCursorPosX((windowWidth - textWidth) * 0.5f);
        ImGui.text(text);
        ImGui.separator();
        float buttonWidth = 100; // Adjust as needed
        float spacing = ImGui.getStyle().getItemSpacing().x;
        float totalWidth = (buttonWidth * 2) + spacing;

        ImGui.setCursorPosX((windowWidth - totalWidth) * 0.5f);
        if (ImGui.button("Cancel", new ImVec2(buttonWidth, 20))) {
          handlerOpen = false;
        }

        ImGui.sameLine(); // Place the next item on the same line
        if (ImGui.button("Install", new ImVec2(buttonWidth, 20))) {
          handlerInstaller.install();
          handlerOpen = false;
        }

      }
      ImGui.end();
    }

//    if (ImGui.beginMenuBar()){
//      if(ImGui.beginMenu("File")){
//        if(ImGui.menuItem("Quit", "Ctrl+Q")){}
//        ImGui.endMenu();
//      }
//      ImGui.endMenuBar();
//    }
//    ImGui.text("Hello, World!");
  }
}
