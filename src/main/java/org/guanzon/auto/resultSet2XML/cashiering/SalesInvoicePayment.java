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
public class SalesInvoicePayment  {
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
        
        System.setProperty("sys.default.path.metadata", "D:/GGC_Maven_Systems/config/metadata/Model_SalesInvoice_Payment.xml");
        
        
        String lsSQL =   " SELECT "           
                        + "    a.sTransNox "         
                        + "  , a.sPayModex "         
                        + "  , a.nPayAmtxx "         
                        + "  , a.sPayTrnCD "          
                        + "  , b.sBankIDxx "          
                        + "  , b.sCardNoxx "          
                        + "  , b.sApprovNo "          
                        + "  , b.sTraceNox "          
                        + "  , b.sRemarksx " 
                        + "  , c.sBankName "
                        + "  , d.sGCertNox "             
                        + "  , d.sPayLoadx AS sGCPayLod "
                        + "  , d.sRemarksx AS sGCRemrks "
                        + " FROM si_master_payment a "
                        + " LEFT JOIN credit_card_trans b ON b.sTransNox = a.sPayTrnCD AND b.cTranStat <> " + SQLUtil.toSQL(TransactionStatus.STATE_CANCELLED)
                        + " LEFT JOIN banks c ON c.sBankIDxx = b.sBankIDxx "
                        + " LEFT JOIN gift_check d ON d.sTransNox = a.sPayTrnCD  "
//                        + "    a.sTransNox "         
//                        + "  , a.sPayModex "         
//                        + "  , a.nPayAmtxx "         
//                        + "  , a.sPayTrnCD "          
//                        + "  , b.sBankIDxx "          
//                        + "  , b.sCardNoxx "          
//                        + "  , b.sApprovNo "          
//                        + "  , b.sTraceNox "          
//                        + "  , b.sRemarksx " 
//                        + "  , c.sBankName "
//                        + " FROM si_master_payment a "
//                        + " LEFT JOIN credit_card_trans b ON b.sTransNox = a.sPayTrnCD AND b.cTranStat <> " + SQLUtil.toSQL(TransactionStatus.STATE_CANCELLED)
//                        + " LEFT JOIN banks c ON c.sBankIDxx = b.sBankIDxx "
                        + " WHERE 0=1";
        
        
        ResultSet loRS = instance.executeQuery(lsSQL);
        try {
            if (MiscUtil.resultSet2XML(instance, loRS, System.getProperty("sys.default.path.metadata"), "si_master_payment", "")){
                System.out.println("ResultSet exported.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    
}
