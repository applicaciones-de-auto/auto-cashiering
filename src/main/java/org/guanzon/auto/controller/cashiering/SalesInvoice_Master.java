/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.guanzon.auto.controller.cashiering;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.guanzon.appdriver.agent.ShowDialogFX;
import org.guanzon.appdriver.base.GRider;
import org.guanzon.appdriver.base.MiscUtil;
import org.guanzon.appdriver.base.SQLUtil;
import org.guanzon.appdriver.constant.EditMode;
import org.guanzon.appdriver.constant.RecordStatus;
import org.guanzon.appdriver.constant.TransactionStatus;
import org.guanzon.appdriver.iface.GTransaction;
import org.guanzon.auto.general.CancelForm;
import org.guanzon.auto.general.SearchDialog;
import org.guanzon.auto.general.TransactionStatusHistory;
import org.guanzon.auto.model.cashiering.Model_SalesInvoice_Master;
import org.guanzon.auto.model.sales.Model_VehicleDeliveryReceipt_Master;
import org.guanzon.auto.validator.cashiering.ValidatorFactory;
import org.guanzon.auto.validator.cashiering.ValidatorInterface;
import org.json.simple.JSONObject;

/**
 *
 * @author Arsiela
 */
public class SalesInvoice_Master implements GTransaction {
    final String XML = "Model_SalesInvoice_Master.xml";
    GRider poGRider;
    String psBranchCd;
    String psTargetBranchCd;
    boolean pbWtParent;
    int pnEditMode;
    String psTransStat;
    
    String psMessagex;
    public JSONObject poJSON;
    
    Model_SalesInvoice_Master poModel;

    public SalesInvoice_Master(GRider foGRider, boolean fbWthParent, String fsBranchCd) {
        poGRider = foGRider;
        pbWtParent = fbWthParent;
        psBranchCd = fsBranchCd.isEmpty() ? foGRider.getBranchCode() : fsBranchCd;
        
        poModel = new Model_SalesInvoice_Master(foGRider);
        pnEditMode = EditMode.UNKNOWN;
    }

    
    @Override
    public int getEditMode() {
        return pnEditMode;
    }
    
    @Override
    public Model_SalesInvoice_Master getMasterModel() {
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

            poModel = new Model_SalesInvoice_Master(poGRider);
            Connection loConn = null;
            loConn = setConnection();

            poModel.newRecord();
            poModel.setTransNo(MiscUtil.getNextCode(poModel.getTable(), "sTransNox", true, poGRider.getConnection(), poGRider.getBranchCode()+"S"));
//            poModel.setReferNo(MiscUtil.getNextCode(poModel.getTable(), "sReferNox", true, poGRider.getConnection(), poGRider.getBranchCode()));
            poModel.setPrinted("0");
            poModel.setBranchCd(poGRider.getBranchCode());
            
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
        
        poModel = new Model_SalesInvoice_Master(poGRider);
        poJSON = poModel.openRecord(fsValue);
        
        return poJSON;
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
        
        ValidatorInterface validator = ValidatorFactory.make( ValidatorFactory.TYPE.SalesInvoice_Master, poModel);
        validator.setGRider(poGRider);
        if (!validator.isEntryOkay()){
            poJSON.put("result", "error");
            poJSON.put("message", validator.getMessage());
            return poJSON;
        }
        
        poJSON = poModel.saveRecord();
        if("error".equalsIgnoreCase((String)poJSON.get("result"))){
            if (!pbWtParent) poGRider.rollbackTrans();
            return poJSON;
        } 
        
        return poJSON;
    }
    
