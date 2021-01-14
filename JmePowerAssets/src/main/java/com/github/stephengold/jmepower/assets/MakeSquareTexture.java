/*
 Copyright (c) 2020-2021, Stephen Gold
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright
 notice, this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright
 notice, this list of conditions and the following disclaimer in the
 documentation and/or other materials provided with the distribution.
 * Neither the name of the copyright holder nor the names of its contributors
 may be used to endorse or promote products derived from this software without
 specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.github.stephengold.jmepower.assets;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import jme3utilities.MyString;
import jme3utilities.Validate;
import org.imgscalr.Scalr;

/**
 * A console application to generate square textures.
 *
 * @author Stephen Gold sgold@sonic.net
 */
public class MakeSquareTexture {
    // *************************************************************************
    // constants and loggers

    /**
     * message logger for this class
     */
    final private static Logger logger
            = Logger.getLogger(MakeSquareTexture.class.getName());
    /**
     * filesystem path to the asset directory/folder for output
     */
    final private static String assetDirPath
            = "../LemurPower/src/main/resources/Textures/JmePower";
    // *************************************************************************
    // fields

    /**
     * working image
     */
    private BufferedImage image;
    /**
     * graphics context
     */
    private Graphics2D graphics;
    /**
     * size of the texture (pixels per side)
     */
    final protected int textureSize;
    // *************************************************************************
    // constructors

    /**
     * Instantiate an application for the specified texture size.
     *
     * @param textureSize (pixels per side, &gt;0)
     */
    protected MakeSquareTexture(int textureSize) {
        this.textureSize = textureSize;
    }
    // *************************************************************************
    // new protected methods

    /**
     * Create a blank, color-buffered, opaque image for the texture map.
     *
     * @param color the desired fill color (not null)
     * @return a new graphics context (not null)
     */
    protected Graphics2D createOpaque(Color color) {
        Validate.nonNull(color, "color");

        image = new BufferedImage(textureSize, textureSize,
                BufferedImage.TYPE_3BYTE_BGR);

        graphics = image.createGraphics();
        graphics.setColor(color);
        graphics.fillRect(0, 0, textureSize, textureSize);

        return graphics;
    }

    /**
     * Downsample the image to the desired final size and write it to a file.
     *
     * @param finalSize the desired image size (pixels per side, &gt;0)
     * @param fileName the name of the file to create (not null, not empty)
     */
    protected void downsampleAndWrite(int finalSize, String fileName) {
        BufferedImage downsampledImage = Scalr.resize(image,
                Scalr.Method.ULTRA_QUALITY, Scalr.Mode.AUTOMATIC, finalSize,
                finalSize, Scalr.OP_ANTIALIAS);

        String filePath = String.format("%s/%s", assetDirPath, fileName);
        try {
            writeImage(filePath, downsampledImage);
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    /**
     * Draw a string of text centered at the specified X coordinate.
     *
     * @param text the text to draw (not null)
     * @param centerX the X coordinate for the center (&ge;0, &le;1)
     * @param baseY the Y coordinate for the baseline (&ge;0, &le;1)
     */
    protected void drawString(String text, double centerX, double baseY) {
        FontMetrics fontMetrics = graphics.getFontMetrics();
        int width = fontMetrics.stringWidth(text);

        int x = (int) Math.round(textureSize * centerX - width / 2.0);
        int y = (int) Math.round(textureSize * baseY);
        graphics.drawString(text, x, y);
    }
    // *************************************************************************
    // private methods

    /**
     * Write an image to a file, attempting to overwrite any pre-existing file.
     * TODO use the Heart library
     *
     * @param filePath the path to the output file (not null, not empty)
     * @param image the image to be written (not null)
     * @throws IOException if the file cannot be written
     */
    private static void writeImage(String filePath, RenderedImage image)
            throws IOException {
        Validate.nonEmpty(filePath, "path");
        Validate.nonNull(image, "image");
        /*
         * Determine the output format based on the filename
         * or else default to PNG.
         */
        String formatName = "png";
        String lowerCase = filePath.toLowerCase();
        if (lowerCase.endsWith(".bmp")) {
            formatName = "bmp";
        } else if (lowerCase.endsWith(".gif")) {
            formatName = "gif";
        } else if (lowerCase.endsWith(".jpg") || lowerCase.endsWith(".jpeg")) {
            formatName = "jpeg";
        }
        // TODO write MicroSoft's DDS file format as well
        /*
         * ImageIO fails silently when asked to write alpha to a BMP.
         * It throws an IIOException when asked to write alpha to a JPEG.
         */
        boolean hasAlpha = image.getColorModel().hasAlpha();
        if (hasAlpha
                && (formatName.equals("bmp") || formatName.equals("jpeg"))) {
            logger.log(Level.SEVERE, "unable to write alpha channel to a {0}",
                    formatName.toUpperCase());
        }

        File textureFile = new File(filePath);
        try {
            /*
             * If a parent directory/folder is needed, create it.
             */
            File parentDirectory = textureFile.getParentFile();
            if (parentDirectory != null && !parentDirectory.exists()) {
                boolean success = parentDirectory.mkdirs();
                if (!success) {
                    throw new IOException();
                }
            }

            ImageIO.write(image, formatName, textureFile);
            logger.log(Level.INFO, "wrote texture to {0}",
                    MyString.quote(filePath));

        } catch (IOException exception) {
            logger.log(Level.SEVERE, "write to {0} failed",
                    MyString.quote(filePath));
            boolean success = textureFile.delete();
            if (success) {
                logger.log(Level.INFO, "deleted file {0}",
                        MyString.quote(filePath));
            } else {
                logger.log(Level.SEVERE, "delete of {0} failed",
                        MyString.quote(filePath));
            }
            throw exception;
        }
    }
}
