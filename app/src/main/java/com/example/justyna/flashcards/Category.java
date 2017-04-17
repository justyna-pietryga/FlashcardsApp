package com.example.justyna.flashcards;

import java.util.List;

public class Category {
    private int id=0;
    private String name;
   // private int idPosition=0;

   /* public Category(int id, String name, int idPosition) {
        this.id = id;
        this.name = name;
        this.idPosition = idPosition;

    } */

    /*public int getIdPosition() {

        return idPosition;
    }

    public void setIdPosition(int idPosition) {
        this.idPosition = idPosition;
    }  */

    private List<Vocabulary> vocabularies;

    public Category(int id, String name) {
        this.id = id;
        this.name = name;
    }

   /* public Category(String name, int idPosition) {
        this.name = name;
        this.idPosition=idPosition;

    } */

    public Category(String name) {
        this.name = name;

    }

    public Category() {
    }

    @Override
    public String toString() {

        //return "Category [id="+id+", name="+name+"]";
        return name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
