package uk.ac.mdx.xmf.swt.model;

import java.io.File;
import java.util.Enumeration;
import java.util.Hashtable;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;

public class ImageManager {

	private static String graphicsDir = "images";

	// private static Vector images = new Vector();

	private static Hashtable images = new Hashtable();

	public static void dispose() {
		Enumeration e = images.keys();
		while (e.hasMoreElements()) {
			Image image = (Image) e.nextElement();
			image.dispose();
		}
	}

	public static Image getImage(String filename) {

		// apply any redirects to the filename

		// filename = FileRedirector.getRedirect(filename);

		// test to see whether the name is absolute or relative

		File file = new File(filename);
		// if (file.exists())
		return getImageFromFileAbsolute(filename);
		// else
		// return getImageFromFileRelative( filename);
	}

	private static Image getImageFromFileAbsolute(String filename) {
		Image image = ImageDescriptor.createFromFile(null, filename)
				.createImage();
		images.put(image, filename);
		return image;
	}

	private static Image getImageFromFileRelative(String filename) {
		// String iconPath = graphicsDir + "/";
		// try {
		// URL installURL = plugin.getBundle().getEntry("/");
		// URL url = new URL(installURL, iconPath + filename);
		// Image image = ImageDescriptor.createFromURL(url).createImage(true);
		// images.put(image, filename);
		// return image;
		// // }
		// } catch (MalformedURLException e) {
		// return ImageDescriptor.getMissingImageDescriptor()
		// .createImage(true);
		// }
		return null;
	}

	public static Image resizeImage(Image image, int width, int height) {
		String filename = (String) images.remove(image);
		image.dispose();
		Image i = getImage(filename);
		ImageData data = i.getImageData().scaledTo(width, height);
		i.dispose();
		Image newi = new Image(null, data);
		images.put(newi, filename);
		return newi;

		/*
		 * ImageData data = image.getImageData().scaledTo(width,height);
		 * images.removeElement(image); image.dispose(); Image resizedimage =
		 * new Image(null,data); images.addElement(resizedimage); return
		 * resizedimage;
		 */
	}

}