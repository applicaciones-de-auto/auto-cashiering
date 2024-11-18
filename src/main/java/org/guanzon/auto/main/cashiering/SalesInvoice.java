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
import org.guanzon.appdriver.iface.GTransaction;
import org.guanzon.auto.controller.cashiering.CashierReceivables_Master;
import org.guanzon.auto.controller.cashiering.SalesInvoice_Advances_Source;
import org.guanzon.auto.controller.cashiering.SalesInvoice_Master;
import org.guanzon.auto.controller.cashiering.SalesInvoice_Payment;
import org.guanzon.auto.controller.cashiering.SalesInvoice_Source;
import org.guanzon.auto.controller.cashiering.StatementOfAccount_Master;
import org.guanzon.auto.controller.clients.Client_Master;
import org.guanzon.auto.controller.clients.Sales_Executive_Master;
import org.guanzon.auto.controller.parameter.Bank_Branches;
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
    CashierReceivables_Master poCAR;
    StatementOfAccount_Master poSOA;
    
    List<String> psCARTransCde = new ArrayList<>();
    List<String> psSOATransCde = new ArrayList<>();
    
    public SalesInvoice(GRider foAppDrver, boolean fbWtParent, String fsBranchCd){
        poController = new SalesInvoice_Master(foAppDrver,fbWtParent,fsBranchCd);
        poDetail = new SalesInvoice_Source(foAppDrver);
        poPayment = new SalesInvoice_Payment(foAppDrver);
        poAdvances = new SalesInvoice_Advances_Source(foAppDrver);
        poCAR = new CashierReceivables_Master(foAppDrver,fbWtParent,fsBranchCd);
        poSOA = new StatementOfAccount_Master(foAppDrver,fbWtParent,fsBranchCd);
        
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
        
        poJSON = poPayment.openDetail(fsValue);
        if(!"success".equals((String) checkData(poJSON).get("result"))){
            pnEditMode = EditMode.UNKNOWN;
            return poJSON;
        }
        
        poJSON = poAdvances.openDetail(fsValue);
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
        //Validate CAR BALANCE
        poJSON = computeCARBalance(true);
        if("error".equals((String) poJSON.get("result"))){
            return poJSON;
        }
        
        //Validate SOA BALANCE
        poJSON = computeSOABalance(true);
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
        
        poJSON =  poPayment.saveDetail((String) poController.getMasterModel().getTransNo());
        if("error".equalsIgnoreCase((String)checkData(poJSON).get("result"))){
            if (!pbWtParent) poGRider.rollbackTrans();
            return checkData(poJSON);
        }
        
        poJSON =  poAdvances.saveDetail((String) poController.getMasterModel().getTransNo());
        if("error".equalsIgnoreCase((String)checkData(poJSON).get("result"))){
            if (!pbWtParent) poGRider.rollbackTrans();
            return checkData(poJSON);
        }
        
        //UPDATE CAR BALANCE
        poJSON = computeCARBalance(false);
        if("error".equals((String) poJSON.get("result"))){
            return poJSON;
        }
        
        //UPDATE SOA BALANCE
        poJSON = computeSOABalance(false);
        if("error".equals((String) poJSON.get("result"))){
            return poJSON;
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
    
    @Override
    public void setTransactionStatus(String string) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
//    public JSONObject loadTransaction(String fsFrom, String fsTo){
//        return poController.loadTransaction(fsFrom, fsTo);
//    }
//    
//    public ArrayList getMasterList(){return poController.getDetailList();}
    
    public JSONObject computeSIAmount(){
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
            if(ldblNetAmt.compareTo(new BigDecimal("0.00")) < 0){
                loJSON.put("result", "error");
                loJSON.put("message", "Invalid Net Amount.");
                return loJSON;
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
        
        loJSON = computeCARBalance(true);
        if("error".equals((String) loJSON.get("result"))){
            return loJSON;
        }
        
        loJSON = computeSOABalance(true);
        if("error".equals((String) loJSON.get("result"))){
            return loJSON;
        }
        
        poController.getMasterModel().setTranTotl(ldblTranTotl);
        poController.getMasterModel().setAdvPaym(ldblAdvPaym);
        poController.getMasterModel().setDiscount(ldblDiscount);
        poController.getMasterModel().setNetTotal(ldblNetTotl);
        
        return loJSON;
    }
    
    public JSONObject computeCARBalance(boolean fisValidate){
        JSONObject loJSON = new JSONObject();
        BigDecimal ldblPaidAmt = new BigDecimal("0.00");
        
        for(int lnCtr = 0; lnCtr <= psCARTransCde.size()-1; lnCtr++){
            ldblPaidAmt = poController.checkPaidAmt(psCARTransCde.get(lnCtr)); 
            ldblPaidAmt = poCAR.getMasterModel().getTotalAmt().subtract(ldblPaidAmt);
            
            if(fisValidate){
                // Get the default locale or specify one
                NumberFormat numberFormat = NumberFormat.getInstance(Locale.US);
                if(ldblPaidAmt.compareTo(new BigDecimal("0.00")) < 0){
                    loJSON.put("result", "error");
                    loJSON.put("result", "Invalid CAR Balance " + numberFormat.format(ldblPaidAmt) 
                            + " computed for CAR No. " + poCAR.getMasterModel().getTransNo() 
                            + ".\n\nPlease check your CAR Total vs Total Payment.");
                    return loJSON;
                }
            } else {
                loJSON = poCAR.openTransaction(psCARTransCde.get(lnCtr));
                if("error".equals((String) loJSON.get("result"))){
                    return loJSON;
                } else {
                    loJSON = poCAR.updateTransaction();
                    if(!"error".equals((String) loJSON.get("result"))){
                        poCAR.getMasterModel().setAmtPaid(ldblPaidAmt);
                        loJSON = poCAR.saveTransaction();
                    } else {
                        return loJSON;
                    }
                }
            }
        }
        
        return loJSON;
    }
    
    public JSONObject computeSOABalance(boolean fisValidate){
        JSONObject loJSON = new JSONObject();
        BigDecimal ldblPaidAmt = new BigDecimal("0.00");
        
        for(int lnCtr = 0; lnCtr <= psSOATransCde.size()-1; lnCtr++){
            ldblPaidAmt = poController.checkPaidAmt(psCARTransCde.get(lnCtr)); 
            ldblPaidAmt = poSOA.getMasterModel().getTranTotl().subtract(ldblPaidAmt);
            
            if(fisValidate){
                // Get the default locale or specify one
                NumberFormat numberFormat = NumberFormat.getInstance(Locale.US);
                if(ldblPaidAmt.compareTo(new BigDecimal("0.00")) < 0){
                    loJSON.put("result", "error");
                    loJSON.put("result", "Invalid SOA Balance " + numberFormat.format(ldblPaidAmt) 
                            + " computed for SOA No. " + poCAR.getMasterModel().getTransNo() 
                            + ".\n\nPlease check your SOA Total vs Total Payment.");
                    return loJSON;
                }
            } else {
                loJSON = poSOA.openTransaction(psSOATransCde.get(lnCtr));
                if("error".equals((String) loJSON.get("result"))){
                    return loJSON;
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
        } else {
            poController.getMasterModel().setClientID("");
            poController.getMasterModel().setAddress("");
        }
        
        return loJSON;
    }
    
    public JSONObject searchBank(String fsValue) {
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
            if(poController.getMasterModel().getClientID() == null){
                poController.getMasterModel().setClientID(loEntity.getMasterModel().getMasterModel().getClientID());
                poController.getMasterModel().setBuyCltNm(loEntity.getMasterModel().getMasterModel().getPayerNme());
                poController.getMasterModel().setAddress(loEntity.getMasterModel().getMasterModel().getPayerAdd());
            } else {
                if(poController.getMasterModel().getClientID().trim().isEmpty()){
                    poController.getMasterModel().setClientID(loEntity.getMasterModel().getMasterModel().getClientID());
                    poController.getMasterModel().setBuyCltNm(loEntity.getMasterModel().getMasterModel().getPayerNme());
                    poController.getMasterModel().setAddress(loEntity.getMasterModel().getMasterModel().getPayerAdd());
                }
            }
            
            for(int lnCtr = 0; lnCtr <= loEntity.getDetailList().size()-1;lnCtr++){
                addSIDetail();
                poDetail.getDetailModel(getSIDetailList().size()-1).setSourceNo(loEntity.getMasterModel().getMasterModel().getTransNo());
                poDetail.getDetailModel(getSIDetailList().size()-1).setSourceCD(loEntity.getMasterModel().getMasterModel().getSourceCD());
                poDetail.getDetailModel(getSIDetailList().size()-1).setTranType(loEntity.getDetailModel().getDetailModel(lnCtr).getTranType());
                poDetail.getDetailModel(getSIDetailList().size()-1).setTranAmt(loEntity.getDetailModel().getDetailModel(lnCtr).getTotalAmt());
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
