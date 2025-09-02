package io.github.jjelliott.imgui;

import imgui.ImGui;
import imgui.ImVec2;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

/**
 * Utility methods for common ImGui operations.
 */
public class ImGuiUtils {

  /**
   * Centers text horizontally in the current window.
   *
   * @param text The text to center
   */
  public static void centerText(String text) {
    float windowWidth = ImGui.getWindowWidth();
    ImGui.setCursorPosX((windowWidth - ImGui.calcTextSize(text).x) * 0.5f);
    ImGui.text(text);
  }

  /**
   * Creates a centered button with the specified text and action.
   *
   * @param text The button text
   * @param buttonWidth The width of the button
   * @param action The action to run when the button is clicked
   */
  public static void centerButton(String text, float buttonWidth, Runnable action) {
    float windowWidth = ImGui.getWindowWidth();
    ImGui.setCursorPosX((windowWidth - buttonWidth) * 0.5f);
    if (ImGui.button(text, new ImVec2(buttonWidth, 0))) {
      action.run();
    }
  }

  /**
   * Centers an image horizontally in the current window.
   *
   * @param image The image to center
   * @param size The size of the image (square)
   */
  public static void centerImage(Image image, int size) {
    centerImage(image, size, size);
  }

  /**
   * Centers an image horizontally in the current window with custom dimensions.
   *
   * @param image The image to center
   * @param width The width of the image
   * @param height The height of the image
   */
  public static void centerImage(Image image, int width, int height) {
    float windowWidth = ImGui.getWindowWidth();
    ImGui.setCursorPosX((windowWidth - width) * 0.5f);
    ImGui.image(image.textureId(), width, height);
  }

  /**
   * Sets the window icon from a classpath resource.
   *
   * @param windowHandle The GLFW window handle
   * @param resourcePath The path to the icon resource
   */
  public static void setWindowIconFromClasspath(long windowHandle, String resourcePath) {
    try (MemoryStack stack = MemoryStack.stackPush()) {
      InputStream inputStream = ImGuiUtils.class.getClassLoader().getResourceAsStream(resourcePath);
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
}
