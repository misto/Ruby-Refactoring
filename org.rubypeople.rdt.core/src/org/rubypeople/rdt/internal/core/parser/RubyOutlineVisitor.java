package org.rubypeople.rdt.internal.core.parser;
import org.ablaf.ast.INode;
import org.jruby.ast.AliasNode;
import org.jruby.ast.AndNode;
import org.jruby.ast.ArgsNode;
import org.jruby.ast.ArrayNode;
import org.jruby.ast.AttrSetNode;
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
import org.jruby.ast.DVarNode;
import org.jruby.ast.DXStrNode;
import org.jruby.ast.DefinedNode;
import org.jruby.ast.DefnNode;
import org.jruby.ast.DefsNode;
import org.jruby.ast.DotNode;
import org.jruby.ast.EnsureNode;
import org.jruby.ast.EvStrNode;
import org.jruby.ast.ExpandArrayNode;
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
import org.jruby.ast.RestArgsNode;
import org.jruby.ast.RetryNode;
import org.jruby.ast.ReturnNode;
import org.jruby.ast.SClassNode;
import org.jruby.ast.ScopeNode;
import org.jruby.ast.SelfNode;
import org.jruby.ast.StrNode;
import org.jruby.ast.SuperNode;
import org.jruby.ast.SymbolNode;
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


public class RubyOutlineVisitor implements NodeVisitor {
	protected RubyFile rubyFile;
	protected RubyClass currentClass;

	protected RubyOutlineVisitor(String fileName) {
		rubyFile = new RubyFile(fileName);
	}

	protected void printNode(String methodName, INode node) {
	}

	public void visitAliasNode(AliasNode iVisited) {
		throw new RuntimeException("Not implemented");
	}

	public void visitAndNode(AndNode iVisited) {
		throw new RuntimeException("Not implemented");
	}

	public void visitArgsNode(ArgsNode iVisited) {
		throw new RuntimeException("Not implemented");
	}

	public void visitArrayNode(ArrayNode iVisited) {
		throw new RuntimeException("Not implemented");
	}

	public void visitAttrSetNode(AttrSetNode iVisited) {
		throw new RuntimeException("Not implemented");
	}

	public void visitBackRefNode(BackRefNode iVisited) {
		throw new RuntimeException("Not implemented");
	}

	public void visitBeginNode(BeginNode iVisited) {
		throw new RuntimeException("Not implemented");
	}

	public void visitBignumNode(BignumNode iVisited) {
		throw new RuntimeException("Not implemented");
	}

	public void visitBlockArgNode(BlockArgNode iVisited) {
		throw new RuntimeException("Not implemented");
	}

	public void visitBlockNode(BlockNode iVisited) {
		printNode("visitBlockNode", iVisited);
	}

	public void visitBlockPassNode(BlockPassNode iVisited) {
		throw new RuntimeException("Not implemented");
	}

	public void visitBreakNode(BreakNode iVisited) {
		throw new RuntimeException("Not implemented");
	}

	public void visitCallNode(CallNode iVisited) {
		throw new RuntimeException("Not implemented");
	}

	public void visitCaseNode(CaseNode iVisited) {
		throw new RuntimeException("Not implemented");
	}

	public void visitClassNode(ClassNode iVisited) {
		printNode("visitClassNode", (INode) iVisited);
		
		currentClass = new RubyClass(iVisited.getClassName());
		rubyFile.add(currentClass);
	}

	public void visitClassVarAsgnNode(ClassVarAsgnNode iVisited) {
		throw new RuntimeException("Not implemented");
	}

	public void visitClassVarDeclNode(ClassVarDeclNode iVisited) {
		currentClass.addClassVariable(iVisited.getName());
	}

	public void visitClassVarNode(ClassVarNode iVisited) {
		throw new RuntimeException("Not implemented");
	}

	public void visitColon2Node(Colon2Node iVisited) {
		throw new RuntimeException("Not implemented");
	}

	public void visitColon3Node(Colon3Node iVisited) {
		throw new RuntimeException("Not implemented");
	}

	public void visitConstDeclNode(ConstDeclNode iVisited) {
		throw new RuntimeException("Not implemented");
	}

