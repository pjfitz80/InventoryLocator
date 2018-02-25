package com.example.patrick.myinventorylocator.utility;

import com.example.patrick.myinventorylocator.model.Vehicle;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Patrick on 2/9/2018.
 */

public class MyXMLParser {

    private HashMap<String, Vehicle> mVehicleHashMap = new HashMap<>();
    private ArrayList<Vehicle> mVehicleList = new ArrayList<>();
    private Vehicle tempVehicle;
    private String tempStockNumber;
    private String mText;



    public HashMap<String, Vehicle> getVehicleHashMap() {
        return mVehicleHashMap;
    }

    public HashMap<String, Vehicle> parse(InputStream input) {
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser parser = factory.newPullParser();

            DecimalFormat df = new DecimalFormat();
            df.setMaximumFractionDigits(2);

            parser.setInput(input, null);

            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                String tagname = parser.getName();
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        if (tagname.equalsIgnoreCase("auction_row")) {
                            // create a new instance of vehicle
                            tempVehicle = new Vehicle();
                        }
                        break;

                    case XmlPullParser.TEXT:
                        mText = parser.getText();
                        break;

                    case XmlPullParser.END_TAG:
                        if (tagname.equalsIgnoreCase("auction_row")) {
                            mVehicleHashMap.put(tempStockNumber, tempVehicle);
                            //mVehicleList.add(tempVehicle);
                            //mVehiclesHashMap.put(tempStockNumber, tempVehicle);
                        }else if (tagname.equalsIgnoreCase("run_order_no")) {
                            tempVehicle.setRunPosition(Integer.parseInt(mText));
                        }else if (tagname.equalsIgnoreCase("stock_num")) {
                            tempVehicle.setStockNumber(mText);
                            tempStockNumber = mText;
                        }else if (tagname.equalsIgnoreCase("year")) {
                            tempVehicle.setYear(Integer.parseInt(mText));
                        }else if (tagname.equalsIgnoreCase("make")) {
                            tempVehicle.setMake(mText);
                        }else if (tagname.equalsIgnoreCase("model")) {
                            tempVehicle.setModel(mText);
                        }else if (tagname.equalsIgnoreCase("trim")) {
                            tempVehicle.setTrim(mText);
                        }
                        break;
                    default:
                        break;
                }
                eventType = parser.next();
            }
        } catch (XmlPullParserException e) {e.printStackTrace();}
        catch (IOException e) {e.printStackTrace();}

        return mVehicleHashMap;
    }


}
