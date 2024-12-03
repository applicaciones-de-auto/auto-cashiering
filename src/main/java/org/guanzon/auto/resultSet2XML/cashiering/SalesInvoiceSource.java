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
public class SalesInvoiceSource  {
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
        
        System.setProperty("sys.default.path.metadata", "D:/GGC_Maven_Systems/config/metadata/Model_SalesInvoice_Source.xml");
        
        
        String lsSQL =   " SELECT "                 
                        + "    a.sTransNox "         
                        + "  , a.sReferNox "         
                        + "  , a.sSourceCD "         
                        + "  , a.sSourceNo "         
                        + "  , a.sTranType "         
                        + "  , a.nTranAmtx "         
                        + "  , a.nDiscount "         
                        + "  , a.nAdvusedx "         
                        + "  , a.nNetAmtxx "         
                        + "  , a.nEntryNox "
                        + "  , IFNULL( aa.sReferNox,IFNULL(c.sVSPNOxxx, IFNULL(d.sReferNox, IFNULL(e.sReferNox,'')))) AS sFormNoxx "
                        + "  , b.sSourceCD AS sDescript "
                        + " FROM si_master_source a "                                              
                        + " LEFT JOIN udr_master aa ON aa.sTransNox = a.sSourceNo "                
                        + " LEFT JOIN cashier_receivables b ON b.sTransNox = a.sSourceNo "         
                        + " LEFT JOIN vsp_master c ON c.sTransNox = b.sReferNox "                  
                        + " LEFT JOIN customer_inquiry_reservation d ON d.sTransNox = b.sReferNox "
                        + " LEFT JOIN insurance_policy_application e ON e.sTransNox = b.sReferNox "
                        + " WHERE 0=1";
        
        
        ResultSet loRS = instance.executeQuery(lsSQL);
        try {
            if (MiscUtil.resultSet2XML(instance, loRS, System.getProperty("sys.default.path.metadata"), "si_master_detail", "")){
                System.out.println("ResultSet exported.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    
}