	public void visitConstNode(ConstNode iVisited) {
		printNode("visitConstNode", iVisited);
	}

	public void visitDAsgnNode(DAsgnNode iVisited) {
		throw new RuntimeException("Not implemented");
	}

	public void visitDefinedNode(DefinedNode iVisited) {
		throw new RuntimeException("Not implemented");
	}

	public void visitDefnNode(DefnNode iVisited) {
		printNode("visitDefnNode", iVisited);
		currentClass.addMethod(iVisited.getName());
	}

	public void visitDefsNode(DefsNode iVisited) {
		throw new RuntimeException("Not implemented");
	}

	public void visitDotNode(DotNode iVisited) {
		throw new RuntimeException("Not implemented");
	}

	public void visitDRegxNode(DRegexpNode iVisited) {
		throw new RuntimeException("Not implemented");
	}

	public void visitDStrNode(DStrNode iVisited) {
		throw new RuntimeException("Not implemented");
	}

	public void visitDVarNode(DVarNode iVisited) {
		throw new RuntimeException("Not implemented");
	}

	public void visitDXStrNode(DXStrNode iVisited) {
		throw new RuntimeException("Not implemented");
	}

	public void visitEnsureNode(EnsureNode iVisited) {
		throw new RuntimeException("Not implemented");
	}

	public void visitEvStrNode(EvStrNode iVisited) {
		throw new RuntimeException("Not implemented");
	}

	public void visitExpandArrayNode(ExpandArrayNode iVisited) {
		throw new RuntimeException("Not implemented");
	}

	public void visitFalseNode(FalseNode iVisited) {
		throw new RuntimeException("Not implemented");
	}

	public void visitFCallNode(FCallNode iVisited) {
		printNode("visitFCallNode", iVisited);
	}

	public void visitFixnumNode(FixnumNode iVisited) {
		printNode("visitFixnumNode", iVisited);
	}

	public void visitFlipNode(FlipNode iVisited) {
		throw new RuntimeException("Not implemented");
	}

	public void visitFloatNode(FloatNode iVisited) {
		throw new RuntimeException("Not implemented");
	}

	public void visitForNode(ForNode iVisited) {
		throw new RuntimeException("Not implemented");
	}

	public void visitGlobalAsgnNode(GlobalAsgnNode iVisited) {
		throw new RuntimeException("Not implemented");
	}

	public void visitGlobalVarNode(GlobalVarNode iVisited) {
		throw new RuntimeException("Not implemented");
	}

	public void visitHashNode(HashNode iVisited) {
		throw new RuntimeException("Not implemented");
	}

	public void visitIfNode(IfNode iVisited) {
		throw new RuntimeException("Not implemented");
	}

	public void visitInstAsgnNode(InstAsgnNode iVisited) {
		printNode("visitInstAsgnNode", iVisited);
		currentClass.addInstanceVariable(iVisited.getName());
	}

	public void visitInstVarNode(InstVarNode iVisited) {
		throw new RuntimeException("Not implemented");
	}

	public void visitIterNode(IterNode iVisited) {
		throw new RuntimeException("Not implemented");
	}

	public void visitLocalAsgnNode(LocalAsgnNode iVisited) {
		printNode("visitLocalAsgnNode", iVisited);
	}

	public void visitLocalVarNode(LocalVarNode iVisited) {
		throw new RuntimeException("Not implemented");
	}

	public void visitMatch2Node(Match2Node iVisited) {
		throw new RuntimeException("Not implemented");
	}

	public void visitMatch3Node(Match3Node iVisited) {
		throw new RuntimeException("Not implemented");
	}

	public void visitMatchNode(MatchNode iVisited) {
		throw new RuntimeException("Not implemented");
	}

	public void visitModuleNode(ModuleNode iVisited) {
		throw new RuntimeException("Not implemented");
	}

	public void visitMultipleAsgnNode(MultipleAsgnNode iVisited) {
		throw new RuntimeException("Not implemented");
	}

	public void visitNewlineNode(NewlineNode iVisited) {
		printNode("visitNewlineNode", (INode) iVisited);
	}

