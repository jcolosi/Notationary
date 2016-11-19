package jjc.notationary;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * AtomFeeder converts a standard text file (one note on each line) into ATOM
 * format. Unfortunately Evernote silently killed the AtomImporter link on their
 * website. So converting to ATOM is no longer very useful. Use EnexFeeder to
 * convert from text to ENEX format and import directly into the client.
 * <p>
 * Note: Ampersands will be converted to the word "and" as ATOM or Evernote does
 * not support them well.
 * 
 * @author John
 *
 */
public class AtomFeeder {

	static private SimpleDateFormat DATE_LONG = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ss.SSSZ");

	static private SimpleDateFormat DATE_SHORT = new SimpleDateFormat(
			"MMMM d, yyyy");

	static private String XML_HEADER = "<?xml version='1.0' encoding='UTF-8'?>\n";
	static private String XML_FEED1 = "<feed xmlns='http://www.w3.org/2005/Atom'\n";
	static private String XML_FEED2 = " xmlns:openSearch='http://a9.com/-/spec/opensearch/1.1/'\n";
	static private String XML_FEED3 = " xmlns:gd='http://schemas.google.com/g/2005'>\n";
	static private String XML_AUTHOR1 = "<author>\n <name>jjcolosi@gmail.com</name>\n";
	static private String XML_AUTHOR2 = " <email>jjcolosi@gmail.com</email>\n</author>\n";
	static private String XML_GENERATOR = "<generator>John's iPhone</generator>\n\n";
	static private String XML_FEED4 = "</feed>\n";

	static private Date Date = new Date();
	static private String DateLong = DATE_LONG.format(Date);
	static private String DateShort = DATE_SHORT.format(Date);

	static private String IGNORE = "AtomFeeder.Readme.txt";
	static private String TEXT_EXTENSION = ".txt";
	static private String ATOM_EXTENSION = ".xml";
	static private String OUT_EXTENSION = ".out";

	/**
	 * @param args
	 * @throws IOException
	 */
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

	private static String getContent(ArrayList<String> list) {
		StringBuilder out = new StringBuilder();
		out.append(XML_HEADER);
		out.append(XML_FEED1);
		out.append(XML_FEED2);
		out.append(XML_FEED3);
		out.append("<updated>" + DateLong + "</updated>\n");
		out.append("<title>Notes from " + DateShort + "</title>\n");
		out.append(XML_AUTHOR1);
		out.append(XML_AUTHOR2);
		out.append(XML_GENERATOR);
		for (String line : list) {
			out.append("<entry>\n <title>" + line + "</title>\n");
			out.append(" <content type='html'>" + line + "</content>\n</entry>\n\n");
		}
		out.append(XML_FEED4);
		return out.toString();
	}

	private static void process(File file) throws IOException {
		// Process input
		String filename = file.getName();
		String handle = Filer.stripExtension(filename);

		// Read input file to create list of notes
		ArrayList<String> notes = Filer.read(file);

		// If the atom extension was there, add "out" to disambiguate
		if (Filer.hasExtension(filename, ATOM_EXTENSION)) handle += OUT_EXTENSION;

		// Convert to the ATOM format
		String content = getContent(notes);
		content = content.replaceAll("&&", "AND");
		content = content.replaceAll("&", "and");

		// Write to output file
		Filer.write(content, new File(handle + ATOM_EXTENSION));

	}
}
