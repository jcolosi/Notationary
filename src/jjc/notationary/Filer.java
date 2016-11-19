package jjc.notationary;

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * This is a copy of jjc.util.Filer with features for Notationary. Don't edit
 * this one.
 * 
 * @author John
 *
 */
public class Filer {

	/**
	 * Determine if the given filename has the given extension.
	 * 
	 * @param toMatch The String to match
	 * @param ext The extension. If you want to match the "dot", then pass it. No
	 *          dot is assumed here.
	 * @return TRUE iff toMatch ends in ext
	 */
	static public boolean hasExtension(String toMatch, String ext) {
		Pattern pattern = Pattern.compile(".*(?i)" + ext + "$");
		return pattern.matcher(toMatch).matches();
	}

	/**
	 * Parse an XML document and return the DOM object tree
	 * 
	 * @return A DOM object tree
	 */
	static public Document parseXmlFile(File infile) {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

		try {
			DocumentBuilder db = dbf.newDocumentBuilder();
			return db.parse(infile);
		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (SAXException se) {
			se.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		return null;
	}

	/**
	 * Read contents of a file into a String array
	 * 
	 * @param infile The file to process
	 * @return A list of String objects
	 * @throws IOException
	 */
	static public ArrayList<String> read(File infile) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(infile));

		ArrayList<String> list = new ArrayList<String>();

		String line;
		while ((line = reader.readLine()) != null) {
			line = line.trim();
			if (line.length() > 0) list.add(line);
		}

		reader.close();
		return list;
	}

	static public String read(InputStream is) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder out = new StringBuilder();

		String line;
		while ((line = reader.readLine()) != null) {
			out.append(line + "\n");
		}

		reader.close();
		return out.toString();
	}

	/**
	 * Reads an image from a file.
	 * 
	 * @param src An image file
	 * @return BufferedImage containing the image from the file
	 * @throws IOException
	 */
	static public BufferedImage readImage(File src) throws IOException {
		return ImageIO.read(src);
	}

	static public String readToString(File infile) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(infile));
		StringBuilder out = new StringBuilder();

		String line;
		while ((line = reader.readLine()) != null) {
			line = line.trim();
			if (line.length() > 0) out.append(line + '\n');
		}

		reader.close();
		return out.toString();
	}

	/**
	 * Remove the file extension.
	 * 
	 * @param filename the filename to process
	 * @return A String with the modified filename
	 */
	static public String stripExtension(String filename) {
		int index = filename.lastIndexOf('.');
		if (index < 0) return filename;
		return filename.substring(0, index);
	}

	/**
	 * Write <code> content </code> to an output file.
	 * 
	 * @param content The String object to write
	 * @param outfile The file to write to.
	 * @throws FileNotFoundException
	 */
	static public void write(String content, File outfile)
			throws FileNotFoundException {
		PrintWriter writer = new PrintWriter(outfile);
		writer.println(content);
		writer.flush();
		writer.close();
	}

	/**
	 * Writes an image to a file
	 * 
	 * @param dst The destination file
	 * @param image The Image to write to disk
	 * @throws IOException
	 */
	static public void writeImage(File dst, RenderedImage image, String format)
			throws IOException {
		ImageIO.write(image, format, dst);
	}

	/**
	 * Print a DOM object tree into an XML file
	 * 
	 * @param doc The DOM object tree
	 * @param outfile The target XML file to write to
	 */
	static public void writeXmlFile(Document doc, File outfile) {
		try {
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(outfile);
			// StreamResult result = new StreamResult(System.out);
			transformer.transform(source, result);
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}
	}
}
