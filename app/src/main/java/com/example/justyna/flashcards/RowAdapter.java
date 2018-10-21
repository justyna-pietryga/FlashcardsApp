package com.example.justyna.flashcards;


import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class RowAdapter extends BaseAdapter  {

    private Context context;
    private Activity activity;
    private ArrayList data;
    private static LayoutInflater inflater=null;
    public Resources res;
    private RowBean tempValues=null;
    int i=0;

    public static final String TAG = "debuggingVocabulary";

    public RowAdapter(Activity activity, ArrayList data, Resources res, Context context) {
        this.activity=activity;
        this.data=data;
        this.res=res;
        this.context=context;

        inflater = ( LayoutInflater )context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE); /////////
    }


    @Override
    public int getCount() {
        if(data.size()<=0)
            return 1;
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }



    public static class ViewHolder{

        public TextView text1;
        public TextView text2;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        ViewHolder holder;

        if(convertView==null){

            /****** Inflate tabitem.xml file for each row ( Defined below ) *******/
            vi = inflater.inflate(R.layout.simple_vocabulary_row, null);

            /****** View Holder Object to contain tabitem.xml file elements ******/

            holder = new ViewHolder();
            holder.text1 = (TextView) vi.findViewById(R.id.firstWord_textView);
            holder.text2=(TextView)vi.findViewById(R.id.secondWord_textView);
            /************  Set holder with LayoutInflater ************/
            vi.setTag( holder );
        }
        else
            holder=(ViewHolder)vi.getTag();

        if(data.size()<=0)
        {
            Log.d(TAG, "no data");
            //holder.text1.setText("No data");

        }
        else
        {
            /***** Get each Model object from Arraylist ********/
            tempValues=null;
            tempValues = (RowBean) data.get( position );

            /************  Set Model values in Holder elements ***********/

            holder.text1.setText( "  "+tempValues.getFirstWord() );
            holder.text2.setText("  "+ tempValues.getSecondWord() );

            /******** Set Item Click Listner for LayoutInflater for each row *******/

        }
        return vi;
    }

}
