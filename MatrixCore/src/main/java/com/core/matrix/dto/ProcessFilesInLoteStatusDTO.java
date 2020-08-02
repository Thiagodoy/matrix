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
import java.util.Map;
import java.util.Observable;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 *
 * @author thiag
 */
@Data
@EqualsAndHashCode(of = {"processInstanceId", "taskId"})
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

    private List<FileParsedDTO> filesByPoint = new ArrayList<>();

    private Status status;
    private transient HeaderDTO header;
    private transient List<InformationDTO> informations = new ArrayList<>();
    private transient List<FileDetailDTO> details = new ArrayList<>();
    private String typeFile;
    private boolean isFinished = false;
    
    private boolean isUnitConsumerOrFlat;

    public void setError(String message) {
        this.errors.add(message);
    }

    public boolean isCompletedSearch() {
        return this.pointsChecked.size() == this.points.size();
    }

    public void isOnlyUnitConsumerOrIsFlat() {
        this.isUnitConsumerOrFlat = true;
        this.isFinished = true;
        this.setChanged();
        this.notifyObservers(Boolean.TRUE);
        this.clearChanged();
    }

    public void pointChecked(List<InformationDTO> informations, HeaderDTO header, String type) {

        if (this.isFinished) {
            return;
        }

        if (details.isEmpty()) {
            return;
        }

        if (this.informations.isEmpty()) {
            this.informations.addAll(informations);
        }

        if (!Optional.ofNullable(this.header).isPresent()) {
            this.header = header;
        }

        if (!Optional.ofNullable(this.typeFile).isPresent()) {
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
        fpdto.setDetails(new ArrayList<>(details));
        fpdto.setType(typeFile);

        return fpdto;
    }

    public String toString() {
        return MessageFormat.format("Processo status -> Task: {0} -  Instance: {1}", this.taskId, this.processInstanceId);
    }

    public Set<FileParsedDTO> prepareFiles() {

        Set<FileParsedDTO> result = new HashSet<>();

        Map<String, List<FileParsedDTO>> groupByLayout = this.filesByPoint
                .stream()
                .collect(Collectors.groupingBy(FileParsedDTO::getType, Collectors.toList()));

        groupByLayout.keySet().forEach(key -> {

            if (groupByLayout.get(key).size() == 1) {
                result.add(groupByLayout.get(key).get(0));
            } else {
                List<FileParsedDTO> list = groupByLayout.get(key);
                FileParsedDTO fileParsedDTO = list.get(0);

                list.subList(1, list.size()).stream().forEach(ff -> {
                    fileParsedDTO.getDetails().addAll(ff.getDetails());
                });
                result.add(fileParsedDTO);
            }

        });

        return result;
    }

}
