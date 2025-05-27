package org.Project.model;


public class CatatanKeuangan {
    private int id;
    private int userId;
    private String judul;
    private double jumlah;
    private String kategori;
    private String tipe;
    private String tanggal;

    public CatatanKeuangan(int userId, String judul, double jumlah, String kategori, String tipe, String tanggal) {
        this.userId = userId;
        this.judul = judul;
        this.jumlah = jumlah;
        this.kategori = kategori;
        this.tipe = tipe;
        this.tanggal = tanggal;
    }

    public CatatanKeuangan(int id, int userId, String judul, double jumlah, String kategori, String tipe, String tanggal) {
        this.id = id;
        this.userId = userId;
        this.judul = judul;
        this.jumlah = jumlah;
        this.kategori = kategori;
        this.tipe = tipe;
        this.tanggal = tanggal;
    }

    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public String getJudul() {
        return judul;
    }

    public double getJumlah() {
        return jumlah;
    }

    public String getKategori() {
        return kategori;
    }

    public String getTipe() {
        return tipe;
    }

    public String getTanggal() {
        return tanggal;
    }
}
