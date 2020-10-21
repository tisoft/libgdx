
package com.badlogic.gdx.backends.korge;

import java.awt.Point;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class LwjglCursor implements Cursor {
	java.awt.Cursor awtCursor;

	public LwjglCursor (Pixmap pixmap, int xHotspot, int yHotspot) {
			if (pixmap == null) {
				awtCursor = null;
				return;
			}

			if (pixmap.getFormat() != Pixmap.Format.RGBA8888) {
				throw new GdxRuntimeException("Cursor image pixmap is not in RGBA8888 format.");
			}

			if ((pixmap.getWidth() & (pixmap.getWidth() - 1)) != 0) {
				throw new GdxRuntimeException("Cursor image pixmap width of " + pixmap.getWidth()
					+ " is not a power-of-two greater than zero.");
			}

			if ((pixmap.getHeight() & (pixmap.getHeight() - 1)) != 0) {
				throw new GdxRuntimeException("Cursor image pixmap height of " + pixmap.getHeight()
					+ " is not a power-of-two greater than zero.");
			}

			if (xHotspot < 0 || xHotspot >= pixmap.getWidth()) {
				throw new GdxRuntimeException("xHotspot coordinate of " + xHotspot + " is not within image width bounds: [0, "
					+ pixmap.getWidth() + ").");
			}

			if (yHotspot < 0 || yHotspot >= pixmap.getHeight()) {
				throw new GdxRuntimeException("yHotspot coordinate of " + yHotspot + " is not within image height bounds: [0, "
					+ pixmap.getHeight() + ").");
			}

			// Convert from RGBA8888 to ARGB8888 and flip vertically
			IntBuffer pixelBuffer = pixmap.getPixels().asIntBuffer();
			int[] pixelsRGBA = new int[pixelBuffer.capacity()];
			pixelBuffer.get(pixelsRGBA);
			int[] pixelsARGB = new int[pixelBuffer.capacity()];
			int pixel;
			if (pixelBuffer.order() == ByteOrder.BIG_ENDIAN) {
				for(int i=0;i<pixelsRGBA.length;i++) {
						pixel = pixelsRGBA[i];
						pixelsARGB[i] = ((pixel >> 8) & 0x00FFFFFF)
							| ((pixel << 24) & 0xFF000000);
				}
			} else {
				for(int i=0;i<pixelsRGBA.length;i++) {
						pixel = pixelsRGBA[i];
						pixelsARGB[i] = ((pixel & 0xFF) << 16)
							| ((pixel & 0xFF0000) >> 16) | (pixel & 0xFF00FF00);
				}
			}

		BufferedImage bi = new BufferedImage( pixmap.getWidth(), pixmap.getHeight(), BufferedImage.TYPE_INT_ARGB );
		final int[] a = ( (DataBufferInt) bi.getRaster().getDataBuffer() ).getData();
		System.arraycopy(pixelsARGB, 0, a, 0, pixelsARGB.length);

		awtCursor = Toolkit.getDefaultToolkit().createCustomCursor(bi, new Point(xHotspot, yHotspot), null);
	}

	@Override
	public void dispose () {
	}
}
