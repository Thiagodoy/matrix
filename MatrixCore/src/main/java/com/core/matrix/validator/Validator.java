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

    private static final String MESSAGE_ERROR_REGEX = "O registro na linha {0} está com formatação inválida para o campo [ {1} ]";
    private static final String MESSAGE_ERROR_REQUIRED = "O registro na linha {0} está com o valor ausente para  o campo [ {1}]";

    private static final String REGEX_ENERGY_TYPE = "[a-zA-Z\\s]+";
    private static final String REGEX_HOUR = "[0-9]+";
    private static final String REGEX_CONSUMPTION_ACTIVE = "[0-9]+(,?[0-9]+)?";
    private static final String REGEX_POINT = "[a-zA-Z0-9-\\s\\(\\)]+";
    private static final String REGEX_DATE = "([0-3]?[0-9]/[0-3]?[0-9]/(?:[0-9]{2})?[0-9]{2})|([0-3]?[0-9]/(?:[0-9]{2})?[0-9]{2})|((?:[0-9]{2})?[0-9]{2}-[0-3]?[0-9]-[0-3]?[0-9])";

    private List<String> errors;

    public void validateMeansurementPoint(Long index, String value) {

        Optional<String> opt = Optional.ofNullable(value);

        if (!opt.isPresent() || (opt.isPresent() && opt.get().isEmpty())) {
            this.errors.add(MessageFormat.format(MESSAGE_ERROR_REQUIRED, (index + 1), "Ponto de Medição"));
        } else {

            Pattern pattern = Pattern.compile(REGEX_POINT);
            Matcher matcher = pattern.matcher(value);

            if (!matcher.matches()) {
                this.errors.add(MessageFormat.format(MESSAGE_ERROR_REGEX, (index + 1), "Ponto de Medição"));
            }
        }
    }

    public void validateDate(Long index, String value) {

        Optional<String> opt = Optional.ofNullable(value);

        if (!opt.isPresent() || (opt.isPresent() && opt.get().isEmpty())) {
            this.errors.add(MessageFormat.format(MESSAGE_ERROR_REQUIRED, (index + 1), "Data"));
        } else {

            Pattern pattern = Pattern.compile(REGEX_DATE);
            Matcher matcher = pattern.matcher(value);

            if (!matcher.matches()) {
                this.errors.add(MessageFormat.format(MESSAGE_ERROR_REGEX, (index + 1), "Data"));
            }
        }

    }

    public void validateHour(Long index, String value) {

        Optional<String> opt = Optional.ofNullable(value);

        if (!opt.isPresent() || (opt.isPresent() && opt.get().isEmpty())) {
            this.errors.add(MessageFormat.format(MESSAGE_ERROR_REQUIRED, (index + 1), "Hora"));
        } else {

            Pattern pattern = Pattern.compile(REGEX_HOUR);
            Matcher matcher = pattern.matcher(value);

            if (!matcher.matches()) {
                this.errors.add(MessageFormat.format(MESSAGE_ERROR_REGEX, (index + 1), "Hora"));
            }
        }

    }

    public void validateConsumptionActive(Long index, String value) {

        Optional<String> opt = Optional.ofNullable(value);

        if (!opt.isPresent() || (opt.isPresent() && opt.get().isEmpty())) {
            this.errors.add(MessageFormat.format(MESSAGE_ERROR_REQUIRED, (index + 1), "Consumo Ativo"));
        } else {

            Pattern pattern = Pattern.compile(REGEX_CONSUMPTION_ACTIVE);
            Matcher matcher = pattern.matcher(value);

            if (!matcher.matches()) {
                this.errors.add(MessageFormat.format(MESSAGE_ERROR_REGEX, (index + 1), "Consumo Ativo"));
            }
        }

    }

    public void validateEnergyType(Long index, String value) {

        Optional<String> opt = Optional.ofNullable(value);

        if (!opt.isPresent() || (opt.isPresent() && opt.get().isEmpty())) {
            this.errors.add(MessageFormat.format(MESSAGE_ERROR_REQUIRED, (index + 1), "Tipo de Energia"));
        } else {

            Pattern pattern = Pattern.compile(REGEX_ENERGY_TYPE);
            Matcher matcher = pattern.matcher(value);

            if (!matcher.matches()) {
                this.errors.add(MessageFormat.format(MESSAGE_ERROR_REGEX, (index + 1), "Tipo de Energia"));
            }
        }
    }

    public void validateSourceCollection(Long index, String value) {

        Optional<String> opt = Optional.ofNullable(value);

        if (!opt.isPresent() || (opt.isPresent() && opt.get().isEmpty())) {
            this.errors.add(MessageFormat.format(MESSAGE_ERROR_REQUIRED, (index + 1), "Origem"));
        }
    }

    public List<String> validate(FileDetailDTO detail, MeansurementFileType type) {

        this.errors = new ArrayList();
        
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
