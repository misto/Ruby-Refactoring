package org.rubypeople.rdt.internal.codeassist;

import java.util.ArrayList;
import java.util.List;

import org.jruby.ast.AliasNode;
import org.jruby.ast.AndNode;
import org.jruby.ast.ArgsCatNode;
import org.jruby.ast.ArgsNode;
import org.jruby.ast.ArrayNode;
import org.jruby.ast.BackRefNode;
import org.jruby.ast.BeginNode;
import org.jruby.ast.BignumNode;
import org.jruby.ast.BlockArgNode;
import org.jruby.ast.BlockNode;
import org.jruby.ast.BlockPassNode;
import org.jruby.ast.BreakNode;
import org.jruby.ast.CallNode;
import org.jruby.ast.CaseNode;
import org.jruby.ast.ClassNode;
import org.jruby.ast.ClassVarAsgnNode;
import org.jruby.ast.ClassVarDeclNode;
import org.jruby.ast.ClassVarNode;
import org.jruby.ast.Colon2Node;
import org.jruby.ast.Colon3Node;
import org.jruby.ast.ConstDeclNode;
import org.jruby.ast.ConstNode;
import org.jruby.ast.DAsgnNode;
import org.jruby.ast.DRegexpNode;
import org.jruby.ast.DStrNode;
import org.jruby.ast.DSymbolNode;
import org.jruby.ast.DVarNode;
import org.jruby.ast.DXStrNode;
import org.jruby.ast.DefinedNode;
import org.jruby.ast.DefnNode;
import org.jruby.ast.DefsNode;
import org.jruby.ast.DotNode;
import org.jruby.ast.EnsureNode;
import org.jruby.ast.EvStrNode;
import org.jruby.ast.FCallNode;
import org.jruby.ast.FalseNode;
import org.jruby.ast.FixnumNode;
import org.jruby.ast.FlipNode;
import org.jruby.ast.FloatNode;
import org.jruby.ast.ForNode;
import org.jruby.ast.GlobalAsgnNode;
import org.jruby.ast.GlobalVarNode;
import org.jruby.ast.HashNode;
import org.jruby.ast.IfNode;
import org.jruby.ast.InstAsgnNode;
import org.jruby.ast.InstVarNode;
import org.jruby.ast.IterNode;
import org.jruby.ast.LocalAsgnNode;
import org.jruby.ast.LocalVarNode;
import org.jruby.ast.Match2Node;
import org.jruby.ast.Match3Node;
import org.jruby.ast.MatchNode;
import org.jruby.ast.ModuleNode;
import org.jruby.ast.MultipleAsgnNode;
import org.jruby.ast.NewlineNode;
import org.jruby.ast.NextNode;
import org.jruby.ast.NilNode;
import org.jruby.ast.Node;
import org.jruby.ast.NotNode;
import org.jruby.ast.NthRefNode;
import org.jruby.ast.OpAsgnAndNode;
import org.jruby.ast.OpAsgnNode;
import org.jruby.ast.OpAsgnOrNode;
import org.jruby.ast.OpElementAsgnNode;
import org.jruby.ast.OptNNode;
import org.jruby.ast.OrNode;
import org.jruby.ast.PostExeNode;
import org.jruby.ast.RedoNode;
import org.jruby.ast.RegexpNode;
import org.jruby.ast.RescueBodyNode;
import org.jruby.ast.RescueNode;
import org.jruby.ast.RetryNode;
import org.jruby.ast.ReturnNode;
import org.jruby.ast.SClassNode;
import org.jruby.ast.SValueNode;
import org.jruby.ast.ScopeNode;
import org.jruby.ast.SelfNode;
import org.jruby.ast.SplatNode;
import org.jruby.ast.StrNode;
import org.jruby.ast.SuperNode;
import org.jruby.ast.SymbolNode;
import org.jruby.ast.ToAryNode;
import org.jruby.ast.TrueNode;
import org.jruby.ast.UndefNode;
import org.jruby.ast.UntilNode;
import org.jruby.ast.VAliasNode;
import org.jruby.ast.VCallNode;
import org.jruby.ast.WhenNode;
import org.jruby.ast.WhileNode;
import org.jruby.ast.XStrNode;
import org.jruby.ast.YieldNode;
import org.jruby.ast.ZArrayNode;
import org.jruby.ast.ZSuperNode;
import org.jruby.ast.visitor.NodeVisitor;
import org.jruby.evaluator.Instruction;
import org.jruby.lexer.yacc.ISourcePosition;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.IRubyScript;
import org.rubypeople.rdt.core.IType;

