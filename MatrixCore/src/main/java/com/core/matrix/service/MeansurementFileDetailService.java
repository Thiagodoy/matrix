/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.service;

import com.core.matrix.dto.DataValidationResultDTO;
import com.core.matrix.jobs.BindFileToProcessJob;
import com.core.matrix.model.MeansurementFile;
import com.core.matrix.model.MeansurementFileDetail;
import com.core.matrix.repository.MeansurementFileDetailRepository;
import com.core.matrix.utils.Constants;
import static com.core.matrix.utils.Constants.CONST_QUALITY_COMPLETE;
import static com.core.matrix.utils.Constants.CONST_SITUATION_3;
import static com.core.matrix.utils.Constants.TYPE_ENERGY_LIQUID;
import com.core.matrix.utils.MeansurementFileDetailStatus;
import com.core.matrix.utils.Utils;
import java.io.File;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.sql.DataSource;

import org.activiti.engine.RuntimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author thiag
 */
@Service
public class MeansurementFileDetailService {

    @Autowired
    private MeansurementFileDetailRepository repository;

    @Autowired
    private MeansurementFileService fileService;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TableSequenceService sequenceService;

    private static File directory;

    private static Connection connection;

    static {
        directory = new File(Constants.DIR_FILE_NO_PESIST);

        if (!directory.isDirectory()) {
            directory.mkdir();
        }
    }

    @Transactional
    public void save(MeansurementFileDetail detail) {
        this.repository.save(detail);
    }

    @Transactional(transactionManager = "matrixTransactionManager")
    public List<MeansurementFileDetail> save(List<MeansurementFileDetail> detail) {
        return this.repository.saveAll(detail);
    }

    @Transactional(readOnly = true)
    public List<MeansurementFileDetail> listByFileId(Long fileId) {
        return this.repository.findByIdMeansurementFile(fileId);
    }

    @Transactional(transactionManager = "matrixTransactionManager")
    public void deleteAll(List<MeansurementFileDetail> details) {
        this.repository.deleteAll(details);
    }

    @Transactional(transactionManager = "matrixTransactionManager")
    public void deleteByMeansurementFileId(List<Long> ids) {
        this.repository.deleteByIdMeansurementFileIn(ids);
    }

    @Transactional(transactionManager = "matrixTransactionManager")
    public void deleteByMeansurementFileId(Long id) {
        this.repository.deleteByIdMeansurementFile(id);
    }

    @Transactional
    public List<MeansurementFileDetail> getDetails(Long id) {
        return this.repository.findByIdMeansurementFile(id);
    }

    public void saveAllBatch(List<MeansurementFileDetail> detail, String process) {

        try {

            int limit = 3000;

            long init = sequenceService.getValue("mtx_arquivo_de_medicao_detalhe", detail.size());

            for (int i = 0; i < detail.size(); i++) {
                detail.get(i).setId(init++);
            }

            Connection con = getConnection();
            int count = 1;
            while (!detail.isEmpty()) {

                int indexEnd = detail.size() > limit ? limit : detail.size();
                List<String> records = Utils.<MeansurementFileDetail>mountBatchInsert(detail.subList(0, indexEnd));

                String query = "INSERT INTO `matrix`.`mtx_arquivo_de_medicao_detalhe`  "
                        + "(id_arquivo_de_medicao_detalhe,id_arquivo_de_medicao,ponto_medicao,data,hora,tipo_energia,ativa_geracao,ativa_consumo,\n"
                        + "reativa_geracao,reativa_consumo,situacao_da_medida,motivo_da_situacao,intervalo,origem_coleta,notificacao_coleta,agente,\n"
                        + "qualidade,origem,status)  VALUES " + records.stream().collect(Collectors.joining(",\n"));

                try {
                    Statement ps = con.createStatement();
                    ps.clearBatch();
                    ps.addBatch(query);
                    ps.executeBatch();
                    detail.subList(0, indexEnd).clear();
                    con.commit();
                } catch (Exception e) {
                    writeFile(query, process, e.getLocalizedMessage(), count++);
                }

            }

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

    }

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

    private void writeFile(String query, String processInstance, String error, int count) {

        String data = new SimpleDateFormat("dd-mm-yyyy").format(new Date());

        try {

            File file = new File(this.directory, MessageFormat.format("detail-p{0}-{1}-{2}.sql", processInstance, data, count));
            FileWriter writer = new FileWriter(file);

            writer.write(error + "\n\n\n" + query);
            writer.flush();
            writer.close();

        } catch (Exception e) {
            Logger.getLogger(MeansurementFileDetailService.class.getName()).log(Level.SEVERE, "[writeFile]", e);
        }

    }

    @Transactional
    public void fixFile(List<DataValidationResultDTO> requests) throws Exception {

        String processInstanceId = requests.stream().findFirst().get().getProcessInstanceId();

        Map<String, List<MeansurementFileDetail>> mapDetails = runtimeService.getVariable(processInstanceId, Constants.VAR_MAP_DETAILS, Map.class);
        List<MeansurementFile> files = runtimeService.getVariable(processInstanceId, Constants.VAR_LIST_FILES, List.class);

        requests.parallelStream().forEach(request -> {
            final Double value = request.getInputManual() / request.getHours();
            final MeansurementFile file = files.stream().filter(f -> f.getId().equals(request.getIdFile())).findFirst().get();
            mapDetails.get(request.getPoint())
                    .parallelStream()
                    .filter(d -> d.getStatus().equals(MeansurementFileDetailStatus.HOUR_ERROR) || d.getStatus().equals(MeansurementFileDetailStatus.DAY_ERROR))
                    .forEach(d -> {

                        d.setConsumptionActive(value);
                        d.setStatus(MeansurementFileDetailStatus.INPUT_MANUAL);
                        switch (file.getType()) {

                            case LAYOUT_A:
                                d.setEnergyType(TYPE_ENERGY_LIQUID);
                                d.setReasonOfSituation(CONST_SITUATION_3);
                                break;

                            case LAYOUT_B:
                                d.setEnergyType(TYPE_ENERGY_LIQUID);
                                d.setSourceCollection(Constants.CONST_SOURCE_COLLECTION_3);
                                break;

                            case LAYOUT_C:
                            case LAYOUT_C_1:
                                d.setQuality(CONST_QUALITY_COMPLETE);
                                d.setOrigem(CONST_SITUATION_3);
                                break;
                        }

                    });

        });

        runtimeService.setVariable(processInstanceId, Constants.VAR_MAP_DETAILS, mapDetails);

    }
}
