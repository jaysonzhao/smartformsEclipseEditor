package com.solar.jseditor.editors.additional;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import com.solar.htmleditor.HTMLPlugin;

/**
 * An abstract class for <code>IAdditionalJavaScriptCompleter</code>.
 * 
 * @author shinsuke
 */
public abstract class AbstractCompleter implements
		IAdditionalJavaScriptCompleter {

	protected Map<File, JsFileCache> jsFileCacheMap = new HashMap<File, JsFileCache>();

	private static byte[] copyFile(URL url, File file) throws IOException {
		InputStream in = url.openStream();
		OutputStream out = new FileOutputStream(file);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			byte[] buf = new byte[1024 * 8];
			int length = 0;
			while ((length = in.read(buf)) != -1) {
				out.write(buf, 0, length);
				baos.write(buf, 0, length);
			}
			return baos.toByteArray();
		} finally {
			try {
				in.close();
			} catch (Exception e) {
			}
			try {
				out.close();
			} catch (Exception e) {
			}
		}
	}

	protected JsFileCache getJsFileCache(String path) throws IOException {
		File baseDir = HTMLPlugin.getDefault().getStateLocation().toFile();
		File file = new File(baseDir, path);

		JsFileCache jsFileCache = jsFileCacheMap.get(file);
		if (jsFileCache != null && file.isFile()
				&& jsFileCache.getLastModified() >= file.lastModified()) {
			return jsFileCache;
		}

		File parentDir = file.getParentFile();
		parentDir.mkdirs();

		URL url = HTMLPlugin.getDefault().getBundle().getEntry(path);
		byte[] bytes = copyFile(url, file);
		jsFileCache = new JsFileCache(file, bytes);
		jsFileCacheMap.put(file, jsFileCache);
		return jsFileCache;
	}

	protected static class JsFileCache {
		protected byte[] bytes;
		protected File file;
		protected long lastModified;

		protected JsFileCache(File file, byte[] bytes) {
			this.file = file;
			this.bytes = bytes;
			this.lastModified = file.lastModified();
		}

		public byte[] getBytes() {
			return bytes;
		}

		public File getFile() {
			return file;
		}

		public long getLastModified() {
			return lastModified;
		}
	}

}