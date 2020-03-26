/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.workflow.task;

import com.core.matrix.model.Log;
import com.core.matrix.service.LogService;
import com.core.matrix.service.MeansurementFileResultService;
import static com.core.matrix.utils.Constants.CONTROLE;
import static com.core.matrix.utils.Constants.RESPONSE_CURTOPRAZO;
import static com.core.matrix.utils.Constants.RESPONSE_FATURAMENTO;
import static com.core.matrix.utils.Constants.RESPONSE_RECOMPRA;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.springframework.context.ApplicationContext;

/**
 *
 * @author Aloysio
 */
public class CheckTake implements JavaDelegate {
 
    private static ApplicationContext context;

    private MeansurementFileResultService resultService;
    private LogService logService; 
    
    public CheckTake(ApplicationContext context) {
        CheckTake.context = context;
    }

    public CheckTake() {
        synchronized (CheckTake.context) {
            resultService = CheckTake.context.getBean(MeansurementFileResultService.class);
            logService = CheckTake.context.getBean(LogService.class);
            
        }
    }
    
    
    @Override
    public void execute(DelegateExecution execution) throws Exception {

        try {
      
            // PRIMEIRO BUSCAR O VALOR DO SOLICITADO LIQUIDO:
                //PODE SER O SOLICITADO LIQUIDO AJUSTADO > TEM QUE VERIFICAR SE FOI APROVADO
                //SENAO TEMOS QUE PEGAR O SOLICITADO LIQUIDO QUE FOI CALCULADO
            
            // SE SOLICITADO LIQUIDO <= LIMITE MINIMO
                //ENTAO  execution.setVariable(CONTROLE, RESPONSE_RECOMPRA);
                
            // SE SOLICITADO LIQUIDO >= LIMITE MAXIMO
                //ENTAO execution.setVariable(CONTROLE, RESPONSE_CURTOPRAZO);
                
            // SE SOLICITADO LIQUIDO > LIMITE MINIMO E SOLICITADO LIQUIDO < LIMITE MAXIMO
                //ENTAO execution.setVariable(CONTROLE, RESPONSE_FATURAMENTO);
                       
            
        } catch (Exception e) {
                        Logger.getLogger(CheckTake.class.getName()).log(Level.SEVERE, "[execute]", e);
                        Log log = new Log();
                        log.setActivitiName(execution.getCurrentActivityName());
                        log.setMessageErrorApplication(e.getLocalizedMessage());
                        log.setMessage("Erro ao verificar o resultado do take.");
                        logService.save(log);
       }
    }
 }
