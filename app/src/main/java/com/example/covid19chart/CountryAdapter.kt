package com.example.covid19chart

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.list_country.view.*
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList

class CountryAdapter(private var negara:ArrayList<Negara>, private val clickListener: (Negara) -> Unit):
RecyclerView.Adapter<CountryAdapter.ViewHolder>(), Filterable{

    var countryFilterList = ArrayList<Negara>()
    init {
        countryFilterList = negara
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CountryAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_country, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return countryFilterList.size
    }

    override fun onBindViewHolder(holder: CountryAdapter.ViewHolder, position: Int) {
        holder.bind(countryFilterList[position], clickListener)
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                countryFilterList = if(charSearch.isEmpty()) {
                    negara
//                    Handler jika data tidak ketemu maka mengeluarkan data default yaitu seluruh data di variable negara
                } else {
                    var resultList = ArrayList<Negara>()
                    for(row in negara) {
                        if(row.Country?.toLowerCase(Locale.ROOT)?.contains(charSearch.toLowerCase(Locale.ROOT))!!) {
                            resultList.add(row) //Menyimpan data found
                        }
                    }
                    resultList
                }
                val filterResults = FilterResults()
                filterResults.values = countryFilterList
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                countryFilterList = results?.values as ArrayList<Negara>
                notifyDataSetChanged()
            }

        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(negara: Negara, clickListener: (Negara) -> Unit) {

//            Membinding data dari model ke widget pada layout model
            val country: TextView = itemView.countryName
            val cTotalCase: TextView = itemView.country_total_case
            val cTotalRecovered: TextView = itemView.country_total_recovered
            val cTotalDeaths: TextView = itemView.country_total_deaths
            val flag: ImageView = itemView.img_flag_circle

//            Membuat format separator seribu
            var formatter : NumberFormat = DecimalFormat("#,###")

//            Menginjeksi data ke variable widget
            country.text = negara.Country
            cTotalCase.text = formatter.format(negara.TotalConfirmed?.toDouble())
            cTotalRecovered.text = formatter.format(negara.TotalRecovered?.toDouble())
            cTotalDeaths.text = formatter.format(negara.TotalDeath?.toDouble())
            Glide.with(itemView).load("https://www.countryflags.io/" + negara.CountryCode + "/flat/16.png").into(flag)

//            Menjadikan data respon untuk di click
            country.setOnClickListener { clickListener(negara) }
            cTotalCase.setOnClickListener { clickListener(negara) }
            cTotalRecovered.setOnClickListener { clickListener(negara) }
            cTotalDeaths.setOnClickListener { clickListener(negara) }
            flag.setOnClickListener { clickListener(negara) }
        }
    }


}