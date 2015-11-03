package mypkg;

import javax.annotation.PostConstruct;
import javax.batch.api.BatchProperty;
import javax.batch.api.chunk.AbstractItemReader;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

@Named("myReader")
@Dependent
public class MyReader extends AbstractItemReader {
       
	 @Inject @BatchProperty(name="inputSize") 
	 String inputSizeStr;
	 int inputSize;
	 int cnt = 0;
	 
	 @PostConstruct 
	 void convert() {
		 inputSize = Integer.parseInt(inputSizeStr);
	 }

	/**
     * @see AbstractItemReader#readItem()
     */
    public Object readItem() {
    	if (cnt < inputSize) {
    		return cnt++;
    	} else {
    		return null;
    	}       
    }
}
