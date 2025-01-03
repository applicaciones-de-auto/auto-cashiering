/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.guanzon.auto.main.cashiering;

import com.mysql.fabric.xmlrpc.Client;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import static org.guanzon.appdriver.base.CommonUtils.NumberFormat;
import static org.guanzon.appdriver.base.CommonUtils.NumberFormat;
import static org.guanzon.appdriver.base.CommonUtils.NumberFormat;
import org.guanzon.appdriver.base.GRider;
import org.guanzon.appdriver.constant.EditMode;
import org.guanzon.appdriver.constant.TransactionStatus;
import org.guanzon.appdriver.iface.GTransaction;
import org.guanzon.auto.controller.cashiering.CashierReceivables_Master;
import org.guanzon.auto.controller.cashiering.Credicard_Trans;
import org.guanzon.auto.controller.cashiering.Gift_Check;
import org.guanzon.auto.controller.cashiering.SalesInvoice_Advances_Source;
import org.guanzon.auto.controller.cashiering.SalesInvoice_Master;
import org.guanzon.auto.controller.cashiering.SalesInvoice_Payment;
import org.guanzon.auto.controller.cashiering.SalesInvoice_Source;
import org.guanzon.auto.controller.cashiering.StatementOfAccount_Master;
import org.guanzon.auto.controller.clients.Client_Master;
import org.guanzon.auto.controller.clients.Sales_Executive_Master;
import org.guanzon.auto.controller.parameter.Bank_Branches;
import org.guanzon.auto.controller.parameter.Bank_Master;
import org.guanzon.auto.controller.parameter.Insurance_Branches;
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
    
    SalesInvoice_Master poController;
    ArrayList<SalesInvoice_Master> paDetail;
    SalesInvoice_Source poDetail;
    SalesInvoice_Payment poPayment;
    SalesInvoice_Advances_Source poAdvances;
    SalesInvoice_Advances_Source poDeductibles;
