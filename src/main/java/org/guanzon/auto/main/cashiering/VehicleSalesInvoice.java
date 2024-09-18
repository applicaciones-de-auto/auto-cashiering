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
import org.guanzon.auto.controller.cashiering.SalesInvoice_Master;
import org.guanzon.auto.controller.cashiering.SalesInvoice_Source;
import org.guanzon.auto.controller.cashiering.VehicleSalesInvoice_Source;
import org.json.simple.JSONObject;

/**
 *
 * @author Arsiela
 */
public class VehicleSalesInvoice implements GTransaction{
    GRider poGRider;
    String psBranchCd;
    boolean pbWtParent;
    int pnEditMode;
    String psTransStat;
    String psMessagex;
    public JSONObject poJSON;

    SalesInvoice_Master poController;
    VehicleSalesInvoice_Source poVSISource;
    
    public VehicleSalesInvoice(GRider foAppDrver, boolean fbWtParent, String fsBranchCd){
        poController = new SalesInvoice_Master(foAppDrver,fbWtParent,fsBranchCd);
        poVSISource = new VehicleSalesInvoice_Source(foAppDrver);
        
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
        
        poJSON = poVSISource.openDetail(fsValue);
        if(!"success".equals((String) checkData(poJSON).get("result"))){
            pnEditMode = EditMode.UNKNOWN;
            return poJSON;
        }
        
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
        
        poVSISource.setTargetBranchCd(poController.getMasterModel().getBranchCd());
        poJSON =  poVSISource.saveDetail((String) poController.getMasterModel().getTransNo());
        if("error".equalsIgnoreCase((String)checkData(poJSON).get("result"))){
            if (!pbWtParent) poGRider.rollbackTrans();
            return checkData(poJSON);
        }
        
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
    
    public JSONObject searchTransaction(String fsValue) {
        poJSON = new JSONObject();  
        poJSON = poController.searchTransaction(fsValue);
        if(!"error".equals(poJSON.get("result"))){
            poJSON = openTransaction((String) poJSON.get("sTransNox"));
        }
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
        return poController.cancelTransaction(fsValue);
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
    public SalesInvoice_Master getMasterModel() {
        return poController;
    }

    @Override
    public void setTransactionStatus(String string) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public VehicleSalesInvoice_Source getVSISourceModel(){return poVSISource;} 
    public ArrayList getVSISourceList(){return poVSISource.getDetailList();}
    public Object addVSISource(){ return poVSISource.addDetail(poController.getMasterModel().getTransNo());}
    
    public JSONObject searchVDR(String fsValue, boolean fbByCode){
        JSONObject loJSON = new JSONObject();
        loJSON = poController.searchVDR(fsValue, fbByCode);
        if(!"error".equals((String) loJSON.get("result"))){
            //Buying Customer Default         
            poController.getMasterModel().setClientID((String) loJSON.get("sClientID"));                                                        
            poController.getMasterModel().setBuyCltNm((String) loJSON.get("sBuyCltNm"));                                                        
            poController.getMasterModel().setAddress(((String) loJSON.get("sAddressx")).trim());                                                     
            poController.getMasterModel().setBranchCd((String) loJSON.get("sBranchCD"));         
            
           if(getVSISourceList().size()-1 < 0){
               addVSISource();
           } 
           
           poVSISource.getDetailModel().setCustType((String) loJSON.get("cCustType"));
           poVSISource.getDetailModel().setUDRNo((String) loJSON.get("sTransNox"));
           poVSISource.getDetailModel().setCSNo((String) loJSON.get("sCSNoxxxx"));
           poVSISource.getDetailModel().setPlateNo((String) loJSON.get("sPlateNox"));
           poVSISource.getDetailModel().setEngineNo((String) loJSON.get("sEngineNo"));
           poVSISource.getDetailModel().setFrameNo((String) loJSON.get("sFrameNox"));
           poVSISource.getDetailModel().setColorDsc((String) loJSON.get("sColorDsc"));
           poVSISource.getDetailModel().setSEName((String) loJSON.get("sSENamexx"));
           poVSISource.getDetailModel().setCoCltNm((String) loJSON.get("sCoCltNmx"));
        } else { 
            poController.getMasterModel().setClientID("");                                                        
            poController.getMasterModel().setBuyCltNm("");                                                        
            poController.getMasterModel().setAddress("");                                                     
            poController.getMasterModel().setBranchCd("");    
            
            poVSISource.getDetailModel().setCustType("");
            poVSISource.getDetailModel().setUDRNo("");
            poVSISource.getDetailModel().setCSNo("");
            poVSISource.getDetailModel().setPlateNo("");
            poVSISource.getDetailModel().setEngineNo("");
            poVSISource.getDetailModel().setFrameNo("");
            poVSISource.getDetailModel().setColorDsc("");
            poVSISource.getDetailModel().setSEName("");
            poVSISource.getDetailModel().setCoCltNm("");   
        }
        return loJSON;
    }
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
