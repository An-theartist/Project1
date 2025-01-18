/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */

/**
 *
 * @author anho2
 */
import DB.ConnectDB;
import Login.Message;
import Login.PanelCover;
import Login.PanelLogin;
import MainGV.GVHome;
import SearchHS.HSHome;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import net.miginfocom.swing.MigLayout;
import org.jdesktop.animation.timing.Animator;
import org.jdesktop.animation.timing.TimingTarget;
import org.jdesktop.animation.timing.TimingTargetAdapter;

public class Main extends javax.swing.JFrame {

    private MigLayout layout;
    private PanelCover cover;
    private PanelLogin login;
    private boolean isLogin;
    private final double addSize=30;
    private final double coverSize=40;
    private final double loginSize=60;
    private final DecimalFormat df = new DecimalFormat("##0.###");
    

    public Main() {
        initComponents();
        init();
    }
    private void init(){
        layout=new MigLayout("fill, insets 0");
        cover=new PanelCover();
        
        
        ActionListener eventGV = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                GV();
            }
        };
        ActionListener eventHS = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                HS();
            }
        };
        login= new PanelLogin(eventHS,eventGV);
        
        TimingTarget target=new TimingTargetAdapter(){
          @Override
          public void timingEvent(float fraction){
              double fractionCover;
              double fractionLogin;
              double size = coverSize;
              if(fraction<=0.5f){
                  size += fraction * addSize;
              } else{
                  size += addSize - fraction * addSize;
              }
              
              if(isLogin) {
                  fractionCover = 1f-fraction;
                  fractionLogin = fraction;
                  if(fraction>=0.5f){
                      cover.HSRight(fractionCover * 100);
                  } else{
                      cover.GVRight(fractionLogin * 100);
                  }
              } else{
                  fractionCover = fraction;
                  fractionLogin = 1f-fraction;
                  if(fraction<=0.5f){
                      cover.HSLeft(fraction * 100);
                  } else {
                      cover.GVLeft((1f-fraction) * 100);
                  }
              }
              if(fraction>=0.5f){
                  login.showHS(isLogin);
              }
              
              fractionCover = Double.valueOf(df.format(fractionCover));
              fractionLogin = Double.valueOf(df.format(fractionLogin));
              layout.setComponentConstraints(cover,"width "+size+"%, pos "+fractionCover+"al 0 n 100%");
              layout.setComponentConstraints(login,"width "+loginSize+"%, pos "+fractionLogin+"al 0 n 100%");
              bg.revalidate();
          }
          
          @Override
          public void end() {
            isLogin = !isLogin;
            
          }
          
        };
        Animator animator = new Animator(1000,target);
        animator.setAcceleration(0.5f);
        animator.setDeceleration(0.5f);
        animator.setResolution(0);
        bg.setLayout(layout);
        bg.add(cover,"width " + coverSize + "%, pos 0al 0 n 100%");
        bg.add(login,"width " + loginSize + "%, pos 1al 0 n 100%"); // 1a1 as 100%
        
        cover.addEvent(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae){
                if(!animator.isRunning()){
                    animator.start();
                }
            }
        });
    }
    
     private void HS(){
        String MSSV = login.getMSSV();
        Connection con = null; // Khai báo kết nối
        PreparedStatement pst = null; // Khai báo PreparedStatement
        try {
            // Khởi tạo kết nối với cơ sở dữ liệu
            con = ConnectDB.getConnection();
            pst = con.prepareStatement("SELECT * FROM sv WHERE MSSV = ?");
            pst.setString(1, MSSV);
            ResultSet rs = pst.executeQuery();
            if(rs.next()){
                HSHome home = new HSHome(MSSV);
                home.setMSSV(MSSV);  // Truyền MSSV vào đối tượng HSHome
                home.setVisible(true);
                showMessage(Message.MessageType.SUCCESS, "Tra cứu thành công");
                setEnabled(false);
            }   else{
                showMessage(Message.MessageType.ERROR, "Không tồn tại MSSV");
            }
        }   catch(SQLException ex){
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Lỗi khi kiểm tra: " + ex.getMessage());
        }
     }
    
    private void GV() {
        String user = login.getUser();
        String password = login.getPassword();
        
        if (user.equals("admin") && password.equals("admin123")) {
                this.dispose();
                new GVHome().setVisible(true);
            } else {
                showMessage(Message.MessageType.ERROR, "Tài khoản hoặc mật khẩu sai");
           }
    }
    
    private void showMessage(Message.MessageType messageType, String message) {
        Message ms = new Message();
        ms.showMessage(messageType, message);
        TimingTarget target = new TimingTargetAdapter() {
            @Override
            public void begin() {
                if (!ms.isShow()) {
                    bg.add(ms, "pos 0.5al -30", 0); //  Insert to bg fist index 0
                    ms.setVisible(true);
                    bg.repaint();
                }
            }

            @Override
            public void timingEvent(float fraction) {
                float f;
                if (ms.isShow()) {
                    f = 40 * (1f - fraction);
                } else {
                    f = 40 * fraction;
                }
                layout.setComponentConstraints(ms, "pos 0.5al " + (int) (f - 30));
                bg.repaint();
                bg.revalidate();
            }

            @Override
            public void end() {
                if (ms.isShow()) {
                    bg.remove(ms);
                    bg.repaint();
                    bg.revalidate();
                } else {
                    ms.setShow(true);
                }
            }
        };
        Animator animator = new Animator(300, target);
        animator.setResolution(0);
        animator.setAcceleration(0.5f);
        animator.setDeceleration(0.5f);
        animator.start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                    animator.start();
                } catch (InterruptedException e) {
                    System.err.println(e);
                }
            }
        }).start();
    }
    

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        bg = new javax.swing.JLayeredPane();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        bg.setBackground(new java.awt.Color(255, 255, 255));
        bg.setOpaque(true);

        javax.swing.GroupLayout bgLayout = new javax.swing.GroupLayout(bg);
        bg.setLayout(bgLayout);
        bgLayout.setHorizontalGroup(
            bgLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 700, Short.MAX_VALUE)
        );
        bgLayout.setVerticalGroup(
            bgLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 500, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(bg)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(bg)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Main().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLayeredPane bg;
    // End of variables declaration//GEN-END:variables
}

