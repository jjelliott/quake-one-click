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
import io.github.jjelliott.q1installer.gui.ConfirmWindow;
import io.github.jjelliott.q1installer.gui.DropdownWindow;
import io.github.jjelliott.q1installer.gui.Image;
import io.github.jjelliott.q1installer.gui.PathsWindow;
import io.github.jjelliott.q1installer.os.ExamplePath;
import io.github.jjelliott.q1installer.os.HandlerInstaller;
import jakarta.inject.Singleton;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.List;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

@Singleton
public class Gui extends Application {

  private final ConfirmWindow handlerWindow;
  private final PathsWindow quakePathWindow;
  private final PathsWindow quake2PathWindow;
  private final ConfirmWindow cacheWindow;
  private final DropdownWindow skillWindow;
  private float windowWidth;
  private final float buttonWidth = 200;
  private Image logoImage;

  public Gui(UserProps userProps, HandlerInstaller handlerInstaller,
      ExamplePath examplePath, MenuOperations menuOperations) {
    this.handlerWindow = new ConfirmWindow("Install Handler", handlerInstaller.textPrompt(),
        "Install",
        handlerInstaller::install);
    this.cacheWindow = new ConfirmWindow("Clear Cache",
        () -> "Cache currently sized at " + menuOperations.getCacheSize()
            + ".\nWould you like to clear it?", "Clear Cache", menuOperations::clearCache);
    this.quakePathWindow = new PathsWindow(Game.QUAKE, userProps, examplePath);
    this.quake2PathWindow = new PathsWindow(Game.QUAKE2, userProps, examplePath);
    this.skillWindow = new DropdownWindow("Default Skill",
        "What skill would you like to launch?",
        List.of("Ask every time", "Easy", "Normal", "Hard", "Nightmare"),
        choice -> userProps.setSkill(choice - 1),
        () -> userProps.getSkill() + 1);
  }

  @Override
  protected void configure(Configuration config) {
    config.setTitle("Quake One-Click Installer");
    config.setWidth(550);
    config.setHeight(650);
  }

  @Override
  protected void preRun() {
    setWindowIconFromClasspath(handle, "q1c.png");
    logoImage = new Image("q1c.png");
    super.preRun();
  }

  public void setWindowIconFromClasspath(long windowHandle, String resourcePath) {
    try (MemoryStack stack = MemoryStack.stackPush()) {
      InputStream inputStream = getClass().getClassLoader().getResourceAsStream(resourcePath);
      if (inputStream == null) {
        System.err.println("Failed to load icon from classpath: " + resourcePath);
        return;
      }

      byte[] bytes = inputStream.readAllBytes();
      ByteBuffer buffer = ByteBuffer.allocateDirect(bytes.length);
      buffer.put(bytes);
      buffer.flip();

      IntBuffer w = stack.mallocInt(1);
      IntBuffer h = stack.mallocInt(1);
      IntBuffer channels = stack.mallocInt(1);

      ByteBuffer imageBuffer = STBImage.stbi_load_from_memory(buffer, w, h, channels, 4);
      if (imageBuffer == null) {
        System.err.println("Failed to decode icon: " + STBImage.stbi_failure_reason());
        return;
      }

      int width = w.get();
      int height = h.get();

      GLFWImage.Buffer iconBuffer = GLFWImage.malloc(1);

      iconBuffer.width(width);
      iconBuffer.height(height);
      iconBuffer.pixels(imageBuffer);

      GLFW.glfwSetWindowIcon(windowHandle, iconBuffer);

      STBImage.stbi_image_free(imageBuffer);
      inputStream.close();

    } catch (IOException e) {
      System.err.println("Error loading icon from classpath: " + resourcePath);
      e.printStackTrace();
    }
  }

  @Override
  public void process() {
    ImGui.getStyle().setColor(ImGuiCol.WindowBg, 0.25f, 0.25f, 0.3f, 1f);
    ImGui.getStyle().setColor(ImGuiCol.Border, 0.3f, 0.3f, 0.25f, 1f);
    ImGui.getStyle().setColor(ImGuiCol.Separator, 0.3f, 0.3f, 0.25f, 1f);
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
      windowWidth = ImGui.getWindowWidth();
      float windowHeight = ImGui.getWindowHeight();
      float spacing = ImGui.getStyle().getItemSpacing().y;

      float totalHeight =
          356 + ImGui.getTextLineHeightWithSpacing() + (ImGui.getFrameHeightWithSpacing() * 6) + (
              spacing * 7); // Calculate total height of elements

      ImGui.setCursorPosY((windowHeight - totalHeight) * 0.5f); // Center vertically

      // Center the logo
      centerImage(logoImage, 356);
      centerText("Quake One-Click Installer Menu");

      button("Install handler", handlerWindow::open);
      button("Set Quake 1 Paths", quakePathWindow::open);
      button("Set Quake 2 Paths", quake2PathWindow::open);
      button("Set default skill", skillWindow::open);
      button("Clear cache", cacheWindow::open);
      button("Exit", () -> {
        dispose(); // Replace with your exit logic
        System.exit(0);
      });

      ImGui.end();

    }

    ImGui.pushStyleColor(ImGuiCol.WindowBg, .1f, .1f, .15f, .9f);

    handlerWindow.render();
    quakePathWindow.render();
    quake2PathWindow.render();
    skillWindow.render();
    cacheWindow.render();

    ImGui.popStyleColor();
  }

  private void centerImage(Image image, int squareSize) {
    centerImage(image, squareSize, squareSize);
  }

  private void centerImage(Image image, int sizeX, int sizeY) {
    ImGui.setCursorPosX((windowWidth - sizeX) * 0.5f);
    ImGui.image(image.textureId(), sizeX, sizeY);
  }

  private void centerText(String text) {
    ImGui.setCursorPosX(
        (windowWidth - ImGui.calcTextSize(text).x) * 0.5f);

    ImGui.text(text);
  }

  private void button(String text, Runnable action) {
    ImGui.setCursorPosX((windowWidth - buttonWidth) * 0.5f);
    if (ImGui.button(text, new ImVec2(buttonWidth, 0))) {
      action.run();
    }
  }


}
