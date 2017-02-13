package com.cyb;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;





import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.DoubleField;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.IntField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queryparser.surround.parser.ParseException;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.NumericRangeQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Scorer;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.search.highlight.Formatter;
import org.apache.lucene.search.highlight.Fragmenter;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleFragmenter;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.wltea.analyzer.lucene.IKAnalyzer;

public class LuceneQueryUtils extends LuceneFileIndex{
	public static void  query(String col,String queryStr) throws IOException, ParseException, org.apache.lucene.queryparser.classic.ParseException {  
        IndexReader reader = DirectoryReader.open(FSDirectory.open(new File(IndexPath)));// 索引读取类  
        IndexSearcher search = new IndexSearcher(reader);// 搜索入口工具类  
        BooleanQuery booleanQuery = new BooleanQuery();  
        // 条件一内容中必须要有life内容  new StandardAnalyzer(Version.LUCENE_46)
        QueryParser queryParser = new QueryParser(Version.LUCENE_46, col, new IKAnalyzer());// 实例查询条件类  
        Query query1 = queryParser.parse(queryStr);  
        // 条件二评分大于等于80  
        Query query2 = NumericRangeQuery.newIntRange("score", 80, 1000, true, false);  
        booleanQuery.add(query1, BooleanClause.Occur.MUST);  
        booleanQuery.add(query2, BooleanClause.Occur.MUST);  
        TopDocs topdocs = search.search(booleanQuery, 100);// 查询前100条  
        System.out.println("查询结果总数---" + topdocs.totalHits);  
        ScoreDoc scores[] = topdocs.scoreDocs;// 得到所有结果集  
        for (int i = 0; i < scores.length; i++) {  
            int num = scores[i].doc;// 得到文档id  
            Document document = search.doc(num);// 拿到指定的文档  
            System.out.println("标题:" + document.get("path")+",score:"+document.get("score"));  
        }  
    }  
	  public static void updateIndex() throws IOException {  
	        Directory director = FSDirectory.open(new File(IndexPath));// 创建Directory关联源文件  
	        Analyzer analyzer = new IKAnalyzer();// 创建一个分词器  
	        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(Version.LUCENE_46, analyzer);// 创建索引的配置信息  
	        IndexWriter indexWriter = new IndexWriter(director, indexWriterConfig);  
	        Document doc = new Document();// 创建文档  
	        Field field1 = new StringField("name", "总理", Store.YES);// 标题 StringField索引存储不分词  
	        Field field2 = new TextField("url", "d:\\lucene\\example\\总理.txt", Store.NO);// 内容 TextField索引分词不存储  
	        Field field3 = new DoubleField("version", 2.0, Store.YES);// 版本 DoubleField类型  
	        Field field4 = new IntField("score", 90, Store.YES);// 评分 IntField类型  
	        doc.add(field1);// 添加field域到文档中  
	        doc.add(field2);  
	        doc.add(field3);  
	        doc.add(field4);  
	        indexWriter.updateDocument(new Term("name", "总理"), doc);  
	        indexWriter.commit();  
	        indexWriter.close();  
	    } 
	  public static void deleteIndex1() throws IOException {  
	        Directory director = FSDirectory.open(new File(IndexPath));// 创建Directory关联源文件  
	        Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_46);// 创建一个分词器  
	        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(Version.LUCENE_46, analyzer);// 创建索引的配置信息  
	        IndexWriter indexWriter = new IndexWriter(director, indexWriterConfig);  
	        indexWriter.forceMerge(1);// 当小文件达到多少个时，就自动合并多个小文件为一个大文件  
	        indexWriter.deleteDocuments(new Term("name", "总理"));  
	        indexWriter.commit();  
	        // indexWriter.rollback();  
	        indexWriter.close();  
	}  
	  public static void createOrderedIndex() throws IOException {  
	        Directory director = FSDirectory.open(new File(IndexPath));// 创建Directory关联源文件  
	        Analyzer analyzer = new IKAnalyzer();// 创建一个分词器  
	        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(VERSION, analyzer);// 创建索引的配置信息  
	        IndexWriter indexWriter = new IndexWriter(director, indexWriterConfig);  
	        for (int i = 1; i <= 5; i++) {  
	            Document doc = new Document();// 创建文档  
	            Field field1 = new StringField("title", "标题" + i, Store.YES);// 标题 StringField索引存储不分词  
	            Field field2 = new TextField("content", "201" + i + "文章内容内容 TextField索引分词不存储,文章内容内容 TextField索引分词不存储 ", Store.YES);// 内容 TextField索引分词不存储  
	            Field field3 = new DoubleField("version", 1.2, Store.YES);// 版本 DoubleField类型  
	            Field field4 = new IntField("score", 90 + i, Store.YES);// 评分 IntField类型  
	            Field field5 = new StringField("date", "2014-07-0" + i, Store.YES);// 评分 IntField类型  
	            doc.add(field1);// 添加field域到文档中  
	            doc.add(field2);  
	            doc.add(field3);  
	            doc.add(field4);  
	            doc.add(field5);  
	            indexWriter.addDocument(doc);// 添加文本到索引中  
	        }  
	        indexWriter.close();// 关闭索引  
	    }  
	  public static  void highlighter() throws IOException, ParseException, org.apache.lucene.queryparser.classic.ParseException, InvalidTokenOffsetsException {  
	        IndexReader reader = DirectoryReader.open(FSDirectory.open(new File(IndexPath)));// 索引读取类  
	        IndexSearcher search = new IndexSearcher(reader);// 搜索入口工具类  
	        Analyzer analyzer = new IKAnalyzer();// 分词器  
	        QueryParser qp = new QueryParser(Version.LUCENE_47, "content", analyzer);// 实例查询条件类  
	        Query query = qp.parse("文章");  
	        TopDocs topDocs = search.search(query, 100);// 查询前100条  
	        System.out.println("共查询出:" + topDocs.totalHits + "条数据");  
	        ScoreDoc scoreDoc[] = topDocs.scoreDocs;// 结果集  
	        // 高亮  
	        Formatter formatter = new SimpleHTMLFormatter("<font color='red'>", "</font>");// 高亮html格式  
	        QueryScorer score = new QueryScorer(query);// 检索评份  
	        Fragmenter fragmenter = new SimpleFragmenter(100);// 设置最大片断为100  
	        Highlighter highlighter = new Highlighter(formatter, score);// 高亮显示类  
	        highlighter.setTextFragmenter(fragmenter);// 设置格式  
	        for (int i = 0; i < scoreDoc.length; i++) {// 遍历结果集  
	            int docnum = scoreDoc[i].doc;  
	            Document doc = search.doc(docnum);  
	            String content = doc.get("content");  
	            System.out.println(content);// 原内容  
	            if (content != null) {  
	                TokenStream tokenStream = analyzer.tokenStream("content", new StringReader(content));  
	                String str = highlighter.getBestFragment(tokenStream, content);// 得到高亮显示后的内容  
	                System.out.println(str);  
	            }  
	        }  
	    }  
	  public void pageTest() throws IOException, ParseException, org.apache.lucene.queryparser.classic.ParseException {  
	        IndexReader reader = DirectoryReader.open(FSDirectory.open(new File(IndexPath)));// 索引读取类  
	        IndexSearcher search = new IndexSearcher(reader);// 搜索入口工具类  
	        String queryStr = "文章";// 搜索关键字  
	        QueryParser queryParser = new QueryParser(VERSION, "content", new StandardAnalyzer(VERSION));// 实例查询条件类  
	        Query query = queryParser.parse(queryStr);// 查询  
	        TopScoreDocCollector results = TopScoreDocCollector.create(100, false);// 结果集  
	        search.search(query, results);// 查询前100条  
	        TopDocs topdocs = results.topDocs(1, 2);// 从结果集中第1条开始取2条  
	        ScoreDoc scores[] = topdocs.scoreDocs;// 得到所有结果集  
	        for (int i = 0; i < scores.length; i++) {  
	            int num = scores[i].doc;// 得到文档id  
	            Document document = search.doc(num);// 拿到指定的文档  
	            System.out.println("内容====" + document.get("content"));// 由于内容没有存储所以执行结果为null  
	            System.out.println("标题====" + document.get("title"));  
	            System.out.println("版本====" + document.get("version"));  
	            System.out.println("评分====" + document.get("score"));  
	            System.out.println("id--" + num + "---scors--" + scores[i].score + "---index--" + scores[i].shardIndex);  
	        }  
	    }  
	  public static void defaultSortTest() throws IOException, ParseException, org.apache.lucene.queryparser.classic.ParseException {  
	        IndexReader reader = DirectoryReader.open(FSDirectory.open(new File(IndexPath)));// 索引读取类  
	        IndexSearcher search = new IndexSearcher(reader);// 搜索入口工具类  
	        String queryStr = "文章";// 搜索关键字  
	        QueryParser queryParser = new QueryParser(VERSION, "content", new StandardAnalyzer(VERSION));// 实例查询条件类  
	        Query query = queryParser.parse(queryStr); 
	        Sort sort = new Sort(new SortField("score", SortField.Type.INT, true));// false升序true降序  
	        TopDocs topdocs = search.search(query, 100, sort);// 查询前100条   // false升序true降序  
	        //TopDocs topdocs = search.search(query, 100);// 查询前100条  
	        System.out.println("查询结果总数---" + topdocs.totalHits);  
	        ScoreDoc scores[] = topdocs.scoreDocs;// 得到所有结果集  
	        for (int i = 0; i < scores.length; i++) {  
	            int num = scores[i].doc;// 得到文档id  
	            Document document = search.doc(num);// 拿到指定的文档  
	            System.out.print("内容:" + document.get("content"));// 由于内容没有存储所以执行结果为null  
	            System.out.print(",标题:" + document.get("title"));  
	            System.out.print(",版本:" + document.get("version"));  
	            System.out.print(",评分:" + document.get("score"));  
	            System.out.print(",日期:" + document.get("date"));  
	            System.out.println(",id:" + num + ",scors:" + scores[i].score + ",index:" + scores[i].shardIndex);  
	        }  
	    }  
	public static void main(String[] args) throws Exception {
		deleteIndex();
		/*LuceneFileIndex.createIndex();
    	
    	LuceneQueryUtils.start("name", "总理");
    	updateIndex();
    	LuceneQueryUtils.start("name", "总理");
    	
    	LuceneQueryUtils.start("name", "总理");
    	deleteIndex1();
    	LuceneQueryUtils.start("name", "总理");*/
		createOrderedIndex();
		defaultSortTest();
		highlighter();
	} 
}
