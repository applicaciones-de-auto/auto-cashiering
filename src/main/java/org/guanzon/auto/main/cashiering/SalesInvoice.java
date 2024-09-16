/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.guanzon.auto.main.cashiering;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.rowset.CachedRowSet;
import org.guanzon.appdriver.base.GRider;
import org.guanzon.appdriver.base.MiscUtil;
import org.guanzon.appdriver.base.SQLUtil;
import org.guanzon.appdriver.constant.EditMode;
import org.guanzon.appdriver.iface.GTransaction;
import org.guanzon.auto.controller.cashiering.SalesInvoiceMaster;
import org.json.simple.JSONObject;

/**
 *
 * @author Arsiela
 */
public class SalesInvoice implements GTransaction{
    GRider poGRider;
    String psBranchCd;
    boolean pbWtParent;
    int pnEditMode;
    String psTransStat;
    String psMessagex;
    public JSONObject poJSON;

    SalesInvoiceMaster poController;
    
    CachedRowSet poPaymentHstry;
    
    public SalesInvoice(GRider foAppDrver, boolean fbWtParent, String fsBranchCd){
        poController = new SalesInvoiceMaster(foAppDrver,fbWtParent,fsBranchCd);
        
        poGRider = foAppDrver;
        pbWtParent = fbWtParent;
        psBranchCd = fsBranchCd.isEmpty() ? foAppDrver.getBranchCode() : fsBranchCd;
    }

    @Override
    public int getEditMode() {
        pnEditMode = poController.getEditMode();
        return pnEditMode;
    }
    
    @Override
    public JSONObject setMaster(int fnCol, Object foData) {
        return poController.setMaster(fnCol, foData);
    }

    @Override
    public JSONObject setMaster(String fsCol, Object foData) {
        return poController.setMaster(fsCol, foData);
    }

    public Object getMaster(int fnCol) {
        if(pnEditMode == EditMode.UNKNOWN)
            return null;
        else 
            return poController.getMaster(fnCol);
    }

    public Object getMaster(String fsCol) {
        return poController.getMaster(fsCol);
    }

    @Override
    public JSONObject newTransaction() {
        poJSON = new JSONObject();
        try{
            poJSON = poController.newTransaction();
            
            if("success".equals(poJSON.get("result"))){
                pnEditMode = poController.getEditMode();
            } else {
                pnEditMode = EditMode.UNKNOWN;
            }
               
        }catch(NullPointerException e){
            poJSON.put("result", "error");
            poJSON.put("message", e.getMessage());
            pnEditMode = EditMode.UNKNOWN;
        }
        return poJSON;
    }

    @Override
    public JSONObject openTransaction(String fsValue) {
        poJSON = new JSONObject();
        
        poJSON = poController.openTransaction(fsValue);
        if("success".equals(poJSON.get("result"))){
            pnEditMode = poController.getEditMode();
        } else {
            pnEditMode = EditMode.UNKNOWN;
        }
        
//        poJSON = poVSPFinance.openDetail(fsValue);
//        if(!"success".equals((String) checkData(poJSON).get("result"))){
//            pnEditMode = EditMode.UNKNOWN;
//            return poJSON;
//        }
        
        return poJSON;
    }

    @Override
    public JSONObject updateTransaction() {
        poJSON = new JSONObject();  
        poJSON = poController.updateTransaction();
        if("error".equals(poJSON.get("result"))){
            return poJSON;
        }
        pnEditMode = poController.getEditMode();
        return poJSON;
    }

    @Override
    public JSONObject saveTransaction() {
        poJSON = new JSONObject();  
        
        
        if (!pbWtParent) poGRider.beginTrans();
        
        poJSON =  poController.saveTransaction();
        if("error".equalsIgnoreCase((String) poJSON.get("result"))){
            if (!pbWtParent) poGRider.rollbackTrans();
            return checkData(poJSON);
        }
        
//        poVSPFinance.setTargetBranchCd(poController.getMasterModel().getBranchCD());
//        poJSON =  poVSPFinance.saveDetail((String) poController.getMasterModel().getTransNo());
//        if("error".equalsIgnoreCase((String)checkData(poJSON).get("result"))){
//            if (!pbWtParent) poGRider.rollbackTrans();
//            return checkData(poJSON);
//        }
        
        if (!pbWtParent) poGRider.commitTrans();
        
        return poJSON;
    }
    
    private JSONObject checkData(JSONObject joValue){
        if(pnEditMode == EditMode.ADDNEW ||pnEditMode == EditMode.READY || pnEditMode == EditMode.UPDATE){
            if(joValue.containsKey("continue")){
                if(true == (boolean)joValue.get("continue")){
                    joValue.put("result", "success");
                    joValue.put("message", "Record saved successfully.");
                }
            }
        }
        return joValue;
    }
    
