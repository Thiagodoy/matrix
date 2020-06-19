/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.dto;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Observable;
import java.util.Optional;
import java.util.Set;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 *
 * @author thiag
 */
@Data
@EqualsAndHashCode(of = {"processInstanceId","taskId"})
public class ProcessFilesInLoteStatusDTO extends Observable implements Serializable {

    private static final long serialVersionUID = 6867842910436787748L;

    public enum Status {
        ERROR,
        ASSOCIATED,
        PENDING,
        INCOMPLETE
    }

    private String processInstanceId;
    private String processInstanceName;
    private String taskId;
    private String taskName;
    private String attachment;
    private List<String> points = new ArrayList<>();
    private Set<String> pointsChecked = new HashSet<>();
    private List<String> errors = new ArrayList<>();
    private Status status;
    private transient HeaderDTO header;
    private transient List<InformationDTO> informations = new ArrayList<>();
    private transient List<FileDetailDTO> details = new ArrayList<>(); 
    private String typeFile;
    private boolean isFinished = false;

    
    
    public void setError(String message){
        this.errors.add(message);
    }
    
    public void pointChecked(String point, List<InformationDTO> informations, HeaderDTO header, List<FileDetailDTO> details, String type) {

        
        if(this.isFinished){
            return;
        }
        
        
        this.pointsChecked.add(point.replaceAll("\\((L|B)\\)", "").trim());
        
        if(details.isEmpty()){
            return;
        } 
        
        
        this.details.addAll(details);

        if (this.informations.isEmpty()) {
            this.informations.addAll(informations);
        }

        if (!Optional.ofNullable(this.header).isPresent()) {
            this.header = header;
        }
        
        if(!Optional.ofNullable(this.typeFile).isPresent()){
            this.typeFile = type;
        }

        if (this.points.size() == pointsChecked.size() && points.containsAll(pointsChecked)) {

            this.isFinished = true;
            this.setChanged();
            this.notifyObservers(Boolean.TRUE);
            this.clearChanged();
            
        }

    }

    public FileParsedDTO getFileParsedDTO() {
        FileParsedDTO fpdto = new FileParsedDTO();
        fpdto.setInformations(informations);
        fpdto.setHeader(header);
        fpdto.setDetails( new ArrayList<>(details));
        fpdto.setType(typeFile);

        return fpdto;
    }

    
    public String toString() {
        return MessageFormat.format("Processo status -> Task: {0} -  Instance: {1}", this.taskId, this.processInstanceId );
    }
    
    

}
