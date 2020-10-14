package jminusminus;

import java.util.ArrayList;

public class JThrowStatement extends JStatement {
    private JExpression expression;

    JThrowStatement (int line, JExpression expression) {
        super(line);
        this.expression = expression;
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

    }

    /**
     * {@inheritDoc}
     */
    public void toJSON(JSONElement json) {
        JSONElement e = new JSONElement();
        json.addChild("JThrowStatement:" + line, e);
        JSONElement exp = new JSONElement();
        e.addChild("Expression", exp);
        expression.toJSON(exp);
    }
}
