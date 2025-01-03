/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.guanzon.auto.main.cashiering;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import org.guanzon.appdriver.base.GRider;
import org.guanzon.appdriver.base.MiscUtil;
import org.guanzon.appdriver.base.SQLUtil;
import org.guanzon.appdriver.constant.EditMode;
import org.guanzon.appdriver.iface.GTransaction;
import org.guanzon.auto.controller.cashiering.CashierReceivables_Detail;
import org.guanzon.auto.controller.cashiering.CashierReceivables_Master;
import org.guanzon.auto.main.insurance.InsurancePolicyApplication;
import org.guanzon.auto.main.sales.Inquiry;
import org.guanzon.auto.main.sales.VehicleSalesProposal;
import org.guanzon.auto.resultSet2XML.cashiering.CashierReceivablesMaster;
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
    ArrayList<CashierReceivables_Master> paDetail;
    
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
        
        poJSON = poDetail.openDetail(fsValue);
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
    
    public CashierReceivables_Detail getDetailModel() {
        return poDetail;
    }
    
    public ArrayList getDetailList(){return poDetail.getDetailList();}
    
    @Override
    public void setTransactionStatus(String string) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    
    public JSONObject loadTransaction(String fsFrom, String fsTo){
        return poController.loadTransaction(fsFrom, fsTo);
    }
    
    public ArrayList getMasterList(){return poController.getDetailList();}
    
    public JSONObject loadReceipts(){
        return poController.loadReceipts();
    }
    
    public ArrayList getReceiptList(){return poController.getReceiptList();}
    
    /**
     * Check Existing CAR
     * @param fsTransSource the Transaction source
     * @param fsTransCode the primary sTransNox of a transaction
     * @return 
     */
    public JSONObject generateCAR(String fsTransSource, String fsTransCode){
        JSONObject loJSON = new JSONObject();
        switch(fsTransSource){
            case "VSP"://source group: VEHICLE SALES
                /*Note for VEHICLE SALES
                VEHICLE SALES transactions were grouped into
                1. Charged to Customer
                2. Charged to Bank -proceeds for Financing and Purchase Order payment mode */
//                CashierReceivablesVSP loVSP = new CashierReceivablesVSP(poGRider, pbWtParent, psBranchCd);
//                loVSP.setObject(this);
                return generateCARVSP(fsTransCode);
            case "VSA": //source group: VEHICLE SALES
                /*Note for VEHICLE SALES ADVANCES

                Will create a Receivable entry for "approved" Customer Advances for Vehicle Purchase only
                */
                return generateCARVSA(fsTransCode);
            case "POLICY"://source group: INSURANCE SALES
                /*Note for INSURANCE SALES 

                Triggered from Insurance Policy Application WIndow;
                Will create a Receivable entry for Non-vehicle Sales Customer only. Receivable entry for Insurance Policy Application coming from Vehicle Sales 
                will be generated from VSP form.
                */
                return generateCARPolicy(fsTransCode);
        }
        return loJSON;
    }
    
    private JSONObject generateCARVSP(String fsTransCode){
        JSONObject loJSON = new JSONObject();
        //Retrieve VSP 
        VehicleSalesProposal loVSP = new VehicleSalesProposal(poGRider, pbWtParent, psBranchCd);
        loJSON = loVSP.openTransaction(fsTransCode);
        if(!"error".equals((String) loJSON.get("result"))){
            //Re-compute amount
            loVSP.computeAmount();

            loJSON = poController.checkExistingCAR("c","VEHICLE SALES", fsTransCode);
            if(!"success".equals((String) loJSON.get("result"))){
                loJSON = newTransaction();
            } else {
                //If CAR is already exist for the said transaction then retrieve the car to UPDATE
                loJSON = openTransaction((String) loJSON.get("sTransNox"));
            } 

            /*1. CHARGED TO CUSTOMER Transactions */
            if(!"error".equals((String) loJSON.get("result"))){
                poController.getMasterModel().setTransactDte(loVSP.getMasterModel().getMasterModel().getTransactDte());
                poController.getMasterModel().setPayerCde("c");
                poController.getMasterModel().setSourceCD("VEHICLE SALES");
                poController.getMasterModel().setReferNo(fsTransCode);
                poController.getMasterModel().setClientID(loVSP.getMasterModel().getMasterModel().getClientID());
                poController.getMasterModel().setGrossAmt(loVSP.getMasterModel().getMasterModel().getTranTotl()); 
                poController.getMasterModel().setDiscAmt(loVSP.getTotalDiscount());
                poController.getMasterModel().setTotalAmt(loVSP.getMasterModel().getMasterModel().getTranTotl().subtract(loVSP.getTotalDiscount())); 
            }

            //Mandatory delete the CAR detail
            loJSON = poDetail.deleteRecord(poController.getMasterModel().getTransNo());
            if("error".equalsIgnoreCase((String)checkData(loJSON).get("result"))){
                return checkData(poJSON);
            }

            //Proceed to save CAR Detail
            String lsModelDesc = "NO MODEL DESCRIPTION YET";
            String lsVhclCSPlateNo = "NO PLATE OR CS NUMBER YET";
            if(loVSP.getMasterModel().getMasterModel().getSerialID() != null){
                if(!loVSP.getMasterModel().getMasterModel().getSerialID().trim().isEmpty()){
                    lsModelDesc = loVSP.getMasterModel().getMasterModel().getVhclFDsc();
                    lsVhclCSPlateNo = loVSP.getMasterModel().getMasterModel().getPlateNo();
                    if(loVSP.getMasterModel().getMasterModel().getPlateNo() == null){
                        lsVhclCSPlateNo = loVSP.getMasterModel().getMasterModel().getCSNo();
                    } else {
                        if(loVSP.getMasterModel().getMasterModel().getPlateNo().trim().isEmpty()){
                            lsVhclCSPlateNo = loVSP.getMasterModel().getMasterModel().getCSNo();
                        }
                    }
                }
            }
            
            
            BigDecimal ldblTransTotl = loVSP.getMasterModel().getMasterModel().getTranTotl().subtract(loVSP.getMasterModel().getMasterModel().getTPLAmt()).subtract(loVSP.getMasterModel().getMasterModel().getCompAmt())
                                        .subtract(loVSP.getMasterModel().getMasterModel().getLTOAmt()).subtract(loVSP.getMasterModel().getMasterModel().getChmoAmt()).subtract(loVSP.getMasterModel().getMasterModel().getLaborAmt())
                                        .subtract(loVSP.getMasterModel().getMasterModel().getAccesAmt()).subtract(loVSP.getMasterModel().getMasterModel().getFrgtChrg()).subtract(loVSP.getMasterModel().getMasterModel().getOthrChrg())
                                        .subtract(loVSP.getMasterModel().getMasterModel().getAdvDwPmt());
            
            BigDecimal ldblDiscount = loVSP.getMasterModel().getMasterModel().getPromoDsc().add(loVSP.getMasterModel().getMasterModel().getBndleDsc())
                                    .add(loVSP.getMasterModel().getMasterModel().getFleetDsc()).add(loVSP.getMasterModel().getMasterModel().getSPFltDsc())
                                    .add(loVSP.getMasterModel().getMasterModel().getAddlDsc());//TODO Add insurance discount

            //IDENTIFIED new and used vehicle sales 
            poDetail.addDetail(poController.getMasterModel().getTransNo());
            poDetail.getDetailModel(poDetail.getDetailList().size()-1).setGrossAmt(ldblTransTotl); //loVSP.getMasterModel().getMasterModel().getTranTotl()
            poDetail.getDetailModel(poDetail.getDetailList().size()-1).setDiscAmt(ldblDiscount);
            poDetail.getDetailModel(poDetail.getDetailList().size()-1).setTotalAmt(ldblTransTotl.subtract(ldblDiscount));
            if(loVSP.getMasterModel().getMasterModel().getIsVhclNw().equals("0")){ 
                //BRAND NEW : UNIT -NEW VEHICLE SALES
                poDetail.getDetailModel(poDetail.getDetailList().size()-1).setTranType("UNIT -NEW VEHICLE SALES"); //Account title
//                poDetail.getDetailModel(poDetail.getDetailList().size()-1).setTranType(lsVhclCSPlateNo + lsModelDesc);//Particulars
            } else {
               //PRE OWNED :  UNIT -USED VEHICLE SALES
                poDetail.getDetailModel(poDetail.getDetailList().size()-1).setTranType("UNIT -USED VEHICLE SALES");//Account title
//                poDetail.getDetailModel(poDetail.getDetailList().size()-1).setTranType(lsVhclCSPlateNo + lsModelDesc);//Particulars
            }

            //OMA&CMF amt has to be paid
            //TODO

            //ins TPL is c/o dealer
            if(loVSP.getMasterModel().getMasterModel().getTPLStat().equals("3")){
                poDetail.addDetail(poController.getMasterModel().getTransNo());
                poDetail.getDetailModel(poDetail.getDetailList().size()-1).setTranType("INSURANCE"); //Account title
//                poDetail.getDetailModel(poDetail.getDetailList().size()-1).setTranType("INSURANCE TPL");//Particulars
                poDetail.getDetailModel(poDetail.getDetailList().size()-1).setGrossAmt(loVSP.getMasterModel().getMasterModel().getTPLAmt());
                poDetail.getDetailModel(poDetail.getDetailList().size()-1).setDiscAmt(new BigDecimal(0.00));
                poDetail.getDetailModel(poDetail.getDetailList().size()-1).setTotalAmt(loVSP.getMasterModel().getMasterModel().getTPLAmt());
            }
            //ins COMPREHENSIVE is c/o dealer
            if(loVSP.getMasterModel().getMasterModel().getCompStat().equals("3")){
                poDetail.addDetail(poController.getMasterModel().getTransNo());
                poDetail.getDetailModel(poDetail.getDetailList().size()-1).setTranType("INSURANCE"); //Account title
//                poDetail.getDetailModel(poDetail.getDetailList().size()-1).setTranType("INSURANCE COMPREHENSIVE");//Particulars
                poDetail.getDetailModel(poDetail.getDetailList().size()-1).setGrossAmt(loVSP.getMasterModel().getMasterModel().getCompAmt());
                poDetail.getDetailModel(poDetail.getDetailList().size()-1).setDiscAmt(new BigDecimal(0.00));
                poDetail.getDetailModel(poDetail.getDetailList().size()-1).setTotalAmt(loVSP.getMasterModel().getMasterModel().getCompAmt());
            }
            //LTO if charge
            if(loVSP.getMasterModel().getMasterModel().getLTOStat().equals("2")){
                poDetail.addDetail(poController.getMasterModel().getTransNo());
                poDetail.getDetailModel(poDetail.getDetailList().size()-1).setTranType("LTO"); //Account title
//                poDetail.getDetailModel(poDetail.getDetailList().size()-1).setTranType("LTO");//Particulars
                poDetail.getDetailModel(poDetail.getDetailList().size()-1).setGrossAmt(loVSP.getMasterModel().getMasterModel().getLTOAmt());
                poDetail.getDetailModel(poDetail.getDetailList().size()-1).setDiscAmt(new BigDecimal(0.00));
                poDetail.getDetailModel(poDetail.getDetailList().size()-1).setTotalAmt(loVSP.getMasterModel().getMasterModel().getLTOAmt());
            }
            //CHMO if charge
            if(loVSP.getMasterModel().getMasterModel().getChmoStat().equals("2")){
                poDetail.addDetail(poController.getMasterModel().getTransNo());
//                poDetail.getDetailModel(poDetail.getDetailList().size()-1).setTranType("CHMO"); //Account title CHATTEL MORTGAGE
                poDetail.getDetailModel(poDetail.getDetailList().size()-1).setTranType("CHATTEL MORTGAGE");//Particulars
                poDetail.getDetailModel(poDetail.getDetailList().size()-1).setGrossAmt(loVSP.getMasterModel().getMasterModel().getChmoAmt());
                poDetail.getDetailModel(poDetail.getDetailList().size()-1).setDiscAmt(new BigDecimal(0.00));
                poDetail.getDetailModel(poDetail.getDetailList().size()-1).setTotalAmt(loVSP.getMasterModel().getMasterModel().getChmoAmt());
            }

            /*VSP LABOR DETAILS*/
//            String lsLabor = "";
//            String lsOtherLabor = "";
//            BigDecimal ldblOtherLaborAmt = new BigDecimal("0.00");
//            BigDecimal ldblOtherLaborDsc = new BigDecimal("0.00");
//            BigDecimal ldblOtherLaborTtl = new BigDecimal("0.00");
//            for(int lnCtr = 0;lnCtr <= loVSP.getVSPLaborList().size()-1;lnCtr++){
//                switch(loVSP.getVSPLaborModel().getDetailModel(lnCtr).getLaborDsc().replace(" ", "").toUpperCase()){
//                    case "PERMASHINE":
//                        //LABOR : PERMASHINE if charge
//                    case "RUSTPROOF":
//                        //LABOR : RUSTPROOF if charge
//                    case "UNDERCOAT":
//                        //LABOR : UNDERCOAT if charge
//                    case "TINT":
//                        //LABOR : TINT if charge
//                        lsLabor = loVSP.getVSPLaborModel().getDetailModel(lnCtr).getLaborDsc();
//                    break;
//                    default:
//                        lsLabor = "";
//                        lsOtherLabor = "LABOR";
//                        ldblOtherLaborAmt = ldblOtherLaborAmt.add(loVSP.getVSPLaborModel().getDetailModel(lnCtr).getLaborAmt());
//                        ldblOtherLaborDsc = ldblOtherLaborDsc.add(loVSP.getVSPLaborModel().getDetailModel(lnCtr).getLaborDscount());
//                        ldblOtherLaborTtl = ldblOtherLaborTtl.add(loVSP.getVSPLaborModel().getDetailModel(lnCtr).getNtLabAmt());
//                    break;
//                }
//                if(!lsLabor.isEmpty()){
//                    poDetail.addDetail(poController.getMasterModel().getTransNo());
//                    poDetail.getDetailModel(poDetail.getDetailList().size()-1).setTranType(lsLabor);
////                    poDetail.getDetailModel(poDetail.getDetailList().size()-1).setTranType(loVSP.getVSPLaborModel().getDetailModel(lnCtr).getLaborDsc());//"LABOR" +  //Account title UNIT -LABOR
//    //                            poDetail.getDetailModel(poDetail.getDetailList().size()-1).setTranType("PERMASHINE");//Particulars}
//                    poDetail.getDetailModel(poDetail.getDetailList().size()-1).setGrossAmt(loVSP.getVSPLaborModel().getDetailModel(lnCtr).getLaborAmt());
//                    poDetail.getDetailModel(poDetail.getDetailList().size()-1).setDiscAmt(loVSP.getVSPLaborModel().getDetailModel(lnCtr).getLaborDscount());
//                    poDetail.getDetailModel(poDetail.getDetailList().size()-1).setTotalAmt(loVSP.getVSPLaborModel().getDetailModel(lnCtr).getNtLabAmt());
//                }
//            } 

//            if(!lsOtherLabor.isEmpty()){
//                poDetail.addDetail(poController.getMasterModel().getTransNo());
//                poDetail.getDetailModel(poDetail.getDetailList().size()-1).setTranType("UNIT -LABOR"); //Account title UNIT -LABOR
//                poDetail.getDetailModel(poDetail.getDetailList().size()-1).setTranType("LABOR"); //Account title UNIT -LABOR
//                            poDetail.getDetailModel(poDetail.getDetailList().size()-1).setTranType("LABOR");//Particulars}
//                poDetail.getDetailModel(poDetail.getDetailList().size()-1).setGrossAmt(ldblOtherLaborAmt);
//                poDetail.getDetailModel(poDetail.getDetailList().size()-1).setDiscAmt(ldblOtherLaborDsc);
//                poDetail.getDetailModel(poDetail.getDetailList().size()-1).setTotalAmt(ldblOtherLaborTtl);
//            }
            
            /*VSP LABOR DETAILS*/
            if(loVSP.getMasterModel().getMasterModel().getLaborAmt().compareTo(new BigDecimal("0.00")) > 0 ){
                poDetail.addDetail(poController.getMasterModel().getTransNo());
                poDetail.getDetailModel(poDetail.getDetailList().size()-1).setTranType("UNIT -LABOR"); //Account title UNIT -LABOR
    //                poDetail.getDetailModel(poDetail.getDetailList().size()-1).setTranType("LABOR"); //Account title UNIT -LABOR
    //                            poDetail.getDetailModel(poDetail.getDetailList().size()-1).setTranType("LABOR");//Particulars}
                poDetail.getDetailModel(poDetail.getDetailList().size()-1).setGrossAmt(loVSP.getMasterModel().getMasterModel().getLaborAmt());
                poDetail.getDetailModel(poDetail.getDetailList().size()-1).setDiscAmt(loVSP.getMasterModel().getMasterModel().getToLabDsc());
                poDetail.getDetailModel(poDetail.getDetailList().size()-1).setTotalAmt(loVSP.getMasterModel().getMasterModel().getLaborAmt().subtract(loVSP.getMasterModel().getMasterModel().getToLabDsc()));
            }
            
            /*VSP PARTS DETAILS*/
//            String lsPartDescConcat = "";
//            for(int lnCtr = 0;lnCtr <= loVSP.getVSPPartsList().size()-1;lnCtr++){
////                            if(loVSP.getVSPPartsModel().getDetailModel(lnCtr).getChrgeTyp().equals("2")){
//                    lsPartDescConcat = lsPartDescConcat + ", " + loVSP.getVSPPartsModel().getDetailModel(lnCtr).getPartDesc();
////                            }
//            }

            if(loVSP.getMasterModel().getMasterModel().getAccesAmt().compareTo(new BigDecimal("0.00")) > 0 ){
                poDetail.addDetail(poController.getMasterModel().getTransNo());
                poDetail.getDetailModel(poDetail.getDetailList().size()-1).setTranType("UNIT -PARTS AND ACCESSORIES"); //Account title UNIT -PARTS AND ACCESSORIES
//                poDetail.getDetailModel(poDetail.getDetailList().size()-1).setTranType("ACCESSORIES"); //Account title UNIT -PARTS AND ACCESSORIES
//                            poDetail.getDetailModel(poDetail.getDetailList().size()-1).setTranType(lsPartDescConcat);//Particulars
                poDetail.getDetailModel(poDetail.getDetailList().size()-1).setGrossAmt(loVSP.getMasterModel().getMasterModel().getAccesAmt());
                poDetail.getDetailModel(poDetail.getDetailList().size()-1).setDiscAmt(loVSP.getMasterModel().getMasterModel().getToPrtDsc());
                poDetail.getDetailModel(poDetail.getDetailList().size()-1).setTotalAmt(loVSP.getMasterModel().getMasterModel().getAccesAmt().subtract(loVSP.getMasterModel().getMasterModel().getToPrtDsc()));
            }

            //other misc amt has to be paid
            if(loVSP.getMasterModel().getMasterModel().getOthrChrg().compareTo(new BigDecimal("0.00")) > 0 ){
                poDetail.addDetail(poController.getMasterModel().getTransNo());
                poDetail.getDetailModel(poDetail.getDetailList().size()-1).setTranType("OTHER SALES"); //Account title
//                            poDetail.getDetailModel(poDetail.getDetailList().size()-1).setTranType(loVSP.getMasterModel().getMasterModel().getOthrDesc());//Particulars
                poDetail.getDetailModel(poDetail.getDetailList().size()-1).setGrossAmt(loVSP.getMasterModel().getMasterModel().getOthrChrg().add(loVSP.getMasterModel().getMasterModel().getAdvDwPmt()));
                poDetail.getDetailModel(poDetail.getDetailList().size()-1).setDiscAmt(new BigDecimal(0.00));
                poDetail.getDetailModel(poDetail.getDetailList().size()-1).setTotalAmt(loVSP.getMasterModel().getMasterModel().getOthrChrg().add(loVSP.getMasterModel().getMasterModel().getAdvDwPmt()));
            }

            //freight amt has to be paid
            if(loVSP.getMasterModel().getMasterModel().getFrgtChrg().compareTo(new BigDecimal("0.00")) > 0 ){
                poDetail.addDetail(poController.getMasterModel().getTransNo());
                poDetail.getDetailModel(poDetail.getDetailList().size()-1).setTranType("FREIGHT AND DELIVERY CHARGE"); //Account title FREIGHT AND DELIVERY CHARGE 
//                poDetail.getDetailModel(poDetail.getDetailList().size()-1).setTranType("FREIGHT"); //Account title FREIGHT AND DELIVERY CHARGE 
//                            poDetail.getDetailModel(poDetail.getDetailList().size()-1).setTranType("FREIGHT");//Particulars
                poDetail.getDetailModel(poDetail.getDetailList().size()-1).setGrossAmt(loVSP.getMasterModel().getMasterModel().getFrgtChrg());
                poDetail.getDetailModel(poDetail.getDetailList().size()-1).setDiscAmt(new BigDecimal(0.00));
                poDetail.getDetailModel(poDetail.getDetailList().size()-1).setTotalAmt(loVSP.getMasterModel().getMasterModel().getFrgtChrg());
            }

            loJSON = saveTransaction();
            if("error".equals((String) loJSON.get("result"))){
                return loJSON;
            }

            /* END OF 1 -CHARGED to Customer */
            poDetail.resetDetail();

            /*2. CHARGED TO BANK Transactions */
            if(loVSP.getVSPFinanceList().size() > 0){
                if(loVSP.getVSPFinanceModel().getVSPFinanceModel().getFinAmt().compareTo(new BigDecimal("0.00")) > 0){
                    loJSON = poController.checkExistingCAR("b","VEHICLE SALES", fsTransCode);
                    //If CAR is already exist for the said transaction then retrieve the car to UPDATE
                    if(!"success".equals((String) loJSON.get("result"))){
                        loJSON = newTransaction();
                    } else {
                        loJSON = openTransaction((String) loJSON.get("sTransNox"));
                    } 

                    if(!"error".equals((String) loJSON.get("result"))){
                        poController.getMasterModel().setTransactDte(loVSP.getMasterModel().getMasterModel().getTransactDte());
                        poController.getMasterModel().setPayerCde("b");
                        poController.getMasterModel().setSourceCD("VEHICLE SALES");
                        poController.getMasterModel().setReferNo(fsTransCode);
                        poController.getMasterModel().setBrBankCd(loVSP.getVSPFinanceModel().getVSPFinanceModel().getBankID());
                        poController.getMasterModel().setGrossAmt(loVSP.getVSPFinanceModel().getVSPFinanceModel().getFinAmt());
                        poController.getMasterModel().setDiscAmt(new BigDecimal("0.00"));
                        poController.getMasterModel().setTotalAmt(loVSP.getVSPFinanceModel().getVSPFinanceModel().getFinAmt());
                    }

                    //Mandatory delete the CAR detail
                    loJSON = poDetail.deleteRecord(poController.getMasterModel().getTransNo());
                    if("error".equalsIgnoreCase((String)checkData(loJSON).get("result"))){
                        if (!pbWtParent) poGRider.rollbackTrans();
                        return checkData(poJSON);
                    }

                    poDetail.addDetail(poController.getMasterModel().getTransNo());
                    if(loVSP.getMasterModel().getMasterModel().getIsVhclNw().equals("0")){ 
                        //BRAND NEW : UNIT -NEW VEHICLE SALES
                        poDetail.getDetailModel(poDetail.getDetailList().size()-1).setTranType("UNIT -NEW VEHICLE SALES"); //Account title :NEWVHCLSALE
//                        poDetail.getDetailModel(poDetail.getDetailList().size()-1).setTranType("NEWPROCEED"); //Account title :NEWVHCLSALE
        //                            poDetail.getDetailModel(poDetail.getDetailList().size()-1).setTranType(lsVhclCSPlateNo + lsModelDesc);//Particulars
                    } else {
                       //PRE OWNED :  UNIT -USED VEHICLE SALES
                        poDetail.getDetailModel(poDetail.getDetailList().size()-1).setTranType("UNIT -USED VEHICLE SALES");//Account title
//                        poDetail.getDetailModel(poDetail.getDetailList().size()-1).setTranType("USEDPROCEED");//Account title
        //                            poDetail.getDetailModel(poDetail.getDetailList().size()-1).setTranType(lsVhclCSPlateNo + lsModelDesc);//Particulars
                    }
                    poDetail.getDetailModel(poDetail.getDetailList().size()-1).setGrossAmt(loVSP.getVSPFinanceModel().getVSPFinanceModel().getFinAmt());
                    poDetail.getDetailModel(poDetail.getDetailList().size()-1).setDiscAmt(new BigDecimal(0.00));
                    poDetail.getDetailModel(poDetail.getDetailList().size()-1).setTotalAmt(loVSP.getVSPFinanceModel().getVSPFinanceModel().getFinAmt());

                    loJSON = saveTransaction();
                    if("error".equals((String) loJSON.get("result"))){
                        return loJSON;
                    }
                    
                    poDetail.resetDetail();
                    
                    //source group: VEHICLE SALES INCENTIVES
                    /*Note for VEHICLE SALES INCENTIVES (separated from VEHICLE SALES group so to differentiate from Proceeds CAR entry which is of similar payer type: Bank)

                    Dealer (DI) and Sales (SI) Incentives gained from VEHICLE SALES
                    1. Charged to Bank -incentives from Bank for Financing and Purchase Order clients 

                    Should be triggered from VSP, remove here once updating of DI and SI rates have been incorporated at VSP window. (review)
                    as_source_form = 'DSI' is quite temporary, did use only to direct the call at this portion of the script, value will be replaced with 'VSP' during saving
                    */

                    //DEALER INCENTIVES
                    if(loVSP.getMasterModel().getMasterModel().getDealrAmt().compareTo(new BigDecimal("0.00")) > 0 || loVSP.getMasterModel().getMasterModel().getSlsInAmt().compareTo(new BigDecimal("0.00")) > 0){
                        loJSON = poController.checkExistingCAR("b","VEHICLE SALES INCENTIVES", fsTransCode);
                        //If CAR is already exist for the said transaction then retrieve the car to UPDATE
                        if(!"success".equals((String) loJSON.get("result"))){
                            loJSON = newTransaction();
                        } else {
                            loJSON = openTransaction((String) loJSON.get("sTransNox"));
                        } 
                        
                        if(!"error".equals((String) loJSON.get("result"))){
                            poController.getMasterModel().setTransactDte(loVSP.getMasterModel().getMasterModel().getTransactDte());
                            poController.getMasterModel().setPayerCde("b");
                            poController.getMasterModel().setSourceCD("VEHICLE SALES INCENTIVES");
                            poController.getMasterModel().setReferNo(fsTransCode);
                            poController.getMasterModel().setBrBankCd(loVSP.getVSPFinanceModel().getVSPFinanceModel().getBankID());
                            poController.getMasterModel().setGrossAmt(loVSP.getMasterModel().getMasterModel().getDealrAmt().add(loVSP.getMasterModel().getMasterModel().getSlsInAmt()));
                            poController.getMasterModel().setDiscAmt(new BigDecimal("0.00"));
                            poController.getMasterModel().setTotalAmt(loVSP.getMasterModel().getMasterModel().getDealrAmt().add(loVSP.getMasterModel().getMasterModel().getSlsInAmt()));
                        }

                        //Mandatory delete the CAR detail
                        loJSON = poDetail.deleteRecord(poController.getMasterModel().getTransNo());
                        if("error".equalsIgnoreCase((String)checkData(loJSON).get("result"))){
                            if (!pbWtParent) poGRider.rollbackTrans();
                            return checkData(poJSON);
                        }
                        
                        if(loVSP.getMasterModel().getMasterModel().getDealrAmt().compareTo(new BigDecimal("0.00")) > 0 ){
                            poDetail.addDetail(poController.getMasterModel().getTransNo());
                            if(loVSP.getMasterModel().getMasterModel().getIsVhclNw().equals("0")){ 
                //                            poDetail.getDetailModel(poDetail.getDetailList().size()-1).setTranType(DEALER INCENTIVE : lsVhclCSPlateNo + lsModelDesc);//Particulars
                            } else {
                //                            poDetail.getDetailModel(poDetail.getDetailList().size()-1).setTranType(DEALER INCENTIVE : lsVhclCSPlateNo + lsModelDesc);//Particulars
                            }
                            poDetail.getDetailModel(poDetail.getDetailList().size()-1).setTranType("DEALER INCENTIVES");//Account title
                            poDetail.getDetailModel(poDetail.getDetailList().size()-1).setGrossAmt(loVSP.getMasterModel().getMasterModel().getDealrAmt());
                            poDetail.getDetailModel(poDetail.getDetailList().size()-1).setDiscAmt(new BigDecimal(0.00));
                            poDetail.getDetailModel(poDetail.getDetailList().size()-1).setTotalAmt(loVSP.getMasterModel().getMasterModel().getDealrAmt());
                        }
                        
                        if(loVSP.getMasterModel().getMasterModel().getSlsInAmt().compareTo(new BigDecimal("0.00")) > 0 ){
                            poDetail.addDetail(poController.getMasterModel().getTransNo());
                            if(loVSP.getMasterModel().getMasterModel().getIsVhclNw().equals("0")){ 
                //                            poDetail.getDetailModel(poDetail.getDetailList().size()-1).setTranType(SALES INCENTIVE : lsVhclCSPlateNo + lsModelDesc);//Particulars
                            } else {
                //                            poDetail.getDetailModel(poDetail.getDetailList().size()-1).setTranType(SALES INCENTIVE : lsVhclCSPlateNo + lsModelDesc);//Particulars
                            }
                            poDetail.getDetailModel(poDetail.getDetailList().size()-1).setTranType("SALES INCENTIVES");//Account title
                            poDetail.getDetailModel(poDetail.getDetailList().size()-1).setGrossAmt(loVSP.getMasterModel().getMasterModel().getSlsInAmt());
                            poDetail.getDetailModel(poDetail.getDetailList().size()-1).setDiscAmt(new BigDecimal(0.00));
                            poDetail.getDetailModel(poDetail.getDetailList().size()-1).setTotalAmt(loVSP.getMasterModel().getMasterModel().getSlsInAmt());
                        }
                        loJSON = saveTransaction();
                        if("error".equals((String) loJSON.get("result"))){
                            return loJSON;
                        }
                    } 
                }
            } else {
                loJSON = poController.checkExistingCAR("b","VEHICLE SALES", fsTransCode);
                //If CAR is already exist for the said transaction then retrieve the car to UPDATE
                if("success".equals((String) loJSON.get("result"))){
                    loJSON = openTransaction((String) loJSON.get("sTransNox"));
                    if(!"error".equals((String) loJSON.get("result"))){
                        //UPDATE EXISTING CAR OF VSP FINANCE ;
                        if(poController.getMasterModel().getRemarks() != null){
                            poController.getMasterModel().setRemarks(poController.getMasterModel().getRemarks() + "; BANK FINANCE AMT WAS CHANGED INTO 0.00");
                        } else {
                            poController.getMasterModel().setRemarks("BANK FINANCE AMT WAS CHANGED INTO 0.00");
                        }
                        //TODO : SET TO CANCEL CAR 

                        loJSON = saveTransaction();
                        if("error".equals((String) loJSON.get("result"))){
                            return loJSON;
                        }
                    }
                } 
                
                if(loVSP.getMasterModel().getMasterModel().getDealrAmt().compareTo(new BigDecimal("0.00")) == 0  
                    && loVSP.getMasterModel().getMasterModel().getSlsInAmt().compareTo(new BigDecimal("0.00")) == 0 ){
                    
                    //VEHICLE SALES INCENTIVES
                    loJSON = poController.checkExistingCAR("b","VEHICLE SALES INCENTIVES", fsTransCode);
                    //If CAR is already exist for the said transaction then retrieve the car to UPDATE
                    if("success".equals((String) loJSON.get("result"))){
                        loJSON = openTransaction((String) loJSON.get("sTransNox"));
                        if(!"error".equals((String) loJSON.get("result"))){
                            //UPDATE EXISTING CAR OF VSP FINANCE ;
                            if(poController.getMasterModel().getRemarks() != null){
                                poController.getMasterModel().setRemarks(poController.getMasterModel().getRemarks() + "; INCENTIVE AMT WAS CHANGED INTO 0.00");
                            } else {
                                poController.getMasterModel().setRemarks("INCENTIVE AMT WAS CHANGED INTO 0.00");
                            }
                            //TODO : SET TO CANCEL CAR 

                            loJSON = saveTransaction();
                            if("error".equals((String) loJSON.get("result"))){
                                return loJSON;
                            }
                        }
                    }
                    
                }
            }/* END OF 2 -CHARGED to BANK */
        
            /*******************************************************************************************/
            /*3. CHARGED TO SUPPLIER Transactions (Other Deduct Amount)*/
             //check for changes in Other Deduct amount (ldbl_recfromotheramt). This column's value can be altered. Cancel existing Cache Receivable entry for Bank if this scenario happens
             
            /* END OF 3 -CHARGED to Supplier */
            /*******************************************************************************************/
           
            
            
        //END OF VSP
        }
        return loJSON;
    }
    
    private JSONObject generateCARVSA(String fsTransCode){
        JSONObject loJSON = new JSONObject();
        //Retrieve Insurance Application
        Inquiry loEntity = new Inquiry(poGRider, pbWtParent, psBranchCd);
        loJSON = loEntity.getReservationModel().openRecord(fsTransCode);
        if(!"error".equals((String) loJSON.get("result"))){
            int lnSize = loEntity.getReservationList().size()-1;
            if(lnSize == 0){
                loJSON = poController.checkExistingCAR("c","VEHICLE SALES", fsTransCode);
                if(!"success".equals((String) loJSON.get("result"))){
                    loJSON = newTransaction();
                } else {
                    //If CAR is already exist for the said transaction then retrieve the car to UPDATE
                    loJSON = openTransaction((String) loJSON.get("sTransNox"));
                } 

                /*1. CHARGED TO CUSTOMER Transactions */
                if(!"error".equals((String) loJSON.get("result"))){
                    poController.getMasterModel().setTransactDte(loEntity.getReservationModel().getDetailModel(lnSize).getApproveDte());
                    poController.getMasterModel().setPayerCde("c");
                    poController.getMasterModel().setSourceCD("VEHICLE SALES");
                    poController.getMasterModel().setReferNo(fsTransCode);
                    poController.getMasterModel().setClientID(loEntity.getReservationModel().getDetailModel(lnSize).getClientID());
                    poController.getMasterModel().setGrossAmt(new BigDecimal(loEntity.getReservationModel().getDetailModel(lnSize).getAmount()));
                    poController.getMasterModel().setDiscAmt(new BigDecimal("0.00")); 
                    poController.getMasterModel().setTotalAmt(new BigDecimal(loEntity.getReservationModel().getDetailModel(lnSize).getAmount()));
                }

                //Mandatory delete the CAR detail
                loJSON = poDetail.deleteRecord(poController.getMasterModel().getTransNo());
                if("error".equalsIgnoreCase((String)checkData(loJSON).get("result"))){
                    return checkData(poJSON);
                }

                BigDecimal ldblDiscount = new BigDecimal("0.00");

                poDetail.addDetail(poController.getMasterModel().getTransNo());
                poDetail.getDetailModel(poDetail.getDetailList().size()-1).setTranType("ADVANCES"); //Account title
    //                poDetail.getDetailModel(poDetail.getDetailList().size()-1).setTranType("INSURANCE TPL");//Particulars
                poDetail.getDetailModel(poDetail.getDetailList().size()-1).setGrossAmt(new BigDecimal(loEntity.getReservationModel().getDetailModel(lnSize).getAmount()));
                poDetail.getDetailModel(poDetail.getDetailList().size()-1).setDiscAmt(new BigDecimal("0.00"));
                poDetail.getDetailModel(poDetail.getDetailList().size()-1).setTotalAmt(new BigDecimal(loEntity.getReservationModel().getDetailModel(lnSize).getAmount()));

                loJSON = saveTransaction();
                if("error".equals((String) loJSON.get("result"))){
                    return loJSON;
                }
            }
        }
        return loJSON;
    }
    
    private JSONObject generateCARPolicy(String fsTransCode){
        JSONObject loJSON = new JSONObject();
        //Retrieve Insurance Application
        InsurancePolicyApplication loEntity = new InsurancePolicyApplication(poGRider, pbWtParent, psBranchCd);
        loJSON = loEntity.openTransaction(fsTransCode);
        if(!"error".equals((String) loJSON.get("result"))){
            
            //DO NOT PROCEED when transaction is related to VSP
            if(loEntity.getMasterModel().getMasterModel().getVSPTrnNo() != null){
                if(!loEntity.getMasterModel().getMasterModel().getVSPTrnNo().trim().isEmpty()){
                    return loJSON;
                }
            }
            
            //Re-compute amount
            loEntity.computeAmount();

            loJSON = poController.checkExistingCAR("i","INSURANCE SALES", fsTransCode);
            if(!"success".equals((String) loJSON.get("result"))){
                loJSON = newTransaction();
            } else {
                //If CAR is already exist for the said transaction then retrieve the car to UPDATE
                loJSON = openTransaction((String) loJSON.get("sTransNox"));
            } 

            /*1. CHARGED TO CUSTOMER Transactions */
            if(!"error".equals((String) loJSON.get("result"))){
                poController.getMasterModel().setTransactDte(loEntity.getMasterModel().getMasterModel().getTransactDte());
                poController.getMasterModel().setPayerCde("i");
                poController.getMasterModel().setSourceCD("INSURANCE SALES");
                poController.getMasterModel().setReferNo(fsTransCode);
                poController.getMasterModel().setBrInsCde(loEntity.getMasterModel().getMasterModel().getBrInsID());
                poController.getMasterModel().setGrossAmt(loEntity.getMasterModel().getMasterModel().getTotalAmt());
//                poController.getMasterModel().setDiscAmt(loEntity.getMasterModel().getMasterModel().getDiscount()); 
                poController.getMasterModel().setTotalAmt(loEntity.getMasterModel().getMasterModel().getTotalAmt());
            }

            //Mandatory delete the CAR detail
            loJSON = poDetail.deleteRecord(poController.getMasterModel().getTransNo());
            if("error".equalsIgnoreCase((String)checkData(loJSON).get("result"))){
                return checkData(poJSON);
            }

            BigDecimal ldblDiscount = new BigDecimal("0.00");
            
            poDetail.addDetail(poController.getMasterModel().getTransNo());
            poDetail.getDetailModel(poDetail.getDetailList().size()-1).setTranType("INSURANCE"); //Account title
//                poDetail.getDetailModel(poDetail.getDetailList().size()-1).setTranType("INSURANCE TPL");//Particulars
            poDetail.getDetailModel(poDetail.getDetailList().size()-1).setGrossAmt(loEntity.getMasterModel().getMasterModel().getTotalAmt());
            poDetail.getDetailModel(poDetail.getDetailList().size()-1).setDiscAmt(new BigDecimal(0.00));
            poDetail.getDetailModel(poDetail.getDetailList().size()-1).setTotalAmt(loEntity.getMasterModel().getMasterModel().getTotalAmt());
            
            loJSON = saveTransaction();
            if("error".equals((String) loJSON.get("result"))){
                return loJSON;
            }
        }
        return loJSON; 
    }
    
}
