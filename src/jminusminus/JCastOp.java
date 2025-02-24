// Copyright 2012- Bill Campbell, Swami Iyer and Bahar Akbal-Delibas

package jminusminus;

import java.util.Hashtable;

import static jminusminus.CLConstants.*;

/**
 * The AST for an cast expression, which has both a cast (a type) and the expression to be cast.
 */
class JCastOp extends JExpression {
    // The cast.
    private Type cast;

    // The expression we're casting.
    private JExpression expr;

    // The conversions table.
    private static Conversions conversions;

    // The converter to use for this cast.
    private Converter converter;

    /**
     * Constructs an AST node for a cast expression.
     *
     * @param line the line in which the expression occurs in the source file.
     * @param cast the type we're casting our expression as.
     * @param expr the expression we're casting.
     */
    public JCastOp(int line, Type cast, JExpression expr) {
        super(line);
        this.cast = cast;
        this.expr = expr;
        conversions = new Conversions();
    }

    /**
     * {@inheritDoc}
     */
    public JExpression analyze(Context context) {
        expr = (JExpression) expr.analyze(context);
        type = cast = cast.resolve(context);
        if (cast.equals(expr.type())) {
            converter = Converter.Identity;
        } else if (cast.isJavaAssignableFrom(expr.type())) {
            converter = Converter.WidenReference;
        } else if (expr.type().isJavaAssignableFrom(cast)) {
            converter = new NarrowReference(cast);
        } else if (conversions.get(expr.type(), cast) != null) {
            converter = conversions.get(expr.type(), cast);
        } else {
            JAST.compilationUnit.reportSemanticError(line,
                    "Cannot cast a " + expr.type().toString() + " to a " + cast.toString());
        }
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public void codegen(CLEmitter output) {
        expr.codegen(output);
        converter.codegen(output);
    }

    /**
     * {@inheritDoc}
     */
    public void toJSON(JSONElement json) {
        JSONElement e = new JSONElement();
        json.addChild("JCastOp:" + line, e);
        e.addAttribute("type", cast == null ? "" : cast.toString());
        JSONElement e1 = new JSONElement();
        e.addChild("Expression", e1);
        expr.toJSON(e1);
    }
}

/**
 * A 2D table of conversions, from one type to another.
 */
class Conversions {
    // Table of conversions; maps a source and target type pair to its converter.
    private Hashtable<String, Converter> table;

    /**
     * Constructs a table of conversions and populates it.
     */
    public Conversions() {
        table = new Hashtable<String, Converter>();

        // Populate the table.
        put(Type.CHAR, Type.INT, Converter.Identity);
        put(Type.CHAR, Type.LONG, new I2L());
        put(Type.CHAR, Type.DOUBLE, new I2D());
        put(Type.INT, Type.CHAR, new I2C());
        put(Type.INT, Type.LONG, new I2L());
        put(Type.INT, Type.DOUBLE, new I2D());
        put(Type.LONG, Type.CHAR, new L2C());
        put(Type.LONG, Type.INT, new L2I());
        put(Type.LONG, Type.DOUBLE, new L2D());
        put(Type.DOUBLE, Type.CHAR, new D2C());
        put(Type.DOUBLE, Type.INT, new D2I());
        put(Type.DOUBLE, Type.LONG, new D2L());

        // Boxing.
        put(Type.CHAR, Type.BOXED_CHAR, new Boxing(Type.CHAR, Type.BOXED_CHAR));
        put(Type.INT, Type.BOXED_INT, new Boxing(Type.INT, Type.BOXED_INT));
        put(Type.DOUBLE, Type.BOXED_DOUBLE, new Boxing(Type.DOUBLE, Type.BOXED_DOUBLE));
        put(Type.LONG, Type.BOXED_LONG, new Boxing(Type.LONG, Type.BOXED_LONG));
        put(Type.BOOLEAN, Type.BOXED_BOOLEAN, new Boxing(Type.BOOLEAN, Type.BOXED_BOOLEAN));

        // Un-boxing.
        put(Type.BOXED_CHAR, Type.CHAR, new UnBoxing(Type.BOXED_CHAR, Type.CHAR, "charValue"));
        put(Type.BOXED_INT, Type.INT, new UnBoxing(Type.BOXED_INT, Type.INT, "intValue"));
        put(Type.BOXED_DOUBLE, Type.DOUBLE, new UnBoxing(Type.BOXED_DOUBLE, Type.DOUBLE, "doubleValue"));
        put(Type.BOXED_LONG, Type.LONG, new UnBoxing(Type.BOXED_LONG, Type.LONG, "longValue"));
        put(Type.BOXED_BOOLEAN, Type.BOOLEAN, new UnBoxing(Type.BOXED_BOOLEAN, Type.BOOLEAN,
                "booleanValue"));
    }

