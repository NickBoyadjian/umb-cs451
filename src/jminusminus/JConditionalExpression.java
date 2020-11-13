package jminusminus;

import static jminusminus.CLConstants.*;

/**
 * AST node for conditional Expression :: e ? e1 e2
 */
class JConditionalExpression extends JExpression {

    // The test case
    private JExpression conditional;
    // True branch
    private JExpression trueBranch;
    // False branch
    private JExpression falseBranch;

    protected JConditionalExpression(int line, JExpression e, JExpression e1, JExpression e2) {
        super(line);
        this.conditional = e;
        this.trueBranch = e1;
        this.falseBranch = e2;
    }

    /**
     * {@inheritDoc}
     */
    public JExpression analyze(Context context) {
        conditional = (JExpression) conditional.analyze(context);
        conditional.type().mustMatchExpected(line(), Type.BOOLEAN);

        trueBranch = (JExpression) trueBranch.analyze(context);
        falseBranch = (JExpression) falseBranch.analyze(context);
        trueBranch.type().mustMatchExpected(line(), falseBranch.type());

        type = trueBranch.type();
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public void codegen(CLEmitter output) {
        String elseLabel = output.createLabel();
        String endLabel = output.createLabel();

        conditional.codegen(output, elseLabel, false);

        trueBranch.codegen(output);
        output.addBranchInstruction(GOTO, endLabel);
        output.addLabel(elseLabel);
        falseBranch.codegen(output);
        output.addLabel(endLabel);
    }

    /**
     * {@inheritDoc}
     */
    public void toJSON(JSONElement json) {
        JSONElement e = new JSONElement();
        json.addChild("JConditionalExpression:" + line, e);
        JSONElement e1 = new JSONElement();
        e.addChild("Condition", e1);
        conditional.toJSON(e1);
        JSONElement e2 = new JSONElement();
        e.addChild("ThenPart", e2);
        trueBranch.toJSON(e2);
        JSONElement e3 = new JSONElement();
        e.addChild("ElsePart", e3);
        falseBranch.toJSON(e3);
    }
}
