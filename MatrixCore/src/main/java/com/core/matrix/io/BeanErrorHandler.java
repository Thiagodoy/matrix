package com.core.matrix.io;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.Data;
import org.beanio.BeanReaderErrorHandler;
import org.beanio.BeanReaderException;
import org.beanio.RecordContext;
import org.springframework.stereotype.Component;

/**
 *
 * @author Thiago H. Godoy <thiagodoy@hotmail.com>
 */
@Component
@Data
public class BeanErrorHandler implements BeanReaderErrorHandler {

    
    private Long fileId;
    private Map<String,StringBuilder>map = new HashMap<>();

    public BeanErrorHandler() {
    }

    @Override
    public void handleError(BeanReaderException e) throws Exception {

        for (int i = 0; i < e.getRecordCount(); i++) {
            RecordContext recordContext = e.getRecordContext(i);

            boolean hasErrorsField = recordContext.hasFieldErrors();
            boolean hasRecordErrorsField = recordContext.hasRecordErrors();

            if (hasRecordErrorsField) {
                Collection<String> errors = recordContext.getRecordErrors();

                for (String error : errors) {
                    
                }
            }
            if (hasErrorsField) {
                Map<String, Collection<String>> errors = recordContext.getFieldErrors();

                for (String key : errors.keySet()) {
                    for (String erro : errors.get(key)) {
                        Logger.getLogger(BeanIoReader.class.getName()).log(Level.SEVERE, erro);
                    }
                    
                      
                    
                }
            }

            
        }

        if (e.getRecordCount() == 0) {
            Logger.getLogger(BeanIoReader.class.getName()).log(Level.SEVERE, e.getMessage());            
        }
    }
    
    private void putValue(String field,String content, String message){
        
        String key = MessageFormat.format("{0}:{1}:{2}", this.fileId,field,content);
        
        if(this.map.containsKey(key)){
            this.map.get(key).append(message + "\n");
        }else{
            this.map.put(key, new StringBuilder(message));
        }
        
    }
}
