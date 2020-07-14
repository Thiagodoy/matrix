/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.service;

import com.core.matrix.dto.DataValidationResultDTO;
import com.core.matrix.model.MeansurementFile;
import com.core.matrix.model.MeansurementFileDetail;
import com.core.matrix.repository.MeansurementFileDetailRepository;
import com.core.matrix.utils.Constants;
import static com.core.matrix.utils.Constants.CONST_QUALITY_COMPLETE;
import static com.core.matrix.utils.Constants.CONST_SITUATION_3;
import static com.core.matrix.utils.Constants.TYPE_ENERGY_LIQUID;
import com.core.matrix.utils.MeansurementFileDetailStatus;
import com.core.matrix.utils.MeansurementFileStatus;
import com.core.matrix.utils.Utils;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.sql.DataSource;
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

    @Transactional
    public void save(MeansurementFileDetail detail) {
        this.repository.save(detail);
    }

    @Transactional(transactionManager = "matrixTransactionManager")
    public void save(List<MeansurementFileDetail> detail) {
        this.repository.saveAll(detail);
    }

    public void saveAllBatch(List<MeansurementFileDetail> detail) {

        try {
            Connection con = dataSource.getConnection();
            int limit = 3000;
            

            while (!detail.isEmpty()) {

                int indexEnd = detail.size() > limit ? limit : detail.size();
                List<String> records = Utils.<MeansurementFileDetail>mountBatchInsert(detail.subList(0, indexEnd));             
                
                String query = "INSERT INTO `matrix`.`mtx_arquivo_de_medicao_detalhe` VALUES " + records.stream().collect(Collectors.joining(","));
                Statement ps = con.createStatement();
                ps.clearBatch();
                ps.addBatch(query);
                ps.executeBatch();
                con.commit();
                detail.subList(0, indexEnd).clear();
           }
        } catch (SQLException ex) {
            Logger.getLogger(MeansurementFileDetailService.class.getName()).log(Level.SEVERE, "[saveAllBatch]", ex);
            throw new RuntimeException(ex.getMessage());
        }

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
    public void deleteByMeansurementFileId(Long id) {
        this.repository.deleteByIdMeansurementFile(id);
    }

    @Transactional
    public void fixFile(DataValidationResultDTO request) throws Exception {

        List<MeansurementFileDetailStatus> status = Arrays.asList(MeansurementFileDetailStatus.HOUR_ERROR, MeansurementFileDetailStatus.DAY_ERROR);

        List<MeansurementFileDetail> result = this.repository.findByIdMeansurementFileAndStatusIn(request.getIdFile(), status);

        MeansurementFile file = fileService.findById(request.getIdFile());

        Double value = request.getInputManual() / request.getHours();

        // Normalizing data for billing
        result.stream().forEach(detail -> {
            detail.setConsumptionActive(value);
            detail.setStatus(MeansurementFileDetailStatus.INPUT_MANUAL);
            switch (file.getType()) {

                case LAYOUT_A:
                    detail.setEnergyType(TYPE_ENERGY_LIQUID);
                    detail.setReasonOfSituation(CONST_SITUATION_3);
                    break;

                case LAYOUT_B:
                    detail.setEnergyType(TYPE_ENERGY_LIQUID);
                    detail.setSourceCollection(Constants.CONST_SOURCE_COLLECTION_3);
                    break;

                case LAYOUT_C:
                case LAYOUT_C_1:
                    detail.setQuality(CONST_QUALITY_COMPLETE);
                    detail.setOrigem(CONST_SITUATION_3);
                    break;
            }
        });

        this.repository.saveAll(result);

        fileService.updateStatus(MeansurementFileStatus.SUCCESS, file.getId());

    }

}
