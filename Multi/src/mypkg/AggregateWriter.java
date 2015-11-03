package mypkg;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.batch.api.BatchProperty;
import javax.batch.api.chunk.AbstractItemWriter;
import javax.inject.Inject;
import javax.inject.Named;
import static mypkg.Util.*; 

@Named("aggregateWriter")
public class AggregateWriter extends AbstractItemWriter {

	// Set at parent level, the ids of the children
	@Inject @BatchProperty String childIds;

	// Set at parent level, children need but never override.
	@Inject @BatchProperty String org;

	// Default at parent level, children may override.
	@Inject @BatchProperty String separatorDefault;

	// Default at parent level, children may override.
	@Inject @BatchProperty String separatorOverrides;

	// Only relevant at child level
	@Inject @BatchProperty String chunkHeader;
	@Inject @BatchProperty String fileName;

	@Inject @BatchProperty String childClassNameDefault;

	private List<ChildWriter> childrenWriters = new ArrayList<ChildWriter>(); 

	@PostConstruct 
	void setupChildren() {
		
		String[] childStrings = splitByComma(childIds);
		String[] chunkHeaders = splitByComma(chunkHeader);
		String[] fileNames = splitByComma(fileName);
		
		for (String childId : childStrings) {
			ChildWriter nextChild = null;
			try {
				Class<ChildWriter> clazz = (Class<ChildWriter>) Class.forName(childClassNameDefault);
				nextChild = clazz.getConstructor().newInstance();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
			nextChild.setChildId(childId);			
			childrenWriters.add(nextChild);
		}	
		
		// Set vals that are either unique for all or the same for all (including 
		// defaults that may be overridden).
		for (int cnt = 0; cnt < childrenWriters.size(); cnt++) {
			ChildWriter nextChild = childrenWriters.get(cnt);
			nextChild.setChunkHeader(chunkHeaders[cnt]);
			nextChild.setFileName(fileNames[cnt]);
			nextChild.setOrg(org);			
			nextChild.setSeparator(separatorDefault);			
		}
		
		String[] sepOverrides = splitByComma(separatorOverrides);
		for (String s: sepOverrides) {
			String[] keyValPair = splitByEquals(s);			
			overrideSeparator(keyValPair[0], keyValPair[1]);					
		}
		
	}
	
	private void overrideSeparator(String childId, String sepOverrideVal) {
		for (ChildWriter nextChild : childrenWriters) {
			if (nextChild.getChildId().equals(childId)) {
				nextChild.setSeparator(sepOverrideVal);
				break;
			}
		}
		System.out.println("SKSK: Missed override ");
	}


	@Override
	public void open(java.io.Serializable checkpoint) throws Exception {
		for (ChildWriter nextChild : childrenWriters) {
			nextChild.open(checkpoint);
		}
	}
	
	@Override
	public void writeItems(List<Object> items) throws Exception {
		for (ChildWriter nextChild : childrenWriters) {
			nextChild.writeItems(items);			
		}
	}
}