public class SelectionVisitor implements NodeVisitor {

	private int end;
	private int start;
	private IRubyScript script;
	private String currentTypeName;
	private List elements;

	public SelectionVisitor(IRubyScript script, int start, int end) {
		this.script = script;
		this.start = start;
		this.end = end;
		this.currentTypeName = "Object";
		this.elements = new ArrayList();
	}

	public Instruction visitAliasNode(AliasNode iVisited) {
		// TODO Auto-generated method stub
		return null;
	}

	public Instruction visitAndNode(AndNode iVisited) {
		// TODO Auto-generated method stub
		return null;
	}

	public Instruction visitArgsNode(ArgsNode iVisited) {
		// TODO Auto-generated method stub
		return null;
	}

	public Instruction visitArgsCatNode(ArgsCatNode iVisited) {
		// TODO Auto-generated method stub
		return null;
	}

	public Instruction visitArrayNode(ArrayNode iVisited) {
		// TODO Auto-generated method stub
		return null;
	}

	public Instruction visitBackRefNode(BackRefNode iVisited) {
		// TODO Auto-generated method stub
		return null;
	}

	public Instruction visitBeginNode(BeginNode iVisited) {
		// TODO Auto-generated method stub
		return null;
	}

	public Instruction visitBignumNode(BignumNode iVisited) {
		// TODO Auto-generated method stub
		return null;
	}

	public Instruction visitBlockArgNode(BlockArgNode iVisited) {
		// TODO Auto-generated method stub
		return null;
	}

	public Instruction visitBlockNode(BlockNode iVisited) {
		// TODO Auto-generated method stub
		return null;
	}

	public Instruction visitBlockPassNode(BlockPassNode iVisited) {
		// TODO Auto-generated method stub
		return null;
	}

	public Instruction visitBreakNode(BreakNode iVisited) {
		// TODO Auto-generated method stub
		return null;
	}

	public Instruction visitConstDeclNode(ConstDeclNode iVisited) {
		// TODO Auto-generated method stub
		return null;
	}

	public Instruction visitClassVarAsgnNode(ClassVarAsgnNode iVisited) {
		// TODO Auto-generated method stub
		return null;
	}

	public Instruction visitClassVarDeclNode(ClassVarDeclNode iVisited) {
		// TODO Auto-generated method stub
		return null;
	}

	public Instruction visitClassVarNode(ClassVarNode iVisited) {
		// TODO Auto-generated method stub
		return null;
	}

	public Instruction visitCallNode(CallNode iVisited) {
		// TODO Auto-generated method stub
		return null;
	}

	public Instruction visitCaseNode(CaseNode iVisited) {
		// TODO Auto-generated method stub
		return null;
	}

	public Instruction visitClassNode(ClassNode iVisited) {
		// TODO Push type names on a stack
		String oldTypeName = currentTypeName;
		currentTypeName = getFullyQualifiedName(iVisited.getCPath());
		iVisited.getBodyNode().accept(this);
		currentTypeName = oldTypeName;
		return null;
	}
	
	private String getFullyQualifiedName(Node node) {
		if (node == null)
			return "";
		if (node instanceof ConstNode) {
			ConstNode constNode = (ConstNode) node;
			return constNode.getName();
		}
		if (node instanceof Colon2Node) {
			Colon2Node colonNode = (Colon2Node) node;
			String prefix = getFullyQualifiedName(colonNode.getLeftNode());
			if (prefix.length() > 0)
				prefix = prefix + "::";
			return prefix + colonNode.getName();
		}
		return "";
	}

	public Instruction visitColon2Node(Colon2Node iVisited) {
		// TODO Auto-generated method stub
		return null;
	}

	public Instruction visitColon3Node(Colon3Node iVisited) {
		// TODO Auto-generated method stub
		return null;
	}

	public Instruction visitConstNode(ConstNode iVisited) {
		// TODO Auto-generated method stub
		return null;
	}

	public Instruction visitDAsgnNode(DAsgnNode iVisited) {
		// TODO Auto-generated method stub
		return null;
	}

	public Instruction visitDRegxNode(DRegexpNode iVisited) {
		// TODO Auto-generated method stub
		return null;
	}

	public Instruction visitDStrNode(DStrNode iVisited) {
		// TODO Auto-generated method stub
		return null;
	}

	public Instruction visitDSymbolNode(DSymbolNode iVisited) {
		// TODO Auto-generated method stub
		return null;
	}

