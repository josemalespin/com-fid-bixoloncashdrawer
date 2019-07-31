/**
 */
package com.fid;

import android.content.Context;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;
import org.apache.cordova.PluginResult.Status;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

import android.util.Log;

import java.util.Date;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class FIDBixolon extends CordovaPlugin {
  private static final String TAG = "FIDBixolon";
  private static BixolonPrinter bxlPrinter = null;
  private static BixolonEntry bEntry = null;

  public static Context mContext;

  public void initialize(CordovaInterface cordova, CordovaWebView webView) {
    super.initialize(cordova, webView);
    mContext = cordova.getActivity().getApplicationContext();
    Log.d(TAG, "Initializing FIDBixolon");
  }

  public boolean execute(String action, JSONArray args, final CallbackContext callbackContext) throws JSONException {

    if (action.equals("print")) {
      //Se obtiene el jSON
      String phrase = args.getString(0);
      Gson gson = new GsonBuilder().create();
      bEntry = gson.fromJson(phrase, BixolonEntry.class);
      bxlPrinter = new BixolonPrinter(mContext);

      //Variables
      //Si algo sale mal aqui se debe de levantar una alerta
      String printerName = bEntry.getPrinterName();
      String printerAddress = bEntry.getPrinterAddress();
      int printerType = bEntry.getPrinterType();
      String dataPrint = bEntry.getData();
      boolean openDrawer = bEntry.getOpenDrawer();
      String cashierName = bEntry.getCashierName();
      String operationTime = bEntry.getOperationTime();
      String reprint = bEntry.getReprint();

      if (dataPrint == null || dataPrint == "") {
        final PluginResult result = new PluginResult(PluginResult.Status.OK, "No hay informacion para imprimir");
        callbackContext.sendPluginResult(result);
        return true;
      }

      //La conexion a la impresora tiene que correr en un hilo
      new Thread(new Runnable() {
        public void run() {

          //El encabezado
          String izquierda = EscapeSequence.getString(4);
          String centro = EscapeSequence.getString(5);
          String derecha = EscapeSequence.getString(6);
          String bold = EscapeSequence.getString(7);
          String nobold = EscapeSequence.getString(8);
          String underline = EscapeSequence.getString(9);
          String nounderline = EscapeSequence.getString(10);

          String header = centro + bold + underline + "CREDEX\n" + nounderline +
            centro + "FID, S.A.\n" + nobold +
            centro + cashierName + "\n" +
            centro + operationTime + "\n\n" + izquierda;

          String footer = centro + reprint + "\n\n" +
            centro + "HE REVISADO LOS DATOS AQUÍ\nCONTENIDOS Y ESTÁN CORRECTOS,\n" +
            centro + "Este recibo no necesita sello \nni firma del cajero.\n\n" +
            centro + "Para reclamos llame al: +(505)2264-7484\n\n\n\n";

          try {
            if (bxlPrinter.printerOpen(printerType, printerName, printerAddress, false)) {
              //Thread.sleep(100);
              if (bxlPrinter.printText(header + dataPrint + footer, 1, 1, 1)) {
                //Thread.sleep(100);
                if (openDrawer == true) {
                  bxlPrinter.cashDrawerOpen();
                  Thread.sleep(100);
                  bxlPrinter.drawerOpen();
                  Thread.sleep(100);
                  bxlPrinter.cashDrawerClose();
                }
                Thread.sleep(100);
                bxlPrinter.printerClose();
                //Exito
                final PluginResult result = new PluginResult(PluginResult.Status.OK, "Impresión exitosa");
                callbackContext.sendPluginResult(result);
              } else {
                //Fallo en printText
                final PluginResult result = new PluginResult(PluginResult.Status.OK, "No se pudo imprimir. Fallo de papel?");
                callbackContext.sendPluginResult(result);
              }
            } else {
              //Fallo el printerOpen
              final PluginResult result = new PluginResult(PluginResult.Status.OK, "No se pudo conectar a la impresora");
              callbackContext.sendPluginResult(result);
            }
          } catch (InterruptedException e) {
            e.printStackTrace();
            //Fallo
            final PluginResult result = new PluginResult(PluginResult.Status.OK, "Error: " + e.getMessage());
            callbackContext.sendPluginResult(result);
          }
        }
      }).start();
    } else if (action.equals("openDrawer")) {

      //Se obtiene el jSON
      String phrase = args.getString(0);
      Gson gson = new GsonBuilder().create();
      bEntry = gson.fromJson(phrase, BixolonEntry.class);
      bxlPrinter = new BixolonPrinter(mContext);

      //Variables
      //Si algo sale mal aqui se debe de levantar una alerta
      String printerName = bEntry.getPrinterName();
      String printerAddress = bEntry.getPrinterAddress();
      int printerType = bEntry.getPrinterType();

      //La conexion a la impresora tiene que correr en un hilo
      new Thread(new Runnable() {
        public void run() {
          try {
            if (bxlPrinter.printerOpen(printerType, printerName, printerAddress, false)) {
              bxlPrinter.cashDrawerOpen();
              Thread.sleep(100);
              bxlPrinter.drawerOpen();
              Thread.sleep(100);
              bxlPrinter.cashDrawerClose();
              Thread.sleep(100);
              bxlPrinter.printerClose();
              //Exito
              final PluginResult result = new PluginResult(PluginResult.Status.OK, "Apertura exitosa");
              callbackContext.sendPluginResult(result);
            } else {
              //Fallo el printerOpen
              final PluginResult result = new PluginResult(PluginResult.Status.OK, "No se pudo conectar a la impresora");
              callbackContext.sendPluginResult(result);
            }
          } catch (InterruptedException e) {
            e.printStackTrace();
            //Fallo
            final PluginResult result = new PluginResult(PluginResult.Status.OK, "Error: " + e.getMessage());
            callbackContext.sendPluginResult(result);
          }
        }
      }).start();
    }

    return true;
  }

}