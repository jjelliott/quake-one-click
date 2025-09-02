package io.github.jjelliott.imgui;

import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImInt;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * A modal window with a dropdown selection.
 */
public class DropdownWindow extends CenteredWindow {

  private final String windowTitle;
  private final String text;
  private final String[] optionsArray;
  private final Runnable cancelAction;
  private final Consumer<Integer> confirmAction;
  private final ImInt currentChoice;
  private final Supplier<Integer> choiceProvider;
  private static final Runnable NO_OP = () -> {};

  /**
   * Creates a new dropdown window.
   *
   * @param windowTitle The window title
   * @param text The description text
   * @param options The list of options for the dropdown
   * @param cancelAction The action to run when canceled
   * @param confirmAction The action to run with the selected index when confirmed
   * @param choiceProvider A supplier that provides the current choice index
   */
  public DropdownWindow(String windowTitle, String text, List<String> options,
      Runnable cancelAction, Consumer<Integer> confirmAction, Supplier<Integer> choiceProvider) {
    this.optionsArray = options.toArray(new String[0]);
    this.windowTitle = windowTitle;
    this.text = text;
    this.cancelAction = cancelAction;
    this.confirmAction = confirmAction;
    this.choiceProvider = choiceProvider;
    this.currentChoice = new ImInt(choiceProvider.get());
  }

  /**
   * Creates a new dropdown window with no cancel action.
   *
   * @param windowTitle The window title
   * @param text The description text
   * @param options The list of options for the dropdown
   * @param confirmAction The action to run with the selected index when confirmed
   * @param choiceProvider A supplier that provides the current choice index
   */
  public DropdownWindow(String windowTitle, String text, List<String> options,
      Consumer<Integer> confirmAction, Supplier<Integer> choiceProvider) {
    this(windowTitle, text, options, NO_OP, confirmAction, choiceProvider);
  }

  @Override
  public void open() {
    currentChoice.set(choiceProvider.get());
    super.open();
  }

  /**
   * Renders the dropdown window.
   */
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

    ImGui.combo("##", currentChoice, optionsArray);

    ImGui.separator();

    float buttonWidth = 80;
    float spacing = ImGui.getStyle().getItemSpacing().x;
    float totalButtonWidth = (buttonWidth * 2) + spacing;
    float windowWidth = ImGui.getWindowWidth();

    ImGui.setCursorPosX((windowWidth - totalButtonWidth) * 0.5f); // Center buttons horizontally

    if (ImGui.button("Cancel", new ImVec2(buttonWidth, 0))) {
      cancelAction.run();
      open = false;
    }
    ImGui.sameLine();
    if (ImGui.button("Save", new ImVec2(buttonWidth, 0))) {
      confirmAction.accept(currentChoice.get());
      open = false;
    }

    ImGui.end();
  }
}
