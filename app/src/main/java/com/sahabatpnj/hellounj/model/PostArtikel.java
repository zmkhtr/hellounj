package com.sahabatpnj.hellounj.model;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class PostArtikel extends UserDetail {
    private String judul, gambar, isi, waktu, author, kategori, id;

    public PostArtikel(String judul, String gambar, String isi, String waktu, String author, String kategori, String id) {
        this.judul = judul;
        this.gambar = gambar;
        this.isi = isi;
        this.waktu = waktu;
        this.author = author;
        this.kategori = kategori;
        this.id = id;
    }

    public PostArtikel() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getJudul() {
        return judul;
    }

    public String getGambar() {
        return gambar;
    }

    public String getIsi() {
        return isi;
    }

    public String getWaktu() {
        return waktu;
    }

    public String getAuthor() {
        return author;
    }

    public String getKategori() {
        return kategori;
    }

    public void setJudul(String judul) {
        this.judul = judul;
    }

    public void setGambar(String gambar) {
        this.gambar = gambar;
    }

    public void setIsi(String isi) {
        this.isi = isi;
    }

    public void setWaktu(String waktu) {
        this.waktu = waktu;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setKategori(String kategori) {
        this.kategori = kategori;
    }

    /*  public void setJudul(String judul, String mJudul) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference mRef = database.getReference("artikel").child(mJudul).child("email");

        mRef.setValue(judul);
        this.judul = judul;
    }

    public void setGambar(String gambar, String judul) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference mRef = database.getReference("artikel").child(judul).child("gambarUrl");

        mRef.setValue(gambar);
        this.gambar = gambar;
    }

    public void setIsi(String isi, String judul) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference mRef = database.getReference("artikel").child(judul).child("isi");

        mRef.setValue(isi);
        this.isi = isi;
    }

    public void setWaktu(String waktu, String judul) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference mRef = database.getReference("artikel").child(judul).child("waktu");

        mRef.setValue(waktu);
        this.waktu = waktu;
    }

    public void setAuthor(String author, String judul) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference mRef = database.getReference("artikel").child(judul).child("author");

        mRef.setValue(author);
        this.author = author;
    }

    public void setKategori(String kategori, String judul) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference mRef = database.getReference("artikel").child(judul).child("kategori");

        mRef.setValue(kategori);
        this.kategori = kategori;
    }*/
}
