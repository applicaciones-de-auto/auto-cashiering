/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.guanzon.auto.main.cashiering;

import java.math.BigDecimal;
import java.util.ArrayList;
import org.guanzon.appdriver.base.GRider;
import org.guanzon.appdriver.constant.EditMode;
import org.guanzon.appdriver.iface.GTransaction;
import org.guanzon.auto.controller.cashiering.SalesInvoice_Master;
import org.guanzon.auto.controller.cashiering.VehicleSalesInvoice_Source;
import org.json.simple.JSONObject;

/**
 *
 * @author Arsiela
 */
public class VehicleSalesInvoice implements GTransaction{
    GRider poGRider;
    String psBranchCd;
    boolean pbWtParent;
    int pnEditMode;
    String psTransStat;
    String psMessagex;
    public JSONObject poJSON;

    SalesInvoice_Master poController;
    VehicleSalesInvoice_Source poVSISource;
    
    public VehicleSalesInvoice(GRider foAppDrver, boolean fbWtParent, String fsBranchCd){
        poController = new SalesInvoice_Master(foAppDrver,fbWtParent,fsBranchCd);
        poVSISource = new VehicleSalesInvoice_Source(foAppDrver);
        
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
        
        poJSON = poVSISource.openDetail(fsValue);
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
        
        computeAmount();
        
        if (!pbWtParent) poGRider.beginTrans();
        
        poJSON =  poController.saveTransaction();
        if("error".equalsIgnoreCase((String) poJSON.get("result"))){
            if (!pbWtParent) poGRider.rollbackTrans();
            return checkData(poJSON);
        }
        
        poVSISource.setTargetBranchCd(poController.getMasterModel().getBranchCd());
        poJSON =  poVSISource.saveDetail((String) poController.getMasterModel().getTransNo());
        if("error".equalsIgnoreCase((String)checkData(poJSON).get("result"))){
            if (!pbWtParent) poGRider.rollbackTrans();
            return checkData(poJSON);
        }
        
        if (!pbWtParent) poGRider.commitTrans();
        
        return poJSON;
    }
    
