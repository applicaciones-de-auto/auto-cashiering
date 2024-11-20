/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.guanzon.auto.controller.cashiering;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.guanzon.appdriver.base.GRider;
import org.guanzon.appdriver.base.MiscUtil;
import org.guanzon.appdriver.base.SQLUtil;
import org.guanzon.appdriver.constant.EditMode;
import org.guanzon.appdriver.constant.TransactionStatus;
import org.guanzon.appdriver.iface.GTranDet;
import org.guanzon.appdriver.iface.GTransaction;
import org.guanzon.auto.general.CancelForm;
import org.guanzon.auto.general.SearchDialog;
import org.guanzon.auto.model.cashiering.Model_Creditcard_Trans;
import org.guanzon.auto.model.cashiering.Model_Creditcard_Trans;
import org.guanzon.auto.model.sales.Model_VehicleDeliveryReceipt_Master;
import org.guanzon.auto.validator.cashiering.ValidatorFactory;
import org.guanzon.auto.validator.cashiering.ValidatorInterface;
import org.json.simple.JSONObject;

/**
 *
 * @author Arsiela
 */
public class Credicard_Trans implements GTransaction{
    GRider poGRider;
    String psBranchCd;
    boolean pbWtParent;
    int pnEditMode;
    String psTransStat;
    
    String psMessagex;
    public JSONObject poJSON;
    
    Model_Creditcard_Trans poModel;
    ArrayList<Model_Creditcard_Trans> paDetail;
    
    public Credicard_Trans(GRider foGRider, boolean fbWthParent, String fsBranchCd) {
        poGRider = foGRider;
        pbWtParent = fbWthParent;
        psBranchCd = fsBranchCd.isEmpty() ? foGRider.getBranchCode() : fsBranchCd;

        poModel = new Model_Creditcard_Trans(foGRider);
        pnEditMode = EditMode.UNKNOWN;
    }
    
    @Override
    public int getEditMode() {
        return pnEditMode;
    }
    
    @Override
    public Model_Creditcard_Trans getMasterModel() {
        return poModel;
    }
    
    @Override
    public JSONObject setMaster(int fnCol, Object foData) {
        JSONObject obj = new JSONObject();
        obj.put("pnEditMode", pnEditMode);
        if (pnEditMode != EditMode.UNKNOWN){
            // Don't allow specific fields to assign values
            if(!(fnCol == poModel.getColumn("sTransNox") ||
                fnCol == poModel.getColumn("cTranStat") ||
                fnCol == poModel.getColumn("sEntryByx") ||
                fnCol == poModel.getColumn("dEntryDte") ||
                fnCol == poModel.getColumn("sModified") ||
                fnCol == poModel.getColumn("dModified"))){
                poModel.setValue(fnCol, foData);
                obj.put(fnCol, pnEditMode);
            }
        }
        return obj;
    }

    @Override
    public JSONObject setMaster(String fsCol, Object foData) {
        return setMaster(poModel.getColumn(fsCol), foData);
    }
    
    public Object getMaster(int fnCol) {
        if(pnEditMode == EditMode.UNKNOWN)
            return null;
        else 
            return poModel.getValue(fnCol);
    }

    public Object getMaster(String fsCol) {
        return getMaster(poModel.getColumn(fsCol));
    }
    
    @Override
    public JSONObject newTransaction() {
        poJSON = new JSONObject();
        try{
            pnEditMode = EditMode.ADDNEW;
            org.json.simple.JSONObject obj;

            poModel = new Model_Creditcard_Trans(poGRider);
            Connection loConn = null;
            loConn = setConnection();

            poModel.setTransNo(MiscUtil.getNextCode(poModel.getTable(), "sTransNox", true, poGRider.getConnection(), poGRider.getBranchCode()));
            poModel.newRecord();
            
            if (poModel == null){
                poJSON.put("result", "error");
                poJSON.put("message", "initialized new record failed.");
                return poJSON;
            }else{
                poJSON.put("result", "success");
                poJSON.put("message", "initialized new record.");
                pnEditMode = EditMode.ADDNEW;
            }
        }catch(NullPointerException e){
            poJSON.put("result", "error");
            poJSON.put("message", e.getMessage());
        }
        
        return poJSON;
    }
    
