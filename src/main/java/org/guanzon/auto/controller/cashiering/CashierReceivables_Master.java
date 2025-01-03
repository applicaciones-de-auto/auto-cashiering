/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.guanzon.auto.controller.cashiering;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.guanzon.appdriver.agent.ShowDialogFX;
import org.guanzon.appdriver.base.GRider;
import org.guanzon.appdriver.base.MiscUtil;
import org.guanzon.appdriver.base.SQLUtil;
import org.guanzon.appdriver.constant.EditMode;
import org.guanzon.appdriver.constant.TransactionStatus;
import org.guanzon.appdriver.iface.GTransaction;
import org.guanzon.auto.general.CancelForm;
import org.guanzon.auto.general.SearchDialog;
import org.guanzon.auto.model.cashiering.Model_Cashier_Receivables;
import org.guanzon.auto.model.cashiering.Model_SalesInvoice_Master;
import org.guanzon.auto.validator.cashiering.ValidatorFactory;
import org.guanzon.auto.validator.cashiering.ValidatorInterface;
import org.json.simple.JSONObject;

/**
 *
 * @author Arsiela
 */
public class CashierReceivables_Master implements GTransaction{
    GRider poGRider;
    String psBranchCd;
    boolean pbWtParent;
    int pnEditMode;
    String psTransStat;
    
    String psMessagex;
    public JSONObject poJSON;
    
    Model_Cashier_Receivables poModel;
    ArrayList<Model_Cashier_Receivables> paDetail;
    ArrayList<Model_SalesInvoice_Master> paReceipt;
    
    public CashierReceivables_Master(GRider foGRider, boolean fbWthParent, String fsBranchCd) {
        poGRider = foGRider;
        pbWtParent = fbWthParent;
        psBranchCd = fsBranchCd.isEmpty() ? foGRider.getBranchCode() : fsBranchCd;

        poModel = new Model_Cashier_Receivables(foGRider);
        pnEditMode = EditMode.UNKNOWN;
    }
    
    @Override
    public int getEditMode() {
        return pnEditMode;
    }
    
