package com.cyb;

import java.io.BufferedReader;  
import java.io.File;  
import java.io.FileInputStream;  
import java.io.InputStreamReader;  
  









import java.util.Random;

import org.apache.lucene.analysis.Analyzer;  
import org.apache.lucene.analysis.standard.StandardAnalyzer;  
import org.apache.lucene.document.Document;  
import org.apache.lucene.document.DoubleField;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Store;  
import org.apache.lucene.document.IntField;
import org.apache.lucene.document.TextField;  
import org.apache.lucene.index.DirectoryReader;  
import org.apache.lucene.index.IndexReader;  
import org.apache.lucene.index.IndexWriter;  
import org.apache.lucene.index.IndexWriterConfig;  
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;  
import org.apache.lucene.search.IndexSearcher;  
import org.apache.lucene.search.Query;  
import org.apache.lucene.search.ScoreDoc;  
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;  
import org.apache.lucene.store.Directory;  
import org.apache.lucene.store.FSDirectory;  
import org.apache.lucene.util.Version;  
import org.wltea.analyzer.lucene.IKAnalyzer;

import com.cyb.cse.IKSynonymsAnalyzer;
  //http://blog.csdn.net/mdcmy/article/details/38167955
public class LuceneFileIndex {  
	public static String DataPath="d:\\lucene\\example";
	public static String IndexPath="d:\\lucene\\index";
      
    /**  
     * ��������  
     * @param analyzer  
     * @throws Exception  
     */  
    public static void createIndex() throws Exception{  
        Directory dire=FSDirectory.open(new File(IndexPath));  
        IndexWriterConfig iwc=new IndexWriterConfig(Version.LUCENE_36, analyzer);  
        IndexWriter iw=new IndexWriter(dire, iwc);  
        LuceneFileIndex.addDoc(iw);  
        iw.close();  
    }  
      
    /**  
     * ��̬���Document  
     * @param iw  
     * @throws Exception  
     */  
    public static void addDoc(IndexWriter iw)  throws Exception{  
        File[] files=new File(DataPath).listFiles();  
        for (File file : files) {  
            Document doc=new Document();  
            String content=LuceneFileIndex.getContent(file);  
            String name=file.getName();  
            String path=file.getAbsolutePath();  
            doc.add(new TextField("content", content, Store.YES));  
            doc.add(new TextField("name", name, Store.YES));  
            doc.add(new TextField("path", path,Store.YES));  
            Double version = new Random().nextDouble()*100;
            Integer score = new Random().nextInt(100)+50;
            Field field3 = new DoubleField("version", version, Store.YES);// 版本 DoubleField类型  
            Field field4 = new IntField("score",score , Store.YES);// 评分 IntField类型 
            doc.add(field3); doc.add(field4);
            System.out.println("创建索引文件："+path+",version:"+version+",score:"+score);  
            iw.addDocument(doc);  
            iw.commit();  
        }  
    }  
      
    /**  
     * ��ȡ�ı�����  
     * @param file  
     * @return  
     * @throws Exception  
     */  
    @SuppressWarnings("resource")  
    public static String getContent(File file) throws Exception{  
        FileInputStream fis=new FileInputStream(file);  
        InputStreamReader isr=new InputStreamReader(fis,"UTF-8");  
        BufferedReader br=new BufferedReader(isr);  
        StringBuffer sb=new StringBuffer();  
        String line=br.readLine();  
        while(line!=null){  
            sb.append(line+"\n");  
            line=null;  
        }  
        return sb.toString();  
    }  
      
    /**  
     * ����  
     * @param query  
     * @throws Exception  
     */  
    private static void search(Query query,String words) throws Exception {  
        Directory dire=FSDirectory.open(new File(IndexPath));  
        IndexReader ir=DirectoryReader.open(dire);  
        IndexSearcher is=new IndexSearcher(ir);  
        TopDocs td=is.search(query, 1000);  
        System.out.println("搜索句子:["+words+"],查询结果"+td.totalHits+"");  
        ScoreDoc[] sds =td.scoreDocs;  
        for (ScoreDoc sd : sds) {   
            Document d = is.doc(sd.doc);   
            System.out.println(d.get("path"));   
        }  
    }  
    private static void searchTerm(String words) throws Exception {  
        Directory dire=FSDirectory.open(new File(IndexPath));  
        IndexReader ir=DirectoryReader.open(dire);  
        IndexSearcher is=new IndexSearcher(ir);
        Query  query = new TermQuery(new Term("name",words));
        //开始检索，并返回检索结果到hits中
        TopDocs td=is.search(query, 1000);  
        System.out.println("搜索句子:["+words+"],查询结果"+td.totalHits+"");  
        ScoreDoc[] sds =td.scoreDocs;  
        for (ScoreDoc sd : sds) {   
            Document d = is.doc(sd.doc);   
            System.out.println(d.get("path"));   
        }  
    }  
      
    public static  void deleteIndex(){
    	File file = new File(IndexPath);
    	for(File f:file.listFiles()){
    		f.delete();
    	}
    }  
    //static Analyzer analyzer=new StandardAnalyzer(Version.LUCENE_46);  
    static Analyzer analyzer=new IKAnalyzer(true
    		);  
    //static Analyzer analyzer=new IKSynonymsAnalyzer();  
    public static void start(String col,String words) throws Exception{
        QueryParser parser = new QueryParser(Version.LUCENE_46, col, analyzer);   
        Query query = parser.parse(words);  
        LuceneFileIndex.search(query,words);  
    }
    public static void main(String[] args) throws Exception, Exception {  
    	deleteIndex();
    	LuceneFileIndex.createIndex();
    	String words = "开户";
    	String col ="name";
    	/*start(col,words);
    	System.out.println("----------------");*/
    	start(col,"银期");
    	System.out.println("----------------");
    	start(col,"期货");
    	System.out.println("----------------");
    	start(col,"期货期");//是词儿就不拆
    	System.out.println("----------------");
    	start(col,"期");//是词儿就不拆
    	System.out.println("----------------");
    	/*start(col,"我先注册，后边在开户");
    	System.out.println("----------------");
    	start(col,"我去，密码丢了，咋办？");
    	System.out.println("----------------");*/
    }  
}  