package expression;

import antlr.ExprBaseVisitor;
import antlr.ExprParser;
import org.antlr.v4.runtime.Token;

import java.util.ArrayList;
import java.util.List;

public class AntlrToExpression extends ExprBaseVisitor<Expression> {


    /*
     *  Given that all visit_* methods are called in a top-down fashion
     *  we can be sure that the order in which we add declared variables in the the 'vars' is
     *  identical to how they are declared in the input program.
     */

    /*
     *  The order the Nodes are visited corresponds to the
     *  order of lines in input program
     */
    private List<String> vars; // stores all the variables declared in the program so far
    private List<String> semanticErrors; // 1.duplicate declaration 2.reference to undeclared variable
    // Note that semantic errors are different from syntax error

    public AntlrToExpression(List<String> semanticErrors) {
        vars = new ArrayList<>();
        this.semanticErrors = semanticErrors;
    }


    @Override
    public Expression visitDeclaration(ExprParser.DeclarationContext ctx) {
        // ID() is a method generated to correspond to the token ID in source grammar.
        Token idToken = (Token) ctx.ID().getSymbol(); // equivalent to: ctx.getChild(0).getSymbol()
        int line = idToken.getLine();
        int column = idToken.getCharPositionInLine() + 1;

        String id = ctx.getChild(0).getText();
        // Maintaining the vars list for semantic error reporting
        if (vars.contains(id)) {
            semanticErrors.add("Error: variable " + id + " already declared (" + line + ", " + column + ")");
        }
        else {
            vars.add(id);
        }
        String type = ctx.getChild(2).getText();
        int value = Integer.parseInt(ctx.NUM().getText());
        return new VariableDeclaration(id, type, value);
    }

    @Override
    public Expression visitMultiplication(ExprParser.MultiplicationContext ctx) {
        Expression left = visit(ctx.getChild(0)); // recursively visit the left subtree of the current Multiplication node
        Expression right = visit(ctx.getChild(2)); // recursively visit the right subtree of the current Multiplication node
        return new Multiplication(left, right);
    }

    @Override
    public Expression visitAddition(ExprParser.AdditionContext ctx) {
        Expression left = visit(ctx.getChild(0)); // recursively visit the left subtree of the current Addition node
        Expression right = visit(ctx.getChild(2)); // recursively visit the right subtree of the current Addition node
        return new Addition(left, right);
    }

    @Override
    public Expression visitSubtraction(ExprParser.SubtractionContext ctx) {
        Expression left = visit(ctx.getChild(0)); // recursively visit the left subtree of the current Subtraction node
        Expression right = visit(ctx.getChild(2)); // recursively visit the right subtree of the current Subtraction node
        return new Subtraction(left, right);
    }

    @Override
    public Expression visitVariable(ExprParser.VariableContext ctx) {
        Token idToken = ctx.ID().getSymbol();
        int line = idToken.getLine();
        int column = idToken.getCharPositionInLine() + 1;

        String id = ctx.getChild(0).getText();
        if(!vars.contains(id)) {
            semanticErrors.add("Error: variable " + id + " not declared (" +  line + ", " + column + ")");
        }

        return new Variable(id);
    }

    @Override
    public Expression visitNumber(ExprParser.NumberContext ctx) {
        String numText = ctx.getChild(0).getText();
        int num = Integer.parseInt(numText);

        return new Number(num);
    }
}
