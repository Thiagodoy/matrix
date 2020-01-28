package com.core.matrix;

import com.core.matrix.dto.FileParsedDTO;
import com.core.matrix.io.BeanIoReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Optional;
import org.junit.jupiter.api.Test;

//@SpringBootTest
class FileParserTests {

    @Test
    void contextLoads() throws FileNotFoundException, IOException, Exception {

       

        BeanIoReader reader = new BeanIoReader();

        FileInputStream inputStream = new FileInputStream(new File("TIPO C.csv"));
        
       Optional<FileParsedDTO> opt = reader.<FileParsedDTO>parse(inputStream);
       
       
        //inputStream.close();
        
        
        
                
//        BOMInputStream bomIn = new BOMInputStream(inputStream);
//        
//        System.out.println(" has getBOMCharsetName -> " + bomIn.getBOMCharsetName() );
//        System.out.println(" has getBOM -> " + bomIn.getBOM() );
//        System.out.println(" has markSupported -> " + bomIn.markSupported() );
//        System.out.println(" has hasBOM -> " + bomIn.hasBOM());
        

        // Optional<FileParsedDTO> fileParsed = reader.<FileParsedDTO>parse(inputStream, Stream.CHECK_LAYOUT_A_PARSER);
       // System.out.println(" check layout -> " + reader.getType(inputStream));

        
    }

    

}
