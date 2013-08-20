package at.rhomberg.fileformats.rhomberg.parser;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.sql.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import at.rhomberg.fileformats.rhomberg.fileformats.Container;
import at.rhomberg.fileformats.rhomberg.fileformats.Entry;
import at.rhomberg.fileformats.rhomberg.fileformats.FileFormats;
import at.rhomberg.fileformats.rhomberg.fileformats.Identifier;
import at.rhomberg.fileformats.rhomberg.fileformats.Translation;


public class KVTML2Parser implements ImportExportInterface {

	private FileFormats fileFormats;
	private int error = 0;
	private String result = "";
    private int idResult;
    private NodeList subNodeList, secondSubNodeList, thirdSubNodeList, fourthSubNodeList, fithSubNodeList;
    private Node node, subNode, secondNode, thirdNode, fourthNode;
    private Element element, subElement, secondSubElement, thirdSubElement;

	public FileFormats importf(String textFile) throws Throwable {

		fileFormats = new FileFormats();

		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse( textFile);
		
		doc.getDocumentElement().normalize();

        if( (doc.getDocumentElement().getNodeName() != "kvtml") || (doc.getDocumentElement().getAttribute("version") != "2.0"))
            throw new IllegalArgumentException();

        NodeList nodeList = doc.getDocumentElement().getChildNodes();

        // search the main branches
        for( int temp = 0; temp < nodeList.getLength(); temp++) {

            Node nNode = nodeList.item( temp);
            NodeList subNodeList;
            Element eElement;

            String qName = nNode.getNodeName();
            subNodeList = nNode.getChildNodes();

            if( qName == "information") {
                if( nNode.getNodeType() == Node.ELEMENT_NODE) {
                    eElement = (Element) nNode;
                    importSearchSetInformation( eElement);
                }
            }
            else if( qName == "identifiers") {
                if( subNodeList.getLength() > 0) {
                    importSearchSetIdentifiers( subNodeList);
                }
            }
            else if( qName == "entries") {
                if( subNodeList.getLength() > 0) {
                    importSearchSetEntries( subNodeList);
                }
            }
            else if( qName == "lessons") {
                if( subNodeList.getLength() > 0) {
                    importSearchSetLessons( subNodeList);
                }
            }
            else if( qName == "wordtypes") {
                if( subNodeList.getLength() > 0) {
                    importSearchSetWordTypes( subNodeList);
                }
            }
			else if( qName == "leitnerboxes") {
                if( subNodeList.getLength() > 0) {
                    importSearchSetLeitnerboxes( subNodeList);
                }
            }
            else {
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
			if( nodeListIdentifiers.item(temp).getNodeName() == "identifier") {

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
                        if( node.getNodeName() == "singular") {
                            thirdSubNodeList = node.getChildNodes();

                            for( int t = 0; t < thirdSubNodeList.getLength(); t++) {
                                subNode = thirdSubNodeList.item(t);

                                // definite
                                if( subNode.getNodeName() == "definite") {
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
                                else if( subNode.getNodeName() == "indefinite") {
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
                        else if( node.getNodeName() == "plural") {
                            thirdSubNodeList = node.getChildNodes();

                            for( int t = 0; t < thirdSubNodeList.getLength(); t++) {
                                subNode = thirdSubNodeList.item(t);

                                // definite
                                if( subNode.getNodeName() == "definite") {
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
                                else if( subNode.getNodeName() == "indefinite") {
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
                            if( node.getNodeName() == "malefemaledifferent")
                                identifier.personalPronouns.maleFemaleDifferent = true;

                            // neutralexists
                            if( node.getNodeName() == "neutralexists")
                                identifier.personalPronouns.neutralExists = true;

                            // dualexists
                            if( node.getNodeName() == "dualexists")
                                identifier.personalPronouns.dualExists = true;

                            for( int t = 0; t < thirdSubNodeList.getLength(); t++) {
                                // singular
                                if( thirdSubNodeList.item(t).getNodeName() == "singular") {
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
                                else if( thirdSubNodeList.item(t).getNodeName() == "dual") {
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
                                else if( thirdSubNodeList.item(t).getNodeName() == "plural") {
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
            if( nodeListEntries.item(temp).getNodeName() == "entry") {
                secondSubNodeList = nodeListEntries.item(temp).getChildNodes();

                // id of entry
                try {
                    idResult = Integer.parseInt(secondSubNodeList.item(temp).getAttributes().getNamedItem("id").getTextContent());

                }
                catch( Exception e) {
                    error++;
                    continue out;
                }

                second: for( int i = 0; i < secondSubNodeList.getLength(); i++) {
                    if( secondSubNodeList.item(i).getNodeName() == "translation") {
                        if( secondSubNodeList.item(i).getNodeType() == Node.ELEMENT_NODE) {

                            // id of translation
                            try {
                                idResult = Integer.parseInt(secondSubNodeList.item(i).getAttributes().getNamedItem("id").getTextContent());
                            }
                            catch( Exception e) {
                                error++;
                                continue second;
                            }

                            translation = new Translation();
                            element = (Element) secondSubNodeList.item(i);

                            // text
                            subNodeList = element.getElementsByTagName("text");
                            if( subNodeList.getLength() > 0) {
                                node = subNodeList.item(0);
                                if( node != null)
                                    translation.text = node.getTextContent();
                            }

                            // grade
                            subNodeList = element.getElementsByTagName("grade");
                            if( subNodeList.getLength() > 0) {
                                node = subNodeList.item(0);
                                if( node != null) {
								
								
								
								// fromid
								
								
								
								
								
								
								
								
								
								
								
								
                                    if( node.getNodeType() == Node.ELEMENT_NODE) {
                                        subElement = (Element) node;

                                        // currentgrade
                                        secondSubNodeList = subElement.getElementsByTagName("currentgrade");
                                        if( secondSubNodeList.getLength() > 0) {
                                            subNode = secondSubNodeList.item(0);
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
                                        secondSubNodeList = subElement.getElementsByTagName("count");
                                        if( secondSubNodeList.getLength() > 0) {
                                            subNode = secondSubNodeList.item(0);
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
                                        secondSubNodeList = subElement.getElementsByTagName("errorcount");
                                        if( secondSubNodeList.getLength() > 0) {
                                            subNode = secondSubNodeList.item(0);
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
                                        secondSubNodeList = subElement.getElementsByTagName("date");
                                        if( secondSubNodeList.getLength() > 0) {
                                            subNode = secondSubNodeList.item(0);
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
                            subNodeList = element.getElementsByTagName("conjugation");
                            if( subNodeList.getLength() > 0) {
                                node = subNodeList.item(0);
                                if( node != null) {
                                    if( node.getNodeType() == Node.ELEMENT_NODE) {
                                        subElement = (Element) node;

                                        // tense
                                        secondSubNodeList = subElement.getElementsByTagName("tense");
                                        if( secondSubNodeList.getLength() > 0) {
                                            subNode = secondSubNodeList.item(0);
                                            if( subNode != null)
                                                translation.conjugation.tense = subNode.getTextContent();
                                        }

                                        // singular
                                        secondSubNodeList = subElement.getElementsByTagName("singular");
                                        if( secondSubNodeList.getLength() > 0) {
                                            subNode = secondSubNodeList.item(0);
                                            if( subNode != null) {
                                                if( subNode.getNodeType() == Node.ELEMENT_NODE) {
                                                    secondSubElement = (Element) subNode;

                                                    // firstperson
                                                    thirdSubNodeList = subElement.getElementsByTagName("firstperson");
                                                    if( thirdSubNodeList.getLength() > 0) {
                                                        secondNode = thirdSubNodeList.item(0);
                                                        if( secondNode != null) {
                                                            if( secondNode.getNodeType() == Node.ELEMENT_NODE) {
                                                                secondSubElement = (Element) secondNode;

                                                                fourthSubNodeList = secondSubElement.getElementsByTagName("text");
                                                                if( fourthSubNodeList.getLength() > 0) {
                                                                    thirdNode = fourthSubNodeList.item(0);
                                                                    if( thirdNode != null)
                                                                        translation.conjugation.singular.firstPerson = thirdNode.getTextContent();
                                                                }
                                                            }
                                                        }
                                                    }

                                                    // secondperson
                                                    thirdSubNodeList = subElement.getElementsByTagName("secondperson");
                                                    if( thirdSubNodeList.getLength() > 0) {
                                                        secondNode = thirdSubNodeList.item(0);
                                                        if( secondNode != null) {
                                                            if( secondNode.getNodeType() == Node.ELEMENT_NODE) {
                                                                secondSubElement = (Element) secondNode;

                                                                fourthSubNodeList = secondSubElement.getElementsByTagName("text");
                                                                if( fourthSubNodeList.getLength() > 0) {
                                                                    thirdNode = fourthSubNodeList.item(0);
                                                                    if( thirdNode != null)
                                                                        translation.conjugation.singular.secondPerson = thirdNode.getTextContent();
                                                                }
                                                            }
                                                        }
                                                    }

                                                    // thirdpersonneutralcommon
                                                    thirdSubNodeList = subElement.getElementsByTagName("thirdpersonneutralcommon");
                                                    if( thirdSubNodeList.getLength() > 0) {
                                                        secondNode = thirdSubNodeList.item(0);
                                                        if( secondNode != null) {
                                                            if( secondNode.getNodeType() == Node.ELEMENT_NODE) {
                                                                secondSubElement = (Element) secondNode;

                                                                fourthSubNodeList = secondSubElement.getElementsByTagName("text");
                                                                if( fourthSubNodeList.getLength() > 0) {
                                                                    thirdNode = fourthSubNodeList.item(0);
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
                                        secondSubNodeList = subElement.getElementsByTagName("dual");
                                        if( secondSubNodeList.getLength() > 0) {
                                            subNode = secondSubNodeList.item(0);
                                            if( subNode != null) {
                                                if( subNode.getNodeType() == Node.ELEMENT_NODE) {
                                                    secondSubElement = (Element) subNode;

                                                    // firstperson
                                                    thirdSubNodeList = subElement.getElementsByTagName("firstperson");
                                                    if( thirdSubNodeList.getLength() > 0) {
                                                        secondNode = thirdSubNodeList.item(0);
                                                        if( secondNode != null) {
                                                            if( secondNode.getNodeType() == Node.ELEMENT_NODE) {
                                                                secondSubElement = (Element) secondNode;

                                                                fourthSubNodeList = secondSubElement.getElementsByTagName("text");
                                                                if( fourthSubNodeList.getLength() > 0) {
                                                                    thirdNode = fourthSubNodeList.item(0);
                                                                    if( thirdNode != null)
                                                                        translation.conjugation.dual.firstPerson = thirdNode.getTextContent();
                                                                }
                                                            }
                                                        }
                                                    }

                                                    // secondperson
                                                    thirdSubNodeList = subElement.getElementsByTagName("secondperson");
                                                    if( thirdSubNodeList.getLength() > 0) {
                                                        secondNode = thirdSubNodeList.item(0);
                                                        if( secondNode != null) {
                                                            if( secondNode.getNodeType() == Node.ELEMENT_NODE) {
                                                                secondSubElement = (Element) secondNode;

                                                                fourthSubNodeList = secondSubElement.getElementsByTagName("text");
                                                                if( fourthSubNodeList.getLength() > 0) {
                                                                    thirdNode = fourthSubNodeList.item(0);
                                                                    if( thirdNode != null)
                                                                        translation.conjugation.dual.secondPerson = thirdNode.getTextContent();
                                                                }
                                                            }
                                                        }
                                                    }

                                                    // thirdpersonneutralcommon
                                                    thirdSubNodeList = subElement.getElementsByTagName("thirdpersonneutralcommon");
                                                    if( thirdSubNodeList.getLength() > 0) {
                                                        secondNode = thirdSubNodeList.item(0);
                                                        if( secondNode != null) {
                                                            if( secondNode.getNodeType() == Node.ELEMENT_NODE) {
                                                                secondSubElement = (Element) secondNode;

                                                                fourthSubNodeList = secondSubElement.getElementsByTagName("text");
                                                                if( fourthSubNodeList.getLength() > 0) {
                                                                    thirdNode = fourthSubNodeList.item(0);
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
                                        secondSubNodeList = subElement.getElementsByTagName("plural");
                                        if( secondSubNodeList.getLength() > 0) {
                                            subNode = secondSubNodeList.item(0);
                                            if( subNode != null) {
                                                if( subNode.getNodeType() == Node.ELEMENT_NODE) {
                                                    secondSubElement = (Element) subNode;

                                                    // firstperson
                                                    thirdSubNodeList = subElement.getElementsByTagName("firstperson");
                                                    if( thirdSubNodeList.getLength() > 0) {
                                                        secondNode = thirdSubNodeList.item(0);
                                                        if( secondNode != null) {
                                                            if( secondNode.getNodeType() == Node.ELEMENT_NODE) {
                                                                secondSubElement = (Element) secondNode;

                                                                fourthSubNodeList = secondSubElement.getElementsByTagName("text");
                                                                if( fourthSubNodeList.getLength() > 0) {
                                                                    thirdNode = fourthSubNodeList.item(0);
                                                                    if( thirdNode != null)
                                                                        translation.conjugation.plural.firstPerson = thirdNode.getTextContent();
                                                                }
                                                            }
                                                        }
                                                    }

                                                    // secondperson
                                                    thirdSubNodeList = subElement.getElementsByTagName("secondperson");
                                                    if( thirdSubNodeList.getLength() > 0) {
                                                        secondNode = thirdSubNodeList.item(0);
                                                        if( secondNode != null) {
                                                            if( secondNode.getNodeType() == Node.ELEMENT_NODE) {
                                                                secondSubElement = (Element) secondNode;

                                                                fourthSubNodeList = secondSubElement.getElementsByTagName("text");
                                                                if( fourthSubNodeList.getLength() > 0) {
                                                                    thirdNode = fourthSubNodeList.item(0);
                                                                    if( thirdNode != null)
                                                                        translation.conjugation.plural.secondPerson = thirdNode.getTextContent();
                                                                }
                                                            }
                                                        }
                                                    }

                                                    // thirdpersonneutralcommon
                                                    thirdSubNodeList = subElement.getElementsByTagName("thirdpersonneutralcommon");
                                                    if( thirdSubNodeList.getLength() > 0) {
                                                        secondNode = thirdSubNodeList.item(0);
                                                        if( secondNode != null) {
                                                            if( secondNode.getNodeType() == Node.ELEMENT_NODE) {
                                                                secondSubElement = (Element) secondNode;

                                                                fourthSubNodeList = secondSubElement.getElementsByTagName("text");
                                                                if( fourthSubNodeList.getLength() > 0) {
                                                                    thirdNode = fourthSubNodeList.item(0);
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
                            subNodeList = element.getElementsByTagName("declension");
                            if( subNodeList.getLength() > 0) {
                                node = subNodeList.item(0);
                                if( node != null) {
                                    if( node.getNodeType() == Node.ELEMENT_NODE) {
                                        subElement = (Element) node;

                                        // female
                                        secondSubNodeList = subElement.getElementsByTagName("female");
                                        if( secondSubNodeList.getLength() > 0) {
                                            subNode = secondSubNodeList.item(0);
                                            if( subNode != null) {
                                                if( subNode.getNodeType() == Node.ELEMENT_NODE) {
                                                    secondSubElement = (Element) subNode;

                                                    // singular
                                                    thirdSubNodeList = subElement.getElementsByTagName("singular");
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
                                                                                    translation.declension.femaleList.singular.nominative = fourthNode.getTextContent();
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
                                                                                    translation.declension.femaleList.singular.genitive = fourthNode.getTextContent();
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
                                                                                    translation.declension.femaleList.singular.dative = fourthNode.getTextContent();
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
                                                                                if( fourthNode != null) {
                                                                                    translation.declension.femaleList.singular.accusative = fourthNode.getTextContent();
                                                                                }
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
                                                                                    translation.declension.femaleList.singular.ablative = fourthNode.getTextContent();
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
                                                                                    translation.declension.femaleList.singular.locative = fourthNode.getTextContent();
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
                                                                                    translation.declension.femaleList.singular.vocative = fourthNode.getTextContent();
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
                                                                                    translation.declension.femaleList.dual.nominative = fourthNode.getTextContent();
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
                                                                                    translation.declension.femaleList.dual.genitive = fourthNode.getTextContent();
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
                                                                                    translation.declension.femaleList.dual.dative = fourthNode.getTextContent();
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
                                                                                    translation.declension.femaleList.dual.accusative = fourthNode.getTextContent();
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
                                                                                    translation.declension.femaleList.dual.ablative = fourthNode.getTextContent();
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
                                                                                    translation.declension.femaleList.dual.locative = fourthNode.getTextContent();
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
                                                                                    translation.declension.femaleList.dual.vocative = fourthNode.getTextContent();
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
                                                                                    translation.declension.femaleList.plural.nominative = fourthNode.getTextContent();
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
                                                                                    translation.declension.femaleList.plural.genitive = fourthNode.getTextContent();
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
                                                                                    translation.declension.femaleList.plural.dative = fourthNode.getTextContent();
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
                                                                                    translation.declension.femaleList.plural.accusative = fourthNode.getTextContent();
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
                                                                                    translation.declension.femaleList.plural.ablative = fourthNode.getTextContent();
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
                                                                                    translation.declension.femaleList.plural.locative = fourthNode.getTextContent();
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
                                        secondSubNodeList = subElement.getElementsByTagName("male");
                                        if( secondSubNodeList.getLength() > 0) {
                                            subNode = secondSubNodeList.item(0);
                                            if( subNode != null) {
                                                if( subNode.getNodeType() == Node.ELEMENT_NODE) {
                                                    secondSubElement = (Element) subNode;

                                                    // singular
                                                    thirdSubNodeList = subElement.getElementsByTagName("singular");
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
                                                                                    translation.declension.maleList.singular.nominative = fourthNode.getTextContent();
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
                                                                                    translation.declension.maleList.singular.genitive = fourthNode.getTextContent();
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
                                                                                    translation.declension.maleList.singular.dative = fourthNode.getTextContent();
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
                                                                                    translation.declension.maleList.singular.accusative = fourthNode.getTextContent();
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
                                                                                    translation.declension.maleList.singular.ablative = fourthNode.getTextContent();
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
                                                                                    translation.declension.maleList.singular.locative = fourthNode.getTextContent();
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
                                        secondSubNodeList = subElement.getElementsByTagName("neutral");
                                        if( secondSubNodeList.getLength() > 0) {
                                            subNode = secondSubNodeList.item(0);
                                            if( subNode != null) {
                                                if( subNode.getNodeType() == Node.ELEMENT_NODE) {
                                                    secondSubElement = (Element) subNode;

                                                    // singular
                                                    thirdSubNodeList = subElement.getElementsByTagName("singular");
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
                                                                                    translation.declension.neutralList.singular.nominative = fourthNode.getTextContent();
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
                                                                                    translation.declension.neutralList.singular.genitive = fourthNode.getTextContent();
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
                                                                                    translation.declension.neutralList.singular.dative = fourthNode.getTextContent();
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
                                                                                    translation.declension.neutralList.singular.accusative = fourthNode.getTextContent();
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
                                                                                    translation.declension.neutralList.singular.ablative = fourthNode.getTextContent();
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
                                                                                    translation.declension.neutralList.singular.locative = fourthNode.getTextContent();
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
                                                                                    translation.declension.neutralList.singular.vocative = fourthNode.getTextContent();
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
                                                                                    translation.declension.neutralList.dual.nominative = fourthNode.getTextContent();
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
                                                                                    translation.declension.neutralList.dual.genitive = fourthNode.getTextContent();
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
                                                                                    translation.declension.neutralList.dual.dative = fourthNode.getTextContent();
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
                                                                                    translation.declension.neutralList.dual.accusative = fourthNode.getTextContent();
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
                                                                                    translation.declension.neutralList.dual.ablative = fourthNode.getTextContent();
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
                                                                                    translation.declension.neutralList.dual.locative = fourthNode.getTextContent();
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
                                                                                    translation.declension.neutralList.dual.vocative = fourthNode.getTextContent();
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
                                                                                    translation.declension.neutralList.plural.nominative = fourthNode.getTextContent();
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
                                                                                    translation.declension.neutralList.plural.genitive = fourthNode.getTextContent();
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
                                                                                    translation.declension.neutralList.plural.dative = fourthNode.getTextContent();
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
                                                                                    translation.declension.neutralList.plural.accusative = fourthNode.getTextContent();
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
                                                                                    translation.declension.neutralList.plural.ablative = fourthNode.getTextContent();
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
                                                                                    translation.declension.neutralList.plural.locative = fourthNode.getTextContent();
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
                            subNodeList = element.getElementsByTagName("comment");
                            if( subNodeList.getLength() > 0) {
                                node = subNodeList.item(0);
                                if( node != null)
                                    translation.comment = node.getTextContent();
                            }

                            // pronunciation
                            subNodeList = element.getElementsByTagName("pronunciation");
                            if( subNodeList.getLength() > 0) {
                                node = subNodeList.item(0);
                                if( node != null)
                                    translation.pronunciation = node.getTextContent();
                            }

                            // example
                            subNodeList = element.getElementsByTagName("example");
                            if( subNodeList.getLength() > 0) {
                                node = subNodeList.item(0);
                                if( node != null)
                                    translation.example = node.getTextContent();
                            }
							
                            // paraphrase
                            subNodeList = element.getElementsByTagName("paraphrase");
                            if( subNodeList.getLength() > 0) {
                                node = subNodeList.item(0);
                                if( node != null)
                                    translation.paraphrase = node.getTextContent();
                            }
							
							// falsefriend
							subNodeList = element.getElementsByTagName("falsefriend");
							if( subNodeList.getLength() > 0) {
								node = subNodeList.item(0);
							}
							
							
							
							
							
							
							
							// antonym
                            subNodeList = element.getElementsByTagName("antonym");
                            if( subNodeList.getLength() > 0) {
                                node = subNodeList.item(0);
                                if( node != null)
                                    translation.antonym = node.getTextContent();
                            }
							
							
							
							
							
							
							
							
							
							
							// synonym
                            subNodeList = element.getElementsByTagName("synonym");
                            if( subNodeList.getLength() > 0) {
                                node = subNodeList.item(0);
                                if( node != null)
                                    translation.synonym = node.getTextContent();
                            }
							
							
							
							
							
							
							
							
							
							
							
							
							
							// comparison
							subNodeList = element.getElementsByTagName("comparison");
							if( subNodeList.getLength() > 0) {
								node = subNodeList.item(0);
								if( node != null) {
									if( node.getNodeType() == Node.ELEMENT_NODE) {
                                        subElement = (Element) node;
										
										// absolute
										secondSubNodeList = subElement.getElementsByTagName("absolute");
										if( secondSubNodeList.getLength() > 0) {
											secondNode = secondSubNodeList.item(0);
											if( secondNode != null)
												translation.comparison.absolute = secondNode.getTextContent();
										}
										
										// comparative
										secondSubNodeList = subElement.getElementsByTagName("comparative");
										if( secondSubNodeList.getLength() > 0) {
											secondNode = secondSubNodeList.item(0);
											if( secondNode != null)
												translation.comparison.comparative = secondNode.getTextContent();
										}
										
										// superlative
										secondSubNodeList = subElement.getElementsByTagName("superlative");
										if( secondSubNodeList.getLength() > 0) {
											secondNode = secondSubNodeList.item(0);
											if( secondNode != null)
												translation.comparison.superlative = secondNode.getTextContent();
										}
									}
								}
							}

							// multipleChoice
							subNodeList = element.getElementsByTagName("multiplechoice");
                            if( subNodeList.getLength() > 0) {
                                node = subNodeList.item(0);
                                if( node != null) {
									secondSubNodeList = node.getChildNodes();
									
									for( int t = 0; secondSubNodeList.getLength() > 0; t++) {
										if( secondSubNodeList.item(t).getNodeName() == "choice") {
											if( secondSubNodeList.getLength() > 0) {
												secondNode = secondSubNodeList.item(0);
													if( secondNode != null) {
														translation.multipleChoice.choice.add(secondNode.getTextContent());
												}
											}
										}
										else {
											error++;
										}
									}	
								}
							}

							// image
							subNodeList = element.getElementsByTagName("image");
                            if( subNodeList.getLength() > 0) {
                                node = subNodeList.item(0);
                                if( node != null)
                                    translation.image = node.getTextContent();
                            }
							
							// sound
							subNodeList = element.getElementsByTagName("sound");
                            if( subNodeList.getLength() > 0) {
                                node = subNodeList.item(0);
                                if( node != null)
                                    translation.sound = node.getTextContent();
                            }
							
                            entry.translationList.put(idResult, translation);
                        }
                    }
					
                    // deactivated
                    else if( secondSubNodeList.item(i).getNodeName() == "deactivated") {
                        if( secondSubNodeList.getLength() > 0) {
                            node = secondSubNodeList.item(0);
                            if( node != null) {
                                result = node.getTextContent();

                                if( (result == "true") || (result == "1"))
                                    entry.deactivated = true;
                            }
                        }
                    }
					
                    // sizehint
                    else if( secondSubNodeList.item(i).getNodeName() == "sizehint") {
                        if( secondSubNodeList.getLength() > 0) {
                            node = secondSubNodeList.item(0);
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
                }

                fileFormats.entryList.put(idResult, entry);
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
            if( nodeListLessons.item(temp).getNodeName() == "container") {
                resultLessonContainer = SearchSetContainer(nodeListLessons.item(temp));
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
            if( nodeListWordTypes.item(temp).getNodeName() == "container") {
                resultWordTypesContainer = SearchSetContainer(nodeListWordTypes.item(temp));
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
            if( nodeListWordTypes.item(temp).getNodeName() == "container") {
                resultWordTypesContainer = SearchSetContainer(nodeListWordTypes.item(temp));
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
    private Container SearchSetContainer( Node nodeContainer) {
        Container container = new Container();
        Container resultWordTypesContainer;
        NodeList secondSubNodeList;
        String result = "";

        secondSubNodeList = nodeContainer.getChildNodes();

        for( int i = 0; i < secondSubNodeList.getLength(); i++) {
			// entry
            if( secondSubNodeList.item(i).getNodeName() == "entry") {
                try {
                    container.entryList.add( Integer.parseInt(secondSubNodeList.item(i).getAttributes().getNamedItem("id").getTextContent()));
                }
                catch( Exception e) {
                    error++;
                }
            }
			// container
            else if( secondSubNodeList.item(i).getNodeName() == "container") {
                resultWordTypesContainer = SearchSetContainer(secondSubNodeList.item(i));
                if( resultWordTypesContainer != null) {
                    container.container.add(resultWordTypesContainer);
                }
            }
			// specialwordtype
            else if( secondSubNodeList.item(i).getNodeName() == "specialwordtype") {
                result = secondSubNodeList.item(i).getTextContent();

                if( (result == Container.NOUN) || (result == Container.NOUN_MALE) || (result == Container.NOUN_FEMALE)
                    || (result == Container.NOUN_NEUTRAL) || (result == Container.VERB) || (result == Container.ADJECTIVE)
                    || (result == Container.ADVERB))
                    container.specialWordType = result;
            }
			// name
            else if( secondSubNodeList.item(i).getNodeName() == "name") {
                container.name = secondSubNodeList.item(i).getTextContent();
            }
            // inpractice
            else if( secondSubNodeList.item(i).getNodeName() == "inpractice") {
                result = secondSubNodeList.item(i).getTextContent();

                if( (result == "true") || (result == "1"))
                    container.inPractice = true;
            }
			// image
			else if( secondSubNodeList.item(i).getNodeName() == "image") {
                container.image = secondSubNodeList.item(i).getTextContent();	
            }
        }
        return container;
    }

	public String export(FileFormats fileFormats) throws Throwable{
		
		return null;
	}
}