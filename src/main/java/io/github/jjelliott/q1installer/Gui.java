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
import imgui.type.ImInt;
import io.github.jjelliott.q1installer.config.UserProps;
import io.github.jjelliott.q1installer.gui.PathsWindow;
import io.github.jjelliott.q1installer.os.ExamplePath;
import io.github.jjelliott.q1installer.os.HandlerInstaller;
import jakarta.inject.Singleton;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_CLAMP_TO_BORDER;

@Singleton
public class Gui extends Application {

    private final UserProps userProps;
    private final HandlerInstaller handlerInstaller;
    private final ExamplePath examplePath;
    private final MenuOperations menuOperations;
    private boolean handlerOpen = false;
    private PathsWindow quakePathWindow;
    private PathsWindow quake2PathWindow;
    private boolean quakePathsOpen = false;
    private boolean quake2PathsOpen = false;
    private boolean skillsOpen = false;
    private final ImInt skillChoice;
    private boolean cacheOpen = false;

    public Gui(UserProps userProps, HandlerInstaller handlerInstaller,
               ExamplePath examplePath, MenuOperations menuOperations) {
        this.userProps = userProps;
        this.handlerInstaller = handlerInstaller;
        this.examplePath = examplePath;
        this.menuOperations = menuOperations;
        skillChoice = new ImInt(userProps.getSkill() + 1);
        this.quakePathWindow = new PathsWindow(Game.QUAKE, userProps, examplePath);
        this.quake2PathWindow = new PathsWindow(Game.QUAKE2, userProps, examplePath);
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
        loadImage("q1c.png");
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
//      iconBuffer.position(0).limit(1 * GLFWImage.SIZEOF);

            iconBuffer.width(width);
            iconBuffer.height(height);
            iconBuffer.pixels(imageBuffer);

            GLFW.glfwSetWindowIcon(windowHandle, iconBuffer);

            STBImage.stbi_image_free(imageBuffer);
//      iconBuffer.free();
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
            float windowWidth = ImGui.getWindowWidth();
            float windowHeight = ImGui.getWindowHeight();
            float spacing = ImGui.getStyle().getItemSpacing().y;

            float totalHeight = 356 + ImGui.getTextLineHeightWithSpacing() + (ImGui.getFrameHeightWithSpacing() * 6) + (spacing * 7); // Calculate total height of elements

            ImGui.setCursorPosY((windowHeight - totalHeight) * 0.5f); // Center vertically

            // Center the logo
            ImGui.setCursorPosX((windowWidth - 356) * 0.5f);
            ImGui.image(logoTextureID, 356, 356);
            ImGui.setCursorPosX((windowWidth - ImGui.calcTextSize("Quake One-Click Installer Menu").x) * 0.5f);

            ImGui.text("Quake One-Click Installer Menu");

            float buttonWidth = 200; // Fixed button width
            ImGui.setCursorPosX((windowWidth - buttonWidth) * 0.5f);
            if (ImGui.button("Install handler", new ImVec2(buttonWidth, 0))) {
                handlerOpen = true;
            }

            ImGui.setCursorPosX((windowWidth - buttonWidth) * 0.5f);
            if (ImGui.button("Set Quake 1 Paths", new ImVec2(buttonWidth, 0))){
                    quakePathWindow.open();
            }

            ImGui.setCursorPosX((windowWidth - buttonWidth) * 0.5f);
            if (ImGui.button("Set Quake 2 Paths", new ImVec2(buttonWidth, 0))){
                quake2PathWindow.open();
            }

            ImGui.setCursorPosX((windowWidth - buttonWidth) * 0.5f);
            if (ImGui.button("Set default skill", new ImVec2(buttonWidth, 0))) {
                skillsOpen = true;
            }

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

        ImGui.pushStyleColor(ImGuiCol.WindowBg, .1f, .1f, .15f, .9f);
        if (handlerOpen) {
            String text = handlerInstaller.textPrompt();
            openConfirm("Install Handler", text, "Install",
                    () -> {
                        handlerInstaller.install();
                        handlerOpen = false;
                    },
                    () -> handlerOpen = false);
        }
        quakePathWindow.render();
        quake2PathWindow.render();
        if (skillsOpen) {
            createConfigWindow("Default Skill",
                    "What skill would you like to launch?",
                    List.of("Ask every time", "Easy", "Normal", "Hard", "Nightmare"),
                    () -> skillsOpen = false,
                    choice -> {
                        skillsOpen = false;
                        userProps.setSkill(choice - 1);
                    }

            );
//            openChoices("Default Skill", "What skill would you like the game to launch?",
//                    List.of(new Choice("Easy", () -> {
//                        skillsOpen = false;
//                    }), new Choice("Normal", () -> {
//                        skillsOpen = false;
//                    }), new Choice("Hard", () -> {
//                        skillsOpen = false;
//                    }), new Choice("Nightmare", () -> {
//                        skillsOpen = false;
//                    }, true), new Choice("Ask Every Time", () -> {
//                        skillsOpen = false;
//                    })));
        }

        if (cacheOpen) {
            String text = "Cache currently sized at " + menuOperations.getCacheSize()
                    + ".\nWould you like to clear it?";
            openConfirm("Clear Cache", text, "Clear Cache", () -> {
                menuOperations.clearCache();
                cacheOpen = false;
            }, () -> cacheOpen = false);
        }


        ImGui.popStyleColor();
    }


