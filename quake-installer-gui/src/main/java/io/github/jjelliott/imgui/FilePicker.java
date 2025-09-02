package io.github.jjelliott.imgui;

import imgui.ImGui;
import imgui.type.ImString;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.util.tinyfd.TinyFileDialogs;

/**
 * A reusable component for selecting files or folders using a native file dialog.
 */
public class FilePicker {

  private final String idFrag;
  private final String label;
  private String filePath;
  private final String buttonLabel;
  private final Runnable onPathSelected;
  private final boolean selectFolder;

  /**
   * Creates a new FilePicker component.
   *
   * @param idFrag A unique identifier fragment for this picker
   * @param label The label to display
   * @param buttonLabel The text for the button that opens the dialog
   * @param selectFolder True to select folders, false to select files
   * @param defaultValue The initial file path
   * @param onPathSelected Callback to run when a path is selected
   */
  public FilePicker(String idFrag, String label, String buttonLabel, boolean selectFolder,
      String defaultValue, Runnable onPathSelected) {
    this.idFrag = idFrag;
    this.label = label;
    this.buttonLabel = buttonLabel;
    this.selectFolder = selectFolder;
    this.filePath = defaultValue;
    this.onPathSelected = onPathSelected;
  }

  /**
   * Gets the currently selected file path.
   *
   * @return The file path
   */
  public String getFilePath() {
    return filePath;
  }

  /**
   * Sets the file path programmatically.
   *
   * @param filePath The new file path
   */
  public void setFilePath(String filePath) {
    this.filePath = filePath;
  }

  /**
   * Renders the file picker component.
   */
  public void render() {
    ImGui.textWrapped(label + ":");
    ImGui.pushItemWidth(-1); // Make input take available width
    ImGui.inputText("##file_path_" + idFrag,
        new ImString(filePath));
    ImGui.popItemWidth();
    if (ImGui.button(buttonLabel + "##" + idFrag)) {
      openPathPicker();
      if (onPathSelected != null) {
        onPathSelected.run(); // Notify that the button was clicked
      }
    }
  }

  private void openPathPicker() {
    try (MemoryStack stack = MemoryStack.stackPush()) {
      String lTheOpenFileName = null;
      if (selectFolder) {
        lTheOpenFileName = TinyFileDialogs.tinyfd_selectFolderDialog(
            "Select a Folder",
            filePath // Use current path as initial directory
        );
      } else {
        PointerBuffer aFilterPatterns = stack.mallocPointer(1);
        aFilterPatterns.put(stack.UTF8("*.*"));
        aFilterPatterns.flip();

        lTheOpenFileName = TinyFileDialogs.tinyfd_openFileDialog(
            "Select a File",
            filePath, // Use current path as initial directory
            aFilterPatterns,
            null,
            false
        );
      }

      if (lTheOpenFileName != null) {
        filePath = lTheOpenFileName;
      }
    }
  }
}
