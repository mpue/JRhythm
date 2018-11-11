package de.pueski.jrhythm.core;

import java.util.HashMap;

import org.lwjgl.input.Keyboard;

public class KeyboardMap extends HashMap<Integer, Character> {

	public KeyboardMap() {
		
		put( Keyboard.KEY_ESCAPE,Character.valueOf((char)0x01));
		put( Keyboard.KEY_1,'1');
		put( Keyboard.KEY_2,'2'); //0x03;
		put( Keyboard.KEY_3,'3'); //0x04;
		put( Keyboard.KEY_4,'4'); //0x05;
		put( Keyboard.KEY_5,'5'); //0x06;
		put( Keyboard.KEY_6,'6'); //0x07;
		put( Keyboard.KEY_7,'7'); //0x08;
		put( Keyboard.KEY_8,'8'); //0x09;
		put( Keyboard.KEY_9,'9'); //0x0A;
		put( Keyboard.KEY_0,'0'); //0x0B;
		put( Keyboard.KEY_MINUS           ,'-'); // 0x0C; /* - on main keyboard */
		put( Keyboard.KEY_EQUALS          ,'='); // 0x0D;
		put( Keyboard.KEY_BACK            ,Character.valueOf((char)0x0E)); // 0x0E; /* backspace */
//		put( Keyboard.KEY_TAB             ,Character.valueOf((char)0x0F)); // 0x0F;
		put( Keyboard.KEY_Q,'Q'); //0x10;
		put( Keyboard.KEY_W,'W'); //0x11;
		put( Keyboard.KEY_E,'E'); //0x12;
		put( Keyboard.KEY_R,'R'); //0x13;
		put( Keyboard.KEY_T,'T'); //0x14;
		put( Keyboard.KEY_Y,'Y'); //0x15;
		put( Keyboard.KEY_U,'U'); //0x16;
		put( Keyboard.KEY_I,'I'); //0x17;
		put( Keyboard.KEY_O,'O'); //0x18;
		put( Keyboard.KEY_P,'P'); //0x19;
		put( Keyboard.KEY_LBRACKET        ,'{'); // 0x1A;
		put( Keyboard.KEY_RBRACKET        ,'}'); // 0x1B;
//		put( Keyboard.KEY_RETURN          ,Character.valueOf((char)0x1C)); // 0x1C; /* Enter on main keyboard */
//		put( Keyboard.KEY_LCONTROL        ,Character.valueOf((char)0x1DE)); // 0x1D;
		put( Keyboard.KEY_A,'A'); //0x1E;
		put( Keyboard.KEY_S,'S'); //0x1F;
		put( Keyboard.KEY_D,'D'); //0x20;
		put( Keyboard.KEY_F,'F'); //0x21;
		put( Keyboard.KEY_G,'G'); //0x22;
		put( Keyboard.KEY_H,'H'); //0x23;
		put( Keyboard.KEY_J,'J'); //0x24;
		put( Keyboard.KEY_K,'K'); //0x25;
		put( Keyboard.KEY_L,'L'); //0x26;
		put( Keyboard.KEY_SEMICOLON       ,';'); // 0x27;
		put( Keyboard.KEY_APOSTROPHE      ,'\''); // 0x28;
		put( Keyboard.KEY_GRAVE           ,'`'); // 0x29; /* accent grave */
//		put( Keyboard.KEY_LSHIFT          ,Character.valueOf((char)0x2A)); // 0x2A;
		put( Keyboard.KEY_BACKSLASH       ,'\\'); // 0x2B;
		put( Keyboard.KEY_Z,'Z'); //0x2C;
		put( Keyboard.KEY_X,'X'); //0x2D;
		put( Keyboard.KEY_C,'C'); //0x2E;
		put( Keyboard.KEY_V,'V'); //0x2F;
		put( Keyboard.KEY_B,'B'); //0x30;
		put( Keyboard.KEY_N,'N'); //0x31;
		put( Keyboard.KEY_M,'M'); //0x32;
		put( Keyboard.KEY_COMMA           ,','); // 0x33;
		put( Keyboard.KEY_PERIOD          ,'.'); // 0x34; /* . on main keyboard */
		put( Keyboard.KEY_SLASH           ,'/'); // 0x35; /* / on main keyboard */
//		put( Keyboard.KEY_RSHIFT          ,Character.valueOf((char)0x36)); // 0x36;
		put( Keyboard.KEY_MULTIPLY        ,'*'); // 0x37; /* * on numeric keypad */
//		put( Keyboard.KEY_LMENU           ,Character.valueOf((char)0x38)); // 0x38; /* left Alt */
		put( Keyboard.KEY_SPACE           ,' '); // 0x39;
//		put( Keyboard.KEY_CAPITAL         ,Character.valueOf((char)0x3A)); // 0x3A;
//		put( Keyboard.KEY_F1              ,Character.valueOf((char)0x3B)); // 0x3B;
//		put( Keyboard.KEY_F2              ,Character.valueOf((char)0x3C)); // 0x3C;
//		put( Keyboard.KEY_F3              ,Character.valueOf((char)0x3D)); // 0x3D;
//		put( Keyboard.KEY_F4              ,Character.valueOf((char)0x3E)); // 0x3E;
//		put( Keyboard.KEY_F5              ,Character.valueOf((char)0x3F)); // 0x3F;
//		put( Keyboard.KEY_F6              ,Character.valueOf((char)0x40)); // 0x40;
//		put( Keyboard.KEY_F7              ,''); // 0x41;
//		put( Keyboard.KEY_F8              ,''); // 0x42;
//		put( Keyboard.KEY_F9              ,''); // 0x43;
//		put( Keyboard.KEY_F10             ,''); // 0x44;
//		put( Keyboard.KEY_NUMLOCK         ,''); // 0x45;
//		put( Keyboard.KEY_SCROLL          ,''); // 0x46; /* Scroll Lock */
//		put( Keyboard.KEY_NUMPAD7         ,''); // 0x47;
//		put( Keyboard.KEY_NUMPAD8         ,''); // 0x48;
//		put( Keyboard.KEY_NUMPAD9         ,''); // 0x49;
//		put( Keyboard.KEY_SUBTRACT        ,''); // 0x4A; /* - on numeric keypad */
//		put( Keyboard.KEY_NUMPAD4         ,''); // 0x4B;
//		put( Keyboard.KEY_NUMPAD5         ,''); // 0x4C;
//		put( Keyboard.KEY_NUMPAD6         ,''); // 0x4D;
//		put( Keyboard.KEY_ADD             ,''); // 0x4E; /* + on numeric keypad */
//		put( Keyboard.KEY_NUMPAD1         ,''); // 0x4F;
//		put( Keyboard.KEY_NUMPAD2         ,''); // 0x50;
//		put( Keyboard.KEY_NUMPAD3         ,''); // 0x51;
//		put( Keyboard.KEY_NUMPAD0         ,''); // 0x52;
//		put( Keyboard.KEY_DECIMAL         ,''); // 0x53; /* . on numeric keypad */
		put( Keyboard.KEY_F11             ,Character.valueOf((char)0x57)); // 0x57;
//		put( Keyboard.KEY_F12             ,''); // 0x58;
//		put( Keyboard.KEY_F13             ,''); // 0x64; /*                     (NEC PC98) */
//		put( Keyboard.KEY_F14             ,''); // 0x65; /*                     (NEC PC98) */
//		put( Keyboard.KEY_F15             ,''); // 0x66; /*                     (NEC PC98) */
//		put( Keyboard.KEY_KANA            ,''); // 0x70; /* (Japanese keyboard)            */
//		put( Keyboard.KEY_CONVERT         ,''); // 0x79; /* (Japanese keyboard)            */
//		put( Keyboard.KEY_NOCONVERT       ,''); // 0x7B; /* (Japanese keyboard)            */
//		put( Keyboard.KEY_YEN             ,''); // 0x7D; /* (Japanese keyboard)            */
//		put( Keyboard.KEY_NUMPADEQUALS    ,''); // 0x8D; /* ,''); // on numeric keypad (NEC PC98) */
//		put( Keyboard.KEY_CIRCUMFLEX      ,''); // 0x90; /* (Japanese keyboard)            */
//		put( Keyboard.KEY_AT              ,''); // 0x91; /*                     (NEC PC98) */
//		put( Keyboard.KEY_COLON           ,''); // 0x92; /*                     (NEC PC98) */
//		put( Keyboard.KEY_UNDERLINE       ,''); // 0x93; /*                     (NEC PC98) */
//		put( Keyboard.KEY_KANJI           ,''); // 0x94; /* (Japanese keyboard)            */
//		put( Keyboard.KEY_STOP            ,''); // 0x95; /*                     (NEC PC98) */
//		put( Keyboard.KEY_AX              ,''); // 0x96; /*                     (Japan AX) */
//		put( Keyboard.KEY_UNLABELED       ,''); // 0x97; /*                        (J3100) */
//		put( Keyboard.KEY_NUMPADENTER     ,''); // 0x9C; /* Enter on numeric keypad */
//		put( Keyboard.KEY_RCONTROL        ,''); // 0x9D;
//		put( Keyboard.KEY_NUMPADCOMMA     ,''); // 0xB3; /* , on numeric keypad (NEC PC98) */
//		put( Keyboard.KEY_DIVIDE          ,''); // 0xB5; /* / on numeric keypad */
//		put( Keyboard.KEY_SYSRQ           ,''); // 0xB7;
//		put( Keyboard.KEY_RMENU           ,''); // 0xB8; /* right Alt */
//		put( Keyboard.KEY_PAUSE           ,''); // 0xC5; /* Pause */
//		put( Keyboard.KEY_HOME            ,''); // 0xC7; /* Home on arrow keypad */
//		put( Keyboard.KEY_UP              ,''); // 0xC8; /* UpArrow on arrow keypad */
//		put( Keyboard.KEY_PRIOR           ,''); // 0xC9; /* PgUp on arrow keypad */
//		put( Keyboard.KEY_LEFT            ,''); // 0xCB; /* LeftArrow on arrow keypad */
//		put( Keyboard.KEY_RIGHT           ,''); // 0xCD; /* RightArrow on arrow keypad */
//		put( Keyboard.KEY_END             ,''); // 0xCF; /* End on arrow keypad */
//		put( Keyboard.KEY_DOWN            ,''); // 0xD0; /* DownArrow on arrow keypad */
//		put( Keyboard.KEY_NEXT            ,''); // 0xD1; /* PgDn on arrow keypad */
//		put( Keyboard.KEY_INSERT          ,''); // 0xD2; /* Insert on arrow keypad */
//		put( Keyboard.KEY_DELETE          ,''); // 0xD3; /* Delete on arrow keypad */
//		put( Keyboard.KEY_LMETA            ,''); // 0xDB; /* Left Windows/Option key */		
		
	}

}
