package org.mism.forfife.visitors;

import static org.mism.forfife.LuaParseTreeUtil.getParentStatContext;
import static org.mism.forfife.LuaParseTreeUtil.hasParentRuleContext;
import static org.mism.forfife.LuaParseTreeUtil.start;

import org.mism.forfife.lua.LuaParser;
import org.mism.forfife.lua.LuaParser.VarContext;

public class VariableVisitor extends LuaCompletionVisitor {
	
	@Override
	public Void visitVar(VarContext ctx) {

		String varName = start(ctx);
		LuaParser.StatContext statCtx = getParentStatContext(ctx);
		boolean local = start(statCtx).equals("local");

		// if it is a subrule of prefixExp, it might as well be a function
		// call
		if (!hasParentRuleContext(ctx, LuaParser.RULE_prefixexp)
				&& !hasParentRuleContext(ctx, LuaParser.RULE_functioncall)) {
			// TODO: find if the var is in the right scope, add it to the list of completions...
		}
		
		return super.visitVar(ctx);
	}

}
