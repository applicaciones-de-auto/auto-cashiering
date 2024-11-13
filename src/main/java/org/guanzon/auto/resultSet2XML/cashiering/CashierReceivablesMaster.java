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
public class CashierReceivablesMaster  {
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
        
        System.setProperty("sys.default.path.metadata", "D:/GGC_Maven_Systems/config/metadata/Model_Cashier_Receivables.xml");
        
        
        String lsSQL =    " SELECT "                                                                         
                        + "   a.sTransNox "                                                                  
                        + " , a.dTransact "                                                                  
                        + " , a.sClientID "                                                                  
                        + " , a.sBrBankCd "                                                                  
                        + " , a.sBrInsCde "                                                                  
                        + " , a.sRemarksx "                                                                  
                        + " , a.sReferNox "                                                                  
                        + " , a.sSourceCD "                                                                  
                        + " , a.cPayerCde "                                                                  
                        + " , a.nGrossAmt "                                                                  
                        + " , a.nDiscAmtx "                                                                  
                        + " , a.nDeductnx "                                                                  
                        + " , a.nTotalAmt "                                                                  
                        + " , a.nChckPayx "                                                                  
                        + " , a.nAmtPaidx "    
                        + " , CASE "
                        + "     WHEN a.cPayerCde = 'a' THEN 'xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx' " //ASSOCIATE
                        + "     WHEN a.cPayerCde = 'b' THEN CONCAT(k.sBankName, ' ', h.sBrBankNm) " //BANK
                        + "     WHEN a.cPayerCde = 'c' THEN b.sCompnyNm " //CUSTOMER
                        + "     WHEN a.cPayerCde = 'i' THEN CONCAT(o.sInsurNme, ' ', l.sBrInsNme) " //INSURANCE
                        + "     WHEN a.cPayerCde = 's' THEN 'xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx' " //SUPPLIER                                  
                        + " 	ELSE ''  "                                                          
                        + "    END AS sPayerNme " 
                        + " , b.sCompnyNm AS sOwnrNmxx "                                                     
                        + " , b.cClientTp "                                                                  
                        + " , TRIM(IFNULL(CONCAT( IFNULL(CONCAT(d.sHouseNox,' ') , ''), "                    
                        + "   IFNULL(CONCAT(d.sAddressx,' ') , ''), "                                        
                        + "   IFNULL(CONCAT(e.sBrgyName,' '), ''),  "                                        
                        + "   IFNULL(CONCAT(f.sTownName, ', '),''), "                                        
                        + "   IFNULL(CONCAT(g.sProvName),'') ), '')) AS sAddressx "                          
                        + " , CONCAT(k.sBankName, ' ', h.sBrBankNm) AS sBankName  "                          
                        + " , CONCAT(IFNULL(h.sAddressx, ''), i.sTownName, j.sProvName) AS sBankAddr "       
                        + " , CONCAT(o.sInsurNme, ' ', l.sBrInsNme) AS sInsNamex "                           
                        + " , CONCAT(IFNULL(l.sAddressx, ''), m.sTownName, n.sProvName) AS sInsAddrx "       
                        + " FROM cashier_receivables a  "                                                    
                        + " LEFT JOIN client_master b ON b.sClientID = a.sClientID "                         
                        + " LEFT JOIN client_address c ON c.sClientID = a.sClientID AND c.cPrimaryx = 1 "    
                        + " LEFT JOIN addresses d ON d.sAddrssID = c.sAddrssID  "                            
                        + " LEFT JOIN barangay e ON e.sBrgyIDxx = d.sBrgyIDxx   "                            
                        + " LEFT JOIN towncity f ON f.sTownIDxx = d.sTownIDxx   "                            
                        + " LEFT JOIN province g ON g.sProvIDxx = f.sProvIDxx       "                        
                        + " LEFT JOIN banks_branches h ON h.sBrBankID = a.sBrBankCd "                        
                        + " LEFT JOIN towncity i ON i.sTownIDxx = h.sTownIDxx "                              
                        + " LEFT JOIN province j ON j.sProvIDxx = i.sProvIDxx "                              
                        + " LEFT JOIN banks k ON k.sBankIDxx = h.sBankIDxx    "                              
                        + " LEFT JOIN insurance_company_branches l ON l.sBrInsIDx = a.sBrInsCde "            
                        + " LEFT JOIN towncity m ON m.sTownIDxx = l.sTownIDxx "                              
                        + " LEFT JOIN province n ON n.sProvIDxx = m.sProvIDxx "                              
                        + " LEFT JOIN insurance_company o ON o.sInsurIDx = l.sInsurIDx "
                        + " WHERE 0=1";
        
        
        ResultSet loRS = instance.executeQuery(lsSQL);
        try {
            if (MiscUtil.resultSet2XML(instance, loRS, System.getProperty("sys.default.path.metadata"), "cashier_receivables", "")){
                System.out.println("ResultSet exported.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    
}
