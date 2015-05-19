/*
 * ******************************************************************************
 *  * Copyright 2015 See AUTHORS file.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *   http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *  *****************************************************************************
 */

package com.uwsoft.editor;

import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.io.File;

import javax.swing.JFrame;
import javax.swing.UIManager;

import org.apache.commons.lang3.SystemUtils;

import com.badlogic.gdx.backends.jglfw.JglfwApplication;
import com.badlogic.gdx.backends.jglfw.JglfwApplicationConfiguration;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.backends.lwjgl.LwjglFrame;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;
import com.uwsoft.editor.splash.*;
import com.uwsoft.editor.splash.SplashScreen;
import com.uwsoft.editor.utils.AppConfig;
import java.awt.*;
import java.awt.event.InputEvent;

public class Main {

    private SplashStarter splash;
    private LwjglFrame mainFrame;

    public Main() {
        splash = new SplashStarter(() -> startLoadingEditor());
    }

    private void startLoadingEditor() {
        //first, kill off the splash
        splash.kill();

        Overlap2D overlap2D = new Overlap2D();
        Rectangle maximumWindowBounds = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
        double width = maximumWindowBounds.getWidth();
        double height = maximumWindowBounds.getHeight();
        if (SystemUtils.IS_OS_MAC_OSX || SystemUtils.IS_OS_MAC) {
            System.setProperty("apple.laf.useScreenMenuBar", "true");
            System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Overlap2D");
            JglfwApplicationConfiguration config = new JglfwApplicationConfiguration();
            config.width = (int) (width);
            config.height = (int) (height - height * .04);
            config.backgroundFPS = 0;
            config.title = "Overlap2D - Public Alpha v" + AppConfig.getInstance().version;
            new JglfwApplication(overlap2D, config);
        } else {
            LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
            config.title = "Overlap2D - Public Alpha v" + AppConfig.getInstance().version;
            config.fullscreen = true;
            config.resizable = false;
            config.width = (int) (width);
            config.height = (int) (height - height * .04);
            config.backgroundFPS = 0;
            mainFrame = new LwjglFrame(overlap2D, config);
            mainFrame.setExtendedState(mainFrame.getExtendedState() | JFrame.MAXIMIZED_BOTH);
            toggleVisible();
        }

    }

    public static void main(String[] argv) throws Exception {
        String input = "../art/textures";
        File file = new File(input);
        //System.out.println("path " + file.getAbsolutePath());
        String output = "style";
        String packFileName = "uiskin";
        TexturePacker.Settings settings =  new TexturePacker.Settings();
        settings.flattenPaths = true;
        TexturePacker.processIfModified(input, output, packFileName);
        processSplashScreenTextures();
        new Main();
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    }

    private static void processSplashScreenTextures() {
        String input = "../art/splash_textures";
        String output = "splash";
        String packFileName = "splash";
        TexturePacker.Settings settings =  new TexturePacker.Settings();
        settings.flattenPaths = true;
        TexturePacker.processIfModified(input, output, packFileName);
    }


    private void toggleVisible() {
        mainFrame.setVisible(!mainFrame.isVisible());
        if (mainFrame.isVisible()) {
            mainFrame.toFront();
            mainFrame.requestFocus();
            mainFrame.setAlwaysOnTop(true);
            try {
                //remember the last location of mouse
                final Point oldMouseLocation = MouseInfo.getPointerInfo().getLocation();

                //simulate a mouse click on title bar of window
                Robot robot = new Robot();
                robot.mouseMove(mainFrame.getX() + 100, mainFrame.getY() + 5);
                robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
                robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);

                //move mouse to old location
                robot.mouseMove((int) oldMouseLocation.getX(), (int) oldMouseLocation.getY());
            } catch (Exception ex) {
                //just ignore exception, or you can handle it as you want
            } finally {
                mainFrame.setAlwaysOnTop(false);
            }
        }
    }
}