    public JSONObject searchTransaction(String fsValue,boolean fbUpdate) {
        poJSON = new JSONObject();  
//        poJSON = poController.searchTransaction(fsValue, fbByCode);
//        if(!"error".equals(poJSON.get("result"))){
//            poJSON = openTransaction((String) poJSON.get("sTransNox"));
//        }
        return poJSON;
    }

    @Override
    public JSONObject deleteTransaction(String string) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public JSONObject closeTransaction(String string) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public JSONObject postTransaction(String string) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public JSONObject voidTransaction(String string) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public JSONObject cancelTransaction(String fsValue) {
        poJSON =  poController.cancelTransaction(fsValue);
        return poJSON;
    }

    @Override
    public JSONObject searchWithCondition(String string) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public JSONObject searchTransaction(String string, String string1, boolean bln) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public JSONObject searchMaster(String string, String string1, boolean bln) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public JSONObject searchMaster(int i, String string, boolean bln) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public SalesInvoiceMaster getMasterModel() {
        return poController;
    }

    @Override
    public void setTransactionStatus(String string) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
//    public SalesInvoice_Finance getVSPFinanceModel(){return poVSPFinance;} 
//    public ArrayList getVSPFinanceList(){return poVSPFinance.getDetailList();}
//    public Object addVSPFinance(){ return poVSPFinance.addDetail(poController.getMasterModel().getTransNo());}
//    public Object removeVSPFinance(int fnRow){ return poVSPFinance.removeDetail(fnRow);}
//    
//    public JSONObject searchTransaction(String fsValue, boolean fbByCode){
//        JSONObject loJSON = new JSONObject();
//        JSONObject loJSONRsv = new JSONObject();
//        loJSON = poController.searchInquiry(fsValue, fbByCode);
//        if(!"error".equals((String) loJSON.get("result"))){
//            //Buying Customer Default         
//            poController.getMasterModel().setInqTran((String) loJSON.get("sTransNox"));          
//            poController.getMasterModel().setInqryID((String) loJSON.get("sInqryIDx"));                                                        
//            poController.getMasterModel().setInqryDte(SQLUtil.toDate((String) loJSON.get("dTransact"), SQLUtil.FORMAT_SHORT_DATE));            
//            System.out.println(getMasterModel().getMasterModel().getInqryDte()); 
//            
//            poController.getMasterModel().setClientID((String) loJSON.get("sClientID"));                                                        
//            poController.getMasterModel().setBuyCltNm((String) loJSON.get("sCompnyNm"));                                                        
//            poController.getMasterModel().setAddress(((String) loJSON.get("sAddressx")).trim());                                                         
//            poController.getMasterModel().setPayMode((String) loJSON.get("cPayModex"));                                                         
//            poController.getMasterModel().setIsVhclNw((String) loJSON.get("cIsVhclNw"));  
//            
//            if((String) loJSON.get("nAmountxx") == null){                                                                                      
//                poController.getMasterModel().setResrvFee(new BigDecimal("0.00"));                                                             
//            } else {          
//                //Automatically add reservation to VSP reservation list
//                loJSONRsv = poOTHReservation.openDetail(poController.getMasterModel().getInqTran(),true, false);
//                if(!"success".equals(loJSONRsv.get("result"))){
//                    if(true == (boolean) loJSONRsv.get("continue")){
//                        loJSONRsv.put("result", "success");
//                        loJSONRsv.put("message", "Record loaded succesfully.");
//                    }
//                }
//                String lsTransID = "";
//                for(int lnCtr = 0; lnCtr <= poOTHReservation.getDetailList().size() - 1; lnCtr++){
//                    //check for approval
//                    if(poOTHReservation.getDetailModel(lnCtr).getTranStat().equals("2")){
//                        if(poOTHReservation.getDetailModel(lnCtr).getTransID() != null){
//                            lsTransID = poOTHReservation.getDetailModel(lnCtr).getTransID();
//                        }
//                        //check for payment
//                        if(lsTransID.isEmpty()){
//                            if(poOTHReservation.getDetailModel(lnCtr).getSINo() != null){
//                                if(!poOTHReservation.getDetailModel(lnCtr).getSINo().trim().isEmpty()){
//                                    addToVSPReservation(poOTHReservation.getDetailModel(lnCtr).getTransNo(),poOTHReservation.getDetailModel(lnCtr).getTransID());
//                                    computeAmount();
//                                }
//                            }
//                        }
//                        
//                    }
//                }
//            }  
//            
//            //Automatically add row for vsp finance when payment mode is not cash
//            if(!poController.getMasterModel().getPayMode().equals("0")){
//                addVSPFinance();
//            }
//                                                                                                                                               
//            //Inquiring Customer                                                                                      
//            poController.getMasterModel().setInqCltID((String) loJSON.get("sClientID"));                                                       
//            poController.getMasterModel().setInqCltNm((String) loJSON.get("sCompnyNm"));                                                       
//            poController.getMasterModel().setInqCltTp((String) loJSON.get("cClientTp"));                                                       
//            poController.getMasterModel().setSourceCD((String) loJSON.get("sSourceCD"));                                                       
//            poController.getMasterModel().setSourceNo((String) loJSON.get("sSourceNo"));                                                       
//            poController.getMasterModel().setPlatform((String) loJSON.get("sPlatform"));                                                       
//            poController.getMasterModel().setAgentID((String) loJSON.get("sAgentIDx"));                                                        
//            poController.getMasterModel().setAgentNm((String) loJSON.get("sSalesAgn"));                                                        
//            poController.getMasterModel().setEmployID((String) loJSON.get("sEmployID"));                                                       
//            poController.getMasterModel().setSEName((String) loJSON.get("sSalesExe"));                                                         
//            poController.getMasterModel().setContctNm((String) loJSON.get("sContctNm"));                                                       
//            poController.getMasterModel().setBranchCD((String) loJSON.get("sBranchCd"));                                                       
//            poController.getMasterModel().setBranchNm((String) loJSON.get("sBranchNm"));       
//            
//        } else {                                                                                                                               
//            //Buying Customer Default                                                                                                          
//            poController.getMasterModel().setClientID("");                                                                                     
//            poController.getMasterModel().setBuyCltNm("");                                                                                     
//            poController.getMasterModel().setAddress("");                                                                                      
//            poController.getMasterModel().setPayMode("");                                                                                      
//            poController.getMasterModel().setIsVhclNw("");                                                                                     
//            poController.getMasterModel().setResrvFee(new BigDecimal("0.00"));                                                                 
//                                                                                                                                               
//            //Inquiring Customer                                                                                                               
//            poController.getMasterModel().setInqryID("");                                                                                      
//            poController.getMasterModel().setInqryDte(SQLUtil.toDate("1900-01-01", SQLUtil.FORMAT_SHORT_DATE));                                
//            System.out.println(poController.getMasterModel().getInqryDte());                                                               
//            poController.getMasterModel().setInqCltID("");                                                                                     
//            poController.getMasterModel().setInqCltNm("");                                                                                     
//            poController.getMasterModel().setInqCltTp("");                                                                                     
//            poController.getMasterModel().setSourceCD("");                                                                                     
//            poController.getMasterModel().setSourceNo("");                                                                                     
//            poController.getMasterModel().setPlatform("");                                                                                     
//            poController.getMasterModel().setAgentID("");                                                                                      
//            poController.getMasterModel().setAgentNm("");                                                                                      
//            poController.getMasterModel().setEmployID("");                                                                                     
//            poController.getMasterModel().setSEName("");                                                                                       
//            poController.getMasterModel().setContctNm("");                                                                                     
//            poController.getMasterModel().setBranchCD("");                                                                                     
//            poController.getMasterModel().setBranchNm("");      
//        }
//        
//        
//        return loJSON;
//    }
//    
//    public JSONObject searchClient(String fsValue, boolean fbBuyClient){
//        JSONObject loJSON = new JSONObject();
//        loJSON = poController.searchClient(fsValue,fbBuyClient);
//        if(!"error".equals((String) loJSON.get("result"))){
//            if(fbBuyClient){
//                poController.getMasterModel().setClientID((String) loJSON.get("sClientID"));
//                poController.getMasterModel().setBuyCltNm((String) loJSON.get("sCompnyNm"));
//                poController.getMasterModel().setAddress((String) loJSON.get("sAddressx"));
//            } else {
//                poController.getMasterModel().setCoCltID((String) loJSON.get("sClientID"));
//                poController.getMasterModel().setCoCltNm((String) loJSON.get("sCompnyNm"));
//            }
//        } else {
//            if(fbBuyClient){
//                poController.getMasterModel().setClientID("");
//                poController.getMasterModel().setBuyCltNm("");
//                poController.getMasterModel().setClientTp("");
//                poController.getMasterModel().setAddress("");
//            } else {
//                poController.getMasterModel().setCoCltID("");
//                poController.getMasterModel().setCoCltNm("");
//            }
//        }
//        return loJSON;
//    }
    

}
