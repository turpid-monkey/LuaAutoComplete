package org.mism.forfife.visitors;

import static org.mism.forfife.LuaParseTreeUtil.next;
import static org.mism.forfife.LuaParseTreeUtil.start;

import org.mism.forfife.LuaResource;
import org.mism.forfife.lua.LuaParser.FunctioncallContext;

public class UgLoadScriptVisitor extends LuaCompletionVisitor {

	@Override
	public Void visitFunctioncall(FunctioncallContext ctx) {
		if (start(ctx).equals("ug_load_script")) {
			String resourceLink = "ug:"
					+ next(ctx).replace("(", "").replace(")", "")
							.replace("\"", "").trim();
			LuaResource res = new LuaResource(resourceLink);
			info.getDependentResources().add(res);
		}
		return super.visitFunctioncall(ctx);
	}

}
