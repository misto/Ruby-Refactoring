package org.rubypeople.rdt.core;


public class Flags {
    
    public static boolean isPublic(int flags) {
        return flags == IMethod.PUBLIC;
    }

    public static boolean isProtected(int flags) {
        return flags == IMethod.PROTECTED;
    }

    public static boolean isPrivate(int modifierFlags) {
        return modifierFlags == IMethod.PRIVATE;
    }

}