    private void openConfirm(String title, String text, String confirmText, Runnable confirmAction,
                             Runnable cancelAction) {

        ImGuiViewport mainViewport = ImGui.getMainViewport();
        ImVec2 screenMidpoint = mainViewport.getSize().div(2, 2);
        float textWidth = Math.max(Math.min(ImGui.calcTextSize(text).x, 450), 300);
        float textHeight = ImGui.calcTextSize(text).y;

        ImGui.setNextWindowPos(Math.max(screenMidpoint.x - ((textWidth * 1.1F) * 0.5F), 0),
                (screenMidpoint.y * 0.85F), ImGuiCond.Always);
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

    private record Choice(String text, Runnable runnable, boolean breakAfter) {
        Choice(String text, Runnable runnable) {
            this(text, runnable, false);
        }
    }

    public void createConfigWindow(String windowTitle, String text, List<String> options, Runnable cancelAction, Consumer<Integer> confirmAction) {

        String[] optionsArray = options.toArray(new String[0]);

        ImGuiViewport mainViewport = ImGui.getMainViewport();
        ImVec2 screenMidpoint = mainViewport.getSize().div(2, 2);
        float textWidth = Math.max(Math.min(ImGui.calcTextSize(text).x, 450), 300);
        float textHeight = ImGui.calcTextSize(text).y;

        ImGui.setNextWindowPos(Math.max(screenMidpoint.x - ((textWidth * 1.1F) * 0.5F), 0),
                screenMidpoint.y * .85f, ImGuiCond.Always);
        ImGui.setNextWindowSize(Math.min(textWidth * 1.1F, mainViewport.getSize().x), 0,
                ImGuiCond.Always);
        ImGui.begin(windowTitle, null,
                ImGuiWindowFlags.NoDocking |
                        ImGuiWindowFlags.NoResize |
                        ImGuiWindowFlags.NoMove |
                        ImGuiWindowFlags.NoCollapse |
                        ImGuiWindowFlags.Modal);

        ImGui.text(text);

        ImGui.combo("##", skillChoice, optionsArray);

        ImGui.separator();

        float buttonWidth = 80;
        float spacing = ImGui.getStyle().getItemSpacing().x;
        float totalButtonWidth = (buttonWidth * 2) + spacing;
        float windowWidth = ImGui.getWindowWidth();

        ImGui.setCursorPosX((windowWidth - totalButtonWidth) * 0.5f); // Center buttons horizontally

        if (ImGui.button("Cancel", new ImVec2(buttonWidth, 0))) {
            cancelAction.run();
        }
        ImGui.sameLine();
        if (ImGui.button("Save", new ImVec2(buttonWidth, 0))) {
            confirmAction.accept(skillChoice.get());
        }

        ImGui.end();
    }

    private void openChoices(String title, String text, List<Choice> choices) {

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
            var longestButton = choices.stream().map(Choice::text).map(choiceText -> ImGui.calcTextSize(choiceText).x).max(Comparator.naturalOrder()).get();
            float windowWidth = ImGui.getWindowWidth();
            float buttonWidth = 100; // Adjust as needed
            float spacing = ImGui.getStyle().getItemSpacing().x;
            float totalWidth = (buttonWidth * choices.size());// + spacing;

            ImGui.setCursorPosX((windowWidth - totalWidth) * 0.5f);
            for (int i = 0; i < choices.size(); i++) {
                var choice = choices.get(i);
                if (ImGui.button(choice.text(), new ImVec2(buttonWidth, 20))) {
                    choice.runnable().run();
                }
//                if (!choice.breakAfter && i < choices.size() - 1)
//                    ImGui.sameLine(); // Place the next item on the same line
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
