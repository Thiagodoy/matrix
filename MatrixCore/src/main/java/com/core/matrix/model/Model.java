/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.model;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author thiag
 */
public interface Model<T extends Model> {

    Long getId();

    default void update(T entity) {
        List<Field> fields = Arrays.asList(entity.getClass().getDeclaredFields());

        fields.forEach(f -> {

            Type type = f.getGenericType();

            try {
                Object valueOfInstance = f.get(this);
                Object valueOfEntity = f.get(entity);

                if (Optional.ofNullable(valueOfEntity).isPresent() && !valueOfEntity.equals(valueOfInstance)) {
                    f.set(this, valueOfEntity);
                }

            } catch (IllegalArgumentException ex) {
                Logger.getLogger(Model.class.getName()).log(Level.SEVERE, "[ update ]", ex);
            } catch (IllegalAccessException ex) {
                Logger.getLogger(Model.class.getName()).log(Level.SEVERE, "[ update ]", ex);
            }
        });
    }

}