    public JSONObject savePrint(boolean fsIsValidate) {
        return poController.savePrinted(fsIsValidate,"VSI");
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

    @Override
    public void setTransactionStatus(String string) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public VehicleSalesInvoice_Source getVSISourceModel(){return poVSISource;} 
    public ArrayList getVSISourceList(){return poVSISource.getDetailList();}
    public Object addVSISource(){ return poVSISource.addDetail(poController.getMasterModel().getTransNo());}
    
    public JSONObject searchVDR(String fsValue, boolean fbByCode, String fsClientType){
        JSONObject loJSON = new JSONObject();
        loJSON = poController.searchVDR(fsValue, fbByCode, fsClientType);
        if(!"error".equals((String) loJSON.get("result"))){
            if(((String) loJSON.get("sSINoxxxx")) != null){
                if(!((String) loJSON.get("sSINoxxxx")).trim().isEmpty()){
                    loJSON.put("result", "error");
                    loJSON.put("message", "VSP No. "+(String) loJSON.get("sReferNox")+" has existing DR No. " + (String) loJSON.get("sSINoxxxx") 
                                            + "\n\nLinking aborted.");
                    return loJSON;
                }
            }
            
            //Buying Customer Default         
            poController.getMasterModel().setClientID((String) loJSON.get("sClientID"));                                                        
            poController.getMasterModel().setBuyCltNm((String) loJSON.get("sBuyCltNm"));                                                        
            poController.getMasterModel().setTaxIDNo((String) loJSON.get("sTaxIDNox"));                                                         
            poController.getMasterModel().setAddress(((String) loJSON.get("sAddressx")).trim());                                                     
            poController.getMasterModel().setBranchCd((String) loJSON.get("sBranchCD"));         
            
            if(getVSISourceList().size()-1 < 0){
                addVSISource();
            } 
           
            poVSISource.getDetailModel().setCustType((String) loJSON.get("cCustType"));
            poVSISource.getDetailModel().setReferNo(poController.getMasterModel().getTransNo()); //(String) loJSON.get("sTransNox")
            poVSISource.getDetailModel().setTranType("UNIT SALES");
            poVSISource.getDetailModel().setSourceCD("VSI");
            poVSISource.getDetailModel().setSourceNo((String) loJSON.get("sTransNox"));
            poVSISource.getDetailModel().setUDRNo((String) loJSON.get("sReferNox"));
            poVSISource.getDetailModel().setCSNo((String) loJSON.get("sCSNoxxxx"));
            poVSISource.getDetailModel().setPlateNo((String) loJSON.get("sPlateNox"));
            poVSISource.getDetailModel().setEngineNo((String) loJSON.get("sEngineNo"));
            poVSISource.getDetailModel().setFrameNo((String) loJSON.get("sFrameNox"));
            poVSISource.getDetailModel().setColorDsc((String) loJSON.get("sColorDsc"));
            poVSISource.getDetailModel().setVhclFDsc((String) loJSON.get("sVhclFDsc"));
            poVSISource.getDetailModel().setVhclDesc((String) loJSON.get("sVhclDesc"));
            poVSISource.getDetailModel().setSEName((String) loJSON.get("sSENamexx"));
            poVSISource.getDetailModel().setCoCltNm((String) loJSON.get("sCoCltNmx"));
            
            poVSISource.getDetailModel().setUnitPrce(new BigDecimal((String) loJSON.get("nUnitPrce")));
            poVSISource.getDetailModel().setPromoDsc(new BigDecimal((String) loJSON.get("nPromoDsc")));
            poVSISource.getDetailModel().setFleetDsc(new BigDecimal((String) loJSON.get("nFleetDsc")));
            poVSISource.getDetailModel().setSPFltDsc(new BigDecimal((String) loJSON.get("nSPFltDsc")));
            poVSISource.getDetailModel().setBndleDsc(new BigDecimal((String) loJSON.get("nBndleDsc")));
            poVSISource.getDetailModel().setAddlDsc(new BigDecimal((String) loJSON.get("nAddlDscx")));
            
            BigDecimal ldblDiscount = poVSISource.getDetailModel().getPromoDsc().add(poVSISource.getDetailModel().getFleetDsc()).add(poVSISource.getDetailModel().getFleetDsc())
                                   .add(poVSISource.getDetailModel().getSPFltDsc()).add(poVSISource.getDetailModel().getBndleDsc()).add(poVSISource.getDetailModel().getAddlDsc());
            poVSISource.getDetailModel().setDiscount(ldblDiscount);
            poVSISource.getDetailModel().setTranAmt(poVSISource.getDetailModel().getUnitPrce().add(ldblDiscount));
            poVSISource.getDetailModel().setNetAmt(poVSISource.getDetailModel().getUnitPrce().subtract(ldblDiscount));
            
            //VSI Master                         
//            poController.getMasterModel().setTranTotl(poVSISource.getDetailModel().getUnitPrce().subtract(ldblDiscount));    
            poController.getMasterModel().setDiscount(ldblDiscount); 
            
            computeAmount();
        } else { 
            poController.getMasterModel().setClientID("");                                                        
            poController.getMasterModel().setBuyCltNm("");                                                        
            poController.getMasterModel().setAddress("");                                                     
            poController.getMasterModel().setBranchCd("");    
            
            poVSISource.getDetailModel().setCustType("");
            poVSISource.getDetailModel().setUDRNo("");
            poVSISource.getDetailModel().setCSNo("");
            poVSISource.getDetailModel().setPlateNo("");
            poVSISource.getDetailModel().setEngineNo("");
            poVSISource.getDetailModel().setFrameNo("");
            poVSISource.getDetailModel().setColorDsc("");
            poVSISource.getDetailModel().setVhclFDsc("");
            poVSISource.getDetailModel().setVhclDesc("");
            poVSISource.getDetailModel().setSEName("");
            poVSISource.getDetailModel().setCoCltNm(""); 
            poVSISource.getDetailModel().setTranType("");
            poVSISource.getDetailModel().setUnitPrce(new BigDecimal("0.00")); 
            poVSISource.getDetailModel().setPromoDsc(new BigDecimal("0.00")); 
            poVSISource.getDetailModel().setFleetDsc(new BigDecimal("0.00")); 
            poVSISource.getDetailModel().setSPFltDsc(new BigDecimal("0.00")); 
            poVSISource.getDetailModel().setBndleDsc(new BigDecimal("0.00")); 
            poVSISource.getDetailModel().setAddlDsc(new BigDecimal("0.00")); 
            //VSI Source
            poVSISource.getDetailModel().setDiscount(new BigDecimal("0.00")); 
            poVSISource.getDetailModel().setTranAmt(new BigDecimal("0.00")); 
            //VSI Master        
            poVSISource.getDetailModel().setSourceNo("");
            poController.getMasterModel().setTranTotl(new BigDecimal("0.00")); 
            poController.getMasterModel().setNetTotal(new BigDecimal("0.00")); 
            poController.getMasterModel().setDiscount(new BigDecimal("0.00")); 
            poController.getMasterModel().setVatAmt(new BigDecimal("0.00")); 
            
            
        }
        return loJSON;
    }
    
    
    
    private JSONObject computeAmount() {
        JSONObject loJSON = new JSONObject();
        
        Double ldblUnitPrice = Double.valueOf(String.valueOf(poVSISource.getDetailModel().getUnitPrce()));
        Double ldblDiscount = Double.valueOf(String.valueOf(poVSISource.getDetailModel().getDiscount()));
        Double ldblVatRate = poVSISource.getDetailModel().getVatRate();
        Double ldblBasePriceWVatPercent = poVSISource.getDetailModel().getBasePriceWVatPercent();
        Double ldblBasePrice = 0.00;// new BigDecimal("0.00");
        Double ldblVatAmt =  0.00; //new BigDecimal("0.00");
        Double ldblVatSales =  0.00;
        Double ldblTranTotl = 0.00; // new BigDecimal("0.00");
        String lsFormType = poVSISource.getDetailModel().getCustType();
        
        /*1. get final ldbl_UnitPrce value */
        if (lsFormType.equals("0")) {
//            ldblUnitPrice = ldblUnitPrice.subtract(ldblDiscount);
            ldblUnitPrice = ldblUnitPrice - ldblDiscount;
        }
        /*2. Compute for the Base Price and VAT Amount 
		using ie vat of 12%
		
		given: ldbl_vhclsrp (vehicle srp) as vat inclusive srp
				112% as equivalent percentage value for this vat inclusive price
				100% as equivalent percentage value for base price (no vat yet)
				base price (no vat yet) = ? 
		principle of ratio: <base price> : 100% = <vat inclusive srp> : 112% (100% + 12% vat)*/
        if (ldblBasePriceWVatPercent > 0.00) {
            //compute the base price (no vat srp)
//            double ldbl = ldblBasePriceWVatPercent / 100 ; //new BigDecimal(String.valueOf(ldblBasePriceWVatPercent/100)).setScale(2, BigDecimal.ROUND_HALF_UP)
//            ldblBasePrice = ldblUnitPrice.divide(new BigDecimal(ldbl).setScale(2, BigDecimal.ROUND_HALF_UP)).setScale(2, BigDecimal.ROUND_HALF_UP); 
            ldblBasePrice = ldblUnitPrice / (ldblBasePriceWVatPercent / 100);
            //compute the ie 12% vat amount from the base price (no vat srp)
//            ldblVatAmt = ldblBasePrice.multiply(new BigDecimal(ldblVatRate/100)).setScale(2, BigDecimal.ROUND_HALF_UP); 
            ldblVatAmt = ldblBasePrice * (ldblVatRate/100);
        }
        
        /*3. Compute for Final Sales Amount 	
	 (base price (no vat srp) + vatamt) should be equal to original value of uprice uprice (vehicle srp) as vat inclusive price */	 	
	if (lsFormType.equals( "0")){  //computation for non-dealer/supplier sales
//            ldblTranTotl = ldblBasePrice.add(ldblVatAmt).setScale(2, BigDecimal.ROUND_HALF_UP);
            ldblTranTotl = ldblBasePrice + ldblVatAmt;
        }else{
            //deduct discounts from end result only
//            ldblTranTotl = (ldblBasePrice.add(ldblVatAmt)).subtract(ldblDiscount).setScale(2, BigDecimal.ROUND_HALF_UP) ;
            ldblTranTotl = (ldblBasePrice + ldblVatAmt) - ldblDiscount;
        }
        ldblVatSales = ldblUnitPrice - ldblVatAmt;
        poController.getMasterModel().setVatSales(new BigDecimal(ldblVatSales)); 
        poController.getMasterModel().setVatAmt(new BigDecimal(ldblVatAmt)); 
        poController.getMasterModel().setTranTotl(new BigDecimal(ldblTranTotl)); 
        return loJSON;
    }
//    
//    public JSONObject searchClient(String fsValue, boolean fbBuyClient){
//        JSONObject loJSON = new JSONObject();
//        loJSON = poController.searchClient(fsValue,fbBuyClient);
//        if(!"error".equals((String) loJSON.get("result"))){
//            if(fbBuyClient){
//                poController.getMasterModel().setClientID((String) loJSON.get("sClientID"));
//                poController.getMasterModel().setBuyCltNm((String) loJSON.get("sCompnyNm"));
//                poController.getMasterModel().setAddress((String) loJSON.get("sAddressx"));
//            } else {
//                poController.getMasterModel().setCoCltID((String) loJSON.get("sClientID"));
//                poController.getMasterModel().setCoCltNm((String) loJSON.get("sCompnyNm"));
//            }
//        } else {
//            if(fbBuyClient){
//                poController.getMasterModel().setClientID("");
//                poController.getMasterModel().setBuyCltNm("");
//                poController.getMasterModel().setClientTp("");
//                poController.getMasterModel().setAddress("");
//            } else {
//                poController.getMasterModel().setCoCltID("");
//                poController.getMasterModel().setCoCltNm("");
//            }
//        }
//        return loJSON;
//    }
    

}
