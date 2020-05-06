/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.validator;

import com.core.matrix.dto.FileDetailDTO;
import com.core.matrix.utils.MeansurementFileType;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.Data;

/**
 *
 * @author thiag
 */
@Data
public class Validator {

    private static final String MESSAGE_ERROR_REGEX = "O registro na linha {0} está com formatação inválida para o campo [ {1} ]. Arquivo [ {2} ]";
    private static final String MESSAGE_ERROR_REQUIRED = "O registro na linha {0} está com o valor ausente para  o campo [ {1}]. Arquivo [ {2} ]";
    private static final String MESSAGE_ERROR_REQUIRED_LIQUID = "O registro na linha {0} está com o (L) ausente para  o campo [ {1}]. Arquivo [ {2} ]";

    private static final String REGEX_ENERGY_TYPE = "[a-zA-Z\\s]+";
    private static final String REGEX_HOUR = "[0-9]+";
    private static final String REGEX_CONSUMPTION_ACTIVE = "[0-9.]+(,?[0-9]+)?";
    private static final String REGEX_POINT = "[a-zA-Z0-9-\\s\\(\\)]+";
    private static final String REGEX_DATE = "([0-3]?[0-9]/[0-3]?[0-9]/(?:[0-9]{2})?[0-9]{2})|([0-3]?[0-9]/(?:[0-9]{2})?[0-9]{2})|((?:[0-9]{2})?[0-9]{2}-[0-3]?[0-9]-[0-3]?[0-9])";

    private List<String> errors;
    private String fileName;

    public void validateMeansurementPoint(Long index, String value) {

        Optional<String> opt = Optional.ofNullable(value);

        if (!opt.isPresent() || (opt.isPresent() && opt.get().isEmpty())) {
            this.errors.add(MessageFormat.format(MESSAGE_ERROR_REQUIRED, index, "Ponto de Medição", this.fileName));
        } else {

            Pattern pattern = Pattern.compile(REGEX_POINT);
            Matcher matcher = pattern.matcher(value);

            if (!matcher.matches()) {
                this.errors.add(MessageFormat.format(MESSAGE_ERROR_REGEX, index, "Ponto de Medição", this.fileName));
            }
        }
    }

    public void validateDate(Long index, String value) {

        Optional<String> opt = Optional.ofNullable(value);

        if (!opt.isPresent() || (opt.isPresent() && opt.get().isEmpty())) {
            this.errors.add(MessageFormat.format(MESSAGE_ERROR_REQUIRED, index, "Data", this.fileName));
        } else {

            Pattern pattern = Pattern.compile(REGEX_DATE);
            Matcher matcher = pattern.matcher(value);

            if (!matcher.matches()) {
                this.errors.add(MessageFormat.format(MESSAGE_ERROR_REGEX, index , "Data", this.fileName));
            }
        }

    }

    public void validateHour(Long index, String value) {

        Optional<String> opt = Optional.ofNullable(value);

        if (!opt.isPresent() || (opt.isPresent() && opt.get().isEmpty())) {
            this.errors.add(MessageFormat.format(MESSAGE_ERROR_REQUIRED, index , "Hora", this.fileName));
        } else {

            Pattern pattern = Pattern.compile(REGEX_HOUR);
            Matcher matcher = pattern.matcher(value);

            if (!matcher.matches()) {
                this.errors.add(MessageFormat.format(MESSAGE_ERROR_REGEX, index , "Hora", this.fileName));
            }
        }

    }

    public void validateConsumptionActive(Long index, String value) {

        Optional<String> opt = Optional.ofNullable(value);

        if (!opt.isPresent() || (opt.isPresent() && opt.get().isEmpty())) {
            this.errors.add(MessageFormat.format(MESSAGE_ERROR_REQUIRED, index , "Consumo Ativo", this.fileName));
        } else {

            Pattern pattern = Pattern.compile(REGEX_CONSUMPTION_ACTIVE);
            Matcher matcher = pattern.matcher(value);

            if (!matcher.matches()) {
                this.errors.add(MessageFormat.format(MESSAGE_ERROR_REGEX, index , "Consumo Ativo", this.fileName));
            }
        }

    }

    public void validateEnergyType(Long index, String value) {

        Optional<String> opt = Optional.ofNullable(value);

        if (!opt.isPresent() || (opt.isPresent() && opt.get().isEmpty())) {
            this.errors.add(MessageFormat.format(MESSAGE_ERROR_REQUIRED, index , "Tipo de Energia", this.fileName));
        } else {

            Pattern pattern = Pattern.compile(REGEX_ENERGY_TYPE);
            Matcher matcher = pattern.matcher(value);

            if (!matcher.matches()) {
                this.errors.add(MessageFormat.format(MESSAGE_ERROR_REGEX, index , "Tipo de Energia", this.fileName));
            }
        }
    }

    public void validateSourceCollection(Long index, String value) {

        Optional<String> opt = Optional.ofNullable(value);

        if (!opt.isPresent() || (opt.isPresent() && opt.get().isEmpty())) {
            this.errors.add(MessageFormat.format(MESSAGE_ERROR_REQUIRED, index , "Origem", this.fileName));
        }
    }
    
    public synchronized static boolean validateContentIfContains(List<FileDetailDTO>details){       
        
         return details.stream().anyMatch(d-> d.getMeansurementPoint().contains("(L)"));    
    }

    public List<String> validate(FileDetailDTO detail, MeansurementFileType type) {

        this.errors = new ArrayList();
        this.fileName = detail.getFileName();
        
        this.validateMeansurementPoint(detail.getLine(), detail.getMeansurementPoint());
        this.validateHour(detail.getLine(), detail.getHour());
        this.validateDate(detail.getLine(), detail.getDate());
        this.validateConsumptionActive(detail.getLine(), detail.getConsumptionActive());
        
        if(type.equals(MeansurementFileType.LAYOUT_B)){
            this.validateSourceCollection(detail.getLine(), detail.getSourceCollection());
            this.validateEnergyType(detail.getLine(), detail.getEnergyType());
        }

        return this.errors;
        
    }

}