    @Override
    public Model_Cashier_Receivables getMasterModel() {
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

            poModel = new Model_Cashier_Receivables(poGRider);
            Connection loConn = null;
            loConn = setConnection();

            poModel.setTransNo(MiscUtil.getNextCode(poModel.getTable(), "sTransNox", true, poGRider.getConnection(), poGRider.getBranchCode()+"CAR"));
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
        
        poModel = new Model_Cashier_Receivables(poGRider);
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
        
        ValidatorInterface validator = ValidatorFactory.make( ValidatorFactory.TYPE.CashierReceivables_Master, poModel);
        validator.setGRider(poGRider);
        if (!validator.isEntryOkay()){
            poJSON.put("result", "error");
            poJSON.put("message", "Error while saving cashier receivables.\n\n" + validator.getMessage());
            return poJSON;
        }
        
        poJSON =  poModel.saveRecord();
        if("error".equalsIgnoreCase((String) poJSON.get("result"))){
            if (!pbWtParent) poGRider.rollbackTrans();
            return checkData(poJSON);
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
    public JSONObject cancelTransaction(String fsTransNox) {
        poJSON = new JSONObject();

        if (poModel.getEditMode() == EditMode.READY
                || poModel.getEditMode() == EditMode.UPDATE) {
            try {
//                poJSON = poModel.setTranStat(TransactionStatus.STATE_CANCELLED);
//                if ("error".equals((String) poJSON.get("result"))) {
//                    return poJSON;
//                }
                
                ValidatorInterface validator = ValidatorFactory.make( ValidatorFactory.TYPE.CashierReceivables_Master, poModel);
                validator.setGRider(poGRider);
                if (!validator.isEntryOkay()){
                    poJSON.put("result", "error");
                    poJSON.put("message", validator.getMessage());
                    return poJSON;
                }
                
                CancelForm cancelform = new CancelForm();
//                if (!cancelform.loadCancelWindow(poGRider, poModel.getTransNo(), poModel.getReferNo(), "POLICY")) { 
                if (!cancelform.loadCancelWindow(poGRider, poModel.getTransNo(), poModel.getTable())) { 
                    poJSON.put("result", "error");
                    poJSON.put("message", "Cancellation failed.");
                    return poJSON;
                }
                
                poJSON = poModel.saveRecord();
            } catch (SQLException ex) {
                Logger.getLogger(CashierReceivables_Master.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            poJSON = new JSONObject();
            poJSON.put("result", "error");
            poJSON.put("message", "No record loaded to update.");
        }
        return poJSON;
    }
    
    
    public JSONObject searchTransaction(String fsValue, boolean fbByCode) {
        String lsHeader = "CAR Date»CAR No»Payer Name»Payer Address";
        String lsColName = "dTransact»sTransNox»sPayerNme»sPayerAdd";
        String lsSQL = MiscUtil.addCondition(poModel.getSQL(), " IFNULL(p.sVSPNOxxx, IFNULL(q.sReferNox, IFNULL(r.sReferNox, ''))) <>  '' " )
                                                + " GROUP BY a.sTransNox ";
        
        System.out.println(lsSQL);
        JSONObject loJSON = SearchDialog.jsonSearch(
                    poGRider,
                    lsSQL,
                    "",
                    lsHeader,
                    lsColName,
                "0.1D»0.2D»0.3D»0.3D", 
                    "CASHIER RECEIVABLES",
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
    
    /**
     * CAR existence checker
     * @param fsPayerCode the Payer code where the car be paid.
     * @param fsSourceCode the Source code of transaction
     * @param fsFormCode the sTransNox of transaction
     * @return 
     */
    public JSONObject checkExistingCAR(String fsPayerCode, String fsSourceCode, String fsFormCode){
        JSONObject loJSON = new JSONObject();
        try {
            //Do not allow multiple application for insurance application
            String lsID = "";
            String lsDesc = "";
            String lsSQL = poModel.makeSelectSQL();
            lsSQL = MiscUtil.addCondition(lsSQL, " sReferNox = " + SQLUtil.toSQL(fsFormCode)
                                                    + " AND cPayerCde = " + SQLUtil.toSQL(fsPayerCode)
                                                    + " AND sSourceCD = " + SQLUtil.toSQL(fsSourceCode));
            System.out.println("EXISTING CAR CHECK: " + lsSQL);
            ResultSet loRS = poGRider.executeQuery(lsSQL);
            if (MiscUtil.RecordCount(loRS) > 0){
                while(loRS.next()){
                    lsID = loRS.getString("sTransNox");
                    lsDesc = xsDateShort(loRS.getDate("dTransact"));
                }

                MiscUtil.close(loRS);
                loJSON.put("result", "success");
                loJSON.put("sTransNox", lsID);
                return loJSON;
            }
        } catch (SQLException ex) {
            Logger.getLogger(CashierReceivables_Master.class.getName()).log(Level.SEVERE, null, ex);
        }
        return loJSON;
    }
    
    public ArrayList<Model_Cashier_Receivables> getDetailList(){
        if(paDetail == null){
           paDetail = new ArrayList<>();
        }
        return paDetail;
    }
    public void setDetailList(ArrayList<Model_Cashier_Receivables> foObj){this.paDetail = foObj;}
    
    public void setDetail(int fnRow, int fnIndex, Object foValue){ paDetail.get(fnRow).setValue(fnIndex, foValue);}
    public void setDetail(int fnRow, String fsIndex, Object foValue){ paDetail.get(fnRow).setValue(fsIndex, foValue);}
    public Object getDetail(int fnRow, int fnIndex){return paDetail.get(fnRow).getValue(fnIndex);}
    public Object getDetail(int fnRow, String fsIndex){return paDetail.get(fnRow).getValue(fsIndex);}
    
    public Model_Cashier_Receivables getDetailModel(int fnRow) {
        return paDetail.get(fnRow);
    }
    
    public JSONObject loadTransaction(String fsFrom, String fsTo){
        paDetail = new ArrayList<>();
        poJSON = new JSONObject();
        String lsSQL = MiscUtil.addCondition(poModel.getSQL(), " DATE(a.dTransact) >= " + SQLUtil.toSQL(fsFrom)
                                                + " AND DATE(a.dTransact) <= " + SQLUtil.toSQL(fsTo)
                                                + " AND IFNULL(p.sVSPNOxxx, IFNULL(q.sReferNox, IFNULL(r.sReferNox, ''))) <>  '' "
                                                + " GROUP BY a.sTransNox "
                                                + " ORDER BY a.dTransact DESC "
                                                );
                
        System.out.println(lsSQL);
        ResultSet loRS = poGRider.executeQuery(lsSQL);
        
       try {
            int lnctr = 0;
            if (MiscUtil.RecordCount(loRS) > 0) {
                while(loRS.next()){
                        paDetail.add(new Model_Cashier_Receivables(poGRider));
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
    
    
    public ArrayList<Model_SalesInvoice_Master> getReceiptList(){
        if(paReceipt == null){
           paReceipt = new ArrayList<>();
        }
        return paReceipt;
    }
    
    public Model_SalesInvoice_Master getReceiptModel(int fnRow) {
        return paReceipt.get(fnRow);
    }
    
    public JSONObject loadReceipts() {
        JSONObject loJSON = new JSONObject();
        paReceipt = new ArrayList<>();
        String lsSQL = " SELECT "
                       + " a.sTransNox "
                       + " , a.sReferNox "
                       + " , a.dTransact "
                       + " , a.cTranStat "
                       + " , b.sSourceNo "
                       + " FROM si_master a "
                       + " LEFT JOIN si_master_source b ON b.sReferNox = a.sTransNox ";
                
        lsSQL = MiscUtil.addCondition(lsSQL, " b.sSourceNo = " + SQLUtil.toSQL(poModel.getTransNo())
                                                + " AND a.cTranStat <> " + SQLUtil.toSQL(TransactionStatus.STATE_CANCELLED)
                                                + " GROUP BY a.sTransNox "
                                                + " ORDER BY a.dTransact DESC "
                                                );
                
        System.out.println(lsSQL);
        ResultSet loRS = poGRider.executeQuery(lsSQL);
        
       try {
            int lnctr = 0;
            if (MiscUtil.RecordCount(loRS) > 0) {
                while(loRS.next()){
                        paReceipt.add(new Model_SalesInvoice_Master(poGRider));
                        paReceipt.get(paReceipt.size() - 1).openRecord( loRS.getString("sTransNox"));
                        
                        pnEditMode = EditMode.UPDATE;
                        lnctr++;
                        loJSON.put("result", "success");
                        loJSON.put("message", "Record loaded successfully.");
                    } 
                
            }else{
                loJSON.put("result", "error");
                loJSON.put("continue", true);
                loJSON.put("message", "No record selected.");
            }
            MiscUtil.close(loRS);
        } catch (SQLException e) {
            loJSON.put("result", "error");
            loJSON.put("message", e.getMessage());
        }
        
        return loJSON;
    }
    
    private static String xsDateShort(java.util.Date fdValue) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String date = sdf.format(fdValue);
        return date;
    }

    private static String xsDateShort(String fsValue) throws org.json.simple.parser.ParseException, java.text.ParseException {
        SimpleDateFormat fromUser = new SimpleDateFormat("MMMM dd, yyyy");
        SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd");
        String lsResult = "";
        lsResult = myFormat.format(fromUser.parse(fsValue));
        return lsResult;
    }
    
    /*Convert Date to String*/
    private LocalDate strToDate(String val) {
        DateTimeFormatter date_formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate localDate = LocalDate.parse(val, date_formatter);
        return localDate;
    }
    
}