    public JSONObject savePrinted(boolean fsIsValidate, String fsFormType){
        JSONObject loJSON = new JSONObject();
        String lsOrigPrint = poModel.getPrinted();
        poModel.setPrinted("1"); //Set to Printed
        if(fsIsValidate){
            ValidatorInterface validator = ValidatorFactory.make( ValidatorFactory.TYPE.SalesInvoice_Master, poModel);
            validator.setGRider(poGRider);
            if (!validator.isEntryOkay()){
                poModel.setPrinted(lsOrigPrint); //Revert to Previous Value
                poJSON.put("result", "error");
                poJSON.put("message", validator.getMessage());
                return poJSON;
            }
        } else {
            loJSON = saveTransaction();
            if(!"error".equals((String) loJSON.get("result"))){
                TransactionStatusHistory loEntity = new TransactionStatusHistory(poGRider);
                loJSON = loEntity.updateStatusHistory(poModel.getTransNo(), poModel.getTable(), fsFormType, "5", "PRINT"); //5 = STATE_PRINTED
                if("error".equals((String) loJSON.get("result"))){
                    return loJSON;
                }
            }
        }
        return loJSON;
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
        poJSON = new JSONObject();

        if (poModel.getEditMode() == EditMode.UPDATE) {
            try {
                poModel.setTranStat(TransactionStatus.STATE_CANCELLED);
                
                ValidatorInterface validator = ValidatorFactory.make( ValidatorFactory.TYPE.SalesInvoice_Master, poModel);
                validator.setGRider(poGRider);
                if (!validator.isEntryOkay()){
                    poJSON.put("result", "error");
                    poJSON.put("message", validator.getMessage());
                    return poJSON;
                } 

                CancelForm cancelform = new CancelForm();
//                if (!cancelform.loadCancelWindow(poGRider, poModel.getReferNo(), poModel.getTransNo(), "VSI")) { 
                if (!cancelform.loadCancelWindow(poGRider, poModel.getTransNo(), poModel.getTable())) { 
                    poJSON.put("result", "error");
                    poJSON.put("message", "Cancellation failed.");
                    return poJSON;
                } 

                poJSON = poModel.saveRecord();
                if ("success".equals((String) poJSON.get("result"))) {
                } else {
                    poJSON.put("result", "error");
                    poJSON.put("message", "Cancellation failed.");
                }
            } catch (SQLException ex) {
                Logger.getLogger(SalesInvoice_Master.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            poJSON = new JSONObject();
            poJSON.put("result", "error");
            poJSON.put("message", "No transaction loaded to update.");
        }
        return poJSON;
    }
    
    /**
     * Search SI Transaction
     * @param fsValue Reference No
     * @param fsReceiptType Document Type
     * @return 
     */
    public JSONObject searchReceipt(String fsValue, String fsReceiptType) {
        String lsHeader = "SI Date»SI No»Customer»Address»Status"; 
        String lsColName = "dTransact»sReferNox»sBuyCltNm»sAddressx»sTranStat"; 
        String lsSQL = poModel.getSQL();
        
        lsSQL = MiscUtil.addCondition(lsSQL, " a.cDocTypex = " + SQLUtil.toSQL(fsReceiptType));
//                                             + " AND a.sTransNox NOT IN (SELECT si_master_source.sReferNox FROM si_master_source WHERE si_master_source.sSourceCD = 'VSI') ");
        System.out.println(lsSQL);
        JSONObject loJSON = SearchDialog.jsonSearch(
                    poGRider,
                    lsSQL,
                    "",
                    lsHeader,
                    lsColName,
                "0.1D»0.2D»0.4D»0.4D»0.2D", 
                    "SALES INVOICE",
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
    
    /**
     * Search SI Transaction
     * @param fsValue Reference No
     * @return 
     */
    public JSONObject searchTransaction(String fsValue) {
        String lsHeader = "SI Date»SI No»Customer»Address»Status"; 
        String lsColName = "dTransact»sReferNox»sBuyCltNm»sAddressx»sTranStat"; 
        String lsSQL = poModel.getSQL();
        
        lsSQL = MiscUtil.addCondition(lsSQL, " a.cDocTypex = '0' "); //FOR VSI
//        lsSQL = MiscUtil.addCondition(lsSQL, " a.sTransNox IN (SELECT si_master_source.sReferNox FROM si_master_source WHERE si_master_source.sSourceCD = 'VSI') ");
        System.out.println(lsSQL);
        JSONObject loJSON = SearchDialog.jsonSearch(
                    poGRider,
                    lsSQL,
                    "",
                    lsHeader,
                    lsColName,
                "0.1D»0.2D»0.4D»0.4D»0.2D", 
                    "VSI",
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
    
    public JSONObject searchVDR(String fsValue, boolean fbByCode, String fsClientType) {
        JSONObject loJSON = new JSONObject();  
        Model_VehicleDeliveryReceipt_Master loEntity = new Model_VehicleDeliveryReceipt_Master(poGRider);
        String lsSQL = loEntity.getSQL();
        String lsTransNo = "sReferNox";
        if(fbByCode){
            lsTransNo = "sTransNox";
        }
        String lsHeader = "VDR No»Customer Name»Address";
        String lsColName = lsTransNo+"»sBuyCltNm»sAddressx"; 
        String lsCriteria = "a."+lsTransNo+"»b.sCompnyNm»" 
                            + "IFNULL(CONCAT( IFNULL(CONCAT(d.sHouseNox,' ') , ''), "                      
                            + " 	IFNULL(CONCAT(d.sAddressx,' ') , ''),  "                                     
                            + " 	IFNULL(CONCAT(e.sBrgyName,' '), ''),   "                                     
                            + " 	IFNULL(CONCAT(f.sTownName, ', '),''),  "                                     
                            + " 	IFNULL(CONCAT(g.sProvName),'') )	, '')";
        
        if(fbByCode){
            lsSQL = MiscUtil.addCondition(lsSQL,  " a.cTranStat <> " + TransactionStatus.STATE_CANCELLED 
                                                + " AND a.sTransNox = " + SQLUtil.toSQL(fsValue)
                                                + " AND a.cCustType = " + SQLUtil.toSQL(fsClientType)
                                                + " GROUP BY a.sTransNox ");
        } else {
            lsSQL = MiscUtil.addCondition(lsSQL,  " a.cTranStat <> " + TransactionStatus.STATE_CANCELLED 
                                                + " AND b.sCompnyNm LIKE " + SQLUtil.toSQL(fsValue + "%")
                                                + " AND a.cCustType = " + SQLUtil.toSQL(fsClientType)
                                                + " GROUP BY a.sTransNox ");
        }
        
        System.out.println("SEARCH VDR: " + lsSQL);
        loJSON = ShowDialogFX.Search(poGRider,
                lsSQL,
                fsValue,
                    lsHeader,
                    lsColName,
                    lsCriteria,
                fbByCode ? 0 : 1);

        if (loJSON != null) {
        } else {
            loJSON = new JSONObject();
            loJSON.put("result", "error");
            loJSON.put("message", "No record loaded.");
            return loJSON;
        }
        return loJSON;
    }
    
    public BigDecimal checkPaidAmt(String fsTransNo, String fsTransType){
        BigDecimal ldblPaidAmt = new BigDecimal("0.00");
        String lsSQL = " SELECT IFNULL(SUM(b.nTranAmtx),0.00) AS nTranAmtx " +
                       " FROM si_master a " +
                       " LEFT JOIN si_master_source b ON b.sReferNox = a.sTransNox ";
        lsSQL = MiscUtil.addCondition(lsSQL, " b.sSourceNo = " + SQLUtil.toSQL(fsTransNo)
                                                +" AND a.sTransNox <> " + SQLUtil.toSQL(poModel.getTransNo())
                                                +" AND a.cTranStat <> " + SQLUtil.toSQL(TransactionStatus.STATE_CANCELLED)) ;
        
        if(!fsTransType.trim().isEmpty()){
            lsSQL = lsSQL + " AND b.sTranType = " + SQLUtil.toSQL(fsTransType);
        }
        
        System.out.println("EXISTING VSI NO CHECK: " + lsSQL);
        ResultSet loRS = poGRider.executeQuery(lsSQL);

        if (MiscUtil.RecordCount(loRS) > 0){
            try {
                while(loRS.next()){
                    ldblPaidAmt = new BigDecimal(loRS.getString("nTranAmtx"));
                }

                MiscUtil.close(loRS);
            } catch (SQLException ex) {
                Logger.getLogger(SalesInvoice_Master.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return ldblPaidAmt;
    }
}
