package com.core.matrix.workflow.model;


import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
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
public class GroupActiviti {

    @Id
    @Column(name = "ID_")
    private String id;
    
    @Column(name = "REV_")
    private String rev;
    
    @Column(name = "NAME_")
    private String name;
    
    @Column(name = "TYPE_")
    private String type;
    
    @OneToMany( cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    @JoinColumn(name = "group_id_")
    private List<AbilityActiviti> abilitys;
    
   
    
    
            
}
