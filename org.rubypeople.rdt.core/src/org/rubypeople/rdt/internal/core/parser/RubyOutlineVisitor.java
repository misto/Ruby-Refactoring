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
		//RubyPlugin.log(new RuntimeException("Not implemented on RubyOutlineVisitor"));
	}

	public void visitAndNode(AndNode iVisited) {
		//RubyPlugin.log(new RuntimeException("Not implemented on RubyOutlineVisitor"));
	}

	public void visitArgsNode(ArgsNode iVisited) {
		//RubyPlugin.log(new RuntimeException("Not implemented on RubyOutlineVisitor"));
	}

	public void visitArrayNode(ArrayNode iVisited) {
		//RubyPlugin.log(new RuntimeException("Not implemented on RubyOutlineVisitor"));
	}

	public void visitAttrSetNode(AttrSetNode iVisited) {
		//RubyPlugin.log(new RuntimeException("Not implemented on RubyOutlineVisitor"));
	}

	public void visitBackRefNode(BackRefNode iVisited) {
		//RubyPlugin.log(new RuntimeException("Not implemented on RubyOutlineVisitor"));
	}

	public void visitBeginNode(BeginNode iVisited) {
		//RubyPlugin.log(new RuntimeException("Not implemented on RubyOutlineVisitor"));
	}

	public void visitBignumNode(BignumNode iVisited) {
		//RubyPlugin.log(new RuntimeException("Not implemented on RubyOutlineVisitor"));
	}

	public void visitBlockArgNode(BlockArgNode iVisited) {
		//RubyPlugin.log(new RuntimeException("Not implemented on RubyOutlineVisitor"));
	}

	public void visitBlockNode(BlockNode iVisited) {
		printNode("visitBlockNode", iVisited);
	}

	public void visitBlockPassNode(BlockPassNode iVisited) {
		//RubyPlugin.log(new RuntimeException("Not implemented on RubyOutlineVisitor"));
	}

	public void visitBreakNode(BreakNode iVisited) {
		//RubyPlugin.log(new RuntimeException("Not implemented on RubyOutlineVisitor"));
	}

	public void visitCallNode(CallNode iVisited) {
		//RubyPlugin.log(new RuntimeException("Not implemented on RubyOutlineVisitor"));
	}

	public void visitCaseNode(CaseNode iVisited) {
		//RubyPlugin.log(new RuntimeException("Not implemented on RubyOutlineVisitor"));
	}

	public void visitClassNode(ClassNode iVisited) {
		printNode("visitClassNode", (INode) iVisited);
		
		currentClass = new RubyClass(iVisited.getClassName());
		rubyFile.add(currentClass);
	}

	public void visitClassVarAsgnNode(ClassVarAsgnNode iVisited) {
		//RubyPlugin.log(new RuntimeException("Not implemented on RubyOutlineVisitor"));
	}

	public void visitClassVarDeclNode(ClassVarDeclNode iVisited) {
		currentClass.addClassVariable(iVisited.getName());
	}

	public void visitClassVarNode(ClassVarNode iVisited) {
		//RubyPlugin.log(new RuntimeException("Not implemented on RubyOutlineVisitor"));
	}

	public void visitColon2Node(Colon2Node iVisited) {
		//RubyPlugin.log(new RuntimeException("Not implemented on RubyOutlineVisitor"));
	}

	public void visitColon3Node(Colon3Node iVisited) {
		//RubyPlugin.log(new RuntimeException("Not implemented on RubyOutlineVisitor"));
	}

	public void visitConstDeclNode(ConstDeclNode iVisited) {
		//RubyPlugin.log(new RuntimeException("Not implemented on RubyOutlineVisitor"));
	}

	public void visitConstNode(ConstNode iVisited) {
		printNode("visitConstNode", iVisited);
	}

	public void visitDAsgnNode(DAsgnNode iVisited) {
		//RubyPlugin.log(new RuntimeException("Not implemented on RubyOutlineVisitor"));
	}

	public void visitDefinedNode(DefinedNode iVisited) {
		//RubyPlugin.log(new RuntimeException("Not implemented on RubyOutlineVisitor"));
	}

	public void visitDefnNode(DefnNode iVisited) {
		printNode("visitDefnNode", iVisited);
		currentClass.addMethod(iVisited.getName());
	}

	public void visitDefsNode(DefsNode iVisited) {
		//RubyPlugin.log(new RuntimeException("Not implemented on RubyOutlineVisitor"));
	}

	public void visitDotNode(DotNode iVisited) {
		//RubyPlugin.log(new RuntimeException("Not implemented on RubyOutlineVisitor"));
	}

	public void visitDRegxNode(DRegexpNode iVisited) {
		//RubyPlugin.log(new RuntimeException("Not implemented on RubyOutlineVisitor"));
	}

	public void visitDStrNode(DStrNode iVisited) {
		//RubyPlugin.log(new RuntimeException("Not implemented on RubyOutlineVisitor"));
	}

	public void visitDVarNode(DVarNode iVisited) {
		//RubyPlugin.log(new RuntimeException("Not implemented on RubyOutlineVisitor"));
	}

	public void visitDXStrNode(DXStrNode iVisited) {
		//RubyPlugin.log(new RuntimeException("Not implemented on RubyOutlineVisitor"));
	}

	public void visitEnsureNode(EnsureNode iVisited) {
		//RubyPlugin.log(new RuntimeException("Not implemented on RubyOutlineVisitor"));
	}

	public void visitEvStrNode(EvStrNode iVisited) {
		//RubyPlugin.log(new RuntimeException("Not implemented on RubyOutlineVisitor"));
	}

	public void visitExpandArrayNode(ExpandArrayNode iVisited) {
		//RubyPlugin.log(new RuntimeException("Not implemented on RubyOutlineVisitor"));
	}

	public void visitFalseNode(FalseNode iVisited) {
		//RubyPlugin.log(new RuntimeException("Not implemented on RubyOutlineVisitor"));
	}

	public void visitFCallNode(FCallNode iVisited) {
		printNode("visitFCallNode", iVisited);
	}

	public void visitFixnumNode(FixnumNode iVisited) {
		printNode("visitFixnumNode", iVisited);
	}

	public void visitFlipNode(FlipNode iVisited) {
		//RubyPlugin.log(new RuntimeException("Not implemented on RubyOutlineVisitor"));
	}

	public void visitFloatNode(FloatNode iVisited) {
		//RubyPlugin.log(new RuntimeException("Not implemented on RubyOutlineVisitor"));
	}

	public void visitForNode(ForNode iVisited) {
		//RubyPlugin.log(new RuntimeException("Not implemented on RubyOutlineVisitor"));
	}

	public void visitGlobalAsgnNode(GlobalAsgnNode iVisited) {
		//RubyPlugin.log(new RuntimeException("Not implemented on RubyOutlineVisitor"));
	}

	public void visitGlobalVarNode(GlobalVarNode iVisited) {
		//RubyPlugin.log(new RuntimeException("Not implemented on RubyOutlineVisitor"));
	}

	public void visitHashNode(HashNode iVisited) {
		//RubyPlugin.log(new RuntimeException("Not implemented on RubyOutlineVisitor"));
	}

	public void visitIfNode(IfNode iVisited) {
		//RubyPlugin.log(new RuntimeException("Not implemented on RubyOutlineVisitor"));
	}

	public void visitInstAsgnNode(InstAsgnNode iVisited) {
		printNode("visitInstAsgnNode", iVisited);
		currentClass.addInstanceVariable(iVisited.getName());
	}

	public void visitInstVarNode(InstVarNode iVisited) {
		//RubyPlugin.log(new RuntimeException("Not implemented on RubyOutlineVisitor"));
	}

	public void visitIterNode(IterNode iVisited) {
		//RubyPlugin.log(new RuntimeException("Not implemented on RubyOutlineVisitor"));
	}

	public void visitLocalAsgnNode(LocalAsgnNode iVisited) {
		printNode("visitLocalAsgnNode", iVisited);
	}

	public void visitLocalVarNode(LocalVarNode iVisited) {
		//RubyPlugin.log(new RuntimeException("Not implemented on RubyOutlineVisitor"));
	}

	public void visitMatch2Node(Match2Node iVisited) {
		//RubyPlugin.log(new RuntimeException("Not implemented on RubyOutlineVisitor"));
	}

	public void visitMatch3Node(Match3Node iVisited) {
		//RubyPlugin.log(new RuntimeException("Not implemented on RubyOutlineVisitor"));
	}

	public void visitMatchNode(MatchNode iVisited) {
		//RubyPlugin.log(new RuntimeException("Not implemented on RubyOutlineVisitor"));
	}

	public void visitModuleNode(ModuleNode iVisited) {
		//RubyPlugin.log(new RuntimeException("Not implemented on RubyOutlineVisitor"));
	}

	public void visitMultipleAsgnNode(MultipleAsgnNode iVisited) {
		//RubyPlugin.log(new RuntimeException("Not implemented on RubyOutlineVisitor"));
	}

	public void visitNewlineNode(NewlineNode iVisited) {
		printNode("visitNewlineNode", (INode) iVisited);
	}

	public void visitNextNode(NextNode iVisited) {
		//RubyPlugin.log(new RuntimeException("Not implemented on RubyOutlineVisitor"));
	}

	public void visitNilNode(NilNode iVisited) {
		//RubyPlugin.log(new RuntimeException("Not implemented on RubyOutlineVisitor"));
	}

	public void visitNotNode(NotNode iVisited) {
		//RubyPlugin.log(new RuntimeException("Not implemented on RubyOutlineVisitor"));
	}

	public void visitNthRefNode(NthRefNode iVisited) {
		//RubyPlugin.log(new RuntimeException("Not implemented on RubyOutlineVisitor"));
	}

	public void visitOpAsgnAndNode(OpAsgnAndNode iVisited) {
		//RubyPlugin.log(new RuntimeException("Not implemented on RubyOutlineVisitor"));
	}

	public void visitOpAsgnNode(OpAsgnNode iVisited) {
		//RubyPlugin.log(new RuntimeException("Not implemented on RubyOutlineVisitor"));
	}

	public void visitOpAsgnOrNode(OpAsgnOrNode iVisited) {
		//RubyPlugin.log(new RuntimeException("Not implemented on RubyOutlineVisitor"));
	}

	public void visitOpElementAsgnNode(OpElementAsgnNode iVisited) {
		//RubyPlugin.log(new RuntimeException("Not implemented on RubyOutlineVisitor"));
	}

	public void visitOptNNode(OptNNode iVisited) {
		//RubyPlugin.log(new RuntimeException("Not implemented on RubyOutlineVisitor"));
	}

	public void visitOrNode(OrNode iVisited) {
		//RubyPlugin.log(new RuntimeException("Not implemented on RubyOutlineVisitor"));
	}

	public void visitPostExeNode(PostExeNode iVisited) {
		//RubyPlugin.log(new RuntimeException("Not implemented on RubyOutlineVisitor"));
	}

	public void visitRedoNode(RedoNode iVisited) {
		//RubyPlugin.log(new RuntimeException("Not implemented on RubyOutlineVisitor"));
	}

	public void visitRegexpNode(RegexpNode iVisited) {
		//RubyPlugin.log(new RuntimeException("Not implemented on RubyOutlineVisitor"));
	}

	public void visitRescueBodyNode(RescueBodyNode iVisited) {
		//RubyPlugin.log(new RuntimeException("Not implemented on RubyOutlineVisitor"));
	}

	public void visitRescueNode(RescueNode iVisited) {
		//RubyPlugin.log(new RuntimeException("Not implemented on RubyOutlineVisitor"));
	}

	public void visitRestArgsNode(RestArgsNode iVisited) {
		//RubyPlugin.log(new RuntimeException("Not implemented on RubyOutlineVisitor"));
	}

	public void visitRetryNode(RetryNode iVisited) {
		//RubyPlugin.log(new RuntimeException("Not implemented on RubyOutlineVisitor"));
	}

	public void visitReturnNode(ReturnNode iVisited) {
		//RubyPlugin.log(new RuntimeException("Not implemented on RubyOutlineVisitor"));
	}

	public void visitSClassNode(SClassNode iVisited) {
		//RubyPlugin.log(new RuntimeException("Not implemented on RubyOutlineVisitor"));
	}

	public void visitScopeNode(ScopeNode iVisited) {
		printNode("visitScopeNode", (INode) iVisited);
	}

	public void visitSelfNode(SelfNode iVisited) {
		//RubyPlugin.log(new RuntimeException("Not implemented on RubyOutlineVisitor"));
	}

	public void visitStrNode(StrNode iVisited) {
		//RubyPlugin.log(new RuntimeException("Not implemented on RubyOutlineVisitor"));
	}

	public void visitSuperNode(SuperNode iVisited) {
		//RubyPlugin.log(new RuntimeException("Not implemented on RubyOutlineVisitor"));
	}

	public void visitSymbolNode(SymbolNode iVisited) {
		//RubyPlugin.log(new RuntimeException("Not implemented on RubyOutlineVisitor"));
	}

	public void visitTrueNode(TrueNode iVisited) {
		//RubyPlugin.log(new RuntimeException("Not implemented on RubyOutlineVisitor"));
	}

	public void visitUndefNode(UndefNode iVisited) {
		//RubyPlugin.log(new RuntimeException("Not implemented on RubyOutlineVisitor"));
	}

	public void visitUntilNode(UntilNode iVisited) {
		//RubyPlugin.log(new RuntimeException("Not implemented on RubyOutlineVisitor"));
	}

	public void visitVAliasNode(VAliasNode iVisited) {
		//RubyPlugin.log(new RuntimeException("Not implemented on RubyOutlineVisitor"));
	}

	public void visitVCallNode(VCallNode iVisited) {
		printNode("visitVCallNode", iVisited);
	}

	public void visitWhenNode(WhenNode iVisited) {
		//RubyPlugin.log(new RuntimeException("Not implemented on RubyOutlineVisitor"));
	}

	public void visitWhileNode(WhileNode iVisited) {
		//RubyPlugin.log(new RuntimeException("Not implemented on RubyOutlineVisitor"));
	}

	public void visitXStrNode(XStrNode iVisited) {
		//RubyPlugin.log(new RuntimeException("Not implemented on RubyOutlineVisitor"));
	}

	public void visitYieldNode(YieldNode iVisited) {
		//RubyPlugin.log(new RuntimeException("Not implemented on RubyOutlineVisitor"));
	}

	public void visitZArrayNode(ZArrayNode iVisited) {
		//RubyPlugin.log(new RuntimeException("Not implemented on RubyOutlineVisitor"));
	}

	public void visitZSuperNode(ZSuperNode iVisited) {
		//RubyPlugin.log(new RuntimeException("Not implemented on RubyOutlineVisitor"));
	}

	public RubyFile result() {
		return rubyFile;
	}
}
