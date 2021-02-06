package com.uveous.loopfoonpay;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;

import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class UtilityFunction {


 /*   public static String convetMMddYYYYToddMMYYYY(String date)
    {
        String split="";
        try {
            String[] Date=date.split("-");
          //  split=Date[0]
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }
*/
    public static String getSplitedDate(String date) {
        if (date != null) {
            String[] split = date.split("T");
            return changeDateFormat(split[0]);
        } else {
            return "No date";
        }
    }

    public static String getSplitedDateTime(String date)
    {
        String splitedDate=getSplitedDate(date);
        String splitedTime=getSplitedTime(date);
        String[]  mm_dd=splitedDate.split("-");
         String dt= mm_dd[1]+"/"+mm_dd[0];

        return dt+" "+splitedTime;
    }

    public static String changeDateFormat(String date) {
        String[] splitDate = date.split("-");
        return splitDate[2] + "-" + splitDate[1] + "-" + splitDate[0];
    }


    public static String getSplitedTime(String date) {
        if (date != null) {
            String[] split = date.split("T");
            return getTime(split[1]);
        } else {
            return "No date";
        }
    }

    public static String getTime(String dateTime)
    {
        String[] splitDate = dateTime.split(":");
        return splitDate[0] + ":" + splitDate[1];
    }

    public static String ddMMyyyyToMMddyyyy(String date)
    {
        String[] splitedDate=date.split("-");
//splitedDate[1]+"-"+splitedDate[0]+"-"+splitedDate[2]
        return date;
    }

    public static int getDaysDifference(Date fromDate,Date toDate)
    {
        if(fromDate==null||toDate==null)
            return 0;

        return (int)( (toDate.getTime() - fromDate.getTime()) / (1000 * 60 * 60 * 24));
    }

    public static Date convertStringToDate(String datestring)
    {
        Date d=null;
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
           d= dateFormat.parse(datestring);
        }catch (Exception e)
        {

        }
        return d;

    }


    public static String changeDateTime(String date)
    {
        if (date != null) {
            String[] split = date.split("T");
            String formated=split[0]+" "+getTime(split[1]);
           /* SimpleDateFormat simpleDateFormat=new SimpleDateFormat(formated);
            simpleDateFormat.format()*/
            Log.d("Date Time",formated);
            return parseDateToddMMMyyyy(formated);

        } else {
            return "No date";
        }
    }

    public static String parseDateToddMMMyyyy(String DateFormat) {
        //SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy hh:mm a");
        String inputPattern = "yyyy-MM-dd HH:mm";
        String outputPattern = "MMM dd,yyyy HH:mm";


        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern,Locale.getDefault());
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern,Locale.getDefault());

        Date date = null;
        String str = null;

        try {
            date = inputFormat.parse(DateFormat);
            str = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }


    public static boolean emailValidator(String email) {
        Pattern pattern;
        Matcher matcher;
        final String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public static String getCalculatedDate(String dateFormat, int days) {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat s = new SimpleDateFormat(dateFormat, Locale.getDefault());
        cal.add(Calendar.DAY_OF_YEAR, days);
        return s.format(new Date(cal.getTimeInMillis()));
    }


    public static void hideKeybord(Context context, View view) {
        InputMethodManager inputManager = (InputMethodManager) context.getSystemService(context.INPUT_METHOD_SERVICE);
          view.requestFocus();
        inputManager.hideSoftInputFromWindow(view.getWindowToken(),
                InputMethodManager.RESULT_UNCHANGED_SHOWN);
    }

  /*  public static void sessionExpired(final Context context, String exception)
    {
       if (exception.equals("com.android.volley.AuthFailureError") || exception.equals("401"))
       {
           final Dialog dialog=new Dialog(context);
           dialog.setContentView(R.layout.inflate_session_expired);
           TextView tv_ok=dialog.findViewById(R.id.tv_ok_logged_out);
           TextView tv_cancel=dialog.findViewById(R.id.tv_cancel_logged_out);
           dialog.setCancelable(false);
           dialog.show();

          tv_ok.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  context.startActivity(new Intent(context, LoginActivity.class));
              }
          });

          tv_cancel.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  dialog.dismiss();
              }
          });



       }else {
           Toast.makeText(context, "Something went wrong.", Toast.LENGTH_SHORT).show();
       }

    }*/


    public static boolean appInstalledOrNot(Context context, String uri)
    {
        PackageManager pm = context.getPackageManager();
        try
        {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            return true;
        }
        catch(PackageManager.NameNotFoundException e)
        {
        }
        return false;
    }


    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    public static String removeLastChar(String str) {
        return str.substring(0, str.length() - 1);
    }

    public static String calculateDistance(double startLat, double startLng, double endLat, double endLng, String provider)
    {
        Location startPoint=new Location(provider);
        startPoint.setLatitude(startLat);
        startPoint.setLongitude(startLng);
      /*  startPoint.setLatitude(17.372102);
        startPoint.setLongitude(78.484196);*/

        Location endPoint=new Location(provider);
      /*  endPoint.setLatitude(17.375775);
        endPoint.setLongitude(78.469218);*/
        endPoint.setLatitude(endLat);
        endPoint.setLongitude(endLng);

        double distance=startPoint.distanceTo(endPoint);
        Log.d("Distance",distance+"");

        return String.valueOf(distance);
    }

    public static String getAddressFromLatlng(Context context, LatLng latLng)
    {
        String address="";
        Geocoder geocoder=new Geocoder(context);
        try {
            List<Address> addresses=geocoder.getFromLocation(latLng.latitude,latLng.longitude,1);
           address= addresses.get(0).getAddressLine(0);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
     return address;
    }


    public static final boolean isNetworkAvailable(Context context)
    {
        if(context == null)
        {
            return false;
        }
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
        try
        {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            if(activeNetworkInfo != null && activeNetworkInfo.isConnected())
            {

                return true;
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return false;
    }

    public static void alertGenerate(Context context,String alertTitle,String alertMesseage) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(alertTitle);
        builder.setMessage(alertMesseage);
        builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

            }
        }).setPositiveButton("Enable Gps", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
               // startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();

    }

  /*  public static void openThanksDialog(Context context,String message)
    {
        final Dialog dialog=new Dialog(context);
        dialog.setContentView(R.layout.inflate_thanks_home_activity);
        TextView tv_ok=dialog.findViewById(R.id.tv_ok_thanks);
        TextView tv_message=dialog.findViewById(R.id.tv_message_thanks);
        tv_message.setText(message);
        dialog.show();
        tv_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }
*/

    public static boolean gpsstatusCheck(Context context) {
        final LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);


        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
          return  buildAlertMessageNoGps(context, manager);

        }else {
            return true;
        }


    }

    public static String filterAddress(Context context,String lat,String lng,String filterType)
    {
        String city="";
        String country="";
        String state="";
        String locality="";
        String postal_code="";
        Geocoder geocoder=new Geocoder(context);
        try {
            List<Address> addresses= geocoder.getFromLocation(Double.parseDouble(lat),Double.parseDouble(lng),1);
            city =addresses.get(0).getSubAdminArea();//.getAdminArea();
            country=addresses.get(0).getCountryName();
            state=addresses.get(0).getAdminArea();
            locality=addresses.get(0).getLocality();
            postal_code=addresses.get(0).getPostalCode();


            Log.d("Locality",addresses.get(0).getLocality());
            Log.d("SubLocality",addresses.get(0).getSubLocality());

        } catch (Exception e) {
            e.printStackTrace();
        }
        if (filterType.equals("city")) {
            return city;
        }else if (filterType.equals("country"))
        {
            return country;
        }else if (filterType.equals("state"))
        {
            return state;
        }else if (filterType.equals("pincode"))
        {
           return postal_code;
        }else
          {
            return locality;
        }

    }

    public static boolean buildAlertMessageNoGps(final Context context, final LocationManager manager) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        context.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));

                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
       return manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }
    public static void logOutAlert(final Activity context)
    {
        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        builder.setTitle("Alert").setMessage("Are you sure you want to Logout from app");
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
               // Constants.ACTIVITY_NAME=Constants.HOME_ACTIVITY;
               /* UrlClass.BASE_URL="http://109.228.49.117:8095/";
                Constants.loginPOJO=null;
*/
               // NetworkWorkOrder.getInstance()=null;

                context.startActivity(new Intent(context, LoginActivity.class));//.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));

                context.finishAffinity();
            }
        });

        AlertDialog dialog=builder.create();
        dialog.show();
    }


    public LatLng convertAddressToLatLng(Context context, String strAddress) {

        Geocoder coder = new Geocoder(context);
        List<Address> address;
        LatLng p1 = null;

        try {
            // May throw an IOException
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }

            Address location = address.get(0);
            p1 = new LatLng(location.getLatitude(), location.getLongitude() );

        } catch (IOException ex) {

            ex.printStackTrace();
        }

        return p1;
    }

    public static void checkEditTextSetValue(String value, EditText editText)
    {
      try {
          if (value==null)
          {
              editText.setText("");

          }else if (value.isEmpty()) {
              editText.setText("");
          }
          else if (value.equals("null"))
          {
              editText.setText("");
          } else {
              editText.setText(value);
          }
      }catch (Exception e)
      {
         e.printStackTrace();
         editText.setText("");
      }
    }
   public static void checkAutoCompleteTextSetValue(String value, AutoCompleteTextView editText)
    {
        if (value==null)
        {
            editText.setText("");

        }else if (value.isEmpty()) {
            editText.setText("");
        }
        else if (value.equals("null"))
        {
            editText.setText("");
        } else {
            editText.setText(value);
        }
    }
   /* public static void checkTextInputEditTextSetValue(String value, TextInputEditText editText)
    {
        if (value==null)
        {
            editText.setText("");
        }else if (value.isEmpty()) {
            editText.setText("");
        }
        else if (value.equals("null"))
        {
            editText.setText("");
        } else {
            editText.setText(value);
        }
    }
    public static void checkTextViewSetValue(String value,TextView textView)
    {
        if (value==null)
        {
            textView.setText("");
        }else if (value.isEmpty()) {
            textView.setText("");
        }
        else if (value.equals("null"))
        {
            textView.setText("");
        } else {
            textView.setText(value);
        }
    }*/

    public static void checkUpdateAssetValue()
    {

    }
/*

    public static void statusCheck(final Context context) {
        final LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            final android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(context);
            builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(final DialogInterface dialog, final int id) {
                            context.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(final DialogInterface dialog, final int id) {
                            dialog.cancel();
                        }
                    });
            final android.support.v7.app.AlertDialog alert = builder.create();
            alert.show();
        }
    }
*/


}


