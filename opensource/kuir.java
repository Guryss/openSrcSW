package opensource;

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
		else if(command.equals("-s")) {
			String route = "src/data/"+args[1];
			if(args[2].equals("-q")) {
				String query = args[3];
				searcher searcher = new searcher(route, query);
				searcher.rank();
			}
			else
				System.out.println("검색되는 문서가 없습니다.");
			
		}
	}

}
