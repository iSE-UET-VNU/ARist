package flute.jdtparser;

import flute.data.type.CustomVariableBinding;
import flute.data.type.IntPrimitiveType;
import flute.jdtparser.utils.ParserUtils;
import flute.utils.parsing.CommonUtils;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.Modifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ClassParser {
    private ITypeBinding orgType;
    private List<IMethodBinding> methods;
    private List<IVariableBinding> fields;

    private void parseSuperMethod(ITypeBinding superClass) {
        if (superClass == null) return;

        IMethodBinding[] superMethods = superClass.getDeclaredMethods();
//        IVariableBinding[] superFields = superClass.getDeclaredFields();
//        for (int i = 0; i < superFields.length; i++) {
//            boolean find = false;
//            if (Modifier.isPrivate(superFields[i].getModifiers()) && Modifier.isDefault(superFields[i].getModifiers()))
//                continue;
//            for (int j = 0; j < fields.size(); j++) {
//                if (superFields[i].getName() == fields.get(j).getName()) {
//                    find = true;
//                    break;
//                }
//            }
//            if (!find) fields.add(superFields[i]);
//        }

        for (int i = 0; i < superMethods.length; i++) {
            boolean find = false;
            if (Modifier.isPrivate(superMethods[i].getModifiers()) && Modifier.isDefault(superMethods[i].getModifiers()))
                continue;
            for (int j = 0; j < methods.size(); j++) {
                if (compareMethod(superMethods[i], methods.get(j))) {
                    find = true;
                    break;
                }
            }
            if (!find) methods.add(superMethods[i]);
        }

        parseSuperMethod(superClass.getSuperclass());
    }

    boolean compareMethod(IMethodBinding method, IMethodBinding coMethod) {
        if (method.getParameterTypes().length != coMethod.getParameterTypes().length) return false;

        for (int i = 0; i < method.getParameterTypes().length; i++) {
            if (method.getParameterTypes()[i] != coMethod.getParameterTypes()[i]) return false;
        }

        if (method.isConstructor() && coMethod.isConstructor()) {
            return true;
        }

        if (coMethod.getName().equals(method.getName())) return true;

        return false;
    }

    public ClassParser(ITypeBinding orgType) {
        this.orgType = orgType;

        methods = new ArrayList<>(Arrays.asList(orgType.getDeclaredMethods()));
        fields = new ArrayList<>(Arrays.asList(orgType.getDeclaredFields()));

        if (orgType.isArray()) {
            //25 -> public static final
            fields.add(new CustomVariableBinding(25, "length", new IntPrimitiveType(), orgType));
        }

        ParserUtils.addVariableToList(ParserUtils.getAllSuperFields(orgType), fields);
        parseSuperMethod(orgType.getSuperclass());
    }

    public ITypeBinding getOrgType() {
        return orgType;
    }

    public void setOrgType(ITypeBinding orgType) {
        this.orgType = orgType;
    }

    public List<IMethodBinding> getMethods() {
        return methods;
    }

    public List<IMethodBinding> getMethodsFrom(ITypeBinding iTypeBinding) {
        return getMethodsFrom(iTypeBinding, false);
    }

    public List<IMethodBinding> getMethodsFrom(ITypeBinding iTypeBinding, boolean isStatic) {
        List<IMethodBinding> canSeenMethods = new ArrayList<>();
        methods.forEach(method -> {
            if (canSeenFrom(method.getModifiers(), iTypeBinding)
                    && (!isStatic || Modifier.isStatic(method.getModifiers())))
                canSeenMethods.add(method);
        });
        return canSeenMethods;
    }

    public List<IVariableBinding> getFields() {
        return fields;
    }

    public List<IVariableBinding> getFieldsFrom(ITypeBinding iTypeBinding) {
        return getFieldsFrom(iTypeBinding, false);
    }

    public List<IVariableBinding> getFieldsFrom(ITypeBinding iTypeBinding, boolean isStatic) {
        List<IVariableBinding> canSeenFields = new ArrayList<>();
        fields.forEach(field -> {
            if (canSeenFrom(field.getModifiers(), iTypeBinding)
                    && (!isStatic || Modifier.isStatic(field.getModifiers())))
                canSeenFields.add(field);
        });
        return canSeenFields;
    }

    public boolean canSeenFrom(int modifier, ITypeBinding clientType) {
        ITypeBinding elementType = orgType.isArray() ? orgType.getElementType() : orgType;
        int classModifier = elementType.getModifiers();

        if (clientType == orgType || Arrays.asList(clientType.getDeclaredTypes()).contains(elementType)) {
            return true;
        } else {
            boolean extended = elementType.isSubTypeCompatible(orgType);

            String fromPackage = clientType.getPackage().getName();

            String toPackage = "-1";
            if (elementType.getPackage() != null) {
                toPackage = elementType.getPackage().getName();
            }

            if (CommonUtils.checkVisibleMember(classModifier, fromPackage, toPackage, extended)) {
                if (CommonUtils.checkVisibleMember(modifier, fromPackage, toPackage, extended)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return orgType.getQualifiedName();
    }
}
