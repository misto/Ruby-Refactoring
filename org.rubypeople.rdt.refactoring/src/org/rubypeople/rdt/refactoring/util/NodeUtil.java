package org.rubypeople.rdt.refactoring.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.jruby.ast.IterNode;
import org.jruby.ast.MethodDefNode;
import org.jruby.ast.Node;
import org.jruby.parser.StaticScope;

// XXX These methods should be removed and an interface to the nodes should be added, we'll do that later, after the first patches are applied
public class NodeUtil {
	public static boolean hasScope(Node node) {
		Method[] methods = node.getClass().getMethods();
		for (int i = 0; i < methods.length; i++) {
			if (methods[i].getName().equals("getScope") || methods[i].equals("getStaticScope")) {
				return true;
			}
		}
		return false;
	}
	
	public static Node getBody(Node node) {
		try {
			Method method = node.getClass().getMethod("getBodyNode", new Class[]{});
			return (Node) method.invoke(node, new Object[]{});
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static StaticScope getScope(Node node) {
		
		String methodName = "getStaticScope";
		if(node instanceof MethodDefNode || node instanceof IterNode) {
			methodName = "getScope";
		}
		
		try {
			Method method = node.getClass().getMethod(methodName, new Class[]{});
			return (StaticScope) method.invoke(node, new Object[]{});
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static boolean nodeAssignableFrom(Node n, Class... klasses) {
		if(n == null) {
			return false;
		}
		for (Class<?> klass : klasses) {
			if (klass.isAssignableFrom(n.getClass())) {
				return true;
			}
		}
		return false;
	}
}
