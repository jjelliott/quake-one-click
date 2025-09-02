package io.github.jjelliott.imgui;

import imgui.ImGui;
import imgui.ImGuiViewport;
import imgui.ImVec2;
import imgui.flag.ImGuiCond;

/**
 * A window that centers itself on the screen based on its content width.
 */
public class CenteredWindow extends Window {

  protected String text;
  protected float maxWidth = 450f;
  protected float minWidth = 300f;

  /**
   * Positions the window in the center of the screen based on content text width.
   */
  protected void position() {
    ImGuiViewport mainViewport = ImGui.getMainViewport();
    ImVec2 screenMidpoint = mainViewport.getSize().div(2, 2);
    float textWidth = Math.max(Math.min(ImGui.calcTextSize(text).x, maxWidth), minWidth);

    ImGui.setNextWindowPos(Math.max(screenMidpoint.x - ((textWidth * 1.1F) * 0.5F), 0),
        (screenMidpoint.y * 0.75F), ImGuiCond.Always);
    ImGui.setNextWindowSize(Math.min(textWidth * 1.1F, mainViewport.getSize().x), 0,
        ImGuiCond.Always);
  }

  /**
   * Sets the text used to calculate the window width.
   *
   * @param text The text content
   */
  public void setText(String text) {
    this.text = text;
  }

  /**
   * Gets the current text.
   *
   * @return The text content
   */
  public String getText() {
    return text;
  }

  /**
   * Sets the maximum width for the window.
   *
   * @param maxWidth The maximum width in pixels
   */
  public void setMaxWidth(float maxWidth) {
    this.maxWidth = maxWidth;
  }

  /**
   * Sets the minimum width for the window.
   *
   * @param minWidth The minimum width in pixels
   */
  public void setMinWidth(float minWidth) {
    this.minWidth = minWidth;
  }
}
