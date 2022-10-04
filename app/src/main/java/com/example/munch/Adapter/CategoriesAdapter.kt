package com.example.munch.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

import com.example.munch.Domain.Categories
import com.example.munch.R

/*class CategoriesAdapter(private val categoriesList: List<Categories>) :
    RecyclerView.Adapter<CategoriesViewHolder>() {



    class CategoriesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val categoryImageView: ImageView = itemView.findViewById(R.id.PIzza)
        val categoryNameTv: TextView = itemView.findViewById(R.id.categoryName)

        init {
            itemView.setOnClickListener {
                listener.onItemClick(adapterPosition)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoriesViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.viewholder_categories, parent, false)
        return CategoriesViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoriesViewHolder, position: Int) {
       val categories = categoriesList[position]
        holder.categoryNameTv.text = categories.categoriesName
        holder.categoryImageView.setImageResource(categories.categoriesImage)
        /*Glide.with(context)
            .load(categories.categoriesImage)
            .into(holder.categoryImageView)*/

    }

    override fun getItemCount(): Int {
       return categoriesList.size
    }
}*/