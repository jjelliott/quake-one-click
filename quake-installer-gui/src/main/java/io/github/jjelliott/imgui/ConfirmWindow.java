package io.github.jjelliott.imgui;

import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiWindowFlags;
import java.util.function.Supplier;

/**
 * A modal dialog window with confirmation and cancellation options.
 */
public class ConfirmWindow extends CenteredWindow {

  private final String title;
  private final Supplier<String> textProvider;
  private final String confirmText;
  private final Runnable confirmAction;
  private final Runnable cancelAction;
  private static final Runnable NO_OP = () -> {};

  /**
   * Creates a new confirmation window with a static text message.
   *
   * @param title The window title
   * @param text The message text
   * @param confirmText The text for the confirmation button
   * @param confirmAction The action to run when confirmed
   * @param cancelAction The action to run when canceled
   */
  public ConfirmWindow(String title, String text, String confirmText, Runnable confirmAction,
      Runnable cancelAction) {
    this.title = title;
    this.text = text;
    this.textProvider = null;
    this.confirmText = confirmText;
    this.confirmAction = confirmAction;
    this.cancelAction = cancelAction;
    open = false;
  }

  /**
   * Creates a new confirmation window with a dynamic text message.
   *
   * @param title The window title
   * @param textProvider A supplier that provides the message text when needed
   * @param confirmText The text for the confirmation button
   * @param confirmAction The action to run when confirmed
   * @param cancelAction The action to run when canceled
   */
  public ConfirmWindow(String title, Supplier<String> textProvider, String confirmText,
      Runnable confirmAction,
      Runnable cancelAction) {
    this.title = title;
    this.textProvider = textProvider;
    this.confirmText = confirmText;
    this.confirmAction = confirmAction;
    this.cancelAction = cancelAction;
    open = false;
  }

  /**
   * Creates a new confirmation window with a static text message and no cancel action.
   *
   * @param title The window title
   * @param text The message text
   * @param confirmText The text for the confirmation button
   * @param confirmAction The action to run when confirmed
   */
  public ConfirmWindow(String title, String text, String confirmText, Runnable confirmAction) {
    this(title, text, confirmText, confirmAction, NO_OP);
  }

  /**
   * Creates a new confirmation window with a dynamic text message and no cancel action.
   *
   * @param title The window title
   * @param textProvider A supplier that provides the message text when needed
   * @param confirmText The text for the confirmation button
   * @param confirmAction The action to run when confirmed
   */
  public ConfirmWindow(String title, Supplier<String> textProvider, String confirmText,
      Runnable confirmAction) {
    this(title, textProvider, confirmText, confirmAction, NO_OP);
  }

  @Override
  public void open() {
    if (textProvider != null) {
      text = textProvider.get();
    }
    super.open();
  }

  /**
   * Renders the confirmation window.
   */
  public void render() {
    if (!open) {
      return;
    }
    position();
    if (ImGui.begin(title, null,
        ImGuiWindowFlags.NoDocking |
            ImGuiWindowFlags.NoResize |
            ImGuiWindowFlags.NoMove |
            ImGuiWindowFlags.NoCollapse |
            ImGuiWindowFlags.Modal)) {

      for (String s : text.split("\n")) {
        ImGui.textWrapped(s);
      }
      ImGui.separator();
      float windowWidth = ImGui.getWindowWidth();
      float buttonWidth = 100; // Adjust as needed
      float spacing = ImGui.getStyle().getItemSpacing().x;
      float totalWidth = (buttonWidth * 2) + spacing;

      ImGui.setCursorPosX((windowWidth - totalWidth) * 0.5f);
      if (ImGui.button("Cancel", new ImVec2(buttonWidth, 20))) {
        cancelAction.run();
        open = false;
      }

      ImGui.sameLine(); // Place the next item on the same line
      if (ImGui.button(confirmText, new ImVec2(buttonWidth, 20))) {
        confirmAction.run();
        open = false;
      }

    }
    ImGui.end();
  }
}
