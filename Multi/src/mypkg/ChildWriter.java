package mypkg;

import java.io.File;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.List;
import java.util.logging.Logger;

import javax.batch.api.BatchProperty;
import javax.batch.api.chunk.AbstractItemWriter;
import javax.inject.Inject;

public class ChildWriter extends AbstractItemWriter {
	
	Logger logger = Logger.getLogger("Multi");
	
	// Set at parent level, the ids of the children
	String childId;
	
	public String getChildId() {
		return childId;
	}

	// Set at parent level, children need but never override.
	//@Inject @BatchProperty 
	String org;
	
	// Default at parent level, children may override.
	//@Inject @BatchProperty 
	String separator;
	
	// Only relevant at child level
	//@Inject @BatchProperty 
	String chunkHeader;
	
	//@Inject @BatchProperty 
	String fileName;


	@Override
	public void writeItems(List<Object> items) throws Exception {
		logger.fine("In writeItesm");
		pw.print(chunkHeader);
		for (int i = 0; i < items.size() - 1; i++) {		
			pw.print(items.get(i) + separator);
		}
		pw.println(items.get(items.size()-1));
		pw.flush();
	}
	
	private File outFile;
	private PrintWriter pw;
	
	@Override
	public void open(Serializable checkpoint) throws Exception {
		
			
		outFile = new File(System.getProperty("java.io.tmpdir"), fileName);
		pw = new PrintWriter(outFile);		
		pw.println("ORG = " + org);
		logger.fine("In child: " + childId +" , opening file: " + outFile );
	}
	
	public void close() throws Exception {		
		pw.close();		
	}		
	
	public void setChildId(String childId) {
		this.childId = childId;
	}
	
	public void setOrg(String org) {
		this.org = org;
	}

	public void setSeparator(String separator) {
		this.separator = separator;
	}

	public void setChunkHeader(String chunkHeader) {
		this.chunkHeader = chunkHeader;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}



}
