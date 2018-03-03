package grp1.art1;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Date;
import java.util.Scanner;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.LowerCaseFilterFactory;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.analysis.custom.CustomAnalyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.en.PorterStemFilterFactory;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.standard.StandardFilterFactory;
import org.apache.lucene.analysis.standard.StandardTokenizerFactory;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class IndexFiles {

	private IndexFiles() {
	}
	
	public static void cranIndexer(String indPath, String docPath, Boolean iwcSim, int valAnalyzer) throws IOException {
		String indexPath = indPath;
		String docsPath = docPath;

		final Path docDir = Paths.get(docsPath);
		if (!Files.isReadable(docDir)) {
			System.out.println("Document directory '" + docDir.toAbsolutePath()
					+ "' does not exist or is not readable, please check the path");
			System.exit(1);
		}

		try {
			Directory dir = FSDirectory.open(Paths.get(indexPath));
			Analyzer analyzer = null;
			// Anlyzer choosing
			if (valAnalyzer == 1) {
				analyzer = new StandardAnalyzer();
			} else if (valAnalyzer == 2) {
				analyzer = new EnglishAnalyzer();
			} else if (valAnalyzer == 3) {
				analyzer = new SimpleAnalyzer();
			} 
		
			IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
			// Always creates a new index everytime you run this
			iwc.setOpenMode(OpenMode.CREATE); 

			//Choosing Similarity
			if (iwcSim == true) {
				iwc.setSimilarity(new BM25Similarity());
			} else {
				iwc.setSimilarity(new ClassicSimilarity());
			}
			IndexWriter writer = new IndexWriter(dir, iwc);
		
			cranIndex(writer, docsPath);
			
			writer.close();

		} catch (IOException e) {
			System.out.println(" caught a " + e.getClass() + "\n with message: " + e.getMessage());
		}
		System.out.println("---- Index Generated ----");
	}

	static void cranIndex(final IndexWriter writer, String cranPath) throws IOException {
		Scanner sc = new Scanner(new File(cranPath));
		sc.useDelimiter(".I");
		while (sc.hasNext()) {
			String chunk = sc.next();
			String[] ops = chunk.split(".I|.T|.A|.B|.W");
			// System.out.println(ops[0]);
			Document doc = new Document();

			// chunk split with .T .A .W .B
			doc.add(new TextField("id", ops[0].trim(), Field.Store.YES));
			doc.add(new TextField("title", ops[1].trim(), Field.Store.YES));
			doc.add(new TextField("author", ops[2].trim(), Field.Store.YES));
			doc.add(new TextField("bibliography", ops[3].trim(), Field.Store.YES));
			doc.add(new TextField("content", ops[4].trim(), Field.Store.YES));
	
			writer.addDocument(doc);
		}
		sc.close();
	}
}