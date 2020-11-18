package jminusminus;

import static jminusminus.CLConstants.GOTO;

public class JContinueStatement extends JStatement {
    Parser p;

    JContinueStatement (int line) { super(line); }

    JContinueStatement (int line, Parser p) {
        super(line);
        this.p = p;
    }

    /**
     * {@inheritDoc}
     */
    public JStatement analyze(Context context) {
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public void codegen(CLEmitter output) {
        System.out.println(p.enclosingStatement.toString());
        if (this.p != null)
            output.addBranchInstruction(GOTO, p.enclosingStatement.continueLabel);
    }

    /**
     * {@inheritDoc}
     */
    public void toJSON(JSONElement json) {
        JSONElement e = new JSONElement();
        json.addChild("JContinueStatement:" + line, e);
    }

}