package at.rhomberg.parser;

import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.sql.Date;

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
                    Log.d( "ParleyDrone info", "start parsing information");
                    eElement = (Element) nNode;
                    importSearchSetInformation( eElement);
                    Log.d( "ParleyDrone info", "finished parsing information");
                }
            }
            else if( qName.equals( "identifiers")) {
                if( subNodeList.getLength() > 0) {
                    Log.d( "ParleyDrone info", "start parsing identifiers");
                    importSearchSetIdentifiers( subNodeList);
                    Log.d( "ParleyDrone info", "finished parsing identifiers");
                }
            }
            else if( qName.equals( "entries")) {
                if( subNodeList.getLength() > 0) {
                    Log.d( "ParleyDrone info", "start parsing entries");
                    importSearchSetEntries( subNodeList);
                    Log.d( "ParleyDrone info", "finished parsing entries");
                }
            }
            else if( qName.equals( "lessons")) {
                if( subNodeList.getLength() > 0) {
                    Log.d( "ParleyDrone info", "start parsing lessons");
                    importSearchSetLessons( subNodeList);
                    Log.d( "ParleyDrone info", "finished parsing lessons");
                }
            }
            else if( qName.equals( "wordtypes")) {
                if( subNodeList.getLength() > 0) {
                    Log.d( "ParleyDrone info", "start parsing wordtypes");
                    importSearchSetWordTypes( subNodeList);
                    Log.d( "ParleyDrone info", "finished parsing wordtypes");
                }
            }
			else if( qName.equals( "leitnerboxes")) {
                if( subNodeList.getLength() > 0) {
                    Log.d( "ParleyDrone info", "start parsing leitnerboxes");
                    importSearchSetLeitnerboxes( subNodeList);
                    Log.d( "ParleyDrone info", "finished parsing leitnerboxes");
                }
            }
            else {
                Log.d( "ParleyDrone info", "an error occured");
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
                fileFormats.info.generator = node.getTextContent();
        }

		// title
        subNodeList = element.getElementsByTagName("title");
        if( subNodeList.getLength() > 0) {
            node = subNodeList.item(0);
            if( node != null)
                fileFormats.info.title = node.getTextContent();
        }

		// author
        subNodeList = element.getElementsByTagName("author");
        if( subNodeList.getLength() > 0) {
            node = subNodeList.item(0);
            if( node != null)
                fileFormats.info.author = node.getTextContent();
        }

		// contact
        subNodeList = element.getElementsByTagName("contact");
        if( subNodeList.getLength() > 0) {
            node = subNodeList.item(0);
            if( node != null)
                fileFormats.info.contact = node.getTextContent();
        }

		// license
        subNodeList = element.getElementsByTagName("license");
        if( subNodeList.getLength() > 0) {
            node = subNodeList.item(0);
            if( node != null)
                fileFormats.info.license = node.getTextContent();
        }

		// comment
        subNodeList = element.getElementsByTagName("comment");
        if( subNodeList.getLength() > 0) {
            node = subNodeList.item(0);
            if( node != null)
                fileFormats.info.comment = node.getTextContent();
        }

		// date
        subNodeList = element.getElementsByTagName("date");
        if( subNodeList.getLength() > 0) {
            node = subNodeList.item(0);
            if( node != null) {
                try {
                    fileFormats.info.date = Date.valueOf( node.getTextContent());
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
                fileFormats.info.category = node.getTextContent();
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
                            Log.d("ParleyDrone info", "translation list added" + idSubResult + translation.text);
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
                    fileFormats.wordTypesContainerList.add(resultWordTypesContainer);
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
		
		return null;
	}
}
