package at.rhomberg.parser;

import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Iterator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import at.rhomberg.fileformats.AntonymSynonymPair;
import at.rhomberg.fileformats.Container;
import at.rhomberg.fileformats.Entry;
import at.rhomberg.fileformats.FileFormats;
import at.rhomberg.fileformats.Identifier;
import at.rhomberg.fileformats.Translation;


public class KVTML2Parser implements ImportExportInterface {

	private FileFormats fileFormats;
	private int error = 0;
	private String result = "";
    private int idResult, idSubResult;
    private NodeList subNodeList, secondSubNodeList, thirdSubNodeList, fourthSubNodeList, fithSubNodeList, sixthSubNodeList;
    private Node node, subNode, secondNode, thirdNode, fourthNode;
    private Element element, subElement, secondSubElement, thirdSubElement;

	public FileFormats importf(String fileLocation) throws Throwable {

		fileFormats = new FileFormats();

		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = null;
        Document doc = null;

        dBuilder = dbFactory.newDocumentBuilder();
        File file = new File(fileLocation);
        doc = dBuilder.parse(file);
        doc.getDocumentElement().normalize();

        if( !(doc.getDocumentElement().getNodeName().equals( "kvtml") && doc.getDocumentElement().getAttribute("version").equals( "2.0")))
            throw new IllegalArgumentException();

        NodeList nodeList = doc.getDocumentElement().getChildNodes();

        // search the main branches
        for( int temp = 0; temp < nodeList.getLength(); temp++) {
            Node nNode = nodeList.item( temp);
            NodeList subNodeList;
            Element eElement;

            String qName = nNode.getNodeName();
            subNodeList = nNode.getChildNodes();

            if( qName.equals( "information")) {
                if( nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Log.d( "ParleyDrone information", "start parsing information");
                    eElement = (Element) nNode;
                    importSearchSetInformation( eElement);
                    Log.d( "ParleyDrone information", "finished parsing information");
                }
            }
            else if( qName.equals( "identifiers")) {
                if( subNodeList.getLength() > 0) {
                    Log.d( "ParleyDrone information", "start parsing identifiers");
                    importSearchSetIdentifiers( subNodeList);
                    Log.d( "ParleyDrone information", "finished parsing identifiers");
                }
            }
            else if( qName.equals( "entries")) {
                if( subNodeList.getLength() > 0) {
                    Log.d( "ParleyDrone information", "start parsing entries");
                    importSearchSetEntries( subNodeList);
                    Log.d( "ParleyDrone information", "finished parsing entries");
                }
            }
            else if( qName.equals( "lessons")) {
                if( subNodeList.getLength() > 0) {
                    Log.d( "ParleyDrone information", "start parsing lessons");
                    importSearchSetLessons( subNodeList);
                    Log.d( "ParleyDrone information", "finished parsing lessons");
                }
            }
            else if( qName.equals( "wordtypes")) {
                if( subNodeList.getLength() > 0) {
                    Log.d( "ParleyDrone information", "start parsing wordtypes");
                    importSearchSetWordTypes( subNodeList);
                    Log.d( "ParleyDrone information", "finished parsing wordtypes");
                }
            }
			else if( qName.equals( "leitnerboxes")) {
                if( subNodeList.getLength() > 0) {
                    Log.d( "ParleyDrone information", "start parsing leitnerboxes");
                    importSearchSetLeitnerboxes( subNodeList);
                    Log.d( "ParleyDrone information", "finished parsing leitnerboxes");
                }
            }
            else {
                Log.d( "ParleyDrone information", "an error occured");
                error++; // not required
            }
        }

        // examples
		//NodeList nList = doc.getElementsByTagName( "");
		//doc.getDocumentElement().getNodeName(); // root element
		//doc.getDocumentElement().getChildNodes().item(1).getNodeName();
		//doc.getDocumentElement().getChildNodes().item(1).getChildNodes();

		return fileFormats;
   }

	// for branch information
	private void importSearchSetInformation( Element element) {

		// generator
        subNodeList = element.getElementsByTagName("generator");
        if( subNodeList.getLength() > 0) {
            node = subNodeList.item(0);
            if( node != null)
                fileFormats.information.generator = node.getTextContent();
        }

		// title
        subNodeList = element.getElementsByTagName("title");
        if( subNodeList.getLength() > 0) {
            node = subNodeList.item(0);
            if( node != null)
                fileFormats.information.title = node.getTextContent();
        }

		// author
        subNodeList = element.getElementsByTagName("author");
        if( subNodeList.getLength() > 0) {
            node = subNodeList.item(0);
            if( node != null)
                fileFormats.information.author = node.getTextContent();
        }

		// contact
        subNodeList = element.getElementsByTagName("contact");
        if( subNodeList.getLength() > 0) {
            node = subNodeList.item(0);
            if( node != null)
                fileFormats.information.contact = node.getTextContent();
        }

		// license
        subNodeList = element.getElementsByTagName("license");
        if( subNodeList.getLength() > 0) {
            node = subNodeList.item(0);
            if( node != null)
                fileFormats.information.license = node.getTextContent();
        }

		// comment
        subNodeList = element.getElementsByTagName("comment");
        if( subNodeList.getLength() > 0) {
            node = subNodeList.item(0);
            if( node != null)
                fileFormats.information.comment = node.getTextContent();
        }

		// date
        subNodeList = element.getElementsByTagName("date");
        if( subNodeList.getLength() > 0) {
            node = subNodeList.item(0);
            if( node != null) {
                try {
                    fileFormats.information.date = Date.valueOf( node.getTextContent());
                }
                catch( IllegalArgumentException iae) {
                    error++;
                }
            }
        }

		// for category
        subNodeList = element.getElementsByTagName("category");
        if( subNodeList.getLength() > 0) {
            node = subNodeList.item(0);
            if( node != null)
                fileFormats.information.category = node.getTextContent();
        }
	}
	
