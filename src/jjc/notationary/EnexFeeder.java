package jjc.notationary;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

/**
 * EnexFeeder converts a standard text file (one note on each line) into ENEX
 * Evernote format.
 * <p>
 * Note: Ampersands will be converted to the word "and" as Evernote may not
 * support them well.
 * 
 * @author John
 *
 */
public class EnexFeeder {

	static private SimpleDateFormat ZULU = new SimpleDateFormat(
			"yyyyMMdd'T'HHmmss'Z'");

	static private String XML_HEADER = "<?xml version='1.0' encoding='UTF-8'?>\n";
	static private String XML_DOCTYPE = "<!DOCTYPE en-export SYSTEM 'http://xml.evernote.com/pub/evernote-export.dtd'>\n";
	static private String XML_EXPORT_A = "<en-export export-date='";
	static private String XML_EXPORT_B = "' application='Evernote/Windows' version='4.x'>\n";
	static private String XML_CONTENT_A = " <content><![CDATA[<?xml version='1.0' encoding='UTF-8'?><!DOCTYPE en-note SYSTEM 'http://xml.evernote.com/pub/enml2.dtd'><en-note>";
	static private String XML_CONTENT_B = "</en-note>]]></content>\n";

	static private Date Date = new Date();
	static private String Zulu;

	static private int TITLE_MAX = 80;

	static private String IGNORE = "Readme.txt";
	static private String TEXT_EXTENSION = ".txt";
	static private String ENEX_EXTENSION = ".enex";
	static private String OUT_EXTENSION = ".out";

	private static String getContent(ArrayList<String> list) {
		StringBuilder out = new StringBuilder();
		out.append(XML_HEADER);
		out.append(XML_DOCTYPE);
		out.append(XML_EXPORT_A + Zulu + XML_EXPORT_B);

		String title;
		for (String line : list) {
			title = line;
			if (title.length() > 80) title = title.substring(0, TITLE_MAX);

			out.append("<note>\n");
			out.append(" <title>" + title + "</title>\n");
			out.append(XML_CONTENT_A + line + XML_CONTENT_B);
			out.append("</note>\n\n");
		}
		out.append("</en-export>\n");
		return out.toString();
	}

	static {
		ZULU.setTimeZone(TimeZone.getTimeZone("UTC"));
		Zulu = ZULU.format(Date);
	}

	public static void main(String[] args) throws IOException {
		execute();
	}

	static private void execute() throws IOException {
		File root = new File(".");
		File[] files = root.listFiles();
		for (File file : files) {
			if (file.isFile()) {
				String filename = file.getName();
				if (Filer.hasExtension(filename, TEXT_EXTENSION)) {
					if (!filename.equals(IGNORE)) {
						process(file);
					}
				}
			}
		}
	}

	private static void process(File file) throws IOException {
		// Process input
		String filename = file.getName();
		String handle = Filer.stripExtension(filename);

		// Read input file to create list of notes
		ArrayList<String> notes = Filer.read(file);

		// If the enex extension was there, add "out" to disambiguate
		if (Filer.hasExtension(filename, ENEX_EXTENSION)) handle += OUT_EXTENSION;

		// Convert to the ENEX format
		String content = getContent(notes);
		content = content.replaceAll("&&", "AND");
		content = content.replaceAll("&", "and");

		// Write to output file
		Filer.write(content, new File(handle + ENEX_EXTENSION));

	}
}
