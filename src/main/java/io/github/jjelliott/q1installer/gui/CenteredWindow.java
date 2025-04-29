package io.github.jjelliott.q1installer.gui;

import imgui.ImGui;
import imgui.ImGuiViewport;
import imgui.ImVec2;
import imgui.flag.ImGuiCond;

public class CenteredWindow extends Window {

  protected String text;

  protected void position() {
    ImGuiViewport mainViewport = ImGui.getMainViewport();
    ImVec2 screenMidpoint = mainViewport.getSize().div(2, 2);
    float textWidth = Math.max(Math.min(ImGui.calcTextSize(text).x, 450), 300);

    ImGui.setNextWindowPos(Math.max(screenMidpoint.x - ((textWidth * 1.1F) * 0.5F), 0),
        (screenMidpoint.y * 0.75F), ImGuiCond.Always);
    ImGui.setNextWindowSize(Math.min(textWidth * 1.1F, mainViewport.getSize().x), 0,
        ImGuiCond.Always);
  }

}