    /**
     * Retrieves and returns a converter for converting from some original type to a target type.
     *
     * @param source the source type.
     * @param target the target type.
     * @return the converter.
     */
    public Converter get(Type source, Type target) {
        return table.get(source.toDescriptor() + "2" + target.toDescriptor());
    }

    // Defines a conversion. This is used for populating the conversions table.
    private void put(Type source, Type target, Converter c) {
        table.put(source.toDescriptor() + "2" + target.toDescriptor(), c);
    }
}

/**
 * A Converter encapsulates any (possibly none) code necessary to perform a cast operation.
 */
interface Converter {
    /**
     * For identity conversion (no run-time code needed).
     */
    public static Converter Identity = new Identity();

    /**
     * For widening conversion (no run-time code needed).
     */
    public static Converter WidenReference = Identity;

    /**
     * Emits code necessary to convert (cast) a source type to a target type.
     *
     * @param output the code emitter.
     */
    public void codegen(CLEmitter output);
}

/**
 * An identity converter.
 */
class Identity implements Converter {
    /**
     * {@inheritDoc}
     */
    public void codegen(CLEmitter output) {
        // Nothing here.
    }
}

/**
 * A narrowing reference converter.
 */
class NarrowReference implements Converter {
    // The target type.
    private Type target;

    /**
     * Constructs a narrowing reference converter.
     *
     * @param target the target type.
     */
    public NarrowReference(Type target) {
        this.target = target;
    }

    /**
     * {@inheritDoc}
     */
    public void codegen(CLEmitter output) {
        output.addReferenceInstruction(CHECKCAST, target.jvmName());
    }
}

/**
 * A boxing converter.
 */
class Boxing implements Converter {
    // The source type.
    private Type source;

    // The target type.
    private Type target;

    /**
     * Constructs a Boxing converter.
     *
     * @param source the source type.
     * @param target the target type.
     */
    public Boxing(Type source, Type target) {
        this.source = source;
        this.target = target;
    }

    /**
     * {@inheritDoc}
     */
    public void codegen(CLEmitter output) {
        output.addMemberAccessInstruction(INVOKESTATIC, target.jvmName(), "valueOf",
                "(" + source.toDescriptor() + ")" + target.toDescriptor());
    }
}

/**
 * An un-boxing converter.
 */
class UnBoxing implements Converter {
    // The source type.
    private Type source;

    // The target type.
    private Type target;

    // The Java method to invoke for the conversion.
    private String methodName;

    /**
     * Constructs an UnBoxing converter.
     *
     * @param source     the source type.
     * @param target     the target type.
     * @param methodName the Java method to invoke for the conversion.
     */
    public UnBoxing(Type source, Type target, String methodName) {
        this.source = source;
        this.target = target;
        this.methodName = methodName;
    }

    /**
     * {@inheritDoc}
     */
    public void codegen(CLEmitter output) {
        output.addMemberAccessInstruction(INVOKEVIRTUAL, source.jvmName(), methodName,
                "()" + target.toDescriptor());
    }
}

/**
 * An int to char converter.
 */
class I2C implements Converter {
    /**
     * {@inheritDoc}
     */
    public void codegen(CLEmitter output) {
        output.addNoArgInstruction(I2C);
    }
}

/**
 * An int to long converter.
 */
class I2L implements Converter {
    /**
     * {@inheritDoc}
     */
    public void codegen(CLEmitter output) {
        output.addNoArgInstruction(I2L);
    }
}

/**
 * An int to double converter.
 */
class I2D implements Converter {
    /**
     * {@inheritDoc}
     */
    public void codegen(CLEmitter output) {
        output.addNoArgInstruction(I2D);
    }
}

/**
 * An long to char converter.
 */
class L2C implements Converter {
    /**
     * {@inheritDoc}
     */
    public void codegen(CLEmitter output) {
        output.addNoArgInstruction(L2I);
        output.addNoArgInstruction(I2C);
    }
}

/**
 * An long to int converter.
 */
class L2I implements Converter {
    /**
     * {@inheritDoc}
     */
    public void codegen(CLEmitter output) {
        output.addNoArgInstruction(L2I);
    }
}

/**
 * An long to double converter.
 */
class L2D implements Converter {
    /**
     * {@inheritDoc}
     */
    public void codegen(CLEmitter output) {
        output.addNoArgInstruction(L2D);
    }
}

/**
 * A double to char converter.
 */
class D2C implements Converter {
    /**
     * {@inheritDoc}
     */
    public void codegen(CLEmitter output) {
        output.addNoArgInstruction(D2I);
        output.addNoArgInstruction(I2C);
    }
}

/**
 * A double to Int converter.
 */
class D2I implements Converter {
    /**
     * {@inheritDoc}
     */
    public void codegen(CLEmitter output) {
        output.addNoArgInstruction(D2I);
    }
}

/**
 * A double to long converter.
 */
class D2L implements Converter {
    /**
     * {@inheritDoc}
     */
    public void codegen(CLEmitter output) {
        output.addNoArgInstruction(D2L);
    }
}

