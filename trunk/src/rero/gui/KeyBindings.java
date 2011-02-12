package rero.gui;

import java.awt.*;
import java.awt.event.*;

import java.util.*;
import rero.client.*;

import javax.swing.SwingUtilities;

import rero.bridges.bind.BindEnvironment;

public class KeyBindings implements KeyEventDispatcher
{
   public static boolean is_dialog_active = false; // bad idea I know, but what can ya do? - used so scripted key bindings don't 
                                                   // mess up dialog keyboard shortcuts.

   protected SessionManager session;
   protected HashMap        binds;   // a hashmap referencing the bound reference for each "session"

   public KeyBindings(SessionManager s)
   {
       session = s;
       binds   = new HashMap();
   }

   public void removeSession(IRCSession session)
   {
       Iterator i = binds.keySet().iterator();
       while (i.hasNext())
       {
          Object temp = i.next();
          if (temp == session)
             i.remove();
       }
   }

   public boolean dispatchKeyEvent(KeyEvent ev)
   {
       if (ev.getID() != KeyEvent.KEY_PRESSED || is_dialog_active)
       {
           return false;
       }

       BindEnvironment temp = (BindEnvironment)binds.get(session.getActiveSession());

       if (temp == null)
       {
          binds.put(session.getActiveSession(), session.getActiveSession().getCapabilities().getDataStructure("bindBridge"));
          temp = (BindEnvironment)binds.get(session.getActiveSession());
       }

       StringBuffer description = new StringBuffer();
       if (ev.getModifiers() != 0)
       {
          description.append(getKeyModifiers(ev));
       }
       
       description.append(getKeyText(ev));

       if (temp != null && temp.isBound(description.toString()))
       {
          SwingUtilities.invokeLater(new ExecuteBind(description.toString(), temp));
          ev.consume();
          return true;
       }

       if (ev.isControlDown() && ev.getKeyCode() == 9) 
       {
          ev.consume();
          return true;
       }       

       return false;
   }

   private static String getKeyModifiers(KeyEvent ev)
   {
       StringBuffer modifiers = new StringBuffer();
       if (ev.isShiftDown())   modifiers.append("Shift+");
       if (ev.isControlDown()) modifiers.append("Ctrl+");
       if (ev.isAltDown())     modifiers.append("Alt+");
       if (ev.isMetaDown())    modifiers.append("Meta+");

       return modifiers.toString();
   }

   private static String getKeyText(KeyEvent ev)
   {
      switch (ev.getKeyCode())
      {
         case KeyEvent.VK_ACCEPT:
            return "Accept";
         case KeyEvent.VK_BACK_QUOTE:
            return "Back_Quote";
         case KeyEvent.VK_BACK_SPACE:
            return "Backspace";
         case KeyEvent.VK_CAPS_LOCK:
            return "Caps_Lock";
         case KeyEvent.VK_CLEAR:
            return "Clear";
         case KeyEvent.VK_CONVERT:
            return "Convert";
         case KeyEvent.VK_DELETE:
            return "Delete";
         case KeyEvent.VK_DOWN:
            return "Down";
         case KeyEvent.VK_END:
            return "End";
         case KeyEvent.VK_ENTER:
            return "Enter";
         case KeyEvent.VK_ESCAPE:
            return "Escape";
         case KeyEvent.VK_F1:
            return "F1";
         case KeyEvent.VK_F2:
            return "F2";
         case KeyEvent.VK_F3:
            return "F3";
         case KeyEvent.VK_F4:
            return "F4";
         case KeyEvent.VK_F5:
            return "F5";
         case KeyEvent.VK_F6:
            return "F6";
         case KeyEvent.VK_F7:
            return "F7";
         case KeyEvent.VK_F8:
            return "F8";
         case KeyEvent.VK_F9:
            return "F9";
         case KeyEvent.VK_F10:
            return "F10";
         case KeyEvent.VK_F11:
            return "F11";
         case KeyEvent.VK_F12:
            return "F12";
         case KeyEvent.VK_FINAL:
            return "Final";
         case KeyEvent.VK_HELP:
            return "Help";
         case KeyEvent.VK_HOME:
            return "Home";
         case KeyEvent.VK_INSERT:
            return "Insert";
         case KeyEvent.VK_LEFT:
            return "Left";
         case KeyEvent.VK_NUM_LOCK:
            return "Num_Lock";
         case KeyEvent.VK_MULTIPLY:
            return "NumPad_*";
         case KeyEvent.VK_PLUS:
            return "NumPad_+";
         case KeyEvent.VK_COMMA:
            return "NumPad_,";
         case KeyEvent.VK_SUBTRACT:
            return "NumPad_-";
         case KeyEvent.VK_PERIOD:
            return "Period";
         case KeyEvent.VK_SLASH:
            return "NumPad_/";
         case KeyEvent.VK_PAGE_DOWN:
            return "Page_Down";
         case KeyEvent.VK_PAGE_UP:
            return "Page_Up";
         case KeyEvent.VK_PAUSE:
            return "Pause";
         case KeyEvent.VK_PRINTSCREEN:
            return "Print_Screen";
         case KeyEvent.VK_QUOTE:
            return "Quote";
         case KeyEvent.VK_RIGHT:
            return "Right";
         case KeyEvent.VK_SCROLL_LOCK:
            return "Scroll_Lock";
         case KeyEvent.VK_SPACE:
            return "Space";
         case KeyEvent.VK_TAB:
            return "Tab";
         case KeyEvent.VK_UP:
            return "Up";
         default:
            return ev.getKeyText(ev.getKeyCode());
      }
   }

   private static class ExecuteBind implements Runnable
   {
       private BindEnvironment bind;
       private String          desc;

       public ExecuteBind(String _desc, BindEnvironment _bind)
       {
          desc = _desc;
          bind = _bind;
       }

       public void run()
       {
          bind.processEvent(desc);
       }
   } 
}
