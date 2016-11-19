package jjc.notationary;

import java.io.File;
import java.io.IOException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * EnexRetitler reads a file exported from Evernote in ENEX format. Title nodes
 * are updated to match the beginning of the note content.
 * <p>
 * Note: Ampersands will be converted to the word "and" as Evernote may not
 * support them well.
 * 
 * @author John
 *
 */
public class EnexRetitler {

	static private String IGNORE = "Readme.txt";
	static private String ENEX_EXTENSION = ".enex";
	static private String RETITLE_TAG = " (retitled)";
	static private String NOTE_ELEMENT = "note";
	static private String CONTENT_ELEMENT = "content";
	static private String TITLE_ELEMENT = "title";
	static private String EN_NOTE_OPEN = "<en-note>";
	static private String EN_NOTE_CLOSE = "</en-note>";
	static private String HTML_BREAK = "<br/>";
	static private int TITLE_MAX = 80;

	public static void main(String[] args) throws IOException {
		execute();
	}

	static private void execute() throws IOException {
		File root = new File(".");
		File[] files = root.listFiles();
		for (File file : files) {
			if (file.isFile()) {
				String filename = file.getName();
				if (Filer.hasExtension(filename, ENEX_EXTENSION)) {
					if (!filename.equals(IGNORE)) {
						process(file);
					}
				}
			}
		}
	}

	private static void process(File infile) throws IOException {
		String filename = infile.getName();
		String handle = Filer.stripExtension(filename);

		Document enex = Filer.parseXmlFile(infile);
		NodeList notes = enex.getElementsByTagName(NOTE_ELEMENT);
		int count = notes.getLength();
		for (int i = 0; i < count; i++) {
			Node note = notes.item(i);
			if (note.getNodeType() == Node.ELEMENT_NODE) {
				Element eElement = (Element) note;
				Node content = eElement.getElementsByTagName(CONTENT_ELEMENT).item(0);
				String text = getTitle(content.getTextContent());
				Node title = eElement.getElementsByTagName(TITLE_ELEMENT).item(0);
				title.setTextContent(text);
			}
		}

		handle += RETITLE_TAG;
		Filer.writeXmlFile(enex, new File(handle + ENEX_EXTENSION));
	}

	static private String getTitle(String content) {
		// Capture <en-note> element
		content = content.replaceAll("\n", "");

		content = content.replaceAll(".*" + EN_NOTE_OPEN, "");
		content = content.replaceAll(EN_NOTE_CLOSE + ".*", "");

		// Remove html tags like <div> and <a>
		content = content.replaceAll(HTML_BREAK, " ");
		content = content.replaceAll("<[^>]*>", "");

		// Remove ampersands
		content = content.replaceAll("&&", "AND");
		content = content.replaceAll("&", "and");

		// Capture good characters, only up to certain length
		StringBuilder out = new StringBuilder();
		char[] chars = content.toCharArray();
		int count = chars.length;
		for (int i = 0; i < count && i < TITLE_MAX; i++) {
			if (chars[i] >= 0x20 && chars[i] <= 0x7e) out.append(chars[i]);
		}
		System.out.println(out);// DEBUG
		return out.toString();
	}
}
