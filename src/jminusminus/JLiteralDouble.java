package jminusminus;


import static jminusminus.CLConstants.DCONST_0;
import static jminusminus.CLConstants.DCONST_1;

/**
 * The AST node for a double literal.
 */
class JLiteralDouble extends JExpression {
    // String representation of the literal.
    private String text;

    /**
     * Constructs an AST node for a double literal given its line number and string representation.
     *
     * @param line line in which the literal occurs in the source file.
     * @param text string representation of the literal.
     */
    public JLiteralDouble(int line, String text) {
        super(line);
        this.text = text;
    }

    /**
     * {@inheritDoc}
     */
    public JExpression analyze(Context context) {
        type = Type.DOUBLE;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public void codegen(CLEmitter output) {
        double i = Double.parseDouble(text);
        if (i == 0)
            output.addNoArgInstruction(DCONST_0);
        else if (i == 1)
            output.addNoArgInstruction(DCONST_1);
        else
            output.addLDCInstruction(i);
    }


    /**
     * {@inheritDoc}
     */
    public void toJSON(JSONElement json) {
        JSONElement e = new JSONElement();
        json.addChild("JLiteralDouble:" + line, e);
        e.addAttribute("type", type == null ? "" : type.toString());
        e.addAttribute("value", text);
    }
}
