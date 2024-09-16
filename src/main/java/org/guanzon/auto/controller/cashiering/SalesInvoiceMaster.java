/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.guanzon.auto.controller.cashiering;

import java.sql.Connection;
import org.guanzon.appdriver.base.GRider;
import org.guanzon.appdriver.base.MiscUtil;
import org.guanzon.appdriver.constant.EditMode;
import org.guanzon.appdriver.iface.GTransaction;
import org.guanzon.auto.model.cashiering.Model_SalesInvoice_Master;
import org.guanzon.auto.validator.cashiering.ValidatorFactory;
import org.guanzon.auto.validator.cashiering.ValidatorInterface;
import org.json.simple.JSONObject;

/**
 *
 * @author Arsiela
 */
public class SalesInvoiceMaster implements GTransaction {
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
//    Model_VehicleSalesProposal_Master poVSPModel;
//    Model_Inquiry_Master poINQModel;

    public SalesInvoiceMaster(GRider foGRider, boolean fbWthParent, String fsBranchCd) {
        poGRider = foGRider;
        pbWtParent = fbWthParent;
        psBranchCd = fsBranchCd.isEmpty() ? foGRider.getBranchCode() : fsBranchCd;
        
        poModel = new Model_SalesInvoice_Master(foGRider);
//        poVSPModel = new Model_VehicleSalesProposal_Master(foGRider);
//        poINQModel = new Model_Inquiry_Master(foGRider);
//        poVHCLModel = new Model_Vehicle_Serial_Master(foGRider);
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

            poModel.setTransNo(MiscUtil.getNextCode(poModel.getTable(), "sTransNox", true, poGRider.getConnection(), poGRider.getBranchCode()+"SI"));
            poModel.setReferNo(MiscUtil.getNextCode(poModel.getTable(), "sReferNox", true, poGRider.getConnection(), poGRider.getBranchCode()));
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
        
        ValidatorInterface validator = ValidatorFactory.make( ValidatorFactory.TYPE.SalesInvoice_Source, poModel);
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
//    
//    /**
//     * Search UDR Transaction
//     * @param fsValue Reference No
//     * @param fIsActive if true search for active else false all transaction
//     * @return 
//     */
//    public JSONObject searchTransaction(String fsValue, boolean fIsActive) {
//        String lsHeader = "UDR Date»UDR No»Customer»CS No»Plate No»Status"; 
//        String lsColName = "dTransact»sReferNox»sBuyCltNm»sCSNoxxxx»sPlateNox»sTranStat"; 
//        String lsSQL = poModel.getSQL();
//        
//        if(fIsActive){
//            lsSQL = MiscUtil.addCondition(lsSQL, " a.sReferNox LIKE " + SQLUtil.toSQL(fsValue + "%") 
//                                                    + " AND a.cTranStat <> " + SQLUtil.toSQL(TransactionStatus.STATE_CANCELLED));
//        } 
//
//        System.out.println(lsSQL);
//        JSONObject loJSON = SearchDialog.jsonSearch(
//                    poGRider,
//                    lsSQL,
//                    "",
//                    lsHeader,
//                    lsColName,
//                "0.1D»0.2D»0.3D»0.2D»0.2D»0.3D", 
//                    "VDR",
//                    0);
//
//        if (loJSON != null && !"error".equals((String) loJSON.get("result"))) {
//        }else {
//            loJSON = new JSONObject();
//            loJSON.put("result", "error");
//            loJSON.put("message", "No Transaction loaded.");
//            return loJSON;
//        }
//
//        return loJSON;
//    }
//    
    
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
    
//    public JSONObject searchVSP(String fsValue, boolean fbByCode) {
//        JSONObject loJSON = new JSONObject();  
//        String lsSQL = poVSPModel.getSQL();
//        String lsTransNo = "sVSPNOxxx";
//        if(fbByCode){
//            lsTransNo = "sTransNox";
//        }
//        String lsHeader = "VSP No»Customer Name";
//        String lsColName = lsTransNo+"»sBuyCltNm"; 
//        String lsCriteria = "h."+lsTransNo+"»b.sCompnyNm»" 
//                            + "IFNULL(CONCAT( IFNULL(CONCAT(d.sHouseNox,' ') , ''), "                      
//                            + " 	IFNULL(CONCAT(d.sAddressx,' ') , ''),  "                                     
//                            + " 	IFNULL(CONCAT(e.sBrgyName,' '), ''),   "                                     
//                            + " 	IFNULL(CONCAT(f.sTownName, ', '),''),  "                                     
//                            + " 	IFNULL(CONCAT(g.sProvName),'') )	, '')";
//        
//        if(fbByCode){
//            lsSQL = MiscUtil.addCondition(lsSQL,  " (a.sSerialID <> NULL OR a.sSerialID <> '') "
//                                                + " AND a.cTranStat = " + TransactionStatus.STATE_CLOSED //APPROVE
//                                                + " AND a.sTransNox = " + SQLUtil.toSQL(fsValue)
//                                                + " GROUP BY a.sTransNox ");
//        } else {
//            lsSQL = MiscUtil.addCondition(lsSQL,  " (a.sSerialID <> NULL OR a.sSerialID <> '') "
//                                                + " AND a.cTranStat = " + TransactionStatus.STATE_CLOSED //APPROVE
//                                                + " AND b.sCompnyNm LIKE " + SQLUtil.toSQL(fsValue + "%")
//                                                + " GROUP BY a.sTransNox ");
//        }
//        
//        System.out.println("SEARCH VSP: " + lsSQL);
//        loJSON = ShowDialogFX.Search(poGRider,
//                lsSQL,
//                fsValue,
//                    lsHeader,
//                    lsColName,
//                    lsCriteria,
//                fbByCode ? 0 : 1);
//
//        if (loJSON != null) {
//        } else {
//            loJSON = new JSONObject();
//            loJSON.put("result", "error");
//            loJSON.put("message", "No record loaded.");
//            return loJSON;
//        }
//        return loJSON;
//    }
    
    
}
