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

/**
 *
 * @author Arsiela
 */
public class StatementOfAccountMaster {
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
        
        System.setProperty("sys.default.path.metadata", "D:/GGC_Maven_Systems/config/metadata/Model_StatementOfAccount.xml");
        
        
        String lsSQL =    " SELECT "                                                                                                                       
                        + "   a.sTransNox "                                                                                                                
                        + " , a.dTransact "                                                                                                                
                        + " , a.sClientID "                                                                                                                
                        + " , a.sBankIDxx "                                                                                                                
                        + " , a.sInsurIDx "                                                                                                                
                        + " , a.sTermIDxx "                                                                                                                
                        + " , a.nTranTotl "                                                                                                                
                        + " , a.nAmtPaidx "                                                                                                                
                        + " , a.sRemarksx "                                                                                                                
                        + " , a.cTranStat "                                                                                                                
                        + " , a.dApproved "                                                                                                                
                        + " , a.dPostedxx "                                                                                                                
                        + " , a.nEntryNox "                                                                                                                
                        + " , a.sModified "                                                                                                                
                        + " , a.dModified "            
                        + " , CASE "
        //                + "     WHEN a.sClientID != null THEN '' " //ASSOCIATE
                        + "     WHEN a.sBankIDxx != null THEN CONCAT(k.sBankName, ' ', h.sBrBankNm) " //BANK
                        + "     WHEN a.sClientID != null THEN b.sCompnyNm " //CUSTOMER
                        + "     WHEN a.sInsurIDx != null THEN CONCAT(o.sInsurNme, ' ', l.sBrInsNme) " //INSURANCE
        //                + "     WHEN a.cPayerCde != null THEN '' " //SUPPLIER                                  
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
                        + " FROM soa_master a "                                                                                                           
                        + " LEFT JOIN client_master b ON b.sClientID = a.sClientID "                                                                       
                        + " LEFT JOIN client_address c ON c.sClientID = a.sClientID AND c.cPrimaryx = 1 "                                                   
                        + " LEFT JOIN addresses d ON d.sAddrssID = c.sAddrssID "                                                                           
                        + " LEFT JOIN barangay e ON e.sBrgyIDxx = d.sBrgyIDxx  "                                                                           
                        + " LEFT JOIN towncity f ON f.sTownIDxx = d.sTownIDxx  "                                                                           
                        + " LEFT JOIN province g ON g.sProvIDxx = f.sProvIDxx  "                                                                           
                        + " LEFT JOIN banks_branches h ON h.sBrBankID = a.sBankIDxx "                                                                      
                        + " LEFT JOIN towncity i ON i.sTownIDxx = h.sTownIDxx  "                                                                           
                        + " LEFT JOIN province j ON j.sProvIDxx = i.sProvIDxx  "                                                                           
                        + " LEFT JOIN banks k ON k.sBankIDxx = h.sBankIDxx     "                                                                           
                        + " LEFT JOIN insurance_company_branches l ON l.sBrInsIDx = a.sInsurIDx "                                                          
                        + " LEFT JOIN towncity m ON m.sTownIDxx = l.sTownIDxx  "                                                                           
                        + " LEFT JOIN province n ON n.sProvIDxx = m.sProvIDxx  "                                                                           
                        + " LEFT JOIN insurance_company o ON o.sInsurIDx = l.sInsurIDx  " 
                        + " WHERE 0=1";
        
        
        ResultSet loRS = instance.executeQuery(lsSQL);
        try {
            if (MiscUtil.resultSet2XML(instance, loRS, System.getProperty("sys.default.path.metadata"), "soa_master", "")){
                System.out.println("ResultSet exported.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
}