	public Instruction visitDVarNode(DVarNode iVisited) {
		// TODO Auto-generated method stub
		return null;
	}

	public Instruction visitDXStrNode(DXStrNode iVisited) {
		// TODO Auto-generated method stub
		return null;
	}

	public Instruction visitDefinedNode(DefinedNode iVisited) {
		// TODO Auto-generated method stub
		return null;
	}

	public Instruction visitDefnNode(DefnNode iVisited) {
		// TODO Auto-generated method stub
		return null;
	}

	public Instruction visitDefsNode(DefsNode iVisited) {
		// TODO Auto-generated method stub
		return null;
	}

	public Instruction visitDotNode(DotNode iVisited) {
		// TODO Auto-generated method stub
		return null;
	}

	public Instruction visitEnsureNode(EnsureNode iVisited) {
		// TODO Auto-generated method stub
		return null;
	}

	public Instruction visitEvStrNode(EvStrNode iVisited) {
		// TODO Auto-generated method stub
		return null;
	}

	public Instruction visitFCallNode(FCallNode iVisited) {
		// TODO Auto-generated method stub
		ISourcePosition pos = iVisited.getPosition();
		if ((start >= pos.getStartOffset()) && (end <= pos.getEndOffset())) {
			String methodName = iVisited.getName();
			IType type = script.getType(currentTypeName);
			int argCount = 0;
			elements.add(type.getMethod(methodName, new String[] {}));			
		}
		return null;
	}

	public Instruction visitFalseNode(FalseNode iVisited) {
		// TODO Auto-generated method stub
		return null;
	}

	public Instruction visitFixnumNode(FixnumNode iVisited) {
		// TODO Auto-generated method stub
		return null;
	}

	public Instruction visitFlipNode(FlipNode iVisited) {
		// TODO Auto-generated method stub
		return null;
	}

	public Instruction visitFloatNode(FloatNode iVisited) {
		// TODO Auto-generated method stub
		return null;
	}

	public Instruction visitForNode(ForNode iVisited) {
		// TODO Auto-generated method stub
		return null;
	}

	public Instruction visitGlobalAsgnNode(GlobalAsgnNode iVisited) {
		// TODO Auto-generated method stub
		return null;
	}

	public Instruction visitGlobalVarNode(GlobalVarNode iVisited) {
		// TODO Auto-generated method stub
		return null;
	}

	public Instruction visitHashNode(HashNode iVisited) {
		// TODO Auto-generated method stub
		return null;
	}

	public Instruction visitInstAsgnNode(InstAsgnNode iVisited) {
		// TODO Auto-generated method stub
		return null;
	}

	public Instruction visitInstVarNode(InstVarNode iVisited) {
		// TODO Auto-generated method stub
		return null;
	}

	public Instruction visitIfNode(IfNode iVisited) {
		// TODO Auto-generated method stub
		return null;
	}

	public Instruction visitIterNode(IterNode iVisited) {
		// TODO Auto-generated method stub
		return null;
	}

	public Instruction visitLocalAsgnNode(LocalAsgnNode iVisited) {
		// TODO Auto-generated method stub
		return null;
	}

	public Instruction visitLocalVarNode(LocalVarNode iVisited) {
		// TODO Auto-generated method stub
		return null;
	}

	public Instruction visitMultipleAsgnNode(MultipleAsgnNode iVisited) {
		// TODO Auto-generated method stub
		return null;
	}

	public Instruction visitMatch2Node(Match2Node iVisited) {
		// TODO Auto-generated method stub
		return null;
	}

	public Instruction visitMatch3Node(Match3Node iVisited) {
		// TODO Auto-generated method stub
		return null;
	}

	public Instruction visitMatchNode(MatchNode iVisited) {
		// TODO Auto-generated method stub
		return null;
	}

	public Instruction visitModuleNode(ModuleNode iVisited) {
		// TODO Auto-generated method stub
		return null;
	}

	public Instruction visitNewlineNode(NewlineNode iVisited) {
		iVisited.getNextNode().accept(this);
		// TODO Auto-generated method stub
		return null;
	}

	public Instruction visitNextNode(NextNode iVisited) {
		// TODO Auto-generated method stub
		return null;
	}

	public Instruction visitNilNode(NilNode iVisited) {
		// TODO Auto-generated method stub
		return null;
	}

	public Instruction visitNotNode(NotNode iVisited) {
		// TODO Auto-generated method stub
		return null;
	}

	public Instruction visitNthRefNode(NthRefNode iVisited) {
		// TODO Auto-generated method stub
		return null;
	}

