
import java.math.BigDecimal;
import java.sql.SQLException;
import org.guanzon.appdriver.base.GRider;
import org.json.simple.JSONObject;
import org.junit.AfterClass;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
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
public class VehicleSalesInvoiceTest {
//    static VehicleSalesInvoice model;
    JSONObject json;
    boolean result;
    static GRider instance;
    public VehicleSalesInvoiceTest(){}
    
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
//        model = new VehicleSalesInvoice(instance,false, instance.getBranchCode());
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
//            json = model.getMasterModel().getMasterModel().setTransactDte(instance.getServerDate());
//            if ("error".equals((String) json.get("result"))){
//                System.err.println((String) json.get("message"));
//                System.exit(1);
//            }
//            json = model.getMasterModel().getMasterModel().setClientID("M00124000031");
//            if ("error".equals((String) json.get("result"))){
//                System.err.println((String) json.get("message"));
//                System.exit(1);
//            }
//            json = model.getMasterModel().getMasterModel().setBranchCd("M001");
//            if ("error".equals((String) json.get("result"))){
//                System.err.println((String) json.get("message"));
//                System.exit(1);
//            }
//            json = model.getMasterModel().getMasterModel().setTranTotl(new BigDecimal("214250.00"));
//            if ("error".equals((String) json.get("result"))){
//                System.err.println((String) json.get("message"));
//                System.exit(1);
//            }
//            json = model.getMasterModel().getMasterModel().setDiscount(new BigDecimal("0.00"));
//            if ("error".equals((String) json.get("result"))){
//                System.err.println((String) json.get("message"));
//                System.exit(1);
//            }
//            
//            model.addVSISource();
//            for(int lnCtr = 0; lnCtr <= model.getVSISourceList().size()-1; lnCtr++){
//                json = model.getVSISourceModel().getDetailModel().setReferNo("M001VDR24004");
//                if ("error".equals((String) json.get("result"))){
//                    System.err.println((String) json.get("message"));
//                    System.exit(1);
//                }
//                
//                json = model.getVSISourceModel().getDetailModel().setTranAmt(new BigDecimal("214250.00"));
//                if ("error".equals((String) json.get("result"))){
//                    System.err.println((String) json.get("message"));
//                    System.exit(1);
//                }
//                
//                json = model.getVSISourceModel().getDetailModel().setDiscount(new BigDecimal("0.00"));
//                if ("error".equals((String) json.get("result"))){
//                    System.err.println((String) json.get("message"));
//                    System.exit(1);
//                }
//                
//                json = model.getVSISourceModel().getDetailModel().setAdvused(new BigDecimal("0.00"));
//                if ("error".equals((String) json.get("result"))){
//                    System.err.println((String) json.get("message"));
//                    System.exit(1);
//                }
//                
//                json = model.getVSISourceModel().getDetailModel().setNetAmt(new BigDecimal("214250.00"));
//                if ("error".equals((String) json.get("result"))){
//                    System.err.println((String) json.get("message"));
//                    System.exit(1);
//                }
//                
//                json = model.getVSISourceModel().getDetailModel().setTranType("VSI");
//                if ("error".equals((String) json.get("result"))){
//                    System.err.println((String) json.get("message"));
//                    System.exit(1);
//                }
//            }
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
//        json = model.openTransaction("M001BA240001");
//        
//        if (!"success".equals((String) json.get("result"))){
//            result = false;
//        } else {
//            System.out.println("--------------------------------------------------------------------");
//            System.out.println("BANK APPLICATION");
//            System.out.println("--------------------------------------------------------------------");
//            System.out.println("sTransNox  :  " + model.getMaster("sTransNox"));
//            System.out.println("sApplicNo  :  " + model.getMaster("sApplicNo"));
//            System.out.println("dAppliedx  :  " + model.getMaster("dAppliedx"));
//            System.out.println("dApproved  :  " + model.getMaster("dApproved"));
//            System.out.println("cPayModex  :  " + model.getMaster("cPayModex"));
//            System.out.println("sSourceCD  :  " + model.getMaster("sSourceCD"));
//            System.out.println("sSourceNo  :  " + model.getMaster("sSourceNo"));
//            System.out.println("sBrBankID  :  " + model.getMaster("sBrBankID"));
//            System.out.println("sRemarksx  :  " + model.getMaster("sRemarksx"));
//            System.out.println("cTranStat  :  " + model.getMaster("cTranStat"));
//            System.out.println("sEntryByx  :  " + model.getMaster("sEntryByx"));
//            System.out.println("dEntryDte  :  " + model.getMaster("dEntryDte"));
//            System.out.println("sModified  :  " + model.getMaster("sModified"));
//            System.out.println("dModified  :  " + model.getMaster("dModified"));
//            System.out.println("sCancelld  :  " + model.getMaster("sCancelld"));
//            System.out.println("dCancelld  :  " + model.getMaster("dCancelld"));
//            System.out.println("sBrBankNm  :  " + model.getMaster("sBrBankNm"));
//            System.out.println("sBankIDxx  :  " + model.getMaster("sBankIDxx"));
//            System.out.println("sBankName  :  " + model.getMaster("sBankName"));
//            System.out.println("sTownName  :  " + model.getMaster("sTownName"));
//            System.out.println("sProvName  :  " + model.getMaster("sProvName"));
//            
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
    
}
