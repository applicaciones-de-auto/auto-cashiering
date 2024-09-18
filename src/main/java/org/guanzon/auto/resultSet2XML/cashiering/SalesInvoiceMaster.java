/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.guanzon.auto.resultSet2XML.cashiering;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.guanzon.appdriver.base.GRider;
import org.guanzon.appdriver.base.MiscUtil;
import org.guanzon.appdriver.base.SQLUtil;
import org.guanzon.appdriver.constant.TransactionStatus;

/**
 *
 * @author Arsiela
 */
public class SalesInvoiceMaster  {
    public static void main (String [] args){
        String path;
        if(System.getProperty("os.name").toLowerCase().contains("win")){
            path = "D:/GGC_Maven_Systems";
        }
        else{
            path = "/srv/GGC_Maven_Systems";
        }
        System.setProperty("sys.default.path.config", path);
        
        GRider instance = new GRider("gRider");

        if (!instance.logUser("gRider", "M001000001")){
            System.err.println(instance.getErrMsg());
            System.exit(1);
        }

        System.out.println("Connected");
        
        System.setProperty("sys.default.path.metadata", "D:/GGC_Maven_Systems/config/metadata/Model_SalesInvoice_Master.xml");
        
        
        String lsSQL =    " SELECT "                                                                                                       
                        + "    a.sTransNox "                                                                                               
                        + "  , a.sBranchCd "                                                                                               
                        + "  , a.dTransact "                                                                                               
                        + "  , a.cDocTypex "                                                                                               
                        + "  , a.sReferNox "                                                                                               
                        + "  , a.sClientID "                                                                                               
                        + "  , a.nTranTotl "                                                                                               
                        + "  , a.nDiscount "                                                                                               
                        + "  , a.nVatSales "                                                                                               
                        + "  , a.nVatAmtxx "                                                                                               
                        + "  , a.nNonVATSl "                                                                                               
                        + "  , a.nZroVATSl "                                                                                               
                        + "  , a.cWTRatexx "                                                                                               
                        + "  , a.nCWTAmtxx "                                                                                               
                        + "  , a.nAdvPaymx "                                                                                               
                        + "  , a.nNetTotal "                                                                                               
                        + "  , a.nCashAmtx "                                                                                               
                        + "  , a.nChckAmtx "                                                                                               
                        + "  , a.nCardAmtx "                                                                                               
                        + "  , a.nOthrAmtx "                                                                                               
                        + "  , a.nGiftAmtx "                                                                                               
                        + "  , a.nAmtPaidx "                                                                                               
                        + "  , a.cPrintedx "                                                                                               
                        + "  , a.cTranStat "                                                                                               
                        + "  , a.sModified "                                                                                               
                        + "  , a.dModified "                                                                                               
                        + "  , CASE        "                                                                                               
                        + "     WHEN a.cTranStat = "+ SQLUtil.toSQL(TransactionStatus.STATE_CANCELLED)+" THEN 'CANCELLED'"                 
                        + "     WHEN a.cTranStat = "+ SQLUtil.toSQL(TransactionStatus.STATE_CLOSED)+" THEN 'APPROVED'    "                 
                        + "     WHEN a.cTranStat = "+ SQLUtil.toSQL(TransactionStatus.STATE_OPEN)+" THEN 'ACTIVE'        "                 
                        + "     WHEN a.cTranStat = "+ SQLUtil.toSQL(TransactionStatus.STATE_POSTED)+" THEN 'POSTED'      "                 
                        + "     ELSE 'ACTIVE'   "                                                                                          
                        + "    END AS sTranStat "                                                                                          
                        /*BUYING COSTUMER*/                                                                                                
                        + " , b.sCompnyNm AS sBuyCltNm "                                                                                   
                        + " , b.cClientTp              "                                                                                  
                        + " , b.sTaxIDNox              "                                                                                    
                        + " , TRIM(IFNULL(CONCAT( IFNULL(CONCAT(d.sHouseNox,' ') , ''), "                                                  
                        + "    IFNULL(CONCAT(d.sAddressx,' ') , ''),                    "                                                  
                        + "    IFNULL(CONCAT(e.sBrgyName,' '), ''),                     "                                                  
                        + "    IFNULL(CONCAT(f.sTownName, ', '),''),                    "                                                  
                        + "    IFNULL(CONCAT(g.sProvName),'') )	, '')) AS sAddressx     "                                                  
                        + " FROM si_master a  "                                                                                            
                         /*CUSTOMER*/                                                                                                      
                        + " LEFT JOIN client_master b ON b.sClientID = a.sClientID  "                                                      
                        + " LEFT JOIN client_address c ON c.sClientID = a.sClientID AND c.cPrimaryx = 1 "                                  
                        + " LEFT JOIN addresses d ON d.sAddrssID = c.sAddrssID "                                                           
                        + " LEFT JOIN barangay e ON e.sBrgyIDxx = d.sBrgyIDxx  "                                                           
                        + " LEFT JOIN towncity f ON f.sTownIDxx = d.sTownIDxx  "                                                           
                        + " LEFT JOIN province g ON g.sProvIDxx = f.sProvIDxx  "                                                           
                        + " LEFT JOIN client_mobile ba ON ba.sClientID = b.sClientID AND ba.cPrimaryx = 1 "                                
                        + " LEFT JOIN client_email_address bb ON bb.sClientID = b.sClientID AND bb.cPrimaryx = 1 "  
                        + " WHERE 0=1";
        
        
        ResultSet loRS = instance.executeQuery(lsSQL);
        try {
            if (MiscUtil.resultSet2XML(instance, loRS, System.getProperty("sys.default.path.metadata"), "si_master", "")){
                System.out.println("ResultSet exported.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    
}
