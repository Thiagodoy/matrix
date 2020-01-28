package com.core.matrix.io;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
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
    private List<String> listErrors = new ArrayList<>();

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
                    listErrors.add(error);
                }
            }
            if (hasErrorsField) {
                Map<String, Collection<String>> errors = recordContext.getFieldErrors();

                for (String key : errors.keySet()) {
                    for (String erro : errors.get(key)) {
                        listErrors.add(erro);
                    }

                }
            }

        }

        if (e.getRecordCount() == 0) {
            Logger.getLogger(BeanIoReader.class.getName()).log(Level.SEVERE, e.getMessage());
        }
    }

}
