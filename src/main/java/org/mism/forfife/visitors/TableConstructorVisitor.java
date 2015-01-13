package org.mism.forfife.visitors;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.antlr.v4.runtime.tree.ParseTree;
import org.mism.forfife.LuaParseTreeUtil;
import org.mism.forfife.lua.LuaParser;
import org.mism.forfife.lua.LuaParser.FieldContext;
import org.mism.forfife.lua.LuaParser.FieldlistContext;
import org.mism.forfife.lua.LuaParser.StatContext;
import org.mism.forfife.lua.LuaParser.TableconstructorContext;

public class TableConstructorVisitor extends LuaCompletionVisitor {

	@Override
	public Void visitTableconstructor(TableconstructorContext ctx) {
		StatContext parent = LuaParseTreeUtil.getParentStatContext(ctx);

		String tableName = LuaParseTreeUtil.start(parent);

		FieldlistContext fl = LuaParseTreeUtil.getChildRuleContext(ctx,
				LuaParser.RULE_fieldlist, FieldlistContext.class);
		List<String> entries = new ArrayList<String>();
		for (ParseTree rctx : fl.children) {
			if (rctx instanceof FieldContext) {
				entries.add(LuaParseTreeUtil.start((FieldContext) rctx));
			}
		}

		for (String entry : entries) {
			addTable(tableName, entry);
		}
		return super.visitTableconstructor(ctx);
	}

	void addTable(String tableName, String entry) {
		if (!info.getTables().containsKey(tableName)) {
			info.getTables().put(tableName, new HashSet<String>());
		}
		info.getTables().get(tableName).add(entry);
	}

}
