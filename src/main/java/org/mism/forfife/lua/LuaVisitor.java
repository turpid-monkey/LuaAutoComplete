// Generated from /Users/tr1nergy/NetBeansProjects/LuaAutoComplete/src/main/antlr4/Lua.g4 by ANTLR 4.2.2
package org.mism.forfife.lua;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link LuaParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface LuaVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link LuaParser#operatorUnary}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOperatorUnary(@NotNull LuaParser.OperatorUnaryContext ctx);

	/**
	 * Visit a parse tree produced by {@link LuaParser#funcname}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFuncname(@NotNull LuaParser.FuncnameContext ctx);

	/**
	 * Visit a parse tree produced by {@link LuaParser#operatorAnd}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOperatorAnd(@NotNull LuaParser.OperatorAndContext ctx);

	/**
	 * Visit a parse tree produced by {@link LuaParser#fieldsep}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFieldsep(@NotNull LuaParser.FieldsepContext ctx);

	/**
	 * Visit a parse tree produced by {@link LuaParser#string}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitString(@NotNull LuaParser.StringContext ctx);

	/**
	 * Visit a parse tree produced by {@link LuaParser#functioncall}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunctioncall(@NotNull LuaParser.FunctioncallContext ctx);

	/**
	 * Visit a parse tree produced by {@link LuaParser#parlist}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParlist(@NotNull LuaParser.ParlistContext ctx);

	/**
	 * Visit a parse tree produced by {@link LuaParser#chunk}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitChunk(@NotNull LuaParser.ChunkContext ctx);

	/**
	 * Visit a parse tree produced by {@link LuaParser#explist}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExplist(@NotNull LuaParser.ExplistContext ctx);

	/**
	 * Visit a parse tree produced by {@link LuaParser#retstat}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRetstat(@NotNull LuaParser.RetstatContext ctx);

	/**
	 * Visit a parse tree produced by {@link LuaParser#varOrExp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVarOrExp(@NotNull LuaParser.VarOrExpContext ctx);

	/**
	 * Visit a parse tree produced by {@link LuaParser#number}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNumber(@NotNull LuaParser.NumberContext ctx);

	/**
	 * Visit a parse tree produced by {@link LuaParser#prefixexp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPrefixexp(@NotNull LuaParser.PrefixexpContext ctx);

	/**
	 * Visit a parse tree produced by {@link LuaParser#nameAndArgs}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNameAndArgs(@NotNull LuaParser.NameAndArgsContext ctx);

	/**
	 * Visit a parse tree produced by {@link LuaParser#namelist}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNamelist(@NotNull LuaParser.NamelistContext ctx);

	/**
	 * Visit a parse tree produced by {@link LuaParser#functiondef}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunctiondef(@NotNull LuaParser.FunctiondefContext ctx);

	/**
	 * Visit a parse tree produced by {@link LuaParser#block}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBlock(@NotNull LuaParser.BlockContext ctx);

	/**
	 * Visit a parse tree produced by {@link LuaParser#operatorComparison}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOperatorComparison(@NotNull LuaParser.OperatorComparisonContext ctx);

	/**
	 * Visit a parse tree produced by {@link LuaParser#varlist}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVarlist(@NotNull LuaParser.VarlistContext ctx);

	/**
	 * Visit a parse tree produced by {@link LuaParser#exp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExp(@NotNull LuaParser.ExpContext ctx);

	/**
	 * Visit a parse tree produced by {@link LuaParser#funcbody}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFuncbody(@NotNull LuaParser.FuncbodyContext ctx);

	/**
	 * Visit a parse tree produced by {@link LuaParser#stat}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStat(@NotNull LuaParser.StatContext ctx);

	/**
	 * Visit a parse tree produced by {@link LuaParser#operatorPower}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOperatorPower(@NotNull LuaParser.OperatorPowerContext ctx);

	/**
	 * Visit a parse tree produced by {@link LuaParser#operatorOr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOperatorOr(@NotNull LuaParser.OperatorOrContext ctx);

	/**
	 * Visit a parse tree produced by {@link LuaParser#var}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVar(@NotNull LuaParser.VarContext ctx);

	/**
	 * Visit a parse tree produced by {@link LuaParser#operatorAddSub}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOperatorAddSub(@NotNull LuaParser.OperatorAddSubContext ctx);

	/**
	 * Visit a parse tree produced by {@link LuaParser#operatorMulDivMod}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOperatorMulDivMod(@NotNull LuaParser.OperatorMulDivModContext ctx);

	/**
	 * Visit a parse tree produced by {@link LuaParser#label}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLabel(@NotNull LuaParser.LabelContext ctx);

	/**
	 * Visit a parse tree produced by {@link LuaParser#fieldlist}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFieldlist(@NotNull LuaParser.FieldlistContext ctx);

	/**
	 * Visit a parse tree produced by {@link LuaParser#operatorStrcat}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOperatorStrcat(@NotNull LuaParser.OperatorStrcatContext ctx);

	/**
	 * Visit a parse tree produced by {@link LuaParser#args}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArgs(@NotNull LuaParser.ArgsContext ctx);

	/**
	 * Visit a parse tree produced by {@link LuaParser#field}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitField(@NotNull LuaParser.FieldContext ctx);

	/**
	 * Visit a parse tree produced by {@link LuaParser#varSuffix}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVarSuffix(@NotNull LuaParser.VarSuffixContext ctx);

	/**
	 * Visit a parse tree produced by {@link LuaParser#tableconstructor}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTableconstructor(@NotNull LuaParser.TableconstructorContext ctx);
}