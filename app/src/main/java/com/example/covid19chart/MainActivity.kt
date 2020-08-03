package com.example.covid19chart

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private var progressBar: ProgressBar? = null
    private var ascending = true

    companion object{
        lateinit var adapters:CountryAdapter
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        progressBar = findViewById(R.id.progress_Bar)

        sv.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adapters.filter.filter(newText)
                return false
            }
        })

        btn_sequence.setOnClickListener {
            getCountry()
            swipe_refresh.isRefreshing = false
        }

        getCountry()
        initializeViews()

    }

    private fun initializeViews() {
        btn_sequence.setOnClickListener {
            sequenceWithoutInternet(ascending)
            ascending = !ascending
        }
    }

    private fun sequenceWithoutInternet(asc: Boolean){
        recyclerViewCountry.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@MainActivity)
            if (asc){
                (layoutManager as LinearLayoutManager).reverseLayout = true
                (layoutManager as LinearLayoutManager).stackFromEnd = true
                Toast.makeText(this@MainActivity, "Z - A", Toast.LENGTH_SHORT).show()
            }else{
                (layoutManager as LinearLayoutManager).reverseLayout = false
                (layoutManager as LinearLayoutManager).stackFromEnd = false
                Toast.makeText(this@MainActivity, "A - Z", Toast.LENGTH_SHORT).show()
            }
            adapter = adapters
        }
    }

    private fun getCountry(){
        val okhttp = OkHttpClient().newBuilder()
            .connectTimeout(15, TimeUnit.SECONDS) //Jika data tidak ada respon selama 15 detik
            .readTimeout(15, TimeUnit.SECONDS) //Jika data terkoneksi namun tidak ada data selama 15 detik
            .writeTimeout(15, TimeUnit.SECONDS) //Jika device mengirim data selama 15 detik
            .build()
        var retrofit = Retrofit.Builder()
            .baseUrl("https://api.covid19api.com/") //Alamat API yang dituju
            .client(okhttp) //Handler data transaksi
            .addConverterFactory(GsonConverterFactory.create()) //Convert data json
            .build() //Build data
        val api = retrofit.create(APIService::class.java)
        api.getAllNegara().enqueue(object : Callback<AllNegara>{
            override fun onFailure(call: Call<AllNegara>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Data unreachable", Toast.LENGTH_SHORT).show()
                progress_Bar.visibility = View.GONE
                //Tidak adanya koneksi atau koneksi jelek
                //Server not found (403)
            }

            override fun onResponse(call: Call<AllNegara>, response: Response<AllNegara>) {
                if (response.isSuccessful) {
                    progress_Bar.visibility = View.GONE
                    Toast.makeText(this@MainActivity, "Data success", Toast.LENGTH_SHORT).show()
                    val getDataListCorona = response.body()!!.Global
                    val formatter: NumberFormat = DecimalFormat("#,###")
                    confirmed_globe.text = formatter.format(getDataListCorona.TotalConfirmed.toDouble())
                    deaths_globe.text = formatter.format(getDataListCorona.TotalConfirmed.toDouble())
                    recovered_globe.text = formatter.format(getDataListCorona.TotalConfirmed.toDouble())
                    recyclerViewCountry.apply {
                        setHasFixedSize(true)
                        layoutManager = LinearLayoutManager(this@MainActivity)
                        adapters = CountryAdapter(response.body()!!.Countries as ArrayList<Negara>) {
                                negara -> itemClicked(negara)
                        }
                        adapter = adapters
                    }
                } else {
                    progress_Bar.visibility = View.GONE
                    Toast.makeText(this@MainActivity, "Data response unreachable", Toast.LENGTH_SHORT).show()
                    //Endpoint wrong tapi server still existed
                    //Galat data / data yang diminta sama API Service tidak sesuai dengan apa yang dikirimkan
                }
            }

        })
    }

    private fun itemClicked(negara : Negara) {
        val pindahData = Intent(this, DetailActivity::class.java)
        pindahData.putExtra(DetailActivity.EXTRA_COUNTRY, negara.Country)
        pindahData.putExtra(DetailActivity.EXTRA_LATESTUPDATE, negara.Date)
        pindahData.putExtra(DetailActivity.EXTRA_NEWCONFIRMED, negara.NewConfirmed)
        pindahData.putExtra(DetailActivity.EXTRA_NEWDEATH, negara.NewDeath)
        pindahData.putExtra(DetailActivity.EXTRA_NEWRECOVERED, negara.NewRecovered)
        pindahData.putExtra(DetailActivity.EXTRA_TOTALCONFIRMED, negara.TotalConfirmed)
        pindahData.putExtra(DetailActivity.EXTRA_TOTALDEATH, negara.TotalDeath)
        pindahData.putExtra(DetailActivity.EXTRA_TOTALRECOVERED, negara.TotalRecovered)
        pindahData.putExtra(DetailActivity.EXTRA_COUNTRYID, negara.CountryCode)
        startActivity(pindahData)
    }

    private fun errorCuy(context: Context){
        val builder = AlertDialog.Builder(context)
        with(builder){
            setTitle("Network Error!")
            setCancelable(false)
            setPositiveButton("REFRESH") { _, _ ->
                super.onRestart()
                val ripres = Intent(this@MainActivity, MainActivity::class.java)
                startActivity(ripres)
                finish()
            }
            setNegativeButton("EXIT") { _, _ ->
                finish()
            }
            create()
            show()
        }
    }


}
