/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.guanzon.auto.controller.cashiering;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.guanzon.appdriver.base.GRider;
import org.guanzon.appdriver.base.MiscUtil;
import org.guanzon.appdriver.base.SQLUtil;
import org.guanzon.appdriver.constant.EditMode;
import org.guanzon.appdriver.iface.GTranDet;
import org.guanzon.auto.model.cashiering.Model_SalesInvoice_Advances_Source;
import org.guanzon.auto.model.sales.Model_VehicleDeliveryReceipt_Master;
import org.guanzon.auto.validator.cashiering.ValidatorFactory;
import org.guanzon.auto.validator.cashiering.ValidatorInterface;
import org.json.simple.JSONObject;

/**
 *
 * @author Arsiela
 */
public class SalesInvoice_Advances_Source implements GTranDet {
    final String XML = "Model_SalesInvoice_Advances_Source.xml";
    GRider poGRider;
    String psTargetBranchCd = "";
    boolean pbWtParent;
    
    int pnEditMode;
    String psMessagex;
    public JSONObject poJSON;
    
    ArrayList<Model_SalesInvoice_Advances_Source> paDetail;
    ArrayList<Model_SalesInvoice_Advances_Source> paRemDetail;

    public SalesInvoice_Advances_Source(GRider foAppDrver){
        poGRider = foAppDrver;
    }
    
    @Override
    public int getEditMode() {
        return pnEditMode;
    }
    
    @Override
    public int getItemCount() {
        if(paDetail == null){
           paDetail = new ArrayList<>();
        }
        return paDetail.size();
    }

    @Override
    public Model_SalesInvoice_Advances_Source getDetailModel(int fnRow) {
        return paDetail.get(fnRow);
    }
    
    public JSONObject addDetail(String fsTransNo){
        if(paDetail == null){
           paDetail = new ArrayList<>();
        }
        
        poJSON = new JSONObject();
        if (paDetail.size()<=0){
            paDetail.add(new Model_SalesInvoice_Advances_Source(poGRider));
            paDetail.get(0).newRecord();
            paDetail.get(0).setTransNo(fsTransNo);
            paDetail.get(0).setEntryNo(0);
            
            poJSON.put("result", "success");
            poJSON.put("message", "SI Advances Source add record.");
        } else {
            paDetail.add(new Model_SalesInvoice_Advances_Source(poGRider));
            paDetail.get(paDetail.size()-1).newRecord();
            paDetail.get(paDetail.size()-1).setTransNo(fsTransNo);
            paDetail.get(paDetail.size()-1).setEntryNo(0);
            poJSON.put("result", "success");
            poJSON.put("message", "SI Advances Source add record.");
        }
        return poJSON;
    }
    
    public JSONObject openDetail(String fsValue, boolean fbIsTransNo){
        paDetail = new ArrayList<>();
        paRemDetail = new ArrayList<>();
        poJSON = new JSONObject();
        Model_SalesInvoice_Advances_Source loEntity = new Model_SalesInvoice_Advances_Source(poGRider);
        String lsSQL =   loEntity.makeSelectSQL();
        if(fbIsTransNo){
            lsSQL = MiscUtil.addCondition(lsSQL, " sTransNox = " + SQLUtil.toSQL(fsValue));
        } else {
            lsSQL = MiscUtil.addCondition(lsSQL, " sReferNox = " + SQLUtil.toSQL(fsValue))
                                                    + "  ORDER BY nEntryNox ASC " ;
        }
        System.out.println(lsSQL);
        ResultSet loRS = poGRider.executeQuery(lsSQL);
        
        try {
            int lnctr = 0;
            if (MiscUtil.RecordCount(loRS) > 0) {
                while(loRS.next()){
                        paDetail.add(new Model_SalesInvoice_Advances_Source(poGRider));
                        paDetail.get(paDetail.size() - 1).openRecord(loRS.getString("sTransNox"));
                        
                        pnEditMode = EditMode.UPDATE;
                        lnctr++;
                        poJSON.put("result", "success");
                        poJSON.put("message", "Record loaded successfully.");
                    } 
                
            }else{
//                paDetail = new ArrayList<>();
//                addDetail(fsValue);
                poJSON.put("result", "error");
                poJSON.put("continue", true);
                poJSON.put("message", "No record selected.");
            }
            MiscUtil.close(loRS);
        } catch (SQLException e) {
            poJSON.put("result", "error");
            poJSON.put("message", e.getMessage());
        }
        return poJSON;
    }
    
