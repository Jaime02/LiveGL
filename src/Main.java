import javax.swing.JFrame;
import gui.MainForm;

public class Main {
    public static void main(String[] args) {
        setStyle();

        final MainForm t = new MainForm();
        t.setExtendedState(t.getExtendedState() | JFrame.MAXIMIZED_BOTH);
        t.setVisible(true);
    }

    private static void setStyle() {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
        } catch (InstantiationException ex) {
        } catch (IllegalAccessException ex) {
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
        }
    }
}
