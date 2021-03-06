package edi.md.mobile;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

//import com.example.igor.terminalmobile.Settings.Assortment;

//import com.RT_Printer.BluetoothPrinter.BLUETOOTH.BluetoothPrintDriver;

import com.rt.printerlibrary.printer.RTPrinter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

import edi.md.mobile.NetworkUtils.RetrofitResults.Assortment;
import edi.md.mobile.app.utils.BaseEnum;

public class Variables extends Application {

    public static Variables instance = null;
    private RTPrinter rtPrinter;

    private int widthPrinterPrint;

    @BaseEnum.CmdType
    private int currentCmdType = BaseEnum.CMD_PIN;//默认为针打

    @BaseEnum.ConnectType
    private int currentConnectType = BaseEnum.NONE;//默认未连接

    private Boolean userAuthentificate = false;
    private Boolean DownloadASLVariable = false;
    private Boolean Recreate = false;
    public static final String mPOSPrinters = "POS printers";
    public static final String mLablePrinters = "Lable printers";
    public static final String[] mRongtaModelList = { "Not selected","RPP-200 57mm", "RPP-300 80mm"};
    public static final String[] mZebraModelList = {"Not selected", "Zebra lable"};
//    BluetoothPrintDriver mBTDevice;


    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static Variables getInstance(){
        return instance;
    }


    public RTPrinter getRtPrinter() {
        return rtPrinter;
    }

    public void setRtPrinter(RTPrinter rtPrinter) {
        this.rtPrinter = rtPrinter;
    }

    @BaseEnum.CmdType
    public int getCurrentCmdType() {
        return currentCmdType;
    }

    public void setCurrentCmdType(@BaseEnum.CmdType int currentCmdType) {
        this.currentCmdType = currentCmdType;
    }

    @BaseEnum.ConnectType
    public int getCurrentConnectType() {
        return currentConnectType;
    }

    public void setCurrentConnectType(@BaseEnum.ConnectType int currentConnectType) {
        this.currentConnectType = currentConnectType;
    }

    public int getWidthPrinterPrint() {
        return widthPrinterPrint;
    }

    public void setWidthPrinterPrint(int widthPrinterPrint) {
        this.widthPrinterPrint = widthPrinterPrint;
    }

    private HashMap<String, Assortment> AssortimentID =new HashMap<>();

    //методы настройки и получение переменной  если пользователь авторизировался или нет
    public Boolean getUserAuthentificate() {
        return userAuthentificate;
    }
    public void setUserAuthentificate(Boolean NewData) {
        this.userAuthentificate = NewData;
    }

    //методы настройки и получение переменной если было пересоздано окно(в случае смены языка)
    public Boolean getIsRecreate() {
        return Recreate;
    }
    public void setRecreate(Boolean NewData) {
        this.Recreate = NewData;
    }

    //методы настройки и получение переменной  если скачан ассортимент или нет
    public void setDownloadASLVariable(Boolean NewData) {
        this.DownloadASLVariable = NewData;
    }
    public Boolean getDownloadASLVariable() {
        return DownloadASLVariable;
    }

    //добавляет ассортимент по айди в массив для сохранение в дальнейшем в работе
    public void add_AssortimentID(String ID,Assortment asl) {
        AssortimentID.put(ID,asl);
    }

