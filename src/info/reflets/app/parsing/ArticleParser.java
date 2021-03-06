package info.reflets.app.parsing;

import info.reflets.app.model.Article;
import info.reflets.app.utils.Tools;

import java.util.ArrayList;

import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.text.Html;
import android.util.Log;

/***
 * Parser
 * @author Alexandre
 *
 */
public class ArticleParser extends DefaultHandler{

	private final static String ITEM 		= "item";
	private final static String TITLE 		= "title";
	private final static String LINK 		= "guid";
	private final static String PUBDATE		= "pubdate";
	private final static String DESCRIPTION = "description";
	private final static String CREATOR 	= "creator";
	private final static String CONTENT 	= "encoded";
	private final static String COMMENT		= "commentRss";
	
	private ArrayList<Article> entries;
	
	private Article currentEntry;
	
	private boolean insideItem = false;

	private StringBuffer buffer;
	
	@Override
	public void processingInstruction(String target, String data) throws SAXException {
		super.processingInstruction(target, data);
	}
	
	public ArticleParser(){
		super();
	}
	
	@Override
	public void startDocument() throws SAXException {
		super.startDocument();
		entries = new ArrayList<Article>();
	}
	
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		super.startElement(uri, localName, qName, attributes);
		
		buffer = new StringBuffer(); 

		if (localName.equalsIgnoreCase(ITEM)){
			this.currentEntry = new Article();
			insideItem = true;
		}
	}
	
	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		super.endElement(uri, localName, qName);
		
		if (localName.equalsIgnoreCase(TITLE) && insideItem){
			this.currentEntry.setTitle(buffer.toString());
			buffer = null;
		}
		else if (localName.equalsIgnoreCase(LINK) && insideItem){
			this.currentEntry.setLink(buffer.toString());
			buffer = null;
		}
		else if (localName.equalsIgnoreCase(PUBDATE) && insideItem){
			this.currentEntry.setDate(buffer.toString());
			buffer = null;
		}
		else if (localName.equalsIgnoreCase(DESCRIPTION) && insideItem){
			this.currentEntry.setDescription( Html.fromHtml(buffer.toString()).toString());
			buffer = null;
		}
		else if (localName.equalsIgnoreCase(CREATOR) && insideItem){
			this.currentEntry.setAuthor(buffer.toString());
			buffer = null;
		}
		else if (localName.equalsIgnoreCase(CONTENT) && insideItem){
			
			this.currentEntry.setContent(buffer.toString());
			
			// Parsing fisrt image
			try {
				HtmlCleaner cleaner = new HtmlCleaner();
				TagNode root = cleaner.clean( this.currentEntry.getContent());

				Object[] images = root.evaluateXPath( "//img" );

				if (images.length > 0 && images[0] instanceof TagNode){
					String url = ((TagNode)images[0]).getAttributeByName("src");
					this.currentEntry.setImage(Tools.urlEncode(url));
				}

				
			} catch (Exception e) {
				Log.d("Parsing", e.getMessage());
			}
			
			buffer = null;
		}
		else if (localName.equalsIgnoreCase(COMMENT) && insideItem){
			this.currentEntry.setCommentUrll(buffer.toString());
			buffer = null;
		}
		else if (localName.equalsIgnoreCase(ITEM)){
			entries.add(currentEntry);
			insideItem = false;
		}
		
	}
	
	public ArrayList<Article> getArticles() {
		return entries;
	}
	
	public void characters(char[] ch,int start, int length) throws SAXException{
		String lecture = new String(ch,start,length);
		if(buffer != null) buffer.append(lecture);
	}
	

}
