
import java.math.BigDecimal;
import java.sql.SQLException;
import org.guanzon.appdriver.base.GRider;
import org.guanzon.auto.main.cashiering.SalesInvoice;
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
public class SalesInvoiceTest {
    static SalesInvoice model;
    JSONObject json;
    boolean result;
    static GRider instance;
    public SalesInvoiceTest(){}
    
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
        model = new SalesInvoice(instance,false, instance.getBranchCode());
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
//            json = model.getMasterModel().getMasterModel().setReferNo("SI0001");
//            if ("error".equals((String) json.get("result"))){
//                System.err.println((String) json.get("message"));
//                System.exit(1);
//            }
//            json = model.getMasterModel().getMasterModel().setClientID("M00124000046");
//            if ("error".equals((String) json.get("result"))){
//                System.err.println((String) json.get("message"));
//                System.exit(1);
//            }
//            json = model.getMasterModel().getMasterModel().setBranchCd("M001");
//            if ("error".equals((String) json.get("result"))){
//                System.err.println((String) json.get("message"));
//                System.exit(1);
//            }
//            json = model.getMasterModel().getMasterModel().setTranTotl(new BigDecimal("34010.50"));
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
//            model.addSIDetail();
//            for(int lnCtr = 0; lnCtr <= model.getSIDetailList().size()-1; lnCtr++){
//                json = model.getSIDetailModel().getDetailModel(lnCtr).setSourceNo("M001CAR24020");
//                if ("error".equals((String) json.get("result"))){
//                    System.err.println((String) json.get("message"));
//                    System.exit(1);
//                }
//                
//                json = model.getSIDetailModel().getDetailModel(lnCtr).setTranAmt(new BigDecimal("34010.50"));
//                if ("error".equals((String) json.get("result"))){
//                    System.err.println((String) json.get("message"));
//                    System.exit(1);
//                }
//                
//                json = model.getSIDetailModel().getDetailModel(lnCtr).setDiscount(new BigDecimal("0.00"));
//                if ("error".equals((String) json.get("result"))){
//                    System.err.println((String) json.get("message"));
//                    System.exit(1);
//                }
//                
//                json = model.getSIDetailModel().getDetailModel(lnCtr).setAdvused(new BigDecimal("0.00"));
//                if ("error".equals((String) json.get("result"))){
//                    System.err.println((String) json.get("message"));
//                    System.exit(1);
//                }
//                
//                json = model.getSIDetailModel().getDetailModel(lnCtr).setNetAmt(new BigDecimal("34010.50"));
//                if ("error".equals((String) json.get("result"))){
//                    System.err.println((String) json.get("message"));
//                    System.exit(1);
//                }
//                
//                json = model.getSIDetailModel().getDetailModel(lnCtr).setSourceCD("POL");
//                if ("error".equals((String) json.get("result"))){
//                    System.err.println((String) json.get("message"));
//                    System.exit(1);
//                }
//                
//                json = model.getSIDetailModel().getDetailModel(lnCtr).setTranType("INSURANCE");
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
//        json = model.openTransaction("M00124000006");
//        
//        if (!"success".equals((String) json.get("result"))){
//            result = false;
//        } else {
//            System.out.println("--------------------------------------------------------------------");
//            System.out.println("SALES INVOICE MASTER");
//            System.out.println("--------------------------------------------------------------------");
//            System.out.println("sTransNox  :  " + model.getMasterModel().getMasterModel().getTransNo());   
//            System.out.println("sBranchCd  :  " + model.getMasterModel().getMasterModel().getBranchCd());  
//            System.out.println("dTransact  :  " + model.getMasterModel().getMasterModel().getTransactDte());  
//            System.out.println("cDocTypex  :  " + model.getMasterModel().getMasterModel().getDocType());   
//            System.out.println("sReferNox  :  " + model.getMasterModel().getMasterModel().getReferNo());   
//            System.out.println("sClientID  :  " + model.getMasterModel().getMasterModel().getClientID());  
//            System.out.println("nTranTotl  :  " + model.getMasterModel().getMasterModel().getTranTotl());  
//            System.out.println("nDiscount  :  " + model.getMasterModel().getMasterModel().getDiscount());  
//            System.out.println("nVatSales  :  " + model.getMasterModel().getMasterModel().getVatSales());  
//            System.out.println("nVatAmtxx  :  " + model.getMasterModel().getMasterModel().getVatAmt());    
//            System.out.println("nNonVATSl  :  " + model.getMasterModel().getMasterModel().getNonVATSl());  
//            System.out.println("nZroVATSl  :  " + model.getMasterModel().getMasterModel().getZroVATSl());  
//            System.out.println("cWTRatexx  :  " + model.getMasterModel().getMasterModel().getWTRate());    
//            System.out.println("nCWTAmtxx  :  " + model.getMasterModel().getMasterModel().getCWTAmt());    
//            System.out.println("nAdvPaymx  :  " + model.getMasterModel().getMasterModel().getAdvPaym());   
//            System.out.println("nNetTotal  :  " + model.getMasterModel().getMasterModel().getNetTotal());    
//            System.out.println("nCashAmtx  :  " + model.getMasterModel().getMasterModel().getCashAmt());   
//            System.out.println("nChckAmtx  :  " + model.getMasterModel().getMasterModel().getChckAmt());   
//            System.out.println("nCardAmtx  :  " + model.getMasterModel().getMasterModel().getCardAmt());   
//            System.out.println("nOthrAmtx  :  " + model.getMasterModel().getMasterModel().getOthrAmt());   
//            System.out.println("nGiftAmtx  :  " + model.getMasterModel().getMasterModel().getGiftAmt());   
//            System.out.println("nAmtPaidx  :  " + model.getMasterModel().getMasterModel().getAmtPaid());   
//            System.out.println("cPrintedx  :  " + model.getMasterModel().getMasterModel().getPrinted());   
//            System.out.println("cTranStat  :  " + model.getMasterModel().getMasterModel().getTranStat());  
//            System.out.println("sModified  :  " + model.getMasterModel().getMasterModel().getModifiedBy());  
//            System.out.println("dModified  :  " + model.getMasterModel().getMasterModel().getModifiedDte());  
//            System.out.println("sTranStat  :  " + model.getMasterModel().getMasterModel().getTranStat());  
//            System.out.println("sBuyCltNm  :  " + model.getMasterModel().getMasterModel().getBuyCltNm());  
//            System.out.println("cClientTp  :  " + model.getMasterModel().getMasterModel().getClientTp());  
//            System.out.println("sTaxIDNox  :  " + model.getMasterModel().getMasterModel().getTaxIDNo());   
//            System.out.println("sAddressx  :  " + model.getMasterModel().getMasterModel().getAddress());   
//            
//            
//            System.out.println("--------------------------------------------------------------------");
//            System.out.println("SALES INVOICE SOURCE");
//            System.out.println("--------------------------------------------------------------------");
//            for(int lnCtr = 0; lnCtr <= model.getSIDetailList().size()-1; lnCtr++){
//                json = model.getSIDetailModel().getDetailModel(lnCtr).setSourceNo("M001CAR24020");
//                if ("error".equals((String) json.get("result"))){
//                    System.err.println((String) json.get("message"));
//                    System.exit(1);
//                }
//                
//                json = model.getSIDetailModel().getDetailModel(lnCtr).setTranAmt(new BigDecimal("34010.50"));
//                if ("error".equals((String) json.get("result"))){
//                    System.err.println((String) json.get("message"));
//                    System.exit(1);
//                }
//                
//                json = model.getSIDetailModel().getDetailModel(lnCtr).setDiscount(new BigDecimal("0.00"));
//                if ("error".equals((String) json.get("result"))){
//                    System.err.println((String) json.get("message"));
//                    System.exit(1);
//                }
//                
//                json = model.getSIDetailModel().getDetailModel(lnCtr).setAdvused(new BigDecimal("0.00"));
//                if ("error".equals((String) json.get("result"))){
//                    System.err.println((String) json.get("message"));
//                    System.exit(1);
//                }
//                
//                json = model.getSIDetailModel().getDetailModel(lnCtr).setNetAmt(new BigDecimal("34010.50"));
//                if ("error".equals((String) json.get("result"))){
//                    System.err.println((String) json.get("message"));
//                    System.exit(1);
//                }
//                
//                json = model.getSIDetailModel().getDetailModel(lnCtr).setSourceCD("POL");
//                if ("error".equals((String) json.get("result"))){
//                    System.err.println((String) json.get("message"));
//                    System.exit(1);
//                }
//                
//                json = model.getSIDetailModel().getDetailModel(lnCtr).setTranType("INSURANCE");
//                if ("error".equals((String) json.get("result"))){
//                    System.err.println((String) json.get("message"));
//                    System.exit(1);
//                }
//            }
//            
//            result = true;
//        }
//        assertTrue(result);
//        //assertFalse(result);
//    }
//    
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
