/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.guanzon.auto.main.cashiering;

import java.math.BigDecimal;
import org.guanzon.appdriver.base.GRider;
import org.guanzon.appdriver.constant.EditMode;
import org.guanzon.appdriver.iface.GTransaction;
import org.guanzon.auto.controller.cashiering.CashierReceivables_Detail;
import org.guanzon.auto.controller.cashiering.CashierReceivables_Master;
import org.guanzon.auto.main.sales.VehicleSalesProposal;
import org.json.simple.JSONObject;

/**
 *
 * @author Arsiela
 */
public class CashierReceivables implements GTransaction{
    GRider poGRider;
    String psBranchCd;
    boolean pbWtParent;
    int pnEditMode;
    String psTransStat;
    String psMessagex;
    public JSONObject poJSON;  
    
    CashierReceivables_Master poController;
    CashierReceivables_Detail poDetail;
    
    public CashierReceivables(GRider foAppDrver, boolean fbWtParent, String fsBranchCd){
        poController = new CashierReceivables_Master(foAppDrver,fbWtParent,fsBranchCd);
        poDetail = new CashierReceivables_Detail(foAppDrver);
        
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
        if("error".equalsIgnoreCase((String) checkData(poJSON).get("result"))){
            if (!pbWtParent) poGRider.rollbackTrans();
            return checkData(poJSON);
        }
        
        poJSON =  poDetail.saveDetail((String) poController.getMasterModel().getTransNo());
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
    
    public JSONObject searchTransaction(String fsValue, boolean fbByCode) {
        poJSON = new JSONObject();  
        poJSON = poController.searchTransaction(fsValue, fbByCode);
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
    public CashierReceivables_Master getMasterModel() {
        return poController;
    }
    @Override
    public void setTransactionStatus(String string) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    /**
     * Check Existing CAR
     * @param fsTransSource the Transaction source
     * @param fsTransCode the primary sTransNox of a transaction
     * @return 
     */
    public JSONObject generateCAR(String fsTransSource, String fsTransCode){
        JSONObject loJSON = new JSONObject();
        String lsCARCode = "";
        switch(fsTransSource){
            case "VSP"://source group: VEHICLE SALES
                /*Note for VEHICLE SALES
                VEHICLE SALES transactions were grouped into
                1. Charged to Customer
                2. Charged to Bank -proceeds for Financing and Purchase Order payment mode */
                
                //Retrieve VSP 
                VehicleSalesProposal loVSP = new VehicleSalesProposal(poGRider, pbWtParent, psBranchCd);
                loJSON = loVSP.openTransaction(fsTransCode);
                if(!"error".equals((String) loJSON.get("result"))){
                    //Re-compute amount
                    loVSP.computeAmount();
                    
                    loJSON = poController.checkExistingCAR("c","VEHICLE SALES", fsTransCode);
                    //If CAR is already exist for the said transaction then retrieve the car to UPDATE
                    if(!"exist".equals((String) loJSON.get("result"))){
                        loJSON = newTransaction();
                    } else {
                        loJSON = openTransaction((String) loJSON.get("sTransNox"));
                    } 
                    
                    /*1. CHARGED TO CUSTOMER Transactions */
                    if(!"error".equals((String) loJSON.get("result"))){
                        poController.getMasterModel().setPayerCde("c");
                        poController.getMasterModel().setSourceCD("VEHICLE SALES");
                        poController.getMasterModel().setReferNo(fsTransCode);
                        poController.getMasterModel().setClientID(loVSP.getMasterModel().getMasterModel().getClientID());
                        poController.getMasterModel().setGrossAmt(loVSP.getMasterModel().getMasterModel().getTranTotl());
                        poController.getMasterModel().setDiscAmt(loVSP.getTotalDiscount());
                        poController.getMasterModel().setTotalAmt(loVSP.getMasterModel().getMasterModel().getNetTTotl());
                    }
                    
                    //Save CAR Head
                    loJSON = saveTransaction();
                    if(!"error".equals((String) loJSON.get("result"))){
                        //Mandatory delete the CAR detail
                        loJSON = poDetail.getMasterModel().deleteRecord(poController.getMasterModel().getTransNo());
                        if("error".equalsIgnoreCase((String)checkData(poJSON).get("result"))){
                            if (!pbWtParent) poGRider.rollbackTrans();
                            return checkData(poJSON);
                        }
                        
                        //Proceed to save CAR Detail
                        //IDENTIFIED new and used vehicle sales 
                        poDetail.addDetail(poController.getMasterModel().getTransNo());
                        if(loVSP.getMasterModel().getMasterModel().getIsVhclNw().equals("0")){ 
                            //BRAND NEW : UNIT -NEW VEHICLE SALES
                            poDetail.getDetailModel(poDetail.getDetailList().size()-1).setTranType("NEWVHCLSALE");
                        } else {
                           //PRE OWNED :  UNIT -USED VEHICLE SALES
                            poDetail.getDetailModel(poDetail.getDetailList().size()-1).setTranType("USEDVHCLSALE");
                        }
                    
                    }
                    
                    
                    
                    
                    
                }
                
                
            break;
        }
        return loJSON;
    }
    
    
}