	public Instruction visitOpElementAsgnNode(OpElementAsgnNode iVisited) {
		// TODO Auto-generated method stub
		return null;
	}

	public Instruction visitOpAsgnNode(OpAsgnNode iVisited) {
		// TODO Auto-generated method stub
		return null;
	}

	public Instruction visitOpAsgnAndNode(OpAsgnAndNode iVisited) {
		// TODO Auto-generated method stub
		return null;
	}

	public Instruction visitOpAsgnOrNode(OpAsgnOrNode iVisited) {
		// TODO Auto-generated method stub
		return null;
	}

	public Instruction visitOptNNode(OptNNode iVisited) {
		// TODO Auto-generated method stub
		return null;
	}

	public Instruction visitOrNode(OrNode iVisited) {
		// TODO Auto-generated method stub
		return null;
	}

	public Instruction visitPostExeNode(PostExeNode iVisited) {
		// TODO Auto-generated method stub
		return null;
	}

	public Instruction visitRedoNode(RedoNode iVisited) {
		// TODO Auto-generated method stub
		return null;
	}

	public Instruction visitRegexpNode(RegexpNode iVisited) {
		// TODO Auto-generated method stub
		return null;
	}

	public Instruction visitRescueBodyNode(RescueBodyNode iVisited) {
		// TODO Auto-generated method stub
		return null;
	}

	public Instruction visitRescueNode(RescueNode iVisited) {
		// TODO Auto-generated method stub
		return null;
	}

	public Instruction visitRetryNode(RetryNode iVisited) {
		// TODO Auto-generated method stub
		return null;
	}

	public Instruction visitReturnNode(ReturnNode iVisited) {
		// TODO Auto-generated method stub
		return null;
	}

	public Instruction visitSClassNode(SClassNode iVisited) {
		// TODO Auto-generated method stub
		return null;
	}

	public Instruction visitScopeNode(ScopeNode iVisited) {
		// TODO Auto-generated method stub
		return null;
	}

	public Instruction visitSelfNode(SelfNode iVisited) {
		// TODO Auto-generated method stub
		return null;
	}

	public Instruction visitSplatNode(SplatNode iVisited) {
		// TODO Auto-generated method stub
		return null;
	}

	public Instruction visitStrNode(StrNode iVisited) {
		// TODO Auto-generated method stub
		return null;
	}

	public Instruction visitSuperNode(SuperNode iVisited) {
		// TODO Auto-generated method stub
		return null;
	}

	public Instruction visitSValueNode(SValueNode iVisited) {
		// TODO Auto-generated method stub
		return null;
	}

	public Instruction visitSymbolNode(SymbolNode iVisited) {
		// TODO Auto-generated method stub
		return null;
	}

	public Instruction visitToAryNode(ToAryNode iVisited) {
		// TODO Auto-generated method stub
		return null;
	}

	public Instruction visitTrueNode(TrueNode iVisited) {
		// TODO Auto-generated method stub
		return null;
	}

	public Instruction visitUndefNode(UndefNode iVisited) {
		// TODO Auto-generated method stub
		return null;
	}

	public Instruction visitUntilNode(UntilNode iVisited) {
		// TODO Auto-generated method stub
		return null;
	}

	public Instruction visitVAliasNode(VAliasNode iVisited) {
		// TODO Auto-generated method stub
		return null;
	}

	public Instruction visitVCallNode(VCallNode iVisited) {
		// TODO Auto-generated method stub
		return null;
	}

	public Instruction visitWhenNode(WhenNode iVisited) {
		// TODO Auto-generated method stub
		return null;
	}

	public Instruction visitWhileNode(WhileNode iVisited) {
		// TODO Auto-generated method stub
		return null;
	}

	public Instruction visitXStrNode(XStrNode iVisited) {
		// TODO Auto-generated method stub
		return null;
	}

	public Instruction visitYieldNode(YieldNode iVisited) {
		// TODO Auto-generated method stub
		return null;
	}

	public Instruction visitZArrayNode(ZArrayNode iVisited) {
		// TODO Auto-generated method stub
		return null;
	}

	public Instruction visitZSuperNode(ZSuperNode iVisited) {
		// TODO Auto-generated method stub
		return null;
	}

	public IRubyElement[] getElements() {
		IRubyElement[] elem = new IRubyElement[elements.size()];
		for (int i = 0; i < elements.size(); i++) {
			elem[i] = (IRubyElement) elements.get(i);
		}
		return elem;
	}

}
