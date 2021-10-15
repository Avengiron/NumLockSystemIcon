package numlocksystemicon;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.KeyEvent;
import java.awt.event.ActionEvent;
import java.awt.PopupMenu;
import java.awt.MenuItem;
import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;

/** @author Xavier */
public class NumLockSystemIcon implements NativeKeyListener {
  /** Etat de la touche NUM_LOCK */
  private boolean NUM_LOCK_STATE;
  /** Images et Tooltips lock/unlock */
  private final Image numLocked = getIcon("numLockIcon.png");
  private final Image numUnlocked = getIcon("numUnlockIcon.png");
  private final String toolLocked = "Pavé numérique verrouillé";
  private final String toolUnlocked = "Pavé numérique déverrouillé";

  public static void main(String[] args) {
    new NumLockSystemIcon();
  }
  
  public NumLockSystemIcon(){
    try {
      GlobalScreen.registerNativeHook();
    } catch (NativeHookException ex) {
      System.err.println("There was a problem registering the native hook.");
      System.err.println(ex.getMessage());
      System.exit(1);
    }
    if (!SystemTray.isSupported()) {
      System.err.println("System tray not supported");
      System.exit(1);
    } 
    
    GlobalScreen.addNativeKeyListener(this);
    SystemTray tray = SystemTray.getSystemTray();
    
    // Creation de la TrayIcon et etat initial
    TrayIcon trayIcon = new TrayIcon(numUnlocked);
    NUM_LOCK_STATE = Toolkit.getDefaultToolkit().getLockingKeyState(KeyEvent.VK_NUM_LOCK);
    if(NUM_LOCK_STATE) {
      trayIcon.setToolTip(toolLocked);
      trayIcon.setImage(numLocked);
    } else {
      trayIcon.setToolTip(toolUnlocked);
      trayIcon.setImage(numUnlocked);
    }
    
    // Creation du menu Fermer
    // Le sous-menu Fermer fait partie d'un menu Popup
    // qui s'affiche au clic droit sur la TrayIcon
    PopupMenu menu = new PopupMenu("Menu");
    MenuItem exitItem = new MenuItem("   Fermer   ");    
    exitItem.addActionListener((ActionEvent e) -> {
      // Sortie propre
      // On enleve la TrayIcon du SystemTray
      // On libere le KeyListener
      tray.remove(trayIcon);
      try {
        GlobalScreen.unregisterNativeHook();
      } catch (NativeHookException nativeHookException) {
        nativeHookException.printStackTrace();
      }
      System.exit(0);
    });
    menu.add(exitItem);
    trayIcon.setPopupMenu(menu);
    
    // Chargement de la TrayIcon dans le SystemTray
    try {
      tray.add(trayIcon);
    } catch (AWTException awte) {
      System.out.println("TrayIcon not loaded");
      System.exit(1);
    }
  }
  
  // Surcharge de toutes les methodes heritees
  // l'interface NativeKeyListener. On agit que 
  // lors de l'appui sur la touche NumLock
  @Override public void nativeKeyReleased(NativeKeyEvent e) {}
  @Override public void nativeKeyTyped(NativeKeyEvent e) {}
  @Override public void nativeKeyPressed(NativeKeyEvent e) {
    if(e.getKeyCode() == NativeKeyEvent.VC_NUM_LOCK){
      SystemTray tray = SystemTray.getSystemTray();
      TrayIcon trayIcon = tray.getTrayIcons()[0];
      
      NUM_LOCK_STATE = !NUM_LOCK_STATE;
      if(NUM_LOCK_STATE){
        trayIcon.setToolTip(toolLocked);
        trayIcon.setImage(numLocked);
      } else {
        trayIcon.setToolTip(toolUnlocked);
        trayIcon.setImage(numUnlocked);
      }
    }
  }
  
  /**
   * Charge l'image. L'image doit se trouver dans le dossier data/ 
   * @param fileName Nom de l'image
   * @return Image chargee
   */
  public static Image getIcon(String fileName) {
    return Toolkit.getDefaultToolkit().getImage("data\\" + fileName);
  }
}