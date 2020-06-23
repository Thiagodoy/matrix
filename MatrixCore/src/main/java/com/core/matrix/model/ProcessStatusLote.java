/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.model;

import com.core.matrix.model.MeansurementFile;
import com.core.matrix.utils.Utils;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author thiag
 */
@Entity
@Table(name = "mtx_status_processamento_lote")
@Data
@NoArgsConstructor
public class ProcessStatusLote implements Serializable{

    private static final long serialVersionUID = 6264517643114257020L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_lote")
    private Long id;
    
    @Column(name = "nome_tarefa")
    private String taskName;
    
    @Column(name = "wbc_contrato")
    private Long contract;
    
    @Column(name = "wbc_ponto_medicao")
    private String point;
    
    @Column(name = "empresa_apelido")
    private String nickname;
    
    @Column(name = "status")
    private String status;
    
    @Column(name = "act_id_processo")
    private String processInstanceId;
    
     @Column(name = "act_id_processo_lote")
    private String processInstanceIdLote;

    public ProcessStatusLote(String taskName, Long contract, String point, String nickname, String status, String processInstance) {
        this.taskName = taskName;
        this.contract = contract;
        this.point = point;
        this.nickname = nickname;
        this.status = status;
        this.processInstanceId = processInstance;
    }

    public ProcessStatusLote(MeansurementFile file) {
        this.contract = file.getWbcContract();
        this.processInstanceId = file.getProcessInstanceId();
        this.point = file.getMeansurementPoint();
        this.status = Utils.getStatus(file.getStatus());
        this.nickname = file.getNickname();
    }

}