	public void visitNextNode(NextNode iVisited) {
		throw new RuntimeException("Not implemented");
	}

	public void visitNilNode(NilNode iVisited) {
		throw new RuntimeException("Not implemented");
	}

	public void visitNotNode(NotNode iVisited) {
		throw new RuntimeException("Not implemented");
	}

	public void visitNthRefNode(NthRefNode iVisited) {
		throw new RuntimeException("Not implemented");
	}

	public void visitOpAsgnAndNode(OpAsgnAndNode iVisited) {
		throw new RuntimeException("Not implemented");
	}

	public void visitOpAsgnNode(OpAsgnNode iVisited) {
		throw new RuntimeException("Not implemented");
	}

	public void visitOpAsgnOrNode(OpAsgnOrNode iVisited) {
		throw new RuntimeException("Not implemented");
	}

	public void visitOpElementAsgnNode(OpElementAsgnNode iVisited) {
		throw new RuntimeException("Not implemented");
	}

	public void visitOptNNode(OptNNode iVisited) {
		throw new RuntimeException("Not implemented");
	}

	public void visitOrNode(OrNode iVisited) {
		throw new RuntimeException("Not implemented");
	}

	public void visitPostExeNode(PostExeNode iVisited) {
		throw new RuntimeException("Not implemented");
	}

	public void visitRedoNode(RedoNode iVisited) {
		throw new RuntimeException("Not implemented");
	}

	public void visitRegexpNode(RegexpNode iVisited) {
		throw new RuntimeException("Not implemented");
	}

	public void visitRescueBodyNode(RescueBodyNode iVisited) {
		throw new RuntimeException("Not implemented");
	}

	public void visitRescueNode(RescueNode iVisited) {
		throw new RuntimeException("Not implemented");
	}

	public void visitRestArgsNode(RestArgsNode iVisited) {
		throw new RuntimeException("Not implemented");
	}

	public void visitRetryNode(RetryNode iVisited) {
		throw new RuntimeException("Not implemented");
	}

	public void visitReturnNode(ReturnNode iVisited) {
		throw new RuntimeException("Not implemented");
	}

	public void visitSClassNode(SClassNode iVisited) {
		throw new RuntimeException("Not implemented");
	}

	public void visitScopeNode(ScopeNode iVisited) {
		printNode("visitScopeNode", (INode) iVisited);
	}

	public void visitSelfNode(SelfNode iVisited) {
		throw new RuntimeException("Not implemented");
	}

	public void visitStrNode(StrNode iVisited) {
		throw new RuntimeException("Not implemented");
	}

	public void visitSuperNode(SuperNode iVisited) {
		throw new RuntimeException("Not implemented");
	}

	public void visitSymbolNode(SymbolNode iVisited) {
		throw new RuntimeException("Not implemented");
	}

	public void visitTrueNode(TrueNode iVisited) {
		throw new RuntimeException("Not implemented");
	}

	public void visitUndefNode(UndefNode iVisited) {
		throw new RuntimeException("Not implemented");
	}

	public void visitUntilNode(UntilNode iVisited) {
		throw new RuntimeException("Not implemented");
	}

	public void visitVAliasNode(VAliasNode iVisited) {
		throw new RuntimeException("Not implemented");
	}

	public void visitVCallNode(VCallNode iVisited) {
		printNode("visitVCallNode", iVisited);
	}

	public void visitWhenNode(WhenNode iVisited) {
		throw new RuntimeException("Not implemented");
	}

	public void visitWhileNode(WhileNode iVisited) {
		throw new RuntimeException("Not implemented");
	}

	public void visitXStrNode(XStrNode iVisited) {
		throw new RuntimeException("Not implemented");
	}

	public void visitYieldNode(YieldNode iVisited) {
		throw new RuntimeException("Not implemented");
	}

	public void visitZArrayNode(ZArrayNode iVisited) {
		throw new RuntimeException("Not implemented");
	}

	public void visitZSuperNode(ZSuperNode iVisited) {
		throw new RuntimeException("Not implemented");
	}

	public RubyFile result() {
		return rubyFile;
	}
}