//    CashierReceivables_Master poCAR;
    StatementOfAccount_Master poSOA;
    CashierReceivables poCAR;
    Credicard_Trans poCreditcard;
    Gift_Check poGiftCheck;
    
    List<String> psOthPayTransCde = new ArrayList<>();
    List<String> psCARTransCde = new ArrayList<>();
    List<String> psSOATransCde = new ArrayList<>();
    
    public SalesInvoice(GRider foAppDrver, boolean fbWtParent, String fsBranchCd){
        poController = new SalesInvoice_Master(foAppDrver,fbWtParent,fsBranchCd);
        poDetail = new SalesInvoice_Source(foAppDrver);
        poPayment = new SalesInvoice_Payment(foAppDrver);
        poAdvances = new SalesInvoice_Advances_Source(foAppDrver);
        poDeductibles = new SalesInvoice_Advances_Source(foAppDrver);
        poCAR = new CashierReceivables(foAppDrver,fbWtParent,fsBranchCd);
//        poCAR = new CashierReceivables_Master(foAppDrver,fbWtParent,fsBranchCd);
        poSOA = new StatementOfAccount_Master(foAppDrver,fbWtParent,fsBranchCd);
        poCreditcard = new Credicard_Trans(foAppDrver,fbWtParent,fsBranchCd);
        poGiftCheck = new Gift_Check(foAppDrver,fbWtParent,fsBranchCd);
        
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
        
//        poJSON = poAdvances.openDetail(fsValue, false);
//        if(!"success".equals((String) checkData(poJSON).get("result"))){
//            pnEditMode = EditMode.UNKNOWN;
//            return poJSON;
//        }
        
//        poJSON = poDeductibles.openDetail(fsValue);
//        if(!"success".equals((String) checkData(poJSON).get("result"))){
//            pnEditMode = EditMode.UNKNOWN;
//            return poJSON;
//        }
        
        poJSON = poPayment.openDetail(fsValue);
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
        
        poJSON = computeSIAmount(true);
        if("error".equals((String) poJSON.get("result"))){
            return poJSON;
        }
        
        //SAVE OTHER PAYMENT : Credicard, Check, Online Payment, Gift Check
        poJSON = saveOtherPayment();
        if("error".equals((String) poJSON.get("result"))){
            return poJSON;
        }
        
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
        
//        poJSON = populateAdvances((String) poController.getMasterModel().getTransNo());
//        if("error".equalsIgnoreCase((String)checkData(poJSON).get("result"))){
//            if (!pbWtParent) poGRider.rollbackTrans();
//            return checkData(poJSON);
//        }
//        
//        poJSON =  poAdvances.saveDetail((String) poController.getMasterModel().getTransNo());
//        if("error".equalsIgnoreCase((String)checkData(poJSON).get("result"))){
//            if (!pbWtParent) poGRider.rollbackTrans();
//            return checkData(poJSON);
//        }
        
        poJSON =  poPayment.saveDetail((String) poController.getMasterModel().getTransNo());
        if("error".equalsIgnoreCase((String)checkData(poJSON).get("result"))){
            if (!pbWtParent) poGRider.rollbackTrans();
            return checkData(poJSON);
        }
        
        if (!pbWtParent) poGRider.commitTrans();
        
        //UPDATE CAR BALANCE
        JSONObject loJSON = new JSONObject();
        loJSON = computeCARBalance(false);
        if("error".equals((String) loJSON.get("result"))){
            return loJSON;
        }
        
        //UPDATE SOA BALANCE
        loJSON = computeSOABalance(false);
        if("error".equals((String) loJSON.get("result"))){
            return loJSON;
        }
        
        
        return poJSON;
    }
    
    public JSONObject savePrint(boolean fsIsValidate) {
        return poController.savePrinted(fsIsValidate, "SI");
    }
    
    private JSONObject saveOtherPayment(){
        JSONObject loJSON = new JSONObject();
        boolean lbisNew = false;
        for(int lnCtr = 0; lnCtr <= poPayment.getDetailList().size()-1; lnCtr++){
            if(poPayment.getDetailModel(lnCtr).getPayTrnCD() == null){
                lbisNew = true;
            } else {
                if(poPayment.getDetailModel(lnCtr).getPayTrnCD().trim().isEmpty()){
                    lbisNew = true;
                }
            }
            
            switch(poPayment.getDetailModel(lnCtr).getPayMode()){
                case "CARD": //CREDIT CARD
                    if(lbisNew){
                        loJSON = poCreditcard.newTransaction();
                    } else {
                        loJSON = poCreditcard.openTransaction(poPayment.getDetailModel(lnCtr).getPayTrnCD());
                        if(!"error".equals((String) loJSON.get("result"))){
                            loJSON = poCreditcard.updateTransaction();
                            if("error".equals((String) loJSON.get("result"))){
                                return loJSON;
                            }
                        } else {
                            return loJSON;
                        }
                    }
                    
                    if(!"error".equals((String) loJSON.get("result"))){
                        poCreditcard.getMasterModel().setCardNo(poPayment.getDetailModel(lnCtr).getCCCardNo());
                        poCreditcard.getMasterModel().setApprovNo(poPayment.getDetailModel(lnCtr).getCCApprovNo());
                        poCreditcard.getMasterModel().setTraceNo(poPayment.getDetailModel(lnCtr).getCCTraceNo());
                        poCreditcard.getMasterModel().setBankID(poPayment.getDetailModel(lnCtr).getCCBankID());
                        poCreditcard.getMasterModel().setRemarks(poPayment.getDetailModel(lnCtr).getCCRemarks()); 
                        poCreditcard.getMasterModel().setAmount(poPayment.getDetailModel(lnCtr).getPayAmt()); 
                    
                        loJSON = poCreditcard.saveTransaction();
                        if(!"error".equals((String) loJSON.get("result"))){
                            if(lbisNew){
                                poPayment.getDetailModel(lnCtr).setPayTrnCD(poCreditcard.getMasterModel().getTransNo());
                            }
                        }
                    }
                break;
                
                case "GC": //GIFT CHECK
                    if(lbisNew){
                        loJSON = poGiftCheck.newTransaction();
                    } else {
                        loJSON = poGiftCheck.openTransaction(poPayment.getDetailModel(lnCtr).getPayTrnCD());
                        if(!"error".equals((String) loJSON.get("result"))){
                            loJSON = poGiftCheck.updateTransaction();
                            if("error".equals((String) loJSON.get("result"))){
                                return loJSON;
                            }
                        } else {
                            return loJSON;
                        }
                    }
                    
                    if(!"error".equals((String) loJSON.get("result"))){
                        poGiftCheck.getMasterModel().setGCertNo(poPayment.getDetailModel(lnCtr).getGCertNo());
                        poGiftCheck.getMasterModel().setPayLoad(poPayment.getDetailModel(lnCtr).getGCPayLod());
                        poGiftCheck.getMasterModel().setRemarks(poPayment.getDetailModel(lnCtr).getGCRemrks()); 
                        poGiftCheck.getMasterModel().setAmount(poPayment.getDetailModel(lnCtr).getPayAmt()); 
                    
                        loJSON = poGiftCheck.saveTransaction();
                        if(!"error".equals((String) loJSON.get("result"))){
                            if(lbisNew){
                                poPayment.getDetailModel(lnCtr).setPayTrnCD(poGiftCheck.getMasterModel().getTransNo());
                            }
                        }
                    }
                    
                break;
            }
        }
        
        return loJSON;
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
    
    public JSONObject searchTransaction(String fsValue, String fsReceiptType) {
        poJSON = new JSONObject();  
        poJSON = poController.searchReceipt(fsValue, fsReceiptType);
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
        poJSON = poController.cancelTransaction(fsValue);
        if("error".equals((String) poJSON.get("result"))){
            return poJSON;
        }
        
        //UPDATE CAR BALANCE
        JSONObject loJSON = new JSONObject();
        loJSON = computeCARBalance(false);
        if("error".equals((String) loJSON.get("result"))){
            return loJSON;
        }
        
        //UPDATE SOA BALANCE
        loJSON = computeSOABalance(false);
        if("error".equals((String) loJSON.get("result"))){
            return loJSON;
        }
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
    public SalesInvoice_Master getMasterModel() {
        return poController;
    }
    
    public SalesInvoice_Source getSIDetailModel() {return poDetail;}
    public ArrayList getSIDetailList(){return poDetail.getDetailList();}
    public Object addSIDetail(){ return poDetail.addDetail(poController.getMasterModel().getTransNo());}
    public Object removeSIDetail(int fnRow){ return poDetail.removeDetail(fnRow);}
    
    public SalesInvoice_Payment getSIPaymentModel() {return poPayment;}
    public ArrayList getSIPaymentList(){return poPayment.getDetailList();}
    public Object addSIPayment(){ return poPayment.addDetail(poController.getMasterModel().getTransNo());}
    public Object removeSIPayment(int fnRow){ return poPayment.removeDetail(fnRow);}
    
    public SalesInvoice_Advances_Source getSIAdvancesModel() {return poAdvances;}
    public ArrayList getSIAdvancesList(){return poAdvances.getDetailList();}
    public Object addSIAdvances(){ return poAdvances.addDetail(poController.getMasterModel().getTransNo());}
    public Object removeSIAdvances(int fnRow){ return poAdvances.removeDetail(fnRow);}
    
    public SalesInvoice_Advances_Source getSIDeductibleModel() {return poDeductibles;}
    public ArrayList getSIDeductibleList(){return poDeductibles.getDetailList();}
    public Object addSIDeductible(){ return poDeductibles.addDetail(poController.getMasterModel().getTransNo());}
    public Object removeSIDeductible(int fnRow){ return poDeductibles.removeDetail(fnRow);}
    
    @Override
    public void setTransactionStatus(String string) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
//    public JSONObject loadTransaction(String fsFrom, String fsTo){
//        return poController.loadTransaction(fsFrom, fsTo);
//    }
//    
//    public ArrayList getMasterList(){return poController.getDetailList();}
    
    public JSONObject computeSIAmount(boolean fsValidate){
        if(psCARTransCde == null){
            psCARTransCde = new ArrayList();
        }
        if(psSOATransCde == null){
            psSOATransCde = new ArrayList();
        }
        JSONObject loJSON = new JSONObject();
        int lnCtr = 0;
        BigDecimal ldblNetAmt = new BigDecimal("0.00");
        BigDecimal ldblTranTotl = new BigDecimal("0.00");
        BigDecimal ldblAdvPaym = new BigDecimal("0.00");
        BigDecimal ldblDiscount = new BigDecimal("0.00");
        BigDecimal ldblNetTotl = new BigDecimal("0.00");
        
        //Update TOTAL amount's in SI MASTER
        for(lnCtr = 0; lnCtr <= poDetail.getDetailList().size()-1; lnCtr++){
            ldblNetAmt = poDetail.getDetailModel(lnCtr).getTranAmt().subtract(poDetail.getDetailModel(lnCtr).getAdvused()).subtract(poDetail.getDetailModel(lnCtr).getDiscount());
            if(fsValidate){
                if(ldblNetAmt.compareTo(new BigDecimal("0.00")) < 0){
                    loJSON.put("result", "error");
                    loJSON.put("message", "Invalid Net Amount.");
                    return loJSON;
                }
            }
            
            poDetail.getDetailModel(lnCtr).setNetAmt(ldblNetAmt);
            
            ldblTranTotl = ldblTranTotl.add(poDetail.getDetailModel(lnCtr).getTranAmt());
            ldblAdvPaym = ldblAdvPaym.add(poDetail.getDetailModel(lnCtr).getAdvused());
            ldblDiscount = ldblDiscount.add(poDetail.getDetailModel(lnCtr).getDiscount());
            ldblNetTotl = ldblNetTotl.add(poDetail.getDetailModel(lnCtr).getNetAmt());
            
            if(poDetail.getDetailModel(lnCtr).getSourceNo() != null){
                if(!poDetail.getDetailModel(lnCtr).getSourceNo().trim().isEmpty()){
                    //Check when exist in CAR
                    loJSON = poCAR.openTransaction(poDetail.getDetailModel(lnCtr).getSourceNo());
                    if(!"error".equals((String) loJSON.get("result"))){
                        if(!psCARTransCde.contains(poDetail.getDetailModel(lnCtr).getSourceNo())){
                            psCARTransCde.add(poDetail.getDetailModel(lnCtr).getSourceNo());
                        }
                    }

                    //Check when exist in SOA
                    loJSON = poSOA.openTransaction(poDetail.getDetailModel(lnCtr).getSourceNo());
                    if(!"error".equals((String) loJSON.get("result"))){
                        if(!psSOATransCde.contains(poDetail.getDetailModel(lnCtr).getSourceNo())){
                            psSOATransCde.add(poDetail.getDetailModel(lnCtr).getSourceNo());
                        }
                    }
                }
            }
        }
        
        BigDecimal ldblCheck = new BigDecimal("0.00");
        BigDecimal ldblCC = new BigDecimal("0.00");
        BigDecimal ldblGC = new BigDecimal("0.00");
        BigDecimal ldblOtherAmt = new BigDecimal("0.00");
        BigDecimal ldblCashAmt = new BigDecimal("0.00");
        BigDecimal ldblPaidAmt = new BigDecimal("0.00");
        for(lnCtr = 0;lnCtr <= poPayment.getDetailList().size()-1;lnCtr++){
            switch(poPayment.getDetailModel(lnCtr).getPayMode()){
                case "CARD":
                    ldblCC = ldblCC.add(poPayment.getDetailModel(lnCtr).getPayAmt());
                break;
                case "CHECK":
                    ldblCheck = ldblCheck.add(poPayment.getDetailModel(lnCtr).getPayAmt());
                break;
                case "GC":
                    ldblGC = ldblGC.add(poPayment.getDetailModel(lnCtr).getPayAmt());
                break;
                default:
                    ldblOtherAmt = ldblOtherAmt.add(poPayment.getDetailModel(lnCtr).getPayAmt());
            }
        
        }
        
        ldblCashAmt = ldblNetTotl.subtract(ldblCheck).subtract(ldblCC).subtract(ldblGC).subtract(ldblOtherAmt);
        ldblPaidAmt = ldblCashAmt.add(ldblCC).add(ldblGC).add(ldblCheck).add(ldblOtherAmt);
        if(fsValidate){
            if(ldblPaidAmt.compareTo(ldblNetTotl) > 0){
                loJSON.put("result", "error");
                loJSON.put("message", "Invalid Total Paid Amount.");
                return loJSON;
            }

            if(ldblCashAmt.compareTo(new BigDecimal("0.00")) < 0){
                loJSON.put("result", "error");
                loJSON.put("message", "Invalid Total Cash Amount.");
                return loJSON;
            }
        }
        
        poController.getMasterModel().setCardAmt(ldblCC);
        poController.getMasterModel().setChckAmt(ldblCheck);
        poController.getMasterModel().setGiftAmt(ldblGC);
        poController.getMasterModel().setOthrAmt(ldblOtherAmt);
        poController.getMasterModel().setCashAmt(ldblCashAmt);
        poController.getMasterModel().setAmtPaid(ldblPaidAmt);
        poController.getMasterModel().setTranTotl(ldblTranTotl);
        poController.getMasterModel().setAdvPaym(ldblAdvPaym);
        poController.getMasterModel().setDiscount(ldblDiscount);
        poController.getMasterModel().setNetTotal(ldblNetTotl);
        
        loJSON = computeCARBalance(true);
        if("error".equals((String) loJSON.get("result"))){
            return loJSON;
        }
        
        loJSON = computeSOABalance(true);
        if("error".equals((String) loJSON.get("result"))){
            return loJSON;
        }
        
        return loJSON;
    }
    
    private JSONObject computeADVBalance(boolean fisValidate){
        JSONObject loJSON = new JSONObject();
        for(int lnCtr = 0; lnCtr <= getSIAdvancesList().size()-1; lnCtr++){
            
            
        }
        
        return loJSON;
    }
    
    private JSONObject populateAdvances(String fsValue){
        JSONObject loJSON = new JSONObject();
        BigDecimal ldblTotlAdv = new BigDecimal("0.00");
        for(int lnCtr = 0; lnCtr <= poDetail.getDetailList().size()-1; lnCtr++){
            //SUM SI Total Advances
            if(poDetail.getDetailModel(lnCtr).getTranType().equals("ADVANCES")){
                ldblTotlAdv = ldblTotlAdv.add(poDetail.getDetailModel(lnCtr).getTranAmt());
            }
        }
        
        //1. Check when SI exist in SI Advances
        loJSON = poAdvances.checkSIAdvancesExist(fsValue);
        if("success".equals((String) loJSON.get("result"))){
            //Update SI Advances
            loJSON = poAdvances.openDetail((String) loJSON.get("sTransNox"), true);
            if("error".equals((String) loJSON.get("result"))){
                return loJSON;
            }
        } else {
            //Create New SI Advances
            if(ldblTotlAdv.compareTo(new BigDecimal("0.00")) > 0){
                addSIAdvances();
                poAdvances.getDetailModel( getSIAdvancesList().size()-1).setReferNo(fsValue);
                poAdvances.getDetailModel( getSIAdvancesList().size()-1).setEntryNo(0);
            }
        }
        
        if( poAdvances.getDetailList().size()>0){
            poAdvances.getDetailModel( getSIAdvancesList().size()-1).setAdvAmt(ldblTotlAdv);
        }
        
        return loJSON;
    }
    
    private JSONObject computeCARBalance(boolean fisValidate){
        JSONObject loJSON = new JSONObject();
        BigDecimal ldblOthPaidAmt = new BigDecimal("0.00");
        BigDecimal ldblDetOthPaidAmt = new BigDecimal("0.00");
        BigDecimal ldblPaidAmt = new BigDecimal("0.00");
        BigDecimal ldblDetPaidAmt = new BigDecimal("0.00");
        
        for(int lnCtr = 0; lnCtr <= psCARTransCde.size()-1; lnCtr++){
            //Get SI Payment
            if(!poController.getMasterModel().getTranStat().equals(TransactionStatus.STATE_CANCELLED)){
                for(int lnRow = 0;lnRow <= poDetail.getDetailList().size()-1;lnRow++){
                    if(psCARTransCde.get(lnCtr).equals(poDetail.getDetailModel(lnRow).getSourceNo())){
                        ldblPaidAmt = ldblPaidAmt.add(poDetail.getDetailModel(lnRow).getTranAmt());
                    }
                }
            }
            
            loJSON = poCAR.openTransaction(psCARTransCde.get(lnCtr));
            if("error".equals((String) loJSON.get("result"))){
                return loJSON;
            } else {
                if(!fisValidate){
                    loJSON = poCAR.updateTransaction();
                    if("error".equals((String) loJSON.get("result"))){
                        return loJSON;
                    }
                }
                
                //Get CAR Detail
                for(int lnCARDet = 0; lnCARDet <= poCAR.getDetailList().size()-1;lnCARDet++){
                    //Store unique acct code TODO
                    
                    //Compute amount paid per account title of CAR detail
                    if(!poController.getMasterModel().getTranStat().equals(TransactionStatus.STATE_CANCELLED)){
                        for(int lnRow = 0; lnRow <= poDetail.getDetailList().size()-1; lnRow++){
                            //SI Detail equal to CAR TransNo
                            if(poDetail.getDetailModel(lnRow).getSourceNo().equals(poCAR.getMasterModel().getMasterModel().getTransNo())){
                                if(poDetail.getDetailModel(lnRow).getTranType().equals(poCAR.getDetailModel().getDetailModel(lnCARDet).getTranType())){
                                    ldblDetPaidAmt = ldblDetPaidAmt.add(poDetail.getDetailModel(lnRow).getTranAmt());
                                }
                            }
                        }
                    }
                    //VALIDATE CAR DETAIL
                    ldblDetOthPaidAmt = poController.checkPaidAmt(psCARTransCde.get(lnCtr), poCAR.getDetailModel().getDetailModel(lnCARDet).getTranType());
                    ldblDetPaidAmt = ldblDetPaidAmt.add(ldblDetOthPaidAmt);
                    
                    if(fisValidate){
                        ldblDetPaidAmt = poCAR.getDetailModel().getDetailModel(lnCARDet).getTotalAmt().subtract(ldblDetPaidAmt);
                        // Get the default locale or specify one
                        NumberFormat numberFormat = NumberFormat.getInstance(Locale.US);
                        if(ldblDetPaidAmt.compareTo(new BigDecimal("0.00")) < 0){
                            loJSON.put("result", "error");
                            loJSON.put("message", "Invalid CAR Balance " + numberFormat.format(ldblDetPaidAmt) 
                                    + " computed for CAR No. " + poCAR.getMasterModel().getMasterModel().getTransNo() 
                                    + " in transaction type. " + poCAR.getDetailModel().getDetailModel(lnCARDet).getTranType()
                                    + ".\n\nPlease check your CAR Total vs Total Payment per transaction type.");
                            return loJSON;
                        }
                    } else {
                        poCAR.getDetailModel().getDetailModel(lnCARDet).setAmtPaid(ldblDetPaidAmt);
                    }
                    
                    //Clear amount paid
                    ldblDetPaidAmt = new BigDecimal("0.00");
                    ldblDetOthPaidAmt = new BigDecimal("0.00");
                }
                
                //VALIDATE CAR MASTER
                ldblOthPaidAmt = poController.checkPaidAmt(psCARTransCde.get(lnCtr), "");
                //CAR TOTAL - (OTHER PAYMENT + CURRENT RECEIPT PAYMENT)
    //            ldblPaidAmt = poCAR.getMasterModel().getTotalAmt().subtract(ldblPaidAmt.add(ldblOthPaidAmt));
                ldblPaidAmt = ldblPaidAmt.add(ldblOthPaidAmt);
                if(fisValidate){
                    ldblPaidAmt = poCAR.getMasterModel().getMasterModel().getTotalAmt().subtract(ldblPaidAmt);
                    // Get the default locale or specify one
                    NumberFormat numberFormat = NumberFormat.getInstance(Locale.US);
                    if(ldblPaidAmt.compareTo(new BigDecimal("0.00")) < 0){
                        loJSON.put("result", "error");
                        loJSON.put("message", "Invalid CAR Balance " + numberFormat.format(ldblPaidAmt) 
                                + " computed for CAR No. " + poCAR.getMasterModel().getMasterModel().getTransNo() 
                                + ".\n\nPlease check your CAR Total vs Total Payment.");
                        return loJSON;
                    }
                    
                } else {
                    poCAR.getMasterModel().getMasterModel().setAmtPaid(ldblPaidAmt);
                    loJSON = poCAR.saveTransaction();
                }
            }
            //Clear amount paid
            ldblPaidAmt = new BigDecimal("0.00");
        }
        
        return loJSON;
    }
    
    private JSONObject computeSOABalance(boolean fisValidate){
        JSONObject loJSON = new JSONObject();
        BigDecimal ldblOthPaidAmt = new BigDecimal("0.00");
        BigDecimal ldblPaidAmt = new BigDecimal("0.00");
        
        for(int lnCtr = 0; lnCtr <= psSOATransCde.size()-1; lnCtr++){
            //Get SI Payment
            if(!poController.getMasterModel().getTranStat().equals(TransactionStatus.STATE_CANCELLED)){
                for(int lnRow = 0;lnRow <= poDetail.getDetailList().size()-1;lnRow++){
                    if(psSOATransCde.get(lnCtr).equals(poDetail.getDetailModel(lnRow).getSourceNo())){
                        ldblPaidAmt = ldblPaidAmt.add(poDetail.getDetailModel(lnRow).getTranAmt());
                    }
                }
            }
            
            loJSON = poSOA.openTransaction(psSOATransCde.get(lnCtr));
            if("error".equals((String) loJSON.get("result"))){
                return loJSON;
            } else {
                ldblOthPaidAmt = poController.checkPaidAmt(psCARTransCde.get(lnCtr), ""); 
                //CAR TOTAL - (OTHER PAYMENT + CURRENT RECEIPT PAYMENT)
    //                ldblPaidAmt = poSOA.getMasterModel().getTranTotl().subtract(ldblPaidAmt.add(ldblOthPaidAmt));
                ldblPaidAmt = ldblPaidAmt.add(ldblOthPaidAmt);

                if(fisValidate){
                    ldblPaidAmt = poSOA.getMasterModel().getTranTotl().subtract(ldblPaidAmt);
                    // Get the default locale or specify one
                    NumberFormat numberFormat = NumberFormat.getInstance(Locale.US);
                    if(ldblPaidAmt.compareTo(new BigDecimal("0.00")) < 0){
                        loJSON.put("result", "error");
                        loJSON.put("message", "Invalid SOA Balance " + numberFormat.format(ldblPaidAmt) 
                                + " computed for SOA No. " + poCAR.getMasterModel().getMasterModel().getTransNo() 
                                + ".\n\nPlease check your SOA Total vs Total Payment.");
                        return loJSON;
                    }
                } else {
                    loJSON = poSOA.updateTransaction();
                    if(!"error".equals((String) loJSON.get("result"))){
                        poSOA.getMasterModel().setAmtPaid(ldblPaidAmt);
                        loJSON = poSOA.saveTransaction();
                    } else {
                        return loJSON;
                    }
                }
            }
            //Clear amount paid
            ldblPaidAmt = new BigDecimal("0.00");
        }
        
        return loJSON;
    }
    
    public JSONObject searchCustomer(String fsValue) {
        JSONObject loJSON = new JSONObject();
        Client_Master loEntity = new Client_Master(poGRider, pbWtParent, psBranchCd);
        loJSON = loEntity.searchClient(fsValue, false);
        if(!"error".equals((String) loJSON.get("result"))){
            poController.getMasterModel().setClientID((String) loJSON.get("sClientID"));
            poController.getMasterModel().setBuyCltNm((String) loJSON.get("sCompnyNm"));
            poController.getMasterModel().setAddress((String) loJSON.get("xAddressx"));
            poController.getMasterModel().setTaxIDNo((String) loJSON.get("sTaxIDNox"));
        } else {
            poController.getMasterModel().setClientID("");
            poController.getMasterModel().setAddress("");
            poController.getMasterModel().setBuyCltNm("");
            poController.getMasterModel().setTaxIDNo("");
        }
        
        return loJSON;
    }
    
    public JSONObject searchPaymentBank(String fsValue, int fnRow, boolean fbByCode) {
        JSONObject loJSON = new JSONObject();
        Bank_Master loEntity = new Bank_Master(poGRider, pbWtParent,psBranchCd) ;
        loJSON = loEntity.searchRecord(fsValue, true, fbByCode);
        if(!"error".equals((String) loJSON.get("result"))){
//            switch(fsPayMode){
//                case "CARD":
                    poPayment.getDetailModel(fnRow).setCCBankName((String) loJSON.get("sBankName"));
                    poPayment.getDetailModel(fnRow).setCCBankID((String) loJSON.get("sBankIDxx"));
//                break;
//            }
        } else {
//            switch(fsPayMode){
//                case "CARD":
                    poPayment.getDetailModel(fnRow).setCCBankName("");
                    poPayment.getDetailModel(fnRow).setCCBankID("");
//                break;
//            }
        }
        
        return loJSON;
    }
    
    public JSONObject searchBankBranch(String fsValue) {
        JSONObject loJSON = new JSONObject();
        Bank_Branches loEntity = new Bank_Branches(poGRider, pbWtParent, psBranchCd);
        loJSON = loEntity.searchRecord(fsValue, true);
        if(!"error".equals((String) loJSON.get("result"))){
            poController.getMasterModel().setClientID((String) loJSON.get("sBrBankID"));
            poController.getMasterModel().setBuyCltNm((String) loJSON.get("sBankName") + " / " + (String) loJSON.get("sBrBankNm"));
            poController.getMasterModel().setAddress((String) loJSON.get("xAddressx"));
           
        } else {
            poController.getMasterModel().setClientID("");
            poController.getMasterModel().setBuyCltNm("");
            poController.getMasterModel().setAddress(""); 
        }
        
        return loJSON;
    }
    
    public JSONObject searchInsurance(String fsValue) {
        JSONObject loJSON = new JSONObject();
        Insurance_Branches loEntity = new Insurance_Branches(poGRider, pbWtParent, psBranchCd);
        loJSON = loEntity.searchRecord(fsValue, true);
        if(!"error".equals((String) loJSON.get("result"))){
            poController.getMasterModel().setClientID((String) loJSON.get("sBrInsIDx"));
            poController.getMasterModel().setBuyCltNm((String) loJSON.get("sInsurNme") + " / " + (String) loJSON.get("sBrInsNme"));
            poController.getMasterModel().setAddress((String) loJSON.get("xAddressx"));
        } else {
            poController.getMasterModel().setClientID("");
            poController.getMasterModel().setBuyCltNm("");
            poController.getMasterModel().setAddress("");
        }
        return loJSON;
    }
    
    public JSONObject searchEmployee(String fsValue, boolean fbByCode) {
        JSONObject loJSON = new JSONObject();
        Sales_Executive_Master loEntity = new Sales_Executive_Master(poGRider, pbWtParent, psBranchCd);
        loJSON = loEntity.searchEmployee(fsValue,fbByCode);
        if(!"error".equals((String) loJSON.get("result"))){
            poController.getMasterModel().setClientID((String) loJSON.get("sClientID"));
            poController.getMasterModel().setBuyCltNm((String) loJSON.get("sCompnyNm"));
            poController.getMasterModel().setAddress((String) loJSON.get("sAddressx"));
        } else {
            poController.getMasterModel().setClientID("");
            poController.getMasterModel().setBuyCltNm("");
            poController.getMasterModel().setAddress("");
        }
        return loJSON;
    }
    
    public JSONObject searchSupplier(String fsValue) {
        JSONObject loJSON = new JSONObject();
        Insurance_Branches loEntity = new Insurance_Branches(poGRider, pbWtParent, psBranchCd);
        loJSON = loEntity.searchRecord(fsValue, true);
        if(!"error".equals((String) loJSON.get("result"))){
            poController.getMasterModel().setClientID((String) loJSON.get("sBrInsIDx"));
            poController.getMasterModel().setBuyCltNm((String) loJSON.get("sInsurNme") + " / " + (String) loJSON.get("sBrInsNme"));
            poController.getMasterModel().setAddress((String) loJSON.get("xAddressx"));
        } else {
            poController.getMasterModel().setClientID("");
            poController.getMasterModel().setBuyCltNm("");
            poController.getMasterModel().setAddress("");
        }
        return loJSON;
    }
    
    public JSONObject searchCAR(String fsValue) {
        JSONObject loJSON = new JSONObject();
        CashierReceivables loEntity = new CashierReceivables(poGRider, pbWtParent, psBranchCd);
        loJSON = loEntity.searchTransaction(fsValue, false);
        if(!"error".equals((String) loJSON.get("result"))){
            //Check for exisiting CAR do not allow when payer type is not the same
            for(int lnCtr = 0; lnCtr <= poDetail.getDetailList().size()-1; lnCtr++){
                if(poDetail.getDetailModel(lnCtr).getPayerCde() != null){
                    if(!poDetail.getDetailModel(lnCtr).getPayerCde().trim().isEmpty()) {
                        if(!poDetail.getDetailModel(lnCtr).getPayerCde().equals(loEntity.getMasterModel().getMasterModel().getPayerCde())){
                            loJSON.put("result", "error");
                            loJSON.put("message","Payer type mis match."
                                                    + "\n\nInsert aborted.");
                            return loJSON;
                        }
                    }
                }
            }
            
            if(poController.getMasterModel().getClientID() == null){
                poController.getMasterModel().setClientID(loEntity.getMasterModel().getMasterModel().getPayerID());
                poController.getMasterModel().setBuyCltNm(loEntity.getMasterModel().getMasterModel().getPayerNme());
                poController.getMasterModel().setAddress(loEntity.getMasterModel().getMasterModel().getPayerAdd());
                poController.getMasterModel().setTaxIDNo(loEntity.getMasterModel().getMasterModel().getTaxIDNo());
            } else {
                if(poController.getMasterModel().getClientID().trim().isEmpty()){
                    poController.getMasterModel().setClientID(loEntity.getMasterModel().getMasterModel().getPayerID());
                    poController.getMasterModel().setBuyCltNm(loEntity.getMasterModel().getMasterModel().getPayerNme());
                    poController.getMasterModel().setAddress(loEntity.getMasterModel().getMasterModel().getPayerAdd());
                    poController.getMasterModel().setTaxIDNo(loEntity.getMasterModel().getMasterModel().getTaxIDNo());
                }
            }
            
            for(int lnCtr = 0; lnCtr <= loEntity.getDetailList().size()-1;lnCtr++){
                addSIDetail();
                poDetail.getDetailModel(getSIDetailList().size()-1).setSourceNo(loEntity.getMasterModel().getMasterModel().getTransNo());
                poDetail.getDetailModel(getSIDetailList().size()-1).setDescript(loEntity.getMasterModel().getMasterModel().getSourceCD());
                poDetail.getDetailModel(getSIDetailList().size()-1).setFormNo(loEntity.getMasterModel().getMasterModel().getFormNo());
                poDetail.getDetailModel(getSIDetailList().size()-1).setPayerCde(loEntity.getMasterModel().getMasterModel().getPayerCde());
                
                poDetail.getDetailModel(getSIDetailList().size()-1).setTranType(loEntity.getDetailModel().getDetailModel(lnCtr).getTranType());
                poDetail.getDetailModel(getSIDetailList().size()-1).setTranAmt(loEntity.getDetailModel().getDetailModel(lnCtr).getTotalAmt());
                poDetail.getDetailModel(getSIDetailList().size()-1).setNetAmt(loEntity.getDetailModel().getDetailModel(lnCtr).getTotalAmt());
                
                if (loEntity.getMasterModel().getMasterModel().getVSPNo() != null) {
                    poDetail.getDetailModel(getSIDetailList().size()-1).setSourceCD("VSP");
                }
                if (loEntity.getMasterModel().getMasterModel().getVSANo() != null) {
                    poDetail.getDetailModel(getSIDetailList().size()-1).setSourceCD("VSA");
                }
                if (loEntity.getMasterModel().getMasterModel().getInsAppNo() != null) {
                    poDetail.getDetailModel(getSIDetailList().size()-1).setSourceCD("POL");
                }
                
            }
        } 
        return loJSON;
    }
    
    public JSONObject searchSOA(String fsValue) {
        JSONObject loJSON = new JSONObject();
        StatementOfAccount loEntity = new StatementOfAccount(poGRider, pbWtParent, psBranchCd);
        loJSON = loEntity.searchTransaction(fsValue, false);
        if(!"error".equals((String) loJSON.get("result"))){
            if(poController.getMasterModel().getClientID() == null){
                poController.getMasterModel().setClientID(loEntity.getMasterModel().getMasterModel().getClientID());
                poController.getMasterModel().setBuyCltNm(loEntity.getMasterModel().getMasterModel().getPayerNme());
//                poController.getMasterModel().setAddress(loEntity.getMasterModel().getMasterModel().getPayerAdd());
            } else {
                if(poController.getMasterModel().getClientID().trim().isEmpty()){
                    poController.getMasterModel().setClientID(loEntity.getMasterModel().getMasterModel().getClientID());
                    poController.getMasterModel().setBuyCltNm(loEntity.getMasterModel().getMasterModel().getPayerNme());
//                    poController.getMasterModel().setAddress(loEntity.getMasterModel().getMasterModel().getPayerAdd());
                }
            }
            for(int lnCtr = 0; lnCtr <= loEntity.getDetailList().size()-1;lnCtr++){
                addSIDetail();
                poDetail.getDetailModel(getSIDetailList().size()-1).setSourceNo(loEntity.getMasterModel().getMasterModel().getTransNo());
                poDetail.getDetailModel(getSIDetailList().size()-1).setSourceCD("AR");
                poDetail.getDetailModel(getSIDetailList().size()-1).setTranType(loEntity.getDetailModel().getDetailModel(lnCtr).getTranType());
                poDetail.getDetailModel(getSIDetailList().size()-1).setTranAmt(loEntity.getDetailModel().getDetailModel(lnCtr).getAmount());
            }
        }
        return loJSON;
    }
}
