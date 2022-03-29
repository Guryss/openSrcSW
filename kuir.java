package opensource_week4;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.xml.sax.SAXException;

public class kuir {

	public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException, TransformerException, ClassNotFoundException {
		// TODO Auto-generated method stub

		String command = args[0];   
		String path = args[1];

		if(command.equals("-c")) {
			makeCollection collection = new makeCollection(path);
		}
		else if(command.equals("-k")) {
			makeKeyword keyword = new makeKeyword(path);
			keyword.convertXml();
	}
		else if (command.equals("-i")) {
			indexer ix = new indexer(path);
			ix.makeHashMap();
			ix.printMap("src/data/index.post");
		}
	}

}
