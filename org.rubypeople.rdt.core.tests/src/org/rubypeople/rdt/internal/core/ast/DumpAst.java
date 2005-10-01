package org.rubypeople.rdt.internal.core.ast;

import java.io.InputStreamReader;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.jruby.ast.Node;
import org.jruby.ast.visitor.DefaultIteratorVisitor;
import org.jruby.ast.visitor.NodeVisitor;
import org.rubypeople.eclipse.shams.resources.ShamFile;
import org.rubypeople.rdt.internal.core.parser.RubyParser;

public class DumpAst {

    private static class DumpingInvocationHandler implements InvocationHandler {

        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            Node node = (Node) args[0];
            System.out.println("Start: " + node.getClass().getName());
            
            return null;
        }
    }

    public static void main(String[] args) throws Exception {
        new DumpAst().dump("require 'x'; class Foo; end");
    }

    private void dump(String rubyCode) throws Exception {
        ShamFile file = new ShamFile("-");
        file.setContents(rubyCode);
        InputStreamReader reader = new InputStreamReader(file.getContents());
        Node rootNode = new RubyParser().parse(file, reader);
        
        NodeVisitor dumpVisitor = (NodeVisitor) Proxy.newProxyInstance(getClass().getClassLoader(), new Class[] {NodeVisitor.class}, new DumpingInvocationHandler());
        rootNode.accept(dumpVisitor);
    }

}
