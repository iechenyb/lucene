package com.cyb;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queryparser.surround.parser.ParseException;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.NumericRangeQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
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
        Query query2 = NumericRangeQuery.newIntRange("score", 80, 100, true, false);  
        booleanQuery.add(query1, BooleanClause.Occur.MUST);  
        booleanQuery.add(query2, BooleanClause.Occur.MUST);  
        TopDocs topdocs = search.search(booleanQuery, 100);// 查询前100条  
        System.out.println("查询结果总数---" + topdocs.totalHits);  
        ScoreDoc scores[] = topdocs.scoreDocs;// 得到所有结果集  
        for (int i = 0; i < scores.length; i++) {  
            int num = scores[i].doc;// 得到文档id  
            Document document = search.doc(num);// 拿到指定的文档  
            System.out.println("标题:" + document.get("path"));  
        }  
    }  
	public static void main(String[] args) throws Exception {
		deleteIndex();
    	LuceneFileIndex.createIndex();
    	LuceneQueryUtils.query("name", "总理");
	} 
}
