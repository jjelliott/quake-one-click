package io.github.jjelliott.q1installer;

import imgui.ImGui;
import imgui.ImGuiViewport;
import imgui.ImVec2;
import imgui.app.Application;
import imgui.app.Configuration;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImBoolean;
import io.github.jjelliott.q1installer.config.UserProps;
import io.github.jjelliott.q1installer.os.ConfigLocation;
import io.github.jjelliott.q1installer.os.ExamplePath;
import io.github.jjelliott.q1installer.os.HandlerInstaller;
import jakarta.inject.Singleton;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_CLAMP_TO_BORDER;
@Singleton
public class Gui extends Application {

  private final UserProps userProps;
  private final ConfigLocation configLocation;
  private final HandlerInstaller handlerInstaller;
  private final ExamplePath examplePath;
  private final MenuOperations menuOperations;
  private boolean handlerOpen = false;
  private boolean quakePathsOpen = false;
  private boolean quake2PathsOpen = false;
  private boolean skillsOpen = false;
  private boolean cacheOpen = false;

  public Gui(UserProps userProps, ConfigLocation configLocation, HandlerInstaller handlerInstaller,
      ExamplePath examplePath, MenuOperations menuOperations) {
    this.userProps = userProps;
    this.configLocation = configLocation;
    this.handlerInstaller = handlerInstaller;
    this.examplePath = examplePath;
    this.menuOperations = menuOperations;
  }

  @Override
  protected void configure(Configuration config) {
    config.setTitle("Quake One-Click Installer");
    config.setWidth(550);
    config.setHeight(650);
  }

  @Override
  public void process() {
    if (logoTextureID == 0) {
      System.out.println("loading image!");
      loadImage("q1c.png");
    }
    ImGui.getStyle().setColor(ImGuiCol.WindowBg, 0.4f,0.4f,0.45f,1f);
    ImGui.getStyle().setColor(ImGuiCol.Border, 0.3f,0.3f,0.25f,1f);
    ImGui.getStyle().setColor(ImGuiCol.Separator, 0.3f,0.3f,0.25f,1f);
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
      float windowWidth = ImGui.getWindowWidth();
      float windowHeight = ImGui.getWindowHeight();
      float spacing = ImGui.getStyle().getItemSpacing().y;

      float totalHeight = 356 + ImGui.getTextLineHeightWithSpacing() + (ImGui.getFrameHeightWithSpacing() * 6) + (spacing * 7); // Calculate total height of elements

      ImGui.setCursorPosY((windowHeight - totalHeight) * 0.5f); // Center vertically

      // Center the logo
      ImGui.setCursorPosX((windowWidth - 256) * 0.5f);
      ImGui.image(logoTextureID, 256, 356);
      ImGui.setCursorPosX((windowWidth - ImGui.calcTextSize("Quake One-Click Installer Menu").x) * 0.5f);

      ImGui.text("Quake One-Click Installer Menu");

      float buttonWidth = 200; // Fixed button width
      ImGui.setCursorPosX((windowWidth - buttonWidth) * 0.5f);
      if (ImGui.button("Install handler", new ImVec2(buttonWidth, 0))) {
        handlerOpen = true;
      }

      ImGui.setCursorPosX((windowWidth - buttonWidth) * 0.5f);
      ImGui.button("Set Quake 1 Paths", new ImVec2(buttonWidth, 0));

      ImGui.setCursorPosX((windowWidth - buttonWidth) * 0.5f);
      ImGui.button("Set Quake 2 Paths", new ImVec2(buttonWidth, 0));

      ImGui.setCursorPosX((windowWidth - buttonWidth) * 0.5f);
      ImGui.button("Set default skill", new ImVec2(buttonWidth, 0));

      ImGui.setCursorPosX((windowWidth - buttonWidth) * 0.5f);
      if (ImGui.button("Clear cache", new ImVec2(buttonWidth, 0))) {
        cacheOpen = true;
      }

      ImGui.setCursorPosX((windowWidth - buttonWidth) * 0.5f);
      if (ImGui.button("Exit", new ImVec2(buttonWidth, 0))) {
         dispose(); // Replace with your exit logic
        System.exit(0);
      }

      ImGui.end();

    }

    if (handlerOpen) {
      String text = handlerInstaller.textPrompt();
      openConfirm("Install Handler", text, "Install",
          () -> {
            handlerInstaller.install();
            handlerOpen = false;
          },
          () -> handlerOpen = false);
    }

    if (cacheOpen) {
      String text = "Cache currently sized at " + menuOperations.getCacheSize()
          + ".\nWould you like to clear it?";
      openConfirm("Clear Cache", text, "Clear Cache", () -> {
        menuOperations.clearCache();
        cacheOpen = false;
      }, () -> cacheOpen = false);
    }
  }

  private void openConfirm(String title, String text, String confirmText, Runnable confirmAction,
      Runnable cancelAction) {

    ImGuiViewport mainViewport = ImGui.getMainViewport();
    ImVec2 screenMidpoint = mainViewport.getSize().div(2, 2);
    float textWidth = Math.max(Math.min(ImGui.calcTextSize(text).x, 450), 300);
    float textHeight = ImGui.calcTextSize(text).y;

    ImGui.setNextWindowPos(Math.max(screenMidpoint.x - ((textWidth * 1.1F) * 0.5F), 0),
        screenMidpoint.y - ((textHeight * 6) * 0.5F), ImGuiCond.Always);
    ImGui.setNextWindowSize(Math.min(textWidth * 1.1F, mainViewport.getSize().x), 0,
        ImGuiCond.Always);
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
      }

      ImGui.sameLine(); // Place the next item on the same line
      if (ImGui.button(confirmText, new ImVec2(buttonWidth, 20))) {
        confirmAction.run();
      }

    }
    ImGui.end();
  }

  private int logoTextureID = 0;
  private int logoImageWidth = 0;
  private int logoImageHeight = 0;

  private boolean loadImage(String resourcePath) {
    try (MemoryStack stack = MemoryStack.stackPush()) {
      InputStream inputStream = getClass().getClassLoader().getResourceAsStream(resourcePath);
      if (inputStream == null) {
        System.err.println("Failed to load resource: " + resourcePath);
        return false;
      }

      byte[] bytes = inputStream.readAllBytes();
      ByteBuffer buffer = ByteBuffer.allocateDirect(bytes.length);
      buffer.put(bytes);
      buffer.flip();

      IntBuffer w = stack.mallocInt(1);
      IntBuffer h = stack.mallocInt(1);
      IntBuffer channels = stack.mallocInt(1);

      ByteBuffer image = STBImage.stbi_load_from_memory(buffer, w, h, channels, 4);

      if (image == null) {
        System.err.println("Failed to decode image: " + STBImage.stbi_failure_reason());
        return false;
      }

      logoImageWidth = w.get();
      logoImageHeight = h.get();
      glEnable(GL_BLEND);
      glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
      logoTextureID = glGenTextures();
      glBindTexture(GL_TEXTURE_2D, logoTextureID);

      glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_BORDER);
      glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_BORDER);
      glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
      glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

      glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, logoImageWidth, logoImageHeight, 0, GL_RGBA, GL_UNSIGNED_BYTE, image);
      glDisable(GL_BLEND);
      STBImage.stbi_image_free(image);
      inputStream.close();

      return true;
    } catch (IOException e) {
      System.err.println("Error loading resource: " + resourcePath);
      e.printStackTrace();
      return false;
    }
  }

}
