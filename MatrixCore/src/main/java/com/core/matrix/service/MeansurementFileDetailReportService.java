/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.service;

import com.core.matrix.jobs.BindFileToProcessJob;
import com.core.matrix.model.MeansurementFileDetailReport;
import com.core.matrix.utils.Utils;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author thiag
 */
@org.springframework.stereotype.Service
public class MeansurementFileDetailReportService {

    private static Connection connection;

    private static final ThreadPoolExecutor pool = (ThreadPoolExecutor) Executors.newFixedThreadPool(1);

    @Autowired
    private TableSequenceService sequenceService;

    @Autowired
    private DataSource dataSource;

    private Connection getConnection() throws SQLException {

        try {
            if (this.connection == null || this.connection.isClosed() || !this.connection.isValid(0)) {
                this.connection = dataSource.getConnection();
                return this.connection;
            } else {
                return this.connection;
            }

        } catch (Exception e) {
            Logger.getLogger(BindFileToProcessJob.class.getName()).log(Level.SEVERE, "[getConnection]", e);
            this.connection = dataSource.getConnection();
            return this.connection;
        }

    }

    public synchronized void saveAllJob(List<MeansurementFileDetailReport> detail) {

        this.pool.submit((() -> {
            this.saveAllBatch(detail);
        }));
    }

    private void saveAllBatch(List<MeansurementFileDetailReport> detail) {

        try {

            int limit = 3000;

            long init = sequenceService.getValue("mtx_arquivo_de_medicao_detalhe_relatorio", detail.size());            
            
            
            for (int i = 0; i < detail.size(); i++) {
                detail.get(i).setId(init++);
            }

            Connection con = getConnection();

            while (!detail.isEmpty()) {

                int indexEnd = detail.size() > limit ? limit : detail.size();
                List<String> records = Utils.<MeansurementFileDetailReport>mountBatchInsert(detail.subList(0, indexEnd));

                String query = "INSERT INTO `matrix`.`mtx_arquivo_de_medicao_detalhe_relatorio`  "
                        + "(id_arquivo_de_medicao_detalhe_relatorio,id_arquivo_de_medicao,ponto_medicao,data,hora,tipo_energia,ativa_geracao,ativa_consumo,\n"
                        + "reativa_geracao,reativa_consumo,situacao_da_medida,motivo_da_situacao,intervalo,origem_coleta,notificacao_coleta,agente,\n"
                        + "qualidade,origem,data_insercao)  VALUES " + records.stream().collect(Collectors.joining(",\n"));

                try {
                    Statement ps = con.createStatement();
                    ps.clearBatch();
                    ps.addBatch(query);
                    ps.executeBatch();
                    detail.subList(0, indexEnd).clear();
                    con.commit();
                } catch (Exception e) {
                    Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "[saveAllBatch]", e);
                }

            }

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

    }

}
