package com.core.matrix.io;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.beanio.BeanReader;
import org.beanio.StreamFactory;
import org.springframework.stereotype.Service;

/**
 *
 * @author Thiago H. Godoy <thiagodoy@hotmail.com>
 */
@Service
public class BeanIoReader {

    

    

    private final String ENCODING = "ISO-8859-1";

    public <T> Optional<T> parse(InputStream inputStream, Stream stream) throws Exception {

        

        
        BeanReader reader = null;
        T record = null;
        try {
            StreamFactory factory = StreamFactory.newInstance();
            InputStream str = factory.getClass().getClassLoader().getResourceAsStream(stream.getStreamFile());
            factory.load(str);
            Reader rr = new InputStreamReader(inputStream, ENCODING);
            reader = factory.createReader(stream.getStreamId(), rr);
            record = (T) reader.read();
        } catch (Exception ex) {
            Logger.getLogger(BeanIoReader.class.getName()).log(Level.SEVERE, "[parse]", ex);
            throw ex;
        }

        reader.close();       

        return Optional.ofNullable(record);
    }

    

   

   

}
