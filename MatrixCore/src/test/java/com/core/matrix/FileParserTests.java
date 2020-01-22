package com.core.matrix;

import com.core.matrix.dto.FileParsedDTO;
import com.core.matrix.io.BeanIoReader;
import com.core.matrix.io.Stream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class FileParserTests {

    
    
    
    
    
	@Test
	void contextLoads() throws FileNotFoundException, IOException {            
            
            
            
//            FileWriter writer = new FileWriter(new File("teste.txt"));
//            
//            writer.write("Teste");
//            writer.flush();
//            writer.close();
            
            
            BeanIoReader reader = new BeanIoReader();
            
            FileInputStream inputStream = new FileInputStream(new File("medidas-consolidadas-conimel.csv"));
            
            
            Optional<FileParsedDTO> fileParsed = reader.<FileParsedDTO>parse(inputStream, Stream.FILE_LAYOUT_PARSER);;
            
            
            
            
	} 

        
        
        
}
