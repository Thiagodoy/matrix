/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import lombok.Data;
import org.apache.tomcat.jni.Address;

/**
 *
 * @author thiag
 */
public class TesteGerais {

    public static void main(String[] args) {

//        try {
//
//            TesteSerialization serialization = new TesteSerialization();
//            serialization.setNome("Thiago");
//            serialization.setSobreNome("Godoy");
//
//            FileOutputStream fout = new FileOutputStream("Teste.bean");
//            ObjectOutputStream oos = new ObjectOutputStream(fout);
//            oos.writeObject(serialization);
//            oos.close();
//            System.out.println("Done");
//
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }

        TesteSerialization address;

        try {

            FileInputStream fin = new FileInputStream("Teste.bean");
            ObjectInputStream ois = new ObjectInputStream(fin);
            address = (TesteSerialization) ois.readObject();
            ois.close();

            System.out.println(address);

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

}

@Data
class TesteSerialization implements Serializable {

    private static final long serialVersionUID = 467311467422319673L;

    private String nome;
    private String sobreNome;
    private Long idade;

    public String toString() {
        return nome + " - " + sobreNome + " - " + idade;
    }

}