	// for branch identifiers, contains identifier
	private void importSearchSetIdentifiers( NodeList nodeListIdentifiers) {
		// the temp variable is the identifier id
		Identifier identifier;
		
		out: for( int temp = 0; temp < nodeListIdentifiers.getLength(); temp++) {
			identifier = new Identifier();

            // identifier
			if( nodeListIdentifiers.item(temp).getNodeName().equals( "identifier")) {

                if( nodeListIdentifiers.item(temp).getNodeType() == Node.ELEMENT_NODE) {
                    element = (Element) nodeListIdentifiers.item(temp);

                    // id of identifier
                    try {
                        idResult = Integer.parseInt(nodeListIdentifiers.item(temp).getAttributes().getNamedItem("id").getTextContent());
                    }
                    catch( Exception e) {
                        error++;
                        continue out;
                    }

                    // name
                    subNodeList = element.getElementsByTagName("name");
                    if( subNodeList.getLength() > 0) {
                        node = subNodeList.item(0);
                        if( node != null)
                            identifier.name = node.getTextContent();
                    }

                    // locale
                    subNodeList = element.getElementsByTagName("locale");
                    if( subNodeList.getLength() > 0) {
                        node = subNodeList.item(0);
                        if( node != null)
                            identifier.locale = node.getTextContent();
                    }

                    // comment
                    subNodeList = element.getElementsByTagName("comment");
                    if( subNodeList.getLength() > 0) {
                        node = subNodeList.item(0);
                        if( node != null)
                            identifier.comment = node.getTextContent();
                    }

                    // sizehint
                    subNodeList = element.getElementsByTagName("sizehint");
                    if( subNodeList.getLength() > 0) {
                        node = subNodeList.item(0);
                        if( node != null) {
                            try {
                                identifier.sizeHint = Integer.parseInt(node.getTextContent());

                            }
                            catch( Exception e) {
                                error++;
                            }
                        }
                    }

                    // tense
                    secondSubNodeList = element.getElementsByTagName("tense");

                    for( int i = 0; i < secondSubNodeList.getLength(); i++) {
                        node = secondSubNodeList.item(i);

                        identifier.tenseList.add(node.getTextContent());
                    }

                    // article
                    secondSubNodeList = element.getElementsByTagName("article");

                    for( int i = 0; i < secondSubNodeList.getLength(); i++) {
                        node = secondSubNodeList.item(i);

                        // singular
                        if( node.getNodeName().equals( "singular")) {
                            thirdSubNodeList = node.getChildNodes();

                            for( int t = 0; t < thirdSubNodeList.getLength(); t++) {
                                subNode = thirdSubNodeList.item(t);

                                // definite
                                if( subNode.getNodeName().equals( "definite")) {
                                    if( subNode.getNodeType() == Node.ELEMENT_NODE) {
                                        subElement = (Element) subNode;

                                        // male
                                        subNodeList = subElement.getElementsByTagName("male");
                                        if( subNodeList.getLength() > 0) {
                                            node = subNodeList.item(0);
                                            if( node != null)
                                                identifier.article.articleSingular.definite.male = node.getTextContent();
                                        }

                                        // female
                                        subNodeList = subElement.getElementsByTagName("female");
                                        if( subNodeList.getLength() > 0) {
                                            node = subNodeList.item(0);
                                            if( node != null)
                                                identifier.article.articleSingular.definite.female = node.getTextContent();
                                        }

                                        // neutral
                                        subNodeList = subElement.getElementsByTagName("neutral");
                                        if( subNodeList.getLength() > 0) {
                                            node = subNodeList.item(0);
                                            if( node != null)
                                                identifier.article.articleSingular.definite.neutral = node.getTextContent();
                                        }
                                    }
                                }
                                // indefinite
                                else if( subNode.getNodeName().equals( "indefinite")) {
                                    if( subNode.getNodeType() == Node.ELEMENT_NODE) {
                                        subElement = (Element) subNode;

                                        // male
                                        subNodeList = subElement.getElementsByTagName("male");
                                        if( subNodeList.getLength() > 0) {
                                            node = subNodeList.item(0);
                                            if( node != null)
                                                identifier.article.articleSingular.indefinite.male = node.getTextContent();
                                        }

                                        // female
                                        subNodeList = subElement.getElementsByTagName("female");
                                        if( subNodeList.getLength() > 0) {
                                            node = subNodeList.item(0);
                                            if( node != null)
                                                identifier.article.articleSingular.indefinite.female = node.getTextContent();
                                        }

                                        // neutral
                                        subNodeList = subElement.getElementsByTagName("neutral");
                                        if( subNodeList.getLength() > 0) {
                                            node = subNodeList.item(0);
                                            if( node != null)
                                                identifier.article.articleSingular.indefinite.neutral = node.getTextContent();
                                        }
                                    }
                                }
                            }
                        }
                        // plural
                        else if( node.getNodeName().equals( "plural")) {
                            thirdSubNodeList = node.getChildNodes();

                            for( int t = 0; t < thirdSubNodeList.getLength(); t++) {
                                subNode = thirdSubNodeList.item(t);

                                // definite
                                if( subNode.getNodeName().equals( "definite")) {
                                    if( subNode.getNodeType() == Node.ELEMENT_NODE) {
                                        subElement = (Element) subNode;

                                        // male
                                        subNodeList = subElement.getElementsByTagName("male");
                                        if( subNodeList.getLength() > 0) {
                                            node = subNodeList.item(0);
                                            if( node != null)
                                                identifier.article.articlePlural.definite.male = node.getTextContent();
                                        }

                                        // female
                                        subNodeList = subElement.getElementsByTagName("female");
                                        if( subNodeList.getLength() > 0) {
                                            node = subNodeList.item(0);
                                            if( node != null)
                                                identifier.article.articlePlural.definite.female = node.getTextContent();
                                        }

                                        // neutral
                                        subNodeList = subElement.getElementsByTagName("neutral");
                                        if( subNodeList.getLength() > 0) {
                                            node = subNodeList.item(0);
                                            if( node != null)
                                                identifier.article.articlePlural.definite.neutral = node.getTextContent();
                                        }
                                    }
                                }
                                // indefinite
                                else if( subNode.getNodeName().equals( "indefinite")) {
                                    if( subNode.getNodeType() == Node.ELEMENT_NODE) {
                                        subElement = (Element) subNode;

                                        // male
                                        subNodeList = subElement.getElementsByTagName("male");
                                        if( subNodeList.getLength() > 0) {
                                            node = subNodeList.item(0);
                                            if( node != null)
                                                identifier.article.articlePlural.indefinite.male = node.getTextContent();
                                        }

                                        // female
                                        subNodeList = subElement.getElementsByTagName("female");
                                        if( subNodeList.getLength() > 0) {
                                            node = subNodeList.item(0);
                                            if( node != null)
                                                identifier.article.articlePlural.indefinite.female = node.getTextContent();
                                        }

                                        // neutral
                                        subNodeList = subElement.getElementsByTagName("neutral");
                                        if( subNodeList.getLength() > 0) {
                                            node = subNodeList.item(0);
                                            if( node != null)
                                                identifier.article.articlePlural.indefinite.neutral = node.getTextContent();
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // personalpronouns
                    secondSubNodeList = element.getElementsByTagName("personalpronouns");

                    for( int i = 0; i < secondSubNodeList.getLength(); i++) {
                        node = secondSubNodeList.item(i);

                        if( node.getNodeType() == Node.ELEMENT_NODE) {
                            thirdSubNodeList = node.getChildNodes();

                            // malefemaledifferent
                            if( node.getNodeName().equals( "malefemaledifferent"))
                                identifier.personalPronouns.maleFemaleDifferent = true;

                            // neutralexists
                            if( node.getNodeName().equals( "neutralexists"))
                                identifier.personalPronouns.neutralExists = true;

                            // dualexists
                            if( node.getNodeName().equals( "dualexists"))
                                identifier.personalPronouns.dualExists = true;

                            for( int t = 0; t < thirdSubNodeList.getLength(); t++) {
                                // singular
                                if( thirdSubNodeList.item(t).getNodeName().equals( "singular")) {
                                    if( thirdSubNodeList.item(t).getNodeType() == Node.ELEMENT_NODE) {
                                        subElement = (Element) thirdSubNodeList.item(t);

                                        // firstperson
                                        subNodeList = subElement.getElementsByTagName("firstperson");
                                        if( subNodeList.getLength() > 0) {
                                            node = subNodeList.item(0);
                                            if( node != null)
                                                identifier.personalPronouns.singular.firstPerson = node.getTextContent();
                                        }

                                        // secondperson
                                        subNodeList = subElement.getElementsByTagName("secondperson");
                                        if( subNodeList.getLength() > 0) {
                                            node = subNodeList.item(0);
                                            if( node != null)
                                                identifier.personalPronouns.singular.secondPerson = node.getTextContent();
                                        }

                                        // thirdpersonmale
                                        subNodeList = subElement.getElementsByTagName("thirdpersonmale");
                                        if( subNodeList.getLength() > 0) {
                                            node = subNodeList.item(0);
                                            if( node != null)
                                                identifier.personalPronouns.singular.thirdPersonMale = node.getTextContent();
                                        }

                                        // thirdpersonfemale
                                        subNodeList = subElement.getElementsByTagName("thirdpersonfemale");
                                        if( subNodeList.getLength() > 0) {
                                            node = subNodeList.item(0);
                                            if( node != null)
                                                identifier.personalPronouns.singular.thirdPersonFemale = node.getTextContent();
                                        }

                                        // thirdpersonneutralcommon
                                        subNodeList = subElement.getElementsByTagName("thirdpersonneutralcommon");
                                        if( subNodeList.getLength() > 0) {
                                            node = subNodeList.item(0);
                                            if( node != null)
                                                identifier.personalPronouns.singular.thirdPersonNeutralCommon = node.getTextContent();
                                        }
                                    }
                                }
                                // dual
                                else if( thirdSubNodeList.item(t).getNodeName().equals( "dual")) {
                                    if( thirdSubNodeList.item(t).getNodeType() == Node.ELEMENT_NODE) {
                                        subElement = (Element) thirdSubNodeList.item(t);

                                        // firstperson
                                        subNodeList = subElement.getElementsByTagName("firstperson");
                                        if( subNodeList.getLength() > 0) {
                                            node = subNodeList.item(0);
                                            if( node != null)
                                                identifier.personalPronouns.dual.firstPerson = node.getTextContent();
                                        }

                                        // secondperson
                                        subNodeList = subElement.getElementsByTagName("secondperson");
                                        if( subNodeList.getLength() > 0) {
                                            node = subNodeList.item(0);
                                            if( node != null)
                                                identifier.personalPronouns.dual.secondPerson = node.getTextContent();
                                        }

                                        // thirdpersonmale
                                        subNodeList = subElement.getElementsByTagName("thirdpersonmale");
                                        if( subNodeList.getLength() > 0) {
                                            node = subNodeList.item(0);
                                            if( node != null)
                                                identifier.personalPronouns.dual.thirdPersonMale = node.getTextContent();
                                        }

                                        // thirdpersonfemale
                                        subNodeList = subElement.getElementsByTagName("thirdpersonfemale");
                                        if( subNodeList.getLength() > 0) {
                                            node = subNodeList.item(0);
                                            if( node != null)
                                                identifier.personalPronouns.dual.thirdPersonFemale = node.getTextContent();
                                        }

                                        // thirdpersonneutralcommon
                                        subNodeList = subElement.getElementsByTagName("thirdpersonneutralcommon");
                                        if( subNodeList.getLength() > 0) {
                                            node = subNodeList.item(0);
                                            if( node != null)
                                                identifier.personalPronouns.dual.thirdPersonNeutralCommon = node.getTextContent();
                                        }
                                    }
                                }
                                // plural
                                else if( thirdSubNodeList.item(t).getNodeName().equals( "plural")) {
                                    if( thirdSubNodeList.item(t).getNodeType() == Node.ELEMENT_NODE) {
                                        subElement = (Element) thirdSubNodeList.item(t);

                                        // firstperson
                                        subNodeList = subElement.getElementsByTagName("firstperson");
                                        if( subNodeList.getLength() > 0) {
                                            node = subNodeList.item(0);
                                            if( node != null)
                                                identifier.personalPronouns.plural.firstPerson = node.getTextContent();
                                        }

                                        // secondperson
                                        subNodeList = subElement.getElementsByTagName("secondperson");
                                        if( subNodeList.getLength() > 0) {
                                            node = subNodeList.item(0);
                                            if( node != null)
                                                identifier.personalPronouns.plural.secondPerson = node.getTextContent();
                                        }

                                        // thirdpersonmale
                                        subNodeList = subElement.getElementsByTagName("thirdpersonmale");
                                        if( subNodeList.getLength() > 0) {
                                            node = subNodeList.item(0);
                                            if( node != null)
                                                identifier.personalPronouns.plural.thirdPersonMale = node.getTextContent();
                                        }

                                        // thirdpersonfemale
                                        subNodeList = subElement.getElementsByTagName("thirdpersonfemale");
                                        if( subNodeList.getLength() > 0) {
                                            node = subNodeList.item(0);
                                            if( node != null)
                                                identifier.personalPronouns.plural.thirdPersonFemale = node.getTextContent();
                                        }

                                        // thirdpersonneutralcommon
                                        subNodeList = subElement.getElementsByTagName("thirdpersonneutralcommon");
                                        if( subNodeList.getLength() > 0) {
                                            node = subNodeList.item(0);
                                            if( node != null)
                                                identifier.personalPronouns.plural.thirdPersonNeutralCommon = node.getTextContent();
                                        }
                                    }
                                }
                            }
                        }
                    }
                    fileFormats.identifierList.put(idResult, identifier);
                }
			}
            else {
                error++;
            }
		}
	}

    // for branch entries, contains entry
    private void importSearchSetEntries( NodeList nodeListEntries) {
        // the temp variable is the entry id
        Entry entry;
        Translation translation;


        out: for( int temp = 0; temp < nodeListEntries.getLength(); temp++) {
            entry = new Entry();

            // entry
            if( nodeListEntries.item(temp).getNodeName().equals( "entry")) {
                subNodeList = nodeListEntries.item(temp).getChildNodes();

                // id of entry
                try {
                    idResult = Integer.parseInt(nodeListEntries.item(temp).getAttributes().getNamedItem("id").getTextContent());
                }
                catch( Exception e) {
                    error++;
                    continue out;
                }

                second: for( int i = 0; i < subNodeList.getLength(); i++) {
                    if( subNodeList.item(i).getNodeName().equals( "translation")) {
                        if( subNodeList.item(i).getNodeType() == Node.ELEMENT_NODE) {

                            // id of translation
                            try {
                                idSubResult = Integer.parseInt(subNodeList.item(i).getAttributes().getNamedItem("id").getTextContent());
                            }
                            catch( Exception e) {
                                error++;
                                continue second;
                            }

                            translation = new Translation();
                            element = (Element) subNodeList.item(i);

                            // text
                            secondSubNodeList = element.getElementsByTagName("text");
                            if( secondSubNodeList.getLength() > 0) {
                                node = secondSubNodeList.item(0);
                                if( node != null) {
                                    translation.text = node.getTextContent();
                                }
                            }

                            // grade
                            secondSubNodeList = element.getElementsByTagName("grade");
                            if( secondSubNodeList.getLength() > 0) {
                                node = secondSubNodeList.item(0);
                                if( node != null) {
								
								
								
								// fromid
								
								
								
								
								
								
								
								
								
								
								

                                    if( node.getNodeType() == Node.ELEMENT_NODE) {
                                        subElement = (Element) node;

                                        // currentgrade
                                        thirdSubNodeList = subElement.getElementsByTagName("currentgrade");
                                        if( thirdSubNodeList.getLength() > 0) {
                                            subNode = thirdSubNodeList.item(0);
                                            if( subNode != null) {
                                                try {
                                                    translation.grade.currentGrade = Integer.parseInt(subNode.getTextContent());
                                                }
                                                catch( Exception e) {
                                                    error++;
                                                }
                                            }
                                        }

                                        // count
                                        thirdSubNodeList = subElement.getElementsByTagName("count");
                                        if( thirdSubNodeList.getLength() > 0) {
                                            subNode = thirdSubNodeList.item(0);
                                            if( subNode != null) {
                                                try {
                                                    translation.grade.count = Integer.parseInt(subNode.getTextContent());
                                                }
                                                catch( Exception e) {
                                                    error++;
                                                }
                                            }
                                        }

                                        // errorcount
                                        thirdSubNodeList = subElement.getElementsByTagName("errorcount");
                                        if( thirdSubNodeList.getLength() > 0) {
                                            subNode = thirdSubNodeList.item(0);
                                            if( subNode != null) {
                                                try {
                                                    translation.grade.errorCount = Integer.parseInt(subNode.getTextContent());
                                                }
                                                catch( Exception e) {
                                                    error++;
                                                }
                                            }
                                        }
                                        // date
                                        thirdSubNodeList = subElement.getElementsByTagName("date");
                                        if( thirdSubNodeList.getLength() > 0) {
                                            subNode = thirdSubNodeList.item(0);
                                            if( subNode != null) {
                                                try {
                                                    translation.grade.date = Date.valueOf(subNode.getTextContent());
                                                }
                                                catch( IllegalArgumentException iae) {
                                                    error++;
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            // conjugation
                            secondSubNodeList = element.getElementsByTagName("conjugation");
                            if( secondSubNodeList.getLength() > 0) {
                                node = secondSubNodeList.item(0);
                                if( node != null) {
                                    if( node.getNodeType() == Node.ELEMENT_NODE) {
                                        subElement = (Element) node;

                                        // tense
                                        thirdSubNodeList = subElement.getElementsByTagName("tense");
                                        if( thirdSubNodeList.getLength() > 0) {
                                            subNode = thirdSubNodeList.item(0);
                                            if( subNode != null)
                                                translation.conjugation.tense = subNode.getTextContent();
                                        }

                                        // singular
                                        thirdSubNodeList = subElement.getElementsByTagName("singular");
                                        if( thirdSubNodeList.getLength() > 0) {
                                            subNode = thirdSubNodeList.item(0);
                                            if( subNode != null) {
                                                if( subNode.getNodeType() == Node.ELEMENT_NODE) {
                                                    secondSubElement = (Element) subNode;

                                                    // firstperson
                                                    fourthSubNodeList = subElement.getElementsByTagName("firstperson");
                                                    if( fourthSubNodeList.getLength() > 0) {
                                                        secondNode = fourthSubNodeList.item(0);
                                                        if( secondNode != null) {
                                                            if( secondNode.getNodeType() == Node.ELEMENT_NODE) {
                                                                secondSubElement = (Element) secondNode;

                                                                fithSubNodeList = secondSubElement.getElementsByTagName("text");
                                                                if( fithSubNodeList.getLength() > 0) {
                                                                    thirdNode = fithSubNodeList.item(0);
                                                                    if( thirdNode != null)
                                                                        translation.conjugation.singular.firstPerson = thirdNode.getTextContent();
                                                                }
                                                            }
                                                        }
                                                    }

                                                    // secondperson
                                                    fourthSubNodeList = subElement.getElementsByTagName("secondperson");
                                                    if( fourthSubNodeList.getLength() > 0) {
                                                        secondNode = fourthSubNodeList.item(0);
                                                        if( secondNode != null) {
                                                            if( secondNode.getNodeType() == Node.ELEMENT_NODE) {
                                                                secondSubElement = (Element) secondNode;

                                                                fithSubNodeList = secondSubElement.getElementsByTagName("text");
                                                                if( fithSubNodeList.getLength() > 0) {
                                                                    thirdNode = fithSubNodeList.item(0);
                                                                    if( thirdNode != null)
                                                                        translation.conjugation.singular.secondPerson = thirdNode.getTextContent();
                                                                }
                                                            }
                                                        }
                                                    }

                                                    // thirdpersonneutralcommon
                                                    fourthSubNodeList = subElement.getElementsByTagName("thirdpersonneutralcommon");
                                                    if( fourthSubNodeList.getLength() > 0) {
                                                        secondNode = fourthSubNodeList.item(0);
                                                        if( secondNode != null) {
                                                            if( secondNode.getNodeType() == Node.ELEMENT_NODE) {
                                                                secondSubElement = (Element) secondNode;

                                                                fithSubNodeList = secondSubElement.getElementsByTagName("text");
                                                                if( fithSubNodeList.getLength() > 0) {
                                                                    thirdNode = fithSubNodeList.item(0);
                                                                    if( thirdNode != null)
                                                                        translation.conjugation.singular.thirdPersonNeutralCommon = thirdNode.getTextContent();
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }

                                        // dual
                                        thirdSubNodeList = subElement.getElementsByTagName("dual");
                                        if( thirdSubNodeList.getLength() > 0) {
                                            subNode = thirdSubNodeList.item(0);
                                            if( subNode != null) {
                                                if( subNode.getNodeType() == Node.ELEMENT_NODE) {
                                                    secondSubElement = (Element) subNode;

                                                    // firstperson
                                                    fourthSubNodeList = subElement.getElementsByTagName("firstperson");
                                                    if( fourthSubNodeList.getLength() > 0) {
                                                        secondNode = fourthSubNodeList.item(0);
                                                        if( secondNode != null) {
                                                            if( secondNode.getNodeType() == Node.ELEMENT_NODE) {
                                                                secondSubElement = (Element) secondNode;

                                                                fithSubNodeList = secondSubElement.getElementsByTagName("text");
                                                                if( fithSubNodeList.getLength() > 0) {
                                                                    thirdNode = fithSubNodeList.item(0);
                                                                    if( thirdNode != null)
                                                                        translation.conjugation.dual.firstPerson = thirdNode.getTextContent();
                                                                }
                                                            }
                                                        }
                                                    }

                                                    // secondperson
                                                    fourthSubNodeList = subElement.getElementsByTagName("secondperson");
                                                    if( fourthSubNodeList.getLength() > 0) {
                                                        secondNode = fourthSubNodeList.item(0);
                                                        if( secondNode != null) {
                                                            if( secondNode.getNodeType() == Node.ELEMENT_NODE) {
                                                                secondSubElement = (Element) secondNode;

                                                                fithSubNodeList = secondSubElement.getElementsByTagName("text");
                                                                if( fithSubNodeList.getLength() > 0) {
                                                                    thirdNode = fithSubNodeList.item(0);
                                                                    if( thirdNode != null)
                                                                        translation.conjugation.dual.secondPerson = thirdNode.getTextContent();
                                                                }
                                                            }
                                                        }
                                                    }

                                                    // thirdpersonneutralcommon
                                                    fourthSubNodeList = subElement.getElementsByTagName("thirdpersonneutralcommon");
                                                    if( fourthSubNodeList.getLength() > 0) {
                                                        secondNode = fourthSubNodeList.item(0);
                                                        if( secondNode != null) {
                                                            if( secondNode.getNodeType() == Node.ELEMENT_NODE) {
                                                                secondSubElement = (Element) secondNode;

                                                                fithSubNodeList = secondSubElement.getElementsByTagName("text");
                                                                if( fithSubNodeList.getLength() > 0) {
                                                                    thirdNode = fithSubNodeList.item(0);
                                                                    if( thirdNode != null)
                                                                        translation.conjugation.dual.thirdPersonNeutralCommon = thirdNode.getTextContent();
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }

                                        // plural
                                        thirdSubNodeList = subElement.getElementsByTagName("plural");
                                        if( thirdSubNodeList.getLength() > 0) {
                                            subNode = thirdSubNodeList.item(0);
                                            if( subNode != null) {
                                                if( subNode.getNodeType() == Node.ELEMENT_NODE) {
                                                    secondSubElement = (Element) subNode;

                                                    // firstperson
                                                    fourthSubNodeList = subElement.getElementsByTagName("firstperson");
                                                    if( fourthSubNodeList.getLength() > 0) {
                                                        secondNode = fourthSubNodeList.item(0);
                                                        if( secondNode != null) {
                                                            if( secondNode.getNodeType() == Node.ELEMENT_NODE) {
                                                                secondSubElement = (Element) secondNode;

                                                                fithSubNodeList = secondSubElement.getElementsByTagName("text");
                                                                if( fithSubNodeList.getLength() > 0) {
                                                                    thirdNode = fithSubNodeList.item(0);
                                                                    if( thirdNode != null)
                                                                        translation.conjugation.plural.firstPerson = thirdNode.getTextContent();
                                                                }
                                                            }
                                                        }
                                                    }

                                                    // secondperson
                                                    fourthSubNodeList = subElement.getElementsByTagName("secondperson");
                                                    if( fourthSubNodeList.getLength() > 0) {
                                                        secondNode = fourthSubNodeList.item(0);
                                                        if( secondNode != null) {
                                                            if( secondNode.getNodeType() == Node.ELEMENT_NODE) {
                                                                secondSubElement = (Element) secondNode;

                                                                fithSubNodeList = secondSubElement.getElementsByTagName("text");
                                                                if( fithSubNodeList.getLength() > 0) {
                                                                    thirdNode = fithSubNodeList.item(0);
                                                                    if( thirdNode != null)
                                                                        translation.conjugation.plural.secondPerson = thirdNode.getTextContent();
                                                                }
                                                            }
                                                        }
                                                    }

                                                    // thirdpersonneutralcommon
                                                    fourthSubNodeList = subElement.getElementsByTagName("thirdpersonneutralcommon");
                                                    if( fourthSubNodeList.getLength() > 0) {
                                                        secondNode = fourthSubNodeList.item(0);
                                                        if( secondNode != null) {
                                                            if( secondNode.getNodeType() == Node.ELEMENT_NODE) {
                                                                secondSubElement = (Element) secondNode;

                                                                fithSubNodeList = secondSubElement.getElementsByTagName("text");
                                                                if( fithSubNodeList.getLength() > 0) {
                                                                    thirdNode = fithSubNodeList.item(0);
                                                                    if( thirdNode != null)
                                                                        translation.conjugation.plural.thirdPersonNeutralCommon = thirdNode.getTextContent();
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            // declension
                            secondSubNodeList = element.getElementsByTagName("declension");
                            if( secondSubNodeList.getLength() > 0) {
                                node = secondSubNodeList.item(0);
                                if( node != null) {
                                    if( node.getNodeType() == Node.ELEMENT_NODE) {
                                        subElement = (Element) node;

                                        // female
                                        thirdSubNodeList = subElement.getElementsByTagName("female");
                                        if( thirdSubNodeList.getLength() > 0) {
                                            subNode = thirdSubNodeList.item(0);
                                            if( subNode != null) {
                                                if( subNode.getNodeType() == Node.ELEMENT_NODE) {
                                                    secondSubElement = (Element) subNode;

                                                    // singular
                                                    fourthSubNodeList = subElement.getElementsByTagName("singular");
                                                    if( fourthSubNodeList.getLength() > 0) {
                                                        secondNode = fourthSubNodeList.item(0);
                                                        if( secondNode != null) {
                                                            if( secondNode.getNodeType() == Node.ELEMENT_NODE) {
                                                                secondSubElement = (Element) secondNode;

                                                                // nominative
                                                                fithSubNodeList = secondSubElement.getElementsByTagName("nominative");
                                                                if( fithSubNodeList.getLength() > 0) {
                                                                    thirdNode = fithSubNodeList.item(0);
                                                                    if( thirdNode != null) {
                                                                        if( thirdNode.getNodeType() == Node.ELEMENT_NODE) {
                                                                            thirdSubElement = (Element) thirdNode;

                                                                            sixthSubNodeList = thirdSubElement.getElementsByTagName("text");
                                                                            if( sixthSubNodeList.getLength() > 0) {
                                                                                fourthNode = sixthSubNodeList.item(0);
                                                                                if( fourthNode != null)
                                                                                    translation.declension.femaleList.singular.nominative = fourthNode.getTextContent();
                                                                            }
                                                                        }
                                                                    }
                                                                }

                                                                // genitive
                                                                fithSubNodeList = secondSubElement.getElementsByTagName("genitive");
                                                                if( fithSubNodeList.getLength() > 0) {
                                                                    thirdNode = fithSubNodeList.item(0);
                                                                    if( thirdNode != null) {
                                                                        if( thirdNode.getNodeType() == Node.ELEMENT_NODE) {
                                                                            thirdSubElement = (Element) thirdNode;

                                                                            sixthSubNodeList = thirdSubElement.getElementsByTagName("text");
                                                                            if( sixthSubNodeList.getLength() > 0) {
                                                                                fourthNode = sixthSubNodeList.item(0);
                                                                                if( fourthNode != null)
                                                                                    translation.declension.femaleList.singular.genitive = fourthNode.getTextContent();
                                                                            }
                                                                        }
                                                                    }
                                                                }

                                                                // dative
                                                                fithSubNodeList = secondSubElement.getElementsByTagName("dative");
                                                                if( fithSubNodeList.getLength() > 0) {
                                                                    thirdNode = fithSubNodeList.item(0);
                                                                    if( thirdNode != null) {
                                                                        if( thirdNode.getNodeType() == Node.ELEMENT_NODE) {
                                                                            thirdSubElement = (Element) thirdNode;

                                                                            sixthSubNodeList = thirdSubElement.getElementsByTagName("text");
                                                                            if( sixthSubNodeList.getLength() > 0) {
                                                                                fourthNode = sixthSubNodeList.item(0);
                                                                                if( fourthNode != null)
                                                                                    translation.declension.femaleList.singular.dative = fourthNode.getTextContent();
                                                                            }
                                                                        }
                                                                    }
                                                                }

                                                                // accusative
                                                                fithSubNodeList = secondSubElement.getElementsByTagName("accusative");
                                                                if( fithSubNodeList.getLength() > 0) {
                                                                    thirdNode = fithSubNodeList.item(0);
                                                                    if( thirdNode != null) {
                                                                        if( thirdNode.getNodeType() == Node.ELEMENT_NODE) {
                                                                            thirdSubElement = (Element) thirdNode;

                                                                            sixthSubNodeList = thirdSubElement.getElementsByTagName("text");
                                                                            if( sixthSubNodeList.getLength() > 0) {
                                                                                fourthNode = sixthSubNodeList.item(0);
                                                                                if( fourthNode != null) {
                                                                                    translation.declension.femaleList.singular.accusative = fourthNode.getTextContent();
                                                                                }
                                                                            }
                                                                        }
                                                                    }
                                                                }

                                                                // ablative
                                                                fithSubNodeList = secondSubElement.getElementsByTagName("ablative");
                                                                if( fithSubNodeList.getLength() > 0) {
                                                                    thirdNode = fithSubNodeList.item(0);
                                                                    if( thirdNode != null) {
                                                                        if( thirdNode.getNodeType() == Node.ELEMENT_NODE) {
                                                                            thirdSubElement = (Element) thirdNode;

                                                                            sixthSubNodeList = thirdSubElement.getElementsByTagName("text");
                                                                            if( sixthSubNodeList.getLength() > 0) {
                                                                                fourthNode = sixthSubNodeList.item(0);
                                                                                if( fourthNode != null)
                                                                                    translation.declension.femaleList.singular.ablative = fourthNode.getTextContent();
                                                                            }
                                                                        }
                                                                    }
                                                                }

                                                                // locative
                                                                fithSubNodeList = secondSubElement.getElementsByTagName("locative");
                                                                if( fithSubNodeList.getLength() > 0) {
                                                                    thirdNode = fithSubNodeList.item(0);
                                                                    if( thirdNode != null) {
                                                                        if( thirdNode.getNodeType() == Node.ELEMENT_NODE) {
                                                                            thirdSubElement = (Element) thirdNode;

                                                                            sixthSubNodeList = thirdSubElement.getElementsByTagName("text");
                                                                            if( sixthSubNodeList.getLength() > 0) {
                                                                                fourthNode = sixthSubNodeList.item(0);
                                                                                if( fourthNode != null)
                                                                                    translation.declension.femaleList.singular.locative = fourthNode.getTextContent();
                                                                            }
                                                                        }
                                                                    }
                                                                }

                                                                // vocative
                                                                fithSubNodeList = secondSubElement.getElementsByTagName("vocative");
                                                                if( fithSubNodeList.getLength() > 0) {
                                                                    thirdNode = fithSubNodeList.item(0);
                                                                    if( thirdNode != null) {
                                                                        if( thirdNode.getNodeType() == Node.ELEMENT_NODE) {
                                                                            thirdSubElement = (Element) thirdNode;

                                                                            sixthSubNodeList = thirdSubElement.getElementsByTagName("text");
                                                                            if( sixthSubNodeList.getLength() > 0) {
                                                                                fourthNode = sixthSubNodeList.item(0);
                                                                                if( fourthNode != null)
                                                                                    translation.declension.femaleList.singular.vocative = fourthNode.getTextContent();
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }

                                                    // dual
                                                    fourthSubNodeList = subElement.getElementsByTagName("dual");
                                                    if( fourthSubNodeList.getLength() > 0) {
                                                        secondNode = fourthSubNodeList.item(0);
                                                        if( secondNode != null) {
                                                            if( secondNode.getNodeType() == Node.ELEMENT_NODE) {
                                                                secondSubElement = (Element) secondNode;

                                                                // nominative
                                                                fithSubNodeList = secondSubElement.getElementsByTagName("nominative");
                                                                if( fithSubNodeList.getLength() > 0) {
                                                                    thirdNode = fithSubNodeList.item(0);
                                                                    if( thirdNode != null) {
                                                                        if( thirdNode.getNodeType() == Node.ELEMENT_NODE) {
                                                                            thirdSubElement = (Element) thirdNode;

                                                                            sixthSubNodeList = thirdSubElement.getElementsByTagName("text");
                                                                            if( sixthSubNodeList.getLength() > 0) {
                                                                                fourthNode = sixthSubNodeList.item(0);
                                                                                if( fourthNode != null)
                                                                                    translation.declension.femaleList.dual.nominative = fourthNode.getTextContent();
                                                                            }
                                                                        }
                                                                    }
                                                                }

                                                                // genitive
                                                                fithSubNodeList = secondSubElement.getElementsByTagName("genitive");
                                                                if( fithSubNodeList.getLength() > 0) {
                                                                    thirdNode = fithSubNodeList.item(0);
                                                                    if( thirdNode != null) {
                                                                        if( thirdNode.getNodeType() == Node.ELEMENT_NODE) {
                                                                            thirdSubElement = (Element) thirdNode;

                                                                            sixthSubNodeList = thirdSubElement.getElementsByTagName("text");
                                                                            if( sixthSubNodeList.getLength() > 0) {
                                                                                fourthNode = sixthSubNodeList.item(0);
                                                                                if( fourthNode != null)
                                                                                    translation.declension.femaleList.dual.genitive = fourthNode.getTextContent();
                                                                            }
                                                                        }
                                                                    }
                                                                }

                                                                // dative
                                                                fithSubNodeList = secondSubElement.getElementsByTagName("dative");
                                                                if( fithSubNodeList.getLength() > 0) {
                                                                    thirdNode = fithSubNodeList.item(0);
                                                                    if( thirdNode != null) {
                                                                        if( thirdNode.getNodeType() == Node.ELEMENT_NODE) {
                                                                            thirdSubElement = (Element) thirdNode;

                                                                            sixthSubNodeList = thirdSubElement.getElementsByTagName("text");
                                                                            if( sixthSubNodeList.getLength() > 0) {
                                                                                fourthNode = sixthSubNodeList.item(0);
                                                                                if( fourthNode != null)
                                                                                    translation.declension.femaleList.dual.dative = fourthNode.getTextContent();
                                                                            }
                                                                        }
                                                                    }
                                                                }

                                                                // accusative
                                                                fithSubNodeList = secondSubElement.getElementsByTagName("accusative");
                                                                if( fithSubNodeList.getLength() > 0) {
                                                                    thirdNode = fithSubNodeList.item(0);
                                                                    if( thirdNode != null) {
                                                                        if( thirdNode.getNodeType() == Node.ELEMENT_NODE) {
                                                                            thirdSubElement = (Element) thirdNode;

                                                                            sixthSubNodeList = thirdSubElement.getElementsByTagName("text");
                                                                            if( sixthSubNodeList.getLength() > 0) {
                                                                                fourthNode = sixthSubNodeList.item(0);
                                                                                if( fourthNode != null)
                                                                                    translation.declension.femaleList.dual.accusative = fourthNode.getTextContent();
                                                                            }
                                                                        }
                                                                    }
                                                                }

                                                                // ablative
                                                                fithSubNodeList = secondSubElement.getElementsByTagName("ablative");
                                                                if( fithSubNodeList.getLength() > 0) {
                                                                    thirdNode = fithSubNodeList.item(0);
                                                                    if( thirdNode != null) {
                                                                        if( thirdNode.getNodeType() == Node.ELEMENT_NODE) {
                                                                            thirdSubElement = (Element) thirdNode;

                                                                            sixthSubNodeList = thirdSubElement.getElementsByTagName("text");
                                                                            if( sixthSubNodeList.getLength() > 0) {
                                                                                fourthNode = sixthSubNodeList.item(0);
                                                                                if( fourthNode != null)
                                                                                    translation.declension.femaleList.dual.ablative = fourthNode.getTextContent();
                                                                            }
                                                                        }
                                                                    }
                                                                }

                                                                // locative
                                                                fithSubNodeList = secondSubElement.getElementsByTagName("locative");
                                                                if( fithSubNodeList.getLength() > 0) {
                                                                    thirdNode = fithSubNodeList.item(0);
                                                                    if( thirdNode != null) {
                                                                        if( thirdNode.getNodeType() == Node.ELEMENT_NODE) {
                                                                            thirdSubElement = (Element) thirdNode;

                                                                            sixthSubNodeList = thirdSubElement.getElementsByTagName("text");
                                                                            if( sixthSubNodeList.getLength() > 0) {
                                                                                fourthNode = sixthSubNodeList.item(0);
                                                                                if( fourthNode != null)
                                                                                    translation.declension.femaleList.dual.locative = fourthNode.getTextContent();
                                                                            }
                                                                        }
                                                                    }
                                                                }

                                                                // vocative
                                                                fithSubNodeList = secondSubElement.getElementsByTagName("vocative");
                                                                if( fithSubNodeList.getLength() > 0) {
                                                                    thirdNode = fithSubNodeList.item(0);
                                                                    if( thirdNode != null) {
                                                                        if( thirdNode.getNodeType() == Node.ELEMENT_NODE) {
                                                                            thirdSubElement = (Element) thirdNode;

                                                                            sixthSubNodeList = thirdSubElement.getElementsByTagName("text");
                                                                            if( sixthSubNodeList.getLength() > 0) {
                                                                                fourthNode = sixthSubNodeList.item(0);
                                                                                if( fourthNode != null)
                                                                                    translation.declension.femaleList.dual.vocative = fourthNode.getTextContent();
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }

                                                    // plural
                                                    fourthSubNodeList = subElement.getElementsByTagName("plural");
                                                    if( fourthSubNodeList.getLength() > 0) {
                                                        secondNode = fourthSubNodeList.item(0);
                                                        if( secondNode != null) {
                                                            if( secondNode.getNodeType() == Node.ELEMENT_NODE) {
                                                                secondSubElement = (Element) secondNode;

                                                                // nominative
                                                                fithSubNodeList = secondSubElement.getElementsByTagName("nominative");
                                                                if( fithSubNodeList.getLength() > 0) {
                                                                    thirdNode = fithSubNodeList.item(0);
                                                                    if( thirdNode != null) {
                                                                        if( thirdNode.getNodeType() == Node.ELEMENT_NODE) {
                                                                            thirdSubElement = (Element) thirdNode;

                                                                            sixthSubNodeList = thirdSubElement.getElementsByTagName("text");
                                                                            if( sixthSubNodeList.getLength() > 0) {
                                                                                fourthNode = sixthSubNodeList.item(0);
                                                                                if( fourthNode != null)
                                                                                    translation.declension.femaleList.plural.nominative = fourthNode.getTextContent();
                                                                            }
                                                                        }
                                                                    }
                                                                }

                                                                // genitive
                                                                fithSubNodeList = secondSubElement.getElementsByTagName("genitive");
                                                                if( fithSubNodeList.getLength() > 0) {
                                                                    thirdNode = fithSubNodeList.item(0);
                                                                    if( thirdNode != null) {
                                                                        if( thirdNode.getNodeType() == Node.ELEMENT_NODE) {
                                                                            thirdSubElement = (Element) thirdNode;

                                                                            sixthSubNodeList = thirdSubElement.getElementsByTagName("text");
                                                                            if( sixthSubNodeList.getLength() > 0) {
                                                                                fourthNode = sixthSubNodeList.item(0);
                                                                                if( fourthNode != null)
                                                                                    translation.declension.femaleList.plural.genitive = fourthNode.getTextContent();
                                                                            }
                                                                        }
                                                                    }
                                                                }

                                                                // dative
                                                                fithSubNodeList = secondSubElement.getElementsByTagName("dative");
                                                                if( fithSubNodeList.getLength() > 0) {
                                                                    thirdNode = fithSubNodeList.item(0);
                                                                    if( thirdNode != null) {
                                                                        if( thirdNode.getNodeType() == Node.ELEMENT_NODE) {
                                                                            thirdSubElement = (Element) thirdNode;

                                                                            sixthSubNodeList = thirdSubElement.getElementsByTagName("text");
                                                                            if( sixthSubNodeList.getLength() > 0) {
                                                                                fourthNode = sixthSubNodeList.item(0);
                                                                                if( fourthNode != null)
                                                                                    translation.declension.femaleList.plural.dative = fourthNode.getTextContent();
                                                                            }
                                                                        }
                                                                    }
                                                                }

                                                                // accusative
                                                                fithSubNodeList = secondSubElement.getElementsByTagName("accusative");
                                                                if( fithSubNodeList.getLength() > 0) {
                                                                    thirdNode = fithSubNodeList.item(0);
                                                                    if( thirdNode != null) {
                                                                        if( thirdNode.getNodeType() == Node.ELEMENT_NODE) {
                                                                            thirdSubElement = (Element) thirdNode;

                                                                            sixthSubNodeList = thirdSubElement.getElementsByTagName("text");
                                                                            if( sixthSubNodeList.getLength() > 0) {
                                                                                fourthNode = sixthSubNodeList.item(0);
                                                                                if( fourthNode != null)
                                                                                    translation.declension.femaleList.plural.accusative = fourthNode.getTextContent();
                                                                            }
                                                                        }
                                                                    }
                                                                }

                                                                // ablative
                                                                fithSubNodeList = secondSubElement.getElementsByTagName("ablative");
                                                                if( fithSubNodeList.getLength() > 0) {
                                                                    thirdNode = fithSubNodeList.item(0);
                                                                    if( thirdNode != null) {
                                                                        if( thirdNode.getNodeType() == Node.ELEMENT_NODE) {
                                                                            thirdSubElement = (Element) thirdNode;

                                                                            sixthSubNodeList = thirdSubElement.getElementsByTagName("text");
                                                                            if( sixthSubNodeList.getLength() > 0) {
                                                                                fourthNode = sixthSubNodeList.item(0);
                                                                                if( fourthNode != null)
                                                                                    translation.declension.femaleList.plural.ablative = fourthNode.getTextContent();
                                                                            }
                                                                        }
                                                                    }
                                                                }

                                                                // locative
                                                                fithSubNodeList = secondSubElement.getElementsByTagName("locative");
                                                                if( fithSubNodeList.getLength() > 0) {
                                                                    thirdNode = fithSubNodeList.item(0);
                                                                    if( thirdNode != null) {
                                                                        if( thirdNode.getNodeType() == Node.ELEMENT_NODE) {
                                                                            thirdSubElement = (Element) thirdNode;

                                                                            sixthSubNodeList = thirdSubElement.getElementsByTagName("text");
                                                                            if( sixthSubNodeList.getLength() > 0) {
                                                                                fourthNode = sixthSubNodeList.item(0);
                                                                                if( fourthNode != null)
                                                                                    translation.declension.femaleList.plural.locative = fourthNode.getTextContent();
                                                                            }
                                                                        }
                                                                    }
                                                                }

                                                                // vocative
                                                                fithSubNodeList = secondSubElement.getElementsByTagName("vocative");
                                                                if( fithSubNodeList.getLength() > 0) {
                                                                    thirdNode = fithSubNodeList.item(0);
                                                                    if( thirdNode != null) {
                                                                        if( thirdNode.getNodeType() == Node.ELEMENT_NODE) {
                                                                            thirdSubElement = (Element) thirdNode;

                                                                            sixthSubNodeList = thirdSubElement.getElementsByTagName("text");
                                                                            if( sixthSubNodeList.getLength() > 0) {
                                                                                fourthNode = sixthSubNodeList.item(0);
                                                                                if( fourthNode != null)
                                                                                    translation.declension.femaleList.plural.vocative = fourthNode.getTextContent();
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }

                                        // male
                                        thirdSubNodeList = subElement.getElementsByTagName("male");
                                        if( thirdSubNodeList.getLength() > 0) {
                                            subNode = thirdSubNodeList.item(0);
                                            if( subNode != null) {
                                                if( subNode.getNodeType() == Node.ELEMENT_NODE) {
                                                    secondSubElement = (Element) subNode;

                                                    // singular
                                                    fourthSubNodeList = subElement.getElementsByTagName("singular");
                                                    if( fourthSubNodeList.getLength() > 0) {
                                                        secondNode = fourthSubNodeList.item(0);
                                                        if( secondNode != null) {
                                                            if( secondNode.getNodeType() == Node.ELEMENT_NODE) {
                                                                secondSubElement = (Element) secondNode;

                                                                // nominative
                                                                fithSubNodeList = secondSubElement.getElementsByTagName("nominative");
                                                                if( fithSubNodeList.getLength() > 0) {
                                                                    thirdNode = fithSubNodeList.item(0);
                                                                    if( thirdNode != null) {
                                                                        if( thirdNode.getNodeType() == Node.ELEMENT_NODE) {
                                                                            thirdSubElement = (Element) thirdNode;

                                                                            sixthSubNodeList = thirdSubElement.getElementsByTagName("text");
                                                                            if( sixthSubNodeList.getLength() > 0) {
                                                                                fourthNode = sixthSubNodeList.item(0);
                                                                                if( fourthNode != null)
                                                                                    translation.declension.maleList.singular.nominative = fourthNode.getTextContent();
                                                                            }
                                                                        }
                                                                    }
                                                                }

                                                                // genitive
                                                                fithSubNodeList = secondSubElement.getElementsByTagName("genitive");
                                                                if( fithSubNodeList.getLength() > 0) {
                                                                    thirdNode = fithSubNodeList.item(0);
                                                                    if( thirdNode != null) {
                                                                        if( thirdNode.getNodeType() == Node.ELEMENT_NODE) {
                                                                            thirdSubElement = (Element) thirdNode;

                                                                            sixthSubNodeList = thirdSubElement.getElementsByTagName("text");
                                                                            if( sixthSubNodeList.getLength() > 0) {
                                                                                fourthNode = sixthSubNodeList.item(0);
                                                                                if( fourthNode != null)
                                                                                    translation.declension.maleList.singular.genitive = fourthNode.getTextContent();
                                                                            }
                                                                        }
                                                                    }
                                                                }

                                                                // dative
                                                                fithSubNodeList = secondSubElement.getElementsByTagName("dative");
                                                                if( fithSubNodeList.getLength() > 0) {
                                                                    thirdNode = fithSubNodeList.item(0);
                                                                    if( thirdNode != null) {
                                                                        if( thirdNode.getNodeType() == Node.ELEMENT_NODE) {
                                                                            thirdSubElement = (Element) thirdNode;

                                                                            sixthSubNodeList = thirdSubElement.getElementsByTagName("text");
                                                                            if( sixthSubNodeList.getLength() > 0) {
                                                                                fourthNode = sixthSubNodeList.item(0);
                                                                                if( fourthNode != null)
                                                                                    translation.declension.maleList.singular.dative = fourthNode.getTextContent();
                                                                            }
                                                                        }
                                                                    }
                                                                }

                                                                // accusative
                                                                fithSubNodeList = secondSubElement.getElementsByTagName("accusative");
                                                                if( fithSubNodeList.getLength() > 0) {
                                                                    thirdNode = fithSubNodeList.item(0);
                                                                    if( thirdNode != null) {
                                                                        if( thirdNode.getNodeType() == Node.ELEMENT_NODE) {
                                                                            thirdSubElement = (Element) thirdNode;

                                                                            sixthSubNodeList = thirdSubElement.getElementsByTagName("text");
                                                                            if( sixthSubNodeList.getLength() > 0) {
                                                                                fourthNode = sixthSubNodeList.item(0);
                                                                                if( fourthNode != null)
                                                                                    translation.declension.maleList.singular.accusative = fourthNode.getTextContent();
                                                                            }
                                                                        }
                                                                    }
                                                                }

                                                                // ablative
                                                                fithSubNodeList = secondSubElement.getElementsByTagName("ablative");
                                                                if( fithSubNodeList.getLength() > 0) {
                                                                    thirdNode = fithSubNodeList.item(0);
                                                                    if( thirdNode != null) {
                                                                        if( thirdNode.getNodeType() == Node.ELEMENT_NODE) {
                                                                            thirdSubElement = (Element) thirdNode;

                                                                            sixthSubNodeList = thirdSubElement.getElementsByTagName("text");
                                                                            if( sixthSubNodeList.getLength() > 0) {
                                                                                fourthNode = sixthSubNodeList.item(0);
                                                                                if( fourthNode != null)
                                                                                    translation.declension.maleList.singular.ablative = fourthNode.getTextContent();
                                                                            }
                                                                        }
                                                                    }
                                                                }

                                                                // locative
                                                                fithSubNodeList = secondSubElement.getElementsByTagName("locative");
                                                                if( fithSubNodeList.getLength() > 0) {
                                                                    thirdNode = fithSubNodeList.item(0);
                                                                    if( thirdNode != null) {
                                                                        if( thirdNode.getNodeType() == Node.ELEMENT_NODE) {
                                                                            thirdSubElement = (Element) thirdNode;

                                                                            sixthSubNodeList = thirdSubElement.getElementsByTagName("text");
                                                                            if( sixthSubNodeList.getLength() > 0) {
                                                                                fourthNode = sixthSubNodeList.item(0);
                                                                                if( fourthNode != null)
                                                                                    translation.declension.maleList.singular.locative = fourthNode.getTextContent();
                                                                            }
                                                                        }
                                                                    }
                                                                }

                                                                // vocative
                                                                fithSubNodeList = secondSubElement.getElementsByTagName("vocative");
                                                                if( fithSubNodeList.getLength() > 0) {
                                                                    thirdNode = fithSubNodeList.item(0);
                                                                    if( thirdNode != null) {
                                                                        if( thirdNode.getNodeType() == Node.ELEMENT_NODE) {
                                                                            thirdSubElement = (Element) thirdNode;

                                                                            sixthSubNodeList = thirdSubElement.getElementsByTagName("text");
                                                                            if( sixthSubNodeList.getLength() > 0) {
                                                                                fourthNode = sixthSubNodeList.item(0);
                                                                                if( fourthNode != null)
                                                                                    translation.declension.maleList.singular.vocative = fourthNode.getTextContent();
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }

                                                    // dual
                                                    thirdSubNodeList = subElement.getElementsByTagName("dual");
                                                    if( thirdSubNodeList.getLength() > 0) {
                                                        secondNode = thirdSubNodeList.item(0);
                                                        if( secondNode != null) {
                                                            if( secondNode.getNodeType() == Node.ELEMENT_NODE) {
                                                                secondSubElement = (Element) secondNode;

                                                                // nominative
                                                                fourthSubNodeList = secondSubElement.getElementsByTagName("nominative");
                                                                if( fourthSubNodeList.getLength() > 0) {
                                                                    thirdNode = fourthSubNodeList.item(0);
                                                                    if( thirdNode != null) {
                                                                        if( thirdNode.getNodeType() == Node.ELEMENT_NODE) {
                                                                            thirdSubElement = (Element) thirdNode;

                                                                            fithSubNodeList = thirdSubElement.getElementsByTagName("text");
                                                                            if( fithSubNodeList.getLength() > 0) {
                                                                                fourthNode = fithSubNodeList.item(0);
                                                                                if( fourthNode != null)
                                                                                    translation.declension.maleList.dual.nominative = fourthNode.getTextContent();
                                                                            }
                                                                        }
                                                                    }
                                                                }

                                                                // genitive
                                                                fourthSubNodeList = secondSubElement.getElementsByTagName("genitive");
                                                                if( fourthSubNodeList.getLength() > 0) {
                                                                    thirdNode = fourthSubNodeList.item(0);
                                                                    if( thirdNode != null) {
                                                                        if( thirdNode.getNodeType() == Node.ELEMENT_NODE) {
                                                                            thirdSubElement = (Element) thirdNode;

                                                                            fithSubNodeList = thirdSubElement.getElementsByTagName("text");
                                                                            if( fithSubNodeList.getLength() > 0) {
                                                                                fourthNode = fithSubNodeList.item(0);
                                                                                if( fourthNode != null)
                                                                                    translation.declension.maleList.dual.genitive = fourthNode.getTextContent();
                                                                            }
                                                                        }
                                                                    }
                                                                }

                                                                // dative
                                                                fourthSubNodeList = secondSubElement.getElementsByTagName("dative");
                                                                if( fourthSubNodeList.getLength() > 0) {
                                                                    thirdNode = fourthSubNodeList.item(0);
                                                                    if( thirdNode != null) {
                                                                        if( thirdNode.getNodeType() == Node.ELEMENT_NODE) {
                                                                            thirdSubElement = (Element) thirdNode;

                                                                            fithSubNodeList = thirdSubElement.getElementsByTagName("text");
                                                                            if( fithSubNodeList.getLength() > 0) {
                                                                                fourthNode = fithSubNodeList.item(0);
                                                                                if( fourthNode != null)
                                                                                    translation.declension.maleList.dual.dative = fourthNode.getTextContent();
                                                                            }
                                                                        }
                                                                    }
                                                                }

                                                                // accusative
                                                                fourthSubNodeList = secondSubElement.getElementsByTagName("accusative");
                                                                if( fourthSubNodeList.getLength() > 0) {
                                                                    thirdNode = fourthSubNodeList.item(0);
                                                                    if( thirdNode != null) {
                                                                        if( thirdNode.getNodeType() == Node.ELEMENT_NODE) {
                                                                            thirdSubElement = (Element) thirdNode;

                                                                            fithSubNodeList = thirdSubElement.getElementsByTagName("text");
                                                                            if( fithSubNodeList.getLength() > 0) {
                                                                                fourthNode = fithSubNodeList.item(0);
                                                                                if( fourthNode != null)
                                                                                    translation.declension.maleList.dual.accusative = fourthNode.getTextContent();
                                                                            }
                                                                        }
                                                                    }
                                                                }

                                                                // ablative
                                                                fourthSubNodeList = secondSubElement.getElementsByTagName("ablative");
                                                                if( fourthSubNodeList.getLength() > 0) {
                                                                    thirdNode = fourthSubNodeList.item(0);
                                                                    if( thirdNode != null) {
                                                                        if( thirdNode.getNodeType() == Node.ELEMENT_NODE) {
                                                                            thirdSubElement = (Element) thirdNode;

                                                                            fithSubNodeList = thirdSubElement.getElementsByTagName("text");
                                                                            if( fithSubNodeList.getLength() > 0) {
                                                                                fourthNode = fithSubNodeList.item(0);
                                                                                if( fourthNode != null)
                                                                                    translation.declension.maleList.dual.ablative = fourthNode.getTextContent();
                                                                            }
                                                                        }
                                                                    }
                                                                }

                                                                // locative
                                                                fourthSubNodeList = secondSubElement.getElementsByTagName("locative");
                                                                if( fourthSubNodeList.getLength() > 0) {
                                                                    thirdNode = fourthSubNodeList.item(0);
                                                                    if( thirdNode != null) {
                                                                        if( thirdNode.getNodeType() == Node.ELEMENT_NODE) {
                                                                            thirdSubElement = (Element) thirdNode;

                                                                            fithSubNodeList = thirdSubElement.getElementsByTagName("text");
                                                                            if( fithSubNodeList.getLength() > 0) {
                                                                                fourthNode = fithSubNodeList.item(0);
                                                                                if( fourthNode != null)
                                                                                    translation.declension.maleList.dual.locative = fourthNode.getTextContent();
                                                                            }
                                                                        }
                                                                    }
                                                                }

                                                                // vocative
                                                                fourthSubNodeList = secondSubElement.getElementsByTagName("vocative");
                                                                if( fourthSubNodeList.getLength() > 0) {
                                                                    thirdNode = fourthSubNodeList.item(0);
                                                                    if( thirdNode != null) {
                                                                        if( thirdNode.getNodeType() == Node.ELEMENT_NODE) {
                                                                            thirdSubElement = (Element) thirdNode;

                                                                            fithSubNodeList = thirdSubElement.getElementsByTagName("text");
                                                                            if( fithSubNodeList.getLength() > 0) {
                                                                                fourthNode = fithSubNodeList.item(0);
                                                                                if( fourthNode != null)
                                                                                    translation.declension.maleList.dual.vocative = fourthNode.getTextContent();
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }

                                                    // plural
                                                    thirdSubNodeList = subElement.getElementsByTagName("plural");
                                                    if( thirdSubNodeList.getLength() > 0) {
                                                        secondNode = thirdSubNodeList.item(0);
                                                        if( secondNode != null) {
                                                            if( secondNode.getNodeType() == Node.ELEMENT_NODE) {
                                                                secondSubElement = (Element) secondNode;

                                                                // nominative
                                                                fourthSubNodeList = secondSubElement.getElementsByTagName("nominative");
                                                                if( fourthSubNodeList.getLength() > 0) {
                                                                    thirdNode = fourthSubNodeList.item(0);
                                                                    if( thirdNode != null) {
                                                                        if( thirdNode.getNodeType() == Node.ELEMENT_NODE) {
                                                                            thirdSubElement = (Element) thirdNode;

                                                                            fithSubNodeList = thirdSubElement.getElementsByTagName("text");
                                                                            if( fithSubNodeList.getLength() > 0) {
                                                                                fourthNode = fithSubNodeList.item(0);
                                                                                if( fourthNode != null)
                                                                                    translation.declension.maleList.plural.nominative = fourthNode.getTextContent();
                                                                            }
                                                                        }
                                                                    }
                                                                }

                                                                // genitive
                                                                fourthSubNodeList = secondSubElement.getElementsByTagName("genitive");
                                                                if( fourthSubNodeList.getLength() > 0) {
                                                                    thirdNode = fourthSubNodeList.item(0);
                                                                    if( thirdNode != null) {
                                                                        if( thirdNode.getNodeType() == Node.ELEMENT_NODE) {
                                                                            thirdSubElement = (Element) thirdNode;

                                                                            fithSubNodeList = thirdSubElement.getElementsByTagName("text");
                                                                            if( fithSubNodeList.getLength() > 0) {
                                                                                fourthNode = fithSubNodeList.item(0);
                                                                                if( fourthNode != null)
                                                                                    translation.declension.maleList.plural.genitive = fourthNode.getTextContent();
                                                                            }
                                                                        }
                                                                    }
                                                                }

                                                                // dative
                                                                fourthSubNodeList = secondSubElement.getElementsByTagName("dative");
                                                                if( fourthSubNodeList.getLength() > 0) {
                                                                    thirdNode = fourthSubNodeList.item(0);
                                                                    if( thirdNode != null) {
                                                                        if( thirdNode.getNodeType() == Node.ELEMENT_NODE) {
                                                                            thirdSubElement = (Element) thirdNode;

                                                                            fithSubNodeList = thirdSubElement.getElementsByTagName("text");
                                                                            if( fithSubNodeList.getLength() > 0) {
                                                                                fourthNode = fithSubNodeList.item(0);
                                                                                if( fourthNode != null)
                                                                                    translation.declension.maleList.plural.dative = fourthNode.getTextContent();
                                                                            }
                                                                        }
                                                                    }
                                                                }

                                                                // accusative
                                                                fourthSubNodeList = secondSubElement.getElementsByTagName("accusative");
                                                                if( fourthSubNodeList.getLength() > 0) {
                                                                    thirdNode = fourthSubNodeList.item(0);
                                                                    if( thirdNode != null) {
                                                                        if( thirdNode.getNodeType() == Node.ELEMENT_NODE) {
                                                                            thirdSubElement = (Element) thirdNode;

                                                                            fithSubNodeList = thirdSubElement.getElementsByTagName("text");
                                                                            if( fithSubNodeList.getLength() > 0) {
                                                                                fourthNode = fithSubNodeList.item(0);
                                                                                if( fourthNode != null)
                                                                                    translation.declension.maleList.plural.accusative = fourthNode.getTextContent();
                                                                            }
                                                                        }
                                                                    }
                                                                }

                                                                // ablative
                                                                fourthSubNodeList = secondSubElement.getElementsByTagName("ablative");
                                                                if( fourthSubNodeList.getLength() > 0) {
                                                                    thirdNode = fourthSubNodeList.item(0);
                                                                    if( thirdNode != null) {
                                                                        if( thirdNode.getNodeType() == Node.ELEMENT_NODE) {
                                                                            thirdSubElement = (Element) thirdNode;

                                                                            fithSubNodeList = thirdSubElement.getElementsByTagName("text");
                                                                            if( fithSubNodeList.getLength() > 0) {
                                                                                fourthNode = fithSubNodeList.item(0);
                                                                                if( fourthNode != null)
                                                                                    translation.declension.maleList.plural.ablative = fourthNode.getTextContent();
                                                                            }
                                                                        }
                                                                    }
                                                                }

                                                                // locative
                                                                fourthSubNodeList = secondSubElement.getElementsByTagName("locative");
                                                                if( fourthSubNodeList.getLength() > 0) {
                                                                    thirdNode = fourthSubNodeList.item(0);
                                                                    if( thirdNode != null) {
                                                                        if( thirdNode.getNodeType() == Node.ELEMENT_NODE) {
                                                                            thirdSubElement = (Element) thirdNode;

                                                                            fithSubNodeList = thirdSubElement.getElementsByTagName("text");
                                                                            if( fithSubNodeList.getLength() > 0) {
                                                                                fourthNode = fithSubNodeList.item(0);
                                                                                if( fourthNode != null)
                                                                                    translation.declension.maleList.plural.locative = fourthNode.getTextContent();
                                                                            }
                                                                        }
                                                                    }
                                                                }

                                                                // vocative
                                                                fourthSubNodeList = secondSubElement.getElementsByTagName("vocative");
                                                                if( fourthSubNodeList.getLength() > 0) {
                                                                    thirdNode = fourthSubNodeList.item(0);
                                                                    if( thirdNode != null) {
                                                                        if( thirdNode.getNodeType() == Node.ELEMENT_NODE) {
                                                                            thirdSubElement = (Element) thirdNode;

                                                                            fithSubNodeList = thirdSubElement.getElementsByTagName("text");
                                                                            if( fithSubNodeList.getLength() > 0) {
                                                                                fourthNode = fithSubNodeList.item(0);
                                                                                if( fourthNode != null)
                                                                                    translation.declension.maleList.plural.vocative = fourthNode.getTextContent();
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }

                                        // neutral
                                        thirdSubNodeList = subElement.getElementsByTagName("neutral");
                                        if( thirdSubNodeList.getLength() > 0) {
                                            subNode = thirdSubNodeList.item(0);
                                            if( subNode != null) {
                                                if( subNode.getNodeType() == Node.ELEMENT_NODE) {
                                                    secondSubElement = (Element) subNode;

                                                    // singular
                                                    fourthSubNodeList = subElement.getElementsByTagName("singular");
                                                    if( fourthSubNodeList.getLength() > 0) {
                                                        secondNode = fourthSubNodeList.item(0);
                                                        if( secondNode != null) {
                                                            if( secondNode.getNodeType() == Node.ELEMENT_NODE) {
                                                                secondSubElement = (Element) secondNode;

                                                                // nominative
                                                                fithSubNodeList = secondSubElement.getElementsByTagName("nominative");
                                                                if( fithSubNodeList.getLength() > 0) {
                                                                    thirdNode = fithSubNodeList.item(0);
                                                                    if( thirdNode != null) {
                                                                        if( thirdNode.getNodeType() == Node.ELEMENT_NODE) {
                                                                            thirdSubElement = (Element) thirdNode;

                                                                            sixthSubNodeList = thirdSubElement.getElementsByTagName("text");
                                                                            if( sixthSubNodeList.getLength() > 0) {
                                                                                fourthNode = sixthSubNodeList.item(0);
                                                                                if( fourthNode != null)
                                                                                    translation.declension.neutralList.singular.nominative = fourthNode.getTextContent();
                                                                            }
                                                                        }
                                                                    }
                                                                }

                                                                // genitive
                                                                fithSubNodeList = secondSubElement.getElementsByTagName("genitive");
                                                                if( fithSubNodeList.getLength() > 0) {
                                                                    thirdNode = fithSubNodeList.item(0);
                                                                    if( thirdNode != null) {
                                                                        if( thirdNode.getNodeType() == Node.ELEMENT_NODE) {
                                                                            thirdSubElement = (Element) thirdNode;

                                                                            sixthSubNodeList = thirdSubElement.getElementsByTagName("text");
                                                                            if( sixthSubNodeList.getLength() > 0) {
                                                                                fourthNode = sixthSubNodeList.item(0);
                                                                                if( fourthNode != null)
                                                                                    translation.declension.neutralList.singular.genitive = fourthNode.getTextContent();
                                                                            }
                                                                        }
                                                                    }
                                                                }

                                                                // dative
                                                                fithSubNodeList = secondSubElement.getElementsByTagName("dative");
                                                                if( fithSubNodeList.getLength() > 0) {
                                                                    thirdNode = fithSubNodeList.item(0);
                                                                    if( thirdNode != null) {
                                                                        if( thirdNode.getNodeType() == Node.ELEMENT_NODE) {
                                                                            thirdSubElement = (Element) thirdNode;

                                                                            sixthSubNodeList = thirdSubElement.getElementsByTagName("text");
                                                                            if( sixthSubNodeList.getLength() > 0) {
                                                                                fourthNode = sixthSubNodeList.item(0);
                                                                                if( fourthNode != null)
                                                                                    translation.declension.neutralList.singular.dative = fourthNode.getTextContent();
                                                                            }
                                                                        }
                                                                    }
                                                                }

                                                                // accusative
                                                                fithSubNodeList = secondSubElement.getElementsByTagName("accusative");
                                                                if( fithSubNodeList.getLength() > 0) {
                                                                    thirdNode = fithSubNodeList.item(0);
                                                                    if( thirdNode != null) {
                                                                        if( thirdNode.getNodeType() == Node.ELEMENT_NODE) {
                                                                            thirdSubElement = (Element) thirdNode;

                                                                            sixthSubNodeList = thirdSubElement.getElementsByTagName("text");
                                                                            if( sixthSubNodeList.getLength() > 0) {
                                                                                fourthNode = sixthSubNodeList.item(0);
                                                                                if( fourthNode != null)
                                                                                    translation.declension.neutralList.singular.accusative = fourthNode.getTextContent();
                                                                            }
                                                                        }
                                                                    }
                                                                }

                                                                // ablative
                                                                fithSubNodeList = secondSubElement.getElementsByTagName("ablative");
                                                                if( fithSubNodeList.getLength() > 0) {
                                                                    thirdNode = fithSubNodeList.item(0);
                                                                    if( thirdNode != null) {
                                                                        if( thirdNode.getNodeType() == Node.ELEMENT_NODE) {
                                                                            thirdSubElement = (Element) thirdNode;

                                                                            sixthSubNodeList = thirdSubElement.getElementsByTagName("text");
                                                                            if( sixthSubNodeList.getLength() > 0) {
                                                                                fourthNode = sixthSubNodeList.item(0);
                                                                                if( fourthNode != null)
                                                                                    translation.declension.neutralList.singular.ablative = fourthNode.getTextContent();
                                                                            }
                                                                        }
                                                                    }
                                                                }

                                                                // locative
                                                                fithSubNodeList = secondSubElement.getElementsByTagName("locative");
                                                                if( fithSubNodeList.getLength() > 0) {
                                                                    thirdNode = fithSubNodeList.item(0);
                                                                    if( thirdNode != null) {
                                                                        if( thirdNode.getNodeType() == Node.ELEMENT_NODE) {
                                                                            thirdSubElement = (Element) thirdNode;

                                                                            sixthSubNodeList = thirdSubElement.getElementsByTagName("text");
                                                                            if( sixthSubNodeList.getLength() > 0) {
                                                                                fourthNode = sixthSubNodeList.item(0);
                                                                                if( fourthNode != null)
                                                                                    translation.declension.neutralList.singular.locative = fourthNode.getTextContent();
                                                                            }
                                                                        }
                                                                    }
                                                                }

                                                                // vocative
                                                                fithSubNodeList = secondSubElement.getElementsByTagName("vocative");
                                                                if( fithSubNodeList.getLength() > 0) {
                                                                    thirdNode = fithSubNodeList.item(0);
                                                                    if( thirdNode != null) {
                                                                        if( thirdNode.getNodeType() == Node.ELEMENT_NODE) {
                                                                            thirdSubElement = (Element) thirdNode;

                                                                            sixthSubNodeList = thirdSubElement.getElementsByTagName("text");
                                                                            if( sixthSubNodeList.getLength() > 0) {
                                                                                fourthNode = sixthSubNodeList.item(0);
                                                                                if( fourthNode != null)
                                                                                    translation.declension.neutralList.singular.vocative = fourthNode.getTextContent();
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }

                                                    // dual
                                                    fourthSubNodeList = subElement.getElementsByTagName("dual");
                                                    if( fourthSubNodeList.getLength() > 0) {
                                                        secondNode = fourthSubNodeList.item(0);
                                                        if( secondNode != null) {
                                                            if( secondNode.getNodeType() == Node.ELEMENT_NODE) {
                                                                secondSubElement = (Element) secondNode;

                                                                // nominative
                                                                fithSubNodeList = secondSubElement.getElementsByTagName("nominative");
                                                                if( fithSubNodeList.getLength() > 0) {
                                                                    thirdNode = fithSubNodeList.item(0);
                                                                    if( thirdNode != null) {
                                                                        if( thirdNode.getNodeType() == Node.ELEMENT_NODE) {
                                                                            thirdSubElement = (Element) thirdNode;

                                                                            sixthSubNodeList = thirdSubElement.getElementsByTagName("text");
                                                                            if( sixthSubNodeList.getLength() > 0) {
                                                                                fourthNode = sixthSubNodeList.item(0);
                                                                                if( fourthNode != null)
                                                                                    translation.declension.neutralList.dual.nominative = fourthNode.getTextContent();
                                                                            }
                                                                        }
                                                                    }
                                                                }

                                                                // genitive
                                                                fithSubNodeList = secondSubElement.getElementsByTagName("genitive");
                                                                if( fithSubNodeList.getLength() > 0) {
                                                                    thirdNode = fithSubNodeList.item(0);
                                                                    if( thirdNode != null) {
                                                                        if( thirdNode.getNodeType() == Node.ELEMENT_NODE) {
                                                                            thirdSubElement = (Element) thirdNode;

                                                                            sixthSubNodeList = thirdSubElement.getElementsByTagName("text");
                                                                            if( sixthSubNodeList.getLength() > 0) {
                                                                                fourthNode = sixthSubNodeList.item(0);
                                                                                if( fourthNode != null)
                                                                                    translation.declension.neutralList.dual.genitive = fourthNode.getTextContent();
                                                                            }
                                                                        }
                                                                    }
                                                                }

                                                                // dative
                                                                fithSubNodeList = secondSubElement.getElementsByTagName("dative");
                                                                if( fithSubNodeList.getLength() > 0) {
                                                                    thirdNode = fithSubNodeList.item(0);
                                                                    if( thirdNode != null) {
                                                                        if( thirdNode.getNodeType() == Node.ELEMENT_NODE) {
                                                                            thirdSubElement = (Element) thirdNode;

                                                                            sixthSubNodeList = thirdSubElement.getElementsByTagName("text");
                                                                            if( sixthSubNodeList.getLength() > 0) {
                                                                                fourthNode = sixthSubNodeList.item(0);
                                                                                if( fourthNode != null)
                                                                                    translation.declension.neutralList.dual.dative = fourthNode.getTextContent();
                                                                            }
                                                                        }
                                                                    }
                                                                }

                                                                // accusative
                                                                fithSubNodeList = secondSubElement.getElementsByTagName("accusative");
                                                                if( fithSubNodeList.getLength() > 0) {
                                                                    thirdNode = fithSubNodeList.item(0);
                                                                    if( thirdNode != null) {
                                                                        if( thirdNode.getNodeType() == Node.ELEMENT_NODE) {
                                                                            thirdSubElement = (Element) thirdNode;

                                                                            sixthSubNodeList = thirdSubElement.getElementsByTagName("text");
                                                                            if( sixthSubNodeList.getLength() > 0) {
                                                                                fourthNode = sixthSubNodeList.item(0);
                                                                                if( fourthNode != null)
                                                                                    translation.declension.neutralList.dual.accusative = fourthNode.getTextContent();
                                                                            }
                                                                        }
                                                                    }
                                                                }

                                                                // ablative
                                                                fithSubNodeList = secondSubElement.getElementsByTagName("ablative");
                                                                if( fithSubNodeList.getLength() > 0) {
                                                                    thirdNode = fithSubNodeList.item(0);
                                                                    if( thirdNode != null) {
                                                                        if( thirdNode.getNodeType() == Node.ELEMENT_NODE) {
                                                                            thirdSubElement = (Element) thirdNode;

                                                                            sixthSubNodeList = thirdSubElement.getElementsByTagName("text");
                                                                            if( sixthSubNodeList.getLength() > 0) {
                                                                                fourthNode = sixthSubNodeList.item(0);
                                                                                if( fourthNode != null)
                                                                                    translation.declension.neutralList.dual.ablative = fourthNode.getTextContent();
                                                                            }
                                                                        }
                                                                    }
                                                                }

                                                                // locative
                                                                fithSubNodeList = secondSubElement.getElementsByTagName("locative");
                                                                if( fithSubNodeList.getLength() > 0) {
                                                                    thirdNode = fithSubNodeList.item(0);
                                                                    if( thirdNode != null) {
                                                                        if( thirdNode.getNodeType() == Node.ELEMENT_NODE) {
                                                                            thirdSubElement = (Element) thirdNode;

                                                                            sixthSubNodeList = thirdSubElement.getElementsByTagName("text");
                                                                            if( sixthSubNodeList.getLength() > 0) {
                                                                                fourthNode = sixthSubNodeList.item(0);
                                                                                if( fourthNode != null)
                                                                                    translation.declension.neutralList.dual.locative = fourthNode.getTextContent();
                                                                            }
                                                                        }
                                                                    }
                                                                }

                                                                // vocative
                                                                fithSubNodeList = secondSubElement.getElementsByTagName("vocative");
                                                                if( fithSubNodeList.getLength() > 0) {
                                                                    thirdNode = fithSubNodeList.item(0);
                                                                    if( thirdNode != null) {
                                                                        if( thirdNode.getNodeType() == Node.ELEMENT_NODE) {
                                                                            thirdSubElement = (Element) thirdNode;

                                                                            sixthSubNodeList = thirdSubElement.getElementsByTagName("text");
                                                                            if( sixthSubNodeList.getLength() > 0) {
                                                                                fourthNode = sixthSubNodeList.item(0);
                                                                                if( fourthNode != null)
                                                                                    translation.declension.neutralList.dual.vocative = fourthNode.getTextContent();
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }

                                                    // plural
                                                    fourthSubNodeList = subElement.getElementsByTagName("plural");
                                                    if( fourthSubNodeList.getLength() > 0) {
                                                        secondNode = fourthSubNodeList.item(0);
                                                        if( secondNode != null) {
                                                            if( secondNode.getNodeType() == Node.ELEMENT_NODE) {
                                                                secondSubElement = (Element) secondNode;

                                                                // nominative
                                                                fithSubNodeList = secondSubElement.getElementsByTagName("nominative");
                                                                if( fithSubNodeList.getLength() > 0) {
                                                                    thirdNode = fithSubNodeList.item(0);
                                                                    if( thirdNode != null) {
                                                                        if( thirdNode.getNodeType() == Node.ELEMENT_NODE) {
                                                                            thirdSubElement = (Element) thirdNode;

                                                                            sixthSubNodeList = thirdSubElement.getElementsByTagName("text");
                                                                            if( sixthSubNodeList.getLength() > 0) {
                                                                                fourthNode = sixthSubNodeList.item(0);
                                                                                if( fourthNode != null)
                                                                                    translation.declension.neutralList.plural.nominative = fourthNode.getTextContent();
                                                                            }
                                                                        }
                                                                    }
                                                                }

                                                                // genitive
                                                                fithSubNodeList = secondSubElement.getElementsByTagName("genitive");
                                                                if( fithSubNodeList.getLength() > 0) {
                                                                    thirdNode = fithSubNodeList.item(0);
                                                                    if( thirdNode != null) {
                                                                        if( thirdNode.getNodeType() == Node.ELEMENT_NODE) {
                                                                            thirdSubElement = (Element) thirdNode;

                                                                            sixthSubNodeList = thirdSubElement.getElementsByTagName("text");
                                                                            if( sixthSubNodeList.getLength() > 0) {
                                                                                fourthNode = sixthSubNodeList.item(0);
                                                                                if( fourthNode != null)
                                                                                    translation.declension.neutralList.plural.genitive = fourthNode.getTextContent();
                                                                            }
                                                                        }
                                                                    }
                                                                }

                                                                // dative
                                                                fithSubNodeList = secondSubElement.getElementsByTagName("dative");
                                                                if( fithSubNodeList.getLength() > 0) {
                                                                    thirdNode = fithSubNodeList.item(0);
                                                                    if( thirdNode != null) {
                                                                        if( thirdNode.getNodeType() == Node.ELEMENT_NODE) {
                                                                            thirdSubElement = (Element) thirdNode;

                                                                            sixthSubNodeList = thirdSubElement.getElementsByTagName("text");
                                                                            if( sixthSubNodeList.getLength() > 0) {
                                                                                fourthNode = sixthSubNodeList.item(0);
                                                                                if( fourthNode != null)
                                                                                    translation.declension.neutralList.plural.dative = fourthNode.getTextContent();
                                                                            }
                                                                        }
                                                                    }
                                                                }

                                                                // accusative
                                                                fithSubNodeList = secondSubElement.getElementsByTagName("accusative");
                                                                if( fithSubNodeList.getLength() > 0) {
                                                                    thirdNode = fithSubNodeList.item(0);
                                                                    if( thirdNode != null) {
                                                                        if( thirdNode.getNodeType() == Node.ELEMENT_NODE) {
                                                                            thirdSubElement = (Element) thirdNode;

                                                                            sixthSubNodeList = thirdSubElement.getElementsByTagName("text");
                                                                            if( sixthSubNodeList.getLength() > 0) {
                                                                                fourthNode = sixthSubNodeList.item(0);
                                                                                if( fourthNode != null)
                                                                                    translation.declension.neutralList.plural.accusative = fourthNode.getTextContent();
                                                                            }
                                                                        }
                                                                    }
                                                                }

                                                                // ablative
                                                                fithSubNodeList = secondSubElement.getElementsByTagName("ablative");
                                                                if( fithSubNodeList.getLength() > 0) {
                                                                    thirdNode = fithSubNodeList.item(0);
                                                                    if( thirdNode != null) {
                                                                        if( thirdNode.getNodeType() == Node.ELEMENT_NODE) {
                                                                            thirdSubElement = (Element) thirdNode;

                                                                            sixthSubNodeList = thirdSubElement.getElementsByTagName("text");
                                                                            if( sixthSubNodeList.getLength() > 0) {
                                                                                fourthNode = sixthSubNodeList.item(0);
                                                                                if( fourthNode != null)
                                                                                    translation.declension.neutralList.plural.ablative = fourthNode.getTextContent();
                                                                            }
                                                                        }
                                                                    }
                                                                }

                                                                // locative
                                                                fithSubNodeList = secondSubElement.getElementsByTagName("locative");
                                                                if( fithSubNodeList.getLength() > 0) {
                                                                    thirdNode = fithSubNodeList.item(0);
                                                                    if( thirdNode != null) {
                                                                        if( thirdNode.getNodeType() == Node.ELEMENT_NODE) {
                                                                            thirdSubElement = (Element) thirdNode;

                                                                            sixthSubNodeList = thirdSubElement.getElementsByTagName("text");
                                                                            if( sixthSubNodeList.getLength() > 0) {
                                                                                fourthNode = sixthSubNodeList.item(0);
                                                                                if( fourthNode != null)
                                                                                    translation.declension.neutralList.plural.locative = fourthNode.getTextContent();
                                                                            }
                                                                        }
                                                                    }
                                                                }

                                                                // vocative
                                                                fithSubNodeList = secondSubElement.getElementsByTagName("vocative");
                                                                if( fithSubNodeList.getLength() > 0) {
                                                                    thirdNode = fithSubNodeList.item(0);
                                                                    if( thirdNode != null) {
                                                                        if( thirdNode.getNodeType() == Node.ELEMENT_NODE) {
                                                                            thirdSubElement = (Element) thirdNode;

                                                                            sixthSubNodeList = thirdSubElement.getElementsByTagName("text");
                                                                            if( sixthSubNodeList.getLength() > 0) {
                                                                                fourthNode = sixthSubNodeList.item(0);
                                                                                if( fourthNode != null)
                                                                                    translation.declension.neutralList.plural.vocative = fourthNode.getTextContent();
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            // comment
                            secondSubNodeList = element.getElementsByTagName("comment");
                            if( secondSubNodeList.getLength() > 0) {
                                node = secondSubNodeList.item(0);
                                if( node != null)
                                    translation.comment = node.getTextContent();
                            }

                            // pronunciation
                            secondSubNodeList = element.getElementsByTagName("pronunciation");
                            if( secondSubNodeList.getLength() > 0) {
                                node = secondSubNodeList.item(0);
                                if( node != null)
                                    translation.pronunciation = node.getTextContent();
                            }

                            // example
                            secondSubNodeList = element.getElementsByTagName("example");
                            if( secondSubNodeList.getLength() > 0) {
                                node = secondSubNodeList.item(0);
                                if( node != null)
                                    translation.example = node.getTextContent();
                            }
							
                            // paraphrase
                            secondSubNodeList = element.getElementsByTagName("paraphrase");
                            if( secondSubNodeList.getLength() > 0) {
                                node = secondSubNodeList.item(0);
                                if( node != null)
                                    translation.paraphrase = node.getTextContent();
                            }
							
							// falsefriend
                            secondSubNodeList = element.getElementsByTagName("falsefriend");
							if( secondSubNodeList.getLength() > 0) {
								node = secondSubNodeList.item(0);
								
								
								
								
								
								
								
								
								
								
								
								
								
								
							}

							// antonym
                            secondSubNodeList = element.getElementsByTagName("antonym");
                            if( secondSubNodeList.getLength() > 0) {
                                node = secondSubNodeList.item(0);
                                if( node != null) {	
									thirdSubNodeList = node.getChildNodes();

									for( int t = 0; thirdSubNodeList.getLength() > 0; t++) {
										AntonymSynonymPair pair = new AntonymSynonymPair();
										Byte translationCount = 0;
				
										// pair
										if( thirdSubNodeList.item(t).getNodeName().equals( "pair")) {
											if( thirdSubNodeList.getLength() > 0) {
												secondNode = thirdSubNodeList.item(0);
												if( secondNode != null) {
													for( int f = 0; thirdSubNodeList.getLength() > 0; f++) {
														
														// entry; there must be two entries
														if( thirdSubNodeList.item(f).getNodeName().equals( "entry")) {
															if( thirdSubNodeList.getLength() > 0) {
																secondNode = thirdSubNodeList.item(0);
																if( secondNode != null) {
																	if( secondNode.getNodeType() == Node.ELEMENT_NODE) {
																		subElement = (Element) secondNode;

																		if( translationCount == 0) {
																			pair.first.entryId = Integer.parseInt(secondNode.getTextContent());
																		}
																		else {
																			pair.second.entryId = Integer.parseInt(secondNode.getTextContent());
																		}				
																		
																		// translation; only 1
																		fourthSubNodeList = subElement.getElementsByTagName("translation");
																		if( fourthSubNodeList.getLength() > 0) {
																			secondNode = fourthSubNodeList.item(0);
																			
																			if( secondNode != null) {
																				try {
																					if( translationCount == 0) {
																						pair.first.translationId = Integer.parseInt(secondNode.getTextContent());
                                                                                        translationCount++;
																					}
																					else {
																						pair.second.translationId = Integer.parseInt(secondNode.getTextContent());
																						translation.antonymPair.add( pair);
																						continue;
																					}
																				}
																				catch( Exception e) {
																					error++;
																				}
																			}
																		}
																	}
																}
                                                                else {
                                                                    error++;
                                                                }
															}
														}
													}
												}
											}
                                            else {
                                                error++;
                                            }
										}
									}
								}
                            }

							// synonym
                            secondSubNodeList = element.getElementsByTagName("synonym");
                            if( secondSubNodeList.getLength() > 0) {
                                node = secondSubNodeList.item(0);
                                if( node != null) {	
									thirdSubNodeList = node.getChildNodes();

									for( int t = 0; thirdSubNodeList.getLength() > 0; t++) {
										AntonymSynonymPair pair = new AntonymSynonymPair();
										Byte translationCount = 0;
				
										// pair
										if( thirdSubNodeList.item(t).getNodeName().equals( "pair")) {
											if( thirdSubNodeList.getLength() > 0) {
												secondNode = thirdSubNodeList.item(0);
												if( secondNode != null) {
													for( int f = 0; thirdSubNodeList.getLength() > 0; f++) {
														
														// entry; there must be two entries
														if( thirdSubNodeList.item(f).getNodeName().equals( "entry")) {
															if( thirdSubNodeList.getLength() > 0) {
																secondNode = thirdSubNodeList.item(0);
																if( secondNode != null) {
																	if( secondNode.getNodeType() == Node.ELEMENT_NODE) {
																		subElement = (Element) secondNode;
																		
																		if( translationCount == 0) {
																			pair.first.entryId = Integer.parseInt(secondNode.getTextContent());
																		}
																		else {
																			pair.second.entryId = Integer.parseInt(secondNode.getTextContent());
																		}				
																		
																		// translation; only 1
																		fourthSubNodeList = subElement.getElementsByTagName("translation");
																		if( fourthSubNodeList.getLength() > 0) {
																			secondNode = fourthSubNodeList.item(0);
																			
																			if( secondNode != null) {
																				try {
																					if( translationCount == 0) {
																						pair.first.translationId = Integer.parseInt(secondNode.getTextContent());
                                                                                        translationCount++;
																					}
																					else {
																						pair.second.translationId = Integer.parseInt(secondNode.getTextContent());

                                                                                        translation.synonymPair.add( pair);
																						continue;
																					}
																				}
																				catch( Exception e) {
																					error++;
																				}
																			}
																		}
																	}
																}
															}
                                                            else {
                                                                error++;
                                                            }
														}
													}
												}
											}
                                            else {
                                                error++;
                                            }
										}
									}
								}
                            }

							// comparison
                            secondSubNodeList = element.getElementsByTagName("comparison");
							if( secondSubNodeList.getLength() > 0) {
								node = secondSubNodeList.item(0);
								if( node != null) {
									if( node.getNodeType() == Node.ELEMENT_NODE) {
                                        subElement = (Element) node;
										
										// absolute
										thirdSubNodeList = subElement.getElementsByTagName("absolute");
										if( thirdSubNodeList.getLength() > 0) {
											secondNode = thirdSubNodeList.item(0);
											if( secondNode != null)
												translation.comparison.absolute = secondNode.getTextContent();
										}
										
										// comparative
                                        thirdSubNodeList = subElement.getElementsByTagName("comparative");
										if( thirdSubNodeList.getLength() > 0) {
											secondNode = thirdSubNodeList.item(0);
											if( secondNode != null)
												translation.comparison.comparative = secondNode.getTextContent();
										}
										
										// superlative
                                        thirdSubNodeList = subElement.getElementsByTagName("superlative");
										if( thirdSubNodeList.getLength() > 0) {
											secondNode = thirdSubNodeList.item(0);
											if( secondNode != null)
												translation.comparison.superlative = secondNode.getTextContent();
										}
									}
								}
							}

							// multipleChoice
                            secondSubNodeList = element.getElementsByTagName("multiplechoice");
                            if( secondSubNodeList.getLength() > 0) {
                                node = secondSubNodeList.item(0);
                                if( node != null) {
									thirdSubNodeList = node.getChildNodes();
									
									for( int t = 0; thirdSubNodeList.getLength() > 0; t++) {
										// choice
										if( thirdSubNodeList.item(t).getNodeName().equals( "choice")) {
											if( thirdSubNodeList.getLength() > 0) {
												secondNode = thirdSubNodeList.item(0);
												if( secondNode != null)
													translation.multipleChoice.choice.add(secondNode.getTextContent());
											}
										}
										else {
											error++;
										}
									}	
								}
							}

							// image
                            secondSubNodeList = element.getElementsByTagName("image");
                            if( secondSubNodeList.getLength() > 0) {
                                node = secondSubNodeList.item(0);
                                if( node != null)
                                    translation.image = node.getTextContent();
                            }
							
							// sound
                            secondSubNodeList = element.getElementsByTagName("sound");
                            if( secondSubNodeList.getLength() > 0) {
                                node = secondSubNodeList.item(0);
                                if( node != null)
                                    translation.sound = node.getTextContent();
                            }

                            entry.translationList.put(idSubResult, translation);
                            Log.d("ParleyDrone information", "translation list added" + idSubResult + translation.text);
                        }

                        // deactivated
                        else if( subNodeList.item(i).getNodeName().equals( "deactivated")) {
                            if( subNodeList.getLength() > 0) {
                                node = subNodeList.item(0);
                                if( node != null) {
                                    result = node.getTextContent();

                                    if( (result.equals( "true")) || (result.equals( "1")))
                                        entry.deactivated = true;
                                }
                            }
                        }

                        // sizehint
                        else if( subNodeList.item(i).getNodeName().equals( "sizehint")) {
                            if( subNodeList.getLength() > 0) {
                                node = subNodeList.item(0);
                                if( node != null) {
                                    try {
                                        entry.sizeHint = Integer.parseInt(node.getTextContent());
                                    }
                                    catch( Exception e) {
                                        error++;
                                    }
                                }
                            }
                        }

                        else {
                            error++;
                        }
                        fileFormats.entryList.put(idResult, entry);
                    }
                }
            }
            else {
                error++;
            }
        }
    }

    // for branch lessons; contains container
    private void importSearchSetLessons( NodeList nodeListLessons) {
        Container resultLessonContainer;

        for( int temp = 0; temp < nodeListLessons.getLength(); temp++) {
            // lesson container
            if( nodeListLessons.item(temp).getNodeName().equals( "container")) {
                resultLessonContainer = searchSetContainer(nodeListLessons.item(temp));
                if( resultLessonContainer != null) {
                    fileFormats.lessonContainerList.add(resultLessonContainer);
                }
            }
            else {
                error++;
            }
        }
    }

    // for branch wordtypes; contains container
    private void importSearchSetWordTypes( NodeList nodeListWordTypes) {
        Container resultWordTypesContainer;

        for( int temp = 0; temp < nodeListWordTypes.getLength(); temp++) {
            // lesson container
            if( nodeListWordTypes.item(temp).getNodeName().equals( "container")) {
                resultWordTypesContainer = searchSetContainer(nodeListWordTypes.item(temp));
                if( resultWordTypesContainer != null) {
                    fileFormats.wordTypesContainerList.add(resultWordTypesContainer);
                }
            }
            else {
                error++;
            }
        }
    }
	
	// for branch leitnerboxes; contains container
    private void importSearchSetLeitnerboxes( NodeList nodeListWordTypes) {
        Container resultWordTypesContainer;

        for( int temp = 0; temp < nodeListWordTypes.getLength(); temp++) {
            // lesson container
            if( nodeListWordTypes.item(temp).getNodeName().equals( "container")) {
                resultWordTypesContainer = searchSetContainer(nodeListWordTypes.item(temp));
                if( resultWordTypesContainer != null) {
                    fileFormats.leitnerboxesContainerList.add(resultWordTypesContainer);
                }
            }
            else {
                error++;
            }
        }
    }

    // for sub branch container; lessons, wordtypes, leitnerboxes
    private Container searchSetContainer( Node nodeContainer) {
        Container container = new Container();
        Container resultWordTypesContainer;
        NodeList secondSubNodeList;
        String result = "";

        secondSubNodeList = nodeContainer.getChildNodes();

        for( int i = 0; i < secondSubNodeList.getLength(); i++) {
			// entry
            if( secondSubNodeList.item(i).getNodeName().equals( "entry")) {
                try {
                    container.entryList.add( Integer.parseInt(secondSubNodeList.item(i).getAttributes().getNamedItem("id").getTextContent()));
                }
                catch( Exception e) {
                    error++;
                }
            }
			// container
            else if( secondSubNodeList.item(i).getNodeName().equals( "container")) {
                resultWordTypesContainer = searchSetContainer(secondSubNodeList.item(i));
                if( resultWordTypesContainer != null) {
                    container.container.add(resultWordTypesContainer);
                }
            }
			// specialwordtype
            else if( secondSubNodeList.item(i).getNodeName().equals( "specialwordtype")) {
                result = secondSubNodeList.item(i).getTextContent();

                if( result.equals( Container.NOUN) || result.equals( Container.NOUN_MALE) || result.equals( Container.NOUN_FEMALE)
                    || result.equals( Container.NOUN_NEUTRAL) || result.equals( Container.VERB) || result.equals( Container.ADJECTIVE)
                    || result.equals( Container.ADVERB) || result.equals( Container.CONJUNCTION))
                    container.specialWordType = result;
            }
			// name
            else if( secondSubNodeList.item(i).getNodeName().equals( "name")) {
                container.name = secondSubNodeList.item(i).getTextContent();
            }
            // inpractice
            else if( secondSubNodeList.item(i).getNodeName().equals( "inpractice")) {
                result = secondSubNodeList.item(i).getTextContent();

                if( result.equals( "true") || result.equals( "1"))
                    container.inPractice = true;
            }
			// image
			else if( secondSubNodeList.item(i).getNodeName().equals( "image")) {
                container.image = secondSubNodeList.item(i).getTextContent();	
            }
        }
        return container;
    }

	public String export(FileFormats fileFormats) throws Throwable{
        StringBuilder export = new StringBuilder();
        export.append( "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<!DOCTYPE kvtml PUBLIC \"kvtml2.dtd\" \"http://edu.kde.org/kvtml/kvtml2.dtd\">\n" +
                "<kvtml version=\"2.0\">\n");

        // information
        String informationContent = buildTag("\t\t", "generator", fileFormats.information.generator) +
                                    buildTag("\t\t", "title", fileFormats.information.title) +
                                    buildTag("\t\t", "author", fileFormats.information.author) +
                                    buildTag("\t\t", "contact", fileFormats.information.contact) +
                                    buildTag("\t\t", "license", fileFormats.information.license) +
                                    buildTag("\t\t", "comment", fileFormats.information.comment) +
                                    buildTag("\t\t", "date", "" + fileFormats.information.date) +
                                    buildTag("\t\t", "category", fileFormats.information.category);

        export.append( buildTag("\t", "information", informationContent));

        // identifiers
        if( fileFormats.identifierList.size() > 0) {
            Iterator<Integer> identifierListIterator = fileFormats.identifierList.keySet().iterator();

            export.append( "\t</identifiers>\n");

            for( int i = 0; identifierListIterator.hasNext(); i++) {
                Integer identifierListKey = identifierListIterator.next();

                export.append( "\t\t<identifier id=\"" + identifierListKey + "\">\n");

                Identifier identifier = fileFormats.identifierList.get( identifierListKey);

                // identifier
                if( identifier != null) {
                    // content of definite in singular
                    String articleSingularDefiniteContent = buildTag("\t\t\t\t\t", "male", identifier.article.articleSingular.definite.male) +
                                                            buildTag("\t\t\t\t\t", "female", identifier.article.articleSingular.definite.female) +
                                                            buildTag("\t\t\t\t\t", "neutral", identifier.article.articleSingular.definite.neutral);

                    // content of indefinite in singular
                    String articleSingularIndefiniteContent = buildTag("\t\t\t\t\t", "male", identifier.article.articleSingular.indefinite.male) +
                                                                buildTag("\t\t\t\t\t", "female", identifier.article.articleSingular.indefinite.female) +
                                                                buildTag("\t\t\t\t\t", "neutral", identifier.article.articleSingular.indefinite.neutral);

                    // definite in singular
                    String articleSingularDefinite = buildTag("\t\t\t\t", "definite", articleSingularDefiniteContent);

                    // indefinite in singular
                    String articleSingularIndefinite = buildTag("\t\t\t\t", "definite", articleSingularIndefiniteContent);


                    // content of definite in plural
                    String articlePluralDefiniteContent = buildTag("\t\t\t\t\t", "male", identifier.article.articlePlural.definite.male) +
                                                            buildTag("\t\t\t\t\t", "female", identifier.article.articlePlural.definite.female) +
                                                            buildTag("\t\t\t\t\t", "neutral", identifier.article.articlePlural.definite.neutral);

                    // content of indefinite in plural
                    String articlePluralIndefiniteContent = buildTag("\t\t\t\t\t", "male", identifier.article.articlePlural.indefinite.male) +
                                                            buildTag("\t\t\t\t\t", "female", identifier.article.articlePlural.indefinite.female) +
                                                            buildTag("\t\t\t\t\t", "neutral", identifier.article.articlePlural.indefinite.neutral);

                    // definite in plural
                    String articlePluralDefinite = buildTag("\t\t\t\t", "definite", articlePluralDefiniteContent);

                    // indefinite in plural
                    String articlePluralIndefinite = buildTag("\t\t\t\t", "definite", articlePluralIndefiniteContent);


                    // singular
                    String articleSingular = buildTag("\t\t\t", "singular", articleSingularDefinite + articleSingularIndefinite);

                    // plural
                    String articlePlural = buildTag("\t\t\t", "plural", articlePluralDefinite + articlePluralIndefinite);


                    // article
                    String article = articleSingular + articlePlural;



                    // singular in personalpronouns
                    String personalPronounsSingularContent = buildTag("\t\t\t\t\t", "firstperson", identifier.personalPronouns.singular.firstPerson) +
                                                                buildTag("\t\t\t\t\t", "secondperson", identifier.personalPronouns.singular.secondPerson) +
                                                                buildTag("\t\t\t\t\t", "thirdpersonneutralcommon", identifier.personalPronouns.singular.thirdPersonFemale);

                    // plural in personalpronouns
                    String personalPronounsPluralContent = buildTag("\t\t\t\t\t", "firstperson", identifier.personalPronouns.plural.firstPerson) +
                            buildTag("\t\t\t\t\t", "secondperson", identifier.personalPronouns.plural.secondPerson) +
                            buildTag("\t\t\t\t\t", "thirdpersonneutralcommon", identifier.personalPronouns.plural.thirdPersonFemale);


                    // singular
                    String personalPronounsSingular = buildTag("\t\t\t\t", "singular", personalPronounsSingularContent);

                    // plural
                    String personalPronounsPlural = buildTag("\t\t\t\t", "plural", personalPronounsPluralContent);

                    // personalpronouns
                    String personalPronouns = buildTag("\t\t\t", "personalpronouns", personalPronounsSingular + personalPronounsPlural);



                    // tense
                    StringBuilder tense = new StringBuilder();
                    for( int n = 0; n < identifier.tenseList.size(); n++) {
                        tense.append( buildTag("\t\t\t", "tense", identifier.tenseList.get(n)));
                    }



                    String identifierContent = buildTag("\t\t\t", "name", identifier.name) +
                                                buildTag("\t\t\t", "locale", identifier.locale) +
                                                buildTag("\t\t\t", "comment", identifier.comment) +
                                                buildTag("\t\t\t", "sizeHint", "" + identifier.sizeHint) +
                                                article +
                                                personalPronouns +
                                                tense;
                }

                export.append( "\t\t</identifier>\n");
            }

            export.append( "\t</identifiers>\n");
        }


        // entries
        if( fileFormats.entryList.size() > 0) {
            Iterator<Integer> entryListIterator = fileFormats.entryList.keySet().iterator();

            export.append( "\t</entries>\n");

            for( int i = 0; entryListIterator.hasNext(); i++) {
                Integer entryListKey = entryListIterator.next();

                export.append( "\t\t<entry id=\"" + entryListKey + "\">\n");

                Entry entry = fileFormats.entryList.get( entryListKey);

                StringBuilder translation = new StringBuilder();

                if( entry.translationList.size() > 0) {
                    Iterator<Integer> translationListIterator = entry.translationList.keySet().iterator();

                    for( int n = 0; ; n++) {
                        Integer translationListKey = translationListIterator.next();

                        translation.append( "\t\t<translation id=\"" + translationListKey + "\">\n");

                        Translation entryTranslation = entry.translationList.get( translationListKey);

                        if( entryTranslation != null) {
                            // content of grade
                            String gradeContent = buildTag("\t\t\t\t", "currentgrade", "" + entryTranslation.grade.currentGrade) +
                                                    buildTag("\t\t\t\t", "count", "" + entryTranslation.grade.count) +
                                                    buildTag("\t\t\t\t", "errorcount", "" + entryTranslation.grade.errorCount) +
                                                    buildTag("\t\t\t\t", "date", "" + entryTranslation.grade.date);

                            // grade
                            String grade = buildTag( "\t\t\t", "grade", gradeContent);



                            // content of singular
                            String translationSingularContent = buildTag( "\t\t\t\t", "firstperson", entryTranslation.conjugation.singular.firstPerson) +
                                                                buildTag( "\t\t\t\t", "secondperson", entryTranslation.conjugation.singular.secondPerson) +
                                                                buildTag( "\t\t\t\t", "thirdpersonneutralcommon", entryTranslation.conjugation.singular.thirdPersonNeutralCommon);

                            // content of dual
                            String translationDualContent = buildTag( "\t\t\t\t", "firstperson", entryTranslation.conjugation.dual.firstPerson) +
                                    buildTag( "\t\t\t\t", "secondperson", entryTranslation.conjugation.dual.secondPerson) +
                                    buildTag( "\t\t\t\t", "thirdpersonneutralcommon", entryTranslation.conjugation.dual.thirdPersonNeutralCommon);

                            // content of plural
                            String translationPluralContent = buildTag( "\t\t\t\t", "firstperson", entryTranslation.conjugation.plural.firstPerson) +
                                    buildTag( "\t\t\t\t", "secondperson", entryTranslation.conjugation.plural.secondPerson) +
                                    buildTag( "\t\t\t\t", "thirdpersonneutralcommon", entryTranslation.conjugation.plural.thirdPersonNeutralCommon);


                            // conjugation
                            String conjugation = buildTag("\t\t\t", "conjugation", translationSingularContent + translationDualContent + translationPluralContent);



                            // content of singular in female
                            String declensionFemaleSingularContent = buildTag("\t\t\t\t\t\t", "nominative", entryTranslation.declension.femaleList.singular.nominative) +
                                                                buildTag("\t\t\t\t\t\t", "genitive", entryTranslation.declension.femaleList.singular.genitive) +
                                                                buildTag("\t\t\t\t\t\t", "dative", entryTranslation.declension.femaleList.singular.dative) +
                                                                buildTag("\t\t\t\t\t\t", "accusative", entryTranslation.declension.femaleList.singular.accusative) +
                                                                buildTag("\t\t\t\t\t\t", "ablative", entryTranslation.declension.femaleList.singular.ablative) +
                                                                buildTag("\t\t\t\t\t\t", "locative", entryTranslation.declension.femaleList.singular.locative) +
                                                                buildTag("\t\t\t\t\t\t", "vocative", entryTranslation.declension.femaleList.singular.vocative);

                            // content of dual in female
                            String declensionFemaleDualContent = buildTag("\t\t\t\t\t\t", "nominative", entryTranslation.declension.femaleList.dual.nominative) +
                                    buildTag("\t\t\t\t\t\t", "genitive", entryTranslation.declension.femaleList.dual.genitive) +
                                    buildTag("\t\t\t\t\t\t", "dative", entryTranslation.declension.femaleList.dual.dative) +
                                    buildTag("\t\t\t\t\t\t", "accusative", entryTranslation.declension.femaleList.dual.accusative) +
                                    buildTag("\t\t\t\t\t\t", "ablative", entryTranslation.declension.femaleList.dual.ablative) +
                                    buildTag("\t\t\t\t\t\t", "locative", entryTranslation.declension.femaleList.dual.locative) +
                                    buildTag("\t\t\t\t\t\t", "vocative", entryTranslation.declension.femaleList.dual.vocative);

                            // content of plural in female
                            String declensionFemalePluralContent = buildTag("\t\t\t\t\t\t", "nominative", entryTranslation.declension.femaleList.plural.nominative) +
                                    buildTag("\t\t\t\t\t\t", "genitive", entryTranslation.declension.femaleList.plural.genitive) +
                                    buildTag("\t\t\t\t\t\t", "dative", entryTranslation.declension.femaleList.plural.dative) +
                                    buildTag("\t\t\t\t\t\t", "accusative", entryTranslation.declension.femaleList.plural.accusative) +
                                    buildTag("\t\t\t\t\t\t", "ablative", entryTranslation.declension.femaleList.plural.ablative) +
                                    buildTag("\t\t\t\t\t\t", "locative", entryTranslation.declension.femaleList.plural.locative) +
                                    buildTag("\t\t\t\t\t\t", "vocative", entryTranslation.declension.femaleList.plural.vocative);

                            // singular in female
                            String declensionFemaleSingular = buildTag( "\t\t\t\t\t", "singular", declensionFemaleSingularContent);

                            // dual in female
                            String declensionFemaleDual = buildTag( "\t\t\t\t\t", "dual", declensionFemaleDualContent);

                            // plural in female
                            String declensionFemalePlural = buildTag( "\t\t\t\t\t", "plural", declensionFemalePluralContent);

                            // female in declension
                            String declensionFemale = buildTag("\t\t\t\t", "female", declensionFemaleSingular + declensionFemaleDual + declensionFemalePlural);


                            // content of singular in male
                            String declensionMaleSingularContent = buildTag("\t\t\t\t\t\t", "nominative", entryTranslation.declension.maleList.singular.nominative) +
                                    buildTag("\t\t\t\t\t\t", "genitive", entryTranslation.declension.maleList.singular.genitive) +
                                    buildTag("\t\t\t\t\t\t", "dative", entryTranslation.declension.maleList.singular.dative) +
                                    buildTag("\t\t\t\t\t\t", "accusative", entryTranslation.declension.maleList.singular.accusative) +
                                    buildTag("\t\t\t\t\t\t", "ablative", entryTranslation.declension.maleList.singular.ablative) +
                                    buildTag("\t\t\t\t\t\t", "locative", entryTranslation.declension.maleList.singular.locative) +
                                    buildTag("\t\t\t\t\t\t", "vocative", entryTranslation.declension.maleList.singular.vocative);

                            // content of dual in male
                            String declensionMaleDualContent = buildTag("\t\t\t\t\t\t", "nominative", entryTranslation.declension.maleList.dual.nominative) +
                                    buildTag("\t\t\t\t\t\t", "genitive", entryTranslation.declension.maleList.dual.genitive) +
                                    buildTag("\t\t\t\t\t\t", "dative", entryTranslation.declension.maleList.dual.dative) +
                                    buildTag("\t\t\t\t\t\t", "accusative", entryTranslation.declension.maleList.dual.accusative) +
                                    buildTag("\t\t\t\t\t\t", "ablative", entryTranslation.declension.maleList.dual.ablative) +
                                    buildTag("\t\t\t\t\t\t", "locative", entryTranslation.declension.maleList.dual.locative) +
                                    buildTag("\t\t\t\t\t\t", "vocative", entryTranslation.declension.maleList.dual.vocative);

                            // content of plural in male
                            String declensionMalePluralContent = buildTag("\t\t\t\t\t\t", "nominative", entryTranslation.declension.maleList.plural.nominative) +
                                    buildTag("\t\t\t\t\t\t", "genitive", entryTranslation.declension.maleList.plural.genitive) +
                                    buildTag("\t\t\t\t\t\t", "dative", entryTranslation.declension.maleList.plural.dative) +
                                    buildTag("\t\t\t\t\t\t", "accusative", entryTranslation.declension.maleList.plural.accusative) +
                                    buildTag("\t\t\t\t\t\t", "ablative", entryTranslation.declension.maleList.plural.ablative) +
                                    buildTag("\t\t\t\t\t\t", "locative", entryTranslation.declension.maleList.plural.locative) +
                                    buildTag("\t\t\t\t\t\t", "vocative", entryTranslation.declension.maleList.plural.vocative);

                            // singular in male
                            String declensionMaleSingular = buildTag( "\t\t\t\t\t", "singular",declensionMaleSingularContent);

                            // dual in male
                            String declensionMaleDual = buildTag( "\t\t\t\t\t", "dual",declensionMaleDualContent);

                            // plural in male
                            String declensionMalePlural = buildTag( "\t\t\t\t\t", "plural",declensionMalePluralContent);

                            // male in declension
                            String declensionMale = buildTag("\t\t\t\t", "male", declensionMaleSingular + declensionMaleDual + declensionMalePlural);


                            // content of singular in neutral
                            String declensionNeutralSingularContent = buildTag("\t\t\t\t\t\t", "nominative", entryTranslation.declension.neutralList.singular.nominative) +
                                    buildTag("\t\t\t\t\t\t", "genitive", entryTranslation.declension.neutralList.singular.genitive) +
                                    buildTag("\t\t\t\t\t\t", "dative", entryTranslation.declension.neutralList.singular.dative) +
                                    buildTag("\t\t\t\t\t\t", "accusative", entryTranslation.declension.neutralList.singular.accusative) +
                                    buildTag("\t\t\t\t\t\t", "ablative", entryTranslation.declension.neutralList.singular.ablative) +
                                    buildTag("\t\t\t\t\t\t", "locative", entryTranslation.declension.neutralList.singular.locative) +
                                    buildTag("\t\t\t\t\t\t", "vocative", entryTranslation.declension.neutralList.singular.vocative);

                            // content of dual in neutral
                            String declensionNeutralDualContent = buildTag("\t\t\t\t\t\t", "nominative", entryTranslation.declension.neutralList.dual.nominative) +
                                    buildTag("\t\t\t\t\t\t", "genitive", entryTranslation.declension.neutralList.dual.genitive) +
                                    buildTag("\t\t\t\t\t\t", "dative", entryTranslation.declension.neutralList.dual.dative) +
                                    buildTag("\t\t\t\t\t\t", "accusative", entryTranslation.declension.neutralList.dual.accusative) +
                                    buildTag("\t\t\t\t\t\t", "ablative", entryTranslation.declension.neutralList.dual.ablative) +
                                    buildTag("\t\t\t\t\t\t", "locative", entryTranslation.declension.neutralList.dual.locative) +
                                    buildTag("\t\t\t\t\t\t", "vocative", entryTranslation.declension.neutralList.dual.vocative);

                            // content of plural in neutral
                            String declensionNeutralPluralContent = buildTag("\t\t\t\t\t\t", "nominative", entryTranslation.declension.neutralList.plural.nominative) +
                                    buildTag("\t\t\t\t\t\t", "genitive", entryTranslation.declension.neutralList.plural.genitive) +
                                    buildTag("\t\t\t\t\t\t", "dative", entryTranslation.declension.neutralList.plural.dative) +
                                    buildTag("\t\t\t\t\t\t", "accusative", entryTranslation.declension.neutralList.plural.accusative) +
                                    buildTag("\t\t\t\t\t\t", "ablative", entryTranslation.declension.neutralList.plural.ablative) +
                                    buildTag("\t\t\t\t\t\t", "locative", entryTranslation.declension.neutralList.plural.locative) +
                                    buildTag("\t\t\t\t\t\t", "vocative", entryTranslation.declension.neutralList.plural.vocative);

                            // singular in neutral
                            String declensionNeutralSingular = buildTag( "\t\t\t\t\t", "singular",declensionNeutralSingularContent);

                            // dual in neutral
                            String declensionNeutralDual = buildTag( "\t\t\t\t\t", "dual",declensionNeutralDualContent);

                            // plural in neutral
                            String declensionNeutralPlural = buildTag( "\t\t\t\t\t", "plural",declensionNeutralPluralContent);

                            // neutral in declension
                            String declensionNeutral = buildTag("\t\t\t\t", "neutral", declensionNeutralSingular + declensionNeutralDual + declensionNeutralPlural);


                            // declension
                            String declension = buildTag("\t\t\t", "declension", declensionFemale + declensionMale + declensionNeutral);


                            String antonymPairEntries = "";
                            // pair in antonym
                            for( int d = 0; d < entryTranslation.antonymPair.size(); d++) {
                                antonymPairEntries += buildTag("\t\t\t\t", "pair", "<\t\t\t\t\tentry id=\"" + entryTranslation.antonymPair.get(d).first.entryId + ">\n" +
                                                                                                "\t\t\t\t\t\t<translation>" + entryTranslation.antonymPair.get(d).first.translationId + "</translation>\n" +
                                                                                            "\t\t\t\t\t</entry>\n" +
                                                                                            "\t\t\t\t\t<entry id=\"" + entryTranslation.antonymPair.get(d).second.entryId + ">\n" +
                                                                                                "\t\t\t\t\t\t<translation>" + entryTranslation.antonymPair.get(d).second.translationId + "</translation>\n" +
                                                                                            "\t\t\t\t\t</entry>\n");
                            }

                            // antonym
                            String antonym = buildTag("\t\t\t", "antonym", antonymPairEntries);


                            String synonymPairEntries = "";
                            // pair in synonym
                            for( int d = 0; d < entryTranslation.synonymPair.size(); d++) {
                                synonymPairEntries += buildTag("\t\t\t\t", "pair", "<\t\t\t\t\tentry id=\"" + entryTranslation.synonymPair.get(d).first.entryId + ">\n" +
                                        "\t\t\t\t\t\t<translation>" + entryTranslation.synonymPair.get(d).first.translationId + "</translation>\n" +
                                        "\t\t\t\t\t</entry>\n" +
                                        "\t\t\t\t\t<entry id=\"" + entryTranslation.synonymPair.get(d).second.entryId + ">\n" +
                                        "\t\t\t\t\t\t<translation>" + entryTranslation.synonymPair.get(d).second.translationId + "</translation>\n" +
                                        "\t\t\t\t\t</entry>\n");
                            }

                            // synonym
                            String synonym = buildTag("\t\t\t", "synonym", synonymPairEntries);



                            // content of comparison
                            String comparisonContent = buildTag("\t\t\t\t", "absolute", entryTranslation.comparison.absolute) +
                                                        buildTag("\t\t\t\t", "comparative", entryTranslation.comparison.comparative) +
                                                        buildTag("\t\t\t\t", "superlative", entryTranslation.comparison.superlative);

                            // comparison
                            String comparison = buildTag("\t\t\t", "comparison", comparisonContent);



                            String choices = "";
                            // choices in multiple choice
                            for( int d = 0; d < entryTranslation.multipleChoice.choice.size(); d++) {
                                choices = buildTag("\t\t\t\t", "choice", entryTranslation.multipleChoice.choice.get(d));
                            }

                            // multiple choice
                            String multipleChoice = buildTag("\t\t\t", "multiplechoice", choices);



                            translation.append( buildTag("\t\t\t", "text", entryTranslation.text) +
                                    grade +
                                    conjugation +
                                    declension +
                                    buildTag("\t\t\t", "comment", entryTranslation.comment) +
                                    buildTag("\t\t\t", "pronunciation", entryTranslation.pronunciation) +
                                    buildTag("\t\t\t", "example", entryTranslation.example) +
                                    buildTag("\t\t\t", "paraphrase", entryTranslation.paraphrase) +
                                    //public String falseFriend = ""; with attr id, id is unique
                                    antonym +
                                    synonym +
                                    comparison +
                                    multipleChoice +
                                    buildTag("\t\t\t", "image", entryTranslation.image) +
                                    buildTag("\t\t\t", "sound", entryTranslation.sound));
                        }


                        translation.append( "\t\t</translation>");
                    }
                }



                // entry
                if( entry != null) {
                    export.append( buildTag("\t\t\t", "deactivated", String.valueOf( entry.deactivated)) +
                                    buildTag("\t\t\t", "sizehint", "" + entry.sizeHint) +
                                    translation);
                }

                export.append( "\t\t</entry>\n");
            }

            export.append( "\t</entries>\n");
        }



        // container
        export.append( buildTag( "\t", "lessons", buildContainerTags( fileFormats.lessonContainerList, 2)));

        // wordtypes
        export.append( buildTag( "\t", "wordtypes", buildContainerTags( fileFormats.wordTypesContainerList, 2)));

        // leitnerboxes
        export.append( buildTag( "\t", "leitnerboxes", buildContainerTags( fileFormats.leitnerboxesContainerList, 2)));


        export.append("kvtml");

		return export.toString();
	}

    // container content in lessons, wordTypes,leitnerboxes
    private String buildContainerTags(ArrayList<Container> containerList, int tabLenghtStart) {
        String containerContent = "";

        for( int i = 0; i < containerList.size(); i++) {
            StringBuilder entry = new StringBuilder();

            // entry content in lessons, wordTypes,leitnerboxes
            for( int n = 0; n < containerList.get(i).entryList.size(); n++) {
                entry.append( "<" + multiplyString( "\t", tabLenghtStart + 1) + "entry id=\"" + containerList.get(i).entryList.get(n).intValue() + "\"/>\n");
            }

            String container = "";
            if( containerList.get(i).container.size() > 0) {
                container = buildContainerTags(containerList.get(i).container, tabLenghtStart + 2);
            }

            containerContent = buildTag( multiplyString( "\t", tabLenghtStart + 1), "name", containerList.get(i).name) +
                    buildTag( multiplyString( "\t", tabLenghtStart + 1), "specialwordtype", containerList.get(i).specialWordType) +
                    buildTag( multiplyString( "\t", tabLenghtStart + 1), "inpractice", String.valueOf( containerList.get(i).inPractice)) +
                    container +
                    entry +
                    buildTag( multiplyString( "\t", tabLenghtStart + 1), "image", containerList.get(i).image);

        }
        return buildTag( multiplyString( "\t", tabLenghtStart), "container", containerContent);
    }

    private String multiplyString( String string, int times) {
        StringBuilder result = new StringBuilder();

        for( int i = 0; i < times; i++) {
            result.append( string);
        }

        return result.toString();
    }

    private String buildTag( String tab, String tag, String content) {
        String result = "";

        if( !content.equals( "") || !content.equals("-1")) {
            if( content.contains("\n"))
                result = tab + "<" + tag + ">\n" + content + "</" + tag + ">\n";
            else
                result = tab + "<" + tag + ">" + content + "</" + tag + ">\n";
        }

        return result;
    }
}
