/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.guanzon.auto.controller.cashiering;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import org.guanzon.appdriver.base.GRider;
import org.guanzon.appdriver.base.MiscUtil;
import org.guanzon.appdriver.base.SQLUtil;
import org.guanzon.appdriver.constant.EditMode;
import org.guanzon.appdriver.iface.GTranDet;
import org.guanzon.auto.model.cashiering.Model_VehicleSalesInvoice;
import org.guanzon.auto.validator.cashiering.ValidatorFactory;
import org.guanzon.auto.validator.cashiering.ValidatorInterface;
import org.json.simple.JSONObject;

/**
 *
 * @author Arsiela
 */
public class VehicleSalesInvoice_Source {
    final String XML = "Model_VehicleSalesInvoice.xml";
    GRider poGRider;
    String psTargetBranchCd = "";
    boolean pbWtParent;
    
    int pnEditMode;
    String psMessagex;
    public JSONObject poJSON;
    
    ArrayList<Model_VehicleSalesInvoice> paDetail;
    ArrayList<Model_VehicleSalesInvoice> paRemDetail;

    public VehicleSalesInvoice_Source(GRider foAppDrver){
        poGRider = foAppDrver;
    }
    
    public int getEditMode() {
        return pnEditMode;
    }
    
    public JSONObject addDetail(String fsTransNo){
//        if(paDetail == null){
           paDetail = new ArrayList<>();
//        }
        
        poJSON = new JSONObject();
        paDetail.add(new Model_VehicleSalesInvoice(poGRider));
        paDetail.get(0).newRecord();

        paDetail.get(0).setValue("sTransNox", fsTransNo);
        poJSON.put("result", "success");
        poJSON.put("message", "VSP Finance add record.");
        return poJSON;
    }
    
    public JSONObject openDetail(String fsValue){
        paDetail = new ArrayList<>();
        paRemDetail = new ArrayList<>();
        poJSON = new JSONObject();
        Model_VehicleSalesInvoice loEntity = new Model_VehicleSalesInvoice(poGRider);
        String lsSQL = loEntity.makeSelectSQL();
        lsSQL = MiscUtil.addCondition(lsSQL, " sTransNox = " + SQLUtil.toSQL(fsValue));
        System.out.println(lsSQL);
        ResultSet loRS = poGRider.executeQuery(lsSQL);
       try {
            int lnctr = 0;
            if (MiscUtil.RecordCount(loRS) > 0) {
                paDetail = new ArrayList<>();
                while(loRS.next()){
                    paDetail.add(new Model_VehicleSalesInvoice(poGRider));
                    paDetail.get(paDetail.size() - 1).openRecord(loRS.getString("sTransNox"), 1 ); //fixed 1 row

                    pnEditMode = EditMode.UPDATE;
                    lnctr++;
                    poJSON.put("result", "success");
                    poJSON.put("message", "Record loaded successfully.");
                } 
                
                System.out.println("lnctr = " + lnctr);
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
    
    public JSONObject saveDetail(String fsTransNo){
        JSONObject obj = new JSONObject();
        
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
        
        if(psTargetBranchCd == null){
            obj.put("result", "error");
            obj.put("continue", false);
            obj.put("message", "Target Branch code cannot be empty.");
            return obj;
        } else {
            if(psTargetBranchCd.isEmpty()){
                obj.put("result", "error");
                obj.put("continue", false);
                obj.put("message", "Target Branch code cannot be empty.");
                return obj;
            }
        }
        
        //for (lnCtr = 0; lnCtr <= lnSize; lnCtr++){
//            if(paDetail.get(0).getTransNo().isEmpty()){
//                //continue; //skip, instead of removing the actual detail
//                return obj;
//            }
            
            paDetail.get(0).setTransNo(fsTransNo);
            paDetail.get(0).setEntryNo(1); //fixed 1 row for vehicle sales invoice
            paDetail.get(0).setTargetBranchCd(psTargetBranchCd);
            
            ValidatorInterface validator = ValidatorFactory.make(ValidatorFactory.TYPE.VehicleSalesInvoice, paDetail.get(0));
            validator.setGRider(poGRider);
            if (!validator.isEntryOkay()){
                obj.put("result", "error");
                obj.put("message", validator.getMessage());
                return obj;
            }
            obj = paDetail.get(0).saveRecord();
        //}    
        
        return obj;
    }
    
    public void setTargetBranchCd(String fsBranchCd){
        psTargetBranchCd = fsBranchCd; 
    }
    
    public ArrayList<Model_VehicleSalesInvoice> getDetailList(){
        if(paDetail == null){
           paDetail = new ArrayList<>();
        }
        return paDetail;
    }
    
    public Model_VehicleSalesInvoice getDetailModel() {
        return paDetail.get(0);
    }
    
//    public Object removeDetail(int fnRow){
//        JSONObject loJSON = new JSONObject();
//        
//        if(paDetail.get(fnRow).getEditMode() == EditMode.UPDATE){ //getTransNo()!= null
//           // if(paDetail.get(fnRow).getTransNo().trim().isEmpty()){
//                RemoveDetail(fnRow);
//          //  }
//        }
//        
//        paDetail.remove(fnRow);
//        return loJSON;
//    }
    
//    private JSONObject RemoveDetail(Integer fnRow){
//        
////        if(paRemDetail == null){
//           paRemDetail = new ArrayList<>();
////        }
//        
//        poJSON = new JSONObject();
////        if (paRemDetail.size()<=0){
//            paRemDetail.add(new Model_VehicleSalesInvoice(poGRider));
//            paRemDetail.get(0).openRecord(paDetail.get(fnRow).getTransNo());
//            poJSON.put("result", "success");
//            poJSON.put("message", "added to remove record.");
////        } 
////        else {
////            paRemDetail.add(new Model_VehicleSalesInvoice(poGRider));
////            paRemDetail.get(paRemDetail.size()-1).openRecord(paDetail.get(fnRow).getTransNo());
////            poJSON.put("result", "success");
////            poJSON.put("message", "added to remove record.");
////        }
//        return poJSON;
//    }

//    @Override
//    public int getItemCount() {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }
//
//    @Override
//    public Object getDetailModel(int i) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }
//
//    @Override
//    public JSONObject setDetail(int i, int i1, Object o) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }
//
//    @Override
//    public JSONObject setDetail(int i, String string, Object o) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
    
}
