
import org.guanzon.appdriver.base.GRider;
import org.guanzon.auto.main.cashiering.StatementOfAccount;
import org.json.simple.JSONObject;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Arsiela
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class StatementOfAccountTest { 
    static StatementOfAccount model;
    JSONObject json;
    boolean result;
    static GRider instance;
    public StatementOfAccountTest(){}
    
    @BeforeClass
    public static void setUpClass() {   
        
        String path;
        if(System.getProperty("os.name").toLowerCase().contains("win")){
            path = "D:/GGC_Maven_Systems";
        }
        else{
            path = "/srv/GGC_Maven_Systems";
        }
        System.setProperty("sys.default.path.config", path);
        instance = new GRider("gRider");
        if (!instance.logUser("gRider", "M001000001")){
            System.err.println(instance.getMessage() + instance.getErrMsg());
            System.exit(1);
        }
        System.out.println("Connected");
        System.setProperty("sys.default.path.metadata", "D:/GGC_Maven_Systems/config/metadata/");
        
        
        JSONObject json;
        
        System.out.println("sBranch code = " + instance.getBranchCode());
        model = new StatementOfAccount(instance,false, instance.getBranchCode());
    }
    
    @AfterClass
    public static void tearDownClass() {
        
    }
    
    /**
     * COMMENTED TESTING TO CLEAN AND BUILD PROPERLY
     * WHEN YOU WANT TO CHECK KINDLY UNCOMMENT THE TESTING CASES (@Test).
     * ARSIELA
     */
    
//    @Test
//    public void test01NewRecord() throws SQLException{
//        System.out.println("--------------------------------------------------------------------");
//        System.out.println("------------------------------NEW RECORD--------------------------------------");
//        System.out.println("--------------------------------------------------------------------");
//        
//        json = model.newTransaction();
//        if ("success".equals((String) json.get("result"))){
//            json = model.getMasterModel().getMasterModel().setClientID("M00124000031");
//            if ("error".equals((String) json.get("result"))){
//                System.err.println((String) json.get("message"));
//                System.exit(1);
//            }
//            
//            json = model.getMasterModel().getMasterModel().setReferNo("M00124000031");
//            if ("error".equals((String) json.get("result"))){
//                System.err.println((String) json.get("message"));
//                System.exit(1);
//            }
//            
//            json = model.getMasterModel().getMasterModel().setSourceCD("sSourceCD");
//            if ("error".equals((String) json.get("result"))){
//                System.err.println((String) json.get("message"));
//                System.exit(1);
//            }
//            
//            json = model.getMasterModel().getMasterModel().setPayerCde("c");
//            if ("error".equals((String) json.get("result"))){
//                System.err.println((String) json.get("message"));
//                System.exit(1);
//            }
//            
//            json = model.getMasterModel().getMasterModel().setRemarks("TESTING LANG ITO");
//            if ("error".equals((String) json.get("result"))){
//                System.err.println((String) json.get("message"));
//                System.exit(1);
//            }
//            
//            
//        } else {
//            System.err.println("result = " + (String) json.get("result"));
//            fail((String) json.get("message"));
//        }
//        
//    }
//    
//    @Test
//    public void test01NewRecordSave(){
//        System.out.println("--------------------------------------------------------------------");
//        System.out.println("------------------------------NEW RECORD SAVING--------------------------------------");
//        System.out.println("--------------------------------------------------------------------");
//        
//        json = model.saveTransaction();
//        System.err.println((String) json.get("message"));
//        
//        if (!"success".equals((String) json.get("result"))){
//            System.err.println((String) json.get("message"));
//            result = false;
//        } else {
//            System.out.println((String) json.get("message"));
//            result = true;
//        }
//        assertTrue(result);
//        //assertFalse(result);
//    }
    
    
//    @Test
//    public void test02OpenRecord(){
//        System.out.println("--------------------------------------------------------------------");
//        System.out.println("------------------------------RETRIEVAL--------------------------------------");
//        System.out.println("--------------------------------------------------------------------");
//        
//        json = model.openTransaction("M001CAR24001");
//        
//        if (!"success".equals((String) json.get("result"))){
//            result = false;
//        } else {
//            System.out.println("--------------------------------------------------------------------");
//            System.out.println("CASHIER RECEIVABLES");
//            System.out.println("--------------------------------------------------------------------");
//            System.out.println("sTransNox  :  " + model.getMasterModel().getMasterModel().getTransNo());        
//            System.out.println("dTransact  :  " + model.getMasterModel().getMasterModel().getTransactDte());      
//            System.out.println("sClientID  :  " + model.getMasterModel().getMasterModel().getClientID());       
//            System.out.println("sBrBankCd  :  " + model.getMasterModel().getMasterModel().getBrBankCd());       
//            System.out.println("sBrInsCde  :  " + model.getMasterModel().getMasterModel().getBrInsCde());       
//            System.out.println("sRemarksx  :  " + model.getMasterModel().getMasterModel().getRemarks());        
//            System.out.println("sReferNox  :  " + model.getMasterModel().getMasterModel().getReferNo());        
//            System.out.println("sSourceCD  :  " + model.getMasterModel().getMasterModel().getSourceCD());       
//            System.out.println("cPayerCde  :  " + model.getMasterModel().getMasterModel().getPayerCde());       
//            System.out.println("nGrossAmt  :  " + model.getMasterModel().getMasterModel().getGrossAmt());       
//            System.out.println("nDiscAmtx  :  " + model.getMasterModel().getMasterModel().getDiscAmt());        
//            System.out.println("nDeductnx  :  " + model.getMasterModel().getMasterModel().getDiscAmt());        
//            System.out.println("nTotalAmt  :  " + model.getMasterModel().getMasterModel().getTotalAmt());       
//            System.out.println("nChckPayx  :  " + model.getMasterModel().getMasterModel().getChckPay());        
//            System.out.println("nAmtPaidx  :  " + model.getMasterModel().getMasterModel().getAmtPaid());        
//            System.out.println("sPayerNme  :  " + model.getMasterModel().getMasterModel().getPayerNme());         
//            System.out.println("sOwnrNmxx  :  " + model.getMasterModel().getMasterModel().getOwnrNm());         
//            System.out.println("cClientTp  :  " + model.getMasterModel().getMasterModel().getClientTp());       
//            System.out.println("sAddressx  :  " + model.getMasterModel().getMasterModel().getAddress());        
//            System.out.println("sBankName  :  " + model.getMasterModel().getMasterModel().getBankName());       
//            System.out.println("sBankAddr  :  " + model.getMasterModel().getMasterModel().getBankAddr());       
//            System.out.println("sInsNamex  :  " + model.getMasterModel().getMasterModel().getInsName());         
//            System.out.println("sInsAddrx  :  " + model.getMasterModel().getMasterModel().getInsAddr());   
//            
//            result = true;
//        }
//        assertTrue(result);
//        //assertFalse(result);
//    }
    
//    @Test
//    public void test03UpdateRecord(){
//        System.out.println("--------------------------------------------------------------------");
//        System.out.println("------------------------------UPDATE RECORD--------------------------------------");
//        System.out.println("--------------------------------------------------------------------");
//        
//        json = model.updateTransaction();
//        System.err.println((String) json.get("message"));
//        if ("error".equals((String) json.get("result"))){
//            System.err.println((String) json.get("message"));
//            result = false;
//        } else {
//            result = true;
//        }
//        
//        json = model.setMaster("dApproved",instance.getServerDate());
//        if ("error".equals((String) json.get("result"))){
//            System.err.println((String) json.get("message"));
//            System.exit(1);
//        }
//        
//        json = model.setMaster("cTranStat","2");
//        if ("error".equals((String) json.get("result"))){
//            System.err.println((String) json.get("message"));
//            System.exit(1);
//        }
//        
//        assertTrue(result);
//        //assertFalse(result);
//    }
//    
//    @Test
//    public void test03UpdateRecordSave(){
//        System.out.println("--------------------------------------------------------------------");
//        System.out.println("------------------------------UPDATE RECORD SAVING--------------------------------------");
//        System.out.println("--------------------------------------------------------------------");
//        
//        json = model.saveTransaction();
//        System.err.println((String) json.get("message"));
//        
//        if (!"success".equals((String) json.get("result"))){
//            System.err.println((String) json.get("message"));
//            result = false;
//        } else {
//            System.out.println((String) json.get("message"));
//            result = true;
//        }
//        assertTrue(result);
//        //assertFalse(result);
//    }
    
//    private static String xsDateShort(Date fdValue) {
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//        String date = sdf.format(fdValue);
//        return date;
//    }
//
//    private static String xsDateShort(String fsValue) throws org.json.simple.parser.ParseException, java.text.ParseException {
//        SimpleDateFormat fromUser = new SimpleDateFormat("MMMM dd, yyyy");
//        SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd");
//        String lsResult = "";
//        lsResult = myFormat.format(fromUser.parse(fsValue));
//        return lsResult;
//    }
//    
//    /*Convert Date to String*/
//    private LocalDate strToDate(String val) {
//        DateTimeFormatter date_formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//        LocalDate localDate = LocalDate.parse(val, date_formatter);
//        return localDate;
//    }
}
