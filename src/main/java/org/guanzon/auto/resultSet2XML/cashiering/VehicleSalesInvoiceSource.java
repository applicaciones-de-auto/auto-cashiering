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
public class VehicleSalesInvoiceSource {
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
        
        System.setProperty("sys.default.path.metadata", "D:/GGC_Maven_Systems/config/metadata/Model_VehicleSalesInvoice.xml");
        
        
        String lsSQL =    " SELECT "               
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
                          /*VDR INFORMATION*/
                        + "  , b.sReferNox AS sUDRNoxxx " 
                        + "  , b.cCustType AS cCustType "     
                          /*VEHICLE INFORMATION*/                                                         
                        + " , c.sCSNoxxxx "                                                               
                        + " , d.sPlateNox "                                                               
                        + " , c.sFrameNox "                                                               
                        + " , c.sEngineNo "                                                               
                        + " , c.sKeyNoxxx "                                                               
                        + " , e.sDescript AS sVhclFDsc " 
                        + " , TRIM(CONCAT_WS(' ',f.sMakeDesc, g.sModelDsc, h.sTypeDesc, e.sTransMsn, e.nYearModl )) AS sVhclDesc "
                        + " , i.sColorDsc "
                        + " , k.sCompnyNm AS sCoCltNmx "                                               
                        + " , m.sCompnyNm AS sSENamexx "  
                        + " FROM si_master_source a "   
                          /*VDR INFORMATION*/
                        + " LEFT JOIN udr_master b ON b.sTransNox = a.sReferNox"     
                         /*VEHICLE INFORMATION*/                                                          
                        + " LEFT JOIN vehicle_serial c ON c.sSerialID = b.sSerialID "                     
                        + " LEFT JOIN vehicle_serial_registration d ON d.sSerialID = b.sSerialID "        
                        + " LEFT JOIN vehicle_master e ON e.sVhclIDxx = c.sVhclIDxx " 
                        + " LEFT JOIN vehicle_make f ON f.sMakeIDxx = e.sMakeIDxx  "
                        + " LEFT JOIN vehicle_model g ON g.sModelIDx = e.sModelIDx "
                        + " LEFT JOIN vehicle_type h ON h.sTypeIDxx = e.sTypeIDxx  "
                        + " LEFT JOIN vehicle_color i ON i.sColorIDx = e.sColorIDx " 
                         /*CO CLIENT*/                                                  
                        + " LEFT JOIN vsp_master j ON j.sTransNox = b.sSourceNo "                                        
                        + " LEFT JOIN client_master k ON k.sClientID = j.sCoCltIDx "  
                        + " LEFT JOIN customer_inquiry l ON l.sTransNox = j.sInqryIDx " 
                        + " LEFT JOIN ggc_isysdbf.client_master m ON m.sClientID = l.sEmployID    "
                        + " WHERE 0=1";
        
        
        ResultSet loRS = instance.executeQuery(lsSQL);
        try {
            if (MiscUtil.resultSet2XML(instance, loRS, System.getProperty("sys.default.path.metadata"), "si_master_source", "")){
                System.out.println("ResultSet exported.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
}
