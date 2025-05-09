package org.Project;

import javafx.beans.property.*;

public class Catatan2 {
    private final IntegerProperty id;
    private final StringProperty judul;
    private final StringProperty kategori;

    public Catatan2(int id, String judul, String kategori) {
        this.id = new SimpleIntegerProperty(id);
        this.judul = new SimpleStringProperty(judul);
        this.kategori = new SimpleStringProperty(kategori);
    }

    public int getId() {
        return id.get();
    }

    public IntegerProperty idProperty() {
        return id;
    }

    public String getJudul() {
        return judul.get();
    }

    public StringProperty judulProperty() {
        return judul;
    }

    public String getKategori() {
        return kategori.get();
    }

    public StringProperty kategoriProperty() {
        return kategori;
    }
}