    public JSONObject saveDetail(String fsTransNo){
        JSONObject obj = new JSONObject();
        
        int lnCtr;
        if(paRemDetail != null){
            int lnRemSize = paRemDetail.size() -1;
            if(lnRemSize >= 0){
                for(lnCtr = 0; lnCtr <= lnRemSize; lnCtr++){
//                    obj = paRemDetail.get(lnCtr).deleteRecord();
                    if("error".equals((String) obj.get("result"))){
                        return obj;
                    }
                }
            }
        }
        
        if(paDetail == null){
            obj.put("result", "error");
            obj.put("continue", true);
            return obj;
        }
        
        int lnSize = paDetail.size() -1;
        if(lnSize < 0){
            obj.put("result", "error");
            obj.put("continue", true);
            return obj;
        }
        
        for (lnCtr = 0; lnCtr <= lnSize; lnCtr++){
            if(lnCtr>0){
                if(paDetail.get(lnCtr).getReferNo().isEmpty()){
                    continue; //skip, instead of removing the actual detail
//                    paDetail.remove(lnCtr);
//                    lnCtr++;
//                    if(lnCtr > lnSize){
//                        break;
//                    } 
                }
            }
            
            paDetail.get(lnCtr).setReferNo(fsTransNo);
//            paDetail.get(lnCtr).setTargetBranchCd(psTargetBranchCd);
            
            ValidatorInterface validator = ValidatorFactory.make(ValidatorFactory.TYPE.SalesInvoice_Advances_Source, paDetail.get(lnCtr));
            validator.setGRider(poGRider);
            if (!validator.isEntryOkay()){
                obj.put("result", "error");
                obj.put("message", validator.getMessage());
                return obj;
            }
            obj = paDetail.get(lnCtr).saveRecord();
        }    
        
        return obj;
    }
    public void setTargetBranchCd(String fsBranchCd){
        psTargetBranchCd = fsBranchCd;
    }
    
    public Object removeDetail(int fnRow){
        JSONObject loJSON = new JSONObject();
        
        if(paDetail.get(fnRow).getEntryNo() != null){
            if(paDetail.get(fnRow).getEntryNo() != 0){
                RemoveDetail(fnRow);
            }
        }
        
        paDetail.remove(fnRow);
        return loJSON;
    }
    
    private JSONObject RemoveDetail(Integer fnRow){
        
        if(paRemDetail == null){
           paRemDetail = new ArrayList<>();
        }
        
        poJSON = new JSONObject();
        if (paRemDetail.size()<=0){
            paRemDetail.add(new Model_SalesInvoice_Advances_Source(poGRider));
            paRemDetail.get(0).openRecord(paDetail.get(fnRow).getTransNo());
            poJSON.put("result", "success");
            poJSON.put("message", "added to remove record.");
        } else {
            paRemDetail.add(new Model_SalesInvoice_Advances_Source(poGRider));
            paRemDetail.get(paRemDetail.size()-1).openRecord(paDetail.get(fnRow).getTransNo());
            poJSON.put("result", "success");
            poJSON.put("message", "added to remove record.");
        }
        return poJSON;
    }
    
    public ArrayList<Model_SalesInvoice_Advances_Source> getDetailList(){
        if(paDetail == null){
           paDetail = new ArrayList<>();
        }
        return paDetail;
    }
    public void setDetailList(ArrayList<Model_SalesInvoice_Advances_Source> foObj){this.paDetail = foObj;}
    
    public Object getDetail(int fnRow, int fnIndex){return paDetail.get(fnRow).getValue(fnIndex);}
    public Object getDetail(int fnRow, String fsIndex){return paDetail.get(fnRow).getValue(fsIndex);}
    
    @Override
    public JSONObject setDetail(int fnRow, int fnIndex, Object foValue){ 
        return paDetail.get(fnRow).setValue(fnIndex, foValue);
    }
    
    @Override
    public JSONObject setDetail(int fnRow, String fsIndex, Object foValue){ 
        return paDetail.get(fnRow).setValue(fsIndex, foValue);
    }

    @Override
    public JSONObject searchDetail(int i, String string, String string1, boolean bln) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public JSONObject searchDetail(int i, int i1, String string, boolean bln) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public JSONObject newTransaction() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public JSONObject openTransaction(String string) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public JSONObject updateTransaction() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public JSONObject saveTransaction() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
    public JSONObject cancelTransaction(String string) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
    public Object getMasterModel() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public JSONObject setMaster(int i, Object o) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public JSONObject setMaster(String string, Object o) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setTransactionStatus(String string) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public JSONObject checkSIAdvancesExist(String fsSITranNo){
        JSONObject loJSON = new JSONObject();
        try {
            String lsID = "";
            Model_SalesInvoice_Advances_Source loEntity = new Model_SalesInvoice_Advances_Source(poGRider);
            String lsSQL = loEntity.getSQL();
            //Validate exisiting VSI Number
            lsSQL = MiscUtil.addCondition(lsSQL, " a.sReferNox = " + SQLUtil.toSQL(fsSITranNo));
            System.out.println("EXISTING SI ADVANCES CHECK: " + lsSQL);
            ResultSet loRS = poGRider.executeQuery(lsSQL);
            if (MiscUtil.RecordCount(loRS) > 0){
                    while(loRS.next()){
                        lsID = loRS.getString("sTransNox");
                    }

                    MiscUtil.close(loRS);
                    loJSON.put("result", "success");
                    loJSON.put("sTransNox", lsID);
                    return loJSON;
            } 
        
        } catch (SQLException ex) {
            Logger.getLogger(SalesInvoice_Advances_Source.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return loJSON;
    }
    
}