    private Connection setConnection(){
        Connection foConn;
        if (pbWtParent){
            foConn = (Connection) poGRider.getConnection();
            if (foConn == null) foConn = (Connection) poGRider.doConnect();
        }else foConn = (Connection) poGRider.doConnect();
        return foConn;
    }

    @Override
    public JSONObject openTransaction(String fsValue) {
        pnEditMode = EditMode.READY;
        poJSON = new JSONObject();
        
        poModel = new Model_Creditcard_Trans(poGRider);
        poJSON = poModel.openRecord(fsValue);
        
        return poJSON;
    }
    
    private JSONObject checkData(JSONObject joValue){
        if(pnEditMode == EditMode.READY || pnEditMode == EditMode.UPDATE){
            if(joValue.containsKey("continue")){
                if(true == (boolean)joValue.get("continue")){
                    joValue.put("result", "success");
                    joValue.put("message", "Record saved successfully.");
                }
            }
        }
        return joValue;
    }

    @Override
    public JSONObject updateTransaction() {
        poJSON = new JSONObject();
        if (pnEditMode != EditMode.READY && pnEditMode != EditMode.UPDATE){
            poJSON.put("result", "error");
            poJSON.put("message", "Invalid edit mode.");
            return poJSON;
        }
        
        pnEditMode = EditMode.UPDATE;
        poJSON.put("result", "success");
        poJSON.put("message", "Update mode success.");
        return poJSON;
    }

