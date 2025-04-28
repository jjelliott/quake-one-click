package io.github.jjelliott.q1installer.gui;

import imgui.ImGui;
import imgui.type.ImString;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.util.tinyfd.TinyFileDialogs;

public class FilePicker {

  private final String idFrag;
  private final String label;
  private String filePath; // Default value is now set during construction
  private final String buttonLabel;
  private final Runnable onPathSelected;
  private final boolean selectFolder;

  public FilePicker(String idFrag, String label, String buttonLabel, boolean selectFolder,
      String defaultValue, Runnable onPathSelected) {
    this.idFrag = idFrag;
    this.label = label;
    this.buttonLabel = buttonLabel;
    this.selectFolder = selectFolder;
    this.filePath = defaultValue; // Initialize with the default value
    this.onPathSelected = onPathSelected;
  }

  public String getFilePath() {
    return filePath;
  }

  public void setFilePath(String filePath) {
    this.filePath = filePath;
  }

  public void render() {
    ImGui.textWrapped(label + ":");
    ImGui.pushItemWidth(-1); // Make input take available width
    ImGui.inputText("##file_path_" + idFrag,
        new ImString(filePath));//,  ImGuiInputTextFlags.ReadOnly);
    ImGui.popItemWidth();
//        ImGui.sameLine();
    if (ImGui.button(buttonLabel + "##" + idFrag)) {
      openPathPicker();
      onPathSelected.run(); // Notify that the button was clicked
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
        filePath = lTheOpenFileName; // Use getStringUTF8 directly
      }
    }
  }
}
