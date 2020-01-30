package com.core.matrix.io;

import com.core.matrix.dto.FileParsedDTO;
import static com.core.matrix.utils.Constants.*;
import com.core.matrix.utils.MeansurementFileType;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.Data;
import org.apache.commons.io.IOUtils;
import org.apache.commons.text.similarity.JaroWinklerDistance;
import org.beanio.BeanReader;
import org.beanio.StreamFactory;

/**
 *
 * @author Thiago H. Godoy <thiagodoy@hotmail.com>
 */
@Data
public class BeanIoReader {

    private final String ENCODING = "ISO-8859-1";

    public List<String> errors = new ArrayList<>();

    public Optional<FileParsedDTO> parse(InputStream inputStream) throws Exception {

        errors.clear();

        BeanReader reader = null;
        BeanErrorHandler errorHandler = new BeanErrorHandler();
        FileParsedDTO record = null;

        byte[] byteArray = IOUtils.toByteArray(inputStream);
        InputStream header = new ByteArrayInputStream(byteArray);
        InputStream file = new ByteArrayInputStream(byteArray);

        MeansurementFileType type = getType(header);
        Stream stream = Stream.getByLayoutFile(type);

        try {
            StreamFactory factory = StreamFactory.newInstance();
            InputStream str = factory.getClass().getClassLoader().getResourceAsStream(stream.getStreamFile());
            factory.load(str);
            Reader rr = new InputStreamReader(file, ENCODING);
            reader = factory.createReader(stream.getStreamId(), rr);
            reader.setErrorHandler(errorHandler);
            record = (FileParsedDTO) reader.read();

            if (record != null) {
                record.setType(type.toString());
            }

            if (!errorHandler.getListErrors().isEmpty()) {
                errors.addAll(errorHandler.getListErrors());
            }

        } catch (Exception ex) {
            Logger.getLogger(BeanIoReader.class.getName()).log(Level.SEVERE, "[parse]", ex);
            throw ex;
        } finally {
            if (reader != null) {
                reader.close();
            }
        }

        return Optional.ofNullable(record);
    }

    public MeansurementFileType getType(InputStream inputStream) throws Exception {

        errors.clear();
        Stream stream = Stream.CHECK_LAYOUT_PARSER;
        BeanReader reader = null;
        FileParsedDTO record = null;

        try {
            StreamFactory factory = StreamFactory.newInstance();
            InputStream str = factory.getClass().getClassLoader().getResourceAsStream(stream.getStreamFile());
            factory.load(str);
            Reader rr = new InputStreamReader(inputStream, ENCODING);
            reader = factory.createReader(stream.getStreamId(), rr);
            record = (FileParsedDTO) reader.read();

            String content = record.getInformations().get(0).getValue();
            JaroWinklerDistance n = new JaroWinklerDistance();

            if (n.apply(CONTENT_ID_LAYOUT_A, content) >= 0.8) {
                return MeansurementFileType.LAYOUT_A;
            } else if (n.apply(CONTENT_ID_LAYOUT_B, content) >= 0.8) {
                return MeansurementFileType.LAYOUT_B;
            } else if (n.apply(CONTENT_ID_LAYOUT_C, content) >= 0.8) {
                return MeansurementFileType.LAYOUT_C;
            } else {
                throw new Exception("Não foi possivel determinar o layout do arquivo!");
            }

        } catch (Exception ex) {
            Logger.getLogger(BeanIoReader.class.getName()).log(Level.SEVERE, "[parse]", ex);
            throw ex;
        } finally {
            if (reader != null) {
                // reader.close();
            }
        }

    }

}
