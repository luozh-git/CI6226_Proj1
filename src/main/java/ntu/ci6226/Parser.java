package ntu.ci6226;

import java.io.*;
import java.util.*;
import javax.xml.parsers.*;

import org.xml.sax.*;
import org.xml.sax.helpers.*;

public class Parser {

    private final int maxAuthorsPerPaper = 200;
    private String uri;
    private Indexer indexer;

    private class ConfigHandler extends DefaultHandler {

        private Locator locator;
        private Indexer indexer;
        private String author;
        private String key;
        private String title;
        private String year;
        private String venue;
        private String recordTag;
        private Person[] persons = new Person[maxAuthorsPerPaper];
        private int numberOfPersons = 0;

        private boolean insidePerson;
        private boolean insideTitle;
        private boolean insideYear;
        private boolean insideVenue;

        public ConfigHandler(Indexer indexer) {
            this.indexer = indexer;
        }

        public void setDocumentLocator(Locator locator) {
            this.locator = locator;
        }

        public void startElement(String namespaceURI, String localName,
                                 String rawName, Attributes atts) throws SAXException {
            insidePerson = (rawName.equals("author") || rawName.equals("editor"));
            insideTitle = (rawName.equals("title"));
            insideYear = (rawName.equals("year"));
            insideVenue = (rawName.equals("journal") || rawName.equals("booktitle"));
            String k;
            if ((atts.getLength() > 0) && ((k = atts.getValue("key")) != null)) {
                key = k;
                recordTag = rawName;
            }
        }

        public void endElement(String namespaceURI, String localName,
                               String rawName) throws SAXException {
            if (rawName.equals("author") || rawName.equals("editor")) {

                Person p = new Person(author);
                if (numberOfPersons < maxAuthorsPerPaper)
                    persons[numberOfPersons++] = p;
                return;
            }
            if (rawName.equals(recordTag)) {
                if (numberOfPersons == 0)
                    return;
                Person pa[] = new Person[numberOfPersons];
                for (int i = 0; i < numberOfPersons; i++) {
                    pa[i] = persons[i];
                    persons[i] = null;
                }
                Publication p = new Publication(key, recordTag, title, year, venue, pa);
                if (this.indexer != null)
                    try {
                        indexer.Index(p);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                numberOfPersons = 0;
            }
        }

        public void characters(char[] ch, int start, int length)
                throws SAXException {
            if (insidePerson)
                author = new String(ch, start, length);
            if (insideVenue)
                venue = new String(ch, start, length);
            if (insideTitle)
                title = new String(ch, start, length);
            if (insideYear)
                year = new String(ch, start, length);

        }

        private void Message(String mode, SAXParseException exception) {
            System.out.println(mode + " Line: " + exception.getLineNumber()
                    + " URI: " + exception.getSystemId() + "\n" + " Message: "
                    + exception.getMessage());
        }

        public void warning(SAXParseException exception) throws SAXException {

            Message("**Parsing Warning**\n", exception);
            throw new SAXException("Warning encountered");
        }

        public void error(SAXParseException exception) throws SAXException {

            Message("**Parsing Error**\n", exception);
            throw new SAXException("Error encountered");
        }

        public void fatalError(SAXParseException exception) throws SAXException {

            Message("**Parsing Fatal Error**\n", exception);
            throw new SAXException("Fatal Error encountered");
        }
    }


    public Parser(String uri, Indexer indexer) {
        this.uri = uri;
        this.indexer = indexer;
    }

    public void Parse() {
        try {
            SAXParserFactory parserFactory = SAXParserFactory.newInstance();
            SAXParser parser = parserFactory.newSAXParser();
            ConfigHandler handler = new ConfigHandler(this.indexer);
            parser.getXMLReader().setFeature(
                    "http://xml.org/sax/features/validation", true);
            parser.parse(new File(this.uri), handler);
        } catch (IOException e) {
            System.out.println("Error reading URI: " + e.getMessage());
        } catch (SAXException e) {
            System.out.println("Error in parsing: " + e.getMessage());
        } catch (ParserConfigurationException e) {
            System.out.println("Error in XML parser configuration: " +
                    e.getMessage());
        }
    }
}


