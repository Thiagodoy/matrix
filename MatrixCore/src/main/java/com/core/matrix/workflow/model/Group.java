package com.core.matrix.workflow.model;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;




/**
 *
 * @author Thiago H. Godoy <thiagodoy@hotmail.com>
 */
@Entity
@Table(schema = "activiti",name="act_id_group")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Group {

    @Id
    @Column(name = "ID_")
    private String id;
    
    @Column(name = "REV_")
    private String rev;
    
    @Column(name = "NAME_")
    private String name;
    
    @Column(name = "TYPE_")
    private String type;
    
   
            
}
