package io.github.jjelliott.q1installer.gui;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_S;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_T;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL13.GL_CLAMP_TO_BORDER;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

public class Image {
  private int textureId = 0;
  private int imageWidth = 0;
  private int imageHeight = 0;

  public Image(String resourcePath) {
    try (MemoryStack stack = MemoryStack.stackPush()) {
      InputStream inputStream = getClass().getClassLoader().getResourceAsStream(resourcePath);
      if (inputStream == null) {
        throw new FileNotFoundException("Failed to load resource: " + resourcePath);
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
        throw new FileNotFoundException("Failed to decode image: " + STBImage.stbi_failure_reason());
      }

      imageWidth = w.get();
      imageHeight = h.get();
      glEnable(GL_BLEND);
      glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
      textureId = glGenTextures();
      glBindTexture(GL_TEXTURE_2D, textureId);

      glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_BORDER);
      glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_BORDER);
      glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
      glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

      glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, imageWidth, imageHeight, 0, GL_RGBA,
          GL_UNSIGNED_BYTE, image);
      glDisable(GL_BLEND);
      STBImage.stbi_image_free(image);
      inputStream.close();

    } catch (IOException e) {
      System.err.println("Error loading resource: " + resourcePath);
      e.printStackTrace();
    }
  }

  public int textureId() {
    return textureId;
  }

  public int imageWidth() {
    return imageWidth;
  }

  public int imageHeight () {
    return imageHeight;
  }
}
