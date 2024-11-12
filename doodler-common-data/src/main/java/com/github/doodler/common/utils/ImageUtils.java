package com.github.doodler.common.utils;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Base64;
import java.util.Base64.Encoder;

import javax.imageio.ImageIO;

import org.apache.commons.io.IOUtils;
import org.springframework.util.Assert;

import lombok.experimental.UtilityClass;

/**
 * @Description: ImageUtils
 * @Author: Fred Feng
 * @Date: 11/12/2022
 * @Version 1.0.0
 */
@UtilityClass
public class ImageUtils {

	public void rewrite(URL url, int width, int height, String format, OutputStream output) throws IOException {
		rewrite(ImageIO.read(url), width, height, format, output);
	}

	public void rewrite(InputStream src, int width, int height, String format, OutputStream output) throws IOException {
		rewrite(ImageIO.read(src), width, height, format, output);
	}

	public void rewrite(BufferedImage src, int width, int height, String format, OutputStream output) throws IOException {
		Assert.notNull(src, "Unspecified image source.");
		Assert.hasText(format, "Unspecified image format");
		BufferedImage copy = new BufferedImage(width, height, src.getType());
		copy.getGraphics().drawImage(src, 0, 0, width, height, null);
		ImageIO.write(copy, format, output);
	}

	public byte[] toByteArray(BufferedImage src, String format) throws IOException {
		Assert.notNull(src, "Unspecified image source.");
		Assert.hasText(format, "Unspecified image format");
		ByteArrayOutputStream bos = null;
		try {
			bos = new ByteArrayOutputStream();
			ImageIO.write(src, format, bos);
		} finally {
			IOUtils.closeQuietly(bos);
		}
		return bos.toByteArray();
	}

	public byte[] toByteArray(InputStream src, String format) throws IOException {
		Assert.notNull(src, "Unspecified image source.");
		return toByteArray(ImageIO.read(src), format);
	}

	public byte[] toByteArray(URL url, String format) throws IOException {
		Assert.notNull(url, "Unspecified image source.");
		return toByteArray(ImageIO.read(url), format);
	}

	public String toBase64(BufferedImage src, String format) throws IOException {
		byte[] bytes = toByteArray(src, format);
		Encoder encoder = Base64.getEncoder();
		String img = encoder.encodeToString(bytes);
		img = "data:image/" + format.toLowerCase() + ";base64," + img;
		return img;
	}

	public String toBase64(InputStream src, String format) throws IOException {
		Assert.isNull(src, "Unspecified image source.");
		return toBase64(ImageIO.read(src), format);
	}

	public String toBase64(URL src, String format) throws IOException {
		Assert.isNull(src, "Unspecified image source.");
		return toBase64(ImageIO.read(src), format);
	}
}