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

	// public static void main(String[] args) throws IOException {
	public static void cranIndexer(String indPath, String docPath, Boolean iwcSim, int valAnalyzer) throws IOException {
		// String indexPath = "C:\\Users\\robin\\Desktop\\TCD\\4 IR & web
		// String docsPath = "C:\\Users\\robin\\Desktop\\TCD\\4 IR & web
		String indexPath = indPath;
		String docsPath = docPath;

		// System.out.println("docspath is :" + docsPath );
		final Path docDir = Paths.get(docsPath);
		if (!Files.isReadable(docDir)) {
			System.out.println("Document directory '" + docDir.toAbsolutePath()
					+ "' does not exist or is not readable, please check the path");
			System.exit(1);
		}

		try {
			// System.out.println("Indexing to directory '" + indexPath + "'...");

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
			// Analyzer analyzer = new StandardAnalyzer();
			// Analyzer analyzer = new EnglishAnalyzer(); // ENGLISH ANALYSER FOR DIFFERENT
/*
			Analyzer analyzer = CustomAnalyzer.builder().withTokenizer(StandardTokenizerFactory.class).addTokenFilter(StandardFilterFactory.class).addTokenFilter(LowerCaseFilterFactory.class).addTokenFilter(PorterStemFilterFactory.class).build();
*/			
			IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
			iwc.setOpenMode(OpenMode.CREATE); // Always creates a new index everytime you run this

			if (iwcSim == true) {
				iwc.setSimilarity(new BM25Similarity());
			} else {
				iwc.setSimilarity(new ClassicSimilarity());
			}
			IndexWriter writer = new IndexWriter(dir, iwc);
			// indexDocs(writer, docDir);
			//writer.flush();
			
			cranIndex(writer, docsPath);
			// NOTE: if you want to maximize search performance,
			// you can optionally call forceMerge here. This can be
			// a terribly costly operation, so generally it's only
			// worth it when your index is relatively static (ie
			// you're done adding documents to it):
			//
			//writer.forceMerge(0); // MAYBE USEFUL

			writer.close();
			// System.out.println("finished");

		} catch (IOException e) {
			System.out.println(" caught a " + e.getClass() + "\n with message: " + e.getMessage());
		}
	}

	static void cranIndex(final IndexWriter writer, String cranPath) throws IOException {
		Scanner sc = new Scanner(new File(cranPath));
		sc.useDelimiter(".I");
		while (sc.hasNext()) {
			// System.out.println("split shown");
			// System.out.println(sc.next());
			String chunk = sc.next();

			// System.out.println(chunk);

			String[] ops = chunk.split(".I|.T|.A|.B|.W");
			// System.out.println("Splits 1");
			// System.out.println(ops[0]);
			// System.out.println(ops[1]);
			// System.out.println(ops[2]);
			// System.out.println(ops[3]);
			// System.out.println(ops[4]);

			Document doc = new Document();
			// System.out.println(chunk);
			// chunk split with .T .A .W .B
			
			doc.add(new TextField("id", ops[0].trim(), Field.Store.YES));
			doc.add(new TextField("title", ops[1].trim(), Field.Store.YES));
			doc.add(new TextField("author", ops[2].trim(), Field.Store.YES));
			doc.add(new TextField("bibliography", ops[3].trim(), Field.Store.YES));
			doc.add(new TextField("content", ops[4].trim(), Field.Store.YES));
			
			
			// System.out.println("\n \n ************** \n \n");
			//writer.addDocument(doc);
			if (writer.getConfig().getOpenMode() == OpenMode.CREATE) {
				// New index, so we just add the document (no old document can be there):
				// System.out.println("adding " + cranPath);

				writer.addDocument(doc);}
//			} else {
//				// Existing index (an old copy of this document may have been indexed) so
//				// we use updateDocument instead to replace the old one matching the exact
//				// path, if present:
//				// System.out.println("updating " + cranPath);
//				writer.updateDocument(new Term("path", cranPath.toString()), doc);
//			}
		}
		sc.close();
		//writer.close();
	}

}