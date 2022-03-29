/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lwjgl;

import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import java.nio.*;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class LWJGLSample {

    // The window handle
    private long window;

    public void run() {
        System.out.println("LWJGL Sample" + Version.getVersion() + "!");

        init();
        loop();

        // Free the window callbacks and destroy the window
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);

        // Terminate GLFW and free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    private void init() {
        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        // Configure GLFW
        glfwDefaultWindowHints(); // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable

        // Create the window
        window = glfwCreateWindow(400, 400, "Cube with LWJGL", NULL, NULL);
        if (window == NULL) {
            throw new RuntimeException("Failed to create the GLFW window");
        }

        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
        glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
                glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
            }
        });

        // Get the thread stack and push a new frame
        try ( MemoryStack stack = stackPush()) {
            IntBuffer pWidth = stack.mallocInt(1); // int*
            IntBuffer pHeight = stack.mallocInt(1); // int*

            // Get the window size passed to glfwCreateWindow
            glfwGetWindowSize(window, pWidth, pHeight);

            // Get the resolution of the primary monitor
            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            // Center the window
            glfwSetWindowPos(
                    window,
                    (vidmode.width() - pWidth.get(0)) / 2,
                    (vidmode.height() - pHeight.get(0)) / 2
            );
        } // the stack frame is popped automatically

        // Make the OpenGL context current
        glfwMakeContextCurrent(window);
        // Enable v-sync
        glfwSwapInterval(1);

        // Make the window visible
        glfwShowWindow(window);
    }

    private void loop() {
        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.

        GL.createCapabilities();

        float rotAngle = 0.1f;

// Set the clear color
        glClearColor(1.0f, 1.0f, 1.0f, 0.0f);

        // Run the rendering loop until the user has attempted to close
        // the window or has pressed the ESCAPE key.
        while (!glfwWindowShouldClose(window)) {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

            glfwSwapBuffers(window); // swap the color buffers

            // Poll for window events. The key callback above will only be
            // invoked during this call.
            glfwPollEvents();

            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);

            glMatrixMode(GL_PROJECTION);
            glLoadIdentity();         // Reset the model-view matrix
            glTranslatef(0.0f, 0.0f, 0.0f);

            glMatrixMode(GL_MODELVIEW);

            GL11.glRotatef(rotAngle, 1.0f, 1.0f, 0.0f);
            glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
            // Draw the side faces

            glBegin(GL_QUADS);                // Begin drawing the color cube with 6 quads
            // Top face (y = 1.0f)
            // Define vertices in counter-clockwise (CCW) order with normal pointing out
            glColor3f(0.0f, 1.0f, 0.0f);     // Green
            glVertex3f(0.2f, 0.2f, -0.2f);
            glVertex3f(-0.2f, 0.2f, -0.2f);
            glVertex3f(-0.2f, 0.2f, 0.2f);
            glVertex3f(0.2f, 0.2f, 0.2f);

            // Bottom face (y = -1.0f)
            glColor3f(1.0f, 0.5f, 0.0f);     // Orange
            glVertex3f(0.2f, -0.2f, 0.2f);
            glVertex3f(-0.2f, -0.2f, 0.2f);
            glVertex3f(-0.2f, -0.2f, -0.2f);
            glVertex3f(0.2f, -0.2f, -0.2f);

            // Front face  (z = 1.0f)
            glColor3f(1.0f, 0.0f, 0.0f);     // Red
            glVertex3f(0.2f, 0.2f, 0.2f);
            glVertex3f(-0.2f, 0.2f, 0.2f);
            glVertex3f(-0.2f, -0.2f, 0.2f);
            glVertex3f(0.2f, -0.2f, 0.2f);

            // Back face (z = -1.0f)
            glColor3f(0.0f, 1.0f, 1.0f);     // Yellow
            glVertex3f(0.2f, -0.2f, -0.2f);
            glVertex3f(-0.2f, -0.2f, -0.2f);
            glVertex3f(-0.2f, 0.2f, -0.2f);
            glVertex3f(0.2f, 0.2f, -0.2f);

            // Left face (x = -1.0f)
            glColor3f(0.0f, 0.0f, 1.0f);     // Blue
            glVertex3f(-0.2f, 0.2f, 0.2f);
            glVertex3f(-0.2f, 0.2f, -0.2f);
            glVertex3f(-0.2f, -0.2f, -0.2f);
            glVertex3f(-0.2f, -0.2f, 0.2f);

            // Right face (x = 1.0f)
            glColor3f(1.0f, 0.0f, 1.0f);     // Magenta
            glVertex3f(0.2f, 0.2f, -0.2f);
            glVertex3f(0.2f, 0.2f, 0.2f);
            glVertex3f(0.2f, -0.2f, 0.2f);
            glVertex3f(0.2f, -0.2f, -0.2f);
            glEnd();  // End of drawing color-cube								// Done Dr
            rotAngle = 1.0f;

            glfwSwapBuffers(window); // swap the color buffers
            // Poll for window events. The key callback above will only be
            // invoked during this call.
            glfwPollEvents();

        }
    }

    public static void main(String[] args) {
        new LWJGLSample().run();
    }

}
