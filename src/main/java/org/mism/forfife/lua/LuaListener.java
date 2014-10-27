// Generated from /Users/tr1nergy/NetBeansProjects/LuaAutoComplete/src/main/antlr4/Lua.g4 by ANTLR 4.2.2
package org.mism.forfife.lua;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link LuaParser}.
 */
public interface LuaListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link LuaParser#operatorUnary}.
	 * @param ctx the parse tree
	 */
	void enterOperatorUnary(@NotNull LuaParser.OperatorUnaryContext ctx);
	/**
	 * Exit a parse tree produced by {@link LuaParser#operatorUnary}.
	 * @param ctx the parse tree
	 */
	void exitOperatorUnary(@NotNull LuaParser.OperatorUnaryContext ctx);

	/**
	 * Enter a parse tree produced by {@link LuaParser#funcname}.
	 * @param ctx the parse tree
	 */
	void enterFuncname(@NotNull LuaParser.FuncnameContext ctx);
	/**
	 * Exit a parse tree produced by {@link LuaParser#funcname}.
	 * @param ctx the parse tree
	 */
	void exitFuncname(@NotNull LuaParser.FuncnameContext ctx);

	/**
	 * Enter a parse tree produced by {@link LuaParser#operatorAnd}.
	 * @param ctx the parse tree
	 */
	void enterOperatorAnd(@NotNull LuaParser.OperatorAndContext ctx);
	/**
	 * Exit a parse tree produced by {@link LuaParser#operatorAnd}.
	 * @param ctx the parse tree
	 */
	void exitOperatorAnd(@NotNull LuaParser.OperatorAndContext ctx);

	/**
	 * Enter a parse tree produced by {@link LuaParser#fieldsep}.
	 * @param ctx the parse tree
	 */
	void enterFieldsep(@NotNull LuaParser.FieldsepContext ctx);
	/**
	 * Exit a parse tree produced by {@link LuaParser#fieldsep}.
	 * @param ctx the parse tree
	 */
	void exitFieldsep(@NotNull LuaParser.FieldsepContext ctx);

	/**
	 * Enter a parse tree produced by {@link LuaParser#string}.
	 * @param ctx the parse tree
	 */
	void enterString(@NotNull LuaParser.StringContext ctx);
	/**
	 * Exit a parse tree produced by {@link LuaParser#string}.
	 * @param ctx the parse tree
	 */
	void exitString(@NotNull LuaParser.StringContext ctx);

	/**
	 * Enter a parse tree produced by {@link LuaParser#functioncall}.
	 * @param ctx the parse tree
	 */
	void enterFunctioncall(@NotNull LuaParser.FunctioncallContext ctx);
	/**
	 * Exit a parse tree produced by {@link LuaParser#functioncall}.
	 * @param ctx the parse tree
	 */
	void exitFunctioncall(@NotNull LuaParser.FunctioncallContext ctx);

	/**
	 * Enter a parse tree produced by {@link LuaParser#parlist}.
	 * @param ctx the parse tree
	 */
	void enterParlist(@NotNull LuaParser.ParlistContext ctx);
	/**
	 * Exit a parse tree produced by {@link LuaParser#parlist}.
	 * @param ctx the parse tree
	 */
	void exitParlist(@NotNull LuaParser.ParlistContext ctx);

	/**
	 * Enter a parse tree produced by {@link LuaParser#chunk}.
	 * @param ctx the parse tree
	 */
	void enterChunk(@NotNull LuaParser.ChunkContext ctx);
	/**
	 * Exit a parse tree produced by {@link LuaParser#chunk}.
	 * @param ctx the parse tree
	 */
	void exitChunk(@NotNull LuaParser.ChunkContext ctx);

	/**
	 * Enter a parse tree produced by {@link LuaParser#explist}.
	 * @param ctx the parse tree
	 */
	void enterExplist(@NotNull LuaParser.ExplistContext ctx);
	/**
	 * Exit a parse tree produced by {@link LuaParser#explist}.
	 * @param ctx the parse tree
	 */
	void exitExplist(@NotNull LuaParser.ExplistContext ctx);

	/**
	 * Enter a parse tree produced by {@link LuaParser#retstat}.
	 * @param ctx the parse tree
	 */
	void enterRetstat(@NotNull LuaParser.RetstatContext ctx);
	/**
	 * Exit a parse tree produced by {@link LuaParser#retstat}.
	 * @param ctx the parse tree
	 */
	void exitRetstat(@NotNull LuaParser.RetstatContext ctx);

	/**
	 * Enter a parse tree produced by {@link LuaParser#varOrExp}.
	 * @param ctx the parse tree
	 */
	void enterVarOrExp(@NotNull LuaParser.VarOrExpContext ctx);
	/**
	 * Exit a parse tree produced by {@link LuaParser#varOrExp}.
	 * @param ctx the parse tree
	 */
	void exitVarOrExp(@NotNull LuaParser.VarOrExpContext ctx);

	/**
	 * Enter a parse tree produced by {@link LuaParser#number}.
	 * @param ctx the parse tree
	 */
	void enterNumber(@NotNull LuaParser.NumberContext ctx);
	/**
	 * Exit a parse tree produced by {@link LuaParser#number}.
	 * @param ctx the parse tree
	 */
	void exitNumber(@NotNull LuaParser.NumberContext ctx);

	/**
	 * Enter a parse tree produced by {@link LuaParser#prefixexp}.
	 * @param ctx the parse tree
	 */
	void enterPrefixexp(@NotNull LuaParser.PrefixexpContext ctx);
	/**
	 * Exit a parse tree produced by {@link LuaParser#prefixexp}.
	 * @param ctx the parse tree
	 */
	void exitPrefixexp(@NotNull LuaParser.PrefixexpContext ctx);

	/**
	 * Enter a parse tree produced by {@link LuaParser#nameAndArgs}.
	 * @param ctx the parse tree
	 */
	void enterNameAndArgs(@NotNull LuaParser.NameAndArgsContext ctx);
	/**
	 * Exit a parse tree produced by {@link LuaParser#nameAndArgs}.
	 * @param ctx the parse tree
	 */
	void exitNameAndArgs(@NotNull LuaParser.NameAndArgsContext ctx);

	/**
	 * Enter a parse tree produced by {@link LuaParser#namelist}.
	 * @param ctx the parse tree
	 */
	void enterNamelist(@NotNull LuaParser.NamelistContext ctx);
	/**
	 * Exit a parse tree produced by {@link LuaParser#namelist}.
	 * @param ctx the parse tree
	 */
	void exitNamelist(@NotNull LuaParser.NamelistContext ctx);

	/**
	 * Enter a parse tree produced by {@link LuaParser#functiondef}.
	 * @param ctx the parse tree
	 */
	void enterFunctiondef(@NotNull LuaParser.FunctiondefContext ctx);
	/**
	 * Exit a parse tree produced by {@link LuaParser#functiondef}.
	 * @param ctx the parse tree
	 */
	void exitFunctiondef(@NotNull LuaParser.FunctiondefContext ctx);

	/**
	 * Enter a parse tree produced by {@link LuaParser#block}.
	 * @param ctx the parse tree
	 */
	void enterBlock(@NotNull LuaParser.BlockContext ctx);
	/**
	 * Exit a parse tree produced by {@link LuaParser#block}.
	 * @param ctx the parse tree
	 */
	void exitBlock(@NotNull LuaParser.BlockContext ctx);

	/**
	 * Enter a parse tree produced by {@link LuaParser#operatorComparison}.
	 * @param ctx the parse tree
	 */
	void enterOperatorComparison(@NotNull LuaParser.OperatorComparisonContext ctx);
	/**
	 * Exit a parse tree produced by {@link LuaParser#operatorComparison}.
	 * @param ctx the parse tree
	 */
	void exitOperatorComparison(@NotNull LuaParser.OperatorComparisonContext ctx);

	/**
	 * Enter a parse tree produced by {@link LuaParser#varlist}.
	 * @param ctx the parse tree
	 */
	void enterVarlist(@NotNull LuaParser.VarlistContext ctx);
	/**
	 * Exit a parse tree produced by {@link LuaParser#varlist}.
	 * @param ctx the parse tree
	 */
	void exitVarlist(@NotNull LuaParser.VarlistContext ctx);

	/**
	 * Enter a parse tree produced by {@link LuaParser#exp}.
	 * @param ctx the parse tree
	 */
	void enterExp(@NotNull LuaParser.ExpContext ctx);
	/**
	 * Exit a parse tree produced by {@link LuaParser#exp}.
	 * @param ctx the parse tree
	 */
	void exitExp(@NotNull LuaParser.ExpContext ctx);

	/**
	 * Enter a parse tree produced by {@link LuaParser#funcbody}.
	 * @param ctx the parse tree
	 */
	void enterFuncbody(@NotNull LuaParser.FuncbodyContext ctx);
	/**
	 * Exit a parse tree produced by {@link LuaParser#funcbody}.
	 * @param ctx the parse tree
	 */
	void exitFuncbody(@NotNull LuaParser.FuncbodyContext ctx);

	/**
	 * Enter a parse tree produced by {@link LuaParser#stat}.
	 * @param ctx the parse tree
	 */
	void enterStat(@NotNull LuaParser.StatContext ctx);
	/**
	 * Exit a parse tree produced by {@link LuaParser#stat}.
	 * @param ctx the parse tree
	 */
	void exitStat(@NotNull LuaParser.StatContext ctx);

	/**
	 * Enter a parse tree produced by {@link LuaParser#operatorPower}.
	 * @param ctx the parse tree
	 */
	void enterOperatorPower(@NotNull LuaParser.OperatorPowerContext ctx);
	/**
	 * Exit a parse tree produced by {@link LuaParser#operatorPower}.
	 * @param ctx the parse tree
	 */
	void exitOperatorPower(@NotNull LuaParser.OperatorPowerContext ctx);

	/**
	 * Enter a parse tree produced by {@link LuaParser#operatorOr}.
	 * @param ctx the parse tree
	 */
	void enterOperatorOr(@NotNull LuaParser.OperatorOrContext ctx);
	/**
	 * Exit a parse tree produced by {@link LuaParser#operatorOr}.
	 * @param ctx the parse tree
	 */
	void exitOperatorOr(@NotNull LuaParser.OperatorOrContext ctx);

	/**
	 * Enter a parse tree produced by {@link LuaParser#var}.
	 * @param ctx the parse tree
	 */
	void enterVar(@NotNull LuaParser.VarContext ctx);
	/**
	 * Exit a parse tree produced by {@link LuaParser#var}.
	 * @param ctx the parse tree
	 */
	void exitVar(@NotNull LuaParser.VarContext ctx);

	/**
	 * Enter a parse tree produced by {@link LuaParser#operatorAddSub}.
	 * @param ctx the parse tree
	 */
	void enterOperatorAddSub(@NotNull LuaParser.OperatorAddSubContext ctx);
	/**
	 * Exit a parse tree produced by {@link LuaParser#operatorAddSub}.
	 * @param ctx the parse tree
	 */
	void exitOperatorAddSub(@NotNull LuaParser.OperatorAddSubContext ctx);

	/**
	 * Enter a parse tree produced by {@link LuaParser#operatorMulDivMod}.
	 * @param ctx the parse tree
	 */
	void enterOperatorMulDivMod(@NotNull LuaParser.OperatorMulDivModContext ctx);
	/**
	 * Exit a parse tree produced by {@link LuaParser#operatorMulDivMod}.
	 * @param ctx the parse tree
	 */
	void exitOperatorMulDivMod(@NotNull LuaParser.OperatorMulDivModContext ctx);

	/**
	 * Enter a parse tree produced by {@link LuaParser#label}.
	 * @param ctx the parse tree
	 */
	void enterLabel(@NotNull LuaParser.LabelContext ctx);
	/**
	 * Exit a parse tree produced by {@link LuaParser#label}.
	 * @param ctx the parse tree
	 */
	void exitLabel(@NotNull LuaParser.LabelContext ctx);

	/**
	 * Enter a parse tree produced by {@link LuaParser#fieldlist}.
	 * @param ctx the parse tree
	 */
	void enterFieldlist(@NotNull LuaParser.FieldlistContext ctx);
	/**
	 * Exit a parse tree produced by {@link LuaParser#fieldlist}.
	 * @param ctx the parse tree
	 */
	void exitFieldlist(@NotNull LuaParser.FieldlistContext ctx);

	/**
	 * Enter a parse tree produced by {@link LuaParser#operatorStrcat}.
	 * @param ctx the parse tree
	 */
	void enterOperatorStrcat(@NotNull LuaParser.OperatorStrcatContext ctx);
	/**
	 * Exit a parse tree produced by {@link LuaParser#operatorStrcat}.
	 * @param ctx the parse tree
	 */
	void exitOperatorStrcat(@NotNull LuaParser.OperatorStrcatContext ctx);

	/**
	 * Enter a parse tree produced by {@link LuaParser#args}.
	 * @param ctx the parse tree
	 */
	void enterArgs(@NotNull LuaParser.ArgsContext ctx);
	/**
	 * Exit a parse tree produced by {@link LuaParser#args}.
	 * @param ctx the parse tree
	 */
	void exitArgs(@NotNull LuaParser.ArgsContext ctx);

	/**
	 * Enter a parse tree produced by {@link LuaParser#field}.
	 * @param ctx the parse tree
	 */
	void enterField(@NotNull LuaParser.FieldContext ctx);
	/**
	 * Exit a parse tree produced by {@link LuaParser#field}.
	 * @param ctx the parse tree
	 */
	void exitField(@NotNull LuaParser.FieldContext ctx);

	/**
	 * Enter a parse tree produced by {@link LuaParser#varSuffix}.
	 * @param ctx the parse tree
	 */
	void enterVarSuffix(@NotNull LuaParser.VarSuffixContext ctx);
	/**
	 * Exit a parse tree produced by {@link LuaParser#varSuffix}.
	 * @param ctx the parse tree
	 */
	void exitVarSuffix(@NotNull LuaParser.VarSuffixContext ctx);

	/**
	 * Enter a parse tree produced by {@link LuaParser#tableconstructor}.
	 * @param ctx the parse tree
	 */
	void enterTableconstructor(@NotNull LuaParser.TableconstructorContext ctx);
	/**
	 * Exit a parse tree produced by {@link LuaParser#tableconstructor}.
	 * @param ctx the parse tree
	 */
	void exitTableconstructor(@NotNull LuaParser.TableconstructorContext ctx);
}