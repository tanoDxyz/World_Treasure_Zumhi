package com.geocache.zumhi.worldtreasure

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*


public class gridadapter(): BaseAdapter() {
    /* Use it like below
    val images = intArrayOf(R.drawable.locationsearch,R.drawable.compas,R.drawable.tracklocation,R.drawable.geotour)
    val names = arrayOf("Location","Geocache","Trackable","GeoTour")
    val examples = arrayOf("3456 or Seatle","GCK2b","EX: TB7VEQH","EX: GT29")
    val adapter = gridadapter(this,images,examples,names)
    gridviewsearch.adapter = adapter
     */
    var images: IntArray?=null
    var context:Context?=null
    var examples:Array<String>?=null
    var searchTypes:Array<String>?= null
    constructor(context: Context,images:IntArray,examples:Array<String>,searchTypes:Array<String> ): this() {
        this.images = images
        this.context = context
        this.examples = examples
        this.searchTypes = searchTypes
    }
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = LayoutInflater.from(parent!!.context.applicationContext).inflate(R.layout.searchgriditem,null)
        val imageView = view.findViewById<ImageView>(R.id.ivsearchicon)
        val searchType = view.findViewById<TextView>(R.id.tvsearchname)
        val example = view.findViewById<TextView>(R.id.examplesearch)
        view.setLayoutParams(AbsListView.LayoutParams(GridView.AUTO_FIT, 400))
        imageView.setImageResource(images!![position])
        searchType.setText(searchTypes!![position])
        example.setText("EX:${examples!![position]}")
        return view
    }

    override fun getItem(position: Int): Any? {
        return null
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getCount(): Int {
      return images!!.size
    }
}