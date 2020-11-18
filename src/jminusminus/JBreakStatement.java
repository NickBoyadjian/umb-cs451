package jminusminus;

import static jminusminus.CLConstants.GOTO;

public class JBreakStatement extends JStatement {
    JStatement enclosingStatement;
    Parser p;

    JBreakStatement (int line) { super(line); }

    JBreakStatement (int line, Parser p) {
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
        if (this.p != null)
            output.addBranchInstruction(GOTO, p.enclosingStatement.breakLabel);
    }

    /**
     * {@inheritDoc}
     */
    public void toJSON(JSONElement json) {
        JSONElement e = new JSONElement();
        json.addChild("JBreakStatement:" + line, e);
    }

}
