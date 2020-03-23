/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.workflow.task;

import com.core.matrix.model.Log;
import com.core.matrix.model.MeansurementFileResult;
import com.core.matrix.service.LogService;
import com.core.matrix.service.MeansurementFileResultService;
import static com.core.matrix.utils.Constants.CONTROLE;
import static com.core.matrix.utils.Constants.RESPONSE_NIVEL_1;
import static com.core.matrix.utils.Constants.RESPONSE_NIVEL_2;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.springframework.context.ApplicationContext;

/**
 *
 * @author Aloysio
 */
public class CheckLevelOfApproval implements JavaDelegate {
 
    private static ApplicationContext context;

    private MeansurementFileResultService resultService;
    private LogService logService;

    List<MeansurementFileResult> resultList;
    
    
    public CheckLevelOfApproval(ApplicationContext context) {
        CheckLevelOfApproval.context = context;
    }

    public CheckLevelOfApproval() {
        synchronized (CheckLevelOfApproval.context) {
            resultService = CheckLevelOfApproval.context.getBean(MeansurementFileResultService.class);
            logService = CheckLevelOfApproval.context.getBean(LogService.class);
        }
    }
    
    
    @Override
    public void execute(DelegateExecution execution) throws Exception {

        try {
    
            //primeiro passo é verificar se o campo foi preenchido`mtx_arquivo_de_medicao_resultado`.`montante_liquido_ajustado`
                //se for rateio sempre consideramos o montante `mtx_arquivo_de_medicao_resultado`.`montante_liquido_ajustado` do contrato pai
                //senao foi preencido ja podemos mandar execution.setVariable(CONTROLE, RESPONSE_NIVEL_1); //sem alcada de aprovacao
                         
            //segundo passo é buscar a diferenca entre os valores (delta)  `mtx_arquivo_de_medicao_resultado`.`montante_liquido_ajustado` e `mtx_arquivo_de_medicao_resultado`.`montante_liquido`
 
            //terceiro passo é buscar em qual nivel de alcada a atividade esta sendo executada.
                //eu adicionei o campo documentation da task o nome do perfil para identificarmos em qual perfil a atividade esta sendo executada.
            
            //quarto passo é verificar na tabela matrix.mtx_alcada_aprovacao 
                //alcada = task.documentation and delta > faixa_max
                    //entao execution.setVariable(CONTROLE, RESPONSE_NIVEL_2);
                    //senao execution.setVariable(CONTROLE, RESPONSE_NIVEL_1);
                        
            } catch (Exception e) {
                        Logger.getLogger(CheckLevelOfApproval.class.getName()).log(Level.SEVERE, "[execute]", e);
                        Log log = new Log();
                        log.setActivitiName(execution.getCurrentActivityName());
                        log.setMessageErrorApplication(e.getLocalizedMessage());
                        log.setMessage("Erro ao verificar alçada de aprovação");
                        logService.save(log);
            }

    }
 }
