/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.workflow.model;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
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

    String getId();

    default void update(T entity) {
        List<Field> fields = Arrays.asList(entity.getClass().getDeclaredFields());

        fields.forEach(f -> {

            Type type = f.getGenericType();

            try {

                boolean isCollection = Collection.class.isAssignableFrom(f.getType());                
               
                
                //boolean isModel = Model.class.isAssignableFrom(((ParameterizedType )f.getGenericType()).getRawType().getClass());

                if (isCollection) {
                    //TODO: Create flow for implmentation
                    
//                    ParameterizedType collectionType = (ParameterizedType) f.getGenericType();
//                    Class<?> stringListClass = (Class<?>) collectionType.getActualTypeArguments()[0];
//                    System.out.println(stringListClass);
//                    
//                    boolean isModel = Model.class.isAssignableFrom(stringListClass);
//                    
//                    
//                    Collection<Model> collection = (Collection<Model>)f.get(this);
//                    Collection<Model> collectionEntity = (Collection<Model>)f.get(entity);
//                    
//                    collectionEntity.forEach(m->{
//                        
//                        Optional<Model> optModel = collection.stream().filter(mm-> mm.getId().equals(m.getId())).findFirst();
//                        
//                        if(optModel.isPresent()){
//                            optModel.get().update(m);
//                        }else{
//                            collection.add(m);
//                        }
//                    });
//                    
//                    collection.forEach(m->{                    
//                        Optional<Model> optModel = collectionEntity.stream().filter(mm-> mm.getId().equals(m.getId())).findFirst();                        
//                        if(!optModel.isPresent()){
//                           collection.remove(m);
//                        }
//                    });
                    
                    
                    
                    
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
