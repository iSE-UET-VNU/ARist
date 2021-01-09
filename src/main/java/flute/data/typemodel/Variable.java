package flute.data.typemodel;

import org.eclipse.jdt.core.dom.ITypeBinding;

public class Variable {
    private boolean isStatic = false;
    private ITypeBinding typeBinding;
    private String name;
    private boolean isInitialized = false;
    private boolean isLocalVariable = false;

    public Variable(ITypeBinding typeBinding, String name) {
        this.typeBinding = typeBinding;
        this.name = name;
    }

    public ITypeBinding getTypeBinding() {
        return typeBinding;
    }

    public void setTypeBinding(ITypeBinding typeBinding) {
        this.typeBinding = typeBinding;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isStatic() {
        return isStatic;
    }

    public void setStatic(boolean aStatic) {
        isStatic = aStatic;
    }

    public boolean isInitialized() {
        return isInitialized;
    }

    public void setInitialized(boolean initialized) {
        isInitialized = initialized;
    }

    public boolean isLocalVariable() {
        return isLocalVariable;
    }

    public void setLocalVariable(boolean localVariable) {
        isLocalVariable = localVariable;
    }

    @Override
    public String toString() {
        return typeBinding.getName() + " " + name;
    }
}
