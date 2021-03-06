package com.anssoftware.otelotomasyonsistemi

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.activity_yeni_musteri_ekle.*

class musteriKaydiSil : AppCompatActivity() {

    private lateinit var db : FirebaseFirestore
    var KayitliTagler = ArrayList<String>()
    var documentIDTut = ArrayList<String>()
    var tag=""
    var tagKullaniliyorMu=false
    var TutulanDocument=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_musteri_kaydi_sil)

        db = FirebaseFirestore.getInstance()
        tagKayitliMi()
        getEtiketData()
    }


    fun tagKayitliMi() {
        db.collection("Musteriler").addSnapshotListener { snapshot, exception ->
            if (exception != null) {
                Toast.makeText(applicationContext,exception.localizedMessage.toString(), Toast.LENGTH_LONG).show()
            }else{
                if(snapshot != null){
                    if(!snapshot.isEmpty){

                        val documents = snapshot.documents

                        for(document in documents){
                            var KontrolTag = document.get("tag") as String
                            KayitliTagler.add(KontrolTag)
                            documentIDTut.add(document.id)
                        }
                    }
                }
            }

        }
    }

    fun getEtiketData(){
        var x=0
        var EsitlikVarMi=false
        db.collection("Etiketler").orderBy("tarih", Query.Direction.DESCENDING).addSnapshotListener { snapshot, exception ->
            if (exception != null) {
                Toast.makeText(applicationContext,exception.localizedMessage.toString(), Toast.LENGTH_LONG).show()
            }else{
                if(snapshot != null){
                    if(!snapshot.isEmpty){

                        val documents = snapshot.documents

                        for(document in documents){
                            val IslemTamamlandi = document.get("IslemTamamlandi") as Boolean
                            if(IslemTamamlandi==false &&x==0){
                                x++
                                tag = document.get("tag") as String
                                for(tagSayac in 1.. KayitliTagler.size ){
                                    if(tag==KayitliTagler[tagSayac-1]){
                                        EsitlikVarMi=true
                                        TutulanDocument=documentIDTut[tagSayac-1]
                                    }
                                }
                                if(!EsitlikVarMi){

                                    Toast.makeText(this, "B??YLE B??R NFC KARTI S??STEME KAYITLI DE????LD??R. L??TFEN BA??KA KART OKUTUNUZ", Toast.LENGTH_LONG).show()
                                    db.collection("Etiketler").document(document.id)
                                            .update("IslemTamamlandi", true)
                                    tagKullaniliyorMu=false
                                    kirmiziYap()
                                    musteri_bilgileri_gir.text="BA??KA KART EKLEMEK ??????N L??TFEN BASINIZ"
                                    break
                                }else{
                                    db.collection("Etiketler").document(document.id)
                                            .update("IslemTamamlandi", true)
                                    tagKullaniliyorMu=true
                                    yesilYap()
                                    break
                                }
                            }
                        }
                    }
                }
            }
        }
    }


    fun yesilYap(){
        okutmaBekleniyor.text="OKUTMA BA??ARILI"
        okutmaBekleniyor.setTextColor(Color.GREEN)
        nfc_image.setImageResource(R.drawable.nfcyesil)
    }

    fun kirmiziYap(){
        okutmaBekleniyor.text="S??STEME KAYITLI OLMAYAN KART"
        okutmaBekleniyor.setTextColor(Color.RED)
        nfc_image.setImageResource(R.drawable.nfckirmizi)
    }

    fun onClickMusteriKaydiSil(view: View) {
        if(tag==""){
            Toast.makeText(this,"L??TFEN ??NCE NFC ET??KET??N?? OKUTUNUZ",Toast.LENGTH_LONG).show()
        }else if(tagKullaniliyorMu==false){
            BaskaKartOkutunuz()
            musteri_bilgileri_gir.text="M????TER?? KAYDI S??L"
        }else{
            db.collection("Musteriler").document(TutulanDocument)
                .delete()
                .addOnSuccessListener {
                    Toast.makeText(this,"KAYIT S??LME ????LEM?? BA??ARILI",Toast.LENGTH_LONG).show()
                    val intent = Intent(applicationContext, MainActivity::class.java)
                    startActivity(intent)
                }
        }
    }

    fun BaskaKartOkutunuz() {
        getEtiketData()
        okutmaBekleniyor.text="OKUTMA BEKLEN??YOR"
        okutmaBekleniyor.setTextColor(getResources().getColor(R.color.sari))
        nfc_image.setImageResource(R.drawable.nfcimage)
    }



}