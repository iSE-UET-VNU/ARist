package flute.data;

import flute.data.typemodel.ArgumentModel;
import flute.jdtparser.FileParser;
import org.eclipse.jdt.core.dom.*;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;

import java.util.ArrayList;
import java.util.List;

public class MethodInvocationModel {
    private Expression expression = null;
    private ITypeBinding expressionType = null;
    private ASTNode orgASTNode = null;
    private IMethodBinding methodBinding = null;
    List arguments = new ArrayList();
    List<ArgumentModel> argumentTypes = new ArrayList<>();
    ITypeBinding curClass;
    SimpleName methodName;

    public MethodInvocationModel(ITypeBinding curClass, MethodInvocation methodInvocation) {
        this.curClass = curClass;
        orgASTNode = methodInvocation;
        methodBinding = methodInvocation.resolveMethodBinding();
        expression = methodInvocation.getExpression();
        expressionType = methodInvocation.getExpression() == null ? null : methodInvocation.getExpression().resolveTypeBinding();
        arguments = methodInvocation.arguments();
        genArgumentTypes();
        methodName = methodInvocation.getName();
    }

    public MethodInvocationModel(ITypeBinding curClass, SuperMethodInvocation superMethodInvocation) {
        this.curClass = curClass;
        orgASTNode = superMethodInvocation;
        methodBinding = superMethodInvocation.resolveMethodBinding();
        expression = superMethodInvocation;
        expressionType = curClass.getSuperclass();
        arguments = superMethodInvocation.arguments();
        genArgumentTypes();
        methodName = superMethodInvocation.getName();
    }

    private void genArgumentTypes() {
        for (Object argument : arguments) {
            if (argument instanceof Expression) {
                Expression argExpr = (Expression) argument;
                argumentTypes.add(new ArgumentModel(argExpr));
            }
        }
    }

    public boolean isStaticExpression() {
        if (orgASTNode instanceof SuperMethodInvocation) return false;
        if (expressionType != null) {
            return expressionType.getName().equals(expression.toString())
                    || expressionType.getQualifiedName().equals(expression.toString());
        } else {
            FileParser.isStaticScope(orgASTNode);
        }
        return false;
    }

    public List arguments() {
        return arguments;
    }

    public List<ArgumentModel> argumentTypes() {
        return argumentTypes;
    }

    public SimpleName getName() {
        return methodName;
    }

    public Expression getExpression() {
        return expression;
    }

    public ITypeBinding getExpressionType() {
        return expressionType;
    }

    public IMethodBinding resolveMethodBinding() {
        return methodBinding;
    }


    public ASTNode getOrgASTNode() {
        return orgASTNode;
    }

    @Override
    public String toString() {
        return orgASTNode.toString();
    }
}
