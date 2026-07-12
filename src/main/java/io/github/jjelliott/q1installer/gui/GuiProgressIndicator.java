package io.github.jjelliott.q1installer.gui;

import io.github.jjelliott.imgui.BaseImGuiApp;
import io.github.jjelliott.q1installer.ProgressIndicator;

public class GuiProgressIndicator extends BaseImGuiApp implements ProgressIndicator {

  public GuiProgressIndicator() {
    super("Quake One-Click Installer", 300, 200, "q1c.png");
  }

  @Override
  public void open() {
    launch(this);
  }

  @Override
  public void update(int percentage) {

  }

  @Override
  public void close() {

  }

  @Override
  protected void initializeUI() {

  }

  @Override
  protected void renderMainContent() {

  }
}
