package io.github.jjelliott.q1installer.gui;

import imgui.ImGui;
import imgui.ImGuiViewport;
import imgui.ImVec2;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImInt;
import java.security.Provider;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class DropdownWindow extends CenteredWindow {

  private final String windowTitle;
  private final String text;
  private final String[] optionsArray;
  private final Runnable cancelAction;
  private final Consumer<Integer> confirmAction;
  private final ImInt skillChoice;
  private final Supplier<Integer> choiceProvider;

  public DropdownWindow(String windowTitle, String text, List<String> options,
      Runnable cancelAction, Consumer<Integer> confirmAction, Supplier<Integer> choiceProvider) {
    this.optionsArray = options.toArray(new String[0]);
    this.windowTitle = windowTitle;
    this.text = text;
    this.cancelAction = cancelAction;
    this.confirmAction = confirmAction;
    this.choiceProvider = choiceProvider;
    skillChoice = new ImInt(choiceProvider.get());
  }

  @Override
  public void open() {
    skillChoice.set(choiceProvider.get());
    super.open();
  }

  public void render() {
    if (!open) {
      return;
    }
    position();
    ImGui.begin(windowTitle, null,
        ImGuiWindowFlags.NoDocking |
            ImGuiWindowFlags.NoResize |
            ImGuiWindowFlags.NoMove |
            ImGuiWindowFlags.NoCollapse |
            ImGuiWindowFlags.Modal);

    ImGui.text(text);

    ImGui.combo("##", skillChoice, optionsArray);

    ImGui.separator();

    float buttonWidth = 80;
    float spacing = ImGui.getStyle().getItemSpacing().x;
    float totalButtonWidth = (buttonWidth * 2) + spacing;
    float windowWidth = ImGui.getWindowWidth();

    ImGui.setCursorPosX((windowWidth - totalButtonWidth) * 0.5f); // Center buttons horizontally

    if (ImGui.button("Cancel", new ImVec2(buttonWidth, 0))) {
      cancelAction.run();
      open=false;
    }
    ImGui.sameLine();
    if (ImGui.button("Save", new ImVec2(buttonWidth, 0))) {
      confirmAction.accept(skillChoice.get());
      open = false;
    }

    ImGui.end();
  }
//  public void createDropdownWindow(String windowTitle, String text, List<String> options,
//      Runnable cancelAction, Consumer<Integer> confirmAction) {
//
//    String[] optionsArray = options.toArray(new String[0]);
//
//    ImGuiViewport mainViewport = ImGui.getMainViewport();
//    ImVec2 screenMidpoint = mainViewport.getSize().div(2, 2);
//    float textWidth = Math.max(Math.min(ImGui.calcTextSize(text).x, 450), 300);
//    float textHeight = ImGui.calcTextSize(text).y;
//
//    ImGui.setNextWindowPos(Math.max(screenMidpoint.x - ((textWidth * 1.1F) * 0.5F), 0),
//        screenMidpoint.y * .85f, ImGuiCond.Always);
//    ImGui.setNextWindowSize(Math.min(textWidth * 1.1F, mainViewport.getSize().x), 0,
//        ImGuiCond.Always);
//    ImGui.begin(windowTitle, null,
//        ImGuiWindowFlags.NoDocking |
//            ImGuiWindowFlags.NoResize |
//            ImGuiWindowFlags.NoMove |
//            ImGuiWindowFlags.NoCollapse |
//            ImGuiWindowFlags.Modal);
//
//    ImGui.text(text);
//
//    ImGui.combo("##", skillChoice, optionsArray);
//
//    ImGui.separator();
//
//    float buttonWidth = 80;
//    float spacing = ImGui.getStyle().getItemSpacing().x;
//    float totalButtonWidth = (buttonWidth * 2) + spacing;
//    float windowWidth = ImGui.getWindowWidth();
//
//    ImGui.setCursorPosX((windowWidth - totalButtonWidth) * 0.5f); // Center buttons horizontally
//
//    if (ImGui.button("Cancel", new ImVec2(buttonWidth, 0))) {
//      cancelAction.run();
//    }
//    ImGui.sameLine();
//    if (ImGui.button("Save", new ImVec2(buttonWidth, 0))) {
//      confirmAction.accept(skillChoice.get());
//    }
//
//    ImGui.end();
//  }
}
