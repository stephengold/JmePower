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
import java.io.IOException;
import java.util.logging.Logger;
import jme3utilities.Heart;
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
            Heart.writeImage(filePath, downsampledImage);
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
}