    //возвращает найденые товары по запросу
    public ArrayList<HashMap<String, Object>> get_Search_Assortment(String search) {
        ArrayList<HashMap<String, Object>> asl_list = new ArrayList<>();
        if(AssortimentID!=null)
            for (String key: AssortimentID.keySet()) {
                Assortment sal = AssortimentID.get(key);
                assert sal != null;
                String code_asl = sal.getCode();
                String barcode_asl =sal.getBarCode();
                String marking_asl =sal.getMarking();

                if(marking_asl == null)
                    marking_asl = "null";
                if (barcode_asl==null)
                    barcode_asl="null";
                if(code_asl==null)
                    code_asl="null";
                String asl_name= sal.getName();

                boolean is_folder = sal.getIsFolder();
                if(marking_asl.toUpperCase().contains(search.toUpperCase()) || barcode_asl.toUpperCase().contains(search.toUpperCase()) || code_asl.toUpperCase().contains(search.toUpperCase()) || asl_name.toUpperCase().contains(search.toUpperCase())) {
                    HashMap<String, Object> asl_ = new HashMap<>();

                    String uid_asl = sal.getAssortimentID();
                    String parent = sal.getAssortimentParentID();
                    if (!is_folder) {
                        String price =  sal.getPrice();
                        String allow_integer = sal.getAllowNonIntegerSale();
                        String incomePrice=sal.getIncomePrice();
                        String unitary=sal.getUnit();
                        String marking = sal.getMarking();
                        String finalUnitPrice=sal.getUnitPrice();
                        String UnitInPackage=sal.getUnitInPackage();

                        Double priceunit = Double.valueOf(price);
                        price =String.format("%.2f",priceunit);
                        String Asl_Price =getResources().getString(R.string.txt_list_asl_view_price)+ price + getResources().getString(R.string.txt_list_asl_view_valuta);

                        asl_.put("Folder_is", false);
                        asl_.put("icon", R.drawable.assortiment_item);
                        asl_.put("Name", asl_name);
                        asl_.put("ID", uid_asl);
                        asl_.put("Code", code_asl);
                        asl_.put("BarCode", barcode_asl);
                        asl_.put("Marking",marking);
                        asl_.put("Bar_code",getResources().getString(R.string.txt_list_asl_view_barcode) + barcode_asl);
                        asl_.put("AllowNonIntegerSale", allow_integer);
                        asl_.put("Price", price);
                        asl_.put("PriceWithText", Asl_Price);
                        asl_.put("IncomePrice", incomePrice);
                        asl_.put("Unit",  " /"+  unitary);
                        asl_.put("UnitPrice", finalUnitPrice);
                        asl_.put("UnitInPackage", UnitInPackage);
                        asl_list.add(asl_);
                    } else {
                        asl_.put("Folder_is", true);
                        asl_.put("Parent_ID", parent);
                        asl_.put("Name", asl_name);
                        asl_.put("ID", uid_asl);
                        asl_.put("Bar_code","");
                        asl_.put("icon", R.drawable.folder_open_black_48dp);
                        asl_list.add(asl_);
                    }
                }
            }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            asl_list.sort(new Comparator<HashMap<String, Object>>() {
                @Override
                public int compare(HashMap<String, Object> o1, HashMap<String, Object> o2) {
                    return String.valueOf(o2.get("Folder_is")).compareTo(String.valueOf(o1.get("Folder_is")));
                }

            });
        }
        return asl_list;
    }

    //возвращает все товары которые которые находится в папке с указаном айди
    public ArrayList<HashMap<String, Object>> get_AssortimentFromParent(String ID_Parinte) {
        ArrayList<HashMap<String, Object>> asl_list = new ArrayList<>();
        if(AssortimentID!=null) {
            //order_sort(AssortimentID);
            for (String key : AssortimentID.keySet()) {
                Assortment sal = AssortimentID.get(key);
                assert sal != null;
                String parent = sal.getAssortimentParentID();
                boolean is_folder = sal.getIsFolder();
                if (parent.equals(ID_Parinte)) {
                    HashMap<String, Object> asl_ = new HashMap<>();
                    String asl_name = sal.getName();
                    String uid_asl = sal.getAssortimentID();
                    if (!is_folder) {
                        String price = sal.getPrice();
                        String code_asl = sal.getCode();
                        String barcode_asl = sal.getBarCode();
                        String allow_integer = sal.getAllowNonIntegerSale();
                        String incomePrice = sal.getIncomePrice();
                        String marking = sal.getMarking();
                        String unitary = sal.getUnit();
                        String finalUnitPrice = sal.getUnitPrice();
                        String UnitInPackage = sal.getUnitInPackage();

                        double priceunit = Double.parseDouble(price);
                        price = String.format("%.2f", priceunit);

                        asl_.put("Folder_is", false);
                        String Asl_Price =getResources().getString(R.string.txt_list_asl_view_price)+ price + getResources().getString(R.string.txt_list_asl_view_valuta);

                        asl_.put("icon", R.drawable.assortiment_item);
                        asl_.put("Name", asl_name );
                        asl_.put("ID", uid_asl);
                        asl_.put("Code", code_asl);
                        asl_.put("BarCode", barcode_asl);
                        asl_.put("Marking",marking);
                        asl_.put("Bar_code", getResources().getString(R.string.txt_list_asl_view_barcode) + barcode_asl);
                        asl_.put("AllowNonIntegerSale", allow_integer);
                        asl_.put("Price", price );
                        asl_.put("PriceWithText", Asl_Price);
                        asl_.put("IncomePrice", incomePrice);
                        asl_.put("Unit",  "/"+  unitary);
                        asl_.put("UnitPrice", finalUnitPrice);
                        asl_.put("UnitInPackage", UnitInPackage);
                        asl_list.add(asl_);
                    }
                    else {
                        asl_.put("Folder_is", true);
                        asl_.put("Parent_ID", parent);
                        asl_.put("Name", asl_name);
                        asl_.put("ID", uid_asl);
                        asl_.put("Bar_code", "");
                        asl_.put("icon", R.drawable.folder_open_black_48dp);
                        asl_list.add(asl_);
                    }
                }
            }
        }
        return asl_list;
    }

    //возвращаеттовар по запрошеному айди товара
    public Assortment get_AssortimentFromID (String ID) {
        return AssortimentID.get(ID);
    }

    //возвращает все папки из массива
    public ArrayList<HashMap<String, Object>> get_AssortimentFolders() {
        ArrayList<HashMap<String, Object>> asl_list = new ArrayList<>();
        if(AssortimentID!=null)
            for (String key: AssortimentID.keySet()) {
                Assortment sal = AssortimentID.get(key);
                assert sal != null;
                boolean is_folder = sal.getIsFolder();
                String asl_name = sal.getName();
                String uid_asl = sal.getAssortimentID();
                if (is_folder) {
                    HashMap<String, Object> asl_folder = new HashMap<>();
                    asl_folder.put("Name", asl_name);
                    asl_folder.put("ID", uid_asl);
                    asl_list.add(asl_folder);
                }
            }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            asl_list.sort(new Comparator<HashMap<String, Object>>() {
                @Override
                public int compare(HashMap<String, Object> o1, HashMap<String, Object> o2) {

                    return (o1.get("Name").toString()).compareTo(o2.get("Name").toString());
                }

            });
        }
        return asl_list;
    }

    //записывание логов в папку IntelectSoft в корневую директорию устройства
    public void appendLog(String text,Context context) {
        File file = null;
        File teste = new File(Environment.getExternalStorageDirectory(),"/IntelectSoft");
        if (!teste.mkdirs()) {
            Log.e("LOG TAG", "Directory not created");
        }
        file = new File(Environment.getExternalStorageDirectory(),"/IntelectSoft/TerminalMobile_log.txt");
        if (!file.exists()) {
            try {
                file.createNewFile();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            //BufferedWriter for performance, true to set append to file flag
            BufferedWriter buf = new BufferedWriter(new FileWriter(file, true));
            Date datess = new Date();
            // To TimeZone Europe/Chisinau
            SimpleDateFormat sdfChisinau = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
            TimeZone tzInChisinau = TimeZone.getTimeZone("Europe/Chisinau");
            sdfChisinau.setTimeZone(tzInChisinau);
            String sDateInChisinau = sdfChisinau.format(datess); // Convert to String first
            String err = sDateInChisinau+ ": " + context.getClass() + ": " + text;
            buf.append(err);
            //buf.write(text);
            buf.newLine();
            buf.close(); }
        catch (IOException e) {
            e.printStackTrace();
        }
    }


    public String getErrorMessage (int code){
        String errorMsg = "";
        switch (code){
            case -1: {
                errorMsg = getString(R.string.errorMsg_Internal);
            }break;
            case 0: {
                errorMsg = getString(R.string.errorMsg_noError);
            }break;
            case 1: {
                errorMsg = getString(R.string.errorMsg_user_notExist);
            }break;
            case 2: {
                errorMsg = getString(R.string.errorMsg_assortmentNotExist);
            }break;
            case 3: {
                errorMsg = getString(R.string.errorMsg_WareHouseNotExist);
            }break;
            case 4: {
                errorMsg = getString(R.string.errorMsg_printTemplateNotSet);
            }break;
            case 5: {
                errorMsg = getString(R.string.errorMsg_printerNotFoundOrNotAvailable);
            }break;
            case 6: {
                errorMsg = getString(R.string.errorMsg_errorReadingPrintTemplate);
            }break;
            case 7: {
                errorMsg = getString(R.string.errorMsg_notRightForEdit);
            }break;
            case 8: {
                errorMsg = getString(R.string.errorMsg_barcodeAlreadyExist);
            }break;
            case 9: {
                errorMsg = getString(R.string.errorMsg_assortimentNotExist);
            }break;
            case 10: {
                errorMsg = getString(R.string.errorMsg_mainOfficetoEnterpriseNotExist);
            }break;
            case 11: {
                errorMsg = getString(R.string.errorMsg_WorkSpacetoMainOfficeNotExist);
            }break;
            default:
                errorMsg = getString(R.string.errorMsg_unknowErrore);
        }
        return errorMsg;
    }
}