    @Override
    public JSONObject saveTransaction() {
        poJSON = new JSONObject();  
        
        ValidatorInterface validator = ValidatorFactory.make( ValidatorFactory.TYPE.Credicard_Trans, poModel);
        validator.setGRider(poGRider);
        if (!validator.isEntryOkay()){
            poJSON.put("result", "error");
            poJSON.put("message", "Error while saving Creditcard Transaction.\n\n" + validator.getMessage());
            return poJSON;
        }
        
        if (!pbWtParent) poGRider.beginTrans();
        
        poJSON =  poModel.saveRecord();
        if("error".equalsIgnoreCase((String) poJSON.get("result"))){
            if (!pbWtParent) poGRider.rollbackTrans();
            return checkData(poJSON);
        } 
        
        if (!pbWtParent) poGRider.commitTrans();
        
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
    public JSONObject cancelTransaction(String fsTransNox) {
        poJSON = new JSONObject();

        if (poModel.getEditMode() == EditMode.READY
                || poModel.getEditMode() == EditMode.UPDATE) {
            try {
                poJSON = poModel.setTranStat(TransactionStatus.STATE_CANCELLED);
                if ("error".equals((String) poJSON.get("result"))) {
                    return poJSON;
                }
                
                ValidatorInterface validator = ValidatorFactory.make( ValidatorFactory.TYPE.Credicard_Trans, poModel);
                validator.setGRider(poGRider);
                if (!validator.isEntryOkay()){
                    poJSON.put("result", "error");
                    poJSON.put("message", validator.getMessage());
                    return poJSON;
                }
                
                CancelForm cancelform = new CancelForm();
//                if (!cancelform.loadCancelWindow(poGRider, poModel.getTransNo(), poModel.getTransNo(),"SOA")) { 
                if (!cancelform.loadCancelWindow(poGRider, poModel.getTransNo(), poModel.getTable())) { 
                    poJSON.put("result", "error");
                    poJSON.put("message", "Cancellation failed.");
                    return poJSON;
                }
                
                poJSON = poModel.saveRecord();
            } catch (SQLException ex) {
                Logger.getLogger(StatementOfAccount_Master.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            poJSON = new JSONObject();
            poJSON.put("result", "error");
            poJSON.put("message", "No record loaded to update.");
        }
        return poJSON;
    }
    
    
    public JSONObject searchTransaction(String fsValue, boolean fbByCode) {
        String lsHeader = "Card Number»Approve No»Bank Name";
        String lsColName = "sCardNoxx»sApprovNo»sPayerNme";
        String lsSQL = poModel.getSQL();
        System.out.println(lsSQL);
        JSONObject loJSON = SearchDialog.jsonSearch(
                    poGRider,
                    lsSQL,
                    "",
                    lsHeader,
                    lsColName,
                "0.1D»0.2D»0.3D", 
                    "CREDIT CARD",
                    0);
            
        if (loJSON != null && !"error".equals((String) loJSON.get("result"))) {
        }else {
            loJSON = new JSONObject();
            loJSON.put("result", "error");
            loJSON.put("message", "No Transaction loaded.");
            return loJSON;
        }
        return loJSON;
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
    public void setTransactionStatus(String string) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
     public ArrayList<Model_Creditcard_Trans> getDetailList(){
        if(paDetail == null){
           paDetail = new ArrayList<>();
        }
        return paDetail;
    }
     
    public void setDetailList(ArrayList<Model_Creditcard_Trans> foObj){this.paDetail = foObj;}
    
    public void setDetail(int fnRow, int fnIndex, Object foValue){ paDetail.get(fnRow).setValue(fnIndex, foValue);}
    public void setDetail(int fnRow, String fsIndex, Object foValue){ paDetail.get(fnRow).setValue(fsIndex, foValue);}
    public Object getDetail(int fnRow, int fnIndex){return paDetail.get(fnRow).getValue(fnIndex);}
    public Object getDetail(int fnRow, String fsIndex){return paDetail.get(fnRow).getValue(fsIndex);}
    
    public Model_Creditcard_Trans getDetailModel(int fnRow) {
        return paDetail.get(fnRow);
    }
    
    public JSONObject loadTransaction(String fsFrom, String fsTo){
        paDetail = new ArrayList<>();
        poJSON = new JSONObject();
        String lsSQL = MiscUtil.addCondition(poModel.getSQL(), " DATE(a.dTransact) >= " + SQLUtil.toSQL(fsFrom)
                                                + " AND DATE(a.dTransact) <= " + SQLUtil.toSQL(fsTo)
                                                + " GROUP BY a.sTransNox "
                                                + " ORDER BY a.dTransact ASC "
                                                );
                
        System.out.println(lsSQL);
        ResultSet loRS = poGRider.executeQuery(lsSQL);
        
       try {
            int lnctr = 0;
            if (MiscUtil.RecordCount(loRS) > 0) {
                while(loRS.next()){
                        paDetail.add(new Model_Creditcard_Trans(poGRider));
                        paDetail.get(paDetail.size() - 1).openRecord( loRS.getString("sTransNox"));
                        
                        pnEditMode = EditMode.UPDATE;
                        lnctr++;
                        poJSON.put("result", "success");
                        poJSON.put("message", "Record loaded successfully.");
                    } 
                
            }else{
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
    
}

//implements GTranDet {
//    final String XML = "Model_Creditcard_Trans.xml";
//    GRider poGRider;
//    String psTargetBranchCd = "";
//    boolean pbWtParent;
//    
//    int pnEditMode;
//    String psMessagex;
//    public JSONObject poJSON;
//    
//    ArrayList<Model_Creditcard_Trans> paDetail;
//    ArrayList<Model_Creditcard_Trans> paRemDetail;
//
//    public Credicard_Trans(GRider foAppDrver){
//        poGRider = foAppDrver;
//    }
//    
//    @Override
//    public int getEditMode() {
//        return pnEditMode;
//    }
//    
//    @Override
//    public int getItemCount() {
//        if(paDetail == null){
//           paDetail = new ArrayList<>();
//        }
//        return paDetail.size();
//    }
//
//    @Override
//    public Model_Creditcard_Trans getDetailModel(int fnRow) {
//        return paDetail.get(fnRow);
//    }
//    
//    public JSONObject addDetail(String fsTransNo){
//        if(paDetail == null){
//           paDetail = new ArrayList<>();
//        }
//        
//        poJSON = new JSONObject();
//        if (paDetail.size()<=0){
//            paDetail.add(new Model_Creditcard_Trans(poGRider));
//            paDetail.get(0).newRecord();
//            paDetail.get(0).setTransNo(fsTransNo);
////            paDetail.get(0).setEntryNo(0);
//            
//            poJSON.put("result", "success");
//            poJSON.put("message", "Credicard add record.");
//        } else {
//            paDetail.add(new Model_Creditcard_Trans(poGRider));
//            paDetail.get(paDetail.size()-1).newRecord();
//            paDetail.get(paDetail.size()-1).setTransNo(fsTransNo);
////            paDetail.get(paDetail.size()-1).setEntryNo(0);
//            poJSON.put("result", "success");
//            poJSON.put("message", "Credicard add record.");
//        }
//        return poJSON;
//    }
//    
//    public JSONObject openDetail(String fsValue){
//        paDetail = new ArrayList<>();
//        paRemDetail = new ArrayList<>();
//        poJSON = new JSONObject();
//        Model_Creditcard_Trans loEntity = new Model_Creditcard_Trans(poGRider);
//        String lsSQL =   loEntity.makeSelectSQL();
//        lsSQL = MiscUtil.addCondition(lsSQL, " sTransNox = " + SQLUtil.toSQL(fsValue));
////                                                + "  ORDER BY nEntryNox ASC " ;
//        System.out.println(lsSQL);
//        ResultSet loRS = poGRider.executeQuery(lsSQL);
//        
//        try {
//            int lnctr = 0;
//            if (MiscUtil.RecordCount(loRS) > 0) {
//                while(loRS.next()){
//                        paDetail.add(new Model_Creditcard_Trans(poGRider));
//                        paDetail.get(paDetail.size() - 1).openRecord(loRS.getString("sTransNox"));
//                        
//                        pnEditMode = EditMode.UPDATE;
//                        lnctr++;
//                        poJSON.put("result", "success");
//                        poJSON.put("message", "Record loaded successfully.");
//                    } 
//                
//            }else{
////                paDetail = new ArrayList<>();
////                addDetail(fsValue);
//                poJSON.put("result", "error");
//                poJSON.put("message", "No record selected.");
//            }
//            MiscUtil.close(loRS);
//        } catch (SQLException e) {
//            poJSON.put("result", "error");
//            poJSON.put("message", e.getMessage());
//        }
//        return poJSON;
//    }
//    
//    public JSONObject saveDetail(String fsTransNo){
//        JSONObject obj = new JSONObject();
//        
//        int lnCtr;
////        if(paRemDetail != null){
////            int lnRemSize = paRemDetail.size() -1;
////            if(lnRemSize >= 0){
////                for(lnCtr = 0; lnCtr <= lnRemSize; lnCtr++){
////                    obj = paRemDetail.get(lnCtr).deleteRecord();
////                    if("error".equals((String) obj.get("result"))){
////                        return obj;
////                    }
////                }
////            }
////        }
//        
//        if(paDetail == null){
//            obj.put("result", "error");
//            obj.put("continue", true);
//            return obj;
//        }
//        
//        int lnSize = paDetail.size() -1;
//        if(lnSize < 0){
//            obj.put("result", "error");
//            obj.put("continue", true);
//            return obj;
//        }
//        
//        for (lnCtr = 0; lnCtr <= lnSize; lnCtr++){
//            if(lnCtr>0){
//                if(paDetail.get(lnCtr).getTransNo().isEmpty()){
//                    continue; //skip, instead of removing the actual detail
////                    paDetail.remove(lnCtr);
////                    lnCtr++;
////                    if(lnCtr > lnSize){
////                        break;
////                    } 
//                }
//            }
//            
//            paDetail.get(lnCtr).setTransNo(fsTransNo);
////            paDetail.get(lnCtr).setEntryNo(lnCtr+1);
////            paDetail.get(lnCtr).setTargetBranchCd(psTargetBranchCd);
//            
//            ValidatorInterface validator = ValidatorFactory.make(ValidatorFactory.TYPE.SalesInvoice_Payment, paDetail.get(lnCtr));
//            validator.setGRider(poGRider);
//            if (!validator.isEntryOkay()){
//                obj.put("result", "error");
//                obj.put("message", validator.getMessage());
//                return obj;
//            }
//            obj = paDetail.get(lnCtr).saveRecord();
//        }    
//        
//        return obj;
//    }
//    public void setTargetBranchCd(String fsBranchCd){
//        psTargetBranchCd = fsBranchCd;
//    }
//    
//    public Object removeDetail(int fnRow){
//        JSONObject loJSON = new JSONObject();
//        
//        if(paDetail.get(fnRow).getModifiedBy() != null){
//            if(paDetail.get(fnRow).getModifiedBy().trim().isEmpty()){ // != 0
//                RemoveDetail(fnRow);
//            }
//        } else {
//            RemoveDetail(fnRow);
//        }
//        
//        paDetail.remove(fnRow);
//        return loJSON;
//    }
//    
//    private JSONObject RemoveDetail(Integer fnRow){
//        
//        if(paRemDetail == null){
//           paRemDetail = new ArrayList<>();
//        }
//        
//        poJSON = new JSONObject();
//        if (paRemDetail.size()<=0){
//            paRemDetail.add(new Model_Creditcard_Trans(poGRider));
//            paRemDetail.get(0).openRecord(paDetail.get(fnRow).getTransNo());
//            poJSON.put("result", "success");
//            poJSON.put("message", "added to remove record.");
//        } else {
//            paRemDetail.add(new Model_Creditcard_Trans(poGRider));
//            paRemDetail.get(paRemDetail.size()-1).openRecord(paDetail.get(fnRow).getTransNo());
//            poJSON.put("result", "success");
//            poJSON.put("message", "added to remove record.");
//        }
//        return poJSON;
//    }
//    
//    public ArrayList<Model_Creditcard_Trans> getDetailList(){
//        if(paDetail == null){
//           paDetail = new ArrayList<>();
//        }
//        return paDetail;
//    }
//    public void setDetailList(ArrayList<Model_Creditcard_Trans> foObj){this.paDetail = foObj;}
//    
//    public Object getDetail(int fnRow, int fnIndex){return paDetail.get(fnRow).getValue(fnIndex);}
//    public Object getDetail(int fnRow, String fsIndex){return paDetail.get(fnRow).getValue(fsIndex);}
//    
//    @Override
//    public JSONObject setDetail(int fnRow, int fnIndex, Object foValue){ 
//        return paDetail.get(fnRow).setValue(fnIndex, foValue);
//    }
//    
//    @Override
//    public JSONObject setDetail(int fnRow, String fsIndex, Object foValue){ 
//        return paDetail.get(fnRow).setValue(fsIndex, foValue);
//    }
//
//    @Override
//    public JSONObject searchDetail(int i, String string, String string1, boolean bln) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }
//
//    @Override
//    public JSONObject searchDetail(int i, int i1, String string, boolean bln) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }
//
//    @Override
//    public JSONObject newTransaction() {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }
//
//    @Override
//    public JSONObject openTransaction(String string) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }
//
//    @Override
//    public JSONObject updateTransaction() {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }
//
//    @Override
//    public JSONObject saveTransaction() {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }
//
//    @Override
//    public JSONObject deleteTransaction(String string) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }
//
//    @Override
//    public JSONObject closeTransaction(String string) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }
//
//    @Override
//    public JSONObject postTransaction(String string) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }
//
//    @Override
//    public JSONObject voidTransaction(String string) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }
//
//    @Override
//    public JSONObject cancelTransaction(String string) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }
//
//    @Override
//    public JSONObject searchWithCondition(String string) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }
//
//    @Override
//    public JSONObject searchTransaction(String string, String string1, boolean bln) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }
//
//    @Override
//    public JSONObject searchMaster(String string, String string1, boolean bln) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }
//
//    @Override
//    public JSONObject searchMaster(int i, String string, boolean bln) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }
//
//    @Override
//    public Object getMasterModel() {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }
//
//    @Override
//    public JSONObject setMaster(int i, Object o) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }
//
//    @Override
//    public JSONObject setMaster(String string, Object o) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }
//
//    @Override
//    public void setTransactionStatus(String string) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }
//    
//
//} 