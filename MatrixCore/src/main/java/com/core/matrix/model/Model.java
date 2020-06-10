/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.model;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
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

                boolean isCollection = Collection.class.isAssignableFrom(f.getType());

                if (isCollection) {

                    Collection<Model> collection = (Collection<Model>) f.get(this);
                    Collection<Model> collectionEntity = (Collection<Model>) f.get(entity);

                    if (!Optional.ofNullable(collectionEntity).isPresent() || collectionEntity.isEmpty()) {
                        if (!collection.isEmpty()) {
                            collection.clear();
                        }
                        return;
                    }

                    // update object if exits into collections
                    collectionEntity.forEach(m -> {

                        Optional<Model> optModel = collection
                                .stream()
                                .filter(mm -> Optional.ofNullable(m.getId()).isPresent() && mm.getId().equals(m.getId()))
                                .findFirst();

                        if (optModel.isPresent()) {
                            optModel.get().update(m);
                        } else {
                            collection.add(m);
                        }
                    });

                    List<Model> removeEntitys = new ArrayList<>();

                    collection.forEach(m -> {
                        boolean has = collectionEntity
                                .stream()
                                .anyMatch(mm -> Optional.ofNullable(m.getId()).isPresent() && mm.getId().equals(m.getId()));
                        if (!has) {
                            removeEntitys.add(m);
                        }
                    });

                    if (!removeEntitys.isEmpty()) {
                        collection.removeAll(removeEntitys);
                    }

                } else {

                    Object valueOfInstance = f.get(this);
                    Object valueOfEntity = f.get(entity);

                    if (Optional.ofNullable(valueOfEntity).isPresent() && !valueOfEntity.equals(valueOfInstance)) {
                        f.set(this, valueOfEntity);
                    }
                }

            } catch (IllegalArgumentException ex) {
                Logger.getLogger(Model.class.getName()).log(Level.SEVERE, "[ update ]", ex);
            } catch (IllegalAccessException ex) {
                Logger.getLogger(Model.class.getName()).log(Level.SEVERE, "[ update ]", ex);
            }
        });
    }

}
