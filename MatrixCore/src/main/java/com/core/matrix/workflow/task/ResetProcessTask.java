/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.workflow.task;

import static com.core.matrix.utils.Constants.CONTROLE;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.transaction.Transactional;
import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;

/**
 *
 * @author thiag
 */
public class ResetProcessTask implements JavaDelegate {

    private TaskService taskService;

    @Override
    public void execute(DelegateExecution execution) throws Exception {

        this.taskService = execution.getEngineServices().getTaskService();

        try {
            this.restartProcess();
        } catch (Exception e) {
            Logger.getLogger(ResetProcessTask.class.getName()).log(Level.SEVERE, "[execute]", e);
        }

    }

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    private void restartProcess() {

        LocalDate dateBilling = LocalDate.now().minusMonths(1);

        int month = dateBilling.getMonthValue();
        int year = dateBilling.getYear();

        this.taskService
                .createNativeTaskQuery()
                .sql("SELECT \n"
                        + "   distinct b.*\n"
                        + "FROM\n"
                        + "    matrix.mtx_arquivo_de_medicao a\n"
                        + "        INNER JOIN\n"
                        + "    activiti.ACT_RU_TASK b ON a.act_id_processo = b.proc_inst_id_\n"
                        + "WHERE\n"
                        + "        a.mes = " + month + "\n"
                        + "        AND a.ano = " + year + "\n"
                        + "        AND a.wbc_ponto_de_medicao IS NOT NULL\n"
                        + "        AND b.TASK_DEF_KEY_ in('task-show-error-1','task-show-error-2','task-show-error-ajustament')")
                .list().parallelStream().forEach(task -> {

                    try {

                        Map<String, Object> parameters = new HashMap<>();

                        if (task.getTaskDefinitionKey().equals("task-show-error-ajustament")) {
                            parameters.put(CONTROLE, "REALIZAR NOVO UPLOAD");
                        }
                        Logger.getLogger(ProcessFilesInLoteTask.class.getName()).log(Level.INFO, "[restartProcess] -> " + task.getId());
                        taskService.complete(task.getId(), parameters);

                    } catch (Exception e) {
                        Logger.getLogger(ProcessFilesInLoteTask.class.getName()).log(Level.SEVERE, "[restartProcess] -> Erro ao realizar o completeTask", e);
                    }
                });

    }

}
