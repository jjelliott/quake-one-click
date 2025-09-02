package io.github.jjelliott.imgui;

/**
 * Base window class that provides core functionality for all ImGui windows.
 */
public class Window {

  protected boolean open;

  /**
   * Opens the window.
   */
  public void open() {
    open = true;
  }

  /**
   * Checks if the window is open.
   *
   * @return true if the window is open, false otherwise
   */
  public boolean isOpen() {
    return open;
  }

  /**
   * Closes the window.
   */
  public void close() {
    open = false;
  }


  /**
   * Default render implementation that does nothing. Subclasses should override this method to
   * provide custom rendering.
   */
  public void render() {
  }


}
